package org.vcell.restq.testresources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.optimization.OptJobStatus;
import org.vcell.optimization.OptRequestMessage;
import org.vcell.optimization.OptStatusMessage;
import org.vcell.optimization.jtd.*;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test stub that mimics vcell-submit's OptimizationBatchServer.initOptimizationQueue()
 * using the same OpenWire JMS protocol (ActiveMQConnectionFactory).
 *
 * Mirrors the real handleSubmitRequest() flow:
 *   1. Receive OptRequestMessage from opt-request queue
 *   2. Validate jobId is numeric
 *   3. Read the OptProblem file from the path in the message (validates filesystem handoff)
 *   4. Write result files to the paths from the message
 *   5. Send QUEUED status back on opt-status queue with htcJobId
 *
 * The only difference from production is that instead of submitting to SLURM,
 * the stub writes result files directly (simulating what the SLURM job would produce).
 */
public class OpenWireOptSubmitStub {
    private static final Logger lg = LogManager.getLogger(OpenWireOptSubmitStub.class);

    private final String host;
    private final int port;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Connection connection;
    private final CountDownLatch ready = new CountDownLatch(1);

    public OpenWireOptSubmitStub(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        // Same connection setup as OptimizationBatchServer.initOptimizationQueue()
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
        factory.setTrustAllPackages(true);
        Connection conn = factory.createConnection();
        conn.start();
        this.connection = conn;

        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination requestQueue = session.createQueue("opt-request");
        Destination statusQueue = session.createQueue("opt-status");
        MessageConsumer consumer = session.createConsumer(requestQueue);
        MessageProducer producer = session.createProducer(statusQueue);

        Thread listenerThread = new Thread(() -> {
            ready.countDown();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Message msg = consumer.receive(1000);
                    if (msg instanceof TextMessage textMsg) {
                        OptRequestMessage req = objectMapper.readValue(textMsg.getText(), OptRequestMessage.class);
                        lg.info("Stub received: command={}, jobId={}", req.command, req.jobId);
                        if ("submit".equals(req.command)) {
                            handleSubmitRequest(req, session, producer);
                        } else if ("stop".equals(req.command)) {
                            lg.info("Stub received stop for job {} (no-op in test)", req.jobId);
                        } else {
                            lg.warn("Stub: unknown command: {}", req.command);
                        }
                    }
                } catch (JMSException e) {
                    if (!Thread.currentThread().isInterrupted()) {
                        lg.error("Stub listener error: {}", e.getMessage());
                    }
                    break;
                } catch (Exception e) {
                    lg.error("Stub processing error: {}", e.getMessage(), e);
                }
            }
        }, "openwire-opt-stub");
        listenerThread.setDaemon(true);
        listenerThread.start();

        if (!ready.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Stub listener did not start in time");
        }
    }

    /**
     * Mirrors OptimizationBatchServer.handleSubmitRequest() — same validation, same file I/O,
     * same status message pattern. Only SLURM submission is replaced with direct file writes.
     */
    /**
     * Validate that a file path is under the expected parest_data directory to prevent path traversal.
     * Same logic as OptimizationBatchServer.validateParestPath().
     */
    private static File validatePath(String filePath, String baseDir) throws java.io.IOException {
        File file = new File(filePath).getCanonicalFile();
        if (!file.getPath().startsWith(new File(baseDir).getCanonicalPath())) {
            throw new java.io.IOException("Invalid file path (outside base dir): " + filePath);
        }
        return file;
    }

    private void handleSubmitRequest(OptRequestMessage request, Session session, MessageProducer producer) {
        try {
            // Validate jobId is numeric (same as real handleSubmitRequest)
            Long.parseLong(request.jobId);

            // Validate and read the OptProblem file from the path in the message
            File optProblemFile = validatePath(request.optProblemFilePath,
                    new File(request.optProblemFilePath).getParentFile().getCanonicalPath());
            OptProblem optProblem = objectMapper.readValue(optProblemFile, OptProblem.class);
            lg.info("Stub: read OptProblem from {}, params={}",
                    optProblemFile.getName(), optProblem.getParameterDescriptionList().size());

            // Validate file paths from the message
            String baseDir = optProblemFile.getParentFile().getCanonicalPath();
            File optOutputFile = validatePath(request.optOutputFilePath, baseDir);
            File optReportFile = validatePath(request.optReportFilePath, baseDir);

            // Write progress report file (simulates what the SLURM CopasiParest job writes)
            writeProgressReport(optReportFile);

            // Write result file (simulates SLURM job completion)
            writeResults(optOutputFile);

            lg.info("Stub: wrote results for job {}", request.jobId);

            // Send QUEUED status back with htcJobId (same as real handleSubmitRequest)
            sendStatusMessage(session, producer, request.jobId, OptJobStatus.QUEUED, null, "SLURM:99999");

        } catch (Exception e) {
            lg.error("Stub: failed to process job {}: {}", request.jobId, e.getMessage(), e);
            try {
                // Send FAILED status (same error handling as real handleSubmitRequest)
                sendStatusMessage(session, producer, request.jobId, OptJobStatus.FAILED, e.getMessage(), null);
            } catch (JMSException jmsEx) {
                lg.error("Stub: failed to send FAILED status: {}", jmsEx.getMessage(), jmsEx);
            }
        }
    }

    private void writeProgressReport(File reportFile) throws Exception {
        reportFile.getCanonicalFile().getParentFile().mkdirs();
        java.nio.file.Files.writeString(reportFile.getCanonicalFile().toPath(),
                "[\"k1\",\"k2\"]\n" +
                "10\t0.5\t1.0\t2.0\n" +
                "20\t0.1\t1.3\t2.4\n" +
                "30\t0.01\t1.5\t2.5\n");
    }

    private void writeResults(File outputFile) throws Exception {
        Vcellopt vcellopt = new Vcellopt();
        vcellopt.setStatus(VcelloptStatus.COMPLETE);
        vcellopt.setStatusMessage("optimization complete");
        OptResultSet resultSet = new OptResultSet();
        resultSet.setNumFunctionEvaluations(30);
        resultSet.setObjectiveFunction(0.001);
        resultSet.setOptParameterValues(Map.of("k1", 1.5, "k2", 2.5));
        OptProgressReport progressReport = new OptProgressReport();
        progressReport.setBestParamValues(Map.of("k1", 1.5, "k2", 2.5));
        OptProgressItem item1 = new OptProgressItem();
        item1.setNumFunctionEvaluations(10);
        item1.setObjFuncValue(0.5);
        OptProgressItem item2 = new OptProgressItem();
        item2.setNumFunctionEvaluations(20);
        item2.setObjFuncValue(0.1);
        OptProgressItem item3 = new OptProgressItem();
        item3.setNumFunctionEvaluations(30);
        item3.setObjFuncValue(0.01);
        progressReport.setProgressItems(List.of(item1, item2, item3));
        resultSet.setOptProgressReport(progressReport);
        vcellopt.setOptResultSet(resultSet);
        objectMapper.writeValue(outputFile.getCanonicalFile(), vcellopt);
    }

    /**
     * Same signature and logic as OptimizationBatchServer.sendStatusMessage().
     */
    private void sendStatusMessage(Session session, MessageProducer producer,
                                   String jobId, OptJobStatus status, String statusMessage, String htcJobId)
            throws JMSException {
        try {
            OptStatusMessage statusMsg = new OptStatusMessage(jobId, status, statusMessage, htcJobId);
            String json = objectMapper.writeValueAsString(statusMsg);
            TextMessage textMessage = session.createTextMessage(json);
            producer.send(textMessage);
            lg.info("Stub sent status: jobId={}, status={}", jobId, status);
        } catch (Exception e) {
            lg.error("Stub: failed to send status for job {}: {}", jobId, e.getMessage(), e);
            throw new JMSException("Failed to serialize status message: " + e.getMessage());
        }
    }

    public void stop() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
