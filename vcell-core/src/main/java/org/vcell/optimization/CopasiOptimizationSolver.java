/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationStatus;
import cbit.vcell.resource.PropertyLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.api.client.VCellApiClient;
import org.vcell.optimization.jtd.*;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.UserLoginInfo;

import javax.swing.*;


public class CopasiOptimizationSolver {

	private final static Logger lg = LogManager.getLogger(CopasiOptimizationSolver.class);
	
	public static OptimizationResultSet solveLocalPython(ParameterEstimationTask parameterEstimationTask)
			throws OptimizationException {
		
		try {
			parameterEstimationTask.refreshMappings();
			OptProblem optProblem = CopasiUtils.paramTaskToOptProblem(parameterEstimationTask);
			Vcellopt optRun = CopasiUtils.runCopasiParameterEstimation(optProblem);
			OptimizationResultSet copasiOptimizationResultSet = CopasiUtils.toOptResults(
					optRun,parameterEstimationTask, new ParameterEstimationTaskSimulatorIDA());
			return copasiOptimizationResultSet;
		} catch (Exception e){
			throw new OptimizationException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
		}
	}

	private static final String STOP_REQUESTED = "stop requested";
	public static OptimizationResultSet solveRemoteApi(
			ParameterEstimationTask parameterEstimationTask,
			CopasiOptSolverCallbacks optSolverCallbacks,
			ClientTaskStatusSupport clientTaskStatusSupport,
			ClientServerInfo clientServerInfo) {

		// return solveLocalPython(parameterEstimationTask);

		try {
			boolean bIgnoreCertProblems = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems, false);
			boolean bIgnoreHostMismatch = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreHostMismatch, false);

			String host = clientServerInfo.getApihost();
			int port = clientServerInfo.getApiport();
			String pathPrefixV0 = clientServerInfo.getPathPrefix_v0();
			// e.g. vcell.serverhost=vcellapi.cam.uchc.edu:443
			VCellApiClient apiClient = new VCellApiClient(host, port, pathPrefixV0, bIgnoreCertProblems, bIgnoreHostMismatch);
			apiClient.authenticate(bIgnoreCertProblems);

			OptProblem optProblem = CopasiUtils.paramTaskToOptProblem(parameterEstimationTask);

			ObjectMapper objectMapper = new ObjectMapper();
			String optProblemJson = objectMapper.writeValueAsString(optProblem);

			if (clientTaskStatusSupport != null) {
				clientTaskStatusSupport.setMessage("Submitting opt problem...");
			}
			//Submit but allow user to get out from restlet blocking call
			final String[] optIdHolder = new String[]{null};
			final Exception[] exceptHolder = new Exception[]{null};
			Thread submitThread = new Thread(() -> {
				try {
					optIdHolder[0] = apiClient.submitOptimization(optProblemJson);
					lg.info("submitted optimization jobID="+optIdHolder[0]);
					if (optSolverCallbacks.getStopRequested()) {
						apiClient.getOptRunJson(optIdHolder[0], optSolverCallbacks.getStopRequested());
						lg.info("user cancelled optimization jobID="+optIdHolder[0]);
					}
				} catch (Exception e) {
					lg.error(e.getMessage(), e);
					exceptHolder[0] = e;
				}
			});
			submitThread.setDaemon(true);
			submitThread.start();

			//
			// wait here until either failure to submit or submitted and retrieved Job ID
			//
			while (optIdHolder[0] == null && exceptHolder[0] == null && !optSolverCallbacks.getStopRequested()) {
				try {
					Thread.sleep(200);
				}catch (InterruptedException e){}
			}

			//
			// failed to submit, throw the exception now
			//
			if (exceptHolder[0] != null) {
				throw exceptHolder[0];
			}


			//
			// loop to query status and collect results
			//
			final long TIMEOUT_MS = 1000 * 200; // 200 second timeout
			long startTime = System.currentTimeMillis();
			if (clientTaskStatusSupport != null) {
				clientTaskStatusSupport.setMessage("Waiting for progress...");
			}
			Vcellopt optRun = null;
			OptProgressReport latestProgressReport = null;
			while ((System.currentTimeMillis() - startTime) < TIMEOUT_MS) {
				//
				// check for user stop request
				//
				boolean bStopRequested = optSolverCallbacks.getStopRequested();
				if (bStopRequested) {
					lg.info("user cancelled optimization jobID="+optIdHolder[0]);
					try {
						apiClient.getOptRunJson(optIdHolder[0], bStopRequested);
						lg.info("requested job to be stopped jobID="+optIdHolder[0]);
					}catch (Exception e){
						lg.error(e.getMessage(), e);
					}finally{
						if (latestProgressReport!=null){
							if (clientTaskStatusSupport != null) {
								clientTaskStatusSupport.setProgress(100);
							}
							OptimizationResultSet copasiOptimizationResultSet = CopasiUtils.getOptimizationResultSet(parameterEstimationTask, latestProgressReport);
							return copasiOptimizationResultSet;
						}
					}
					throw UserCancelException.CANCEL_GENERIC;
				}

				String optRunServerMessage = apiClient.getOptRunJson(optIdHolder[0], false);
				if (optSolverCallbacks.getStopRequested()) {
					throw UserCancelException.CANCEL_GENERIC;
				}
				if (optRunServerMessage.startsWith(VcelloptStatus.QUEUED.name() + ":")) {
					if (clientTaskStatusSupport != null) {
						clientTaskStatusSupport.setMessage("Queued...");
					}

				} else if (optRunServerMessage.startsWith(VcelloptStatus.FAILED.name()+":") || optRunServerMessage.toLowerCase().startsWith("exception:")){
					if (clientTaskStatusSupport != null) {
						clientTaskStatusSupport.setMessage(optRunServerMessage);
					}

				} else if (optRunServerMessage.startsWith(VcelloptStatus.RUNNING.name() + ":")) {
					if (clientTaskStatusSupport != null) {
						clientTaskStatusSupport.setMessage("Running (waiting for progress) ...");
					}

				} else {
					// consider that optRunServerMessage is either a progress report (OptProgressReport) or a final solution (Vcellopt)
					Object optObject = null;
					try {
						optObject = objectMapper.readValue(optRunServerMessage, Vcellopt.class);
					}catch (Exception e){
						optObject = objectMapper.readValue(optRunServerMessage, OptProgressReport.class);
					}

					if (optObject instanceof Vcellopt) {
						//
						// have final solution with progress report and analytics
						//
						optRun = (Vcellopt) optObject;
						final OptProgressReport optProgressReport = optRun.getOptResultSet().getOptProgressReport();
						VcelloptStatus status = optRun.getStatus();
						if (optProgressReport != null){
							SwingUtilities.invokeLater(() -> optSolverCallbacks.setProgressReport(optProgressReport));
						}
						if (status == VcelloptStatus.COMPLETE) {
							lg.info("job " + optIdHolder[0] + ": status " + status + " " + optRun.getOptResultSet().toString());
							if (clientTaskStatusSupport != null) {
								clientTaskStatusSupport.setProgress(100);
							}
							break;
						}
						if (status == VcelloptStatus.FAILED) {
							String msg = "optimization failed, message=" + optRun.getStatusMessage();
							lg.error(msg);
							throw new RuntimeException(msg);
						}
						lg.info("job " + optIdHolder[0] + ": status " + status);
					}else if (optObject instanceof OptProgressReport){
						//
						// have intermediate progress report
						//
						latestProgressReport = (OptProgressReport) optObject;
						final OptProgressReport progressReport = latestProgressReport;
						SwingUtilities.invokeLater(() -> {
							try {
								optSolverCallbacks.setProgressReport(progressReport);
							} catch (Exception e) {
								lg.error(optRunServerMessage, e);
							}
							if (clientTaskStatusSupport != null) {
								int numFunctionEvaluations = 0;
								if (progressReport.getProgressItems()!=null && progressReport.getProgressItems().size()>0){
									OptProgressItem lastItem = progressReport.getProgressItems().get(progressReport.getProgressItems().size()-1);
									numFunctionEvaluations = lastItem.getNumFunctionEvaluations();
								}
								clientTaskStatusSupport.setMessage("Running ...");
							}
						});
					}
				}
				try {
					Thread.sleep(2000);
				}catch (InterruptedException e){}
			}
			if((System.currentTimeMillis()-startTime) >= TIMEOUT_MS) {
				lg.warn("optimization timed out.");
				throw new RuntimeException("optimization timed out.");
			}
			OptResultSet optResultSet = optRun.getOptResultSet();
			if(optResultSet == null) {
				String msg = "optResultSet is null, status is " + optRun.getStatusMessage();
				lg.error(msg);
				throw new RuntimeException(msg);
			}
			if(optResultSet != null && optResultSet.getOptParameterValues() == null) {
				String msg = "getOptParameterValues is null, status is " + optRun.getStatusMessage();
				lg.error(msg);
				throw new RuntimeException(msg);
			}
			if(clientTaskStatusSupport != null) {
				clientTaskStatusSupport.setMessage("Done, getting results...");
			}

			OptimizationResultSet copasiOptimizationResultSet = CopasiUtils.optRunToOptimizationResultSet(
					parameterEstimationTask,
					optRun.getOptResultSet(),
					new OptimizationStatus(OptimizationStatus.NORMAL_TERMINATION, optRun.getStatusMessage()));
			lg.info("done with optimization");
			if (lg.isTraceEnabled()) {
				lg.trace("-----------SOLUTION FROM VCellAPI---------------\n" + optResultSet.toString());
			}

			return copasiOptimizationResultSet;
		}catch(UserCancelException e) {
			throw e;
		} catch (Exception e) {
			lg.error(e.getMessage(), e);
			throw new OptimizationException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
		}
	}
		
}
