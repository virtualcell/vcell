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
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serial;
import java.util.stream.IntStream;

/**
 * Creation date: (4/19/2001 12:33:58 PM)
 *
 * @author Ion Moraru
 */
public class Plot2DDataPanel extends JPanel {
	@Serial
	private static final long serialVersionUID = org.vcell.util.Serial.serialFromSVNRevision("$Rev$");
	private static final Logger lg = LogManager.getLogger(Plot2DDataPanel.class);

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == Plot2DDataPanel.this.getJMenuItemCopyCells()) Plot2DDataPanel.this.copyCells(CopyAction.COPY);
			else if (e.getSource() == Plot2DDataPanel.this.getJMenuItemCopyAll()) Plot2DDataPanel.this.copyCells(CopyAction.COPY_ALL);
			else if (e.getSource() == Plot2DDataPanel.this.getJMenuItemCopyRow()) Plot2DDataPanel.this.copyCells(CopyAction.COPY_ROW);
			else if (e.getSource() == Plot2DDataPanel.this.getJMenuItemExportHDF5()) Plot2DDataPanel.this.exportHDF5();
		}

		public void mouseClicked(java.awt.event.MouseEvent e) {}

		public void mouseEntered(java.awt.event.MouseEvent e) {}

		public void mouseExited(java.awt.event.MouseEvent e) {}

		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() != Plot2DDataPanel.this.getScrollPaneTable()) return;
			if (!e.isPopupTrigger()) return;
			Plot2DDataPanel.this.showPopupMenu(e, Plot2DDataPanel.this.getJPopupMenu1());
		}

		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() != Plot2DDataPanel.this.getScrollPaneTable()) return;
			if (!e.isPopupTrigger()) return;
			Plot2DDataPanel.this.showPopupMenu(e, Plot2DDataPanel.this.getJPopupMenu1());
		}

		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() != Plot2DDataPanel.this) return;
			if (!"plot2D".equals(evt.getPropertyName())) return;
			Plot2DDataPanel.this.pushUserFacingPlotToShadowPlot();
		}

		public void stateChanged(javax.swing.event.ChangeEvent e) {
			Plot2D shadowPlot = Plot2DDataPanel.this.getShadowPlot();
			if (e.getSource() != shadowPlot || null == shadowPlot) return;
			try {
				Plot2DDataPanel.this.getNonEditableDefaultTableModel1().setDataVector(
						shadowPlot.getVisiblePlotDataValuesByRow(),
						shadowPlot.getVisiblePlotColumnTitles()
				);
			} catch (Exception exception) {
				Plot2DDataPanel.this.handleException(exception);
			}
		}

	}

	private Plot2D userFacingPlot = new Plot2D(null, null, null, null);
	private boolean currentlyAligning = false;
	private Plot2D shadowPlot2D = null;
	private ScrollTable ivjScrollPaneTable = null;
	private NonEditableDefaultTableModel nonEditableDefaultTableModel = null;
	private JMenuItem copyMenuItem = null;
	private JPopupMenu popupMenu = null;
	private JMenuItem copyAllMenuItem = null;
	private JMenuItem copyRowMenuItem = null;
	IvjEventHandler eventHandler = new IvjEventHandler();

	private enum CopyAction {COPY, COPY_ROW, COPY_ALL}

	/**
	 * Plot2DDataPanel constructor comment.
	 */
	public Plot2DDataPanel() {
		super();
		this.initialize();
	}

	private void exportHDF5() {
//		int r = getScrollPaneTable().getSelectedRowCount();
//		int c = getScrollPaneTable().getSelectedColumnCount();
//		int[] rows = getScrollPaneTable().getSelectedRows();
//		int[] columns = getScrollPaneTable().getSelectedColumns();
//		System.out.println("rcount "+r+" ccount "+c+" rlen"+rows.length+" clen"+columns.length);
		this.copyCells0(CopyAction.COPY, true);

//		ArrayList<Double> hdf5Times = new ArrayList<Double>();
//		//Check if multiple columns with time (happens when viewing 'Time Plot with Multiple Parameter Value-sets')
//		for(int i=0;i<columns.length;i++) {
//			String selectedColName = getScrollPaneTable().getColumnName(columns[0]);
//			if(selectedColName.equals(ReservedVariable.TIME.getName())){
//	//			bHasTimeColumn = true;
//				if(hdf5Times.size()==0) {
//					for(int j=0;j<rows.length;j++) {
//						hdf5Times.add(new Double(getScrollPaneTable().getValueAt(rows[i], columns[j]).toString()));
//					}
//				}else {
//					for(int j=0;j<rows.length;j++) {
//						Double val = new Double(getScrollPaneTable().getValueAt(rows[i], columns[j]).toString());
//						if(val != hdf5Times.get(j)) {
//							DialogUtils.showErrorDialog(this, "Found multiple time column selections with non-matching values");
//							return;
//						}
//					}
//				}
//			}
//		}
	}

	/**
	 * connPtoP1SetTarget:  (DefaultTableModel1.this <--> ScrollPaneTable.model)
	 */
	private void connPtoP1SetTarget() {
		/* Set the target from the source */
		try {
			this.getScrollPaneTable().setModel(this.getNonEditableDefaultTableModel1());
			this.getScrollPaneTable().createDefaultColumnsFromModel();
		} catch (Exception exception) {
			this.handleException(exception);
		}
	}
	private void pushShadowPlotToUserFacingPlot() {
		/* Set the source from the target */
		try {
			if (!this.currentlyAligning) {
				this.currentlyAligning = true;
				if ((this.getShadowPlot() != null)) {
					this.setUserFacingPlot(this.getShadowPlot());
				}
				this.currentlyAligning = false;
			}
		} catch (Exception exception) {
			this.currentlyAligning = false;
			this.handleException(exception);
		}
	}

	private void pushUserFacingPlotToShadowPlot() {
		/* Set the target from the source */
		try {
			if (!this.currentlyAligning) {
				this.currentlyAligning = true;
				this.setShadowPlot(this.getUserFacingPlot());
				this.currentlyAligning = false;
			}
		} catch (Exception exception) {
			this.currentlyAligning = false;
			this.handleException(exception);
		}
	}

	private void controlKeys() {
		//TODO: Update to use new `ShortcutsWizard` class
		this.registerKeyboardAction(e -> Plot2DDataPanel.this.copyCells(CopyAction.COPY), KeyStroke.getKeyStroke("ctrl C"), WHEN_IN_FOCUSED_WINDOW);
		this.registerKeyboardAction(e -> Plot2DDataPanel.this.copyCells(CopyAction.COPY_ALL), KeyStroke.getKeyStroke("ctrl K"), WHEN_IN_FOCUSED_WINDOW);
	}

	private synchronized void copyCells(CopyAction copyAction) {
		this.copyCells0(copyAction, false);
	}

	private synchronized void copyCells0(CopyAction copyAction, boolean isHDF5) {
		try {
			int[] rows;
			int[] columns;
			if (copyAction == CopyAction.COPY) {
				rows = this.getScrollPaneTable().getSelectedRows();
				columns = this.getScrollPaneTable().getSelectedColumns();
			} else if (copyAction == CopyAction.COPY_ALL) {
				rows = IntStream.range(0, this.getScrollPaneTable().getRowCount()).toArray();
				columns = IntStream.range(0, this.getScrollPaneTable().getColumnCount()).toArray();
			} else if (copyAction == CopyAction.COPY_ROW) {
				int selectedRowCount = this.getScrollPaneTable().getSelectedRowCount();
				if (selectedRowCount != 1) lg.warn("only expected one selected row, but {} selected", selectedRowCount);
				rows = this.getScrollPaneTable().getSelectedRows();
				columns = IntStream.range(0, this.getScrollPaneTable().getColumnCount()).toArray();
			} else {
				throw new RuntimeException("unknown action: " + copyAction);
			}

			//make sure there is at least a table cell is selected
			if (rows.length < 1 || columns.length < 1) throw new Exception("No table cell is selected.");

			//check if it is histogram (check name of the table first column name)
			String firstColName = this.getScrollPaneTable().getColumnName(0);
			boolean bHistogram = !firstColName.equals((this.xVarColumnName == null ? ReservedVariable.TIME.getName() : this.xVarColumnName));
			String blankCellValue = "-1";

			StringBuilder buffer = new StringBuilder();
			//check if selected first column is time.
			boolean bHasTimeColumn = false;

			if (isHDF5) {
				if (bHistogram) {
					try {
						blankCellValue = DialogUtils.showInputDialog0(this, "Enter value to use if histogram bin has no values", blankCellValue);
					} catch (UtilCancelException e) {
						return;
					}
				}
				int columnCount = this.getScrollPaneTable().getColumnCount();
				int rowCount = this.getScrollPaneTable().getRowCount();
				String[] columnNames = new String[columnCount];
				for (int i = 0; i < columnCount; i++) {
					columnNames[i] = this.getScrollPaneTable().getColumnName(i);
				}
				Object[][] rowColValues = new Object[rowCount][columnCount];
				for (int i = 0; i < rowCount; i++) {
					for (int j = 0; j < columnCount; j++) {
						rowColValues[i][j] = this.getScrollPaneTable().getValueAt(i, j);
					}
				}

				File hdf5TempFile = UiTableExporterToHDF5.exportTableToHDF5(bHistogram, blankCellValue, columns, rows, this.xVarColumnName, this.hdf5DescriptionText, columnNames, this.paramScanParamNames, this.paramScanParamValues, rowColValues);

				while (true) {
					JFileChooser jfc = new JFileChooser();
					if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						File destinationFile = jfc.getSelectedFile();
						try {
							if (destinationFile.exists()) {
								String retval = DialogUtils.showWarningDialog(this, "Overwrite exiting File...", destinationFile.getAbsolutePath() + "exists,\ndo you want to overwrite?",
										new String[]{UserMessage.OPTION_YES, UserMessage.OPTION_NO, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_CANCEL);
								if (retval == null || retval.equals(UserMessage.OPTION_CANCEL)) {
									break;
								} else if (retval.equals(UserMessage.OPTION_NO)) {
									continue;
								}
							}
							Files.copy(hdf5TempFile, destinationFile);
//							System.out.println("/home/vcell/Downloads/hdf5/HDFView/bin/HDFView "+destinationFile.getAbsolutePath());
							break;
						} catch (Exception e) {
							e.printStackTrace();
							DialogUtils.showErrorDialog(this, "Error saving from " + hdf5TempFile.getAbsolutePath() + " to " + destinationFile.getAbsolutePath() + "\n" + e.getMessage());
							break;
						}
					} else {
						break;
					}
				}
				return;

			} else {        // not HDF5
				String selectedFirstColName = this.getScrollPaneTable().getColumnName(columns[0]);
				if (selectedFirstColName.equals((this.xVarColumnName == null ? ReservedVariable.TIME.getName() : this.xVarColumnName))) {
					bHasTimeColumn = true;
				}
			}
			int timeColumnIndexShift = bHasTimeColumn ? 1 : 0;
			SymbolTableEntry[] tableSymbolTableEntries = new SymbolTableEntry[columns.length - (timeColumnIndexShift)];
			Expression[] resolvedValues = new Expression[tableSymbolTableEntries.length];
			// if copying more than one cell, make a string that will paste like a table in spreadsheets
			// also include column headers in this case
			for (int i = 0; i < columns.length; i++) {
				String suffix = (i == columns.length - 1 ? "" : "\t");
				String columnName = this.getScrollPaneTable().getColumnName(columns[i]);
				//this if condition is dangerous, because it assumes that "t" appears only on column idx 0, other column numbers should be
				//greater than 0. However, histogram doesn't have "t" and there is sth. else in column 0 of the table.
				if (!bHistogram && (!bHasTimeColumn || i > 0)) {
					//dataNames[i-(bHasTimeColumn?1:0)] = getScrollPaneTable().getColumnName(columns[i]);
					int targetIndex = i - timeColumnIndexShift;
					tableSymbolTableEntries[targetIndex] = null;
					SymbolTableEntry ste = null;
					if (this.getUserFacingPlot().getSymbolTableEntries() != null) {
						ste = this.getUserFacingPlot().getPlotDataSymbolTableEntry(columns[i]);
					}
					tableSymbolTableEntries[targetIndex] = ste;
					buffer.append(ste != null ? "(Var=" + (ste.getNameScope() != null ? ste.getNameScope().getName() + "_" : "") + ste.getName() + ") " : "").append(columnName).append(suffix);
				} else {
					buffer.append(columnName).append(suffix);
				}
			}
			for (int row : rows) {
				buffer.append("\n");
				for (int j = 0; j < columns.length; j++) {
					Object cell = this.getScrollPaneTable().getValueAt(row, columns[j]);
					cell = cell != null ? cell : "";
					if (((rows.length + columns.length) == 2)) {// single table cell copy, just the value
						buffer = new StringBuilder(cell.toString());
					} else {
						buffer.append(cell).append(j == columns.length - 1 ? "" : "\t");
					}
					if (!cell.equals("") && (!bHasTimeColumn || j > 0)) {
						resolvedValues[j - (timeColumnIndexShift)] = new Expression((Double) cell);
					}
				}
			}


			VCellTransferable.ResolvedValuesSelection rvs =
					new VCellTransferable.ResolvedValuesSelection(tableSymbolTableEntries, null, resolvedValues, buffer.toString());
			VCellTransferable.sendToClipboard(rvs);
		} catch (Exception e) {
			lg.error(e);
			DialogUtils.showErrorDialog(Plot2DDataPanel.this, "Copy failed.  " + e.getMessage(), e);
		}
	}


	/**
	 * Return the JMenuItemCopy property value.
	 *
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemCopyCells() {
		if (this.copyMenuItem == null) {
			try {
				this.copyMenuItem = new javax.swing.JMenuItem();
				this.copyMenuItem.setName("JMenuItemCopy");
				this.copyMenuItem.setText("Copy Cells");
			} catch (Exception exception) {
				this.handleException(exception);
			}
		}
		return this.copyMenuItem;
	}


	/**
	 * Return the JMenuItemCopyAll property value.
	 *
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemCopyAll() {
		if (this.copyAllMenuItem == null) {
			try {
				this.copyAllMenuItem = new javax.swing.JMenuItem();
				this.copyAllMenuItem.setName("JMenuItemCopyAll");
				this.copyAllMenuItem.setText("Copy All");
			} catch (Exception exception) {
				this.handleException(exception);
			}
		}
		return this.copyAllMenuItem;
	}

	private JMenuItem ivjJMenuItemExportHDF5;

	private javax.swing.JMenuItem getJMenuItemExportHDF5() {
		if (this.ivjJMenuItemExportHDF5 == null) {
			try {
				this.ivjJMenuItemExportHDF5 = new javax.swing.JMenuItem();
				this.ivjJMenuItemExportHDF5.setName("JMenuItemExportHDF5");
				this.ivjJMenuItemExportHDF5.setText("Export Selected cells as HDF5 file");
			} catch (Exception exception) {
				this.handleException(exception);
			}
		}
		return this.ivjJMenuItemExportHDF5;
	}

	private javax.swing.JMenuItem getJMenuItemCopyRow() {
		if (this.copyRowMenuItem == null) {
			try {
				this.copyRowMenuItem = new javax.swing.JMenuItem();
				this.copyRowMenuItem.setName("JMenuItemCopyRow");
				this.copyRowMenuItem.setText("Copy Rows");
			} catch (Exception exception) {
				this.handleException(exception);
			}
		}
		return this.copyRowMenuItem;
	}

	private javax.swing.JPopupMenu getJPopupMenu1() {
		if (this.popupMenu == null) {
			try {
				this.popupMenu = new javax.swing.JPopupMenu();
				this.popupMenu.setName("JPopupMenu1");
				this.popupMenu.add(this.getJMenuItemCopyCells());
				this.popupMenu.add(this.getJMenuItemCopyRow());
				this.popupMenu.add(this.getJMenuItemCopyAll());
				this.popupMenu.add(this.getJMenuItemExportHDF5());
			} catch (Exception exception) {
				this.handleException(exception);
			}
		}
		return this.popupMenu;
	}

	/**
	 * Return the NonEditableDefaultTableModel1 property value.
	 *
	 * @return cbit.gui.NonEditableDefaultTableModel
	 */
	private org.vcell.util.gui.NonEditableDefaultTableModel getNonEditableDefaultTableModel1() {
		if (this.nonEditableDefaultTableModel == null) {
			try {
				this.nonEditableDefaultTableModel = new org.vcell.util.gui.NonEditableDefaultTableModel();
			} catch (Exception exception) {
				this.handleException(exception);
			}
		}
		return this.nonEditableDefaultTableModel;
	}


	/**
	 * Gets the plot2D property (cbit.plot.Plot2D) value.
	 *
	 * @return The plot2D property value.
	 * @see #setUserFacingPlot
	 */
	public Plot2D getUserFacingPlot() {
		return this.userFacingPlot;
	}

	private Plot2D getShadowPlot() {
		return this.shadowPlot2D;
	}

	/**
	 * Return the ScrollPaneTable property value.
	 *
	 * @return javax.swing.JTable
	 */
	private ScrollTable getScrollPaneTable() {
		if (this.ivjScrollPaneTable == null) {
			try {
				this.ivjScrollPaneTable = new ScrollTable();
				// The simulation results as numbers - the spreadsheet behind the plot. Named
				// because "ScrollPaneTable" is shared by seven other panels, and this is the
				// one a script reads a result out of. Note it holds its data even while the
				// plot view is showing, so it can be read without toggling the view.
				this.ivjScrollPaneTable.setName("PlotDataTable");
				this.ivjScrollPaneTable.setCellSelectionEnabled(true);
				this.ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			/*
			RenderDoubleWithTooltip rdwtt = new RenderDoubleWithTooltip(); 
			ivjScrollPaneTable.setDefaultRenderer(Double.class,rdwtt);
			ivjScrollPaneTable.setDefaultRenderer(Object.class,rdwtt);
			ivjScrollPaneTable.setDefaultRenderer(Number.class,rdwtt);
			*/
			} catch (Exception exception) {
				this.handleException(exception);
			}
		}
		return this.ivjScrollPaneTable;
	}

	/**
	 * Called whenever the part throws an exception.
	 *
	 * @param exception java.lang.Throwable
	 */
	private void handleException(Exception exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}


	/**
	 * Initializes connections
	 *
	 * @throws java.lang.Exception The exception description.
	 */
	private void initConnections() throws java.lang.Exception {
		this.addPropertyChangeListener(this.eventHandler);
		this.getScrollPaneTable().addMouseListener(this.eventHandler);
		this.getJMenuItemCopyCells().addActionListener(this.eventHandler);
		this.getJMenuItemCopyAll().addActionListener(this.eventHandler);
		this.getJMenuItemCopyRow().addActionListener(this.eventHandler);
		this.getJMenuItemExportHDF5().addActionListener(this.eventHandler);
		this.pushUserFacingPlotToShadowPlot();
		this.connPtoP1SetTarget();
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			this.setName("Plot2DDataPanel");
			this.setLayout(new java.awt.BorderLayout());
			this.setSize(541, 348);
			this.add(this.getScrollPaneTable().getEnclosingScrollPane(), "Center");

			JLabel lblNewLabel = new JLabel("<html>To <b>Copy</b> table data or <b>Export</b> as HDF5, select rows/cells and use the right mouse button menu.</html>");
			this.add(lblNewLabel, BorderLayout.SOUTH);
			this.initConnections();
			this.controlKeys();
		} catch (Exception exception) {
			this.handleException(exception);
		}
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 *
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
				}

			});
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
			aPlot2DDataPanel.setUserFacingPlot(Plot2DPanel.getSamplePlot2D());
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}


	/**
	 * Sets the plot2D property (cbit.plot.Plot2D) value.
	 *
	 * @param userFacingPlot The new value for the property.
	 * @see #getUserFacingPlot
	 */
	public void setUserFacingPlot(Plot2D userFacingPlot) {
		Plot2D oldValue = this.userFacingPlot;
		this.userFacingPlot = userFacingPlot;
		this.firePropertyChange("plot2D", oldValue, userFacingPlot);
	}

	private void setShadowPlot(Plot2D newValue) {
		if (newValue == this.shadowPlot2D) return;
		try {
			/* Stop listening for events from the current object */
			if (this.shadowPlot2D != null) this.shadowPlot2D.removeChangeListener(this.eventHandler);

			Plot2D oldValue = this.shadowPlot2D;
			this.shadowPlot2D = newValue;

			/* Listen for events from the new object */
			if (this.shadowPlot2D != null) this.shadowPlot2D.addChangeListener(this.eventHandler);

			this.pushShadowPlotToUserFacingPlot();
			if (this.shadowPlot2D != null) {
				this.getNonEditableDefaultTableModel1().setDataVector(this.getShadowPlot().getVisiblePlotDataValuesByRow(), this.getShadowPlot().getVisiblePlotColumnTitles());
			} else {
				this.getNonEditableDefaultTableModel1().setDataVector(null, (Object[]) null);
			}
			this.firePropertyChange("plot2D", oldValue, newValue);
		} catch (Exception exception) {
			this.handleException(exception);
		}
	}

	/**
	 * Comment
	 */
	private void showPopupMenu(MouseEvent mouseEvent, javax.swing.JPopupMenu menu) {
		if (!mouseEvent.isPopupTrigger()) return;
		JMenuItem cellCopy = this.getJMenuItemCopyCells();
		JMenuItem rowCopy = this.getJMenuItemCopyRow();
		JMenuItem exportHDF5 = this.getJMenuItemExportHDF5();
		int rowCount = this.getScrollPaneTable().getSelectedRowCount();
		int columnCount = this.getScrollPaneTable().getColumnCount();
		boolean anySelected = 0 < rowCount;
		boolean singleRow = 1 == rowCount;
		boolean singleColumn = 1 == columnCount;
		boolean singleSelection = singleRow && singleColumn;

		cellCopy.setText(singleSelection ? "Copy Cell" : "Copy Cells");
		cellCopy.setEnabled(anySelected);

		rowCopy.setText(singleRow ? "Copy Row" : "Copy Rows");
		rowCopy.setEnabled(anySelected);

		exportHDF5.setText(singleSelection ? "Export Selected Cell as HDF5 file" : "Export Selected Cells as HDF5 file");
		exportHDF5.setEnabled(anySelected);

		menu.show(this.getScrollPaneTable(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}

	/**
	 * set speciality renderer on the scrolltable
	 *
	 * @param str not null
	 */
	public void setSpecialityRenderer(SpecialtyTableRenderer str) {
		this.getScrollPaneTable().setSpecialityRenderer(str);
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
