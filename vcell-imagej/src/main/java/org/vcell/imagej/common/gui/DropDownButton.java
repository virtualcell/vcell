package org.vcell.imagej.common.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class DropDownButton extends JButton {
	
	private JPopupMenu menu;
	
	public DropDownButton(String text) {
		super(text + "   "); // Add some extra space for the arrow
		menu = new JPopupMenu();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Component component = e.getComponent();
				menu.show(component, 7, component.getHeight() - 4);
			}
		});
	}
	
	public JPopupMenu getMenu() {
		return menu;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Rectangle bounds = g.getClipBounds();
		
		int maxX = (int) bounds.getMaxX();
		int[] xCoordinates = {
				maxX - 25,
				maxX - 20,
				maxX - 14
		};
		
		int midY = (int) ((bounds.getMinY() + bounds.getMaxY()) / 2);
		int[] yCoordinates = {
				midY - 3,
				midY + 3,
				midY - 3
		};
		g.fillPolygon(xCoordinates, yCoordinates, 3);
	}
}
