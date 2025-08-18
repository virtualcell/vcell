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
import org.vcell.pathway.persistence.PathwayReader;
import org.vcell.pathway.persistence.RDFXMLContext;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public void parseTest() throws IOException {

        Document document = XmlUtil.readXML(insulinPathwayFile);
        doWork(document);
        System.out.println("done parseTest");
    }

    // -------------------------------------------------------------------------------------------------------------

    private static void doWork(Document document) {

        RDFXMLContext rdfXmlContext = new RDFXMLContext();
        PathwayReader pathwayReader = new PathwayReader(rdfXmlContext);

        Element rootElement = document.getRootElement();
        assertEquals("rdf:RDF", rootElement.getQualifiedName(), "Expected root element <rdf:RDF>");

        Map<String, Integer> childCounts = new LinkedHashMap<>();
        for (Element child : rootElement.getChildren()) {
            String name = child.getName();
            childCounts.put(name, childCounts.getOrDefault(name, 0) + 1);
        }
        childCounts.forEach((name, count) ->
                System.out.println(name + " " + count)
        );

        System.out.println("starting parsing");
        PathwayModel pathwayModel = pathwayReader.parse(rootElement, null);
        System.out.println("ending parsing");

        Map<String, Integer> parsedCounts = new LinkedHashMap<>();
        for(BioPaxObject bpo : pathwayModel.getBiopaxObjects()) {

            if("XrefProxy".equals(bpo.getTypeLabel())) {
                continue;   // we skip the proxies, we only want the real things
            }
            String rawId = bpo.getIDShort();        // e.g. "#unificationXref5"
            String cleanedId = rawId.replaceFirst("^#", "") // remove leading #
                    .replaceFirst("\\d+$", "");             // remove trailing digits
            parsedCounts.put(cleanedId, parsedCounts.getOrDefault(cleanedId, 0) + 1);
        }
        parsedCounts.forEach((name, count) ->
                System.out.println(name + " " + count)
        );

        // ---- compare XML objects with BioPax (parsed) objects ------------------------

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
        System.out.println("Elements in XML but missing in parsed model:");
        missingInParsed.forEach(System.out::println);

        System.out.println("\nElements present in both:");
        matchedCounts.forEach((key, value) -> System.out.println(key + " → " + value));


        // ----------------------------------------------------------------------

        for (PhysicalEntityParticipant pe : pathwayModel.getObjects(PhysicalEntityParticipant.class)) {
            // here we should see the proxy
            System.out.println("PhysicalEntityParticipant: " + pe.getID());
        }

        pathwayModel.reconcileReferences(null);
        String text = pathwayModel.show(true);

        for (PhysicalEntity pe : pathwayModel.getObjects(PhysicalEntity.class)) {
            if (!(pe instanceof Protein ||
                    pe instanceof Complex ||
                    pe instanceof SmallMolecule ||
                    pe instanceof DnaRegion ||
                    pe instanceof RnaRegion)) {
                System.out.println("Unsubclassed PhysicalEntity: " + pe.getID());
            }
        }

        for (PhysicalEntityParticipant pe : pathwayModel.getObjects(PhysicalEntityParticipant.class)) {
            // here we should see the real PhysicalEntityParticipant
            System.out.println("PhysicalEntityParticipant: " + pe.getID());
        }

        System.out.println(text);
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
//            Document document = XmlUtil.readSanitizedXML(new File("CmyFile.xml"));    // for malformed xml files, like trailing garbage
//            Document document = XmlUtil.readXML(new File("C:\\TEMP\\pathway\\insulinPathway-5683177.xml"));
            Document document = XmlUtil.readXML(new File("C:/TEMP/pathway/egfrPathway-180292.xml"));
            doWork(document);
            System.out.println("done manual run");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
