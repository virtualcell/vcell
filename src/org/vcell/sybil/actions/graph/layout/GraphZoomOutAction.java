package org.vcell.sybil.actions.graph.layout;

/*   GraphZoomOutAction  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2009
 *   Zoom out
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;

import org.vcell.sybil.actions.graph.layout.ZoomDefaultPercentages;

public class GraphZoomOutAction extends BaseAction {

	private static final long serialVersionUID = -2244457805781249806L;

	public GraphZoomOutAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}

	public void actionPerformed(ActionEvent event) {
		coreManager().graphSpace().setZoomPercent(ZoomDefaultPercentages.nextLowerThan(coreManager()
				.graphSpace().zoomPercent()));
	}
	
}
