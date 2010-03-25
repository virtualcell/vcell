package cbit.vcell.mapping.gui;

import java.util.Vector;

import org.vcell.util.Coordinate;

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
	private Electrode fieldElectrode = null;
	private boolean ivjConnPtoP1Aligning = false;
	private Coordinate ivjCoordinateFactory = null;
	private Electrode ivjelectrode1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private Geometry fieldGeometry = null;
	private boolean ivjConnPtoP2Aligning = false;
	private Geometry ivjgeometry1 = null;
	private Model fieldModel = null;
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
 * connEtoM10:  (Coordinate.this --> TextFieldZ.text)
 * @param value cbit.vcell.geometry.Coordinate
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(Coordinate value) {
	try {
		getTextFieldZ().setText(this.getZString());
	} catch (java.lang.Throwable ivjExc) {
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
private void connEtoM7(Electrode value) {
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
 * @param value cbit.vcell.geometry.Coordinate
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
private void connPtoP3SetTarget() {
	try {
		if (ivjConnPtoP3Aligning == false) {
			ivjConnPtoP3Aligning = true;
			setmodel1(this.getModel());
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
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
	Geometry geometry = getGeometry();
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
	enableX = false;
	enableY = false;
	enableZ = false;
	getLabelX().setEnabled(enableX);
	getTextFieldX().setEnabled(enableX);
	getLabelY().setEnabled(enableY);
	getTextFieldY().setEnabled(enableY);
	getLabelZ().setEnabled(enableZ);
	getTextFieldZ().setEnabled(enableZ);
	getPositionLabel().setEnabled(enableX);
	getSetCoordButton().setEnabled(enableX);

	//
	// rest of the widgets.
	//
	getFeatureLabel().setEnabled(enable);
	getFeatureValueLabel().setEnabled(enable);
	getSetFeatureButton().setEnabled(enable);
	
	return;
}
/**
 * Return the Coordinate property value.
 * @return cbit.vcell.geometry.Coordinate
 */
private Coordinate getCoordinate() {
	return ivjCoordinate;
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
			ivjFeatureLabel.setText("Feature");
			ivjFeatureLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjFeatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		} catch (java.lang.Throwable ivjExc) {
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
	Vector<String> nameList = new Vector<String>();
	Structure[] structures = model.getStructures();
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
private javax.swing.JLabel getFeatureValueLabel() {
	if (ivjFeatureValueLabel == null) {
		try {
			ivjFeatureValueLabel = new javax.swing.JLabel();
			ivjFeatureValueLabel.setName("FeatureValueLabel");
			ivjFeatureValueLabel.setText("Please Select a Feature");
			ivjFeatureValueLabel.setForeground(java.awt.Color.black);
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
/**
 * Return the LabelX property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLabelX() {
	if (ivjLabelX == null) {
		try {
			ivjLabelX = new javax.swing.JLabel();
			ivjLabelX.setName("LabelX");
			ivjLabelX.setText("X");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLabelX;
}
/**
 * Return the LabelY property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLabelY() {
	if (ivjLabelY == null) {
		try {
			ivjLabelY = new javax.swing.JLabel();
			ivjLabelY.setName("LabelY");
			ivjLabelY.setText("Y");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLabelY;
}
/**
 * Return the LabelZ property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLabelZ() {
	if (ivjLabelZ == null) {
		try {
			ivjLabelZ = new javax.swing.JLabel();
			ivjLabelZ.setName("LabelZ");
			ivjLabelZ.setText("Z");
		} catch (java.lang.Throwable ivjExc) {
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
public Model getModel() {
	return fieldModel;
}
/**
 * Return the model1 property value.
 * @return cbit.vcell.model.Model
 */
private Model getmodel1() {
	return ivjmodel1;
}
/**
 * Return the PositionLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPositionLabel() {
	if (ivjPositionLabel == null) {
		try {
			ivjPositionLabel = new javax.swing.JLabel();
			ivjPositionLabel.setName("PositionLabel");
			ivjPositionLabel.setText("Tip Position");
			ivjPositionLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjPositionLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPositionLabel;
}
/**
 * Return the SetCoordButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSetCoordButton() {
	if (ivjSetCoordButton == null) {
		try {
			ivjSetCoordButton = new javax.swing.JButton();
			ivjSetCoordButton.setName("SetCoordButton");
			ivjSetCoordButton.setText("Set");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSetCoordButton;
}
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
 * Return the TextFieldX property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getTextFieldX() {
	if (ivjTextFieldX == null) {
		try {
			ivjTextFieldX = new javax.swing.JTextField();
			ivjTextFieldX.setName("TextFieldX");
			ivjTextFieldX.setColumns(10);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTextFieldX;
}
/**
 * Return the TextFieldY property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getTextFieldY() {
	if (ivjTextFieldY == null) {
		try {
			ivjTextFieldY = new javax.swing.JTextField();
			ivjTextFieldY.setName("TextFieldY");
			ivjTextFieldY.setColumns(10);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTextFieldY;
}
/**
 * Return the TextFieldZ property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getTextFieldZ() {
	if (ivjTextFieldZ == null) {
		try {
			ivjTextFieldZ = new javax.swing.JTextField();
			ivjTextFieldZ.setName("TextFieldZ");
			ivjTextFieldZ.setColumns(10);
		} catch (java.lang.Throwable ivjExc) {
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
private void initConnections() throws java.lang.Exception {
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
private void initialize() {
	try {
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
/**
 * Comment
 */
private void selectFeature(Model model) {
	String featureNames[] = getFeatureNames(model);
	if (featureNames == null || featureNames.length == 0) {
		PopupGenerator.showErrorDialog(this,"No defined feature present !");
		return;
	}
	String selection = (String)PopupGenerator.showListDialog(this,featureNames,"Select feature for electrode:");
	if (selection == null){
		return;
	}
	try {
		Feature feature = (Feature)model.getStructure(selection);
		getelectrode1().setFeature(feature);
	}catch (Exception e){
		handleException(e);
		PopupGenerator.showErrorDialog(this,"Error changing feature for electrode");
	}
}
/**
 * Set the Coordinate to a new value.
 * @param newValue cbit.vcell.geometry.Coordinate
 */
private void setCoordinate(Coordinate newValue) {
	if (ivjCoordinate != newValue) {
		try {
			ivjCoordinate = newValue;
			connEtoM8(ivjCoordinate);
			connEtoM9(ivjCoordinate);
			connEtoM10(ivjCoordinate);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the CoordinateFactory to a new value.
 * @param newValue cbit.vcell.geometry.Coordinate
 */
private void setCoordinateFactory(Coordinate newValue) {
	if (ivjCoordinateFactory != newValue) {
		try {
			ivjCoordinateFactory = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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
			connEtoM7(ivjelectrode1);
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
/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(Model model) {
	Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}
/**
 * Set the model1 to a new value.
 * @param newValue cbit.vcell.model.Model
 */
private void setmodel1(Model newValue) {
	if (ivjmodel1 != newValue) {
		try {
			Model oldValue = getmodel1();
			ivjmodel1 = newValue;
			connPtoP3SetSource();
			firePropertyChange("model", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}

}
