#!/usr/bin/env bash
# Driver for the 2026 NIH RPPR usage report.
# Period: REPORT_START .. REPORT_END (defaults below).
#
# Outputs (one file per metric, plus a per-user CSV) land in REPORT_OUT_DIR.
#
# PRECONDITION: the Maven project must be built and dependencies copied to
# vcell-admin/target/maven-jars BEFORE running this script. The script does
# NOT rebuild — that decision is left to the caller so a clean build doesn't
# block every invocation. From the project root:
#
#     mvn clean package dependency:copy-dependencies -DskipTests -pl '!vcell-rest'
#
# (vcell-rest is excluded because Quarkus dependency-convergence enforcement
# fails locally; admincli does not depend on it.)
#
# Skip the rebuild if you have already built since your last source edit.
#
# DB credentials are NOT in this script. Source them from your environment
# (e.g. ~/.vcell/admincli-env, your local secrets store, or paste them inline
# for the run). Required env vars:
#
#   VCELL_DB_URL         e.g. jdbc:oracle:thin:@vcell-oracle.cam.uchc.edu:1521/ORCLPDB1
#   VCELL_DB_DRIVER      e.g. oracle.jdbc.driver.OracleDriver
#   VCELL_DB_USER        the dev/reporting role (NOT the service role)
#   VCELL_DB_PASSWORD    password for that role
#
# Use the dev role (e.g. vcell_dev) so reporting credentials stay separate
# from service credentials. Both roles point at the same database.
#
# See .claude/commands/admin-report.md for what each metric means and the
# SLURM caveat for "jobs submitted to compute server".

set -euo pipefail

START="${REPORT_START:-2025-07-01}"
END="${REPORT_END:-2026-05-01}"
PROJECT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
OUT_DIR="${REPORT_OUT_DIR:-$PROJECT_DIR/tools/report/output/rppr-2026}"

JAR="$PROJECT_DIR/vcell-admin/target/vcell-admin-0.0.1-SNAPSHOT.jar"
LIB="$PROJECT_DIR/vcell-admin/target/maven-jars"

require_env() {
    for v in VCELL_DB_URL VCELL_DB_DRIVER VCELL_DB_USER VCELL_DB_PASSWORD; do
        if [ -z "${!v:-}" ]; then
            echo "ERROR: $v is not set." >&2
            exit 2
        fi
    done
}

ensure_built() {
    # Verify the build artifacts exist; do NOT rebuild. The caller is responsible
    # for ensuring the Maven project is up-to-date before invoking this script.
    if [ ! -f "$JAR" ] || [ ! -d "$LIB" ] || [ -z "$(ls -A "$LIB" 2>/dev/null)" ]; then
        cat >&2 <<EOF
ERROR: Maven build artifacts missing.
       Expected:
         $JAR
         $LIB/*.jar

       Build first, then re-run this script:
         (cd "$PROJECT_DIR" && \\
          mvn clean package dependency:copy-dependencies -DskipTests -pl '!vcell-rest')
EOF
        exit 3
    fi
}

run_admincli() {
    # -Dlog4j2.statusLoggerLevel=OFF suppresses Log4j2's StatusLogger warnings
    # (e.g. "package scanning is deprecated") that otherwise land on stdout
    # and pollute single-integer metric outputs.
    java \
        -Dlog4j2.statusLoggerLevel=OFF \
        -Dvcell.installDir="$PROJECT_DIR" \
        -Dvcell.server.dbConnectURL="$VCELL_DB_URL" \
        -Dvcell.server.dbDriverName="$VCELL_DB_DRIVER" \
        -Dvcell.server.dbUserid="$VCELL_DB_USER" \
        -Dvcell.server.dbPassword="$VCELL_DB_PASSWORD" \
        -cp "$JAR:$LIB/*" \
        org.vcell.admin.cli.AdminCLI "$@"
}

require_env
ensure_built
mkdir -p "$OUT_DIR"

echo "RPPR 2026 — period [$START, $END] -> $OUT_DIR" >&2
echo >&2

# Each count command emits the integer as the final stdout line; earlier lines
# may carry Log4j2 StatusLogger initialization warnings. `tail -1` grabs just the
# count for the per-metric output file.

run_admincli report count-new-users --start "$START" --end "$END" \
    | tail -1 > "$OUT_DIR/new_users.txt"

run_admincli report count-sim-jobs-in-db --start "$START" --end "$END" \
    | tail -1 > "$OUT_DIR/sim_jobs_in_db.txt"

for m in total-users users-with-sims biomodels mathmodels total-models simulations \
         public-biomodels public-mathmodels public-models \
         public-biomodel-sims public-mathmodel-sims public-sims; do
    run_admincli report count-asof --metric "$m" --asof "$END" \
        | tail -1 > "$OUT_DIR/asof_${m}.txt"
done

run_admincli report list-active-users \
    --start "$START" --end "$END" \
    -o "$OUT_DIR/active_users.csv"

cat >&2 <<EOF

------------------------------------------------------------------------
Done. Outputs in: $OUT_DIR

Period metrics:
  new_users:         $(cat "$OUT_DIR/new_users.txt")
  sim_jobs_in_db:    $(cat "$OUT_DIR/sim_jobs_in_db.txt") (undercount; see SLURM below)

Cumulative as of $END:
  total_users:           $(cat "$OUT_DIR/asof_total-users.txt")
  users_with_sims:       $(cat "$OUT_DIR/asof_users-with-sims.txt")
  total_models:          $(cat "$OUT_DIR/asof_total-models.txt")
  total_simulations:     $(cat "$OUT_DIR/asof_simulations.txt")
  public_models:         $(cat "$OUT_DIR/asof_public-models.txt")
  public_simulations:    $(cat "$OUT_DIR/asof_public-sims.txt")

active_users.csv: $(wc -l < "$OUT_DIR/active_users.csv") line(s) (incl. header)

Reminder: the true count of jobs submitted to the compute cluster lives in
SLURM accounting. On the HTC head node:
    sacct -S "$START" -E "$END" --truncate -X --noheader -o JobID | wc -l
------------------------------------------------------------------------
EOF
