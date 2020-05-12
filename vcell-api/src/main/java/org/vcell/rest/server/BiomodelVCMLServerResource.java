package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.common.BiomodelVCMLResource;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class BiomodelVCMLServerResource extends AbstractServerResource implements BiomodelVCMLResource {

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
	@Get(BiomodelVCMLResource.APPLICATION_VCML_XML)
	public StringRepresentation get_xml() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
        String vcml = getBiomodelVCML(vcellUser);
        
        if (vcml != null){
        	String bioModelID = (String)getRequestAttributes().get(VCellApiApplication.BIOMODELID);
//        	setAttribute("Content-type", "application/vcml+xml");
        	setAttribute("Content-Disposition", "attachment; filename=\"VCBioModel_"+bioModelID+".vcml\"");
        	return new StringRepresentation(vcml, BiomodelVCMLResource.VCDOC_MEDIATYPE);
        }
        throw new RuntimeException("biomodel not found");
	}

	
	private String getBiomodelVCML(User vcellUser) {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
			try {
				String cachedVcml = restDatabaseService.query(this,vcellUser);
				BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(cachedVcml));
				String latestVcml = XmlHelper.bioModelToXML(bioModel);
				return latestVcml;
			} catch (PermissionException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "permission denied to requested resource");
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "biomodel not found");
			} catch (Exception e){
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			}
//		}
	}


}
