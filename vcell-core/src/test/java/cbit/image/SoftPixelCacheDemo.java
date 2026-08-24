package cbit.image;

import org.vcell.util.Extent;

import java.util.ArrayList;
import java.util.List;

/**
 * Does holding the inflated pixels softly actually change the outcome under the conditions of the
 * #2021 incident? Not a unit test — it deliberately drives the JVM to the edge of its heap, which
 * is not something to do inside a shared test run.
 *
 * Recreates the shape of the incident: N geometries in one document, each holding the same size of
 * image, all reachable at once. With the pixels held strongly this exhausts the heap exactly as
 * production did. With them held softly the collector reclaims what it needs and the work
 * completes, re-inflating on demand.
 *
 * Run both sides:
 *
 * <pre>
 *   java -Xmx256m -Dvcell.image.softPixelCache=false -cp ... cbit.image.SoftPixelCacheDemo 11 220
 *   java -Xmx256m -Dvcell.image.softPixelCache=true  -cp ... cbit.image.SoftPixelCacheDemo 11 220
 * </pre>
 *
 * Args: number of images, and the cube edge (pixels per image is edge^3).
 */
public class SoftPixelCacheDemo {

    private static String fmt(long b) {
        return String.format("%,.1f MB", b / (1024.0 * 1024.0));
    }

    private static long usedHeap() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    /** Segmented-looking content: a few large regions, so it compresses the way real images do. */
    private static byte[] pattern(int edge, int seed) {
        byte[] pixels = new byte[edge * edge * edge];
        double c = edge / 2.0, r = edge * (0.30 + 0.02 * seed);
        int i = 0;
        for (int z = 0; z < edge; z++) {
            for (int y = 0; y < edge; y++) {
                for (int x = 0; x < edge; x++, i++) {
                    double d = (x - c) * (x - c) + (y - c) * (y - c) + (z - c) * (z - c);
                    pixels[i] = (byte) (d < r * r ? 1 : 0);
                }
            }
        }
        return pixels;
    }

    public static void main(String[] args) throws Exception {
        int count = args.length > 0 ? Integer.parseInt(args[0]) : 11;
        int edge = args.length > 1 ? Integer.parseInt(args[1]) : 220;

        boolean soft = Boolean.parseBoolean(System.getProperty("vcell.image.softPixelCache", "true"));
        long pixelsEach = (long) edge * edge * edge;

        System.out.printf("soft pixel cache: %s%n", soft ? "ON" : "OFF");
        System.out.printf("max heap        : %s%n", fmt(Runtime.getRuntime().maxMemory()));
        System.out.printf("%d images x %,d px = %s if all held inflated%n%n",
                count, pixelsEach, fmt(count * pixelsEach));

        List<VCImageCompressed> images = new ArrayList<>();
        try {
            for (int i = 0; i < count; i++) {
                VCImageCompressed img = new VCImageCompressed(new VCImageUncompressed(
                        null, pattern(edge, i), new Extent(1, 1, 1), edge, edge, edge));
                images.add(img);
                // touch the pixels, as parsing a geometry does
                long checksum = 0;
                byte[] px = img.getPixels();
                for (int k = 0; k < px.length; k += 4096) checksum += px[k];
                System.out.printf("  image %2d/%d  compressed %-10s  heap now %-11s  (checksum %d)%n",
                        i + 1, count, fmt(img.getPixelsCompressed().length), fmt(usedHeap()), checksum);
            }
        } catch (OutOfMemoryError e) {
            System.out.printf("%nOUT OF MEMORY after %d of %d images — this is the incident%n",
                    images.size(), count);
            System.exit(3);
        }

        // Everything is still reachable and still correct.
        long total = 0;
        for (VCImageCompressed img : images) {
            total += img.getPixels().length;
        }
        System.out.printf("%nCOMPLETED all %d images; re-read %s of pixels on demand%n",
                count, fmt(total));
        System.out.printf("final heap: %s%n", fmt(usedHeap()));
    }
}
