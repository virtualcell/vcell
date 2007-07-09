package cbit.vcell.mapping.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import cbit.vcell.geometry.*;
import cbit.vcell.mapping.*;
import cbit.vcell.modelapp.FeatureMapping;
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
	private cbit.vcell.modelapp.FeatureMapping fieldFeatureMapping = null;
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
private void connEtoC4(cbit.vcell.modelapp.FeatureMapping value) {
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
private void connEtoM1(cbit.vcell.modelapp.FeatureMapping value) {
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
private void connEtoM2(cbit.vcell.modelapp.FeatureMapping value) {
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
public cbit.vcell.modelapp.FeatureMapping getFeatureMapping() {
	return fieldFeatureMapping;
}
/**
 * Return the featureMapping1 property value.
 * @return cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.modelapp.FeatureMapping getfeatureMapping1() {
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
			org.vcell.util.gui.BevelBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.BevelBorderBean();
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
public void setFeatureMapping(cbit.vcell.modelapp.FeatureMapping featureMapping) {
	FeatureMapping oldValue = fieldFeatureMapping;
	fieldFeatureMapping = featureMapping;
	firePropertyChange("featureMapping", oldValue, featureMapping);
}
/**
 * Set the featureMapping1 to a new value.
 * @param newValue cbit.vcell.mapping.FeatureMapping
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setfeatureMapping1(cbit.vcell.modelapp.FeatureMapping newValue) {
	if (ivjfeatureMapping1 != newValue) {
		try {
			cbit.vcell.modelapp.FeatureMapping oldValue = getfeatureMapping1();
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
	D0CB838494G88G88G640171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BD8BD8D4E55AD656D2D1EAD1D9D911E1E131C5E545EED4D436D9D159E605C5E59B3BD93F7D6B367E3BD6BC6B61A660202012E2D1D12122A1B7D191EFDCC68523A0E58915CDE618830C8EB3534CC1D1297E773B3C5FB9B3F306C15AFD1E7F67F9DE4F795E6F3DFF774B99255CCFC3531212F3A5A9A9CD62FFDB12A449F6CA12AABFFF4805383176B60324797B8CE029743AB1951E2110D7B83B4D10A3BD
	7EF9862427C0BAE8D61BE1A53CAF171E7EFC5A8F7062C7CFA864456C7875D5C36B191C02FA52E9BADC4E02679200A84011E72A03680F1173B8BE1E63911212A5E9AE25F9C94E673886C8F782C4G44B898FD9CBCB32966F32B2BB8DD4738F0E95E632E6445C40EC6A6C2151371F670E70174DED26A8FC4D7EAF7966789013CD8G62FC0A7475D73B60393D65D84F4EAE33D5A9B95EA55BECA5FDA62753EA6FA9696937D6D79FD0DC7DDDCA3FCB7E1AE16B9C7DF60B49F5327524D3B6DACDB6C70FB40A6AE86A4E20
	B2419FC8E739886E7D55C46FD4C847834C9378FF9491FC8B3C77820CF2212FBB56EE673E341F3EC7CA68F9A4B35B0F2F63DD4C4798176AEB41FAF34AAADAAEEF261278A7C2DE013B4DD089508EB0GB09E60A3DA9F66262C07E72DD929F77475B96C35CEE71D4D627A5AE111ED70DEDD8DB99C37DF6EF238AC12444AFCD1F7310EBDC381634BBBE3BE9613700DD87E917BEF15626F3CBCB9DDA7A671612713A35DAC96535CEACC186EEF7D6A1E6D762C1F1E3A3F13343A25B7AB5253F4F467BFDF111E486564A82A
	6E96051435E38DA96BB2F8A7FC24D1B17C0F949F2241333A1AC471A4BD8572727A31EE34AFC1DB1AD2A7CAB965D76846E17CC34149037D4C0610FEEFDBDC6B096C18FE26F30B3065F20A2FBA2E62F93BD91745531110E79BE8B3307A754EC252F60BCE35990CG9DG8AC088C0A44054D3D8473626168E330E3518DCCADD3F55E611DD9263FDA7F585BC658145E5EA343B95133DCBEEB3392CA633CDF6CB1C667DD4928333871F14C66AFE976858AB5BE517C931BA6CA05D54A7AB324B4DE8B25316929A17CCF3DB
	2DFDB2A868F3CAA8F7E99A4811ECA63772CF27452448AA854BDF15C64A6438298DA402GF8E7F179B40344AB817CCF8448BA0DF18821723E12DDF659D6DD5D64F09C6BF73692CF240E53A44E3D346FA800772A5398630FB289AE9452D1C24E93E94BB4F20C56AEE2BEF4C658467A57C047AE4582FDA211BB58E6A882B08EE29D7A6FF62CC346453F160A1F3BCC378E0D057A5CB1486A0EB2685D164E21BA081D93A0AF9AA09EA0F190470474B56DDE720A86BD5B9831CF2F4D99B99D5A6E3FAFA97051116915CE71
	535E8D7C1946E5695130781F3F0814E57E0FD0A6G6DBFE299ACDCD8406AF41D49ADE3ADEEF6C18514ADEA09B0BB87565BF8FC1B81CCGA7GB68154832C83788260E300CFA5B6461236369AE0AB40DA00EF813E8458A0115EC81248F8D58BC05A55EE00E3G65G9B819AC5FCA6D03DCE30F9BCC08C40EC001C1F409700CE00A000C800B8G69E728A7GADG83G21G53G6682640DEAB354G74820C850882188510F1D61B218C209DE054D9D8760B56C5A6690FD74381ED9C3E60F1D84B6304B1A855783B09
	47EB2BC68646271E47E7890F8B66B77318E27ABB9DBBCCBA38E3FEECBEB1C2DF7FAF81AB3B13346C7C351BC0C07A378FGD6D2D92C9FA5E5192DA94B8D1AF2A36DF095AFF31401653C18177392DE3E18CF5A6CE71A74DABF3EF8634A7D586C5DE77CFF8456A7A5AEA4F1160F4BF645A8BBE53B85DE5CBCAFA51D78494751EE07604924F8A70C3B75B0503985BEBB1D141FC9D1DCD6F33FA29F109585263DEA7E4AF4224B5DEFF62AC3B66A7A044E0F9CA7E05C9574DF2413B12B4BE1374B34F394ACBCFFB59DB3E5
	1B5C87C657436CDAE178AFB348DA6318FC120F5925998BA5B2665A7A49784F666F11A6D20F1CAEEB9FF439E48AA1A51EC546531F3B73B4B18A879CD907CC07E742D9B8961E7882474242D3132518A34EE43D395D78C9E3120DE73331287DECF5ACE4B695E46A5B3E060E5D9C6FE636DE547DB9BC07311C20EDF039945FC3E4F4439485284B99EEED6DA2967BB6072D3FCFAEC75A2736E62B34DCF2B94FEB5CC07C6D119DB081F21DA4ED010DEF8F99086D8360D3B0C05859B83ECD4CA479B8476083DB3D43E6E345
	C7C2F78EDDE352F9G09C7B4704EB81B45F809482FD90B798D106EBC47BB7FEE11BF965261BE79070BB0BF8A5253B5794C4EB10BF2C8AC649F7AE5189A124954D7222EDC3EE819D7DE9B2FB7AC7F5AC5245E58B473302651C4679B6BF1CDBA6B9CB6971B4F75D60EE63A995D4DDFA25D7851EC6E93B91A5145F03A76F3891D39D848837CFC003CF3BD65651DC76856ECC03A30F359FCAA9A1E33GA4487FC450FDDC02F41673985DA0BCC34F7326DBD20AF4B11CAE8E1E09BEF48B84DD7E2F98DDA5BC5BFF65CDB7
	378C6986B85DD8F8CE7121FB2D9C69E6F03AB9704C89D269D87CFEDF01F4E5C1AC9E8D7C5999240DCB70C6249B45734787B139D30234FA5BAA11EEAA270B8FE26502F4CCEF7EA6241B2B1163DBEE5385DD4679AA5D847EA4784E73693AE9B352951C4F66CED51C3E891EA5C2DE7396240B50480B761177C1157063FCB687C3F9719E721E96F473B4793E72AE5B0AF4D99730391C3E3C46EA24AB3C0079399D1ECDG368B3472D6EEC33A418B581C1048A374A197F858A76842AED073FD65DD369D6922AFE0F3CBFD
	F90DBBC43D3A0079C96C1B63A3AFFB277077C2B6C72D62F1A93CD0AB6FD1C1D7F9211A6FAB6F22DDC857F4A11B6BFA4BE3FD7B7E9FC81FC977110E1A0E1B86CA5CA720872E2E5E07DDBA5FA3FA132E73880F65C25C8BCA6CA57D7084C8CF81083D904727E79663DED04831F0A97E4D72F4FF7BDE8997B21BB3025571E94A1FEB9732FEBEEBB15159GF96D4158472FB2E8572335EE37234BCA17423CFF1CE6500EDF8C7724010CBD5DD617DBE9048946G76254FD2FB49DA1923795E41DF68BE869903EC20B7AC98
	775C4A2BBF11F4765C481EC0F5759EB604617ECB57C60C4158D313244E4B2ECAB67809C1BC77BDD19303621F6627B1DBFA5622AD1597C199DF0436EC22F3D3EFDBF02C2C2EBE00A3AD7769E5A1A78CE4448364DECC709F9611398A691B985D59C5C8578E79E3AFC63A87A8DD3C204B79926926C13EB4264D40466C93DF1641936A1045F12204BD6AED8EB75429A2D23FCACCA71492BA5BAB790EE66EA66F6B18CFB72EA465E4313A1D8E37DC342E8DE3B9F1F98D0FD56C2D93256DBFFF1F2C373798145D1BDCB506
	3701B1EAAC9BAEB127F4GB03B1F25F693FD91C013A06C2EAEA67EA93DD6BE67113A48DC52A5F73BE4F76FEEBB5D3B21A671F8DED80C7E670FEDB334824C99CB700F1567EAEC8A865C9400383128671D628AF81A68042404BDDAE4D7374355A7DB3CA3D4CB33D904B0963F7EC49B0B8C53393A3188FE33A739F29C5FD39E2746A29262B0D54422F98346A28CE86247210DF19B48DA484C67F7758E3B45CAB71BE022575EA7B1BF7F5A2775B37792666776CBD0C6DA89116D16495CCB36B4AAF29F337F1DDA2D7DD1
	69413A76775EB4BE396592E67740A52A7D15571BD39A2EC77B7714207D5300C63A94F5BFDD62577E03E87F45321E7DD517E27D5ED20A321BGB7DA480EA975AB7B904A5E31D5CFF63410DDD406322783AEA384E55FDE66BF6ECEAEFB1F22A73BA684E52FAAC7592D008B9632A7167B0F894AFE24CECF76D4A1FBD1854A0E83DC4EE5A87B528A7FB1C1595B1A75E4B7DC06325F9732BBG97A2E41BB7923C558E92CDB66BA96896D87BF4E5AF6578FAC71F59D1679870EC1FF7ED447699CBF4DC0E724A0A08FE4F75
	862EC5EAAA318FE0B2FE2EC4995B01DF91B21EABA2E38C76350CF677A6512FGDD426568573B1BC4BFF1857493D7A07E014D088F865C54AB30DF4F5DC91EF42DA821DDD5DB52D51BF9BFB0F8905B512858A9D25E720BF45B919DD72EE06D27F03C5A0E86C34DA9E3C3319D8DECC1DB942009980FB66E2CC2FC8260722FC47F6F29A2B608351C0879FCC15F8A3441D7221C7B37A2FEAA6092855ED5AD46362BE0EC3B8A654F2C46F2E0F567EB33366E04DD456A4EB4C11F340D524B838A13DB338D654683CD6E55C8
	779BCAE7B775495CDEC15784B4232F9676EEC7FCB46066893CEB87620BA73499AC93304FFF61081EFD93A6A0DF55CE640B845C6C89223D6F7434E72E20AB388666BC57A07F5D3B90BF96F0532E41BD4B7F9E72DCCA3A56F39FBA97526C4C44E9A5720A2EE5E7A6C4AE431F2278AA8D1E69CF36227E86480B38967779DF4AA6FB95B95735990A810CG9DG03G932E43795A61AC32EFB664B9CA23DD01691C49F640E3E3864FB396933D476413BC7D41E2ED7BE0F1787B9A9207286B74775D750071252DA5FC534F
	000F79DD33141437948AED822092208940960ABEDF3538B8104FF89E441DE576742D9D199F274F588F46777A88757D650C7531BEEB5DA16DF9F86A52FD4924EC757A2C60D0A6FBC2287799CED05148EC3E3CE8E4B1DA71E520335F78D0CF190C2FB0A01FF43DA79F2BDBFFD8C656EDF910D7895084508B908C908E90FBBD5631B7966F88D44768443155D18F38860763D83D77BE2630F5260F2DF33CEC5377718D9F3E922FF446C4BD3EF145A30B69F581798A3C7431B1B57B001FB5EEDD3F2250FDC13677FA3BD5
	3B77DAB5116D23F5405389405641D2C58637A7E8A22EF7FF5C23DD6FC6E77F68F72D97436D1BB5D12D5705FD2D692C6CA7651232AF38816651G2DGCE009000C80099B7E059D7AD59B64C32E7AB885F12E7717C67066114433C9B5418B23E2E00FCF9E1ED0632B0150F15C30F6DA37DF240A99C5FEBD8EF477BA84EB09CEB72D7105804C3FAAAC0BC40FC007CC9D0C6G9D13B0B697AC298B941BDABCA00078602BCE63E03EB6150CACC6078272A9E0F370A4150F797E080C3E47CE42F1FEC76948EC78AAA0DF6A
	0D90439B3DEDD036228D9DB7228DA5E5A333E1FBC03E90509161E343D98A5AB0C75870E97948EC5890102F70A6E8F3B7F95B10D807B68C5E04B664D40C4C06CF8272CD819D31BEB6381A51062CF034A1E963487A6BEC9F3E922F34B1DC0F6FDE9FBEBD1FFD799E8B4857212B6F5C4A11693BB4A01F222B6F2BCDA30B67C19FBE6FF8866B6ADB6043B7BC7F96EF8E4497262B6F1100FC7A7A1E8D48976D250F7555D72DA4FD758C481B8B10B1394DD08850GE081881A0CFD75AB017BEAF17DE9B8FD75CFDBC666E7
	F0C03E5049FAFC462A11695B9F10AFCAD75FBFC628AFEBEBA03E693A7A2686645357777B00FC33F5758DD40FCC5FF98179241B756605FF25E76DBA7311A613D936317EF73419743FE41E17F7B3763FFB360D4C4E5600FCADB76B167B8875E5ED8F44572B2BEFEAC0BEBF659E10EF142E3E019DA3AC7700FC633D7431B28D1131CCA3C5196E5CB9B29B1A82724D567579EFA354173AABE0DD3FC50FAFBAA01F3E3E47837265F969E377C253689D10B248EBGE038513D84E7037428DBB06E472DE48FACD824C751B4
	59BFE3698C2B77FD4911C230171D2A0F1F7D5BF4685DC1E3E3DDC2367FFBCE7F152DFF4F69AF597A771C7ECE773E818FB234370FB1FFC17650772052F3746EC1FDCC6DFFBE196C75397A6D0A35CF26EAD9DFA98F107BEBB0A26F6F3733611AE8F6FB6FE7899D8DCB7D7B5B3CD45F5FFFAE5577777052217D6DDA6A4FDFB69F094BA3F5AA6C961CF73C3734C2721DF76C36D895078B9DEA15CD2E666EEEB169B860143BA49E23B794F2DF5067DE58B726BEF54F7E6BAFB3B94DEE45D1DFAEEC1D334CFFDC7E3ACCBF
	AE6FAEA36767EEC73F2BCB7676DDDA1EA772540991B9EFA366707BF24B893FE2F27548BE314B217CAC4F9F7F2A657E624B6271580AECDABEEE376C269B8FC613E262F148A9564621C244E7DA71A2957FF5F919981357799913DBCCF69C135F3462DD0418DB70AE024DC66A6DFCC867C434990AA2307FC873D8F755C430BE2015BFFBA3FC7B84D6E716FEC876F887A1FFBCC0A440B4005991D8076EDB9EF0DFFB7740907B5A37657A2F87F7676A570368DC7F7560615C4075A0B657FFBDF8B6B7F0BD084F9D3A9E3C
	EEDFA9E450FA50FCDCF6392C9619368CD66E419B30BE34A80E160D22BE04ED986939B8AB67D27BD063D4462FFD4B195FF7DC613F9CB62C50AF070DAB7C17C3750A4065D03F42FFB96CDB91389C8E2CA07B0BE62B456A62B79BD98C4FA9514636D244766A12DF965B476521E21B2F93DB26D7A9555A33C96003CBFF19BDA707DCDF147935C7AE535A33D960FFAC7BE5761C278CE50F512FBDED65DAFB36883C3D7C175913D4BF14BD6DFE6D59DB213527CA600FD47CB2FBCEB48FE50F4D2FBDD59B35E3075AD73C33
	D1EB67D6010F2EDC244113B105614F5E241523360B4D1BB5782F4B85BEF1338165F43B9CFDAD953CED062D745FD75C32D23F2F38FD257F3EA2EAE5603E6241157E7B0AE9AB8377954F2C546FAB6E5A2275DD9D17CFD7F97A3E1163B7ED55522BE5717ED6CF7ACA0E3F2DDACBCFE24E6A4279E6EDDD98454BBE04BF4361996DD59FF89B5D6629876F08EF5A2E552376B16FEF77245F04F66D5052EF96F6DD695147CD67B6A47067FCF8E65D2AE7D77FCECFBD1BB9FE43AE2D9E35ED7FF517A77D96BE6F397D8441F7
	3BCCFDB02452496614DB693D5F9EBC13AA00F4A5C0AB008290F2AB5EA3383A1748D02C8A1DFFA75ECA3EFFDEC5266BDC673796B2B7029A22108ACBA6D2FCBC4FA3F5CB0C67FC7E4A6F96D83D5BEC4DED2A7F2D377936D9F607A0380F686A3D0D5DA1D8A77013A9FE2A864F6FD450F99BC997C43699EC3763DAB11F1E33C55D51E6083D83F1FFA8A3383910166EC45C5EF2DCE7E65DA96E08FE683F1D96FC285FCE57FC283F76A87EF06835C765077E66C44C3EBDF6342FC3587C1A0C3888019B3B95F10982F70D02
	3826A9084B2BC75C84013B3899F1F384AE146EDBED3F8BF86FC25C13BF93DCA7241D82D7A1A16F2040BDFB0460E2A11DA0F011B24A1BAFF0DDA58417F5F71BA17FEE44CDABC1F9C582B7E6B54A9B389B4FD16B687EG298B41001BFAB73D1752233DC7C36A6E9F13581EBA4B1F68113F14579BB5FF06C7BEA94F77D3347916DEEDFE1A061F1D63BE319A4FF1931F0814ECF70D767B3DFE56BD2C7E1771E741D3D7A6650CEFD94864BCFDD8FB4FA167AADBF2129FB93D1CDFC1B952FBC9FC7F64A809CFB860236F41
	B8D65BB036336E41754DA5FDAD24ADC535998A810CG960051GE1GD38162G6682E47C264DD0GD08950G50816084988DB09EA086A081A07537A87BB6AA1BFC4BC13E2BA05FEACC615FC4106FB148B7A185C0DB835089B08AA094A086E09600F4AF6483545C4B6E4FF75E4B6E2973BDBFDBCD12EFBC083D449672CE6CC7BC31895F09FFB13F997EBD8E62F386775B9FCDE154507292B43CC69D5A869ECBADEEF4G19F3357E717738GBC9D3C3CB04DE27904EEAC717BC5723ED803DF6755C6D68FB30E84487A
	0E6CFD2C9A02A6AD008C6D370FFA5FBC627717DA1CA1004C651AF75C03DB9A00072CD9BF73620335DD8579D64F6CF051CEFE6E3D744EED850E4123A0BD9EE0AAC05C3D2C8DB03EF74B708EA29BFFDFA61F4CC936E8427FFD391173D7C57B16AF7E0602015B5084B484BE60F91D707EA9FFE7727EE5C7F9D3F46425C93817426F67EB6491FCFCB49B03D5F973E514B75B0F3CF40D3CC442F31FAA2F70BEEFF9B7EDC5F9E577694B4B5448B382CD3BC61E4DC75E9485658D68484B7472B798E8C2B572267848AB2DC7
	F9B1FE64E97D1D81B433B57292FD64CDEAC6F999776B4B537A1B8FB4E5772B720C772B72787DDAB329CB5DB0DF8B3E1F0E5BF4ACEF01770E7B313EA57C6CD95F24C2353EB5CAE84FF49DFB70DBD17431492BFEA478587356917A7D0C43A5773848A5EF08B135AB39134E4760BD910061E652F9C27E83B07EBF00361EAA7134750B552A2D2F16202D6D8F685B1A22315586B4845056D18F2836B27DD12B51266087F02C595B93688CC32F4CA46941F41C8FCC796D4DD254F9C63F773AA2386D317C9995FBE592F932
	3D44DDEB68EF5AC0DEDECC1BE1BBC0ABC0AF4028981C33FE7F993FB3CA3621EB95455455CB3FE0929B6CD679040AA68B84A65BCC3AD3DC1FFEC6825BE6BA2A3B233D8F966A4D6623308AECB57508324DBFCA7ABB1B692423DF417EEE63D16C6FC2403E880099G33E3583DE669C1C2732DADCFA78E1EFD29D4CD66DB5A6F045B7B6AFB9D56AE19659F5C6B136F5466472C774EBF6841FF47F11FFC8FFE0751BB7F10C77EB505BE799E7233AD24AEB59FE35784996EF787C8DBE82F9D303A69A9B17A3AA009608F7A
	606B72887E108FFE5CG291B6DFD8C49F0FF31D11CD30BFBBB1D608EFA50BD72AF02BB64016B13A91D876F7F2C23F49E3872DD242E6C175D8E5BF159A23E69EEA50BC7AD5DFB07D76851F978302D140C3B62CBB39F990BB317E873B9AF3BB3BF6B8B3DBB738D8F621D7A37F56F54F708FC0BC9AF5FA9722F504D9FAD72EF537D26E03C480F52650F9079B3F579E3C47ECD3A7CB3C47EDADD7E59A27FE3DD7EC4113FDA17BF67A1712DC2855E59AF84DC47C378FD49239FFAFE43C8FFA92C8E564A36BABA672913CD
	F6BCA77FED855EBD8BF9884FD9CA6937CB3110CEGC8FC886F408E2C413EB26246095274BF6AFF73B92B7547645C07D99FD97530663B2E75669426F3CCB91E6DA88F33F98F29E399127A4746101DE49A4B7B695732D0F77EADB7C846970C49DF68682636F11D099A5D23BBAAB3196E0B6D283BF22A1E6E393B186E37494F0F71982D935F455A7C707877A127D81593379565041D311C068D2A1CBFAAA8A77E0C659CABD165F456231C5CC74ED4CED829AA6779E61453F446F29EADD35F4FA1DBFB3CDFF970E7B239
	364BA78A4466739E18AC95FDEE215FACFFDEDCD0D6DCD0DEA13E198CBD21DD0FD7ADBADB7777B2880CFE04550B717C993C604AA46647A9897D188EF93C9EECD66D9BDF2D5AE720EDD358D66F30B9DCA5E61B296B98231D37DD257D2EE4383EB0BBF2CA510E0C5F8D55BED66ED4F53C365A1FBDEC8E7146CE12DF8372BAGC68104G047F8E67A672EAE2A33BB96DBDAF59ED69A1FB5764D74E94F1763FE608337FB501FF9B2DFD8D99B34D8E98F57B08FC44FF3F060CE70A43C9117C6CFF8DA9E3173527D7E1249C
	EFDDC36F1A485D944D7A4443C733F839CF855F92FFA73E6B6EA477954519B389D2F575DE1755D2E76AE922B3A3967B9B0E621ED56523589FBEC3E55AA0BD9AE04223589F9EBD266D13C8BF63EFAFE94A23FC6D7F285AA7AD916DE7E52138FFB68D774E8ABB9157A2F0D71B9097A4F0CF1970DB0598017B1399F173856E4F022E6CB14465F4A12ED760DE91F42182773D1097A7F02D02AE67F144F5883A0647459ED040BB51239E573D7795106F932FB463FBC017EF42637E77DC585E758DF44C0BFCDCDDEBB0FC84
	45C7EB702C1EEDAA43FAB68D720A1F40EF984E2DA1F36631100EG188A908F108810970BED6DCFEB4B24G778ADA9D4EA6B2E0EB362319FFA19E775C0BC1E6CD6C507BC94C2E37EA499A2C95E81DGA1G91GB1GF1GF3G24A75B8C39G45GC600F6G9B40A80031GE1GD1CF228F672CA303336EBA84103CE151CE43E53212EE836FEFFB1C6147023CD9CF8EEDBF7FB64163BB48F940137154F078DE76B8C74E871E32E1713D65F15EEB841E76E171499E6732B66099B5AC3E17BC4ECFC300A7BCG9FAB57
	96BA06C783ED5CD3D8B653B6FDB9C45934389CCE59251CE4D87A4BC35C061FA52D8D73C1DE565343317D830FEFD08A0027B2G9F33734FBB7D0FA1FF5B99F88CD930137E8E88747DE4DD47F0293B48DA57515D6D1645D81539ABC7609A5DAD8EB77DF41D59EEBAFA267B860C4FB1C23E70B366E37D507B47C8FF537034F77F14CA4F56DA1F766E1FDE9063E0A764CDFD9A7B27330C24FFAA1A8EED9C2097E094C088C044F42CBF576E5AB8C47D596FF0A864FC111CB5C25316DD1AD64EFC4CAFC49FE3262B75E086
	FF1F4D1F091ABC46372E9379F21ED1738A79FB95FFB6BD634D97EAC23ECECD1E425F0378F3028F5F33A63D320864F4D379B34E076F3F4CFAFCF3B81D74FB764C7D3DB75FBCDDFDC51CEEBBFF367870FD502547E761F4837C996C43773A2E3ED0CEB705BF277970F5697A974F696672E76A335EFC87F57565F13AE27E2C7161B3696AEB65F43D7CB92861E3F751E2681DC72B5DE6A535117E222E04E7324F53F56DD86089FB9667A21369DA033D5F21F95FD6766F3A9B6C3F5D377EC7F530F6FBF10D7F7E74329A7D
	B3712BEB744F442F2F997AFE71649A7F3F3398D9237D1DC5D69E3750B5FF54332C1F62674D94B71D63986FBFEA7D7B10D697F8CCD8D2273F2E482D73DDD72C2A53DFD7FCDA27DDD7B0DCE19D71298766B56D02EE1D409D943892AA13603E33DA14DE012F24F691FC03CC940A3B04542E9394574EF13BB438039CE72CA3639A385C6BF0A1EFE39DBDC3305AECAC7DB525315AE118937158CFED34BACD9A3B3F93380322ACEE367B164FEDF6ED79B05C6EEEDF3AFD5D3EF473367A523D3B5517AE295F17EEE13FAF5D
	E03DAF5DD9753EF46FB47B527D3D59371E7D33FE0936FE33E9904F6B284F15B43FC7C3FFFA6CG905AFB183C4BA5DF9DD7CB3E3694177A521517EA6978DDBE1BA45E97E95EDFBE7A1F6F177E769F55413F57B67AEF53630C015B74D5C67DB69DEA74ED53130C7AED7A962336CD333258C8575DF320ACB2E6607D0BF927685996240B856E60E93C13D1A5F0C1747ECC902443852E269365C58B5C5F691AF8BA24E789DCC717384BA3F06BCC3E726EB1213C6D4FA16EE10AEB01F427409519D11ECD60BAF4643DC5F1
	51C093AB681A85EF1C40A9E6DF5EE4EAF351739003679157A37CB08A5C0FBAFA8DD4DE88500489BA27509BA9F0576A68AD26BA2497607D85440D93FA73854E262377BD0A33814D2020BBA27486895C9C9D5ED314AE8EE8E68B3AD7856FBC017BC327AF2F134AB3463599DA6390771C89F9BB85EE190E3F83949789B4B1026EE521B7D660A6F854BFFE0ED4C17A75AE3275A761D9E96AC6DA1FE736990AE662DE18097E76A16F5F68B0457079830BD5BC8ED3FCFFF1DFA149F33A64EE596512AD872C27E421230B36
	23D6105FB993EDB11C76BC8F7EF08FA9C6F69E6CBC0D674141B3756FCAE84F7A3D6FCA044E74BE3B3E6E88599F050151FA0A2CC9EC35B6EB0F1D6CE0F27BFEF384774DA2E66299361516DB9C2493044DCB3ABCED5E31D735595D05B6673C28EFF37A90B6973E68FD06BD0B558740EFFF91753FE872545F59206A5FE5C67D3DFE74E78E21FF70C56F18A519BDF51D53286A6A963A02F4F4258CD1BE84BF41C757AA2F385E38CF55751308EB380E2EF4AFDD51C0C3GF5CD7751753917DF0FFD256A7A1570AB5E0FDF
	5AB21C8BB4F3B53AD2DF7256D56F65571CA6D5D738DADFDE525725AD2FC220A1003A2AFCF4AD75726B0BEFD4DD4705DFC69DDD69DEFE3583CD3BC6174DC757B8AFDDFB1AD5DDFF933A867C68527A958CB4419ADD21BE3AFE65D5679FEDD1F54DB1212E889DDDA8876B07F7BD0C7151B5464B2F373ED5F54D93FE457A51252D773300E616462FF99ADDCC76EB2D12503B2C0C1C61F81C7F50731922724DE54584AAB6EDDA4779D25BD43EB7CFFD624DE734C23FC67ED317224D1B0A8B4A3708B3236BCF631911940F
	E7D7EB3FD3E5CDBFD2A03173A17E39DD498172AF9E6B38CB76B76EEDE8E5FC7B5AD53EB966615B905605B6D4899B7A4892037BB67610AAB74AB4FC39EC2EC064BA055CBB8F93D9AC7F8A119FAE721F6FD073EF93F64D0A9F6ABC6D5DFF29BCD1C2E65E4BC371649FD1F9E68ABDADC3726C6AD4F9EE92FA020664B11BD41E35C2CF4C10BC671AD51E0F051E79AFE30C423B547C55C2E659ABC349FC42C25E595A6146535AFD1BDE609BFCE5B87B4513BD76334682CF5830787ABD76092200A7F6D8FC93BC78E6814F
	3CE171CD7130B363D518EF3EBA9C3E7BBC7415810FF1D8FC2FF868EB879E5B3078A2BC3F37811E10E17195FB688B871E68E171FD6E21EFBA70A48C0BEF3D073E794013B5AB40DDE8BA573D6F346FBA7A21533E6B6D18AEDF3AC73AFC69024D3EF4634C3EF4F76A50C56950BD2123F73A0E5E99BA72E66A487B9F9DF96F6948FBD8C75EB49DF975BA729AF464ED551137D3C7DE2D0E3CBD9E72587F69F8FF6D45D07091EFCBE90A1407D7708A44EF879710D39B37A2A5B7FF25C8C985845CAEA9DB0C870DF4A3EB56
	710D55A92F5F1012AC25B6CA8B59A6C02314CAD6720DD29ADD68B7CAA90A43B9C07E03B43251104AA15B7D037413C88978C558C5E70CF5C90BBDEEFDBCF27D26BFC13765CE1196B6CA2B48856083320B7D6E3105FCD34F7F5336BDD22653E105CC40033BEE7AC8F3ABA7EDF213145B3D1F7C67A1EEC536089FAF00C0FE50C4B41576ABD6DBE903495D2B10DD14EDCDD2AA7979D9C5DA4632EDA6FBCFA95B4E5AC6E316D4DBA311BF32A814405E6554DEA225C4C8D9FEEF766A4F76A5C425C849FB24C5B607493247
	5425B8DC0DD22652CBFEBE5DE1338CB8F7B1C199A7C8DD20F75DC15CC742FD62ECC9ABCC3F045147862E89DEBCFB711EE418CE3A24D2CAC966E7A50242D25FEBF201AA595528107FAE4F61AA94F9E7FD222CF9F26F4A0EE4A909BBC90AE04900D3E18193350938DC32872CD6DEFC7B3109AFED1FBB87D876C8E974A7E6C1F10ECA75CDFF1FD9F61D97F159470610D997923B1CA4BC34F017A95242EED31F55F692DE587F1D0078CC45E5323B4967322034404BE34C920617DC7AD859E5135FBBC48DD7D99569D3D6
	C440DACAFEFA3A34363B5BEA1705GA8AF907F3190CFE8CA3CE8168D0C7BE05FF38B4CD03F0CD2CADFD9997D371C7EDBC17EAD2718F20AA92F00C21BAB8D79671DBFB0C0FC7EE840CF0BF1C9CBBCE739C913D61E13B3E6E3FF32143107444F666CB5B57258B7C986B3ED4264945126C8B92A7F1497AAEFF297546C0FD4BC5D5EE039871C26AED23FB321DFB9C6A59013D69F16B23BDDBDE62AGFA1743FB8E03DA1AA259EB1B24C5CAAFBD3D040A26C8ABD551AD648C13E73026A1ADGF052BF6AEE16CFF3A1589C
	5FFACF5039B4F385A1A7AF2BC425A129FC1A79897D773311954186BFC5GFA5A8BCC83D6085B420378F2083F2435977CA16892C5CAE9AF7892D287D9AA6D90479EA45814C3645FDC3C98DCA0EE86D7C3A71D3B177FA6FA013874DBDDC08D21DFA7AC888DDD20C9A5A4E8D37067547CE9F21C4EBBBC525FFCB368C1691216792E93EAAF1E79C24491E3EE32148CC3C32F4C46023493E4C70B969FB9422A75885292C95B33577E1BE8069EB7961D816DFF8A3BC84D613F537C33763E647CF33B7C5E176C1D4D4FDFE7
	2BF75758DD0297F753F9G60E74C463B836BF7130B2A7E3ECFB850E7325994136DD82D5DC23ED528BB29486A19622B3B09FCB75270CCFEE7EAB7317B280667FF81D0CB87883E783B8617A5GG14FAGGD0CB818294G94G88G88G640171B43E783B8617A5GG14FAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG51A5GGGG
**end of data**/
}
}
