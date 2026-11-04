package org.vcell.cli.run;

import org.vcell.cli.run.hdf5.Hdf5Comparison;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Compares the reports a CLI run wrote through the legacy code with those it wrote through the new data system (with
 * {@link ExecutionJob#UNIFIED_REPORTS_PROPERTY} set): reports.h5 against {@link ExecutionJob#UNIFIED_REPORTS_FILE} by
 * content, and every report CSV and plot PNG and PDF against its copy under {@link ExecutionJob#UNIFIED_REPORTS_DIRECTORY}
 * byte for byte (PDFs apart from when they were made; CSVs by report id, as the file names differ, see
 * {@link #withoutSIdWrapper}).
 */
public final class UnifiedReportsComparison {
    private UnifiedReportsComparison() { }

    /** @return a description of every difference, empty when both systems wrote the same reports */
    public static List<String> differences(Path outputDirectory) throws IOException {
        List<String> differences = new ArrayList<>();
        Path legacyHdf5 = outputDirectory.resolve("reports.h5"), unifiedHdf5 = outputDirectory.resolve(ExecutionJob.UNIFIED_REPORTS_FILE);
        if (Files.exists(legacyHdf5) != Files.exists(unifiedHdf5))
            differences.add((Files.exists(legacyHdf5) ? "missing " : "unexpected ") + unifiedHdf5.getFileName());
        else if (Files.exists(legacyHdf5))
            differences.addAll(Hdf5Comparison.differences(legacyHdf5, unifiedHdf5, UnifiedReportsComparison::withoutSIdWrappers));

        Path unifiedDirectory = outputDirectory.resolve(ExecutionJob.UNIFIED_REPORTS_DIRECTORY);
        for (String extension : List.of("csv", "png", "pdf"))
            UnifiedReportsComparison.compareFiles(outputDirectory, unifiedDirectory, extension, differences);
        return differences;
    }

    private static void compareFiles(Path outputDirectory, Path unifiedDirectory, String extension, List<String> differences) throws IOException {
        Map<Path, Path> legacyFiles = new TreeMap<>();
        for (Path file : UnifiedReportsComparison.files(outputDirectory, unifiedDirectory, extension))
            legacyFiles.put(UnifiedReportsComparison.withoutSIdWrapper(file), file);
        Set<Path> unifiedFiles = UnifiedReportsComparison.files(unifiedDirectory, null, extension);
        Set<Path> all = new TreeSet<>(legacyFiles.keySet());
        all.addAll(unifiedFiles);
        for (Path file : all) {
            if (!unifiedFiles.contains(file)) differences.add(file + ": missing from the new system's outputs");
            else if (!legacyFiles.containsKey(file)) differences.add(file + ": unexpected in the new system's outputs");
            else {
                byte[] legacy = Files.readAllBytes(outputDirectory.resolve(legacyFiles.get(file)));
                byte[] unified = Files.readAllBytes(unifiedDirectory.resolve(file));
                if (extension.equals("pdf")) {
                    legacy = UnifiedReportsComparison.withoutPdfTimestamps(legacy);
                    unified = UnifiedReportsComparison.withoutPdfTimestamps(unified);
                }
                if (Arrays.equals(legacy, unified)) continue;
                differences.add(file + ": differs" + (extension.equals("csv") ? "\n--- legacy\n" + new String(legacy, StandardCharsets.UTF_8)
                        + "--- new\n" + new String(unified, StandardCharsets.UTF_8) : ""));
            }
        }
    }

    /**
     * The legacy code labels a plot's data sets {@code SId [<plot>].SId [<curve>]::X} in the HDF5
     * (https://github.com/virtualcell/vcell/issues/2192); the new system labels them {@code <plot>.<curve>::X}.
     */
    private static Object withoutSIdWrappers(String attribute, Object value) {
        if (!attribute.equals("sedmlDataSetLabels") || !(value instanceof String[] labels)) return value;
        return Arrays.stream(labels).map(label -> label.replaceAll("SId \\[([^\\]]*)\\]", "$1")).toArray(String[]::new);
    }

    /** A PDF records when it was made, and an id derived from that; neither says anything about the content. */
    private static byte[] withoutPdfTimestamps(byte[] pdf) {
        String text = new String(pdf, StandardCharsets.ISO_8859_1)
                .replaceAll("/(CreationDate|ModDate)\\s*\\([^)]*\\)", "/$1()")
                .replaceAll("/ID\\s*\\[[^\\]]*\\]", "/ID[]");
        return text.getBytes(StandardCharsets.ISO_8859_1);
    }

    /**
     * The legacy code names a report's CSV {@code SId [<id>].csv} (https://github.com/virtualcell/vcell/issues/2195);
     * the new system names it {@code <id>.csv}.
     */
    private static Path withoutSIdWrapper(Path csv) {
        String name = csv.getFileName().toString();
        if (!name.startsWith("SId [") || !name.endsWith("].csv")) return csv;
        return csv.resolveSibling(name.substring("SId [".length(), name.length() - "].csv".length()) + ".csv");
    }

    /** @return the files with {@code extension} under {@code root}, relative to it, leaving out anything under {@code excluded} */
    private static Set<Path> files(Path root, Path excluded, String extension) throws IOException {
        Set<Path> found = new TreeSet<>();
        if (!Files.isDirectory(root)) return found;
        try (Stream<Path> files = Files.walk(root)) {
            files.filter(file -> file.toString().endsWith("." + extension))
                    .filter(file -> excluded == null || !file.startsWith(excluded))
                    .forEach(file -> found.add(root.relativize(file)));
        }
        return found;
    }
}
