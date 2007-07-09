package cbit.plot;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (4/19/2001 12:33:58 PM)
 * @author: Ion Moraru
 */
public class Plot2DDataPanel extends JPanel {
	private Plot2D fieldPlot2D = new Plot2D(null, null);
	private boolean ivjConnPtoP2Aligning = false;
	private JScrollPane ivjJScrollPane1 = null;
	private Plot2D ivjplot2D1 = null;
	private JTable ivjScrollPaneTable = null;
	private org.vcell.util.gui.NonEditableDefaultTableModel ivjNonEditableDefaultTableModel1 = null;
	private JMenuItem ivjJMenuItemCopy = null;
	private JPopupMenu ivjJPopupMenu1 = null;
	private JMenuItem ivjJMenuItemCopyAll = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == Plot2DDataPanel.this.getJMenuItemCopy()) 
				connEtoC2(e);
			if (e.getSource() == Plot2DDataPanel.this.getJMenuItemCopyAll()) 
				connEtoC4(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == Plot2DDataPanel.this.getScrollPaneTable()) 
				connEtoC1(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == Plot2DDataPanel.this && (evt.getPropertyName().equals("plot2D"))) 
				connPtoP2SetTarget();
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == Plot2DDataPanel.this.getplot2D1()) 
				connEtoM2(e);
		};
	};
/**
 * Plot2DDataPanel constructor comment.
 */
public Plot2DDataPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (ScrollPaneTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> Plot2DDataPanel.scrollPaneTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
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
 * connEtoC2:  (JMenuItemCopy.action.actionPerformed(java.awt.event.ActionEvent) --> Plot2DDataPanel.copySelection()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (Plot2DDataPanel.initialize() --> Plot2DDataPanel.controlKeys()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
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
 * connEtoC4:  (JMenuItemCopyAll.action.actionPerformed(java.awt.event.ActionEvent) --> Plot2DDataPanel.copyCells(Ljava.lang.String;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoM1:  (plot2D1.this --> DefaultTableModel1.setDataVector([[Ljava.lang.Object;[Ljava.lang.Object;)V)
 * @param value cbit.plot.Plot2D
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(Plot2D value) {
	try {
		// user code begin {1}
		// user code end
		if ((getplot2D1() != null) || (getplot2D1() != null)) {
			getNonEditableDefaultTableModel1().setDataVector(getplot2D1().getVisiblePlotDataValuesByRow(), getplot2D1().getVisiblePlotColumnTitles());
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
 * connEtoM2:  (plot2D1.change.stateChanged(javax.swing.event.ChangeEvent) --> NonEditableDefaultTableModel1.setDataVector([[Ljava.lang.Object;[Ljava.lang.Object;)V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getplot2D1() != null) || (getplot2D1() != null)) {
			getNonEditableDefaultTableModel1().setDataVector(getplot2D1().getVisiblePlotDataValuesByRow(), getplot2D1().getVisiblePlotColumnTitles());
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
 * connPtoP1SetTarget:  (DefaultTableModel1.this <--> ScrollPaneTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getNonEditableDefaultTableModel1());
		getScrollPaneTable().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetSource:  (Plot2DDataPanel.plot2D <--> plot2D1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getplot2D1() != null)) {
				this.setPlot2D(getplot2D1());
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
 * connPtoP2SetTarget:  (Plot2DDataPanel.plot2D <--> plot2D1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setplot2D1(this.getPlot2D());
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
private void controlKeys() {
	registerKeyboardAction(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			copyCells("Copy");
		}
	}, KeyStroke.getKeyStroke("ctrl C"), WHEN_IN_FOCUSED_WINDOW);
	registerKeyboardAction(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			copyCells("Copy All");
		}
	}, KeyStroke.getKeyStroke("ctrl K"), WHEN_IN_FOCUSED_WINDOW);
}
/**
 * Comment
 */
private void copyCells(String actionCommand) {
	org.vcell.util.gui.SimpleTransferable.sendToClipboard(getTabDelimitedData(actionCommand));
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
 * Return the NonEditableDefaultTableModel1 property value.
 * @return cbit.gui.NonEditableDefaultTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.NonEditableDefaultTableModel getNonEditableDefaultTableModel1() {
	if (ivjNonEditableDefaultTableModel1 == null) {
		try {
			ivjNonEditableDefaultTableModel1 = new org.vcell.util.gui.NonEditableDefaultTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNonEditableDefaultTableModel1;
}
/**
 * Gets the plot2D property (cbit.plot.Plot2D) value.
 * @return The plot2D property value.
 * @see #setPlot2D
 */
public Plot2D getPlot2D() {
	return fieldPlot2D;
}
/**
 * Return the plot2D1 property value.
 * @return cbit.plot.Plot2D
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Plot2D getplot2D1() {
	// user code begin {1}
	// user code end
	return ivjplot2D1;
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
			ivjScrollPaneTable.setCellSelectionEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
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
		r = getScrollPaneTable().getSelectedRowCount();
		c = getScrollPaneTable().getSelectedColumnCount();
		rows = getScrollPaneTable().getSelectedRows();
		columns = getScrollPaneTable().getSelectedColumns();
	}
	if (actionCommand.equals("Copy All")) {
		r = getScrollPaneTable().getRowCount();
		c = getScrollPaneTable().getColumnCount();
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
			buffer.append(getScrollPaneTable().getColumnName(columns[i]) + (i==c-1?"":"\t"));
		}
		for (int i = 0; i < r; i++){
			buffer.append("\n");
			for (int j = 0; j < c; j++){
				Object cell = getScrollPaneTable().getValueAt(rows[i], columns[j]);
				cell = cell != null ? cell : ""; 
				buffer.append(cell.toString() + (j==c-1?"":"\t"));
			}
		}
	}
	// if copying a single cell, just get that value 
	if (r + c == 2) {
		Object cell = getScrollPaneTable().getValueAt(rows[0], columns[0]);
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
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addMouseListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopyAll().addActionListener(ivjEventHandler);
	connPtoP2SetTarget();
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
		setName("Plot2DDataPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(541, 348);
		add(getJScrollPane1(), "Center");
		initConnections();
		connEtoC3();
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
		JFrame frame = new javax.swing.JFrame();
		Plot2DDataPanel aPlot2DDataPanel;
		aPlot2DDataPanel = new Plot2DDataPanel();
		frame.setContentPane(aPlot2DDataPanel);
		frame.setSize(aPlot2DDataPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		aPlot2DDataPanel.setPlot2D(Plot2DPanel.getSamplePlot2D());
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Sets the plot2D property (cbit.plot.Plot2D) value.
 * @param plot2D The new value for the property.
 * @see #getPlot2D
 */
public void setPlot2D(Plot2D plot2D) {
	Plot2D oldValue = fieldPlot2D;
	fieldPlot2D = plot2D;
	firePropertyChange("plot2D", oldValue, plot2D);
}
/**
 * Set the plot2D1 to a new value.
 * @param newValue cbit.plot.Plot2D
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setplot2D1(Plot2D newValue) {
	if (ivjplot2D1 != newValue) {
		try {
			cbit.plot.Plot2D oldValue = getplot2D1();
			/* Stop listening for events from the current object */
			if (ivjplot2D1 != null) {
				ivjplot2D1.removeChangeListener(ivjEventHandler);
			}
			ivjplot2D1 = newValue;

			/* Listen for events from the new object */
			if (ivjplot2D1 != null) {
				ivjplot2D1.addChangeListener(ivjEventHandler);
			}
			connPtoP2SetSource();
			connEtoM1(ivjplot2D1);
			firePropertyChange("plot2D", oldValue, newValue);
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
private void showPopupMenu(MouseEvent mouseEvent, javax.swing.JPopupMenu menu) {
	if (SwingUtilities.isRightMouseButton(mouseEvent)) {
		menu.show(getScrollPaneTable(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5BFC71B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BB8BD4D4573556A0E2BEC68DC94813A4E810202262AB264632726C0BE9E924AF7645A4A4A5D93C5426F455B63E46765175D8EFDDFEB328408CEA8DEDC0C8348D3ED0A2EABE8FBF0BEF024AE782A3C1C5618297E6F418BB1D3988A81F3777B9775C39B35CE18CCD5EE32D4D3DF71FF3766F6C3D4FF7387D45E1D9E9697BB9AEAD0B137FBEC9633830B39C37E3D748EB8C374529B3F02A3FF7817239FBEF
	E642B3894AC222F586A3776A4DEC785E895F92FC3F8D6F7B38C62BF49ADEFC70E90432D9DFBEB3B6BD1F33E30C0F1EFCC71C4F01E79EC08A6070D967C27ECF1DAF107105AE0AE710164EF1C6896B3CFC3ED04685C11DB000F800A439FE8ABCF7914EF3AABF156BF5AE0C6436BE3BD11F0BF4D4B499F4C8346D187C6438867D61A164155E18A3370901326DG28FC86776141E2F81EB2DD9FB8535BE3154A9DB6D12A2CB4417FE3F5F53C441BF83BE06342880DF30D59248D47D9603B6C8662F25A116EB678CE8558
	FF0361E7DFC63C835E03G42EEB0DDCE3FFF0A49FA6BBBDC7281C73E4107AE6BEFD09D12EE38F529BA33B27D0F446E7346513E0543BAC3950099E084A084E0BD40BB243F970C7DB7BC2BFB24DAF1F0D034D7BB9CB536BEE71358A758603D3292CAE4DC2B50ABBA7BB80E76E9F6E31906BC5381EDF760AB3723B609BC4E7AB72AF5B5977B6DEDFA3D06CDE2A3FF285FB2CCED11BC6C3689655D6213F77608277FF9727E12D3730E9BB0913F70663D4DECB2944AF40E0E38F95BC6302FE7919FB041FB1ACC17626FA1
	78E8951E7A62DE02476FF8A8BBBA4AFC23A30F49521839145BF174E71AF6987967D7744123D40628D1EFD97ABB11F642A865D92C48B2C170A5B75DF8AACB9071D37C36C0D95FAD1D017A5722C90C4D6EB11DE18CA098A08AA09EA099A0F50C79584BE39F5E260F19F827D4B3E4357589CE0E36DDB4FEG1E420864644F5ADD92EF6F955AF927156F3189AECE2E33F89CED506351AE0C78FEAF7078DC308BCEDE320AF6204E8F8A1260F451BA4B4773310ED3A025E66B20G8C869D9C233BF6FC9F5A06F7C9979CFD
	3CA438EB5072A74731CFEE706DC0918840BB354BBFF1E82F42F11D6184C05DB83343098924F7DEF0C23A282CEC94456BC38EB3EA42850F13FEA13923835E9D634C465FCDC3DC82FC6FD068DC1F582B22D3E76DC571F967A80B3140F6A3BCB3A7F4069200C6006E8966BF3F6EE07ED3173B0A4BF961271A7E53F141E4981920FE33E042BB0E6EA374D14675D016G10B241F2FDC1FB47ED46F5494454186B564065493AB4AABCAAEE1BC7160F18562A0BB9DB2F7461118C520713BA4327G9D134C769193A55417EB
	F817403C3745890EA77439FB024AE96BBCA5535D830063110160118E477D8160ED5976A86791008FE47E482B08E37D99C068B8C00690000DG49G46D9A09BC0B7C090C0B4C082G77AD1D21944084B0829076ADE6377917F7487CB23CECB31DFCEAF9346C46647EAAB97D7F92286EBFA23A6F13F57F03170F68E53F703683533B5887EDE61F7FEF9DFD81755950C954C738A15825BA41A1587B6045A517BDB609FA4B7935DFD45AACA7F8876463DAC800AE853FE6926D42CB12535AB3A489ED02A4D96D836E726F
	90DE2E219E07BB15B3DECFCD222D45E14847CA7D27A7B117750AF63BC002C7E9A217BFC3F229E09386C168DAF148AED17CE68E67185705D1B917BF4769B84C4536A19C97683CAD1A477EF4B82D039012B834F08533B14F0EB79628EC94877101734F6D7064E633BCB9FC08654943B717F3E1DDCF6935467C11E52F6A3BE7531CA54DF667C9AA5346B4ED593FCFF23A0CF7D1D9832F225D3C671499247E7FFC06BE58AC5A6B7B2C920E97F5C2BFBFE4134858C106518A592F77F17DA4569D04CE050C9BC9C7BB1CB3
	090EA1C713E09F2A10F3DCCFA64A978CF2C782EC1C4DF2DC8D19BF3271C5CEEE35224DC63B884A0CF71039F97BFE15AD92E6D3BB627BACD21E75A5AB6F00EF479D6EF25A8FF997BD7AC1D53E638EA64B0799B85638043F8D89B07423426715BE7FB8E32FD7D93B5C7F347CCC866AEDD30D33F581487769CE3626488E20BCCB8268B8F6821E87D15EB9E4EEF60555DBG78C800F839FE769CAAE7A229E73A4A6A054F21B9BAF68E9D936361D92E50BB58456A1586421C9C20BB10528B8B2474E83D5715FA71F279B6
	79199A28669B5A4D6A954C25E342513914EE55DCB55FC11E556B843CB4F7AADFCA6F5D9ED6AFF8AE9DD722647A0C9E355FDA01550B1DCB67GA7E47BEC1DCB6DD7970475467AD93D6439EE7BE2FDAEC84D776800E267A0BA06C586517A6B03A8DFDAAF43426AA54A75F64A75B2674972913EAFA9754A6651723AF9DAFC97DBD93D00F9F43C8C1D2745D738466AC54975B64D7364CBE3782DA3684FE41DF80D3F410F143B06A1B7D6D61EA341CC5707BFA7F3BC2C3FF59EDB876EB3E19C665C29B39C86B8F5A74BC1
	BF4EE56B40606B11DC38254560EBCDEB3E136AB5F227BB87ED5BD5350B46F9D49672DC8FE5F1F73238FAB2C3BD972DF63944DEAB19864B31E349D467A80A9B4C5C8D4FFE2B53A51D05C1E4044519AB132CE7F885AD5B23AC6783CE3DEE9E9832D6D6CE174664755678E7EA3DC38D1B75FBFC681DA86B1B2A52FB6C4D3F99683AE5D1C70E1C97CF5D85FE8C90F597621F6B403183E31E726BBD4E78AD38390C2B3ABF59A73F9DF7D1BE463B5D7C4AA6FE17CD755595947A68FF16F9E9FF1F3C08F2B5G0DEE0091G
	AA6F76CEA66F82404581507C7D338E9C5BE8CEE7F9FE788A6AA0D92CAE2A43EF2A2BD84ED83D148B513F22575AB3E8F87C8DBDF78F153D749E378E66F5AB53ADGD40EA72E6448FDB882F52281E83E8EECD3676BE440674C071C83D0B71F49DAF4752F709C9407DCC2B979DFEB3376DE977ACA51A2657C30D4CE66A165CDD8D60F2FC794FF396E2A05274BA28EAB86ABED74596EE4234A4865EE9B1FBC52AE7B5152FDEA1B94CEC6E95A44E2F8C25FB91F5AA2E03E5BA616081569E3914CA623D7D95FEC01BA5977
	B2FD132EE2BF704E01E374BB3B4B33DF46AEB299E64547F0A9FBFF22A983C2553D143765DE378C21B1AB5323E3188C1FF731FED98FF5F6G50F178B7975563F06982BA761896B099D7F79DC3994960DDCE9FA64159AFBA872776CBB5A9E69DC37B6533894C693D22E334D63059DCF21F58F8B0FB9B4C836D83E8F2F92FE0CFB713318F7807AAB2B8895EEA37CAD65EE63DA9149E21341F7FA20F5036CBCE5106C3FB291B56B35D4C66DB01CE4EC2C62B0C17EDDEC13F4DFC2E229FEEB17076BE0E51F800E77254C1
	7B31054CE6311734EC9633104562399E46BB8EF05CA246FBD8E03CE93DD784D66FD32863D06A897DD9A48EB02BB1D91675333A11D0AFE9915BDF1CB0FD02FEAFC8B871EB07C91668AC3DFCD9E565161EEB00BD2901D2FCFD55C1352FA7F7BF2F69E736DF2C53975CC77D2B6ABE371FED81BF5F26787A4581A6E7905459F89F53E96300272FA78F607A670655E505840ED3D718A38E8D5A4DD689B7E7A82DB28B231582F40E86B3DA77D9BC7B703B16BFF952A236A053391A51D6F118591033B2FA96209526509B30
	220F77DA40B6C239CB62A521163CD387E7D9027AB8ADE1BECE69EED268A6GCD637D0C6E8757184E345E7CEB2CDE83541925547BAD2BD7C1571B5B276CB52E3F5F3D3791FF7F54FD8E3A87B7174C85126E27FBF0998AFEA1416FD06129BCFFB4B1F93887F4063E87583E518F32B1D7474177B600D4007D8F427C8B407CA01B837CE766F14E4F3E5CD93B84D3845E56663120E37BE3D9489756E072773AAC74E732866358BE4AC73BB14E9C4073EE00F000D800C4C506D3BA3FB2540FE84AC06D9C5201F6CEFDD0FB
	5FC88B64B38952EEFF486D3723F1F664127A1CE11BB1C41F2191E788F5B2EDF308BB4E28CDDE5B03B68986FCB4C09CC092G779033C9202E4C1FCD589E28EC8C6AA3EB483C2264A16AA3D90A6FECA07893AABC751D6A0BACB7B6C0D954C3EC1F68B71D7E76680D7F60E9B75A0E3B622F5D892FF6549EA7F2F15E5488E59600D9G21G9B81928138454CAEDBF5B57E6CD24DB6A64C2272DA6B3D3940640D1DA2EF41E2CF793475FCF6CA3B035EFAFA5121F97E5AAD9F6B83B28326F32CB813FA0E5538186E850F40
	73EEG3A06F82A0D2DA1429733B544458B581709703D93A0A714650165F9E85B4370DD855081A08104G44GA404B25B3E2FBF6DB78EB1E11A45DA40B5401ADA432C32FDA62F4E4CFF6E715BAEA5D44BFF0E98D1C76343E08B00BA00EE00G00B000CD8FB39DEF6A7D7A0FF2C4F0BB7E5354B5B37F39BC25DD3957F76243DA7E9317750E367F6034772CA48C4AE710C7AE31B3481D8F33184E6F1E192C45D35AF94BDA7608D63B4DD35A5D1E8F3C6C37DD1DA63FD0FEE67A2D18524EDB3FCEAFFE5446EFA9B6169E
	E1B6BEA04CCC6743FE5BC5E96A1CB4C3FE3F765BEE13A63F157DB36377245FF6090FE84575A3799857BB214C78284ED08AD087E08188G08FC1445F53C7EA4F7FB392BDADEA3F887B41533EBE0E67A8D7AED977728D63BE31619712B725BAEC9131F4EBAB3FE7B7D36CB5164776C8C79A578EF972655AE785A4C78AD755B2E540B9FBD2F4DE8429CD085E5E6G0ACBEAC65C88FCAF88E3F9619E932E8DE2146F8749B76E1D48B956F43B671953C10C170C1EBAFE7A357048700127F138A55B77B953CB595A67CC2F
	E6EB1FB37DB4BB1F6201067AD6GAB7FE5767467D0EFE57BBE07BA3D5B371CF53B3565BC37DBDBCE536E69656C586DCB4EA2E24F9753F15E669C32CB56C1011026E33DB002F729A0F3358E59E9DAC36AAE6F79BF47F265EBFB51276243D8CEFCF377B1EEEACE441DD751490818855E59525F2FCCF45A9CC2AF137B0DBD3EED742BBD5AB6FAEB0F360DFE3FE7FA9B2565783291755DF597B12612C1374CA5ACE6B6929C1EF560798445FDFE91FD9C6BA47B08053F87A80FEE42A3EB1AD878FB007A444FA5BC1F1EF2
	A65A8CB3D30E6D7F9E3932CB2EE312C453B14516E7F2FD77DBFD2EF63F1D4F457DB317B8646C953C7B26B537C0A9F30F1BB859C4F1E81D4B243D44BB8704A9FD5BC35A53B2DF6D2F6762FEFB0F354F6A14CF98299DFEF840C07C466592DCE4G47BDA1D97F37947D6BA53136C2691301FAEC2BB1C7EE6BF50AB69B1E44F0EC8E47FD11AD77EB4992360E7AF1AB46E387FCBBG0294FFFB3ACDBDC604AD71742F18A5D37D0D2E3DFFC456E5F1CB685ABBCD413FC870DBD5F83AA66F3A406EA0A6C1D95DD2964F5F4E
	735D2F9B72347BF5E31E6FFE7DC11E7FFE5D12673BDFDF4A735F2FC9F93E621A766193D777B09F6EF70A032663B26D63C65F3AD69A35F52DB27A5675F323FFDDCDC65F3AFEE9742FEB37515B07290E81DDEABFBD26641172CBDAF30E48253E73962557502D26F7DC4127731E36BCA1631F675575CFA87CD3B479477A655FA4F87279C8463F5D2F4EC76E7B882F772B78B7D5A8B60D4C775D47EB72357BF8DD3E6FBE0E4D775F474F647B6E6378FC7FFD7CC27E747E7C400027ED587D8B2B4593FFCC461F362AEDF3
	CC2113F64D337E47B2FE33977E93997F27A6BC9F347123621084396FB14CA395CDEC9FE98B74E9B2009170079F237BC9B4D72E9DC6FA7DCEFE9054A003FFC1B8367D0FE656B6G6A07G2C87C8GC8F90C6DF7A718B1CF2932EA8535516BFBF355B6729833C4F237CA592D28959D23CA59760F0C5EE555B69B75552CAF182FF603FCD238270F86057B4A3D75EDA8E3E83877DEFA8B41C705FB6F253F544A7652E3212CE4991B533FC2765B8260BBDC417D454C66796B1731B162798B1C92DF092AF7614257B1771E
	FEDEB2710D7220BA5713BD0FC450F73BE28743E506CBD9464E4EAEB631B1ACB3C2E7B895417C6D0DF20F7DABBCF52C2C2C9171FED1A3F5DEBAAEAFA16BAAECAFC530F179B9C2B38A3EB781A4C630337B113F30337B280825DCC81F769D090D664D7A1D91725D2347D5675EE7D626D31EC5AD0C27994A6FFE9CF167DB54E7046897CF26537DA1DABE6ED10E39814F325D65EBBC4AD15E13196A72FF73A84FD235273AED7EB353ADF5F3B457386EDFFD7E26E12B2CD30A7CDC1F112A8B5B503C0B76D1A119679C0D04
	F5EAA46B378FDA91E7016F858AAE301579F4F8A4736973ED382E5822FCDFA05FF85707FE3F5A2A76BB2C77757B766FBCF8FC5D73EE1AFF7E3D8D7B22D730595A84791EDB3D9DD7D8FD291164DC5B4C767B3643B74D13274DACCFD6AD875B82C8G41GB14BD91E6C3CC46E8340E036F5B97ED6268C0FB9B97A5E026140761E1AE676FB0A4199360B784A6DE86EDC520CF1D83042FBBE3B02608F2F701E4F3E20640223D0963C021DCB9CABC45B2540777E159063GF5GFDG33D632B96F35FDC7B9BF7BFAE65151
	8813881BEAD9C975FB2FE5E63FB3791E1E45DC4C0695DCC860A21FF70102D7D2DF0F111F2674D49DBE29FE4F9421FE5B200C0B023C88F08A4084E009E27A7D63BE791C8F1765308A67FBAD6436153248378A43EEB48E5054270EFFC14E5D8527E46D65EDF4882E611DA651E66D9D65283FB6131847FCB29645FC312695E53308CE6BCD51AECDED4949ED2F12BC0067C13150F67FAA44BD5CDAB20D6D38D5548672DD0FC11C5B392786746EA5ADFB4FCC6241B9A4D0FB078D2B4FD38BF6EF513CE7CB4AE59E8E79F9
	78A348F14447FA4DACF7C72EE239FB2159BBF7AFCCD3675E274D5E399B5B3B4BFFEB764E5D1E6D5F3F601D3BD9FB2A57F31755FA6DF8E8031E450537DEC932BE5BD7F97F4E6271E67C226F512A771FB4FD7373091D5FA88F6A7FA78BFD2F81EA8A3557G8D055AFBCC4D05536F8736977A5E8F3CD2283E17CE637741E26C5F12559037G5DG92C050EA963B3FACFC0FA3312B913775FD83388641DFABC90C7F3F9479567505A27F3FF1FA2D886FDE760812A48EA2FD065FD6047DA5098E022438DF94210D1C5681
	0BC42B4A78B70BB0CEECC2BFC153F8FC279557049EB7695C01DB5EDE5FEABEDBDB5D5855D65B5A5258D8D35D5AD55D5655DCDFDFD7DFC75B8F35FEBCCD7B0616563317DA1A4D5A94D83E58500A6B17DA816F1A30789996D89EAEDC799817FAF03E1E452837AF052F26F133E535BBFE5CF97877A528F3669A184F2FE1FDB76BA05ECBED672FCD5D95852449A9BAA0350ED2AC3902A44704457CD54794AA43CE5C4A157B607B877F6C0BB7F84EB930A33D59E5668714B60F967B761D08E27F3E3326185C6505BEB70F
	BA6435ECCCB15ADC6C6FF7890A0FBED9ECD4F0E7DDA651E545E9905561402192F8D6018DBB81E68104GC481EC82C8G58215877FCC969B4762593FE929FCE5E4AFE9371DFA5F96A7825E617654AFA97E3D139BBC82EA9530B07347C5DC33E6DD3FA483FFDAA8EE94756A70726465629C35A31D5F3C89DDB723E13CC5729EC5ED2FC53A11C475375FA93EF6787C85E213F3FEE98C5F923FE45E5C9DCA62E02A5AE9B9FA5309AD66EA0CD2A471604B9EBFD0E190951726FA9223D6FA0855EC2F94B566A8C8DEBD93F
	3DF8732F5378E65BA0EF33412C61FA353D8F7D34E6D4925C79752EDBC85F456A480572EFB8EF6D12F384EB733FD0CB87880821EB2B9B95GG48BEGGD0CB818294G94G88G88G5BFC71B40821EB2B9B95GG48BEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD595GGGG
**end of data**/
}
}
