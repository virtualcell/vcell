package org.vcell.rest.health;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.vcell.util.BigString;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCInfoContainer;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class HealthService {
	
	private static final long LOGIN_TIME_WARNING = 10*1000;
	private static final long LOGIN_TIME_ERROR = 30*1000;
	private static final long SIMULATION_TIME_WARNING = 15*1000;
	private static final long SIMULATION_TIMEOUT = 60*1000;
	private static final long LOGIN_LOOP_SLEEP = 60*1000;
	private static final long RUNSIM_LOOP_SLEEP = 5*60*1000;

	public static enum HealthEventType {
		LOGIN_START,
		LOGIN_FAILED,
		LOGIN_SUCCESS,
		RUNSIM_START,
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
	}
	
	public static class NagiosStatus {
		String nagiosStatusName;
		int nagiosStatusCode;
		String message;
		Long elapsedTime_MS;
		
		// exit with parsed nagios code (0-OK,1-Warning,2-Critical,3-Unknown)
		NagiosStatus(String nagiosStatusName, int nagiosStatusCode, String message, Long elapsedTime_MS){
			this.nagiosStatusName = nagiosStatusName;
			this.nagiosStatusCode = nagiosStatusCode;
			this.message = message;
			this.elapsedTime_MS = elapsedTime_MS;
		}

		static NagiosStatus OK(Long elapsedTime_MS) {
			return new NagiosStatus("OK",0,null,elapsedTime_MS);
		}
		static NagiosStatus Warning(Long elapsedTime_MS) {
			return new NagiosStatus("Warning",1,null,elapsedTime_MS);
		}
		static NagiosStatus Critical(Long elapsedTime_MS, String message) {
			return new NagiosStatus("Critical",2,message,elapsedTime_MS);
		}
		static NagiosStatus Unknown(Long elapsedTime_MS, String message) {
			return new NagiosStatus("Unknown",3,null,elapsedTime_MS);
		}
	}

	final AtomicLong eventSequence = new AtomicLong(0);
	final ConcurrentLinkedDeque<HealthEvent> healthEvents = new ConcurrentLinkedDeque<>();
	Thread loginThread;
	Thread runsimThread;
	final String host;
	final int port;
	final boolean bIgnoreCertProblems;
	final boolean bIgnoreHostMismatch;
	final String testUserid;
	final DigestedPassword testPassword;

	public HealthService(String host, int port, boolean bIgnoreCertProblems, boolean bIgnoreHostMismatch, String testUserid, DigestedPassword testPassword) {
		this.host = host;
		this.port = port;
		this.bIgnoreCertProblems = bIgnoreCertProblems;
		this.bIgnoreHostMismatch = bIgnoreHostMismatch;
		this.testUserid = testUserid;
		this.testPassword = testPassword;
	}
			
	private long simStartEvent() {
		long id = eventSequence.getAndIncrement();
		healthEvents.addFirst(new HealthEvent(id, HealthEventType.RUNSIM_START, "starting simulation ("+id+")"));
		return id;
	}
	
	private void simFailed(long id, String message) {
		healthEvents.addFirst(new HealthEvent(id, HealthEventType.RUNSIM_FAILED, "simulation failed ("+id+"): " + message));
	}
	
	private void simSuccess(long id) {
		healthEvents.addFirst(new HealthEvent(id, HealthEventType.RUNSIM_SUCCESS, "simulation success ("+id+")"));
	}
	
	private long loginStartEvent() {
		long id = eventSequence.getAndIncrement();
		healthEvents.addFirst(new HealthEvent(id, HealthEventType.LOGIN_START, "starting login ("+id+")"));
		return id;
	}
	
	private void loginFailed(long id, String message) {
		healthEvents.addFirst(new HealthEvent(id, HealthEventType.LOGIN_FAILED, "login failed ("+id+"): " + message));
	}
	
	private void loginSuccess(long id) {
		healthEvents.addFirst(new HealthEvent(id, HealthEventType.LOGIN_SUCCESS, "simulation success ("+id+")"));
	}
	
	
	public HealthEvent[] query(long starttimestamp_MS, long endtimestamp_MS) {
		ArrayList<HealthEvent> eventList = new ArrayList<HealthEvent>();
		Iterator<HealthEvent> iter = healthEvents.iterator();
		while (iter.hasNext()) {
			HealthEvent event = iter.next();
			if (event.timestamp_MS >= starttimestamp_MS && event.timestamp_MS <= endtimestamp_MS) {
				eventList.add(0, event);
			}
		}
		HealthEvent[] eventArray = eventList.toArray(new HealthEvent[0]);
		return eventArray;
	}

	public void start() {
		if (loginThread!=null || runsimThread!=null) {
			throw new RuntimeException("only call start() once.");
		}
		loginThread = new Thread(() -> loginLoop(), "login monitor thread");
		loginThread.setDaemon(true);
		loginThread.setPriority(Thread.currentThread().getPriority()-1);
		loginThread.start();
		
		runsimThread = new Thread(() -> runsimLoop(), "runsim monitor thread");
		runsimThread.setDaemon(true);
		runsimThread.setPriority(Thread.currentThread().getPriority()-1);
		runsimThread.start();
	}
	
	private void loginLoop() {
		try {
			Thread.sleep(15*1000);
		} catch (InterruptedException e1) {
		}
		while (true) {
			long id = loginStartEvent();
			try {
				UserLoginInfo userLoginInfo = new UserLoginInfo(testUserid, testPassword);
				RemoteProxyVCellConnectionFactory vcellConnectionFactory = new RemoteProxyVCellConnectionFactory(host, port, userLoginInfo);
				VCellConnection vcellConnection = vcellConnectionFactory.createVCellConnection();
				VCInfoContainer vcInfoContainer = vcellConnection.getUserMetaDbServer().getVCInfoContainer();
				loginSuccess(id);
			}catch (Throwable e) {
				loginFailed(id, e.getMessage());
			}
			try {
				Thread.sleep(LOGIN_LOOP_SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void runsimLoop() {
		try {
			Thread.sleep(15*1000);
		} catch (InterruptedException e1) {
		}
		UserLoginInfo userLoginInfo = new UserLoginInfo(testUserid, testPassword);
		while (true) {
			long id = simStartEvent();
			KeyValue savedBioModelKey = null;
			VCSimulationIdentifier runningSimId = null;
			try {
				RemoteProxyVCellConnectionFactory vcellConnectionFactory = new RemoteProxyVCellConnectionFactory(host, port, userLoginInfo);
				VCellConnection vcellConnection = vcellConnectionFactory.createVCellConnection();
				
				String vcmlString = IOUtils.toString(getClass().getResourceAsStream("/TestTemplate.vcml"));
				BioModel templateBioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlString));
				BigString vcml = new BigString(XmlHelper.bioModelToXML(templateBioModel));
				String[] independentSims = new String[0];
				String newName = "test_"+System.currentTimeMillis();
				BigString savedBioModelVCML = vcellConnection.getUserMetaDbServer().saveBioModelAs(vcml, newName, independentSims);
				BioModel savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(savedBioModelVCML.toString()));
				savedBioModelKey = savedBioModel.getVersion().getVersionKey();
				
				Simulation sim = savedBioModel.getSimulation(0);
				VCSimulationIdentifier vcSimId = new VCSimulationIdentifier(sim.getKey(), sim.getVersion().getOwner());
				SimulationStatus initialSimStatus = vcellConnection.getSimulationController().startSimulation(vcSimId, 1);
				runningSimId = vcSimId;
				
				long startTime_MS = System.currentTimeMillis();
				SimulationStatus simStatus = vcellConnection.getSimulationController().getSimulationStatus(sim.getKey());
				while (simStatus.isActive()) {
					if ((System.currentTimeMillis() - startTime_MS) > SIMULATION_TIMEOUT ) {
						throw new RuntimeException("simulation took longer than "+SIMULATION_TIMEOUT+" to complete");
					}
					Thread.sleep(1000);
					simStatus = vcellConnection.getSimulationController().getSimulationStatus(sim.getKey());
				}
				runningSimId = null;
				
				if (!simStatus.isCompleted()) {
					throw new RuntimeException("failed: "+simStatus.getDetails());
				}
				
				simSuccess(id);
				
			}catch (Throwable e) {
				
				simFailed(id, e.getMessage());
			
			}finally {
				// cleanup
				try {
					RemoteProxyVCellConnectionFactory vcellConnectionFactory = new RemoteProxyVCellConnectionFactory(host, port, userLoginInfo);
					VCellConnection vcellConnection = vcellConnectionFactory.createVCellConnection();
					if (runningSimId!=null) {
						try {
							vcellConnection.getSimulationController().stopSimulation(runningSimId);
						}catch (Exception e) {
							e.printStackTrace(System.out);
						}
					}
					if (savedBioModelKey!=null) {
						vcellConnection.getUserMetaDbServer().deleteBioModel(savedBioModelKey);
					}
				}catch (Exception e) {
					e.printStackTrace(System.out);
				}
			}
			try {
				Thread.sleep(RUNSIM_LOOP_SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public NagiosStatus getLoginStatus() {
		long curr_MS = System.currentTimeMillis();
		// get last 5 minutes of events to determine status
		HealthEvent[] events = query(curr_MS-(300*1000), curr_MS);
		
		// find last completed login event (if any)
		HealthEvent loginCompleteEvent = null;
		for (HealthEvent event : events) {
			if (event.eventType == HealthEventType.LOGIN_FAILED 
					|| event.eventType == HealthEventType.LOGIN_SUCCESS) {
				if (loginCompleteEvent==null || loginCompleteEvent.timestamp_MS < event.timestamp_MS) {
					loginCompleteEvent = event;
				}
			}
		}

		// find corresponding START event to determine elapsed time
		Long elapsedTime_MS = null;
		if (loginCompleteEvent!=null) {
			for (HealthEvent event : events) {
				if (event.eventType == HealthEventType.LOGIN_START 
						&& event.transactionId == loginCompleteEvent.transactionId) {
					elapsedTime_MS = loginCompleteEvent.timestamp_MS - event.timestamp_MS;
				}
			}
		}
		
		if (loginCompleteEvent==null) {
			return NagiosStatus.Unknown(null, "no completed login attempts");
			
		} else if (loginCompleteEvent.eventType==HealthEventType.LOGIN_SUCCESS) {
			//
			// OK or WARNING depending on time
			//
			if (elapsedTime_MS==null || elapsedTime_MS > LOGIN_TIME_ERROR) {
				return NagiosStatus.Critical(elapsedTime_MS, "login timeout");
			}
			if (elapsedTime_MS < LOGIN_TIME_WARNING) {
				return NagiosStatus.OK(elapsedTime_MS);
			}
			return NagiosStatus.Warning(elapsedTime_MS);

		} else if (loginCompleteEvent.eventType==HealthEventType.LOGIN_FAILED) {
			return NagiosStatus.Critical(elapsedTime_MS, loginCompleteEvent.message);
		} else {
			throw new RuntimeException("unexpected HealthEventType");
		}
	}
	
	public NagiosStatus getRunsimStatus() {
		long curr_MS = System.currentTimeMillis();
		// get last 5 minutes of events to determine status
		HealthEvent[] events = query(curr_MS-(300*1000), curr_MS);
		
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
			return NagiosStatus.Unknown(null, "no completed simulation attempts");
			
		} else if (loginCompleteEvent.eventType==HealthEventType.RUNSIM_SUCCESS) {
			//
			// OK or WARNING depending on time
			//
			if (elapsedTime_MS==null || elapsedTime_MS > SIMULATION_TIMEOUT) {
				return NagiosStatus.Critical(elapsedTime_MS, "simulation timeout");
			}
			if (elapsedTime_MS==null || elapsedTime_MS < SIMULATION_TIME_WARNING) {
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
