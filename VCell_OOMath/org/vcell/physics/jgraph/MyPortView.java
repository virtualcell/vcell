/*
 * @(#)MyPortView.java 3.3 23-APR-04
 * 
 * Copyright (c) 2001-2005, Gaudenz Alder All rights reserved.
 * 
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.vcell.physics.jgraph;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortRenderer;
import org.jgraph.graph.PortView;

public class MyPortView extends PortView {

	static {
		URL portUrl = MyPortView.class.getClassLoader().getResource("org/vcell/physics/jgraph/resources/port.gif");
		if (portUrl != null){
			portIcon = new ImageIcon(portUrl);
		}
	}
//	public static void main(String[] args) {
//		URL portUrl = Main.class.getClassLoader().getResource("org/jgraph/example/resources/port.gif");
//		if (portUrl != null)
//			portIcon = new ImageIcon(portUrl);
//		JFrame frame = new JFrame("PortView");
//		JGraph graph = new JGraph(new DefaultGraphModel());
//		graph.getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {
//			/**
//			 * Constructs a new instance of a PortView view for the specified object
//			 */
//			protected PortView createPortView(Port p) {
//				return new MyPortView(p);
//			}
//		});
//		// Adds something interesting to the model
//		// now that the new cell view factory is attached.
//		JGraph.addSampleData(graph.getModel());
//		graph.setPortsVisible(true);
//		frame.getContentPane().add(new JScrollPane(graph));
//		frame.pack();
//		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);
//	}

	protected static ImageIcon portIcon;

	protected static MyPortRenderer renderer = new MyPortRenderer();

	public MyPortView(Object cell) {
		super(cell);
	}

	/** 
	* Returns the bounds for the port view. 
	*/
	public Rectangle2D getBounds() {
		if (portIcon != null) {
			Point2D pt = (Point2D) getLocation().clone();
			int width = portIcon.getIconWidth();
			int height = portIcon.getIconHeight();
			Rectangle2D bounds = new Rectangle2D.Double();
			bounds.setFrame(pt.getX() - width / 2,
							pt.getY() - height / 2,
							width,
							height);
			return bounds;
		}
		return super.getBounds();
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class MyPortRenderer extends PortRenderer {

		public void paint(Graphics g) {
			portIcon.paintIcon(this, g, 0, 0);
		}

	}

}
