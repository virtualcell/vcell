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
	}
	private Plot2D fieldPlot2D = new Plot2D(null,null, null);
	private boolean ivjConnPtoP2Aligning = false;
	private JScrollPane ivjJScrollPane1 = null;
	private Plot2D ivjplot2D1 = null;
	private JTable ivjScrollPaneTable = null;
	private cbit.gui.NonEditableDefaultTableModel ivjNonEditableDefaultTableModel1 = null;
	private JMenuItem ivjJMenuItemCopy = null;
	private JPopupMenu ivjJPopupMenu1 = null;
	private JMenuItem ivjJMenuItemCopyAll = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

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
		boolean bHasTimeColumn = columns[0] == 0;
		cbit.vcell.parser.SymbolTableEntry[] symbolTableEntries = new cbit.vcell.parser.SymbolTableEntry[c - (bHasTimeColumn?1:0)];
		cbit.vcell.parser.Expression[] resolvedValues = new cbit.vcell.parser.Expression[symbolTableEntries.length];
		//String[] dataNames = new String[symbolTableEntries.length];//don't include "t" for SimulationResultsSelection
		// if copying more than one cell, make a string that will paste like a table in spreadsheets
		// also include column headers in this case
		if (r + c > 2) {
			for (int i = 0; i < c; i++){
				if(!bHasTimeColumn || i>0){
					//dataNames[i-(bHasTimeColumn?1:0)] = getScrollPaneTable().getColumnName(columns[i]);
					symbolTableEntries[i-(bHasTimeColumn?1:0)] = null;
					if(getPlot2D().getSymbolTableEntries() != null){
						int index = getplot2D1().getVisiblePlotIndices()[columns[i]-1];
						cbit.vcell.parser.SymbolTableEntry ste = getPlot2D().getSymbolTableEntries()[index];
						symbolTableEntries[i-(bHasTimeColumn?1:0)] = ste;
						buffer.append(
							( ste != null?"(Var="+(ste.getNameScope() != null?ste.getNameScope().getName()+"_":"")+ste.getName()+") ":"")+
							getScrollPaneTable().getColumnName(columns[i]) + (i==c-1?"":"\t"));
					}
				}else{
					buffer.append(getScrollPaneTable().getColumnName(columns[i]) + (i==c-1?"":"\t"));
				}
			}
			for (int i = 0; i < r; i++){
				buffer.append("\n");
				for (int j = 0; j < c; j++){
					Object cell = getScrollPaneTable().getValueAt(rows[i], columns[j]);
					cell = cell != null ? cell : ""; 
					buffer.append(cell.toString() + (j==c-1?"":"\t"));
					if(!bHasTimeColumn || j>0){
						resolvedValues[j-(bHasTimeColumn?1:0)] = new cbit.vcell.parser.Expression(((Double)cell).doubleValue());
					}
				}
			}
		}
		// if copying a single cell, just get that value 
		if (r + c == 2) {
			Object cell = getScrollPaneTable().getValueAt(rows[0], columns[0]);
			cell = (cell != null ? cell : ""); 
			buffer.append(cell.toString());
			if(!bHasTimeColumn){
				//dataNames[0] = getScrollPaneTable().getColumnName(columns[0]);
				symbolTableEntries[0] = null;
				if(getPlot2D().getSymbolTableEntries() != null){
					int index = getplot2D1().getVisiblePlotIndices()[columns[0]-1];
					cbit.vcell.parser.SymbolTableEntry ste =getPlot2D().getSymbolTableEntries()[index];
					symbolTableEntries[0] = ste;
				}
				resolvedValues[0] = new cbit.vcell.parser.Expression(((Double)cell).doubleValue());
			}
		}

		cbit.gui.SimpleTransferable.ResolvedValuesSelection rvs =
			new cbit.gui.SimpleTransferable.ResolvedValuesSelection(symbolTableEntries,null,resolvedValues,buffer.toString());
		cbit.gui.SimpleTransferable.sendToClipboard(rvs);
	}catch(Throwable e){
		cbit.gui.DialogUtils.showErrorDialog("Plot2DDataPanel copy failed.  "+e.getMessage());
	}
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
private cbit.gui.NonEditableDefaultTableModel getNonEditableDefaultTableModel1() {
	if (ivjNonEditableDefaultTableModel1 == null) {
		try {
			ivjNonEditableDefaultTableModel1 = new cbit.gui.NonEditableDefaultTableModel();
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
	D0CB838494G88G88GC3FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BB8DF4D465156C2E76E4ADDB290D3BD1292594EBEAD305EA0FD453AEEE591E281C154D7A03EBBC65347857C33BEC6BF6B9EEF67BB281C990A4CAD0F6C509ADEA109072C792A61310E4A6101FC9B2A4C3888904F919F9C9865E4C9BE7DECCC0543D773B6FFB73E672A603296E661C1B775E7D3E6F7EFD775E6FF704623D0BB667D90A85A1EF33207D5565894222A6C1386D211BD2B92E7E3D6CC2417077
	B640AB027D4CA6F836E31914DDD8A4D4D88A607B8CFC6778328BDF07771D025025B642CB82BECEA8FB346457F93373F926007359423E2FB9348D1EAF83F8G074F0C77107F62C33BB57CD59A1EC31EC59026CB314E0F8E6D51F09B20CE8160EAG5ACB293E871EA4405556FA2D5E192F268B4FBF9036ECC7BA861A9CD6E8ED57E94F9261053C797B115753CCCEEC33873E5DG28FC3EF0682D3D703439AE0CB7F9C77DEAF9C8D6D42B55857F2B9C8ED195DDE2D01205027711C6F9C581EBA3882B21FDAA43EDB802
	F41D706E83387CBE470F9DC57C9AF85FGD0F0006B52781ECD13F570233B05072B821BF6A4502564G6950FEA02A4B3C4013167FE6F6FFEFAB5A77AAA8CB874884D88BA014E59716G3C417A3B3C70FDF8360C2AF6A590D002AD21D02B6C8BF7ABBEC906F72B95CAB4DC1F64D542BEC120BEDDFF284CC41E59005A3D70195B11CD522BF97FE6745DAE645C795716ADA6B649C97F48D2DFC636F015C5EDC23C2BAB92718E15457ADFAC6FD3021177266D0F66EFB661DD7242A379D7E9F4528C3C9FAE433E76D9312F
	1743FB1EC617709FB07C6E03D1BC79626F184F60F78D14259D643EB178B2174539E931D0D479B726F6C87B3B79165C03A4C3F1F93CAC1D4DC83B391CF86E55E599E57894831EE411191F6277EAA8DBF5A83B107C6B13DE0C4D2C0A6C42F5G39G4515901FGAEGDFA577312942C3D768E3AEB12C36CE7AE51F949628ED45565760A9CD2BE131A398D14520D79A9043FEF1D416A202D66748D63441E8CC3BA6667BDE60F1DC8ACAE1D175ABC120AE86A4D58AC7284E712D2FE01D3044CA5D7E00848C82A101536D
	5D8A79D0104508FAB26493D5A9DA034ACFED45BE79D09CG0AC0G5E49AE5FF5213D2E827D9782E468F6A869C1FADDD2985205556AD4148B13A1B7EAA264D632FEE139E3853C2F2964B63E7584621A2B328B872BB81DD3BD2F9A68B87CDE94DF8CDF64B1F65EDA04F2C37D94003BG322A387F7CFA107B0FE37B770432C72EB7751FD4706535D564B79B2B6263E80A51C799CB2A338B1B81BC55BC57679E993C4238CE291A99F3D9A63817B5DDD08F62314BFA25BCB6A708E9333A18333768FD382B1F75A1683588
	E0C5B5377DF5BDA5644B2DE2C4625E5B9B864713FC511EA0B9DD4DB60D6ECBGB89E6DE0F8B64E8A3BGDE57EC0FF26E87B82071C7DEEF8A3CBFA7981DB5A003D013DDD88A60821886D88410853081E077E1688B00890085G4B8156829454F23B050FEE5078654759E6B6790C7218590D4B7DD9F27A7FA5106E4B186EBBB55DF7457948964DAF626D40755E1B00B6374F7F370E0900FCB625977511BE14022AC38AC9C19F3CC43432EBFBD1EFAD3F0EA9FA1B6B99BE8479588E89B02263EF6AC53B082A9A760FCE
	2AD23F242A7E60F8347CDB0CD7E4F2B494CD651C57ED3DE8EBE58A7231DE7F0EDE4CE5DEA59814D87068CD3472E5AC17CA3294G216D4AE4D0A57C5DAE1CE3DE10AEEA397CDE570B8266E2F99247851A37AD9531BFC3E1FFGC2920796E1DEAD66D931E23741C695004379279B1E9335BCCFCE7D116749FD173EA32CB89732180D79E937DCEB492A251C15D39B4D13A4D34689F3593F4FF23A060F102CDF9AC13B454FA973D97DA3475087FB14E01B4F2F62F861104644C9D9E5E3879BC6ABB53F5EA90C6DC0DF8E
	B1BA159AEE5A02F668F4A92149D037941C2C54F2DC0F9B654BC51B54C1DE29536704FDD84E4797AD395995D926AE023269BAB6B737969BEC51DCC7F64477E7D979F3FA798A78DEE3A827FE780DAD269F6A23650375DC16B77AF12C09C89FCCCAB0742342DDFA1F7F217F553832812D7F297C7DFE54DBB60C339975487756E63E2688E9BCD36AE99CDB884F37D05EA3ECEE56426BED44F6002BB9C275C3C7C8CE07956B553472FA5B2C142377D9E9CC2C01E739CE4FE2677596GBE9DA04BCA748A9A089E557B29DE
	2F26014A1D5A5357E064BB4F416B4DEB20B1A12D016826B798791EE86375D682BE27E1A6DF223779982F175BC063CA310D6AF3FAE43F6BBBF83DFDB61A83AC546CE333117DB2ECD84F53496B39ECD17BE27D891B11EFF117EEE79B0DE1C50DD43F241178D23DE774FA0EC62AF7C62BA7EB75086F3DCEDEAFD5AB4FE8B4637BE9B72F373E11464BCDCDE6FCBBFAF83D62A62AD756944B17E278377B511F59BA713C7821B8DD9E19025CE835F632E026756103EC0E0775EDCDFC9D786F559807AAFC4F87D85244F350
	BFEE676B40548B69425A6D0F64A7DA53E6B611DEEB1B22B9A8670E29FC0A733413483364E8F6E145D19ED737749B6722AD1108627533E93096BBEE37B1C7916E34FBABBC477C61085A810348B40F33F3EE149767499ADA33C759368302F15DBCBE6937DAE7CBE35AFA6B63E3C63DB3F7FC4C7217195E0E23242F6FE8D46F154F7DAE1F56AD9F5A36E9F9F1891467G94B7A37ECE9B0E9D9873444FDB4D79AD38F40B60397E46047C861B094FF4F314DF46414379246F0B3B77A468FF1EF9293F77B622DCCB01C696
	40DAG12F7F5B317F7A360AADA328BA9FF5FEF43310DF2BA4F7343AD2803BA610F108E3FE9E956E4DCF57BE2E17916799633BD0309DB53AC93ADA47B0216288E1EDFBCE9D181C80E9BDB36E9FD38966A6CEE4DAE24FCFD3E4E182FDD00D78152GB2DA392CF92D876199D0A6A3D2B97BEF177D5E8B122F94ADD2AECE2965EC9ED25E0DE5ED783ADF77178B91BBBCA3934A14EE30D2E742F68DCE03114B23B6EE58BF207951621DC61B58BE9DB135C94A0E894B4AD632457A5628CD54071F3424BC4CEDF2361577CD
	3D9D723F1D6B3B32957BC18C0FD751777A5658FE39FC1A4BB0EF55DD426E1DFFE5AA83C23A1DF82F36C7E5C87F7913164C1FF3998E58F93F14B8E0BD88C06370230D46F1F80103461E658EAE63F576AA14118D5E6574F0C961B1A59C1859AFAD2C18F78C754B31CFB027FB1550C53BA44B912DCFE4914C5E8F734060B81ADC5BAB7897DD46D540FFD39B17E14001F8FF502F7AC559FFC9AA5DCF347F69444B0CF6D08DABB28E6D25D1DA5FF1F01B5B000E2A53AAEA53ECDEC95F75ED5BF57DF00BC18C7A84CE63D2
	9B17A7835A2FEB63B6DB54E4E633BD47F8AC169F633CABGB7F10C739E6E603C295E0FBBF83DC5D0E70DDE2F23F3B30B834CEADC163F6C64F50B0EE797369F677B0B1F386A506FA595A7FE83B049D24225434386AB770E1E87EC0389CA77756F3DE574758271EAD3BF1B7F5C38E55EF1722F74630631827CFC036E6B0DDDDC4E8DD027341D6B3424AB5657D7F5617A67C3FF448F899C272EB0C71C8C845DFE95B7E708D611135372G1DB41D5614B3368F3F617C1FD8DA4C96ECBA57FA31CF1962B6DC2753DB8D34
	8ABAB83D2EEE74F16F8458C6AA0F2822AA5959BBB9B84F926463D442FD1C6826F7F33A4DC0F3DA273B23076BCC7542BD3C5ED228732CDE6FF1DE2F12561B2BE76CB516F4C677B6EABAE76EF3509E5C059A245F5EC9FBF079BAFE1261878DF81267BF2B39BC93D0362A136F9B5DB908393A228B660EGBE004BG8B81B23B789C6099F73510E4DF2EA3284294C1147BE396F45A387F7513489756E05A77B7CF22BF33B5184077D15E9E42B8DB83BC1F8758660439B1004349E5A89DC8AAC35B34298CE4674BB63433
	4FE93EEFE4865A1984EBF779B33423B85BBFE4BCE75874CA27A55FA44E90B2B45A194EE81C11CD9E98C61B6482FEF7B70C73G6DG935D5CA6610132E4B661FB201AB148C73E466695A95D64A31BF55F391961979A7064BB6FB4725C38944A0AFB78BE51234D49766827FBE26DC66D56A5ED37B02E9D592364AC4E1B6E023255G4F82EC6A053C8360G186865F6D9B1501A4CAEADFCE3422D682F76784D85AE6F2216F8F96775464AE72E676DB35A3D952FE79C9D4A7367BFCA30BEE0B3E01AE3E5549867D8F775
	52DE70DAF8BE8FC0EB0845F5FC8D314D4557900D6C3C40815FE7GD4974F83570F20ED6743F7BA408A009CG61840CF5G4DA738EDF71EEACC9A0718B05D0A9DF06D3026B6B12BE69FEF6B5C7C6703246DBCA74C7CE71787F51C0632D4008C00AC0075G85FD30D66B63BA0E1DCA6ABF7A91411578CF25FDEE7EF3F4C63B7238EFC71F197FE4EDFE435CFFF05A5B21CA816D8CF2FB93BF03BC5347E37A5F66A8EB3EA3192C29FDE66D6E18516E4AFC60C7C95BE518721B57B6B77D3EB223DD3CFEAB6378110D9F53
	ED1C235B7805763969DC50112CDDF13FD93B15C95B19734BCE5A2E4C145F351DF3637775246D9C7DE6F1FD0D08F1FD864A268196GE4G2C86D88FD06466F13D7CD403F0E5392BC5DBA34487B449E96F1A1BFEA712362BF01B35FB4DB9B7FE7B1236EBB765774BB9725B18341D47145F6D5DF36317193C1DA93F691E39717BD252F68B627851F96DB3ECAFAF9D4AB2818837328AF1EB61FB231B67058FEAF0ED3067A47F7E08FD635E0916E3EB2E74BCF3B6C07AE6F8623167F361111FGCFF118B918781C69A703
	6667CC778F1A1FB3BDB4788A61010671D6GAFFFECF076F3281F8DA6BE075ABF14D84E0AA1F3B9EB074C65348D4DAE27E3A8111CEFB2FBBEEA41F9DBF8B2287A8392A3CDE33DB40DF729A0F375CD86A92DA175C87C7CDF60397201D174291A13BCA7BEB1D4A54C4C0938732A04B99137A406FB47467409CEFFC872F2393307935B6871E1F39B7DEC585CC6CF8D4FEE23DF8DA73291796E8D0D98D3AE50CD56E3E8894361D9871ECF906EC0A37AB856F1A50805BF850807037158BCCBAC7CA9C0BE710C0A6753B34E
	C4FBE0E6AA707D5F6DAD05DA9D172A382AF4DB3EFFB6F13FD51EB56F373A3338FF96D1A643DEA93EEF1A4E6E564B2263A6CEB6D19C6D9E8DEB2F0A61F1E9C65F763076D416287D2933385FBE6A7779435A89A359617E57F6B03F09C424889B40F1CFC8537FB1DD7FB6D531D76AFDB25E06EDCD66487D5E30A24BF892A370B95C3A1A822DDFD3CE72F5548FEAB19ED740779A008D3A3F5DDAE79CA38A86E27DEB4F40CCFF233577B236AE2B982035F71E0E3F0761ED86BC2D496DC7789D44F6A84B9860717C0D1144
	7D7A6D9173FE4D98C95C2FF70EA46F574C11447D7A1311647D3AEAA4D1DCD39F5E587A927761313092F0D5EB3477FA926B7A2E47DC57B2CFE2DD2BBD49F535F9926BEA77A45755690977E152F142EE7453AABD0F3C5AE4B667A8BA15B8EF913DB20711DE350EFF5A91EB4B9A8DFFF71B31FE0D4EBF47147F3E247CABBBE2799C56707F51E14CC7517B88BF6DB4706F2E54ED1AAAA66E631BC473BE7E2618380F534564FD3CCCCC5C474B4564FDFC1FB83BBFDF6A0A358D3FFF516D0C45D7E978373B0D36295269BC
	559DDB3FD6435F51930B2F53707FD501670332F8D119D485E149A94CA33BAA79BED2BDFC3BG2681661F227DA44A154B26105ED8D88C009AEC701FB708ED1F2862ED57C3FD81F0A5G4DG1EC13E5F3D065D9DB1E455CA3251BF0CEEB75AA8E64CD2585DAAFD37422E04AE6AE56B8F9745173548B2796AF3B55CD73300FF4EE02C0FEE98CC14FB7757210C1BCE476F25D7B3FC7169783D74FBEB79DE7ABEA8CBB94D6774BFE67BED6B61FB5B9047ED2D67737C12A1BEC65CFDC45063EB05613D630A6FC44EFDDEB2
	7A0572A01D7733BD8F87686B566D70DB9D679962E7E70D15FC8C138137E4087B5B5365B17BD7F86AE83536AAF83F48C94ECB6372551585DA7B1CA1BEAE5F49E8968FE7971681B80679597D743B7C6CBE635B0B05078B4D6FC82CF5FFECB9B32C5DBD9A0E6E4727861E34904F3CEA4EB3934A1F9FC65C21EA6399A17A458FAD34BFC465E2CCB96686BC4B0E16FFAD269C65ED58E4AC7F7B18724D0676245B036FF05DF29FDCAA48BF38AA61EF9AECE7345FC3E84F856BEE48CB7B7D25FC6AA361B06A1686E519E7F8
	3F6D28C55CEA785E28634E57F21F5EF6167B7421BADCD7546B5FC758B75E75216F7BEA0DFE0775BEFF5F7E67989E1F773C1B724FA3F55897DEC9167BA56D1EDBDB90D7D8BE5FD9F62EDD47777B5C70CDF9726DFA1EA7538117891083108B30E704674966A6365607414CB6023F1559FD9EFB165E9BAE883A0E764A397D1E62449C5BDD73195BD16E3C3A8A63F05EC87CFC76AB8CBFFFA4FEBE7BC3BD9724C1D96E88BF17F8CBC65BF94EE597DE86C88548GD885706CB9BE679D70D688C976755CCA488913885930
	2CA47DDE2C1E5B6FCC6E5B42E36E2EFBEE9316FF491170AED06EB97275BD9EFA167E6206BCFC12FE4B82281F934AA6G6683AC81D88E305A43753B592B1D7361329CD66122F7025D36529779FEE9AA0A46811AFC6A75436C5CDD8A2BFE2FA853905CAA06DD0A6C77DE9448DF2BD84CE3BED967613E78EEAD4AB621047D171420BA33252035EDE5F9G4F03760950BFA2627E2C36E4965BCD08E48362938E605CAEBAB5203B17D476E2BD0B077024C476DEB4E5BCCF2D5F7A672677EC91D6EABC56E84F5CDDB766D1
	6EBEDE4FF3F751A84F5D137571397B2BF9465CFBEBFDFC6E46765172476B63F3F7EC7B170E4467EE5E1E74FAE85828D7510D320547C53CDE6D23240FFBB47EF796DF46A921962F2906773F2D7C626793EB3ED09E647F6F490957G6532799A20C6B65FE3324A336F87364A097783BBE4633DF40A5F3FD030FFD320CF16GE48164GEC9865317B187C8701452EC95C367946F18D023FD6D2B97FFB8209F53DAF107CB7CE8F84706E6528222ACAG69F37C6AG76172A049812F00F8450C6E17F7804CAD5B57C9381
	0C93D99AE3E80A475F9F46B5E14CCD3AE860168F347539BB6CAD4EF37D763EDE273335256FDCCB7F391E36B6C71B035A1F2E2D1D25FDFBEFDF47D0EF0F5B1C824F97B757627A45AE61DD939EBFD3924F43FB3E7BADE14F5EE68B0F51F8DF5A6625382977C663A71A071F8AB11D21AC454B7BCE8E623D5481717C4CDDD1C03A42CA88D26BC54232ABC8DACCB86BBF6B18C2B22C7DC0507B607B41F79271864F6984BB524DAE37B82E3779321258F72ED3127B4ECD8A3B4B8BFD6E3E985256320B943439B2B69611F4
	9F3DC5A952F19D9117927163B408F430C4CA60198EB6DC8970AC00604BAEAC86A883E8869874F17B5605CAE731AFCD78D9FC04C5BF7FCD44AFC3AF9B63174C2E49752FA10CC57D6EA03B26CC978F29FCFBA431FD0AA3496D73E644BC367E981999DB25917358AA0F98E3CB5BF7526806754DDB6D770A911C4753FA3DDB8C0A63AC6F506F2FF307D05E0CDF891BD5E1932E02E1D1040F92D88D6BF710BEB50EAD9B2F76A69CB39D1276FB8AA97E8E1275AC4A1B8A7825926F37EB4E9C1C45B77B8322AC432C61C2CB
	5007FE5AFAD11522797568D9249F61F534C2AABB46761D4F9B5A7CAFD0CB87881A0B515F8A95GG48BEGGD0CB818294G94G88G88GC3FBB0B61A0B515F8A95GG48BEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC495GGGG
**end of data**/
}
}