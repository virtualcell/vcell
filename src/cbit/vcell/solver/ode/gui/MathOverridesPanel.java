/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.desktop.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.MathOverrides;
/**
 * Insert the type's description here.
 * Creation date: (10/22/2000 11:19:00 AM)
 * @author: 
 */
public class MathOverridesPanel extends JPanel {
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private MathOverridesTableModel ivjMathOverridesTableModel = null;
	private boolean fieldEditable = true;
	private ScrollTable ivjJTableFixed = null;
	private MathOverrides fieldMathOverrides = null;
	private boolean ivjConnPtoP6Aligning = false;
	private MathOverridesTableCellRenderer ivjMathOverridesTableCellRenderer1 = null;
	private JMenuItem ivjJMenuItemCopy = null;
	private JMenuItem ivjJMenuItemCopyAll = null;
	private JPopupMenu ivjJPopupMenu1 = null;
	private JLabel ivjJLabelTitle = null;
	private JMenuItem ivjJMenuItemPaste = null;
	private JMenuItem ivjJMenuItemPasteAll = null;
	
	private JButton removeUnusedButton = null;
	

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MathOverridesPanel.this.getJMenuItemCopy()) 
				connEtoC4(e);
			if (e.getSource() == MathOverridesPanel.this.getJMenuItemCopyAll()) 
				connEtoC5(e);
			if (e.getSource() == MathOverridesPanel.this.getJMenuItemPaste()) 
				connEtoC10(e);
			if (e.getSource() == MathOverridesPanel.this.getJMenuItemPasteAll()) 
				connEtoC11(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == MathOverridesPanel.this.getJTableFixed()) 
				connEtoC3(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == MathOverridesPanel.this.getJTableFixed()) 
				connEtoC8(e);
			if (e.getSource() == MathOverridesPanel.this.getJTableFixed()) 
				connEtoC9(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathOverridesPanel.this && (evt.getPropertyName().equals("mathOverrides"))) 
				connPtoP6SetTarget();
			if (evt.getSource() == MathOverridesPanel.this.getMathOverridesTableModel() && (evt.getPropertyName().equals("mathOverrides"))) 
				connPtoP6SetSource();
			if (evt.getSource() == MathOverridesPanel.this && (evt.getPropertyName().equals("editable"))) 
				connEtoC1(evt);
		};
	};

/**
 * MathOverridesPanel constructor comment.
 */
public MathOverridesPanel() {
	super();
	initialize();
}

/**
 * connEtoC1:  (MathOverridesPanel.editable --> MathOverridesPanel.updateEditableMode(Z)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateEditableMode(this.getEditable());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (JMenuItemPaste.action.actionPerformed(java.awt.event.ActionEvent) --> MathOverridesPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPaste_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (JMenuItemPasteAll.action.actionPerformed(java.awt.event.ActionEvent) --> MathOverridesPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPaste_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (MathOverridesPanel.initialize() --> MathOverridesPanel.controlKeys()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.controlKeys();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JTableFixed.mouse.mousePressed(java.awt.event.MouseEvent) --> MathOverridesPanel.showPopupMenu(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showPopupMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JMenuItemCopy.action.actionPerformed(java.awt.event.ActionEvent) --> MathOverridesPanel.copyCells(Ljava.lang.String;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.copyCells(getJMenuItemCopy().getActionCommand());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JMenuItemCopyAll.action.actionPerformed(java.awt.event.ActionEvent) --> MathOverridesPanel.copyCells(Ljava.lang.String;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.copyCells(getJMenuItemCopyAll().getActionCommand());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC8:  (JTableFixed.mouse.mouseReleased(java.awt.event.MouseEvent) --> MathOverridesPanel.jTableFixed_MouseReleased(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTableFixed_MouseReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (JTableFixed.mouse.mouseReleased(java.awt.event.MouseEvent) --> MathOverridesPanel.showPopupMenu(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showPopupMenu(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (MathOverridesPanel.initialize() --> JTableFixed.setDefaultRenderer(Ljava.lang.Class;Ljavax.swing.table.TableCellRenderer;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getJTableFixed().setDefaultRenderer(String.class, getMathOverridesTableCellRenderer1());
		getJTableFixed().setDefaultRenderer(ScopedExpression.class, getMathOverridesTableCellRenderer1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetTarget:  (MathOverridesTableModel.this <--> MathOverridesTableCellRenderer1.mathOverridesTableModel)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getMathOverridesTableCellRenderer1().setMathOverridesTableModel(getMathOverridesTableModel());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetTarget:  (MathOverridesTableModel.this <--> Table.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		getJTableFixed().setModel(getMathOverridesTableModel());
		getJTableFixed().createDefaultColumnsFromModel();
		getJTableFixed().setDefaultEditor(ScopedExpression.class, new TableCellEditorAutoCompletion(getJTableFixed()));
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP6SetSource:  (MathOverridesPanel.mathOverrides <--> MathOverridesTableModel.mathOverrides)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			this.setMathOverrides(getMathOverridesTableModel().getMathOverrides());
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
 * connPtoP6SetTarget:  (MathOverridesPanel.mathOverrides <--> MathOverridesTableModel.mathOverrides)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP6SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP6Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP6Aligning = true;
			getMathOverridesTableModel().setMathOverrides(this.getMathOverrides());
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
 * Comment
 */
private void controlKeys() {
	registerKeyboardAction(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			copyCells("Copy");
		}
	}, KeyStroke.getKeyStroke("ctrl C"), WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	registerKeyboardAction(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			copyCells("Copy All");
		}
	}, KeyStroke.getKeyStroke("ctrl K"), WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
}


/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 4:52:52 PM)
 * @param actionCommand java.lang.String
 * @return java.lang.String
 */
private synchronized void copyCells(String actionCommand) {
	try{
		int r = 0;
		int c = 0;
		int[] rows = new int[0];
		int[] columns = new int[0];
		if (actionCommand.equals("Copy")) {
			r = getJTableFixed().getSelectedRowCount();
			c = getJTableFixed().getSelectedColumnCount();
			rows = getJTableFixed().getSelectedRows();
			columns = getJTableFixed().getSelectedColumns();
		}
		if (actionCommand.equals("Copy All")) {
			r = getJTableFixed().getRowCount();
			c = getJTableFixed().getColumnCount();
			rows = new int[r];
			columns = new int[c];
			for (int i = 0; i < rows.length; i++){
				rows[i] = i;
			}
			for (int i = 0; i < columns.length; i++){
				columns[i] = i;
			}
		}

		StringBuffer buffer = new StringBuffer();
		// if copying more than one cell, make a string that will paste like a table in spreadsheets
		// also include column headers in this case
		if (r + c > 2) {
			for (int i = 0; i < c; i++){
				buffer.append(getJTableFixed().getColumnName(columns[i]) + (i==c-1?"":"\t"));
			}
			for (int i = 0; i < r; i++){
				buffer.append("\n");
				for (int j = 0; j < c; j++){
					Object cell = getJTableFixed().getValueAt(rows[i], columns[j]);
					cell = cell != null ? cell : ""; 
					buffer.append(cell.toString() + (j==c-1?"":"\t"));
				}
			}
		}
		// if copying a single cell, just get that value 
		if (r + c == 2) {
			Object cell = getJTableFixed().getValueAt(rows[0], columns[0]);
			cell = cell != null ? cell : ""; 
			buffer.append(cell.toString());
		}

		//Copy SimulationParameterSelection to clipboard along with "original style" formatted string
		if(r > 0){
			java.util.Vector<SymbolTableEntry> primarySymbolTableEntriesV = new java.util.Vector<SymbolTableEntry>();
			java.util.Vector<Expression> resolvedValuesV = new java.util.Vector<Expression>();
			//java.util.Vector selectedNamesV = new java.util.Vector();
			for(int i=0;i<rows.length;i+= 1){
				String rowName = (String)getJTableFixed().getValueAt(rows[i],MathOverridesTableModel.COLUMN_PARAMETER);
				primarySymbolTableEntriesV.add(getMathOverrides().getConstant(rowName));
				resolvedValuesV.add(getMathOverrides().getActualExpression(rowName,0));
				
			}
			VCellTransferable.ResolvedValuesSelection rvs =
				new VCellTransferable.ResolvedValuesSelection(
					(SymbolTableEntry[])BeanUtils.getArray(primarySymbolTableEntriesV,SymbolTableEntry.class),
					null,
					(Expression[])BeanUtils.getArray(resolvedValuesV,Expression.class),
					buffer.toString());

			VCellTransferable.sendToClipboard(rvs);
		}else{
			VCellTransferable.sendToClipboard(buffer.toString());
		}
	}catch(Throwable e){
		PopupGenerator.showErrorDialog(this, "MathOverridesPanel copy failed.  "+e.getMessage(), e);
	}
}


/**
 * Gets the editable property (boolean) value.
 * @return The editable property value.
 * @see #setEditable
 */
public boolean getEditable() {
	return fieldEditable;
}

/**
 * Return the JLabelTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
			ivjJLabelTitle.setText("Specify non-default parameter values or scan over a range of values:");
			ivjJLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabelTitle.setFont(ivjJLabelTitle.getFont().deriveFont(java.awt.Font.BOLD));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
}

/**
 * Return the JMenuItemCopy property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCopy() {
	if (ivjJMenuItemCopy == null) {
		try {
			ivjJMenuItemCopy = new javax.swing.JMenuItem();
			ivjJMenuItemCopy.setName("JMenuItemCopy");
			ivjJMenuItemCopy.setText("Copy");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopy;
}


/**
 * Return the JMenuItemCopyAll property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCopyAll() {
	if (ivjJMenuItemCopyAll == null) {
		try {
			ivjJMenuItemCopyAll = new javax.swing.JMenuItem();
			ivjJMenuItemCopyAll.setName("JMenuItemCopyAll");
			ivjJMenuItemCopyAll.setText("Copy All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopyAll;
}


/**
 * Return the JMenuItemPaste property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPaste() {
	if (ivjJMenuItemPaste == null) {
		try {
			ivjJMenuItemPaste = new javax.swing.JMenuItem();
			ivjJMenuItemPaste.setName("JMenuItemPaste");
			ivjJMenuItemPaste.setText("Paste");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPaste;
}


/**
 * Return the JMenuItemPasteAll property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPasteAll() {
	if (ivjJMenuItemPasteAll == null) {
		try {
			ivjJMenuItemPasteAll = new javax.swing.JMenuItem();
			ivjJMenuItemPasteAll.setName("JMenuItemPasteAll");
			ivjJMenuItemPasteAll.setText("Paste All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPasteAll;
}


/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getJPopupMenu1() {
	if (ivjJPopupMenu1 == null) {
		try {
			ivjJPopupMenu1 = new javax.swing.JPopupMenu();
			ivjJPopupMenu1.setName("JPopupMenu1");
			ivjJPopupMenu1.add(getJMenuItemCopy());
			ivjJPopupMenu1.add(getJMenuItemCopyAll());
			ivjJPopupMenu1.add(getJMenuItemPaste());
			ivjJPopupMenu1.add(getJMenuItemPasteAll());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenu1;
}

/**
 * Return the JTableFixed property value.
 * @return cbit.gui.JTableFixed
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ScrollTable getJTableFixed() {
	if (ivjJTableFixed == null) {
		try {
			ivjJTableFixed = new ScrollTable();
			ivjJTableFixed.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			ivjJTableFixed.setName("JTableFixed");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTableFixed;
}

/**
 * Gets the mathOverrides property (cbit.vcell.solver.MathOverrides) value.
 * @return The mathOverrides property value.
 * @see #setMathOverrides
 */
public MathOverrides getMathOverrides() {
	return fieldMathOverrides;
}


/**
 * Return the MathOverridesTableCellRenderer1 property value.
 * @return cbit.vcell.solver.ode.gui.MathOverridesTableCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathOverridesTableCellRenderer getMathOverridesTableCellRenderer1() {
	if (ivjMathOverridesTableCellRenderer1 == null) {
		try {
			ivjMathOverridesTableCellRenderer1 = new MathOverridesTableCellRenderer();
			ivjMathOverridesTableCellRenderer1.setName("MathOverridesTableCellRenderer1");
			ivjMathOverridesTableCellRenderer1.setText("MathOverridesTableCellRenderer1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathOverridesTableCellRenderer1;
}


/**
 * Return the MathOverridesTableModel property value.
 * @return cbit.vcell.solver.ode.gui.MathOverridesTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathOverridesTableModel getMathOverridesTableModel() {
	if (ivjMathOverridesTableModel == null) {
		try {
			ivjMathOverridesTableModel = new MathOverridesTableModel(getJTableFixed());
			ivjMathOverridesTableModel.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathOverridesTableModel;
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
	getJTableFixed().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getMathOverridesTableModel().addPropertyChangeListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopyAll().addActionListener(ivjEventHandler);
	getJTableFixed().addMouseListener(ivjEventHandler);
	getJMenuItemPaste().addActionListener(ivjEventHandler);
	getJMenuItemPasteAll().addActionListener(ivjEventHandler);
	connPtoP5SetTarget();
	connPtoP6SetTarget();
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MathOverridesPanel");
		setLayout(new GridBagLayout());
		setSize(404, 262);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		add(getJLabelTitle(), gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(getJTableFixed().getEnclosingScrollPane(), gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		removeUnusedButton = new JButton();
		removeUnusedButton.setForeground(Color.red);
		removeUnusedButton.setText("remove unused parameter overrides");
		removeUnusedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MathOverrides mathOverrides = getMathOverrides();
				if (mathOverrides!=null){
					mathOverrides.removeUnusedOverrides();
					removeUnusedButton.setVisible(false);
				}
			}
		});
		
		add(removeUnusedButton, gridBagConstraints);

		initConnections();
		connEtoM1();
		connEtoC2();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void jMenuItemPaste_ActionPerformed(java.awt.event.ActionEvent actionEvent) {


	java.util.Vector<String> pasteDescriptionsV = new java.util.Vector<String>();
	java.util.Vector<Expression> newConstantsV = new java.util.Vector<Expression>();
	java.util.Vector<String> changedParameterNamesV = new java.util.Vector<String>();
	try{

		if(actionEvent.getSource().equals(getJMenuItemPaste()) || actionEvent.getSource().equals(getJMenuItemPasteAll())){
			int[] rows = null;
			if(actionEvent.getSource() == getJMenuItemPasteAll()){
				rows = new int[getJTableFixed().getRowCount()];
				for(int i=0;i<rows.length;i+= 1){
					rows[i] = i;
				}
			}else{
				rows = getJTableFixed().getSelectedRows();
			}	
	
			Object pasteThis = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
			for(int i=0;i<rows.length;i+= 1){
				if(pasteThis instanceof VCellTransferable.ResolvedValuesSelection){
					VCellTransferable.ResolvedValuesSelection rvs =
						(VCellTransferable.ResolvedValuesSelection)pasteThis;
	
					for(int j=0;j<rvs.getPrimarySymbolTableEntries().length;j+= 1){
						Constant pastedConstant = null;
						if(rvs.getPrimarySymbolTableEntries()[j] instanceof Constant){
							pastedConstant = (Constant)rvs.getPrimarySymbolTableEntries()[j];
						}else if(rvs.getAlternateSymbolTableEntries() != null && rvs.getAlternateSymbolTableEntries()[j] instanceof Constant){
							pastedConstant = (Constant)rvs.getAlternateSymbolTableEntries()[j];
						}
						//
						// if a Constant is not on the clipboard, try to find a corresponding Constant that starts with "varname_init_"
						//
						if(pastedConstant == null && 
							(rvs.getPrimarySymbolTableEntries()[j] instanceof Function) ||
							(rvs.getPrimarySymbolTableEntries()[j] instanceof VolVariable) ||
							(rvs.getPrimarySymbolTableEntries()[j] instanceof VolumeRegionVariable) ||
							(rvs.getPrimarySymbolTableEntries()[j] instanceof MemVariable) ||
							(rvs.getPrimarySymbolTableEntries()[j] instanceof MembraneRegionVariable)){
							
							MathDescription mathDescription = getMathOverrides().getSimulation().getMathDescription();
							Enumeration<Constant> constants = mathDescription.getConstants();
							while (constants.hasMoreElements()){
								Constant constant = constants.nextElement();
								if (constant.getName().startsWith(rvs.getPrimarySymbolTableEntries()[j].getName()+DiffEquMathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX)){
									pastedConstant = new Constant(constant.getName(),rvs.getExpressionValues()[j]);
								}
							}
						}
						
						//
						// find row of math overrides table with the same name as the pastedConstant and propose to change that override to the pasted value
						//
						String rowName = (String)getJTableFixed().getValueAt(rows[i],MathOverridesTableModel.COLUMN_PARAMETER);
						if(pastedConstant != null && pastedConstant.getName().equals(rowName)){
							changedParameterNamesV.add(rowName);
							newConstantsV.add(rvs.getExpressionValues()[j]);
							String originalValueDescription = null;
							if(getMathOverrides().getConstantArraySpec(rowName) != null){
								originalValueDescription = getMathOverrides().getConstantArraySpec(rowName).toString();
							}else if(getMathOverrides().getActualExpression(rowName,0) != null){
								originalValueDescription = getMathOverrides().getActualExpression(rowName,0).infix();
							}else{
								throw new Exception("MathOverridesPanel can't find value for '"+rowName+"'");
							}
							pasteDescriptionsV.add(
								VCellCopyPasteHelper.formatPasteList(
									rowName,
									pastedConstant.getName(),
									originalValueDescription,
									rvs.getExpressionValues()[j].infix()+"")
							);
						}
					}
				}
			}
		}
	}catch(Throwable e){
		PopupGenerator.showErrorDialog(this, "Paste failed during pre-check (no changes made).\n"+e.getClass().getName()+" "+e.getMessage(), e);
		return;
	}

	//Do paste
	try{
		if(pasteDescriptionsV.size() > 0){
			String[] pasteDescriptionArr = new String[pasteDescriptionsV.size()];
			pasteDescriptionsV.copyInto(pasteDescriptionArr);
			String[] changedParameterNamesArr = new String[changedParameterNamesV.size()];
			changedParameterNamesV.copyInto(changedParameterNamesArr);
			VCellCopyPasteHelper.chooseApplyPaste(this, pasteDescriptionArr,getMathOverrides(),changedParameterNamesArr,newConstantsV);
		}else{
			PopupGenerator.showInfoDialog(this, "No paste items match the destination (no changes made).");
		}
	}catch(Throwable e){
		PopupGenerator.showErrorDialog(this, "Paste Error\n"+e.getClass().getName()+" "+e.getMessage(), e);
	}
}


/**
 * Comment
 */
private void jTableFixed_MouseReleased(java.awt.event.MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() != 2) {
		return;
	}
	int c = getJTableFixed().getSelectedColumn();
	int r = getJTableFixed().getSelectedRow();
	if (c == MathOverridesTableModel.COLUMN_ACTUAL &&
		getMathOverrides().isScan(getMathOverridesTableModel().getValueAt(r, MathOverridesTableModel.COLUMN_PARAMETER).toString())) {
			getMathOverridesTableModel().setValueAt(getMathOverridesTableModel().getValueAt(r, c), r, c);
	}
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		MathOverridesPanel aMathOverridesPanel;
		aMathOverridesPanel = new MathOverridesPanel();
		frame.setContentPane(aMathOverridesPanel);
		frame.setSize(aMathOverridesPanel.getSize());
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
 * Sets the editable property (boolean) value.
 * @param editable The new value for the property.
 * @see #getEditable
 */
public void setEditable(boolean editable) {
	boolean oldValue = fieldEditable;
	fieldEditable = editable;
	firePropertyChange("editable", new Boolean(oldValue), new Boolean(editable));
}


/**
 * Sets the mathOverrides property (cbit.vcell.solver.MathOverrides) value.
 * @param mathOverrides The new value for the property.
 * @see #getMathOverrides
 */
public void setMathOverrides(MathOverrides mathOverrides) {
	MathOverrides oldValue = fieldMathOverrides;
	fieldMathOverrides = mathOverrides;
	if (fieldMathOverrides!=null && fieldMathOverrides.hasUnusedOverrides()){
		this.removeUnusedButton.setVisible(true);
	}else{
		this.removeUnusedButton.setVisible(false);
	}
	firePropertyChange("mathOverrides", oldValue, mathOverrides);
}


/**
 * Comment
 */
private void showPopupMenu(MouseEvent mouseEvent) {
	if (mouseEvent.isPopupTrigger()) {
		Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
		boolean bPaste =
			obj instanceof VCellTransferable.ResolvedValuesSelection;

		getJMenuItemPaste().setEnabled(bPaste && (getJTableFixed().getSelectedRowCount() > 0));
		getJMenuItemPaste().setVisible(getEditable());
		getJMenuItemPasteAll().setEnabled(bPaste);
		getJMenuItemPasteAll().setVisible(getEditable());
		getJMenuItemCopy().setEnabled(getJTableFixed().getSelectedRowCount() > 0);
		getJMenuItemCopyAll().setEnabled(getJTableFixed().getRowCount() > 0);
		getJPopupMenu1().show(getJTableFixed(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}
}


/**
 * Comment
 */
private void updateEditableMode(boolean editable) {
	getJLabelTitle().setVisible(editable);
	getJTableFixed().setRequestFocusEnabled(editable);
	getJTableFixed().setCellSelectionEnabled(editable);
	getMathOverridesTableModel().setEditable(editable);
	setMathOverrides(getMathOverrides()); // re-initializes keys
}

}
