package org.vcell.sybil.models.bpimport.edges;

/*   ProcessEdge  --- by Oliver Ruebenacker, UCHC --- July 2008 to September 2009
 *   A set representing an SBPAX reaction participation edge
 */

import org.vcell.sybil.models.sbbox.SBBox;

public interface ProcessEdge {
	
	public SBBox.Process process();
	public SBBox.Participant participant();
	public SBBox.Species species();
	public SBBox.Substance entity();
	public SBBox.RDFType entityType();
	public SBBox.Substance substance();
	public SBBox.Location location();
	public SBBox.Stoichiometry stoichiometry();
	public float stoichiometricCoeff();

}
