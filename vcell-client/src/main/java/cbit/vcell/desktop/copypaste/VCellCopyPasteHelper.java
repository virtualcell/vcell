/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop.copypaste;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.table.*;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.parser.ExpressionException;
import org.vcell.client.logicalwindow.LWDialog;
import org.vcell.util.Compare;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.modelopt.ParameterMappingSpec;
import cbit.vcell.parser.Expression;
import org.vcell.util.Pair;
import org.vcell.util.gui.DialogUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.vcell.util.gui.ShortcutsWizard;

public class VCellCopyPasteHelper {
	private static final Logger lg = LogManager.getLogger(VCellCopyPasteHelper.class);
	private static final java.awt.Font monoFont = new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12);

	public VCellCopyPasteHelper() {
		super();
	}


	public static <T> void chooseApplyPaste(Component requester, java.util.List<PasteOperationDataSource<T>> rawChangeData){
		if (rawChangeData.isEmpty()) {
			PopupGenerator.showErrorDialog(requester, "Aborting paste: no data was provided for paste calculation.");
			return;
		}

		Stream<PasteOperationDataSource<T>> prunedStream = rawChangeData.stream().filter(PasteOperationDataSource::isProposedDifferentThanOriginal);
		Map<PasteOperationTableModelRow<T>, PasteOperationDataSource<T>> displayToSourceMapping = prunedStream.collect(
				Collectors.toMap(
					PasteOperationDataSource::createTableModelRow,
					Function.identity(),
					(v1, v2) -> v1, // we don't expect duplicates
					LinkedHashMap::new
				)
		);

		if (displayToSourceMapping.isEmpty()) {
			PopupGenerator.showInfoDialog(requester, "All valid paste values are equal to the destination values.\nNo paste needed.");
			return;
		}

		java.util.List<String[]> columnNamesOptions = displayToSourceMapping.values().stream().map(PasteOperationDataSource::getColumnNames).distinct().toList();
		String[] selectedColumnNames = columnNamesOptions.get(0);

		java.util.List<PasteOperationTableModelRow<T>> selectedRows = VCellCopyPasteHelper.showChoices(requester, displayToSourceMapping.keySet(), selectedColumnNames);
		if (selectedRows.isEmpty()) return;

		boolean allHaveSucceeded = true;
		StringBuilder statusMessages = new StringBuilder();

		Map<String, PasteOperationDataSource<T>> displayStringMapping = displayToSourceMapping.keySet().stream().collect(
				Collectors.toMap(
						PasteOperationTableModelRow::getDisplayString,
						displayToSourceMapping::get,
						(v1, v2) -> v1,
						LinkedHashMap::new
				)
		);

		for (PasteOperationTableModelRow<T> row : selectedRows) {
			allHaveSucceeded &= VCellCopyPasteHelper.performPasteOperation(row, displayStringMapping, statusMessages);
		}
		if (allHaveSucceeded) return;
		PopupGenerator.showErrorDialog(requester, "Paste Errors Detected:\n" + statusMessages);
	}

	private static <T> java.util.List<PasteOperationTableModelRow<T>> showChoices(Component requester, java.util.Collection<PasteOperationTableModelRow<T>> data, String[] columnNames){
		JPanel panel = new JPanel(new GridBagLayout());
		ShortcutsWizard wizard = new ShortcutsWizard(panel);

		PasteOperationTableModel<T> tableModel = new PasteOperationTableModel<>(data, columnNames);
		JTable tableContainer = VCellCopyPasteHelper.createTableContainer(tableModel);
		JScrollPane scrollPane = new JScrollPane(tableContainer);
		scrollPane.setName(tableModel.isMultipleChoice() ? "Choose Parameters to Paste" : "Confirm Paste to Parameter");
		scrollPane.setPreferredSize(tableContainer.getPreferredSize());
		panel.add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 20.0, 20.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		if (tableModel.isMultipleChoice()) {
			JButton button = wizard.configureSelectAll(wizard.createAction("selectAll", AsynchClientTask.TASKTYPE_SWING_BLOCKING, e -> {
				for (int i = 0; i < tableModel.getRowCount(); i++) {
					if (!(tableModel.getValueAt(i, 0) instanceof Boolean boolVal)) continue;
					if (!boolVal) tableModel.setValueAt(true, i, 0);
				}
			}), JButton.class);
			panel.add(button, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		panel.setName(scrollPane.getName());
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		Object result = VCellCopyPasteHelper.displaySelectionPane(requester, panel);
		if (!Objects.equals(JOptionPane.OK_OPTION, result)) return java.util.List.of();
		return tableModel.getSelectedRows();
	}

	private static <T> boolean performPasteOperation(PasteOperationTableModelRow<T> selectedRow,
	                                     Map<String, PasteOperationDataSource<T>> displayToSourceMapping,
	                                     StringBuilder statusMessages) {
		PasteOperationDataSource<T> source = displayToSourceMapping.get(selectedRow.getDisplayString());
		try {
			source.performChange();
		} catch (Exception e) {
			String exceptionMessage = e.getMessage() == null ? "" : "(" + e.getMessage() + ")";
			String error = String.format("(Failed) %s [cause: %s %s]", selectedRow.getDisplayString(), e.getClass().getName(), exceptionMessage);
			statusMessages.append(error).append("\n");
			return false;
		}
		statusMessages.append("(OK) ").append(selectedRow.getDisplayString()).append("\n");
		return true;
	}

	private static Object displaySelectionPane(Component requester, JComponent panelContents){
		JOptionPane selectionPane = new JOptionPane(panelContents, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
		selectionPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		LWDialog selectionDialog = DialogUtils.createDialog(requester, selectionPane, panelContents.getName());
		selectionDialog.setMinimumSize(selectionDialog.getPreferredSize());
		selectionDialog.setResizable(true);
		selectionDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		selectionDialog.setVisible(true);
		return selectionPane.getValue();
	}

	private static <T> JTable createTableContainer(PasteOperationTableModel<T> tableModel){
		JTable tableContainer = new JTable(tableModel, tableModel.getTableColumnModel());
		tableContainer.setRowSelectionAllowed(false);
		tableContainer.setColumnSelectionAllowed(false);
		tableContainer.setCellSelectionEnabled(false);

		// X Dim Size & Set Custom Cell Renderer & Editor
		TableColumnModel columnModel = tableContainer.getColumnModel();
		FontMetrics fontMetrics = tableContainer.getTableHeader().getFontMetrics(tableContainer.getFont());
		for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
			TableColumn column = columnModel.getColumn(columnIndex);
			TableCellRenderer tcr = tableContainer.getDefaultRenderer(tableModel.getColumnClass(columnIndex));
			if (tcr instanceof DefaultTableCellRenderer defaultTcr) defaultTcr.setHorizontalAlignment(JLabel.CENTER);
			column.setCellRenderer(tcr);

			String headerText = column.getHeaderValue().toString();
			int headerWidth = (int)(1.5 * fontMetrics.stringWidth(headerText));
			String longestTextInColumn = tableModel.getLongestColumnEntry(columnIndex);
			int maxTextWidth = (int)(1.5 * fontMetrics.stringWidth(longestTextInColumn));
			boolean headerIsLonger = headerWidth > maxTextWidth;
			column.setPreferredWidth(10 + (headerIsLonger ? headerWidth : maxTextWidth));
		}

		// Y Dim Size
		int rowHeight = tableContainer.getRowHeight();
		int rowCount = tableModel.getRowCount();
		//int headerHeight = tableContainer.getTableHeader().getHeight();
		int headerHeight = (int)(1.5 * tableContainer.getRowHeight());
		int tableYSize = (rowHeight * rowCount + headerHeight);
		Dimension newDim = new Dimension(tableContainer.getPreferredSize().width, tableYSize);
		tableContainer.setMinimumSize(newDim);
		tableContainer.setPreferredSize(newDim);
		return tableContainer;
	}
}
