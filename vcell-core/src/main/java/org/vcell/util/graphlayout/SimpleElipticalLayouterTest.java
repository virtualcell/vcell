/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.graphlayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

	
    
public class SimpleElipticalLayouterTest extends JPanel {
	
	final String[][] pairs = {			// edge definitions
//			{ "s", "t", "11" },
//			{ "t", "u", "12" },
//			{ "t", "v", "13" },
			{ "a", "b", "1" },
			{ "b", "c", "2" },
			{ "b", "g", "21" },
			{ "b", "h", "22" },
			{ "g", "i", "23" },
			{ "f", "a", "24" },
			{ "c", "d", "3" },
			{ "d", "e", "4" },
			{ "e", "f", "5" },
			{ "x", "i", "700" },		// 2 nodes outside the central compartment
			{ "y", "h", "701" }
	};
		
	final String[][] nodes = {			// vertex initial coordinates
//			{ "s", "25", "155" },			// 4 nodes not connected to the others
//			{ "t", "45", "155" },
//			{ "u", "65", "155" },
//			{ "v", "85", "155" },
			{ "a", "99", "30" },
			{ "b", "50", "10" },		// b has multiple edges
			{ "c", "10", "10" },
			{ "d", "20", "20" },
			{ "e", "25", "25" },
			{ "f", "30", "30" },
			{ "g", "35", "50" },
			{ "h", "50", "110" },
			{ "i", "35", "110" },
			{ "x", "-120", "110" },		// compartment to the left
			{ "y", "210", "110" }		// compartment to the right
	};
	
	DirectedSparseMultigraph<String, Number> g = null;
	SpringLayout<String, Number> layout = null;
//	ISOMLayout<String, Number> layout = null;
	JPanel displayPanel = null;
	int xOffset = 0;
	int width = 200;	// width of the working area
	int height = 300;
	int radius = 3;		// radius of circle depicting nodes
	
	private class DisplayPanel extends JPanel {
		
		public DisplayPanel() {
			Dimension d = new Dimension(3*width, height);
			setPreferredSize(d);
			setBackground(Color.lightGray);
		}

		@Override
		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			if(g == null) {
				return;
			}
				
			Graphics2D g2 = (Graphics2D)graphics;
			g2.setColor(Color.gray);					// compartment margins - no point in drawing horizontal top
			g2.drawLine(xOffset, 0, xOffset, height);				// vertical left
			g2.drawLine(xOffset+width, 0, xOffset+width, height);	// vertical right
			g2.drawLine(xOffset, height, xOffset+width, height);	// horizontal bottom
			
			g2.setColor(Color.black);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			for(String v : g.getVertices()) {
				int x = (int)layout.getX(v) + xOffset;
				int y = (int)layout.getY(v);
				g2.fillOval(x-radius, y-radius, radius*2, radius*2);
			}

			for (int i = 0; i < pairs.length; i++) {
				String[] pair = pairs[i];
				g.addEdge(Integer.parseInt(pair[2]), pair[0], pair[1]);
				String v1 = pair[0];
				String v2 = pair[1];
				int x1 = (int)layout.getX(v1) + xOffset;
				int y1 = (int)layout.getY(v1);
				int x2 = (int)layout.getX(v2) + xOffset;
				int y2 = (int)layout.getY(v2);
				g2.drawLine(x1, y1, x2, y2);
			}
			System.out.println("Done painting");
		}
	}
		

	
	public SimpleElipticalLayouterTest() {
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTH;
		JPanel p1 = new JPanel();
		JButton b1 = new JButton("Recalculate");
		b1.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
			    recalculate(1500);
			  } 
			} );
		p1.add(b1);
		JButton b2 = new JButton("Initialize");
		b2.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
			    recalculate(0);
			  } 
			} );
		p1.add(b2);
		add(p1, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.SOUTH;
		displayPanel = new DisplayPanel();
		add(displayPanel, gbc);
		
		
	}
	
	public void recalculate(int steps) {
		
		try {
			g = new DirectedSparseMultigraph<>();
			for (int i = 0; i < pairs.length; i++) {
				String[] pair = pairs[i];
				g.addEdge(Integer.parseInt(pair[2]), pair[0], pair[1]);
			}
			
			layout = new SpringLayout<String, Number>(g);
			Dimension d = displayPanel.getSize();
			
			width = (int)(d.getWidth() / 3);
			height = (int)d.getHeight();
			xOffset = width;
			
			layout.setSize(new Dimension(width, height));
			for(int i = 0; i < nodes.length; i++) {
				String[] node = nodes[i];
				layout.setLocation(node[0], Double.parseDouble(node[1]), Double.parseDouble(node[2]));
			}
			
			layout.lock("x", true);
			layout.lock("y", true);
			
			int step = 0;
			while (!layout.done() && step < steps) {		// number of steps
				layout.step();
				step++;
			}
			System.out.println("Done recalculating, steps = " + step);
			displayPanel.repaint();
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of SimpleElipticalLayouterTest");
			exception.printStackTrace(System.out);
		}
	}
		

		
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SimpleElipticalLayouterTest p = new SimpleElipticalLayouterTest();
		f.getContentPane().add(p);
		f.pack();
//		f.setResizable(false);
		f.setVisible(true);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				p.recalculate(0);
			}
		});	

	}
	
}
	
	
