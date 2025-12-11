package org.vcell.util.bioregistry.ncbitaxon;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class OrganismLookup {

    private static final Logger lg = LogManager.getLogger(OrganismLookup.class);

    public enum NameType {
        COMMON,
        SCIENTIFIC
    }

    private static final String PREFIX = "http://bioregistry.io/ncbitaxon:";

    private static final Map<String, String> COMMON_NAMES = new LinkedHashMap<>(Map.ofEntries(
            Map.entry("9606", "human"),
            Map.entry("10090", "house mouse"),
            Map.entry("10116", "Norway rat"),
            Map.entry("7955",  "zebrafish"),
            Map.entry("7227",  "fruit fly"),
            Map.entry("6239",  "Nematode worm"),
            Map.entry("10141", "domestic guinea pig"),
            Map.entry("9986",  "rabbit"),
            Map.entry("9615",  "dog"),
            Map.entry("9544",  "Rhesus monkey"),
            Map.entry("9685",  "domestic cat"),
            Map.entry("9913",  "domestic cattle"),
            Map.entry("9031",  "chicken"),
            Map.entry("8364",  "tropical clawed frog"),
            Map.entry("8296", "axolotl"),
            Map.entry("9825",  "domestic pig"),
            Map.entry("9796",  "horse"),
            Map.entry("9940",  "sheep"),
            Map.entry("9925",  "goat")
    ));

    private static final Map<String, String> SCIENTIFIC_NAMES = new LinkedHashMap<>(Map.ofEntries(
            Map.entry("9606", "Homo sapiens"),
            Map.entry("10090", "Mus musculus"),
            Map.entry("10116", "Rattus norvegicus"),
            Map.entry("7955",  "Danio rerio"),
            Map.entry("7227",  "Drosophila melanogaster"),
            Map.entry("6239",  "Caenorhabditis elegans"),
            Map.entry("10141", "Cavia porcellus"),
            Map.entry("9986",  "Oryctolagus cuniculus"),
            Map.entry("9615",  "Canis lupus familiaris"),
            Map.entry("9544",  "Macaca mulatta"),
            Map.entry("9685",  "Felis catus"),
            Map.entry("9913",  "Bos taurus"),
            Map.entry("9031",  "Gallus gallus"),
            Map.entry("8364",  "Xenopus tropicalis "),
            Map.entry("8296", "Ambystoma mexicanum"),
            Map.entry("9825",  "Sus scrofa domesticus"),
            Map.entry("9796",  "Equus caballus"),
            Map.entry("9940",  "Ovis aries"),
            Map.entry("9925",  "Capra hircus")
    ));

    // get name from full URI
    public static String getName(String fullUri, NameType type) {
        if (!fullUri.startsWith(PREFIX)) return "Unknown";
        String taxonId = fullUri.substring(PREFIX.length());

        return switch (type) {
            case COMMON     -> COMMON_NAMES.getOrDefault(taxonId, "Unknown");
            case SCIENTIFIC -> SCIENTIFIC_NAMES.getOrDefault(taxonId, "Unknown");
        };
    }

    // reverse lookup: get full URI from name
    public static String getUriFromName(String name, NameType type) {
        Map<String, String> sourceMap = switch (type) {
            case COMMON     -> COMMON_NAMES;
            case SCIENTIFIC -> SCIENTIFIC_NAMES;
        };

        return sourceMap.entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(name))
                .map(entry -> PREFIX + entry.getKey())
                .findFirst()
                .orElse("Unknown");
    }

    public static HttpResponse<String> fetchTaxonomyResponse(String taxonId) throws IOException, InterruptedException {
        String url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi"
                + "?db=taxonomy&id=" + taxonId + "&retmode=json";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public static String parseTaxonomyName(String taxonId, String jsonBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonBody);
        JsonNode resultNode = root.path("result").path(taxonId);

        if (resultNode.isMissingNode()) {
            return "Error: Taxon ID not found";
        }

        String scientificName = resultNode.path("scientificname").asText("Unknown");
        String commonName = resultNode.path("commonname").asText(null);

        String name = (commonName != null && !commonName.isEmpty())
                ? scientificName + " (" + commonName + ")"
                : scientificName;
        return name;
    }

    public static void verifyAllTaxonMappings() {
        Random random = new Random();

        for (Map.Entry<String, String> entry : SCIENTIFIC_NAMES.entrySet()) {
            String taxonId = entry.getKey();
            String expectedScientific = SCIENTIFIC_NAMES.getOrDefault(taxonId, "(none)");
            String expectedCommon = COMMON_NAMES.getOrDefault(taxonId, "(none)");

            try {
                HttpResponse<String> response = fetchTaxonomyResponse(taxonId);
                String result = parseTaxonomyName(taxonId, response.body());

                lg.debug("Taxon ID: {}", taxonId);
                lg.debug("  Returned: {}", result);
                lg.debug("  Expected: {} ({})", expectedScientific, expectedCommon);
                lg.debug(""); // optional, consider removing if it adds no value

            } catch (java.net.http.HttpTimeoutException e) {
                lg.warn("Timeout for Taxon ID: {}", taxonId, e);
            } catch (java.net.UnknownHostException e) {
                lg.warn("Host unreachable for Taxon ID: {}", taxonId, e);
            } catch (Exception e) {
                lg.warn("Error for Taxon ID: {}", taxonId, e);
            }

            // wait 5 to 10 random seconds before calls so that we won't look like a denial of service attack
            int delaySeconds = 5 + random.nextInt(6); // 5–10 seconds
            try {
                Thread.sleep(delaySeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                lg.warn("Interrupted during sleep");
            }
        }
    }

}
