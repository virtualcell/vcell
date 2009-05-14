package cbit.vcell.client.desktop.geometry;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;


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
	private cbit.vcell.geometry.gui.GeometrySummaryPanel ivjGeometrySummaryPanel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton ivjJButton1 = null;
    protected transient ActionListener actionListener = null;
	private JButton ivjJButtonViewSurfaces = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GeometrySummaryViewer.this.getJButton1()) 
				connEtoC1(e);
			if (e.getSource() == GeometrySummaryViewer.this.getJButtonViewSurfaces()) 
				connEtoC2(e);
			if (e.getSource() == GeometrySummaryViewer.this.getJButtonOpenGeometry()) 
				connEtoC4(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometrySummaryViewer.this && (evt.getPropertyName().equals("geometry"))) 
				connEtoM1(evt);
			if (evt.getSource() == GeometrySummaryViewer.this && (evt.getPropertyName().equals("geometry"))) 
				connEtoC3(evt);
		};
	};
	private JButton ivjJButtonOpenGeometry = null;

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
		this.initSurfaceButton();
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
		getGeometrySummaryPanel1().setGeometry(this.getGeometry());
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
private cbit.vcell.geometry.gui.GeometrySummaryPanel getGeometrySummaryPanel1() {
	if (ivjGeometrySummaryPanel1 == null) {
		try {
			ivjGeometrySummaryPanel1 = new cbit.vcell.geometry.gui.GeometrySummaryPanel();
			ivjGeometrySummaryPanel1.setName("GeometrySummaryPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometrySummaryPanel1;
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("Change Geometry...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton1;
}


/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOpenGeometry() {
	if (ivjJButtonOpenGeometry == null) {
		try {
			ivjJButtonOpenGeometry = new javax.swing.JButton();
			ivjJButtonOpenGeometry.setName("JButtonOpenGeometry");
			ivjJButtonOpenGeometry.setText("Open Geometry");
			ivjJButtonOpenGeometry.setActionCommand("Open Geometry");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOpenGeometry;
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
			ivjJButtonViewSurfaces.setText("View Surfaces");
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
	getJButton1().addActionListener(ivjEventHandler);
	getJButtonViewSurfaces().addActionListener(ivjEventHandler);
	getJButtonOpenGeometry().addActionListener(ivjEventHandler);
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
		setSize(877, 471);

		java.awt.GridBagConstraints constraintsGeometrySummaryPanel1 = new java.awt.GridBagConstraints();
		constraintsGeometrySummaryPanel1.gridx = 0; constraintsGeometrySummaryPanel1.gridy = 0;
		constraintsGeometrySummaryPanel1.gridwidth = 3;
		constraintsGeometrySummaryPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGeometrySummaryPanel1.weightx = 1.0;
		constraintsGeometrySummaryPanel1.weighty = 1.0;
		constraintsGeometrySummaryPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getGeometrySummaryPanel1(), constraintsGeometrySummaryPanel1);

		java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
		constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 1;
		constraintsJButton1.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButton1.weightx = 1.0;
		constraintsJButton1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButton1(), constraintsJButton1);

		java.awt.GridBagConstraints constraintsJButtonViewSurfaces = new java.awt.GridBagConstraints();
		constraintsJButtonViewSurfaces.gridx = 1; constraintsJButtonViewSurfaces.gridy = 1;
		constraintsJButtonViewSurfaces.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButtonViewSurfaces(), constraintsJButtonViewSurfaces);

		java.awt.GridBagConstraints constraintsJButtonOpenGeometry = new java.awt.GridBagConstraints();
		constraintsJButtonOpenGeometry.gridx = 2; constraintsJButtonOpenGeometry.gridy = 1;
		constraintsJButtonOpenGeometry.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonOpenGeometry.weightx = 1.0;
		constraintsJButtonOpenGeometry.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButtonOpenGeometry(), constraintsJButtonOpenGeometry);
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
private void initSurfaceButton() {
	boolean bSpatial = (getGeometry() != null) && (getGeometry().getDimension() > 0);
	getJButtonViewSurfaces().setEnabled(bSpatial);
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
	fieldGeometry = geometry;
	firePropertyChange("geometry", oldValue, geometry);
}

public void setChangeGeometryEnabled(boolean enabledOrNot)
{
	getJButton1().setEnabled(enabledOrNot);
}

public void setOpenGeometryEnabled(boolean enabledOrNot)
{
	getJButtonOpenGeometry().setEnabled(enabledOrNot);
}

}