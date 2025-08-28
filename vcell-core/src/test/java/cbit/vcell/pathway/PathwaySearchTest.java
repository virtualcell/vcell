package cbit.vcell.pathway;

import cbit.vcell.resource.PropertyLoader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.*;
import cbit.util.xml.XmlUtil;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;



@Tag("Fast")
public class PathwaySearchTest {

    private static final Namespace RDF_NS = Namespace.getNamespace("rdf",
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    private static final Namespace BP_NS = Namespace.getNamespace("bp",
            "http://www.biopax.org/release/biopax-level3.owl#");


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
        pathwayDownload(pathwayId);
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

    public static String pathwayDownload(String pathwayId) throws MalformedURLException {
        final URL url = new URL("https://reactome.org/ReactomeRESTfulAPI/RESTfulWS/biopaxExporter/Level2/" + pathwayId);

        String ERROR_CODE_TAG = "error_code";
        String contentString = downloadBytes(url, Duration.ofSeconds(20));      // download

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
        // 1. build lookup: rdf:ID → Element
        Map<String, Element> idToElement = new HashMap<>();
        for (Element e : collectElementsWithId(inputDoc)) {
            String id = e.getAttributeValue("ID", RDF_NS);
            idToElement.put(id, e);
        }

        // 2. BFS with depth tracking, visited set, and a LinkedHashMap for cloned results
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

            // 3. clone and prune unwanted bits
            Element cloned = original.clone();
            pruneUnwanted(cloned);
            clones.put(currentId, cloned);

            // 4. enqueue any referenced IDs
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

        // 5. assemble new RDF root and attach each clone in insertion order
        Element newRoot = new Element("RDF", RDF_NS);
        newRoot.addNamespaceDeclaration(RDF_NS);
        newRoot.addNamespaceDeclaration(BP_NS);

        for (Element clone : clones.values()) {
            newRoot.addContent(clone);
        }

        return new Document(newRoot);
    }

    /**
     * Finds every Element in the document that carries an rdf:ID attribute.
     */
    private static List<Element> collectElementsWithId(Document doc) {
        List<Element> list = new ArrayList<>();
        for (Element e : doc.getRootElement().getDescendants(new ElementFilter())) {
            if (e.getAttribute("ID", RDF_NS) != null) {
                list.add(e);
            }
        }
        return list;
    }

    /**
     * Detaches any children matching the unwanted patterns:
     *   • <bp:COMMENT rdf:datatype="…">
     *   • <bp:publicationXref>
     *   • <bp:AUTHORS>
     *   • <bp:XREF rdf:resource="#unificationXref">
     *   • <bp:DATA-SOURCE rdf:resource="#dataSource">
     *   • <bp:SYNONYMS>
     */
    private static void pruneUnwanted(Element root) {
        // COMMENT with rdf:datatype
        Iterator<Element> comments = root.getDescendants(
                new ElementFilter("COMMENT", BP_NS));
        while (comments.hasNext()) {
            comments.next().detach();
        }

        // publicationXref
        Iterator<Element> pubs = root.getDescendants(
                new ElementFilter("publicationXref", BP_NS));
        while (pubs.hasNext()) {
            pubs.next().detach();
        }

        // AUTHORS
        Iterator<Element> authors = root.getDescendants(
                new ElementFilter("AUTHORS", BP_NS));
        while (authors.hasNext()) {
            authors.next().detach();
        }

        // XREF pointing to unificationXref
        Iterator<Element> xrefs = root.getDescendants(
                new ElementFilter("XREF", BP_NS));
        while (xrefs.hasNext()) {
            Element x = xrefs.next();
            String res = x.getAttributeValue("resource", RDF_NS);
            if (res != null && res.endsWith("#unificationXref")) {
                x.detach();
            }
        }

        // DATA-SOURCE pointing to dataSource
        Iterator<Element> sources = root.getDescendants(
                new ElementFilter("DATA-SOURCE", BP_NS));
        while (sources.hasNext()) {
            Element s = sources.next();
            String res = s.getAttributeValue("resource", RDF_NS);
            if (res != null && res.endsWith("#dataSource")) {
                s.detach();
            }
        }

        // SYNONYMS
        Iterator<Element> syns = root.getDescendants(
                new ElementFilter("SYNONYMS", BP_NS));
        while (syns.hasNext()) {
            syns.next().detach();
        }
    }

    private static void downloadPathway(String pathwayId, String destinationDir) {
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

            System.out.println("Saved BioPAX to: " + outputFile.toAbsolutePath());
        }
        catch (Exception e) {
            System.err.println("Failed to download or save pathway " + pathwayId);
            e.printStackTrace();
        }
    }

    private static String filterPathwayByTargetId(String pathwayId, String targetId, String sourceDir) {

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
            // Fallback: download from Pathway Commons if local file is missing or unreadable
            System.err.println("Failed to read local file — falling back to download");
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

            // output the result as a pretty‐printed XML String
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            String filteredString = out.outputString(filteredDoc);
            System.out.println(filteredString);
            return filteredString;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

// ------------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {

        String pathwayId = (args.length > 0) ? args[0] : "9615017";                 // reactome pathway ID (ex: 9615017
        String targetId = (args.length > 1) ? args[1] : "biochemicalReaction29";    // the rdf:ID we want to extract (ex: biochemicalReaction1)

//        downloadPathway(pathwayId, null);
        filterPathwayByTargetId(pathwayId, targetId, null);
        System.out.println("Done work on pathway id " + pathwayId);
   }


}
/*
complex : "FOXO1,FOXO1:PPARGC1A,FOXO3,FOXO4,FOXO6:G6PC gene"  ID='http://www.reactome.org/biopax/9615017##complex10' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "SREBF1 gene expression is inhibited by FOXO1"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction29' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO1,FOXO3,FOXO4:IGFBP1 gene"  ID='http://www.reactome.org/biopax/9615017##complex13' : Unable to resolve proxy component to PhysicalEntity
complex : "FOXO1:RETN gene"  ID='http://www.reactome.org/biopax/9615017##complex23' : Unable to resolve proxy component to PhysicalEntity
complex : "FOXO4:ATXN3:SOD2 gene"  ID='http://www.reactome.org/biopax/9615017##complex3' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "RETN gene expression is stimulated by FOXO1"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction34' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO1:PPARGC1A,FOXO4 binds PCK1 gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction18' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "IGFBP1 gene expression is stimulated by FOXO1,FOXO3,FOXO4"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction21' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "GCK gene expression is inhibited by FOXO1, SIN3A and HDACs"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction27' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO3,FOXO6,(FOXO1) binds CAT gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction7' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FBXO32 gene expression is stimulated by FOXO1,FOXO3,(FOXO4)"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction23' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO1 and SIN3A:HDAC complex bind GCK gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction26' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "CAT gene expression is stimulated by FOXO3,FOXO6,(FOXO1)"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction8' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO1:SREBF1 gene"  ID='http://www.reactome.org/biopax/9615017##complex18' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "SOD2 gene expression is stimulated by FOXO4, FOXO3 and FOXO1"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction6' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO1,FOXO3,(FOXO4):FBXO32 gene"  ID='http://www.reactome.org/biopax/9615017##complex14' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "FOXO1,FOXO3,FOXO4 bind IGFBP1 gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction20' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO1 binds RETN gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction33' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "ABCA6 gene expression is stimulated by FOXO1,FOXO3"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction25' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "POMC gene expression is inhibited by FOXO1"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction14' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO1:AGRP gene"  ID='http://www.reactome.org/biopax/9615017##complex7' : Unable to resolve proxy component to PhysicalEntity
complex : "FOXO1:NR3C1:(ALDO,11DCORST,CORST,CORT):TRIM63 gene"  ID='http://www.reactome.org/biopax/9615017##complex21' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "G6PC gene expression is stimulated by FOXO1,FOXO3,FOXO4,FOXO6"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction17' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "NPY gene expression is stimulated by FOXO1"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction10' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO3:SOD2 gene"  ID='http://www.reactome.org/biopax/9615017##complex4' : Unable to resolve proxy component to PhysicalEntity
complex : "FOXO1:PPARGC1A,FOXO3,FOXO4:PCK1 gene"  ID='http://www.reactome.org/biopax/9615017##complex12' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "FOXO4:ATXN3 binds SOD2 gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction4' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "AGRP gene expression is stimulated by FOXO1"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction12' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO1 binds AGRP gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction11' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO1,FOXO3 and SMAD3 bind TRIM63 gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction31' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO1 and NR3C1 bind TRIM63 gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction30' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO6:PLXNA4 gene"  ID='http://www.reactome.org/biopax/9615017##complex1' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "FOXO1 binds NPY gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction9' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "TRIM63 gene expression is stimulated by FOXO1 and FOXO3"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction32' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO1,FOXO3:p-2S-SMAD2/3:SMAD4:TRIM63 gene"  ID='http://www.reactome.org/biopax/9615017##complex22' : Unable to resolve proxy component to PhysicalEntity
complex : "FOXO1:NPY gene"  ID='http://www.reactome.org/biopax/9615017##complex6' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "FOXO1 binds SREBF1 gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction28' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO3,FOXO6,(FOXO1):CAT gene"  ID='http://www.reactome.org/biopax/9615017##complex5' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "FOXO6 binds PLXNA4 gene"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction1' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO1,FOXO3,(FOXO4) bind FBXO32 gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction22' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO3 binds SOD2 gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction5' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "PCK1 gene expression is stimulated by FOXO1:PPARGC1A"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction19' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO1,FOXO3:ABCA6 gene"  ID='http://www.reactome.org/biopax/9615017##complex15' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "FOXO1,FOXO1:PPARGC1A,FOXO3,FOXO4,FOXO6 bind G6PC gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction16' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "FOXO1 binds POMC gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction13' : Unable to resolve proxy participant to PhysicalEntity
biochemical reaction : "PLXNA4 gene expression is stimulated by FOXO6"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction2' : Unable to resolve proxy participant to PhysicalEntity
complex : "FOXO1:POMC gene"  ID='http://www.reactome.org/biopax/9615017##complex8' : Unable to resolve proxy component to PhysicalEntity
complex : "FOXO1:SIN3A:HDAC1.HDAC2 dimers:GCK gene"  ID='http://www.reactome.org/biopax/9615017##complex16' : Unable to resolve proxy component to PhysicalEntity
biochemical reaction : "FOXO1,FOXO3 bind ABCA6 gene promoter"  ID='http://www.reactome.org/biopax/9615017##biochemicalReaction24' : Unable to resolve proxy participant to PhysicalEntity

 */