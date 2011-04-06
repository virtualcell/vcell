package org.vcell.sybil.gui.graph.nodes;

/*   ParticipantNodeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shape for nodes representing participants in reactions or complexes
 */

import java.awt.Dimension;
import java.awt.Graphics2D;

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public class NodeShapeParticipant extends NodeShape {
	int radius = 4;

	public NodeShapeParticipant(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
		setColorFGSelected(java.awt.Color.black);
		setColorBG(java.awt.Color.black);
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		java.awt.FontMetrics fm = g.getFontMetrics();
		labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
		labelSize.width = fm.stringWidth(label());
		preferedSize.height = radius*2;
		preferedSize.width = radius*2;
		return preferedSize;
	}

}
