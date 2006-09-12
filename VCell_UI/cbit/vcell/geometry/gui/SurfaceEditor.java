package cbit.vcell.geometry.gui;
import cbit.image.render.DisplayAdapterService;
import cbit.render.*;
import cbit.util.UserCancelException;
import cbit.vcell.desktop.controls.AsynchClientTask;
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
	private SurfaceCollection ivjsurfaceCollection = null;
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
private void connEtoC3(SurfaceCollection value) {
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
private SurfaceCollection getsurfaceCollection() {
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
		java.awt.image.IndexColorModel colorModel = (java.awt.image.IndexColorModel)DisplayAdapterService.getHandleColorMap();
		int[][] surfaceColors = new int[argSurfaceCollection.getSurfaceCount()][1];
		final int colorOffset = 1;
		for (int i = 0; i < argSurfaceCollection.getSurfaceCount(); i++){
			surfaceColors[i][0] =
				0x00000000 |
				(0x00FF0000 & (colorModel.getRed(i+colorOffset)<<16)) |
				(0x0000FF00 & (colorModel.getGreen(i+colorOffset)<<8)) |
				(0x000000FF & (colorModel.getBlue(i+colorOffset)));
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
private void setsurfaceCollection(SurfaceCollection newValue) {
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
	D0CB838494G88G88G4D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BD8FDCD4D556E4D8E4D624D614D428D818E8D414E4D614E826E4D8D66CFE36CB2D6D5AFEFE3B34315F3A2D3B7B781798281414EE31C99B95A491AAAA9926A8A6AA9649B88C884A1B19B74364B0B34E3C611F245F39773E73664D4C9BC679BE777377BB3D39671EF34F39671CFB6E1FF79FF1C51B22F2B3330AB8AEB317137E6D4C6438C4B347DDADE65C0438F01BB61FD37C7B8860ED6E13769CF8C6C1
	DD5FF35A7CC2CE57158765F8A8A7BC2B4D5FG3F57F39DDDAB0C70A3001C94283BEF7E2A1C21655C5504F2D6517244E38570DC8B10GB8724C319379B30FED1470CBA4BCC2E69660A84DF3470AA5DC8314BB81A2GE26D0CBE891E2B2964113BEAA43A766BE3380C794E2CB5249DC51B88258E465BA0BDCB38B7B2539DC4565F281E04A7844A89G2473595C695D1B61D95BF45A7C0DC1EF91ABFB8C0255DAE9966C5D02686C2FB43BAD3BF6B53B1DA65EA09CB0DAC43B13331EA16DFD5B1CC779B92E9A4AABA8AE
	D3CFE444416FF9GE9B27EE1A34157BA35799AG2B937B357B33DAC9EF5D59995CD2731C556B8274AB54497A936B7474AB74F3FD76BF288FFE7B833175B228ABF0E973CBG6A810CG21G9FD05F3F5674B9BC6B7462FEFBF7375DD667F06C339A1DC76DC6418A3FF76D029A89F7CCB0581D908B4C3FB94D95AA7A8C850C6F1D0B66E3B60959063E0EBDB61DDBF25F0DD92BD4EC32A466ECD6240B59A25E65318913FDA4206C45AE6FD874165D42A9E5F32FEEDD152BA23B78792D2B16C96D2CD4482E95092F5DB4
	860A61B761A3830861AF277890851E4565AA0AA765A8284BF1E3EC685622AE0DB9933942ADB7295AA1627151D9DDEE26C3085BD717978552F62C1B49AC17F5F9036273FABCF8264BEC9A2724EC043A38DEEDBE0B2F571B49B8DD59274D5F582F4D2F82E8G68828885086847985B7543179798E3CD3CD35C6736D80D0213E33C2F7F709EBC05BE51499F3239C45EE690343C53426B2D020B13E8D67EC0EC2077624B24F946GB28E8AB641490B963B8DDA67E1788ACE9723F96B585B0446A950DA0D25DBG815D8E
	8E5BFD6758FAF8DAF917F85CE164C541C3416A5FBFC6FC5243EB21C5E8GFEB33B149CA776CA007E27G2449F6380136F7C4F05A846B2EDD0DF67BE937C3C3FA42D58D90BB6BE96E48G7A028134F1311660C4A80F9E40F6120F3D23E8275EE2A06A734EFE9CE3E775C5A4EE00FE96C08AC0BA40463398C33F53E18C552F1946D5BCF703EA8C05C3BC571CE53153F456F7ACDDC5E590BD7B20AE82A081A075AC667FB5FA9D091F8175F1958CB6CA7222947CA9D2FFBC3964D3AAE375B05ADFAB0DAD724CD660C83B
	3952EFE67FD1AD4417ABC01F22C1EDFE75A07AE0D5F3890B69FD3CCB40A87E5E8981A898BD9EE1FA7ECAE86258BC79B140A7141F6348DC359360AB004FG5E85A073719E00CDG5BG8E91BF839C8578DE9A03E4DE7AC23681E7A2EDB702DEA2C0A4C09240F200221F347935GC60051G71GCBGF24EE973ABGB4G03G51G49G99G4567E10EB80FBEFC5238B7B3307D56A8FE2FD7682F245964536F4FFC7A00F45B24A75A8371FBD4647A5AE4B83E7FF78233E531B014AD73953FD7AB6C2924A171C8E2B5
	5763FBAF1BA25D878A7F2CD360FDFDC3601FBEBE7A7F36D5B0E0E36F2F4D24DFC20FE0936B8507E0B3428F17D41723A17613668B13DD66F90B629DB03F6C07046E1271059AE2C7DE941D963DDB941A85D134584C1E7A0D9AA24B6556BBBCD3934A5A24A13E3077427CA25317EAC8EEB658EDB601A68119C52A7F144E8D02D5688625775B5DB61161AB0E137573E921DF1A1B2A0E3F491139456AA6739CDB134671A4CE9CCECBB72496B2D5F2334E13F9233FF92342C6D6401135F5B8BC234FE34E6F7D98F3FE6940
	94AE7114A3CBED8D93B1793A2C1C9016E3B706F8F2BE5369A32D3A6E1F51B9CA423B182E69EF127873DEA13378DD4F752CA5FD6BA6737F9644C56CFE47C32F35883D02D3E337FB6A676DA532F07DAD51558BAE035362A0F65EA2511DFD6FED6AAFDA3F5FEE35B2B7F0184FE76BC89FEA20EF0DG46904C671334249E67D4A911FB71C70790DB2750F90F58E5904A63C2182DC9396CB2D26FB294A96C39DCD14F78EF176B9DC09FF619EF7DC1A356C7C0DD1C221E69995ED2C87AA71CF18B3094A20B07A3F24C5C54
	720ECF1DD60A9FD6FFFB8B099F2BE25D51B802482CB761FEAB9D6465812EEC841B57EB6179A16017D23A35E6240B9841740A9B41667EF9A324356665044E5C0DF4D51733F929811E6D173376AA6576BE30A1DDB254AD854889E56D5507327618FE3F14695AC319DCB1142DB7A2C3157ACD31A3DD2C22BED5FAFA7433BB10AEA9144D13C4BFC23FA4D4295F66B3C837B254D36F5F5EEBB2DD51C8B65F7A3617B81250CDF6A2DD85146B896DC8561F68112CBDA65762C23A02ABD8BB25D7B0FFD4DF21242B9165FD32
	C457A95105FA519538E53FC975F170CC87A83C1251F5DDC968FEAD5315DC495688249FC43F9AAF3A309E240B3F122DA710AE79CA25DDBE6FC5BAAE0C2DB5B606B1BD4B4214F67E2BCC57A8559B61A982C4043176187D9675A1DD22C4179A4664AE73223B2A9F69B6DE457C05F4D5D7A97B31FBG69C22FE2EBA36CC724C447F2596B65A43F5233009F799E3E2F52558BF3442EDD071BF84896EC5F7FAB3AF6A774332E427D7DF11AEBB220DCB08AFCB88AF37122B5383F9FF7BA06DB726A16D5014EADEAC7B1BDBA
	C7F9F2F1F8FA5D1BAC172DE9A3B22320AEEE94663177F54ABDC61D4BE5B7D86876C64A81CF6A14391A61966A5E02274962F40907E0B26D43FC31D8C774A57B9F89AD6D877FC47726A45FA6015C15G55D793FC35014CE1A4F730FE9A36E1BF4387A6F31DB75C12159F201FFDD7337EC5DC6D69E7453995394C7E194DDFF297F8966319B9243D7BCF5FAAEDBDEB5DCF81F5C811E427ABF458EB572CE2FD1EEB40BED70D06BD6EE86CF33611741964A3C6E7B362F9D29450240E467324E3C55B066A03B4E1497DD04C
	BC2C5DEE934AAF3C866294004DB7BB3D661B904047GA48330797030014827F3AD07F3698A9355394B62E2B67AC35DDE09BFF97AC4AEE15568AC3573262EBBA332522EE136493B56E32308897A6CD8G2667CEB36A29831A70EBD10F5BA85EE23308965EEA99904A4A25F95DCCE29F67EB5E564313359753EB5A07CA3D7278112AFA0DFE551CB5CF52A7DD21D751D8FDF659D854AB3C9B752A39CE1B6F388E7DD2748DF952F596993BF4BDD5582ADCCF45DC477663E9704C388EE3FB3095F58BC90E67B62E3FC6D5
	B71A434319CE55619E5D5222745999D128DB3D8DF58B851AD96168DBE70752379900AF3EDE1B3F77FA3469D45BD6F872B4CCAA5923C9F01A6C4EEE41D8C6B2D5A55FABD652F5F2E59D2DBEC0FE17E3DE7B53B9A257A93884110C1B32F2AD53A74F0E7AE8C056E8D95EBF0C6FF3C32D41B03E465AB13ED8FB4F486DA5C1DBAB2F47319261C0FC7598ED3EF58C4AF97BCC20B59C63531CC13EE860D9B2865BFB5F09782231303E9B0B780594CF7A9C8A38D9E3513EDF9C7135AF99BB99E3D10F6D2E7F1DFDE3CF1135
	1A1B1E9DC956829B33781938DE995B3567CF2A46CF583A2E2C3CF1ACEE2A46F962A7F60CBEBBF18C464F989776FB90E86247217EFFF6219FD879A3D1996B0C179751B64B012F7486641DA7127D2749E2958567FE37E8B7193EF53205E53F1CC77E28525EF6B76A528EED0D135B0BF493DB9A3AF81BD928F409E0117D74B7B324B425CA36E4B5E8CB662BB359CADFA5C33B69GC5B7E27BA9EE5AD75878B1BD5675E03F4A002673C69C6F77ECAF25B1ACEAF8375E62B9927C56E96FEE36DAE059CF574CE565754CCF
	FF79A77A2934BE1A2B49B89DF01CC75CA82DC1EED478E94D1FF3938118CE7748BA955E84B97CA654E94E0ECF18CE8A8508BEBEAA1655179F126D5C5C0BF68E01F692EEC2BB4C6CA5F6601D66AD2C7CF26FD0FEE4EDFD5A07FA25C3BBE5915856B5FD98BFACAF7E12D766454E881697236119A0735C5F97ACC7B019EF48B2D300B76FE664677BD1A6334DED9F7D8B1EF65BF7C26F41000D422842F66F6D47F677C21BFDB7230D0F37B7E0BBFED3ECD96390D3F2259F795718A7CD83A8AB8E64AC13E5FD5504E7G87
	C47B519D272408BA0467B64B7C5EC3145C62B9672CD67C16570174FC7EC93A1EEC38051D4F2F16714FD33CD101E77A1D20F8D28E013A2537601972E08771E343F8D8E7838C820C830887D8BC9E57116D2DE43DB264197DA19B44140D37B6FB9D0E6059F99B7167E921DFAA3F57264C954C8EFF5593BB240F3F70B3E24617EFA0FCF911974E4746F2F91B72FDE14E5B073332830C659A296D06C84FD8E6367B629449C3E1000F8618853098A0A3926DB63E2DA2185D707D05E4B0566E58CE52EE512D30C781E8G68
	848839955BFD25AD28BF8E74297A0359AC59B0BCDB7F7C22ED4D78C463705E8F0E98A61F4B940CAF6AD66FBEB03E4B0272A5797031B1DE7C95711B4A1E1065F08E571206CE5F33B912B317C01B69GEC6FE8B310BD995917645D06E374D51E6CCD77C2399DC08488870887C88148388D63E27B09FD4162228E8FB7B5F67967FE5F83CA3447EE73706CF8A4A8DF416D5EFC2C1FA78D241F25D03797C087A082C4G4483245E0E7D3C724457C1631FAC98200F34DB7E9DE4FA5E58BD3C7EC58765DBF13BDA1C641C3F
	60FB84526E0745C4C3AB5EA3A80C427304BF34527C8F65CE003EA80C15DF98098D43219C8B1084108610B3C11BDF82D0BF81ED78336031A2AFB6AEA4D68E5B06E7CBDDD03E7689EA36DC579CE04C6DF30BA25D9F30B1B739CDED4C0950E658849CF3D3BA72A4FD22A6607B2E3576617527B8A8DF6A84B53E54E14A7BF5D03EF4D5F937B906A7AFB6A8DF5EC4B53E36B343135795142FC4D5DE09F3F872AA037255284ADB6C475764C327AE6FE5BF3ECA1F3255279D963346A3F84698BA9147FEB3BD3B1F8565D400
	F40002C93006GE884F0CC4231DF513E1B3B307C399FF08DF67BE915E1AF699F619A1E1D6F884A97BEC90D4FAC8E4F4EEEBFBEDFBB47284A7B5ABD3C7E9D8A4A17B8C9AD2F3DEE8AF03E5C486B85AB4BE987EBFDF39AB92BC8057616CF421C96131D47317742B95118535E6D991E6DBE76637335DDD7345AFC7EACFDC79482F511G71GA9G99G051335795513B19E4F3757850BC745DC94BCA6E78C331FF30276D3B3D90D2F35F7F8B1E28E4AE7D515D751B73C7E6D72637B50279CBAD9ADA66DADB5DC30F54B
	B141E537768846C63B015D20A23B9A45BB118DFA1CE3A3A7639A66BB1E2CE196C3F9B9C04E9D9827DC7F707A37560F4F57FF45F72871A58CD35EDCBFBEDFFBD62B4A338E8CCF5E009F1FEF7F9AFD64313BE4FF276F230CD0578740F0D67A1EBE924A71325D1F5611B34AD4A8E7G645D294DAF3E936B1650BA725E161CBB13FCC35E4931BADE27D35A78CEEF9DB294BA657954A9A1758E75BA296FDEB2FC6FF9657A508712119DG4F7250480E40F7C9C6F7285FA5995321FE17A42263ED0607B614B79931BE2AE3
	683BA6B19DEAF7CD7EC97B70FC96BD2BF75BC4CB37C04532F1AA74113BBB10AD0F39EDAC159249AE5F33868E7373FA8BB9DF2B3F937370B7A737F27EF918DD5840C6B4826F7C5EE412B7844D8E4100BAEFBF9558FE3527546D376F143A7D9ACE8DED3F26D3C35BEFFCE7E0DDA6F62A6B9253292E4B744E21F5F920B310AE4CDF3F216767FE77003E03990D4373733BCC2BA51AA6515EC47C40726678CEDC67F55E09F9F3103EF30D00F29C40BCF90C3A6879CC1A6F7803F2DE8C0C5B98695DC10C27DE3AD74B8736
	559B3C3A2DB8BDFDD7E7F7BB8D022FBDD669B74AF51ED97A10A8F413AECBF72DA83F48BB4D021FBDB7D0FED697083FC4CF4EC874962345A93D91E2F6944DE8C7F25E3ACDEE334B90380FF603FA9F5D0640FD9CB4846F63C8C360BE0EB6846F638D06C0F1457A7AD4F7BE460C49E96FEE5AA2654A232DC32DAB6AE37C73ACBBBBB63511385245303363CC99EF27F823824F4E14D734627BED8754AD0E41BC324C9858563FB72A5B7A0F464036DEE98CEE6BDCA1302D8B046036FED7D00F274BEC4AF85AAE0F4B975B
	F0DC56CC113F61E8A776F0C0B98CA0EA8A0E4B699DEA7EC818427C108C4F34A95EBEE1328F5815B9E10B0CDF67D06A6441BF67700E072D927E7AB3CA7A2DB2FD0B531BFE1B142F1EFC0F784F81A9D0F0510DE0BDAF7292CF3ECB5156FEA21B7551F2C42D0F99D23F726E720FB9E6778ED360D8B11A54E365C7D360D8B9E38A9EAB83264031F219A9F82C0CB28DBDAE1FD65A68688E99FF1B28445748F827DB095FA9475879DADCB3975F05B1764867A446B4D03682045E05B1B6ADDB39BEC208146C9EA7BD13FC7C
	108A6565BEB8EE2AB63F8820EC2A874F74EC6C710A9770BE33470B6640BEDCEAD67761EF4D01FD781AB9388F5FB08776E1E6D7F09F3E55B5348FFF6753D79CCB613D5EE3E63B04B774A9FD7815BCAE0A75B8AEEA277A0F01A64099FD70AC67BECE77C6FDD3FDF371820A8F1D6A1B0BB5BC6662F0284B180A6FD0F776AB472BE76C7F5E8B3FCD46CF9A706EF73504FFCFC36E69DA79FE3BDB04B80A25F7A6B4F88E21033283A082A0B6161D31CA6F58293DCC30FB87735245E254B47A2D1396F98B20DC8230972093
	A0E49A6ABE2D1C54FB1D084BF7004F34917B32DBF7F85F7749FD9E9C7AAB175EEB8B3CBB45985EE0D9230C87E9ED46FC9855691B5B16CD7378EC6534C073E9BADD2795CE77FDC77BD70A2F186E7B0EF6049E5F51B6C0DD6CF45C2B0D226F048A6F5666D75C0D3867A8EEAF14B5B26EC3A341F5C1F9D046190D384F8B17F14F9CA638B4A8E748B8E3AB526545E1CE3136F1328D5C0A5FFFEBD54BB57FD740E4ECE83B74B2DEEE7375EBF51C47178DF1017C3A25037829BD0E79AFDB46FFCD71D685DE7AE62EDD3E13
	83F5E9F1E873B9743DC37DBD6043FB907779C974C3270C7B5399020B03F2120C8BB9023CA9B26E25A3483B649E74E1E8AD763162DE561F3D77FA7A2539B7D09F37EEA6BAF75D4B7A32CA46FFC371A18ABC6BE3921D17C8B9826AD65E0BBABD1D0DFAF65E07382A9C0233C2B9D4469D2467C391D01EA563A2A9AE854A4BE5DCBF0DFBAEDE1B3FB19EF18B8C586F8A997799458DC2F91C0CFB0962E220BCCF46ED23B216C0F9050C937458DE417DB2AF4555C03969FE346DBC5EB3A696A9FE377117BEF6DD17D48673
	69F18D4EA9C668F3647D1853FFDDC9F226FCC6CDEE3E6D5A354FCE3E13E8E49384DB3F5C2A41B572627BF17D72A07D66256081EDFEA9C06D83F807326FD33C1B9BFB47C4AE659575FB50E91A1F32B48F30186DFBC0F16F74F3FD3674AE0F1EEB913EF828DF7EG413D20D55E97A36310DCAC2A04A72BDF5D222CA76B82728D15277EEB2FFA226F9EAF7E0FF54A7ADC85BF6B5B02CF30EFAB9744F1567BC383FE1BDEBD0375C9A7BDCB965E1C39BC2269CD9603B3BA719CAFFC86466596B9D663E7E0DC7E371E1C55
	25C1B98320A0816657842C4BB610BA72DD87796620BA015D1DE3F5E17A8B0D29B454AF01356FDB1FB1439F47E4CC38E099F8D6371AD3BF534B8D40331E1BC07B584D7FE8F7EAAC06534D8E5EG0B8FE3823157CC834624A68157994B8D2BA8FD1F255B5D4DB13AECBAEF3A81E2E43A6DC6CAE731A9689A0D98EF89247CA0411DB356CB7A4CD3318D5EB34A16E0897181403B12DEB98FFA56F6CC6E2B0715772518DC0E66FAF28FC2877461D46E595692C9C657037EF287A59C0B1BBF37E1AC25BE083164EA47D8DA
	79A046444E8EFA0FFFA6166BE819FC1FA3FD975AFEA972901167091917AF090CEC45F3BD3724836F0C6E1DC97E2685CFBE7D6618BF6BCE568F510E52D7F44E877F044EE4FFCF0378ADE2262FBF57BA69B7EF30BF3158E0BE2672DEB9C364315FF9AE0F6C95C7D0F674CCFF1F667A48CEG1AE4000FA5FC1AC2B6737178DA1C6F164FC43FDF361D4E298F413E6FA144F1BBB196EA9EC2DF575039BF8C4A11GF1B23E1E62139EE25F91AD157173BF9F4AEF04BF180F42A41AC895DA69FB6AA1E58C75AE40B78E5472
	8973C70D48493EBB18134F49392259B2A0C8637194DD53644C025F33B0DF1C54133FD750E5F7DA866CB61137EAA03A1A6CAE8B3B7B4A787A7518BFEAE53E7B68592843A9188427D3B0D2A9C81FCC692DC05BA753079B48B7BB3C55E23611AFF9F4C89BC3F3DDB450A54C425C74328163E9592C6039E9A561FB1865A602AFFD58B7163FB1D0FB301C283007164A2E827A5A07D14F950675DE7628C30DA51EF764DC5AA5735CDB9B58966DEC4D8834518FE38E9C5417C8FDC9F9583FFFCBA51C240FDEDD77C22A7B8A
	204DF984E37904E6F8F791AD43641BB8A4DF9568D57F085A394197743BDC5DA33E6786BBA85E78086F3941234765B3DC28DB7C885EA57940CD7667B50F425E84408A908A908D1078A81E735EEE2962023C3757589D0DC20FE0D53CB6114EC43597EB973616FAEC952E2562E75E45A5DCD19F709B2E450FB27BE4C84F04641BB3491375EFFA8F69DFE922B6FFAF0091E090A09CA0A6917B779B0BF4FF173C822B93C55E50C53F1413DF28D904DE8F1A9CEE30F311B4791E67ACE8EBB140CA00424758FBD396278DF2
	1C2AC159E34AB835EEA071A85DA6156E720FEBA07D86CD2C7D520DB7067F7ADDFA86C2EF0B6039C84A7B1EF311ED52B7943B9BFD3F21A03A55BE0663EE110158F42BEC7728DE65FD691A37C628FEB3CE20C9525DA8BDC77F7D66CC56667B46ACF8EE2F41FE990CF4DE62703C73EEA32E4DA201F79325015910737CD343319CFDAD79A4D1527525820C11622913387A976A33F0BE740B91C9475247BDFB891D29FAB553EF4391226F562FD03FC92D28DF7B63E8277FE8A5F379FE015CFFC7793D02D2FE71263DD9EF
	8710BFCE121B2810CF1EAC0E7EF58263A863F196C78533E1ADBF1B3D03E7F1B42AFD28B82A192D0C23528F48BC51E56F967012A453776993CA5F965EE24D4A8C20EF03541EE636C7DFFF1F246FE1BED9B840493EADEFC45B054DD673ED7A1E403EFD6DAC794DF27375FBC8FDA23411BC9BFD90315D7FEC726E6DE83B9533196D8A13E04F194476BBECAE5C309367425AA47539D089CDC0E3CCE2F3A1A977A5794E05DF1E710CBDBCFF7C791B9E5CB669BE4DFBE75F14F178CDD36FE635EF1AC213707BB44F5F82D0
	56C7C0BD33D179CF1C2CC72D1E5CC331893DA57207E97596C8D1648F93156D282AA859D2DD0E3461FAF2E7C0795DD5B3796FFEBB7D83CB222BF27F016FB4870E493E501F73483199AEC40E3FDF5376B07EC78D9FCB32A9AF4CA28E3B0DFE74C5E51E2AEBBCF4703B2370DB0A2175F2FE08CD3A58989AF95E73BB06737461798B6A8323ADB7DE1C2DCEE6F9643451FC9FC04E36AAA2282A2A1C75B3DA0F7DDC1A04B13EC4CF4A4D9061DDF27B17E5FB5A5F428F517E96EC1F2D6D9F2467B245F3347995F3F03DFF6A
	B8BDCF06F2539CDC73EC531276596FDA456FB947AF7D7956AFAE298C696F3338833F375A6CD6FFEF35452DFEC7E427FB687BA8FB5C017FF64C37EE655F0EE16B139FF248F8B702AFC2G228192G1267605AE4311BFC8B473E6870DD179CB01A858CCA14BF23A7F0DF9F69897EF7551E68A1F3075E8E73C6B7E99F71CF7590FF09F687C5B25CB3BD44C6CE0B39CBE42492BE35076EDD8493C5CB5F974B6F2D16C15FCA6662996316CEF2F754670C712053E25C471B59A1A39BABCDBC6E99F4F3710C719CEDB39C4A
	B1G09F3F18C89F48CFDE7F752B14478C5F9DECA1D0BEB5FC4BA968A1F007171845A5B52CB3E5D5072BF7A5F0A82E413536E901CE2BF4352EF7C24B80E777A0629915A6BFCE268FDAF53E125164CCDA25006834483A48324812CGA8188736822886E884B08274810C8608820883C882C81D077ABF5BD7BE047E12E1E950B8F98B899BC97F982F3BA44B21BD6E498B517F96DD89BC0B00368AC083E085888108G0885C8FA92F57BC7FF59903A3103E50FEAACEE8E1F4038C903F6B64E47FB4FE3CEE0BCB44D47F8
	C82434E1D00E82C8180F71F0A77DFB28AD02CBE4BAA737F2F22C1E6C7FA410CEB0428EC3C8FE2F7FD1B0089A5EAC5B2A32D1E92B941033F47E5036E2F93C62844749FF8FE97B907351D6C863DB2B4AF16F7642F634418A10D335GED30E9073C0FDE00B6B83A0350C6C1B981A0C526ED5809713FEC41452E394736F372B81A3E995BA9C946364F497335AE9967140DDE63A19C705149C35B0871CD51AB799200A7B9881F347751909F7718F987DBBF565331CD4E945212B18EFA685F5B6A12E9C64BB4694998232D
	B90173E7E7CE707CE92167C38264BDCD3FC33A03E04BA16BC03B49649264BC6D4AA914F107DCF8DAA45DEF6607F716D0BCCC3E63974D474E9A7EC073AF77146F9944DFA83E60A95FB308D0F98E5888F503CFE12C74B72A49CFFBCA5A57BF9568CE511C636437BC9733F4AC55A59FA7732A7C37C768ED00E6A0341925F709B4E67396B29D99AE0562CAA41CF4FF31A5F0BCECE8899E8FA5AD6A736927AD7E736966967579F4CB0BF2BEE5389DADE43C19A11F6BE43A2FE5DC2B0C5BCB5BA438960B114E0752DA046A
	C5708D82910878C62AD7AF4569A45C8F8ADC2B045336101806AEF711BF7246F0A7DA4878B2D92CD6D6BEC5E9ACB688F1599EC62A234541AB74364838D659970F3622FF6AB29117540A7EF9CD661D2C8B6C1FD8DDF07F5C2FD3774FC3BAFF7FA46A547DB3D7676F1F85D23BCE7982B043AF5211FC437630C7F99BEFD6FC4F52F4E2F8637764B079EE3AE8BEB63E2B69BD075A053E63FEB745B7AC749D77F365B9DC83F5898BF1AFF22051FF8C9EEED40EC126670B5B07573F7F9CA6DF690E615965B1BA7796BF6DEB
	177994DF76342FDD1AF720DD2A21AE62E94C07C7F7AA652F84FCE14A054C1D77EDD6533BD4622DD1E90371DD2E7A7DFD23C45F294247FC7525CA8E2D764A218C1728C73AC359081B23C73A7119B8071FB8CEF8BC77A8A749E526678A455D0BCC3D677728FF43BD0CC917D48633C9F8A35E2191D3F00C3C56086F67C2D2F04F44FFCA62C61A5BE8DA177EBEC15F9A8F9E53BAFE93566E3157FE456F6493175EF62F7C9BE4BC3143532794456F569D17DE3673124A106E6D6D749FEB161D7EE3EDBEDD1F27C02C2449
	B174E5854650F299F78B9D27350BE0DF3988F1FF176F906A9661F963839D44166C9CB56FA81E2346AC724F893E6F47E7ADE2779DBE1070A90BFC4FD1DF5A6C7DAE7EE7A7BD323E2DC0D9CBAEC0D6BA113548737EB26799DFD977983DE52DBC6511751E00328A1F897E6E5DD7D629C2965B8FFDF59477C3D54F281FDFB29DB651FC46F8FAAB1027CB158777E27B749E1E038272C4BD037EA2DFE9E1DCFEF3D41957D301E639CA7F7C63F930C24947BD2B4DAFFA76C2789A85A5DF9970548661E3F1F77C28FFEC379D
	750F6D31957EF491957EF486411F2ECBD052317F874A042C7B20907BDFDC2E48651057A5AEA737CE5E0E9F226B56341EED3B32FFB5A9BB0B4BB9443D49560907389C3258BB4465523560A1AE9B5609FD64A2C91692C4AE173E3793396C2A4AED2EB35CB9961BCA1031DE7F8250BA39F77C8F8B6A2E7D598B31276A0B3238ACD030CBE09A657612E9016AC46CDCCBDB4C73AA296B126747E104502ED016B823DC22FF3FC83DB511DB6D6A365B45AE58F20944E0AE97BC3A6DCEA108A5AEG0BFF5B693C72FD560A11
	06006FAAE7BD4762B065B95F3F6DD4CC6F6696BC2F4DAFFB9E4FG2C6D646F5487BA93EA6E662DD611371E2E33994979503EFED1701C7FFE54C15AF7A10DD4496A4A68B724BFAAF87E87D0CB8788409F3C5870A0GG68E7GGD0CB818294G94G88G88G4D0171B4409F3C5870A0GG68E7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAAA1GGGG
**end of data**/
}
}