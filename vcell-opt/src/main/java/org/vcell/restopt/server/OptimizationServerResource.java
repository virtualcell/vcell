package org.vcell.restopt.server;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.resource.ResourceException;
import org.vcell.optimization.CopasiServicePython;
import org.vcell.optimization.OptServerImpl.OptRunContext;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.restopt.VCellOptApplication;
import org.vcell.restopt.common.OptimizationResource;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;

public class OptimizationServerResource extends WadlServerResource implements OptimizationResource {

	@Override
	public JsonRepresentation get_json() {
        OptRun optRun = getOptRun();
        
        try {
			TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
			String optRunJson = new String(serializer.serialize(optRun));
			JsonRepresentation optRunJsonRep = new JsonRepresentation(optRunJson);
			return optRunJsonRep;
		} catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}

	
	private OptRun getOptRun() {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			try {
				String optimizationId = (String)getRequestAttributes().get(VCellOptApplication.OPTIMIZATIONID);
				VCellOptApplication application = ((VCellOptApplication)getApplication());
				OptRunContext optRunContext = application.getOptServerImpl().getOptRunContextByOptimizationId(optimizationId);
				if (optRunContext == null){
					throw new ObjectNotFoundException("optimization id '"+optimizationId+"' not found");
				}
				switch (optRunContext.getStatus()){
					case Complete:{
						OptRun optRun = CopasiServicePython.readOptRun(optRunContext.getOptRunBinaryFile());
						encodeStatusParams(optimizationId, optRunContext, optRun);
						return optRun;
					}
					case Queued:
					case Running:
					case Failed:{
						OptProblem optProblem = CopasiServicePython.readOptProblem(optRunContext.getOptProblemBinaryFile());
						OptRun optRun = new OptRun();
						optRun.setOptProblem(optProblem);
						optRun.setStatus(optRunContext.getStatus());
						encodeStatusParams(optimizationId, optRunContext, optRun);
						return optRun;
					}
					default:{
						throw new RuntimeException("unexpected optimization status '"+optRunContext.getStatus()+"'");
					}
				}
			} catch (PermissionException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "permission denied to requested resource");
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "optimization not found");
			} catch (Exception e){
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			}
//		}
	}


	private void encodeStatusParams(String optimizationId, OptRunContext optRunContext, OptRun optRun)
			throws IOException {
		java.io.File f = new java.io.File("/root/.vcell/optimization/"+optimizationId+"/interresults.txt");
		String statusMessage = optRunContext.getStatus().name();
		if(f.exists()) {
			List<String> progressLines = Files.readAllLines(f.toPath());
			if(progressLines.size()>1) {
				StringTokenizer st = new StringTokenizer(progressLines.get(progressLines.size()-1), " \t\r\n");
				if(st.countTokens() == 3) {
					statusMessage = statusMessage+":"+st.nextToken()+":"+st.nextToken()+":"+st.nextToken();
				}
			}
		}
		optRun.setStatusMessage(statusMessage);
	}


}
