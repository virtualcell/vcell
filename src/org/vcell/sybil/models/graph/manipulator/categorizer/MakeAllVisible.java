package org.vcell.sybil.models.graph.manipulator.categorizer;

/*   MakeAllVisible  --- by Oliver Ruebenacker, UCHC --- January 2008 to January 2009
 *   A GraphManip implementation making all shapes visible.
 */

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.Visibility;
import org.vcell.sybil.models.graph.manipulator.GraphManipulator;

public class MakeAllVisible<S extends UIShape<S>, G extends UIGraph<S, G>> 
implements GraphManipulator<S, G> {

	public void applyToGraph(G graph) { graph.unhideAllComps(Visibility.hiderFilter); }

}
