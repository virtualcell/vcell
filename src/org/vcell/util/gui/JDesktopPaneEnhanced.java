package org.vcell.util.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
/**
 * Insert the type's description here.
 * Creation date: (3/19/01 4:04:38 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class JDesktopPaneEnhanced extends javax.swing.JDesktopPane {
	
	private class InternalEventHandler implements HierarchyBoundsListener {

		public void ancestorMoved(HierarchyEvent e) {
			// ignore moved			
		}

		public void ancestorResized(HierarchyEvent e) {	
			try {
				internalDesktopManager.relayoutFrames();
			} catch (Exception ex) {
				ex.printStackTrace();
			}				
		}
		
	}
	private class InternalDesktopManager extends DefaultDesktopManager {
		public void relayoutFrames() {
			JInternalFrame[] allFrames = getAllFrames();
			for (JInternalFrame frame : allFrames) {
				if (frame.isIcon()) {
//					Rectangle r = getBoundsForIconOf(frame);
//					frame.getDesktopIcon().setBounds(r.x, r.y, r.width, r.height);
				} else if (frame.isVisible()) {
					relayoutFrame(frame);
				}
			}			
		}

		@Override
		public void activateFrame(JInternalFrame f) {
			if (f == null) {
				return;
			}
			// TODO Auto-generated method stub
			super.activateFrame(f);
		}
		
		
	}
	
	private InternalDesktopManager internalDesktopManager = new InternalDesktopManager();
	
	/**
	 * JDesktopPaneEnhanced constructor comment.
	 */
	public JDesktopPaneEnhanced() {
		super();
		addHierarchyBoundsListener(new InternalEventHandler());
		setDesktopManager(internalDesktopManager);
	}
	
	private void relayoutFrame(JInternalFrame frame) {				
		Dimension paneSize = getSize();
		Point componentLocation = frame.getLocation();				
		Dimension componentSize = frame.getSize();
		int newX = Math.max(0, Math.min(componentLocation.x, paneSize.width - componentSize.width));	
		int newY = Math.max(0, Math.min(componentLocation.y, paneSize.height - componentSize.height));
		if (newX != componentLocation.x || newY != componentLocation.y) {
			frame.setLocation(newX, newY);
		}
	}
}
