/**
 * Modular admincli report toolkit for period-bounded VCell usage reports
 * (NIH RPPR and similar).
 *
 * <p>Each command in this package does exactly one thing and emits a single
 * value (an integer count) or a single CSV. Compose them in shell to assemble
 * a report for a particular period; see {@code .claude/commands/admin-report.md}
 * for the recipe and a checklist for adding new metrics next year.
 *
 * <p>SQL implementations live on
 * {@link cbit.vcell.modeldb.AdminDBTopLevel} (search for {@code countNewUsers},
 * {@code countSimJobsInDb}, {@code countAsOf}, {@code listActiveUsersInPeriod}).
 * They follow the convention of building queries from
 * {@link cbit.sql.Field#getQualifiedColName Field.getQualifiedColName()} on
 * {@code Table}-class singletons rather than from raw column-name string
 * literals, and bind dates via {@code PreparedStatement.setDate}.
 *
 * <p>Caveat: the {@code count-sim-jobs-in-db} metric undercounts the true number
 * of jobs ever submitted to the compute cluster because {@code vc_simulationjob}
 * rows cascade-delete with their parent simulation. The full count must be
 * obtained from SLURM accounting (e.g. {@code sacct}); see the slash-command
 * markdown for the invocation.
 */
package org.vcell.admin.cli.tools.report;
