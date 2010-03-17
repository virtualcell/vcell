package org.vcell.sybil.models.graphcomponents;

/*   SyCoStatementLike  --- by Oliver Ruebenacker, UCHC --- December 2007
 *   A component of a graph, corresponding to a single Statement
 */

import com.hp.hpl.jena.rdf.model.Statement;

public interface RDFGraphCompRelation extends RDFGraphComponent {

	public Statement statement();

}
