package org.vcell.sybil.gui.graph;

/*   ContainerShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2009
 *   Shape of container in Sybil
 */

import java.awt.Dimension;

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;


public abstract class ContainerShape extends RectangleShape {
	
	public ContainerShape(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
		size = new Dimension(700, 700);
		location.setP(size.width/2, size.height/2);
		preferedSize = new Dimension(700, 700);
		updateScreenSize = false;
		updatePreferedSize = false;
	}
	
	public PaintLevel paintLevel() { return PaintLevel.Container; }

}