package cbit.vcell.geometry;

import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Quadrilateral;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.parser.Expression;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Deterministic descriptions of what geometry surface generation produces, and the fixtures to
 * generate them from.
 *
 * VCell's regression suites are math-generation centric and mostly non-spatial, so nothing pins the
 * output of {@code RegionImage} and {@code SurfaceCollection}. Any change to region finding,
 * surface tessellation, smoothing or membrane adjacency could alter every spatial model silently.
 * These descriptions are the pin; {@code GeometrySurfaceRegressionTest} compares them against
 * committed goldens.
 *
 * The description deliberately includes both halves of the picture:
 *
 *   - what VCML's own {@code <SurfaceDescription>} records — sample size, cutoff, the volume and
 *     membrane regions with their sizes — because that is what a stored document carries;
 *   - and what it does NOT record: the mesh itself, the region-label map, membrane adjacency. That
 *     is where the algorithms actually live.
 *
 * On floating point: node coordinates are quantised to 1e-9 before hashing. Java arithmetic is
 * IEEE-754 and strict since 17, so +-*\/ and sqrt reproduce across platforms, but quantising keeps a
 * last-ulp difference from turning into a spurious golden mismatch while still being far tighter
 * than any real algorithmic change. Aggregate figures (bounding box, total area) are printed in
 * full so a human can see HOW a golden moved, not merely that it did.
 *
 * Regenerate goldens with {@code GeometrySurfaceRegressionTest.main}.
 */
public class GeometrySurfaceGolden {

    /** Quantum for coordinate hashing; see the class comment. */
    private static final double QUANTUM = 1e-9;

    // ------------------------------------------------------------------ fixtures

    /** name -> geometry supplier. LinkedHashMap so golden files are produced in a stable order. */
    public static Map<String, GeometryFactory> fixtures() {
        Map<String, GeometryFactory> map = new LinkedHashMap<>();
        map.put("image2d_two_subvolumes", () -> {
            // GeometryTest.getImageExample2D() does not build surfaces; without updateAll() this
            // fixture records "regionImage none / surfaceCollection none" and pins nothing. The
            // negative control caught that -- it was the one fixture that did not notice a
            // deliberate node perturbation.
            Geometry geometry = GeometryTest.getImageExample2D();
            geometry.getGeometrySurfaceDescription().updateAll();
            return geometry;
        });
        map.put("image3d_nested_spheres", () -> nestedSpheres(32, 0.6));
        map.put("image3d_nested_spheres_smoothed", () -> nestedSpheres(32, 0.3));
        map.put("image3d_four_shells", () -> shells(24, 4));
        map.put("image2d_stripes", () -> stripes(48));
        map.put("analytic3d_sphere", () -> analyticSphere());
        return map;
    }

    public interface GeometryFactory {
        Geometry create() throws Exception;
    }

    /** Two concentric spheres in a cube: three subvolumes, curved interfaces, nested regions. */
    private static Geometry nestedSpheres(int edge, double cutoff) throws Exception {
        byte[] pixels = new byte[edge * edge * edge];
        double c = (edge - 1) / 2.0, rOuter = edge * 0.40, rInner = edge * 0.22;
        int i = 0;
        for (int z = 0; z < edge; z++) {
            for (int y = 0; y < edge; y++) {
                for (int x = 0; x < edge; x++, i++) {
                    double d = (x - c) * (x - c) + (y - c) * (y - c) + (z - c) * (z - c);
                    pixels[i] = (byte) (d < rInner * rInner ? 2 : (d < rOuter * rOuter ? 1 : 0));
                }
            }
        }
        return imageGeometry("nested_spheres", pixels, edge, edge, edge, cutoff);
    }

    /** Concentric shells: several subvolumes, so several distinct interfaces. */
    private static Geometry shells(int edge, int numShells) throws Exception {
        byte[] pixels = new byte[edge * edge * edge];
        double c = (edge - 1) / 2.0, maxR = edge * 0.5;
        int i = 0;
        for (int z = 0; z < edge; z++) {
            for (int y = 0; y < edge; y++) {
                for (int x = 0; x < edge; x++, i++) {
                    double r = Math.sqrt((x - c) * (x - c) + (y - c) * (y - c) + (z - c) * (z - c));
                    int band = (int) (Math.min(r, maxR - 1e-9) / maxR * numShells);
                    pixels[i] = (byte) Math.min(band, numShells - 1);
                }
            }
        }
        return imageGeometry("four_shells", pixels, edge, edge, edge, 0.6);
    }

    /** Flat interfaces, and several disconnected regions of the same pixel value. */
    private static Geometry stripes(int edge) throws Exception {
        byte[] pixels = new byte[edge * edge];
        for (int y = 0; y < edge; y++) {
            for (int x = 0; x < edge; x++) {
                pixels[x + edge * y] = (byte) ((x / 8) % 2);
            }
        }
        return imageGeometry("stripes", pixels, edge, edge, 1, 0.6);
    }

    private static Geometry imageGeometry(String name, byte[] pixels, int nx, int ny, int nz,
                                          double cutoff) throws Exception {
        VCImage image = new VCImageUncompressed(null, pixels, new Extent(1, 1, 1), nx, ny, nz);
        Geometry geometry = new Geometry(name, image);
        geometry.getGeometrySpec().setOrigin(new Origin(0, 0, 0));
        int sv = 0;
        for (SubVolume subVolume : geometry.getGeometrySpec().getSubVolumes()) {
            subVolume.setName("sv" + (sv++));
        }
        geometry.getGeometrySurfaceDescription().setFilterCutoffFrequency(cutoff);
        geometry.getGeometrySurfaceDescription().updateAll();
        return geometry;
    }

    /** Not image based: exercises the analytic/CSG sampling path into RegionImage. */
    private static Geometry analyticSphere() throws Exception {
        Geometry geometry = new Geometry("analytic_sphere", 3);
        geometry.getGeometrySpec().setExtent(new Extent(1, 1, 1));
        geometry.getGeometrySpec().setOrigin(new Origin(0, 0, 0));
        // Handles must be set explicitly: the AnalyticSubVolume(name, expression) constructor
        // leaves handle = -1, which GeometrySpec.vetoableChange rejects.
        AnalyticSubVolume inside = new AnalyticSubVolume("inside",
                new Expression("((x-0.5)^2 + (y-0.5)^2 + (z-0.5)^2) < 0.09"));
        inside.setHandle(0);
        AnalyticSubVolume outside = new AnalyticSubVolume("outside", new Expression("1.0"));
        outside.setHandle(1);
        geometry.getGeometrySpec().setSubVolumes(new SubVolume[]{inside, outside});
        geometry.getGeometrySurfaceDescription().setVolumeSampleSize(new ISize(24, 24, 24));
        geometry.getGeometrySurfaceDescription().setFilterCutoffFrequency(0.6);
        geometry.getGeometrySurfaceDescription().updateAll();
        return geometry;
    }

    // ------------------------------------------------------------------ description

    public static String describe(Geometry geometry) throws Exception {
        StringBuilder out = new StringBuilder();
        GeometrySpec spec = geometry.getGeometrySpec();
        GeometrySurfaceDescription gsd = geometry.getGeometrySurfaceDescription();

        line(out, "geometry", geometry.getName(), "dimension=" + geometry.getDimension());
        line(out, "extent", fmt(spec.getExtent().getX()), fmt(spec.getExtent().getY()),
                fmt(spec.getExtent().getZ()));
        line(out, "origin", fmt(spec.getOrigin().getX()), fmt(spec.getOrigin().getY()),
                fmt(spec.getOrigin().getZ()));

        VCImage image = spec.getImage();
        if (image == null) {
            line(out, "image", "none");
        } else {
            line(out, "image", image.getNumX() + "x" + image.getNumY() + "x" + image.getNumZ(),
                    "pixelClasses=" + image.getNumPixelClasses(),
                    "pixelsSHA=" + sha(image.getPixels()));
        }

        SubVolume[] subVolumes = spec.getSubVolumes();
        line(out, "subVolumes", String.valueOf(subVolumes.length));
        String[] svNames = new String[subVolumes.length];
        for (int i = 0; i < subVolumes.length; i++) {
            svNames[i] = subVolumes[i].getName() + "(handle=" + subVolumes[i].getHandle() + ")";
        }
        Arrays.sort(svNames);
        for (String s : svNames) {
            line(out, "  subVolume", s);
        }

        ISize sample = gsd.getVolumeSampleSize();
        line(out, "sampleSize", sample.getX() + "x" + sample.getY() + "x" + sample.getZ());
        line(out, "cutoffFrequency", fmt(gsd.getFilterCutoffFrequency()));

        describeRegionImage(out, gsd);
        describeSurfaces(out, gsd);
        describeGeometricRegions(out, gsd);
        describeSurfaceClasses(out, gsd);
        return out.toString();
    }

    private static void describeRegionImage(StringBuilder out, GeometrySurfaceDescription gsd)
            throws Exception {
        RegionImage regionImage = gsd.getRegionImage();
        if (regionImage == null) {
            line(out, "regionImage", "none");
            return;
        }
        RegionImage.RegionInfo[] infos = regionImage.getRegionInfos();
        line(out, "regionImage", "regions=" + regionImage.getNumRegions(),
                "dims=" + regionImage.getNumX() + "x" + regionImage.getNumY() + "x" + regionImage.getNumZ());

        // A self-consistency check rather than a pin: every pixel must belong to exactly one
        // region, so the region sizes must account for the whole volume. A golden can only ever
        // say "this is what it did"; this line says whether what it did is coherent.
        long pixelSum = 0;
        for (RegionImage.RegionInfo info : infos) {
            pixelSum += info.getNumPixels();
        }
        long totalPixels = (long) regionImage.getNumX() * regionImage.getNumY() * regionImage.getNumZ();
        line(out, "regionImage.pixelPartition",
                "sumOfRegions=" + pixelSum, "totalPixels=" + totalPixels,
                "complete=" + (pixelSum == totalPixels));

        // sorted by regionIndex, which the implementation already assigns densely
        List<String> rows = new ArrayList<>();
        for (RegionImage.RegionInfo info : infos) {
            rows.add(String.format(Locale.ROOT, "  region index=%d pixelValue=%d numPixels=%d",
                    info.getRegionIndex(), info.getPixelValue(), info.getNumPixels()));
        }
        rows.sort(null);
        for (String r : rows) {
            out.append(r).append('\n');
        }
        // The per-pixel region assignment, which nothing else here would notice changing.
        line(out, "regionImage.encodedRegionIndexSHA",
                sha(regionImage.getShortEncodedRegionIndexImage()));
    }

    private static void describeSurfaces(StringBuilder out, GeometrySurfaceDescription gsd) {
        SurfaceCollection surfaces = gsd.getSurfaceCollection();
        if (surfaces == null) {
            line(out, "surfaceCollection", "none");
            return;
        }
        line(out, "surfaceCollection", "surfaces=" + surfaces.getSurfaceCount(),
                "nodes=" + surfaces.getNodeCount());

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        StringBuilder nodeText = new StringBuilder();
        for (int i = 0; i < surfaces.getNodeCount(); i++) {
            Node n = surfaces.getNodes(i);
            minX = Math.min(minX, n.getX());
            maxX = Math.max(maxX, n.getX());
            minY = Math.min(minY, n.getY());
            maxY = Math.max(maxY, n.getY());
            minZ = Math.min(minZ, n.getZ());
            maxZ = Math.max(maxZ, n.getZ());
            nodeText.append(quant(n.getX())).append(',')
                    .append(quant(n.getY())).append(',')
                    .append(quant(n.getZ())).append(';');
        }
        if (surfaces.getNodeCount() > 0) {
            line(out, "  nodeBounds",
                    "x=[" + fmt(minX) + "," + fmt(maxX) + "]",
                    "y=[" + fmt(minY) + "," + fmt(maxY) + "]",
                    "z=[" + fmt(minZ) + "," + fmt(maxZ) + "]");
        }
        line(out, "  nodeCoordsSHA", sha(nodeText.toString().getBytes(StandardCharsets.UTF_8)));

        double totalArea = 0;
        StringBuilder polyText = new StringBuilder();
        // Which two volume elements each membrane quad separates. This is the region adjacency the
        // mesh itself carries, and it is NOT implied by the node indices: a change that kept every
        // polygon in place but re-assigned its volume neighbours would otherwise pass unnoticed.
        StringBuilder volNeighbourText = new StringBuilder();
        for (int s = 0; s < surfaces.getSurfaceCount(); s++) {
            Surface surface = surfaces.getSurfaces(s);
            double area = 0;
            for (int p = 0; p < surface.getPolygonCount(); p++) {
                Polygon polygon = surface.getPolygons(p);
                area += polygon.getArea();
                for (Node n : polygon.getNodes()) {
                    polyText.append(n.getGlobalIndex()).append(',');
                }
                polyText.append(';');
                if (polygon instanceof Quadrilateral) {
                    Quadrilateral quad = (Quadrilateral) polygon;
                    volNeighbourText.append(quad.getVolIndexNeighbor1()).append(':')
                            .append(quad.getVolIndexNeighbor2()).append(';');
                }
            }
            totalArea += area;
            line(out, "  surface[" + s + "]",
                    "interiorRegion=" + surface.getInteriorRegionIndex(),
                    "exteriorRegion=" + surface.getExteriorRegionIndex(),
                    "polygons=" + surface.getPolygonCount(),
                    "area=" + fmt(area));
        }
        line(out, "  polygonNodeIndicesSHA", sha(polyText.toString().getBytes(StandardCharsets.UTF_8)));
        line(out, "  polygonVolumeNeighborsSHA",
                sha(volNeighbourText.toString().getBytes(StandardCharsets.UTF_8)));
        line(out, "  totalArea", fmt(totalArea));
        describeMembraneEdgeNeighbors(out, surfaces);
    }

    /**
     * The membrane connectivity graph: which membrane element abuts which, along which edge.
     *
     * Solvers use this for membrane diffusion, and RegionImage builds it in its own pass
     * (calculateNeighbors) from an edge map. None of it is implied by the polygon list, so without
     * this a change to adjacency would leave every other line of the golden untouched.
     */
    private static void describeMembraneEdgeNeighbors(StringBuilder out, SurfaceCollection surfaces) {
        ArrayList<RegionImage.MembraneEdgeNeighbor>[][] neighbors = surfaces.getMembraneEdgeNeighbors();
        if (neighbors == null) {
            line(out, "  membraneEdgeNeighbors", "none");
            return;
        }
        long total = 0;
        StringBuilder text = new StringBuilder();
        for (int s = 0; s < neighbors.length; s++) {
            if (neighbors[s] == null) {
                continue;
            }
            for (int p = 0; p < neighbors[s].length; p++) {
                List<RegionImage.MembraneEdgeNeighbor> list = neighbors[s][p];
                if (list == null) {
                    continue;
                }
                total += list.size();
                // Sorted: the adjacency SET is the meaningful thing, and the order the edge map
                // happens to yield it in is not something worth pinning.
                List<String> entries = new ArrayList<>();
                for (RegionImage.MembraneEdgeNeighbor n : list) {
                    RegionImage.MembraneElementIdentifier id = n.getMembraneElementIdentifier();
                    entries.add(String.format(Locale.ROOT, "%d/%d/%d-%d->%s",
                            s, p, n.edgeBaseNodeIndex, n.edgeOtherNodeIndex,
                            id == null ? "null"
                                    : id.surfaceIndex + "." + id.nonMasterPolygonIndex + "."
                                      + id.planePerpendicularToAxis));
                }
                entries.sort(null);
                for (String e : entries) {
                    text.append(e).append(';');
                }
            }
        }
        line(out, "  membraneEdgeNeighbors", "total=" + total,
                "SHA=" + sha(text.toString().getBytes(StandardCharsets.UTF_8)));
    }

    private static void describeGeometricRegions(StringBuilder out, GeometrySurfaceDescription gsd) {
        GeometricRegion[] regions = gsd.getGeometricRegions();
        if (regions == null) {
            line(out, "geometricRegions", "none");
            return;
        }
        line(out, "geometricRegions", String.valueOf(regions.length));
        List<String> rows = new ArrayList<>();
        for (GeometricRegion region : regions) {
            List<String> adjacent = new ArrayList<>();
            GeometricRegion[] adj = region.getAdjacentGeometricRegions();
            if (adj != null) {
                for (GeometricRegion a : adj) {
                    adjacent.add(a.getName());
                }
            }
            adjacent.sort(null);
            String type = region instanceof SurfaceGeometricRegion ? "surface"
                    : (region instanceof VolumeGeometricRegion ? "volume" : "other");
            rows.add(String.format(Locale.ROOT, "  %s %s size=%s adjacent=%s",
                    type, region.getName(), fmt(region.getSize()), adjacent));
        }
        rows.sort(null);
        for (String r : rows) {
            out.append(r).append('\n');
        }
    }

    private static void describeSurfaceClasses(StringBuilder out, GeometrySurfaceDescription gsd) {
        SurfaceClass[] classes = gsd.getSurfaceClasses();
        if (classes == null) {
            line(out, "surfaceClasses", "none");
            return;
        }
        line(out, "surfaceClasses", String.valueOf(classes.length));
        List<String> rows = new ArrayList<>();
        for (SurfaceClass surfaceClass : classes) {
            List<String> adjacent = new ArrayList<>();
            for (SubVolume sv : surfaceClass.getAdjacentSubvolumes()) {
                adjacent.add(sv.getName());
            }
            adjacent.sort(null);
            rows.add("  " + surfaceClass.getName() + " adjacent=" + adjacent);
        }
        rows.sort(null);
        for (String r : rows) {
            out.append(r).append('\n');
        }
    }

    // ------------------------------------------------------------------ helpers

    private static void line(StringBuilder out, String key, String... values) {
        out.append(key);
        for (String v : values) {
            out.append(' ').append(v);
        }
        out.append('\n');
    }

    /** Fixed-form, locale-independent, and never scientific notation for readability. */
    private static String fmt(double v) {
        if (Double.isNaN(v)) return "NaN";
        if (Double.isInfinite(v)) return v > 0 ? "Inf" : "-Inf";
        return String.format(Locale.ROOT, "%.9f", v);
    }

    private static String fmt(Double v) {
        return v == null ? "null" : fmt(v.doubleValue());
    }

    /** Quantise before hashing so a last-ulp difference cannot flip a golden. */
    private static String quant(double v) {
        return String.format(Locale.ROOT, "%.0f", Math.rint(v / QUANTUM));
    }

    static String sha(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                sb.append(String.format("%02x", d[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Path goldenDir() {
        return Path.of("src", "test", "resources", "cbit", "vcell", "geometry", "surface-golden");
    }

    public static Path goldenPath(String fixture) {
        return goldenDir().resolve(fixture + ".txt");
    }

    /** Writes every fixture's description to the golden directory. */
    public static void writeGoldens() throws Exception {
        Files.createDirectories(goldenDir());
        for (Map.Entry<String, GeometryFactory> e : fixtures().entrySet()) {
            String text = describe(e.getValue().create());
            Files.writeString(goldenPath(e.getKey()), text, StandardCharsets.UTF_8);
            System.out.printf("wrote %-38s %d bytes%n", e.getKey(), text.length());
        }
    }

    public static void main(String[] args) throws Exception {
        writeGoldens();
    }
}
