package cbit.vcell.client.desktop.geometry;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.gui.GeometryViewer;


/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 11:48:54 PM)
 * @author: Ion Moraru
 */
public class GeometrySummaryViewer extends JPanel {

	//
	public static class GeometrySummaryViewerEvent extends ActionEvent{

		private cbit.vcell.geometry.Geometry geometry;
		
		public GeometrySummaryViewerEvent(cbit.vcell.geometry.Geometry argGeom,Object source, int id, String command,int modifiers) {
			super(source, id, command, modifiers);
			geometry = argGeom;
		}
		public cbit.vcell.geometry.Geometry getGeometry(){
			return geometry;
		}
	}
	private GeometryViewer geometryViewer = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton ivjJButtonReplace = null;
    protected transient ActionListener actionListener = null;
	private JButton ivjJButtonViewSurfaces = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private boolean bStochastic = false;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GeometrySummaryViewer.this.getJButtonReplace()) 
				connEtoC1(e);
			if (e.getSource() == GeometrySummaryViewer.this.getJButtonViewSurfaces()) 
				connEtoC2(e);
			if (e.getSource() == GeometrySummaryViewer.this.getJButtonCreateGeometry()) 
				connEtoC4(e);
//			if (e.getSource() == GeometrySummaryViewer.this.getBtnEditGeometry()){
//				GeometrySummaryViewer.this.refireActionPerformed(e);
//			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometrySummaryViewer.this && (evt.getPropertyName().equals("geometry"))) 
				connEtoM1(evt);
			if (evt.getSource() == GeometrySummaryViewer.this && (evt.getPropertyName().equals("geometry"))) 
				connEtoC3(evt);
			if(evt.getSource() == (getGeometry()!= null?getGeometry().getGeometrySpec():null) &&
					evt.getPropertyName().equals("sampledImage")){
				ActionEvent actionEvent = new ActionEvent(getGeometry(), 0, GuiConstants.ACTIONCMD_EDIT_OCCURRED_GEOMETRY);
				GeometrySummaryViewer.this.refireActionPerformed(actionEvent);
			}
		};
	};
	private JButton ivjJButtonCreateGeometry = null;
	private JButton btnEditGeometry;

public GeometrySummaryViewer() {
	super();
	initialize();
}

public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
}


/**
 * connEtoC1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySummaryViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonViewSurfaces.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySummaryViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (GeometrySummaryViewer.geometry --> GeometrySummaryViewer.initSurfaceButton()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.initButtons();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySummaryViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (GeometrySummaryViewer.geometry --> GeometrySummaryPanel1.geometry)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getGeometryViewer().setGeometry(this.getGeometry());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


protected void fireActionPerformed(ActionEvent e) {
	if (actionListener != null) {
		actionListener.actionPerformed(e);
	}         
}


/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.geometry.Geometry getGeometry() {
	return fieldGeometry;
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
			ivjJButtonReplace.setText("Replace Geometry...");
			ivjJButtonReplace.setActionCommand(GuiConstants.ACTIONCMD_CHANGE_GEOMETRY);
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
private javax.swing.JButton getJButtonCreateGeometry() {
	if (ivjJButtonCreateGeometry == null) {
		try {
			ivjJButtonCreateGeometry = new javax.swing.JButton();
			ivjJButtonCreateGeometry.setName("JButtonCreateGeometry");
			ivjJButtonCreateGeometry.setText("Create New Geometry...");
			ivjJButtonCreateGeometry.setActionCommand(GuiConstants.ACTIONCMD_CREATE_GEOMETRY);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCreateGeometry;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonViewSurfaces() {
	if (ivjJButtonViewSurfaces == null) {
		try {
			ivjJButtonViewSurfaces = new javax.swing.JButton();
			ivjJButtonViewSurfaces.setName("JButtonViewSurfaces");
			ivjJButtonViewSurfaces.setText("View Surfaces...");
			ivjJButtonViewSurfaces.setActionCommand(GuiConstants.ACTIONCMD_VIEW_SURFACES);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonViewSurfaces;
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
	getJButtonViewSurfaces().addActionListener(ivjEventHandler);
	getJButtonCreateGeometry().addActionListener(ivjEventHandler);
//	getBtnEditGeometry().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
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
		setSize(877, 568);

		java.awt.GridBagConstraints constraintsGeometrySummaryPanel1 = new java.awt.GridBagConstraints();
		constraintsGeometrySummaryPanel1.gridx = 0; constraintsGeometrySummaryPanel1.gridy = 0;
		constraintsGeometrySummaryPanel1.gridwidth = 4;
		constraintsGeometrySummaryPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGeometrySummaryPanel1.weightx = 1.0;
		constraintsGeometrySummaryPanel1.weighty = 1.0;
		constraintsGeometrySummaryPanel1.insets = new Insets(4, 4, 5, 4);
		add(getGeometryViewer(), constraintsGeometrySummaryPanel1);
//		GridBagConstraints gbc_btnEditGeometry = new GridBagConstraints();
//		gbc_btnEditGeometry.insets = new Insets(0, 0, 0, 5);
//		gbc_btnEditGeometry.gridx = 1;
//		gbc_btnEditGeometry.gridy = 1;
//		add(getBtnEditGeometry(), gbc_btnEditGeometry);

		java.awt.GridBagConstraints gbc_ivjJButtonReplace = new java.awt.GridBagConstraints();
		gbc_ivjJButtonReplace.gridx = 0; gbc_ivjJButtonReplace.gridy = 1;
		gbc_ivjJButtonReplace.insets = new Insets(4, 4, 4, 5);
		add(getJButtonReplace(), gbc_ivjJButtonReplace);

		java.awt.GridBagConstraints constraintsJButtonViewSurfaces = new java.awt.GridBagConstraints();
		constraintsJButtonViewSurfaces.anchor = GridBagConstraints.EAST;
		constraintsJButtonViewSurfaces.weightx = 1.0;
		constraintsJButtonViewSurfaces.gridx = 2; constraintsJButtonViewSurfaces.gridy = 1;
		constraintsJButtonViewSurfaces.insets = new Insets(4, 4, 4, 5);
		add(getJButtonViewSurfaces(), constraintsJButtonViewSurfaces);

		java.awt.GridBagConstraints gbc_ivjJButtonCreateGeometry = new java.awt.GridBagConstraints();
		gbc_ivjJButtonCreateGeometry.gridx = 1; gbc_ivjJButtonCreateGeometry.gridy = 1;
		gbc_ivjJButtonCreateGeometry.anchor = java.awt.GridBagConstraints.WEST;
		gbc_ivjJButtonCreateGeometry.weightx = 1.0;
		gbc_ivjJButtonCreateGeometry.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButtonCreateGeometry(), gbc_ivjJButtonCreateGeometry);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void initButtons() {
	boolean bSpatial = (getGeometry() != null) && (getGeometry().getDimension() > 0);
	getJButtonViewSurfaces().setEnabled(bSpatial);
//	getBtnEditGeometry().setEnabled(bSpatial && !bStochastic);
	getJButtonReplace().setEnabled(!bStochastic);
	getJButtonCreateGeometry().setEnabled(!bStochastic);
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
		frame.setVisible(true);
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
	//fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
	fireActionPerformed(new GeometrySummaryViewerEvent(getGeometry(),this, e.getID(), e.getActionCommand(), e.getModifiers()));
}


public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
}


/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) {
	cbit.vcell.geometry.Geometry oldValue = fieldGeometry;
	if(oldValue != null){
//		oldValue.removePropertyChangeListener(ivjEventHandler);
		if(oldValue.getGeometrySpec() != null){
			oldValue.getGeometrySpec().removePropertyChangeListener(ivjEventHandler);
//			SubVolume subVolumes[] = oldValue.getGeometrySpec().getSubVolumes();
//			for (int i = 0;subVolumes!=null && i < subVolumes.length; i++){
//				subVolumes[i].removePropertyChangeListener(ivjEventHandler);
//			}
		}
	}
	fieldGeometry = geometry;
	if(fieldGeometry != null ){
//		fieldGeometry.removePropertyChangeListener(ivjEventHandler);
//		fieldGeometry.addPropertyChangeListener(ivjEventHandler);
		if(fieldGeometry.getGeometrySpec() != null){
			fieldGeometry.getGeometrySpec().removePropertyChangeListener(ivjEventHandler);
			fieldGeometry.getGeometrySpec().addPropertyChangeListener(ivjEventHandler);
//			SubVolume subVolumes[] = fieldGeometry.getGeometrySpec().getSubVolumes();
//			for (int i = 0;subVolumes!=null && i < subVolumes.length; i++){
//				subVolumes[i].removePropertyChangeListener(ivjEventHandler);
//				subVolumes[i].addPropertyChangeListener(ivjEventHandler);
//			}
		}
	}

	firePropertyChange("geometry", oldValue, geometry);
}

public void setStochastic(boolean bStochastic){
	this.bStochastic = bStochastic;
	initButtons();
}

}