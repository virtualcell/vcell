package org.vcell.admin.cli.tools.report;

import org.vcell.admin.cli.CLIDatabaseService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.LocalDate;
import java.util.concurrent.Callable;

/**
 * Count of {@code vc_userinfo} rows whose insertDate falls in {@code [start, end]} (inclusive) — i.e.,
 * the number of new VCell users registered during the period.
 *
 * <p>Output: integer on stdout, one human-readable line on stderr.
 *
 * <p>Part of the {@code report} toolkit; see {@code .claude/commands/admin-report.md}
 * for the full RPPR recipe.
 */
@Command(name = "count-new-users",
        description = "Count of vc_userinfo rows with insertDate in [start, end].")
public class CountNewUsersCommand implements Callable<Integer> {

    @Option(names = "--start", required = true,
            description = "Period start date (yyyy-MM-dd, inclusive).")
    private LocalDate start;

    @Option(names = "--end", required = true,
            description = "Period end date (yyyy-MM-dd, inclusive).")
    private LocalDate end;

    @Override
    public Integer call() throws Exception {
        try (CLIDatabaseService db = new CLIDatabaseService()) {
            int n = db.countNewUsers(start, end);
            System.err.println("New users in [" + start + ", " + end + "]:");
            System.out.println(n);
        }
        return 0;
    }
}
