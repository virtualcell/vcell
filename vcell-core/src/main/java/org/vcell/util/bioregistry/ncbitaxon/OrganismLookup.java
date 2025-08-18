package org.vcell.util.bioregistry.ncbitaxon;

import java.util.Map;

public class OrganismLookup {

    public enum NameType {
        COMMON,
        SCIENTIFIC
    }

    private static final String PREFIX = "http://bioregistry.io/ncbitaxon:";

    private static final Map<String, String> COMMON_NAMES = Map.ofEntries(
            Map.entry("9606", "Human"),
            Map.entry("10090", "Mouse"),
            Map.entry("10116", "Rat"),
            Map.entry("7955",  "Zebrafish"),
            Map.entry("7227",  "Fruit fly"),
            Map.entry("6239",  "Nematode worm"),
            Map.entry("10141", "Guinea pig"),
            Map.entry("9986",  "Rabbit"),
            Map.entry("9615",  "Dog"),
            Map.entry("9823",  "Pig"),
            Map.entry("9544",  "Rhesus monkey"),
            Map.entry("9685",  "Cat"),
            Map.entry("9913",  "Cow"),
            Map.entry("9031",  "Chicken"),
            Map.entry("8364",  "Xenopus frog"),
            Map.entry("28377", "Axolotl"),
            Map.entry("9825",  "Mini pig"),
            Map.entry("9796",  "Horse"),
            Map.entry("9915",  "Sheep"),
            Map.entry("9940",  "Goat")
    );

    private static final Map<String, String> SCIENTIFIC_NAMES = Map.ofEntries(
            Map.entry("9606", "Homo sapiens"),
            Map.entry("10090", "Mus musculus"),
            Map.entry("10116", "Rattus norvegicus"),
            Map.entry("7955",  "Danio rerio"),
            Map.entry("7227",  "Drosophila melanogaster"),
            Map.entry("6239",  "Caenorhabditis elegans"),
            Map.entry("10141", "Cavia porcellus"),
            Map.entry("9986",  "Oryctolagus cuniculus"),
            Map.entry("9615",  "Canis lupus familiaris"),
            Map.entry("9823",  "Sus scrofa"),
            Map.entry("9544",  "Macaca mulatta"),
            Map.entry("9685",  "Felis catus"),
            Map.entry("9913",  "Bos taurus"),
            Map.entry("9031",  "Gallus gallus"),
            Map.entry("8364",  "Xenopus laevis"),
            Map.entry("28377", "Ambystoma mexicanum"),
            Map.entry("9825",  "Sus scrofa domesticus"),
            Map.entry("9796",  "Equus caballus"),
            Map.entry("9915",  "Ovis aries"),
            Map.entry("9940",  "Capra hircus")
    );

    // 🧪 Get name from full URI
    public static String getName(String fullUri, NameType type) {
        if (!fullUri.startsWith(PREFIX)) return "Unknown";
        String taxonId = fullUri.substring(PREFIX.length());

        return switch (type) {
            case COMMON     -> COMMON_NAMES.getOrDefault(taxonId, "Unknown");
            case SCIENTIFIC -> SCIENTIFIC_NAMES.getOrDefault(taxonId, "Unknown");
        };
    }

    // 🔁 Reverse lookup: get full URI from name
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
}
