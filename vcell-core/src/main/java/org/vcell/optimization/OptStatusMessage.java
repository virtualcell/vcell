package org.vcell.optimization;

/**
 * Internal message sent from vcell-submit back to vcell-rest with status updates.
 * Serialized as JSON over ActiveMQ (JMS/OpenWire on vcell-submit side, AMQP 1.0 on vcell-rest side).
 */
public class OptStatusMessage {
    public String jobId;
    public OptJobStatus status;
    public String statusMessage;
    public String htcJobId;            // set when SLURM job is submitted

    public OptStatusMessage() {}

    public OptStatusMessage(String jobId, OptJobStatus status, String statusMessage, String htcJobId) {
        this.jobId = jobId;
        this.status = status;
        this.statusMessage = statusMessage;
        this.htcJobId = htcJobId;
    }
}
