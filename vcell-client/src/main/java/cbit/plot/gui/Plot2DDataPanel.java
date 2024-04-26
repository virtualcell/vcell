/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.plot.gui;

import cbit.plot.Plot2D;
import cbit.vcell.client.UserMessage;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.UiTableExporterToHDF5;
import cbit.vcell.solver.Simulation;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.UtilCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.NonEditableDefaultTableModel;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.SpecialtyTableRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Insert the type's description here.
 * Creation date: (4/19/2001 12:33:58 PM)
 * @author: Ion Moraru
 */
public class Plot2DDataPanel extends JPanel {
	private static final long serialVersionUID = org.vcell.util.Serial.serialFromSVNRevision("$Rev$");
	private static final Logger LG = LogManager.getLogger(Plot2DDataPanel.class);

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == Plot2DDataPanel.this.getJMenuItemCopy()) 
				copyCells(CopyAction.copy);
			else if (e.getSource() == Plot2DDataPanel.this.getJMenuItemCopyAll()) 
				copyCells(CopyAction.copyall);
			else if (e.getSource() == Plot2DDataPanel.this.getJMenuItemCopyRow()) 
				copyCells(CopyAction.copyrow);
			else if (e.getSource() == Plot2DDataPanel.this.getJMenuItemExportHDF5()) 
				exportHDF5();
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == getScrollPaneTable() && e.isPopupTrigger()) 
				showPopupMenu(e, getJPopupMenu1());
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == getScrollPaneTable() && e.isPopupTrigger()) 
				showPopupMenu(e, getJPopupMenu1());
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == Plot2DDataPanel.this && (evt.getPropertyName().equals("plot2D"))) 
				connPtoP2SetTarget();
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == Plot2DDataPanel.this.getplot2D1()) 
				connEtoM2(e);
		};
	}
	private Plot2D fieldPlot2D = new Plot2D(null,null,null, null);
	private boolean ivjConnPtoP2Aligning = false;
	private Plot2D ivjplot2D1 = null;
	private ScrollTable ivjScrollPaneTable = null;
	private NonEditableDefaultTableModel ivjNonEditableDefaultTableModel1 = null;
	private Simulation simulation = null;
	private JMenuItem ivjJMenuItemCopy = null;
	private JPopupMenu ivjJPopupMenu1 = null;
	private JMenuItem ivjJMenuItemCopyAll = null;
	private JMenuItem ivjJMenuItemCopyRow = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private static enum CopyAction {copy,copyrow,copyall};

/**
 * Plot2DDataPanel constructor comment.
 */
public Plot2DDataPanel() {
	super();
	initialize();
}

private void exportHDF5() {
//	int r = getScrollPaneTable().getSelectedRowCount();
//	int c = getScrollPaneTable().getSelectedColumnCount();
//	int[] rows = getScrollPaneTable().getSelectedRows();
//	int[] columns = getScrollPaneTable().getSelectedColumns();
//	System.out.println("rcount "+r+" ccount "+c+" rlen"+rows.length+" clen"+columns.length);
	copyCells0(CopyAction.copy,true);
	
//	ArrayList<Double> hdf5Times = new ArrayList<Double>();
//	//Check if multiple columns with time (happens when viewing 'Time Plot with Multiple Parameter Value-sets')
//	for(int i=0;i<columns.length;i++) {
//		String selectedColName = getScrollPaneTable().getColumnName(columns[0]);
//		if(selectedColName.equals(ReservedVariable.TIME.getName())){
////			bHasTimeColumn = true;
//			if(hdf5Times.size()==0) {
//				for(int j=0;j<rows.length;j++) {
//					hdf5Times.add(new Double(getScrollPaneTable().getValueAt(rows[i], columns[j]).toString()));	
//				}
//			}else {
//				for(int j=0;j<rows.length;j++) {
//					Double val = new Double(getScrollPaneTable().getValueAt(rows[i], columns[j]).toString());
//					if(val != hdf5Times.get(j)) {
//						DialogUtils.showErrorDialog(this, "Found multiple time column selections with non-matching values");
//						return;
//					}
//				}						
//			}
//		}
//	}

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
 * connEtoM1:  (plot2D1.this --> DefaultTableModel1.setDataVector([[Ljava.lang.Object;[Ljava.lang.Object;)V)
 * @param value cbit.plot.Plot2D
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(Plot2D value) {
	try {
		// user code begin {1}
		// user code end
		if (getplot2D1() != null) {
			getNonEditableDefaultTableModel1().setDataVector(getplot2D1().getVisiblePlotDataValuesByRow(), getplot2D1().getVisiblePlotColumnTitles());
		}else{
			getNonEditableDefaultTableModel1().setDataVector((Object [][])null,(Object [])null);			
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
		if (getplot2D1() != null) {
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
			copyCells(CopyAction.copy);
		}
	}, KeyStroke.getKeyStroke("ctrl C"), WHEN_IN_FOCUSED_WINDOW);
	registerKeyboardAction(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			copyCells(CopyAction.copyall);
		}
	}, KeyStroke.getKeyStroke("ctrl K"), WHEN_IN_FOCUSED_WINDOW);
}

public void setSimulation(Simulation simulation) {
	this.simulation = simulation;
}

private synchronized void copyCells(CopyAction copyAction) {
	copyCells0(copyAction,false);
}

private synchronized void copyCells0(CopyAction copyAction,boolean isHDF5) {
	try{
		int r = 0;
		int c = 0;
		int[] rows = new int[0];
		int[] columns = new int[0];
		if (copyAction == CopyAction.copy) {
			r = getScrollPaneTable().getSelectedRowCount();
			c = getScrollPaneTable().getSelectedColumnCount();
			rows = getScrollPaneTable().getSelectedRows();
			columns = getScrollPaneTable().getSelectedColumns();
		}
		else if (copyAction == CopyAction.copyall) {
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
		else if (copyAction == CopyAction.copyrow) {
			r = getScrollPaneTable().getSelectedRowCount();
			if (r != 1) {
				LG.warn("only expected one selected row, but " + r + " selected");
			}
			rows = getScrollPaneTable().getSelectedRows();
			c = getScrollPaneTable().getColumnCount();
			columns = new int[c];
			for (int i = 0; i < columns.length; i++){
				columns[i] = i;
			}
		}
		//make sure there is at least a table cell is selected
		if(rows.length < 1 || columns.length < 1)
		{
			throw new Exception("No table cell is selected.");
		}

		//check if it is histogram (check name of the table first column name)
		boolean bHistogram = false;
		String firstColName = getScrollPaneTable().getColumnName(0);
		String blankCellValue = "-1";
		if(!firstColName.equals((xVarColumnName==null?ReservedVariable.TIME.getName():xVarColumnName)))
		{
			bHistogram = true;
		}

		StringBuffer buffer = new StringBuffer();
		//check if selected first column is time.
		boolean bHasTimeColumn = false;
		
		if(isHDF5) {
			if(bHistogram) {
				try {
					String result = DialogUtils.showInputDialog0(this, "Enter value to use if histogram bin has no values", blankCellValue);
					blankCellValue = result;
				} catch (UtilCancelException e) {
					return;
				}
			}
			int columnCount = getScrollPaneTable().getColumnCount();
			int rowCount = getScrollPaneTable().getRowCount();
			String[] columnNames = new String[columnCount];
			for (int i=0; i<columnCount; i++){
				columnNames[i] = getScrollPaneTable().getColumnName(i);
			}
			Object[][] rowColValues = new Object[rowCount][columnCount];
			for (int i=0; i<rowCount; i++){
				for (int j=0; j<columnCount; j++){
					rowColValues[i][j] = getScrollPaneTable().getValueAt(i, j);
				}
			}

			File hdf5TempFile = UiTableExporterToHDF5.exportTableToHDF5(bHistogram, blankCellValue, columns, rows, xVarColumnName, hdf5DescriptionText, columnNames, paramScanParamNames, paramScanParamValues, rowColValues);

			while(true) {
				JFileChooser jfc = new JFileChooser();
				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File destinationFile = jfc.getSelectedFile();
					try {
						if(destinationFile.exists()) {
							String retval = DialogUtils.showWarningDialog(this, "Overwrite exiting File...", destinationFile.getAbsolutePath()+"exists,\ndo you want to overwrite?",
									new String[] {UserMessage.OPTION_YES, UserMessage.OPTION_NO, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CANCEL) ;
							if(retval == null || retval.equals(UserMessage.OPTION_CANCEL)) {
								break;
							}else if(retval.equals(UserMessage.OPTION_NO)) {
								continue;
							}
						}
						Files.copy(hdf5TempFile, destinationFile);
//							System.out.println("/home/vcell/Downloads/hdf5/HDFView/bin/HDFView "+destinationFile.getAbsolutePath());
						break;
					} catch (Exception e) {
						e.printStackTrace();
						DialogUtils.showErrorDialog(this, "Error saving from "+hdf5TempFile.getAbsolutePath()+" to "+destinationFile.getAbsolutePath()+"\n"+e.getMessage());
						break;
					}
				}else {
					break;
				}
			}
			return;

		}else {		// not HDF5
			String selectedFirstColName = getScrollPaneTable().getColumnName(columns[0]);
			if(selectedFirstColName.equals((xVarColumnName==null?ReservedVariable.TIME.getName():xVarColumnName))){
				bHasTimeColumn = true;
			}
		}
		SymbolTableEntry[] tableSymbolTableEntries = new SymbolTableEntry[c - (bHasTimeColumn?1:0)];
		Expression[] resolvedValues = new Expression[tableSymbolTableEntries.length];
		//String[] dataNames = new String[symbolTableEntries.length];//don't include "t" for SimulationResultsSelection
		// if copying more than one cell, make a string that will paste like a table in spreadsheets
		// also include column headers in this case
		for (int i = 0; i < c; i++) {
			String suffix = (i==c-1?"":"\t");
			String columnName = getScrollPaneTable().getColumnName(columns[i]);
			//this if condition is dangerous, because it assumes that "t" appears only on column idx 0, other column numbers should be
			//greater than 0. However, histogram doesn't have "t" and there is sth. else in column 0 of the table.
			if(!bHistogram && (!bHasTimeColumn || i>0)) {
				//dataNames[i-(bHasTimeColumn?1:0)] = getScrollPaneTable().getColumnName(columns[i]);
				tableSymbolTableEntries[i-(bHasTimeColumn?1:0)] = null;
				SymbolTableEntry ste = null;
				if(getPlot2D().getSymbolTableEntries() != null) {
					ste = getPlot2D().getPlotDataSymbolTableEntry(columns[i]);
				}
				tableSymbolTableEntries[i-(bHasTimeColumn?1:0)] = ste;
				buffer.append(
					(ste != null?"(Var="+(ste.getNameScope() != null?ste.getNameScope().getName()+"_":"")+ste.getName()+") ":"")+
					columnName + suffix);
			} else {
				buffer.append(columnName + suffix);
			}
		}
		for (int i = 0; i < r; i++){
			buffer.append("\n");
			for (int j = 0; j < c; j++){
				Object cell = getScrollPaneTable().getValueAt(rows[i], columns[j]);
				cell = cell != null ? cell : "";
				if(((r+c)==2)){// single table cell copy, just the value
					buffer = new StringBuffer(cell.toString());
				}else{
					buffer.append(cell.toString() + (j==c-1?"":"\t"));
				}
				if(!cell.equals("") && (!bHasTimeColumn || j>0) ){
					resolvedValues[j-(bHasTimeColumn?1:0)] = new Expression(((Double)cell).doubleValue());
				}
			}
		}


		VCellTransferable.ResolvedValuesSelection rvs =
			new VCellTransferable.ResolvedValuesSelection(tableSymbolTableEntries,null,resolvedValues,buffer.toString());
		VCellTransferable.sendToClipboard(rvs);
	}catch(Throwable e){
		e.printStackTrace();
		DialogUtils.showErrorDialog(Plot2DDataPanel.this, "Copy failed.  "+e.getMessage(), e);
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
			ivjJMenuItemCopy.setText("Copy Cells");
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

private JMenuItem ivjJMenuItemExportHDF5;
private javax.swing.JMenuItem getJMenuItemExportHDF5() {
	if (ivjJMenuItemExportHDF5 == null) {
		try {
			ivjJMenuItemExportHDF5 = new javax.swing.JMenuItem();
			ivjJMenuItemExportHDF5.setName("JMenuItemExportHDF5");
			ivjJMenuItemExportHDF5.setText("Export Selected cells as HDF5 file");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemExportHDF5;
}

private javax.swing.JMenuItem getJMenuItemCopyRow() {
	if (ivjJMenuItemCopyRow == null) {
		try {
			ivjJMenuItemCopyRow = new javax.swing.JMenuItem();
			ivjJMenuItemCopyRow.setName("JMenuItemCopyRow");
			ivjJMenuItemCopyRow.setText("Copy Rows");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopyRow;
}
private javax.swing.JPopupMenu getJPopupMenu1() {
	if (ivjJPopupMenu1 == null) {
		try {
			ivjJPopupMenu1 = new javax.swing.JPopupMenu();
			ivjJPopupMenu1.setName("JPopupMenu1");
			ivjJPopupMenu1.add(getJMenuItemCopy());
			ivjJPopupMenu1.add(getJMenuItemCopyRow());
			ivjJPopupMenu1.add(getJMenuItemCopyAll());
			ivjJPopupMenu1.add(getJMenuItemExportHDF5());
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
private ScrollTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new ScrollTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			ivjScrollPaneTable.setCellSelectionEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			/*
			RenderDoubleWithTooltip rdwtt = new RenderDoubleWithTooltip(); 
			ivjScrollPaneTable.setDefaultRenderer(Double.class,rdwtt);
			ivjScrollPaneTable.setDefaultRenderer(Object.class,rdwtt);
			ivjScrollPaneTable.setDefaultRenderer(Number.class,rdwtt);
			*/
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
	getJMenuItemCopyRow().addActionListener(ivjEventHandler);
	getJMenuItemExportHDF5().addActionListener(ivjEventHandler);
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
		add(getScrollPaneTable().getEnclosingScrollPane(), "Center");
		
		JLabel lblNewLabel = new JLabel("<html>To <b>Copy</b> table data or <b>Export</b> as HDF5, select rows/cells and use the right mouse button menu.</html>");
		add(lblNewLabel, BorderLayout.SOUTH);
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
	if (mouseEvent.isPopupTrigger()) {
		getJMenuItemCopy().setEnabled(getScrollPaneTable().getSelectedColumnCount() > 0);
		getJMenuItemCopyRow().setEnabled(getScrollPaneTable().getSelectedColumnCount() > 0);
		getJMenuItemExportHDF5().setEnabled(getScrollPaneTable().getSelectedColumnCount() > 0);
		menu.show(getScrollPaneTable(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
//		if(simulation.getMathDescription().isNonSpatialStoch()) {
//			getJMenuItemCopy().setEnabled(false);
//			getJMenuItemCopyRow().setEnabled(false);
//			getJMenuItemExportHDF5().setEnabled(false);
//		}
	}
}

/**
 * set speciality renderer on the scrolltable
 * @param str not null
 */
public void setSpecialityRenderer(SpecialtyTableRenderer str) {
	getScrollPaneTable().setSpecialityRenderer(str);
}

private String xVarColumnName;
public void setXVarName(String xVarColumnName) {
	this.xVarColumnName = xVarColumnName;
}

private String hdf5DescriptionText;
public void setHDF5DescriptionText(String descr) {
	this.hdf5DescriptionText = descr;
}
private String[] paramScanParamNames;
private Double[][] paramScanParamValues;
public void setHDF5ParamScanParamNames(String[] paramScanParamNames) {
	this.paramScanParamNames = paramScanParamNames;
}
public void setHDF5ParamScanParamValues(Double[][] paramScanParamValues) {
	this.paramScanParamValues = paramScanParamValues;
}

}
