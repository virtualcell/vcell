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
	private cbit.vcell.simulation.MathOverrides fieldMathOverrides = null;
	private boolean ivjConnPtoP6Aligning = false;
	private MathOverridesTableCellRenderer ivjMathOverridesTableCellRenderer1 = null;
	private JMenuItem ivjJMenuItemCopy = null;
	private JMenuItem ivjJMenuItemCopyAll = null;
	private JPopupMenu ivjJPopupMenu1 = null;
	private JLabel ivjJLabelTitle = null;

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
		this.showPopupMenu(arg1, getJPopupMenu1());
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
 * Comment
 */
private void copyCells(String actionCommand) {
	cbit.vcell.desktop.VCellTransferable.sendToClipboard(getTabDelimitedData(actionCommand));
	//StringSelection ss = new StringSelection(getTabDelimitedData(actionCommand));
	//Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	//clipboard.setContents(ss, ss);
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
private void showPopupMenu(MouseEvent mouseEvent, javax.swing.JPopupMenu menu) {
	if (SwingUtilities.isRightMouseButton(mouseEvent)) {
		menu.show(getJTableFixed(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
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
	D0CB838494G88G88G3B0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BC8BF4D45535D183840D9AA5EAC05400C183C49AA5C82CD463D3DF73AC3E5216AAEBB5FD0BD571D52DD57ACAED5A326A4D8F226193CB1ED28990B0E8A0E14C8F21B1DAC2BEA6B0A443A48C6123494D4CCDE6E0B2F71C39A189C47376B97B1CBBF7A6F7B218272B2C3539F377B97BFB76B9E71F4F0DD030B3B6A7A333C090B2F284766FE006A0180E8B423C5ED1116322DC16BCC173EF9740DB02E04D06
	E7AC1465BEEC494B97DE4C4C0577A4F8AF7B3EA56F9D783DC5F86E3873987C88A227814AAE9FFDEC4178F2863E66F256537746368D705C84108CB8724C16097C4F5B36B2FC9A43F348489404D5344ED0DBA143B543FBB7C0ACC0120C751742738DAAF9D24DA1D66F5CCD71421A27BC199B899F8DCF8EE5EE24B531E71170670CF4B711B50B6AC9E8A260BD8500981FA57C6E4DCAF8561AAE74FDEA6DF1A8060BD64969B4F8E567C549E310ED1221EF40D1D3F3DCD46CED0071B8EC1257A43AA427A0A8DFD2BF3765
	D2BE40835E858F41DDD5C8E4A5437BB20097D4FC5BFB845FG3F4F81A89EEE5FA79F54B27DBBAFAF9636FA1FFCE3F3907BA2BDE8D71247E71F3334A76B6F34AD960D921F2F0632822FA52F8440886084088458C6E3E049577B60D95723544B7D7D322B4E6DBE6234F90E0325CE78DDD383A58CE7162C3247A688584E7736156968B39EA05D22EFCC07BE092F62ED1EE0DEA024AD3CB5F33D0ECF5262AFE746F95197CB3CBE1F206CD7024ACE737A47243F6CD302D63670E2657A9C9D59054FD42EDF4D78E4ABBE59
	4D8AE96BBF5198A8065F040E44AB62F33793FC24860F71B90556A76F09D0D6B840E323F393574518BDDB482F38CD578FB17F961999B100BA448E846AE2ADA63C178E204C8FD4DDC6A93E78228F0F3A1C24F1CA5EC720ACF95012077175C886692FEB06204EB0449740E100EE00910028E19EE3B9239FDEE10C19C40FF2E4406134C99E81E99F99FD9B1E52106291DBDCDEC5F4D9A50B68F108BDCE49AB30BABF9CA5BE687123DBCAE35F8AB2BE13DC12C7D49C328B380B7D12A2F93CD8676723EF11BA9E0916F6B8
	7AA590506F96B85FDF0EEE0127D374AAA75DB6D111FCB5307C79D152A697C58BF08486709B7D721BCC622FC430BF95E0056A07EE0170BBA6F9E03C2829B14A720581F78731C4A839C47C1CE3A2E3474BD0BF7B9277F1F98341B961BD7C9267F31B70B78D1F86071528AFFA06F99FFB3D10589E8375138152G568294DC0676394C636815CE9EC78D9B6F934AFEFE0BEE9CC5C1CCF7DE4678F1DF8E6CCF1B3693B9C45798A8CB86D881108E30F5044F875797F673183C646BE3E96CB7516F70E59FFE6DA57DFE2805
	024BE36B202CF8D5D6D6C099F966307E246D5B21E465696030DD6EECA4EDDC8BF6F6828C0D703679A97807467A9151AB7168EE73C0E0CAB6DFCB213E0EE2A313719E409E5647BE81A88558CC6B88B4E67E97609DA61B48598E30D340B978A040BFG0E2BFE18C479C601CE49GAB81F23F32641583F48204814483AC83C887A87A5A12578C60860881C881F881A0FF5412F78820FB14376559774AB3467753156A2C35F79333D70FAF37ED93734BF8EDF57C8A5A73DF81683BF9C1FD47E375CAFD1743F872785D98
	C4AE77F130B8564287A14A7FD500FDAAB50378C63AA839148649AD39EC70434B4AFE1CC17C406607DED92579B9453BE1BE2907815C2B62FF11C1FCACAA0A4751B320C86D1222B8DCFD3E726728AC6FC00F5BB795F1D9AF5184D99E0479C42D7FEA86990B2D324BA5514E2D1230725F5139C0F2CA7D20F43DBC60D2907F47CC12B7DF100659DC74174CF58219CB1C83E4DE43DCB4D1A46D6676B87AE148A0D32330E2144C9305262D9A9FC5G0E64548970CC9965637B60FBFCFCAF3EB4D7C869F2E76A65ACB1F3EE
	4CAC8A437134BA4CB73E23CE879A74F57F184EC98C6FC5DD271EA6FE504B0B33A84DDB056F841653290FE6812C0E370C70E810FA458127D28FF9F7234D21481E8A4EE3FF591B7004A442AD3BC0320A5FBB3CD317B7E1E1A6616211008956B567931F9EB5496E8177F149B5D0414673E71B082D9D6083BBC0D8989F4F97B710F2BE5732013CDEF6BA313921ECF598E9934B76822DDF4330CD52882F2BC8F9768EDEFE885ECDD7E1B9F94772CBEAF9AA3C2F5414A37F55BB347C332F46F293BCB1A6FED1240D895B55
	BE7A482B39ADF10D79A43625AF87A4C82308070E2971B73F71EF81E596960BD83E3011784D2949D9322FA1F2D30B791AAD6EEA4C831640F3A940EA009D000F2275BEAF6175CC57E0397D9A4C99422EC1BD315E7E3D3CDE1A26FC05265E21F0D26F77EA3DB557609C37A19C6B974153C0FCC4795D370F578B8FC71F4C8CE779CAB86AD7BC092E51CAF93D652C5E4B6CA9CC526AD7D3466B39A7E1F944A434A7961E69G285F457D3C5E0649B86796CF465C28FA32D63F86832F57B099752AE6754ECD566A3765C3DE
	AFECB24A0DE14F4449289F56FBDA2D37F4B26A350A55AB1A2275DFD439EA47944C81A226E05E96B38575C3FEDFD470FA7100CF81D8B905710B504A5DDD496B9981EF0B98DB8F65BE23568B0F401CE3E68472CD08C039D86F5EAADEAFB5827312150CAF2F07F23F2C6675D6C7E01C64CEC5390727EA752BBA207AEFAA7AA50655DB6ED76FB5355E56EBB18FAF3F966BB5DF0B75F03C7B7DDE525F68BE41F97122B8E4708E42BCD2D3F3140EFA3897B019667324FE7735FC4D7FC0BB99E7E260BD91A075DABEDE7FE4
	A3DF73C7DF089752DE2CD89FECCFE3A553A37BBA5FF83DE6F55DBA9C47F6B593198DD056F19D6F7706A66D3A234E6B152D8E3A64616B1CA66DF80E38DFB5113136576171AAADB0028E71F16005A622AFD993B1B47347E876C1619B6E1BE846FF7457755B795E097DBA6E172F1A34FED932792B4C3CA0FE09E57EC85178E5255C319E573025BB78FE0F9089FDB4126F77149EF9CF379D474CC2ECAF40DA4575093AB4C7683EE5C6D0FD5C11EC5C087469D376F5FA8E5BFFD275C905F261FA2E4F2FC77C624AA08EAA
	B5B56A1407F5E6F5FFBAA1DFEB67C33EDF7541CE9E2307D49D56EDAD8C925BFC5644D84E5DC5ECF083DD84C0ACG5A76BB3A0FC05AB199F02B81F06E6D5CC3F29E456E70621C757EA96D1CD5F2834CE7G8AC0448D1C2629046854CF23046F477CCF5DE1BE46AD18ADA42F0F4C545B9F335F9B1399FB837ABE7586DF9B044D6E490A86C0DDA717ECE076EF3D91645F48657E3604443F55577CACFFF8E7AF3781F539EF07D617DCF1122EAE11AF76E5C65C08BAA45E6853E56D341E2C7CE82E0BE5AF77DBBA54A90F
	627EAC5C4771F64045C671F6AA7814BCE94EA8F07DEE59C774327200D1FDB6F21A6B9936B4C9583A65FADDBD892C08C27D041BFCFA264E6A49CA1B4575FCC6554784F5A2EE62BE7B24140CD97D5A38D375326B16A13FF9253C8DD200D73A4A6F2DB2BAAE017FC974224D6D1F117D20813A9F5348CA4898D2F2E62F055BBAFB0B36CDEAC73F5035B5E233BD337CE6343173E61F2D5150A67153382D56B22EDB9C54C93B196B769445BBDC8E45A1BA9D1724123D28DF2511EC29FBA5058DA03C3F1598E9F7F60A2EBE
	83DD11FCEC5474AD833543B02617FE8CC6E1BFFD3D0C0F0D8553F8DC5C351F370391F0615338EE6B8CA4C6C5CFDFA53E9B8CBCE6112E5F406916G4D9A15AE5D70CEE09BE946E8242D7D107B23AC9A6663E8CEFB77077A7E785909CDB49EDD0AC7F612F43304349372FACD65B59378ACD7F9FDDE3E1F7679812FE4207F57BB9D568B122D040F08863A96B49CA7E50D6467DE014FC3973C75447FF6F9D09D2C186B75687C9B41B73E913F230E8F15F3DFED38451257F08B573128425F474D957EBE1ED241ED9B811A
	A4G4C639F5D254D63DF3E85F32E02DBB95FD7AA487903C8979786FC18A4CF2F6C699FEBFF9DAD668EC07B1B3E5EC17D6D9EA6F1645506DDBB2CF9DDFD9F7B7CFF2C126BD882723BEF65BACE5D2D27E34C2DBC6EDE2F64BEC9G5CAAD5775EAA66138AFC3F21EA23EA8B597A95DDB6354FAF2B62BC8AEF03DC65B64E63E0B5670175E6D4F39DA3214E93EA3D0C83645C22D7368EF88D74FF236CD5829D54C48A7C7DB35FDC299D9FAB3A1AD46240B071D1EBFCF66EG57E7AD68D29D4375F97C0041BFE64D10CF09
	5E90C10BBADD4CA23953F95A571A9CC3122D6B389F07F162D70D531D9FF13D1C20D31C2A57640F747B65F0EB9E6D6B9724A33253C6BAA56ED3DDE78ABCA3C90B716D372E0E993B770AE7876FF790B95953716CA0C745D7D07CD68D9E75CDEED74791A80B1A4E773BEB0F111CE08D3C674F306415G9C866886881B4173D977E7D5C2087304961782692E68EC775B48E1B96C7B4D44B79724E1763E2FD9BB0F229FEE5EBE5EB9554C99E37D007A4FB3123E138865CB81D6812C85A83C1D6BFEFFF3C85D9B07F4F5C7
	3DAAF6CC6C5C6D1F5F188E6DD95FCA6C29857DBB815CG91G712ABDFFE9AE8BE58FBF2FE106307343895A31E802F4E5BB7568D2EF47F6CC3BBDD8DC0F509CF7756D01F1BD056233E786467526DDEABE89E5A3B3795E799F0BC3693DF2263F8EC81733A794DD769D7EF4ECFD6AFDD7502E9168126028EF089158DE2223C31B03975E01EB5EE6F81E83405C630BD3BC77D06E606B58451D444E84F8FF82A06D8E5EFFBF27E778EB60BD7FCE683FG8DGF600F00078BBF95C34349C8995B7F5FC1336C3D6FF5687EE
	34F2BFC516847AA965CEFF3F687BF746983A9D816FE981FC504E1F1A281DD016FF97588950GE0878887083F0B5BF9FB4B47216C24A3B958486649C083D14FF3FBA796BF67C352255E25C737FB5F44641507263BCB4F1FE6B371E7BA149544DA724AG1A811CG91G89315C1F3F8D9DB76AB16F1544CD5A846DFCA9A45D9331FAF40925930B53C746500D0953D8BD3F7E301D46A914654F02B885E8G30830483444F62FEBDFC7413AB0C53FA40B54B72859D17B2DD9DE59333F1F08C1DA1B0D6E76951E56FA7F4
	ABE70D9D430381524D59BF3136DF9812EE0D2E1EDE434464DD9512EE43ECBD3AA39FCE2C9DCCE368825B21D8D75E5A894AFB33BCD4EC576A4AFBF48C5D1579736910F49D3A722E2F1818FD370F218B7427F3365E9C1E1AC37A264EBE9CD92236A8D2BF5B9BF8ED971F1F43E773FC63C46544F46D9AC397286B12GDDD107145DDC07652A8EB9D593EB1FADA169B65C2DC7774C84652D8AC9D72CABAF36FAE2BEDEB006EECC3C6B4A6BB9B0B1F96EB1F40172BAF4656D79E8E2722A46500D0977GF9F87FE9B6BDAF
	89073298G440D1A89EE893CAF3F1B47D5D987412543FBFE9C479D24B832FFDC22629EE96F8C521E69CCBE219F2F5D47831471ECD0997CDEC74E84799340393971D8707B8F46E37A779F4C47746FBFF49EFB8B7140C3FB5B0E17F79F9B7FFE047D185E7D88EC2B39747C2699FCFAAE0E67958F98036B7E10D1DF77C70D7A3ABFE99CDF77270D636B3E39B538AEEF376A6BD2582A2F4B6E5671F559579ACC9774D5BDBDF7F1031F4262F82C476F64319E29467055F4ECCF047725AA6EFA0AA3E72664BC94F1853BC8
	9C12BA097F0F189EBF5677D399B91A587EB6E1BB657BEB055CFD9AF31F2695B2FA011FA598763C416A189459F4C055292F11705019AF5B2D9E5969A4A733821F2FE65171025875C29CDF53FEE9A1EB5A22B9163CC3G26B95C3756D3634D8F76B941768ECACF93FEA3F3F00FA0C345D7D3FC04860FFB87BF6864E70251D016BE0777213E3660F16BEA530FDB250D6C3DFA65810FD58A0C4D4B74AEBE167932F812D090B7E31DF0CA2F081EBEE9CCECDFC76931AC98FDF49B59DF6CF1589C9EF6D3865B77BF5E5ECC
	7B0D57AB615EEA0328082CDD9FAF616D5A284847ABD5F9E9266076BFE752377FC5D3F07BD71BC25B7FA7D3F07B0593216DFF63C4307E0F36CE5B2B3535CA451F5E1747E33B57A3771BAA184CF36660BE301A75FD60B08777015B9C5A87436660BE88B3077641D4F3E08C200DDBCA357D77A35576DF17EAFCD2DF214A7AC9FBF05BD7346B5B7EDF6D41EDFF3EBD346D2F3487377D8F6D21ED7FEB7B786D9FDB26EDFFE2AB4E815B7613F1695CBD16BC67BDFC8FBFED3FFFCCD4B29E738CDABFD6283C07BF547A31D2
	45D715EB713E18FB2D5C1FFF9543BFD1212DDFAD70F960AF3B74464311FB024FBB48AF22D24B6F004A6F495DFA7C22C2722B2D72573B1A61B7D67B638FB07C3355DA793E381BFEC00BBF24624FFD24459FD4718F1948DDCD27B8AC8FA810C35ECB4F3354BB19F120779200D5G697760FEAA4ECF778F923DFABDE2BF04C23BDB328AAFD35ADB4D1C36935E1DG11G89GCB6F65B1706AC764BE02F6A2E3779156537B0CC631C7F2F6B89427446E8B1EBF2DBD432B0EC7BDBA63B93F146D5A337511F8C2732CFA7F
	462956BB33131EEBC287CB09A75FFFEDBE8B2FCC6FC7F7F23D3367DA7236829C826884981A4B69E5FABFD9DD31564B6EE1550FAF9F488FAC2BF3BAB1A66E6960B191833C92667A47C24A5CE0F36E73F48EDFBE976756AC957FAA452F5260F14E9D3470F348B5C477F9BC973BF40A67E2CB6671BC2062342046EC0D6677631D4156G5FDE6E15761D4A4076285C2F1E3D4EA3BEE8266FABD51F7C0D2E71EB67DB720C73B9AECD45F54E67674369A61E4758GB7F3BE0F7F7FB6786DE7605D26A3B239D3EB444E0439
	596193390CFE79FC1E1B39CE901E9B92ACF945G35897CFE58507B7CBED642BD3305E54F6B5F1BDB5971D5E6C782464CD00266AED0E9CF964ADCE266B213207C658402FB49DC9060230D193816457242F6EDB9996F278A5A72D63FF2226F5EACEDF918DFB971D19523C75BFE34075B3676C70902F3D1F8506F9C2B6FE377D8583368BF27E718261B563133205EA0F79569284172615339CCEE54FD6ADD046D346FFB86A454A7F6D07B5D6521B72F563DD3CEA0156911461E112FCF4FC09EBFBE2B6511BF43191991
	04C74182E64B82DFFB212E4F6F64ED5531003755162240365A6557960DC501EDF5A2C3DBBED89458D6B779154FAD8AEC2B8F32319C7B418F68FAA29274C9DC407B46521384778451D1453DEF61630A70BDBE2E7C46C27CDC263E7F013E13BB2A787E15655B6A7BC4067E78F26B37A6C3EFED087345CD6AFD3F86502393E088A086A079FBBC663E20F71A5AFA4EC3D2E53012A5B77BA6D65246CD79E5E3E6D61038D941ECFC61FB3E58E35F896C761F4BEE7E1CF0465F0B3E90D4DF7CF54C9D08D000F49BCECC0C2E
	719B53611CF658CC6FD6A4862EAFDBA83EB8B1F0FD291C6063F2B914C5A772B35B18AE52AE6970DEF03FA52F8C2081408690F6BFDF03DA4E168BA14E07BAE4379112F427E6BB846DFB4CBC31EFFE9FDB4F473F246F4F9312A7B7843DEB9BFDBF7B8E1ABD1317CE4FA0CF342FD4A4762D02B2618118BFGEA81CCG768738FDF71EE57798482542BAC5912DF6FA9BDA3D44601086FDE8123C31BC653456361187581DDF760CE34F6487FC7D8EF5EA6DA16340D2403F206A707CD9729D1CC5BC4F3C0BB7CF298600B4
	F9E43764D10691CB3F4EE03CDA2C44DB4823745C1EE0BC400A23B07CE35F6A907B5478FBC977EEC428BE3EC5D85EC16E8BDE84259CD6510939699151E3121D8E6B3000B9652BF4DCA47773F29772BE7E5CC92237DD76B8AE49AEE5AC2540E8DF276367BFC9DC836D4C0584770925E89C3DA296221FD1CE5DBFE8EE2C397400734762D37C4E732A05FC7EF875D4607C61BF7E6F6A8C1CBFC89C7A4A5BBB471FBFA6F5864E9F1C9E5B304AC6462642A49E8B72395D634482B903456BFC3C8D1B4C025A1EED1A5F17CE
	FC773969346FD4867AE3D6D770BD0078AE7DBD0085DD7AFB37C9DD636F93AF698A7E8D6063DD5AEFG316D1AA452366550F6C6GB7C0B8C00C5A16A73AC9DB620D1640F1255156C776A048176D8A17DFAD863775E3B17477708DA2D9AB76480AA277937E9CFFD4A46D2548EE0AC4DC1BC8FC64F17459952C4A70A7C57A5D3954CB51AC4721B9E29258151A4473192F3651EF5661FD2D0A7B55B61E6364AE6238ED0584D78D6F9D0B38DF5AFBB6E95B8107B4A67FCC8F7129FAEF11FE2E02910FFDFB49C9329F61F7
	FB5737B8B6D89A4D9DAD75F5462E76FAF31B51F8244E5CD5575E555A585850580074EFDBAA4721EFEEB3371CE9EB6D7063505A88EC7958F246C276G6AA5F24F104FD703921F2F8A675FAD946EBC1C494709401836AF42F1AAFA11AF37757527A5FE7B4D29D0A7ED5178FB7A683303B62213170D8B08EB15024752C9A9F4ACFDA161BEC11F54B16CE6FBDBD6094811FBFB3D129A33F6A9DF4535F8CD3257C1DA0C4D93275751F87450CDB45CAF794DE91E8B2E865BF29F3464D583B4BF0867F7C8973D4DBFDFCBA2
	D3160073401F36950D630F6E873573C0E5A1797E49B75F54B99DFDAE528B59BCE3AC646B8965C11E93CD37937EF1701E82308AE0ADC07EE2508F209620E3B10F63BFCBA5C270319A9738340FFBC407CB417E70B3BA8739018719A790370262A2988EF336ADE7787EC49C6053967335F45EA6F2069BF03F2F315F2D8C233CA3126862E79DE74F285F8EA4737574D36F923EC6F837810CA4F31F3FDDC878374A9E452E7A4FDA38AB202F34137F793065B554B736F6102E41E2A14E9EBC66665BC347DC12DDFF7C7A3E
	FD6C787528DDFF7CFA522E9D3F907794636BD18F14903F4CCE5655381FF7DCF409FDF4ECC7BF0B6FEB6ECF1FF25894BB628B07B67A704D92112F765F3F1F8915CFC5A76B1D1BFF9012AE705C9E697E1D662E0949D7FE6E076B04CF37915B1EC88EDCBFFCC6714B1283578F4B5E656B07B4A8BB7C10A58F4FF4D23620AF1C8E17D40FFFF1C560B17D4B33249D6220EE72C3FCBD5BC05C4EF4BF2E797D4C3B5FFD2E726AF7AA837B4B1A336B28BFE0A874727565347344CE7C3BDB5709CF43B36137C20EA26C8C7159
	3AA2E413FDD1C548A50F22BA27D3919A48724351BBDC6412DD7BED781DD1111B5FFAAC42BFA2D0A4FB0A3CD651D5A48373A23148A33A7A24A239179557F8BD42E6753B02962A6B4A0BD5B5D94F5D1D15A9E437886B307F35885924933588B9340F3588D9507F06485F48A1FD097FA33FF18E5CBB2ADD2727CF32865DA3B2BE0C715A7D30EF8E4431770FD692B3B3811F7CB09FE3BF3312EF02022DEB5A7BC5F00C683CD0673211B54E11E1C57265F3EB2D043F1757E105D816E5A3FA1F57507C9FD0CB8788E2A491
	131799GGF8CDGGD0CB818294G94G88G88G3B0171B4E2A491131799GGF8CDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5199GGGG
**end of data**/
}
}