package cbit.vcell.solver.ode.gui;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import cbit.vcell.desktop.VCellTransferable;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (10/22/2000 11:19:00 AM)
 * @author: 
 */
public class MathOverridesPanel extends JPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JScrollPane ivjScrollPane = null;
	private MathOverridesTableModel ivjMathOverridesTableModel = null;
	private boolean fieldEditable = true;
	private org.vcell.util.gui.JTableFixed ivjJTableFixed = null;
	private DefaultCellEditor ivjDefaultCellEditor1 = null;
	private Component ivjComponent1 = null;
	private cbit.vcell.solver.MathOverrides fieldMathOverrides = null;
	private boolean ivjConnPtoP6Aligning = false;
	private MathOverridesTableCellRenderer ivjMathOverridesTableCellRenderer1 = null;
	private JMenuItem ivjJMenuItemCopy = null;
	private JMenuItem ivjJMenuItemCopyAll = null;
	private JPopupMenu ivjJPopupMenu1 = null;
	private JLabel ivjJLabelTitle = null;
	private JMenuItem ivjJMenuItemPaste = null;
	private JMenuItem ivjJMenuItemPasteAll = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener {
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
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == MathOverridesPanel.this.getComponent1()) 
				connEtoC6(e);
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
			if (evt.getSource() == MathOverridesPanel.this.getJTableFixed() && (evt.getPropertyName().equals("cellEditor"))) 
				connEtoM2(evt);
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
 * Comment
 */
private void component1_FocusLost(java.awt.event.FocusEvent focusEvent) {
	if((getDefaultCellEditor1() != null) && !(getDefaultCellEditor1().getComponent() instanceof JCheckBox)){
		getDefaultCellEditor1().stopCellEditing();
	}
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
 * connEtoC6:  (Component1.focus.focusLost(java.awt.event.FocusEvent) --> MathOverridesPanel.component1_FocusLost(Ljava.awt.event.FocusEvent;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.component1_FocusLost(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (MathOverridesPanel.initialize() --> MathOverridesPanel.makeBold()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.makeBold();
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
		getJTableFixed().setDefaultRenderer(Object.class, getMathOverridesTableCellRenderer1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (JTableFixed.cellEditor --> DefaultCellEditor1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setDefaultCellEditor1((javax.swing.DefaultCellEditor)getJTableFixed().getCellEditor());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (DefaultCellEditor1.this --> Component1.this)
 * @param value javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(javax.swing.DefaultCellEditor value) {
	try {
		// user code begin {1}
		// user code end
		if ((getDefaultCellEditor1() != null)) {
			setComponent1(getDefaultCellEditor1().getComponent());
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
			java.util.Vector primarySymbolTableEntriesV = new java.util.Vector();
			java.util.Vector resolvedValuesV = new java.util.Vector();
			//java.util.Vector selectedNamesV = new java.util.Vector();
			for(int i=0;i<rows.length;i+= 1){
				String rowName = (String)getJTableFixed().getValueAt(rows[i],MathOverridesTableModel.COLUMN_PARAMETER);
				primarySymbolTableEntriesV.add(getMathOverrides().getConstant(rowName));
				resolvedValuesV.add(getMathOverrides().getActualExpression(rowName,0));
				
			}
			VCellTransferable.ResolvedValuesSelection rvs =
				new VCellTransferable.ResolvedValuesSelection(
					(cbit.vcell.parser.SymbolTableEntry[])org.vcell.util.BeanUtils.getArray(primarySymbolTableEntriesV,cbit.vcell.parser.SymbolTableEntry.class),
					null,
					(cbit.vcell.parser.Expression[])org.vcell.util.BeanUtils.getArray(resolvedValuesV,cbit.vcell.parser.Expression.class),
					buffer.toString());

			VCellTransferable.sendToClipboard(rvs);
		}else{
			VCellTransferable.sendToClipboard(buffer.toString());
		}
	}catch(Throwable e){
		cbit.vcell.client.PopupGenerator.showErrorDialog("MathOverridesPanel copy failed.  "+e.getMessage());
	}
}


/**
 * Return the Component1 property value.
 * @return java.awt.Component
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Component getComponent1() {
	// user code begin {1}
	// user code end
	return ivjComponent1;
}


/**
 * Return the DefaultCellEditor1 property value.
 * @return javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.DefaultCellEditor getDefaultCellEditor1() {
	// user code begin {1}
	// user code end
	return ivjDefaultCellEditor1;
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
			org.vcell.util.gui.EmptyBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.EmptyBorderBean();
			ivjLocalBorder.setInsets(new java.awt.Insets(10, 0, 10, 0));
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setBorder(ivjLocalBorder);
			ivjJLabelTitle.setText("Specify non-default parameter values or scan over a range of values:");
			ivjJLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
private org.vcell.util.gui.JTableFixed getJTableFixed() {
	if (ivjJTableFixed == null) {
		try {
			ivjJTableFixed = new org.vcell.util.gui.JTableFixed();
			ivjJTableFixed.setName("JTableFixed");
			getScrollPane().setColumnHeaderView(ivjJTableFixed.getTableHeader());
			getScrollPane().getViewport().setBackingStoreEnabled(true);
			ivjJTableFixed.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
public cbit.vcell.solver.MathOverrides getMathOverrides() {
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
			ivjMathOverridesTableCellRenderer1 = new cbit.vcell.solver.ode.gui.MathOverridesTableCellRenderer();
			ivjMathOverridesTableCellRenderer1.setName("MathOverridesTableCellRenderer1");
			ivjMathOverridesTableCellRenderer1.setText("MathOverridesTableCellRenderer1");
			ivjMathOverridesTableCellRenderer1.setBounds(531, 168, 200, 16);
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
			ivjMathOverridesTableModel = new cbit.vcell.solver.ode.gui.MathOverridesTableModel();
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
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getScrollPane() {
	if (ivjScrollPane == null) {
		try {
			ivjScrollPane = new javax.swing.JScrollPane();
			ivjScrollPane.setName("ScrollPane");
			ivjScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			getScrollPane().setViewportView(getJTableFixed());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPane;
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
		setLayout(new java.awt.BorderLayout());
		setSize(404, 262);
		add(getScrollPane(), "Center");
		add(getJLabelTitle(), "North");
		initConnections();
		connEtoM1();
		connEtoC2();
		connEtoC7();
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


	java.util.Vector pasteDescriptionsV = new java.util.Vector();
	java.util.Vector newConstantsV = new java.util.Vector();
	java.util.Vector changedParameterNamesV = new java.util.Vector();
	try{
	//
	//
//cbit.vcell.math.MathDescription md = getMathOverrides().getSimulation().getMathDescription();
	//
	//

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


		Object pasteThis = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);
		for(int i=0;i<rows.length;i+= 1){
			if(pasteThis instanceof VCellTransferable.ResolvedValuesSelection){
				VCellTransferable.ResolvedValuesSelection rvs =
					(VCellTransferable.ResolvedValuesSelection)pasteThis;

				for(int j=0;j<rvs.getPrimarySymbolTableEntries().length;j+= 1){
					cbit.vcell.math.Constant pastedConstant = null;
					if(rvs.getPrimarySymbolTableEntries()[j] instanceof cbit.vcell.math.Constant){
						pastedConstant = (cbit.vcell.math.Constant)rvs.getPrimarySymbolTableEntries()[j];
					}else if(rvs.getAlternateSymbolTableEntries() != null && rvs.getAlternateSymbolTableEntries()[j] instanceof cbit.vcell.math.Constant){
						pastedConstant = (cbit.vcell.math.Constant)rvs.getAlternateSymbolTableEntries()[j];
					}
					if(pastedConstant == null && (
						(rvs.getPrimarySymbolTableEntries()[j] instanceof cbit.vcell.math.VolVariable) ||
						(rvs.getPrimarySymbolTableEntries()[j] instanceof cbit.vcell.math.Function)
					)){
						pastedConstant = new cbit.vcell.math.Constant(rvs.getPrimarySymbolTableEntries()[j].getName()+"_init",rvs.getExpressionValues()[j]);
					}
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
							cbit.vcell.desktop.VCellCopyPasteHelper.formatPasteList(
								rowName,
								pastedConstant.getName(),
								originalValueDescription,
								rvs.getExpressionValues()[j].infix()+"")
						);
					}
				}
			}
		}









		
		////int[] rows = getJTableFixed().getSelectedRows();// Paste to selected only
		////if(rows == null || rows.length == 0){// Try Paste by searching  All
			////rows = new int[getJTableFixed().getRowCount()];
			////for(int i=0;i<rows.length;i+= 1){
				////rows[i] = i;
			////}
		////}
		//Object obj = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);
			////obj instanceof cbit.vcell.desktop.VCellTransferable.SimulationParameterSelection
			////obj instanceof cbit.vcell.desktop.VCellTransferable.InitialConditionsSelection
			////obj instanceof cbit.vcell.desktop.VCellTransferable.SimulationResultsSelection
		//for(int i=0;i<rows.length;i+= 1){
			//String moParameterName = (String)getJTableFixed().getValueAt(rows[i],0);
			//String originalValueDescription = null;
			//if(getMathOverrides().getConstantArraySpec(moParameterName) != null){
				//originalValueDescription = getMathOverrides().getConstantArraySpec(moParameterName).toString();
			//}else if(getMathOverrides().getActualExpression(moParameterName,0) != null){
				//originalValueDescription = getMathOverrides().getActualExpression(moParameterName,0).infix();
			//}else{
				//throw new Exception("MathOverridesPanel can't find value for '"+moParameterName+"'");
			//}
			//cbit.vcell.solver.ConstantArraySpec oldCAS = getMathOverrides().getConstantArraySpec(moParameterName);
////System.out.println("row="+i+" "+moParameterName/*+" md-var="+md.getVariable(moParameterName)*/);
			//if(obj instanceof cbit.gui.SimpleTransferable.PlotDataSelection){
				//cbit.gui.SimpleTransferable.PlotDataSelection pds =
					//(cbit.gui.SimpleTransferable.PlotDataSelection)obj;
				////Match Sim results names to InitalConditons parameters by name
				//for(int j=0;j<pds.getSymbolTableEntries().length;j+= 1){
					//if((srs.getDataNames()[j]+"_"+cbit.vcell.mapping.SpeciesContextSpec.RoleNames[
							//cbit.vcell.mapping.SpeciesContextSpec.ROLE_InitialConcentration]).equals(moParameterName)){
						////cbit.vcell.parser.Expression exp = getMathOverrides().getActualExpression(moParameterName,0);
						////if(exp != null){
							////System.out.println(rows[i]+" "+moParameterName+" "+exp.infix());
							//changedParameterNamesV.add(moParameterName);
							//cbit.vcell.math.Constant newConstant = new cbit.vcell.math.Constant(moParameterName,new cbit.vcell.parser.Expression(srs.getDataValues()[0][j]));
							//newConstantsV.add(newConstant);
							//pasteDescriptionsV.add(
								//cbit.vcell.desktop.VCellCopyPasteHelper.formatPasteList(
									//moParameterName,
									//srs.getDataNames()[j],
									//originalValueDescription,
									////(oldCAS.getNumValues() == 1?oldCAS.getConstants()[0].getExpression().infix():
										////oldCAS.getMinValue()+"..."+oldCAS.getMaxValue()),
									//newConstant.getExpression().infix())
									////srs.getDataValues()[0][j]+""/*000(bPastedEqualCurrentParameter?"":" (from "+srs.getDataNames()[j]+")")*/)
							//);

						////}
					//}
				//}
				
			//}else if(obj instanceof cbit.vcell.desktop.VCellTransferable.SimulationParameterSelection){
				//cbit.vcell.desktop.VCellTransferable.SimulationParameterSelection sps =
					//(cbit.vcell.desktop.VCellTransferable.SimulationParameterSelection)obj;
				////Match parameter names directly
				//cbit.vcell.solver.ConstantArraySpec newCAS = sps.getConstantArraySpec(moParameterName);
				//cbit.vcell.math.Constant newConstant = null;
				//if(sps.getActualExpression(moParameterName) != null){
					//newConstant = new cbit.vcell.math.Constant(moParameterName,sps.getActualExpression(moParameterName));
				//}
				////if(newCAS != null && newConstant != null && newCAS.getNumValues() != 1){
					////throw new Exception("Pasting SimulationParameterSelection has values for both ConstantArraySpec and Constant, expected only 1 value.");
				////}
				////cbit.vcell.parser.Expression newExpression = sps.getActualExpression(moParameterName);
				//if(newCAS != null || newConstant != null){
					////cbit.vcell.solver.ConstantArraySpec newCAS = cbit.vcell.solver.ConstantArraySpec.clone(sps.getConstantArraySpec(moParameterName));
					////cbit.vcell.math.Constant newConstant = new cbit.vcell.math.Constant(moParameterName,sps.getActualExpression(moParameterName));
					////if(newConstant != null){
						////System.out.println(rows[i]+" "+moParameterName+" "+exp.infix());
						//changedParameterNamesV.add(moParameterName);
						//newConstantsV.add((newCAS != null?(Object)newCAS:(Object)newConstant));
						//pasteDescriptionsV.add(
							//cbit.vcell.desktop.VCellCopyPasteHelper.formatPasteList(
								//moParameterName,
								//moParameterName,
								//originalValueDescription,
								////(oldCAS.getNumValues() == 1?oldCAS.getConstants()[0].getExpression().infix():
									////oldCAS.getMinValue()+"..."+oldCAS.getMaxValue()),
								//(newCAS != null?newCAS.toString():newConstant.getExpression().infix())
								////(newCAS.getNumValues() == 1?newCAS.getConstants()[0].getExpression().infix():
									////newCAS.getMinValue()+"..."+newCAS.getMaxValue())
							//)
								////srs.getDataValues()[0][j]+""/*000(bPastedEqualCurrentParameter?"":" (from "+srs.getDataNames()[j]+")")*/)
						//);
					////}
				//}
			//}else if(obj instanceof cbit.vcell.desktop.VCellTransferable.InitialConditionsSelection){
				//cbit.vcell.desktop.VCellTransferable.InitialConditionsSelection ics =
					//(cbit.vcell.desktop.VCellTransferable.InitialConditionsSelection)obj;
					
				////cbit.vcell.parser.SymbolTableEntry ste = ics.getMathSymbolMapping().getBiologicalSymbol(md.getVariable(moParameterName));
				////if(ste != null){
					////System.out.println("ste "+ste.getName());
				////}else{
					////System.out.println("ste Not Found");
				////}

				////cbit.vcell.math.Variable var = ics.getMathSymbolMapping().getVariable(md.getVariable(moParameterName));
				////if(var != null){
					////System.out.println("var "+var.getName());
				////}else{
					////System.out.println("var Not Found");
				////}

				
				//cbit.vcell.mapping.SpeciesContextSpec[] scsArr = ics.getSpeciesContextSpecs();
				//for(int k=0;k<scsArr.length;k+= 1){
					////System.out.println("--- "+scsArr[k].getClass().getName()+" "+scsArr[k].toString());
					//cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter[] scspArr =
						//(cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter[])scsArr[k].getParameters();
					//for(int l = 0;l<scspArr.length;l+= 1){
						////if(ics.hasValuesForSCSRole(scspArr[l].getRole())){
						//if((scsArr[k].getSpeciesContext().getName()+"_"+scspArr[l].getName()).equals(moParameterName)){
							////String pastedFullParamName = scsArr[k].getSpeciesContext().getName()+"_"+cbit.vcell.mapping.SpeciesContextSpec.RoleNames[scspArr[l].getRole()];
							////if(moParameterName.startsWith(scsArr[k].getSpeciesContext().getName()+"_")){
								////System.out.println("--- "+scsArr[k].getSpeciesContext().getName()+" "+scspArr[l].getName());
							////}
							////cbit.vcell.math.Variable var = ics.getMathSymbolMapping().getVariable(scspArr[l]);
							////if(var == null){
								////System.out.println(scspArr[l]+" not found in math ");
							////}else if(var.getName().equals(moParameterName)){
								////System.out.println("Match Found -- var "+var.getName());
							////}
							//cbit.vcell.math.Constant newConstant = new cbit.vcell.math.Constant(moParameterName,scspArr[l].getExpression());
							//changedParameterNamesV.add(moParameterName);
							//newConstantsV.add(newConstant);
							//pasteDescriptionsV.add(
								//cbit.vcell.desktop.VCellCopyPasteHelper.formatPasteList(
									//moParameterName,
									//moParameterName,
									//originalValueDescription,
									////(oldCAS.getNumValues() == 1?oldCAS.getConstants()[0].getExpression().infix():
										////oldCAS.getMinValue()+"..."+oldCAS.getMaxValue()),
									//newConstant.getExpression().infix())
									////srs.getDataValues()[0][j]+""/*000(bPastedEqualCurrentParameter?"":" (from "+srs.getDataNames()[j]+")")*/)
							//);
						//}
					//}
				//}
			//}
		//}
	}
	}catch(Throwable e){
		cbit.vcell.client.PopupGenerator.showErrorDialog("Paste failed during pre-check (no changes made).\n"+e.getClass().getName()+" "+e.getMessage());
		return;
	}

	//Do paste
	try{
		if(pasteDescriptionsV.size() > 0){
			String[] pasteDescriptionArr = new String[pasteDescriptionsV.size()];
			pasteDescriptionsV.copyInto(pasteDescriptionArr);
			String[] changedParameterNamesArr = new String[changedParameterNamesV.size()];
			changedParameterNamesV.copyInto(changedParameterNamesArr);
			//cbit.vcell.math.Constant[] newConstantsArr = new cbit.vcell.math.Constant[newConstantsV.size()];
			//newConstantsV.copyInto(newConstantsArr);
			cbit.vcell.desktop.VCellCopyPasteHelper.chooseApplyPaste(pasteDescriptionArr,getMathOverrides(),changedParameterNamesArr,newConstantsV);
		}else{
			cbit.vcell.client.PopupGenerator.showInfoDialog("No paste items match the destination (no changes made).");
		}
	}catch(Throwable e){
		cbit.vcell.client.PopupGenerator.showErrorDialog("Paste Error\n"+e.getClass().getName()+" "+e.getMessage());
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
	if (c == getMathOverridesTableModel().COLUMN_ACTUAL &&
		getMathOverrides().isScan(getMathOverridesTableModel().getValueAt(r, getMathOverridesTableModel().COLUMN_PARAMETER).toString())) {
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
private void makeBold() {
	getJLabelTitle().setFont(getJLabelTitle().getFont().deriveFont(java.awt.Font.BOLD));
}


/**
 * Comment
 */
public void newSimulation(cbit.vcell.solver.Simulation simulation) {
	getMathOverridesTableModel().setMathOverrides(simulation == null ? null : simulation.getMathOverrides());
}


/**
 * Set the Component1 to a new value.
 * @param newValue java.awt.Component
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setComponent1(java.awt.Component newValue) {
	if (ivjComponent1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjComponent1 != null) {
				ivjComponent1.removeFocusListener(ivjEventHandler);
			}
			ivjComponent1 = newValue;

			/* Listen for events from the new object */
			if (ivjComponent1 != null) {
				ivjComponent1.addFocusListener(ivjEventHandler);
			}
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
 * Set the DefaultCellEditor1 to a new value.
 * @param newValue javax.swing.DefaultCellEditor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setDefaultCellEditor1(javax.swing.DefaultCellEditor newValue) {
	if (ivjDefaultCellEditor1 != newValue) {
		try {
			ivjDefaultCellEditor1 = newValue;
			connEtoM3(ivjDefaultCellEditor1);
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
public void setMathOverrides(cbit.vcell.solver.MathOverrides mathOverrides) {
	cbit.vcell.solver.MathOverrides oldValue = fieldMathOverrides;
	fieldMathOverrides = mathOverrides;
	firePropertyChange("mathOverrides", oldValue, mathOverrides);
}


/**
 * Comment
 */
private void showPopupMenu(MouseEvent mouseEvent) {
	if (mouseEvent.isPopupTrigger()) {
		Object obj = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);
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
	getJTableFixed().setShowGrid(editable);
	getJTableFixed().setBackground(editable ? javax.swing.UIManager.getColor("JTable.background") : javax.swing.UIManager.getColor("ScrollPane.background"));
	getJTableFixed().setForeground(editable ? javax.swing.UIManager.getColor("JTable.foreground") : java.awt.Color.blue);
	getMathOverridesTableModel().setEditable(editable);
	setMathOverrides(getMathOverrides()); // re-initializes keys
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD7F2B1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4DC6519D4930B869B2244BC0CCA9E2DF41BFA62B6DDE91753E5BDF1B72DE99B5BF4CB5BF4CDD7545826EE5A125358C33717991E89F9D5D43454900DCAD624A1A1B512C4F9AA68049018A4A41240B0F31999384C9D6E5C010110305F7F786E5C193943A0DB77EC4E79F267FE7F777E3F7F7DDF04127D49F9B92692C1484993783F37F384E161D9C1F874633BDFC65C7A3399C5026E5F8100FD42B3
	07F36139894A9217E5946D9552DFAA0477ACF8CFDE1AD174827CFED6F8682564B370A30A1EG143DBEABEB7354FAC6BFC2BD8574FD7DE4B1BCF783EC879CF9B6F5907D1BA7CBB93E2C036191F2CC02F03013501805B20E0B871AE400CDGD91C3E98E8188133EB0FF33A6EF9A9C256379453AEA2C7A793C16AE43C097CD9AE7CAE673815682A940AB94FDAA893GC71CB78B3F5AF1841EA75B871D6F5A7B5CEA5508DD1424AA1FAC0D08CA156C902B1CFEF7ED6DE91B6ABA8398456D90FD6DB60FA88942EAAAD795
	8A299CC11800773AF384B761A03A3613F2F82FB80778941960E7416FA5G2B4F21FF275EB8496D6F3A7A00F0782B47F66E09625F3AF34C2F2CF3C17FB2A7B71ADE24F5710005447C70790CA2AB008BA081E0A540BA00BF519C7846C77F8D4F7ABE35D19E9A12BD75DEEF0364D0CE0327927C2E2D05920E6B906D32629084D64FC7A0021176CC850C2F6E93733118249C45BADF59F11F3071ABF7188A8CE232B1652AA973BC0BC57679E0CC186E31283A4BAE0466E4286E8B02DE775A618D66BC835D19D7B6188FF3
	B9CD97023A63AF103A4E22B960005F040F64AB431BC502DF2743337C3C015213772DD0565D053951359BED3164AE9376D6AFB00C43428793CCEB3B188D1B3A42ED5960A5320BAFB21DEFEA367C1A62A59D1E59F29F4DD3723E8E4AAABE4EA8E279155EC65A6B2CEEE82BG29G6B81368294DF4EA8AA3F0CB9D6E0794BB4F32C5D26288DFE37649095017126DB1E072798D095DB2B47275ABCF6713CCDF15B7AA451A7F01AEFDAC88C7AC2783EC3F35F8EBABE90BD22E2D35D3287245B06C4D5D4FC0C660716FD04
	C691E92955BDA40202A12F00F29F35BCCB62E17329673C8E1BAA86A9D8791396D2A7A33673A0918440EF9617DF1EA1712A837F3BGDCDA9C7ACE93F99F098A7497353596D99E74FB2D4493A175B209F3A26DBBA660F752E50C71D36D84178D6FA5D7D04E2253FF5449E9F25B0979B6E58C5B586F9C44778A20EF8110G92GD6G2C3F02F974F49766D1532EAF8B15BF18EF18C7DDBD9B4C1B2F307C91FA425BD311C87490DBAB202C854085908F10520363C13C23174B2A3892ECE3927F5D42EDC2FC429563F628
	079586B4CC57F2CD17B92C0CBC73F8FB52376DD83AF69A60D83DDC68206D967C5C8CD0580BF53361F4B94B75861BCF446CBE23C0E20A0EE0CDB1FBFF6C35F29D2F823C86708640A95E5676D09AA2D3901E63B979E7007D829BFB5F86F88760B44985G9B408900C3DABC36D37965E0DB8B0097E0A1C0BA40E6003D360C2263G3DG71G2BGB2G04BE28CF00F600GC0B240DA002DG25F6983B6CD8372B65761C2963966E8316077B22777F90775FC8AE7AFC0247A9DA3DFD4463B21D7145B6CD3A7FABE0315D
	690D96DB4C6DE831A5F33B97F5B14E63B20BF8DCF7C5510BF5F1C8B04EFB0457F8FDCD4717B77E9F44D38F2C0DFE2B0D44CE9C91BDEA136895BD8E786163E50F3411B8717126DF56F8FEC871DE981F9AE1C070E9781F3611BA30292A626E732BE227282AEE0FB3D87EB855657377F903C39B6A5A52C66AC69E0571C9237FD59B695B6D3247A3524EC2E361653F21E30BA809C3E0F4236C77288C7F4C99B28F9F94477858767BB37982991BA4BF99A7595CF6150D542BD7F18FC197C406DAA155CE461DFB05D2DD0C
	DC00A3F37484F8A65BF13C98FD95470B0363DF9452FB3CA623B950424F4FB5898E56BF17B8026385336969F6E35B3368984771BEE66B4D97C99C0C6659E64A332F6C0570F2BA1452D9852771D59299CDE23F4DAF290DB00FEFF638D5D929C699EFD56E00A7CCCA3C3287B4EB78C3E37B8DE593919DA4DD949104B05A0E3DA4269F364BDE3F77346871D77371A1311378DA83B1E88168F5607860BDCB4AF16C6683C323ACC92C3A21EC3D035449D931C49757000355C99A11A51272BFE8652570FED8E465641D157B
	7B31BC855E53F565CC7EAF7A7572B3F97961FE1847511C78B717BEA71A7A037CD2BF7AF239E3AF49EDF158AF42340CC468A3AD7F9C9DFF8CABBB4FF31115CB9DA4EE12EE8E1459CF741E9A46B5602CFEB62FD8824F15G6B814822576BA4F4458A529DF6327296A71B0374BA191D0CEE23C61726ABF76968CADD04EE198F69B6B859D83905538BD0DEC5E2C46D6BD6114E61E2B119F0B1BB973A18FD39EEC2F7580FF42BB95DBA7E5C64525B7714C6576EE665AEB773A78E1E99G4C3E57C611EE0B1B0D5D3983EC
	2ED5B220376FB79A5D4181E6D78927BBB920376F41G52758EB03D817ECC9CE076B13A09B124DB8978B5G191C6E60201E2EEEDC7383702E41C8BA66C716C697BF48669C4903CC6E2AC1668723FB68AA522D9BE47312CCAE9769185E3993C817B5486A35D8E2FA1BA43DFDE72FE9F9A53178AE61F499A1F4A557112EEC084D43EB06981DE5C8CF779D0D4EB14464CDF03A54903AF913C837155395FA0C682C5A5EC79314F783CCF03AB40F1E6E6EFC24AB14E10D85D0A70752317E73B707C87B257B98833691DB20
	4AB78A63D2ED6D073497E5FB957BF28AB9FD1C0CFB9227F2C93F158A6F6B81B64B587FBF328B77A41286D304954A774D5176DC84AF336320B7587F176D761BD93F78E3AB516900B2550B7D48139D7AF5D13D4FA75B5DF4C9467B0ABBBA756383435D59C97A6EFE3762D3DB21C78EE03F32341358CB56EC9C4D63B1197B36708977F5F463890BD73C08FBBB71DE0C4B357775F1C95BF34D34B3CADC52F9BCB6696262F8A64B4C56582F7763FED479B02C9B07F1BF6A4D06D78D6BB1E2D463FB957623E8CF62786705
	5E790B235A13B84C6CD8B59C34A73D75A837E709E64FE6A8AFD7501EA7A7C2722A4AB62A5656EAC3A823D95A7B6E0CE22D9FDFF1BF6D95A766E817E6C3FEE9D9145C46D118653259CDFCC8843E9400F4G66DB7FF0B12F478C406D75E59431317C5B43E48E253A5CBEB6865EDF209F83DB004E8B1084102271EC9BA6B68D51AC417D22DF5757E15FF55FB2A135A041E434FF673AF721A95D47E23F59972C03B55BB61AB2G182D4714E26E7FF195742B28F709C2725F9E2CFEBE9F799785FDE036FC79E53DAD0536
	590636A4BC65B42528FCFE2086ED29FBF223497AA45A12674338157AE1FD6847F8BE2C616381176E47FAAAF917BC699CD4C07B1AD4E2179D93C60B5944C534B3EE5DEA217459DB8D6DA4305D4F6D9B895AD98C71AA57E2B6DBC5FB3CC013B202B15B2212BEEBC81FF71ADDAF780D4A183C0BFE2C03CDA02BF49465257BE93F8471A7594BFC6E7C006CD77969FED1B3AFA1FDC84525C3675157E54F6A6B64646495C3DF637738CC6D234C4740E850570CA7B61A36BC013E7EF184EDDB83B4591AEDE3946F7638D537
	CDF20F0B950718FDC7ACE4ED6C93D55E01E0FB2B305066AC59BC4EAA3A42B9E151352DAA6AC7D5C4ABBD81CE31F6FA5F88760DB5814C0B36D12C8789F0AB82E85BD7C7C90E5A946791767E53D14CD946D796C03E0D40D3B206FC7786DE882FA3DD9F4DF8FF9140F8D800EF16465BB1E69C0F6F1F5DCD735123AA32C4262F95241E182C4F0F212CB410138540664E5FF06B674E1563EC3F4BBA0E3A0A474999020DCE682B5823DDD47AE5E5C8F4D4E02FD9C5571FD5753438197CBE04E3557B57DF26B6F947C82CFD
	7A2A6904F52647F9A2E823FB9CEDF4017E64F134B1FE4048460CF12C1BBFDF4598EF815C412BE87B2C2B3CEE2A597B4A2B3BB4DF48762D4D63505A45531A0CEE60CF52E41C1BC0990CAEF582EDDC87B43993C8377F9AB9FB68176DFEDF957D5FA27B5470G3DCF8AC26373250EA37ABE243A67FD0D599215DB973371EBE84FC930C5556C7959B5B22F9C127DBE310A7E5F81F38E1BAF3252CE1342D01BC64CE4FEB1C07371FDF7C0F4741C8E1190EED8D084B10C59F56ABA5A3592EC4A546CDAF15DB8F7475AF652
	76B0A8B6481223C21373DBCDCE59B5484BEBA8674A64DB217E35AB22AF16FB5C3FC357495E114FA50FEA036C34FC7A7AA45A12GF62C51ECB9A04CA456CC26B99FE5E6033C1A6B1873ED03C6B9AFDDC71D83797F3BF6B9391B6CB98E905FDBD5F1289DA619E2CFFD181C70206884519464BE7C2B66C39C58172A79D0AA997930D573212AE026BE30FD4567F3424F40F2A7037B69253A5F5AB8C44F066E276B024AC9F6B614276153A83ECE07E77EB56622FFD6A8CB1D44730CED97481C2AAC8E7AA100F6G97C0BC
	C0CA9C2E878ED90F8AB14E0BDABDAAAC97ECD2E748469A1E5BD8C92C8645B17E7E18D5BF8FE1F138D51C6A9CF2CDDCE49C187D760B24FEB220BC8B2078B350F782B4FD86ED9FB89753766600216D4C2E4A7E191D2B56FEE2BEFE06F7097863G7B63G96822C82D82F79337ADCE5ACFF70BC0EBB426CD9B6C3BFD64D106F8D27915F564F7035738D5172FA142E91CAEFE079DB2461E7D1FC0D8E4F723A086249FBBB142D3E814FC22461D8F6175E98EA83639B0F49F7B20C0F2F6FFDAF897AB596DDD2FD989CFE84
	3E371B152BDF43D8EFE4FB9AF164894066EECF95605CED650D388F50D0C47C4C0477EC0012D958FE1F6FA6EB2FCAF8EF81688518GC886C88358B48B7346F43EA1D65E54632639D556FEB606EFFCE31CCEA861F15ABEABB4AE4671ED0D60FBB96C3DE4F6A89F73F34EE56AA71435G7482CCGA4832481EC1A0DFEDA4F1F0865A76D0041C7BE078AF7101F7778E616BFAF466493EEB262FBE4067A9E0D4DF713D1BCF77610F89E06B28B0083A08EE085C0BAC066CD984FAF444E9B6D98FFBAF9B3C71D191F0BE272
	E59B463547BF33BC9D0A600B48534F9A45B52E1766A91435G7482CCGA4832481EC7AAC46751973272619270D00EB1165C10310F2DBCBC7E666636B91FCD561399AEF4477954AD79A9F5907C78346E79D1DD95D7745642BB4347315404C749D0E4957E4286FD791FC532B07BFC4700557C337213EAF0D4DCC5F57A27842752971C6E35C5ABC123B867BFCB88367FB8F5B5CB8FEA54463F8AC0F4F4C56EB91FC61362E8933155970CF83E843C64D06D72F4EAC9F2AE372155CEC4417B5C3FD26091879EE286F9B91
	FC530B713A883E703E2E49D0DF5C3519697BDC84DF383EEEC3FD0D57E7266FEC84DFC43E9B6A7B7D8C756D1C0C65DF02213E74883E69697BCECCFDAB8C7595654FCC5F8B91FC617A52EEB6EA13338731CDEE38995B6437E7E843CFE25AD0FC0B118D4F49E843C1A8E7777C5E2267EE5D706E55F0BB4C8417846FABEFC1DB9F48A53875703ED943BDC8F1649CC2180338565C2EB0DB9056F31BB6075976C9000F09D1F560FD243C994AA74066A87FF5A17A3D1C3CAE63FBB9BB3B0C6F656C6B5A4770A0C3FF2B944B
	DF6C1A7A5E4E01AE23FBBB2C2ED650F340F208FF4D9C1CDF8DDE0CEE3BF251587640C5E35B2FDF1C5A761BAECEED7B5ACB51ED796EA5E3DBFEF04958161FDC1A5A1647AEC53305456ADDFAFE5782F16A1A03393E5C09396E50F2781A1B5CA509077795G69G9B3532DBE89939A3C04E7F4959B8B977E6E5FB5CA4AF89CFBC670915EB695320894D7D8A2AA30F677AF46E0D96CCD3BE01BFD339CF2A648EDF443D2FB6D86988F8C635FC380853342BF27BB14DA6E7B311E1B0CF6A34AB32A41193FF817B234A3CC2
	EEDF769CDC6BE7533D481A84D81B81B892B036CF97CCB5AF18C8083627728FF46FA0B1016D1D64E878FF2278A5BABC5BD3E9A942336694A8AB3E955BD4F9F774BCFE235BB80F5F6AA66795BE592F584570DC2D69AE554A022B9BB211A4E1E6B427A83FEAD31CE2C42EB7D1FED6960D7FF4B759136FF3BB5C8A3F51456A771B4F6F216D48679359F9C413CD3571FAD5942C57E6D5BEFDC453B77FF2F47F17DCB676FF5965687EFF71F2EC7F7F7EF2F47F3FF6B9367F8FDE0E569FB0DF9BFCFADF0FEA78C2F5A766F6
	3FA28F35D7F31D4F75C40FC1D90FF18CDE69099E03C3BD31E3F024A7FA8C5E69099D03061E709CE0BEBE286A5B6FDFB55F6F706BE252D82D693A25B73A6F73FA0DFDDF509B5D773BFBE37BFEEFEFF45F6F6B0D6D7B833DD3577F1991FD7D93DF591870ADFAFE6C05369FF7AB1EF54C998D4D09A3DC464500BE0E551A6C0AB1FD9C0FE8787F884157885AF792EE237EAD7156686383137705F13D3CE31A3CB583C672D64414F769EA281FB59C7F66C4A87E9847EF1B506B8F665183577478639AFE7C3A9E7F36062F
	19546303ED7137A178F7B4FCDD7E8EFD1E9EC63FEF99B472BBB5265F3985A1722AD15ECB32113CF5B165B56510BB4F12EDCC762B02E0799C492B0FF3F07FFB9370EF87A887FC4D6758BEB89BBF6F27F9562F5806A0D5BB3D22DD2818CBF88F1811B78968D3G5682EC86A81C0BB97A1FFFA5BE6887DAFE1F24005E8B36587AC4496AD6A5115F3B5DD624BF5F734EE5F6A4E9727ED1545FA9C925F6FCCF3BF71622511DF352BB935081E44FA55FE52EF542AB377B6BCE343BAEB123488A2082A4812CCEC4FE19FE87
	206D24B44A5EB1AD0EDB0E6D8DAF2B17A4D676747E5D7AB2FA860AFCBD4F6D0BA883C62DFDD467E13DEEGBBB6A70656E7F6E234790437004C8F764EE3738633069F2778F29D1E4DA7CC85F8AF61B014A54D43F9E7DEA14EBB374F43B9CE9A3D1243FEBF247B5DD694ED3D7337G264379296A60F79AC670AEC64DED74EEGFDAF3B8DE3320F6EDB8560BDDE437DC443A5E93825F4AD1D8C6F2BB4DCB5DD5B26417B3A5B70DE490DB9B8174BA07AEE47B676C4D548DE9E3BB758A0137B6F9656E0597C74E13AB6A6
	7CAD37637C7499AAB38E5E97822C3A9D6FDE86DE473B0EAB3F30CCD868B13E133A49FA4D3476F616DBDBEE8F5E235ABE395144F4FEE0C61D95C999C53DC9747E3C39A4AC163BCCEC9F07155F9BD2CE727EE6C1DF7E2310F2E26FA1333EFCE72E3E1C4468A867E73EBD7C9A7A167D70AAC17A6A2C285FB827A4B11F5671E7E266221C263F864C7C1C30BF4ABDE05AB371B5410EC22EB7F7BE768FCF0934FFD17CA233A7F9D47F4D75719DB798FE7FC16064FCE6C7BBFFE6BF37B80749786E47FA99FB97CB261CA8B2
	62B9EF527CE0FDB1DBB7B9312E564E473A5A6D8A2F2B83A1F5D1678A2F2B33B97AF24595DED773C24AD738426B6A0DDCD64E5AC18BDDD389F7E4949D3C835B4605FC02BB8E6F3D9A6E7EFC6CFF54BB307F99A9A0F1CE565E6368BB39A74E5E8B8B7ED6FD8451E15C8F9D781B69B09B605818D4D608E3D2AA58318EE08BC071020C224A8518F33B68FD41B3FD83B0312C3213ED887EBD3C284F1B43D7ADA6F3143C31ACE0BEF6AF88669E1F9B8E040E179F8D90496C37E7D090303FCC9D8C251BA7854BDA645032A7
	0684AD06B7C75C2B09850CEF418C7956FFE2BEB6E6BEECA2B1CED8903EB65FC0718B97042F4D7F207567AB202CF0A15E837826075427846F71G49G29G99GD98BF17D3E4785D344294F9C2D3257828B9CC93735447C3318E776F7827E39G7B4D55DF7FBBA175262628775F8B97319C294067E30BF2481379576DA57EF5C1198A3090E095409A000D0B503F8BAEFEB706DC742DD7D51B5DC53FD050AE46384551A01ACCAC79BCE8075E372DDCFF6EE27E1D8CFFD6AE8E36D7E65364B069BF6AG5F3D98ED385B
	C56E651D378D7068325B605482C036AB32D7D454B10625DFE0F1D963E4D94757F5192E572249GAFBE04E10335C92B4D29655F630677EDE2758DB0C74CA3F7F8C740A8375DA631F9F303CDE917A537FDCCE0735DF15A1F123B07930B31EF7064933BDD32629E17BDEAA42740F9EF21FD6CFBGE9403B77CE02DB1BDFBE05DD59BC4ECCCF7DBBF45E2E3BC843461D06C26DBB04BBF15C99AF8C9FF7C2470D150561638E4943E0793F97CEBD6E6CAD8A9FF7101F552143C77A2344BBB197AA5C2FCC118B64DC1FDD9F
	566E0018842DBEFF2C7B1DE77A74673EAFFF2ABAD8BC3E67093EFF72A30F717E49239E63FD7047BDD36F397F52937DBB5FAC0F7EBBDFD6F723AA295B95D0F769G19G5B810A17E0DD5EA0133AE43724427B15E607136C5F103F062122FE519B5DD7499B7BEFE8783DE49D5BA72B2ABCC464A37E2A175417AAFBA91261623CA4C60A5B69D299A91ECBF8C99B1144FE0A66F3A3BA372CG3FCEAE41F9D040CEF03D706E55F0BF35635CE8C243BD6FA03894F8DF2B45E55670EEFDBD30AE0D6B1FB3CCE22A5D13261F
	10310CE7ED7B03FC3277237F2CC137C3DEF53E39435A5AD8EF6969EC6CB8E331B454F77454F77634B5B7B7B5B7B17E25747EF0B47E16B39D2D174E34D9C3A434B503D86CDB9E4FA77B930DA2393B0A6355280863D55917168BE57B6BCC58CF04677446A52C1FAA3CABB8A78E36278F8A757D71C920E93FEB6AF38E96B3170F5864637D8243CD2A51F369A6B5F6AE4DD5599E06D3340EF9793EE012CA74487D7DBED14B59C56ADE8D576AEB17FDEED2E3FC1C5811CF73D1219B10ECAF2772944EA19D605B84C08AC0
	5ADD6CAC14714D35074E5172C6C91459B810E5AF1FA29E9B6E520F83EFB948B709417126DEF2BBBD249572F16683872EC3B65F05F3221F0F927925F7E79455GF481F881E681AC81D8853076EE4C63B77D95C2743E1AAD0CE99BD7ECEE0F4A5A4395BA86E5028CB2CEB01C1D6232B90E4D592C27346F346E01BC38875760BBF713B36B30BB234DC3DEF50C69EB90ED9EBCA7DAF2CA7B56719EDC07BF54C7642643FB864056FBB0667B9CC4FE1B2C28AEADFED79C87425ACAA7799F3BADDFD5E3F31B15B48D1E8B5B
	C62367DC76E86C1C4B8B9877DFBB83117D575E00F17F75FCC05FFFB15C1F38DCC5BB0C63674381329EE7FB0D27ED9E1B13766DAC4E3657F55FEBDCF0BBD4974317D19E0EEF9109FE2D7DDEBA95EBBED518ECF4AF419513AF7C1E8263ABF80F709DCC1E7ED9A8DBA73CE3A73E9DCF8EDFBFB4D0FCD3F2787A6161BEDCBF34C359EAGF69E167EAC0B056476080D6C2FB4891853AF3FC76A21F4A92C8D1762BA78BD3B20595E227B7DC85F27BFD77979272A033517E36F6553F8C0D76843F569EDE4DAAB303F55B779
	171F41F365AF05BCD5589F63CFD328C2AE5933D505C272A82F17A4D548233B312AD0C41F8C57C416A46E7E31F20F6CF94B413E87AC77626D5AF27647C34AE5255CE737F94AE5D0D8EEABD7EC9E27D8AE777362DA1FA26C513EBBE92576EF9AB9DAEBFEEC3959A464368A792CCD368A3924E1358AF9345D358AE6E81381723736C87B42FF64B79B978FCD6A577C5BE75B236EB7EDD8466F38AC8B0E0B2CBF5EDACD726860F2683F16E33FDBD7CD3E010A3656699C32C1E0EC52E03D47C156BD8DE32A981C63ED2BA6
	72FDC8438BF99FD5CD6C9E50717C8FD0CB87883EDCE3A0F59AGG40D1GGD0CB818294G94G88G88GD7F2B1B63EDCE3A0F59AGG40D1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2F9AGGGG
**end of data**/
}
}