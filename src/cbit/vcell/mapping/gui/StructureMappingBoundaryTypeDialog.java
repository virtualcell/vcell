package cbit.vcell.mapping.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import cbit.vcell.mapping.*;
/**
 * Insert the type's description here.
 * Creation date: (5/24/00 3:28:01 PM)
 * @author: 
 */
public class StructureMappingBoundaryTypeDialog extends javax.swing.JDialog implements java.awt.event.ActionListener, java.awt.event.WindowListener, java.beans.PropertyChangeListener {
	private javax.swing.JComboBox ivjBoundaryTypeXmChoice = null;
	private javax.swing.JComboBox ivjBoundaryTypeXpChoice = null;
	private javax.swing.JComboBox ivjBoundaryTypeYmChoice = null;
	private javax.swing.JComboBox ivjBoundaryTypeYpChoice = null;
	private javax.swing.JComboBox ivjBoundaryTypeZmChoice = null;
	private javax.swing.JComboBox ivjBoundaryTypeZpChoice = null;
	private javax.swing.JPanel ivjContentsPane = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JLabel ivjResolvedStructureLabel = null;
	private javax.swing.JLabel ivjResolvedStructureTitleLabel = null;
	private javax.swing.JLabel ivjSubVolumeLabel = null;
	private javax.swing.JLabel ivjSubVolumeTitleLabel = null;
	private javax.swing.JLabel ivjXAxisLabel = null;
	private javax.swing.JLabel ivjXmLabel = null;
	private javax.swing.JLabel ivjXpLabel = null;
	private javax.swing.JLabel ivjYAxisLabel = null;
	private javax.swing.JLabel ivjYmLabel = null;
	private javax.swing.JLabel ivjYpLabel = null;
	private javax.swing.JLabel ivjZAxisLabel = null;
	private javax.swing.JLabel ivjZmLabel = null;
	private javax.swing.JLabel ivjZpLabel = null;
	private cbit.vcell.model.Feature ivjfeature1 = null;
	private SubVolume ivjsubVolume1 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP5Aligning = false;
	private boolean ivjConnPtoP6Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private boolean ivjConnPtoP8Aligning = false;
	private cbit.vcell.mapping.FeatureMapping fieldFeatureMapping = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;
	private FeatureMapping ivjfeatureMapping1 = null;
	private Geometry ivjgeometry1 = null;
	private boolean ivjConnPtoP10Aligning = false;
	private boolean ivjConnPtoP9Aligning = false;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public StructureMappingBoundaryTypeDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public StructureMappingBoundaryTypeDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getOkButton()) 
		connEtoM10(e);
	if (e.getSource() == getBoundaryTypeXmChoice()) 
		connPtoP3SetSource();
	if (e.getSource() == getBoundaryTypeYmChoice()) 
		connPtoP4SetSource();
	if (e.getSource() == getBoundaryTypeZmChoice()) 
		connPtoP5SetSource();
	if (e.getSource() == getBoundaryTypeXpChoice()) 
		connPtoP6SetSource();
	if (e.getSource() == getBoundaryTypeYpChoice()) 
		connPtoP7SetSource();
	if (e.getSource() == getBoundaryTypeZpChoice()) 
		connPtoP8SetSource();
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (StructureMappingBoundaryTypeDialog.window.windowClosing(java.awt.event.WindowEvent) --> StructureMappingBoundaryTypeDialog.dispose()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (Geometry.this --> StructureMappingBoundaryTypeDialog.refreshEnables()V)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshEnables();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (StructureMappingBoundaryTypeDialog.initialize() --> StructureMappingBoundaryTypeDialog.initComboBoxes()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.initComboBoxes();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (featureMapping1.this --> StructureMappingBoundaryTypeDialog.refreshEnables()V)
 * @param value cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(cbit.vcell.mapping.FeatureMapping value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshEnables();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (featureMapping1.this --> feature1.this)
 * @param value cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.mapping.FeatureMapping value) {
	try {
		// user code begin {1}
		// user code end
		if ((getfeatureMapping1() != null)) {
			setfeature1(getfeatureMapping1().getFeature());
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
 * connEtoM10:  (OkButton.action.actionPerformed(java.awt.event.ActionEvent) --> StructureMappingBoundaryTypeDialog.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (featureMapping1.this --> subVolume1.this)
 * @param value cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.mapping.FeatureMapping value) {
	try {
		// user code begin {1}
		// user code end
		if ((getfeatureMapping1() != null)) {
			setsubVolume1(getfeatureMapping1().getSubVolume());
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
 * connEtoM3:  (featureMapping1.subVolume --> subVolume1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getfeatureMapping1() != null)) {
			setsubVolume1(getfeatureMapping1().getSubVolume());
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
 * connEtoM4:  (feature1.this --> ResolvedStructureLabel.text)
 * @param value cbit.vcell.model.Feature
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(cbit.vcell.model.Feature value) {
	try {
		// user code begin {1}
		// user code end
		if ((getfeature1() != null)) {
			getResolvedStructureLabel().setText(getfeature1().getName());
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
 * connEtoM5:  (feature1.name --> ResolvedStructureLabel.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getfeature1() != null)) {
			getResolvedStructureLabel().setText(getfeature1().getName());
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
 * connEtoM6:  (subVolume1.this --> SubVolumeLabel.text)
 * @param value cbit.vcell.geometry.SubVolume
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.geometry.SubVolume value) {
	try {
		// user code begin {1}
		// user code end
		if ((getsubVolume1() != null)) {
			getSubVolumeLabel().setText(getsubVolume1().getName());
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
 * connEtoM7:  (subVolume1.name --> SubVolumeLabel.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getsubVolume1() != null)) {
			getSubVolumeLabel().setText(getsubVolume1().getName());
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
 * connPtoP10SetSource:  (StructureMappingBoundaryTypeDialog.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP10SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP10Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP10Aligning = true;
			if ((getgeometry1() != null)) {
				this.setGeometry(getgeometry1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP10Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP10Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP10SetTarget:  (StructureMappingBoundaryTypeDialog.geometry <--> geometry1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP10SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP10Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP10Aligning = true;
			setgeometry1(this.getGeometry());
			// user code begin {2}
			// user code end
			ivjConnPtoP10Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP10Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP3SetSource:  (FeatureMapping.boundaryConditionTypeXm <--> BoundaryTypeXmChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getfeatureMapping1().setBoundaryConditionTypeXm((cbit.vcell.math.BoundaryConditionType)getBoundaryTypeXmChoice().getSelectedItem());
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
 * connPtoP3SetTarget:  (FeatureMapping.boundaryConditionTypeXm <--> BoundaryTypeXmChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getBoundaryTypeXmChoice().setSelectedItem(getfeatureMapping1().getBoundaryConditionTypeXm());
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
 * connPtoP4SetSource:  (FeatureMapping.boundaryConditionTypeYm <--> BoundaryTypeYmChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getfeatureMapping1().setBoundaryConditionTypeYm((cbit.vcell.math.BoundaryConditionType)getBoundaryTypeYmChoice().getSelectedItem());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP4SetTarget:  (FeatureMapping.boundaryConditionTypeYm <--> BoundaryTypeYmChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getBoundaryTypeYmChoice().setSelectedItem(getfeatureMapping1().getBoundaryConditionTypeYm());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP5SetSource:  (FeatureMapping.boundaryConditionTypeZm <--> BoundaryTypeZmChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getfeatureMapping1().setBoundaryConditionTypeZm((cbit.vcell.math.BoundaryConditionType)getBoundaryTypeZmChoice().getSelectedItem());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP5SetTarget:  (FeatureMapping.boundaryConditionTypeZm <--> BoundaryTypeZmChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getBoundaryTypeZmChoice().setSelectedItem(getfeatureMapping1().getBoundaryConditionTypeZm());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP6SetSource:  (FeatureMapping.boundaryConditionTypeXp <--> BoundaryTypeXpChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getfeatureMapping1().setBoundaryConditionTypeXp((cbit.vcell.math.BoundaryConditionType)getBoundaryTypeXpChoice().getSelectedItem());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP6SetTarget:  (FeatureMapping.boundaryConditionTypeXp <--> BoundaryTypeXpChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getBoundaryTypeXpChoice().setSelectedItem(getfeatureMapping1().getBoundaryConditionTypeXp());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP6Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP6Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP7SetSource:  (FeatureMapping.boundaryConditionTypeYp <--> BoundaryTypeYpChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getfeatureMapping1().setBoundaryConditionTypeYp((cbit.vcell.math.BoundaryConditionType)getBoundaryTypeYpChoice().getSelectedItem());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP7SetTarget:  (FeatureMapping.boundaryConditionTypeYp <--> BoundaryTypeYpChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getBoundaryTypeYpChoice().setSelectedItem(getfeatureMapping1().getBoundaryConditionTypeYp());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP8SetSource:  (FeatureMapping.boundaryConditionTypeZp <--> BoundaryTypeZpChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP8Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP8Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getfeatureMapping1().setBoundaryConditionTypeZp((cbit.vcell.math.BoundaryConditionType)getBoundaryTypeZpChoice().getSelectedItem());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP8Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP8Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP8SetTarget:  (FeatureMapping.boundaryConditionTypeZp <--> BoundaryTypeZpChoice.selectedItem)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP8SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP8Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP8Aligning = true;
			if ((getfeatureMapping1() != null)) {
				getBoundaryTypeZpChoice().setSelectedItem(getfeatureMapping1().getBoundaryConditionTypeZp());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP8Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP8Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP9SetSource:  (StructureMappingBoundaryTypeDialog.featureMapping <--> featureMapping1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP9SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP9Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP9Aligning = true;
			if ((getfeatureMapping1() != null)) {
				this.setFeatureMapping(getfeatureMapping1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP9Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP9Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP9SetTarget:  (StructureMappingBoundaryTypeDialog.featureMapping <--> featureMapping1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP9SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP9Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP9Aligning = true;
			setfeatureMapping1(this.getFeatureMapping());
			// user code begin {2}
			// user code end
			ivjConnPtoP9Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP9Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the BoundaryTypeXmChoice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBoundaryTypeXmChoice() {
	if (ivjBoundaryTypeXmChoice == null) {
		try {
			ivjBoundaryTypeXmChoice = new javax.swing.JComboBox();
			ivjBoundaryTypeXmChoice.setName("BoundaryTypeXmChoice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBoundaryTypeXmChoice;
}
/**
 * Return the BoundaryTypeXpChoice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBoundaryTypeXpChoice() {
	if (ivjBoundaryTypeXpChoice == null) {
		try {
			ivjBoundaryTypeXpChoice = new javax.swing.JComboBox();
			ivjBoundaryTypeXpChoice.setName("BoundaryTypeXpChoice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBoundaryTypeXpChoice;
}
/**
 * Return the BoundaryTypeYmChoice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBoundaryTypeYmChoice() {
	if (ivjBoundaryTypeYmChoice == null) {
		try {
			ivjBoundaryTypeYmChoice = new javax.swing.JComboBox();
			ivjBoundaryTypeYmChoice.setName("BoundaryTypeYmChoice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBoundaryTypeYmChoice;
}
/**
 * Return the BoundaryTypeYpChoice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBoundaryTypeYpChoice() {
	if (ivjBoundaryTypeYpChoice == null) {
		try {
			ivjBoundaryTypeYpChoice = new javax.swing.JComboBox();
			ivjBoundaryTypeYpChoice.setName("BoundaryTypeYpChoice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBoundaryTypeYpChoice;
}
/**
 * Return the BoundaryTypeZmChoice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBoundaryTypeZmChoice() {
	if (ivjBoundaryTypeZmChoice == null) {
		try {
			ivjBoundaryTypeZmChoice = new javax.swing.JComboBox();
			ivjBoundaryTypeZmChoice.setName("BoundaryTypeZmChoice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBoundaryTypeZmChoice;
}
/**
 * Return the BoundaryTypeZpChoice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBoundaryTypeZpChoice() {
	if (ivjBoundaryTypeZpChoice == null) {
		try {
			ivjBoundaryTypeZpChoice = new javax.swing.JComboBox();
			ivjBoundaryTypeZpChoice.setName("BoundaryTypeZpChoice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBoundaryTypeZpChoice;
}
/**
 * Return the ContentsPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getContentsPane() {
	if (ivjContentsPane == null) {
		try {
			ivjContentsPane = new javax.swing.JPanel();
			ivjContentsPane.setName("ContentsPane");
			ivjContentsPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsBoundaryTypeXmChoice = new java.awt.GridBagConstraints();
			constraintsBoundaryTypeXmChoice.gridx = 2; constraintsBoundaryTypeXmChoice.gridy = 2;
			constraintsBoundaryTypeXmChoice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBoundaryTypeXmChoice.weightx = 1.0;
			constraintsBoundaryTypeXmChoice.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getBoundaryTypeXmChoice(), constraintsBoundaryTypeXmChoice);

			java.awt.GridBagConstraints constraintsBoundaryTypeXpChoice = new java.awt.GridBagConstraints();
			constraintsBoundaryTypeXpChoice.gridx = 4; constraintsBoundaryTypeXpChoice.gridy = 2;
			constraintsBoundaryTypeXpChoice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBoundaryTypeXpChoice.weightx = 1.0;
			constraintsBoundaryTypeXpChoice.insets = new java.awt.Insets(4, 4, 4, 10);
			getContentsPane().add(getBoundaryTypeXpChoice(), constraintsBoundaryTypeXpChoice);

			java.awt.GridBagConstraints constraintsBoundaryTypeYmChoice = new java.awt.GridBagConstraints();
			constraintsBoundaryTypeYmChoice.gridx = 2; constraintsBoundaryTypeYmChoice.gridy = 3;
			constraintsBoundaryTypeYmChoice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBoundaryTypeYmChoice.weightx = 1.0;
			constraintsBoundaryTypeYmChoice.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getBoundaryTypeYmChoice(), constraintsBoundaryTypeYmChoice);

			java.awt.GridBagConstraints constraintsBoundaryTypeYpChoice = new java.awt.GridBagConstraints();
			constraintsBoundaryTypeYpChoice.gridx = 4; constraintsBoundaryTypeYpChoice.gridy = 3;
			constraintsBoundaryTypeYpChoice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBoundaryTypeYpChoice.weightx = 1.0;
			constraintsBoundaryTypeYpChoice.insets = new java.awt.Insets(4, 4, 4, 10);
			getContentsPane().add(getBoundaryTypeYpChoice(), constraintsBoundaryTypeYpChoice);

			java.awt.GridBagConstraints constraintsBoundaryTypeZpChoice = new java.awt.GridBagConstraints();
			constraintsBoundaryTypeZpChoice.gridx = 4; constraintsBoundaryTypeZpChoice.gridy = 4;
			constraintsBoundaryTypeZpChoice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBoundaryTypeZpChoice.weightx = 1.0;
			constraintsBoundaryTypeZpChoice.insets = new java.awt.Insets(4, 4, 4, 10);
			getContentsPane().add(getBoundaryTypeZpChoice(), constraintsBoundaryTypeZpChoice);

			java.awt.GridBagConstraints constraintsBoundaryTypeZmChoice = new java.awt.GridBagConstraints();
			constraintsBoundaryTypeZmChoice.gridx = 2; constraintsBoundaryTypeZmChoice.gridy = 4;
			constraintsBoundaryTypeZmChoice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBoundaryTypeZmChoice.weightx = 1.0;
			constraintsBoundaryTypeZmChoice.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getBoundaryTypeZmChoice(), constraintsBoundaryTypeZmChoice);

			java.awt.GridBagConstraints constraintsOkButton = new java.awt.GridBagConstraints();
			constraintsOkButton.gridx = 0; constraintsOkButton.gridy = 5;
			constraintsOkButton.gridwidth = 5;
			constraintsOkButton.ipadx = 15;
			constraintsOkButton.insets = new java.awt.Insets(15, 4, 15, 4);
			getContentsPane().add(getOkButton(), constraintsOkButton);

			java.awt.GridBagConstraints constraintsXAxisLabel = new java.awt.GridBagConstraints();
			constraintsXAxisLabel.gridx = 0; constraintsXAxisLabel.gridy = 2;
			constraintsXAxisLabel.insets = new java.awt.Insets(4, 14, 4, 4);
			getContentsPane().add(getXAxisLabel(), constraintsXAxisLabel);

			java.awt.GridBagConstraints constraintsYAxisLabel = new java.awt.GridBagConstraints();
			constraintsYAxisLabel.gridx = 0; constraintsYAxisLabel.gridy = 3;
			constraintsYAxisLabel.insets = new java.awt.Insets(4, 14, 4, 4);
			getContentsPane().add(getYAxisLabel(), constraintsYAxisLabel);

			java.awt.GridBagConstraints constraintsZAxisLabel = new java.awt.GridBagConstraints();
			constraintsZAxisLabel.gridx = 0; constraintsZAxisLabel.gridy = 4;
			constraintsZAxisLabel.insets = new java.awt.Insets(4, 14, 4, 4);
			getContentsPane().add(getZAxisLabel(), constraintsZAxisLabel);

			java.awt.GridBagConstraints constraintsXmLabel = new java.awt.GridBagConstraints();
			constraintsXmLabel.gridx = 1; constraintsXmLabel.gridy = 2;
			constraintsXmLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsXmLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getXmLabel(), constraintsXmLabel);

			java.awt.GridBagConstraints constraintsXpLabel = new java.awt.GridBagConstraints();
			constraintsXpLabel.gridx = 3; constraintsXpLabel.gridy = 2;
			constraintsXpLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsXpLabel.insets = new java.awt.Insets(4, 10, 4, 4);
			getContentsPane().add(getXpLabel(), constraintsXpLabel);

			java.awt.GridBagConstraints constraintsYmLabel = new java.awt.GridBagConstraints();
			constraintsYmLabel.gridx = 1; constraintsYmLabel.gridy = 3;
			constraintsYmLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsYmLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getYmLabel(), constraintsYmLabel);

			java.awt.GridBagConstraints constraintsZmLabel = new java.awt.GridBagConstraints();
			constraintsZmLabel.gridx = 1; constraintsZmLabel.gridy = 4;
			constraintsZmLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsZmLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getZmLabel(), constraintsZmLabel);

			java.awt.GridBagConstraints constraintsYpLabel = new java.awt.GridBagConstraints();
			constraintsYpLabel.gridx = 3; constraintsYpLabel.gridy = 3;
			constraintsYpLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsYpLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getYpLabel(), constraintsYpLabel);

			java.awt.GridBagConstraints constraintsZpLabel = new java.awt.GridBagConstraints();
			constraintsZpLabel.gridx = 3; constraintsZpLabel.gridy = 4;
			constraintsZpLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsZpLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getZpLabel(), constraintsZpLabel);

			java.awt.GridBagConstraints constraintsResolvedStructureTitleLabel = new java.awt.GridBagConstraints();
			constraintsResolvedStructureTitleLabel.gridx = 0; constraintsResolvedStructureTitleLabel.gridy = 0;
			constraintsResolvedStructureTitleLabel.gridwidth = 2;
			constraintsResolvedStructureTitleLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsResolvedStructureTitleLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getResolvedStructureTitleLabel(), constraintsResolvedStructureTitleLabel);

			java.awt.GridBagConstraints constraintsResolvedStructureLabel = new java.awt.GridBagConstraints();
			constraintsResolvedStructureLabel.gridx = 2; constraintsResolvedStructureLabel.gridy = 0;
			constraintsResolvedStructureLabel.gridwidth = 3;
			constraintsResolvedStructureLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsResolvedStructureLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getResolvedStructureLabel(), constraintsResolvedStructureLabel);

			java.awt.GridBagConstraints constraintsSubVolumeTitleLabel = new java.awt.GridBagConstraints();
			constraintsSubVolumeTitleLabel.gridx = 0; constraintsSubVolumeTitleLabel.gridy = 1;
			constraintsSubVolumeTitleLabel.gridwidth = 2;
			constraintsSubVolumeTitleLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSubVolumeTitleLabel.insets = new java.awt.Insets(4, 10, 4, 4);
			getContentsPane().add(getSubVolumeTitleLabel(), constraintsSubVolumeTitleLabel);

			java.awt.GridBagConstraints constraintsSubVolumeLabel = new java.awt.GridBagConstraints();
			constraintsSubVolumeLabel.gridx = 2; constraintsSubVolumeLabel.gridy = 1;
			constraintsSubVolumeLabel.gridwidth = 3;
			constraintsSubVolumeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSubVolumeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getContentsPane().add(getSubVolumeLabel(), constraintsSubVolumeLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane;
}
/**
 * Return the feature1 property value.
 * @return cbit.vcell.model.Feature
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Feature getfeature1() {
	// user code begin {1}
	// user code end
	return ivjfeature1;
}
/**
 * Gets the featureMapping property (cbit.vcell.mapping.FeatureMapping) value.
 * @return The featureMapping property value.
 * @see #setFeatureMapping
 */
public cbit.vcell.mapping.FeatureMapping getFeatureMapping() {
	return fieldFeatureMapping;
}
/**
 * Return the featureMapping1 property value.
 * @return cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.mapping.FeatureMapping getfeatureMapping1() {
	// user code begin {1}
	// user code end
	return ivjfeatureMapping1;
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
 * Return the OkButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getOkButton() {
	if (ivjOkButton == null) {
		try {
			cbit.gui.BevelBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.BevelBorderBean();
			ivjLocalBorder.setColor(new java.awt.Color(160,160,255));
			ivjOkButton = new javax.swing.JButton();
			ivjOkButton.setName("OkButton");
			ivjOkButton.setBorder(ivjLocalBorder);
			ivjOkButton.setText("OK");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkButton;
}
/**
 * Return the ResolvedStructureLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getResolvedStructureLabel() {
	if (ivjResolvedStructureLabel == null) {
		try {
			ivjResolvedStructureLabel = new javax.swing.JLabel();
			ivjResolvedStructureLabel.setName("ResolvedStructureLabel");
			ivjResolvedStructureLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjResolvedStructureLabel.setText(" ");
			ivjResolvedStructureLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResolvedStructureLabel;
}
/**
 * Return the ResolvedStructureTitleLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getResolvedStructureTitleLabel() {
	if (ivjResolvedStructureTitleLabel == null) {
		try {
			ivjResolvedStructureTitleLabel = new javax.swing.JLabel();
			ivjResolvedStructureTitleLabel.setName("ResolvedStructureTitleLabel");
			ivjResolvedStructureTitleLabel.setText("Resolved Structure: ");
			ivjResolvedStructureTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResolvedStructureTitleLabel;
}
/**
 * Return the subVolume1 property value.
 * @return cbit.vcell.geometry.SubVolume
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.SubVolume getsubVolume1() {
	// user code begin {1}
	// user code end
	return ivjsubVolume1;
}
/**
 * Return the SubVolumeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSubVolumeLabel() {
	if (ivjSubVolumeLabel == null) {
		try {
			ivjSubVolumeLabel = new javax.swing.JLabel();
			ivjSubVolumeLabel.setName("SubVolumeLabel");
			ivjSubVolumeLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjSubVolumeLabel.setText(" ");
			ivjSubVolumeLabel.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSubVolumeLabel;
}
/**
 * Return the SubVolumeTitleLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSubVolumeTitleLabel() {
	if (ivjSubVolumeTitleLabel == null) {
		try {
			ivjSubVolumeTitleLabel = new javax.swing.JLabel();
			ivjSubVolumeTitleLabel.setName("SubVolumeTitleLabel");
			ivjSubVolumeTitleLabel.setText("Geometry SubVolume: ");
			ivjSubVolumeTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSubVolumeTitleLabel;
}
/**
 * Return the XAxisLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getXAxisLabel() {
	if (ivjXAxisLabel == null) {
		try {
			ivjXAxisLabel = new javax.swing.JLabel();
			ivjXAxisLabel.setName("XAxisLabel");
			ivjXAxisLabel.setPreferredSize(new java.awt.Dimension(65, 17));
			ivjXAxisLabel.setText("X axis");
			ivjXAxisLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXAxisLabel;
}
/**
 * Return the XmLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getXmLabel() {
	if (ivjXmLabel == null) {
		try {
			ivjXmLabel = new javax.swing.JLabel();
			ivjXmLabel.setName("XmLabel");
			ivjXmLabel.setText("X -");
			ivjXmLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXmLabel;
}
/**
 * Return the XpLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getXpLabel() {
	if (ivjXpLabel == null) {
		try {
			ivjXpLabel = new javax.swing.JLabel();
			ivjXpLabel.setName("XpLabel");
			ivjXpLabel.setText("X +");
			ivjXpLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXpLabel;
}
/**
 * Return the YAxisLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getYAxisLabel() {
	if (ivjYAxisLabel == null) {
		try {
			ivjYAxisLabel = new javax.swing.JLabel();
			ivjYAxisLabel.setName("YAxisLabel");
			ivjYAxisLabel.setPreferredSize(new java.awt.Dimension(65, 17));
			ivjYAxisLabel.setText("Y axis");
			ivjYAxisLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYAxisLabel;
}
/**
 * Return the YmLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getYmLabel() {
	if (ivjYmLabel == null) {
		try {
			ivjYmLabel = new javax.swing.JLabel();
			ivjYmLabel.setName("YmLabel");
			ivjYmLabel.setText("Y -");
			ivjYmLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYmLabel;
}
/**
 * Return the YpLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getYpLabel() {
	if (ivjYpLabel == null) {
		try {
			ivjYpLabel = new javax.swing.JLabel();
			ivjYpLabel.setName("YpLabel");
			ivjYpLabel.setText("Y +");
			ivjYpLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYpLabel;
}
/**
 * Return the ZAxisLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getZAxisLabel() {
	if (ivjZAxisLabel == null) {
		try {
			ivjZAxisLabel = new javax.swing.JLabel();
			ivjZAxisLabel.setName("ZAxisLabel");
			ivjZAxisLabel.setPreferredSize(new java.awt.Dimension(65, 17));
			ivjZAxisLabel.setText("Z axis");
			ivjZAxisLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZAxisLabel;
}
/**
 * Return the ZmLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getZmLabel() {
	if (ivjZmLabel == null) {
		try {
			ivjZmLabel = new javax.swing.JLabel();
			ivjZmLabel.setName("ZmLabel");
			ivjZmLabel.setText("Z -");
			ivjZmLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZmLabel;
}
/**
 * Return the ZpLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getZpLabel() {
	if (ivjZpLabel == null) {
		try {
			ivjZpLabel = new javax.swing.JLabel();
			ivjZpLabel.setName("ZpLabel");
			ivjZpLabel.setText("Z +");
			ivjZpLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZpLabel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Comment
 */
private void initComboBoxes() {
	getBoundaryTypeXmChoice().addItem(cbit.vcell.math.BoundaryConditionType.getDIRICHLET());
	getBoundaryTypeXmChoice().addItem(cbit.vcell.math.BoundaryConditionType.getNEUMANN());
	getBoundaryTypeXpChoice().addItem(cbit.vcell.math.BoundaryConditionType.getDIRICHLET());
	getBoundaryTypeXpChoice().addItem(cbit.vcell.math.BoundaryConditionType.getNEUMANN());
	
	getBoundaryTypeYmChoice().addItem(cbit.vcell.math.BoundaryConditionType.getDIRICHLET());
	getBoundaryTypeYmChoice().addItem(cbit.vcell.math.BoundaryConditionType.getNEUMANN());
	getBoundaryTypeYpChoice().addItem(cbit.vcell.math.BoundaryConditionType.getDIRICHLET());
	getBoundaryTypeYpChoice().addItem(cbit.vcell.math.BoundaryConditionType.getNEUMANN());
	
	getBoundaryTypeZmChoice().addItem(cbit.vcell.math.BoundaryConditionType.getDIRICHLET());
	getBoundaryTypeZmChoice().addItem(cbit.vcell.math.BoundaryConditionType.getNEUMANN());
	getBoundaryTypeZpChoice().addItem(cbit.vcell.math.BoundaryConditionType.getDIRICHLET());
	getBoundaryTypeZpChoice().addItem(cbit.vcell.math.BoundaryConditionType.getNEUMANN());
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addWindowListener(this);
	getOkButton().addActionListener(this);
	getBoundaryTypeXmChoice().addActionListener(this);
	getBoundaryTypeYmChoice().addActionListener(this);
	getBoundaryTypeZmChoice().addActionListener(this);
	getBoundaryTypeXpChoice().addActionListener(this);
	getBoundaryTypeYpChoice().addActionListener(this);
	getBoundaryTypeZpChoice().addActionListener(this);
	this.addPropertyChangeListener(this);
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP6SetTarget();
	connPtoP7SetTarget();
	connPtoP8SetTarget();
	connPtoP9SetTarget();
	connPtoP10SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("StructureMappingBoundaryTypeDialog");
		setSize(509, 242);
		setTitle("Boundary Condition Types");
		setContentPane(getContentsPane());
		initConnections();
		connEtoC3();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == getfeatureMapping1() && (evt.getPropertyName().equals("boundaryConditionTypeXm"))) 
		connPtoP3SetTarget();
	if (evt.getSource() == getfeatureMapping1() && (evt.getPropertyName().equals("boundaryConditionTypeYm"))) 
		connPtoP4SetTarget();
	if (evt.getSource() == getfeatureMapping1() && (evt.getPropertyName().equals("boundaryConditionTypeZm"))) 
		connPtoP5SetTarget();
	if (evt.getSource() == getfeatureMapping1() && (evt.getPropertyName().equals("boundaryConditionTypeXp"))) 
		connPtoP6SetTarget();
	if (evt.getSource() == getfeatureMapping1() && (evt.getPropertyName().equals("boundaryConditionTypeYp"))) 
		connPtoP7SetTarget();
	if (evt.getSource() == getfeatureMapping1() && (evt.getPropertyName().equals("boundaryConditionTypeZp"))) 
		connPtoP8SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("featureMapping"))) 
		connPtoP9SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("geometry"))) 
		connPtoP10SetTarget();
	if (evt.getSource() == getfeatureMapping1() && (evt.getPropertyName().equals("subVolume"))) 
		connEtoM3(evt);
	if (evt.getSource() == getfeature1() && (evt.getPropertyName().equals("name"))) 
		connEtoM5(evt);
	if (evt.getSource() == getsubVolume1() && (evt.getPropertyName().equals("name"))) 
		connEtoM7(evt);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void refreshEnables() {
	int dimension = (getGeometry()!=null)?getGeometry().getDimension():0;
	boolean bResolved = (getFeatureMapping()!=null)?getFeatureMapping().getResolved():false;
	boolean bEnable1D =  (dimension >= 1) && bResolved;
	boolean bEnable2D =  (dimension >= 2) && bResolved;
	boolean bEnable3D =  (dimension >= 3) && bResolved;
	
	getXAxisLabel().setEnabled(bEnable1D);
	getXmLabel().setEnabled(bEnable1D);
	getXpLabel().setEnabled(bEnable1D);
	getBoundaryTypeXmChoice().setEnabled(bEnable1D);
	getBoundaryTypeXpChoice().setEnabled(bEnable1D);

	getYAxisLabel().setEnabled(bEnable2D);
	getYmLabel().setEnabled(bEnable2D);
	getYpLabel().setEnabled(bEnable2D);
	getBoundaryTypeYmChoice().setEnabled(bEnable2D);
	getBoundaryTypeYpChoice().setEnabled(bEnable2D);

	getZAxisLabel().setEnabled(bEnable3D);
	getZmLabel().setEnabled(bEnable3D);
	getZpLabel().setEnabled(bEnable3D);
	getBoundaryTypeZmChoice().setEnabled(bEnable3D);
	getBoundaryTypeZpChoice().setEnabled(bEnable3D);
}
/**
 * Set the feature1 to a new value.
 * @param newValue cbit.vcell.model.Feature
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setfeature1(cbit.vcell.model.Feature newValue) {
	if (ivjfeature1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjfeature1 != null) {
				ivjfeature1.removePropertyChangeListener(this);
			}
			ivjfeature1 = newValue;

			/* Listen for events from the new object */
			if (ivjfeature1 != null) {
				ivjfeature1.addPropertyChangeListener(this);
			}
			connEtoM4(ivjfeature1);
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
 * Sets the featureMapping property (cbit.vcell.mapping.FeatureMapping) value.
 * @param featureMapping The new value for the property.
 * @see #getFeatureMapping
 */
public void setFeatureMapping(cbit.vcell.mapping.FeatureMapping featureMapping) {
	FeatureMapping oldValue = fieldFeatureMapping;
	fieldFeatureMapping = featureMapping;
	firePropertyChange("featureMapping", oldValue, featureMapping);
}
/**
 * Set the featureMapping1 to a new value.
 * @param newValue cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setfeatureMapping1(cbit.vcell.mapping.FeatureMapping newValue) {
	if (ivjfeatureMapping1 != newValue) {
		try {
			cbit.vcell.mapping.FeatureMapping oldValue = getfeatureMapping1();
			/* Stop listening for events from the current object */
			if (ivjfeatureMapping1 != null) {
				ivjfeatureMapping1.removePropertyChangeListener(this);
			}
			ivjfeatureMapping1 = newValue;

			/* Listen for events from the new object */
			if (ivjfeatureMapping1 != null) {
				ivjfeatureMapping1.addPropertyChangeListener(this);
			}
			connPtoP3SetTarget();
			connPtoP4SetTarget();
			connPtoP5SetTarget();
			connPtoP6SetTarget();
			connPtoP7SetTarget();
			connPtoP8SetTarget();
			connPtoP9SetSource();
			connEtoC4(ivjfeatureMapping1);
			connEtoM1(ivjfeatureMapping1);
			connEtoM2(ivjfeatureMapping1);
			firePropertyChange("featureMapping", oldValue, newValue);
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
	Geometry oldValue = fieldGeometry;
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
			connEtoC2(ivjgeometry1);
			connPtoP10SetSource();
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
 * Set the subVolume1 to a new value.
 * @param newValue cbit.vcell.geometry.SubVolume
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setsubVolume1(cbit.vcell.geometry.SubVolume newValue) {
	if (ivjsubVolume1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjsubVolume1 != null) {
				ivjsubVolume1.removePropertyChangeListener(this);
			}
			ivjsubVolume1 = newValue;

			/* Listen for events from the new object */
			if (ivjsubVolume1 != null) {
				ivjsubVolume1.addPropertyChangeListener(this);
			}
			connEtoM6(ivjsubVolume1);
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
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowActivated(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowClosed(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowClosing(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == this) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowDeactivated(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowDeiconified(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowIconified(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowOpened(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE3FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8BD8D46735A8A71244B6A4A1C69B125AD3E3C9CADB53CBD35BF092EFEBDA5B1806769A222DEDC88EB935BD36171C5A9C5B7AA5743A99A0022222E2A4BEC9E2A28AA288A8A2AA0F61A9C422C8D0C962406CE1C60619494CC6C00D5E75BF563F774C5E43A0E93F6FDE3EEF3177DE6FFF7D6F475EA3656E1A1111EA4A1524548C097F9DCE15A46795C97A523E7B7E0E384ABD89D912666FAD008DD2DAEB
	BADC1B0196BFA3A1ABC77A631ECCF86E0167127B933236407DA669CFFB66FF8AB7C16C4CG5A7EB3397FBE361D06AC34330ABE0F8EE443F59D40C540116B2CBD447EBD83F99C9F4E718829A6C90AAAA0BC338636F35CB2604984DCA7003D0071074115B9F0EBD5A5676B39A7C6DA7EB32FE9AD512351091044E5537732EB3E74EA6A2C3D4456A26AA7112906E78500A4BECDEABFF90260BA867A0F7775BA14624BFD3253D9BCE871F89C2E7E627EA1C7D5559945BB54278CF965F6062DF38F392C966FE86728C7B6
	BBACCEF73FD4300F5858614C24BAA1BD20FFB6455596923BCE388F87183E8F7166E202DFC16E8B93328A8AB12D4776D47334F4DFFDCCDAF8F0B0FDFD1034569432B45A8B5534A68E3CE0FA13664B2D2BC97C2301B69FA091C0AACA484A87288158C64BC3C456BE385676AA756E41C1372B566329F3DA3D6DEE2B6C047B2AAA20F05CE9394F6D35CA924B7367870A8C7C998B185C9FEEDA0E45A4268C73FF5669EFCB095F7D12E915C1CC92E32E1A3A0BD8ACBCC5EACC186D371D41ECC7967917CFFF5B9FCADA5BA3
	7FE3D1DA06016D29539725C5F3BDF19A5BAB0AC8DEFF0F160145F0CF645610FC27789FD3FC75FE954F4AEA934513679E204D5D0FE523FB9D7A5216BED34AA93D4FB08ED1BF0AB06DAEE6BED497877A92F700683697B31B0705AF09941F2441B3DFDAE9B9A54F5D879232F2CB9332D8793AAB1B545D858793321281524B92323683D483F483B84B300CAD4ABAB04EB256E171AAF5C38E27D576CACC762E55EF40D59ED13C169617CF31387A64AE0B57E169F54ABE09735C371A442057CFEEBAAD7BFDE023C9F649DE
	0B62F03BC03BE5D0D6E42F0F71BC34FA8361714A145A699814414020C7C23D0F2C5EC46A2D45271C75D8AD0A2CF2B07A77D613BC39EC69820D20G6ED9DC1ED8CB629581690F8618AD62D0320668BBA9FBDD32332A2A4D6D9E9872F41214C86965A44EC7E85BB19F78134AB0460F2DA338E620DFACC7BD7DEBB6EA74189DFD44FDE80C300EBDD1888D3B24GFFB4C09C40820064F2ACC37F550DE5483C769129E851B443B2147915C5E969952C6CEC2F882CCBCFD39B44CFB3502CG61G519558A73497F68768A3
	BEE81F15F263BA174C7168FB70364426331195701C96243E2F61578C2E4B0807457F5D9C121773A0DDC9G52A14C0319EB72D9192E3378E4AC45273CD0GE52B1AA34C4F9F9CF072781E8230G8C83D4G54836C85F887E08740AE097531242E3D8BF084E08F4087G05G7BA5529AC992692FEA81C83DEAG98GA885A883E89171798FEAB78FFC2E813083C4824481A481E49E06F181C0B7C098C08C40820095G791589D94DG9E00A80039GCB81F20EA4E4D582DC84188C908B3098C02A027E2B8A733E23B8
	B655383F9A8FE863708E0F439E9EA70C4181CDFAABF83C0EE8F4E0FC6AF9FC567138A07D940FA9BEFFE8600745GB7904467618926757FA5E0F977C31AF7416ACDA8A06D5BE600ADD497EBC7C9DE2E576465FECD3E11FA78A64FF35401793C166773BA1E3FC8A7F576AD4D731EA0E98944158671B9304D787F91D81B9415CD62ACDF16DD0AD97648AEAB5C78B86D2B59A41D3C9F3539054CC3946F01FE379EBABA1F40FFAB1B641FC5D13C0E5EA1C5BEA3AB8A8CFBD57AF729AD5FD02FC76D3251566359A40F5C43
	506F8A7E9FE613BE2B4F6DF24934F194A21C7EA46DB3E527BC88CE574368DAE178275612394600BC4A7B6CE7563EAE11BE57B9C47AFFB6FE0F3510F26471BA862149A5C388A93A0A74279D4EBCCD0CBAGC766A1A3F00D28423EF0789D6C8BF7DF7906B477930F49E8EC9775505D2664AA5697C9C7553E1079B4E71D316FBF22FDB7477B182FF75A5E066BB82693348EEE12927B098E9B8CD1003B146176542EE6316FF2BB078665D2647D451175AAAF57DC4AE9AD7BC9FA7BE5B78C003C2324AE307E6D638D4477
	FCC8D3B9C05BD16C5F0E2FA7F49CE3700E2D5E6DF4326C83DA4CD192631C22DC9EA3ABBCDFBB4AE23CCE50EF9374E5701CD99DC8CF9174CA20F568688F6EC77AC5200DE8684C4F421C9C928B7953A1990606E4B0F5D2141503B99B83E8DD3C5CB0FAD58EA9B7CE4DB8ECF9B531393F846724132B59D8EC86371BD84DECB33EE5022F66989B7BF49FE3FC76E33C2C9CA7FCB125C29F60E7G4CBD662FEFAE65F39EC43E3663ECBCE5056BE40098001D022F2F8C7932CFB03E5DF0EDBE914857D90EFCF64E978657E8
	9DDFC385724D63FC09F015EA82798E9DC23E7C9A46D789579E9D5F1E4348F70D73CD05EB1C060F45AF2F9279965730F8AC63574CDAEDDC9284DFC1AD235754B23DBD35DA3B530FA01F1373055732FCC1BEE637378A7922B4FA74797646D1640B557019791560B36B683CC9704D2FE5E327A44E3F9C2E45C25F34EA646B2CD375D9F57AAE9C936928E3E3B05497DE27553779B872CD2FD369FAFD4F8B3E59F5ECACE72C6F6E934897DF475239842E4B81F26B357ABE2AC13E5D75ECCC184463D2DD6F67DF2DA84F75
	AADD2F6FF941E72DE7E3CBE3FDF75709F2D54F52C97C1BDA9F286FEC3DC8EFBD9B23A671B8477B69DBEBC63E44FA152E57771C60DBDE4F463A017AD85BFE7AD352E652F524CB164B1611E25FB03440D5D52D582473B5224769BC0F48E41BF1AD6867AE528E1B613987C0B1E37F746CDADC8B1ABA90A3F546AECC8B366E95E1E6BE471A557E29A0E2B80D3573B19B094DE5C0139A300D7FDE0EF6BEDA6B73397B9CF4AA4C5BC76B86ED7F45F00E8D246F31B93CBE25858698A358163AB790FF49DC1923795A416122
	EC0975CF39E0372D81575CCA2BF6CA86EBEEE4CD202A2A11F5E1387E52D706B1083C7210F4F15A8326B5C1E2905E40670E8D1AB50F2C0AB4664BA34217C42067B622AF95F4EC9A688B7615D5D5E7302765E9FAE7BF6AE9839D61GF39A897E62FEB2D6A1ED93631BAF7824A6E8AB1B10AF231870058B3E2E926473GCF8CG6B337FE7C989DC218CD95D43456CD26FF47B20CC951072D5EC99D60A69E82F78C3CAECA077FBD91A9E5EC272496A70F95CBE39E0EF9746F266269A2C635F1EA9D55F7858E43436B8F9
	3D5D1454446BC0339A4B8589AF1816GB03F5FAC4D66653893F8A21A516F30D212BE456E60E39E290F0CA53D324DAB7B6C8DAE3AF6C3DD6271FC369453BF8FF424GCCEDA1781FDE4A5578D48E389E00309634D3F970A0DCADF4C0D24CAE9D325766768E4A56408855D2B20B904662111D5AD8E4DAEEB50CC5449F7ACD09ADFCCD39D50DC565F3AF185A1E43D8CCBB0831E8831E70D674F1E799198B757271DD3D5BE5F55045A69868F58FCAAC1D73FC5AF44EE9E569DCA2F41CACA33AFDB299FB495696C59EE47E
	3FD22B75BFEED504217F762FC719D6205FA7D57F673C7402E941CB68FFC4997A6F811E1813E8FBF3F9D07F4F217F53860D7CCFBA09657B33F2543D9CF0C5ED287B750A203A4F236E23D6A35D56B6546D2AC05DA3000B953A7F96DCF737076B7E603A116E25C277A707D0F78A604A5BD17752C341E3023ACB5B0DF4BB5BD1F747E1549D8638B8217B5707034784F55FF156C877B2213B3692F5273F8FFD687B28BB06629DAE50E8F1BA2EC033406A27F77D860E2FF78F763A6B5CA37E757335CA2C1F8A681AA574DD
	5ECF6C7B4FB7F0AEF25B91EC83180E1F9CC19DCBC0FE7BA954312D1874B15856B25EBB2AC43B82FC9127B0DDA70E0AF682F0A9821FD10D78728E08F187366B394749154E95A5746BFA75AA55E75E8EDCBB0775A8ACFE36143769CE43FAC47B158ED6FF62BB54FA148EF5A8CF54239F88DF36FF10105579817A98FE8C7191001B7781267F6F47088FE2AEA7E2FE7CB87227GEF79E95413FE8271CE40C58BFC1C40A782AE778C6A5FD50379404A4E0B7D5A3253F606159D0F60EFE27C720842745ED603FA4301E70E
	60DBC979DC16C1197BDB0BFC4B012728D378DB07F8AB60268BFC1C40AF84DC76D9EC732D3211FF6633A8F73D9E653A8197F9D65477FAFFFF8E1911EFBE70C8DDA81FA270150073F4611A658F036E8D5139E8177FBA749CF8E6FBA635EE22EFC1975BB3A1FA997EA445A7E9704C7EF3AE343F8CE81D9F62BA7FAD1B49DAC59CBCAF84C886C86F06F99D00399B47EBFF5BC0564D465CC7E9F1A9B01C33B84F78AD4C60FE46C6E2F7C09E654F5F59282D9FAC8E470AC89CAEF69B2F3B9B8113BBC965C6EEC20E257BBF
	5FA0799D83F273G92811683641E43B49F5AD494AA4D389F44934B7C796E84537164CD2703496D5DBFB1FB07775F2CBD56E66DBD2F5D8FCF5F50EAA2F9EB54E6151FE33A4D678277F0FEB1C11F93EF5AE7A657D392EA6FB77C1C3FCEA6F7A924DCCC009CABDBDF48A3733639C0CB84D88E10F39E5AD2008EG65BC1631C91B0E05AAE3F46058692E87DC335BBDD09F380EA9FC5DD59A686B54737E3E9927F13FCE2EB860B9763C115C2FF4F2630B696FC24A4D8F30477A5475E7024CF16B0694052E8B323557DB5C
	5A35572473EC9DAD7DC2C2D69EG1B877F7DD2A6772764824EF7BFEB544EF7672C7FAC68DC4FFE0179B77902E6CC9E71D7BE5FED5BCA72FEBE5016G24903BBDB096826886385603F93FF05351F166BD1BC168F31E45734B8747138F53FB5418B239EF04141B8BB20BB5F2AC9F7A1D5B0C738107F0FC2D61B59F1EC7497B887B1A9FEFA3316900E7A7C0B8408C00F9GC9G699FE3EC5E4B2D8B951BDA5CA00078602DC165E0E91DD2B631984D88A9379DFCAE7FD815E3E90F9F4434AB9FE3BFBF29FCE2BE5C93D2
	EE965818277341EBC59F52BFC19F140A0979F0CBC839EA305179C9208F132F238FD3058F72210979608BA9978FB61668FC3037238F3BAF228FDD07A76603B524DC8F58D0AE867A7023336843EC61C3D365445A6B33BA39620067640BC6F229C7A656DE2F8FA917EEB112FBE60276FE9DD2EE3B213DFB2AA6964F2F6A648263D9EEE82F61684452F7A624DC1B213DB555933377E6C8B9EB00BD56D6576EA0ED75B520C58144824483AC83486ECD48AA6945367ACA6836DA9CFF9ACFDBBDEF0269CC88A9575CEBA477
	45E393339795D26E2221BD73710959BB9DD2EE44505E6A9393331797D2AE52505E8FA6E8AFBE24DCCC00BDB69E79EF9B996F990CC75AAC3D3213353F1B6D247DA5633C393D587EC654CC4C4F87C24A2DB04E775A8966FBC8391CBE43FC2F1BE03E0714ABB0347703895A0B8FA9D793E00F6569DB8318275DFD182761759373E1CAC839C843B4D71BA7E62FB524DC0C21BDD343446C6504141B9BE00F1D8B7DA55DAFDC8C34E5G8CF756C5F039D618CBD945D846CD56404A45738774192C1F71BE5D95F8DEF222C0
	EC8462180DD77EE1B60C4E20313E6E0E4D414FB9CD59ECFC4EA9EA3371B927FF5D3C0161C1077674B1529F5EBC76B928EFEFB6BA8735037A7FCB93D96B738E39944720CC4D3236D29EA1675720C7BEBD64E25DB5316C8BDC4F92B67E74C670747E758D6374263CE11CDE531631533BFACB30743271480D1D24CC35D9F15C7160160312FE5C51E0F5A8EEAFA628D336F8CF59ECE250F146A377C9BCC67F291073023AF3E16FDB8655B57BFBCB32B8CF03622EAF953E5E1B97BCAE8F649947E5C69E59BF7739073CFD
	F2E05A3F1E17A7E86A40086C3791F798CFAC15D7AC5EFED9973B4728BC2385131F1B97AC3EAC9ECF533C76C01649BE3A70E036A8969E072ED2ED9C8E0A782CBB38DA4537171608BEF9EF10BE39436242BE79F1B71EC530DB71AC42B1AFA937B360B98EE001E8CFCCFE6FEEAC65EDC08A3F66487AB601151938DDE40DF7B750EBG3A81BCG11B216A16B1BA157359BC646D8576E5A9A3C9CFC3455389C74ED8DDE8EAEED8DDD8EFCDB031703ABDBC31703C9DB47AE873FF5ED91BAE8B9B8F5D976FA9DD619568C16
	6F898731BCF4A86E0EB2D19E7EB861FDF1164F12B2D6BF95EB104FACCDBF5F96BC9F96EEB34E075FEC8B1E8FAFED8B1D8FFF5C96BC9F7E3CADF4BE3C3A0D2CAF76BA2C8EAFBF5948E278F419B63665A236AF15FD3E58BEBBB8D6EC6799441659FD3CDC6BCF0540A714FFBEFF062DE37933B828BF5F2A507AF3C860FFDC71797C39737AD87EA48775E77AA12DBF0785FE76214F674FE07BD87EC836E07E5CF9D86BCF2540BFF4787379735459317C498D6A4F75CAED5F21369515C734FE9E91F8D355EA8D1E74A98C
	BF3FCA2BC72D97570EEA706D2582DFD73D8675583C6E410E033CEE1E5C913C2DB83D43382D685E913C2D78E4C76836423EA3F8DB615E913A2D9859E15CD63CD62DCD3B5AAFBFF94CBF6DE59CFF753816DF4D0B5A937E7C659C7F1793DAFE92F3D696365A35E5210067FDB53FF640559AD09EF89D2D713703E7442F56EA6D28EDCCED1DBFFF857AD52765BFA47CFA4FEF1CB042FD08605799F01DED685793757EF68EF13C4F2C3523566DA38D7E7C07793867BB4384EF73DA8621CB25034D1EFEB22EF9D93C8FB09F
	6CA78224GEC87DAF5BF1EA3F03A088E45215071F7F4BFF97FF96EFB12307901150C0D2004A824401201946F4FF71232A57AF3BEFE65E78B5C01F5F6E93F1A7E14FEFD1DE5E788BE7291DBB9F6F606E02F40DF21F827864F4E965453F19BF91E8F345CCBB8D7FC134E51AF8EA4E4A98308BBCE77052260B9C6603E6643F9666C819C1B4D5D953C1EBE354B381E7EFC17715C6339DDE34FBD1E5F95ECCC447C5B2220FF69CE74F969A062BA856EAEAB622285AE7ABA62168BDCF8BB624C0308FB71AC62268ADCA9DD
	37DA824F4B856E29F4024BF4A5E4653990D71401323B856E32CCF08ABCC7885C318154B7C3608E15935CECF81EA7F0DFAEC7FD8B84EE51BE54176F46FD54CDB48624AC14834E692667D27C4E6411327B3BD436264E68BEBF7A86DEEED47AC3FEF4121F2F25E96995AEADBDC3A34F76F11FF99777F1D31E09151C5F8B8F7A3E7EECB7AB7F0B79B5F2497D29CB1E3F1246747C2F8B5AF38EB98FB8CD29C17464F8783B2A9E4D59AC36FEC267B89D0037FAB00EEB3C985B499E1C5F7479BAC8DD0267F800E4006CCF93
	320AG5AG1CGE1GD1G31G73819281168124FB219CG948154G5881A2GE6F9D1F79855CD5E65A06FD510F7B5FA78BB9164FD8C72CE48FC60DD8A106903FC85E8863083CC860881188F3054474E4FE7AA6C1CBA535D622DC955470378CBFCA17744FF444F722A77A4FDF62F3E7D898673F95A93BF8DAE936F99DBDF0446DE3281AF09E7DE00CFC5E358A39025CD9F579996C2A61D6797BE33D8CEB70CA53E3FC86E57EA70FB836A483B630CA381B23FA3EB9FEF0E411391C207765DC723F79E717DCBADEECD
	881D1BB477388637A1048C19333E95A037C9AA99A06F6A753A3D34110FD2C83DEB925F84A900679AGA7C018426AG13BB6B43B3082C7F8DAF00C7A9F7082ED7784CDCBEC95167AFFEC3E18D77E1B97090584CE91920E3973FE77A0A955457B3245717A161DA8ABF1FBF246AA37870A156872B7AD68E223E48A07AD6E974C583CF5C102AAFDE276FA1AB6ADB9CC4DF16C6DFB270C817D5FD39178375BDF19D7565DF566B4B8AC8EFB970B4EB74756874FD399D755903685326779A70C4EA74C56B747D65AC6A0B8D
	22CF1B5EF94033D823AFD9230F1525E6BBA9CBB6982F158F937D9F533EFC8570258FE3F9FBB15D3F3CFDFD2FDA5E6AB3501F11E1637827EA5228ADEF0496B19C684F1DB2FDFF466D157B3D6410B744187A75909DA7C441FDB4G435968B8E19E5CAF903E36177B7B7A33FD2A2F0F16232F5208312FE99ADFF30107G7ADAB0227A4A6C7F1FFD68D3798876B5F9E32EDF98816B471FDC056301590FFFD30A3B4D9C74DCD7A777DD6157025F5E1FCA2EECAD717705741BB6C01B8B3084A085A0E7947A4CD19C33E66C
	61E7C649B2F42D22D87A6C748DA631406E1007D5B419A0B05DD96455F41C1F6EA1EDF9176512610AF6ABCC54CF75DE02D9E02725DF646D4BEE525EB9AD236EA1855B3BF48F36F75560DFA740B50048D1F62EB9E614703C6D4DB30803FFDBAAD511711676BD616E41FA3B5B51A7B37A39A69D5D23254F5D97C8BF67A77F68E59D5DCF5EED8E241F7723FFE5370E6E27FFBD9D131F9AE04784996E27E7C8DD682E9DF17868AEB126F5E5AA411F53616B72887E3C8EFF4F88A91B5D038C49F0FFF6D21CC70BFBF99541
	1D73637B49C784F75E8FB7A8D3BEBF593F6C25FCFE385293242C1C16FDEE67E559AA5E696EA413C7AD5F2B975EB06273CB435183245F95EF1A69F42C25F5C15039ACBBB35FECB5BAB33FEC944F54BFE6C8CF3F0274E3B6A3FA1E205B8C69C5027E9B43F78AEA847D0621FC27203FE4A8EF9774D78C5F8938A6680F984AC78AFA0D217DE8B57D0672F1D7701D8177G1E590F87DC7AD5FC3F64292D7E6FB052AF055541DC59D9C747BCF53245057B640F8D6059336A2B384FF2871D2FAB709C8190FD954F400E3C07
	ED652C07E7CA4D330C5F79CC6A7C4CB467AAEBA3132E2AF31EE583AF18184D7F6D433D1D6D575838071431CCC97DE3FD486F0A49BD337DD2B65A5E7E2D2FC966DF19CD6F9858266D74B5E6B37A1AEAFB2AABBF0D597EC4C15B090636171DE0362712D79CF90CCADCA8137B1931CC70B4BCD62A6A79259575345D341EBF9DD475A4DFC7BD61B72DE7E3392AA73E9D754C39E9BDA7ABD4BD6FF5211E65B72D47F5C83D5FC70EBC631ADBBA799E05CB9E4E970B4F0DB0D8AAF85BCA5FD9FE3BA83F24A83F7420F8E7F2
	4630F6BEDE397A96436FE590A83A4E4AC58D3F26ECFEA01525C34940F40CG0D1703EA553F93B52AFF6F503AA9FC2BF7BB5D5E62DE2725EF0071BEDC27726EAB9FEFDA189F3FADC7BFE22F0FD5BF1E2ED7ED6CA98C668F9BC35CCF7B492520AF7D860CC9G2A81BAEE6058A43B107848CECE870ECB9A2C7DE46D1AFC65CC91FB7F05E36C7D97067EB65A4AC252E7763A21579DA47A911FD1C47AB3456D21C83E77DFC472586B6837AB0C1563F30AC8CC1C320D22D91BD8644966796E0434C55F906FF5DFA4679545
	1EB389D2D5D51357E12D337437511191AF839EDC33CA3C016D61CC2AB3B78C7A96GF3983607178634ED92E9E7022DA575047139FF185AA62D93756706D51CBF8B4335337F92389582275890D772AF087B0A8D5FD5308B5C86BB62E6885C77855FE2014B93FCB937A0EE2F60EB9638A57D088B9338E14197A7F00A60DBF68B0E4B07C2FED731E012511924C9A165F686BCB339B906F266C94157DC585A75CA5A67F5CFD2679A8C1FC957342D9ABCABE777F8311CF90036F0923E43F038110C19AB43E19C81608488
	870886189B0EF56D4E7DA5D208F3851DEECF9B6930B54B51ACFD4BFC5A74AD841DCB4347DECFE2FECDEFA2F33094604D3B956ABEC0A7009DA08CE0AAC08C409C0005G49G52EDB08F83A8G2884E88338F89B26E15F81B299B51C07G12D7AC5AE8F8AD8E52EC6099F93FBDFC8574CD3EEDEC7F19DC05CBABB79DE4E247A5773C5FBE72BC10D9BCAE39BF7B6D77A6030CF47BF8641E755B174D851902F14995F6E9652AC126A3049C4BD7D38B29F3D6608D3B9D7346FD34F00C3C69703ABD32D799E5D87A65A16E
	43E80656079920EF7638FCDF6A778E4AFC10C98CA1474FE8190377A1B16650FD48F74C74BBA0507613F99D43FD5FCC663AEE1B4DA70B3E6A89F30E403578BA5CBE7A6ABA737D2067E6578D18DC4D8465C6EFDA0E35C3711F1276E6596D016D53A20ACF39BD30FD0A917DE066E4E807A6E37BD456CA5A2785701C8C107385A81FG55G1DDF40725351D0B6C679B96DF6ABE4FF116CB5C255163D1ADA4E4F590ABAE77F02DA8E2E717BC8FE0D5650185CFF8B39B88DAD1E5FA7716BF21D5C288D65B23F285236737B
	92FEB5FFB1D0EE264DA8AF3AB91F13DF43F4F2EF580D6426F23E98FE1D2313FB42505E824E3704DFD76864F6985A4B3E030FA578357C0EC039E2C3FB4D1C2F07DFBDBA39BF749B4905F33EA8FE1D25133BE6E8EFAE67DB482FCBF5F2230676D2B8DFCE843B6E0ED06558D934391B48F84B61F2BAC8E924DF5415F0CF76B9BA2F2D841936889C0BE410ADDBEEF31D667EBE6FBF6AECF070FA9F7BCF35416AEDCDE37076342951F8CF3C3D51F8CF7CCC6358670BAFB4867F4EE2EF2376BB0BACBFD651F977458856CE
	B1DCB6450DF09CBF475A94BC8D0FB5056E9366B6994FABFE5A241FD7BC53E4BC2FF836C9BB2FE038DFB611B475433826DB70BDAFF06784AE096AA4388F9DD645AE703F27FE91FC33CC8CA2BE197AB5CCF15DF8BED003BB07F32AA6522FC1126DEEAF4A3E56C477909CCEA71FD3D19E078B3AB9910F74E6622343E351781DA5F067C4DEDCB5481FB03F7CE138B6171E6F83171E6F952B1E6FEFD6BDDF668DBD5F5A9BFA3E256DFA3E175B75FCF5DDFA3E26AEFDB9DB5532CEDB3E59B00853563564CA1A6F5150CF0F
	1D81C6D7BF5397192137B1ADC36F4B4AF2BDDFFA05160F3F1BC6FE79G472B1A7BEFFB7E796D52D37FD49BAC1E552D416BF4FDEB68BA5D56EADC27CF376A6B74072D46F57A23D6ED1D6667D8E8F91C8AF9919B0167AF26517D2EF9703CD060DE1571CCC612401D2632A5F742B87DCE442D16D11FD560E652B97688BCCF9638D58EF116C760CCB63D3EC89B6ADBA2F0B21DE32F00674C3B907733FE5417ABF005867A7E0D622C4023883E1DC2B6CC603E5A2F175DCF5331GF812845F94BB4AA68B1C5940EE8245D5
	5F8DFD715D08BBAC6CF68B5C4386F64B290D98600993FC778B3B73846EB1D9AF5BC06D66C6A6E46D0EC45CB7EDA8DBA2F0DE833B92359186BC1102CF96F6278BDC06C1FA6F2538E460C991FCAB045D4CFB4439B6833B1194578DBCF641779791ABC560767A25171F2F9FA06DFA9FD97A13F02FB4153E23B58F64965C03EBE116773255760DF6D38C3FFDE42D0A47EE0A2FAFBEEAA5B40FD73649DE2FECBD6338A28B9B4F503A10827AB32720AF33E47F7D60781324DDE37B419BEC389FDCBE45782C04F62FBF702C
	C47314403DEB07152C0FC2476838C266A44EDA27235FC5D6B039FF1344DADA67945C43AE23718B03678861733F597DFD7ED31B6A73CE877A9C974467D5E3789CBFA5F08F1B55454500DFA26CDF6B77373F71FD557ECB7DE8BF67DEE37BD9E3585FFDEFE04C96587CEDD51CD2EDD588DBA586365246489F02B76BEC1983625A5D215A7A069DEDF59858DA95E04B8ABC845056084E569F8252F5758355567BA2DD61C152254D43A8600952581A2533F5B2A0DD7FFADA3595AB5295974416B63F6201A7DEE3ABC9E72B
	A7A0DD0B4E28361E9475A9B9C88C35691226027CD44DD91C290136368424EBC527EA6B1AC8D77ED4E3DB5AF41583CF3946D6334E56B78352B55A255ADAAF722B5340966A417291D88E6DBADBDF8F28DF53BFD4EDE50BF4A9C1ECE94B7DE460192CC957F40DADDE964ECB422E4BCB76F07C76FF687ECCC1692112A28287ABAA76F239A7AF28F2DF14F7864A199D502E119FF5A9B8D4D114DFFAD86C995559F04FA8E6AA6EDDAD69D1F5BDE04D17587E90FF5D2E788C790F5BBA3E6253ADCD4D1DCCEE55472A5CFD76
	717BF04A01BEA4899F76FDA2091834DFD4751E6F9F3F5E0DF6541BB78D75FA484FE0F03A2D9F699D02FEEF2FCA7F0D101FBCED2C7D346F7729B2B7044E39E34A7C5A2A4A3CA46C2C98D3A6C5D6E5DE9179D6722531E4F6752BB20F88BB76B1E56A6C2ACC0DC84F0CAFE10CE407CABFA6F4AE9ED36764CB641E3F576D375E13F39F0C336E9B4FFAF127C3ABD789B2ED63125B62378EF691E414F1498D782DAFCD86196963127BF7BFFB31A0B3EFDCF261FE769603CC723864FE6FE7CF0A0271FC54F8647E66178F85
	A0D3BDAE39C77D6CF5000CF5DCF28DFE699B811908F1495579591B81B233C34831316E47B67DBC3A4F261FEF8FB874FC9E071E2F582E67AB336B79EE6957735D5E2F677B2B013E578C74FD59C05F8C83FDFFB750E7B2C847BA83FD1B8C74BDED206F97867E759868339868BBE7206FE3BFFD6CB79DDFEC3C8BB2FE564BD206A26561913CFC716D60FC32EB63D3A45329130A141ACF406715560B7E2005AEE4A5DDAE2BCA7B6D5752CCD2FA0B74BADB84E8115249CC3EC54A20937D96A9CDF1FBC6488F24110506F4
	8E6BFD1FCA574513004F05DDFD53D82F34566F5447CF9E2C78FD7C0BDD3EB469759669CDF2G780C6CE55FBD3612F76A790F36B5CAD99E3783080007647A68A543270CBA65B6A957F61A7CF808CF112D62638590484DED445201A14561BC50EC715995320AF234CDCAA71F1FD5240D0C6C34387A8F30652C23B4E62935B59279A313C2897C5DC47DA5DA0A0516CDCF2CFF772D56A5F1E91229D1DA6DF4DB2C0D16BE456DED113294BB79FC3A5BE99D713083B2D266B0A98B742CBB285BA612CF92DB5C8943AF6174
	4048D7A256AEDD5BE8022263158ED0CEB2BEAB969C56FA3B458B26E4EF0BC2FEAE4F6D5DADE87F32D3F96F67CDDBFACCD2AACFA44902F5A39EC5FA5DE699F4B8C761067D46814D1C0D8AF86B3538FC6435D670ACBF40B3A489834577BEDDB26D9B2F1E2786D4D1C55A45C289228748A7228F545AEC8E17AC94C0DCC17D8E211E70948770EC9C39E7F36B22153DD08E4CD25AE0C9897DDFCA7F9FA47FCBA92614E2CA8FC2F017C9E37E8552C7B04D3470081AC5323F3891F2D0794D4BCF4FFC3EFAD9B2242ED14A20
	1F5B05CC48D1395E9F9A6C153D374D1A36E34D5424BBC89EF9C8D199A1FA5F9889D2E33C52BA7FD1EE6AC3DBA6655CDDB6E412B29BC93EB8BDF6CB8B3757A62D6925D5186CA2BA95A9C70DAB150542EB6A03123DCD4553658DC6BD63317411721D856D4AG55C0DCFA770214E5737677D283503ADCE83CGE66993A16FE913D6ABF63AFB8985CD1136282ABB489EA6A7302AA12D847050BF1A5CECDDF5A1589CFD39A768DCD17A4853964A7AA67D3F7D267312C86D243279A28B0A02E481186D4E370CB8A0EE2F1F
	431B737CA623BB7FBFA6CFD124346E7CC2F8BA471EB24EF36CB902CDBBCF7E67624160FCF1B2388A9A695CA67ECD74FCF16837AA1FBA321B86EC460C151A27A5CB34CF7067517CE9A89E4F23FE4F6F3FFF4D0F53ABED544F93EA6F7A752FE6FDE24EB5C9A6689A6CB26B8BB206490A964DBE3205D56B9724F51236E52F7D87710C5DEF2C3E895EFF96F635CA615FE93E21BDAF3962563E2067A5F39E647B2F8F2AE75758D902D7691A7AC540DFFB904F8E3436121795023D1FF0E65062F4AA9667C02D4BCA5ED528
	9BD5E4F5CFF1E5AB516FC39ECEE43457CF923FAFE9E47EAFD0CB878847A3538CEEA5GG14FAGGD0CB818294G94G88G88GE3FBB0B647A3538CEEA5GG14FAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG28A5GGGG
**end of data**/
}
}
