package cbit.vcell.solver.ode.gui;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import org.vcell.expression.IExpression;
import org.vcell.expression.SymbolTableEntry;
import org.vcell.expression.ui.ResolvedValuesSelection;

import cbit.vcell.math.Constant;
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
	private cbit.vcell.simulation.MathOverrides fieldMathOverrides = null;
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
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == MathOverridesPanel.this.getComponent1()) 
				connEtoC6(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == MathOverridesPanel.this.getJTableFixed()) 
				connEtoC3(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == MathOverridesPanel.this.getJTableFixed()) 
				connEtoC8(e);
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
 * connEtoC3:  (JTableFixed.mouse.mouseClicked(java.awt.event.MouseEvent) --> MathOverridesPanel.showPopupMenu(Ljava.awt.event.MouseEvent;Ljavax.swing.JPopupMenu;)V)
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
			java.util.Vector<Constant> primarySymbolTableEntriesV = new java.util.Vector<Constant>();
			java.util.Vector<IExpression> resolvedValuesV = new java.util.Vector<IExpression>();
			//java.util.Vector selectedNamesV = new java.util.Vector();
			for(int i=0;i<rows.length;i+= 1){
				String rowName = (String)getJTableFixed().getValueAt(rows[i],MathOverridesTableModel.COLUMN_PARAMETER);
				primarySymbolTableEntriesV.add(getMathOverrides().getConstant(rowName));
				resolvedValuesV.add(getMathOverrides().getActualExpression(rowName,0));
				
			}
			ResolvedValuesSelection rvs = new ResolvedValuesSelection(
					(SymbolTableEntry[])cbit.util.BeanUtils.getArray(primarySymbolTableEntriesV,SymbolTableEntry.class),
					null,
					(IExpression[])cbit.util.BeanUtils.getArray(resolvedValuesV,IExpression.class),
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
public cbit.vcell.simulation.MathOverrides getMathOverrides() {
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
 * Insert the method's description here.
 * Creation date: (4/20/2001 4:52:52 PM)
 * @param actionCommand java.lang.String
 * @return java.lang.String
 */
private synchronized String getTabDelimitedData(String actionCommand) {
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
	return buffer.toString();
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
	getJTableFixed().addMouseListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopyAll().addActionListener(ivjEventHandler);
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


	java.util.Vector<String> pasteDescriptionsV = new java.util.Vector<String>();
	java.util.Vector<IExpression> newConstantsV = new java.util.Vector<IExpression>();
	java.util.Vector<String> changedParameterNamesV = new java.util.Vector<String>();
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
			if(pasteThis instanceof ResolvedValuesSelection){
				ResolvedValuesSelection rvs = (ResolvedValuesSelection)pasteThis;

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
							cbit.gui.DialogUtils.formatPasteList(
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
public void newSimulation(cbit.vcell.simulation.Simulation simulation) {
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
public void setMathOverrides(cbit.vcell.simulation.MathOverrides mathOverrides) {
	cbit.vcell.simulation.MathOverrides oldValue = fieldMathOverrides;
	fieldMathOverrides = mathOverrides;
	firePropertyChange("mathOverrides", oldValue, mathOverrides);
}


/**
 * Comment
 */
private void showPopupMenu(MouseEvent mouseEvent) {
	if (mouseEvent.isPopupTrigger()) {
		Object obj = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);
		boolean bPaste = (obj instanceof ResolvedValuesSelection);

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


}