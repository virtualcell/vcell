package org.vcell.smoldyn.simulationsettings;


import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.smoldyn.simulation.SimulationUtilities;
import org.vcell.smoldyn.simulation.SmoldynException;
import org.vcell.smoldyn.simulationsettings.ObservationEvent.EventType;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;
import org.vcell.smoldyn.simulationsettings.util.Filehandle;
import org.vcell.smoldyn.simulationsettings.util.SimulationGraphics;
import org.vcell.smoldyn.simulationsettings.util.SpeciesGraphics;
import org.vcell.smoldyn.simulationsettings.util.SurfaceGraphics;



/**
 * @author mfenwick
 *
 * A SimulationSettings can be associated with a Model through a SmoldynRun.  A SimulationSettings
 * has time settings (SmoldynTime), simulation settings such as those pertaining to
 * random number generation (InternalSettings), filehandles for writing results 
 * to disk (Filehandle), display information for molecules, surfaces, and so on (Graphics),
 * and two different types of events--events that take measurements
 * of the current state of the simulation and record the results, sending them to disk
 * (ObservationEvent), and events which give the user control over the running of the simulation,
 * allowing him to do such things as pause the simulation (ControlEvent).
 */
public class SimulationSettings {

	private ArrayList<VCellObservationEvent> vcellobservationevents = new ArrayList<VCellObservationEvent>();
	private ArrayList<ObservationEvent> observationevents = new ArrayList<ObservationEvent>();
	private ArrayList<ControlEvent> controlevents = new ArrayList<ControlEvent>();
	private Hashtable<String, Filehandle> filehandles = new Hashtable<String, Filehandle>();
	private ArrayList<SpeciesGraphics> speciesgraphics = new ArrayList<SpeciesGraphics>();
	private ArrayList<SurfaceGraphics> surfacegraphics = new ArrayList<SurfaceGraphics>();	
	private SimulationGraphics simulationgraphics;
	private SmoldynTime smoldyntime;
	private InternalSettings internalsettings;
	
	/**
	 * First, initialize an empty SimulationSettings.  Then, add information using addX and 
	 * setX methods.
	 */
	public SimulationSettings() {
		
	}


	public void addSpeciesGraphics(SpeciesGraphics speciesgraphics) {
		SimulationUtilities.checkForNull("species graphics", speciesgraphics);
		this.speciesgraphics.add(speciesgraphics);
	}
	
	public SpeciesGraphics [] getSpeciesGraphics() {
		return this.speciesgraphics.toArray(new SpeciesGraphics [this.speciesgraphics.size()]);
	}
	
	public void addSurfaceGraphics(SurfaceGraphics surfacegraphics) {
		SimulationUtilities.checkForNull("surface graphics", surfacegraphics);
		this.surfacegraphics.add(surfacegraphics);
	}
	
	public SurfaceGraphics [] getSurfaceGraphics() {
		return this.surfacegraphics.toArray(new SurfaceGraphics [this.surfacegraphics.size()]);
	}
	
	public void setSimulationGraphics(SimulationGraphics simulationgraphics) {
		SimulationUtilities.checkForNull("simulation graphics", simulationgraphics);
		this.simulationgraphics = simulationgraphics;
	}
	
	public SimulationGraphics getSimulationGraphics() {
		return this.simulationgraphics;
	}

	
	public ObservationEvent [] getObservationEvents() {
		return observationevents.toArray(new ObservationEvent [observationevents.size()]);
	}
	
	public void addObservationEvent(EventTiming eventtiming, EventType eventtype, String filehandlename) throws SmoldynException {
		SimulationUtilities.checkForNull("argument to addObservationEvent", eventtiming, eventtype);
		Filehandle filehandle = this.getFilehandle(filehandlename);
		ObservationEvent observationevent = new ObservationEvent(eventtiming, eventtype, filehandle);
		this.observationevents.add(observationevent);
	}
	
	
	public VCellObservationEvent [] getVCellObservationEvents() {
		return vcellobservationevents.toArray(new VCellObservationEvent [vcellobservationevents.size()]);
	}
	
	public void addVCellObservationEvent(VCellObservationEvent vcellObservationEvent) {
		this.vcellobservationevents.add(vcellObservationEvent);
	}
	
	
	public ControlEvent [] getControlEvents() {
		return controlevents.toArray(new ControlEvent [controlevents.size()]);
	}
	
	
	public void addControlEvent(ControlEvent controlevent) {
		// TODO is s already in this.events?
		SimulationUtilities.checkForNull("control event", controlevent);
		this.controlevents.add(controlevent);
	}
	
	
	public Filehandle [] getFilehandles() {
		return filehandles.values().toArray(new Filehandle [filehandles.size()]);
	}
	
	/**
	 * Tries to add a new Filehandle for Smoldyn output to the SimulationSettings.  A new Filehandle will be added if there is currently
	 * no other Filehandle with the same filename.
	 * 
	 * @param filehandlename
	 * @throws SmoldynException if already a filehandle with the path specified by filehandlename
	 * 
	 */
	public void addFilehandle(String filehandlename) throws SmoldynException {
		if(hasFilehandle(filehandlename)) {
			SimulationUtilities.throwAlreadyHasKeyException("filehandle name was <" + filehandlename + ">");
		}
		this.filehandles.put(filehandlename, new Filehandle(filehandlename));
	}
	
	/**
	 * @param filehandlename
	 * @return
	 * @throws SmoldynException if no filehandle with the path specified by filehandlename
	 */
	public Filehandle getFilehandle(String filehandlename) throws SmoldynException {
		if(!hasFilehandle(filehandlename)) {
			SimulationUtilities.throwNoAssociatedValueException("filehandle (name was <" + filehandlename + ">)");
		}
		return this.filehandles.get(filehandlename);
	}
	
	private boolean hasFilehandle(String filehandlename) {
		if(this.filehandles.get(filehandlename) == null) {
			return false;
		}
		return true;
	}
	
	
	public InternalSettings getInternalSettings() {
		return this.internalsettings;
	}
	
	public void setInternalSettings(InternalSettings internalsettings) {
		SimulationUtilities.checkForNull(InternalSettings.class.toString(), internalsettings);
		this.internalsettings = internalsettings;
	}

	public SmoldynTime getSmoldyntime() {
		return smoldyntime;
	}

	public void setSmoldyntime(SmoldynTime smoldyntime) {
		SimulationUtilities.checkForNull(SmoldynTime.class.toString(), smoldyntime);
		this.smoldyntime = smoldyntime;
	}	
}
