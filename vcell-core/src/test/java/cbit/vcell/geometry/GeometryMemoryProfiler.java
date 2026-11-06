package cbit.vcell.geometry;

import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import com.sun.management.ThreadMXBean;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.List;

/**
 * Measures where the heap goes when an image-based geometry is deserialized.
 *
 * Motivation (#2021): {@code GET /api/v0/biomodel/101963252/simulation/98916046} -- a search-engine
 * crawler request -- parsed a 61,920,000 pixel geometry ELEVEN times in one request (once per
 * SimulationContext) and exhausted a 1000 MB heap, killing two prod api pods. Every geometry parse
 * runs {@code XmlReader:2090 precomputeAll(factory,false,false)} -> {@code updateAll()} ->
 * {@code new RegionImage(...)}.
 *
 * Three numbers per phase, and they answer different questions:
 * <ul>
 *   <li>PEAK      -- the high-water mark of heap actually needed to get through the phase, taken
 *                    from {@link MemoryPoolMXBean#getPeakUsage()} with the peak reset at phase
 *                    entry. This is the number that decides whether the JVM survives.</li>
 *   <li>RETAINED  -- live heap still held after the phase, measured across a settled GC with every
 *                    intermediate explicitly held. Sets the floor for concurrent requests.</li>
 *   <li>ALLOCATED -- bytes this thread allocated during the phase (GC churn). Sets the collector
 *                    pressure a burst produces, which is what killed the pods: eleven sequential
 *                    parses, none individually fatal.</li>
 * </ul>
 *
 * Run it (from the repo root, after {@code mvn test-compile -pl vcell-core -am}):
 *
 * <pre>
 *   mvn -q -pl vcell-core exec:java -Dexec.classpathScope=test \
 *       -Dexec.mainClass=cbit.vcell.geometry.GeometryMemoryProfiler
 * </pre>
 *
 * Optional args: cube edge lengths, e.g. {@code 64 96 128 160}; total pixels is edge^3. A warmup
 * pass at edge 48 runs first and is not reported -- without it the first measured phase absorbs
 * class loading and JIT and reads tens of MB high. The default sweep stays small enough for a
 * modest dev heap; bytes-per-pixel is linear across it, so prod-scale figures are extrapolated
 * from the fit rather than measured directly.
 */
public class GeometryMemoryProfiler {

    private static final ThreadMXBean TMX = (ThreadMXBean) ManagementFactory.getThreadMXBean();

    private static final List<MemoryPoolMXBean> HEAP_POOLS = new ArrayList<>();

    static {
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == MemoryType.HEAP) HEAP_POOLS.add(pool);
        }
    }

    /** One measured phase. */
    private static class Phase {
        final String name;
        final long peak;
        final long retainedDelta;
        final long allocated;
        final long millis;

        Phase(String name, long peak, long retainedDelta, long allocated, long millis) {
            this.name = name;
            this.peak = peak;
            this.retainedDelta = retainedDelta;
            this.allocated = allocated;
            this.millis = millis;
        }
    }

    private final List<Phase> phases = new ArrayList<>();
    /** Everything a phase produced, held so RETAINED means what it says. */
    private final List<Object> holds = new ArrayList<>();
    private long retainedBefore;

    private static void resetPeaks() {
        for (MemoryPoolMXBean pool : HEAP_POOLS) pool.resetPeakUsage();
    }

    private static long peakHeap() {
        long total = 0;
        for (MemoryPoolMXBean pool : HEAP_POOLS) total += pool.getPeakUsage().getUsed();
        return total;
    }

    /**
     * Settle the heap and report live bytes. Repeated collection matters: a single System.gc()
     * leaves recently-promoted garbage behind and would understate retention as a memory "saving".
     */
    private static long liveBytes() {
        for (int i = 0; i < 4; i++) {
            System.gc();
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private interface Work<T> {
        T run() throws Exception;
    }

    private <T> T phase(String name, Work<T> work) throws Exception {
        long tid = Thread.currentThread().getId();
        long allocBefore = TMX.getThreadAllocatedBytes(tid);
        resetPeaks();
        long t0 = System.currentTimeMillis();

        T result = work.run();

        long millis = System.currentTimeMillis() - t0;
        long peak = peakHeap();
        long allocated = TMX.getThreadAllocatedBytes(tid) - allocBefore;
        holds.add(result);                       // hold BEFORE measuring retention
        long retainedAfter = liveBytes();
        phases.add(new Phase(name, peak, retainedAfter - retainedBefore, allocated, millis));
        retainedBefore = retainedAfter;
        return result;
    }

    /**
     * A synthetic geometry with enough structure to exercise region-finding realistically: a
     * background, a large sphere, and a smaller offset sphere, so region growing has to link and
     * merge rather than flood-fill one uniform block.
     */
    private static byte[] syntheticPixels(int edge) {
        byte[] pixels = new byte[edge * edge * edge];
        double c = edge / 2.0;
        double r1 = edge * 0.38, r2 = edge * 0.15;
        double c2 = edge * 0.70;
        int i = 0;
        for (int z = 0; z < edge; z++) {
            for (int y = 0; y < edge; y++) {
                for (int x = 0; x < edge; x++, i++) {
                    double d1 = (x - c) * (x - c) + (y - c) * (y - c) + (z - c) * (z - c);
                    double d2 = (x - c2) * (x - c2) + (y - c2) * (y - c2) + (z - c) * (z - c);
                    if (d2 < r2 * r2) {
                        pixels[i] = 2;
                    } else if (d1 < r1 * r1) {
                        pixels[i] = 1;
                    } else {
                        pixels[i] = 0;
                    }
                }
            }
        }
        return pixels;
    }

    private void report(int edge, long pixelCount) {
        System.out.printf("%n=== edge=%d  pixels=%,d ===%n", edge, pixelCount);
        System.out.printf("%-36s %12s %12s %8s %12s %8s %7s%n",
                "phase", "peak", "retained", "B/px", "allocated", "B/px", "ms");
        long maxPeak = 0, totRet = 0, totAlloc = 0, totMs = 0;
        for (Phase p : phases) {
            System.out.printf("%-36s %12s %12s %8.2f %12s %8.2f %7d%n",
                    p.name, fmt(p.peak), fmt(p.retainedDelta), (double) p.retainedDelta / pixelCount,
                    fmt(p.allocated), (double) p.allocated / pixelCount, p.millis);
            maxPeak = Math.max(maxPeak, p.peak);
            totRet += p.retainedDelta;
            totAlloc += p.allocated;
            totMs += p.millis;
        }
        System.out.printf("%-36s %12s %12s %8.2f %12s %8.2f %7d%n",
                "TOTAL (peak=max, rest=sum)", fmt(maxPeak), fmt(totRet), (double) totRet / pixelCount,
                fmt(totAlloc), (double) totAlloc / pixelCount, totMs);
    }

    /**
     * How wide do the per-pixel index arrays actually need to be? RegionImage stores one int per
     * pixel in {@code mapImageIndexToLinkRegion}; if the number of link regions fits in a short or
     * a byte, three quarters of that array is zero padding. Read by reflection so the profiler
     * needs no accessor added to production code.
     */
    private void census(RegionImage regionImage, long pixelCount) {
        try {
            java.lang.reflect.Field f = RegionImage.class.getDeclaredField("mapImageIndexToLinkRegion");
            f.setAccessible(true);
            int[] map = (int[]) f.get(regionImage);
            int maxLink = 0;
            for (int v : map) if (v > maxLink) maxLink = v;

            f = RegionImage.class.getDeclaredField("mapLinkRegionToDistinctRegion");
            f.setAccessible(true);
            int[] distinct = (int[]) f.get(regionImage);

            int surfaces = regionImage.getSurfacecollection() == null
                    ? 0 : regionImage.getSurfacecollection().getSurfaceCount();
            long polygons = 0;
            for (int i = 0; i < surfaces; i++) {
                polygons += regionImage.getSurfacecollection().getSurfaces(i).getPolygonCount();
            }
            long nodes = regionImage.getSurfacecollection() == null
                    ? 0 : regionImage.getSurfacecollection().getNodeCount();

            int widthBytes = maxLink <= 0xFF ? 1 : (maxLink <= 0xFFFF ? 2 : 4);
            int maxDistinct = 0;
            for (int v : distinct) if (v > maxDistinct) maxDistinct = v;
            System.out.printf("%ncensus: link regions=%,d (max index %,d -> fits in %d byte%s), "
                            + "distinct regions=%,d%n",
                    distinct.length, maxLink, widthBytes, widthBytes == 1 ? "" : "s",
                    maxDistinct + 1);
            System.out.printf("census: mapImageIndexToLinkRegion int[%,d] = %s; "
                            + "at %d byte/px it would be %s (saving %s)%n",
                    map.length, fmt(4L * map.length), widthBytes, fmt((long) widthBytes * map.length),
                    fmt((4L - widthBytes) * map.length));
            // Counts only. Bytes per polygon come from an external `jcmd <pid> GC.class_histogram`
            // during the -Dprofiler.hold window -- guessing object layout here would be fiction.
            System.out.printf("census: surfaces=%,d polygons=%,d nodes=%,d (%.4f polygons/pixel)%n",
                    surfaces, polygons, nodes, (double) polygons / pixelCount);
        } catch (Exception e) {
            System.out.println("census unavailable: " + e);
        }
    }

    private static String fmt(long bytes) {
        if (Math.abs(bytes) > 1024 * 1024) return String.format("%,.1f MB", bytes / (1024.0 * 1024.0));
        if (Math.abs(bytes) > 1024) return String.format("%,.1f KB", bytes / 1024.0);
        return bytes + " B";
    }

    private void profile(int edge, boolean quiet) throws Exception {
        long pixelCount = (long) edge * edge * edge;
        phases.clear();
        holds.clear();
        retainedBefore = liveBytes();

        final byte[] pixels = phase("0. synthetic pixels (byte[N])", () -> syntheticPixels(edge));

        final Extent extent = new Extent(1, 1, 1);
        final Origin origin = new Origin(0, 0, 0);

        // Stands in for XmlReader.getVCImage: the decoded pixel array becomes a VCImage.
        VCImage image = phase("1. new VCImageUncompressed",
                () -> new VCImageUncompressed(null, pixels, extent, edge, edge, edge));

        // XmlReader:2006 `new Geometry(version, newimage)` -> GeometrySpec.setImage -> the veto,
        // plus a full scan of the pixels to derive VCPixelClasses and ImageSubVolumes.
        Geometry geometry = phase("2. new Geometry(name,image) [setImage]",
                () -> new Geometry("profiled", image));

        GeometrySurfaceDescription gsd = geometry.getGeometrySurfaceDescription();
        ISize sampleSize = gsd.getVolumeSampleSize();

        // The steps XmlReader:2090 precomputeAll -> updateAll() performs, measured apart.
        VCImage sampled = phase("3. createSampledImage",
                () -> geometry.getGeometrySpec().createSampledImage(sampleSize));

        // dimension=0 asks RegionImage for regions WITHOUT the surface collection, which isolates
        // region-finding cost from surface/quadrilateral cost.
        phase("4. RegionImage regions only (dim 0)",
                () -> new RegionImage(sampled, 0, extent, origin, RegionImage.NO_SMOOTHING));

        RegionImage withSurfaces = phase("5. RegionImage + surfaces (dim 3)",
                () -> new RegionImage(sampled, 3, extent, origin, RegionImage.NO_SMOOTHING));

        if (!quiet) census(withSurfaces, pixelCount);

        if (!quiet) report(edge, pixelCount);

        // -Dprofiler.hold=<seconds> keeps every structure reachable so an external
        // `jcmd <pid> GC.class_histogram` can name what is actually on the heap.
        int holdSeconds = quiet ? 0 : Integer.getInteger("profiler.hold", 0);
        if (holdSeconds > 0) {
            System.out.printf("pid %d holding %d s for GC.class_histogram%n",
                    ProcessHandle.current().pid(), holdSeconds);
            System.out.flush();
            Thread.sleep(holdSeconds * 1000L);
        }
        holds.clear();
    }

    public static void main(String[] args) throws Exception {
        int[] edges;
        if (args.length > 0) {
            edges = new int[args.length];
            for (int i = 0; i < args.length; i++) edges[i] = Integer.parseInt(args[i]);
        } else {
            edges = new int[]{64, 96, 128, 160};
        }
        System.out.printf("max heap: %s%n", fmt(Runtime.getRuntime().maxMemory()));

        // Warm up class loading and JIT; otherwise the first measured phase reads tens of MB high.
        new GeometryMemoryProfiler().profile(48, true);

        for (int edge : edges) {
            new GeometryMemoryProfiler().profile(edge, false);
        }
    }
}
