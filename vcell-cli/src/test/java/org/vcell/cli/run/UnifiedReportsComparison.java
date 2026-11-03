package org.vcell.cli.run;

import org.vcell.cli.run.hdf5.Hdf5Comparison;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Compares the reports a CLI run wrote through the legacy code with those it wrote through the new data system (with
 * {@link ExecutionJob#UNIFIED_REPORTS_PROPERTY} set): reports.h5 against {@link ExecutionJob#UNIFIED_REPORTS_FILE} by
 * content, and every report CSV against its copy under {@link ExecutionJob#UNIFIED_REPORTS_DIRECTORY} byte for byte (by
 * report id: the file names differ, see {@link #withoutSIdWrapper}).
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
            differences.addAll(Hdf5Comparison.differences(legacyHdf5, unifiedHdf5));

        Path unifiedDirectory = outputDirectory.resolve(ExecutionJob.UNIFIED_REPORTS_DIRECTORY);
        Map<Path, Path> legacyCsvFiles = new TreeMap<>();
        for (Path csv : UnifiedReportsComparison.csvs(outputDirectory, unifiedDirectory))
            legacyCsvFiles.put(UnifiedReportsComparison.withoutSIdWrapper(csv), csv);
        Set<Path> legacyCsvs = legacyCsvFiles.keySet();
        Set<Path> unifiedCsvs = UnifiedReportsComparison.csvs(unifiedDirectory, null);
        Set<Path> all = new TreeSet<>(legacyCsvs);
        all.addAll(unifiedCsvs);
        for (Path csv : all) {
            if (!unifiedCsvs.contains(csv)) differences.add(csv + ": missing from the new system's CSVs");
            else if (!legacyCsvs.contains(csv)) differences.add(csv + ": unexpected in the new system's CSVs");
            else if (Files.mismatch(outputDirectory.resolve(legacyCsvFiles.get(csv)), unifiedDirectory.resolve(csv)) != -1)
                differences.add(csv + ": differs\n--- legacy\n" + Files.readString(outputDirectory.resolve(legacyCsvFiles.get(csv)))
                        + "--- new\n" + Files.readString(unifiedDirectory.resolve(csv)));
        }
        return differences;
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

    /** @return the CSV files under {@code root}, relative to it, leaving out anything under {@code excluded} */
    private static Set<Path> csvs(Path root, Path excluded) throws IOException {
        Set<Path> csvs = new TreeSet<>();
        if (!Files.isDirectory(root)) return csvs;
        try (Stream<Path> files = Files.walk(root)) {
            files.filter(file -> file.toString().endsWith(".csv"))
                    .filter(file -> excluded == null || !file.startsWith(excluded))
                    .forEach(file -> csvs.add(root.relativize(file)));
        }
        return csvs;
    }
}
