package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JCheckBox;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.SimulationTaskRepresentation;
import org.vcell.rest.common.SimulationTasksResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.console.JobTableModel;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationExecutionStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SimulationQueueID;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationIdentifier;

public class SimulationTasksServerResource extends WadlServerResource implements SimulationTasksResource {

    @Override
    protected void describe(ApplicationInfo applicationInfo) {
        RepresentationInfo rep = new RepresentationInfo(MediaType.TEXT_PLAIN);
        rep.setIdentifier("account");
        applicationInfo.getRepresentations().add(rep);

        DocumentationInfo doc = new DocumentationInfo();
        doc.setTitle("SimulationTask");
        doc.setTextContent("Simple string containing the simulation task ID");
        rep.getDocumentations().add(doc);
    }

    @Override
    protected RepresentationInfo describe(MethodInfo methodInfo,
            Class<?> representationClass, Variant variant) {
        RepresentationInfo result = new RepresentationInfo(variant);
        result.setReference("simulationTask");
        return result;
    }

    @Override
    protected void doInit() throws ResourceException {
        setName("Mail accounts resource");
        setDescription("The resource containing the list of mail accounts");
    }

    @Override
    public SimulationTaskRepresentation[] retrieve() {
        return getSimulationTaskRepresentations();
    }
    
    private List<SimpleJobStatus> query(AdminDBTopLevel adminDbTop, Long maxNumRows, Long simid, String computeHost, String serverID, String userID, String statusFlags, Long submitLow, Long submitHigh, Long startLow, Long startHigh, Long endLow, Long endHigh) throws SQLException, DataAccessException {	
    	
    	ArrayList<String> conditions = new ArrayList<String>();
    	
    	if (simid!=null){
   			conditions.add(SimulationJobTable.table.simRef.getQualifiedColName() + "=" + simid);
     	}

    	if (computeHost != null && computeHost.length()>0){
     		conditions.add("lower(" + SimulationJobTable.table.computeHost.getQualifiedColName() + ")='" + computeHost.toLowerCase() + "'");
    	}

    	if (serverID!=null && serverID.length()>0){
    		conditions.add("lower(" + SimulationJobTable.table.serverID.getQualifiedColName() + ")='" + serverID + "'");
    	}
    		
    	if (userID!=null && userID.length()>0){
    		conditions.add(UserTable.table.userid.getQualifiedColName() + "='" + userID + "'");
    	}

/**
 * 		w = WAITING(0,"waiting"),
 *		q = QUEUED(1,"queued"),
 *		d = DISPATCHED(2,"dispatched"),
 *		r = RUNNING(3,"running"),
 *		c = COMPLETED(4,"completed"),
 *		s = STOPPED(5,"stopped"),
 *		f = FAILED(6,"failed");
 *
 */
    	if (statusFlags!=null){
	    	StringBuffer status = new StringBuffer();
	    	for (SimulationJobStatus.SchedulerStatus schedStatus : SimulationJobStatus.SchedulerStatus.values()){
	    		if (statusFlags.toLowerCase().contains(schedStatus.name().toLowerCase().substring(0, 1))){
					if (status.length() > 0) {
						status.append(" OR ");
					}					
					status.append(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + schedStatus.getDatabaseNumber());
	    		}
	    	}	
	    	if (status.length() > 0) {
	     		conditions.add("(" + status + ")");
	    	}
    	}
    	
    	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
    	
    	if (submitLow != null){
    		conditions.add("(" + SimulationJobTable.table.submitDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(submitLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (submitHigh != null){
    		conditions.add("(" + SimulationJobTable.table.submitDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(submitHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (startLow != null){
    		conditions.add("(" + SimulationJobTable.table.startDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(startLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (startHigh != null){
    		conditions.add("(" + SimulationJobTable.table.startDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(startHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (endLow != null){
    		conditions.add("(" + SimulationJobTable.table.endDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(endLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (endHigh != null){
    		conditions.add("(" + SimulationJobTable.table.endDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(endHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}

    	conditions.add("(" + "rownum" + " <= " + maxNumRows + ")");
    	
    	StringBuffer conditionsBuffer = new StringBuffer();
    	for (String condition : conditions) {
    		if (conditionsBuffer.length() > 0) {
    			conditionsBuffer.append(" AND ");
    		}
			conditionsBuffer.append(condition);
		}
    	
   		List<SimpleJobStatus> resultList = adminDbTop.getSimulationJobStatus(conditionsBuffer.toString(), true);
   		return resultList;
    }


    
	private SimulationTaskRepresentation[] getSimulationTaskRepresentations() {
		Long maxNumRows = new Long(100);
		String userID = getAttribute("user");
		Long simid = (getQueryValue("simid")!=null) ? Long.parseLong(getQueryValue("simid")) : null;
		String computeHost = getQueryValue("computeHost");
		String serverID = getQueryValue("serverID");
		String statusFlags = getQueryValue("statusFlags");
		Long submitLow = (getQueryValue("submitLow")!=null) ? Long.parseLong(getQueryValue("submitLow")) : null;
		Long submitHigh = (getQueryValue("submitHigh")!=null) ? Long.parseLong(getQueryValue("submitHigh")) : null;
		Long startLow = (getQueryValue("startLow")!=null) ? Long.parseLong(getQueryValue("startLow")) : null;
		Long startHigh = (getQueryValue("startHigh")!=null) ? Long.parseLong(getQueryValue("startHigh")) : null;
		Long endLow = (getQueryValue("endLow")!=null) ? Long.parseLong(getQueryValue("endLow")) : null;
		Long endHigh = (getQueryValue("endHigh")!=null) ? Long.parseLong(getQueryValue("endHigh")) : null;
		ArrayList<SimulationTaskRepresentation> simTaskReps = new ArrayList<SimulationTaskRepresentation>();
		AdminDBTopLevel adminDbTopLevel = ((VCellApiApplication)getApplication()).adminDBTopLevel;
		if (adminDbTopLevel!=null){
			try {
				List<SimpleJobStatus> simJobStatusList = query(adminDbTopLevel, maxNumRows, simid, computeHost, serverID, userID, statusFlags, submitLow, submitHigh, startLow, startHigh, endLow, endHigh);
				for (SimpleJobStatus simpleJobStatus : simJobStatusList) {
					SimulationTaskRepresentation simTaskRep = new SimulationTaskRepresentation(simpleJobStatus);
					simTaskReps.add(simTaskRep);
				}
			} catch (DataAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve active jobs from VCell Database : "+e.getMessage());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve active jobs from VCell Database : "+e.getMessage());
			}
		}else{
			simTaskReps.add(new SimulationTaskRepresentation(new SimulationJobStatus(VCellServerID.getServerID("JIMS"),
							new VCSimulationIdentifier(new KeyValue("123"),new User("schaff",new KeyValue("222"))), 
							0, // jobIndex
							new Date(), // submission date
							SchedulerStatus.RUNNING,
							0, // taskID
							SimulationMessage.MESSAGE_JOB_RUNNING_UNKNOWN,
							new SimulationQueueEntryStatus(new Date(), 1, SimulationQueueID.QUEUE_ID_SIMULATIONJOB),
							new SimulationExecutionStatus(new Date(), "myhost", new Date(), null, false, null))));
			simTaskReps.add(new SimulationTaskRepresentation(new SimulationJobStatus(VCellServerID.getServerID("JIMS"),
							new VCSimulationIdentifier(new KeyValue("124"),new User("schaff",new KeyValue("222"))), 
							0, // jobIndex
							new Date(), // submission date
							SchedulerStatus.QUEUED,
							0, // taskID
							SimulationMessage.MESSAGE_JOB_RUNNING_UNKNOWN,
							new SimulationQueueEntryStatus(new Date(), 1, SimulationQueueID.QUEUE_ID_SIMULATIONJOB),
							new SimulationExecutionStatus(new Date(), "myhost", new Date(), null, false, null))));
		}
		return simTaskReps.toArray(new SimulationTaskRepresentation[0]);
	}

}
