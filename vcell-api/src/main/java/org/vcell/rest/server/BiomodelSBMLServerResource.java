package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.Request;
import org.restlet.data.Form;
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
import org.vcell.rest.common.BiomodelSBMLResource;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class BiomodelSBMLServerResource extends AbstractServerResource implements BiomodelSBMLResource {

	private String biomodelid;
	private String appName;
	
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
        appName = getRequest().getOriginalRef().getQueryAsForm(true).getFirstValue("appname");
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
	@Get(BiomodelSBMLResource.APPLICATION_SBML_XML)
	public StringRepresentation get_xml() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
        String vcml = getBiomodelSBML(vcellUser);
        
        if (vcml != null){
        	String bioModelID = (String)getRequestAttributes().get(VCellApiApplication.BIOMODELID);
        	setAttribute("Content-Disposition", "attachment; filename=\"VCBioModel_"+bioModelID+".vcml\"");
        	return new StringRepresentation(vcml, BiomodelSBMLResource.VCDOC_MEDIATYPE);
        }
        throw new RuntimeException("biomodel not found");
	}

	
	private String getBiomodelSBML(User vcellUser) {
		RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
		try {
			//Make temporary resource compatible with restDatabaseService so we can re-use
			BiomodelVCMLServerResource bmsr = new BiomodelVCMLServerResource() {
				@Override
				public Map<String, Object> getRequestAttributes() {
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put(VCellApiApplication.BIOMODELID, BiomodelSBMLServerResource.this.biomodelid);
					return hashMap;
				}
				@Override
				public Request getRequest() {
					// TODO Auto-generated method stub
					return BiomodelSBMLServerResource.this.getRequest();
				}
			};
			String biomodelVCML = restDatabaseService.query(bmsr,vcellUser);
			BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(biomodelVCML));
			//public SBMLExporter(BioModel argBioModel, int argSbmlLevel, int argSbmlVersion, boolean isSpatial) {
			SimulationContext simulationContext = null;
			if(appName != null) {
				simulationContext = bioModel.getSimulationContext(appName);
			}else {
				simulationContext = bioModel.getSimulationContext(0);
			}
			return XmlHelper.exportSBML(simulationContext.getBioModel(), 3, 1, 0, (simulationContext.getGeometryContext().getGeometry().getDimension()>0), simulationContext, null);			
		} catch (PermissionException e) {
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "permission denied to requested resource");
		} catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}


}
