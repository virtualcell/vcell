package org.vcell.admin.cli.tools.report;

import org.vcell.admin.cli.CLIDatabaseService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.LocalDate;
import java.util.concurrent.Callable;

/**
 * Count of {@code vc_simulationjob} rows whose submitDate falls in {@code [start, end]} (inclusive).
 *
 * <p><strong>This number undercounts the true historical job count.</strong>
 * {@code vc_simulationjob} has ON DELETE CASCADE on its parent simulation, so jobs
 * whose parent simulation was later deleted/replaced are gone. For total jobs ever
 * submitted to the compute cluster, use SLURM accounting:
 * <pre>
 *   sacct -S &lt;start&gt; -E &lt;end&gt; --truncate -X --noheader -o JobID | wc -l
 * </pre>
 *
 * <p>Output: integer on stdout, one human-readable line on stderr.
 * See {@code .claude/commands/admin-report.md} for context.
 */
@Command(name = "count-sim-jobs-in-db",
        description = "Count of vc_simulationjob rows with submitDate in [start, end] "
                + "(undercount due to cascade-delete; SLURM accounting is the true source).")
public class CountSimJobsInDbCommand implements Callable<Integer> {

    @Option(names = "--start", required = true,
            description = "Period start date (yyyy-MM-dd, inclusive).")
    private LocalDate start;

    @Option(names = "--end", required = true,
            description = "Period end date (yyyy-MM-dd, inclusive).")
    private LocalDate end;

    @Override
    public Integer call() throws Exception {
        try (CLIDatabaseService db = new CLIDatabaseService()) {
            int n = db.countSimJobsInDb(start, end);
            System.err.println("Sim jobs in DB with submitDate in [" + start + ", " + end + "]:");
            System.err.println("  (undercount; see SLURM sacct for true value)");
            System.out.println(n);
        }
        return 0;
    }
}
