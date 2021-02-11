/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
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

	public static final String SEARCHPPANEL_EXPANDED = "SEARCHPPANEL_EXPANDED";

	public class ComponentTitledBorder implements Border, MouseListener {
		int offset = 7;

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

		public void setTitle(String title) {
			if(comp instanceof JLabel) {
				((JLabel)comp).setText(title);
				((JLabel)comp).setPreferredSize(null);
			}
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
	private JPanel contentPanel = null;

	public CollapsiblePanel(String borderTitle) {
		this(borderTitle, true);
	}
	public CollapsiblePanel(String borderTitle, boolean expanded, Border border) {
		super();
		this.bExpanded = expanded;
		borderLabel  = new JLabel(borderTitle, bExpanded ? expandedIcon : collapsedIcon, SwingConstants.LEFT);
		borderLabel.setForeground(borderTitleColor);
		borderLabel.setFont(borderLabel.getFont().deriveFont(Font.PLAIN));
		Dimension size = new Dimension(borderLabel.getPreferredSize().width + 5, borderLabel.getPreferredSize().height);
		borderLabel.setPreferredSize(size);
		setBorder(new ComponentTitledBorder(borderLabel, this, border));
		borderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				bExpanded = !bExpanded;
				toggleExpand();
			}			
		});
		contentPanel = new JPanel();
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();  
        gbc.weightx = 1.0;  
        gbc.weighty = 1.0;  
        gbc.fill = GridBagConstraints.BOTH; 
        add(contentPanel, gbc);
        contentPanel.setVisible(bExpanded);
	}
	public CollapsiblePanel(String borderTitle, boolean expanded) {
		super();
		this.bExpanded = expanded;
		borderLabel  = new JLabel(borderTitle, bExpanded ? expandedIcon : collapsedIcon, SwingConstants.LEFT);
		borderLabel.setForeground(borderTitleColor);
		borderLabel.setFont(borderLabel.getFont().deriveFont(Font.BOLD));
		Dimension size = new Dimension(borderLabel.getPreferredSize().width + 5, borderLabel.getPreferredSize().height);
		borderLabel.setPreferredSize(size);
		setBorder(new ComponentTitledBorder(borderLabel, this, BorderFactory.createEtchedBorder()));
		borderLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				bExpanded = !bExpanded;
				toggleExpand();
			}			
		});
		contentPanel = new JPanel();
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();  
        gbc.weightx = 1.0;  
        gbc.weighty = 1.0;  
        gbc.fill = GridBagConstraints.BOTH; 
        add(contentPanel, gbc);
        contentPanel.setVisible(bExpanded);
	}
	
	private void toggleExpand() {
		if (!isVisible()) {
			return;
		}
		borderLabel.setIcon(bExpanded ? expandedIcon : collapsedIcon);
		contentPanel.setVisible(bExpanded);
		revalidate();
		firePropertyChange(SEARCHPPANEL_EXPANDED, !bExpanded, bExpanded);
	}
	
	public JPanel getContentPanel() {
		return contentPanel;
	}
	
	public void expand(boolean bexpand) {
		if (bExpanded == bexpand) {
			return;
		}
		bExpanded = bexpand;
		toggleExpand();
	}
}
