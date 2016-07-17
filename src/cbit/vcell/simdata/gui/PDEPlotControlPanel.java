/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.NumberUtils;
import org.vcell.util.gui.DefaultListModelCivilized;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.image.DisplayAdapterService;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.data.DataIdentifierFilter;
import cbit.vcell.client.data.DefaultDataIdentifierFilter;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionPrintFormatter;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.solver.AnnotatedFunction;
/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 10:29:53 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class PDEPlotControlPanel extends JPanel {
	
	private JComboBox<String> filterComboBox;
	private JLabel ivjJLabel1 = null;
	private JTextField ivjJTextField1 = null;
	private DefaultListModelCivilized ivjDefaultListModelCivilized1 = null;
	private JList<DataIdentifier> ivjJList1 = null;
	private JPanel ivjJPanel1 = null;
	private JPanel ivjJPanel2 = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JLabel ivjJLabelMax = null;
	private JLabel ivjJLabelMin = null;
	private JSlider ivjJSliderTime = null;
	private JSplitPane ivjJSplitPane1 = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JPanel ivjTimeSliderJPanel = null;
	private double[] myTimePoints;
	private DataIdentifier[] myDataIdentifiers;
	private AnnotatedFunction[] myAnnotFunctions;	
	private DataIdentifierFilter dataIdentifierFilter;// = new DefaultDataIdentifierFilter();
	
	private Timer filterSelectTimer;
	private ActionListener filterChangeActionListener =
		new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if((filterSelectTimer = ClientTaskDispatcher.getBlockingTimer(PDEPlotControlPanel.this,null,null,filterSelectTimer,new ActionListener() {@Override public void actionPerformed(ActionEvent e2) {filterChangeActionListener.actionPerformed(e);}}))!=null){
					return;
				}
				try{
					filterVariableNames();
				}catch(Exception e2){
					e2.printStackTrace();
				}
			}
	};
	private JButton viewFunctionButton = null;
	private boolean bHasOldUserDefinedFunctions = false;
	
class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, javax.swing.event.ChangeListener, javax.swing.event.ListDataListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getJTextField1()) 
				connEtoC2(e);
			if (e.getSource() == getViewFunctionButton()) {
				viewFunction();
			}
		};
		public void contentsChanged(javax.swing.event.ListDataEvent e) {};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getJTextField1()) 
				connEtoC6(e);
		};
		public void intervalAdded(javax.swing.event.ListDataEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getDefaultListModelCivilized1()) 
				connEtoM4(e);
		};
		public void intervalRemoved(javax.swing.event.ListDataEvent e) {};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == getJSliderTime().getModel()) 
				setTimeFromSlider();
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getJList1() && !PDEPlotControlPanel.this.getJList1().getValueIsAdjusting()) 
				PDEPlotControlPanel.this.variableChanged();
		};
	};

/**
 * PDEPlotControlPanel constructor comment.
 */
public PDEPlotControlPanel() {
	super();
	initialize();
	setDataIdentifierFilter(new DefaultDataIdentifierFilter());
}

public void viewFunction() {
	Object selectedValue = getJList1().getSelectedValue();
	if (selectedValue == null) {
		return;
	}
	DataIdentifier di = (DataIdentifier)selectedValue;
	AnnotatedFunction func = findFunction(di,Arrays.asList(myAnnotFunctions));
	if (func == null || !func.isOldUserDefined()) {
		return;
	}
	
	try {
		Expression newexp = new Expression(func.getExpression());
		for (AnnotatedFunction af : myAnnotFunctions) {
			if (af.isOldUserDefined()) {
				newexp.substituteInPlace(new Expression(af.getName()), new Expression(af.getDisplayName()));
			}					 
		}		
		 
		java.awt.Font italicFont = getFont().deriveFont(Font.BOLD, 11);
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel nameLabel = new JLabel(func.getDisplayName() + " = ");
		nameLabel.setFont(italicFont);
		panel.add(nameLabel);
		JLabel label = new JLabel();
		ExpressionPrintFormatter epf = new ExpressionPrintFormatter(newexp);
		java.awt.image.BufferedImage graphicsContextProvider = new java.awt.image.BufferedImage(10,10,java.awt.image.BufferedImage.TYPE_BYTE_GRAY);
		java.awt.Graphics2D tempG2D = (java.awt.Graphics2D)graphicsContextProvider.getGraphics();
		java.awt.Dimension dim = epf.getSize(tempG2D);
		java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(dim.width,dim.height,java.awt.image.BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics2D g2d = bi.createGraphics();
		g2d.setClip(0,0,dim.width,dim.height);
		italicFont = getFont().deriveFont(Font.BOLD + Font.ITALIC, 11);
		g2d.setFont(italicFont);
		g2d.setBackground(getBackground());
		g2d.setColor(getForeground());
		
		g2d.clearRect(0,0,dim.width,dim.height);
		epf.paint(g2d);
		javax.swing.ImageIcon newImageIcon = new javax.swing.ImageIcon(bi);
		label.setIcon(newImageIcon);
		panel.add(label);
		
		String COPYEXP = "Copy Expression";
		JOptionPane inputDialog = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {COPYEXP, UserMessage.OPTION_CLOSE});
		final JDialog d = inputDialog.createDialog(this, "Function '" + func.getDisplayName() + "'");
		d.setResizable(true);
		d.pack();
		try {
			DialogUtils.showModalJDialogOnTop(d,this);
			if (inputDialog.getValue() != null && inputDialog.getValue().equals(COPYEXP)) {
				VCellTransferable.sendToClipboard(newexp.infix());
			}
		}finally {
			d.dispose();
		}		
	} catch (Exception ex) {
		DialogUtils.showErrorDialog(this, ex.getMessage(), ex);
	}
	
}

private void setDataIdentifiers(DataIdentifier[] dataIdentifiers){
	myDataIdentifiers = dataIdentifiers;
}
public void setDataIdentifierFilter(DataIdentifierFilter dataIdentifierFilter) {
	this.dataIdentifierFilter = dataIdentifierFilter;
	filterComboBox.removeActionListener(filterChangeActionListener);
	filterComboBox.removeAllItems();
	if(dataIdentifierFilter != null){
		String[] filterSetNames = this.dataIdentifierFilter.getFilterSetNames();
		for (int i = 0; i < filterSetNames.length; i++) {
			filterComboBox.addItem(filterSetNames[i]);
		}
		filterComboBox.setSelectedItem(dataIdentifierFilter.getDefaultFilterName());
		filterComboBox.addActionListener(filterChangeActionListener);
	}else{
		filterComboBox.addItem("All Variables");
		filterComboBox.setSelectedIndex(0);
	}
}
public DataIdentifierFilter getDataIdentifierFilter(){
	return dataIdentifierFilter;
}
///**
// * connEtoC1:  (model1.change.stateChanged(javax.swing.event.ChangeEvent) --> PDEPlotControlPanel.setTimeFromSlider(I)V)
// * @param arg1 javax.swing.event.ChangeEvent
// */
///* WARNING: THIS METHOD WILL BE REGENERATED. */
//private void connEtoC1(javax.swing.event.ChangeEvent arg1) {
//	try {
//		// user code begin {1}
//		// user code end
//		if ((getmodel1() != null)) {
//			this.setTimeFromSlider(getmodel1().getValue());
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
 * connEtoC2:  (JTextField1.action.actionPerformed(java.awt.event.ActionEvent) --> PDEPlotControlPanel.setTimeFromTextField()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField1().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (JTextField1.focus.focusLost(java.awt.event.FocusEvent) --> PDEPlotControlPanel.setTimeFromTextField(Ljava.lang.String;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField1().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (pdeDataContext1.this --> DefaultListModelCivilized1.contents)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(PDEDataContext value) {
	try {
		filterVariableNames();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM4:  (DefaultListModelCivilized1.listData.intervalAdded(javax.swing.event.ListDataEvent) --> JList1.selectedIndex)
 * @param arg1 javax.swing.event.ListDataEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(javax.swing.event.ListDataEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJList1().setSelectedIndex(0);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM7:  (pdeDataContext1.this --> JSliderTime.value)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(PDEDataContext value) {
	try {
		// user code begin {1}
		// user code end
		getJSliderTime().setValue(0);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

private AsynchClientTask[] getFilterVarNamesTasks(){
	final Object oldselection = getJList1().getSelectedValue();	
	final ArrayList<DataIdentifier> displayDataIdentifiers = new ArrayList<DataIdentifier>(); 
	AsynchClientTask task2 = new AsynchClientTask("filter variables", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(PDEPlotControlPanel.this.getName().equals("PostProcessPDEPCP")){
				System.out.println("PostProcessPDEPCP");
			}
			getViewFunctionButton().setVisible(bHasOldUserDefinedFunctions);
			
			if(myDataIdentifiers != null && myDataIdentifiers.length > 0){
				DataIdentifier[] dataIdentifierArr = new DataIdentifier[myDataIdentifiers.length];
				System.arraycopy(myDataIdentifiers, 0, dataIdentifierArr, 0, myDataIdentifiers.length);
				Arrays.sort(dataIdentifierArr, new Comparator<DataIdentifier>(){
					public int compare(DataIdentifier o1, DataIdentifier o2) {
						int bEqualIgnoreCase = o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
						if (bEqualIgnoreCase == 0){
							return o1.getDisplayName().compareTo(o2.getDisplayName());
						}
						return bEqualIgnoreCase;
					}
				});
	
				if(dataIdentifierFilter == null){
					displayDataIdentifiers.addAll(Arrays.asList(dataIdentifierArr));
				}else{
					ArrayList<DataIdentifier> acceptedDataIdentifiers = dataIdentifierFilter.accept((String)filterComboBox.getSelectedItem(),Arrays.asList(myAnnotFunctions), dataIdentifierArr);
					if(acceptedDataIdentifiers != null){
						displayDataIdentifiers.addAll(acceptedDataIdentifiers);
					}
				}
			}
		}
	};
	
	AsynchClientTask task3 = new AsynchClientTask("Update filtered variables", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(PDEPlotControlPanel.this.getName().equals("PostProcessPDEPCP")){
				System.out.println("PostProcessPDEPCP");
			}
			getDefaultListModelCivilized1().removeListDataListener(ivjEventHandler);
			getDefaultListModelCivilized1().setContents(displayDataIdentifiers.size() == 0?null:displayDataIdentifiers.toArray(new DataIdentifier[0]));
			getJList1().clearSelection();
			getDefaultListModelCivilized1().addListDataListener(ivjEventHandler);
			if(getJList1().getModel().getSize() > 0){
				if(oldselection == null){
					getJList1().setSelectedIndex(0);
				}else{
					boolean bFound = false;
					for (int i = 0; i < getJList1().getModel().getSize(); i++) {
						if(oldselection.equals(getJList1().getModel().getElementAt(i))){
							getJList1().setSelectedIndex(i);
							bFound = true;
							break;
						}
					}
					if(!bFound){
						getJList1().setSelectedIndex(0);
					}
				}
				
			}
		}
		
	};

	return new AsynchClientTask[] {task2, task3};
}
private  void filterVariableNames() throws Exception{
	AsynchClientTask[] filterVarNamesTasks = getFilterVarNamesTasks();
	Hashtable<String, Object> hashTable = new Hashtable<>();
	for (int i = 0; i < filterVarNamesTasks.length; i++) {
		final AsynchClientTask nextTask = filterVarNamesTasks[i];
		nextTask.run(hashTable);
	}
}

/**
 * Return the DefaultListModelCivilized1 property value.
 * @return cbit.gui.DefaultListModelCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DefaultListModelCivilized getDefaultListModelCivilized1() {
	if (ivjDefaultListModelCivilized1 == null) {
		try {
			ivjDefaultListModelCivilized1 = new DefaultListModelCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultListModelCivilized1;
}

/**
 * Insert the method's description here.
 * Creation date: (3/3/2004 5:29:59 PM)
 * @return cbit.vcell.math.AnnotatedFunction[]
 */
private void setFunctions(AnnotatedFunction[] newFunctions) {
	myAnnotFunctions = newFunctions.clone();
	bHasOldUserDefinedFunctions = false;
	for (int i = 0; i < myAnnotFunctions.length; i++) {
		if (myAnnotFunctions[i].isOldUserDefined()) {
			bHasOldUserDefinedFunctions = true;
		}
	}
}

public void setup(AnnotatedFunction[] newFunctions,DataIdentifier[] newDataIdentifiers,double[] newTimePoints,String selectedVar,int selectedTimeIndex) throws Exception{
	setTimePoints(newTimePoints);
//	setDataIdentifierFilter(newDataIdentifierFilter);
	setFunctions(newFunctions);
	setDataIdentifiers(newDataIdentifiers);
	filterVariableNames();
	for (int i = 0; i < getJList1().getModel().getSize(); i++) {
		if(getJList1().getModel().getElementAt(i).getName().equals(selectedVar)){
			getJList1().setSelectedIndex(i);
			break;
		}
	}
	getJSliderTime().setValue(selectedTimeIndex);	
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Time");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the JLabelMax property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMax() {
	if (ivjJLabelMax == null) {
		try {
			ivjJLabelMax = new javax.swing.JLabel();
			ivjJLabelMax.setName("JLabelMax");
			ivjJLabelMax.setText("10.00");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMax;
}


/**
 * Return the JLabelMin property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMin() {
	if (ivjJLabelMin == null) {
		try {
			ivjJLabelMin = new javax.swing.JLabel();
			ivjJLabelMin.setName("JLabelMin");
			ivjJLabelMin.setText("0.00");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMin;
}


/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public JList<DataIdentifier> getJList1() {
	if (ivjJList1 == null) {
		try {
			ivjJList1 = new JList<DataIdentifier>();
			ivjJList1.setName("JList1");
			ivjJList1.setBounds(0, 0, 131, 238);
			ivjJList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJList1;
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
			ivjJPanel1.setMinimumSize(new java.awt.Dimension(84, 150));

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.gridwidth = 2;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
			constraintsJTextField1.gridx = 0; constraintsJTextField1.gridy = 1;
			constraintsJTextField1.gridwidth = 2;
			constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField1.weightx = 1.0;
			constraintsJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextField1(), constraintsJTextField1);

			java.awt.GridBagConstraints constraintsTimeSliderJPanel = new java.awt.GridBagConstraints();
			constraintsTimeSliderJPanel.gridx = 1; constraintsTimeSliderJPanel.gridy = 2;
			constraintsTimeSliderJPanel.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsTimeSliderJPanel.weightx = 1.0;
			constraintsTimeSliderJPanel.weighty = 1.0;
			constraintsTimeSliderJPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getTimeSliderJPanel(), constraintsTimeSliderJPanel);
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setPreferredSize(new java.awt.Dimension(267, 150));
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			gridBagLayout.rowHeights = new int[] {7};
			ivjJPanel2.setLayout(gridBagLayout);
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(55, 150));

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);

			filterComboBox = new JComboBox<String>();
			filterComboBox.insertItemAt("All Variables", 0);
			filterComboBox.setSelectedIndex(0);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(4, 4, 0, 4);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			ivjJPanel2.add(filterComboBox, gridBagConstraints);
			ivjJPanel2.add(getJScrollPane1(), constraintsJScrollPane1);
			
			java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 2;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanel2.add(getViewFunctionButton(), gbc);

			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

private JButton getViewFunctionButton() {
	if (viewFunctionButton == null) {
		viewFunctionButton = new JButton("View Function");
		viewFunctionButton.setEnabled(false);
		viewFunctionButton.setVisible(false);
	}
	return viewFunctionButton;
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
			ivjJScrollPane1.setPreferredSize(new java.awt.Dimension(240, 100));
			ivjJScrollPane1.setMaximumSize(new java.awt.Dimension(259, 100));
			getJScrollPane1().setViewportView(getJList1());
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
 * Return the JSlider1 property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getJSliderTime() {
	if (ivjJSliderTime == null) {
		try {
			ivjJSliderTime = new javax.swing.JSlider();
			ivjJSliderTime.setName("JSliderTime");
			ivjJSliderTime.setPaintLabels(false);
			ivjJSliderTime.setInverted(true);
			ivjJSliderTime.setPaintTicks(true);
			ivjJSliderTime.setMajorTickSpacing(1);
			ivjJSliderTime.setSnapToTicks(true);
			ivjJSliderTime.setOrientation(javax.swing.JSlider.VERTICAL);
			sliderUpDownBusyActions();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSliderTime;
}

private Timer arrowTimer;

private void sliderUpDownBusyActions(){
	InputMap im_focus = ivjJSliderTime.getInputMap();//map KeyStroke to actionID (for WHEN_FOCUS condition)
	ActionMap am = ivjJSliderTime.getActionMap();//map actionID to Action
	//Get actionID for DOWN-ARROW keystroke
	Object downArrowActionID = im_focus.get(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
	//Get actionID for UP-ARROW keystroke
	Object upArrowActionID = im_focus.get(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
	//remove any currently defined actions for up and down actionIDs
	am.remove(downArrowActionID);
	am.remove(upArrowActionID);
	//redefine Actions of up and down arrows on JSlider
	//If getPdeDataContext isBusy then ignore up and down arrow actions to avoid
	//having the "wait" popup appear when using the up and down arrows on the JSlider
	am.put(downArrowActionID, new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			if(ClientTaskDispatcher.isBusy(null,null)){
				return;
			}
			if(ivjJSliderTime.getValue() == ivjJSliderTime.getMaximum()){
				return;
			}
			if(arrowTimer == null){
				arrowTimer = new Timer(100, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						synchronized (arrowTimer) {
							PDEPlotControlPanel.this.arrowTimer.stop();
							PDEPlotControlPanel.this.arrowTimer = null;
						}
						ivjJSliderTime.setValue(ivjJSliderTime.getValue()+1);
					}
				});
				arrowTimer.setRepeats(false);
				arrowTimer.start();					
			}else{
				return;
			}
		}
	});
	am.put(upArrowActionID, new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			if(ClientTaskDispatcher.isBusy(null,null)){
				return;
			}
			if(ivjJSliderTime.getValue() == ivjJSliderTime.getMinimum()){
				return;
			}
			if(arrowTimer == null){
				arrowTimer = new Timer(100, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						synchronized (arrowTimer) {
							PDEPlotControlPanel.this.arrowTimer.stop();
							PDEPlotControlPanel.this.arrowTimer = null;
						}
						ivjJSliderTime.setValue(ivjJSliderTime.getValue()-1);
					}
				});
				arrowTimer.setRepeats(false);
				arrowTimer.start();					
			}else{
				return;
			}
		}
	});

}
/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			ivjJSplitPane1.setPreferredSize(new java.awt.Dimension(269, 235));
			ivjJSplitPane1.setContinuousLayout(true);
			ivjJSplitPane1.setMinimumSize(new java.awt.Dimension(86, 235));
			getJSplitPane1().add(getJPanel1(), "top");
			getJSplitPane1().add(getJPanel2(), "bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField1() {
	if (ivjJTextField1 == null) {
		try {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField1;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTimeSliderJPanel() {
	if (ivjTimeSliderJPanel == null) {
		try {
			ivjTimeSliderJPanel = new javax.swing.JPanel();
			ivjTimeSliderJPanel.setName("TimeSliderJPanel");
			ivjTimeSliderJPanel.setLayout(new java.awt.GridBagLayout());
			ivjTimeSliderJPanel.setMinimumSize(new java.awt.Dimension(76, 208));

			java.awt.GridBagConstraints constraintsJLabelMin = new java.awt.GridBagConstraints();
			constraintsJLabelMin.gridx = 1; constraintsJLabelMin.gridy = 0;
			constraintsJLabelMin.anchor = java.awt.GridBagConstraints.NORTHEAST;
			constraintsJLabelMin.insets = new java.awt.Insets(4, 4, 4, 4);
			getTimeSliderJPanel().add(getJLabelMin(), constraintsJLabelMin);

			java.awt.GridBagConstraints constraintsJLabelMax = new java.awt.GridBagConstraints();
			constraintsJLabelMax.gridx = 1; constraintsJLabelMax.gridy = 1;
			constraintsJLabelMax.anchor = java.awt.GridBagConstraints.SOUTHWEST;
			constraintsJLabelMax.insets = new java.awt.Insets(4, 4, 4, 4);
			getTimeSliderJPanel().add(getJLabelMax(), constraintsJLabelMax);

			java.awt.GridBagConstraints constraintsJSliderTime = new java.awt.GridBagConstraints();
			constraintsJSliderTime.gridx = 0; constraintsJSliderTime.gridy = 0;
			constraintsJSliderTime.gridheight = 2;
			constraintsJSliderTime.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsJSliderTime.weighty = 1.0;
			constraintsJSliderTime.insets = new java.awt.Insets(4, 4, 4, 4);
			getTimeSliderJPanel().add(getJSliderTime(), constraintsJSliderTime);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeSliderJPanel;
}

public int getTimeSliderValue(){
	return getJSliderTime().getValue();
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
	getJTextField1().addActionListener(ivjEventHandler);
	getJTextField1().addFocusListener(ivjEventHandler);
	getDefaultListModelCivilized1().addListDataListener(ivjEventHandler);
	getViewFunctionButton().addActionListener(ivjEventHandler);
	getJList1().setModel(getDefaultListModelCivilized1());
	getJSliderTime().getModel().addChangeListener(ivjEventHandler);
	getJList1().addListSelectionListener(ivjEventHandler);
}

private Timer varChangeTimer;
private Timer sliderChangeTimer;
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.ipady = 0;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints.gridx = 0;
		setName("PDEPlotControlPanel");
		setPreferredSize(new java.awt.Dimension(150, 600));
		setLayout(new GridBagLayout());
		setSize(144, 643);
		setMaximumSize(new java.awt.Dimension(200, 800));
		setMinimumSize(new java.awt.Dimension(125, 300));
		this.add(getJSplitPane1(), gridBagConstraints);
		initConnections();

	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	setIdentifierListRenderer();
	// user code end
}
private void setIdentifierListRenderer() {
	getJList1().setCellRenderer(new IdentifierListCellRenderer(new FunctionListProvider() {
		@Override
		public List<AnnotatedFunction> getAnnotatedFunctions() {
			return Arrays.asList(myAnnotFunctions);
		}
	}));
}

public interface FunctionListProvider {
	List<AnnotatedFunction> getAnnotatedFunctions();
}
public static class IdentifierListCellRenderer extends DefaultListCellRenderer {
	FunctionListProvider functionListProvider;
	public IdentifierListCellRenderer(FunctionListProvider functionListProvider) {
		super();
		this.functionListProvider=functionListProvider;
	}
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
		if(value==null){
			return component;
		}
		DataIdentifier di = (DataIdentifier)value;
		AnnotatedFunction f = findFunction(di,functionListProvider.getAnnotatedFunctions());
		if (f != null) {
			if (f.isOldUserDefined()) {
				((JLabel)component).setIcon(VCellIcons.getOldOutputFunctionIcon());
			} else if (f.isOutputFunction()) {
				((JLabel)component).setIcon(VCellIcons.getOutputFunctionIcon());
			}
		}
		((JLabel)component).setText(di.getDisplayName());
		return component;
	}
}

/**
 * Comment
 */
private void setTimePoints(double[] newTimePoints) {
	double[] oldValue = myTimePoints;
	myTimePoints = newTimePoints;
	//
	getJSliderTime().setSnapToTicks(true);//So arrow keys work correctly with no minor tick marks
	//
	if (myTimePoints == null || myTimePoints.length == 1) {
		getJSliderTime().setMinimum(0);
		getJSliderTime().setMaximum(0);
		getJLabelMin().setText((myTimePoints == null?"":myTimePoints[0]+""));
		getJLabelMax().setText((myTimePoints == null?"":myTimePoints[0]+""));
		getJTextField1().setText((myTimePoints == null?"":myTimePoints[0]+""));
		if(getJSliderTime().isEnabled()){
			BeanUtils.enableComponents(getTimeSliderJPanel(),false);
		}
	} else {
		if(!getJSliderTime().isEnabled()){
			BeanUtils.enableComponents(getTimeSliderJPanel(),true);
		}
		int sValue = getJSliderTime().getValue();
		getJSliderTime().setMinimum(0);
		//getJSliderTime().setExtent(1);//Can't do this because of bug in JSlider won't set last value
		getJSliderTime().setMaximum(myTimePoints.length - 1);
		if(sValue >= 0 && sValue < myTimePoints.length){
			getJSliderTime().setValue(sValue);
		}else{
			getJSliderTime().setValue(0);
		}
		getJSliderTime().setMajorTickSpacing((myTimePoints.length < 10?1:myTimePoints.length/10));
//		getJSliderTime().setMinorTickSpacing(getJSliderTime().getMajorTickSpacing());//hides minor tick marks
		getJSliderTime().setMinorTickSpacing(1);// testing....
		//
		getJLabelMin().setText(NumberUtils.formatNumber(myTimePoints[0],8));
		getJLabelMax().setText(NumberUtils.formatNumber(myTimePoints[myTimePoints.length - 1],8));
	}
}

/**
 * Comment
 */
private void setTimeFromSlider() {
	if (!getJSliderTime().getValueIsAdjusting()) {
//		if((sliderChangeTimer = ClientTaskDispatcher.getBlockingTimer(this,null,null,sliderChangeTimer,new ActionListener() {@Override public void actionPerformed(ActionEvent e2) {setTimeFromSlider();}}))!=null){
//			return;
//		}
		firePropertyChange(PDEDataContext.PROPERTY_NAME_TIME_POINT, -1, myTimePoints[getJSliderTime().getValue()]);
	}
	updateTimeTextField(myTimePoints[getJSliderTime().getValue()]);
}

/**
 * Comment
 */
private void setTimeFromTextField(String typedValue) {
	int oldVal = getJSliderTime().getValue();
	double time = 0;
	try {
		time = Double.parseDouble(typedValue);
	} catch (NumberFormatException e) {
		// if typedTime is crap, put back old value
		updateTimeTextField(myTimePoints[oldVal]);
		return;
	}
	// we find neighboring time value; if out of bounds, it is set to corresponding extreme
	// we correct text, then adjust slider; change in slider will fire other updates
	int val = 0;
	if (time > myTimePoints[0]) {
		if (time >= myTimePoints[myTimePoints.length - 1]) {
			val = myTimePoints.length - 1;
		} else {
			for (int i=0;i<myTimePoints.length;i++) {
				val = i;
				if ((time >= myTimePoints[i]) && (time < myTimePoints[i+1]))
					break;
			}
		}
	}
	updateTimeTextField(myTimePoints[val]);
	getJSliderTime().setValue(val);
}

/**
 * Comment
 */
private void variableChanged() {
//	if((varChangeTimer = ClientTaskDispatcher.getBlockingTimer(this,null,null,varChangeTimer,new ActionListener() {@Override public void actionPerformed(ActionEvent e2) {variableChanged();}}))!=null){
//		return;
//	}
	firePropertyChange(PDEDataContext.PROPERTY_NAME_VCDATA_IDENTIFIER, null, getJList1().getSelectedValue());
}

private void updateTimeTextField(double newTime){
	getJTextField1().setText(Double.toString(newTime));
}

private static AnnotatedFunction findFunction(DataIdentifier identifier,List<AnnotatedFunction> myFunctionList) {
	AnnotatedFunction f = null;
	if (myFunctionList != null && myFunctionList.size() > 0 && identifier != null) {
		AnnotatedFunction[] funcs = (AnnotatedFunction[])myFunctionList.toArray(new AnnotatedFunction[myFunctionList.size()]);
		for (int i = 0; i < funcs.length; i++) {
			if (funcs[i] != null && funcs[i].getName() != null && funcs[i].getName().equals(identifier.getName())) {
				f = funcs[i];
				break;
			}
		}
	}
	return f;
}

public ListCellRenderer getVariableListCellRenderer() {
	return getJList1().getCellRenderer();
}
}
