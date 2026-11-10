package org.vcell.restq.db;

import cbit.image.VCImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.util.BigString;
import org.vcell.util.document.KeyValue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Geometry-memory plan (docs/geometry-memory.md, section 5.10): saving a NEW BioModel whose applications share
 * one image wrote one vc_image row per application.
 *
 * VCML inlines a full copy of the geometry, image included, into every application, so parsing the document
 * gives N distinct-but-equal VCImage objects. ServerDocumentManager.saveBioModel kept its
 * memory-to-database map keyed by object identity, so each copy of a never-saved image took the insert branch,
 * and each insert after the first collided on the name and was renamed with a random suffix. Production holds
 * ~944 surplus rows written this way; "10spines", the image in this fixture, has 15 rows for 15 geometries.
 *
 * The fixture is published biomodel 31584491: three applications over one 3D image.
 */
@QuarkusTest
public class SaveBioModelImageRowsTest {

    private static final String FIXTURE = "/org/vcell/restq/db/three_apps_one_image.vcml";

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    private DatabaseServerImpl databaseServer;

    @BeforeAll
    public static void setupConfig() {
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void setup() throws Exception {
        databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }

    @AfterEach
    public void cleanup() throws Exception {
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }

    /** The fixture as a never-saved document: every version cleared, so every image takes the insert branch. */
    private static BioModel newBioModelFromFixture(String name) throws Exception {
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(TestEndpointUtils.getResourceString(FIXTURE)));
        bioModel.clearVersion();
        bioModel.visitChildVersionables(new BioModel.ClearVersion());
        bioModel.setName(name);
        return bioModel;
    }

    private BioModel save(BioModel bioModel) throws Exception {
        BigString saved = databaseServer.saveBioModel(TestEndpointUtils.administratorUser,
                new BigString(XmlHelper.bioModelToXML(bioModel)), null);
        return XmlHelper.XMLToBioModel(new XMLSource(saved.toString()));
    }

    private int countRows(String table) throws Exception {
        Connection connection = agroalConnectionFactory.getConnection(new Object());
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        } finally {
            agroalConnectionFactory.release(connection, new Object());
        }
    }

    private static Set<KeyValue> imageKeys(BioModel bioModel) {
        Set<KeyValue> keys = new HashSet<>();
        for (SimulationContext sc : bioModel.getSimulationContexts()) {
            VCImage image = sc.getGeometry().getGeometrySpec().getImage();
            Assertions.assertNotNull(image, "every application of the fixture uses the image geometry");
            Assertions.assertNotNull(image.getKey(), "a saved image has a key");
            keys.add(image.getKey());
        }
        return keys;
    }

    @Test
    public void newModelWithOneImageInThreeApplicationsWritesOneImageRow() throws Exception {
        BioModel bioModel = newBioModelFromFixture("three_apps_one_image");
        Assertions.assertEquals(3, bioModel.getNumSimulationContexts());

        BioModel saved = save(bioModel);

        Assertions.assertEquals(1, countRows("vc_image"), "one image, so one vc_image row");
        Assertions.assertEquals(1, imageKeys(saved).size(), "all three applications reference that one row");
        Assertions.assertEquals("10spines", saved.getSimulationContext(0).getGeometry().getGeometrySpec().getImage().getName(),
                "the image keeps its name; nothing collided, so nothing was renamed");
    }

    @Test
    public void resavingUnchangedWritesNoImageRows() throws Exception {
        BioModel saved = save(newBioModelFromFixture("three_apps_one_image"));
        int imageRows = countRows("vc_image");

        // any change, so the save is not refused as a no-op; the images are untouched
        saved.setDescription("edited " + System.nanoTime());
        BioModel resaved = save(saved);

        Assertions.assertEquals(imageRows, countRows("vc_image"), "an unchanged image is not inserted again");
        Assertions.assertEquals(imageKeys(saved), imageKeys(resaved), "the applications keep the same image row");
    }

    @Test
    public void distinctImagesStayDistinct() throws Exception {
        BioModel bioModel = newBioModelFromFixture("three_apps_two_images");
        // a genuinely different image in the last application: same pixels, different name
        VCImage last = bioModel.getSimulationContext(2).getGeometry().getGeometrySpec().getImage();
        last.setName("10spines_copy");

        BioModel saved = save(bioModel);

        Assertions.assertEquals(2, countRows("vc_image"), "two different images, two rows");
        Assertions.assertEquals(2, imageKeys(saved).size());
    }
}
