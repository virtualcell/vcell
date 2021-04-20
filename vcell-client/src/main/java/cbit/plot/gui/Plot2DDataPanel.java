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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.NonEditableDefaultTableModel;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.SimpleUserMessage;
import org.vcell.util.gui.SpecialtyTableRenderer;

import com.google.common.io.Files;

import cbit.plot.Plot2D;
import cbit.vcell.client.UserMessage;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
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
			getNonEditableDefaultTableModel1().setDataVector((Vector<Object>)null,(Vector<Object>)null);			
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


private synchronized void copyCells(CopyAction copyAction) {
	copyCells0(copyAction,false);
}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2001 4:52:52 PM)
 * @param actionCommand java.lang.String
 * @return java.lang.String
 */
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
		if(!firstColName.equals(ReservedVariable.TIME.getName()))
		{
			bHistogram = true;
		}

		StringBuffer buffer = new StringBuffer();
		//check if selected first column is time.
		boolean bHasTimeColumn = false;
		double[] hdf5Times = null;
		int hdf5FileID = -1;//Used if HDF5 format
		File hdf5TempFile = null;
		
		if(isHDF5) {
			try {
				//Check if multiple columns with time (happens when viewing 'Time Plot with Multiple Parameter Value-sets')
				ArrayList<Integer> nonTColumns = new ArrayList<Integer>();
				for(int i=0;i<columns.length;i++) {
					String selectedColName = getScrollPaneTable().getColumnName(columns[i]);
					if(selectedColName.equals(ReservedVariable.TIME.getName())){
						bHasTimeColumn = true;
						if(hdf5Times == null) {
							hdf5Times = new double[rows.length];
							for(int j=0;j<rows.length;j++) {
								hdf5Times[j] = new Double(getScrollPaneTable().getValueAt(rows[j], columns[i]).toString()).doubleValue();	
							}
						}else {
							for(int j=0;j<rows.length;j++) {
								Double val = new Double(getScrollPaneTable().getValueAt(rows[j], columns[i]).toString());
								if(val != hdf5Times[j]) {
									DialogUtils.showErrorDialog(this, "Found multiple time column selections with non-matching values");
									return;
								}
							}						
						}
					}else {
						nonTColumns.add(i);
					}
				}
				double[] hdfValues = null;
				if(nonTColumns.size() > 0) {
					hdfValues = new double[rows.length*nonTColumns.size()];
					int cnt=0;
					for(int j=0;j<rows.length;j++) {
						for(int i=0;i<nonTColumns.size();i++) {
							Double val = null;
							final Object varValObj = getScrollPaneTable().getValueAt(rows[j], columns[nonTColumns.get(i)]);
							if(varValObj == null) {
								if(bHistogram) {
									val = new Double(-1);
								}else {
									DialogUtils.showErrorDialog(this, "Missing values only allowed for 'histogram' datasets");
									return;
								}
							}else {
								val = new Double(varValObj.toString());	
							}
							
							hdfValues[cnt] = val;
							cnt++;
						}
					}
				}
				if(hdf5Times == null && hdfValues == null) {
					DialogUtils.showWarningDialog(this, "Select cells to export in HDF5 format.");
					return;
				}
				hdf5TempFile = File.createTempFile("pde", ".hdf5");
//				System.out.println("/home/vcell/Downloads/hdf5/HDFView/bin/HDFView "+hdf5TempFile.getAbsolutePath());
				hdf5FileID = H5.H5Fcreate(hdf5TempFile.getAbsolutePath(), HDF5Constants.H5F_ACC_TRUNC,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
				if( hdf5Times != null) {
					long[] dimsTime = new long[] {hdf5Times.length};
					int hdf5DataspaceIDTime = H5.H5Screate_simple(dimsTime.length, dimsTime, null);
					int hdf5DatasetIDTime = H5.H5Dcreate(hdf5FileID, "Times (rows)",HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDTime,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
					H5.H5Dwrite_double(hdf5DatasetIDTime, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, hdf5Times);
					H5.H5Dclose(hdf5DatasetIDTime);
					H5.H5Sclose(hdf5DataspaceIDTime);
				}
				if( hdfValues != null) {
					long[] dimsValues = new long[] {rows.length,nonTColumns.size()};
					int hdf5DataspaceIDValues = H5.H5Screate_simple(dimsValues.length, dimsValues, null);
					int hdf5DatasetIDValues = H5.H5Dcreate(hdf5FileID, "DataValues",HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDValues,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
					H5.H5Dwrite_double(hdf5DatasetIDValues, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, hdfValues);
					H5.H5Dclose(hdf5DatasetIDValues);
					H5.H5Sclose(hdf5DataspaceIDValues);
				}

				int FIXED_STR_LEN = 100;
				StringBuffer colNamesSB = new StringBuffer();
				final int numColDescr = (hdf5Times != null?1:0)+nonTColumns.size();
				for(int i=0;i<numColDescr;i++) {
					String currColName = null;
					if(i==0 && hdf5Times != null) {
						currColName = "t";
					}else {
						final Integer realColIndex = nonTColumns.get(i-(hdf5Times != null?1:0));
						SymbolTableEntry ste =  getPlot2D().getPlotDataSymbolTableEntry(columns[realColIndex]);
						currColName = /*( ste != null?"(Var="+(ste.getNameScope() != null?ste.getNameScope().getName()+"_":"")+ste.getName()+") ":"")+*/
								getScrollPaneTable().getColumnName(columns[realColIndex]);
					}
					colNamesSB.append(StringUtils.rightPad(currColName,FIXED_STR_LEN));
				}
				long[] dimsCoord = new long[] {numColDescr};
				int h5tcs1 = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
				H5.H5Tset_size(h5tcs1, FIXED_STR_LEN);
				int hdf5DataspaceIDCoord = H5.H5Screate_simple(dimsCoord.length, dimsCoord, null);
				int hdf5DatasetIDCoord = H5.H5Dcreate(hdf5FileID, "DataName (columns)",h5tcs1, hdf5DataspaceIDCoord,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
				final byte[] bytes = colNamesSB.toString().getBytes();
				H5.H5Dwrite(hdf5DatasetIDCoord, h5tcs1, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, bytes);
				H5.H5Dclose(hdf5DatasetIDCoord);
				H5.H5Sclose(hdf5DataspaceIDCoord);
				H5.H5Tclose(h5tcs1);
	
				if(hdf5DescriptionText != null) {
					String attrText = hdf5DescriptionText;
					int h5attrcs1 = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
					H5.H5Tset_size(h5attrcs1, attrText.length());
					int dataspace_id = H5.H5Screate (HDF5Constants.H5S_SCALAR);
					int attribute_id = H5.H5Acreate (hdf5FileID, "Model/App/Simulation Info", h5attrcs1, dataspace_id, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
					H5.H5Awrite (attribute_id, h5attrcs1, attrText.getBytes());
					H5.H5Sclose(dataspace_id);
					H5.H5Aclose(attribute_id);
					H5.H5Tclose(h5attrcs1);
				}
				
				if(hdf5FileID != -1) {
					H5.H5Fclose(hdf5FileID);
					hdf5FileID = -1;
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
//								System.out.println("/home/vcell/Downloads/hdf5/HDFView/bin/HDFView "+destinationFile.getAbsolutePath());
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
				}
				return;
			}finally {
				if(hdf5FileID != -1) {try{H5.H5Fclose(hdf5FileID);}catch(Exception e){e.printStackTrace();}}
				if(hdf5TempFile != null && hdf5TempFile.exists()) {try{hdf5TempFile.delete();}catch(Exception e){e.printStackTrace();}}
			}
		}else {
			String selectedFirstColName = getScrollPaneTable().getColumnName(columns[0]);
			if(selectedFirstColName.equals(ReservedVariable.TIME.getName())){
				bHasTimeColumn = true;
			}
		}
		SymbolTableEntry[] symbolTableEntries = new SymbolTableEntry[c - (bHasTimeColumn?1:0)];
		Expression[] resolvedValues = new Expression[symbolTableEntries.length];
		//String[] dataNames = new String[symbolTableEntries.length];//don't include "t" for SimulationResultsSelection
		// if copying more than one cell, make a string that will paste like a table in spreadsheets
		// also include column headers in this case
		for (int i = 0; i < c; i++){
			//this if condition is dangerous, because it assumes that "t" appears only on column idx 0, other column numbers should be
			//greater than 0. However, histogram doesn't have "t" and there is sth. else in column 0 of the table.
			if(!bHistogram && (!bHasTimeColumn || i>0)){ 
				//dataNames[i-(bHasTimeColumn?1:0)] = getScrollPaneTable().getColumnName(columns[i]);
				symbolTableEntries[i-(bHasTimeColumn?1:0)] = null;
				if(getPlot2D().getSymbolTableEntries() != null){
					SymbolTableEntry ste =  getPlot2D().getPlotDataSymbolTableEntry(columns[i]);
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
			new VCellTransferable.ResolvedValuesSelection(symbolTableEntries,null,resolvedValues,buffer.toString());
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
	}
}

/**
 * set speciality renderer on the scrolltable
 * @param str not null
 */
public void setSpecialityRenderer(SpecialtyTableRenderer str) {
	getScrollPaneTable().setSpecialityRenderer(str);
}

private String hdf5DescriptionText;
public void setHDF5DescriptionText(String descr) {
	this.hdf5DescriptionText = descr;
}
}
