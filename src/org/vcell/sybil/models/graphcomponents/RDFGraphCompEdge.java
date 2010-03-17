package org.vcell.sybil.models.graphcomponents;

/*   SyCoStatementLike  --- by Oliver Ruebenacker, UCHC --- December 2007 to April 2008
 *   An edge of a graph
 */

public interface RDFGraphCompEdge extends RDFGraphCompRelation {

	public RDFGraphComponent startComp();
	public RDFGraphComponent endComp();
	public RDFGraphCompRelation edgeComp();
	
}
