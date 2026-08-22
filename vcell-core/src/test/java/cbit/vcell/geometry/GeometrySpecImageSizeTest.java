package cbit.vcell.geometry;

import cbit.image.VCImageUncompressed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Extent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Limits on geometry images (#2021), where a stored 61,920,000 pixel geometry needed ~1.4 GB to
 * parse against a 1000 MB heap and terminated two prod api pods.
 *
 * The design these tests pin is grandfathering: a stored geometry LOADS whatever its size, and the
 * limits apply only where a NEW image is submitted. Vetoing on the load path instead would make
 * existing models impossible to open -- including the one from the incident, which loads today.
 */
@Tag("Fast")
public class GeometrySpecImageSizeTest {

    @AfterEach
    public void clearOverrides() {
        System.clearProperty(GeometrySpec.PROPERTY_NEW_IMAGE_SIZE_LIMIT);
        System.clearProperty(GeometrySpec.PROPERTY_NEW_IMAGE_REGION_LIMIT);
    }

    private static VCImageUncompressed image(int x, int y, int z) throws Exception {
        return new VCImageUncompressed(null, new byte[x * y * z], new Extent(1, 1, 1), x, y, z);
    }

    /** A cube whose voxels cycle through {@code numClasses} distinct pixel values. */
    private static VCImageUncompressed imageWithClasses(int edge, int numClasses) throws Exception {
        byte[] pixels = new byte[edge * edge * edge];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (byte) (i % numClasses);
        }
        return new VCImageUncompressed(null, pixels, new Extent(1, 1, 1), edge, edge, edge);
    }

    // ---- the load path stays permissive -------------------------------------------------

    /**
     * The test that protects backward compatibility. A geometry far above the submission limit
     * must still construct, because stored models contain exactly that and users must be able to
     * open them. If this ever fails, existing models have become unopenable.
     */
    @Test
    public void anImageOverTheSubmissionLimitStillLoads() throws Exception {
        int edge = 260;                                     // 17,576,000 px
        assertTrue((long) edge * edge * edge > GeometrySpec.getNewImageSizeLimit(),
                "this test is only meaningful if the image exceeds the submission limit");

        GeometrySpec spec = new GeometrySpec("stored_oversized", image(edge, edge, edge));
        assertNotNull(spec.getImage(), "a stored oversized geometry must still open");
    }

    @Test
    public void manyPixelClassesStillLoad() throws Exception {
        // Measured: memory is flat in pixel-class count -- 64 subvolumes is unremarkable. Nothing
        // about class count should block a load.
        GeometrySpec spec = new GeometrySpec("many_subvolumes", imageWithClasses(16, 64));
        assertEquals(64, spec.getImage().getNumPixelClasses());
    }

    @Test
    public void smallImageIsAccepted() throws Exception {
        GeometrySpec spec = new GeometrySpec("small", image(10, 10, 10));
        assertNotNull(spec.getImage(), "a 1,000 pixel image must be accepted");
    }

    // ---- the submission check ------------------------------------------------------------

    @Test
    public void anOrdinaryNewImageIsAccepted() throws Exception {
        assertNull(GeometrySpec.checkNewImageAcceptable(image(64, 64, 64), 3),
                "a normal segmented geometry must be accepted for saving");
    }

    @Test
    public void anOversizedNewImageIsRefused() throws Exception {
        System.setProperty(GeometrySpec.PROPERTY_NEW_IMAGE_SIZE_LIMIT, "1000");

        String reason = GeometrySpec.checkNewImageAcceptable(image(20, 20, 20), 3);   // 8,000 px
        assertNotNull(reason, "an image over the submission size limit must be refused");
        assertTrue(reason.contains("8000"), "reason should name the actual size: " + reason);
        assertTrue(reason.contains("1000"), "reason should name the limit: " + reason);
    }

    /**
     * Region count, not pixel-class count, is the guard against an unsegmented image. Measured on
     * a 256^3 volume: 64 pixel classes in 64 resolved regions costs 300 MB, while a 128-class
     * image whose shells fragment into 14,050 regions costs 1,652 MB. The class count does not
     * predict the cost; the region count does.
     */
    @Test
    public void aFragmentedNewImageIsRefused() throws Exception {
        String reason = GeometrySpec.checkNewImageAcceptable(image(64, 64, 64), 50000);
        assertNotNull(reason, "an image that fragments into many regions must be refused");
        assertTrue(reason.contains("50000"), "reason should name the region count: " + reason);
        assertTrue(reason.contains("segmented"),
                "reason should say what is actually wrong: " + reason);
    }

    @Test
    public void manySubvolumesAreNotTreatedAsFragmentation() throws Exception {
        // 64 subvolumes, 64 regions: legitimate, and cheap. A pixel-class limit would have
        // rejected this; a region limit must not.
        assertNull(GeometrySpec.checkNewImageAcceptable(imageWithClasses(16, 64), 64),
                "a genuinely multi-subvolume geometry must still be saveable");
    }

    @Test
    public void anUncomputedRegionCountIsNotGuessedAt() throws Exception {
        assertNull(GeometrySpec.checkNewImageAcceptable(image(64, 64, 64), -1),
                "when regions were never computed the region check must be skipped, not assumed");
    }

    @Test
    public void nonImageGeometriesAreUnaffected() {
        assertNull(GeometrySpec.checkNewImageAcceptable(null, -1),
                "an analytic/CSG geometry has no image and must not be refused");
    }

    @Test
    public void limitsAreConfigurable() throws Exception {
        assertEquals(GeometrySpec.NEW_IMAGE_SIZE_LIMIT_DEFAULT, GeometrySpec.getNewImageSizeLimit());
        assertEquals(GeometrySpec.NEW_IMAGE_REGION_LIMIT_DEFAULT, GeometrySpec.getNewImageRegionLimit());

        System.setProperty(GeometrySpec.PROPERTY_NEW_IMAGE_SIZE_LIMIT, "12345");
        System.setProperty(GeometrySpec.PROPERTY_NEW_IMAGE_REGION_LIMIT, "7");
        assertEquals(12345, GeometrySpec.getNewImageSizeLimit());
        assertEquals(7, GeometrySpec.getNewImageRegionLimit());

        assertNotNull(GeometrySpec.checkNewImageAcceptable(image(64, 64, 64), 8),
                "lowering the region limit must actually tighten the check");
    }

    /**
     * The default region limit must be generous against real segmentations -- a tissue image with
     * hundreds of separate cells is ordinary science, not an attack.
     */
    @Test
    public void theRegionLimitIsGenerousAgainstRealSegmentations() throws Exception {
        assertTrue(GeometrySpec.getNewImageRegionLimit() >= 500,
                "the region limit must leave room for a segmentation with many separate cells");
        assertNull(GeometrySpec.checkNewImageAcceptable(image(64, 64, 64), 500));
    }
}
