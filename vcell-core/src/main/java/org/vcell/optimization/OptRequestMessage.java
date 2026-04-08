package org.vcell.optimization;

/**
 * Internal message sent from vcell-rest to vcell-submit to request job submission or stop.
 * Serialized as JSON over ActiveMQ (AMQP 1.0 on vcell-rest side, JMS/OpenWire on vcell-submit side).
 */
public class OptRequestMessage {
    public String jobId;
    public String command;             // "submit" or "stop"
    public String optProblemFilePath;   // for submit
    public String optOutputFilePath;    // for submit
    public String optReportFilePath;    // for submit
    public String htcJobId;            // for stop (SLURM job to cancel)

    public OptRequestMessage() {}

    public OptRequestMessage(String jobId, String command, String optProblemFilePath,
                             String optOutputFilePath, String optReportFilePath, String htcJobId) {
        this.jobId = jobId;
        this.command = command;
        this.optProblemFilePath = optProblemFilePath;
        this.optOutputFilePath = optOutputFilePath;
        this.optReportFilePath = optReportFilePath;
        this.htcJobId = htcJobId;
    }
}
