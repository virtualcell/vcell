package cbit.vcell.geometry.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.*;
import java.awt.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.beans.*;

import cbit.gui.DialogUtils;
import cbit.image.ImagePlaneManager;
import cbit.image.ImagePlaneManagerPanel;
import cbit.util.BeanUtils;
import cbit.vcell.geometry.*;
import cbit.vcell.model.gui.ScopedExpressionTableCellRenderer;
/**
 * This type was created in VisualAge.
 */
public class GeometrySubVolumePanel extends javax.swing.JPanel {
	private javax.swing.JButton ivjBackButton = null;
	private javax.swing.JButton ivjDeleteButton = null;
	private javax.swing.JButton ivjFrontButton = null;
	private Geometry ivjGeometry = null;
	private javax.swing.JPanel ivjPanel1 = null;
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
	private final JButton addShapeButton = new JButton();

	private AddShapeJPanel addShapeJPanel = null;


class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GeometrySubVolumePanel.this.getFrontButton()) 
				connEtoC4(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getFrontButton()) 
				connEtoM6(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getFrontButton()) 
				ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(), null, null);
			if (e.getSource() == GeometrySubVolumePanel.this.getBackButton()) 
				connEtoC5(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getBackButton()) 
				connEtoM9(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getBackButton()) 
				ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(), null, null);
			if (e.getSource() == GeometrySubVolumePanel.this.getDeleteButton()) 
				connEtoC7(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getDeleteButton()) 
				connEtoM1(e);
			if (e.getSource() == GeometrySubVolumePanel.this.getDeleteButton()) 
				ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(), null, null);
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
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,0,0,7};
			ivjJPanel1.setLayout(gridBagLayout);

			java.awt.GridBagConstraints constraintsFrontButton = new java.awt.GridBagConstraints();
			constraintsFrontButton.gridx = 0; constraintsFrontButton.gridy = 0;
			constraintsFrontButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			getJPanel1().add(getFrontButton(), constraintsFrontButton);

			java.awt.GridBagConstraints constraintsBackButton = new java.awt.GridBagConstraints();
			constraintsBackButton.gridx = 0; constraintsBackButton.gridy = 1;
			constraintsBackButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			getJPanel1().add(getBackButton(), constraintsBackButton);

			java.awt.GridBagConstraints constraintsDeleteButton = new java.awt.GridBagConstraints();
			constraintsDeleteButton.gridx = 0; constraintsDeleteButton.gridy = 3;
			constraintsDeleteButton.fill = java.awt.GridBagConstraints.HORIZONTAL;

			addShapeButton.setText("Add Structure...");
			addShapeButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(addShapeJPanel == null){
						addShapeJPanel = new AddShapeJPanel();
						addShapeJPanel.setBorder(new LineBorder(Color.black,1));
						addShapeJPanel.setDefaultCenter(
								getGeometry().getOrigin().getX()+getGeometry().getExtent().getX()/2,
								(getGeometry().getDimension() > 1?getGeometry().getOrigin().getY()+getGeometry().getExtent().getY()/2:null),
								(getGeometry().getDimension() > 2?getGeometry().getOrigin().getZ()+getGeometry().getExtent().getZ()/2:null));
						addShapeJPanel.setDimension(getGeometry().getDimension());
					}
					while(true){
						try {
							final boolean[] acceptFlag = new boolean[] {false};
							final JDialog d = new JDialog();
							d.setTitle("Define New Geometry Structure");
							
							JPanel main = new JPanel();
							BoxLayout mainBoxLayout = new BoxLayout(main,BoxLayout.Y_AXIS);
							main.setLayout(mainBoxLayout);
							
							JPanel addCancelJPanel = new JPanel();
							addCancelJPanel.setBorder(new EmptyBorder(10,10,10,10));
							BoxLayout addCancelBoxLayout = new BoxLayout(addCancelJPanel,BoxLayout.X_AXIS);
							addCancelJPanel.setLayout(addCancelBoxLayout);
							final JButton addJButton = new JButton("Add New Structure");
							addJButton.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent e) {
									d.dispose();
									acceptFlag[0] = true;
								}
							});
							addCancelJPanel.add(addJButton);
							JButton cancelJButton = new JButton("Cancel");
							cancelJButton.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent e) {
									d.dispose();
								}
							});
							addCancelJPanel.add(cancelJButton);
							
							main.add(addShapeJPanel);
							main.add(addCancelJPanel);
							
							addShapeJPanel.addPropertyChangeListener(new PropertyChangeListener(){
								public void propertyChange(PropertyChangeEvent evt) {
									if(evt.getPropertyName().equals(AddShapeJPanel.PROPCHANGE_VALID_ANALYTIC)){
										addJButton.setEnabled(((Boolean)evt.getNewValue()));
									}
								}
							});
							d.setModal(true);
							d.getContentPane().add(main);
							d.setSize(300,275);
							d.setLocation(300,200);
//							BeanUtils.centerOnComponent(GeometrySubVolumePanel.this,null);
							cbit.gui.ZEnforcer.showModalDialogOnTop(d,null);

							if(acceptFlag[0]){
								AddShapeJPanel.addSubVolumeToGeometrySpec(addShapeJPanel,getGeometrySpec());
								ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(), null, null);
							}
							break;
						} catch (Exception e1) {
							e1.printStackTrace();
							DialogUtils.showErrorDialog("Error adding shape:\n"+e1.getMessage());
						}
					}
				}
			});
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 0;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.gridx = 0;
			ivjJPanel1.add(addShapeButton, gridBagConstraints);
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
//			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
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
	addShapeButton.setEnabled(true);
	SubVolume selectedSubVolume = getSelectedSubVolume();
	if (getGeometry()==null || getGeometry().getDimension()==0 || getGeometrySpec() == null){
		getFrontButton().setEnabled(false);
		getBackButton().setEnabled(false);
		getDeleteButton().setEnabled(false);
		addShapeButton.setEnabled(false);
	}else if (selectedSubVolume == null){
		getFrontButton().setEnabled(false);
		getBackButton().setEnabled(false);
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
	}
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
}
