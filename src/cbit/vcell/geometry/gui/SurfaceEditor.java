package cbit.vcell.geometry.gui;
import org.vcell.util.UserCancelException;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.desktop.controls.ClientTask;
/**
 * Insert the type's description here.
 * Creation date: (5/25/2004 4:45:55 PM)
 * @author: Jim Schaff
 */
public class SurfaceEditor extends javax.swing.JPanel {
	private javax.swing.JPanel ivjJPanel1 = null;
	private SurfaceCanvas ivjSurfaceCanvas1 = null;
	private javax.swing.JLabel ivjJLabel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JSlider ivjJSlider1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabel3 = null;
	private boolean ivjConnPtoP2Aligning = false;
	private javax.swing.BoundedRangeModel ivjmodel1 = null;
	private SurfaceViewerTool ivjSurfaceViewerTool1 = null;
	private cbit.vcell.geometry.surface.GeometrySurfaceDescription fieldGeometrySurfaceDescription = null;
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.vcell.geometry.surface.GeometrySurfaceDescription ivjgeometrySurfaceDescription1 = null;
	private cbit.vcell.geometry.surface.SurfaceCollection ivjsurfaceCollection = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private javax.swing.JButton ivjApplyButton = null;
	private java.awt.BorderLayout ivjSurfaceEditorBorderLayout = null;
	private ResolvedLocationTablePanel ivjResolvedLocationTablePanel1 = null;
	private javax.swing.JButton ivjhomeButton = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SurfaceEditor.this.gethomeButton()) 
				connEtoM5(e);
			if (e.getSource() == SurfaceEditor.this.getApplyButton()) 
				connEtoC1(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SurfaceEditor.this.getJSlider1() && (evt.getPropertyName().equals("model"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == SurfaceEditor.this && (evt.getPropertyName().equals("geometrySurfaceDescription"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == SurfaceEditor.this.getgeometrySurfaceDescription1() && (evt.getPropertyName().equals("surfaceCollection"))) 
				connEtoM2(evt);
			if (evt.getSource() == SurfaceEditor.this.getgeometrySurfaceDescription1() && (evt.getPropertyName().equals("filterCutoffFrequency"))) 
				connEtoM8(evt);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == SurfaceEditor.this.getmodel1()) 
				connEtoM7(e);
		};
	};

/**
 * SurfaceEditor constructor comment.
 */
public SurfaceEditor() {
	super();
	initialize();
}


/**
 * connEtoC1:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> SurfaceEditor.updateSurface()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSurface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (geometrySurfaceDescription1.this --> SurfaceEditor.onNewGeometrySurfaceDescription()V)
 * @param value cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(cbit.vcell.geometry.surface.GeometrySurfaceDescription value) {
	try {
		// user code begin {1}
		// user code end
		this.onNewGeometrySurfaceDescription();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (surfaceCollection.this --> SurfaceEditor.onNewSurfaceCollection(Lcbit.vcell.geometry.surface.SurfaceCollection;)V)
 * @param value cbit.vcell.geometry.surface.SurfaceCollection
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(cbit.vcell.geometry.surface.SurfaceCollection value) {
	try {
		// user code begin {1}
		// user code end
		this.onNewSurfaceCollection(getsurfaceCollection());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (geometrySurfaceDescription1.this --> surfaceCollection.this)
 * @param value cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.geometry.surface.GeometrySurfaceDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getgeometrySurfaceDescription1() != null)) {
			setsurfaceCollection(getgeometrySurfaceDescription1().getSurfaceCollection());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (geometrySurfaceDescription1.surfaceCollection --> surfaceCollection.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getgeometrySurfaceDescription1() != null)) {
			setsurfaceCollection(getgeometrySurfaceDescription1().getSurfaceCollection());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (SurfaceEditor.initialize() --> SurfaceViewerTool1.surfaceCanvas)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getSurfaceViewerTool1().setSurfaceCanvas(getSurfaceCanvas1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (model1.change. --> JLabel3.text)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(cbit.vcell.geometry.surface.GeometrySurfaceDescription value) {
	try {
		// user code begin {1}
		// user code end
		getmodel1().setValue(this.getSliderValueFromTaubinParameter((getgeometrySurfaceDescription1().getFilterCutoffFrequency()).doubleValue()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> SurfaceViewerTool1.resetView()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSurfaceViewerTool1().resetView();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (geometrySurfaceDescription1.this --> model1.value)
 * @param value cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.geometry.surface.GeometrySurfaceDescription value) {
	try {
		// user code begin {1}
		// user code end
		getmodel1().setValue(this.connEtoM6_Value());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private int connEtoM6_Value() {
	
	if(getgeometrySurfaceDescription1() == null){
		return (getmodel1().getMinimum()+getmodel1().getMaximum())/2;
	}
	
	return this.getSliderValueFromTaubinParameter((getgeometrySurfaceDescription1().getFilterCutoffFrequency()).doubleValue());
}


/**
 * connEtoM7:  (model1.change.stateChanged(javax.swing.event.ChangeEvent) --> geometrySurfaceDescription1.filterCutoffFrequency)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJLabel3().setText(String.valueOf(this.getTaubinParameterFromSliderValue()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (geometrySurfaceDescription1.filterCutoffFrequency --> JLabel3.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getmodel1().setValue(this.getSliderValueFromTaubinParameter((getgeometrySurfaceDescription1().getFilterCutoffFrequency()).doubleValue()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM9:  (geometrySurfaceDescription1.this --> ResolvedLocationTablePanel1.geometrySurfaceDescription)
 * @param value cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(cbit.vcell.geometry.surface.GeometrySurfaceDescription value) {
	try {
		// user code begin {1}
		// user code end
		getResolvedLocationTablePanel1().setGeometrySurfaceDescription(getgeometrySurfaceDescription1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (SurfaceEditor.geometrySurfaceDescription <--> geometrySurfaceDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getgeometrySurfaceDescription1() != null)) {
				this.setGeometrySurfaceDescription(getgeometrySurfaceDescription1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (SurfaceEditor.geometrySurfaceDescription <--> geometrySurfaceDescription1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setgeometrySurfaceDescription1(this.getGeometrySurfaceDescription());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (JSlider1.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getmodel1() != null)) {
				getJSlider1().setModel(getmodel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (JSlider1.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setmodel1(getJSlider1().getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getApplyButton() {
	if (ivjApplyButton == null) {
		try {
			ivjApplyButton = new javax.swing.JButton();
			ivjApplyButton.setName("ApplyButton");
			ivjApplyButton.setText("Apply");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjApplyButton;
}


/**
 * Gets the geometrySurfaceDescription property (cbit.vcell.geometry.surface.GeometrySurfaceDescription) value.
 * @return The geometrySurfaceDescription property value.
 * @see #setGeometrySurfaceDescription
 */
public cbit.vcell.geometry.surface.GeometrySurfaceDescription getGeometrySurfaceDescription() {
	return fieldGeometrySurfaceDescription;
}


/**
 * Return the geometrySurfaceDescription1 property value.
 * @return cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.surface.GeometrySurfaceDescription getgeometrySurfaceDescription1() {
	// user code begin {1}
	// user code end
	return ivjgeometrySurfaceDescription1;
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton gethomeButton() {
	if (ivjhomeButton == null) {
		try {
			ivjhomeButton = new javax.swing.JButton();
			ivjhomeButton.setName("homeButton");
			ivjhomeButton.setText("home");
			ivjhomeButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjhomeButton;
}


/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel() {
	if (ivjJLabel == null) {
		try {
			ivjJLabel = new javax.swing.JLabel();
			ivjJLabel.setName("JLabel");
			ivjJLabel.setText("more");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("smoothing");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("less");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}


/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("0.3");
			ivjJLabel3.setMaximumSize(new java.awt.Dimension(60, 14));
			ivjJLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel3.setPreferredSize(new java.awt.Dimension(60, 14));
			ivjJLabel3.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjJLabel3.setMinimumSize(new java.awt.Dimension(60, 14));
			ivjJLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintshomeButton = new java.awt.GridBagConstraints();
			constraintshomeButton.gridx = 0; constraintshomeButton.gridy = 0;
			constraintshomeButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintshomeButton.anchor = java.awt.GridBagConstraints.NORTH;
			constraintshomeButton.weighty = 1.0;
			getJPanel1().add(gethomeButton(), constraintshomeButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJSlider1 = new java.awt.GridBagConstraints();
			constraintsJSlider1.gridx = 1; constraintsJSlider1.gridy = 0;
			constraintsJSlider1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJSlider1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJSlider1(), constraintsJSlider1);

			java.awt.GridBagConstraints constraintsApplyButton = new java.awt.GridBagConstraints();
			constraintsApplyButton.gridx = 2; constraintsApplyButton.gridy = 0;
			constraintsApplyButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsApplyButton.weightx = 1.0;
			constraintsApplyButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getApplyButton(), constraintsApplyButton);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabel1.weightx = 1.0;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
			constraintsJPanel3.gridx = 1; constraintsJPanel3.gridy = 1;
			constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJPanel3(), constraintsJPanel3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}


/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setPreferredSize(new java.awt.Dimension(20, 20));
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 1; constraintsJLabel3.gridy = 0;
			constraintsJLabel3.weightx = 1.0;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
			constraintsJLabel.gridx = 2; constraintsJLabel.gridy = 0;
			constraintsJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel(), constraintsJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}


/**
 * Return the JSlider1 property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getJSlider1() {
	if (ivjJSlider1 == null) {
		try {
			ivjJSlider1 = new javax.swing.JSlider();
			ivjJSlider1.setName("JSlider1");
			ivjJSlider1.setMajorTickSpacing(2);
			ivjJSlider1.setMaximum(58);
			ivjJSlider1.setMinimum(0);
			ivjJSlider1.setValue(29);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSlider1;
}


/**
 * Return the model1 property value.
 * @return javax.swing.BoundedRangeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.BoundedRangeModel getmodel1() {
	// user code begin {1}
	// user code end
	return ivjmodel1;
}


/**
 * Return the ResolvedLocationTablePanel1 property value.
 * @return cbit.vcell.geometry.gui.ResolvedLocationTablePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ResolvedLocationTablePanel getResolvedLocationTablePanel1() {
	if (ivjResolvedLocationTablePanel1 == null) {
		try {
			ivjResolvedLocationTablePanel1 = new cbit.vcell.geometry.gui.ResolvedLocationTablePanel();
			ivjResolvedLocationTablePanel1.setName("ResolvedLocationTablePanel1");
			ivjResolvedLocationTablePanel1.setPreferredSize(new java.awt.Dimension(300, 100));
			ivjResolvedLocationTablePanel1.setMinimumSize(new java.awt.Dimension(100, 100));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResolvedLocationTablePanel1;
}


/**
 * Comment
 */
private int getSliderValueFromTaubinParameter(double cutoffFrequency) {	
	//
	// normalizedSliderValue = (value-minimum)/(maximum-minimum)
	// cutoffFrequency = 0.6 - (normalizedSliderValue*0.58)
	//

	double normalizedSliderValue = - (cutoffFrequency - 0.6)/0.58;
	int min = getmodel1().getMinimum();
	int max = getmodel1().getMaximum();
	int value = (int)Math.round(normalizedSliderValue*(max-min) + min);
	
	return value;
}


/**
 * Return the SurfaceCanvas1 property value.
 * @return cbit.vcell.geometry.gui.SurfaceCanvas
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SurfaceCanvas getSurfaceCanvas1() {
	if (ivjSurfaceCanvas1 == null) {
		try {
			ivjSurfaceCanvas1 = new cbit.vcell.geometry.gui.SurfaceCanvas();
			ivjSurfaceCanvas1.setName("SurfaceCanvas1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSurfaceCanvas1;
}


/**
 * Return the surfaceCollection property value.
 * @return cbit.vcell.geometry.surface.SurfaceCollection
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.surface.SurfaceCollection getsurfaceCollection() {
	// user code begin {1}
	// user code end
	return ivjsurfaceCollection;
}


/**
 * Return the SurfaceEditorBorderLayout property value.
 * @return java.awt.BorderLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.BorderLayout getSurfaceEditorBorderLayout() {
	java.awt.BorderLayout ivjSurfaceEditorBorderLayout = null;
	try {
		/* Create part */
		ivjSurfaceEditorBorderLayout = new java.awt.BorderLayout();
		ivjSurfaceEditorBorderLayout.setVgap(6);
		ivjSurfaceEditorBorderLayout.setHgap(6);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjSurfaceEditorBorderLayout;
}


/**
 * Return the SurfaceViewerTool1 property value.
 * @return cbit.vcell.geometry.gui.SurfaceViewerTool
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SurfaceViewerTool getSurfaceViewerTool1() {
	if (ivjSurfaceViewerTool1 == null) {
		try {
			ivjSurfaceViewerTool1 = new cbit.vcell.geometry.gui.SurfaceViewerTool();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSurfaceViewerTool1;
}


/**
 * Comment
 */
private double getTaubinParameterFromSliderValue() {
	//
	// map slider value to scale
	//
	// normalizedSliderValue = (value-minimum)/(maximum-minimum)
	// cutoffFrequency = 0.6 - (normalizedSliderValue*0.58)
	//
	//
	double minimum = getJSlider1().getMinimum();
	double maximum = getJSlider1().getMaximum();
	double value = getJSlider1().getValue();
	value = (value-minimum)/(maximum-minimum);  // normalized (0,1)
	double cutoffFrequency = 0.6 - (value*0.58); //0.6*Math.exp(-value*3.4);
	//System.out.println("cutoff = "+cutoffFrequency);
	return Math.round(cutoffFrequency*100.0)/100.0;
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
	getJSlider1().addPropertyChangeListener(ivjEventHandler);
	gethomeButton().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getApplyButton().addActionListener(ivjEventHandler);
	connPtoP2SetTarget();
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SurfaceEditor");
		setLayout(getSurfaceEditorBorderLayout());
		setSize(543, 572);
		add(getSurfaceCanvas1(), "Center");
		add(getJPanel1(), "West");
		add(getJPanel2(), "North");
		add(getResolvedLocationTablePanel1(), "South");
		initConnections();
		connEtoM3();
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SurfaceEditor aSurfaceEditor;
		aSurfaceEditor = new SurfaceEditor();
		frame.setContentPane(aSurfaceEditor);
		frame.setSize(aSurfaceEditor.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void onNewGeometrySurfaceDescription() {
	resetView();
}


/**
 * Comment
 */
private void onNewSurfaceCollection(SurfaceCollection argSurfaceCollection) {
	getSurfaceViewerTool1().setDimension(new Integer(getGeometrySurfaceDescription().getGeometry().getDimension()));
	getSurfaceCanvas1().setOrigin(getGeometrySurfaceDescription().getGeometry().getOrigin());
	getSurfaceCanvas1().setExtent(getGeometrySurfaceDescription().getGeometry().getExtent());
	getSurfaceCanvas1().setSurfaceCollection(argSurfaceCollection);
	
	if(getGeometrySurfaceDescription().getGeometry().getDimension() < 3){
		if(argSurfaceCollection != null){
			boolean[] bWireframe = new boolean[argSurfaceCollection.getSurfaceCount()];
			java.util.Arrays.fill(bWireframe,true);
			getSurfaceCanvas1().setSurfacesWireframe(bWireframe);
		}else{
			getSurfaceCanvas1().setSurfacesWireframe(null);
		}
		getSurfaceCanvas1().setEnableDepthCueing(false);
	}else{
		getSurfaceCanvas1().setSurfacesWireframe(null);
		getSurfaceCanvas1().setEnableDepthCueing(true);
	}

	//Set Geometry handle colors
	if(argSurfaceCollection != null){
		java.awt.image.IndexColorModel colorModel = (java.awt.image.IndexColorModel)cbit.vcell.geometry.GeometrySpec.getHandleColorMap();
		int[][] surfaceColors = new int[argSurfaceCollection.getSurfaceCount()][1];
		final int colorOffset = 1;
		for (int i = 0; i < argSurfaceCollection.getSurfaceCount(); i++){
			surfaceColors[i][0] =
				0x00000000 |
				(0x00FF0000 & (colorModel.getRed(Math.min(255, i+colorOffset))<<16)) |
				(0x0000FF00 & (colorModel.getGreen(Math.min(255, i+colorOffset))<<8)) |
				(0x000000FF & (colorModel.getBlue(Math.min(255, i+colorOffset))));
		}
		getSurfaceCanvas1().setSurfacesColors(surfaceColors);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2005 2:12:55 PM)
 */
public void resetView() {
	
	getSurfaceViewerTool1().resetView();	
	
}


/**
 * Sets the geometrySurfaceDescription property (cbit.vcell.geometry.surface.GeometrySurfaceDescription) value.
 * @param geometrySurfaceDescription The new value for the property.
 * @see #getGeometrySurfaceDescription
 */
public void setGeometrySurfaceDescription(cbit.vcell.geometry.surface.GeometrySurfaceDescription geometrySurfaceDescription) {
	cbit.vcell.geometry.surface.GeometrySurfaceDescription oldValue = fieldGeometrySurfaceDescription;
	fieldGeometrySurfaceDescription = geometrySurfaceDescription;
	firePropertyChange("geometrySurfaceDescription", oldValue, geometrySurfaceDescription);
}


/**
 * Set the geometrySurfaceDescription1 to a new value.
 * @param newValue cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometrySurfaceDescription1(cbit.vcell.geometry.surface.GeometrySurfaceDescription newValue) {
	if (ivjgeometrySurfaceDescription1 != newValue) {
		try {
			cbit.vcell.geometry.surface.GeometrySurfaceDescription oldValue = getgeometrySurfaceDescription1();
			/* Stop listening for events from the current object */
			if (ivjgeometrySurfaceDescription1 != null) {
				ivjgeometrySurfaceDescription1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjgeometrySurfaceDescription1 = newValue;

			/* Listen for events from the new object */
			if (ivjgeometrySurfaceDescription1 != null) {
				ivjgeometrySurfaceDescription1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM1(ivjgeometrySurfaceDescription1);
			connEtoM6(ivjgeometrySurfaceDescription1);
			connEtoM9(ivjgeometrySurfaceDescription1);
			connEtoC2(ivjgeometrySurfaceDescription1);
			firePropertyChange("geometrySurfaceDescription", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Set the model1 to a new value.
 * @param newValue javax.swing.BoundedRangeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmodel1(javax.swing.BoundedRangeModel newValue) {
	if (ivjmodel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjmodel1 != null) {
				ivjmodel1.removeChangeListener(ivjEventHandler);
			}
			ivjmodel1 = newValue;

			/* Listen for events from the new object */
			if (ivjmodel1 != null) {
				ivjmodel1.addChangeListener(ivjEventHandler);
			}
			connPtoP2SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Set the surfaceCollection to a new value.
 * @param newValue cbit.vcell.geometry.surface.SurfaceCollection
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsurfaceCollection(cbit.vcell.geometry.surface.SurfaceCollection newValue) {
	if (ivjsurfaceCollection != newValue) {
		try {
			ivjsurfaceCollection = newValue;
			connEtoC3(ivjsurfaceCollection);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Comment
 */
public void updateSurface() throws java.beans.PropertyVetoException, cbit.image.ImageException, cbit.vcell.geometry.GeometryException, cbit.vcell.parser.ExpressionException {
	if (getGeometrySurfaceDescription()==null){
		javax.swing.SwingUtilities.invokeLater(new Runnable (){
			public void run(){
				getSurfaceCanvas1().setSurfaceCollection(null);
				getSurfaceCanvas1().repaint();
			}
		});
		return;
	}
	
	final boolean bResetAfterGenerate = (getGeometrySurfaceDescription().getSurfaceCollection()==null);
	
	getSurfaceCanvas1().setOrigin(getGeometrySurfaceDescription().getGeometry().getOrigin());
	getSurfaceCanvas1().setExtent(getGeometrySurfaceDescription().getGeometry().getExtent());
	getSurfaceViewerTool1().setDimension(new Integer(getGeometrySurfaceDescription().getGeometry().getDimension()));
	getGeometrySurfaceDescription().setFilterCutoffFrequency(new Double(getTaubinParameterFromSliderValue()));
	
	java.util.Hashtable hash = new java.util.Hashtable();

	hash.put("geometrySurfaceDescription",getGeometrySurfaceDescription());
	
	AsynchClientTask repaintViewTask = new AsynchClientTask() {
		public String getTaskName() { return "repainting view"; }
		public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_NONBLOCKING; }
		public void run(java.util.Hashtable hash) throws Exception{
			getSurfaceCanvas1().setDisableRepaint(false);
			if(bResetAfterGenerate){
				getSurfaceViewerTool1().resetView();
			}else{
				getSurfaceViewerTool1().fullRepaint();
				//getSurfaceCanvas1().repaint();
			}
		}
		public boolean skipIfAbort() {
			return false;
		}
		public boolean skipIfCancel(UserCancelException e) {
			return false;
		}
	};

	AsynchClientTask tasks[] = null;
	tasks = new AsynchClientTask[] { new SurfaceGenerationTask(),repaintViewTask};

	getSurfaceCanvas1().setDisableRepaint(true);

	cbit.vcell.client.task.ClientTaskDispatcher.dispatch(this, hash, tasks, false);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC5FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BD8DD8DC551528C9C4133A59FE58E20B5DB445EE2A5852CA3FD21BEEE99BEDDA73EDD31ADA34E8D1D99B3769CAD7EAD3456DE30048B4C423C10DB59A2AC9A59511C0B4047CC1A08A898993A004C492F8B0EFE65EB0FF19F9838CBFE14F7DB9EF5E4C3CE1885BF473FD47B7775CF36EB9771CF34F7DF97721D03EFDC9C92121DC908ACB847EEFEF21A0E4D885E1672F1EB4A32E7E6DCC23207977970097
	0403834570EC053A0A9BB20D1B0582D7A9147B20BC30A853788A7C5EA2943A5713D62248D98DF56FAA1FDFB7331C6FF5231CE7E9796656B2F8BE87B0GB872CCF91B487FFA6BD60E1F477188058640D5921A552D5BB8AE8FE8CA81578850DD49689D70E48A4CEF2867F4FD7F3CCC487F115730093423E993E195674D634F8A61294244DDC4569F281E0427924A83G2473C5427041F760F940B4ECBDB4B4A8AB55A3C31243D1ED155CCEC971862A2DFE3921214B6F3508C352D1332C383DC2795FC8FBCDED2514DF
	9032212DA59457E9A3B2FA613787A07EEF08FFEE1860F361F781C079BB582F03EF9F60FA77CEFCC3D8D36BACFEBECA3F2A5EE17D69FEA7582FB8678306D7298F8CBD44560B21AE8DE0A540BA00322ACCE39540EB54774653FF03E75320526CF6BA5D2EA60F6708436CBD61B6CB8E785D50GB59C57A98D393D908B4C3FFFEC2D5251E7A6E0FC1B5AAE170F59E4D99D7ABA25734BC2769D1FB2BC2BE3136CE5930616AAE60B3E2A20CD186C8F234A1E2C8A0D45D059E7842D6C62CD77961568482EF86AE7C50BF9BB
	499A5939D54457F75398D8813F895FC662F70ADFC371156F8671ACAEFBA91E14DB21AE65DD0C0D5E67D01776622542665D1F5635C362779699B6D7B39DAA2B43F529F610363B2B194CDD2AAE47A9BED503E73A1453B8A5653277B20D3DB519C696DF9BFB49B8CD5E1DE94CG4882488358DC8B718550D80BB156F97A3DD94618C974AAC77C3243ACF985463B3167E5F8CAE30AD7ECF3799451B5A47508DED99CF4C8BE01531477909B8C06703DCAE3FF88E4FCA439A42F2848EE9734AE4270153CBEC673E64F8B04
	46AB515AEE59A901G27C740F67F5633851E8E51271C7618C5C58AD2307A5A9E6213913187DA048660B733CB7DD9E22F81687F98C03CEA075BE9FB47A52FCBF2B4B4343B5D43FECFB7690910D5CB6CFC1E660EA4781DD60BB65EF30E603655E59A7754E1BB39BDAFEA5AE91107087A22B700E3EC152D1C448D501B8146G9281B26AB0067E2B97E328E5536DC2554FEE540D218408671CBA96BB6B6B4247527DD4865133E28F789AE0GA0EE8F667F8B36DE92BFF57A63AA96E4F03ED68D7F986FCFB0170CD29946
	B9347F9C9FDB64D9244111F6CB78EFE67F259F93DFAE01FEAD87485E03BE58FE260245749151A7E194F7F8A1GA5F350A3CC4FBD8E1340664937GF6D2FEC1A0F355DE00FDGEF833C84C0666343G5B816AG5A081F81CEGF470B1C86625F7D59B1C22ED6703DE5B5E4FB436GB8G92G1683648214FDG31835087B08FA085E08DC081400E3D1946F6G85A089E085403A3D6843BF8FB796C6375FA64D6FAD9A7D35B45B437A7DF6D89F102E0EBF519E08BF2CA3B75CA6F3717DBF9218AD1DB35AF2236637D1
	E3CFAD8D09C7922BA5EA1B21B6C53A57B47EF9DE038F778D015743FC747FED2BD840465ECB3D24DF520864D2DAA40F64B2438F9F2F5B56CB6C47678B0BDB65F91362BDB03FB4C3C27729784ADEE2C7D1D13C7220DF113AA4C511DD56E0FD8D1565738FFA02D3934A5A5BCBFC619E0579C525BF50CBF273105B6512E892D0D9F87DA1BAB7C88E4989CAB73B7DAE0561DB4E127573309460F3D36B598D8219DB9CFEB24F31B5E92AC8624463151D10DA48D4A9186B493C2134ED5558289CF0E4EDDD8F4F76FA4C7923
	EFE14E5FB17EA5A1235FE350DB43A45E72CF0614FA16E3B36A03B91F69346F1C3E6E07699C4571BE26EB5E8692FF21ABE4963FDB041167C85F1CE47E5F0D384403AF8669FBE4E9D472F63B5D417A150DC4962E3FB9DD0B649B724A9EE2675D1CEE626597283FE8FD335B61E0EE90B01F7F220F74A1877A168FD0368F7379374E11FA1CD3F9A28F61EF5FC7EC7D97399C6D8865837B182DC9F9B52DDF215693C85054B37EBC35FEEBC3263126A13CBE510E750DD0572B29E7FAAE7BF8B3691FF451AF41D208AC9E0E
	2BB173150FDF8C2B6B6171436A3F71B1099F07E65D115FC0E45E68407DD6A214D381D6B730F9BD971EEFGFE51FEC2576FC43A467DCC2F5E7DEC6E776C67EB4D8304AE5D07F4D97B593C14874F62832C3DEA35BD0702F48A54AD82C8B9405ADBF7003547742B71A3DD71C1A6F75BC1365EE8B928556FA9152EDBD39F471FC17DB2C6104EF1104D13C4BFC2BF6F20D63F49D124CBBE982C0FEC6F489852ADBF48665B7076EC07885D13AA5D9A402FA33407D8FF5A8F3176185CBB83C817F60835336A9073C7F6885D
	F5636ABE1953159EE6F4D50735F467A7D43F717ADEF8A68224F33A4D0D04EE6FA4522DBC4C5688F95C4FB9A1F47729F4FD0DECBD01F4CA2356AE93D3C8372C112DB5B29A191E2B9B35F6BEF9896972F9FDD993789820310935476C7766B452591A78FA330949DD9CC2F73FCA1751447C05F4D9CD5AFE5CA0A0DD559136B642FE349CE1F4AC17BD310B64D7FA96E097C74431EA5FA84C918D8D47CCA2E48B366FFF102E5D893D79886E6FCDA7C92EC902F29A402AA3180BFF3289777789434B041CCDBFAB0AF6EE11
	4B75A8ED8E66626C2F0C9631DC36738211598AF53D4D1847EA7B34FB0CA61F4FBDA4536D8D4F818F75E9F3B543BD5A77A7F8DAE42FCFE90349F48C73453ABE22AF597FF0B45F8F364AE5BC17BBC0EEB2C0F6B3414F17499CC6F2876B67D09D76F37178AD42400D1FB1EC0C524F0A96563F4616E0BFD35EFD3F08593F306BBDE116E7B14119036F5D27BE545AFA7973D3D1F598633293B5BA2CF86C49A25667D76C5867AC285FFC947B5CEDA7FDA6790851FD470E67C92DC093F7944F13BA4B6BE66A831F3054FEE8
	E69E566E3FB9D0FEBA34198740661BFA3BF63E297CB053E882D0G58FC78D9194827F32D00F3E91B13682C58E49F3351FFB7B5F27ED5DFDEAA24BF3B4820F75EE47B57C4C37C074CB6299F86EDB470488386850069996FC4BD050FE08D72916A71B8454BAED911C507BCAED56EE278D7DDA476F13E96DDA3A2D9FBB13DEEFFC32BD729B8DFD72FC50FD98D1E0F386FBE520C13B58F98725620DE8F79D02F9C20595A0AFEA9BFC41EF41DC546AEDDCFED6C502E27CC2DECBF9E8F4F24D60C6D493328DB5C2AB4E16B
	16CF686AC6F3F8AB53A93BB5285B32079FB0ACFF98F53BD1C15D2A5AE00F5B063EF5F6E9FD1B847895GEB5B502685CAADBCC59AA6556CE1123C963757A919ABC9262A96C715EA3ACE2EEE2255C7496FDD1857FEFF094875CABEC9A16326F2D78F53672C9F75A9GD9FB0E21BC3B7D4F42CCEBB00C2FDCBF4697EB6F2D91B5DFC0DB4947F02CBC26623381D7FE9C658C0EC6DB43B13EDBC6112F9DF8669D47760647903F9CF079AAFEFB0060C91F2B5A413E6D6A58698937AF99BBC96D284702407F4D3EA97DE42D
	6627E7C75CDAE0E396BFCB37E8E37B4074855D7809FF5EE6C8EDE7F113559E0C9F8562BADE0D6DC782586F9DA7E01FFF8275BFB60EFEE0E56F38B656996F574651B6894037CA65FDF5026CBFAD32C3113C4DFE45ED31FC68E58B4B001AC7BE1C08ECEF41846AD2FC925647A7313D5FCC90DB8E59C417D52A76A9E011E67A1B9912CF295C162C86ED49FCB54F2A7515826DA682ACD75B7F4BA46DABEC7C189E8313582F55C0D35A01637D2B6F6F20B12CF40B7EC1B9F8A47821576D6CF248306C27EB664ADDAD4CCF
	CF3F0EFE5A5112AC54678FC79D670D9DFC8D529174D355375FAFEA84E0BA9926D027F42049D3F53A6B031DCCA70D82C41FB095ABDBF63529F6CE1EC2BBD71AE0ADE4C2BBEC3CC46CA0FA2D3BD9396E52CCFEE4ED0DDEC23D92211D55EADB8FDE4278E1F9B14E264D0B2527D8DEEC076740A96459B09DABC7B0194727D1669870262A7CDF1BC6194CB61F7B4B1B70F43BCECA239FC5ED94C69536DBA4E03BEB214D0ACE3471693ED6ECA7E20A2DEC1FE1CA2E0EA05F0FF9729B2A2CDE1033D815354F04E7GC79577
	098F7AF9C43561394DA3916FA1D6F6864FB933B53F55F5A0BD1FDFC15713F91D6CFC5E2862FFC271E5DDC1BC532F0762C939926A96F56119F202C87C18876532EED817839CG6883186C46F524F21E2CD7E6BC33EFF3C1CC39C4C7D74861881E1DDFA07E9C16823CDCFDC11BAB189DCE4A448E095D33BFA3E6FCFD14AF75B27858D85EF5CE7B3E307805E30622A8E3B907371D579D9C4B4CF6C707C89E2AB98DF18FE0861884C8BA0DF63B63C2D5AC3B617B8BEEB0566EEDE6526EF2E8A78BA08F2034A753D85903
	6DBEF3A126BF0E0E697A03596CF57B5CEC7D0E7DF2ED4D786E324F6D7D60CF67483742910B2F35A7348F0CAFB3A61FA30C0F0D71ED7B085FF47604AC078B3816B4DB434F66C84E1C87EDA682303D235BCE76E4E4DF125A03E33450C27626EB21DCFC867642G75GFDGE3GC9E7B0AECE749F099597CDF838596DD6FFB6079FD022BDEEF04E4D0EC9B179524E047231FE0ED8C9BFD7C15DDAG21977A8A5088508790570B7D7C177E7DB1631FAC98200F34DB119DE4FA7E4AB7377E7DAEA65F12DE3DB8A91E1E75
	BD823E7BE1B1515C01778852FB71BC61B79DB47FC33974AC4CEDE7B1D61E34919B56C3399B4081908F10823092E05DD93461EFE3470A3A5818CD2CFCD6191BAD3F94132F781C1EAD1F6F0AB2660E7895056E8F5818DBE2529BF35B204D1AF3B866123BCA39BE2D6770FDD73FFFEE7DF146640BBB274737EBE4EE72764664CB541577789C65BD9313AFD5D75EED23F313774D18FCABF565DD9C1B1B3C6918FCB93A72F6864279560751694B2B0B602B8EAB17FF9C4A4767099EBCE32C7A98477E301D0CFDB31463G
	928152G32G728136766158EF99B8A84CAEFFB6832E556D9E569976DC7F47A2741F1D1DFF9F132F3ECF0FAFFDFCEEF63EBB02AF5C4EA6DDF90BA666563F4F44643375696535A7ACD14E175B45C14941F25A534561B90D1CD544C1FB89FD18533EE5AD9558FB6114BE4CE916493959CE09608B375D6673FA73792F07C9BCD6C2DD8BC09F4098C092C0BAC076F90C47CF8FB4450AC74DDC94BBA6CB2766564F17A378427BD9F0DE0F6F56883E5945C8FACC3E728BFAFCD31766563F0591FCEF04152BAE6845247BCC
	3D90EB5D52A9795C0E91495C6E9EE2B728482EC673CEA45D06F3EC4B85DC437C0B152CE1A6211CG10F281637454745C7A579F41976E3F953A76B48AF31377CA84DF38BD33F565FDFB0E726E09608B6FDFFE18BCF6176CA97ABE2A2C9F5609G8CE7EDA73896A87775235DFFF4121CD146C1B989A095E005DA77F3DAC75E5B2E60E723641D9C5F031F6C555A38BFD407A4CDB9B52CCE8BF1D16A188CFF080C70FBDEA5E17451E494C54133BCF433987DAEC93228FF1764D6D17FAEC92A788243C39B5A1B0CD81FAE
	4EFC57A4C3543BEB72BA6D433D86FAD66FF7A932D322E259B81546485D9D48161DFE97CB25C432AF7C2CC1407C7C8EFD77332E9F73F04FE02D90190759058DEC24DB923D9D960B3AA16872C8C32873092168766B9E52375F39A1FD7B8D8C4DECBF5B504C763B439CDD17BB4D7A3AE41875F5395BBC33AE7FEE0E268B73572F94F26E9AF18F68A44CE8821E1FBF6DB0F29A1362B691BF303C19E445F5DE69G664DA97A4E35914A3DG1E819C078ABDAF099F889BFFD0CE85D84171D91AFAFE2F56925DD6AFDA74ED
	3555C25F55397D5EA1A95C9E5B699A065585E769B6C5F212AE733BD614DF913DD6A9421E6FD1FED6970D3F5EC24EC886E5336C65EF04189D6FF2229D49F9EB1D5AE61CB5FA9F2F356A7771935668FDCC30466E634D5668FDCC36466E636D56E8F1457A7A063291E34662F5BBCD3BF92EEC6D18E9DD31EEA0B24F3233E351C462CA905959F1210A37D3FC19864F4E141F6840775BDB21EED244BCA24851ED7D2C2CEF6BCDF2F4DB174B31ED7D3A9C5D56BB645836FED7560F27CCC59BCF6F2B63B24B0463B2C744F1
	195EC96C31F5B053D88350BA086372CBDDFA7E9898E4FED06099BF986A93A67BD3FEEDCE582D6287C634BA8571FF9D8D0D07DA0EFFF8D4CBDF2B52AF9B8B252F63796A07AF937FF9A085CABE3A91EC919511737485B4EDB59359BC77746875B10977ABF5B0B266185D7D766831B2E9570F152B6D51E365BAFB6CD87924BDFA2C5CE40F9DABCB6DB30F4B0AF12D3DBFD0710F07606BD57C77A734783DEA0C5515601AF945A04658610BA4468A20DCBE14E92C9A42984B30EA57C788ADC34C6E3D7C69988A75C39C14
	9342F04B203C9CE035864F743CF9B2A4DE407B4C9E3F9B0E6E4302E1FD9F9A9C51FDE8F44476610B0E68BEFC55915B07EFBAE676616129503E62D8FAA894EFFA1F6353AEE9FD38CF9D975F366138489D0A9C836B81D7E68E45331C3B05FE73D3E18E4F455BA83E4A9C1E0BEF31E2AE2E073AA4B33EC30D1F560E576058EF943478BA951FAF0476FB8F473FF00C5C53F5088137DF0127C4E4D69E43F388814ADB819A813AA5F6464A5F31531CE4015DBB1817AE96DBAD0477D2BB7226817DCA0035G25D0D7E9C15D
	EF5FC56AC3CE4455BB40974F917B32DBF7F85F7707C702B874D7893D5796FDF70AB17C1EF313B69E785A0C79707356705C36589274D932A55AFC7ADB3ACECB37043F237D9F0ADFE389FFC7FB0D0C6FE873202E5B0AFB3505741DD0BA14572838FBA9EEAD148BD45C76E1025BEC4BB46E30A14EB60C7B3CFA957740E9020B07F2120A6B6FC03AD49B6694AB791C045B4023797D078E3DDC737782A663C55315177100A95C2F593620AF73EC517CDA55C57CD4ACB37F9529788FA83EDC03677775BA55BBB9D097AF23
	4DB368FB07F5D0AED0F1BB3B518F25F644BD9DA038DEA8BBD45C429E649DD3F16BFA10F71E9DFDD8DF02FDDCE3E77DD9EB8F762B409E2D0FF750F9E673B06B4B33AA7EBB94DF294133BE9E25F8D2EE043A64E15469E1AB6AD96AC0DCB3BD8BAA07F2150AFB14629A21ECD6F17BE5029B03F2020A3B1E36378C4A99AAAE5B0A7DDE23626E223CBB1C194683CE44BDE7A1B89314BDAAEE8D15BB8F4ACBD45C868B361726622E26389CA82FF722EDDF358547441B1A5F37694E45FF5F58DDF1C5E5B01F1A0E611CD266
	4AB43638B027BFDACDF226FAC6CDEE3EB5B49CF1136FA45A59844156AF0947F00DBC694275CB5AF152E69A14D781643A708E6558DF71EEEE4A97178AEB0C7A7720F33A278C85AEFE8F439D3C0F1160FC40405F6551F3AD4257877589EE024BEC575E97A363B0D04C4E4ED8FD7189EDBDD99710EF2802759F045493FD8F07703FF1D2DBDF2261E7FD3BE7A7762D601ED4417175F9D13FCD4F66FD92BC6C39B877264244BF0E973198CC3762B9DE3D876332D6D64F71BC98173F1749D9DDBC1413G52GD62AF545F6
	D2C73E6BA05F9CE4FB585DB9D6F71DBC5B180A677A2D646D0757A76960180C25331601E7F5C6C17FCC2FA48A8FD91F13BEBAC53B5B5BAD8F8DF7F944A1D8FC14DD246FC56C9813859E75BE227DD9CABFA6BB7DCE01519553F9538FE03A08F49F8CD3BA592521BBB10C71B6G65F40A1B9EEE617AF8AEC65A816F9995F11887B409GAFF13DD2AE8657F6FCCDF6DAFBDF0A495DC8F3BD3987A1F8A19E3C84B752D141E5EC76C64A5D41F1ACEE72CD98CBF1DE0CA5D7A746D23297E322360B5E63D74B87E819FC1F43
	73D0671548C32B392EFC1E3EA2B20AB44FAD424F3B704E68DAAF791B96810518167973C0F74B8C6DE8FDC567FC602972313F27C17C56688B77670B63741BB7581F48AE180F293CE7C828715F9523C159C5E7D0F63BAF5227A5E1328700C681F80B6363B532190FBBCAF03ECB70215F4BFCF4CE0572F29537D041D84871212F3FC967789A85668200DE8571F7D13CC3E15F91ADD27147AF4E64B7429F4BC7B51C26C5071649B04FA8E326F78161F12017CF18BFCC64F3976E3BF3360D021AAB3A64F1090FC7AFDDC7
	24001E698A660BE7EC646F95585CDEF95C6DD2C4C7B7C417496D13595DD74637D5467C112B72F55233AC0FD732C8DE2FE426D210FE1852177BB30D95FE247F3C15FC33A3BAE42B0BFC49530B345F2473C5BB508D78B1B7BDE145F8DA6C0F1D1B12891F1F65A602DF658F0F65ECFAEE46F322469E79D44FAC204FD5755CE95992E20FA65418739C3620ADB60FA00F25A43AAD1EE1EBC620ED9F419C783A250277E5ECA432FF0BB80E35FD13C5DF772FD2BD16GED4A8846F24F31395DC5B44F116F66997932C02FF5
	A3FA6786156D748CF7B47C5CE0B745170D061F9B5CF9DCBD43053A49D13CCBD297A07B739CA897G140FC1CE83E887300D61B96FB71CB5C20C77765DEECF3BB4A2B9B42FCD7819E87B655A052D253E7BAC2E25526E3CD5C8DF5092759B2E49B1E61FA47E2CFC6426C272E47D3BFB1C74EF95542D85A88B401E99209E4094407E7D51496F6F12D7E0CD0AA28E596817F26A8BB5D99A8D22496186BB97F9C83D67E90636A681128152836C3DA90B53B639E2063E2E8EE86354718A09C7FE1B145F65CFE8A57D86CD9C
	81FE630D61773FC44FC068ED91BC97D97D676039C89D7F06623B7E70EFA808EE39819CF719F6E253DA556ECBC63577256B7FF4356EB7638456F35D4B46593360254F9432B6DF9BB640737DFA6C17F918701398E2EB7754E1DC1B35G6FF6CAA38825C2701F9E0E6568AFCE8A2A2E8F15E10CEC3B6D8BC24B4FDB8CB89FC6448857F155F8F0AF11F53122086977E68F5137F69F6A77398E542FF8826DF4EF87194B1BA5F27F9D650FCADA795B36B79ADE08A27F40847FCEE4A2A81FBCD99C3DFE8A63A8E90245D19A
	BCD7CE30F770AC0E66F74E94C7B9935AB85A719A19A7ECEE270417A4193EBF7ED86B5B4D1FF1988A23681B475BAB50689B69133C434CA70FDE92D45F6EB903362B195473ED5E61683EE5DA325CFC3414545B208DE592FD7016AF72EC32410736DBB249EC178E4FD513EC3F436642258A4E0539137AF321965683CD59941B8BC939E2AAFCAEFC6FE2F06C6179637D9B0238BAFE1F6665098DAA8E3FE9DAFFD16F1B262AA97CBECD316855B7C2BD33D193D1034B7C050D5CC3F1C923956A07E9ADB224A8720709AABF
	28292A583DE7172A33CC6E8CE83F3B6AA27FEDF653BF3024782A1B0F1E6CBE5A297AE25C9014B3ED1D0D1CC83F669C66F9497A9617CDF9E1967138DD7423AFAA333F293D6D2313A760B70F219DB60C216E294B0D211B0B023FB30A03FDA8324C5ED6BD164B33554806201C27250A68F26AEA08201A1ADD2C1FC7AD584FC5D39863D396D26E028837296D5F74EC307D2B2DB3343F9B5BE7EB7BB4FAAE33825ADEB3056B7DF34769F9B21457CF611A273A1D344F6F99EBFEE79C3F7267DB77DDD1997C6F3384223F37
	EA8E683F37EA8B685F9169884CFC9F65CCA07A5F0E3990507E6D9836BE11CB49F8AF3B14E92C84E88798GD0AE615AE4DD00FC8B473E6888DF179CB5DBA58CCA147F637168FD3DF7BC765FD57B65B819BB865DB0EFB8C97B087F0FF162AF456D21C8067BCF7A1E53ABDBED8AA365783CF192E38E49C251EC7DE934617AF3B174ED65A5BCE35CBDC06E0E061DB1FE64154DC7C4ABBBE4E4E365148D778C42B41EB1CE53B66B21EC8230CD2BFB897A7D7BC937170EA1463F5E0FE3ADEE9A573EE67AF79152213CE69A
	6DFD558479F623C734C75E0A8224496B76C8DEA54030749BBF9E47DF9F562EF373213D523819773DCC07A567495C348DE86B817AG9400F800A5GE9GAB81328172814A2E027DA0409E00D600DEG87C05CD5287F2FA7F74D20BFB7AC8D9A2FA81330617AFFA164AEC98234376C2A5968FF283F821E4B01B68B20G207CEA08E500C600EEG4755285B6E294A99F4E3874BC155787B06D398B77150CE4655F86F799327B09E56DF0D7170ADCADBF38D5886E0609A0C07251DE4A2B8A379942633E7C0D0E3F5E4EA
	E7B41DE0049D03106C9834CBC3CA37E8D5ED356F0C56D6E3A0E751B5B35B0A65715522A0E09EAF744DB09F55C29A2F2D5905FB37C19F5AE08948493A86EDF02BCF5DC74FC39BE4D15AD6A88FG0C4DC35A1FAA987F0B67DD6E1AFB6F10200E23773C584ECA356D5B54F9CC180FF34AAE1B56C67500EF1FBF330D985FB10B16EFGF81498FC4C1E779CA3BE9E310A9EFE46C067B9F22690BF9F636062F1C2E3D3E92ED5E99267E30CDEAC0D1EBF47CAE3674F2B683AD5023C579D7070BB888BCA498241ED3178A4B5
	CFAFAC5D2C625AFCF8DA44EC703CEDEEE7892F4F112F67327958D94393B47FAE1B9FFE86D1C071E973434FA0662BF3C086546DD80031720C6E37D4718B783EFEC134BBC5F29F792D4E45AC9D73BACF9F19D7553FBDC2EF83F481214B4A56C12B689EB4F581531161FEC2F1ABB90E3513F6BEFABCFC6BFC6CF8D8F1DEFFBE7D517948797447677567531F1D574E278CF77FF9B25E2C104FFBD53ADF2A3833AAAE1736C9F0E7E4B31D8F795CCD75A278D60988D467EE2A57A84575623D9F8D6EAC47BDF51E44B4F459
	C67E489BBFC7B9CF4617C5F6B8D8396842866A8388F1559E25741BF059A3EA745E24624E2A3E3833837DC3F6BC8C771D8E744FEFD55E367E687EB17547764F59FEFD7F7477C77A475C2F6F9FFBFF24FF5C3CDD2FFA81185F036BA779066DE1CF08AE512A791E657829390D5F33F3647B64E57331717D9E3D67103BA0FC5C57D3FC5E0270F17FDDF58EAF003A01EBF1AFE2BA93B986CF1F510EC12627AD66F7387A7D9B19A35FD7AE1B0F753F1D7ECD1F9557065B65B4452F3EB65CAE8F2A73FAB654B546E3BE4C8A
	79D6B6997069713319BB77F87574DE45F9F3F45AE0FC2996BD3EFCCEDFFADDA49F73D523AD52FFC7EDDA7FB11CE8C13A36A244D9ACC87759C2F55FFD1C70846FD1FECEAD737BC81A3B9786B978FB216D4A6FD3132F288CE61372913CC3336DBA9CA37E91FCBFD7F99D6E19443F123861F39BCD6B7C6F930CED8A62B12DF37D5F6F8C5A6B1066778FCEDDF95B657C83E4F4AA41BEFD2C791D6B3B72321F3922B2D89CBC2EC40E35FCA5F22C9D276B73B10815F8B506D23D98C389AA6E87F47C65C2B9DF45D529772B
	046B713C719C7DFFCD30F354C5A3F80EEA3ABEB2A7043F9FB7DF4F6EBB3C4671E357071F238EFAC35F45CFC9C1D9BF70222CF9331015C8E4DD9FFCFF1992A16BC1CB282CA516202CE675DDE93A0E2C70F76F6132D6E9E4317D5037C7F0BF14F53D7E79A553617BE4CB0967936A3EEA73C27D7D99BFEB3785F994751B21560568AF23DDD06372FBA15F704781CD4242594CA34F044CA34B00E7792C7882A177E7D783CFEE8CBE96F7770DC4467603A31131DD6E0B24FB5597C937589AC9F723D5CB477E9FA8C70DF7
	C0A165B7C209A2941357A5BE2F703C3A9DEF236B561C113A0622C73ED0E4900A5B048DEC1D58A6941345DE1BD0C25702EDC2912C9347487FC3402085C5A8216F6D942128263A4EF7D138C4E324D88BAAB664DF94DA2F70E264E1C1538D777DBC253F255CA098C0C11B44B4AA99A5538255095879GED31B424242FCBE98487A40042AAEDC9B02BA53247AA57EFCD910CBE275B2D58E04B25900379FC70F03A3DD28CCB4C820BFF5BE9DA7BBEEB7D7C21286FAA4DB730B89C3BA17CEFBB6D91C9BF52G3F7A86BC83
	18B7C07ECEFD34B321AE2768F0A822E33849E5A667C3C7820A94BC7F5DA71276FDC843ABD9DDA37D06542E6179DFD0CB878830DF1F6B54A0GG68E7GGD0CB818294G94G88G88GC5FBB0B630DF1F6B54A0GG68E7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8EA1GGGG
**end of data**/
}
}