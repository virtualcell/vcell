package org.vcell.sybil.models.graph;

/*   GraphModel  --- by Oliver Ruebenacker, UCHC --- January 2008 to March 2010
 *   Generic model for graph independent of graphical representation.
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.util.collections.MultiHashMap;
import org.vcell.sybil.util.collections.MultiMap;


public class GraphModel {

	public interface Listener {
		public void startNewGraph();
		public void updateView();
		public void clear();
	}
	
	protected SBBox box;
	protected Set<Listener> listeners = new HashSet<Listener>();
	protected Set<RDFGraphComponent> selectedComps = new HashSet<RDFGraphComponent>();
	protected RDFGraphComponent chosenComp;
	protected MultiMap<NamedThing, RDFGraphComponent> thingToComponentMap = 
		new MultiHashMap<NamedThing, RDFGraphComponent>();
	
	public GraphModel(SBBox box) { this.box = box; }
	
	public SBBox box() { return box; }
	public Set<Listener> listeners() { return listeners; }
	public void listenersUpdate() { for(Listener listener : listeners) { listener.updateView(); } }
	public void listenersClear() { for(Listener listener : listeners) { listener.clear(); } }
	public Set<RDFGraphComponent> selectedComps() { return selectedComps; }
	public void setChosenComp(RDFGraphComponent chosenComp) { this.chosenComp = chosenComp; }
	public RDFGraphComponent chosenComp() { return chosenComp; }
	public MultiMap<NamedThing, RDFGraphComponent> thingToComponentMap() { return thingToComponentMap; }
	
	public void clear() {
		selectedComps.clear();
		chosenComp = null;
		thingToComponentMap.clear();
		listenersClear();
	}
	
}
