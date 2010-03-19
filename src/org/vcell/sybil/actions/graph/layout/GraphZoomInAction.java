package org.vcell.sybil.actions.graph.layout;

/*   GraphZoomInAction  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2009
 *   Zoom in
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;

import org.vcell.sybil.actions.graph.layout.ZoomDefaultPercentages;

public class GraphZoomInAction extends BaseAction {

	private static final long serialVersionUID = 5165788067478125559L;

	public GraphZoomInAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}

	public void actionPerformed(ActionEvent event) {
		coreManager().graphSpace().setZoomPercent(ZoomDefaultPercentages.nextHigherThan(coreManager()
				.graphSpace().zoomPercent()));
	}
	
}
