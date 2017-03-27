/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import java.awt.GridBagConstraints;
import java.util.Vector;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.Electrode;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
/**
 * Insert the type's description here.
 * Creation date: (4/18/2002 9:29:41 AM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class ElectrodePanel extends javax.swing.JPanel {
	private Feature ivjFeature = null;
	private javax.swing.JLabel ivjFeatureLabel = null;
	private javax.swing.JLabel ivjFeatureValueLabel = null;
	private javax.swing.JButton ivjSetFeatureButton = null;
	private Electrode fieldElectrode = null;
	private boolean ivjConnPtoP1Aligning = false;
	private Electrode ivjelectrode1 = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Geometry fieldGeometry = null;
	private boolean ivjConnPtoP2Aligning = false;
	private Geometry ivjgeometry1 = null;
//	private Model fieldModel = null;
	private boolean ivjConnPtoP3Aligning = false;
//	private Model ivjmodel1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ElectrodePanel.this.getSetFeatureButton()) 
				selectFeature();
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ElectrodePanel.this && (evt.getPropertyName().equals("electrode"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == ElectrodePanel.this.getFeature() && (evt.getPropertyName().equals("name"))) 
				connEtoM1(evt);
			if (evt.getSource() == ElectrodePanel.this.getelectrode1() && (evt.getPropertyName().equals("feature"))) 
				connEtoM3(evt);
			if (evt.getSource() == ElectrodePanel.this && (evt.getPropertyName().equals("geometry"))) 
				connPtoP2SetTarget();
//			if (evt.getSource() == ElectrodePanel.this && (evt.getPropertyName().equals("model"))) 
//				connPtoP3SetTarget();
		};
	};
/**
 * ElectrodePanel constructor comment.
 */
public ElectrodePanel() {
	super();
	initialize();
}

///**
// * connEtoC1:  (SetFeatureButton.action.actionPerformed(java.awt.event.ActionEvent) --> ElectrodePanel.selectFeature(QModel;)V)
// * @param arg1 java.awt.event.ActionEvent
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connEtoC1(java.awt.event.ActionEvent arg1) {
//	try {
//		// user code begin {1}
//		// user code end
//		if ((getmodel1() != null)) {
//			this.selectFeature(getmodel1());
//		}
//		// user code begin {2}
//		// user code end
//	} catch (java.lang.Throwable ivjExc) {
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}
/**
 * connEtoC2:  (geometry1.this --> ElectrodePanel.enableComponents()V)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(Geometry value) {
	try {
		// user code begin {1}
		// user code end
		this.enableComponents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ElectrodePanel.initialize() --> ElectrodePanel.enableComponents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.enableComponents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (Feature.name --> FeatureValueLabel.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getFeatureValueLabel().setText(String.valueOf(getFeature().getName()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (Feature.this --> FeatureValueLabel.text)
 * @param value cbit.vcell.model.Feature
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(Feature value) {
	try {
		// user code begin {1}
		// user code end
		if ((getFeature() != null)) {
			getFeatureValueLabel().setText(getFeature().getName());
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
 * connEtoM3:  (electrode1.feature --> Feature.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setFeature(getelectrode1().getFeature());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (Feature.this --> electrode1.feature)
 * @param value cbit.vcell.model.Feature
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(Feature value) {
	try {
		// user code begin {1}
		// user code end
		getelectrode1().setFeature(getFeature());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM5:  (electrode1.this --> Feature.this)
 * @param value cbit.vcell.mapping.Electrode
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(Electrode value) {
	try {
		// user code begin {1}
		// user code end
		if ((getelectrode1() != null)) {
			setFeature(getelectrode1().getFeature());
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
 * connPtoP1SetSource:  (ElectrodePanel.electrode <--> electrode1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getelectrode1() != null)) {
				this.setElectrode(getelectrode1());
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
 * connPtoP1SetTarget:  (ElectrodePanel.electrode <--> electrode1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setelectrode1(this.getElectrode());
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
 * connPtoP2SetSource:  (ElectrodePanel.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getgeometry1() != null)) {
				this.setGeometry(getgeometry1());
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
 * connPtoP2SetTarget:  (ElectrodePanel.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setgeometry1(this.getGeometry());
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
///**
// * connPtoP3SetSource:  (ElectrodePanel.model <--> model1.this)
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connPtoP3SetSource() {
//	/* Set the source from the target */
//	try {
//		if (ivjConnPtoP3Aligning == false) {
//			// user code begin {1}
//			// user code end
//			ivjConnPtoP3Aligning = true;
//			if ((getmodel1() != null)) {
//				this.setModel(getmodel1());
//			}
//			// user code begin {2}
//			// user code end
//			ivjConnPtoP3Aligning = false;
//		}
//	} catch (java.lang.Throwable ivjExc) {
//		ivjConnPtoP3Aligning = false;
//		// user code begin {3}
//		// user code end
//		handleException(ivjExc);
//	}
//}
///**
// * connPtoP3SetTarget:  (ElectrodePanel.model <--> model1.this)
// */
//private void connPtoP3SetTarget() {
//	try {
//		if (ivjConnPtoP3Aligning == false) {
//			ivjConnPtoP3Aligning = true;
//			setmodel1(this.getModel());
//			ivjConnPtoP3Aligning = false;
//		}
//	} catch (java.lang.Throwable ivjExc) {
//		ivjConnPtoP3Aligning = false;
//		handleException(ivjExc);
//	}
//}
/**
 * Comment
 */
private void enableComponents() {

	//
	// enable/disable coordinates
	//
	Geometry geometry = getGeometry();
	boolean enable = (geometry!=null);

	//
	// rest of the widgets.
	//
	getFeatureLabel().setEnabled(enable);
	getFeatureValueLabel().setEnabled(enable);
	getSetFeatureButton().setEnabled(enable);
	
	return;
}

/**
 * Gets the electrode property (cbit.vcell.mapping.Electrode) value.
 * @return The electrode property value.
 * @see #setElectrode
 */
public Electrode getElectrode() {
	return fieldElectrode;
}
/**
 * Return the electrode1 property value.
 * @return cbit.vcell.mapping.Electrode
 */
private Electrode getelectrode1() {
	return ivjelectrode1;
}
/**
 * Return the Feature property value.
 * @return cbit.vcell.model.Feature
 */
private Feature getFeature() {
	return ivjFeature;
}
/**
 * Return the FeatureLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getFeatureLabel() {
	if (ivjFeatureLabel == null) {
		try {
			ivjFeatureLabel = new javax.swing.JLabel();
			ivjFeatureLabel.setName("FeatureLabel");
			ivjFeatureLabel.setText("Feature : ");
			ivjFeatureLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjFeatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFeatureLabel;
}
/**
 * Return the FeatureValueLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getFeatureValueLabel() {
	if (ivjFeatureValueLabel == null) {
		try {
			ivjFeatureValueLabel = new javax.swing.JLabel();
			ivjFeatureValueLabel.setName("FeatureValueLabel");
			ivjFeatureValueLabel.setText("not set");
			ivjFeatureValueLabel.setForeground(java.awt.Color.blue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFeatureValueLabel;
}
/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public Geometry getGeometry() {
	return fieldGeometry;
}
/**
 * Return the geometry1 property value.
 * @return cbit.vcell.geometry.Geometry
 */
private Geometry getgeometry1() {
	return ivjgeometry1;
}

///**
// * Gets the model property (cbit.vcell.model.Model) value.
// * @return The model property value.
// * @see #setModel
// */
//public Model getModel() {
//	return fieldModel;
//}
///**
// * Return the model1 property value.
// * @return cbit.vcell.model.Model
// */
//private Model getmodel1() {
//	return ivjmodel1;
//}

/**
 * Return the SetFeatureButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSetFeatureButton() {
	if (ivjSetFeatureButton == null) {
		try {
			ivjSetFeatureButton = new javax.swing.JButton();
			ivjSetFeatureButton.setName("SetFeatureButton");
			ivjSetFeatureButton.setText("Set...");
			ivjSetFeatureButton.setMaximumSize(new java.awt.Dimension(63, 25));
			ivjSetFeatureButton.setMinimumSize(new java.awt.Dimension(63, 25));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSetFeatureButton;
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
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getSetFeatureButton().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
//	connPtoP3SetTarget();
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ElectrodePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(391, 70);

		java.awt.GridBagConstraints constraintsFeatureLabel = new java.awt.GridBagConstraints();
		constraintsFeatureLabel.gridx = 0; constraintsFeatureLabel.gridy = 0;
		constraintsFeatureLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsFeatureLabel.insets = new java.awt.Insets(4, 10, 4, 4);
		constraintsFeatureLabel.anchor = GridBagConstraints.LINE_START;
		add(getFeatureLabel(), constraintsFeatureLabel);

		java.awt.GridBagConstraints constraintsFeatureValueLabel = new java.awt.GridBagConstraints();
		constraintsFeatureValueLabel.gridx = 1; constraintsFeatureValueLabel.gridy = 0;
		constraintsFeatureValueLabel.weightx = 1.0;
		constraintsFeatureValueLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsFeatureValueLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getFeatureValueLabel(), constraintsFeatureValueLabel);

		java.awt.GridBagConstraints constraintsSetFeatureButton = new java.awt.GridBagConstraints();
		constraintsSetFeatureButton.gridx = 2; constraintsSetFeatureButton.gridy = 0;
		constraintsSetFeatureButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSetFeatureButton.insets = new java.awt.Insets(4, 4, 4, 10);
		constraintsSetFeatureButton.anchor = GridBagConstraints.LINE_END;
		add(getSetFeatureButton(), constraintsSetFeatureButton);

		initConnections();
		connEtoC3();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ElectrodePanel aElectrodePanel;
		aElectrodePanel = new ElectrodePanel();
		frame.setContentPane(aElectrodePanel);
		frame.setSize(aElectrodePanel.getSize());
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

interface ElectrodeFeatureListProvider {
	Feature[] getFeatures(ElectrodePanel electrodePanel);
}
private ElectrodeFeatureListProvider electrodeFeatureListProvider;
public void setElectrodeFeatureListProvider(ElectrodeFeatureListProvider electrodeFeatureListProvider){
	this.electrodeFeatureListProvider = electrodeFeatureListProvider;
}
/**
 * Comment
 */
private void selectFeature() {
	Feature features[] = (electrodeFeatureListProvider!=null?electrodeFeatureListProvider.getFeatures(this):new Feature[0]);
	if (features == null || features.length == 0) {
		PopupGenerator.showErrorDialog(this,"No defined feature present !");
		return;
	}
	String[] featureNames = new String[features.length];
	for (int i = 0; i < featureNames.length; i++) {
		featureNames[i] = features[i].getName();
	}
	String selection = (String)PopupGenerator.showListDialog(this,featureNames,"Select feature for electrode:");
	if (selection == null){
		return;
	}
	Feature feature = null;
	for (int i = 0; i < features.length; i++) {
		if(features[i].getName().equals(selection)){
			feature = features[i];
			break;
		}
	}
	try {
		if(feature == null){
			throw new Exception("Couldn't find Structure for selected name '"+selection+"'");
		}
		getelectrode1().setFeature(feature);
	}catch (Exception e){
		handleException(e);
		PopupGenerator.showErrorDialog(this,"Error changing feature for electrode", e);
	}
}

/**
 * Sets the electrode property (cbit.vcell.mapping.Electrode) value.
 * @param electrode The new value for the property.
 * @see #getElectrode
 */
public void setElectrode(Electrode electrode) {
	Electrode oldValue = fieldElectrode;
	fieldElectrode = electrode;
	firePropertyChange("electrode", oldValue, electrode);
}
/**
 * Set the electrode1 to a new value.
 * @param newValue cbit.vcell.mapping.Electrode
 */
private void setelectrode1(Electrode newValue) {
	if (ivjelectrode1 != newValue) {
		try {
			Electrode oldValue = getelectrode1();
			/* Stop listening for events from the current object */
			if (ivjelectrode1 != null) {
				ivjelectrode1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjelectrode1 = newValue;

			/* Listen for events from the new object */
			if (ivjelectrode1 != null) {
				ivjelectrode1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM5(ivjelectrode1);
			firePropertyChange("electrode", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the Feature to a new value.
 * @param newValue cbit.vcell.model.Feature
 */
private void setFeature(Feature newValue) {
	if (ivjFeature != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjFeature != null) {
				ivjFeature.removePropertyChangeListener(ivjEventHandler);
			}
			ivjFeature = newValue;

			/* Listen for events from the new object */
			if (ivjFeature != null) {
				ivjFeature.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM2(ivjFeature);
			connEtoM4(ivjFeature);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(Geometry geometry) {
	Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	firePropertyChange("geometry", oldValue, geometry);
}
/**
 * Set the geometry1 to a new value.
 * @param newValue cbit.vcell.geometry.Geometry
 */
private void setgeometry1(Geometry newValue) {
	if (ivjgeometry1 != newValue) {
		try {
			Geometry oldValue = getgeometry1();
			ivjgeometry1 = newValue;
			connPtoP2SetSource();
			connEtoC2(ivjgeometry1);
			firePropertyChange("geometry", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
///**
// * Sets the model property (cbit.vcell.model.Model) value.
// * @param model The new value for the property.
// * @see #getModel
// */
//public void setModel(Model model) {
//	Model oldValue = fieldModel;
//	fieldModel = model;
//	firePropertyChange("model", oldValue, model);
//}
///**
// * Set the model1 to a new value.
// * @param newValue cbit.vcell.model.Model
// */
//private void setmodel1(Model newValue) {
//	if (ivjmodel1 != newValue) {
//		try {
//			Model oldValue = getmodel1();
//			ivjmodel1 = newValue;
//			connPtoP3SetSource();
//			firePropertyChange("model", oldValue, newValue);
//		} catch (java.lang.Throwable ivjExc) {
//			handleException(ivjExc);
//		}
//	};
//}

}
