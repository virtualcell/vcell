# admin-report — VCell usage-report toolkit

Use this when someone asks for a period-bounded VCell usage report (NIH RPPR, year-end summary, custom date-range stats) or for an active-user list. The toolkit is a set of single-purpose `admincli report` subcommands that each emit one number or one CSV. Compose them in shell to assemble whatever shape the request needs.

## When invoked

The argument is a free-form description of what is needed (e.g. "RPPR for July 1, 2025 through May 8, 2026; we need new-user count, total-models as-of, and the active-user email list"). Translate it into a sequence of admincli invocations, capture each output to a file, and report the collected numbers + the path to the active-user CSV.

The four primitives:

| Command | Output | What it computes |
| --- | --- | --- |
| `admincli report count-new-users --start <date> --end <date>` | int on stdout | `vc_userinfo` rows whose `insertDate` is in `[start, end]` |
| `admincli report count-sim-jobs-in-db --start <date> --end <date>` | int on stdout | `vc_simulationjob` rows whose `submitDate` is in `[start, end]`. **Undercount** — see SLURM caveat below. |
| `admincli report count-asof --metric <m> --asof <date>` | int on stdout | Cumulative count of `<m>` as of `<date>`. See "Available `--metric` values" below. |
| `admincli report list-active-users --start <date> --end <date> [-o <file>]` | CSV on stdout (or `-o`) | One row per distinct user who submitted a sim job in `[start, end]`: `userid,email,company,country,tld_extension`. |

All commands write a one-line human-readable description to stderr; pipelines that capture stdout get just the number/CSV.

## Available `--metric` values for `count-asof`

Case-insensitive, hyphens or underscores both work.

- `total-users` — total registered VCell users
- `users-with-sims` — distinct users with at least one simulation
- `biomodels`, `mathmodels`, `total-models` — model counts (total = biomodels + mathmodels)
- `simulations` — total simulations
- `public-biomodels`, `public-mathmodels`, `public-models` — `PRIVACY=0` model counts
- `public-biomodel-sims`, `public-mathmodel-sims`, `public-sims` — simulations belonging to public models

Each metric is evaluated as `<expr> WHERE versionDate <= asof` (for the model/sim counts) or `WHERE insertDate <= asof` (for `total-users`).

## SLURM caveat for "jobs submitted"

`count-sim-jobs-in-db` undercounts the historical job total because `vc_simulationjob` cascade-deletes when its parent simulation is removed/replaced. The true count of jobs submitted to the compute cluster lives in SLURM accounting. Run on the HTC head node:

```bash
sacct -S <start> -E <end> --truncate -X --noheader -o JobID | wc -l
```

The DB number is still useful as a lower bound and as a sanity check against SLURM.

## Build prerequisite

The driver script does **not** rebuild the Maven project. Before running it for the first time after any source change, build once from the project root:

```bash
mvn clean package dependency:copy-dependencies -DskipTests -pl '!vcell-rest'
```

(vcell-rest is excluded because Quarkus dependency-convergence enforcement fails locally; admincli doesn't depend on it.) If your sources haven't changed since the last build, skip this — `tools/report/rppr-2026.sh` will fail fast with a clear message if `vcell-admin/target/maven-jars` is missing.

## Recipe — RPPR-style report

The packaged driver is `tools/report/rppr-2026.sh`. It expects four DB env vars (`VCELL_DB_URL`, `VCELL_DB_DRIVER`, `VCELL_DB_USER`, `VCELL_DB_PASSWORD`) and writes one file per metric plus an active-users CSV to `tools/report/output/rppr-2026/` by default. Override the period or output dir with `REPORT_START=`/`REPORT_END=`/`REPORT_OUT_DIR=`.

If you need to call the primitives by hand instead:

```bash
START=2025-07-01
END=2026-05-08
OUT=/tmp/rppr-2026
mkdir -p "$OUT"

admincli report count-new-users        --start "$START" --end "$END" > "$OUT/new_users.txt"
admincli report count-sim-jobs-in-db   --start "$START" --end "$END" > "$OUT/sim_jobs_in_db.txt"

for m in total-users users-with-sims total-models simulations public-models public-sims; do
  admincli report count-asof --metric "$m" --asof "$END" > "$OUT/asof_${m}.txt"
done

admincli report list-active-users      --start "$START" --end "$END" -o "$OUT/active_users.csv"

# True job-submission count (SLURM head node):
#   sacct -S "$START" -E "$END" --truncate -X --noheader -o JobID | wc -l
```

Each `*.txt` contains a single integer; `active_users.csv` is a header-included CSV ready for spreadsheet import.

## Adding a new metric next year

- **New as-of metric** (e.g. "models published in the last year"): add an enum value to `cbit.vcell.modeldb.AdminDBTopLevel.AsOfMetric` and a corresponding SQL branch in `countAsOfImpl`. No new command class needed; `count-asof --metric <new-name>` works immediately.
- **New period-bounded count** (e.g. "models created in period"): add a new method on `AdminDBTopLevel` and a new `*Command.java` in `vcell-admin/src/main/java/org/vcell/admin/cli/tools/report/`, then register it in `ReportCommand.subcommands`. ~80 lines total. Existing commands untouched.
- **New list output** (e.g. "active users grouped by country"): new command class, reuses or composes existing query methods.
- **Different report shape** (PDF, slack message, …): wrap the existing primitives in a shell script. Don't touch Java.

## SQL convention (when modifying the queries)

`AdminDBTopLevel.getBasicStatistics()` (the older method that drives `/api/v1/admin/usage`) embeds raw column names as string literals. **Do not follow that example.** The new report queries reference columns through `Table`-class `Field` objects:

```java
UserTable.table.insertDate.getQualifiedColName()      // -> "vc_userinfo.insertDate"
SimulationTable.table.versionDate.getQualifiedColName()
BioModelTable.table.privacy.getQualifiedColName()
```

Date predicates use `PreparedStatement.setDate(i, java.sql.Date.valueOf(localDate))` — never string-interpolated dates.

## Internal references

- SQL: `vcell-server/src/main/java/cbit/vcell/modeldb/AdminDBTopLevel.java`
  — `countNewUsers`, `countSimJobsInDb`, `countAsOf`, `countAsOfImpl`, `listActiveUsersInPeriod`, `AsOfMetric`, `ActiveUser`.
- CLI: `vcell-admin/src/main/java/org/vcell/admin/cli/tools/report/`.
- Service wrapper: `vcell-admin/src/main/java/org/vcell/admin/cli/CLIDatabaseService.java`.
- Schema (Field references):
  - `vc_userinfo`: `UserTable.table.{insertDate, email, companyName, country, userid, id}`.
  - Versioning columns inherited via `VersionTable` → `BioModelTable`, `MathModelTable`, `SimulationTable`: `versionDate`, `privacy`, `ownerRef`.
  - `vc_simulationjob`: `SimulationJobTable.table.{submitDate, simRef}`.
  - Link tables: `BioModelSimulationLinkTable`, `MathModelSimulationLinkTable`.
