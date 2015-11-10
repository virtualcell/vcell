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

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.client.logicalwindow.LWTitledDialog;
import org.vcell.documentation.VcellHelpViewer;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.UtilCancelException;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.ImageSubVolume;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class GeometrySubVolumePanel extends DocumentEditorSubPanel {
	private javax.swing.JButton ivjBackButton = null;
	private javax.swing.JButton ivjDeleteButton = null;
	private javax.swing.JButton ivjFrontButton = null;
	private Geometry ivjGeometry = null;
	private GeometrySubVolumeTableModel ivjgeometrySubVolumeTableModel = null;
	private javax.swing.JPanel buttonPanel = null;
	private ScrollTable ivjScrollPaneTable = null;
	private SubVolume ivjSelectedSubVolume = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private GeometrySpec ivjGeometrySpec = null;
	private JButton addShapeButton = null;

	//private AddShapeJPanel addShapeJPanel = null;
	private JPopupMenu addSubVolumePopupMenu;
	private JMenuItem addAnalyticSubVolumeMenuItem;
	private JMenuItem addCSGSubVolumeMenuItem;


class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GeometrySubVolumePanel.this.getFrontButton()) 
				moveSubvolumeFront();
			else if (e.getSource() == GeometrySubVolumePanel.this.getBackButton()) 
				moveBack();
			else if (e.getSource() == GeometrySubVolumePanel.this.getDeleteButton()) 
				deleteSubvolume();
			else if (e.getSource() == addShapeButton) {
				getAddSubVolumePopupMenu().show(addShapeButton, 0, addShapeButton.getHeight());
			} else if (e.getSource() == addAnalyticSubVolumeMenuItem) {
				addAnalyticSubVolume();
			} else if (e.getSource() == addCSGSubVolumeMenuItem) {
				addSubVolume(new CSGObject(null, getGeometrySpec().getFreeSubVolumeName(), -1));
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometrySubVolumePanel.this.getGeometrySpec() && (evt.getPropertyName().equals("subVolumes"))) {
				refreshButtons();
			}
			if (evt.getSource() == GeometrySubVolumePanel.this.getGeometrySpec() && (evt.getPropertyName().equals("subVolumes"))) {
				setSelectedSubVolume(findSubVolume());
			} 
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == GeometrySubVolumePanel.this.getScrollPaneTable().getSelectionModel()) {
				setSelectedSubVolume(findSubVolume());
				setSelectedObjects(new Object[]{getSelectedSubVolume()});
			}
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
 * connEtoM1:  (DeleteButton.action.actionPerformed(java.awt.event.ActionEvent) --> Geometry.removeAnalyticSubVolume(Lcbit.vcell.geometry.AnalyticSubVolume;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void deleteSubvolume() {
	try {
		if ((getSelectedSubVolume() != null)) {
			AsynchClientTask task1 = new AsynchClientTask("removing subdomain", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					if (getSelectedSubVolume() instanceof AnalyticSubVolume) {
						getGeometrySpec().removeAnalyticSubVolume((AnalyticSubVolume)getSelectedSubVolume());
					} else if (getSelectedSubVolume() instanceof CSGObject) {
						getGeometrySpec().removeCSGObject((CSGObject)getSelectedSubVolume());
					}
					getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
				}
			};
			ClientTaskDispatcher.dispatch(GeometrySubVolumePanel.this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1}, false);			
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (FrontButton.action.actionPerformed(java.awt.event.ActionEvent) --> Geometry.bringForward(Lcbit.vcell.geometry.AnalyticSubVolume;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void moveSubvolumeFront() {
	try {
		if ((getSelectedSubVolume() != null)) {
			AsynchClientTask task1 = new AsynchClientTask("moving to front", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					SubVolume selectedSubVolume = getSelectedSubVolume();
					if (selectedSubVolume instanceof CSGObject) {
						getGeometrySpec().bringForward((CSGObject)selectedSubVolume);
					} else if (selectedSubVolume instanceof AnalyticSubVolume) {
						getGeometrySpec().bringForward((AnalyticSubVolume)selectedSubVolume);
					}
					getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
				}
			};
			ClientTaskDispatcher.dispatch(GeometrySubVolumePanel.this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1}, false);
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoM9:  (BackButton.action.actionPerformed(java.awt.event.ActionEvent) --> Geometry.sendBackward(Lcbit.vcell.geometry.AnalyticSubVolume;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void moveBack() {
	try {
		if ((getSelectedSubVolume() != null)) {
			AsynchClientTask task1 = new AsynchClientTask("moving to back", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					SubVolume selectedSubVolume = getSelectedSubVolume();
					if (selectedSubVolume instanceof CSGObject) {
						getGeometrySpec().sendBackward((CSGObject)selectedSubVolume);
					} else if (selectedSubVolume instanceof AnalyticSubVolume) {
						getGeometrySpec().sendBackward((AnalyticSubVolume)selectedSubVolume);
					}
					getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
				}
			};
			ClientTaskDispatcher.dispatch(GeometrySubVolumePanel.this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1}, false);
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private SubVolume findSubVolume() {
	int selectedIndex = getScrollPaneTable().getSelectionModel().getMinSelectionIndex();
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
	
	getScrollPaneTable().setDefaultRenderer(SubVolume.class,new GeometrySubVolumeTableCellRenderer());
	getScrollPaneTable().setDefaultEditor(SubVolume.class,new DefaultCellEditor(new JTextField()) {
			private int lastRow = -1;
			private int lastCol = -1;
		   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			   lastRow = row;
			   lastCol = column;
			   	delegate.setValue(((SubVolume)value).getName());
			   	return editorComponent;
		   }
		   public final boolean stopCellEditing() {

				//
				//Three things can happen:
				//1.  The current editor contains a value that is validated OK,
				//		continue normally.
				//2.  The current editor contains a value that is validated NOT OK,
				//		user re-enters value until VALIDATE_OK -OR- user CANCELS and edit is lost.
				//3.  The current editor contains a value that CANNOT be validated (Exceptions outside verify,verify not implemented,etc...),
				//		validation is UNKNOWN, keep unvalidated value and continue.
				//
				try{
					String name = (String)delegate.getCellEditorValue();
					while(true){
						if(name.equals(TokenMangler.fixTokenStrict(name))){
							break;
						}
						name = DialogUtils.showInputDialog0(getComponent(), "Subdomain name "+name+" has illegal characters." +"\nProvide new value.", name);
					}
					delegate.setValue(name);//VALIDATE_OK, delegate gets New Good value
				}catch(UtilCancelException e){					
					delegate.setValue(((SubVolume)getScrollPaneTable().getValueAt(lastRow, lastCol)).getName());//delegate gets Last Good value
				}catch(Throwable e){//Delegate keeps UNVALIDATED value
				}			
				return super.stopCellEditing();
			}
	});
	
	getScrollPaneTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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

private JPopupMenu getAddSubVolumePopupMenu() {
	if (addSubVolumePopupMenu == null) {
		addAnalyticSubVolumeMenuItem = new JMenuItem("Analytic ...");
		addAnalyticSubVolumeMenuItem.addActionListener(ivjEventHandler);
		addCSGSubVolumeMenuItem = new JMenuItem("Constructed Solid Geometry");
		addCSGSubVolumeMenuItem.addActionListener(ivjEventHandler);
		addSubVolumePopupMenu = new JPopupMenu();
		addSubVolumePopupMenu.add(addAnalyticSubVolumeMenuItem);
		addSubVolumePopupMenu.add(addCSGSubVolumeMenuItem);
	}
	return addSubVolumePopupMenu;
}
/**
 * Return the Geometry property value.
 * @return cbit.vcell.geometry.Geometry
 */
private Geometry getGeometry() {
	return ivjGeometry;
}
/**
 * Return the GeometrySpec property value.
 * @return cbit.vcell.geometry.GeometrySpec
 */
private GeometrySpec getGeometrySpec() {
	return ivjGeometrySpec;
}
/**
 * Return the geometrySubVolumeTableModel property value.
 * @return cbit.vcell.geometry.gui.GeometrySubVolumeTableModel
 */
private GeometrySubVolumeTableModel getgeometrySubVolumeTableModel() {
	if (ivjgeometrySubVolumeTableModel == null) {
		try {
			ivjgeometrySubVolumeTableModel = new GeometrySubVolumeTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjgeometrySubVolumeTableModel;
}

private void addSubVolume(final SubVolume subVolume) {
	AsynchClientTask task1 = new AsynchClientTask("adding subdomain", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (subVolume instanceof AnalyticSubVolume) {
				getGeometrySpec().addSubVolume((AnalyticSubVolume)subVolume, true);
			} else if (subVolume instanceof CSGObject) {
				getGeometrySpec().addSubVolume((CSGObject)subVolume, true);
			}
			getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("adding subdomain", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			DocumentEditorSubPanel.setTableSelections(new Object[] {subVolume}, ivjScrollPaneTable, ivjgeometrySubVolumeTableModel);
		}
	};
	ClientTaskDispatcher.dispatch(GeometrySubVolumePanel.this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, true, false, false, null, true);
}

private void addAnalyticSubVolume() {
	Geometry g = getGeometry();
	final int dim = g.getDimension();
	AddShapeJPanel addShapeJPanel = new AddShapeJPanel(dim);
	addShapeJPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	addShapeJPanel.setDefaultCenter(
			getGeometry().getOrigin().getX()+getGeometry().getExtent().getX()/2,
			(getGeometry().getDimension() > 1?getGeometry().getOrigin().getY()+getGeometry().getExtent().getY()/2:null),
				(getGeometry().getDimension() > 2?getGeometry().getOrigin().getZ()+getGeometry().getExtent().getZ()/2:null));
	while(true){
		try {
			final boolean[] acceptFlag = new boolean[] {false};
			LWContainerHandle lwParent = LWNamespace.findLWOwner(GeometrySubVolumePanel.this);
			final JDialog d = new LWTitledDialog(lwParent,"Define New Subdomain Shape");
//			BeanUtils.centerOnComponent(d, GeometrySubVolumePanel.this);
			
			JPanel main = new JPanel();
			BoxLayout mainBoxLayout = new BoxLayout(main,BoxLayout.Y_AXIS);
			main.setLayout(mainBoxLayout);
			
			JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			menuPanel.add( LWNamespace.createRightSideIconMenuBar( ) );
			main.add(menuPanel);
			
			JPanel addCancelJPanel = new JPanel();
			addCancelJPanel.setBorder(new EmptyBorder(10,10,10,10));
			BoxLayout addCancelBoxLayout = new BoxLayout(addCancelJPanel,BoxLayout.X_AXIS);
			addCancelJPanel.setLayout(addCancelBoxLayout);
			
			{
				JButton helpButton = new JButton("Help");
				helpButton.addActionListener( ae -> { VcellHelpViewer.showStandaloneViewer();} ); 
				addCancelJPanel.add(helpButton);
			}
			
			JButton addJButton = new JButton("New Subdomain");
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
			main.add(Box.createVerticalStrut(10));
			main.add(addCancelJPanel);
			main.add(Box.createVerticalStrut(10));
			
			addShapeJPanel.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(AddShapeJPanel.PROPCHANGE_VALID_ANALYTIC)){
						addJButton.setEnabled(((Boolean)evt.getNewValue()));
					}
				}
			});
			d.setModalityType(ModalityType.DOCUMENT_MODAL);
//			d.setModal(true);
			d.getContentPane().add(main);
			d.pack();
			d.setSize(new Dimension(400, 400));
			d.setVisible(true);
			//DialogUtils.showModalJDialogOnTop(d, GeometrySubVolumePanel.this);

			if(acceptFlag[0]){
				addSubVolume(new AnalyticSubVolume(null, getGeometrySpec().getFreeSubVolumeName(),
							new Expression(addShapeJPanel.getCurrentAnalyticExpression()), -1));
					
			}
			break;
		} catch (Exception e1) {
			e1.printStackTrace();
			DialogUtils.showErrorDialog(GeometrySubVolumePanel.this, "Error adding shape:\n"+e1.getMessage(), e1);
		}
	}
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getButtonPanel() {
	if (buttonPanel == null) {
		try {
			addShapeButton = new JButton("Add Subdomain", new DownArrowIcon());
			addShapeButton.setHorizontalTextPosition(JButton.LEFT);
			
			getFrontButton().putClientProperty("JButton.buttonType", "roundRect");
			getBackButton().putClientProperty("JButton.buttonType", "roundRect");
			addShapeButton.putClientProperty("JButton.buttonType", "roundRect");
			getDeleteButton().putClientProperty("JButton.buttonType", "roundRect");
			
			buttonPanel = new javax.swing.JPanel();
			buttonPanel.setName("JPanel1");
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			buttonPanel.setLayout(gridBagLayout);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			buttonPanel.add(getFrontButton(), gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			buttonPanel.add(getBackButton(), gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			buttonPanel.add(addShapeButton, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			buttonPanel.add(getDeleteButton(), gbc);

			addShapeButton.addActionListener(ivjEventHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return buttonPanel;
}

/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ScrollTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new ScrollTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}
/**
 * Comment
 */
private SubVolume getSelectedSubVolume() {
	// user code begin {1}
	// user code end
	return ivjSelectedSubVolume;
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
	getScrollPaneTable().setModel(getgeometrySubVolumeTableModel());
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
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

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.insets = new java.awt.Insets(2,2,2,2);
		add(getScrollPaneTable().getEnclosingScrollPane(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.gridheight = 2;
		constraintsJPanel1.insets = new java.awt.Insets(2,2,2,2);
		add(getButtonPanel(), constraintsJPanel1);
		
		initConnections();
		geometrySubVolumePanel_Initialize();
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
	boolean bHasGeometry = getGeometry()!= null;
	boolean bHasGeomSpec = (bHasGeometry?getGeometry().getGeometrySpec()!= null:false);
	boolean bImageBased = (bHasGeomSpec?getGeometry().getGeometrySpec().getImage() != null:false);

	addShapeButton.setEnabled(!bImageBased);
	SubVolume selectedSubVolume = getSelectedSubVolume();
	if (!bHasGeomSpec  || getGeometry().getDimension()==0){
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
		int numAnalyticOrCSGSubVolumes = geometrySpec.getNumAnalyticOrCSGSubVolumes();
		if (numAnalyticOrCSGSubVolumes>1){
			getFrontButton().setEnabled(geometrySpec.getSubVolumeIndex(selectedSubVolume)>0);
			getBackButton().setEnabled(geometrySpec.getSubVolumeIndex(selectedSubVolume)<(numAnalyticOrCSGSubVolumes-1));
		}else{
			getFrontButton().setEnabled(false);
			getBackButton().setEnabled(false);
		}
		if (selectedSubVolume instanceof ImageSubVolume || (!bImageBased && (geometrySpec.getNumSubVolumes() <= 1))){
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
public void setGeometry(Geometry newValue) {
	if (ivjGeometry != newValue) {
		try {
			Geometry oldValue = getGeometry();
			ivjGeometry = newValue;
			if (ivjGeometry != null) {
				getgeometrySubVolumeTableModel().setGeometry(ivjGeometry);
				setGeometrySpec(ivjGeometry.getGeometrySpec());
			}
			firePropertyChange("geometry", oldValue, newValue);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Set the GeometrySpec to a new value.
 * @param newValue cbit.vcell.geometry.GeometrySpec
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometrySpec(GeometrySpec newValue) {
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
			this.refreshButtons();
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
private void setSelectedSubVolume(SubVolume newValue) {
	if (ivjSelectedSubVolume != newValue) {
		try {
			ivjSelectedSubVolume = newValue;
			refreshButtons();
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

@Override
public void setIssueManager(IssueManager issueManager) {
	super.setIssueManager(issueManager);
	getgeometrySubVolumeTableModel().setIssueManager(issueManager);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	setTableSelections(selectedObjects, getScrollPaneTable(), getgeometrySubVolumeTableModel());
}
}
