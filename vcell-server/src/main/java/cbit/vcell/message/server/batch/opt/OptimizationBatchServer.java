package cbit.vcell.message.server.batch.opt;

import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.jms.artemis.VCMessagingServiceArtemis;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.optimization.OptJobStatus;
import org.vcell.optimization.OptRequestMessage;
import org.vcell.optimization.OptStatusMessage;
import org.vcell.optimization.jtd.OptProblem;
import org.vcell.optimization.jtd.Vcellopt;
import org.vcell.util.exe.ExecutableException;


import java.io.*;

public class OptimizationBatchServer {

    private static final VCellQueue OPT_REQUEST_QUEUE = new VCellQueue("opt-request");
    private static final VCellQueue OPT_STATUS_QUEUE = new VCellQueue("opt-status");
    /** status updates are only useful while the caller is still waiting */
    private static final long STATUS_TIME_TO_LIVE_MS = 3600_000L;

    private VCMessagingService optMessagingService;
    private VCMessageSession optStatusSession;


    private final static Logger lg = LogManager.getLogger(OptimizationBatchServer.class);
    private HtcProxy.HtcProxyFactory htcProxyFactory = null;

    public OptimizationBatchServer(HtcProxy.HtcProxyFactory htcProxyFactory){
        this.htcProxyFactory = htcProxyFactory;
    }

    private HtcProxy getHtcProxy() {
        return htcProxyFactory.getHtcProxy();
    }

    /**
     * Initialize a JMS queue listener on "opt-request" for cross-protocol messaging with vcell-rest (AMQP 1.0).
     * Receives submit/stop commands as JSON text messages, dispatches to SLURM, and sends status updates
     * back on "opt-status".
     */
    /**
     * Listen for optimization requests on the Artemis broker.
     *
     * This used to hand-roll its own JMS: a raw ActiveMQConnectionFactory on a plain
     * "tcp://host:port" URL, its own daemon thread, and a while(true) receive loop. That worked,
     * but it sat outside {@link VCMessagingService} and so inherited none of its policy -- no
     * bounded failover URL, no {@link cbit.vcell.message.jms.JmsFailoverWatchdog}, no delegate,
     * no shared connection handling. Nobody decided the optimization path should be less
     * resilient than everything else; it simply was not routed through the place that decides.
     *
     * Going through VCMessagingServiceArtemis gets all of that, and the request/reply shape is
     * unchanged on the wire: JSON text on "opt-request" and "opt-status", which is what
     * vcell-rest publishes and consumes over AMQP 1.0 on the same addresses.
     */
    public void initOptimizationQueue(String jmsHost, int jmsPort) {
        VCMessagingServiceArtemis messagingService = new VCMessagingServiceArtemis();
        messagingService.setConfiguration(new SimpleMessagingDelegate(), jmsHost, jmsPort);
        initOptimizationQueue(messagingService);
    }

    /** Package-visible so tests can supply a service pointed at a broker container. */
    void initOptimizationQueue(VCMessagingService messagingService) {
        this.optMessagingService = messagingService;
        this.optStatusSession = messagingService.createProducerSession();
        ObjectMapper objectMapper = new ObjectMapper();

        VCQueueConsumer consumer = new VCQueueConsumer(OPT_REQUEST_QUEUE, (vcMessage, session) -> {
            String json = vcMessage.getTextContent();
            if (json == null) {
                lg.warn("optimization request had no text content, ignoring");
                return;
            }
            try {
                OptRequestMessage request = objectMapper.readValue(json, OptRequestMessage.class);
                lg.info("Received optimization request: command={}, jobId={}", request.command, request.jobId);
                if ("submit".equals(request.command)) {
                    handleSubmitRequest(request, objectMapper);
                } else if ("stop".equals(request.command)) {
                    handleStopRequest(request);
                } else {
                    lg.warn("Unknown optimization command: {}", request.command);
                }
            } catch (Exception e) {
                lg.error("Error processing optimization request: {}", e.getMessage(), e);
            }
        }, null, "optQueueListener", 1);

        messagingService.addMessageConsumer(consumer);
        lg.info("Optimization queue listener started on {}", OPT_REQUEST_QUEUE.getName());
    }

    /**
     * Validate that a file path is under the expected parest_data directory to prevent path traversal.
     */
    private static File validateParestPath(String filePath) throws IOException {
        File file = new File(filePath).getCanonicalFile();
        java.nio.file.Path parestDir = new File(PropertyLoader.getRequiredProperty(
                PropertyLoader.primarySimDataDirInternalProperty), "parest_data").getCanonicalFile().toPath();
        if (!file.toPath().startsWith(parestDir)) {
            throw new IOException("Invalid optimization file path (outside parest_data): " + filePath);
        }
        return file;
    }

    private void handleSubmitRequest(OptRequestMessage request, ObjectMapper objectMapper) {
        try {
            // Validate jobId is numeric (database key) to prevent injection in file names
            Long.parseLong(request.jobId);

            // Validate paths are under parest_data directory
            File optProblemFile = validateParestPath(request.optProblemFilePath);
            File optOutputFile = validateParestPath(request.optOutputFilePath);
            File optReportFile = validateParestPath(request.optReportFilePath);

            // The OptProblem file is already written by vcell-rest — read it
            OptProblem optProblem = objectMapper.readValue(optProblemFile, OptProblem.class);

            HtcProxy htcProxyClone = getHtcProxy().cloneThreadsafe();
            File htcLogDirExternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal));
            File htcLogDirInternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirInternal));
            String slurmOptJobName = "CopasiParest_" + request.jobId;
            String optSubFileName = slurmOptJobName + ".sub";
            File sub_file_external = new File(htcLogDirExternal, optSubFileName);
            File sub_file_internal = new File(htcLogDirInternal, optSubFileName);

            HtcJobID htcJobID = htcProxyClone.submitOptimizationJob(
                    slurmOptJobName, sub_file_internal, sub_file_external,
                    optProblemFile, optOutputFile, optReportFile);

            lg.info("Submitted SLURM job {} for optimization jobId={}", htcJobID, request.jobId);

            // Send QUEUED status back with htcJobId
            sendStatusMessage(objectMapper,
                    request.jobId, OptJobStatus.QUEUED, null, htcJobID.toDatabase());
        } catch (Exception e) {
            lg.error("Failed to submit optimization job {}: {}", request.jobId, e.getMessage(), e);
            try {
                sendStatusMessage(objectMapper,
                        request.jobId, OptJobStatus.FAILED, e.getMessage(), null);
            } catch (VCMessagingException jmsEx) {
                lg.error("Failed to send FAILED status for job {}: {}", request.jobId, jmsEx.getMessage(), jmsEx);
            }
        }
    }

    private void handleStopRequest(OptRequestMessage request) {
        if (request.htcJobId == null) {
            lg.warn("Cannot stop optimization job {} — no htcJobId", request.jobId);
            return;
        }
        try {
            HtcProxy htcProxyClone = getHtcProxy().cloneThreadsafe();
            // htcJobId is in toDatabase() format: "SLURM:12345" or "SLURM:12345.server"
            String htcJobIdStr = request.htcJobId;
            HtcJobID htcJobID;
            if (htcJobIdStr.startsWith("SLURM:")) {
                htcJobID = new HtcJobID(htcJobIdStr.substring("SLURM:".length()), HtcJobID.BatchSystemType.SLURM);
            } else {
                htcJobID = new HtcJobID(htcJobIdStr, HtcJobID.BatchSystemType.SLURM);
            }
            String jobName = "CopasiParest_" + request.jobId;
            htcProxyClone.killJobSafe(new HtcProxy.HtcJobInfo(htcJobID, jobName));
            lg.info("Stopped SLURM job {} for optimization jobId={}", request.htcJobId, request.jobId);
        } catch (Exception e) {
            lg.error("Failed to stop optimization job {}: {}", request.jobId, e.getMessage(), e);
        }
    }

    private void sendStatusMessage(ObjectMapper objectMapper,
                                   String jobId, OptJobStatus status, String statusMessage, String htcJobId)
            throws VCMessagingException {
        try {
            OptStatusMessage statusMsg = new OptStatusMessage(jobId, status, statusMessage, htcJobId);
            String json = objectMapper.writeValueAsString(statusMsg);
            optStatusSession.sendQueueMessage(OPT_STATUS_QUEUE, optStatusSession.createTextMessage(json),
                    Boolean.FALSE, STATUS_TIME_TO_LIVE_MS);
            lg.info("Sent optimization status: jobId={}, status={}", jobId, status);
        } catch (Exception e) {
            lg.error("Failed to send status message for job {}: {}", jobId, e.getMessage(), e);
            throw new VCMessagingException("Failed to serialize status message: " + e.getMessage(), e);
        }
    }

}