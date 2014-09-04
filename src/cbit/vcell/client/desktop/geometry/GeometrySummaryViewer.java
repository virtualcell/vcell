/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.geometry;
import java.awt.AWTEventMulticaster;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.vcell.util.gui.DownArrowIcon;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.geometry.gui.GeometryViewer;


/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 11:48:54 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class GeometrySummaryViewer extends JPanel {

	//
//	public static class GeometrySummaryViewerEvent extends ActionEvent {
//
//		private Geometry geometry;
//		
//		public GeometrySummaryViewerEvent(Geometry argGeom,Object source, int id, String command,int modifiers) {
//			super(source, id, command, modifiers);
//			geometry = argGeom;
//		}
//		public Geometry getGeometry(){
//			return geometry;
//		}
//	}
	private GeometryViewer geometryViewer = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton ivjJButtonReplace = null;
    protected transient ActionListener actionListener = null;
	private GeometryOwner geometryOwner = null;
	private JMenuItem newGeometryMenuItem = null;
	private JMenuItem existingGeometryMenuItem = null;
	private JPopupMenu popupMenu = null;

	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == newGeometryMenuItem
					|| e.getSource() == existingGeometryMenuItem) {
				refireActionPerformed(e);
			} else if (e.getSource() == getJButtonReplace()) {
				getPopupMenu().show(getJButtonReplace(), getJButtonReplace().getWidth()/2, getJButtonReplace().getHeight());
			}
		}
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometrySummaryViewer.this && (evt.getPropertyName().equals("geometryOwner"))) {
				onGeometryOwnerChange(evt);
			}
			if (evt.getSource() == geometryOwner && evt.getPropertyName().equals("geometry")) {
				onGeometryChange();
			}
		}
	}

public GeometrySummaryViewer() {
	super();
	initialize();
}

public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
}

protected void fireActionPerformed(ActionEvent e) {
	if (actionListener != null) {
		actionListener.actionPerformed(e);
	}         
}

/**
 * Return the GeometrySummaryPanel1 property value.
 * @return cbit.vcell.geometry.gui.GeometrySummaryPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryViewer getGeometryViewer() {
	if (geometryViewer == null) {
		try {
			geometryViewer = new GeometryViewer();
			geometryViewer.setName("GeometryViewer");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return geometryViewer;
}


public static final String REPLACE_GEOMETRY_SPATIAL_LABEL = "Select Different Geometry";
public static final String REPLACE_GEOMETRY_NONSPATIAL_LABEL = "Add Geometry";
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonReplace() {
	if (ivjJButtonReplace == null) {
		try {
			ivjJButtonReplace = new javax.swing.JButton();
			ivjJButtonReplace.setName("JButton1");
			ivjJButtonReplace.setText(REPLACE_GEOMETRY_SPATIAL_LABEL);
			ivjJButtonReplace.setIcon(new DownArrowIcon());
			ivjJButtonReplace.setHorizontalTextPosition(SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonReplace;
}


/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JPopupMenu getPopupMenu() {
	if (popupMenu == null) {
		try {
			popupMenu = new JPopupMenu();
			newGeometryMenuItem = new JMenuItem("New...");
			newGeometryMenuItem.setActionCommand(GuiConstants.ACTIONCMD_CREATE_GEOMETRY);
			newGeometryMenuItem.addActionListener(ivjEventHandler);
			existingGeometryMenuItem = new JMenuItem("Open...");
			existingGeometryMenuItem.setActionCommand(GuiConstants.ACTIONCMD_CHANGE_GEOMETRY);
			existingGeometryMenuItem.addActionListener(ivjEventHandler);
			popupMenu.add(newGeometryMenuItem);
			popupMenu.add(existingGeometryMenuItem);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return popupMenu;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonReplace().addActionListener(ivjEventHandler);
//	getBtnEditGeometry().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getGeometryViewer().addPropertyChangeListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GeometrySummaryViewer");
		setLayout(new java.awt.GridBagLayout());
		//setSize(877, 568);

		java.awt.GridBagConstraints constraintsGeometrySummaryPanel1 = new java.awt.GridBagConstraints();
		constraintsGeometrySummaryPanel1.gridx = 0; constraintsGeometrySummaryPanel1.gridy = 0;
		constraintsGeometrySummaryPanel1.gridwidth = 4;
		constraintsGeometrySummaryPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGeometrySummaryPanel1.weightx = 1.0;
		constraintsGeometrySummaryPanel1.weighty = 1.0;
		constraintsGeometrySummaryPanel1.insets = new Insets(4, 4, 5, 4);
		add(getGeometryViewer(), constraintsGeometrySummaryPanel1);

		java.awt.GridBagConstraints gbc_ivjJButtonReplace = new java.awt.GridBagConstraints();
		gbc_ivjJButtonReplace.gridx = 0; gbc_ivjJButtonReplace.gridy = 1;
		gbc_ivjJButtonReplace.weightx = 1.0;
		gbc_ivjJButtonReplace.anchor = GridBagConstraints.LINE_START;
		gbc_ivjJButtonReplace.insets = new Insets(4, 4, 4, 5);
		add(getJButtonReplace(), gbc_ivjJButtonReplace);

		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		GeometrySummaryViewer aGeometrySummaryViewer;
		aGeometrySummaryViewer = new GeometrySummaryViewer();
		frame.setContentPane(aGeometrySummaryViewer);
		frame.setSize(aGeometrySummaryViewer.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


private void refireActionPerformed(ActionEvent e) {
	// relays an action event with this as the source
	fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
//	fireActionPerformed(new GeometrySummaryViewerEvent(getGeometry(),this, e.getID(), e.getActionCommand(), e.getModifiers()));
}


public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
}

public GeometryOwner getGeometryOwner() {
	return geometryOwner; 
}
/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometryOwner(GeometryOwner newValue) {
	GeometryOwner oldValue = geometryOwner;
	geometryOwner = newValue;
	firePropertyChange("geometryOwner", oldValue, newValue);
}

private void onGeometryOwnerChange(PropertyChangeEvent evt) {
	GeometryOwner oldValue = (GeometryOwner) evt.getOldValue();
	if (oldValue != null){
		oldValue.removePropertyChangeListener(ivjEventHandler);
		if (oldValue.getGeometry() != null && oldValue.getGeometry().getGeometrySpec() != null){
			oldValue.getGeometry().getGeometrySpec().removePropertyChangeListener(ivjEventHandler);
		}
	}
	GeometryOwner newValue = (GeometryOwner) evt.getNewValue();
	if (newValue != null ){
		newValue.addPropertyChangeListener(ivjEventHandler);
		if (newValue.getGeometry() != null && newValue.getGeometry().getGeometrySpec() != null){		
			newValue.getGeometry().getGeometrySpec().removePropertyChangeListener(ivjEventHandler);
			newValue.getGeometry().getGeometrySpec().addPropertyChangeListener(ivjEventHandler);			
		}
	}
	onGeometryChange();
}
private void onGeometryChange() {
	if (geometryOwner.getGeometry() == null || geometryOwner.getGeometry().getDimension() < 1){
		getGeometryViewer().setVisible(false);
		getJButtonReplace().setText(REPLACE_GEOMETRY_NONSPATIAL_LABEL);
	} else {	
		getJButtonReplace().setText(REPLACE_GEOMETRY_SPATIAL_LABEL);
		getGeometryViewer().setVisible(true);
	}
	getGeometryViewer().setGeometry(geometryOwner.getGeometry());
}
}
