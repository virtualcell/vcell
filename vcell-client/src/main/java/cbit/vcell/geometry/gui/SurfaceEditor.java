/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import java.awt.AlphaComposite;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cbit.image.DisplayAdapterService;
import cbit.image.ImageException;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.parser.ExpressionException;
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
	private javax.swing.JButton ivjhomeButton = null;
	private JSlider opacityslider;
	private JLabel opacityLabel;

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
private GeometrySurfaceDescription getgeometrySurfaceDescription1() {
	return ivjgeometrySurfaceDescription1;
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton gethomeButton() {
	if (ivjhomeButton == null) {
		try {
			ivjhomeButton = new javax.swing.JButton();
			ivjhomeButton.setName("homeButton");
			ivjhomeButton.setText("Reset View");
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
			constraintshomeButton.anchor = GridBagConstraints.NORTH;
			constraintshomeButton.weighty = 1.0;
			constraintshomeButton.insets = new Insets(0, 0, 5, 0);
			constraintshomeButton.gridx = 0; constraintshomeButton.gridy = 0;
			constraintshomeButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			getJPanel1().add(gethomeButton(), constraintshomeButton);
			GridBagConstraints gbc_opacityLabel = new GridBagConstraints();
			gbc_opacityLabel.insets = new Insets(0, 4, 0, 4);
			gbc_opacityLabel.gridx = 0;
			gbc_opacityLabel.gridy = 1;
			ivjJPanel1.add(getOpacityLabel(), gbc_opacityLabel);
			GridBagConstraints gbc_opacityslider = new GridBagConstraints();
			gbc_opacityslider.weighty = 0.5;
			gbc_opacityslider.anchor = GridBagConstraints.NORTH;
			gbc_opacityslider.fill = GridBagConstraints.VERTICAL;
			gbc_opacityslider.gridx = 0;
			gbc_opacityslider.gridy = 2;
			ivjJPanel1.add(getOpacityslider(), gbc_opacityslider);
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
			constraintsJPanel3.insets = new java.awt.Insets(0, 4, 0, 4);
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
//			constraintsJLabel2.insets = new java.awt.Insets(0, 0, 0, 0);
			getJPanel3().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 1; constraintsJLabel3.gridy = 0;
			constraintsJLabel3.weightx = 1.0;
//			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
			constraintsJLabel.gridx = 2; constraintsJLabel.gridy = 0;
//			constraintsJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
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
//		add(getJPanel2(), "North");
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
		frame.setVisible(true);
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
				(0x00FF0000 & (colorModel.getRed(Math.min(255, i+colorOffset))<<16)) |
				(0x0000FF00 & (colorModel.getGreen(Math.min(255, i+colorOffset))<<8)) |
				(0x000000FF & (colorModel.getBlue(Math.min(255, i+colorOffset))));
		}
		getSurfaceCanvas1().setSurfacesColors(surfaceColors);
		updateSurfaceOpacity();
		if(argSurfaceCollection.getSurfaceCount()  < 2 || getgeometrySurfaceDescription1().getGeometry().getDimension() < 3){
			opacityslider.setVisible(false);
			opacityLabel.setVisible(false);
		}else{
			opacityslider.setVisible(true);
			opacityLabel.setVisible(true);
		}
		getSurfaceViewerTool1().resetView();
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
public void setGeometrySurfaceDescription(GeometrySurfaceDescription geometrySurfaceDescription) {
	GeometrySurfaceDescription oldValue = fieldGeometrySurfaceDescription;
	fieldGeometrySurfaceDescription = geometrySurfaceDescription;
	firePropertyChange("geometrySurfaceDescription", oldValue, geometrySurfaceDescription);
}


/**
 * Set the geometrySurfaceDescription1 to a new value.
 * @param newValue cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometrySurfaceDescription1(GeometrySurfaceDescription newValue) {
	if (ivjgeometrySurfaceDescription1 != newValue) {
		try {
			GeometrySurfaceDescription oldValue = getgeometrySurfaceDescription1();
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
private void setsurfaceCollection(SurfaceCollection newValue) {
	if (ivjsurfaceCollection != newValue) {
		try {
			ivjsurfaceCollection = newValue;
			this.onNewSurfaceCollection(getsurfaceCollection());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}


/**
 * Comment
 */
public void updateSurface() throws java.beans.PropertyVetoException, ImageException, GeometryException, ExpressionException {
	if (getGeometrySurfaceDescription()==null){
		javax.swing.SwingUtilities.invokeLater(new Runnable (){
			public void run(){
				getSurfaceCanvas1().setSurfaceCollection(null);
				getSurfaceCanvas1().repaint();
			}
		});
		return;
	}	
	
	getSurfaceCanvas1().setOrigin(getGeometrySurfaceDescription().getGeometry().getOrigin());
	getSurfaceCanvas1().setExtent(getGeometrySurfaceDescription().getGeometry().getExtent());
	getSurfaceViewerTool1().setDimension(new Integer(getGeometrySurfaceDescription().getGeometry().getDimension()));
	getGeometrySurfaceDescription().setFilterCutoffFrequency(new Double(getTaubinParameterFromSliderValue()));
	
	getSurfaceCanvas1().setDisableRepaint(false);
//	if (bResetAfterGenerate){	
		getSurfaceViewerTool1().resetView();
//	}else{
//		getSurfaceViewerTool1().fullRepaint();
//		//getSurfaceCanvas1().repaint();
//	}
}
	private void updateSurfaceOpacity(){
		//Transparency
		if(getsurfaceCollection() != null &&
				getsurfaceCollection().getSurfaceCount() > 1 &&
				opacityslider.getValue() != 100 &&
				getgeometrySurfaceDescription1().getGeometry().getDimension() > 2){
			AlphaComposite alphaComposite = java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER,(float)opacityslider.getValue()/100.0f);
			AlphaComposite[] surfaceTransparency = new AlphaComposite[getsurfaceCollection().getSurfaceCount()];
			for (int i = 0; i < surfaceTransparency.length; i++) {
				surfaceTransparency[i] = alphaComposite;
			}
			getSurfaceCanvas1().setSurfaceTransparency(surfaceTransparency);
		}else{
			getSurfaceCanvas1().setSurfaceTransparency(null);
		}
	}
	private JSlider getOpacityslider() {
		if (opacityslider == null) {
			opacityslider = new JSlider();
			opacityslider.setPaintLabels(true);
			opacityslider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if(!opacityslider.getValueIsAdjusting()){
						updateSurfaceOpacity();
						getSurfaceCanvas1().repaint();
					}
				}
			});
			opacityslider.setMajorTickSpacing(25);
			opacityslider.setValue(75);
			opacityslider.setPaintTicks(true);
			opacityslider.setOrientation(SwingConstants.VERTICAL);
		}
		return opacityslider;
	}
	private JLabel getOpacityLabel() {
		if (opacityLabel == null) {
			opacityLabel = new JLabel("Opacity");
		}
		return opacityLabel;
	}
}
