package org.vcell.sybil.gui.space;

/*   GUIPortSpace  --- by Oliver Ruebenacker, UCHC --- June 2008 to October 2009
 *   A wrapper for QueryPanel to configure and launch ports
 */

import org.vcell.sybil.gui.port.PortPanel;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.util.ui.UIPortSpace;

public class GUIPortSpace<P extends PortPanel> extends GUISpace<P> implements UIPortSpace {
	
	protected GUIPortSpace(P panelNew) { super(panelNew); }

	public P panel() { return component(); }

	public void update(SBWorkView view) { panel().update(view); }

}
