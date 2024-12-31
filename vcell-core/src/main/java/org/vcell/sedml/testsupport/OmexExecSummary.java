package org.vcell.sedml.testsupport;

public class OmexExecSummary {

    public enum ActualStatus {
        PASSED,
        FAILED
    }

    public String file_path;
    public ActualStatus status;
    public FailureType failure_type;
    public String failure_desc;
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
