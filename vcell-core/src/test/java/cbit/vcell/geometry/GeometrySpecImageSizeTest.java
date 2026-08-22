package cbit.vcell.geometry;

import cbit.image.VCImageUncompressed;
import cbit.vcell.resource.PropertyLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Extent;
import org.vcell.util.Origin;

import java.beans.PropertyVetoException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The image-size veto in {@link GeometrySpec#vetoableChange}. Regression cover for #2021, where a
 * 61,920,000 pixel image exhausted the heap and terminated two prod api pods because the veto had
 * been commented out since 2017.
 *
 * The interesting case is the middle one: the veto also fires when deserializing saved geometries,
 * so an image above the historical 4,000,000 limit but below the enforced ceiling must still be
 * ACCEPTED, or existing models stop opening.
 */
@Tag("Fast")
public class GeometrySpecImageSizeTest {

    @AfterEach
    public void clearOverride() {
        System.clearProperty(GeometrySpec.PROPERTY_IMAGE_SIZE_LIMIT);
    }

    private static VCImageUncompressed image(int x, int y, int z) throws Exception {
        return new VCImageUncompressed(null, new byte[x * y * z],
                new Extent(1, 1, 1), x, y, z);
    }

    @Test
    public void smallImageIsAccepted() throws Exception {
        GeometrySpec spec = new GeometrySpec("small", image(10, 10, 10));
        assertNotNull(spec.getImage(), "a 1,000 pixel image must be accepted");
    }

    @Test
    public void imageOverHistoricalLimitButUnderCeilingIsStillAccepted() throws Exception {
        // 200^3 = 8,000,000 -- twice the historical IMAGE_SIZE_LIMIT of 4,000,000, but well under
        // the enforced ceiling. Enforcing the historical value here would break saved models.
        int n = 200;
        assertTrue((long) n * n * n > GeometrySpec.IMAGE_SIZE_LIMIT);
        assertTrue((long) n * n * n < GeometrySpec.getImageSizeLimit());

        GeometrySpec spec = new GeometrySpec("large-but-ok", image(n, n, n));
        assertNotNull(spec.getImage(),
                "images between the historical limit and the enforced ceiling must still load");
    }

    @Test
    public void imageOverTheCeilingIsVetoed() throws Exception {
        // keep the allocation small: lower the ceiling rather than build a 62MP image
        System.setProperty(GeometrySpec.PROPERTY_IMAGE_SIZE_LIMIT, "1000");

        GeometrySpec spec = new GeometrySpec("veto", 3);
        PropertyVetoException e = assertThrows(PropertyVetoException.class,
                () -> spec.setImage(image(20, 20, 20)),   // 8,000 pixels > 1,000
                "an image over the enforced ceiling must be vetoed, not merely logged");

        assertTrue(e.getMessage().contains("8000"), "message should name the actual size: " + e.getMessage());
        assertTrue(e.getMessage().contains("1000"), "message should name the limit: " + e.getMessage());
    }

    @Test
    public void ceilingIsConfigurable() {
        assertEquals(GeometrySpec.IMAGE_SIZE_LIMIT_DEFAULT, GeometrySpec.getImageSizeLimit(),
                "with no override the default ceiling applies");

        System.setProperty(GeometrySpec.PROPERTY_IMAGE_SIZE_LIMIT, "12345");
        assertEquals(12345, GeometrySpec.getImageSizeLimit(),
                "the ceiling must be tunable without a code change");
    }
}
