package org.vcell.rest.health;

import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import com.google.gson.Gson;
import org.vcell.api.types.events.EventWrapper;
import org.vcell.api.types.events.SimulationJobStatusEventRepresentation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.rest.events.RestEventService;
import org.vcell.rest.server.RestDatabaseService;
import org.vcell.util.BigString;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.api.types.utils.DTOModelTransformerV0;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

public class HealthService {
	
	private static final long SIMULATION_TIME_WARNING = 45*1000;
	private static final long SIMULATION_TIMEOUT = 8*60*1000;
	private static final long SIMULATION_LOOP_START_DELAY = 60*1000;
	private static final long SIMULATION_LOOP_SLEEP = 5*60*1000;
	
	Logger lg = LogManager.getLogger(HealthService.class);

	public enum HealthEventType {
		RUNSIM_START,
		RUNSIM_SUBMIT,
		RUNSIM_FAILED,
		RUNSIM_SUCCESS
	}
	
	public static class HealthEvent {
		public final long timestamp_MS;
		public final long transactionId;
		public final HealthEventType eventType;
		public final String message;
		
		public HealthEvent(long transactionId, HealthEventType eventType, String message) {
			this.timestamp_MS = System.currentTimeMillis();
			this.transactionId = transactionId;
			this.eventType = eventType;
			this.message = message;
		}
		
		public String toString() {
			return "HealthEvent("+transactionId+","+timestamp_MS+","+eventType+","+message;
		}
	}
	
	public static class NagiosStatus {
		String nagiosStatusName;
		int nagiosStatusCode;
		String message;
		Long elapsedTime_MS;
		String tags;
		
		// exit with parsed nagios code (0-OK,1-Warning,2-Critical,3-Unknown)
		NagiosStatus(String nagiosStatusName, int nagiosStatusCode, String message, Long elapsedTime_MS, String tags){
			this.nagiosStatusName = nagiosStatusName;
			this.nagiosStatusCode = nagiosStatusCode;
			this.message = message;
			this.elapsedTime_MS = elapsedTime_MS;
			this.tags = tags;
		}

		static NagiosStatus OK(Long elapsedTime_MS) {
			return new NagiosStatus("OK",0,null,elapsedTime_MS, "UP");
		}
		static NagiosStatus Warning(Long elapsedTime_MS) {
			return new NagiosStatus("Warning",1,null,elapsedTime_MS, "UP WARNING");
		}
		static NagiosStatus Critical(Long elapsedTime_MS, String message) {
			return new NagiosStatus("Critical",2,message,elapsedTime_MS, "DOWN");
		}
		static NagiosStatus Unknown() {
			return new NagiosStatus("Unknown",3,null,null, "WARNING");
		}
	}

	final RestEventService eventService;
	final DatabaseServerImpl databaseServer;
	final SimulationDatabaseDirect simulationDatabaseDirect;
	final AtomicLong eventSequence = new AtomicLong(0);
	final ConcurrentLinkedDeque<HealthEvent> healthEvents = new ConcurrentLinkedDeque<>();
	Thread runsimThread;
	final boolean bIgnoreCertProblems;
	final boolean bIgnoreHostMismatch;
	final UserInfo testUserinfo;
	final VCMessagingService vcMessagingService;

	public HealthService(VCMessagingService vcMessagingService, DatabaseServerImpl databaseServer, RestEventService eventService,
						 boolean bIgnoreCertProblems, boolean bIgnoreHostMismatch,
						 UserInfo testUserinfo) {
		this.eventService = eventService;
		this.databaseServer = databaseServer;
		this.simulationDatabaseDirect = new SimulationDatabaseDirect(databaseServer.getAdminDBTopLevel(), databaseServer, false);
		this.bIgnoreCertProblems = bIgnoreCertProblems;
		this.bIgnoreHostMismatch = bIgnoreHostMismatch;
		this.testUserinfo = testUserinfo;
		this.vcMessagingService = vcMessagingService;
	}
			
	private long simStartEvent() {
		long id = eventSequence.getAndIncrement();
		HealthEvent healthEvent = new HealthEvent(id, HealthEventType.RUNSIM_START, "starting simulation loop ("+id+")");
		addHealthEvent(healthEvent);
		return id;
	}
	
	private void simSubmitEvent(long id, VCSimulationIdentifier vcSimId) {
		HealthEvent healthEvent = new HealthEvent(id, HealthEventType.RUNSIM_SUBMIT, "simulation "+vcSimId.getID()+"_0_0"+" submitted ("+id+")");
		addHealthEvent(healthEvent);
	}
	
	private void simFailed(long id, String message) {
		HealthEvent healthEvent = new HealthEvent(id, HealthEventType.RUNSIM_FAILED, "simulation failed ("+id+"): " + message);
		addHealthEvent(healthEvent);
	}
	
	private void simSuccess(long id) {
		HealthEvent healthEvent = new HealthEvent(id, HealthEventType.RUNSIM_SUCCESS, "simulation success ("+id+")");
		addHealthEvent(healthEvent);
	}
	
	private void addHealthEvent(HealthEvent healthEvent) {
		if (lg.isDebugEnabled()) {
			lg.debug(healthEvent.toString());
		}
		healthEvents.addFirst(healthEvent);
	}

	public HealthEvent[] query(long starttimestamp_MS, long endtimestamp_MS) {
		var eventList = new ArrayList<HealthEvent>();
        for (HealthEvent event : healthEvents) {
            if (event.timestamp_MS >= starttimestamp_MS && event.timestamp_MS <= endtimestamp_MS) {
                eventList.add(0, event);
            }
        }
        return eventList.toArray(new HealthEvent[0]);
	}

	public void start() {
		if (runsimThread!=null) {
			throw new RuntimeException("only call start() once.");
		}
		runsimThread = new Thread(() -> runsimLoop(), "runsim monitor thread");
		runsimThread.setDaemon(true);
		runsimThread.setPriority(Thread.currentThread().getPriority()-1);
		runsimThread.start();
	}
	
	private void runsimLoop() {
		try {
			Thread.sleep(SIMULATION_LOOP_START_DELAY);
		} catch (InterruptedException e1) {
		}
		Gson gson = new Gson();
		UserLoginInfo userLoginInfo = new UserLoginInfo(testUserinfo.userid);
		User user = new User(testUserinfo.userid, testUserinfo.id);
		userLoginInfo.setUser(user);
		while (true) {
			long id = simStartEvent();
			KeyValue savedBioModelKey = null;
			VCSimulationIdentifier runningSimId = null;
			try {
				String vcmlString = IOUtils.toString(getClass().getResourceAsStream("/TestTemplate.vcml"));
				
				BioModel templateBioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlString));
				templateBioModel.clearVersion();
				templateBioModel.visitChildVersionables(new BioModel.ClearVersion());
				String newBiomodelName = "test_"+System.currentTimeMillis();
				templateBioModel.setName(newBiomodelName);
				// remove all existing simulations from stored template model, and add new one
				while (templateBioModel.getNumSimulations()>0) {
					templateBioModel.removeSimulation(templateBioModel.getSimulation(0));
				}
				MathMappingCallback callback = new MathMappingCallback() {
					@Override public void setProgressFraction(float fractionDone) { }
					@Override public void setMessage(String message) { }
					@Override public boolean isInterrupted() { return false; }
				};
				templateBioModel.getSimulationContext(0).addNewSimulation("sim", callback, NetworkGenerationRequirements.ComputeFullStandardTimeout);
				
				BigString vcml = new BigString(XmlHelper.bioModelToXML(templateBioModel));
				String[] independentSims = new String[0];
				BigString savedBioModelVCML = databaseServer.saveBioModel(user, vcml, independentSims);
				BioModel savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(savedBioModelVCML.toString()));
				savedBioModelKey = savedBioModel.getVersion().getVersionKey();
				
				Simulation sim = savedBioModel.getSimulation(0);
				VCSimulationIdentifier vcSimId = new VCSimulationIdentifier(sim.getKey(), sim.getVersion().getOwner());
				long eventTimestamp = System.currentTimeMillis();

				// submit simulation via an RPC call
				RestDatabaseService.callStartSimulation(vcMessagingService, user, vcSimId.getSimulationKey(), sim.getSimulationVersion().getOwner(), sim.getJobCount());
				// get initial status from database
				SimulationStatus simStatus = simulationDatabaseDirect.getSimulationStatus(vcSimId.getSimulationKey());

				simSubmitEvent(id, vcSimId);
				runningSimId = vcSimId;
				
				long startTime_MS = System.currentTimeMillis();
				while (simStatus.isActive()) {
					if ((System.currentTimeMillis() - startTime_MS) > SIMULATION_TIMEOUT ) {
						throw new RuntimeException("simulation took longer than "+SIMULATION_TIMEOUT+" to complete");
					}
					Thread.sleep(1000);
					EventWrapper[] eventWrappers = eventService.query(user.getName(), eventTimestamp);
					if (eventWrappers!=null) {
						for (EventWrapper eventWrapper : eventWrappers) {
							if (eventWrapper.eventType == EventWrapper.EventType.SimJob) {
								SimulationJobStatusEventRepresentation simJobStatusEventRep =
										gson.fromJson(eventWrapper.eventJSON, SimulationJobStatusEventRepresentation.class);
								SimulationJobStatusEvent jobEvent = DTOModelTransformerV0.simulationJobStatusEventFromJsonRep(this, simJobStatusEventRep);
								SimulationJobStatus jobStatus = jobEvent.getJobStatus();
								VCSimulationIdentifier eventSimId = jobStatus.getVCSimulationIdentifier();
								if (eventSimId.getOwner().equals(userLoginInfo.getUser()) && eventSimId.getSimulationKey().equals(sim.getKey())) {
									simStatus = SimulationStatus.updateFromJobEvent(simStatus, jobEvent);
								}
							}
						}
					}
				}
				runningSimId = null;

				if (!simStatus.isCompleted()) {
					throw new RuntimeException("failed: "+simStatus.getDetails());
				}

				// before declaring success, retrieve some data (time array is sufficient)
				RestDatabaseService.callGetDataSetTimeSeries(vcMessagingService, userLoginInfo, sim.getKey(), user, 0, new String[] { "t" });

				simSuccess(id);
				
			}catch (Throwable e) {
				
				simFailed(id, e.getMessage());
			
			}finally {
				// cleanup
				try {
					if (runningSimId!=null) {
						try {
							RestDatabaseService.callStopSimulation(vcMessagingService, user, runningSimId.getSimulationKey(), runningSimId.getOwner());
						}catch (Exception e) {
							lg.error(e.getMessage(), e);
						}
					}
					if (savedBioModelKey!=null) {
						databaseServer.deleteBioModel(user, savedBioModelKey);
					}
				}catch (Exception e) {
					lg.error(e.getMessage(), e);
				}
			}
			try {
				Thread.sleep(SIMULATION_LOOP_SLEEP);
			} catch (InterruptedException e) {
				lg.error(e.getMessage(), e);
			}
		}
	}
	
	public NagiosStatus getRunsimStatus(long status_timestamp) {
		// get last 5 minutes of events to determine status
		HealthEvent[] events = query(status_timestamp-(SIMULATION_TIMEOUT+SIMULATION_LOOP_SLEEP+120000), status_timestamp);
		
		// find last completed sim event (if any)
		HealthEvent loginCompleteEvent = null;
		for (HealthEvent event : events) {
			if (event.eventType == HealthEventType.RUNSIM_SUCCESS 
					|| event.eventType == HealthEventType.RUNSIM_FAILED) {
				if (loginCompleteEvent==null || loginCompleteEvent.timestamp_MS < event.timestamp_MS) {
					loginCompleteEvent = event;
				}
			}
		}

		// find corresponding START event to determine elapsed time
		Long elapsedTime_MS = null;
		if (loginCompleteEvent!=null) {
			for (HealthEvent event : events) {
				if (event.eventType == HealthEventType.RUNSIM_START 
						&& event.transactionId == loginCompleteEvent.transactionId) {
					elapsedTime_MS = loginCompleteEvent.timestamp_MS - event.timestamp_MS;
				}
			}
		}
		
		if (loginCompleteEvent==null) {
			return NagiosStatus.Unknown(); // no completed simulation attempts
			
		} else if (loginCompleteEvent.eventType==HealthEventType.RUNSIM_SUCCESS) {
			//
			// OK or WARNING depending on time
			//
			if (elapsedTime_MS==null || elapsedTime_MS > SIMULATION_TIMEOUT) {
				return NagiosStatus.Critical(elapsedTime_MS, "simulation timeout");
			}
			if (elapsedTime_MS < SIMULATION_TIME_WARNING) {
				return NagiosStatus.OK(elapsedTime_MS);
			}
			return NagiosStatus.Warning(elapsedTime_MS);

		} else if (loginCompleteEvent.eventType==HealthEventType.RUNSIM_FAILED) {
			return NagiosStatus.Critical(elapsedTime_MS, loginCompleteEvent.message);
		} else {
			throw new RuntimeException("unexpected HealthEventType");
		}
	}
	
}
