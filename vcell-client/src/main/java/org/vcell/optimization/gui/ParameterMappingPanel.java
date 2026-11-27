/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.copypaste.PasteOperationDataSource;
import cbit.vcell.desktop.copypaste.PasteOperationParameterMappingDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.*;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.desktop.copypaste.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Kinetics.UnresolvedParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ParameterMappingSpec;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;


public class ParameterMappingPanel extends javax.swing.JPanel {
    private static final Logger lg =  LogManager.getLogger(ParameterMappingPanel.class);

	private JSortTable parameterMappingTable = null;
	private ParameterMappingTableModel parameterMappingTableModel = null;
	private final EventHandler eventHandler = new EventHandler();
	private ParameterEstimationTask parameterEstimationTask = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem copyAllMenuItem = null;
	private JMenuItem pasteMenuItem = null;
	private JMenuItem pasteAllMenuItem = null;
	private JPopupMenu copyPastePopupMenu = null;
	private JButton addButton;
	private JButton deleteButton;

	private class InternalScrollTableActionManager extends DefaultScrollTableActionManager {

		InternalScrollTableActionManager(JTable table) {
			super(table);
		}

		@Override
		protected void constructPopupMenu() {
			if (this.popupMenu == null) this.constructExtendedPopupMenu();
			Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
			boolean bPastable = obj instanceof VCellTransferable.ResolvedValuesSelection;

			boolean bSelected = ParameterMappingPanel.this.getScrollPaneTable().getSelectedRowCount() > 0;
			bPastable = bPastable && bSelected;

			if (bSelected) {
				ParameterMappingPanel.this.getJMenuItemCopy().setText("Copy 'Initial Guess'");
				ParameterMappingPanel.this.getJMenuItemCopyAll().setText("Copy All 'Initial Guess'");
				ParameterMappingPanel.this.getJMenuItemPaste().setText("Paste 'Initial Guess'");
				ParameterMappingPanel.this.getJMenuItemPasteAll().setText("Paste All 'Initial Guess'");
			}

			ParameterMappingPanel.this.getJMenuItemPaste().setEnabled(bPastable);
			ParameterMappingPanel.this.getJMenuItemPasteAll().setEnabled(bPastable);

		}

		private void constructExtendedPopupMenu() {
			super.constructPopupMenu();
            List<JComponent> orderedMenuItems = List.of(
                    ParameterMappingPanel.this.getJMenuItemCopy(),
                    ParameterMappingPanel.this.getJMenuItemCopyAll(),
                    ParameterMappingPanel.this.getJMenuItemPaste(),
                    ParameterMappingPanel.this.getJMenuItemPasteAll(),
                    new JSeparator()
            );
            for (int i = 0; i < orderedMenuItems.size(); i++) this.popupMenu.insert(orderedMenuItems.get(i), i);
		}
	}

	private class EventHandler implements java.awt.event.ActionListener, /*java.awt.event.MouseListener, */java.beans.PropertyChangeListener, ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ParameterMappingPanel.this.addButton) {
				ParameterMappingPanel.this.addParameter();
			} else if (e.getSource() == ParameterMappingPanel.this.deleteButton) {
				ParameterMappingPanel.this.deleteParameter();
			} else if (e.getSource() == ParameterMappingPanel.this.getJMenuItemCopy()) {
				ParameterMappingPanel.this.performCopyAction(false);
			} else if (e.getSource() == ParameterMappingPanel.this.getJMenuItemCopyAll()) {
				ParameterMappingPanel.this.performCopyAction(true);
			} else if (e.getSource() == ParameterMappingPanel.this.getJMenuItemPaste()) {
				ParameterMappingPanel.this.performPasteAction(false, false);
			} else if (e.getSource() == ParameterMappingPanel.this.getJMenuItemPasteAll()) {
				ParameterMappingPanel.this.performPasteAction(true, false);
			}
		}

		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ParameterMappingPanel.this && (evt.getPropertyName().equals("parameterEstimationTask"))) {
				ParameterMappingPanel.this.parameterMappingTableModel.setParameterEstimationTask(ParameterMappingPanel.this.getParameterEstimationTask());
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) return;
			ParameterMappingPanel.this.deleteButton.setEnabled(ParameterMappingPanel.this.getScrollPaneTable().getSelectedRowCount() > 0);
		}
	}

	/**
	 * ModelParameterPanel constructor comment.
	 */
	public ParameterMappingPanel() {
		super();
		this.initialize();
	}

	public void deleteParameter() {
        Arrays.stream(this.getScrollPaneTable().getSelectedRows()).mapToObj(this.parameterMappingTableModel::getValueAt).forEach(pms -> pms.setSelected(false));
	}

	private static class SelectParameterTableModel extends VCellSortTableModel<ParameterMappingSpec> implements PropertyChangeListener {

		private final static int COLUMN_NAME = 0;
		private final static int COLUMN_SCOPE = 1;
		private final static String[] LABELS = {"Parameter", "Context"};

		private final ParameterEstimationTask parameterEstimationTask;

        private record ParameterColumnComparator(int index, int scale) implements Comparator<ParameterMappingSpec> {
            private ParameterColumnComparator(int index, boolean scale) {
                this(index, scale ? 1 : -1);
            }

            /**
             * Compares its two arguments for order.  Returns a negative integer,
             * zero, or a positive integer as the first argument is less than, equal
             * to, or greater than the second.<p>
             */
            public int compare(ParameterMappingSpec pms1, ParameterMappingSpec pms2) {
                Parameter parm1 = pms1.getModelParameter();
                Parameter parm2 = pms2.getModelParameter();

                return switch (this.index) {
                    case COLUMN_NAME -> this.scale * parm1.getName().compareToIgnoreCase(parm2.getName());
                    case COLUMN_SCOPE -> this.scale * parm1.getNameScope().getName().compareToIgnoreCase(parm2.getNameScope().getName());
                    default -> 1;
                };
            }
        }

		public SelectParameterTableModel(ScrollTable table, ParameterEstimationTask task) {
			super(table, LABELS);
			this.parameterEstimationTask = task;
		}
        
		public Class<?> getColumnClass(int column) {
			return switch (column) {
				case COLUMN_NAME, COLUMN_SCOPE -> String.class;
				default -> null;
			};
		}
        
		private void refreshData() {
            if (null == this.parameterEstimationTask) {
                this.setData(new ArrayList<>());
                return;
            }
			Stream<ParameterMappingSpec> mappingSpecs = Arrays.stream(this.parameterEstimationTask.getModelOptimizationSpec().getParameterMappingSpecs());
            this.setData(mappingSpecs.filter(pms -> !pms.isSelected()).toList());
		}

        
		public Object getValueAt(int row, int col) {
			ParameterMappingSpec parameterMappingSpec = this.getValueAt(row);
			return switch (col) {
				case COLUMN_NAME -> parameterMappingSpec.getModelParameter().getName();
				case COLUMN_SCOPE -> this.getColumnScopeValue(parameterMappingSpec);
				default -> null;
			};
		}
        
        private String getColumnScopeValue(ParameterMappingSpec parameterMappingSpec){
            if (parameterMappingSpec.getModelParameter() instanceof UnresolvedParameter) return "unresolved";
            if (parameterMappingSpec.getModelParameter().getNameScope() == null) return "null";
            if (parameterMappingSpec.getModelParameter() instanceof ModelParameter) return "Model";
            return parameterMappingSpec.getModelParameter().getNameScope().getName();
        }


		protected Comparator<ParameterMappingSpec> getComparator(int col, boolean ascending) {
			return new ParameterColumnComparator(col, ascending);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			// Do nothing
		}
	}

	public void addParameter() {
		JPanel panel = new JPanel(new BorderLayout());
		ScrollTable table = new ScrollTable();
		SelectParameterTableModel model = new SelectParameterTableModel(table, this.getParameterEstimationTask());
		table.setModel(model);
		model.refreshData();

		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(400, 300));
		int returnCode = DialogUtils.showComponentOKCancelDialog(this, panel, "Select Parameters");
		if (returnCode == JOptionPane.OK_OPTION) {
			for (int row : table.getSelectedRows()) {
				ParameterMappingSpec pms = model.getValueAt(row);
				pms.setSelected(true);
			}
		}
	}
    
	private javax.swing.JMenuItem getJMenuItemCopy() {
        if (null != this.copyMenuItem) return this.copyMenuItem;
		try {
			this.copyMenuItem = new JMenuItem();
			this.copyMenuItem.setName("JMenuItemCopy");
			this.copyMenuItem.setText("Copy");
		} catch (Exception thrownException) {
			this.handleException(thrownException);
		}
		return this.copyMenuItem;
	}
    
	private javax.swing.JMenuItem getJMenuItemCopyAll() {
        if (null != this.copyAllMenuItem) return this.copyAllMenuItem;
		try {
			this.copyAllMenuItem = new JMenuItem();
			this.copyAllMenuItem.setName("JMenuItemCopyAll");
			this.copyAllMenuItem.setText("Copy All");
		} catch (Exception thrownException) {
			this.handleException(thrownException);
		}
		return this.copyAllMenuItem;
	}

	private javax.swing.JMenuItem getJMenuItemPaste() {
        if (null != this.pasteMenuItem) return this.pasteMenuItem;
		try {
			this.pasteMenuItem = new JMenuItem();
			this.pasteMenuItem.setName("JMenuItemPaste");
			this.pasteMenuItem.setText("Paste");
		} catch (Exception thrownException) {
			this.handleException(thrownException);
		}
		return this.pasteMenuItem;
	}

	private javax.swing.JMenuItem getJMenuItemPasteAll() {
        if (null != this.pasteAllMenuItem) return this.pasteAllMenuItem;
		try {
			this.pasteAllMenuItem = new JMenuItem();
			this.pasteAllMenuItem.setName("JMenuItemPasteAll");
			this.pasteAllMenuItem.setText("Paste All");
		} catch (Exception thrownException) {
			this.handleException(thrownException);
		}
		return this.pasteAllMenuItem;
	}

	private JPopupMenu getJPopupMenuCopyPaste() {
        if (null != this.copyPastePopupMenu) return this.copyPastePopupMenu;
		try {
			this.copyPastePopupMenu = new JPopupMenu();
			this.copyPastePopupMenu.setName("JPopupMenuCopyPaste");
			this.copyPastePopupMenu.add(this.getJMenuItemCopy());
			this.copyPastePopupMenu.add(this.getJMenuItemCopyAll());
			this.copyPastePopupMenu.add(this.getJMenuItemPaste());
			this.copyPastePopupMenu.add(this.getJMenuItemPasteAll());
		} catch (Exception thrownException) {
			this.handleException(thrownException);
		}
		return this.copyPastePopupMenu;
	}

	/**
	 * Gets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
	 *
	 * @return The parameterEstimationTask property value.
	 * @see #setParameterEstimationTask
	 */
	public ParameterEstimationTask getParameterEstimationTask() {
		return this.parameterEstimationTask;
	}

	private JSortTable getScrollPaneTable() {
        if (null != this.parameterMappingTable) return this.parameterMappingTable;
		try {
			this.parameterMappingTable = new JSortTable();
			this.parameterMappingTable.setName("ScrollPaneTable");
			this.parameterMappingTableModel = new ParameterMappingTableModel(this.parameterMappingTable);
			this.parameterMappingTable.setScrollTableActionManager(new InternalScrollTableActionManager(this.parameterMappingTable));
			this.parameterMappingTable.setModel(this.parameterMappingTableModel);
		} catch (Exception thrownException) {
			this.handleException(thrownException);
		}
		return this.parameterMappingTable;
	}

	private void handleException(java.lang.Exception exception) {
        lg.warn("Exception was detected in ParameterMappingPanel:", exception);
	}


	private void initConnections()  {
		this.addPropertyChangeListener(this.eventHandler);
		this.getJMenuItemCopy().addActionListener(this.eventHandler);
		this.getJMenuItemCopyAll().addActionListener(this.eventHandler);
		this.getJMenuItemPaste().addActionListener(this.eventHandler);
		this.getJMenuItemPasteAll().addActionListener(this.eventHandler);
	}


	private void initialize() {
		try {
			this.setName("ModelParameterPanel");
			this.setLayout(new BorderLayout());
			this.setSize(655, 226);
			this.addButton = new JButton(VCellIcons.addIcon);
			this.deleteButton = new JButton(VCellIcons.deleteIcon);
			this.deleteButton.setEnabled(false);
			JToolBar toolBar = new JToolBar();
			toolBar.setFloatable(false);
			toolBar.add(this.addButton);
			toolBar.add(this.deleteButton);
			this.add(this.getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
			this.add(toolBar, BorderLayout.NORTH);

			this.addButton.addActionListener(this.eventHandler);
			this.deleteButton.addActionListener(this.eventHandler);
			this.getScrollPaneTable().getSelectionModel().addListSelectionListener(this.eventHandler);
			this.initConnections();
			this.getScrollPaneTable().setDefaultRenderer(Double.class, new DefaultScrollTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					this.setHorizontalAlignment(RIGHT);
					return this;
				}
			});
            this.setUpKeybinds();
		} catch (Exception thrownException) {
			this.handleException(thrownException);
		}
	}

    private void setUpKeybinds() {
	    String COPY_TASK_NAME = "parameterMappingPanelCopyAction", PASTE_TASK_NAME = "parameterMappingPanelPasteAction";
        ShortcutsWizard wizard = new ShortcutsWizard(this);
        wizard.configureCopy(wizard.createAction(COPY_TASK_NAME, AsynchClientTask.TASKTYPE_SWING_BLOCKING, ignored -> this.performCopyAction(0 == this.getScrollPaneTable().getSelectedRowCount())));
        wizard.configurePaste(wizard.createAction(PASTE_TASK_NAME, AsynchClientTask.TASKTYPE_SWING_BLOCKING, ignored -> this.performPasteAction(0 == this.getScrollPaneTable().getSelectedRowCount(), true)));
        // default "super + a" behavior is acceptable for select all.
    }

	private void performCopyAction(final boolean isCopyAll) {
		try {
			//Copy Optimization Parameters (Initial Guess or Solution)
			IntStream rows = isCopyAll ? IntStream.range(0, this.getScrollPaneTable().getRowCount()) : Arrays.stream(this.getScrollPaneTable().getSelectedRows());
			SimulationContext sc = this.getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext();
			MathSymbolMapping msm = null;
			try {
				MathMapping mm = sc.createNewMathMapping();
				msm = mm.getMathSymbolMapping();
			} catch (Exception e) {
                lg.warn("current math not valid, some paste operations will be limited", e);
				DialogUtils.showWarningDialog(this, "current math not valid, some paste operations will be limited\n\nreason: " + e.getMessage());
			}
			boolean bInitialGuess = (this.getScrollPaneTable().getSelectedColumn() == ParameterMappingTableModel.COLUMN_CURRENTVALUE);
			List<SymbolTableEntry> primarySymbolTableEntries = new ArrayList<>();
			List<SymbolTableEntry> alternateSymbolTableEntries = new ArrayList<>();
			List<Expression> resolvedValues = new ArrayList<>();

			//Create formatted string for text/spreadsheet pasting.
			StringBuilder sb = new StringBuilder();
			sb.append("\"Parameters for (Optimization Task)").append(this.getParameterEstimationTask().getName())
                    .append(" -> ").append("(BioModel)").append(this.getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext().getBioModel().getName())
                    .append(" -> ").append("(App)").append(this.getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext().getName()).append("\"\n");
			sb.append("\"Parameter Name\"\t\"").append(bInitialGuess ? "Initial Guess" : "Solution").append("\"\n");
			for (int row : rows.toArray()) {
				ParameterMappingSpec pms = this.parameterMappingTableModel.getValueAt(row);
				primarySymbolTableEntries.add(pms.getModelParameter());
				alternateSymbolTableEntries.add(null == msm ? null : msm.getVariable(pms.getModelParameter()));
				Double resolvedValue = null;
				if (!bInitialGuess) resolvedValue = this.getParameterEstimationTask().getCurrentSolution(pms);
				if (resolvedValue == null) resolvedValue = pms.getCurrent();

				resolvedValues.add(new Expression(resolvedValue));
				sb.append("\"").append(pms.getModelParameter().getName()).append("\"\t").append(resolvedValue).append("\n");
			}

			//Send to clipboard
			VCellTransferable.ResolvedValuesSelection rvs =
					new VCellTransferable.ResolvedValuesSelection(
							primarySymbolTableEntries.toArray(SymbolTableEntry[]::new),
							alternateSymbolTableEntries.toArray(SymbolTableEntry[]::new),
							resolvedValues.toArray(Expression[]::new),
							sb.toString());

			VCellTransferable.sendToClipboard(rvs);
		} catch (Exception e) {
			PopupGenerator.showErrorDialog(this, "ParameterMappingPanel copy failed.  " + e.getMessage(), e);
		}
	}

    private void performPasteAction(final boolean shouldPasteAll, boolean triggeredByKeybind) {
        Object pasteThis = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
        if (!(pasteThis instanceof VCellTransferable.ResolvedValuesSelection resolvedValuesSelection)) {
            if (!triggeredByKeybind) PopupGenerator.showInfoDialog(ParameterMappingPanel.this, "No paste items match the destination (no changes made).");
            return;
        }
        final List<PasteOperationDataSource<Expression>> rawDataSource = new ArrayList<>();
        AsynchClientTask task1 = new ParameterMappingPanel.ValidatePasteRequestTask(resolvedValuesSelection, rawDataSource, shouldPasteAll);
        AsynchClientTask task2 = new ParameterMappingPanel.PerformSmartPasteTask(rawDataSource);
        ClientTaskDispatcher.dispatch(this, new Hashtable<>(), new AsynchClientTask[]{task1, task2});
    }


	/**
	 * main entrypoint - starts the part when it is run as an application
	 *
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			ParameterMappingPanel aParameterMappingPanel;
			aParameterMappingPanel = new ParameterMappingPanel();
			frame.setContentPane(aParameterMappingPanel);
			frame.setSize(aParameterMappingPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				}

			});
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}

	private void popupCopyPaste(java.awt.event.MouseEvent mouseEvent) {
		if (mouseEvent.isPopupTrigger()) {
			Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
			boolean bPastable =
					obj instanceof VCellTransferable.ResolvedValuesSelection;

			boolean bInitGuessSelected = this.getScrollPaneTable().getSelectedColumn() == ParameterMappingTableModel.COLUMN_CURRENTVALUE;
			bPastable = bPastable && bInitGuessSelected;
//		boolean bSolutionSelected = getScrollPaneTable().getSelectedColumn() ==  ParameterMappingTableModel.COLUMN_SOLUTION;
//		boolean bSomethingSelected = (bInitGuessSelected || bSolutionSelected);

			if (bInitGuessSelected) {
				this.getJMenuItemPaste().setVisible(true);
				this.getJMenuItemPasteAll().setVisible(true);
				this.getJMenuItemCopy().setText("Copy 'Initial Guess'");
				this.getJMenuItemCopyAll().setText("Copy All 'Initial Guess'");
				this.getJMenuItemPaste().setText("Paste 'Initial Guess'");
				this.getJMenuItemPasteAll().setText("Paste All 'Initial Guess'");
//		}else if(bSolutionSelected){
//			getJMenuItemPaste().setVisible(false);
//			getJMenuItemPasteAll().setVisible(false);
//			getJMenuItemCopy().setText("Copy 'Solution'");
//			getJMenuItemCopyAll().setText("Copy All 'Solution'");
//			getJMenuItemPaste().setText("Paste");
//			getJMenuItemPasteAll().setText("Paste All");
			} else {
//			PopupGenerator.showInfoDialog(this, "For Copy/Paste select a cell in the \"Initial Guess\" or \"Solution\" column");
				return;
			}

			this.getJMenuItemPaste().setEnabled(bPastable/* && bSomethingSelected*/);
			this.getJMenuItemPasteAll().setEnabled(bPastable);
//		getJMenuItemCopy().setEnabled(bSomethingSelected);
//		getJMenuItemCopyAll().setEnabled(bSomethingSelected);
			this.getJPopupMenuCopyPaste().show(this.getScrollPaneTable(), mouseEvent.getX(), mouseEvent.getY());
		}
	}

	/**
	 * Sets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
	 *
	 * @param parameterEstimationTask The new value for the property.
	 * @see #getParameterEstimationTask
	 */
	public void setParameterEstimationTask(ParameterEstimationTask parameterEstimationTask) {
		ParameterEstimationTask oldValue = this.parameterEstimationTask;
		this.parameterEstimationTask = parameterEstimationTask;
		this.firePropertyChange("parameterEstimationTask", oldValue, parameterEstimationTask);
	}

    private class ValidatePasteRequestTask extends AsynchClientTask {
        final VCellTransferable.ResolvedValuesSelection resolvedValue;
        final List<PasteOperationDataSource<Expression>> rawDataSources;
        final boolean shouldPasteAll;

        ValidatePasteRequestTask(
                final VCellTransferable.ResolvedValuesSelection resolvedValuesSelection,
                final List<PasteOperationDataSource<Expression>> rawDataSources,
                final boolean shouldPasteAll) {
            super("Validate paste request for ParameterMappingPanel", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
            this.resolvedValue = resolvedValuesSelection;
            this.rawDataSources = rawDataSources;
            this.shouldPasteAll = shouldPasteAll;
        }

        @Override
        public void run(Hashtable<String, Object> hashTable) {
            try {
                SimulationContext sc = ParameterMappingPanel.this.getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext();
                MathSymbolMapping msm = null;
                Exception mathMappingException = null;
                try {
                    MathMapping mm = sc.createNewMathMapping();
                    msm = mm.getMathSymbolMapping();
                } catch (Exception e) {
                    mathMappingException = e;
                    lg.warn(e);
                }

                IntStream rows = this.shouldPasteAll ? IntStream.range(0, ParameterMappingPanel.this.getScrollPaneTable().getRowCount()) : Arrays.stream(ParameterMappingPanel.this.getScrollPaneTable().getSelectedRows());

                //Check paste
                StringBuilder errors = new StringBuilder();
                for (int row : rows.toArray()) {
                    ParameterMappingSpec pms = ParameterMappingPanel.this.parameterMappingTableModel.getValueAt(row);
                    try {
                        SymbolTableEntry[] transitionArr;
                        Queue<SymbolTableEntry> primaryEntries = new LinkedList<>(Arrays.stream(this.resolvedValue.getPrimarySymbolTableEntries()).toList());
                        Queue<SymbolTableEntry> alternateEntries = new LinkedList<>(null == (transitionArr = this.resolvedValue.getAlternateSymbolTableEntries()) ? List.of() : Arrays.asList(transitionArr));
                        Queue<Expression> correlatedExpressions  = Arrays.stream(this.resolvedValue.getValues()).filter(Expression.class::isInstance).map(Expression.class::cast).collect(Collectors.toCollection(LinkedList::new));

                        while (!primaryEntries.isEmpty()) {
                            SymbolTableEntry primarySTE = primaryEntries.poll();
                            SymbolTableEntry alternateSTE = alternateEntries.poll();
                            Expression correlatedExpression =  correlatedExpressions.poll();
                            if (!this.selectionMatchesParameterEntry(pms, primarySTE, alternateSTE) && !this.selectionMatchesVariableEntry(pms, primarySTE, alternateSTE, msm, mathMappingException))
                                continue;
                            this.rawDataSources.add(new PasteOperationParameterMappingDataSource(pms, correlatedExpression));
                        }
                    } catch (Exception e) {
                        lg.error(String.format("Error while processing `%s`:", pms.getModelParameter().getName()), e);
                        errors.append(String.format("%s (%s) %s\n", pms.getModelParameter().getName(), e.getClass().getSimpleName(), e.getMessage()));
                    }
                }
                if (!errors.isEmpty()) throw new RuntimeException(errors.toString());
            } catch (Exception e) {
                PopupGenerator.showErrorDialog(ParameterMappingPanel.this, "Paste failed during pre-check (no changes made).\n" + e.getMessage(), e);
            }
        }

        private boolean selectionMatchesParameterEntry(ParameterMappingSpec pms, SymbolTableEntry primarySTE, SymbolTableEntry alternateSTE){
            Parameter clipboardBiologicalParameter;
            if (primarySTE instanceof Parameter paramSTE) {
                clipboardBiologicalParameter = paramSTE;
            } else if (alternateSTE instanceof Parameter paramSTE) {
                clipboardBiologicalParameter = paramSTE;
            } else {
                clipboardBiologicalParameter = null;
            }

            if (null == clipboardBiologicalParameter) return false;
            if (!pms.getModelParameter().getNameScope().getName().equals(clipboardBiologicalParameter.getNameScope().getName())) return false;
            if (!pms.getModelParameter().getClass().equals(clipboardBiologicalParameter.getClass())) return false;
            return pms.getModelParameter().getName().equals(clipboardBiologicalParameter.getName());
        }

        private boolean selectionMatchesVariableEntry(ParameterMappingSpec pms, SymbolTableEntry primarySTE, SymbolTableEntry alternateSTE,
                                                      MathSymbolMapping msm, Exception mathMappingException) throws Exception {
            Variable pastedMathVariable;
            if (primarySTE instanceof Variable varSTE) {
                pastedMathVariable = varSTE;
            } else if (alternateSTE instanceof Variable varSTE) {
                pastedMathVariable = varSTE;
            } else {
                pastedMathVariable = null;
            }

            if (null == pastedMathVariable) return false;
            if (msm == null) throw mathMappingException;

            Variable localMathVariable = msm.findVariableByName(pastedMathVariable.getName());
            if (null == localMathVariable) return false;

            SymbolTableEntry[] localBiologicalSymbolArr = msm.getBiologicalSymbol(localMathVariable);
            for (SymbolTableEntry symbolTableEntry : localBiologicalSymbolArr) {
                if (symbolTableEntry != pms.getModelParameter()) continue;
                return true;
            }
            return false;
        }
    }

    private class PerformSmartPasteTask extends AsynchClientTask {
        final List<PasteOperationDataSource<Expression>> rawDataSources;

        PerformSmartPasteTask(List<PasteOperationDataSource<Expression>> rawDataSources) {
            super("perform smart paste for ParameterMappingPanel", AsynchClientTask.TASKTYPE_SWING_BLOCKING);
            this.rawDataSources = rawDataSources;
        }

        @Override
        public void run(Hashtable<String, Object> hashTable) {
            //Do paste
            if (this.rawDataSources.isEmpty()) {
                PopupGenerator.showInfoDialog(ParameterMappingPanel.this, "No paste items match the destination (no changes made).");
                return;
            }
            VCellCopyPasteHelper.chooseApplyPaste(ParameterMappingPanel.this, this.rawDataSources);
        }
    }

}
