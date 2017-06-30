package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.common.BiomodelSimulationStopResource;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import cbit.vcell.modeldb.SimulationRep;

public class BiomodelSimulationStopServerResource extends AbstractServerResource implements BiomodelSimulationStopResource {

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
            setDescription("The resource describing the simulation task id \"" + this.biomodelid + "\"");
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

	@Override
	public Representation stop() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		RestDatabaseService restDatabaseService = application.getRestDatabaseService();
		try {
			SimulationRep simRep = restDatabaseService.stopSimulation(this, vcellUser);
			Representation representation = new StringRepresentation("simulation stopped",MediaType.TEXT_PLAIN);
			redirectSeeOther("/"+VCellApiApplication.SIMTASK+
					"?"+SimulationTasksServerResource.PARAM_SIM_ID+"="+simRep.getKey().toString()+
					"&"+SimulationTasksServerResource.PARAM_STATUS_COMPLETED+"=on"+
					"&"+SimulationTasksServerResource.PARAM_STATUS_DISPATCHED+"=on"+
					"&"+SimulationTasksServerResource.PARAM_STATUS_FAILED+"=on"+
					"&"+SimulationTasksServerResource.PARAM_STATUS_QUEUED+"=on"+
					"&"+SimulationTasksServerResource.PARAM_STATUS_RUNNING+"=on"+
					"&"+SimulationTasksServerResource.PARAM_STATUS_STOPPED+"=on"+
					"&"+SimulationTasksServerResource.PARAM_STATUS_WAITING+"=on"+
					"&"+SimulationTasksServerResource.PARAM_START_ROW+"=1"+
					"&"+SimulationTasksServerResource.PARAM_MAX_ROWS+"="+Integer.toString(simRep.getScanCount()*4));
			return representation;
		} catch (PermissionException e) {
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "not authorized to stop simulation");
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "simulation not found");
		} catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}


}
