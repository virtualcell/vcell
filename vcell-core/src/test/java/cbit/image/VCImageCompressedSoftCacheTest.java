package cbit.image;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Extent;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link VCImageCompressed} holds its inflated pixels through a {@link SoftReference}, so the
 * collector can reclaim them under memory pressure and they are re-inflated on demand. Real
 * geometry images compress 50-100x, so what stays resident between uses is about 1 MB rather than
 * 62 MB for the image that exhausted a prod api heap (#2021).
 *
 * The distinction these tests defend is soft versus weak. A weak reference is cleared at the next
 * GC whatever the heap looks like, which would make an image in active use re-inflate on
 * essentially every collection. Nothing about the class's ordinary behaviour would look wrong if
 * someone changed it, so {@link #ordinaryGcDoesNotDiscardThePixels} exists to fail if they do.
 */
@Tag("Fast")
public class VCImageCompressedSoftCacheTest {

    private static final int EDGE = 40;                     // 64,000 px

    @AfterEach
    public void clearOverride() {
        System.clearProperty(VCImageCompressed.PROPERTY_SOFT_PIXEL_CACHE);
    }

    private static byte[] pattern() {
        byte[] pixels = new byte[EDGE * EDGE * EDGE];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (byte) ((i / 97) % 5);              // segmented-ish, so it compresses
        }
        return pixels;
    }

    private static VCImageCompressed image() throws Exception {
        return new VCImageCompressed(new VCImageUncompressed(
                null, pattern(), new Extent(1, 1, 1), EDGE, EDGE, EDGE));
    }

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

    @Test
    public void thePixelsAreCorrect() throws Exception {
        assertArrayEquals(pattern(), image().getPixels(),
                "inflating must reproduce the original pixels");
    }

    @Test
    public void repeatedCallsReturnTheSameArrayWhileItIsCached() throws Exception {
        VCImageCompressed image = image();
        assertSame(image.getPixels(), image.getPixels(),
                "a cached inflate must not be repeated on every call");
    }

    /**
     * Soft, not weak. An ordinary collection must NOT discard the pixels — if this fails, someone
     * has changed the reference type and every geometry in use will re-inflate on every GC.
     */
    @Test
    public void ordinaryGcDoesNotDiscardThePixels() throws Exception {
        VCImageCompressed image = image();
        byte[] first = image.getPixels();
        int identity = System.identityHashCode(first);
        first = null;

        collect();                                          // no memory pressure

        assertEquals(identity, System.identityHashCode(image.getPixels()),
                "a plain GC must not clear the cache — SoftReference, not WeakReference");
    }

    /**
     * When the reference IS cleared, the pixels come back correctly. Cleared explicitly rather than
     * by exhausting the heap: forcing a real OutOfMemoryError inside a shared test JVM to make the
     * collector clear soft references would put every other test in the run at risk.
     */
    @Test
    public void clearedPixelsAreReinflatedCorrectly() throws Exception {
        VCImageCompressed image = image();
        byte[] before = image.getPixels().clone();

        clearSoftReference(image);

        byte[] after = image.getPixels();
        assertNotNull(after);
        assertArrayEquals(before, after, "re-inflating must reproduce the same pixels");
    }

    @Test
    public void nullifyStillWorks() throws Exception {
        VCImageCompressed image = image();
        byte[] before = image.getPixels().clone();
        image.nullifyUncompressedPixels();
        assertArrayEquals(before, image.getPixels(),
                "nullifyUncompressedPixels must drop the cache without losing the pixels");
    }

    @Test
    public void theCompressedFormIsAlwaysHeld() throws Exception {
        VCImageCompressed image = image();
        image.getPixels();
        clearSoftReference(image);
        assertNotNull(image.getPixelsCompressed(),
                "the compressed bytes must be held strongly — they are what makes rehydration possible");
        assertTrue(image.getPixelsCompressed().length < EDGE * EDGE * EDGE,
                "the whole point is that the retained form is smaller than the inflated one");
    }

    @Test
    public void softCachingCanBeSwitchedOff() throws Exception {
        System.setProperty(VCImageCompressed.PROPERTY_SOFT_PIXEL_CACHE, "false");
        VCImageCompressed image = image();
        byte[] pixels = image.getPixels();

        // With the escape hatch set the pixels are held strongly, so clearing the soft reference
        // must make no difference at all.
        clearSoftReference(image);
        assertSame(pixels, image.getPixels(),
                "with soft caching off the array must be held strongly");
    }

    /**
     * Clear the reference the way the collector would, without needing memory pressure.
     *
     * Typed as {@link Reference}, not {@link SoftReference}, on purpose. When the field's type was
     * temporarily switched to WeakReference to check that these tests detect it, this helper threw
     * ClassCastException and turned two unrelated tests into errors -- burying the one failure that
     * actually explains the problem, {@link #ordinaryGcDoesNotDiscardThePixels}.
     */
    private static void clearSoftReference(VCImageCompressed image) throws Exception {
        Field f = VCImageCompressed.class.getDeclaredField("softPixels");
        f.setAccessible(true);
        Reference<?> ref = (Reference<?>) f.get(image);
        if (ref != null) {
            ref.clear();
        }
    }
}
