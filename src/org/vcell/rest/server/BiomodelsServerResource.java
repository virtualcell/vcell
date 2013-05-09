package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.BiomodelRepresentation;
import org.vcell.rest.common.BiomodelsResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.User;

import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.modeldb.BioModelTable;
import cbit.vcell.modeldb.DatabaseServerImpl;

import com.google.gson.Gson;

import freemarker.template.Configuration;

public class BiomodelsServerResource extends AbstractServerResource implements BiomodelsResource {

    private static final String PARAM_USER = "user";
	private static final String PARAM_SIM_ID = "simId";
	private static final String PARAM_JOB_ID = "jobId";
	private static final String PARAM_TASK_ID = "taskId";
	private static final String PARAM_SAVED_HIGH = "savedHigh";
	private static final String PARAM_SAVED_LOW = "savedLow";
	private static final String PARAM_MAX_ROWS = "maxRows";

	@Override
	protected void doInit() throws ResourceException {
		setName("SimulationTask resource");
		setDescription("The resource containing the list of Biomodels");
	}
	
	@Override
    protected void describe(ApplicationInfo applicationInfo) {
        RepresentationInfo rep = new RepresentationInfo(MediaType.APPLICATION_JSON);
        rep.setIdentifier(VCellApiApplication.BIOMODEL);
        applicationInfo.getRepresentations().add(rep);

        DocumentationInfo doc = new DocumentationInfo();
        doc.setTitle(VCellApiApplication.BIOMODEL);
        doc.setTextContent("jdom containing list of biomodels");
        rep.getDocumentations().add(doc);
    }

	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo(PARAM_USER,false,"string",ParameterStyle.TEMPLATE,"VCell user id"));
        parameterInfos.add(new ParameterInfo(PARAM_SIM_ID,false,"string",ParameterStyle.QUERY,"VCell simulation database id"));
        parameterInfos.add(new ParameterInfo(PARAM_JOB_ID,false,"string",ParameterStyle.QUERY,"VCell simulation job id (parameter scan index)"));
        parameterInfos.add(new ParameterInfo(PARAM_TASK_ID,false,"string",ParameterStyle.QUERY,"VCell simulation task id (retry index)"));
        parameterInfos.add(new ParameterInfo(PARAM_SAVED_LOW,false,"string",ParameterStyle.QUERY,"earliest saved timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_SAVED_HIGH,false,"string",ParameterStyle.QUERY,"latest saved timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_MAX_ROWS,false,"string",ParameterStyle.QUERY,"max number of records returned (default is 100)"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}
	
	@Override
    public BiomodelRepresentation[] get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		org.restlet.security.User autheticatedUser = getClientInfo().getUser();
		User vcellUser = application.getVCellUser(autheticatedUser);
		
        return getBiomodelRepresentations(vcellUser);
    }
    
	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		org.restlet.security.User autheticatedUser = getClientInfo().getUser();
		User vcellUser = application.getVCellUser(autheticatedUser);
		
		BiomodelRepresentation[] biomodels = getBiomodelRepresentations(vcellUser);
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("userId", getAttribute(PARAM_USER));
		dataModel.put("simId", getLongQueryValue(PARAM_SIM_ID));
		dataModel.put("jobId",  getLongQueryValue(PARAM_JOB_ID));
		dataModel.put("taskId",  getLongQueryValue(PARAM_TASK_ID));
		dataModel.put("savedLow", getLongQueryValue(PARAM_SAVED_LOW));
		dataModel.put("savedHigh", getLongQueryValue(PARAM_SAVED_HIGH));
		Long maxRowsParam = getLongQueryValue(PARAM_MAX_ROWS);
		if (maxRowsParam!=null){
			dataModel.put("maxRows", maxRowsParam);
		}else{
			dataModel.put("maxRows", 100);
		}

		dataModel.put("biomodels", Arrays.asList(biomodels));
		
		
		if (vcellUser!=null){
			dataModel.put("userid",vcellUser.getName());
		}
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(biomodels));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/biomodels.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}


	private BiomodelRepresentation[] getBiomodelRepresentations(User vcellUser) {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			ArrayList<BiomodelRepresentation> biomodelReps = new ArrayList<BiomodelRepresentation>();
			DatabaseServerImpl databaseServerImpl = ((VCellApiApplication)getApplication()).getDatabaseServerImpl();
			try {
				BioModelInfo[] bioModelInfoList = query(databaseServerImpl, vcellUser);
				for (BioModelInfo bioModelInfo : bioModelInfoList) {
					BiomodelRepresentation biomodelRep = new BiomodelRepresentation(bioModelInfo);
					biomodelReps.add(biomodelRep);
				}
			} catch (DataAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
			}
			return biomodelReps.toArray(new BiomodelRepresentation[0]);
//		}
	}

    private BioModelInfo[] query(DatabaseServerImpl databaseServerImpl, User vcellUser) throws SQLException, DataAccessException {	
    	
		Long simid = getLongQueryValue(PARAM_SIM_ID);
		Long jobid = getLongQueryValue(PARAM_JOB_ID);
		Long taskid = getLongQueryValue(PARAM_TASK_ID);
		Long savedLow = getLongQueryValue(PARAM_SAVED_LOW);
		Long savedHigh = getLongQueryValue(PARAM_SAVED_HIGH);
		Long maxRowsParam = getLongQueryValue(PARAM_MAX_ROWS);
		int maxRows = 100; // default
		if (maxRowsParam!=null){
			maxRows = maxRowsParam.intValue();
		}
    	ArrayList<String> conditions = new ArrayList<String>();
    	
    	if (simid!=null){
   			conditions.add(SimulationJobTable.table.simRef.getQualifiedColName() + "=" + simid);
     	}

    	if (jobid!=null){
   			conditions.add(SimulationJobTable.table.jobIndex.getQualifiedColName() + "=" + jobid);
     	}

    	if (taskid!=null){
   			conditions.add(SimulationJobTable.table.taskID.getQualifiedColName() + "=" + taskid);
     	}

     	
    	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
    	
    	if (savedLow != null){
    		conditions.add("(" + BioModelTable.table.versionDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(savedLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (savedHigh != null){
    		conditions.add("(" + BioModelTable.table.versionDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(savedHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
 
    	StringBuffer conditionsBuffer = new StringBuffer();
    	for (String condition : conditions) {
    		if (conditionsBuffer.length() > 0) {
    			conditionsBuffer.append(" AND ");
    		}
			conditionsBuffer.append(condition);
		}
    	
//    	if (statusConditions.size()==0){
//    		// no status conditions wanted ... nothing to query
//    		return new BioModelInfo[0];
//    	}else{
	   		BioModelInfo[] bioModelInfos = databaseServerImpl.getBioModelInfos(vcellUser, false);
	   		return bioModelInfos;
//    	}
    }
 }
