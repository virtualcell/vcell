package org.vcell.admin.cli.tools.report;

import cbit.vcell.modeldb.AdminDBTopLevel.AsOfMetric;
import org.vcell.admin.cli.CLIDatabaseService;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;

import java.time.LocalDate;
import java.util.concurrent.Callable;

/**
 * Cumulative count for the chosen as-of metric, evaluated at the given date (inclusive).
 *
 * <p>Metric names are case-insensitive and may use hyphens or underscores
 * (e.g. {@code --metric total-users} maps to {@link AsOfMetric#TOTAL_USERS}).
 * Available metrics: {@code total-users}, {@code users-with-sims},
 * {@code biomodels}, {@code mathmodels}, {@code total-models}, {@code simulations},
 * {@code public-biomodels}, {@code public-mathmodels}, {@code public-models},
 * {@code public-biomodel-sims}, {@code public-mathmodel-sims}, {@code public-sims}.
 *
 * <p>Output: integer on stdout, one human-readable line on stderr.
 *
 * <p>To add a new as-of metric next year: add an enum value to
 * {@link AsOfMetric} plus a SQL branch in {@code AdminDBTopLevel.countAsOfImpl}.
 * No new command class needed. See {@code .claude/commands/admin-report.md}.
 */
@Command(name = "count-asof",
        description = "Cumulative count for the chosen metric as of a given date.")
public class CountAsOfCommand implements Callable<Integer> {

    @Option(names = "--metric", required = true,
            converter = AsOfMetricConverter.class,
            description = "Which cumulative metric to compute. "
                    + "One of: total-users, users-with-sims, biomodels, mathmodels, "
                    + "total-models, simulations, public-biomodels, public-mathmodels, "
                    + "public-models, public-biomodel-sims, public-mathmodel-sims, public-sims.")
    private AsOfMetric metric;

    @Option(names = "--asof", required = true,
            description = "Date at which to evaluate the count (yyyy-MM-dd, inclusive).")
    private LocalDate asOf;

    @Override
    public Integer call() throws Exception {
        try (CLIDatabaseService db = new CLIDatabaseService()) {
            int n = db.countAsOf(metric, asOf);
            System.err.println(metric.name() + " as of " + asOf + ":");
            System.out.println(n);
        }
        return 0;
    }

    /** Accepts hyphenated or underscored, case-insensitive metric names. */
    public static class AsOfMetricConverter implements ITypeConverter<AsOfMetric> {
        @Override
        public AsOfMetric convert(String value) {
            return AsOfMetric.valueOf(value.trim().toUpperCase().replace('-', '_'));
        }
    }
}
