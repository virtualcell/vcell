package org.vcell.rest.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.vcell.api.client.VCellApiClient;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.optimization.thrift.OptRunStatus;

public class OptimizationApiTest {
	public static final String host = "localhost";
	public static final int port = 8081;
	
	public static void main(String[] args){
		try {
			boolean bIgnoreCertProblems = true;
			boolean bIgnoreHostMismatch = true;
			VCellApiClient apiClient = new VCellApiClient(host, port, "123456", bIgnoreCertProblems, bIgnoreHostMismatch);

			File optProbFile = new File("../pythonScripts/VCell_Opt/optprob.bin");
			System.out.println("using optProblem: "+optProbFile.getAbsolutePath());
			OptProblem optProblem = readOptProblem(optProbFile);
			TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
			String optProblemJson = serializer.toString(optProblem);

			ArrayList<String> jobIDs = new ArrayList<String>();
			jobIDs.add(apiClient.submitOptimization(optProblemJson));
			jobIDs.add(apiClient.submitOptimization(optProblemJson));
			jobIDs.add(apiClient.submitOptimization(optProblemJson));
			jobIDs.add(apiClient.submitOptimization(optProblemJson));
			
			boolean done = false;
			while (!done){
				done = true;
				for (String jobID : jobIDs){
					String optRunJson = apiClient.getOptRunJson(jobID);
					TDeserializer deserializer = new TDeserializer(new TJSONProtocol.Factory());
					OptRun optRun = new OptRun();
					deserializer.deserialize(optRun, optRunJson.getBytes());
					OptRunStatus status = optRun.status;
					if (status!=OptRunStatus.Complete && status!=OptRunStatus.Failed){
						done = false;
					}
					
					if (status==OptRunStatus.Complete){
						System.out.println("job "+jobID+": status "+status+" "+optRun.getOptResultSet().toString());
					}else{
						System.out.println("job "+jobID+": status "+status);
					}
				}
				try {
					Thread.sleep(1000);
				}catch (InterruptedException e){}
			}
			System.out.println("done with all jobs");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static OptProblem readOptProblem(File optProblemFile) throws IOException {
		TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		try {
			OptProblem optProblem = new OptProblem();
			byte[] bytes = FileUtils.readFileToByteArray(optProblemFile);
			deserializer.deserialize(optProblem, bytes);
			return optProblem;
		} catch (TException e) {
			e.printStackTrace();
			throw new IOException("error reading optProblem from file "+optProblemFile.getPath()+": "+e.getMessage(),e);
		}
	}

}
