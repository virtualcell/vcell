package org.vcell.sybil.gui.graph;

/*   SybilGraphContainerShape  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2009
 *   Somehow support for Panel for Graph
 *   Last change: added catching of ConModExceps in preferredSize
 */

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ConcurrentModificationException;

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;


public class SybilGraphContainerShape extends ContainerShape {

	public SybilGraphContainerShape(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
		bNoFill = false;
		setColorFGSelected(java.awt.Color.red);
		setColorBGSelected(java.awt.Color.white);
		this.setColorBG(java.awt.Color.white);
	}

	@Override
	public void updateOtherSizes(Graphics2D g) {
		location.setP(size.width/2, size.height/2);
		super.updateOtherSizes(g);
	}
	
	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		Dimension emptySize = super.getPreferedSize(g);
		boolean successful = false;
		int countFailures = 0;
		while(countFailures < 3 && !successful) {
			try {
				for (Shape shape : children()) {
					if (shape instanceof ElipseShape){
						emptySize.width = Math.max(emptySize.width, shape.p().x + shape.size().width/2);
						emptySize.height = Math.max(emptySize.height, shape.p().y + shape.size().height/2);
					}
				}	
				successful = true;
			} catch (ConcurrentModificationException e) { countFailures++; }			
		}
		return emptySize;
	}

}
