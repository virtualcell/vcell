package cbit.vcell.client.desktop.biomodel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Keeps {@code bioModelsNetInfo.xml} -- the list behind the desktop's BMDB tab -- honest about
 * which BioModels Database models VCell can actually open.
 *
 * <p>The tab marks each model supported or not; unsupported ones get a warning icon reading "model
 * not compatible with vCell", so that a user is not surprised by an import that fails. That list was
 * hand-maintained and had drifted: 66 models were being flagged as incompatible that VCell imports
 * perfectly well, and one was flagged compatible that does not import.
 *
 * <p>The authority for "can VCell open this" is {@code test_cases.ndjson}, which the BMDB nightly
 * executes against the real collection. This test derives the {@code Supported} attribute from it
 * and fails if the checked-in XML disagrees, so the list cannot silently go stale again as the
 * importer improves.
 *
 * <p>To accept a legitimate change, regenerate and commit:
 *
 * <pre>
 *   mvn test -pl vcell-client -Dtest=BioModelsNetInfoTest -Dvcell.updateBioModelsNetInfo=true
 * </pre>
 *
 * <p>The nightly records execution -- import <em>and</em> simulation -- while this list is only
 * about whether the model opens. So the mapping keys on the failure type: a model that imports and
 * then fails in the solver is still perfectly loadable and stays supported. See
 * {@link #IMPORT_BLOCKING_FAILURES}.
 */
@Tag("Fast")
public class BioModelsNetInfoTest {

    private static final Path XML = Paths.get("src/main/resources/bioModelsNetInfo.xml");
    private static final Path NDJSON = Paths.get("../vcell-cli/src/main/resources/test_cases.ndjson");
    private static final String UPDATE_PROPERTY = "vcell.updateBioModelsNetInfo";

    /**
     * Failure types that mean the model never became a BioModel. Everything else -- solver failures,
     * divide-by-zero, the SEDML-level outcomes -- happens after a successful import, so the model
     * still opens in the desktop and should not be flagged incompatible.
     */
    private static final Set<String> IMPORT_BLOCKING_FAILURES = new HashSet<>(List.of(
            "SBML_IMPORT_FAILURE",
            "SBML_XML_NODE_FAILURE",
            "UNSUPPORTED_NON_INT_STOCH",
            "UNSUPPORTED_NON_NUMERIC_STOCH",
            "UNSUPPORTED_NON_CONSTANT_COMPARTMENTS",
            "UNSUPPORTED_DELAY_SBML",
            "MATH_GENERATION_FAILURE"));

    private static final Pattern ID_ATTR = Pattern.compile("ID=\"([^\"]+)\"");
    private static final Pattern SUPPORTED_ATTR = Pattern.compile("Supported=\"(true|false)\"");

    @Test
    public void supportedFlagsMatchTheNightlyResults() throws IOException {
        assertTrue(Files.exists(XML), XML + " not found (run from the vcell-client module directory)");
        if(!Files.exists(NDJSON)){
            // vcell-cli is a sibling module, not a dependency; if the checkout is partial there is
            // nothing to compare against and this check simply does not apply.
            System.out.println("skipping: " + NDJSON + " not present");
            return;
        }

        Map<String, Boolean> importsOk = readNightlyImportResults();
        // Split keeping the terminators: this file is CRLF, and rewriting it with the platform
        // separator would turn a handful of attribute edits into a whole-file diff.
        String original = new String(Files.readAllBytes(XML), StandardCharsets.UTF_8);
        List<String> lines = splitKeepingLineEndings(original);
        List<String> updated = new ArrayList<>(lines.size());
        List<String> changes = new ArrayList<>();

        for(String line : lines){
            Matcher id = ID_ATTR.matcher(line);
            Matcher supported = SUPPORTED_ATTR.matcher(line);
            if(!id.find() || !supported.find()){
                updated.add(line);
                continue;
            }
            Boolean expected = importsOk.get(id.group(1));
            if(expected == null){
                updated.add(line);      // no nightly evidence: leave the curated value alone
                continue;
            }
            boolean current = Boolean.parseBoolean(supported.group(1));
            if(current == expected){
                updated.add(line);
                continue;
            }
            changes.add(id.group(1) + ": Supported " + current + " -> " + expected);
            updated.add(supported.replaceFirst("Supported=\"" + expected + "\""));
        }

        if(changes.isEmpty()){
            return;
        }
        if(Boolean.getBoolean(UPDATE_PROPERTY)){
            Files.write(XML, String.join("", updated).getBytes(StandardCharsets.UTF_8));
            System.out.println("updated " + XML + " (" + changes.size() + " models):");
            changes.forEach(c -> System.out.println("   " + c));
            return;
        }
        fail(changes.size() + " model(s) in " + XML.getFileName() + " disagree with the BMDB nightly"
                + " results in test_cases.ndjson:\n   " + String.join("\n   ", changes)
                + "\n\nRegenerate with: mvn test -pl vcell-client -Dtest=" + getClass().getSimpleName()
                + " -D" + UPDATE_PROPERTY + "=true");
    }

    /** Splits on line boundaries but keeps the terminators, so the file can be rewritten byte-for-byte. */
    private static List<String> splitKeepingLineEndings(String text){
        List<String> lines = new ArrayList<>();
        Matcher m = Pattern.compile("[^\\r\\n]*(\\r\\n|\\r|\\n|$)").matcher(text);
        int end = 0;
        while(m.find() && m.start() < text.length()){
            lines.add(m.group());
            end = m.end();
        }
        if(end < text.length()){
            lines.add(text.substring(end));
        }
        return lines;
    }

    /** BioModels id -> whether the nightly shows VCell importing it. */
    private static Map<String, Boolean> readNightlyImportResults() throws IOException {
        Map<String, Boolean> result = new HashMap<>();
        for(String line : Files.readAllLines(NDJSON, StandardCharsets.UTF_8)){
            String trimmed = line.trim();
            if(trimmed.isEmpty() || !trimmed.contains("\"SYSBIO_BIOMD\"")){
                continue;
            }
            String id = jsonString(trimmed, "file_path");
            String status = jsonString(trimmed, "known_status");
            if(id == null || status == null || "SKIP".equals(status)){
                continue;   // SKIP carries no evidence either way
            }
            id = id.replace(".omex", "");
            String failureType = jsonString(trimmed, "known_failure_type");
            result.put(id, "PASS".equals(status) || !IMPORT_BLOCKING_FAILURES.contains(failureType));
        }
        return result;
    }

    /**
     * Reads one string field. Deliberately not a JSON parser: vcell-client has no JSON dependency,
     * and these fields are flat strings written by the same tool every night.
     */
    private static String jsonString(String json, String field){
        Matcher m = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : null;
    }
}
