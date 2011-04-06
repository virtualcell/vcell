package org.vcell.sybil.models.views;

/*   SBBasicView  --- by Oliver Ruebenacker, UCHC --- October 2009 to February 2010
 *   Simple way to store a view of an sbbox
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox.Location;
import org.vcell.sybil.models.sbbox.SBBox.Process;
import org.vcell.sybil.models.sbbox.SBBox.Substance;
import org.vcell.sybil.models.sbbox.SBInferenceBox;

public class SBBasicView implements SBView {
	
	protected SBInferenceBox box;
	protected Set<Process> processes = new HashSet<Process>();
	protected Set<Substance> substances = new HashSet<Substance>();
	protected Set<Location> locations = new HashSet<Location>();
	
	public SBBasicView(SBInferenceBox box) { 
		this.box = box; 
	}
	
	public SBInferenceBox box() { return box; }
	public Set<Process> processes() { return processes; }
	public Set<Location> locations() { return locations; }
	public Set<Substance> substances() { return substances; }
	
}
