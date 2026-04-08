package copasi;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.api.client.VCellApiClient;
import org.vcell.optimization.CopasiOptSolverCallbacks;
import org.vcell.optimization.CopasiUtils;
import org.vcell.optimization.jtd.*;
import org.vcell.restclient.api.OptimizationResourceApi;
import org.vcell.restclient.model.OptJobStatus;
import org.vcell.restclient.model.OptimizationJobStatus;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;

import java.util.function.Consumer;

public class CopasiOptimizationSolverRemote {
    private final static Logger lg = LogManager.getLogger(CopasiOptimizationSolverRemote.class);

    /**
     * Entry point for the desktop client GUI. Gets the OptimizationResourceApi from the request manager
     * and delegates to solveRemoteApi(OptimizationResourceApi, ...) with Swing-based progress updates.
     */
    public static OptimizationResultSet solveRemoteApi(
            ParameterEstimationTask parameterEstimationTask,
            CopasiOptSolverCallbacks optSolverCallbacks,
            ClientTaskStatusSupport clientTaskStatusSupport,
            ClientRequestManager requestManager) {

        VCellApiClient vcellApiClient = requestManager.getClientServerManager().getVCellApiClient();
        OptimizationResourceApi optApi = vcellApiClient.getOptimizationApi();

        // Use SwingUtilities.invokeLater for progress updates in the GUI
        Consumer<Runnable> progressDispatcher = javax.swing.SwingUtilities::invokeLater;

        return solveRemoteApi(parameterEstimationTask, optSolverCallbacks, clientTaskStatusSupport,
                optApi, progressDispatcher);
    }

    /**
     * Core optimization solver logic. Testable without Swing — accepts an OptimizationResourceApi
     * directly and a pluggable progress dispatcher.
     *
     * @param parameterEstimationTask the parameter estimation task to solve
     * @param optSolverCallbacks callbacks for progress reports and stop requests
     * @param clientTaskStatusSupport status message and progress bar updates (nullable)
     * @param optApi the generated REST client API for optimization endpoints
     * @param progressDispatcher how to dispatch progress updates (SwingUtilities::invokeLater in GUI, Runnable::run in tests)
     */
    public static OptimizationResultSet solveRemoteApi(
            ParameterEstimationTask parameterEstimationTask,
            CopasiOptSolverCallbacks optSolverCallbacks,
            ClientTaskStatusSupport clientTaskStatusSupport,
            OptimizationResourceApi optApi,
            Consumer<Runnable> progressDispatcher) {

        try {
            // Convert parameter estimation task to OptProblem (vcell-core types)
            OptProblem optProblemCore = CopasiUtils.paramTaskToOptProblem(parameterEstimationTask);

            // Convert to generated client model type for the API call
            ObjectMapper objectMapper = new ObjectMapper();
            String optProblemJson = objectMapper.writeValueAsString(optProblemCore);
            org.vcell.restclient.model.OptProblem optProblem = objectMapper.readValue(
                    optProblemJson, org.vcell.restclient.model.OptProblem.class);

            if (clientTaskStatusSupport != null) {
                clientTaskStatusSupport.setMessage("Submitting opt problem...");
            }

            // Submit optimization job
            OptimizationJobStatus submitResult = optApi.submitOptimization(optProblem);
            Long jobId = Long.parseLong(submitResult.getId());
            lg.info("submitted optimization jobID={}", jobId);

            // Poll for status and results
            final long TIMEOUT_MS = 1000 * 200; // 200 second timeout
            long startTime = System.currentTimeMillis();
            if (clientTaskStatusSupport != null) {
                clientTaskStatusSupport.setMessage("Waiting for progress...");
            }

            Vcellopt optRun = null;
            OptProgressReport latestProgressReport = null;

            while ((System.currentTimeMillis() - startTime) < TIMEOUT_MS) {
                // Check for user stop request
                if (optSolverCallbacks.getStopRequested()) {
                    lg.info("user cancelled optimization jobID={}", jobId);
                    try {
                        optApi.stopOptimization(jobId);
                        lg.info("requested job to be stopped jobID={}", jobId);
                    } catch (Exception e) {
                        lg.error(e.getMessage(), e);
                    } finally {
                        if (latestProgressReport != null) {
                            if (clientTaskStatusSupport != null) {
                                clientTaskStatusSupport.setProgress(100);
                            }
                            return CopasiUtils.getOptimizationResultSet(parameterEstimationTask, latestProgressReport);
                        }
                    }
                    throw UserCancelException.CANCEL_GENERIC;
                }

                // Poll status
                OptimizationJobStatus status = optApi.getOptimizationStatus(jobId);
                if (optSolverCallbacks.getStopRequested()) {
                    throw UserCancelException.CANCEL_GENERIC;
                }

                switch (status.getStatus()) {
                    case SUBMITTED:
                    case QUEUED:
                        if (clientTaskStatusSupport != null) {
                            clientTaskStatusSupport.setMessage("Queued...");
                        }
                        break;

                    case RUNNING:
                        if (status.getProgressReport() != null) {
                            String progressJson = objectMapper.writeValueAsString(status.getProgressReport());
                            latestProgressReport = objectMapper.readValue(progressJson, OptProgressReport.class);
                            final OptProgressReport progressReport = latestProgressReport;
                            progressDispatcher.accept(() -> {
                                try {
                                    optSolverCallbacks.setProgressReport(progressReport);
                                } catch (Exception e) {
                                    lg.error("error updating progress", e);
                                }
                                if (clientTaskStatusSupport != null) {
                                    clientTaskStatusSupport.setMessage("Running ...");
                                }
                            });
                        } else {
                            if (clientTaskStatusSupport != null) {
                                clientTaskStatusSupport.setMessage("Running (waiting for progress) ...");
                            }
                        }
                        break;

                    case COMPLETE:
                        if (status.getResults() != null) {
                            String resultsJson = objectMapper.writeValueAsString(status.getResults());
                            optRun = objectMapper.readValue(resultsJson, Vcellopt.class);
                            if (optRun.getOptResultSet() != null && optRun.getOptResultSet().getOptProgressReport() != null) {
                                final OptProgressReport finalProgress = optRun.getOptResultSet().getOptProgressReport();
                                progressDispatcher.accept(() -> optSolverCallbacks.setProgressReport(finalProgress));
                            }
                            lg.info("job {}: COMPLETE {}", jobId, optRun.getOptResultSet());
                            if (clientTaskStatusSupport != null) {
                                clientTaskStatusSupport.setProgress(100);
                            }
                        }
                        break;

                    case FAILED:
                        String failMsg = "optimization failed, message=" + status.getStatusMessage();
                        lg.error(failMsg);
                        throw new RuntimeException(failMsg);

                    case STOPPED:
                        if (latestProgressReport != null) {
                            if (clientTaskStatusSupport != null) {
                                clientTaskStatusSupport.setProgress(100);
                            }
                            return CopasiUtils.getOptimizationResultSet(parameterEstimationTask, latestProgressReport);
                        }
                        throw UserCancelException.CANCEL_GENERIC;
                }

                if (optRun != null) {
                    break; // COMPLETE
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) { /* ignore */ }
            }

            if ((System.currentTimeMillis() - startTime) >= TIMEOUT_MS) {
                lg.warn("optimization timed out.");
                throw new RuntimeException("optimization timed out.");
            }

            OptResultSet optResultSet = optRun.getOptResultSet();
            if (optResultSet == null) {
                throw new RuntimeException("optResultSet is null, status is " + optRun.getStatusMessage());
            }
            if (optResultSet.getOptParameterValues() == null) {
                throw new RuntimeException("getOptParameterValues is null, status is " + optRun.getStatusMessage());
            }
            if (clientTaskStatusSupport != null) {
                clientTaskStatusSupport.setMessage("Done, getting results...");
            }

            OptimizationResultSet copasiOptimizationResultSet = CopasiUtils.optRunToOptimizationResultSet(
                    parameterEstimationTask,
                    optRun.getOptResultSet(),
                    new OptimizationStatus(OptimizationStatus.NORMAL_TERMINATION, optRun.getStatusMessage()));
            lg.info("done with optimization");
            return copasiOptimizationResultSet;

        } catch (UserCancelException e) {
            throw e;
        } catch (Exception e) {
            lg.error(e.getMessage(), e);
            throw new OptimizationException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
        }
    }
}
