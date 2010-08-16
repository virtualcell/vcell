package org.vcell.smoldyn.simulationsettings;


import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.smoldyn.simulation.SimulationUtilities;
import org.vcell.smoldyn.simulationsettings.ObservationEvent.EventType;
import org.vcell.smoldyn.simulationsettings.VCellObservationEvent.VCellEventType;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;
import org.vcell.smoldyn.simulationsettings.util.Filehandle;
import org.vcell.smoldyn.simulationsettings.util.SimulationGraphics;
import org.vcell.smoldyn.simulationsettings.util.SpeciesStateGraphics;
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
	private ArrayList<SpeciesStateGraphics> speciesstategraphics = new ArrayList<SpeciesStateGraphics>();
	private ArrayList<SurfaceGraphics> surfacegraphics = new ArrayList<SurfaceGraphics>();	
	private SimulationGraphics simulationgraphics;
	private SmoldynTime smoldyntime;
	private InternalSettings internalsettings;
	private final int [] boxes = new int [3];

	
	/**
	 * First, initialize an empty SimulationSettings.  Then, add information using addX and 
	 * setX methods.
	 */
	public SimulationSettings() {
		
	}


	public void addSpeciesStateGraphics(SpeciesStateGraphics speciesstategraphics) {
		this.speciesstategraphics.add(speciesstategraphics);
	}
	
	public SpeciesStateGraphics [] getSpeciesStateGraphics() {
		return this.speciesstategraphics.toArray(new SpeciesStateGraphics [this.speciesstategraphics.size()]);
	}
	
	public void addSurfaceGraphics(SurfaceGraphics surfacegraphics) {
		this.surfacegraphics.add(surfacegraphics);
	}
	
	public SurfaceGraphics [] getSurfaceGraphics() {
		return this.surfacegraphics.toArray(new SurfaceGraphics [this.surfacegraphics.size()]);
	}
	
	public void setSimulationGraphics(SimulationGraphics simulationgraphics) {
		this.simulationgraphics = simulationgraphics;
	}
	
	public SimulationGraphics getSimulationGraphics() {
		return this.simulationgraphics;
	}

	
	public ObservationEvent [] getObservationEvents() {
		return observationevents.toArray(new ObservationEvent [observationevents.size()]);
	}
	
	public void addObservationEvent(EventTiming eventtiming, EventType eventtype, String filehandlename) {
		SimulationUtilities.checkForNull("argument to addObservationEvent", eventtiming, eventtype);
		Filehandle filehandle = this.getFilehandle(filehandlename);
		ObservationEvent observationevent = new ObservationEvent(eventtiming, eventtype, filehandle);
		this.observationevents.add(observationevent);
	}
	
	
	public VCellObservationEvent [] getVCellObservationEvents() {
		return vcellobservationevents.toArray(new VCellObservationEvent [vcellobservationevents.size()]);
	}
	
	public void addVCellObservationEvent(EventTiming eventtiming, VCellEventType eventtype) {
		SimulationUtilities.checkForNull("argument to addVCellDataRecordingEvent", eventtiming, eventtype);
		VCellObservationEvent vcellobservationevent = new VCellObservationEvent(eventtiming, eventtype);
		this.vcellobservationevents.add(vcellobservationevent);
	}
	
	
	public ControlEvent [] getControlEvents() {
		return controlevents.toArray(new ControlEvent [controlevents.size()]);
	}
	
	
	public void addControlEvent(ControlEvent controlevent) {
		// TODO is s already in this.events?
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
	 * @throws RuntimeException if SimulationSettings already has a {@link Filehandle} of the given name
	 * 
	 */
	public void addFilehandle(String filehandlename) {
		if(hasFilehandle(filehandlename)) {
			SimulationUtilities.throwAlreadyHasKeyException("filehandle name was <" + filehandlename + ">");
		}
		this.filehandles.put(filehandlename, new Filehandle(filehandlename));
	}
	
	public Filehandle getFilehandle(String filehandlename) {
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
	
	public void setInternalSettings(InternalSettings simulationsettings) {
		this.internalsettings = simulationsettings;
	}

	public SmoldynTime getSmoldyntime() {
		return smoldyntime;
	}

	public void setSmoldyntime(SmoldynTime smoldyntime) {
		this.smoldyntime = smoldyntime;
	}	
	
	
	public void setBoxes(int x, int y, int z) {
		this.boxes[0] = x;
		this.boxes[1] = y;
		this.boxes[2] = z;
	}
	
	public int [] getBoxes() {
		return this.boxes;
	}
}
