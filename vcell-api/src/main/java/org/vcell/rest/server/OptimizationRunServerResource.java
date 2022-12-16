package org.vcell.rest.server;

import cbit.vcell.resource.PropertyLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.vcell.optimization.OptMessage;
import org.vcell.optimization.jtd.OptProblem;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.OptimizationRunResource;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class OptimizationRunServerResource extends AbstractServerResource implements OptimizationRunResource {

	private final static Logger lg = LogManager.getLogger(OptimizationRunServerResource.class);

	private static class OptSocketStreams{
		public Socket optSocket;
		public ObjectInputStream ois;
		public ObjectOutputStream oos;
		public String optID;
		public OptSocketStreams(Socket optSocket, ObjectInputStream ois, ObjectOutputStream oos) {
			super();
			this.optSocket = optSocket;
			this.ois = ois;
			this.oos = oos;
		}
		public void closeAll(String optID) {
			paramOptActiveSockets.remove(optID);
			try{ois.close();}catch(Exception e2) { lg.error(e2); }
			try{oos.close();}catch(Exception e2) { lg.error(e2); }
			try{optSocket.close();}catch(Exception e2) { lg.error(e2); }
		}
		public OptMessage.OptResponseMessage sendCommand(OptMessage.OptCommandMessage optCommandMessage) throws IOException,ClassNotFoundException{
			lg.info("sending command "+optCommandMessage+" with ID="+optCommandMessage.optID);
	        oos.writeObject(optCommandMessage);
			lg.info("reading response for command "+optCommandMessage+" with ID="+optCommandMessage.optID);
			try {
				OptMessage.OptResponseMessage response = (OptMessage.OptResponseMessage) ois.readObject();
				lg.info("responded with "+response+" with ID="+response.optID);
				return response;
			} catch (EOFException | SocketException e){
				lg.error(e);
				throw e;
			}
		}
		public static OptSocketStreams create(String ipnum) throws UnknownHostException, IOException {
	        Socket optSocket = new Socket(ipnum, 8877);
			lg.info("Client connected");
	        ObjectOutputStream os = new ObjectOutputStream(optSocket.getOutputStream());
	        ObjectInputStream objIS = new ObjectInputStream(optSocket.getInputStream());
	        lg.info("got streams");
			return new OptSocketStreams(optSocket, objIS, os);
		}
	}

	private static Hashtable<String, OptSocketStreams> paramOptActiveSockets = new Hashtable<>();
    private static final int MAX_ENTRIES = 10;
    private static LinkedHashMap<String, JsonRepresentation> paramOptResults = new LinkedHashMap<String, JsonRepresentation>() {
		@Override
		protected boolean removeEldestEntry(Entry<String, JsonRepresentation> eldest) {
			return size() > MAX_ENTRIES;
		}
    };

	private String submitOptProblem(Representation optProblemJsonRep,ServerResource serverResource) throws ResourceException {
		synchronized (paramOptActiveSockets) {
			if (paramOptActiveSockets.size() >= 20) {
				String[] keys = paramOptActiveSockets.keySet().toArray(new String[0]);
				for (int i = 0; i < keys.length; i++) {
					OptSocketStreams optSocketStreams = paramOptActiveSockets.get(keys[i]);
					String optID = keys[i];
					try {
						// simple status query - so that we can close the connection and cache the results.
						OptMessage.OptResponseMessage response = optSocketStreams.sendCommand(new OptMessage.OptJobQueryCommandMessage(optID));
						if (response instanceof OptMessage.OptErrorResponseMessage) {
							OptMessage.OptErrorResponseMessage errorResponse = (OptMessage.OptErrorResponseMessage) response;
							throw new RuntimeException("Failed to query optimization ID=" + optID + ": " + errorResponse.errorMessage);
						} else if (response instanceof OptMessage.OptJobStatusResponseMessage) {
							OptMessage.OptJobStatusResponseMessage statusResponse = (OptMessage.OptJobStatusResponseMessage) response;
							if (statusResponse.status == OptMessage.OptJobMessageStatus.FAILED) {
								throw new RuntimeException("job for optimization ID=" + optID + " failed: " + statusResponse.statusMessage);
							}
						} else if (response instanceof OptMessage.OptJobSolutionResponseMessage) {
							OptMessage.OptJobSolutionResponseMessage solutionResponse = (OptMessage.OptJobSolutionResponseMessage) response;
							paramOptResults.put(optID, new JsonRepresentation(solutionResponse.optRunJsonString));
							break;
						}
					} catch (Exception e) {//ioError socket  closed
						lg.error(e);
						optSocketStreams.closeAll(optSocketStreams.optID);
					}
				}
				if (paramOptActiveSockets.size() >= 20) {
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Too many active optimization jobs, try again later");
				}
			}
		}
		//		VCellApiApplication application = ((VCellApiApplication)getApplication());
		//		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		if (optProblemJsonRep!=null && optProblemJsonRep.getMediaType().isCompatible(MediaType.APPLICATION_JSON)){
			try {
				JsonRepresentation jsonRep = new JsonRepresentation(optProblemJsonRep);
				JSONObject json = jsonRep.getJsonObject();
				ObjectMapper objectMapper = new ObjectMapper();

				// round trip validation
				OptProblem optProblem = objectMapper.readValue(json.toString(),OptProblem.class);
				String optProblemJsonString = objectMapper.writeValueAsString(optProblem);

				// create new socket resources (remove on failure)
				OptSocketStreams optSocketStreams = createOptSocketStreams();

				OptMessage.OptJobRunCommandMessage runCommand = new OptMessage.OptJobRunCommandMessage(optProblemJsonString);
				OptMessage.OptResponseMessage response = optSocketStreams.sendCommand(runCommand);
				if (response instanceof OptMessage.OptErrorResponseMessage){
					OptMessage.OptErrorResponseMessage errorResponse = (OptMessage.OptErrorResponseMessage) response;
					String errMsg = "opt job run command failed: " + errorResponse.errorMessage;
					lg.error(errMsg);
					optSocketStreams.closeAll(optSocketStreams.optID);
					throw new RuntimeException(errMsg);
				} else if (response instanceof OptMessage.OptJobRunResponseMessage){
					OptMessage.OptJobRunResponseMessage runResponse = (OptMessage.OptJobRunResponseMessage) response;
					optSocketStreams.optID = runResponse.optID;
					lg.info("optimizationJobID="+optSocketStreams.optID+" created socket connect to submit");
					synchronized (paramOptActiveSockets) {
						paramOptActiveSockets.put(optSocketStreams.optID, optSocketStreams);
					}
					return optSocketStreams.optID;
				} else {
					throw new RuntimeException("unexpected response "+response+" from opt job submission");
				}
			} catch (Exception e) {
				lg.error(e);
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,e.getMessage(), e);
			}
		}else{
			throw new RuntimeException("unexpected post representation "+optProblemJsonRep);
		}
	}

	private OptSocketStreams createOptSocketStreams() throws IOException, InterruptedException {
		//			Server:		127.0.0.11
//			justamq_api.1.16o695tgthpt@dockerbuild    | Address:	127.0.0.11#53
//			justamq_api.1.16o695tgthpt@dockerbuild    |
//			justamq_api.1.16o695tgthpt@dockerbuild    | Non-authoritative answer:
//			justamq_api.1.16o695tgthpt@dockerbuild    | Name:	tasks.justamq_submit
//			justamq_api.1.16o695tgthpt@dockerbuild    | Address: 10.0.7.14

		//VCELL_SITE defined manually during deploy
		//stackname = {"vcell"} + {$VCELL_SITE (from swarm *.config)}
		//see vcell/docker/swarm/README.md "CLIENT and SERVER deploy commands" and "To create deploy configuration file"
		//tasks.{taskName}, taskName comes from combining stackname + {taskname defined by docker}
		//Container gets vcell.server.id from vcell:docker:swarm:deploy.sh and *.config variable VCELL_SITE
		//see vcell/docker/swarm/deploy.sh -> echo "env \$(cat $remote_config_file | xargs) docker stack deploy -c $remote_compose_file $stack_name"
		//lookup swarm ip number for task

		//
		// use optional vcell.submit.service.host property to connect to vcell-submit service (e.g. localhost during dev)
		//
		String swarmSubmitTaskName = PropertyLoader.getProperty(PropertyLoader.vcellsubmit_service_host, null);
		if (swarmSubmitTaskName == null){
			// if not provided, then calculate the DNS name of the docker swarm service for vcell-submit
			swarmSubmitTaskName = "tasks."+"vcell"+System.getProperty("vcell.server.id").toLowerCase()+"_submit";
		}
		ProcessBuilder pb =new ProcessBuilder("nslookup",swarmSubmitTaskName);
		pb.redirectErrorStream(true);
		Process process = pb.start();
		java.io.InputStream is = process.getInputStream();
		java.io.InputStreamReader isr = new java.io.InputStreamReader(is);
		java.io.BufferedReader br = new java.io.BufferedReader(isr);
		String line;
		String ipnum = null;
		boolean bFound = false;
		while ((line = br.readLine()) != null) {
			if(line.contains(swarmSubmitTaskName)) {
				bFound = true;
			}else if (bFound && line.trim().startsWith("Address:")) {
				ipnum = line.trim().substring("Address:".length()).trim();
				break;
			}
		}
		br.close();
		int errCode = process.waitFor();
		lg.debug("nslookup errcode="+errCode);

		OptSocketStreams optSocketStreams = OptSocketStreams.create(ipnum);
		return optSocketStreams;
	}

	private JsonRepresentation queryOptJobStatus(String optID, ServerResource serverResource) throws ResourceException {
		synchronized (paramOptActiveSockets) {
			if (paramOptResults.containsKey(optID)) {//return cached results, socket already closed
				return paramOptResults.remove(optID);
			}
			boolean bStop = Boolean.parseBoolean(serverResource.getQueryValue("bStop"));
			OptSocketStreams optSocketStreams = paramOptActiveSockets.get(optID);
			if(optSocketStreams != null) {
				try {
					if (bStop){
						OptMessage.OptResponseMessage response = optSocketStreams.sendCommand(new OptMessage.OptJobStopCommandMessage(optID));
						if (response instanceof OptMessage.OptErrorResponseMessage){
							OptMessage.OptErrorResponseMessage errorResponse = (OptMessage.OptErrorResponseMessage) response;
							throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Failed to stop optimization ID="+optID+": "+errorResponse.errorMessage);
						}else{
							return new JsonRepresentation("stop requested for optimization ID="+optID);
						}
					} else {
						// simple status query
						OptMessage.OptResponseMessage response = optSocketStreams.sendCommand(new OptMessage.OptJobQueryCommandMessage(optID));
						if (response instanceof OptMessage.OptErrorResponseMessage){
							OptMessage.OptErrorResponseMessage errorResponse = (OptMessage.OptErrorResponseMessage) response;
							throw new RuntimeException("status request failed for optID="+optID+": "+errorResponse.errorMessage);
						}else if (response instanceof OptMessage.OptJobStatusResponseMessage){
							OptMessage.OptJobStatusResponseMessage statusResponse = (OptMessage.OptJobStatusResponseMessage) response;
							return new JsonRepresentation(statusResponse.status.name()+":");
						}else if (response instanceof OptMessage.OptJobSolutionResponseMessage){
							OptMessage.OptJobSolutionResponseMessage solutionResponse = (OptMessage.OptJobSolutionResponseMessage) response;
							return new JsonRepresentation(solutionResponse.optRunJsonString);
						}else{
							throw new RuntimeException("unexpected response "+response+" from opt job query request - optID="+optID);
						}
					}
				} catch (Exception e) {
					lg.error(e);
					optSocketStreams.closeAll(optID);
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage() + " optimization ID=" + optID, e);
				}
			}else {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Can't find connection for optimization ID="+optID);
			}
		}
	}

	@Override
	@Post
	public Representation run(Representation optProblemJson) {
		String optID = submitOptProblem(optProblemJson,this);
		getResponse().setStatus(Status.SUCCESS_OK);
		Representation representation = new StringRepresentation(optID,MediaType.TEXT_PLAIN);
		return representation;
	}

	@Override
	public JsonRepresentation get_json() {
		String optID = (String)getRequestAttributes().get(VCellApiApplication.OPTIMIZATIONID);
		JsonRepresentation jsonRepresentation = queryOptJobStatus(optID,this);
		getResponse().setStatus(Status.SUCCESS_OK);
		return jsonRepresentation;
	}
}
