package cbit.vcell.pathway;

import cbit.util.xml.XmlUtil;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.pathway.*;
import org.vcell.pathway.persistence.BiopaxProxy;
import org.vcell.pathway.persistence.PathwayReader;
import org.vcell.pathway.persistence.RDFXMLContext;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class PathwayParseTest {

    static File insulinPathwayFile;

    @BeforeAll
    public static void setUp() throws IOException {
        insulinPathwayFile = File.createTempFile("SimID_284673710_0_", ".ida");
        // vcell\vcell-core\src\test\resources\org\vcell\pathway
        Resources.asByteSource(Resources.getResource("org/vcell/pathway/insulinPathway-5683177.xml"))
                .copyTo(Files.asByteSink(insulinPathwayFile));
    }

    @AfterAll
    public static void tearDown() {

        insulinPathwayFile.delete();
    }

// -----------------------------------------------------------------------------------------------------------------

    @Test
    public void pathwayParseTest() throws IOException {

        Document document = XmlUtil.readXML(insulinPathwayFile);
        pathwayParse(document);
    }

    // -------------------------------------------------------------------------------------------------------------

    private static void pathwayParse(Document document) {

        RDFXMLContext rdfXmlContext = new RDFXMLContext();
        PathwayReader pathwayReader = new PathwayReader(rdfXmlContext);

        Element rootElement = document.getRootElement();
        assertEquals("rdf:RDF", rootElement.getQualifiedName(), "Expected root element <rdf:RDF>");

        Map<String, Integer> childCounts = new LinkedHashMap<>();
        for (Element child : rootElement.getChildren()) {
            String name = child.getName();
            childCounts.put(name, childCounts.getOrDefault(name, 0) + 1);
        }
        // check that we found a few children that are of particular interes to us
        assertTrue(childCounts.getOrDefault("biochemicalReaction", 0) > 0, "Expected at least one biochemicalReaction element");
        assertTrue(childCounts.getOrDefault("physicalEntityParticipant", 0) > 0, "Expected at least one physicalEntityParticipant element");
        assertTrue(childCounts.getOrDefault("sequenceParticipant", 0) > 0, "Expected at least one sequenceParticipant element");

        PathwayModel pathwayModel = pathwayReader.parse(rootElement, null);

        int proxyCount = 0;
        Map<String, Integer> parsedCounts = new LinkedHashMap<>();
        for(BioPaxObject bpo : pathwayModel.getBiopaxObjects()) {

            if(bpo instanceof BiopaxProxy.RdfObjectProxy) {
                proxyCount++;
                continue;   // we skip the proxies, we only want the real things
            }
            String rawId = bpo.getIDShort();        // e.g. "#unificationXref5"
            String cleanedId = rawId.replaceFirst("^#", "") // remove leading #
                    .replaceFirst("\\d+$", "");             // remove trailing digits
            parsedCounts.put(cleanedId, parsedCounts.getOrDefault(cleanedId, 0) + 1);
        }
        // check that we parsed these children
        assertTrue(parsedCounts.getOrDefault("biochemicalReaction", 0) > 0, "Expected at least one parsed biochemicalReaction");
        assertTrue(parsedCounts.getOrDefault("physicalEntityParticipant", 0) > 0, "Expected at least one parsed physicalEntityParticipant");
        assertTrue(parsedCounts.getOrDefault("sequenceParticipant", 0) > 0, "Expected at least one parsed sequenceParticipant");
        assertTrue(proxyCount > 0, "Expected at least one RdfObjectProxy");

        List<String> missingInParsed = new ArrayList<>();
        for (String key : childCounts.keySet()) {
            if (!parsedCounts.containsKey(key)) {
                missingInParsed.add(key);
            }
        }
        Map<String, String> matchedCounts = new LinkedHashMap<>();
        for (String key : childCounts.keySet()) {
            if (parsedCounts.containsKey(key)) {
                int xmlCount = childCounts.get(key);
                int parsedCount = parsedCounts.get(key);
                matchedCounts.put(key, "XML: " + xmlCount + ", Parsed: " + parsedCount);
            }
        }
        // ---- compare counts for XML objects and BioPax (parsed) objects ------------------------
        assertEquals(childCounts.get("physicalEntityParticipant"), parsedCounts.get("physicalEntityParticipant"),
                "Mismatch in count for physicalEntityParticipant between XML and parsed model");
        assertEquals(childCounts.get("sequenceParticipant"), parsedCounts.get("sequenceParticipant"),
                "Mismatch in count for sequenceParticipant between XML and parsed model");


        pathwayModel.reconcileReferences(null);
        pathwayModel.refreshParentMap();
        String text = pathwayModel.show(true);

        //
        Set<PhysicalEntityParticipant> participants = pathwayModel.getObjects(PhysicalEntityParticipant.class);
        assertTrue(participants.isEmpty(), "Expected no remaining PhysicalEntityParticipant instances after reconciliation");

        // all PhysicalEntities should be subclassed
        // for many models this may actually fail, it's quite a common occurrence to use raw PhysicalEntities
        // we may want to keep this commented out
//        for (PhysicalEntity pe : pathwayModel.getObjects(PhysicalEntity.class)) {
//            if (!(pe instanceof Protein ||
//                    pe instanceof Complex ||
//                    pe instanceof SmallMolecule ||
//                    pe instanceof DnaRegion ||
//                    pe instanceof RnaRegion)) {
//                fail("Found unsubclassed PhysicalEntity: " + pe.getID());   // throw an assertion
//            }
//        }
    }

    @Test
    public void testXXEProtection_blocksExternalEntity() {
        String maliciousXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE foo [\n" +
                "  <!ELEMENT foo ANY >\n" +
                "  <!ENTITY xxe SYSTEM \"file:///etc/passwd\" >\n" +
                "]>\n" +
                "<foo>&xxe;</foo>";

        Reader reader = new StringReader(maliciousXml);

        try {
            Document doc = XmlUtil.readXML(reader, null, null, null);
            String content = doc.getRootElement().getText();

            // AI generated test, checks for vulnerabilities
            // If XXE is blocked, content should not contain sensitive data
            // It may be empty or contain literal "&xxe;" depending on parser behavior
            // If this assertion fails, it means:
            // - the content string contains either "root:" or "bin:"
            // - these are typical markers of external entity resolution, often from XML content
            // - it suggests that your parser did not properly block or sanitize external entities, which can lead to:
            //     - security vulnerabilities (e.g. XXE attacks)
            //     - unexpected file access or leakage
            //     - broken isolation in our data pipeline
            assertFalse(content.contains("root:") || content.contains("bin:"), "External entity was resolved!");
       } catch (RuntimeException e) {
            // Expected: parser may throw due to disallowed DOCTYPE
            assertTrue(e.getMessage().contains("DOCTYPE") || e.getMessage().contains("entity"));
        }
    }



    public static void main(String args[]) {
        try {
//            Document document = XmlUtil.readSanitizedXML(new File("MyFile.xml"));    // for malformed xml files, like trailing garbage
//            Document document = XmlUtil.readXML(new File("C:\\TEMP\\pathway\\insulinPathway-5683177.xml"));   // Defective ABCC8 does not form functional KATP
//            Document document = XmlUtil.readXML(new File("C:/TEMP/pathway/egfrPathway-180292.xml"));
            Document document = XmlUtil.readXML(new File("C:/TEMP/pathway/R-HSA-9615017.xml")); // insulin pathway: FOXO mediated transcription of...
            pathwayParse(document);
            System.out.println("done manual run");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
