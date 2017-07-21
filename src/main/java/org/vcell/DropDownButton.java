package org.vcell;

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
		super(text);
		menu = new JPopupMenu();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Component component = e.getComponent();
				menu.show(component, component.getX() + 2, component.getY() + component.getHeight() - 10);
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
		int[] xCoordinates = {
				(int) bounds.getMaxX() - 20,
				(int) bounds.getMaxX() - 15,
				(int) bounds.getMaxX() - 10
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
