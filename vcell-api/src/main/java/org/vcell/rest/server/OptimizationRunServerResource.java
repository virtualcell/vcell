package org.vcell.rest.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.lang.model.type.NullType;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRunStatus;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.OptimizationRunResource;

public class OptimizationRunServerResource extends AbstractServerResource implements OptimizationRunResource {

	private String biomodelid;
	
	
    @Override
    protected RepresentationInfo describe(MethodInfo methodInfo,
            Class<?> representationClass, Variant variant) {
        RepresentationInfo result = new RepresentationInfo(variant);
        result.setReference("biomodel");
        return result;
    }

    /**
     * Retrieve the account identifier based on the URI path variable
     * "accountId" declared in the URI template attached to the application
     * router.
     */
    @Override
    protected void doInit() throws ResourceException {
        String simTaskIdAttribute = getAttribute(VCellApiApplication.BIOMODELID);

        if (simTaskIdAttribute != null) {
            this.biomodelid = simTaskIdAttribute;
            setName("Resource for biomodel \"" + this.biomodelid + "\"");
            setDescription("The resource for saving a modified version of the simulation task id \"" + this.biomodelid + "\"");
        } else {
            setName("simulation task resource");
            setDescription("The resource describing a simulation task");
        }
    }
	

	@Override
	protected void describePost(MethodInfo info) {
		super.describePost(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo(VCellApiApplication.BIOMODELID,false,"string",ParameterStyle.TEMPLATE,"VCell biomodel id"));
        parameterInfos.add(new ParameterInfo(VCellApiApplication.SIMULATIONID,false,"string",ParameterStyle.TEMPLATE,"VCell simulation id"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}

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
			try{ois.close();}catch(Exception e2) {e2.printStackTrace();}
			try{oos.close();}catch(Exception e2) {e2.printStackTrace();}
			try{optSocket.close();}catch(Exception e2) {e2.printStackTrace();}
		}
		public Object writeObject(Object obj,boolean bStop) throws IOException,ClassNotFoundException{
	        oos.writeObject(obj);
	        oos.writeObject(new Boolean(bStop));
	        return ois.readObject();
		}
		public static OptSocketStreams create(String ipnum) throws UnknownHostException, IOException {
	        Socket optSocket = new Socket(ipnum, 8877);
	        System.out.println("Client connected...");
	        ObjectOutputStream os = new ObjectOutputStream(optSocket.getOutputStream());
	        ObjectInputStream objIS = new ObjectInputStream(optSocket.getInputStream());
	        System.out.println("got streams...");
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

	private Object doOP(Object input,ServerResource serverResource) throws ResourceException {
		synchronized (paramOptActiveSockets) {
		if(input instanceof String) {
			String optID = (String)input;
			if(paramOptResults.containsKey(optID)) {//return cached results, socket already closed
				return paramOptResults.remove(optID);
			}
			boolean bStop = Boolean.parseBoolean(serverResource.getQueryValue("bStop"));
			OptSocketStreams optSocketStreams = paramOptActiveSockets.get(optID);
			if(optSocketStreams != null) {
				try {
					Object mesg = optSocketStreams.writeObject(optID, bStop);
					if(mesg instanceof Boolean) {//return status of stop command
						optSocketStreams.closeAll(optID);
						return new JsonRepresentation(mesg.toString());
					}else if(mesg instanceof String) {//return status of optID query
						return new JsonRepresentation(mesg.toString());
					}else if(mesg instanceof byte[]){//return of optResults when done
						optSocketStreams.closeAll(optID);
						String base64String = java.util.Base64.getEncoder().encodeToString((byte[])mesg);
						return new JsonRepresentation(new StringRepresentation(base64String));
					}
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Unexpected return message "+mesg.getClass().getName()+" for optimization ID="+optID);
				} catch (Exception e) {
					e.printStackTrace();
					optSocketStreams.closeAll(optID);
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage()+" optimization ID="+optID);
				}
			}else {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Can't find connection for optimization ID="+optID);
			}

		}else if(input instanceof Representation) {
			Representation optProblemJson = (Representation)input;
			if(paramOptActiveSockets.size() >= 5){
				String[] keys = paramOptActiveSockets.keySet().toArray(new String[0]);
				for (int i = 0; i < keys.length; i++) {
					OptSocketStreams optSocketStreams = paramOptActiveSockets.get(keys[i]);
					try {
						Object obj = optSocketStreams.writeObject("checkIfDone", false);//Check connection
						if(obj instanceof Boolean && ((Boolean)obj)) {//pending
//							optSocketStreams.closeAll(optSocketStreams.optID);
						}else if(obj instanceof byte[]) {
							optSocketStreams.closeAll(optSocketStreams.optID);
							String base64String = java.util.Base64.getEncoder().encodeToString((byte[])obj);
							paramOptResults.put(optSocketStreams.optID, new JsonRepresentation(new StringRepresentation(base64String)));
							break;
						}else if(obj instanceof String) {
							if(obj.toString().startsWith(OptRunStatus.Failed.name())) {
								throw new Exception(obj.toString());
							}
						}
					} catch (Exception e) {//ioError socket  closed
						e.printStackTrace();
						optSocketStreams.closeAll(optSocketStreams.optID);
					}
				}
				if(paramOptActiveSockets.size() >= 5){
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Too many active optimization jobs, try again later");
				}
			}
	//		VCellApiApplication application = ((VCellApiApplication)getApplication());
	//		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
			if (optProblemJson!=null && optProblemJson.getMediaType().isCompatible(MediaType.APPLICATION_JSON)){
				try {
					JsonRepresentation jsonRep = new JsonRepresentation(optProblemJson);
					JSONObject json = jsonRep.getJsonObject();
					TDeserializer deserializer = new TDeserializer(new TJSONProtocol.Factory());
					OptProblem optProblem = new OptProblem();
					deserializer.deserialize(optProblem, json.toString().getBytes());
					
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
					String swarmSubmitTaskName = "tasks."+"vcell"+System.getProperty("vcell.server.id")+"_submit";
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
//			  System.out.println(line);
						if(line.contains(swarmSubmitTaskName)) {
							bFound = true;
						}else if (bFound && line.trim().startsWith("Address:")) {
							ipnum = line.trim().substring("Address:".length()).trim();
							break;
						}
					}
					br.close();
					int errCode = process.waitFor();
					System.out.println("----------nslookup errcode="+errCode);
					
					OptSocketStreams optSocketStreams = OptSocketStreams.create(ipnum);
					optSocketStreams.optID = (String) optSocketStreams.writeObject(optProblem,false);
					System.out.println("----------optimizationJobID="+optSocketStreams.optID+" created socket connect to submit");
					paramOptActiveSockets.put(optSocketStreams.optID, optSocketStreams);
					return optSocketStreams;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL,e.getMessage());
				}
			}else{
				throw new RuntimeException("unexpected post representation "+optProblemJson);
			}
		}
		throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Unexpected operation type="+input.getClass().getName());
		}
	}
	
	@Override
	@Post
	public Representation run(Representation optProblemJson) {
		OptSocketStreams optSocketStreams = (OptSocketStreams)doOP(optProblemJson,this);
		getResponse().setStatus(Status.SUCCESS_OK);
		Representation representation = new StringRepresentation(optSocketStreams.optID,MediaType.TEXT_PLAIN);
		return representation;
	}

	@Override
	public JsonRepresentation get_json() {
		String optID = (String)getRequestAttributes().get(VCellApiApplication.OPTIMIZATIONID);
		JsonRepresentation jsonRepresentation = (JsonRepresentation)doOP(optID,this);
		getResponse().setStatus(Status.SUCCESS_OK);
		return jsonRepresentation;
	}
}
