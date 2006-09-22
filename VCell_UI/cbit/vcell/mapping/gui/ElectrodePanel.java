package cbit.vcell.mapping.gui;

import java.util.*;

import cbit.util.Coordinate;
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
	private cbit.vcell.modelapp.Electrode fieldElectrode = null;
	private boolean ivjConnPtoP1Aligning = false;
	private Coordinate ivjCoordinateFactory = null;
	private cbit.vcell.modelapp.Electrode ivjelectrode1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.geometry.Geometry ivjgeometry1 = null;
	private cbit.vcell.model.Model fieldModel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private Model ivjmodel1 = null;
	private Coordinate ivjCoordinate = null;

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
 * @param value Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(Coordinate value) {
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
private void connEtoM5(cbit.vcell.modelapp.Electrode value) {
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
	Coordinate localValue = null;
	try {
		// user code begin {1}
		// user code end
		getelectrode1().setPosition(localValue = new Coordinate(new Double(getTextFieldX().getText()).doubleValue(), new Double(getTextFieldY().getText()).doubleValue(), new Double(getTextFieldZ().getText()).doubleValue()));
		
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
private void connEtoM7(cbit.vcell.modelapp.Electrode value) {
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
 * @param value Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(Coordinate value) {
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
 * @param value Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(Coordinate value) {
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
 * @return Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Coordinate getCoordinate() {
	// user code begin {1}
	// user code end
	return ivjCoordinate;
}
/**
 * Return the CoordinateFactory property value.
 * @return Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Coordinate getCoordinateFactory() {
	// user code begin {1}
	// user code end
	return ivjCoordinateFactory;
}
/**
 * Gets the electrode property (cbit.vcell.mapping.Electrode) value.
 * @return The electrode property value.
 * @see #setElectrode
 */
public cbit.vcell.modelapp.Electrode getElectrode() {
	return fieldElectrode;
}
/**
 * Return the electrode1 property value.
 * @return cbit.vcell.mapping.Electrode
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelapp.Electrode getelectrode1() {
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
 * @param newValue Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCoordinate(Coordinate newValue) {
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
 * @param newValue Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setCoordinateFactory(Coordinate newValue) {
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
public void setElectrode(cbit.vcell.modelapp.Electrode electrode) {
	cbit.vcell.modelapp.Electrode oldValue = fieldElectrode;
	fieldElectrode = electrode;
	firePropertyChange("electrode", oldValue, electrode);
}
/**
 * Set the electrode1 to a new value.
 * @param newValue cbit.vcell.mapping.Electrode
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setelectrode1(cbit.vcell.modelapp.Electrode newValue) {
	if (ivjelectrode1 != newValue) {
		try {
			cbit.vcell.modelapp.Electrode oldValue = getelectrode1();
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
	D0CB838494G88G88G3C0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8BF89465353990B4CA1408284126B5E2D05408C123C6C10DB6BE102868CDABED2325D7DA510B3DE9AF345CABEDA7AF88A63CB48A3D86C985B5E89A1ED688D1F9848CB5E0A4F18D81B1333B135DCD76456EEC32C082777C0F734F6C6EECB6643B3D5F57FC5F496C1C7F3C7EF34E7F1CFFC62ADC17DADAD8D4A9C90525927F7B38D012DAAC12B4795F570EC6DC125BD4AE697E7E8230CA3A3538842E29
	D0D6761429FC25B45EDE867719F0FF7C89D379EA787D3A7483FB4AF770A30A9E954A529F7FE84460FA2E788A75AC21778F59AA603A9CA08BF064DA62A17A1F352D6178FC0EC7A8AC829C25D9E82B6238A6386F84C886C877B07A9C38AE251AC7556F60F447AF1BA44DFF4CD7340C485149C45860E53CCD7CDAAD7DF7E10117687A0B3D02734441FD96G29FC3174ED5F07F0EDE86931FDEAB1BB543ADE0B62F45639E42F5761365559820E7A7A7DCE45227ABCD625C5F6ABCE49F91248F3D07F9683EC077B059477
	0ECCF4E4406FC781E68B7CFE05609BFC2672B6G278F6B75497B8D5C6E76B3F7C9F3EC8FADDD9125DE71BED61FF41FD62F2C8F4C45FF26B1785191626B39D0D661B715D783B482D88162815E22314F7F6A83386EB12B7BBCAE17473D476B5D6B347A8EC12D1C703B3E9ECAB86E08E271782C1244627B3E3D56401E410071FDF45EFC4CA71336E12C530FCC1672EF3F32E80901CF72A71DA9CA71B3DFE47AB51FB05D6E283A737C2139982A7B9BC92FDBFAF16B12D2835DD5BF5E3AE4AE1733C8273BD1A5313E1E66
	C0957CA6FCA41E8CFFA745A76870ACAFEBA81E5CE7C0D9EDGF323FDB95A52DCB2C1DA3965AAC3BFA4BF18D8B4A940EC488E045B72D09711BDAF40F46E94363CC47176DE8D4FEC790266A9391F83E599C1D3B94B2F675AC8BB6DBF8D6D93A08360D10039GA5E7CC65EB4EE00E3DFBE45390F32CC57629FB838E27D571C90C7739A3EF42D5892ABE79205B2F4AEE0BE212FD8E596CD47C9227F961887101B904EF9E4DFD8B6878DCF1ABBED9F5F85CA0DDF6A92A6273B31A85C7D6919A1FC2CB5B9CAE8594383C92
	4A7D431157616A147D6A57DE2B2CAA9A85ABAFEDA5B16915CDA0918440EF6617F29371578E28FF8BC02770C3DCAB11770562036E223E3E59636989F85BC8CD240CB3444F23E85F21426F44B36863A55F905CFC382F68C7B9F7353E26135368309073E55FA9ECE369B209CBB550B7G34G588192GD281B27BB117FE5D0E3954386C962976C7D79866D29264F5EEBF4B21B97D61EDEA9A55C56C2D9800BC85E886F082A482248F60F8D0A7933F2676C736B3E2CF3581BE960457ADFA1FF3086ADEE6D0CE5AD771F9
	6AA5D04AE593BF16C721E1718B3413DC4885BF4C85A8BB0BB17CEDEBB5EB93FBE53F022D60CB9FA430E255A24A6C1F516565F1A9B6503D0C57E39515ABC9EFG2C667A57832C8358A631F17AE300F7815E87588870D700ADG9F819C84789660A449AD613B822A3B9A6CEE827082A483E4834C85D8F94ED43E832093A081A083E0960094E7AA2F81E88188822482BC8AD0G3066DFCC653B816CGC9GD9G33814AC6184AB783340F401C79CA49AE9CDE3DB1866BF83D570755FBB92F7BF2DE7FED5C879F8FA176
	8739FFE251FDBB44DCBAB90C7C7BE782964B9C9A4B730D63DBE131A4F1DC2B0B6307E1315C44633D0547BB16EDA46EBB07C07751906BBA147C79E78656F74DEDA371D2FA95375A28F8953795FE78F959FF34113872713A4BA3F896D03C974677FDB0207A857E15B692F7D9D5FD8EF3C0D5DA95D5058936D6DE7CB551658F183D5A54GF53D7AB549A5CF9F0C6F02FE4557E4EC34F85CEE05F6220205172F22E33362D4DCE074BECF402DB27CEA93D93F74A827785C60AD53E2090C6D4EG19E730B5C106CC72556B
	F33820EBA6D395E96688B2EE27585768FC9487B83236C903EB56889CEB7B5663D83B61740DD276F75EA223B9E47244B1C5EBC632312CF624B656B21BAA3EB1367DB5BAC760F8BF3375B68F618DDB22507687BE781C343F83B06D8978940E4B5D77AA13416837A06D228A921FFD9E187ABB5CB087BAA0DBD48F19EA70723FFECC6AE7D3BCB031721D5AA2709796917F39489CCF48BA32715590D9B8C6CEBECA6A558C756D84880E44B17252EFC8B94EF37860384F63F43250C2D95EC8627FD2F3A577DF925CCF9A49
	7C3FDC141F9165D57126726D71DAF9C2BCA9DFE741F2820D3CDC05AB633FC8144F845C5C78F07935D6ACDFC8640D8AAFFFD214D7C3598EDDB92B67197615A447151381852637E4C278054843916D2F051519F8CE32720B5BC9CEBAF5F349C923084E86310636F3FDF123587CAC992EEF93FFDFC068965B11EE7EA8B6072BGFC95C043854CC6C617A76812AFE0F4C40E1EAE1F52DD6DC03A0C8B58FC61518BB439F5C25E61EE242B391059357BC2CD2E26774FBDC85726ABC7FA0D6E67024EF921D6CF3DBCE65FC4
	A75245DF4866AF44BE142759F75405F4A9004F844863F2AB9318BCEF82215B6E9675C8E073A0A20F4C01F7A768759ED711EEDA821BAB6541F58EC049C5CCAF237BA000F41597B13ADA38B6DC94CEB7DF509D67F4AADC63F5F4ABAFA6F47775A2DD9A6027DD446AC172A17FA2E69F4B97DF1F10F7B14F7F0B59BCBE61E216AFBC2F02A28FB8DD4645EC2E0FF44C3E22D3C817F3B11B8B666B68B47BF2855DBC1DBEA237E434BE3EF12711EE43E89687C2174E2F1A1F8F1CC13A0651EC3E0971ED9B2D0F6F1F841DFD
	34A60744A3AEC46F74FE24CB525111F804520D99C03AC9BA3AF89D9D2B6F57E711AEFBB42B672C5121755D1EC868D61CC33A0251EC3ECC6AC1684A9275F4D384DDDCA21BD3A3DDF288DD2BA4621148665DC8171F282F4782C1D7F60966B73D1F5978721F9B4918C777473A65DEB9D8676F03F13B3E7E6FF424E1FBE10B9DE51C3E7A925C737A44C97A7AF6387782A4DC026363D34BF04FEBDC4FA4A97F45ADCB226D6525DE427781AF5146475C02BD0B59D832300368AC39945A6925B80E149E552F3B77787D9E0B
	03AE79F99F1AF9D4BFFEB25C5DC749D8556570795503B04189E2FFFB7FD1E2AF599360E83EC7D2EB2E1058B8D783FABBAF45FD44AD75EFCB867B08E4DC2C2F47A1186FB3D936218F12CECF14BA2F383A28BC0A8F12AFE5F54F3AD473C16D5905254C1689421682A82F9983760C21FD24194CF148B840EC2B7A609DE11B47D567B03B6A823D2EBA592C56A9D6078A7661E04F62E932605EE65A980C5B4081FD5C262D9808EAF37E983EB7B6C637D769E9DB42EC4E30204D8DC95077A4214DAFDB084D24AFE2F42DD6
	24CB871AB902AE49CA68B2845D6F84DD55E5104317E11D1F37EDB008874EDF6A6B355989132358D0CEA2484881E0F31985662512EE3EA4619C2740C66D35BB7C4CBF3F5D331B57BFF77284A9EBC9E21151DE2E7D066422C517B13FEC982B4B6BEB4D45338118AD1B68DEB529078AB469E35116AB6DC427DBF6A9ECBEB34B271F4F4C994B76134AAEC77A7BA83D8A13F4E663ADEF6BEDAC13C7995A1878222D286AF2E6DB63651A0DCD171B0B0FDF0EB67E41AE7C85B4B917E3BCFE68C07CA240ED9F0771287C14DC
	69BCDB42F650FF946D094B4D14563CFE0921BDF42C9F476C98B7CE3327927CD5A3FC56570DBE1B83B4D5D7208F1E6BA6F360AE5EG996D5E9E24ED833A44AB5076B2A75A1E8338C5827F0440EF3F92462EAB91BF420578B440658330386C6C54472572AA9617062B502697DDDB61AA5349ED9D3B34A83EAE0F4F25D8EBC8AFDBA777C153A46B2E3ABD34F8BF793D91F3EF2D1B34CF2F476FA0E54C16677DE8CB8B68C9388A7D3EEB93E97BDA3657D8BD8118BD56D607DEB7E2CC549D981374276F105A3BAF0D9A13
	6C2BD8AC66DE2545C49D7FD2D182G33E914AA7C168CFECB467A4FD3C9DE4ABE5B96662F696B747ECACBE67E4A957415814CE35EB784D06EEC20D9B39E696E89F039DB195C973EB3125BB29E6337369765D88117A2644477F2B95B181CF132111CBCA16705DE4C2739002B3E9A73206D10D19E34DF0D317950DFA311B9BF69DC7890D1539CBEFA54517D73DD9BCDACBE93DE57376106F3A78C63133042DE643C1A37192B35784C9EEBAE1EBF965B4C777A049F01E64D77307E45D612DF58D7B25CFBC16C6FF8FF99
	947986FC890277FAAB299B35DC42B17343D3C81B8DF48B856D58D318870CEE41A974636694186324201F7A9713F1DDF113E54FBE0F4B6BF113713BE623094B3F71B44ACF851ED9A998974F99442F7C3E293C697B28F76E194876D3530F34F1C017A5E8EF6F8F4D3D5F8A3A82202979816A4A9AC0FCA76012857E6CD92C53CC4015DD03F2FFC9718EB718A0BB9D27151A0D0CFE47B9245F8D3441EB508747CE3FCDE32236372ABE180155ECEC26BD05D3F65B6A98EA973661D756E20EECE8CC13F64C6F095A06B32E
	E139B16B9AADC752177DD7E9B6G336727421E1AD49803D305BD255C1E23035943E474CBA8A39978E7EAB24EF0999D31E53CDD08B24A2E05F9723538F7DC6E88FF56973CD6FBC610287B0D401E017D067669295732E7E02B853E14766933F4F826FF9F25A777ABA7C05FB4811F5B641CA07363046BCC65E9G53G72G6683D426613C74738E327FB868F33103EE95262D3233B5E4838C1FCFF5101C69D1CE717B42E37A311479E12B1978E1F35A501F2FB03E5D142F69BC78D83DB752B1AE88FC63G5281F2G66
	08BADFF02CB6D61D71799E2FAC33E752B06BF167F9570335150D9D7A3385A52B7EDED49C252D14CCE432ABA706BF9BBBE609763C39ED2231BD4C07BF23630A9DE86281D2GB28172A6228FE79E0B19B77B0306F943FDE2991EAFF3AC676BCB46F754BABCFDB66B7074AD9A263ED71561696B3145BA53B0BFAC5E0C6FD4CC3EB5570772317C58E2A1EB744DD0568CE0850883C883488618F3BD6649074777464A13BD38715E6691BF7705EF1EC7BF3B31A84CBE63FA6E0B60FBBB3C1EB7047271BD062EA8FB8C4D32
	997798D6784B381E4DB7E05FFB3D1578478E7771GA9GD9G79G8B812AA621FF860E6F0A659FBA41865FD0F7C4BA06552FB8E22C99DA7CD74564EB18E444B7FD187A1E094957B649A85F5E6BA27EF4C2D982C0AA40B40059G0BG2AEFC4FFBE99BB5F44910C21645BC0777072EDF484DFF83E6D3851082F29E7F87ACC91FC617ADA8C75ADF78EAF0ED5B1796C067A7206296F5FE27245996ACBF08DCF5F15B17912EFB46AAFD63412FCB568AF7686D415BEAFE2FD06379377A5B3EE4479620B7E61597A7B18FC
	F3EFB4EAD3E974790CF49374C7G5B81DAGD400A4004C1B30CD3D736DE752F93429C17B29516A706AB8BEA6DF5ECDC671F837264AB89EED30ECBA60D01CD6968F299A63B16FA60C47514070EC3545645BF0F3A89F3361ED11938DB7238D6BFB07E7431618FC2A218DB3E434A1D15870F2042C2175C92FC47055055D67985AB07A9031014C0173EF467174AD7ACC2FB25DD43E992089408A10G108670E8BA662A7A6DA731F2150F277BG5764717498F47D5C7EEB7B06677BDBE3724DCDB7626B898EF38E9713CF
	3A45B0C7CE8DCF5FE7B17956986AFBA502EFE8F93474F42CBC5AEC286F56883E21556F5E18FCCD067A82E70627EFE4CC3ECEC3FDF57D4353579013AFE828EF61B075AD9E084517E4286F3E18FC467AF2E372CDB25497FFF6F879B9B602AFBCBF338D75FDFAEEF87A1AA37842754D8A53477A506F51738E85D0D6B159D43E81E0B70095A08EA0EDB276210FFC3BC79A7AF89F3B9F1DBD4CFA7EBAE6BDF3A69B710D100627AFA902AFDCDF3E213E8D05435337B502AFFC4EBEBFCC9FBB237D8EFD26DDF1AB2CFBG98
	6E559E02EB047B633762387DFBBAE70E037B9400CC00BC008200328C187381EC86E882B88E904C907B16F46E1B847799996CFC8DB933C14EA54C047B028CF676E06594F6A6051CAFE0FC871D99F0A53216F33D616D2EAAC343ED66BAB3F5F4E59911BC88F91CAE45C0AE018283FC53A072C2E39124363F41F1C66731D768FE1373E82BB9AF620C4EF6A3AC655752A865C66737DF0FE1B78156DE9FBF917DACE45E89633310BFBDE1FC96F27609D58C8FB274EFC208F317A786BFABD9F042682C64DA6A539F9711FD
	EADF402DBADC8AD54B6668CA101C1705DE65C8404D3A9C22599F3E97ABF4EC690CDE5F9D1D46757D3453383E0D1D03573739B3DAFD79BEE98FF98633F98A761D4FF7EE15A27B4E7DD6076A71E1055A945977E5D71758FCE875AA96EC5BBEFA3E21F98ABB2347E54A516B7B12ECDC5F8259383E3F13872F6F9F65E875E5B1FCC1A5EDA462EC6BE1988DA4FC86BC4136145334281E16ADC276AECB74FA6C3598572349C25A155F9370D914F0DB3F342C91E55A68F3D0D5DC449CC653CE79D559E7D3A26A7A9D65E7E5
	51786D9652B64D8E2B4347CFFCB2BFCCFFF38575235F2F78690222D1D6E5DEFF279D6B3FDF759C5AA27A18CA43F5FA679456C729F0CD18925ADFB19BB22C517DF6175558EF77DA237BAD479A5BEF0FDB237BAD4F9A5BEF4FDA23659173D13DC36F232D82AFF517E36EF479BCAE489EA6EFF3D7F49FFC5CE56C03CF3A227B2031AB368F8EF5C577C1DBD7EC9FFC5B951EBB2C0E195D7A3AEF93F5BF572347EF97787DCEBD7EA301AFF7057AEAAB475F6F52537FCD64DF2053A87FD2F84EE5CC099CAB193CEAB5C44F
	A10C49E4DB74185CE1B30E49D4DB7418BCE80B9D13993668B1F95A96BBA6BF338D1E178F065635E59B77DDE975E0EB0A1CB05F4D1A024F521382217236F3F96B8DE301B0AFCC1EF49B4A6B688D15779117B7D39ECCDE65ED2172EA043C8DFD3ADC592775D5C9070C64B5F0B9ADF0358284F5F279FB160E68B9D16BB04E092D0E68B9315391BBA776BA226744C1C76C1CB86298BCA7E6756947B32D2F1A90546335F6ECBF959A233FF17C5653FADFEBF2FE25479F7AD860534F044A79186315FEBD7D8EC13FE9C00F
	5FA9708B86DE556137ECC67C5433FAFBB47B8367C2746E4376BE31BBFAEC533B0DE3FBDBF774585E5D9DBB368FF6C70F6D746E5831FD3AFB70582E16C26B0AFD685DE178ED9CFF2CB04417623D121D8E722E09D3BE658928123470B6B217EBF160BBB909F01F86108BB07BB6F6560A4DFD277491BDDD301A863368C4B06FF67ACD009E645D8D77EDGAA40B8004C5B316D3EFC067826051F95228FA4C53D72B61039B6BF39C50B983EF76DAA8DCFDF6388657B573564BD90F093A7E10FADD8D9C2B5293B58B8EF97
	67CDBFA6F527F284AE21C9600E0AB1FFE7883F403F32CE50F6A06E058D4B199D74F8A0BEBCE1E5CF3504486940F7E88E380C4E1CD6E562193785DE23724D227C26BE2372C6D1DE701DD1F93BA88FBD0B07654ECC1C477F3CE719BE97791C1965C1121D743B3DF2F75BA9AF3E0B7DA1FD2E9C877CC919384ECF7263BABFC5606E24FB7673603E648E44A552E789EB603EC660FE53093C5B852E2A9AF1718277E59F4165417DDC012B973C0B84EEA7555BFC27293C73CE443DA0A31DAAF00FD05E9C381FA5F0B750BD
	7EF9F03FC860AA8EA3EF59DD083BE3874135433DD7609E5EC4F071F01FF2971EE58B52333D64ECCC8660665EA54E3EF706BC7FA127D76B6BBF77B92CFBE5DBB3ED2D6C19C3B65DD3A17C55D9781E402F294CF6387782A4E461F97360FBF85EBC7D7A89524CDF9A3FA7B03BED20A8B50B3FA71025BBCF7D013918697C3C87F5CAF743FA6FEE028B7468F308647BA5925B07E265B7041413367CCCC879CFC24A093DAB0A756565CEFDB97151D14E4F6AB663DD2C5B22999912730E78285FB3093B1B55A90DDF2B1F98
	DFD8B33EE5B12B5B00CBBC4301F2062BBD09384AFB90770BBEFC5E9494387735E7238227BD171AFB8F76FFFB7DF87E31FAAA799E4CFE522DCB49D3291F7C0D5CCE6BD463F9ADFE6B027C8E828D01B7380E44296C0C9D3E778D6BA1193E0B6171A9B61FA76036CAD245B4226717542E59C0BFEFAA5AF5DDE728DD832AE6D7190CF635CC0B34EB955707E0859A82E8D7F01AE69713372D9A65459B48DB92A6AF99E852F57232270557332B0F70D8BC4E004B6D67F59C20B1A2BC39F04F74FE5509FA738D74160769AD
	G1A12FBB53DEB6E8D575BA2C76A354928F7433D28770F42FF5B6F0D543BA2CCEF93509C576975C668FD2EB3D26F02CE545BFF2FF8DF61D0E8CCD383DACC579FC61B928DECDA2553C74F8183CD2A4E264C881BF2F7907F3964204395F035BACEAB5C2EB934AF4C867ADCE157331BA82D439DC63B0076118B012E64BE5C1FBD9B736C0DD17E2C129207497750F97331F32EB3687B921B6F537C4270F9945F2043333EFF3E687B1B20AC7DBEFCE6BC32174C21D65EEFAA2F85E884B88E508F10FCBF0E49B6A7CC0787
	FF56516671B6432457295BAE634FE1A24E5C44825647BE30847B584CFBEE1232AEE80C7A3ED27A7DAC1FF279B5ABF7FCA139327A3D99A475DB84E5EB32CD658DGEDGF60038EC2C5F2DCEFE5615ECC16ED1D559E227EF18094DD507522721498414BFC3B1BC7FB2AE1B59B109DF2741F5A67F4D1FA91FECB7601B139D5AAE96E6475E172FF8000FAB8FE834CC472F8C1F2FEF67F4CD8F9877032B24DA43B577F1CE6F0D4AE73C360E67744986FCAC4ECF9E531F5FDDF935338873B6A24EDCC656835ABC010DFB33
	7C62DC8A142D1358F301B2C97BE37D51FB27A52177979518DFD5B7DFA7B5BE53D834BE0A5E8D8F723D4787B53DB50E6DCB194CD77A09B4BEF7276F0B08970076F91C9EDF1D59A9DBFA184E54BEFDDDF73CBA42705B82342EDCD7B63FA67EE9BC2F6BCEB146AFFCF0303AAAE749EF46E39567F6B67F500827603368F2924EE9759B57D784D732B6E6F2C708B9087D3C655EACE9F24F4A21F2B719D1EE6AF94BFD22D013DB91A6376DB04A1DE5A897E57C26C87B5DDE22491B3A09BCFBF4ABFD55A2460D8E689248C7
	25EA7E362D36FA6BC79B11EF57B79ADF7A3A217031F674D4B761E33FFF2A7BDD5AED54E77CDF81537176BFD4876BEB93FB236F0FDC5EEB3CBFB23E5778B94DB53D03BFE73B21B77AB7C9A6776A3FC9423F8756CE5A6F4A9C988B81DAG2CG419C9CA79A7A483BF86CB4F67898315FEAA37BB3644BE7AA6ADF970CDE577702313F1736A9C86653E68F2C6BDDC4BE62BF8A12F8299EAFC5B2DCBD5DE773B9ECF61511F27C6EA049EF2752C551ECEDB81E2EBB46C13D52F2F03D186946F5E50640B5F8E95BF848D4DE
	7D906246D05CF638EF9738D63AE6F142FD3F40E5D1DC825C278BDC058FF5E48B5CB2CA3772E1D3796607917787BA17EE047BB601B3871137D36076517EA7836E33854EEDC13A5C07C3575BD50FC0DC9FC1DC339969F6BC02F3219A324C63FE1B99A45FA5B2495D06CFA77F8E6D78CBF337E2D15BE41BE8BF4F06BC3BCAG39A90F8CBE06B33D2D9DE4EE1C813439G85GE50F1A4A2BG3683B4819C877082A4G2482E482BC8A108FB08FC01AEEAA2F1C0EF95AF3FAE3B45B81499776B4D9FD320324AB37636EE3
	44A7B5A027D1485ABD7061A032DAFC9E2F62D3CFB1AC5DAA43F3BBA17381AB488BCE9F5C970C4F91728CA191F8D20744F7E1483CA593F89E9D925FF6331EAF9FF88A0644F774301E2F64B118DFBEB6B89F73676B6D517B047FE90F5DA72CEF275FC00036CCB641982E261D6C8DFA3A3A7C0A68FB7ECA3FD543F0877D387F4946238CD768BA6D837289B43ECE1B6146F5DA2DC1FD74EBB4823B0106G2E535A9E8BDF2735FA4998EB77789C27BDEED5F66EF1BAECEE52F372B5184B0BE3F267E3629DD46F1BA1BCE4
	9793EB00FB8A8F883EC44157FDB234DE07CEC8420EA7CF46167952C9141975982E25577B509FF32278C36F1305C033D0670F0A99617E58F0F2F0FFFCAAECD8B3C33CBF9D8C2D1777BBADDE2702E8DF638C63BD9CBD34838D01B738CD4E887BBA4276E5AE6F54F40D32202E20012E25E13A9201A6D127ABB5C2579F65D0DDF74A1AAE0B99F5259B68DA9626AB9BE832F53A72F43AD8FFBF1F2E91E6F3BC437D9745AD64B83E566D085EBE5D9D315BE7DF077118BD5091B9E60F68B09E3393BA74E3B643DD52C1F242
	86FDF73B209BABF0C7856EAAAA13603EF1D8D53B40FF1F5AC570CD8AD108786B28DDFD94574EF113F438239CF7CB8769E3204A103A6239D58709DF174369E477F7D29A079B3A9C610F2954C607D756591DADF01A5D779DA3F195F3B9B6147032070ED5CA3AF72D69A7E1586B45AC0EDF4B11316D107531E538386AC83AD15511F4BF680C243B2EB3126E8BF3A4DD0BB9124EFBB812AEF0D8CF4756A70B5C5876ABF387DB1F7C44AC09B6946FA5F43A57B66ADA49FF1C0A7A6B1A779FE843E73467C1DB689B8AADDF
	D779506E66C16D2E31EAF6FFFDF26872EF8A227C78C165FFC11A9067395302BC538665394B26710C936B4E7903727C0AB4AB7ECCCAFECF779DG5A6478B7FB024BB4BCB6B9BEF7A87468678E350F43987A7810669CBEBDDF9B70580744F7EB483B8F7D4013B4A43E29A16FC52781CF56107846074C29F201E7F68CBE56A656F8A25B49DA0FC1FB729934A7DFA45D4341C83A9941C83A27AD11F44FD8A269B28C5A7B9DA16D1DFD277F6142EB0AE05CFAD9AAD52555ADCEC576AB552D74ABA155F2B5FFE62CCA45E4
	3DAAAD067FF5F5F5FE1F34C2EC8E9C24BD686C5EED75454FDFD7DCA4159C149633916520D4C2060503D2A99DB58ECA45B022844917FFCB74G521607EEC395CEDCBDF26518ED01A2296C003438CBF6FA6DB2C8215FA0E8164A4DD41C6283DAD5DAC93F623089F8B7D15EDD4DD211452FCAEFE9F88B413352D62FECD100E429D57675D089A725335262E35252AE1F4DCC9528D251318347C0AD3DA345E54D522B2A1DAC1849688FDE52C43710A589AF58C52BA67D8940CB7F7C0439B8223A7FA82CCFFAADF247E64F
	25BFF9A67D3B464AA2298862E2D7D8A0CA7BC85B2776124554E7D462CA3C0359DD1B43DB0D93CED5DAA25220309D60A8C0477FEF65707B896774FB128BC7D9226EC9B6BF455AF067D35ABEA8DBB71650F5E38A60331E42F5E3DB877956EF3475F32BCBF6BAE1765B3347EDA5EB693D27D4C55B37D9CA6782FE2461052CEC65B1E2F7370E67FF81D0CB87882CF3B3D1559EGGF8E3GGD0CB818294G94G88G88G3C0171B42CF3B3D1559EGGF8E3GG8CGGGGGGGGGGGGGGGGGE2F5E9
	ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8F9FGGGG
**end of data**/
}
}
