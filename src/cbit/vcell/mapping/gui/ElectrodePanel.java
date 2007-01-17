package cbit.vcell.mapping.gui;

import java.util.*;
import cbit.vcell.model.*;
/**
 * Insert the type's description here.
 * Creation date: (4/18/2002 9:29:41 AM)
 * @author: Anuradha Lakshminarayana
 */
public class ElectrodePanel extends javax.swing.JPanel {
	private Feature ivjFeature = null;
	private javax.swing.JLabel ivjFeatureLabel = null;
	private javax.swing.JLabel ivjFeatureValueLabel = null;
	private javax.swing.JLabel ivjLabelX = null;
	private javax.swing.JLabel ivjLabelY = null;
	private javax.swing.JLabel ivjLabelZ = null;
	private javax.swing.JLabel ivjPositionLabel = null;
	private javax.swing.JButton ivjSetCoordButton = null;
	private javax.swing.JButton ivjSetFeatureButton = null;
	private javax.swing.JTextField ivjTextFieldX = null;
	private javax.swing.JTextField ivjTextFieldY = null;
	private javax.swing.JTextField ivjTextFieldZ = null;
	private cbit.vcell.mapping.Electrode fieldElectrode = null;
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.vcell.geometry.Coordinate ivjCoordinateFactory = null;
	private cbit.vcell.mapping.Electrode ivjelectrode1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.geometry.Geometry ivjgeometry1 = null;
	private cbit.vcell.model.Model fieldModel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private Model ivjmodel1 = null;
	private cbit.vcell.geometry.Coordinate ivjCoordinate = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ElectrodePanel.this.getSetCoordButton()) 
				connEtoM6(e);
			if (e.getSource() == ElectrodePanel.this.getSetFeatureButton()) 
				connEtoC1(e);
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
			if (evt.getSource() == ElectrodePanel.this && (evt.getPropertyName().equals("model"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == ElectrodePanel.this.getelectrode1() && (evt.getPropertyName().equals("position"))) 
				connEtoM11(evt);
		};
	};
/**
 * ElectrodePanel constructor comment.
 */
public ElectrodePanel() {
	super();
	initialize();
}
/**
 * ElectrodePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ElectrodePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ElectrodePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ElectrodePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ElectrodePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ElectrodePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  (SetFeatureButton.action.actionPerformed(java.awt.event.ActionEvent) --> ElectrodePanel.selectFeature(QModel;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getmodel1() != null)) {
			this.selectFeature(getmodel1());
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
 * connEtoC2:  (geometry1.this --> ElectrodePanel.enableComponents()V)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(cbit.vcell.geometry.Geometry value) {
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
 * connEtoM10:  (Coordinate.this --> TextFieldZ.text)
 * @param value cbit.vcell.geometry.Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(cbit.vcell.geometry.Coordinate value) {
	try {
		// user code begin {1}
		// user code end
		getTextFieldZ().setText(this.getZString());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM11:  (electrode1.position --> Coordinate.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setCoordinate(getelectrode1().getPosition());
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
private void connEtoM2(cbit.vcell.model.Feature value) {
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
private void connEtoM4(cbit.vcell.model.Feature value) {
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
private void connEtoM5(cbit.vcell.mapping.Electrode value) {
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
 * connEtoM6:  (SetCoordButton.action.actionPerformed(java.awt.event.ActionEvent) --> electrode1.position)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.ActionEvent arg1) {
	cbit.vcell.geometry.Coordinate localValue = null;
	try {
		// user code begin {1}
		// user code end
		getelectrode1().setPosition(localValue = new cbit.vcell.geometry.Coordinate(new Double(getTextFieldX().getText()).doubleValue(), new Double(getTextFieldY().getText()).doubleValue(), new Double(getTextFieldZ().getText()).doubleValue()));
		
		// user code begin {2}
		System.out.println("Feature : "+ivjelectrode1.getFeature().toString());
		System.out.println("Co-ord : x="+ ivjelectrode1.getPosition().getX()+", y="+ivjelectrode1.getPosition().getY()+", z="+ivjelectrode1.getPosition().getZ());

		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setCoordinateFactory(localValue);
}
/**
 * connEtoM7:  (electrode1.this --> Coordinate.this)
 * @param value cbit.vcell.mapping.Electrode
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.mapping.Electrode value) {
	try {
		// user code begin {1}
		// user code end
		if ((getelectrode1() != null)) {
			setCoordinate(getelectrode1().getPosition());
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
 * connEtoM8:  (Coordinate.this --> TextFieldX.text)
 * @param value cbit.vcell.geometry.Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(cbit.vcell.geometry.Coordinate value) {
	try {
		// user code begin {1}
		// user code end
		getTextFieldX().setText(this.getXString());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM9:  (Coordinate.this --> TextFieldY.text)
 * @param value cbit.vcell.geometry.Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(cbit.vcell.geometry.Coordinate value) {
	try {
		// user code begin {1}
		// user code end
		getTextFieldY().setText(this.getYString());
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
/**
 * connPtoP3SetSource:  (ElectrodePanel.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getmodel1() != null)) {
				this.setModel(getmodel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetTarget:  (ElectrodePanel.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setmodel1(this.getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
private void enableComponents() {

	//
	// enable/disable coordinates
	//
	cbit.vcell.geometry.Geometry geometry = getGeometry();
	boolean enable = (geometry!=null);
	boolean enableX = enable;
	boolean enableY = enable;
	boolean enableZ = enable;

	if (geometry!=null){
		if (geometry.getDimension()<1){
			enableX = false;
		}
		if (geometry.getDimension()<2){
			enableY = false;
		}
		if (geometry.getDimension()<3){
			enableZ = false;
		}
	}
	getLabelX().setEnabled(enableX);
	getTextFieldX().setEnabled(enableX);
	getLabelY().setEnabled(enableY);
	getTextFieldY().setEnabled(enableY);
	getLabelZ().setEnabled(enableZ);
	getTextFieldZ().setEnabled(enableZ);
	getPositionLabel().setEnabled(enable);

	//
	// rest of the widgets.
	//
	getFeatureLabel().setEnabled(enable);
	getFeatureValueLabel().setEnabled(enable);
	getSetCoordButton().setEnabled(enable);
	getSetFeatureButton().setEnabled(enable);
	
	return;
}
/**
 * Return the Coordinate property value.
 * @return cbit.vcell.geometry.Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Coordinate getCoordinate() {
	// user code begin {1}
	// user code end
	return ivjCoordinate;
}
/**
 * Return the CoordinateFactory property value.
 * @return cbit.vcell.geometry.Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Coordinate getCoordinateFactory() {
	// user code begin {1}
	// user code end
	return ivjCoordinateFactory;
}
/**
 * Gets the electrode property (cbit.vcell.mapping.Electrode) value.
 * @return The electrode property value.
 * @see #setElectrode
 */
public cbit.vcell.mapping.Electrode getElectrode() {
	return fieldElectrode;
}
/**
 * Return the electrode1 property value.
 * @return cbit.vcell.mapping.Electrode
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.Electrode getelectrode1() {
	// user code begin {1}
	// user code end
	return ivjelectrode1;
}
/**
 * Return the Feature property value.
 * @return cbit.vcell.model.Feature
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Feature getFeature() {
	// user code begin {1}
	// user code end
	return ivjFeature;
}
/**
 * Return the FeatureLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFeatureLabel() {
	if (ivjFeatureLabel == null) {
		try {
			ivjFeatureLabel = new javax.swing.JLabel();
			ivjFeatureLabel.setName("FeatureLabel");
			ivjFeatureLabel.setText("Feature");
			ivjFeatureLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjFeatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFeatureLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2002 10:32:25 AM)
 * @return java.lang.String[]
 * @param model cbit.vcell.model.Model
 */
private String[] getFeatureNames(Model model) {
	Vector nameList = new Vector();
	cbit.vcell.model.Structure[] structures = model.getStructures();
	for (int i=0;i<structures.length;i++){
		if (structures[i] instanceof Feature) {
			nameList.add(structures[i].getName());
		}
	}
	
	String names[] = new String[nameList.size()];
	nameList.copyInto(names); 

	return names; 	
}
/**
 * Return the FeatureValueLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFeatureValueLabel() {
	if (ivjFeatureValueLabel == null) {
		try {
			ivjFeatureValueLabel = new javax.swing.JLabel();
			ivjFeatureValueLabel.setName("FeatureValueLabel");
			ivjFeatureValueLabel.setText("Please Select a Feature");
			ivjFeatureValueLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
public cbit.vcell.geometry.Geometry getGeometry() {
	return fieldGeometry;
}
/**
 * Return the geometry1 property value.
 * @return cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Geometry getgeometry1() {
	// user code begin {1}
	// user code end
	return ivjgeometry1;
}
/**
 * Return the LabelX property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabelX() {
	if (ivjLabelX == null) {
		try {
			ivjLabelX = new javax.swing.JLabel();
			ivjLabelX.setName("LabelX");
			ivjLabelX.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabelX;
}
/**
 * Return the LabelY property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabelY() {
	if (ivjLabelY == null) {
		try {
			ivjLabelY = new javax.swing.JLabel();
			ivjLabelY.setName("LabelY");
			ivjLabelY.setText("Y");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabelY;
}
/**
 * Return the LabelZ property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabelZ() {
	if (ivjLabelZ == null) {
		try {
			ivjLabelZ = new javax.swing.JLabel();
			ivjLabelZ.setName("LabelZ");
			ivjLabelZ.setText("Z");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabelZ;
}
/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
}
/**
 * Return the model1 property value.
 * @return cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Model getmodel1() {
	// user code begin {1}
	// user code end
	return ivjmodel1;
}
/**
 * Return the PositionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPositionLabel() {
	if (ivjPositionLabel == null) {
		try {
			ivjPositionLabel = new javax.swing.JLabel();
			ivjPositionLabel.setName("PositionLabel");
			ivjPositionLabel.setText("Tip Position");
			ivjPositionLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjPositionLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPositionLabel;
}
/**
 * Return the SetCoordButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSetCoordButton() {
	if (ivjSetCoordButton == null) {
		try {
			ivjSetCoordButton = new javax.swing.JButton();
			ivjSetCoordButton.setName("SetCoordButton");
			ivjSetCoordButton.setText("Set");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSetCoordButton;
}
/**
 * Return the SetFeatureButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSetFeatureButton() {
	if (ivjSetFeatureButton == null) {
		try {
			ivjSetFeatureButton = new javax.swing.JButton();
			ivjSetFeatureButton.setName("SetFeatureButton");
			ivjSetFeatureButton.setText("Set...");
			ivjSetFeatureButton.setMaximumSize(new java.awt.Dimension(63, 25));
			ivjSetFeatureButton.setMinimumSize(new java.awt.Dimension(63, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSetFeatureButton;
}
/**
 * Return the TextFieldX property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTextFieldX() {
	if (ivjTextFieldX == null) {
		try {
			ivjTextFieldX = new javax.swing.JTextField();
			ivjTextFieldX.setName("TextFieldX");
			ivjTextFieldX.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextFieldX;
}
/**
 * Return the TextFieldY property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTextFieldY() {
	if (ivjTextFieldY == null) {
		try {
			ivjTextFieldY = new javax.swing.JTextField();
			ivjTextFieldY.setName("TextFieldY");
			ivjTextFieldY.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextFieldY;
}
/**
 * Return the TextFieldZ property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTextFieldZ() {
	if (ivjTextFieldZ == null) {
		try {
			ivjTextFieldZ = new javax.swing.JTextField();
			ivjTextFieldZ.setName("TextFieldZ");
			ivjTextFieldZ.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextFieldZ;
}
/**
 * Comment
 */
private java.lang.String getXString() {
	if (getCoordinate() == null) {
		return "";
	} else {
		return (Double.toString(getCoordinate().getX()));
	}
}
/**
 * Comment
 */
private java.lang.String getYString() {
	if (getCoordinate() == null) {
		return "";
	} else {
		return (Double.toString(getCoordinate().getY()));
	}
}
/**
 * Comment
 */
private java.lang.String getZString() {
	if (getCoordinate() == null) {
		return "";
	} else {
		return (Double.toString(getCoordinate().getZ()));
	}
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
	this.addPropertyChangeListener(ivjEventHandler);
	getSetCoordButton().addActionListener(ivjEventHandler);
	getSetFeatureButton().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ElectrodePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(391, 70);

		java.awt.GridBagConstraints constraintsPositionLabel = new java.awt.GridBagConstraints();
		constraintsPositionLabel.gridx = 0; constraintsPositionLabel.gridy = 1;
		constraintsPositionLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPositionLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPositionLabel(), constraintsPositionLabel);

		java.awt.GridBagConstraints constraintsFeatureLabel = new java.awt.GridBagConstraints();
		constraintsFeatureLabel.gridx = 0; constraintsFeatureLabel.gridy = 0;
		constraintsFeatureLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsFeatureLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getFeatureLabel(), constraintsFeatureLabel);

		java.awt.GridBagConstraints constraintsFeatureValueLabel = new java.awt.GridBagConstraints();
		constraintsFeatureValueLabel.gridx = 1; constraintsFeatureValueLabel.gridy = 0;
		constraintsFeatureValueLabel.gridwidth = 7;
		constraintsFeatureValueLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsFeatureValueLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getFeatureValueLabel(), constraintsFeatureValueLabel);

		java.awt.GridBagConstraints constraintsSetFeatureButton = new java.awt.GridBagConstraints();
		constraintsSetFeatureButton.gridx = 8; constraintsSetFeatureButton.gridy = 0;
		constraintsSetFeatureButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSetFeatureButton.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSetFeatureButton(), constraintsSetFeatureButton);

		java.awt.GridBagConstraints constraintsTextFieldX = new java.awt.GridBagConstraints();
		constraintsTextFieldX.gridx = 3; constraintsTextFieldX.gridy = 1;
		constraintsTextFieldX.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTextFieldX.weightx = 1.0;
		constraintsTextFieldX.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTextFieldX(), constraintsTextFieldX);

		java.awt.GridBagConstraints constraintsLabelX = new java.awt.GridBagConstraints();
		constraintsLabelX.gridx = 1; constraintsLabelX.gridy = 1;
		constraintsLabelX.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLabelX(), constraintsLabelX);

		java.awt.GridBagConstraints constraintsLabelY = new java.awt.GridBagConstraints();
		constraintsLabelY.gridx = 4; constraintsLabelY.gridy = 1;
		constraintsLabelY.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLabelY(), constraintsLabelY);

		java.awt.GridBagConstraints constraintsTextFieldY = new java.awt.GridBagConstraints();
		constraintsTextFieldY.gridx = 5; constraintsTextFieldY.gridy = 1;
		constraintsTextFieldY.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTextFieldY.weightx = 1.0;
		constraintsTextFieldY.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTextFieldY(), constraintsTextFieldY);

		java.awt.GridBagConstraints constraintsLabelZ = new java.awt.GridBagConstraints();
		constraintsLabelZ.gridx = 6; constraintsLabelZ.gridy = 1;
		constraintsLabelZ.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLabelZ(), constraintsLabelZ);

		java.awt.GridBagConstraints constraintsSetCoordButton = new java.awt.GridBagConstraints();
		constraintsSetCoordButton.gridx = 8; constraintsSetCoordButton.gridy = 1;
		constraintsSetCoordButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSetCoordButton.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSetCoordButton(), constraintsSetCoordButton);

		java.awt.GridBagConstraints constraintsTextFieldZ = new java.awt.GridBagConstraints();
		constraintsTextFieldZ.gridx = 7; constraintsTextFieldZ.gridy = 1;
		constraintsTextFieldZ.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTextFieldZ.weightx = 1.0;
		constraintsTextFieldZ.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTextFieldZ(), constraintsTextFieldZ);
		initConnections();
		connEtoC3();
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
		ElectrodePanel aElectrodePanel;
		aElectrodePanel = new ElectrodePanel();
		frame.setContentPane(aElectrodePanel);
		frame.setSize(aElectrodePanel.getSize());
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
private void selectFeature(Model model) {
	String featureNames[] = getFeatureNames(model);
	if (featureNames == null || featureNames.length == 0) {
		cbit.vcell.client.PopupGenerator.showErrorDialog(this,"No defined feature present !");
		return;
	}
	String selection = (String)cbit.vcell.client.PopupGenerator.showListDialog(this,featureNames,"Select feature for electrode:");
	if (selection == null){
		return;
	}
	try {
		Feature feature = (Feature)model.getStructure(selection);
		getelectrode1().setFeature(feature);
	}catch (Exception e){
		handleException(e);
		cbit.vcell.client.PopupGenerator.showErrorDialog(this,"Error changing feature for electrode");
	}
}
/**
 * Set the Coordinate to a new value.
 * @param newValue cbit.vcell.geometry.Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCoordinate(cbit.vcell.geometry.Coordinate newValue) {
	if (ivjCoordinate != newValue) {
		try {
			ivjCoordinate = newValue;
			connEtoM8(ivjCoordinate);
			connEtoM9(ivjCoordinate);
			connEtoM10(ivjCoordinate);
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
 * Set the CoordinateFactory to a new value.
 * @param newValue cbit.vcell.geometry.Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCoordinateFactory(cbit.vcell.geometry.Coordinate newValue) {
	if (ivjCoordinateFactory != newValue) {
		try {
			ivjCoordinateFactory = newValue;
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
 * Sets the electrode property (cbit.vcell.mapping.Electrode) value.
 * @param electrode The new value for the property.
 * @see #getElectrode
 */
public void setElectrode(cbit.vcell.mapping.Electrode electrode) {
	cbit.vcell.mapping.Electrode oldValue = fieldElectrode;
	fieldElectrode = electrode;
	firePropertyChange("electrode", oldValue, electrode);
}
/**
 * Set the electrode1 to a new value.
 * @param newValue cbit.vcell.mapping.Electrode
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setelectrode1(cbit.vcell.mapping.Electrode newValue) {
	if (ivjelectrode1 != newValue) {
		try {
			cbit.vcell.mapping.Electrode oldValue = getelectrode1();
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
			connEtoM7(ivjelectrode1);
			firePropertyChange("electrode", oldValue, newValue);
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
 * Set the Feature to a new value.
 * @param newValue cbit.vcell.model.Feature
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setFeature(cbit.vcell.model.Feature newValue) {
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
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) {
	cbit.vcell.geometry.Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	firePropertyChange("geometry", oldValue, geometry);
}
/**
 * Set the geometry1 to a new value.
 * @param newValue cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometry1(cbit.vcell.geometry.Geometry newValue) {
	if (ivjgeometry1 != newValue) {
		try {
			cbit.vcell.geometry.Geometry oldValue = getgeometry1();
			ivjgeometry1 = newValue;
			connPtoP2SetSource();
			connEtoC2(ivjgeometry1);
			firePropertyChange("geometry", oldValue, newValue);
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
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}
/**
 * Set the model1 to a new value.
 * @param newValue cbit.vcell.model.Model
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmodel1(cbit.vcell.model.Model newValue) {
	if (ivjmodel1 != newValue) {
		try {
			cbit.vcell.model.Model oldValue = getmodel1();
			ivjmodel1 = newValue;
			connPtoP3SetSource();
			firePropertyChange("model", oldValue, newValue);
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAEFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8BF4D465192831521AEA5A45B53A3A2608162E29CD15D65A52EE34EC97AB2D9881218D1616F445DD5A66EC59ADEDF374E612C02281028641E354ECC5CDA004A081E2C088E440G23C492E04803E4B2F367114CABB3B76F84767B9F5FFF6F4C5C4904B95BBD27B9674B1D7B7D5F7B7F7E677D6F15CA5FCEA948B514CAD2EE01447F8E66CAD233D5125EF9685ED244553D1FD1A4E97E7E87E00B74E4E1
	BEDC1B20AC6D7399C5A552F7BC05F05F817779B7E594ED035F2FCABF705C5585BF2268D1206C76EF66CC1BDC4F5E0B28E7833D1FD9D98C57CDG5D00A3575477097E6FD6EE6778840EC748B5G2E0250AC29AC6338EC20A984DC83C0EB8523F7431599F0E3FD9D276B7852ECE96D0F83060DC40EC6A642C24E1B4D2F65521FF213AB092E97281D0427826E3B810873F9D26750AE389EEE6EF39C317638146AC12B6CF6D7FBACFE3F4B6B28F68C386A6BCF38E52B92705964E60BD7F6CB25BB093C13BB8BA93FA4E5
	022C940ABBEEA7BA4C705B8FB0FDA76265DE024F025FB9G253B502F0F5EBF4C6DB60FBDA2AD7E40133FB90ADFD53B18BF2D3BD43F0C5E950657E99D741BC92C1320EC8E408200B5G45D599C5D5GEF503A9F3E38932E47FA14C61F4763739E737B0F3BED01CF40ABB77C2E2F07920EBBA7DBFD811BA4317A4D5FD925E34FE44078DE39E6BE9613597B302ED34FBDA8E5BE7C77068DBAB1491CBDE6B0D631D8F4D429B1E13A4FC455BDDE951A0B213AAFCADA5D435E25F985BA3AE734AF4DCB62F2E6E9F42F29A2
	F57DA31A837361B761A3754970CBA93EE6370AE7F959C471645E8CE50BF7E3EE18B721AD267C19D2495E5BF563107CD82221391A59602C8E376557FDC4760CEA2673C3E14BCBEE02AF59236219ADFDB4CF49FDA2141977E694317C9AEDA36DF447BEE81FGE6GBFC092C0AAC07ABE4C31C7AF6E19E20EB5DB824A7181175BA687A446BBEAFE8D2E723092301C7286950B57AADF3084DC169E379C14B84D8DE692031E903E69B4772D2063E359AB87AC0A4B678569960F2C4801A0237912F98B2189483434556511
	C10147AF215CBB4C2F4255ED89AA677DB60BA22B942C7CABE6D2A703168BA0918440EF96172F3613F82D847F57819456E29C4AAE91F9E764GF4977575A61F2FEF405FCABC114C35A44EBD346FA8035F35359863FBBB886EAE381FA36434DCFAC5A34768329273AD0191ECE38B6D24DE96GFD9640BAA2FBFFC6D18DC093C047FE4C255F1AB1170C9B3FAED5AD39CDB7178ABB1666A97BD98EA56E8FEFD319D49731F78E14AD86D88BD0FAGFCGE8BD00634127F69257267D116D0C58C3EC3D16BE01C038EF517B
	9C8F553DD1271C342F3CEB54CB20004BA6F1FCB98A8D2B3FAD17C9AEA89007A4003483D80737DCAAE7ED6238A5A8E3ABB89B008416EDEA0DB27B7F234F4F6BA5CFC777C66E4796AAD7123682EC637A5F81F89BE01F44466903G6F823C8FD089309BE0AF40FE00D3G1DG7DA437C46CCA5CC4778230BB9BE07BC1E86BGCE00A40039GAB818A6B208EG4CG53GE683AC82D887D07EA1649D009FA099A09DE0B5C0C9FDC6D19DC0B740F400B400E5G52A14C197E5E745C787C46BAF81B7B7DCE185F1B386F1B
	387F7BF88C8ECE216ECF717844226B1CE2AE754711FFFFCB406AB20B56653556639BE1F5C96A71CDCDBD6E8A2B4BBD3C3E77727A0EE59B29778F27C037FF0A3ECEA5FF7E160175DD89ED243E64C1592B98E53F6C35410FA0AF3B390D54AB9F2F6DBE4173A50A7743785E8883EAD060EFEFA375EED114002BE7C011DBE4C5018936DAFE8F55959C68712BD38354F5FF9B49A55F900C6F027E01B6B2B6DAFDDE2FCCBBD141424B3FCE47E659ADFB4068C65F00D7E178075B497A25CF9E61F303C75A57CBE4ECF78F10
	F986DB9324D9C83E7A83AE8FF44DE42AA28D9FA26376484E6D1A98D5G0E2CEDCCF06DBE04E36D50BBB8566E987D1A145E65B7684DA1136F3B5510FE080DE50B8F29E3AD33E9F6073E6D8F52B9824787192D8F7988EF589205363FD725AD9F1376F79226BD8381196396B63E44E4B07A3DC81BD3CC6A275187D3FF179766C0A7ADD645C7269A3CFC77C1621FC3764144AAB032D760EFB21078F9489CCF48BAD779D208AC9CA3CFF7913F56023F0507E13CB80CE3644ECED20E739CBEB8B67A5CEED635D0B6FE1844
	7FDDC7A90FDF9D5CB79FE6715FA44A7FC1144F077BCCCDF94DC7247C28934B892C6165E5D046783FA64A07811774D138FC138B4BD320ECEEC4796FC47982A8DB29A9E7FEEE3BDCC2F2DC6E9F10E1FACBA604E7C49E3EF5791530328BBCA7D979FB17C9CE3AB5F34966A3C4E7079B5750A5C7183E0AA3ECFE56G5737C83CA95DFB9E243B6B881B434D016BFC002CA34CC6C637D650B59CE5F48DE1F489C7697A5E0BF46623EC3E60BF2A4A2D96723CBE24DBF4145935DAA3D7557B219F69F2B465C82F523DA868CA
	9BD4BF357218FD7F520FF4D58DEC7E625768D76D9B89A01D9168BAG46395CF98DCC5E76E3042EB5A87CE8E073A0A20F4C01D7B7E8750E0FA01D6D981BAB0D43B591A07598534B68CE0FA25DBCCE37982ED991F425E3C817FF1C511541356A38CAB777B821FBD65019605EF61C79C172A161B8330F654B1747053CC61E7F0DEC9EDF534872056755044883CEE7EEE4F3FD24E3767D658A52399B59DCB0C1C3275A37C6504D5068A3F2D39B35751BF2956996B632FAA0F42D1CDE0D33DDC23A2CC6B65F447A4DE954
	566F5B3922FD98D5B924BEAA0CDA3D4F8B3ABA8DDDD6845DB78CC8572C21A37501F44C5F01BC24F39A191F530C217EE69A895D1EFC24CBB63279B2710350259935F4CF893A0A93ECCE0DF48DA734F44182D19FA7583C9B6992CEE87D583A9E6952CE28F15346190DAF3F277BF8F4FF2C57B2E8992E8E8E4138DDDFFF1A0EB4ECAFEC95DD4F927A85A7F04FEBF3B5696B251350B7G541C447171290D3867B523EF36546ADB1297EDAF2F69A45F87BC290E0F798B077258D8B2231B68CC0532F9A7F19C794AE56D3A
	7BD8B068333A68121F77214DDD5A7113614EF711314A6E8A8415D3B04199467E363D0B58CB7684B81A6F11189C45929B6796015E420FF19FF1EF7DDB124EBEA299976B6BF188667BCC56FD980324517B246E5B6EB43C9CA5868D9FB35F3BBFD6E33078547EBCE64BE34216E4A8DF84D058C47BC887196310F10059D63673AF42B61F275A5563299E9874D4DBFA14EA5966D240BE9C6CD9FD8EBAF1EF53540475B6F1D2DBEF73B6CFC435B921096F0DB529B61B4E7C015B3C4809B6E7C1F959A934F9230B58CC7AA2
	C697F4A1DDAB50A48A3AB4CAE796F42F75A25DFC2049B905BE2FF26C50298F1C3F54572B3393A667C6B74A29BD1DD16486E0F3192D4EA2C9B3DF12F00ED362A1F6A8CED7104567BF0FB5E03F7560CCE96E06C4035EDE2E732B4906D927D9DC961ED66333A3EB05218E0059F256D34C5BD35999E813E75016EFD31DDE0BC7E673995F10F6BE13F8066DA725897AE7190DB0C9E7B6FE7DAD2D0D05169BF5EDCCFC5EE118FF065936660CEAE34A129506B9CB5046B73DA2DEA60817896BE315404F82DC26896B237488
	3952F936046DE07C925AB3ED619CE97B2BDF5435070E751FB0BB8EFF225A63DE3542B0ED955AF33B8FE31688B473BF41983C60A3F3E0BBEF000C36470F34B9C0D77BA95ADE510F363B81B7CB60FFAB701900ABBD0B781981441BG17G406A45A2EB6BE55ED9D6AFD9E75126226087F0355049EDB53BB44B813BAF6011ED9524172D368CC153A46B2E6AE334788479DD093977D11034CF3FAF68A2E54C163C9134E59D31239963FEE88FE97BEA36D758FC83B0FB2CAA8F3DD6E21DA8F5D8A729995F124C3D37C42D
	93E7B32B0B24E635CE1A327FF5C387G3369BEE1D3A65014FE067E970F123C34849CFBD93C1CB6ED3CCC1F31F8A9023EF6947318778D23A8F7BA50248B3A1F215C8F185CF53AF257FD06757751984AA9BE17D1E4BC07F2E60DF1B97B181C9D8EBDB96367D04E06F14C27A440ADB807F97062A53DBC10DA30EEF685ABA4B267A71D8B9FA2AACC6123C7B55DBFBFD4F901554F4CD735ED7870554B3A75B3FD3353D05A425BCC0BDABFF550FECD228DFFEFDC4491E852DB507FF7DCA43F302FE4381393585F717EF2C2
	649B705534A26F0FDC44B7EA3904E36619ABC86B843A94C17B50954C83C63775AA46F19950141C47B80D2FA7633A6CA54B1EC61F476F7312713B227282177F78D5145F84BC534EE33D5CAAA1FEAE60324F235E425C487653140B349597E0BDFF81E91F4E8D4D3DD78CE24C851AC597D057B201AF3C88F3320B087F47BC74E998F0E997D1EEC1BE413B3CE002456D9A15ABAA993DB99F69D783ED3999E350BE7A962D93455C2284E086D6D1E922BD055B62F5D4B354A1EC43FFFE93F3E407F116D43736AFEA9BB61B
	D9EECCB32BB9523AFCFF1EF3B94EB17EA46CD984B4051704BD855C1ECB135943E45CD500B29A00FFD815B146E5344516F1E4BD4AC8837E5517F06FF8F544333E72B67599C12D66B782FB8676C3BABFEDEAE34F4036897CB394BFCD036739C97164FEAE14A96D785C663C0D4C0FEBBAB20ACCGB600F1003BG66F5603CF4D59759FF1C743958A92F8253D60B3BA5E4838C1FCFF5131C6913C7787D5D5D5A311445611C0344E1D947541F2FB03EEE4A17FD8DFC4C6F6F52B12E3CB32368B0C0AB009BA03193FD7E0B
	25AA164F78FC0FBB4B6CD990279FCB2F598F56D6AA5B34E78B72371CB664C5E9AB291DCC763C4E70E7E3E34EE84F1BF3BA756DE1B1F453F12564B22C9F810CG9DG6317B106EECB4C3CB9B12C1BB7BCA64E78E219653C56D8B23E91D7FC7AEEF245272F2CB7BEFDBBFB6353F733BB5619063BAE074AE4FCF74464CB8F63E3797118132C5117C159DA00622E0C228AG930093A0318B73E41E65F82CBCB9069B6F2DBE7133B1FC739C6DBD6F8937F7D6D728FD7AFEDAA3785E8A77B3CC8E5FE330C759E3B0D9FAF0
	0F617E21C22EE7D997763D87FBC9FCCA3A217DG98813A8192GD2G66F7E3FCB6759C0A959FBA41065850F0C4860679770EB73E7A5F9B13AF2BDB0F6F79B8757DA1A6DFCE37DE3EBD52C762D9EA01799CC093008DE0BA40AC0085960CE7F7CF4CFC93C7B02612EFF77B624B3787A2784273ED25C50F4F690FCF5FE084DF383EF53A7A2A7B632B4743B179CAFA74785646294F900845D7212BEFF6CCBEFDFD5F0E4957502357DFECEEA1792A53DF9C9FD0947A3C08759949B26ECB1AFBF03ED8B4921F2D5BE272A5
	756835297D74F94AECA81B8F1089308E204C1AD1D48750E145B675F04FD1699A5A54247D5483F17A781D18FC63D63D7AF83722CC3FBEDAE159F452A53BED2CBE6E31E1FD4C30E2FD0C0C46E76BCDB1799606594AEC58A3F2A2CB5850B8961F8D1F4564AB336959506EC09BEAEDE8434671787A24B7A2782A436E5D3AB69CE8A3B610B9F0028D4753D47ACCEF9E5CAF834886A816E1ACGB08178E54C55826BC731F2150F270D00EB72797AF43AFEEE7F239176CFAD76CF4664CB127578EE1E08F38E9713EF362E3E
	4F2E44272FABA6DF3A2E3EB72E4617C71591FC61F934CCD75F1391FCD373EFC5CC3EECDDFD37C971693BB7A6DF21DD0F6F535C7874F54464AB5755375D901F3E77E27255696AFBB6CEFDEBE272B56B6A1B1597DFFEBE9441971E1FCEDDFD17736353670E608B57B7ADCC9F6BC32B687904E4A81B833090E0B5C0318356EEGA6877621E66BB1E96A63FD6CFED4AA084F4FCD91FC61FE3A9DFAFCB363541796D3DF022EBE637A78741D0B608B1F335F95260F1D51FE11BE531E83E58BG98AE330A605640FD3E9347
	6D472B097C8A38B782F4G0C83A48324814C87D886108D106F4AA8AAF7A15F8A4AD78777E697BBDFC34EEC10F38943F01F6CE2E78F663A581994F23E00776F55E9F0A532B6F13D616DEE3E86C7F4931D9D9ADC1A8E8F42B8AFB3C621C95641E7CFA2AF34AECED35B37F21C5EF96C4D1A5F64BC5AB64E0BB83D335D88C57CDA9025DC6F7C762BB16CA6405A6BE5DB74331032CD7FACE41FCD7FACE43FED8B43038C6D1B90D8BEE21B7C2C64F5B63D3312EF5218AE254FA18283DE45651129DAB6C7170749F9D968D5
	4E8DF8D917C3B4874377E2050E4764687EBEAE6B7B7B242C6F6F92F9F2FFD748517C657B247D64994CB297761D0A7D83A9326FBCE1F3A93EGBA54AADB82E76DF631795062172D58363FCC4FB72CF531B3FADC26A33A3FE38EFDFF2FF3687BFB13E3F2FFEFF5C473175561F38AE9A391E7DBBF0551C042E7400F390BB8CD3362EB5EABE4AFF4C67763E9273E9F4B1D24DD85FD83812B9CEE6BCA67F6D1260EBE279459C34CE1B44FD3FE4592F048913EE6D3FED6960D7F0FCE52B6FBDCB6D7001F78E4F1F87C354D
	B40E41209C248B8A23C531F07F93BD687F89457749DE5147BCB024B7AFA96CE5FDD499DCEBFAC37BABE643315E68F1EB6A550F5B273D5163F63EB7F65CAE77C60F1B5C9BBBEE5E5EE8F944E254665546688301AF773D0C39E38F78BC10BD7C7D2D3E68B1D850279F031F76C50F41123E58B1F836AFFA8CFE5D97BB863F6D8B4F9D6663E21F5677FD4277FB7CDAFC2D404B7DDA7CFE015F95880D55879C7F0B00167E00483F3BE53D7CB3721CB377C60E15CCDE43C8081ECF30CE0EFA225749C90FFE1D18BC516B24
	45933BCEBABC516B446A09DDA7EE4F64F9792BD0DF1B77615A40B6591A429D963BE93D78ACBDEDB4D4DEAD17D72BDB9788B342644D9672C647C2656D8FF9D69ACD5E3CB0F90B04BC6338A6D79A553E6AD01B1E3CACAEE79DDC0B7BE07E56274AE5717D36B7FACEFC5F2B1F93BF74C64F099FFBE36744535E68B971B3EF6C1CD8651DBCA7560DEB47B3352FFAECC20BD75B7174AB21F5F4G5FBD382A0D35AAE73D24ED2F8785FE2194AA67A0475F182B252F9374E78DDA7C07823FB56FA58DFEEF8D62FF1E2735C7
	357F367C903D0D585E8F7823576DC7FE7D3AED74C72F5B537E58F55B620FDE37ED7E58F5EB75CFDE37878BC2FD45BEF4F998FE9F470F2D8F0925F82F6435DD64DD9337E544B720C8D2CA9F194B6D5E056F6454423D89C081185E474EDA31396FB707089EBB2C2641ACBA919CF7935E5FD7A16FEA204F81A8837CE1008EB7365D5F0C1158B47333C274012470EB718EB25766A737E891438F3E39C5455357B8C2791EFE133C8782E162A46C31854F6BF2623B58B8B7A3CF76C162BB15A3F0531B846E92626ABF8C61
	9778BF3FADE85B90775C0ECD4C8EFABC909F1E3032271AC3643461BBB45B77681DB91D6F46B3EFAF681EC9DDA64AFFE555ABDFA34A975A744AA58F164F346B1517FAF09E6F6F5F284DC5BEE7E6F91064A47D6E202537F5440F6FE2BFC61FABD7GFF1D8757796F8F63BA5FA8F02BFD84B7836ED385EE8EFD16108E770B846EF5BB72E68A5C77EC082B72A26EA6AB410D43FD12401D1111EE1640FDCC652D057BC29F62069CC8D7A6F04FD1DEB75CCF9338F3F40FFF865C4F9238C75A10B7CD60BE5FC7F012BF23E8
	3B9FF17F6EA538AA38B77A71AC5B79DDA45F495998B360127C626CFBF748739FF2FA353E7E63004BF65C62B051564A1EB95C3D2B1073AF7063FB82CFD019D2BF68862869477366436F61F973547BE7CACDFD7A6F892CEC1DB0B4757377847A55F3B9E66F8A8353797AEE54B99B4AD77653186E5666904977AFCAEC9F0A150F041413367CCCC87983A165445E4DF95A7295A165A4C617B8BF736D09F751371CA752A477379222FE4F24A240FCB271EBD2569D3969EF0C65B15F4E6E914FF0201C61768FA3EE1E403D
	AE1E3B1587911727BE9B953865F644A585317FDBBE04679F97844977E01612D7A12486051E499BB6F2BB0B957DF9ADFE6B027CAE879A82DB390EDA051D3143773EDF15DE7451F7B1FC815991708DF8ED12B4076A5966A7F6CD075FB394346B89B9542ECD232ADDB5F634EB1D0EDDDB38BE04628118E78D28F6158F28F6B1F999B614D7B590A9EFC3183C8620E95548F38E047B790FD642E3753987BC5EA07771DB34BFA0BC8A50B33DBDB26ACD50517BF2185EE420C95568CD0F50FB599E29F7400EFA978A3D37B8
	D0EF260E5E4DE1FA3301A6FFD0553BFDB0DC6F8BF2245E2DC26F0EC12CD3D7DBE81D6E9DD36B7427EDE8D36D20FE1D66EAECEA821AA60DCD9D91B62550F52347B26C728CF8DADC23B237EBAE6D8B1DC02F883BF269FA99A68FE1342F53B1A18568D287F1FF36FDD72C67A2FA7933C5F24549B7731A7958B9573B2BC83F3BCC9397063F1F6233B4F85677BFA57A7EECA8EB9D42E7462563E48EB5976E97832C81489F4EA85A8150B00CE3723AFE188ECF7E2C235567B74124572D59AEE37E3D357BDA7DE3FD6C239B
	300F1D735DFF1266FE4E9875FD2556E116CF8A3FD62C3AA317DC79B7DEA608FF3320AC9DA08BA08720E4A42328E2847DEB8870332EE48B721822D82CCE7A061958DCF549C3AA1ACCC019EF2DFB74FCBBBC426CE866D79BDC0779EF46D7BFEC5661CB9C89ED97A9A331776567F01A859ADA9EF75D35F8A6274B56114D788AF5575C79230CFE7BE8B43EE76DFAFCD51C3EC1070F5573136D5A733BA5F73A8D183791754CE5F40F2A7384B66E7906453994A8FBDBE24F85681C087F317E68112B12507B4BE24C2F3287
	6E150C4F988D6FC4513B106BAB9BD375E6854B73184CC48311467A19DF5377C5448BC00DBE37AFD05D6336D87B184E14A12D2FF5AFDD2F7BED816AAB576564571C2DF7F2DFADE20CCF991B4C577FA6A6C961F305E53ABC59C7234BF93DC075EF2115B8CB5646CC6E5F8939A56357AA7770FAD5EE22BDD46EBF0B73B5CD57AC37EB03AA37AACC6E0BEDA8F71A2EDC14F1FD217A7B79A2D55EAF3C645923D79EAA97F5ECF4C117C0BEAAD5F1E0DFD579877BAB112F3DC365BBE31B8A9FBF67C01A917F1D2A797D4CAE
	3DBE637F8A180E6C3F2A8E56573EB99EFDFFE447387E7E484EF17D67B4B56313BFE72B9B0F7ECD1223635AEF123031E2D7A7E93FF3A1B796832C83A81E00B96F840E93BF99A76F6231535861E344891B036C4F10AF1FA928BFEDA23A2F0FCC447EDEDA7A8419CF7778E0DD6FA172917F2389D2DF0A4FCF118C77845DE78B389CCE0511F27CD393F4DDA95BA91A2D8D7B683A63B078E51A40756267EAF0DDE9963893F4EDBD9B6E9788DCB245E5423DF485F19D347FAD057B9D8277A8455540FD2B40ED9DC19DCE01
	1BB5C0F0F361FE19406DE76BED384F9138BB459A3C70AA625E267D0F996E1D82F75A09F40A407D976DB367437DE201EB93EB7015D7F1AE54F4D992F133DCA15FA53AE06955FDBAF99A5A71591EDE592A34DA9C227DA8435AB130E69A2C3D27CDBE06B33D41AEB2B7B6832D82108C1086B09FA093A09BA07FBAD8DF8354GB481F4G788192GE6G4C8618F79D66E94E554AE8368312AF6CE93286ACAE122E5C0E65741D7CC5A0E70D107514E157A4321A83BE3F9CD0C69816EE1571D858C366834557C3BB3AFE72
	D8684DBFEA0127E9CAFC49A1FC9D40631F925F7C10E782894013BCA53E7CF6ADDFAA70244760E3715C57993DCF286F0C5DA7B4F652EFA0C0DBA61BE07C79E6A799CCFDF6FBD096FD4FA71DA582F7AA087B1FECBC3AFECF68BA6DF48F49FB36CE3B3B8657E90BF57C512E51882C869A8238CE4B39BEFC1D56B1C446D827AF609A75F9950B7B185B6570121E132F418607F0CCAE3C8157EB5F98FAAD04076CE22287382770E341D7AB78024321FEF92C1230E359F0EC193F9773486E9BF0ADDDB50271C83CC1BF9E5A
	1824G8D810C471C9B4263713D01496331E4GEDC897FE19EC21FEA559547A7A26585BD823E3DFDE98C889401F406CA3B615A6045BF7A0ECDF66E1D955E5F1222E72847D793CD6D7AD5054EAF4B5C568AAF6046DCD59D5DD7DE28F23D5C75746B0DDCE20F1EAF40DEBF4317E7EE6DA3753B91E6112A8AE056378590F2E686D73162E586D7376AE7DB17B6E2E48B1FBE6177E18BD3BCBBBE6736F21F5111CF0C05FED96F48F895CA501FB044AA4380BAE1B6294786FD33B883EC9A68A45F3C5EA57904519712C0086
	F70963FE52C57A98F099D297F9B33AC87D59DDEEB73BDFCAE9DCDE68F2C4BCFECEEDF479AD9A3BD78A1CEA77335D24DE45DC0E8DA5F80E203BD4523CEBCDBF8943DEAFE675B8E10F2C5B6B6D5A3A554E97C269F64811F45F50211B23C377F3C7A45DCAC7A45D9B6D11F4656DDABA36BE79E98D367DF989132DCF7EC8269C3C8DFDF10850E9DE5B28EEA17FF1AA9A2CB61DB8594AE9FFF98D34AF0DCC0516595DB902F62F1D546EA617EA771A01294B7F15587F2E3AF1B279FD3D1208A5BBA3C6F8EC1372BC63D6F9
	E60AF567DD13722C77CA92BE13323C27798EGEDF27C1BBD439BD5BCB6B9BEF7F83AC6BBF7D88C3AD65FB815B9C7CF487BBDB940D372392970BD9732973483F86A2644F7FB48FB51A660691E925FD7C366D48A70CC0F41471FD955C436135F55C436271111C83AABA311F4AF58A26972F45A71034EC83A071D11F4BF50E96FBF8CE96F6CBB7D7F367E9E834C89FFA395A85236E637EC894A65AD74ABA16516F27E4CD8117248FAD5DA8F7F2B2B2B0381E933589CB8C5FB5015037B6A73D65D1BE71072CFC96B5908
	F2CA4AA74342A929000E9A2724BC98D1064917FF7335G52B605EEC3655E376D0612DB778D9824421352FA3B456DF7DAC08A7D8601C9FA39070A1383C02BC8A574AB8EFB00F78F65BDE4128C5620A23D21622D844FCADB7C962B8CA4C5B6CB200FCA68172EC86B5B25A2FB4051C395A812217DE4BB2825F7243850A43D24B84902190C7E90A5D5F4B3D9127002C354B569C5GBF7D8B92663C88F77FDA580074CA640E4D31DB16BF135AE5ACB5C8862897274CAA22E00834FDEAAFD9CC9D2592CB70CE110ADBDD7E
	F21CF0AA5286118639E600CBGED7F3F4E6177932EEA77A4575DE80D3AA73976732C8D97FEC15D87E56B46943AEEB4823E7B8B38EE4C6EA25F7A0D36FEEE71D85CEE187D769D735A48DA7A7808A22B7BB6775139C090E9F8A1ABCB6DA6F677EAF87E97D0CB8788B0839D36319EGGF8E3GGD0CB818294G94G88G88GAEFBB0B6B0839D36319EGGF8E3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6B9EGGGG
**end of data**/
}
}
