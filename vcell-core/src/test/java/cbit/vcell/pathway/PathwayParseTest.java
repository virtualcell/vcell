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
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.PathwayModel;
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

        // ------------------------------------------------------------

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

        pathwayModel.reconcileReferences(null);
        String text = pathwayModel.show(true);




        System.out.println(text);
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
