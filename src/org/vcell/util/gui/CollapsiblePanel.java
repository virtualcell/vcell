package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class CollapsiblePanel extends JPanel {

	private class ComponentTitledBorder implements Border, MouseListener {
		int offset = 5;

		Component comp;
		JComponent container;
		Rectangle rect;
		Border border;

		public ComponentTitledBorder(JComponent comp, JComponent container,
				Border border) {
			this.comp = comp;
			comp.setOpaque(true);
			this.container = container;
			this.border = border;
			container.addMouseListener(this);
		}

		public boolean isBorderOpaque() {
			return true;
		}

		public void paintBorder(Component c, Graphics g, int x, int y, int width,
				int height) {
			Insets borderInsets = border.getBorderInsets(c);
			Insets insets = getBorderInsets(c);
			int temp = (insets.top - borderInsets.top) / 2;
			border.paintBorder(c, g, x, y + temp, width, height - temp);
			Dimension size = comp.getPreferredSize();
			rect = new Rectangle(offset, 0, size.width, size.height);
			SwingUtilities.paintComponent(g, comp, (Container) c, rect);
		}

		public Insets getBorderInsets(Component c) {
			Dimension size = comp.getPreferredSize();
			Insets insets = border.getBorderInsets(c);
			insets.top = Math.max(insets.top, size.height);
			return insets;
		}

		private void dispatchEvent(MouseEvent me) {
			if (rect != null && rect.contains(me.getX(), me.getY())) {
				Point pt = me.getPoint();
				pt.translate(-offset, 0);
				comp.setBounds(rect);
				comp.dispatchEvent(new MouseEvent(comp, me.getID(), me.getWhen(),
						me.getModifiers(), pt.x, pt.y, me.getClickCount(), me
								.isPopupTrigger(), me.getButton()));
				if (!comp.isValid())
					container.repaint();
			}
		}

		public void mouseClicked(MouseEvent me) {
			dispatchEvent(me);
		}

		public void mouseEntered(MouseEvent me) {
			dispatchEvent(me);
		}

		public void mouseExited(MouseEvent me) {
			dispatchEvent(me);
		}

		public void mousePressed(MouseEvent me) {
			dispatchEvent(me);
		}

		public void mouseReleased(MouseEvent me) {
			dispatchEvent(me);
		}
	}
	
	public static Icon expandedIcon = UIManager.getIcon("Tree.expandedIcon");
	public static Icon collapsedIcon = UIManager.getIcon("Tree.collapsedIcon");
	public static final Color borderTitleColor = UIManager.getColor("TitledBorder.titleColor");
	private JLabel borderLabel = null;
	private boolean bExpanded = true;

	public CollapsiblePanel(String borderTitle) {
		this(borderTitle, true);
	}
	public CollapsiblePanel(String borderTitle, boolean expanded) {
		super();
		this.bExpanded = expanded;
		borderLabel  = new JLabel(borderTitle, bExpanded ? expandedIcon : collapsedIcon, SwingConstants.RIGHT);
		borderLabel.setForeground(borderTitleColor);
		setBorder(new ComponentTitledBorder(borderLabel, this, BorderFactory.createEtchedBorder()));
		borderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				bExpanded = !bExpanded;
				expand();				
			}			
		});
		addComponentListener(new ComponentListener() {			
			public void componentShown(ComponentEvent e) {
				expand();
			}			
			public void componentResized(ComponentEvent e) {
				expand();
			}			
			public void componentMoved(ComponentEvent e) {				
			}			
			public void componentHidden(ComponentEvent e) {
			}
		});
	}
	
	private void expand() {
		if (!isVisible()) {
			return;
		}
		borderLabel.setIcon(bExpanded ? expandedIcon : collapsedIcon);
		for (Component child : getComponents()) {
			child.setVisible(bExpanded);
		}
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		for (Component child : getComponents()) {
			child.setVisible(aFlag);
		}
		super.setVisible(aFlag);
	}
}
