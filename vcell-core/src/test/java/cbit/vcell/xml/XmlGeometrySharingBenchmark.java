package cbit.vcell.xml;

import cbit.image.VCImageUncompressed;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelTest;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import org.vcell.util.Extent;
import org.vcell.util.Origin;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.List;

/**
 * What sharing identical {@code <Geometry>} elements is worth, measured rather than argued.
 *
 * Builds a BioModel with N spatial applications over ONE image geometry -- the shape of the model
 * in #2021, where Xmlproducer writes a full copy of the geometry inside every SimulationContext --
 * serialises it to VCML, and parses it back with sharing on and off.
 *
 * Run (after {@code mvn test-compile -pl vcell-core -am}):
 *
 * <pre>
 *   mvn -q -pl vcell-core exec:java -Dexec.classpathScope=test \
 *       -Dexec.mainClass=cbit.vcell.xml.XmlGeometrySharingBenchmark
 * </pre>
 *
 * Args: {@code <edge> <numApplications>}, default {@code 128 6}. Pixels is edge². Keep it modest:
 * the OFF case is the one that OOMs, which is the whole point.
 */
public class XmlGeometrySharingBenchmark {

    private static final List<MemoryPoolMXBean> HEAP_POOLS = new ArrayList<>();

    static {
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == MemoryType.HEAP) HEAP_POOLS.add(pool);
        }
    }

    private static void resetPeaks() {
        for (MemoryPoolMXBean pool : HEAP_POOLS) pool.resetPeakUsage();
    }

    private static long peakHeap() {
        long total = 0;
        for (MemoryPoolMXBean pool : HEAP_POOLS) total += pool.getPeakUsage().getUsed();
        return total;
    }

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

    private static String fmt(long bytes) {
        if (Math.abs(bytes) > 1024 * 1024) return String.format("%,.1f MB", bytes / (1024.0 * 1024.0));
        if (Math.abs(bytes) > 1024) return String.format("%,.1f KB", bytes / 1024.0);
        return bytes + " B";
    }

    /** A 2D two-subvolume image: the cheapest thing that still produces real regions and surfaces. */
    private static Geometry imageGeometry(int edge) throws Exception {
        byte[] pixels = new byte[edge * edge];
        for (int y = 0; y < edge; y++) {
            for (int x = 0; x < edge; x++) {
                pixels[x + edge * y] = (byte) (x > edge / 2 ? 50 : 100);
            }
        }
        VCImageUncompressed image = new VCImageUncompressed(null, pixels, new Extent(10, 10, 1), edge, edge, 1);
        Geometry geometry = new Geometry("benchmark_geometry", image);
        geometry.getGeometrySpec().setOrigin(new Origin(-5, -5, -5));
        ImageSubVolume cytosol = geometry.getGeometrySpec().getImageSubVolumeFromPixelValue(50);
        cytosol.setName("cytosol");
        ImageSubVolume ec = geometry.getGeometrySpec().getImageSubVolumeFromPixelValue(100);
        ec.setName("ec");
        geometry.precomputeAll(new GeometryThumbnailImageFactoryAWT(), true, false);
        return geometry;
    }

    private static String vcmlWithNApplications(int edge, int numApplications) throws Exception {
        BioModel bioModel = new BioModel(null);
        bioModel.setName("benchmark");
        bioModel.setModel(ModelTest.getExample_Wagner_simple(false));
        Model model = bioModel.getModel();
        Geometry geometry = imageGeometry(edge);

        SubVolume cytosol = geometry.getGeometrySpec().getSubVolume("cytosol");
        SubVolume ec = geometry.getGeometrySpec().getSubVolume("ec");
        SurfaceClass pm = geometry.getGeometrySurfaceDescription().getSurfaceClass(cytosol, ec);

        SimulationContext[] apps = new SimulationContext[numApplications];
        for (int i = 0; i < numApplications; i++) {
            SimulationContext simContext = new SimulationContext(model, geometry, null, null,
                    SimulationContext.Application.NETWORK_DETERMINISTIC);
            simContext.setName("application_" + i);
            GeometryContext geoContext = simContext.getGeometryContext();
            Structure ecStruct = model.getStructure("extracellular");
            Structure cytStruct = model.getStructure("cytosol");
            Structure pmStruct = model.getStructure("plasmaMembrane");
            geoContext.assignStructure(ecStruct, ec);
            geoContext.getStructureMapping(ecStruct).getUnitSizeParameter().setExpression(new Expression(1.0));
            geoContext.assignStructure(cytStruct, cytosol);
            geoContext.getStructureMapping(cytStruct).getUnitSizeParameter().setExpression(new Expression(0.5));
            geoContext.assignStructure(pmStruct, pm);
            geoContext.getStructureMapping(pmStruct).getUnitSizeParameter().setExpression(new Expression(1.0));
            apps[i] = simContext;
        }
        bioModel.setSimulationContexts(apps);
        return XmlHelper.bioModelToXML(bioModel);
    }

    private static void measure(String label, String vcml, boolean share) throws Exception {
        System.setProperty(XmlReader.PROPERTY_SHARE_IDENTICAL_GEOMETRIES, Boolean.toString(share));
        long before = liveBytes();
        resetPeaks();
        long t0 = System.currentTimeMillis();

        BioModel parsed = XmlHelper.XMLToBioModel(new XMLSource(vcml));

        long millis = System.currentTimeMillis() - t0;
        long peak = peakHeap();
        long retained = liveBytes() - before;

        int distinctGeometries = (int) java.util.Arrays.stream(parsed.getSimulationContexts())
                .map(SimulationContext::getGeometry)
                .distinct()
                .count();

        System.out.printf("%-22s peak %12s   retained %12s   %5d ms   distinct Geometry objects: %d%n",
                label, fmt(peak), fmt(retained), millis, distinctGeometries);

        if (parsed.hashCode() == Integer.MIN_VALUE) System.out.println(parsed);   // keep reachable
    }

    public static void main(String[] args) throws Exception {
        int edge = args.length > 0 ? Integer.parseInt(args[0]) : 128;
        int numApplications = args.length > 1 ? Integer.parseInt(args[1]) : 6;

        String vcml = vcmlWithNApplications(edge, numApplications);
        System.out.printf("max heap %s; %d applications over one %d x %d image; VCML %s%n%n",
                fmt(Runtime.getRuntime().maxMemory()), numApplications, edge, edge,
                fmt(vcml.length()));

        // Warm up class loading and JIT, or the first measured parse absorbs both.
        measure("(warmup, ignore)", vcml, false);
        System.out.println();

        measure("sharing OFF (before)", vcml, false);
        measure("sharing ON  (after)", vcml, true);

        System.clearProperty(XmlReader.PROPERTY_SHARE_IDENTICAL_GEOMETRIES);
    }
}
