package cbit.vcell.pathway;

import cbit.vcell.resource.PropertyLoader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.*;
import cbit.util.xml.XmlUtil;
import org.vcell.util.bioregistry.ncbitaxon.OrganismLookup;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.vcell.util.network.ClientDownloader.downloadBytes;


@Tag("Fast")
public class PathwaySearchTest {

    private static final Logger lg = LogManager.getLogger(PathwaySearchTest.class);

    private static final Namespace RDF_NS = Namespace.getNamespace("rdf",
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    private static final Namespace BP_NS = Namespace.getNamespace("bp",
            "http://www.biopax.org/release/biopax-level2.owl#");


    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 30_000;


    @BeforeAll
    public static void setUp() throws IOException {

    }

    @AfterAll
    public static void tearDown() {

    }

    /*
    Given a reactome pathway id, it downloads a pathway as xml document

    pathway = {Pathway@14156} "[Pathway: primaryId="http://bioregistry.io/reactome:R-HSA-5683177"; name="Defective ABCC8 can cause hypo- and hyper-glycemias";\ndataSource=[DataSource: primaryId="pc14:reactome"; name="pc14:reactome"]]"
     primaryId = "http://bioregistry.io/reactome:R-HSA-5683177"
     name = "Defective ABCC8 can cause hypo- and hyper-glycemias"
     organism = {Organism@14223} "[Organism: ncbiOrganismId="http://bioregistry.io/ncbitaxon:9606"; commonName="Human"; speciesName="Homo sapiens"]"
     dataSource = {DataSource@14224} "[DataSource: primaryId="pc14:reactome"; name="pc14:reactome"]"

    corresponds to insulinPathway-5683177.xml
    */
    @Test
    public void pathwayDownloadTest() throws IOException {
        // Reactome being down is handled inside pathwayDownload, by inspecting the status of
        // the request the test actually makes. The pre-flight isDatabaseAvailable(reactomeSite)
        // probe that used to guard this checked the site root, not the API path below.
        String pathwayId = "5683177";  // Reactome pathway ID
        pathwayDownload(pathwayId);
        lg.debug("pathwayDownloadTest - done");
    }


    // This test will query for insulin and receives a list of insulin pathways
    // returns the searchResponse.xml resource
    @Test
    public void searchTest() throws IOException {
        String searchText = "Insulin";
        String encodedQ = URLEncoder.encode('"' + searchText + '"', StandardCharsets.UTF_8.name());
        String uri = "https://www.pathwaycommons.org/pc2/search?"
                + "q=" + encodedQ
                + "&type=pathway";
        lg.debug("Query URI: " + uri);

        HttpURLConnection conn = (HttpURLConnection) new URL(uri).openConnection();
        conn.setRequestProperty("Accept", "application/xml");
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);

        // Skips if PathwayCommons itself is failing. This deliberately replaces a
        // pre-flight isDatabaseAvailable(pathwaysSite) check: that probed the site
        // root, which stays HTTP 200 while /pc2/search returns 502 — so the guard
        // passed and the request below then failed the build during a real outage.
        int status = statusOrSkip(conn, "PathwayCommons");
        assertEquals(200, status, "Unexpected HTTP status from " + uri);

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
        lg.debug("searchTest - done");
    }

    @Test
    public void fetchTaxonomyNameFromIdTest() {
        // As above, no pre-flight probe: the status of the real request decides. A timeout
        // or unreachable host means NCBI is down, which used to fail() this test and, since
        // it carries the Fast tag, block every merge in the repo.
        String taxonId = "9940"; // Ovis aries (sheep)
        HttpResponse<String> response;
        try {
            response = OrganismLookup.fetchTaxonomyResponse(taxonId);
        } catch (HttpTimeoutException e) {
            Assumptions.abort("NCBI Taxonomy timed out — skipping test");
            return;
        } catch (UnknownHostException e) {
            Assumptions.abort("NCBI Taxonomy is unreachable (check network or endpoint) — skipping test");
            return;
        } catch (IOException e) {
            Assumptions.abort("I/O error contacting NCBI Taxonomy (" + e.getMessage() + ") — skipping test");
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assumptions.abort("Request interrupted — skipping test");
            return;
        }

        // their outage skips; a 4xx or the wrong name is still a real failure
        assertEquals(200, statusOrSkip(response, "NCBI Taxonomy"), "Unexpected HTTP status");
        try {
            assertEquals("Ovis aries (sheep)", OrganismLookup.parseTaxonomyName(taxonId, response.body()),
                    "Incorrect taxonomy name");
        } catch (IOException e) {
            fail("Malformed taxonomy response: " + e.getMessage());
        }
    }


    // --- Utilities ---------------------------------------------------------------------------------------------------

    // wrapper to get a jdom2 Document
    private static org.jdom2.Document downloadAndParseXml(URL url, Duration timeout) {
        String xmlContent = downloadBytes(url, timeout);
        return XmlUtil.stringToXML(xmlContent, null);
    }

    private static String pathwayDownload(String pathwayId) throws IOException {
        final URL url = new URL("https://reactome.org/ReactomeRESTfulAPI/RESTfulWS/biopaxExporter/Level2/" + pathwayId);

        String ERROR_CODE_TAG = "error_code";
        String contentString = downloadOrSkip(url, "Reactome");      // download, or skip if Reactome is down

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

        return contentString;
    }

    /**
     * Produces a filtered BioPAX sub-document rooted at the element whose rdf:ID is rootId.
     *
     * @param inputDoc  the full BioPAX RDF document
     * @param rootId    the ID of the element to start from (without “#”)
     * @param maxDepth  maximum graph-distance to traverse
     * @return a new Document containing only the cloned, pruned elements in encounter order
     */
    private static Document filter(Document inputDoc, String rootId, int maxDepth) {

        // build lookup: rdf:ID → Element
        Map<String, Element> idToElement = new HashMap<>();
        for (Element e : collectElementsWithId(inputDoc)) {
            String id = e.getAttributeValue("ID", RDF_NS);
            idToElement.put(id, e);
        }

        // BFS (Breadth-First Search)
        // with depth tracking, visited set, and a LinkedHashMap for cloned results
        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> depth   = new HashMap<>();
        Set<String> visited          = new HashSet<>();
        Map<String, Element> clones  = new LinkedHashMap<>();

        queue.add(rootId);
        depth.put(rootId, 0);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            int d = depth.getOrDefault(currentId, Integer.MAX_VALUE);
            if (d > maxDepth || visited.contains(currentId)) {
                continue;
            }
            visited.add(currentId);

            Element original = idToElement.get(currentId);
            if (original == null) {
                continue; // missing reference, skip
            }

            // clone and prune unwanted bits
            Element cloned = original.clone();
            pruneInlineChildren(cloned);
            clones.put(currentId, cloned);

            // enqueue any referenced IDs
            for (Element child : original.getDescendants(new ElementFilter())) {
                String res = child.getAttributeValue("resource", RDF_NS);
                if (res != null && res.contains("#")) {
                    String refId = res.substring(res.lastIndexOf('#') + 1);
                    if (!visited.contains(refId)) {
                        queue.add(refId);
                        depth.put(refId, d + 1);
                    }
                }
            }
        }

        // assemble new RDF root and attach each clone in insertion order
        Element newRoot = new Element("RDF", RDF_NS);
        newRoot.addNamespaceDeclaration(RDF_NS);
        newRoot.addNamespaceDeclaration(BP_NS);

        for (Element clone : clones.values()) {
            newRoot.addContent(clone);
        }

        return new Document(newRoot);
    }

    // finds every Element in the document that carries an rdf:ID attribute.
    private static List<Element> collectElementsWithId(Document doc) {
        List<Element> list = new ArrayList<>();
        for (Element e : doc.getRootElement().getDescendants(new ElementFilter())) {
            if (e.getAttribute("ID", RDF_NS) != null) {
                list.add(e);
            }
        }
        return list;
    }

    private static void pruneInlineChildren(Element element) {
        List<Element> toRemove = new ArrayList<>();
        for (Element e : element.getDescendants(new ElementFilter("COMMENT", BP_NS))) {
            toRemove.add(e);
        }
        for (Element e : element.getDescendants(new ElementFilter("SYNONYMS", BP_NS))) {
            toRemove.add(e);
        }
        for (Element e : element.getDescendants(new ElementFilter("AUTHORS", BP_NS))) {
            toRemove.add(e);
        }
        for (Element e : toRemove) {
            e.detach();
        }
    }

    // given a pathwayId, extracts it and stores it as xml file in the specified directory
    // the goal is to facilitate debugging by not repeatedly download it every time we
    // need to parse it
    private static void pathwayDownloadToFile(String pathwayId, String destinationDir) {
        destinationDir = destinationDir != null
                ? destinationDir
                : "C:"
                + File.separator + "temp"
                + File.separator + "pathway"
                + File.separator + "downloads";

        try {
            // download the RDF/XML as a String
            String contentString = pathwayDownload(pathwayId);

            // create directory (no-op if it already exists)
            Path dirPath = Paths.get(destinationDir);
            java.nio.file.Files.createDirectories(dirPath);

            // resolve output filename and write UTF-8 bytes
            Path outputFile = dirPath.resolve(pathwayId + ".xml");
            java.nio.file.Files.write(outputFile, contentString.getBytes(StandardCharsets.UTF_8));

            lg.debug("Saved BioPAX to: " + outputFile.toAbsolutePath());
        }
        catch (Exception e) {
            lg.warn("Failed to download or save pathway " + pathwayId);
            e.printStackTrace();
        }
    }

    /*
       Given a large pathway xml file, we extract an entity we want to analize (for example a biochemical reaction)
       We also recursively extract its referenced subgraph
       Input: directory containing downloaded files
            File format: pathwayId + ".xml" - use pathwayDownloadToFile() to extract the pathway
       pathwayId (just the numeric part, like 9615017)
       targetId - the entity we want to extract, ex biochemicalReaction27
     */
    private static Document filterPathwayByTargetId(String pathwayId, String targetId, String sourceDir) {

        sourceDir = sourceDir != null
                ? sourceDir
                : "C:"
                + File.separator + "temp"
                + File.separator + "pathway"
                + File.separator + "downloads";

        int maxDepth   = 10;                           // how far down the reference graph to follow
        String contentString = null;
        boolean readSuccess = true;
        try {
            Path dirPath = Paths.get(sourceDir);
            java.nio.file.Files.createDirectories(dirPath);                 // create directory (no-op if it already exists)
            Path sourceFile = dirPath.resolve(pathwayId + ".xml");    // resolve source filename
            contentString = java.nio.file.Files.readString(sourceFile);     // attempt to read the RDF/XML file from disk

        } catch (IOException e) {
            // fallback: download from Pathway Commons if local file is missing or unreadable
            lg.warn("Failed to read local file — falling back to download");
            readSuccess = false;
        }
        try {
            if(readSuccess == false) {
                contentString = pathwayDownload(pathwayId);     // as a last resort, download the full BioPAX RDF/XML as a String
            }
            SAXBuilder sax = new SAXBuilder();
            Document fullDoc = sax.build(new StringReader(contentString));      // parse it into a JDOM Document

            // filter down to just that element + its referenced subgraph
            Document filteredDoc = filter(fullDoc, targetId, maxDepth);
            return filteredDoc;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeFilteredPathway(Document doc, String pathwayId, String targetId, String sourceDir) {
        // 1. Resolve output directory
        sourceDir = sourceDir != null
                ? sourceDir
                : "C:"
                + File.separator + "temp"
                + File.separator + "pathway"
                + File.separator + "downloads";
        try {
            Path dirPath = Paths.get(sourceDir);    // ensure directory exists
            Files.createDirectories(dirPath);
            String fileName = pathwayId + "_" + targetId + ".xml";  // construct output file path
            Path outputPath = dirPath.resolve(fileName);

            // write the JDOM Document to file
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            try (FileWriter writer = new FileWriter(outputPath.toFile())) {
                outputter.output(doc, writer);
            }
            lg.debug("Filtered pathway written to: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            lg.warn("Failed to write filtered pathway to file", e);
        }
    }

    /**
     * Response status of a live request, skipping the test when the fault is the remote
     * service's rather than ours. A third party being unreachable or returning 5xx is an
     * outage, not a VCell regression, and must not fail CI — these tests sit in the
     * required "Fast" check, so a sustained outage would otherwise block every merge.
     * A 4xx still fails: that means <em>our</em> request is wrong.
     *
     * <p>Prefer this over a pre-flight {@link #isDatabaseAvailable} probe. That probe
     * asks a different URL than the test goes on to use — a service can serve its home
     * page while the API path is down — and even a probe of the exact URL races with the
     * request that follows it.
     */
    private static int statusOrSkip(HttpURLConnection conn, String serviceName) {
        final int status;
        try {
            status = conn.getResponseCode();
        } catch (IOException e) {
            return Assumptions.abort(serviceName + " is unreachable (" + e + ") — skipping test");
        }
        return checkStatus(status, serviceName);
    }

    /** {@link #statusOrSkip(HttpURLConnection, String)} for the java.net.http client. */
    private static int statusOrSkip(HttpResponse<?> response, String serviceName) {
        return checkStatus(response.statusCode(), serviceName);
    }

    private static int checkStatus(int status, String serviceName) {
        Assumptions.assumeTrue(status < 500,
                serviceName + " returned HTTP " + status + " — remote service outage, skipping test");
        return status;
    }

    /**
     * GET the URL, skipping the test if the service is unreachable or failing on its
     * side, and returning the body otherwise.
     *
     * <p>Deliberately does not use {@code ClientDownloader.downloadBytes}: that returns
     * {@code response.body()} whatever the status, so during an outage the test received
     * an HTML error page and failed on a content assertion — reporting a VCell
     * regression when the truth was that the remote service was down.
     */
    private static String downloadOrSkip(URL url, String serviceName) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        int status = statusOrSkip(conn, serviceName);
        assertEquals(200, status, "Unexpected HTTP status from " + url);
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            return r.lines().collect(Collectors.joining("\n"));
        }
    }

    // isDatabaseAvailable(String) was removed deliberately. It probed a URL the test did
    // not go on to use — a service can serve its home page (HTTP 200) while the API path
    // is returning 502 — which is how a real PathwayCommons outage broke CI despite the
    // guard. Every network test here now decides from the status of its own request.


// ------------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {

        String pathwayId = (args.length > 0) ? args[0] : "9615017";                 // reactome pathway ID (ex: 9615017
        String targetId = (args.length > 1) ? args[1] : "biochemicalReaction29";    // the rdf:ID we want to extract (ex: biochemicalReaction1)

//        pathwayDownloadToFile(pathwayId, null);
        Document filteredDoc = filterPathwayByTargetId(pathwayId, targetId, null);
        if(filteredDoc == null) {
            lg.debug("Filtered document is null");
            System.exit(1);
        }
        // output the result as a pretty‐printed XML String
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        String filteredString = out.outputString(filteredDoc);
        lg.debug(filteredString);

        // optional, save it
        writeFilteredPathway( filteredDoc, pathwayId, targetId, null);
        lg.debug("Done work on pathway id " + pathwayId);
   }


}
