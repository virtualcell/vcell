package cbit.vcell.pathway;

import cbit.util.xml.XmlUtil;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.ode.ODESolverResultSet;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Attribute;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.persistence.PathwayIOUtil;
import org.vcell.pathway.persistence.RDFXMLContext;
import org.xml.sax.SAXException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@Tag("Fast")
public class PathwaySearchTest {

    @BeforeAll
    public static void setUp() throws IOException {

    }

    @AfterAll
    public static void tearDown() {

    }

    /*
    pathway = {Pathway@14156} "[Pathway: primaryId="http://bioregistry.io/reactome:R-HSA-5683177"; name="Defective ABCC8 can cause hypo- and hyper-glycemias";\ndataSource=[DataSource: primaryId="pc14:reactome"; name="pc14:reactome"]]"
     primaryId = "http://bioregistry.io/reactome:R-HSA-5683177"
     name = "Defective ABCC8 can cause hypo- and hyper-glycemias"
     organism = {Organism@14223} "[Organism: ncbiOrganismId="http://bioregistry.io/ncbitaxon:9606"; commonName="Human"; speciesName="Homo sapiens"]"
     dataSource = {DataSource@14224} "[DataSource: primaryId="pc14:reactome"; name="pc14:reactome"]"

    corresponds to insulinPathway-5683177.xml
    */
    @Test
    public void pathwayDownloadTest() throws MalformedURLException {
        String pathwayId = "5683177";  // Reactome pathway ID
        final URL url = new URL("https://reactome.org/ReactomeRESTfulAPI/RESTfulWS/biopaxExporter/Level2/" + pathwayId);

        String ERROR_CODE_TAG = "error_code";
        String contentString = downloadBytes(url, Duration.ofSeconds(10));      // download

        // assert response is XML
        assertTrue(contentString.trim().startsWith("<?xml"), "Response does not start with XML declaration");

        // parse XML
        org.jdom2.Document jdomDocument = XmlUtil.stringToXML(contentString, null);
        org.jdom2.Element rootElement = jdomDocument.getRootElement();
        assertNotNull(rootElement, "Root element is null");

        // check for error code
        String errorCode = rootElement.getChildText(ERROR_CODE_TAG);
        assertNull(errorCode, "Reactome returned error_code: " + errorCode);

        // assert xml:base contains pathway ID
        String xmlBase = rootElement.getAttributeValue("base", org.jdom2.Namespace.XML_NAMESPACE);
        assertNotNull(xmlBase, "Missing xml:base attribute");
        assertTrue(xmlBase.contains(pathwayId), "xml:base does not contain pathway ID: " + pathwayId);

        // assert xmlns:bp refers to biopax level2
        assertEquals(
                "http://www.biopax.org/release/biopax-level2.owl#",
                rootElement.getNamespace("bp").getURI(),
                "Expected BioPAX Level 2 namespace for prefix 'bp'"
        );

        // assert element Ontology imports biopax lvl2
        Namespace owlNs = Namespace.getNamespace("http://www.w3.org/2002/07/owl#");
        Namespace rdfNs = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");

        Element ontologyEl = rootElement.getChild("Ontology", owlNs);
        assertNotNull(ontologyEl, "Missing <owl:Ontology> element");

        List<Element> imports = ontologyEl.getChildren("imports", owlNs);
        assertFalse(imports.isEmpty(), "No <owl:imports> elements found under <owl:Ontology>");

        boolean foundLevel2Import = imports.stream()
                .anyMatch(el -> "http://www.biopax.org/release/biopax-level2.owl".equals(el.getAttributeValue("resource", rdfNs)));

        assertTrue(foundLevel2Import, "Expected owl:imports to reference BioPAX Level 2 ontology");


        // assert pathway name contains expected substring
        List<org.jdom2.Element> pathways = rootElement.getChildren("pathway", Namespace.getNamespace("http://www.biopax.org/release/biopax-level2.owl#"));
        assertFalse(pathways.isEmpty(), "No <bp:pathway> elements found");

        String pathwayName = "Defective ABCC8 can cause hypo";      // pathway name substring
        boolean nameMatchFound = pathways.stream()
                .anyMatch(p -> {
                    String name = p.getChildText("NAME", Namespace.getNamespace("http://www.biopax.org/release/biopax-level2.owl#"));
                    return name != null && name.contains(pathwayName);
                });
        assertTrue(nameMatchFound, "The pathway name doesn't contain the expected substring: '" + pathwayName + "'");
        System.out.println("pathwayDownloadTest - done");
    }


    /*
    pathway = {Pathway@14297} "[Pathway: primaryId="http://bioregistry.io/reactome:R-HSA-180292"; name="GAB1 signalosome";\ndataSource=[DataSource: primaryId="pc14:reactome"; name="pc14:reactome"]]"
     primaryId = "http://bioregistry.io/reactome:R-HSA-180292"
     name = "GAB1 signalosome"
     organism = {Organism@14309} "[Organism: ncbiOrganismId="http://bioregistry.io/ncbitaxon:9606"; commonName="Human"; speciesName="Homo sapiens"]"
     dataSource = {DataSource@14310} "[DataSource: primaryId="pc14:reactome"; name="pc14:reactome"]"

    corresponds to egfrPathway-180292.xml
     */
// TODO: another possible test for a much larger file, probably not worth it



    // This test will query for insulin and receives a list of insulin pathways
    // returns the searchResponse.xml resource
    @Test
    public void searchTest() throws IOException {

        String searchText = "Insulin";
        String encodedQ = URLEncoder.encode('"' + searchText + '"', StandardCharsets.UTF_8.name());
        String uri = "https://www.pathwaycommons.org/pc2/search?"
                + "q=" + encodedQ
                + "&type=pathway";
        System.out.println("Query URI: " + uri);

        HttpURLConnection conn = (HttpURLConnection) new URL(uri).openConnection();
        conn.setRequestProperty("Accept", "application/xml");

        String content = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));  // no leading newline

        // assert response is XML
        String contentType = conn.getContentType();
        assertNotNull(contentType, "Content-Type header is missing");
        assertTrue(contentType.contains("xml"), "Expected XML response, but got: " + contentType);

        // parse with JDOM2
        org.jdom2.Document doc = XmlUtil.stringToXML(content, null);
        org.jdom2.Element root = doc.getRootElement();
        assertEquals("searchResponse", root.getName(), "Unexpected root element");

        List<org.jdom2.Element> hits = root.getChildren("searchHit");
        assertFalse(hits.isEmpty(), "No <searchHit> elements found");
        assertEquals(100, hits.size(), "Expected 100 <searchHit> elements, but found " + hits.size());
        System.out.println("searchTest - done");
    }

    // --- Utilities ---------------------------------------------------------------------------------------------------

    public static String downloadBytes(final URL url, Duration timeout) {
        try {
            boolean bIgnoreHostMismatch = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreHostMismatch, false);
            boolean bIgnoreCertProblems = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems, false);
            boolean ignoreCertProblems = bIgnoreCertProblems || bIgnoreHostMismatch;

            HttpClient client;
            if (ignoreCertProblems) {
                // create a trust manager that does not validate certificate chains
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                        }
                };

                // Install the all-trusting trust manager
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // Create an HttpClient that uses the custom SSL context
                client = HttpClient.newBuilder()
                        .sslContext(sslContext)
                        .build();
            } else {
                // Use default SSL verification
                client = HttpClient.newHttpClient();
            }

            // Send a GET request
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder().uri(url.toURI()).timeout(timeout).GET().build();
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (NoSuchAlgorithmException | KeyManagementException | URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // wrapper to get a jdom2 Document
    public static org.jdom2.Document downloadAndParseXml(URL url, Duration timeout) {
        String xmlContent = downloadBytes(url, timeout);
        return XmlUtil.stringToXML(xmlContent, null);
    }


}
