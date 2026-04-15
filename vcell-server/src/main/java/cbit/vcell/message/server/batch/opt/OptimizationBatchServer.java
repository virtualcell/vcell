package cbit.vcell.message.server.batch.opt;

import cbit.vcell.message.server.htc.HtcProxy;
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

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.*;

public class OptimizationBatchServer {

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
    public void initOptimizationQueue(String jmsHost, int jmsPort) {
        Thread optQueueThread = new Thread(() -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                        "tcp://" + jmsHost + ":" + jmsPort);
                connectionFactory.setTrustAllPackages(true);
                Connection connection = connectionFactory.createConnection();
                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination requestQueue = session.createQueue("opt-request");
                Destination statusQueue = session.createQueue("opt-status");
                MessageConsumer consumer = session.createConsumer(requestQueue);
                MessageProducer producer = session.createProducer(statusQueue);

                lg.info("Optimization JMS queue listener started on opt-request");

                while (true) {
                    Message message = consumer.receive(2000); // 2 second poll
                    if (message == null) continue;

                    if (message instanceof TextMessage textMessage) {
                        try {
                            String json = textMessage.getText();
                            OptRequestMessage request = objectMapper.readValue(json, OptRequestMessage.class);
                            lg.info("Received optimization request: command={}, jobId={}", request.command, request.jobId);

                            if ("submit".equals(request.command)) {
                                handleSubmitRequest(request, session, producer, objectMapper);
                            } else if ("stop".equals(request.command)) {
                                handleStopRequest(request);
                            } else {
                                lg.warn("Unknown optimization command: {}", request.command);
                            }
                        } catch (Exception e) {
                            lg.error("Error processing optimization request: {}", e.getMessage(), e);
                        }
                    }
                }
            } catch (Exception e) {
                lg.error("Optimization JMS queue listener failed: {}", e.getMessage(), e);
            }
        }, "optQueueListener");
        optQueueThread.setDaemon(true);
        optQueueThread.start();
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

    private void handleSubmitRequest(OptRequestMessage request, Session session,
                                     MessageProducer producer, ObjectMapper objectMapper) {
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
            sendStatusMessage(session, producer, objectMapper,
                    request.jobId, OptJobStatus.QUEUED, null, htcJobID.toDatabase());
        } catch (Exception e) {
            lg.error("Failed to submit optimization job {}: {}", request.jobId, e.getMessage(), e);
            try {
                sendStatusMessage(session, producer, objectMapper,
                        request.jobId, OptJobStatus.FAILED, e.getMessage(), null);
            } catch (JMSException jmsEx) {
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

    private void sendStatusMessage(Session session, MessageProducer producer, ObjectMapper objectMapper,
                                   String jobId, OptJobStatus status, String statusMessage, String htcJobId)
            throws JMSException {
        try {
            OptStatusMessage statusMsg = new OptStatusMessage(jobId, status, statusMessage, htcJobId);
            String json = objectMapper.writeValueAsString(statusMsg);
            TextMessage textMessage = session.createTextMessage(json);
            producer.send(textMessage);
            lg.info("Sent optimization status: jobId={}, status={}", jobId, status);
        } catch (Exception e) {
            lg.error("Failed to send status message for job {}: {}", jobId, e.getMessage(), e);
            throw new JMSException("Failed to serialize status message: " + e.getMessage());
        }
    }

}