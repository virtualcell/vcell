package org.vcell.admin.cli.tools.report;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Parent picocli command grouping the period-bounded usage-report primitives.
 * Has no body of its own; subcommands do the work.
 *
 * <p>See {@code .claude/commands/admin-report.md} for the toolkit overview.
 */
@Command(name = "report",
        description = "Period-bounded VCell usage report primitives (RPPR-style metrics).",
        subcommands = {
                CountNewUsersCommand.class,
                CountSimJobsInDbCommand.class,
                CountAsOfCommand.class,
                ListActiveUsersCommand.class,
                CommandLine.HelpCommand.class
        })
public class ReportCommand {
}
