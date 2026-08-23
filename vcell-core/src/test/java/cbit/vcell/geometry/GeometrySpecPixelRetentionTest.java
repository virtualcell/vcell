package cbit.vcell.geometry;

import cbit.image.VCImage;
import cbit.image.VCImageCompressed;
import cbit.image.VCImageUncompressed;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Extent;

import java.lang.ref.WeakReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GeometrySpec must not hold its own reference to the image's inflated pixel array.
 *
 * It used to: a transient {@code uncompressedPixels} field cached
 * {@code getImage().getPixels()}, making GeometrySpec a second strong reference to the very same
 * array {@link VCImageCompressed} already caches. The inflated pixels then stayed reachable for as
 * long as the geometry did, whatever the image did with its own copy — 62 MB per geometry at the
 * size that killed two prod api pods (#2021), and the thing that would defeat any later attempt to
 * make the image's own cache reclaimable.
 *
 * These tests assert reachability rather than structure, so they would still catch the problem if
 * the reference came back somewhere other than that field.
 */
@Tag("Fast")
public class GeometrySpecPixelRetentionTest {

    private static final int EDGE = 40;             // 64,000 px — enough to be a real array

    private static VCImageCompressed compressedImage() throws Exception {
        byte[] pixels = new byte[EDGE * EDGE * EDGE];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (byte) (i % 3);             // compressible, and >1 pixel class
        }
        VCImageUncompressed raw = new VCImageUncompressed(null, pixels, new Extent(1, 1, 1),
                EDGE, EDGE, EDGE);
        return new VCImageCompressed(raw);
    }

    /** Ask the collector several times; one call is not a guarantee. */
    private static void collect() {
        for (int i = 0; i < 6; i++) {
            System.gc();
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * The regression this fix is for. Once the image drops its own cache, nothing should keep the
     * inflated array alive — before the fix, GeometrySpec did.
     */
    @Test
    public void geometrySpecDoesNotPinTheInflatedPixels() throws Exception {
        VCImageCompressed image = compressedImage();
        GeometrySpec spec = new GeometrySpec("retention", image);

        byte[] pixels = spec.getUncompressedPixels();
        assertNotNull(pixels);
        assertEquals(EDGE * EDGE * EDGE, pixels.length);

        WeakReference<byte[]> watch = new WeakReference<>(pixels);
        pixels = null;
        image.nullifyUncompressedPixels();          // the image releases its own cache
        collect();

        // assertTrue, not assertNull: on failure assertNull renders the whole 64,000-byte array
        // into the message, which buries the actual problem under 375 KB of output.
        assertTrue(watch.get() == null,
                "GeometrySpec must not hold a second reference to the image's inflated pixels");

        // and the geometry is still usable afterwards — the array is regenerated on demand
        assertNotNull(spec.getImage(), "the spec must still hold the image itself");
        assertEquals(EDGE * EDGE * EDGE, spec.getUncompressedPixels().length,
                "the pixels must be recoverable after the cache was dropped");
    }

    /**
     * The negative control for the test above: while the image is still holding its own cache the
     * array must NOT be collectable. Without this, a test that always passed — because nothing ever
     * kept the array — would look like a working fix.
     */
    @Test
    public void theImagesOwnCacheStillPinsThePixels() throws Exception {
        VCImageCompressed image = compressedImage();
        GeometrySpec spec = new GeometrySpec("retention", image);

        byte[] pixels = spec.getUncompressedPixels();
        WeakReference<byte[]> watch = new WeakReference<>(pixels);
        pixels = null;
        collect();                                   // note: no nullifyUncompressedPixels()

        assertTrue(watch.get() != null,
                "while VCImageCompressed still caches the pixels they must stay reachable — "
                        + "if this fails the other test proves nothing");
        assertNotNull(image);
    }

    @Test
    public void repeatedCallsReturnTheImagesCachedArray() throws Exception {
        VCImage image = compressedImage();
        GeometrySpec spec = new GeometrySpec("identity", image);

        // Delegating rather than caching must not mean re-inflating on every call: the image's own
        // cache should hand back the same array.
        assertSame(spec.getUncompressedPixels(), spec.getUncompressedPixels());
        assertSame(image.getPixels(), spec.getUncompressedPixels(),
                "the spec should hand back the image's array, not a copy of it");
    }

    @Test
    public void contentSurvivesTheRoundTrip() throws Exception {
        VCImageCompressed image = compressedImage();
        GeometrySpec spec = new GeometrySpec("content", image);

        byte[] before = spec.getUncompressedPixels().clone();
        image.nullifyUncompressedPixels();
        byte[] after = spec.getUncompressedPixels();

        assertArrayEquals(before, after,
                "re-inflating after the cache was dropped must reproduce the same pixels");
    }

    @Test
    public void aGeometryWithoutAnImageIsUnaffected() throws Exception {
        GeometrySpec spec = new GeometrySpec("analytic", 3);
        assertNull(spec.getImage());
        assertNull(spec.getUncompressedPixels(),
                "a non-image geometry must still return null rather than throwing");
    }
}
