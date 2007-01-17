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
 * connEtoC12:  (Geometry.this --> GeometrySubVolumePanel.geometry_This(Lcbit.vcell.geometry.Geometry;)V)
 * @param value cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(cbit.vcell.geometry.Geometry value) {
	try {
		// user code begin {1}
		// user code end
		this.geometry_This(getGeometry());
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
private cbit.vcell.geometry.SubVolume findSubVolume() {
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
private void geometry_This(cbit.vcell.geometry.Geometry arg1) {
	cbit.vcell.model.gui.ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
}
/**
 * Comment
 */
private void geometrySubVolumePanel_Initialize() {
	
	getScrollPaneTable().setDefaultRenderer(SubVolume.class,new GeometrySubVolumeTableCellRenderer());
	getScrollPaneTable().setDefaultRenderer(cbit.vcell.parser.ScopedExpression.class,new cbit.vcell.model.gui.ScopedExpressionTableCellRenderer());
	
	getgeometrySubVolumeTableModel().addTableModelListener(
		new javax.swing.event.TableModelListener(){
			public void tableChanged(javax.swing.event.TableModelEvent e){
				cbit.vcell.model.gui.ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
			}
		}
	);

	
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
			connEtoC12(ivjGeometry);
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
	D0CB838494G88G88GD7FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFDFFDCD45715B80918E2C29313504426F657E6CDCBD2B6715BB5295DE53BF657B66EAE1F2C25E4EBB63A6BB6643B264B76EBDAB7EBB7FC1B47G4AC4B4A4A1890D985198030A880A0A0048A0A8A8AA8AAAAAC68666410C4EAF5EBCFEA911BD675EFB5E3C19F943A0E97ED9BE1F631BF76E39777C3C771EFB5FFDCFA9FF532C6C8CD33EA4E5E4CB626FC006A415F8A56945EF3D7A8661AABFCDB6CB3A3F
	4DGEFCBD7AFE64135894AE29FCAB667C9B555B9F05F8977B1F149668F60773B12253A4885BF4270D9651424D31FA6FCE7FCBEFF371D782CE177CF9D4C056BFA00AE4061B561D3647F6C4182010F96F8028C13A44596A34DAF8E968A5C8A204981DC93C0E7B127D7614A8518DAD5A968BA6F0F17D67E1DE2DA076D685AA4C891F5D709EB11745F993336A32F35D53922CE995C5B81D079CC69457A8A385634DE6F2B6D69F6282503BD3253D95AA7FBDC322A0C14768DB82A2A0E093B36016E739E6700CBEE353AE5
	E7618EEC372DAA0735A3C92950E6BC43EDF1A22FAE78BD8CB0FD87619F75A0BE8DFEE76DCCB6976EA47D8EFDDAA3646F3879FDA9E50FAB6B2DB07AD56C64FAF56E746B1766DCEA7ABD73451BBB5166B320AC91E09140CA007C12E4F38540869683EF6F5881D7CB37FA4463F2F95C962F375EE9D3CEFAEC3293FED7D5C109401D15FBBC0ACD12381F5F2EAAB110E7BC6075B65DF6BDEE13783D64730433CFCACB7E6CA1539A831BAC093FE9EAA96136301578ED42F9F704659DDD929813013CAFC8FA5EA5AFBC1F19
	ED403B69A74FE74E906DA468F82FACC1DFFF1B45C0927C46FA68CF0EFF0661ABF77971BCBE0B999E6F3B20EC49AE0A0D0E75A4CBCB5623D2DE794306F618715731260ED2AE0337B4D816337D58764CD24E532249F213610BF77B71DC16F89627F8BF974A6617A71BF9FC3D3B8B7BEB525EE473F2G29A259DCGD089508EE02F20983B34E37784E32C552A2875838E27CDD6A4DE775D1D6F43D59ED695EB335B27DA5DBD72F92B6230F6BBE51FA4E88AF7228D3A836AEDE6315F83BC0E49EED9312A8E0F9BDA37C2
	4715959F27593173ED24D1E4D65A6EF049404085432BE8E34F4EF7616A347A54F3DE1BD5157D943C3CEAA77AE450FA9EDA048660B737CBDDA95AAB86741F85B0C73343532C3D93328223C5D5D50B47F3FD405B0E1AC852BE3433070D9D8B00FEC9855938E6B7629A203CF39F351336739DDDBB8D0E9E945F2A0CD09FCBF022DF3CC09F87908F1084309CF96CCFB6176D27D87AD58745D2433ABF15CA1E7F1AE1ACA5C2DCB76D67B15435BF38CFA5B2DEA8EF8C144583AC84D88E10F3A059DCFC006605D227D73407
	F2C56A734BF5B4C9C20F327D116BA1448BBD7DE34EC90DF766846A2F8D6A7F59229FCEF40CDBA7FEF37FBDD106315084F63081C49FA09FFEDAD2447BC43D55A7D3AFB82DC0G4BB63FC74538559F85573DG1BG76819C84B8404A30ADC9EA8418A6FC379360B0C0B5C0B940B1000BGD781BE11783C7DB140C9GAB40G4027G5B81F6C93C1FE3FFBD845086F08620CF33659DCC161920478200B4007C4AE4F38D008DA096E0AE4092002C0349669200F600D10059GC9GAB818A2B124D8DGCE00B800C400D4
	005CEA181BGBA81228192GD2G52813656A41BDBG549A0A29A7BD1249F8DE5483594828EC134E9E1F686C30D358E2373061DEE1778342765542BE07053D568B9F9C5335FDD2DC4F88BB927E226E77D561833A6F9BC70F018946603489527DEF819E8B65DE0C0577A250E25F8C571733F57E7FC467778FA4FF1F5BA871B184F962B801638376E70C876C6F87A4FF4CED52353DD3DCB1DE8E69701BF53FB186F7696EADD2F8BDF6CF50B661627EFFAB7031F563AE7431BCA83B558659AB3BED7043A74A366EC21F
	09FC22572355594170DE48BF0E40046F5370E53B5017D6D5D59C5D832A5CA62B2A435D67AF2FE43CFC835DDEFF6AC23C8E6F4218728CC17E2151B76C42393B4763F64BEC10572A087223ACF7101D328B04BE6299F02B9C5FD20A6B2C6B720848DDDACBD7CB18FBB887B08F62EB17B9D60CC92F62F0415401291414D603F945DDD585BA9B4D849C2E416643B52506F201210FA99758FA63F1E97ED52F49A8471D7158FD260AC3FCEEEDBA644F8538CC165D46329FE3B90C407B382C779F403C4EF8A5457B583BD26C
	769C9D8D477DFABD6AE2F1DB1DA32A23C72BF7545A23FA08266EA47389B0E4A6E609E639E87341BC1429AF18AFCB439821209B390AFEEFE3ED48B60DCEA259C65C39FA793DF28F1F6FDFAAC73C9DECB38A90F708667B3F590D65143309097E0847A9E40432150750D7592EFCE16B8438DFF0087B8A6F23EAD90E2E15A3545472F2A7DCF97D1735720500DBD69BDC7FEE373E7E8ADDB92F7F0CD6DEF9B8595CFAB8383C44C365D1D0B6C3D74E751CD31647EC5FBFA0C32A0ED6BD21456C774B5E89AABBAF6217177F
	D599462FD31797F755A14F1F76533EC09270CB85482A63FC9B6AC4ACDA106EEB8A51A554F13A85F5BC874D32087C1535F751C7F4F100CF3004EBEF1D2A7195F454DEEAC0FBBF52680A6B134DF575615A9B98A03AAEC1B7DC4F5B1BD74F69E69DC13A0303C417DE4F732A7CA3BC375E8A57D8000A8624B38F93DD5A911EFBE5B570767261DA2A497722C6978578998D61643BEF046866893AC5223D3CC6CEB73F9169DE985374E864397EB0DCE3G92GBE526862A5224BBD4A4B379E656B03E1C14765F3E790DD5D
	D13E0E68B8EAA45FB6935159855D30609FF51453F13B3C2A51459D65F9E7020E5EEF1747B209EE3E10EB31A1DFE79651EDBD4669EA0E7175CD4CB1CE17F68C691A3309EE46B11E63A60876669C534B776F9ADDE5934F13DB1B7C6D15EAF4CF2F566C5C44736DE1C33A31B5C49753447371D9C1F4DC5F669C221BA34A97B6F17996B76963B943CCF42B1A047F1B836938DD7EC223ABEE7653D13BFE3BAC5A20793719677741F6616DA905C457A15A7342B596A021D9CFF7E4A351A5B6F33F26887A14E63D1D57FDC4
	F4294DFCADC1F1105E2C374B339ADD7EF1D833G349E8F0C83CEB7ED13E6E7D1BECBDC679D57539DA9A23A654739DC5289A3BB3F2E51651D60F2958B3A4A931C0E4F153F590EF39E5B133CE69D348E177A06A087292ABA2E5BF7DC41768E10DEBDC17B0B0535B897A5407D8200A5A7E82E7F69BA5ADF0C3B9EAF657CD3CAE6387D5315C20E02967FDCBF772121CCBE576D5A0BBCBB204C5EC273DCD539FE0F436273F9FA9CECFBC50C712F146BF3810E7BD5791BF06DF5A8BE35991235E11A8FD616233C387FA250
	E2BF2A4415AB71F9F894784EED213D5BA73236C86177EE7D7B36342F5733176CB07D46E3D257579EB12D8DE307B421FF6EC93F9D227377E5F23E05BB7CFCBD2ED2C7372BF4E050D5EA6DD6CBE51BC32D2A925909A4F1DFFD4FC5FB448DA749A71F9F557BA4712D4F434AB2AAE4182913A56D6957B239EDF25DE41BA5D01EFF8A564C27D85FF7E37E0863AF1739787241F1EC95E61FDB17A511AF16EA7C26831F0527287DFDECBD935E977E0C0A375366217DF869B4447FE9EA6701FD5BA664D34C40C8267FE67B18
	D85F8B6D4483705C674A95FD6E338C70B92D505F81F8CEE7D57640554A9232D2FEE91515DE0F6212ED455873CA2DC360D2DCD714DAD8F1A37E5ECE7DB4679276239E5C23F4B602630B371F17A8569EFD37CE70CDFA72D129E66CB313511EFE4CDBF6D3DDAB772B3D556F5FE5BFDBEADA8940757B21C27A4D851AB4CD7ECFFCDFCCFEE975FE38F6ABB0389C75A8C3D645D65CE2949CA159F7B5AA4A757C530F74FA66D8279A6A997BCA1FE96B992EDF4B993F1ECDA9CBCD1DA924671DBE52B38EE8124E109FDFD00C
	7C18F59646E3D6EF3FCA758AG57FA16E269EF6AD1BF9FAC886B2DBD57EFD7BF5E76AF3536E3205D79E7C926C3C3C6B22D52E47A23812A175B8673CA9B79ACFB700B796C21CC5449EA73AFD1EEDF276EC112ED94641A2B49763703B8E6D8153EF24E6B75CB38F79552D2B163676BF716C22A4FB27A12A2A639536A6EABEDD3B11ECA0AF4C241D259A7AB250D43DEC576614EF9C9110329A4464774C19C9FF10FA52D0D46C7351262AAA179E929635A3D06F145F234F691D76D7E384ADB716C1A2DGDC5FBD43B4E6
	4DG1AC56D246F5707053EFB787D0F06F18D582B4832266BA9ABDB83F219C6AF12CCD1C9F32502F73F9AD6261CF3DC1632F3FE1972FF3154D47C8B0A752C9172C194504CBBC7F1B53F4FA82ED61C23382A9D23FAD967A1AFBDCF3ABCB67645626AA7EF617E13A23BBC03F22847EFA344C40C19A432398AB2A6E8F2BAA43439EAF77878DE458517FE2FA275BC4F47F2AEC0BEFD016A5C4FE4F409F9087B6257967D38B3EFCD2C21AF6C5F19E12A38A01E19DE707BA2897C30CC7345CFB5D99300A6D563FB553DDE8A
	5ED7A07D9E4EA0BF94F6A41B5BBB284E6FB2C8BFCEF75AC4ED47824D820D6E1FCC9873E4CBCE7BE1A6512683DD45C59A4772EB714A76FEB47BBE17C97C1DC0B77BA2353BAF8B6D62F47854B6123B1455BCE2075EA9E2C1E414A29CDA7444BCAAB80F2FE5E93985341FFF09F8247FC1F8703676E6875ACB49A63DDA00DF74A53241AB9FE37E5E6BF06B863E6D06E1698FC70A1557B7D22CECED18ADD52E3C9E365FA6DE62B112FA499FABD93377E596GF07972D713CDAAAEA71B1D1749A6B7D7535849697ED9231B
	8DB4CBB43A46B501F471EB08AE3F93F237CE22FBABA710CE49A13BC483CD22C677C28E590F5B6960605B2C9F43A86633578F282A476DAB56754B9DE6EAE7A534D1F2C56BE3E64C49FDE4BB9F51271AC9BE9B504E3CC26350EFB7D0BBA9004B7D0C705FA8A4FC8360C6BF2376CD9B511EB0873BA1A8CE42B4E06D13E90F6A1B8E7D18B777B37EBCF431D6F757C62623BC2CF29E4E0DA4D39A50EC3DCAF4BF5B9898CB7BBFA23ACE200953681EE278BEDDA64969D7FCC432A781EDD69764DAEC0C0A39259F23EAGEF
	8708851855C5EDDE580467AAFABDBD833ED276EF0B4727868F3CC7310046DD4E73FB1B0867BCE8EB05565E6F0B906FF0BBD40755693881414E7D3B4133D5B2584FE4AB432B4DBA727342D6C554FE31B5595CE555ECD594E82B2D1B09EE8650AC52686E599C98DFAF1FA955713F5A8E85463383A56AECD2604FEC7EB364BCC6CE377F99EF216E37369EE567942633F5EDD9B7BF27102D613F41708DBABC57E5CBAD695281E5095D74EC7DD77B510FC5BDB0EF823483B881E28192FAE8BDBB3A871F810DFBF621592D
	42F2576AEC8BF888C0E78876225D2E4BA362FE7FDE7D584D6D304705F6D858B331675B6B35FAF52C5E325B284775EE2B447E15E7037E8E50G5089B0EAA31D7FE2EFC9A41D698C06D0164B73FDD73873B5B3ED46B2F2F9FEF090651983B4C9G29G69G05B24913DD9151870D4306BE60F23D631E1CFD0B5C37EBDFBE3FEC3F24BF1F167576F1934600517CD2A30BDCD58EBED77152A4E5FE7536E56675AEFAA6F71E2BEF1275A4A538DE2C9CA8F3FC50BD2F37BE24DEE950FDD2D0BDBECE3D550631E630074667
	C4A863E3FB279A7C2C8C4776D4E8F395GDF1F44DD213D363CDE9ACBAED763DEDE935C5BGC681E6GA482AC86C86F25D8EE2FB894B116F1C2E8779C81DC1347F37DC87033C24D968BA6E9436408B6AC6C8B2C47F5CC2EC19DAB202C95408E908D908F30GE0F99F6978D8C57584F564AB0FD08D391CB13E496977D0C83DE07DF26C017538DF73FC6457A2BB1DB129D1A7A7437110FA41B2F4580D6A2D18A43F5FC56467B554D99DA21DE3B41DE78FCCCE066F05548B16E13E214E764149719B8C29974CEF31A13F
	0CA12C37C214E5B9C2474A8207D17C1F2D4578AF0132A600AE00D100D9G09G4B9C947F2FD530F3C163453FF694B1D29F289C0EB43E9A5B66E844FA52B543BEFE98F5AC00328A0096GBBC0ACC0BCC052B55231F9DF7D6D68C867A78C86B3AE6F1BA3130B010210FA41B110FA4DA86E7F2B0F62BE7D9A45FD435864E4B89BD2AFD8060A6BC675F2244979767D0875DA2E9B4D0157EE06F90E44767539DD6EF311DDEC5769B9126CE5670561FEAEC052F51A730ED9B0D6D261DEF2A61B8BGAAGBAGD40099CE0A
	15EFC60E95ADC0A692ABBF1F245DDE0ED8EF0E53285EB7B3A697975F8D29979C978B8D791DB3CDCE3F1E0875169972AB4A1C9C3F520875D69972CB8929B7B1FB7EA224DE30BD73DCC6753E11B5B97D9E0FD82F58105FF07664743BAB24DE30FEF5AE23B17B4775588FBB20CC8508850887C882D88E106B26FE786ABE5C531D68189DB97F7CF075646CDA9C31DE095B28DE6AA4797DBAE23D86C3FECF2E199C3F440875BA8D798D67CCB2EEC26A85470DEA482F4CBCB9FE35A1750279459A727BE5C83D0959F3D544
	FA338D793D31E1F27A2DAB0C24DF22A13FEF05541B983F27C26AFD94F41F9244CF1C8B698DB31F37D83B69DCC80AC3FB166626BCC759B8B9BFC4C52C3755E3D42F66234971BB9E31DE0DA13F57C26ACD4C8FD91BA2793D5DE364073E460D46FEE86BD1BCCEA76E45CA343F70335A607D855C5BF6C23BD19E5ADF18FF0BFCB643233DAB485E87DB84774B81A4AF79F2DE084C935375A791F5AD709A55D30AA647AFAA24DEB03FB2C3FEBBB6CF0EDFE5C83D60BE5594440F3F03154B5E136D02B2950063DE29C15C8C
	381F23597D21DA4425407DF28D77AD06E34F821A89D7DC5BA15ACF896277C5E039E80B7330E8BCA672FE53C4A1DBDCF96E70DF7B431F297F5DFE63B375D9870C4F543FF960ED0E07B674EF7CD179BB8746BFF37F618123B3777C5D0CFFE44FD71581376AF0490CAD6F2372B03EEB81994E5981B7CFFF1033AFF8BFDB63F139B23C3E5D15467A5AAB0D75F5D70E2F6FE0E5B8FDF97CBD4756E4D97D30466A27DC6EC787434B37702031FC4F9DB416AF65607872ADB998CEBE1EFFE6B760B321D6CDB6DF559EA9B44F
	4493D19E058C5EAEDB15533D3D5AE20F1F5F62BE7C5B775FE27278FCB2FFBA56E0D52D226C5FD4FC47A7647D89F1AE03BF23BA201885CD236AB9026FF30BFDDB1556364EFEEDDC6D44F8198D7709GA97D54EF3FFE45E86EC96B677DCED2C27BB257DD2E8E6F97E73531DF14EABCB766738CA8BDF2306DC72A8B34B2FFA65E2C4AAED419535C496A2BD625CF8E715DB4D61F17052B7FC0F5B87FF2FB3E2E3767497D7E3828892F6BCBB5463A3ED293DE5795B511F57DEDCDF8DD25C311F5B59F9ADF573F7269E387
	5F7B627170DE481EB2C23E0803E203F8606D79D43D6D8EEA78CF8774FC76EBFCC6060C78540876DB4372F9D15F5E49FD9A7E0F87578AFCEB2F62F11D5CAB461482366FEB0376D40566392B8C978D77F19A6EA9061B8D77F3956A5F1D35617D5ED3EB6CF7C7EDF83FFBEBA37BFD24B63C5F23EAA37BFDDA6D78FE778C875829351CE27C70B8B1FEB8CC0C9F9EA7468FCFA0468F0F9363F5930871BA54255BE1F3A8626DA22E23E5C49FF3873418CB6FB30A3905A25616040D391BE37A18AB577007A542372A1E56F2
	0DCFDC58674388AB0278E479124D7C397E8BEC7F3140977C3CBF1561CBFC414F7B2FFB69F9FF9D144D76D15C1E310477E5074558171FD9427BD236C476255B925E178316483E3CE5B176657A8C3D0D77E836CF49507B38D243471A743E7253DF488C0C7BBD823FB6CB5F4EDE0DBED90FBF6947BF159D508E0C1CDC7EFF288FEF73FF2CB73679127A70B6FF29BE324D536A435B7CB77511ED1EDEBF7ED8F1E6F5204D768A7C06B501788A01FFB9883F0FEC162337313F7F741975F83F2FB63F29377D818D3F3FB050
	76742E636242B7757D330C7A618B8E23FE1823727ED78857B2B530AF72769E5F28676F0FAD5FC7017A6D9778DD1B74FA605C4773303507A98FEBD0A98F7B696758E7C761BE8EA0C125BC6C4737C8568500DB9CA4D71A9ABAB6713161FD760E08B4404706CC8DDF4470F9BA3CB8F7672433E1C5D096BDC0671D7E5FA61D8D0F208D05AD0A8270650437EE8E406FA17C4755783E2E53BA6299D0A529FD90F9D6D553DE40F260A7812E9820E6106F8970F54B7F99C25B76AAD69704A0CB124B0630AE4FF5306ECC201F
	833088E08536BBC432F7F810A7BB8A2649F74FD16C9B47AF7003F7EC23C822FD0C69F634792075DA7B08D796386F58715D75706FDF049C7A957D4C5C30CE5F07C4FEAEF26B10E75543C3FEBF468E0575699574512CA16EBB33067F186193F5F861D3165B637DE2A82B99261CE68EBB3FB18B6E67E838973BE80D1F2861CAFCC417BBC2382F7A082EC043CD9FA2BA3B06FBF50868549172476EA1BA4319B202B40D6CFE01D6BEC865FBF9F9AC3BAF38C165BF21720A4E9BD83E0471F8837EDAEE905FF76DA44BAC8D
	275AC916848D77DD976152B55C9EAF620AEEF2BBF2DF7D8B43B5814EF6137A64AF99AE8A6EE7E938FEAF6DC1E87BABB70D77BBECE170638167F1F7880FF07B9D593759BEDB53B19B2CE07B2573C146C5B749AE672E112D16E8385F306747A523496606D132C15E6160386E98756B6A9C8D97570D37B0CE23C603731BD6060F9B8D4EEFFE7EB965B771D0167BB94934046D9BFA61BECE436D9BC3DCBC5CAF52F05347C81F544FC976C29746D645AD3A5F466E711DDD71AC442937BD525D2EFDA37B7759G9EFF483D
	2C751A6D3A2B51F6E84FD650A166960D3365DD788CCD5B4345C33AD5D54794072D5E8A23A40E5AFC6EFA026D8FE27D7937E86EDA4C5ADC8577F9E3B0060F51BBAB435B681D3E04EFBFAA25BFEF7C1E6532764FCDF5E35C1E36B17FB9C049355444F9EEBBC4BCE7C17962B144359F521F11C6DB79B2781EAAAF3FAB209C73997C541F3FFCDEC0B94AFBAED3DF7E4B00F23451AED11F6B76771F10EE69FFBFC7F2BE9D9D769BFCC5D1DC2786F11D1E7A758C6F7F3D11A946E236A7B38AE55323A8BEF78FD2FC4E0D52
	62738AFED7A06B0EE4F3A1C085C04B9DD4361D15617B7E780EBE3ED70F6F2E0B386D8C9EB302A1CB575EF8F4058665A2EEA37220EF4618A350990DDBFC6CFF768A1DF9375D015FE1ECBF01DED8F8875A6B57DD8D633419A1A0D3C0BC54C1F8CF70CB3C030FAF883C6D2196FF5B33FD115A4E9C276D3CBB035BFE64243F6D1C61C8EDE70F53F6212EEDEE234BECFEF48FB81D62BB0A327E9CEE594D163067F00B6F643E2D945774779E4960EDBEBA045F82F24BC3633E40D6DCDEDA36DD1363B7C3A6386EAA273EB2
	EBC8AFC7651BF79AFEDB8541AE780F6A6460F66BBC6D375BBD76C8F6DBB30E5D666A6C46F3493F565E391A8FE5A9GAB817227C03F18C239C73377C338DA87A029456F65415A8B12BD376FA86EA9717720B85DFB67F03D478E94F192167971323F9C4D8BACE3AF8B72F7BE8A7B7563CD7B947E3D0868A9749E5C824528FC06A65F1AEB017DA33655EFAF67B532D7421450BECC71C331B69FE89048DEA9D302636CCDBBFBDF5D2348FD0AE740ED836F30FCEC8B4B331681FD5A949A4BBEE5EB1D7CE81897228957E9
	21712DA51A462993EC6E1E8977F3B55CF906E36F3F3592EE6967630DB5D8F7A2F3614CB0F462796938BCA61ADB051B3FC59F39055F559A1495D8BBD81D7CC1E93DD5E975B89DBDA3421E6BD91E027DEAE1B47939E58C1FB958BD0A6306472D0656A4DF58D9DDBCEB1DB3B5594C6360BBE39487ADD30D63C0BF1EF5810DBA1547815E47CC8D0E834505316EFF484B3FD9A34E34D6CF6EDC7C60A46BBDF55B75F80E79C48D4BB1F53AF17C1FB17CAC9DDE3C472665CF89D016F7971D85741CE17BAAF0BF9DA09EE0BE
	40B200743BE87F6D7E86D8BE0FFF7E285D63ED1187E5276EB19457EF67215B550F0F3DBFDAC3F94A5CBFFFC21AF7D7C3586FAA64FD0547C41938963F74758C3CF27DCCED28DF97140D824C8218871084107A95526F0586715E8CBE6A32282A35474E3EF221BDE4F448C3FEB4AE66455C2E066E8D7C33769E422A2F703D013C18E4735698BE9670F8BEBAC871DC93E33CC72B0F67D62031457863F9B8A6B81EEB5621EDE0C8F72B622D880EBF1507639D3EB3CFAFCBF07CB75A5046961BCDFB0702638F2DE36FE149
	CED915F9917745BFDC564F03F90FB8CD44BFD876E8214F7498FFBE3C09358D7DD9727F7131FE5BB9C973773F66123F8B3F7BA7D2434F9BCC9F076131C0341DBF4D4FA35DD314495BFC7E3C3F7DF5B89C0AB878738E1CE7DA26114F171E44EFFF1C37DE8BFD4A89C8BCC347DF00573E87FA111DAF007A23DA9BDBCE6D9C270DD6456305E1F204E35914A9645AF0996562EB04D39EDAA34C1CC6EB04D1CF709A617E00B542131E60B582567717AF73842F91826B3F638DDEA3D0FDBE177AEE206D034F45142AB8CCD6
	D56972G21C3DBFF0E6013B20D4E423C2E90AE77EE42FD731A36FF883870F1515E697FBD65B3C90B4F55CE34BB66F45A7B18A007C7A95E33EFDFC9D9C979F6C1D7FBB834DF1EBBCC7DF274EE5EAF63609AFFB7FFCF0877CB1F057A657C3BA34FB3A9C0337CEEFF3FCC3FBB38DFB676E1FF521DA6AA6778CB0DB8B69E979BFC9C774998BE6F561BDBD72F74FC63597666A316162BEDC74E1EEEE929371C3DEAE93BFA2A3131213101579F9B5BBFCE7D2653E71BAF1DBE55EE5C82456262B172CF4EBDC67ED9F15888
	477D74ED7CA42A1853665478FF7FC077BB35BA52FA6B0BG67717F3FD49E3C5F8F1F897FECE46C0C71331129E70C4FF05CF3E67CB3B38F1E897F1D4AC74E683FD349472941AE6C5BF5603FCE00A800B80078FBE84CAABE0B639EBF399FBC4FB55A7A70598CFE2DDBA57EEB5B426B7ACEDB64EFFCEFE843B9285B8333098B5BA77C66B674176A71B2A447EDE373126268332B1CD460CB5AB05E1CF2AFC373FCE675G65B38BC03774FBE80CB9B5C063E9C5AC6D730CB2DAA75CC7834C0CA55A21C10A7739314663D1
	78F8B7F7CB1A4D1FE13F392C09ED386EB41C9340424721FF1E6E3EA677286D56BE2D7E3F7660381386B264FCB559DC8250G508160850886188110G30GE089408A002CFB134D85G45GB5G2D77120F2F1F5B9ECE86C00A4DB166E8456AC0D78B59FF58016326CF4CFB9C775445757A78645318A8FB66A2461A76EEBB7BDC981FB5F9DFEC1B64BB269D7DD44F86BA8D5F9BB896470A7BD9BA3CF8B7D5BB6BB1974A9682AC51512C38B75CBE50235774F2E65D87F63DEF7C75980F413DF59403A5C0EF3B0F626A
	E61DB6275FC7B178B823CD017BB4002C69C4BB5BC2B1D8B87DF6475C3F44AEA2F4D89A70AED3BB34E51FBE3E8E62B9F5D7787EBD35ABF27F3E17BDFF1121DF6243A7B1E6F5E19CF9FAFBFD32B60E4C684A53F04D3ED60F4F015142E54F8DF97FF0E2F17261A46B355DF6BD3E36DA414611516941EB2E5FB2FC4C7460B5571D5A989587E52B2653DE40E8407B0F1D77C3BF3FFFA23E7A4F1E703ECA6F096C2BEC1B71D83C5696BA9667590C476277ED7A31D80C71B60C5FBE98D7BAB43AA28DF7D143FD425AC45C85
	07CD35EB781DCCAE44B74948107065CC2EA1066B206FB068F097852E5A067186AA5BBD8A552D33E13E5F6BF0BA79FD8323F138A17CB4FBB4B3999DDE2BCE6ED39A8E65967F3F834BEF2341C7B36F273D209FB0DC825C4F57F0A616732640FD1A06F3B05CAA384FFD00F09F78283DA28DF759C76D15E938BFE63886386F54F02F780807D7437D006555238F707863389FB2DC5C837AE702876BA657DF0ECD32DE54ED57637DE923857B4D1C87027B59B606CFFCA0381FBD250D438B212C64C16AE70FDB747C2783FE
	76036377B3EE3B15E321767C6DD828BDDF43CF7B53BBAB3A5FB183DFFE1E7B2DAF158757794141D0BB4C98545B012FBD6E6A477E6B406148516B40E414BF877F23FE6C03739E24BD40C57D583706C45F677BFD7F467A51AEE8ACC9237B3D027B99F6598A392EFE5D3C2CD752F8C631FE65328E332104377530C2EDAD0136388CF5BE14A18D6EB9CDB46BBF1B3111B862770C8F65E75FA5939FAC65344F7968FB1FF9C04BE548C45145BA593A8D63CE64BFCC8E71DD5161F5FEBC8DE942EF9FDB7CF1D2227B7DF45D
	179FB34FFD29BCF8FC4C3704464C0FAD21B1B3379F4763ADEE2BCB5EC2385F756335FCDF1938FFD2893CBF6FC3BD36300F70EC213DAC9FDEAB4A0866E5FC1C4A7E8F887EFF254D3A709AA0927EC34AD625EC36C526CAD938A526CA19961BCD15D6737DAED54AA872A952DB5A4EC2B31BAA168D6E2D4AFC69CFB2CDD2D633341ACF1D4DD2964EFF4DD2B61B9E1B25CC18BA07713F2A89787375CB37246CBF88B6CB8FA0657A407D0F0C47BE1812F75F5E811314F3D4DA5DEBF5FA6DD6100E5929C5DA5B4D44149520
	D5253C962C3B9B6A6EE6F52BDBA4D30FCF15B67871BD086725EDDEEB0F8CA4E61BD5394EDAC011F2AFCB66DE252F1BB1D0A55365231701AD3B436275AD521B2A9DD71278D8C715BE70B75D0A6B8DD1D04DCD76469BD206177D05D1F7FCAC81FB92842E4500D2056E5AF7FD46747B172B267604FD0E3768A1BEBF24BE645FDB64EB34731758991B07E12D75B02D333EFD911F2D07DB6B35392CCE27EAF5DE3738ED386E2B9FD1E57F3A7E72A5EC5FC7B422D03C27F289653E262B73BFD0CB8788695E646972A2GG
	D4F0GGD0CB818294G94G88G88GD7FBB0B6695E646972A2GGD4F0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGACA3GGGG
**end of data**/
}
}
