package cbit.vcell.solver.ode.gui;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
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
	private cbit.gui.JTableFixed ivjJTableFixed = null;
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
			cbit.gui.SimpleTransferable.ResolvedValuesSelection rvs =
				new cbit.gui.SimpleTransferable.ResolvedValuesSelection(
					(cbit.vcell.parser.SymbolTableEntry[])cbit.util.BeanUtils.getArray(primarySymbolTableEntriesV,cbit.vcell.parser.SymbolTableEntry.class),
					null,
					(cbit.vcell.parser.Expression[])cbit.util.BeanUtils.getArray(resolvedValuesV,cbit.vcell.parser.Expression.class),
					buffer.toString());

			cbit.vcell.desktop.VCellTransferable.sendToClipboard(rvs);
		}else{
			cbit.vcell.desktop.VCellTransferable.sendToClipboard(buffer.toString());
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
			cbit.gui.EmptyBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.EmptyBorderBean();
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
private cbit.gui.JTableFixed getJTableFixed() {
	if (ivjJTableFixed == null) {
		try {
			ivjJTableFixed = new cbit.gui.JTableFixed();
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
			if(pasteThis instanceof cbit.gui.SimpleTransferable.ResolvedValuesSelection){
				cbit.gui.SimpleTransferable.ResolvedValuesSelection rvs =
					(cbit.gui.SimpleTransferable.ResolvedValuesSelection)pasteThis;

				for(int j=0;j<rvs.getPrimarySymbolTableEntries().length;j+= 1){
					cbit.vcell.math.Constant pastedConstant = null;
					if(rvs.getPrimarySymbolTableEntries()[j] instanceof cbit.vcell.math.Constant){
						pastedConstant = (cbit.vcell.math.Constant)rvs.getPrimarySymbolTableEntries()[j];
					}else if(rvs.getAlternateSymbolTableEntries() != null && rvs.getAlternateSymbolTableEntries()[j] instanceof cbit.vcell.math.Constant){
						pastedConstant = (cbit.vcell.math.Constant)rvs.getAlternateSymbolTableEntries()[j];
					}
					if(pastedConstant == null && rvs.getPrimarySymbolTableEntries()[j] instanceof cbit.vcell.math.VolVariable){
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
			obj instanceof cbit.gui.SimpleTransferable.ResolvedValuesSelection;

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
	D0CB838494G88G88GADFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D4D715F197142E69CA95EB2C28C851469A7A4595B5F53162AE6B07153618C2172F42AEEE3396152DF40D0AFE597DDE7EA00110041F3628E9C9AD15500688A414907E72CB93C8E0A0838990A0AFB3AFB38313F913B7EF12898459F36E3D674D1B491BCC1A357BAD5FF7F8734E3D67771EFB6F39BFAF52761D337233F336CBD2F63EA47E9D4A162424F31274547A177FC0385A5D690512695F6BG
	DBA4576F7360598AE56B06538BCB25950182F86F017782F8FF85FEEF13FE9818F596FE4410930232DFBC71D86378F2E6F4121C8D6C3DE7C391BC0B81FA81074F145DA8DF5F30C3602788BCC1F60EA4A5D6E01D0FECA893383528A760DA81FAAAF8FD9D1EDC0129F53522DE4FFD49D26663DA4EE664E362C930D4502E934FF269B7593376202CB426A7526C03F7B7G9A1FAB3D3871GBC0F5AEE3A0EBB7ABCFA5510C371FA2B822AF7C8512AD427D2658AFA6A6ABAE45DFD96B01A472984EC32CF71CAD259DB48D7
	AAA8E0FCA4A983F8A6B35CBA99E575426F90407437883FEBG71EB60F75E5B6905E5EF13FD47F69F957AF75F7E3A347440C0DEC98C7BEA5E66F6753C9D364F6FDB1973DB5696CB6D6873A4A81B8F3084A093E0FBE5FAE18D406FD98C7C2073ADF8B67469CD6A40006AEB707B9B3DCE2D83AC75426F3ABAA89138730AC3551C12445B79318D15967A0C871CEE71FB2663BEC92E26B6CFB97F083462AB7767EC3070490A645BB96D155C974E4A30CF386C7C02D83227D4C646E4246CAE49ACFB51871F494D37101D91
	F8A6B7C970C9B149DEDB09ED6D6F4336DE8C3F118E6315636FB2FC4D5EB01E4767FF33184177EEA8DB3817E2233B18F4E94F1BAD156E7F04259F127EA1B1E7DF95572135AADA971B8FF96BD5DC665B06AEBE06DFEA42F3DD5EE2F10A6FB5FF02785D17DE4863EB65C56C2FEB76C3DFBD10DED88ED08B508D2083CCBBC0B1364B7E27894618CD56744620476BD4B409532E343F8CCFA524EBF21BAF204BBE07F2D156BCF21FD789C8224E0F6C68033E883A1F3258F7000CF7951F22493AC77581F7F9C05195AD406B
	E45A37E09DCDE125F64F008282867C92714D32EF0327D78E68977CCED9D742B5F8F95E85EC13A179A2F08486701B7BE5E3B77AAB996CDFG304470435D8B486F342241F8D1D7572E2AB703FEBBDAA215D7231F4B5858118175338E100F57DFC21C934AC72B094F57AEECB571E976B8D0FDD99B21BEB6540736CF077A29GCB81B2810AEA20FDEAA80EDE6826B8EA5EBCD72ADC76F14BB86A561F4935557078F157C4772729CC8E6ABA9D4AD28116G2C85A8BDC873C1DEDF2F60057A986332780D7A555604711955
	567D508CC5B5E36BF0D9BB8DD939D1E5784C977D495C3763495AE401636D22DFE67D966C3481788FD25B3CF8211C47FA239CD0A83A4FEA90180AB35CD2DC5F0BBE3B107186401BG3B810E093ED6426AA0CFC9FAC944642BGBBA5BE779E82B88C500131GA0839C8158E378C3E77C2701EE29G4B8124F7528BABGECGA100D9G0BG56826CB814DED88F6086188E1086308A2020B63DF09FC0B7C082C0F2AD356D3E81DB7678FE0B36014A23EDB15B3FC758EF4517ECBEA27C942B5DCE8B3FCCE4FE11A7D86F
	7F8A38EFBF945337945B31FC0B395DEFCDBE4E97BC8B05DFB74710CBED31C7320EFB02B7C5FBCD44165D7F8F7CE9865EC71F3F083ED306941F5E2C78951F93FE84C4598B9751CFE23E69D78D1ACC0677437C548493C240407F66A236012C6B1A27AF28AB1D0A2EFBFC2EF0F9CE974A8A847B7C61290DE495F6E15B2843B0BF99750B3BF0ECF728BE1F42868B03C414173239C571AA8320F4139A74699C7FF2B76661B71591B1377D2EFB3D04F313B70873A44FEDD3E5ECD73F669900A18827DA297CB04EBB5585BB
	CCBEDA827D9BF374CCF8969C2679E2788D1AAFF65D7A2234603ABF47AA87CA7A7CC7F25C077978BCFAB8BCDFF01DB6DD3256FD8B1B6384BE40F53D67927A41AA4F4E65B4E52FC417332914E59522CE2092F9B4AB7DF2502BB7C19E5F6274682A361FF86C2D5C88CFC8CA7C2A8FA49B78BDA3BBADF9A30B7398AE1A82CCF85D73256853D3B6559F74F7A83E60FEB1BF4C3D02364E849F2482ACBACC73433D17301C66EEB1B1B429DEAFEFEEA82B2D43B6F922EF3B492F2B8E73B65985E50BD879ED8715A740FBD29D
	AF47F7DE7E22D1DEFA84467CA361F24E5F65B473EF9765C97064B1F13E5F9C93A9A67A25C74816DB17CBB13615412082E999FA683491FF9F383CB52A6C2208C5DEFE4FE5741B571483359FC5390F7AE88D3866884FAB72G3F9D209660B540AFE7752629D4AFC914279E65B9482223DCCFDE2F5BCF75F69D8B17AFB155CBB80675DE9B24FAC70F713952AE6A3B61D905BEE27C169B75969F63BE49B84675140EF37D3C4731DE0AC6754A0E73FAB56259FA5C2C1F9220FAE922FC49F1EE4FEAF8561F00B9176977B9
	1D6A59CF70395BFB0267DA23A74C7A29C12A17F8026BB5AA6A4DB9E1566F789055DBF4024BDDA51E6BCEF07DF83DF5C63D6D75690515G6D753CDEE23D395E7CE1438E40AF3128476D70070CB5FEBD4FB98A9AB85F9D8D5C8EDE2FE1046A55B4703C243D017325FADC6E26DBD42F2701372B5E406526B41875FB42283726017BB72F11572BEFB4571BFE1B6ACDE964F978CCD1EFDEC43DD323C69CB4F2FE9922DEF913395E66BBD42F3B095773B7D955DBE254CB817CC2008CD1EFD733395E6F72281E9F7053G12
	1BA36B71717367FB307F32FD0C9B72109C2A8A8C433CD4D7F70A0D22FC2F6253EC3D0175D7B7531E44F7B76138D55E827D8D4056C2637F77B7531E440C1B49521C8FAD4B0D356762EE61FAA43604477F4CFF9E4E6563622FFAD066E2A8DB51C263C85E95733A28A190D09D9E36A493E345D72E1867870ECB3B02E3F73FC78B68EDB0A207E8DCF96C8A6A0BEBB601967E381BF7C8FA0F7BBA2679047BAB4FC1FBBBEB8D3F5CB9E9764B7C12BBB91BE278256224D80F1F8C7BE52663D739FC0DBD5BC17BD15320FC41
	C95A0FFA3B718D4BF69CB32B093D8AC7B569B37D5667255E0FBF90D31FF5C20F9D6F0675D9F8612050E707137431C17934F7C91F67C7A3622ACA9E566B6A0CA9145779DC6F71C9795ABC3F52FE5AE71C94238B8C9D566FA80B915BB48B73D83E5B0FB62C833A52D618838138ED7F4676B9309D6B81978260F3F91B0AB9146E7684789CF82C44BC8726C23D65GD9G25EDC4B32822CE83ACCAE83F68BF9B6AE9ECF8E4361436A1B147EA7F4E7DD0D2CEC59B773D2DAD5C86360C15B9CEG2E6B1755A2E17FAC2833
	5C103B430F716F88B73F48C79A7DE48357E56EEBE6DD8A6429163AA47E5815D3FACA648727423AACDE36B2E755B2526583FE72DB8254C9BBC57EFCF71070EB81D7F11A5AE97BF1FC329CD4A27D968C22DE8E8A9843E72317C84F044573249D5B6E3554932177B457EF5A69301EC94F2D4CC9F90E744C56C81F65D027341DFC6656F04C9AB0471D215747AC4BB83FE59A35C1AB70CAB0789DE5FB6A8A789F23175B5C79AE6ED78559FED10BA841B12462721E0BE46B6CED66B6B9FA771A252D53CA5CB9E96D5C46D5
	6DE1DB1D509EA123CD7EB6C03AD5F62497BABBC837DFB03C476751BD3257F3CB29584375BB500EEB6300220B81047ADBC5BB6B4EDE59672AE2AB1CA36D263ED5456C289A53CB0F00D13C1F3E255358B833036262F11D5AE1A9600A4E10EE7B0398233266BA405FAF85A9E6B9DDD210681A01E65420FBE3681568B6B20D511C56B5C47E1887F4EB8C5AA7072C7D717439E2960FBEDDD33D183ED6E0BBF1DE2F8F932FDDE7538BFBGF86EDC67B2674EB34E727D2E79E7C95634E1BCC310D9C2DF459FB6C56BD73581
	45D9C123E695DBFFD6B53062967C3D07662A13FFFE0D69649FC1DF874CCD53896BCC1F6BC8D847FFB7F4DC82728BEC24E31E5BCA47FA9B354DA7C364E3BB6092ED243BB4A25AE6BFFF2F9859EC58025B37324FE9740B9BA344E3A150E799BC3EEF706075EAEE110EB567A0EFBDC775BEF58B4F9E7AD5C7B0D0457EEFD783FA3403CEE2C124FF3EF47E00F98C59FF7D24C15C9E135A6433DF9A7A4C81DDD6987ADC3D0DF965009A8CA8D56C7F7310F34801310D56010511BA8D65E2FEF1034563C9CFC8F1DE6F0860
	902DD81885AA4675FA74B669357DBC643B67C92F72D16B589DB9330975071BCA236AF5D698FC822344E78A7018EF70F9F6F4EF24FDB6CD8944B3CF5837674F38F794F02B4346A4BBA11B5E39C33AE4029E151D244B276FCC46571C675DDC626984FEB3BBA9669F370C7925064C9F657E6F7A655DE25CF33C013637694A008D12CC65FAC3941FE8271898319094B6B465118D2BC13FF2BB59B043E3E5C3371DECF8B8EF32B670FD4559E34E403C7670FEFA0269B781BF9BBAD6047A4E30733321FC835F4A7049A6BC
	376F29CDE45FFCA8AB3FC067992F76E2CEB5855EE7812481AC81D88BD0FA115683A7FB2A25B867C5EDBE9D168B3237B3E2E30D4EEDFA50D7B7159171AEDDB567A15C8F050E714EA1ABAF0E7583577F43B236CFBD1477G68G09GA9066E9F3D9AD7771610256EDC2F64F1753282CE773577CC476DD94D761996037E2B8124AED8DF8354F611BDAB2ED64633074E6304A1DC1F571C133323F212F48FDA52F5F70935F3D72C387EB9DBA3A4F47178ADB470120B6D5F1970BC2E27327A781E86E5E55DF496325A97CF
	6F046EC89DB85DAF63524D0922936B7B406FA4739A0BAD29CE05279FC96C6DA6941957B0733B791E46EAFC82705CADD8C2395B76CB348F70E4A95A598E6FCE0051CB54FF0FBBF16DB5835ED3819681E4G94DCCEAF5C8550FA1962662DEB0D71622601B64D6D2A7133A9FA631B7C74F5B55ACF3D17A37DE26D5FFF9AC377DA547BE8949FEE67D794E6A7142582AC8248GA83882F68234DEA1BBC72F9D09E7A79B0041C611C3C59BA84EFB7C130B1F0F4725F3DF31226B981C1C3CAB71692ED879335A057ECC0232
	F9G0B81D683947540FA91203D077C79E47C38B10E71A792B79B34495979D2DCBAE70F955D8F4650CDACCEFFBC06EECC1C76D879B5594D62944AD2819681E4G94DC05B885E83DCAFEFD7D7A318946E99360DAD5752605CB052EB3821333F176983A2A68D83DEAC577A79D69922E0E9D43E38127DB22CF2E6D7FA5AE5D8CCBBDBF931C1C3C1438F4A916728606A6578E374750C53743C2CBF93B07A7A76F60983AE8F9AB2EDA4DF10B72B1F6AD7679A88397FB8F03AE1A3FB22F52FC7C53C96A7A6B383AD6DE0B24
	63BA9CF1138E4D57C8074F06A6978FF363520DDE3322730FCCCE5E1F63524D3014D7F7EBF2BEEE9DC397BD5625D84A4B1A243C0D376349DBE8A96FDBE368A6A66F09B1F4E3625DD25E48686464FDE88CDD34FD1916720E5E191C3CD3E368226595DD37221B1ABBB9F99F9BC397ADEF57F52BBE196D21BEF974BA7549363C496950B506AEDA875DD2077B3C24C3A21473FBFE0F32F33705703E5C405D3F91F1D9703E3D17F4EDAAC2DCAD3C5B8C5C990643F3883701DB3629BBCA97822445272DF762F3E7B4F099AF
	4414C1771172A7499F0167A8FF6C0DFDAFE7EF2F753D1C6ADE6BFBB907FB37F0BC70B05FAA2572065E716F6D3475DA5D5B61EDD54E4E812701CFE776D2FE75B1B9366E8F48563A4F122DF57F02BC3E6E0F48636B3E36AF36AE3F6A3356653F7A2CF549F50C2F4BA6C7ACDD382F3E414E6FD241CF8BFAA956F7AA946B0B0D987EF5BF5EA5D98B6FC5B22C89G1AE5AADB6F4232CAF12FG4F46715C1B17FD389F6392E91096E96245DA45846AC4467E9CA6A3DF447AC46E0DEE18A0FF04D7995F67F53C43B7665E57
	99D869C8F4C635D3AD94F5EC3AEABBE86864EAC19E96F9D227C3D33DDEBC7117E8BCCAE363876A6714E92DBF2D186DF5407BBC0045065FEF150C1797E4D878116F1DB4EEE1672EB25FBB49B6702799BE2FAF0C67FBAACBCB692C39944A74BE6AD32D4E58F1FC56E99D47971CF8DE91D0031AC3090E559E678E23AC3C3A41C4925D4C6BA80CDE17B517B2A6563D0C1E1745228FBAF1CF3E4F6374E862C697EF5F6F3EDC427AD1A020707308E6D917C53B3EE034EB0B2EF69CB064FDD309ED7FC2455A7E47155876FF
	DF09EF7F33CAEC7BFF2844377FA7CA2C71005B7A185FECEB35011FB23809E23BDFD387EC7B054CE32E58BEE8F6D97B6014AB368F4E39627B6032AB368FE4D7FC9F5CF0C547G377138E66E3F6F9836EF8D18FC52345F1075F76E5836BF6A36367DDB6E5836FF479D5F76275C31EDFF469D5F76FFF50F5F7E5FB35B5A013672B961A4BBBFDE8EFDFFF59F1DF5EC88C646448141E3D9506C477D866FB9C3E6BF9EB07057064D789A4978CE42E5B53E2D6B0BBDBFF0FEFF8819799DB478553B2D7895B96271FBA694E9
	E70D40BFB49209BFA87003374C7243F1D4FD5B0C2FB5703F0C409FB2702923E6FC38AF866E1871078D7C7C5C0D66B85DC7F62F77D859DD9E576E043C88FE7B095F7DDEABFEB5F179ADA940BB4FDEF9C48D6A12B44F01F1753CF13739955EFB8126814CF470FDF0BEFFFEF9987D5C2F4983902A1DFE45A1CDF7A26D5C0DC41B053CG378F408660F7D20C7E6C9D3441BC51F27CAB9B583D60F639CF715ABD3AD79177EEBF3045FC3E375C497548B2789DF2186F1414ABC8F34638F7D62A98E76CCEF6E78286G2702
	5FE52E45A1C0687D0EC2FAA7C359FC0095GD9GE57DC42F326FG0C1D14A655BFE278F14D41526832862FD74CD5BB0B4DE56C8C14682E3F34E5CC99909A7D631BC5542EC7C18FDBFFE4FBBA7BE36593D9ACBF8975733CA157402FAFC17CB493DE5C09A926FB89C9D01655CFF967FDA514F7763AA847E9C51505FE67CC3F1FAD0D355E79CBG17717377D50638532053DD0C19AEF6B7003DCFF111CF6EE5FBE72B60FD2D016BF293AE4B403D4A5652856E74429DEE424DE5EB5BDD70DE6326FBA5A505144B5583AE
	6426BE76232A083DBCFEEF30D1457B6F6D3C43727C342B30C050273AA9BF1D4AF82E06F7498372BDF477B274C73A6B18720559D252DFDB5FC9DDE53F13334F43E34B6E895F231A62DB1943E5BE3D11E4CE0772C59E36F730E9FB14AFB76770FD9CDEDE9DD10E01F30FE4AED7A24AD15FBD396672CFC414230F2A85BD376DFBEF12EDD95FCB153CDF1D92739B67529B5C269A711C1E71496C545D37F345B9E1FF0CFB40ECE492EB02AD85C22E77860D8F0A030DAFDAD0617A4C9AB6FFF3DD3B712FAC3F7FC018A374
	C8934F2C179E4866BC1E3AE266D17A00B7A7BB860F35C4FBA35CDEDC579E055AEA5FCDEA2B3FD1225B6A750836182FC4375539ECF3798BCAF4DB5D97D1DE5E9F5DD63B73F8B96F87CB591A4A8D7AA45E243E715CE6444DC29D8D5C31E29AFFD65C2471A727847DDC602577A2760E774445D9CB71DFEA0CC0995663505C3F180CDC8B9C1F13120D3B346520C78D009DC087186125183B075D97BC5BF783924BAA87EEC3086F6195F35C6C3B5D1E139BA3EE66899B97FA4331A7F2C3D764FC994034D6623F6F4774D4
	0C17B51E48FA0F6A6132055E483262A1497061CB05133B377046A469FA5EB39D1FB33BB6220FB33D51EB73DE0617862257669FB64673A2A8738F50BD00970730BD17427BDA00829F44B8C0BDC00F0F566F077C10A20EFF66E8D77D6D3040711A361638FD4BB7CE6E6F84FCFB830D1B733E71301476416618775F7DBEB1DE0AE745F31F4C46A737EFCA886DDBGE5ABGA495669D00CA00E655781E5BAF6E4660C55F86DD179DEE760502F1B146238C075118D80ABCE80B5936EED55CD1914F84711C21067BAB5769
	21919CBF12813F505061BBFE3C17F7D13EA13C4BEF03B38DGE953D43F2269A39C4B3E40923CE65FC2EFF19EC503EF466281D61C02E90377C93B6CB262CFD65EEB7B70BE7453A23C43BB84CAF99C3217674D0D32E6D33D9E470844735D75EC6C443B0799AA0D8D3F590CFA3BD54DF3CB7569E3A9A5C17BB29BE3CFG6C72C37E61C71C3D38FC9C3D1CFE6EE7AE2761B04B5BCD97E9783C73E409719D021F661D75255173CE643CD1DB9ABD6FE09C064BEF140EBF6F4CAC0D1EF7081E37E162ED9C0F5679A996EC03
	FF98A79670DC1FDF9F26B63C0ACB97713B5F747B3E4277BF77FD78FD15A13E4D9D0A3DFF12BDEC3DFF32E158FA9FFC737078FB6EAF8D477E4E37EC587C1DAFEF3B4F0EE25B968D2697D6G34837482680354168F8FE3DB72DBD25163CA0B53057BB77857B0F412FFEFA8362DB3C2717F064627C398GFD2A2E2B83481F708F06303DF4554F109C174C76BFB40F4B2D732AF4AE9142BE62D57A99DA64C6AC371C8EF64D9924BCA83397F10B60FD39013BA4D3EE14E160BE4AF0259A2C3BB572CB4AC831399D7810C6
	5F090D20CF0DFB526C93B29E713CEFBF3D9977FE4C1FB518F6482BAE361C3737B5B534DF6FECBAFF363D3D31617C75064E6BE7DADA1ADB1A45774245874621EFBDFB3E6D7259B376888EE7DA00AD0DAD034538BF5124605DD51A2F06951A2F4A3E7420D43633BE0746096818EE5678B8655742B9F138BFBD9D31D7BF876A24E9631FF3F01FCD3F0DBA85443840F18F0D460E25C7C663475257C7799E06CB310F78453E607CD1142376778794A3E63FBDDAEA605A82B6B56041969373441675AC9EB536814977F2F2
	36D28E3998EC4BGA88D24976E8A7033D0CE37C90E4C51765EC5AF73F940AF170F630F238173BC70C5993FC98C4FB78DDE0F4B073DD04CB37FA853BA4496201CE82B047C9360FDA640820065GEBG72F4489FG766994471DF7AA2458E3B5DF9833BE2E499E1F4E7B43CF589C548EBCF01E60381FB11CD360445F6B59EAFC278578B41D56601B0A714CBA6A4EE84B00DF9F61729A9559C767C45AD663DBC71D56618D3D4837A288F99EC0F710FCFE2F0C7C4F281A6EB67C772C7CFAD4DF69447FE9588AD4B5351C
	31E3579031702A94BB66F6C971E3EE2FE4BDFED5CBE3472FDA49FA7CBAAE1947AF0EEB92FCB563B0CE1C8F337DB33E5758A17BE4979B5B391F65BF1A3E576872B8F5B7471705B607712D8A4AB77A6F3E2D7172A9FF506ADE427138F4517794B85D4AEDC817981C78D9A8DFA7FC1545402CE0747A61B106CF89C62F9F5E6D2575C39A14158D2597727330855B38AF3C9E1F52447FCA13C4B11D398D5BA1816A4E9822F5705F77CA066E8BCD3FBBFA5F7FDC257FFD15A1462D6D6B19BFE0A88C503A749B68B6097F2D
	3ECF957C907D7B9FD23EAE6D0C7327A9F4A98F77ECF529G9F658DDE2FAE65335DD8DDAAE4CF0EEB46A50927FF245C277A76BA79770065FE3ADDDB4E7FF8C8392A15879C322FDC850165F239A67BE0EED77BC5F19DAC24CB0C6FCE5A187E2B062A6BF21FFBB0B7C74AEB135673BE59A665E147EA1372D93FEB13F221CF0670EFED3144D37C435FFCDE5CF3573C66DFB75591F33F69E8C85CF1890567C5BE9EDFAB43B8CA84FCF208465DC7FE0F5FC045DA6BF48E48609859FB33416744F5CF63082E04F3BCC79972
	8FD09DD1A84E7C4AD06F9BA61A7F81D0CB8788177798A5F59AGG40D1GGD0CB818294G94G88G88GADFBB0B6177798A5F59AGG40D1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2F9AGGGG
**end of data**/
}
}