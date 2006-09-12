package cbit.vcell.geometry.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.beans.*;
import cbit.vcell.geometry.*;
/**
 * This type was created in VisualAge.
 */
public class GeometrySubVolumePanel extends javax.swing.JPanel {
	private javax.swing.JButton ivjBackButton = null;
	private javax.swing.JButton ivjDeleteButton = null;
	private javax.swing.JButton ivjFrontButton = null;
	private Geometry ivjGeometry = null;
	private javax.swing.JPanel ivjPanel1 = null;
	private javax.swing.JButton ivjAddButton = null;
	private AnalyticSubVolume ivjAnalyticSubVolumeFactory = null;
	private boolean ivjConnPtoP2Aligning = false;
	private GeometrySubVolumeTableModel ivjgeometrySubVolumeTableModel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private javax.swing.ListSelectionModel ivjselectionModel1 = null;
	private javax.swing.table.TableColumn ivjTableColumnName = null;
	private javax.swing.table.TableColumn ivjTableColumnValue = null;
	private SubVolume ivjSelectedSubVolume = null;
	private javax.swing.JLabel ivjJWarningLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private GeometrySpec ivjGeometrySpec = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GeometrySubVolumePanel.this.getFrontButton()) 
				connEtoC4(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getFrontButton()) 
				connEtoM6(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getBackButton()) 
				connEtoC5(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getBackButton()) 
				connEtoM9(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getAddButton()) 
				connEtoC6(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getAddButton()) 
				connEtoM5(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getDeleteButton()) 
				connEtoC7(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getDeleteButton()) 
				connEtoM1(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == GeometrySubVolumePanel.this.getJScrollPane1()) 
				connEtoC9(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometrySubVolumePanel.this.getScrollPaneTable() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == GeometrySubVolumePanel.this.getGeometrySpec() && (evt.getPropertyName().equals("subVolumes"))) 
				connEtoC2(evt);
			if (evt.getSource() == GeometrySubVolumePanel.this.getGeometrySpec() && (evt.getPropertyName().equals("subVolumes"))) 
				connEtoM7(evt);
			if (evt.getSource() == GeometrySubVolumePanel.this.getGeometrySpec() && (evt.getPropertyName().equals("warningMessage"))) 
				connEtoM10(evt);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == GeometrySubVolumePanel.this.getselectionModel1()) 
				connEtoC3(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getselectionModel1()) 
				connEtoM3(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GeometrySubVolumePanel() {
	super();
	initialize();
}
/**
 * Comment
 */
private void cancelEdit() {
	if(getScrollPaneTable() != null &&
		getScrollPaneTable().getCellEditor() != null){
			//This line will apply current editing changes
			getScrollPaneTable().getCellEditor().stopCellEditing();

			//This line will NOT apply current editing changes
			//getScrollPaneTable().getCellEditor().cancelCellEditing();
		}
}
/**
 * connEtoC1:  (SelectedSubVolume.this --> GeometrySubVolumePanel.refreshButtons()V)
 * @param value cbit.vcell.geometry.SubVolume
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(cbit.vcell.geometry.SubVolume value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshButtons();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (GeometrySubVolumePanel.initialize() --> GeometrySubVolumePanel.geometrySubVolumePanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10() {
	try {
		// user code begin {1}
		// user code end
		this.geometrySubVolumePanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (GeometrySpec.this --> GeometrySubVolumePanel.refreshButtons()V)
 * @param value cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(cbit.vcell.geometry.GeometrySpec value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshButtons();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (Geometry.subVolumes --> GeometrySubVolumePanel.refreshButtons()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refreshButtons();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> GeometrySubVolumePanel.cancelEdit()V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancelEdit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (FrontButton.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySubVolumePanel.cancelEdit()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancelEdit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (BackButton.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySubVolumePanel.cancelEdit()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancelEdit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (AddButton.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySubVolumePanel.cancelEdit()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancelEdit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (DeleteButton.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySubVolumePanel.cancelEdit()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancelEdit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (Geometry.this --> GeometrySubVolumePanel.cancelEdit()V)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		this.cancelEdit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (JScrollPane1.focus.focusLost(java.awt.event.FocusEvent) --> GeometrySubVolumePanel.cancelEdit()V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancelEdit();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (DeleteButton.action.actionPerformed(java.awt.event.ActionEvent) --> Geometry.removeAnalyticSubVolume(Lcbit.vcell.geometry.AnalyticSubVolume;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getSelectedSubVolume() != null)) {
			getGeometrySpec().removeAnalyticSubVolume((cbit.vcell.geometry.AnalyticSubVolume)getSelectedSubVolume());
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
 * connEtoM10:  (Geometry.warningMessage --> JWarningLabel.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJWarningLabel().setText(String.valueOf(getGeometrySpec().getWarningMessage()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (Geometry.this --> geometrySubVolumeTableModel.geometry)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		if ((getGeometry() != null)) {
			getgeometrySubVolumeTableModel().setGeometry(getGeometry());
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
 * connEtoM3:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SelectedSubVolume.this)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setSelectedSubVolume(this.findSubVolume());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM5:  (AddButton.action.actionPerformed(java.awt.event.ActionEvent) --> Geometry.addSubVolume(Lcbit.vcell.geometry.AnalyticSubVolume;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	cbit.vcell.geometry.AnalyticSubVolume localArg1 = null;
	try {
		// user code begin {1}
		// user code end
		getGeometrySpec().addSubVolume(localArg1 = new cbit.vcell.geometry.AnalyticSubVolume(null, getGeometrySpec().getFreeSubVolumeName(), new cbit.vcell.parser.Expression(1.0), -1));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setAnalyticSubVolumeFactory(localArg1);
}
/**
 * connEtoM6:  (FrontButton.action.actionPerformed(java.awt.event.ActionEvent) --> Geometry.bringForward(Lcbit.vcell.geometry.AnalyticSubVolume;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getSelectedSubVolume() != null)) {
			getGeometrySpec().bringForward((cbit.vcell.geometry.AnalyticSubVolume)getSelectedSubVolume());
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
 * connEtoM7:  (Geometry.subVolumes --> SelectedSubVolume.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setSelectedSubVolume(this.findSubVolume());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM8:  (Geometry.this --> GeometrySpec.this)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		if ((getGeometry() != null)) {
			setGeometrySpec(getGeometry().getGeometrySpec());
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
 * connEtoM9:  (BackButton.action.actionPerformed(java.awt.event.ActionEvent) --> Geometry.sendBackward(Lcbit.vcell.geometry.AnalyticSubVolume;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getSelectedSubVolume() != null)) {
			getGeometrySpec().sendBackward((cbit.vcell.geometry.AnalyticSubVolume)getSelectedSubVolume());
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
 * connPtoP1SetTarget:  (ScrollPaneTable.model <--> geometrySubVolumeTableModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getgeometrySubVolumeTableModel());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetSource:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getselectionModel1() != null)) {
				getScrollPaneTable().setSelectionModel(getselectionModel1());
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
 * connPtoP2SetTarget:  (ScrollPaneTable.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setselectionModel1(getScrollPaneTable().getSelectionModel());
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
 * Comment
 */
public cbit.vcell.geometry.SubVolume findSubVolume() {
	int selectedIndex = getselectionModel1().getMinSelectionIndex();
	if (selectedIndex>=0 && getGeometry()!=null && selectedIndex<getGeometry().getGeometrySpec().getNumSubVolumes()){
		return getGeometry().getGeometrySpec().getSubVolumes(selectedIndex);
	}else{
		return null;
	}
}
/**
 * Comment
 */
private void geometrySubVolumePanel_Initialize() {
	getScrollPaneTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	getScrollPaneTable().setDefaultEditor(
		Object.class,
		new cbit.gui.ValidatingCellEditor(
			new javax.swing.JTextField(),
			new cbit.gui.EditorValueProvider () {
				public Object getEditorValue(Object obj){
					if(obj instanceof SubVolume){
						return ((SubVolume)obj).getName();
					}else if(obj instanceof cbit.vcell.parser.ScopedExpression){
						return ((cbit.vcell.parser.ScopedExpression)obj).getExpression().infix();
					}else{
						return obj;
					}
				}
			}
			)
	);
	refreshButtons();
}
/**
 * Return the AddButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddButton() {
	if (ivjAddButton == null) {
		try {
			ivjAddButton = new javax.swing.JButton();
			ivjAddButton.setName("AddButton");
			ivjAddButton.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddButton;
}
/**
 * Return the AnalyticSubVolumeFactory property value.
 * @return cbit.vcell.geometry.AnalyticSubVolume
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.AnalyticSubVolume getAnalyticSubVolumeFactory() {
	// user code begin {1}
	// user code end
	return ivjAnalyticSubVolumeFactory;
}
/**
 * Return the BackButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBackButton() {
	if (ivjBackButton == null) {
		try {
			ivjBackButton = new javax.swing.JButton();
			ivjBackButton.setName("BackButton");
			ivjBackButton.setText("Back");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBackButton;
}
/**
 * Return the DeleteButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDeleteButton() {
	if (ivjDeleteButton == null) {
		try {
			ivjDeleteButton = new javax.swing.JButton();
			ivjDeleteButton.setName("DeleteButton");
			ivjDeleteButton.setText("Delete");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteButton;
}
/**
 * Return the FrontButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getFrontButton() {
	if (ivjFrontButton == null) {
		try {
			ivjFrontButton = new javax.swing.JButton();
			ivjFrontButton.setName("FrontButton");
			ivjFrontButton.setText("Front");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFrontButton;
}
/**
 * Return the Geometry property value.
 * @return cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public cbit.vcell.geometry.Geometry getGeometry() {
	// user code begin {1}
	// user code end
	return ivjGeometry;
}
/**
 * Return the GeometrySpec property value.
 * @return cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.GeometrySpec getGeometrySpec() {
	// user code begin {1}
	// user code end
	return ivjGeometrySpec;
}
/**
 * Return the geometrySubVolumeTableModel property value.
 * @return cbit.vcell.geometry.gui.GeometrySubVolumeTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometrySubVolumeTableModel getgeometrySubVolumeTableModel() {
	if (ivjgeometrySubVolumeTableModel == null) {
		try {
			ivjgeometrySubVolumeTableModel = new cbit.vcell.geometry.gui.GeometrySubVolumeTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgeometrySubVolumeTableModel;
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

			java.awt.GridBagConstraints constraintsFrontButton = new java.awt.GridBagConstraints();
			constraintsFrontButton.gridx = 0; constraintsFrontButton.gridy = 0;
			constraintsFrontButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			getJPanel1().add(getFrontButton(), constraintsFrontButton);

			java.awt.GridBagConstraints constraintsBackButton = new java.awt.GridBagConstraints();
			constraintsBackButton.gridx = 0; constraintsBackButton.gridy = 1;
			constraintsBackButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			getJPanel1().add(getBackButton(), constraintsBackButton);

			java.awt.GridBagConstraints constraintsAddButton = new java.awt.GridBagConstraints();
			constraintsAddButton.gridx = 0; constraintsAddButton.gridy = 2;
			constraintsAddButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			getJPanel1().add(getAddButton(), constraintsAddButton);

			java.awt.GridBagConstraints constraintsDeleteButton = new java.awt.GridBagConstraints();
			constraintsDeleteButton.gridx = 0; constraintsDeleteButton.gridy = 3;
			constraintsDeleteButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			getJPanel1().add(getDeleteButton(), constraintsDeleteButton);
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPane1().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JWarningLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJWarningLabel() {
	if (ivjJWarningLabel == null) {
		try {
			ivjJWarningLabel = new javax.swing.JLabel();
			ivjJWarningLabel.setName("JWarningLabel");
			ivjJWarningLabel.setText(" ");
			ivjJWarningLabel.setForeground(new java.awt.Color(255,0,1));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJWarningLabel;
}
/**
 * Return the Panel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getPanel1() {
	if (ivjPanel1 == null) {
		try {
			ivjPanel1 = new javax.swing.JPanel();
			ivjPanel1.setName("Panel1");
			ivjPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getPanel1().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weighty = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getPanel1().add(getJPanel1(), constraintsJPanel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPanel1;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(false);
			ivjScrollPaneTable.addColumn(getTableColumnName());
			ivjScrollPaneTable.addColumn(getTableColumnValue());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}
/**
 * Comment
 */
private cbit.vcell.geometry.SubVolume getSelectedSubVolume() {
	// user code begin {1}
	// user code end
	return ivjSelectedSubVolume;
}
/**
 * Return the selectionModel1 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel1() {
	// user code begin {1}
	// user code end
	return ivjselectionModel1;
}
/**
 * Return the TableColumnName property value.
 * @return javax.swing.table.TableColumn
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.table.TableColumn getTableColumnName() {
	if (ivjTableColumnName == null) {
		try {
			ivjTableColumnName = new javax.swing.table.TableColumn();
			ivjTableColumnName.setIdentifier("name");
			ivjTableColumnName.setWidth(150);
			ivjTableColumnName.setCellRenderer(new cbit.vcell.geometry.gui.GeometrySubVolumeTableCellRenderer());
			ivjTableColumnName.setHeaderValue("name");
			ivjTableColumnName.setMaxWidth(150);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTableColumnName;
}
/**
 * Return the TableColumnValue property value.
 * @return javax.swing.table.TableColumn
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.table.TableColumn getTableColumnValue() {
	if (ivjTableColumnValue == null) {
		try {
			ivjTableColumnValue = new javax.swing.table.TableColumn();
			ivjTableColumnValue.setIdentifier("value");
			ivjTableColumnValue.setWidth(400);
			ivjTableColumnValue.setModelIndex(1);
			ivjTableColumnValue.setCellRenderer(new cbit.vcell.geometry.gui.GeometrySubVolumeTableCellRenderer());
			ivjTableColumnValue.setHeaderValue("value");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTableColumnValue;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in GeometrySubVolumePanel");
	exception.printStackTrace(System.out);
}
/**
 * This method was created in VisualAge.
 * @param geometry cbit.vcell.geometry.Geometry
 */
public void init(Geometry geometry) {
	setGeometry(geometry);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getFrontButton().addActionListener(ivjEventHandler);
	getBackButton().addActionListener(ivjEventHandler);
	getAddButton().addActionListener(ivjEventHandler);
	getDeleteButton().addActionListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	getJScrollPane1().addFocusListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GeometrySubVolumePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(426, 185);

		java.awt.GridBagConstraints constraintsPanel1 = new java.awt.GridBagConstraints();
		constraintsPanel1.gridx = 0; constraintsPanel1.gridy = 0;
		constraintsPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPanel1.weightx = 1.0;
		constraintsPanel1.weighty = 1.0;
		constraintsPanel1.insets = new java.awt.Insets(5, 5, 5, 3);
		add(getPanel1(), constraintsPanel1);

		java.awt.GridBagConstraints constraintsJWarningLabel = new java.awt.GridBagConstraints();
		constraintsJWarningLabel.gridx = 0; constraintsJWarningLabel.gridy = 1;
		constraintsJWarningLabel.gridwidth = 2;
		constraintsJWarningLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJWarningLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJWarningLabel(), constraintsJWarningLabel);
		initConnections();
		connEtoC10();
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
		GeometrySubVolumePanel aGeometrySubVolumePanel;
		aGeometrySubVolumePanel = new GeometrySubVolumePanel();
		frame.setContentPane(aGeometrySubVolumePanel);
		frame.setSize(aGeometrySubVolumePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 */
private void refreshButtons() {
		
	SubVolume selectedSubVolume = getSelectedSubVolume();
	if (getGeometry()==null || getGeometry().getDimension()==0 || getGeometrySpec() == null){
		getFrontButton().setEnabled(false);
		getBackButton().setEnabled(false);
		getAddButton().setEnabled(false);
		getDeleteButton().setEnabled(false);
	}else if (selectedSubVolume == null){
		getFrontButton().setEnabled(false);
		getBackButton().setEnabled(false);
		getAddButton().setEnabled(true);
		getDeleteButton().setEnabled(false);
	}else{
		GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
		int numSubVolumes = geometrySpec.getNumAnalyticSubVolumes();
		if (numSubVolumes>1){
			getFrontButton().setEnabled(geometrySpec.getSubVolumeIndex(selectedSubVolume)>0);
			getBackButton().setEnabled(geometrySpec.getSubVolumeIndex(selectedSubVolume)<(numSubVolumes-1));
		}else{
			getFrontButton().setEnabled(false);
			getBackButton().setEnabled(false);
		}
		if (selectedSubVolume instanceof ImageSubVolume || numSubVolumes <= 1){
			getDeleteButton().setEnabled(false);
		}else{
			getDeleteButton().setEnabled(true);
		}
		getAddButton().setEnabled(true);
	}
}
/**
 * Set the AnalyticSubVolumeFactory to a new value.
 * @param newValue cbit.vcell.geometry.AnalyticSubVolume
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setAnalyticSubVolumeFactory(cbit.vcell.geometry.AnalyticSubVolume newValue) {
	if (ivjAnalyticSubVolumeFactory != newValue) {
		try {
			ivjAnalyticSubVolumeFactory = newValue;
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
 * Set the Geometry to a new value.
 * @param newValue cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void setGeometry(cbit.vcell.geometry.Geometry newValue) {
	if (ivjGeometry != newValue) {
		try {
			cbit.vcell.geometry.Geometry oldValue = getGeometry();
			ivjGeometry = newValue;
			connEtoC8(ivjGeometry);
			connEtoM2(ivjGeometry);
			connEtoM8(ivjGeometry);
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
 * Set the GeometrySpec to a new value.
 * @param newValue cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometrySpec(cbit.vcell.geometry.GeometrySpec newValue) {
	if (ivjGeometrySpec != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjGeometrySpec != null) {
				ivjGeometrySpec.removePropertyChangeListener(ivjEventHandler);
			}
			ivjGeometrySpec = newValue;

			/* Listen for events from the new object */
			if (ivjGeometrySpec != null) {
				ivjGeometrySpec.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoC11(ivjGeometrySpec);
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
public void setGeometrySubVolumeTableCellRenderer() {
	getScrollPaneTable().setDefaultRenderer(cbit.vcell.parser.Expression.class,new GeometrySubVolumeTableCellRenderer());
	getScrollPaneTable().setDefaultRenderer(java.awt.Color.class,new GeometrySubVolumeTableCellRenderer());
}
/**
 * Set the SelectedSubVolume to a new value.
 * @param newValue cbit.vcell.geometry.SubVolume
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setSelectedSubVolume(cbit.vcell.geometry.SubVolume newValue) {
	if (ivjSelectedSubVolume != newValue) {
		try {
			ivjSelectedSubVolume = newValue;
			connEtoC1(ivjSelectedSubVolume);
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
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel1(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.removeListSelectionListener(ivjEventHandler);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addListSelectionListener(ivjEventHandler);
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5A0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8DD8D5D556B01318D824E8D4D8B6C30DCED4D4D4D8D4BAC305264698B526D4B4C33DB6E30D6FFCCC79FEE3EFBE5361471F820D5E1C11940DCA8D939164C7C0B4D1B4342B1292E16A4001FB008B17FB2F779E949074DB7BE76DFB6E3D67F211266FF93E0F67D91EFB56DEFB2F5F3D775A7B6CF31456FC923AACB9E50DA4A5AF13785F2EE4C94AD1A52925F06DA7088B325427CB1A3FCF81BE14A29B52
	609A8AE5712F5427E7CA4165AB61BE926E677E21BEFDAD7C7EC81AD03E669C7C7042E7118DF8BDF8F16660FC36EDC6BEAB687D34768C382E8608829C3926D9897F675B33B8BE01639112D3A4E98125C9EA4F66389A38EF8288850834B27AD8383EC7B90F2CA863F4E74605C90BE75BD3D611F6B4EDA26459D85DDAFE4D117EBBF9090D707A5814416B8440FDB4GD1BED5FAE9FFB1DCABEBBB5B7636B41B547C73AD0A591C5F26D83B94555E1B5F56ED2A28781A5F1D6CEE2E371A3B3B14DA59221855F324DD5934
	123683ED403DE4A738DFD01BC441FD9C40C2017F44CC7055707B8C00EAC77D76FCD14965EF3878303420ED7AFB9FF851AF504E740A34BB754B7932B975E36A0B4EAD4466C9D03646D11F1E8BE0GB08384822C27B1E05F7CA5DC2B1A5583562EAE2B254AE65BEFB65A0FDA0D0A99FED7D4C089471DD0DA2CF623A4B1BFFFE24A5311E7B0E07536DFF1BDE61330A274F97809FB250487EFCCD921E31304300BA9139C4C96D39DCE1BB05E2DDEF9A7B8DCE352157777121637747A4E954BF4F8E74F5B39A209371326
	BAF99BD4626B4FE93CED025F249E71A74397D1FC10864F62F3B20D99F29F85E5455D989B8D2BD196C35A6DD2E661CD3AF688F9A2B0A5221B49B0335BDD16A73BC85B0B3B994F2F04ACFF26787E73CEBC13E5B30DD3F21FF681E23737BE1D45171ACB7A6B523E7A742C0B756985GB5G6DG7EGA197B1469E5B3CE308B1D6AB5B557D5DA633D131CB2C2E3A651FF0D5FAD43BFC5862D0E5CB0BD2AF5BCDF233D9F1C81CE6E08B31C133CB3D2BE86C37G0F2F950BE217D51355822D4B50F1953B0351DC3B65C3C2
	E3D7E8E91D29CB8186DDB06C71B62E5F7291DC4D32C37D4EE614D545C9414AEF59C2FCF2DE2E07962181784D6CF25BD65A5FC17FB800C4E107FFFEC15A7BC63143E8D1D1E130DABB3BEDF5C49329201F58F9000E9D0B01BE239FEDFC6B17842742FDE0BF36F3740B7F5134D3EDEAA1624B76DE6CE3DBCD44AF21C09F83908F3094A0EB20BEFD9FC053G4652DF9AB0162AD75DA365BDFF03EEAC85C1DC778C30988A9AF06FD365149711B78A4A628116GE4DD2ACFAF83E8380473425D6DB65E9E114BD71F4F9AF0
	52AC65FAB48D782EC700683B0F53B25E33856F54A154CFF76B7F4BF8BF9C6A98370A7FE67ECB4FA3315083F68886083C04BE8C78A20775097D32C341DEF05C8E812C981D9EE5724F6A6A07EB91G1974CBG4A81F651B25216A49D84984DFD370D77555DG05GDF831C82E88458AC31F97BF30023GB2C0B74097GDB81369319GC8FF5D83F09260DBE1473FD2B9E2C10745G59176B532B814CG41GD38196GE47841F885F086409FA09CE0AE40D200CDBF03B988C085188890833088E04DD5756915GC600
	C000C800840034917569F9GF5G7DA3B016361913127567C3AD20ED744ABE5158E133C67FED5C86BB386D0A383DF7F11B6F66F6790A5BE9B5377D571A360F726B375CFE08BF25795D48EDBF14186A9EA25D68A1527D3F8A4C77F7D15F7F4387AD69035E7A6CB20D3FB7EB7C3CD6F2762D8D929BAB88CFB29E10F1E0BD77BF69573BA4E70CFD22E9FB9B3F1278582341FF2A79CDE2EE7B90F55EB1C4BAEFF17E7F8B30B173D2AE7129F2DE3128550ACD3198610703170D4CA5BE62F9C22BD554391662ED10D79C00
	095CA17063F2096FE4D5351B1A3BD56524222AA6CB1B33FC82656568EE36B9D39264951ACBE248FA81728AC1BFB9174C49ADD60BC521033728424B6F20B901E2D63AC0688356EE0B4A7077ECA56B27CE251767A477EFDDAE111C425CCD729B36A60910C98C5A6C26AE1892C80AA4AD9EC157F426AC0D0DA6810E2C2DE6126B881C63AFFC0EF37C263EBB2568C6DB0ADE6E9AB2F9ECCA25BFDFAB79BB67F8A6536DDF6A4B9EC6F3930EF7B0D9476DA2791A7E8A0975290F24402DABB5B48C7766EA22CB15C5B6772A
	2696D16F105C22DA11E65FD16A93E0C8CDCC9348C25E667519C426B6F73EB43D22041C2E3711787DA4EDC3B18ABA89E56B35E4E86537A9ADEC9E2F5ECE70B6300DBF40C4FF1C47B7EDA365180B71897C00554CE50432257E44D7D71956F0DBC742FD2CBF7395398F98C94A1708F28255A3D9398AD7D63F39834B63G37F024FB7D951D5A7ACBB4652CFE11A85FF7B564E5D73B175FA94A8320ACD4D34E747CB0AF135A7EDC3782A9B8316AB7A2E633727E472D2C1E47AFAB4F49A371EB56643B6D23884FFD96DC6F
	4F85FE0BGB2C6B13E06D1BC968388DD1A9569A2C7B13A58D1AC374C88E0F42C3D1902EEA260A3833C3557EF93FCB99D363748253D02F3C8D7B09A56B6233D3537C05035F3BA3F512C3D68510CAE6C9AC297E6C73A34EBD84E14FD8D4B197348BDC06535046E6F3DC837789A16D7E5DC4B5A4B06EB3E10AF2E8F6982G9FFA2DB7797CAFA2DDB4270B676DE58572B5E2A0212B6997FA84329C5E8F2EC1G11G9B855D5A8124DBF39DAB4F3B0E657DFE1C0E497732202B390E2D8F1A2E53136F06CBC8E763F4FE1C
	FF40F50C0E59256932706FF5AC270C5450BB6D32C6C23A19DC2EC4DD3EAF893A3CB10C2EFA8CDB37840DE1F40B47903A28E4248B9D437257C85EDE54982DFCBF24A05D3E31AC87EE986BECAFDF50EDCA95F69E4BF2E9BFDD3A3F883A2031AC578EF323E37AC625A1DD94AF0F9B4B64CB9C2B0D6779EB11CE8A627E8FF225E3F69917A562DEC3076DBA6D727D47423FC1ACF7F7378BEB6FEDC157445B6B01EBB0C0E41016EE76BA240B89E2FECD607489C1DABB872FC73AC5C1EC1D00F110B6CEEB17E359C8178D78
	E20006F12EF14068B2B788BB7372B0FE0D9E27251BAD681246B139D60EF7359F1B835F5ACA66B23A0758A11F17FB729D97A0372828B82259A77CBBDD6B937A2C71389F78A55DFF29057BF6G3F71B807BF3B8A778303BB432404578BD7F85B6F8C994F773E46BB67703C242A65EC8E9B13CFF8AE05320C6BF17E3AA5CF3BA7D165F0D8DBCCF4BB040F5DC736EB67F806BB3E7DFD38361A6C8E75B0A4E1BDB84EFF371D48CB76CBB81A6F9F5DC97738487C1A83FC9B2E473D563B52BE133C6F35BA77D9F19F2E2588
	6D9054B7D9EA3A616614F4AFF6883A1E699FF13D468E1716ACE3FC333FF47235F6651B1A3B723B4FF7654B4DEA3EE2B4299595BC6B10A4662B9D9D38273B70FA744940A12DCF26FEB060D5161CE0A6433EE027AC71DDF5AB18EDC6F720ED7C20BC8AA0A91860E7F4103C100C2BCC665C536503584A4B3E34A67BC1DF7C50097C0AEF00357B8D58FE89DD17F87705B3D3E26D44F5627E79A4E8A3C134B33EE44B10FCCAB2AB1449C4F3AED2FF4D0D50B781D8CE13DB2E4DE97C819F8E908740F235B84BCE384AB451
	4AE717DA455EEA35F7A946DC527372658B60D232DE482F224587496F2D58CFD77EC07AD18B59D3B49F844767EE2D17B056EE7FE89F679BFB6F6DD26565FF25686D4187FC501E3260C666570C1B1C7E2D995F1CFA860069375D0A7AB5GCD50CDA8FF30754749AFADAF05EB339D8617C3D67B8559EE4CB5688507C7D63D1BA84A743CE723D64F1572C8DDBD83DFEFCB194975CB52689981BA4688BD7FAA74AC8B29CF3705209F6F59236747B0A867FB5DB62CB795F00BC3B0161E5CCF74F340C2EF3F5C52F9257A31
	36CF1D433673A6409CB581E5FA2CD4CF260993D026D5F62C978138059350E72A7D47796C46D42213ECF4AEBD2EDC27E705ECB9B743F8FAB34AD666A0E306ECEFABE43C5E79016CB9F93414CB79B94E1973A1052719FADE8E155CACDB5A72CF2AA41E72F2B4C24192582158738F76586C0A036CF467651828CAFCFC6CF41071116C15845D0C6323DA06F1957E5C942921E30CEEDC519C6DE69ED7B7BB634A7F26B7D2C2G183EDF746118D5F98B2C09EEC1FD4774F1FDF7327B077A485A2E552EA8C257E3B2DD5BB1
	197ACF21CCFE3111D256C757F915A97C96A64B5CDB1CB27507B6278605E22C3F5D07BE58B431BE7D4CC40C2B106DFAF195BC91636A1E0BD8AF8CF00993D117B57DBFAE2EE6FCC076A36DCA17753C626971AB88B19EE3677A5166D9375627575E0AF24677931B2B6DA6875B0398F1D63B8791F0AB4B3342619AAD6AC883C446AEBE8FB1DF3CD9259DF722D6846A7A227D0E101478DB198F241FBBFDD1FCDBF3EA4DED680BFD83A8EBAD50847C9C79A6F42C165C778BD03F6517508F51C03FC454693A047AB13ADFDF
	42368BFE81F959AF106E70E59273E8CBBE17DFC65AD0200B7F850EE3EB7612AB5D5391765DA3A17F4CD0D8B306E23B13A4E2973349211EC43973E94D836D50BBF9AC700C120703C1CB4C220271C8CB963985349FA5F8F4A67FBBF83036EECFF63557AB4928D792704B3D8DED707A67A4AFEFB5D9B4835FD65D30F406A3464ABB9BB0D6B6D5CF124A96F7FA6D37CD3771BD115B1C31923E6A6DE551GCC3E91A9E813F820493C9DED72C68A0E1D0C6EE8AA5255G0D1F200BCCF525DB1B06F4D1C013A468AE253952
	552CC53B647E9256413FC43A7B56227D181D4A4FFFC87BB10CE20E767D5D2AEA35B8F2B57DD24A42F6C2200DB851CEDB9649499DE8BB8752BF1A05722D1CD41F3EEF920EC3FE9FE3BBBD000B90784F56A1FEA160F2A6E37B235791FB429CEC0120B88A53005C26605ED361E66D1857B019BD3F6C97F5A357D19D159E1571F8EB9D4A9484B4B3855D8F6BDDE3E95AFA2413FE856B4FDFA1DDE9F606266F11CC1251DF48C659EDC09B86404628153D5AB1AA917099F7542797GD45F01ED7EBE1B1C03E83536F4BB72
	693F862BC3F59FF88F11829CF7994F728D48738C3495AC5A8B25F81345241AE433298F021D79F73DF5132433CFC9D7060D07B564755CD6227DD8E8BBADCC58EAA35A0ABD97C96DF0BF5BD09966FCDEDA2B790D401E797F1C2EB90DE16C197FB2013F1362FBB4F8A6CFC1974A9388E5C9E1781C7A78CEE2633ABBA16F8388G88850886C83C93571AAFEFA74FD986BD87F0582242D2D4B61FF459F847677179C477CE25175F5F142F9DD7199D6E23E7BC165EB934E7452BC53DE9345E1A3B06DE0F699DD9C4E23F9A
	6AB58174G8481C45C05BAE76F487325B31EE7604AB2F90ABD7C099067C5C6A64F3A92A24F8220116E2ECF4F82A8G283D9B65E95D61538787FBF4FD40649A5BB1BC7B5E5AF12576E5E37F569F34E73D52BEBC12C2E2C0EF6C375D4D5A763B5B7D0CC2D36770E436F4DE294C2C5E3C4E611D0DDAB04CFA10103B61236FF615B9416D1E559B7028176FF6AF053B56E3E35887A7C92C696CEF31798A4A58389B50667EFC0A0C3BD950E69EGDBBBB41663BED8F5B80EA5D3CB49BEDBBF5C07GC4G4482A481E45C83
	71FC8F4672149DFBFC46B2992C6B2C87GD7E335F69EF0FFBEA7ECD1E49D1E8D2BBC6A395B30769E57FACC4722B222E3BB147983CC840882C8GD88210FDAF6A383CE07790F5E42B82CF8D191CFF9B26FE4BEC3E742B3C57359E736B02BD68573AFB4599A00F3606A643A3BEE5883CD72FDE633961716B7228674EEF122E4EEF16224ED3054E9F5907A743279E755CE5D82C2B737CE172FB4DA73F0C7B746A599C245EA6DED6F61FCE1EF11FDE7CBFD8CE623F894A7A810281A2G6682A4812C0940787F3E001E35
	99AC7E4531BEDFFDE0FB1F2F71D55FB63BFD56AB0E506D633B090E86A8EB8770831888908D10GA05D0FBA5ED5307FCAF444B38BBA03991377DD8FF90796832BAE7A0A012C7B75627E196D98778577E35CBF68515650E4F85C278C6D776B553B50BFBC5F0E74D94F6FFE3DB930632217E7BCF44F1D59653133E817107B71994F38A612BF47417DC202FBG673C30CA92ABD9F0DF8CE0GE8878884888308FDGE3656F3EE3C58448D0E2655040706CD667335E0287746AE5DC9ADEDC2C7328679E97CBF57945F854
	9B1AFE4F792C3766C13DFAE3AF8F0F5F2DBE6B656972BBA08D4F1E5FFA54F337E735AE3F77130727DF164FFAE7F4797D49235E50747BAF0FFA6E7A298F6A0D59DB7612FE9888E5E1G51G89GD2A464E4G7BA2319F1EA8A47B2DC39D33FD671F23D306E7571BFC56EB0A542BF7B2F5F87CE41F75FAF479EDC89B9E3FEDBE6B856972BB36F6F8F1F346231EFB5C046972DB1EB5BCFEEBBC6A39730B5665976DD1EFE8761C6333DE3CAE3F2BBF9E1EFE419E755C75CB5265173BEEF87CCABD6AEDF43BDFB9C5EFBE
	FF2B554BFCEE101B714CC67BE6FC4E15B385731C37BD788D4D8FAB567B2A57B0C52F5E13BE6B6973FB56E7BD1BAE3F016C617961DA0FFA6EFE8F5075C35B418D7AFEB859E2371A4DE41FD4427D05D73ABC76977A6B53C3215D48A9383F302F97FD96BB85F330B9E7C88E1676900C69G458F21AF3F5AB0BCDD0FFA54F357557010DE3D370749EF45C6DF7C0CEE7C587BC56B68B916FEA88B82E0383FF55277C261BED658E1D29741A541FD5A43083B0F62487E8DB937457BDAD783EFBF490D5F0F814253296BF141
	E3A86F6E8C951671AB1B4B9BF6FABFD75E38D37FDC3932D37FDCF9674E8F999E5A503E4D06650E1D031FBB3F38D36F5CB9FB9FE19EFDF6E56F362826AE0532E5FDC669A16F97C046F1225B4252914259613E3FACF8CCAD722E6F13C57A7A4EAA525777192241751DDF64CDDF96FF5F50184C811F97BF0C3955E7A55E655BDA22AF5F0E92FD79CACA8617EFCF09B779D8BE587A95F94EE296324DAD59A9F966FD647410550E862FD3E47B7156D63178E2E71D188FE77D7383AA0F432130A7C95532AA7332BF29643D
	960FF788789986765C67D71677B84DC155FA003C2B4C463968B6DCEB86BC0C63DCD7990917E8380F87C892FD59D82EB7972C0CE27DAEBB4A33AFB35DE714FA774BEFCB757D723BD2F2464AE16D3637A86E36FF31B4CB14B9B363432A52C5D4E6B47FC16B2B323DCD71705D9B34BEAB73D67F2DD2EF7EE5766C30EA6CF934549997E55EF52DAE535775C819F7DD3FAD732D6B8FE55EF5154BFC6B5AD1B6382E79B6ED6C10F71DD8BC3C3EC7AF9E8AF89CD46B4483EB6FD5EDFBC74B857E46F3DABE25024F5FCA7578
	B470764DDE799C31EB7914887CEAC7BA4757365A2DDDC70B7818B20A1EC77203760222F01E1BC3F121F09FA1F09BD5020B067B38A86C5F534A3D7BBD26DC5F6F334B3D7B7D59F25FFEFF315C3B5FDFA9776D77B74A87773B314F45CE350598633B8709715DDEE2FC77A0B13EFB88B13EFB10985FBD04985FCDF4E9B699CDF67E068DBF93F0D19BF33BC44C4D5D2E97F38BF82CAD719AF3EFE85BBBDAA8707776A33ED63556968ABE51E7874B2FB39EF1651383776CB97BB2190C51450F38BFFF4F24781AC75C1F3F
	5F54A44E9BC0D974A398378FD4FA7765A3157A3E7CCD25F7DF4E28746D4BE7AA3D7BF2DE25EFDF7E21D25F178383DA9B6F9436BFF0C96B63B201FFC70BBF6A241FF159B56EF7F2FC1F24ED27C850D7A5EB5BF162B726383483A3A7133FE02FF71B6F5A2BEF73BDFB3D5B3CFA2FEF1B9F5D6B5D66F5FBFD5B7C6C5E41470AE8D7DDEB0BB87E1AD4D7FCB14757253962CBB8FE64DA2D0D1D7DE7CF16966F74555F3478233B84FE5A472E3647777D9A573D2F6D1F8562595DE63DFE3869D17EFC8A2E06C7DD7BA2EB2F
	E03D163FB3367EFB3D2BFE259C9F192D55034CFDAC8F7B5802F9D853231807FD5BC37AEC885CC7G4CFC947330033DA8EBA260963B493572B1EFE7F6761FA56DE5BF464606D401B7D0FC1E864F460C39E74479D9A88BFD8C4F901D5F20316181E2C3EE0B0DAE78C24497F610F7D34DF22F35DB059CF6AABD575601EB70B4384F862886E8184A5662ECFDF27F85E243D63B5C8521C613E143B4D2F735996BC681FDAC40A2D28F4A7226210C8DD642139E0F92F2DCFB087401A35F3343E8F403C6427D0320F6E25B73
	F2C7DD2FCD6138BB5A49FB595E5FC970B8884B7BD347D72B34FD0567614CF7535B5C63ACF81A53DFE1533C796E951A13CF1D46FC741E407F056263B5F866BB8745137B45D05670EB4CDDAE52F393D361BED660924BF1AD9FAFF052DE244B7D8D62643DC8D7ACF02BF7A11D3F40D9F7A1DD50EF509FBF97E74C13FEC3E8A269FD22A8FF8D4B0BD879BB743EB89A4BCBD4DEDE5C93CD57FFB487FB977E5A23116F1F3723ACD3854E100F324C943879E7911775B8627E55C4F0150FB3BBB2DF254B84E7841C5F635877
	7EC9F19361FE2A405D5A04FB8DE29F65F17DFD8DBFAF78410071F848030737FD0DE5D758BE4D59298D7C69BEE5824838C85865754DE82BA58237E90060EA1E28CFEFFA82ED305163CC107A04D35700A73C4575F4BA46CDFC42BD0F09237808A75C73186FFBB00F0901325CA7D126B3F43FAE906EA384EE8A4545407DA2014B6CC3FD2498143D66AC092DDAF1FF025E1377D3597D0BE73436A7F4D7EADF5F7EFD4B05473FF34FEA35301D0D3EB3C66CE9869DA64560B8DB58C41EDD093DD3F2F0352262EB3B4938DF
	06D1120C5AEC0E0A22FBAE24FEC28C4ED1FF24ED2E198EE3AFC075F4FC0F23E78B3E67967E2B5B253857745FBDCC2C9BC8B9B31D1F3F1B2EB9277FE5F3AA63D9ECC61ED321FC71F402EBB6EB4F8D93DBB91259DEA6AB8FF6A9A7F9CB1724AD1F61D2CE647DAED5DB3E44251C58E8BB2F4FF4FBFAB36A3674698849BC455F6BF764AAE7B01D1A78B5E74E046400105A65AC9693685ECB8814054F40787C540C7199B783636FCFE5649D7A1C276A534BGEA815A1F423245340C3C5BCE5EC7A76F10137734D9D9EF19
	7B18618EB91A768623AB53A9E7BC467B64015FC3F94F871D5E3845467E7165F88E5C6FA972AD41A907089796BCC577214B2B87E9B319C3AA079828C3609F1CDF7CD3ECFCA1405A0E7F5A59F64FDEDFED278E52F65ECC77365F29F136FD75AEDFEDAF9B246DB2CD5B4CC63369FCE969B61B7937G956D7957020B862F67DF2BE7B25F5671EB603B9312D91B9F2A64BBB796654220AFF565966697EC95F2AC6CCD016B76C26CAB219734F2143DFF156EF7C48878F37EA19AB9185D269DF15A2DA55F175DD68CE237B8
	0D5DD8AE7997719ED28214A581E44E023E88D0BB8BF30F6E26F5F0153BA129A55FFC03B596A4FB9647A132F7445E8DE2F47F780E2C6B68C19EC6C2B3BF3E9E694FF4AD23AF503177A0EAE46DF8E31B453E0D90BA8B5F8D3399754AA305FC37ECF66D9F7F790D53DE529634574CD91EFD986387E3AD81E888203D12E63947596E817A8E37552E34592D5D96A30C74B49FBBC9F30225C03FF2B60EE5A5F4CDD3G77558257E143712DFDB60ED3CF7790DC945C47895CBC0A236F045D083833BD030DB5246ED06642A8
	AFF40C4779C1F98CB53772B6FFB39BBAFA49B7234EABF6D8BB48E67600F23FEC2F351ACDAD3D5C1EE3E86EC27A550259686758DE72EC215DEAB775D9AD2AE7CD7445C2DA171CF15E74F4FDBA0B03AF7AB08E5A1F560F836DF8568FB4C1CF33B8A077131EF60F83FB9709F56743D576FD96EEC32F6FA0F883D64FFF187526DDF1BD16E3C651F751A3B43AB17C6394BFD503E77953A211BF4D04323CD8BC0397E7A0E3C8905C078344GA4GAC85487AAD6E333D7995AC1387BF77D3E7359914730AD9733809FFAB51
	FC257A31317771951827C4BEFA1794F5F53557EF8D647D16450401DF23E2A7A413AB532F6598512F9F4AC2G26824C85D888A04DC17DAA767177D548A32DAAD515DB5A69179F4443C413F24109A60BF996C7EF35F96E8D3CAF3E793BE68E5B9B48036B3EB9ECACE071DCE646F8EE18233F972B0DE7B350784DF146F3709C77F82EDCC5EC83C33AC565EFA3B07C31CCB25E11774871A585BE661EA4B62EB29A453B8BFC1FEA950911EA452C288AABE23EF86634F69E4C3C591C027C5DE58F657A044FF166431F5036
	A5E925647CE3E37D28DAC9787B158C74F7765D3F142A67D727FC6E05C7A2EF3B6099A70F68266277D81B25A71C6DDB482788F99C2C2FA373CC7BB368733D074977B06A658E4F271900A4E75758CB61621B165F51F384D0BFC434B1627036C15A2835DBEDB0CC76B2AC1DB239DC7D6455C63EF7565708EB0428E7F00D9056683EC6986732C6F83151FD0DC06ABB4BB31A5C57882E7577B6392F9130BE1BCB9DFD44766E67D172D5B2CCD6D4E872G2E43E53A2FC0E2B06999BC03F20791F139F108AB5CA276898167
	BDAE26FC6F7C3D003C4E4F63F3C4BB31BB496944BB0AA007551E3B3324A42FA02FF0AB27B3D8BC7BE50B857BE5C89C6B1791F00D09E36F6730FE79B39B764B04B85F73CC925024BD6B6C17D94F3A774B03ED24BFE9CE71947277988F123171885F60E338493D643936565C1AFA797587CF549DBED0E5E8BCF96044F103E1FF5509462A130D478E9E2CBED84D6A3F5BDBBAC87D1A63A78E7FF07CD81DFE8B980B8DFD681FCD4F6A79A769ABBD9C73530A53640E0D793F6EF47EFECA737B4D8EDF6B2D9F83FC5F2B73
	2764417A7D8B866F4FC0928D7A4FC07EE850BF2B3148B078591845866F5FE4FC47207DA6A39B27661FA6FD7B8C782F87E0A2C084C04C33B8E60DBECA46BDF6E25EFD1EBBE8ECA34FE0489727D564EFBE66DDD747B15F5F296EBFC66620E6AB4CA6DD24FD44DFF50C78CB355AA81261C69DA3B6321B5A5AD5C64A71579DA371E6D6DAA91A65B3CBC4BE13883AE5BDA74E3EF562F8DA7B9C6E73D8E8CE94G7721GD10256E446F80FFBCEFFBC729E6F9DE7A5E173357F12042C9B0E13F5276E1CG96BE827D73F8F3
	0752225649ED227E7EC6B26E2C7CDDFD7AA6009A00A6G95A090A094A092E0A6C0A24092000C397569B9G45G55G8DG66396863585A2D5EE4GA45F9C230E364BA662EAAE7BA7F5E45CF470F90F61B23FDB2D0DCFB60D7132355F11D8936FFB53CFE831D91375454B0D434C25E52C6787BA854FE5E3F1983FCE655738394EB11A558BB7E23D85D03684A063F93053730C2E78F9EF7BC025DB34F256G5D999DDAAD30982C32E28C5A00BE6CF90C2B51D60C414467B1869F2634D973E0AD89D0B38FE99F3009B9
	F15E150E399B496714398EE7DD5EFF8A013642678D2E83FF9EFD5AFB7F7E63E95F7D7BFF1D265FCA03FEC99EBE71B16BB409A3EBEB2BC39163485B27B3856E3023566AB011E8E1327FFD18EB270CE13EBBFC700A6B3135552BF49C091E673E667AB345474EF3DFF32962FBE171D0D6B99F7782ACAEEB22D040C74E9F0A2F6A9B3D7B6AEC23EFDF35B66A0F4566C64F3178DC237ED8FC21D1BB9673B13E1144EF9B0CAB8D026EE782F7CA602E26ED925C77A6235AAE7001D4AE022FD188C3440F27F2DD2038867C7E
	0186F70A63A6B612F883155B2DF62CFBDBA34977DBCDE6B33B1FCCE9CC9688BFE10F3B280CA61B2C117BBE01A3F233FD1FDBE8FEBB93FC14B89F7702DE2338A438CF0BC7DCFEB341E511FB013B3705606A605EACF07F2039F18F5C878A5C13C66491AAF0AB946491A1F05FD0DC8C5CA7885C8D0C8E6ED73EA0F834A15FBC01BBCA736F3297D81CB25C890A2BFDC17B6C70EB0F772607562F3E9BE63DB1D7DC0F753BED749BEA66975C7BE3B145773C605E9F2709715A9F4A963E007D71E117F72D8DAF427C7B6260
	7D1159CE6D7334E7EF1F27BD97EAF2603FEAFE777D2439AA63F17DCF4A0369BC526CE907EB4CDABB30B54A83B26967A6B2EC19DACDA4E9E54F4BE74BF48F78C55CABDCAA13BEF8010F91ECDF70A35A3736C3E381026EE0B371A75959BFC186C33BE2E7EB3D334DE22D873491GCB81B2DF02FC78F12CBB3D052CCD5B95996AE95766D54D1210B70F7649AE39078ED7CC0E83C614A3975AE372AFB292794B601E5194507E78A9B4E2F88979B5ABC4F77ABDB07E21D0C65B2760F7B64F885AF0C5DF2F988575B23F44
	747207EB48CB6CF9BE135F58C264E76BF8F98BE90767E7D4877EAD501ED5CEBC8E39BCDE8AED4E782C507CFE527A5347EA42CF4A031F0935F9466A6FEC1E311AA8137962B30B5C25FC0638AF1B49353024000FA7A31B710CD18478E061CB6219F457BEE9F85FADBD2071B94B8FFF5B0C39E4CE8266126FD03E8DF0EF8388C8905FC7B222FC6CBEC5A1F67D0CFEAC68B30E7BDAA1576282249927103649D9B53ECF2C20CE2150EEFC826629452DA8C7664BA8C7F3ABBD2F8977CDGBDAF63F8FAED334F7797DF56FB
	47E7024FFA6E6F35717716FD56F387B6CFD8DB087C61AF3B4F9FBD949F75327B7C115C0273C78C14657E9E75FDD3991E3ED26B7074AD77D9CFDF5FF954DF013FF757778F949F72FBF7FD5BDBD15FC9D036728F75696C1D0D680F5878E1B6D914836CFF1B9232F55371077D3E24793D2265279FBFF27EAF70C8E8F56A741A663725752767BD6AA765417EFF447F2A7F5F6070707F14162952B27AC8C1155248A384D5CA2DB29AD5E9B9FBBE20CA49B98E3B74015809BDCCD36B4473C595297F714B5494A96D30341C
	ADB58ECBE9E43DF0D8DAC617930725D4D8EA745039C57B67B8A7DD12167DDB30E9DAGA9D73B6E97A7CFDEBBA2F3ECD1F70A347210343CD5B65B5AE5100E0E1386A93D190A295801D615B28D246E8E283B03565DED10D2DA9C2A345E09EFA1F8D6FA52A637A8C0721ED136F7529608C899272577DA6DED4D1401AA251CBEF49A5852BBD23C5AA03D2F36131DB77298DC1556BA1B2EA57BB33CE0B7B3593B6FCA49B67A67C55D4131887449B93816CCF0AA57DDFCFF6C3276F94F1211ADDE4FBDE43F4A1F453C6AFC
	964376349EBAC55709008FF8D5FC576994B90B64EDEF6CE417ECB62B3239334AE2A47BE47BFBD545398F7A58A9523E83E9F8A1ABFB6294113BC3D3677FGD0CB8788E067F2C784A4GGC8F4GGD0CB818294G94G88G88G5A0171B4E067F2C784A4GGC8F4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGBEA4GGGG
**end of data**/
}
}
