package org.vcell.rest.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.ext.wadl.*;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.common.BiomodelOMEXResource;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import java.util.ArrayList;
import java.util.List;

public class BiomodelOMEXServerResource extends AbstractServerResource implements BiomodelOMEXResource {
	private final static Logger lg = LogManager.getLogger(BiomodelOMEXServerResource.class);

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
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo("biomodelid",false,"string",ParameterStyle.TEMPLATE,"VCell biomodel id"));
		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}

	@Override
	@Get(BiomodelOMEXResource.APPLICATION_OMEX_ZIP)
	public ByteArrayRepresentation get_omex() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		boolean bSkipUnsupported = false;
        ByteArrayRepresentation omexRep = getOmex(vcellUser, bSkipUnsupported);
        
        if (omexRep != null){
			String bioModelID = (String)getRequestAttributes().get(VCellApiApplication.BIOMODELID);
//        	setAttribute("Content-type", "application/vcml+xml");
			setAttribute("Content-Disposition", "attachment; filename=\"biomodel_"+bioModelID+".omex\"");
			return omexRep;
        }
        throw new RuntimeException("biomodel not found");
	}


	private ByteArrayRepresentation getOmex(User vcellUser, boolean bSkipUnsupported) {
		RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
		try {
			ByteArrayRepresentation omexRep = restDatabaseService.query(this, vcellUser, bSkipUnsupported);
			return omexRep;
		} catch (PermissionException e) {
			lg.error(e);
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "permission denied to requested resource");
		} catch (ObjectNotFoundException e) {
			lg.error(e);
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "biomodel not found");
		} catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.toString());
		}
	}


}
