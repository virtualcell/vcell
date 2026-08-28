/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.*;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.copypaste.PasteOperationDataSource;
import cbit.vcell.desktop.copypaste.PasteOperationMathOverrideDataSource;
import cbit.vcell.solver.ConstantArraySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.desktop.copypaste.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.MathOverrides;
import org.vcell.util.gui.ShortcutsWizard;

public class MathOverridesPanel extends JPanel {
	private static final Logger lg = LogManager.getLogger(MathOverridesPanel.class);
	private final EventHandler eventHandler = new EventHandler();

	private MathOverridesTableModel mathOverridesTableModel = null;
	private boolean isEditable = true;
	private ScrollTable scrollTable = null;
	private MathOverrides mathOverrides = null;
	private MathOverridesTableCellRenderer mathOverridesTableCellRenderer = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem copyAllMenuItem = null;
	private JMenuItem pasteMenuItem = null;
	private JMenuItem pasteAllMenuItem = null;
	private JPopupMenu rightClickMenu = null;
	private JLabel titleLabel = null;
	private JButton removeUnusedButton = null;
    private final Semaphore mathOverridesSynchronizationLock = new Semaphore(1);

	private enum ActionType {
		COPY,
		PASTE
	}

	class EventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (!(e.getSource() instanceof JMenuItem menuItem)) return;
			try {
				MathOverridesPanel.this.processPerformedAction(menuItem);
			} catch (Exception exception) {
				MathOverridesPanel.this.logException(exception);
			}
		}

		public void mouseClicked(java.awt.event.MouseEvent e) {}

		public void mouseEntered(java.awt.event.MouseEvent e) {}

		public void mouseExited(java.awt.event.MouseEvent e) {}

		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() != MathOverridesPanel.this.getScrollTable()) return;
			try {
				MathOverridesPanel.this.showPopupMenu(e);
			} catch (Exception exception) {
				MathOverridesPanel.this.logException(exception);
			}
		}

		public void mouseReleased(java.awt.event.MouseEvent e) {
			try {
				if (e.getSource() == MathOverridesPanel.this.getScrollTable())
					MathOverridesPanel.this.mouseReleasedOnScrollTable(e);
				if (e.getSource() == MathOverridesPanel.this.getScrollTable()) MathOverridesPanel.this.showPopupMenu(e);
			} catch (Exception exception) {
				MathOverridesPanel.this.logException(exception);
			}

		}

		public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if ("mathOverrides".equals(evt.getPropertyName())){
                if (evt.getSource() == MathOverridesPanel.this) MathOverridesPanel.this.pushMathOverridesToTableModel();
                else if (evt.getSource() == MathOverridesPanel.this.getMathOverridesTableModel()) MathOverridesPanel.this.pullMathOverridesFromTableModel();
            } else if ("editable".equals(evt.getPropertyName()) && evt.getSource() == MathOverridesPanel.this)
                MathOverridesPanel.this.updateEditableMode(MathOverridesPanel.this.getEditable());
		}

	}

	/**
	 * MathOverridesPanel constructor comment.
	 */
	public MathOverridesPanel() {
		super();
		this.initialize();
	}

	public void processPerformedAction(JMenuItem menuItemSelected) {
		JMenuItem item;
		if (menuItemSelected == (item = this.getCopyMenuItem())) this.copyCells(item.getActionCommand());
		else if (menuItemSelected == (item = this.getCopyAllMenuItem())) this.copyCells(item.getActionCommand());
		else if (menuItemSelected == (item = this.getPasteMenuItem())) this.pasteCells(item.getActionCommand());
		else if (menuItemSelected == (item = this.getPasteAllMenuItem())) this.pasteCells(item.getActionCommand());
	}

	private void setUpKeyBinds() {
        String COPY_TASK_NAME = "mathOverridesPanelKeybindCopy", PASTE_TASK_NAME = "mathOverridesPanelKeybindPaste";
        ShortcutsWizard wizard = new ShortcutsWizard(this);
        wizard.configureCopy(wizard.createAction(COPY_TASK_NAME, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING, this::copyKeypressPerformed));
        wizard.configurePaste(wizard.createAction(PASTE_TASK_NAME, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING, this::pasteKeypressPerformed));
	}

    private void copyKeypressPerformed(ActionEvent ignored) {
        this.processPerformedAction(this.determineActionType(ActionType.COPY));
    }

    private void pasteKeypressPerformed(ActionEvent ignored) {
        this.processPerformedAction(this.determineActionType(ActionType.PASTE));
    }

	private synchronized JMenuItem determineActionType(ActionType ACTION) {
		boolean nothingSelected = 0 == this.getScrollTable().getSelectedRowCount();
		return switch (ACTION) {
			case COPY -> nothingSelected ? this.getCopyAllMenuItem() : this.getCopyMenuItem();
			case PASTE -> nothingSelected ? this.getPasteAllMenuItem() : this.getPasteMenuItem();
		};
	}


	private void pushMathOverridesToTableModel() {
		// This was originally done by a fragile boolean locking system; behavior is otherwise preserved.
        if (!this.mathOverridesSynchronizationLock.tryAcquire()) return;
        try {
            this.getMathOverridesTableModel().setMathOverrides(this.getMathOverrides());
        } catch (Exception exception){
            this.logException(exception);
        } finally {
            this.mathOverridesSynchronizationLock.release();
        }
	}

    private void pullMathOverridesFromTableModel() {
        // This was originally done by a fragile boolean locking system; behavior is otherwise preserved.
        if (!this.mathOverridesSynchronizationLock.tryAcquire()) return;
        try {
           this.setMathOverrides(this.getMathOverridesTableModel().getMathOverrides());
        } catch (Exception exception){
            this.logException(exception);
        } finally {
            this.mathOverridesSynchronizationLock.release();
        }
    }

	public boolean getEditable() {
		return this.isEditable;
	}

	private javax.swing.JLabel getTitleLabel() {
        if (null != this.titleLabel) return this.titleLabel;
		try {
			this.titleLabel = new JLabel();
			this.titleLabel.setName("JLabelTitle");
			this.titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
			this.titleLabel.setText("Specify non-default parameter values or scan over a range of values:");
			this.titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			this.titleLabel.setFont(this.titleLabel.getFont().deriveFont(Font.BOLD));
		} catch (Exception exception) {
			this.logException(exception);
		}
		return this.titleLabel;
	}

	private javax.swing.JMenuItem getCopyMenuItem() {
        if (null != this.copyMenuItem) return this.copyMenuItem;
		try {
			this.copyMenuItem = new JMenuItem();
			this.copyMenuItem.setName("JMenuItemCopy");
			this.copyMenuItem.setText("Copy");
		} catch (Exception exception) {
			this.logException(exception);
		}
		return this.copyMenuItem;
	}

	private javax.swing.JMenuItem getCopyAllMenuItem() {
        if (null != this.copyAllMenuItem) return this.copyAllMenuItem;
		try {
			this.copyAllMenuItem = new JMenuItem();
			this.copyAllMenuItem.setName("JMenuItemCopyAll");
			this.copyAllMenuItem.setText("Copy All");
		} catch (Exception exception) {
			this.logException(exception);
		}
		return this.copyAllMenuItem;
	}

	private javax.swing.JMenuItem getPasteMenuItem() {
        if (null != this.pasteMenuItem) return this.pasteMenuItem;
		try {
			this.pasteMenuItem = new JMenuItem();
			this.pasteMenuItem.setName("JMenuItemPaste");
			this.pasteMenuItem.setText("Paste");
		} catch (Exception exception) {
			this.logException(exception);
		}
		return this.pasteMenuItem;
	}

	private javax.swing.JMenuItem getPasteAllMenuItem() {
        if (null != this.pasteAllMenuItem) return this.pasteAllMenuItem;
		try {
			this.pasteAllMenuItem = new JMenuItem();
			this.pasteAllMenuItem.setName("JMenuItemPasteAll");
			this.pasteAllMenuItem.setText("Paste All");
		} catch (Exception exception) {
			this.logException(exception);
		}
		return this.pasteAllMenuItem;
	}

	private javax.swing.JPopupMenu getRightClickMenu() {
        if (null != this.rightClickMenu) return this.rightClickMenu;
		try {
			this.rightClickMenu = new JPopupMenu();
			this.rightClickMenu.setName("JPopupMenu1");
			this.rightClickMenu.add(this.getCopyMenuItem());
			this.rightClickMenu.add(this.getCopyAllMenuItem());
			this.rightClickMenu.add(this.getPasteMenuItem());
			this.rightClickMenu.add(this.getPasteAllMenuItem());
		} catch (Exception exception) {
			this.logException(exception);
		}
		return this.rightClickMenu;
	}

	private ScrollTable getScrollTable() {
        if (null != this.scrollTable) return this.scrollTable;
		try {
			this.scrollTable = new ScrollTable();
			this.scrollTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			this.scrollTable.setName("JTableFixed");
		} catch (Exception exception) {
			this.logException(exception);
		}
		return this.scrollTable;
	}

	public MathOverrides getMathOverrides() {
		return this.mathOverrides;
	}

	private MathOverridesTableCellRenderer getMathOverridesTableCellRenderer() {
        if (null != this.mathOverridesTableCellRenderer) return this.mathOverridesTableCellRenderer;
		try {
			this.mathOverridesTableCellRenderer = new MathOverridesTableCellRenderer();
			this.mathOverridesTableCellRenderer.setName("MathOverridesTableCellRenderer1");
			this.mathOverridesTableCellRenderer.setText("MathOverridesTableCellRenderer1");
		} catch (Exception exception) {
			this.logException(exception);
		}
		return this.mathOverridesTableCellRenderer;
	}

	private MathOverridesTableModel getMathOverridesTableModel() {
        if (null != this.mathOverridesTableModel) return this.mathOverridesTableModel;
		try {
			this.mathOverridesTableModel = new MathOverridesTableModel(this.getScrollTable());
			this.mathOverridesTableModel.setEditable(true);
		} catch (Exception exception) {
			this.logException(exception);
		}
		return this.mathOverridesTableModel;
	}

	private void logException(java.lang.Exception exception) {
        String errMsg = String.format("Exception encountered in %s:",  this.getClass().getSimpleName());
        lg.warn(errMsg, exception);
	}

	private void initConnections() {
		this.getScrollTable().addPropertyChangeListener(this.eventHandler);
		this.addPropertyChangeListener(this.eventHandler);
		this.getMathOverridesTableModel().addPropertyChangeListener(this.eventHandler);
		this.getCopyMenuItem().addActionListener(this.eventHandler);
		this.getCopyAllMenuItem().addActionListener(this.eventHandler);
		this.getScrollTable().addMouseListener(this.eventHandler);
		this.getPasteMenuItem().addActionListener(this.eventHandler);
		this.getPasteAllMenuItem().addActionListener(this.eventHandler);
		try {
			this.getScrollTable().setModel(this.getMathOverridesTableModel());
			this.getScrollTable().createDefaultColumnsFromModel();
			this.getScrollTable().setDefaultEditor(ScopedExpression.class, new TableCellEditorAutoCompletion(this.getScrollTable()));
			this.getMathOverridesTableCellRenderer().setMathOverridesTableModel(this.getMathOverridesTableModel());
		} catch (Exception exception) {
			this.logException(exception);
		}
		this.pushMathOverridesToTableModel();
	}

	private void initialize() {
		try {
			this.setName("MathOverridesPanel");
			this.setLayout(new GridBagLayout());
			this.setSize(404, 262);

			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			this.add(this.getTitleLabel(), gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			this.add(this.getScrollTable().getEnclosingScrollPane(), gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			this.removeUnusedButton = new JButton();
			this.removeUnusedButton.setForeground(Color.red);
			this.removeUnusedButton.setText("remove unused parameter overrides");
			this.removeUnusedButton.addActionListener(e -> {
				MathOverrides mathOverrides = this.getMathOverrides();
				if (mathOverrides != null) {
					mathOverrides.removeUnusedOverrides();
					this.removeUnusedButton.setVisible(false);
				}
			});

			this.add(this.removeUnusedButton, gridBagConstraints);

			this.initConnections();
			this.getScrollTable().setDefaultRenderer(String.class, this.getMathOverridesTableCellRenderer());
			this.getScrollTable().setDefaultRenderer(ScopedExpression.class, this.getMathOverridesTableCellRenderer());
			this.setUpKeyBinds();
		} catch (java.lang.Exception exception) {
			this.logException(exception);
		}
	}

	private void copyCells(final String actionCommand) {
		AsynchClientTask task1 = new ValidateAndPerformSmartCopyAsynchClientTask(actionCommand);
		ClientTaskDispatcher.dispatch(this, new Hashtable<>(), new AsynchClientTask[]{task1});
	}

	private void pasteCells(final String actionCommand) {
		boolean shouldPasteSome = MathOverridesPanel.this.getPasteMenuItem().getActionCommand().equals(actionCommand);
		boolean shouldPasteAll = MathOverridesPanel.this.getPasteAllMenuItem().getActionCommand().equals(actionCommand);
		if (!shouldPasteSome && !shouldPasteAll) return;
		java.util.List<PasteOperationDataSource<String>> rawDataSources = new java.util.ArrayList<>();
		AsynchClientTask task1 = new ValidateAndComputePasteAsynchClientTask(rawDataSources, shouldPasteAll);
		AsynchClientTask task2 = new SmartPasteAsynchClientTask(rawDataSources);
		ClientTaskDispatcher.dispatch(this, new Hashtable<>(), new AsynchClientTask[]{task1, task2});
	}

	private void mouseReleasedOnScrollTable(java.awt.event.MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() != 2) return;

		int overrideColumn = MathOverridesTableModel.OVERRIDE_VALUE_COLUMN_INDEX;
		if (this.getScrollTable().getSelectedColumn() != overrideColumn) return;
		int selectedRow = this.getScrollTable().getSelectedRow();
		Object value = this.getMathOverridesTableModel().getValueAt(selectedRow, MathOverridesTableModel.PARAMETER_COLUMN_INDEX);
		if (!this.getMathOverrides().isScan(value.toString())) return;
		// Does this actually do anything?
		Object replacementValue = this.getMathOverridesTableModel().getValueAt(selectedRow, overrideColumn);
		this.getMathOverridesTableModel().setValueAt(replacementValue, selectedRow, overrideColumn);
	}

	public void setEditable(boolean editable) {
		boolean oldValue = this.isEditable;
		this.isEditable = editable;
		this.firePropertyChange("editable", oldValue, editable);
	}

	public void setMathOverrides(MathOverrides mathOverrides) {
		// Note: no propertyEventChange fired for change to `removeUnusedButton`
		MathOverrides oldValue = this.mathOverrides;
		this.mathOverrides = mathOverrides;
		this.removeUnusedButton.setVisible(this.mathOverrides != null && this.mathOverrides.hasUnusedOverrides());
		this.firePropertyChange("mathOverrides", oldValue, mathOverrides);
	}

	private void showPopupMenu(MouseEvent mouseEvent) {
		if (!mouseEvent.isPopupTrigger()) return;
		Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
		boolean bPaste = obj instanceof VCellTransferable.ResolvedValuesSelection;

		this.getPasteMenuItem().setEnabled(bPaste && (this.getScrollTable().getSelectedRowCount() > 0));
		this.getPasteMenuItem().setVisible(this.getEditable());
		this.getPasteAllMenuItem().setEnabled(bPaste);
		this.getPasteAllMenuItem().setVisible(this.getEditable());
		this.getCopyMenuItem().setEnabled(this.getScrollTable().getSelectedRowCount() > 0);
		this.getCopyAllMenuItem().setEnabled(this.getScrollTable().getRowCount() > 0);
		this.getRightClickMenu().show(this.getScrollTable(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}

	private void updateEditableMode(boolean editable) {
        try {
            this.getTitleLabel().setVisible(editable);
            this.getScrollTable().setRequestFocusEnabled(editable);
            this.getScrollTable().setCellSelectionEnabled(editable);
            this.getMathOverridesTableModel().setEditable(editable);
            this.setMathOverrides(this.getMathOverrides()); // re-initializes keys
        } catch (java.lang.Exception exception) {
            this.logException(exception);
        }
	}

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
                }
            });
            java.awt.Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Exception exception) {
            System.err.println("Exception occurred in main() of javax.swing.JPanel");
            exception.printStackTrace(System.out);
        }
    }

	protected class ValidateAndPerformSmartCopyAsynchClientTask extends AsynchClientTask {
		private final String actionCommand;

		public ValidateAndPerformSmartCopyAsynchClientTask(final String actionCommand) {
			super("validate and perform smart copy", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
			this.actionCommand = actionCommand;
		}

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			boolean isCopy = "Copy".equals(this.actionCommand);
			boolean isCopyAll = "Copy All".equals(this.actionCommand);
			if (!isCopy && !isCopyAll) return;
			int numSelectedRows = isCopy ? MathOverridesPanel.this.getScrollTable().getSelectedRowCount() : MathOverridesPanel.this.getScrollTable().getRowCount();
			int numSelectedColumns = isCopy ? MathOverridesPanel.this.getScrollTable().getSelectedColumnCount() : MathOverridesPanel.this.getScrollTable().getColumnCount();
			int[] rows = isCopy ? MathOverridesPanel.this.getScrollTable().getSelectedRows() : IntStream.range(0, numSelectedRows).toArray();
			int[] columns = isCopy ? MathOverridesPanel.this.getScrollTable().getSelectedColumns() : IntStream.range(0, numSelectedColumns).toArray();

			if (0 >= numSelectedRows || 0 >= numSelectedColumns) return;
			StringBuilder stringBuilder = new StringBuilder();
			boolean isMultiCellSelection = numSelectedRows + numSelectedColumns > 2;
			try {
				// if copying more than one cell, make a string that will paste like a table in spreadsheets
				//      meaning we should also include column headers
				// if copying a single cell, just get that value, no headers!
				stringBuilder.append(isMultiCellSelection ? this.getStringRepresentation(rows, columns) : this.getStringRepresentation(rows[0], columns[0]));

				//Copy SimulationParameterSelection to clipboard along with "original style" formatted string
				List<SymbolTableEntry> primarySymbolTableEntries = new ArrayList<>();
				List<Object> resolvedValues = new ArrayList<>();
				for (int row : rows) {
					String rowName = (String) MathOverridesPanel.this.getScrollTable().getValueAt(row, MathOverridesTableModel.PARAMETER_COLUMN_INDEX);
					primarySymbolTableEntries.add(MathOverridesPanel.this.getMathOverrides().getConstant(rowName));
					boolean overrideIsScan = (Boolean) MathOverridesPanel.this.getScrollTable().getValueAt(row, MathOverridesTableModel.PERFORM_SCAN_COLUMN_INDEX);
					MathOverrides overrides = MathOverridesPanel.this.getMathOverrides();
					Object resolvedValue = overrideIsScan ? overrides.getConstantArraySpec(rowName) : overrides.getActualExpression(rowName, MathOverrides.ScanIndex.ZERO);
					resolvedValues.add(resolvedValue);
				}
				VCellTransferable.ResolvedValuesSelection rvs =
						new VCellTransferable.ResolvedValuesSelection(
								primarySymbolTableEntries.toArray(SymbolTableEntry[]::new),
								null,
								resolvedValues.toArray(Object[]::new),
								stringBuilder.toString());

				VCellTransferable.sendToClipboard(rvs);
			} catch (Throwable e) {
				PopupGenerator.showErrorDialog(MathOverridesPanel.this, "MathOverridesPanel copy failed.  " + e.getMessage(), e);
			}
		}

		private String getStringRepresentation(int[] rowIndexes, int[] columnIndexes) {
			StringBuilder stringBuilder = new StringBuilder();
			String[] headers = Arrays.stream(columnIndexes).mapToObj(MathOverridesPanel.this.getScrollTable()::getColumnName).toArray(String[]::new);
			stringBuilder.append(String.join("\t", headers)).append("\n");

			Stream<Integer> rowIndexStream = Arrays.stream(rowIndexes).boxed();
			Stream<String> rowsAsStrings = rowIndexStream.map(
					rowIndex -> Arrays.stream(columnIndexes).mapToObj(columnIndex -> ValidateAndPerformSmartCopyAsynchClientTask.this.getStringRepresentation(rowIndex, columnIndex)).collect(Collectors.joining("\t"))
			);
			String completeStringRepresentation = rowsAsStrings.collect(Collectors.joining("\n"));
			stringBuilder.append(completeStringRepresentation);
			return stringBuilder.toString();
		}

		private String getStringRepresentation(int rowIndex, int columnIndex) {
			Object cell;
			return null == (cell = MathOverridesPanel.this.getScrollTable().getValueAt(rowIndex, columnIndex)) ? "" : cell.toString();
		}
	}

	protected class ValidateAndComputePasteAsynchClientTask extends AsynchClientTask {
		private final boolean shouldPasteAll;
		private final java.util.List<PasteOperationDataSource<String>> rawDataSources;

		public ValidateAndComputePasteAsynchClientTask(final java.util.List<PasteOperationDataSource<String>> rawDataSources, final boolean shouldPasteAll) {
			super("validating paste request", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
			this.shouldPasteAll = shouldPasteAll;
			this.rawDataSources = rawDataSources;
		}

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			IntStream rows = this.shouldPasteAll ?
					IntStream.range(0, MathOverridesPanel.this.getScrollTable().getRowCount()) :
					Arrays.stream(MathOverridesPanel.this.getScrollTable().getSelectedRows());
			try {
				Object pasteThis = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
				if (!(pasteThis instanceof VCellTransferable.ResolvedValuesSelection rvs)) return;
				for (int row : rows.toArray()) {
					Object[] potentialValues = rvs.getValues();
					for (int j = 0; j < rvs.getPrimarySymbolTableEntries().length; j += 1) {
						SymbolTableEntry primarySymbolTableEntry = rvs.getPrimarySymbolTableEntries()[j];
						SymbolTableEntry alternateSymbolTableEntry = null == rvs.getAlternateSymbolTableEntries() ? null : rvs.getAlternateSymbolTableEntries()[j];
						Object currentValue = potentialValues[j];
						Constant pastedConstant;
						if (primarySymbolTableEntry instanceof Constant constantSymbolTableEntry) {
							pastedConstant = constantSymbolTableEntry;
						} else if (alternateSymbolTableEntry instanceof Constant constantSymbolTableEntry) {
							pastedConstant = constantSymbolTableEntry;
						} else {
							pastedConstant = null;
						}

						// if a Constant is not on the clipboard, try to find a corresponding Constant that starts with "varname_init_"
						if (pastedConstant == null &&
								(primarySymbolTableEntry instanceof Function) ||
								(primarySymbolTableEntry instanceof VolVariable) ||
								(primarySymbolTableEntry instanceof VolumeRegionVariable) ||
								(primarySymbolTableEntry instanceof MemVariable) ||
								(primarySymbolTableEntry instanceof MembraneRegionVariable)) {

							MathDescription mathDescription = MathOverridesPanel.this.getMathOverrides().getSimulation().getMathDescription();
							for (Constant constant : Collections.list(mathDescription.getConstants())) {
								String prefix = primarySymbolTableEntry.getName() + DiffEquMathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX;
								if (!constant.getName().startsWith(prefix)) continue;
								if (!(currentValue instanceof Expression currentExpression)) continue;
								pastedConstant = new Constant(constant.getName(), currentExpression);
								break;
							}
						}

						// find row of math overrides table with the same name as the pastedConstant and propose to change that override to the pasted value
						String rowName = (String) MathOverridesPanel.this.getScrollTable().getValueAt(row, MathOverridesTableModel.PARAMETER_COLUMN_INDEX);
						if (null == pastedConstant || !rowName.equals(pastedConstant.getName())) continue;
						Object originalValueDescription;
						ConstantArraySpec arraySpec;
						Expression actualExpression;
						if (null != (arraySpec = MathOverridesPanel.this.getMathOverrides().getConstantArraySpec(rowName))) {
							originalValueDescription = arraySpec;
						} else if (null != (actualExpression = MathOverridesPanel.this.getMathOverrides().getActualExpression(rowName, MathOverrides.ScanIndex.ZERO))) {
							originalValueDescription = actualExpression;
						} else {
							throw new Exception("MathOverridesPanel can't find value for '" + rowName + "'");
						}
						this.rawDataSources.add(new PasteOperationMathOverrideDataSource(
								MathOverridesPanel.this.getMathOverrides(),
								rowName,
								originalValueDescription,
								currentValue
						));
					}
				}
			} catch (Throwable e) {
				PopupGenerator.showErrorDialog(MathOverridesPanel.this, "Paste failed during pre-check (no changes made).\n" + e.getClass().getName() + " " + e.getMessage(), e);
			}
		}
	}

	protected class SmartPasteAsynchClientTask extends AsynchClientTask {
		private final java.util.List<PasteOperationDataSource<String>> rawDataSources;

		public SmartPasteAsynchClientTask(final java.util.List<PasteOperationDataSource<String>> rawDataSources) {
			super("performing validated paste request", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
			this.rawDataSources = rawDataSources;
		}

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (this.rawDataSources.isEmpty()) {
				PopupGenerator.showInfoDialog(MathOverridesPanel.this, "No paste items match the destination (no changes made).");
				return;
			}
			//Do paste
			try {
				VCellCopyPasteHelper.chooseApplyPaste(MathOverridesPanel.this, this.rawDataSources);
			} catch (Throwable e) {
				PopupGenerator.showErrorDialog(MathOverridesPanel.this, "Paste Error\n" + e.getClass().getName() + " " + e.getMessage(), e);
			}
		}
	}

}
