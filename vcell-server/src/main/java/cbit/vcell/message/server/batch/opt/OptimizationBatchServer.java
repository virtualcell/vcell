package cbit.vcell.message.server.batch.opt;

import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.optimization.CopasiUtils;
import org.vcell.optimization.OptMessage;
import org.vcell.optimization.jtd.OptProblem;
import org.vcell.optimization.jtd.OptProgressReport;
import org.vcell.optimization.jtd.Vcellopt;
import org.vcell.util.exe.ExecutableException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;

public class OptimizationBatchServer {

    private final static Logger lg = LogManager.getLogger(OptimizationBatchServer.class);
    private Random random = new Random(System.currentTimeMillis());
    private ServerSocket optimizationServersocket;
    private HtcProxy.HtcProxyFactory htcProxyFactory = null;

    private static class OptServerJobInfo {
        private String optID;
        private HtcProxy.HtcJobInfo htcJobInfo;

        public OptServerJobInfo(String optID, HtcProxy.HtcJobInfo htcJobInfo) {
            super();
            this.optID = optID;
            this.htcJobInfo = htcJobInfo;
        }

        public String getOptID() {
            return optID;
        }

        public HtcProxy.HtcJobInfo getHtcJobInfo() {
            return htcJobInfo;
        }
    }

    public class OptCommunicationThread extends Thread {
        private final Socket optSocket;

        public OptCommunicationThread(Socket optSocket){
            super();
            setName("optCommunicationThread");
            this.optSocket = optSocket;
        }

        @Override
        public void run() {
            OptServerJobInfo optServerJobInfo = null;
            // let the client socket close the connection - keep listening as long as the client is active
            try (ObjectInputStream is = new ObjectInputStream(optSocket.getInputStream());
                 ObjectOutputStream oos = new ObjectOutputStream(optSocket.getOutputStream());
                 Socket myOptSocket = optSocket) {
                while (true) {
                     OptMessage.OptCommandMessage optCommandMessage = null;
                    try {
                        optCommandMessage = (OptMessage.OptCommandMessage) is.readObject();
                        if (optServerJobInfo != null && !optCommandMessage.optID.equals(optServerJobInfo.getOptID())) {
                            String errMsg = "command optID=" + optCommandMessage.optID + " doesn't match socket optID=" + optServerJobInfo.optID;
                            oos.writeObject(new OptMessage.OptErrorResponseMessage(optCommandMessage, errMsg));
                            oos.flush();
                            lg.error(errMsg);
                        } else if (optCommandMessage instanceof OptMessage.OptJobStopCommandMessage) {
                            OptMessage.OptJobStopCommandMessage stopRequest = (OptMessage.OptJobStopCommandMessage) optCommandMessage;
                            optServerStopJob(optServerJobInfo);
                            oos.writeObject(new OptMessage.OptJobStopResponseMessage(stopRequest));
                            oos.flush();
                            lg.info("send stop request to batch system for job optID=" + optCommandMessage.optID);
                        } else if (optCommandMessage instanceof OptMessage.OptJobQueryCommandMessage) {
                            OptMessage.OptJobQueryCommandMessage jobQuery = (OptMessage.OptJobQueryCommandMessage) optCommandMessage;
                            Vcellopt optRun = getOptResults(optServerJobInfo.getOptID());
                            if (optRun != null) {
                                // job is done, return results
                                ObjectMapper objectMapper = new ObjectMapper();
                                String optRunJsonString = objectMapper.writeValueAsString(optRun);
                                oos.writeObject(new OptMessage.OptJobSolutionResponseMessage(jobQuery, optRunJsonString));
                                oos.flush();
                                lg.info("returned solution for job optID=" + optCommandMessage.optID);
                            }
                            // else ask the batch system for the status
                            HtcJobStatus htcJobStatus = optServerGetJobStatus(optServerJobInfo.getHtcJobInfo());
                            if (htcJobStatus == null) {//pending
                                oos.writeObject(new OptMessage.OptJobStatusResponseMessage(jobQuery, OptMessage.OptJobMessageStatus.QUEUED, "queued", null));
                                oos.flush();
                                lg.info("returned status of " + OptMessage.OptJobMessageStatus.QUEUED + " for job optID=" + optCommandMessage.optID);
                            } else {
                                if (htcJobStatus.isFailed()) {
                                    String errorMsg = "slurm job " + optServerJobInfo.getHtcJobInfo().getHtcJobID() + " failed";
                                    oos.writeObject(new OptMessage.OptJobStatusResponseMessage(
                                            jobQuery, OptMessage.OptJobMessageStatus.FAILED, errorMsg,null));
                                    oos.flush();
                                    lg.error(errorMsg);
                                    throw new Exception(errorMsg);
                                } else if (htcJobStatus.isComplete()) { // but file not found yet
                                    String optID = optCommandMessage.optID;
                                    String errMsg = "job optID=" + optID + " status is COMPLETE but result file "+generateOptOutputFilePath(optID)+" not found yet";
                                    String progressReportJsonString = getProgressReportJsonString(optID);
                                    oos.writeObject(new OptMessage.OptJobStatusResponseMessage(
                                            jobQuery, OptMessage.OptJobMessageStatus.RUNNING, errMsg, progressReportJsonString));
                                    oos.flush();
                                    lg.error(errMsg);
                                } else {//running
                                    String optID = optCommandMessage.optID;
                                    String msg = "slurm job " + optServerJobInfo.getHtcJobInfo().getHtcJobID() + " running";
                                    String progressReportJsonString = getProgressReportJsonString(optID);
                                    if (progressReportJsonString == null) {
                                        lg.warn("failed to read progress report for optID="+optID);
                                    }
                                    oos.writeObject(new OptMessage.OptJobStatusResponseMessage(
                                            jobQuery, OptMessage.OptJobMessageStatus.RUNNING, msg, progressReportJsonString));
                                    oos.flush();
                                    lg.info(msg);
                                }
                            }
                        } else if (optCommandMessage instanceof OptMessage.OptJobRunCommandMessage) {
                            OptMessage.OptJobRunCommandMessage runCommand = (OptMessage.OptJobRunCommandMessage) optCommandMessage;
                            lg.info("submitting optimization job");
                            ObjectMapper objectMapper = new ObjectMapper();
                            OptProblem optProblem = objectMapper.readValue(runCommand.optProblemJsonString, OptProblem.class);
                            optServerJobInfo = submitOptProblem(optProblem);
                            String optID = optServerJobInfo.getOptID();
                            oos.writeObject(new OptMessage.OptJobRunResponseMessage(optID, runCommand));
                            oos.flush();
                        } else {
                            throw new Exception("Unexpected command " + optCommandMessage);
                        }
                    } catch (SocketException | EOFException e) {
                        String optID = (optServerJobInfo!=null) ? optServerJobInfo.optID : "null";
                        String errMsg = "Socket exception - shutting down thread for optID=" + optID + ": " + e.getMessage();
                        lg.error(errMsg, e);
                        return;
                    } catch (Exception e) {
                        String optID = (optServerJobInfo!=null) ? optServerJobInfo.optID : "null";
                        String errMsg = "error processing command for optID=" + optID + ": " + e.getMessage();
                        lg.error(errMsg, e);
                        try {
                            oos.writeObject(new OptMessage.OptErrorResponseMessage(optCommandMessage, errMsg));
                            oos.flush();
                        } catch (Exception e2) {
                            lg.error(e2);
                        }
                    }
                }
            } catch (Exception e) {
                lg.error(e.getMessage(), e);
            } finally {
                //cleanup
                try {
                    if (optServerJobInfo != null && optServerJobInfo.getOptID() != null) {
                        File optDir = generateOptimizeDirName(optServerJobInfo.getOptID());
                        if (optDir.exists()) {
//                            generateOptProblemFilePath(optServerJobInfo.getOptID()).delete();
//                            generateOptOutputFilePath(optServerJobInfo.getOptID()).delete();
//                            generateOptInterresultsFilePath(optServerJobInfo.getOptID()).delete();
                            optDir.delete();
                        }
                    }
                }catch (Exception e2){
                    lg.error(e2);
                }
            }
        }
    }

    private String getProgressReportJsonString(String optID) throws IOException {
        String progressReportJsonString = null;
        OptProgressReport progressReport = getProgressReport(optID);
        lg.info(CopasiUtils.progressReportString(progressReport));
        if (progressReport != null){
            ObjectMapper objectMapper = new ObjectMapper();
            progressReportJsonString = objectMapper.writeValueAsString(progressReport);
        }
        return progressReportJsonString;
    }

    public OptimizationBatchServer(HtcProxy.HtcProxyFactory htcProxyFactory){
        this.htcProxyFactory = htcProxyFactory;
    }

    private HtcProxy getHtcProxy() {
        return htcProxyFactory.getHtcProxy();
    }

    public void initOptimizationSocket() {
        Thread optThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    optimizationServersocket = new ServerSocket(8877);
                    while (true) {
                        Socket optSocket = optimizationServersocket.accept();
                        OptCommunicationThread optCommunicationThread = new OptCommunicationThread(optSocket);
                        optCommunicationThread.setDaemon(true);
                        optCommunicationThread.start();
                    }
                } catch (Exception e) {
                    lg.error(e.getMessage(), e);
                }
            }
        });
        optThread.setDaemon(true);
        optThread.start();
    }

    private Vcellopt getOptResults(String optID) throws IOException {
        File f = generateOptOutputFilePath(optID);
        if (f.exists()) {// opt job done
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(f,Vcellopt.class);
        }
        return null;
    }

    private OptProgressReport getProgressReport(String optID) throws IOException {
        File f = generateOptReportFilePath(optID);
        if (f.exists()) { // opt has report (may still be open for reading)
            return CopasiUtils.readProgressReportFromCSV(f);
        }else {
            return null;
        }
    }

    private OptServerJobInfo submitOptProblem(OptProblem optProblem) throws IOException, ExecutableException {
        HtcProxy htcProxyClone = getHtcProxy().cloneThreadsafe();
        File htcLogDirExternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal));
        File htcLogDirInternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirInternal));
        int optID = random.nextInt(1000000);
        String optSubFileName = generateOptFilePrefix(optID+"")+".sub";
        File sub_file_external = new File(htcLogDirExternal, optSubFileName);
        File sub_file_internal = new File(htcLogDirInternal, optSubFileName);
        File optProblemFile = generateOptProblemFilePath(optID+"");
        File optOutputFile = generateOptOutputFilePath(optID+"");
        File optReportFile = generateOptReportFilePath(optID+"");
        CopasiUtils.writeOptProblem(optProblemFile, optProblem);//save param optimization problem to user dir
        //make sure all can read and write
        File optDir = generateOptimizeDirName(optID+"");
        optDir.setReadable(true,false);
        optDir.setWritable(true,false);

        String slurmOptJobName = generateOptFilePrefix(optID+"");
        HtcJobID htcJobID = htcProxyClone.submitOptimizationJob(slurmOptJobName, sub_file_internal, sub_file_external,optProblemFile,optOutputFile,optReportFile);
        return new OptServerJobInfo(optID+"", new HtcProxy.HtcJobInfo(htcJobID, slurmOptJobName));
    }
    private void optServerStopJob(OptServerJobInfo optServerJobInfo) {
        try {
            HtcProxy htcProxyClone = getHtcProxy().cloneThreadsafe();
            htcProxyClone.killJobSafe(optServerJobInfo.getHtcJobInfo());
//		CommandOutput commandOutput = htcProxyClone.getCommandService().command(new String[] {"scancel",optServerJobInfo.htcJobID.getJobNumber()+""});
//		return commandOutput.getExitStatus()==0;
        } catch (Exception e) {
            lg.error(e.getMessage(), e);
        }
    }
    private HtcJobStatus optServerGetJobStatus(HtcProxy.HtcJobInfo htcJobInfo) {
        HtcProxy htcProxyClone = getHtcProxy().cloneThreadsafe();
        try {
            return htcProxyClone.getJobStatus(Arrays.asList(new HtcProxy.HtcJobInfo[] {htcJobInfo})).get(htcJobInfo);
        } catch (Exception e) {
            lg.error(e.getMessage(), e);
            return null;
        }
    }

//    private static boolean hackFileExists(File watchThisFile) {
//        try {
//            //Force container bind mount to update file status
//            ProcessBuilder pb = new ProcessBuilder("sh","-c","ls "+watchThisFile.getAbsolutePath()+"*");
//            pb.redirectErrorStream(true);
//            Process p = pb.start();
//            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
////		StringBuffer sb = new StringBuffer();
//            String line = null;
//            while((line = br.readLine()) != null) {
//                //sb.append(line+"\n");
//                System.out.println("'"+line+"'");
//                if(line.trim().startsWith("ls: ")) {
////				System.out.println("false");
//                    break;
//                }else if(line.trim().equals(watchThisFile.getAbsolutePath())) {
////				System.out.println("true");
//                    return true;
//                }
//            }
//            p.waitFor(10, TimeUnit.SECONDS);
//            br.close();
////		System.out.println("false");
//        } catch (Exception e) {
//            lg.error(e.getMessage(), e);
//        }
//        return false;
//    }

    private File generateOptimizeDirName(String optID) {
        File primaryUserDirInternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty));
        File optProblemDir = new File(primaryUserDirInternal,"parest_data");
        optProblemDir.mkdir();
        return optProblemDir;
    }
    private File generateOptOutputFilePath(String optID) {
        String optOutputFileName = generateOptFilePrefix(optID)+"_optRun.json";
        return new File(generateOptimizeDirName(optID), optOutputFileName);
    }
    private File generateOptReportFilePath(String optID) {
        String optOutputFileName = generateOptFilePrefix(optID)+"_optReport.txt";
        return new File(generateOptimizeDirName(optID), optOutputFileName);
    }
    private File generateOptProblemFilePath(String optID) {
        String optOutputFileName = generateOptFilePrefix(optID)+"_optProblem.json";
        return new File(generateOptimizeDirName(optID), optOutputFileName);
    }
//    private File generateOptInterresultsFilePath(String optID) {
//        return new File(generateOptimizeDirName(optID), "interresults.txt");
//    }
    private String generateOptFilePrefix(String optID) {
        return "CopasiParest_"+optID;
    }
}