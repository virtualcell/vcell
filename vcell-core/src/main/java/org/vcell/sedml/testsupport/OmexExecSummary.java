package org.vcell.sedml.testsupport;

/**
 * What one archive actually did on <i>this</i> run -- the observed half of the comparison
 * whose expected half is {@link OmexTestCase}.
 *
 * <p>One of these is written per archive as {@code exec_summary.json}, and the nightly
 * concatenates them into {@code exec_summary.ndjson} for {@code test-report} to read. Unlike
 * the baseline it is never committed: it is evidence of a single run, and downloading it from
 * a nightly is the way to reproduce that run's verdict locally.
 *
 * <p><b>{@link #failure_type} is decided at execution time</b>, by the summarising code that
 * classifies the exception as it happens -- not by {@code test-report} afterwards. So a new
 * classification rule only takes effect from the next run, and cannot be validated against an
 * {@code exec_summary.ndjson} that has already been recorded.
 *
 * @see OmexTestCase the accepted baseline this is compared against
 */
public class OmexExecSummary {

    public enum ActualStatus {
        PASSED,
        FAILED
    }

    /** Absolute path of the archive as the run saw it, e.g. {@code /root/BIOMD0000000613.omex}. */
    public String file_path;
    public ActualStatus status;
    /** Classified as the failure happened; null when {@link #status} is PASSED. */
    public FailureType failure_type;
    /** The raw message. Its shape varies by failure type; only {@link #failure_type} is compared. */
    public String failure_desc;
    /**
     * Wall-clock duration. Useful for telling apart failures at different phases of the
     * pipeline -- an import failure is much quicker than one that reached the solver.
     */
    public long elapsed_time_ms;

    @Override
    public String toString() {
        return "OmexExecSummary{" +
                ", file_path='" + file_path + '\'' +
                ", status=" + status +
                ", failure_type=" + failure_type +
                ", failure_desc="+((failure_desc!=null)?('\'' + failure_desc + '\''):null) +
                ", elapsed_time_ms=" + elapsed_time_ms +
                '}';
    }
}
