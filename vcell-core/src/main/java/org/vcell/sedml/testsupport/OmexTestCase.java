package org.vcell.sedml.testsupport;

/**
 * One row of the <b>expected results baseline</b> for the OMEX execution suites --
 * {@code vcell-cli/src/main/resources/test_cases.ndjson}, one JSON object per line.
 *
 * <p><b>What this file is.</b> It is the source of truth for <i>what each test case did last
 * time we accepted its result</i>. The nightly runs the whole collection, records what
 * actually happened as {@link OmexExecSummary}, and {@link OmexTestReport} fails the run when
 * the two disagree. It is therefore both a regression gate ("did we break something that
 * worked") and a compliance record ("which parts of SBML and SED-ML we handle, and where we
 * stop").
 *
 * <p><b>What this file is not.</b> It is not what the desktop client reads. The BMDB tab has
 * its own file, {@code vcell-client/src/main/resources/bioModelsNetInfo.xml}, which predicts
 * ahead of time which models will <i>open</i>; see
 * {@code cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo}. That file is <i>derived</i>
 * from this one and the two share vocabulary, but they answer different questions and
 * {@code BioModelsNetInfoTest} is what keeps them consistent -- a change here that moves a
 * model between import-blocking and not will fail that test until the XML is regenerated.
 *
 * <p><b>How to change it.</b> Never by hand. {@code test-report --update-test-cases <file>}
 * writes the baseline the current run implies, across all collections; accepting a change
 * means committing that artifact over this file, so the diff shows exactly which cases moved.
 *
 * @see OmexExecSummary what a run actually produced
 * @see FailureType the shared reason vocabulary
 */
public class OmexTestCase {

    public enum Status {
        /** The case ran and produced the results it was supposed to. */
        PASS,
        /** The case ran and did not. {@link #known_failure_type} says why. */
        FAIL,
        /**
         * The case was not run, so there is nothing to compare and it cannot fail the gate.
         *
         * <p>Two different situations currently share this value, and the distinction lives
         * only in {@link #known_failure_desc}:
         * <ul>
         *   <li><b>excluded from the run</b> -- too slow. The 11 SYSBIO_BIOMD models here are
         *       excluded by a hard-coded id list in {@code NightlyBMDB_CLI.yml}, not by
         *       anything in this file, so check that list before believing a
         *       "stopped running" signal.</li>
         *   <li><b>not applicable</b> -- VCell does not support the feature the case exists
         *       to exercise, e.g. {@code SEDML_SBML_LEVEL_CHANGE}. This is a capability
         *       statement wearing a skip.</li>
         * </ul>
         */
        SKIP
    }

    public OmexTestingDatabase.TestCollection test_collection;
    /** Archive path within the collection, e.g. {@code BIOMD0000000613.omex}. */
    public String file_path;
    /** True for cases whose whole point is to be rejected -- failing is the pass condition. */
    public Boolean should_fail;
    /** What we accepted last time. Null means the case has no documented result yet. */
    public Status known_status;
    /** Why it failed, from the shared vocabulary. Null unless {@link #known_status} is FAIL. */
    public FailureType known_failure_type;
    /**
     * The recorded message, kept verbatim so a changed failure can be read without re-running.
     * For a SKIP it carries the reason for skipping instead.
     */
    public String known_failure_desc;

    public OmexTestCase(OmexTestingDatabase.TestCollection testCollection, String filePath, Boolean shouldFail, Status status, FailureType failureType, String failureDesc) {
        this.test_collection = testCollection;
        this.file_path = filePath;
        this.should_fail = shouldFail;
        this.known_status = status;
        this.known_failure_type = failureType;
        this.known_failure_desc = failureDesc;
    }

    public OmexTestCase() {
    }

    @Override
    public String toString() {
        return "OmexTestCase{" +
                "test_collection=" + test_collection +
                ", file_path='" + file_path + '\'' +
                ", should_fail=" + should_fail +
                ", known_status=" + known_status +
                ", known_failure_type=" + known_failure_type +
                ", known_failure_desc="+((known_failure_desc!=null)?('\'' + known_failure_desc + '\''):null) +
                '}';
    }

}
