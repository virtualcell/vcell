package org.vcell.sybil.util.ui;

/*   UserInterface  --- by Oliver Ruebenacker, UCHC --- August 2007 to January 2009
 *   An interface for GUIBase or equvalent
 */

import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public interface UserInterfaceGraph<S extends UIShape<S>, G extends UIGraph<S, G>> {

	public UIModelGraphSpace<S, G> createGraphSpace();
}
