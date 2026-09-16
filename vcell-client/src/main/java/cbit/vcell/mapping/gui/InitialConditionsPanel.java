/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import cbit.vcell.desktop.copypaste.PasteOperationDataSource;
import cbit.vcell.desktop.copypaste.PasteOperationScspDataSource;
import cbit.vcell.mapping.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.StringEscapeUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.*;
import org.vcell.util.gui.ScrollTable.ScrollTableBooleanCellRenderer;
import org.vcell.util.gui.sorttable.JSortTable;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.copypaste.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.graph.SmallShapeManager;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.gui.StructureMappingTableRenderer.TextIcon;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

/**
 * This type was created in VisualAge.
 */
public class InitialConditionsPanel extends DocumentEditorSubPanel implements ApplicationSpecificationsPanel.Specifier {
    private static final Logger lg = LogManager.getLogger(InitialConditionsPanel.class);

    private SimulationContext simulationContext = null;
	private JRadioButton conRadioButton = null; //added in July 2008. Enable selection of initial concentration or amount
	private JRadioButton particleCountRadioButton = null; //added in July 2008. Enable selection of initial concentration or amount
	private ButtonGroup radioGroup = null; //added in July 2008. Enable selection of initial concentration or amount
	private JPanel radioButtonAndCheckboxPanel = null; //added in July 2008. Used to accommodate the two radio buttons
	private JCheckBox randomizeInitCondCheckBox = null;    //added in Feb, 2013. Enable randomization of initial concentration or amount
	private JSortTable table = null;
	private SpeciesContextSpecTableModel tableModel = null;
	private final SmallShapeManager shapeManager = new SmallShapeManager(false, false, false, false);

	private final EventHandler eventHandler = new EventHandler();
	private javax.swing.JMenuItem pasteMenuItem = null;
	private javax.swing.JMenuItem copyMenuItem = null;
	private javax.swing.JMenuItem copyAllMenuItem = null;
	private javax.swing.JMenuItem pasteAllMenuItem = null;

	private enum ActionType {
		COPY,
		PASTE
	}

	public InitialConditionsPanel() {
		super();
		try {
			this.setName("InitialConditionsPanel");
			this.setLayout(new BorderLayout());
			this.add(this.getRadioButtonAndCheckboxPanel(), BorderLayout.NORTH);
			this.add(this.getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);

			this.getScrollPaneTable().getSelectionModel().addListSelectionListener(this.eventHandler);
			this.getPasteMenuItem().addActionListener(this.eventHandler);
			this.getCopyMenuItem().addActionListener(this.eventHandler);
			this.getCopyAllMenuItem().addActionListener(this.eventHandler);
			this.getPasteAllMenuItem().addActionListener(this.eventHandler);
			this.getAmountRadioButton().addActionListener(this.eventHandler);
			this.getConcentrationRadioButton().addActionListener(this.eventHandler);
			this.getRandomizeInitCondCheckbox().addActionListener(this.eventHandler);

			DefaultTableCellRenderer renderer = new IcpGeneralScrollTableRenderer();
			DefaultTableCellRenderer rbmSpeciesShapeDepictionCellRenderer = new RbmIcpSpeciesShapeDepictionCellRenderer();
			DefaultScrollTableCellRenderer rulesTableCellRenderer = new RulesTableCellRenderer();

			this.getScrollPaneTable().setDefaultRenderer(SpeciesContext.class, renderer);
			this.getScrollPaneTable().setDefaultRenderer(Structure.class, renderer);
			this.getScrollPaneTable().setDefaultRenderer(SpeciesPattern.class, rbmSpeciesShapeDepictionCellRenderer);    // depiction icons
			this.getScrollPaneTable().setDefaultRenderer(Species.class, renderer);
			this.getScrollPaneTable().setDefaultRenderer(ScopedExpression.class, renderer);
			this.getScrollPaneTable().setDefaultRenderer(Boolean.class, new ScrollTableBooleanCellRenderer());
			this.getScrollPaneTable().setDefaultRenderer(SpeciesContextSpecTableModel.RulesProvenance.class, rulesTableCellRenderer);    // rules icons

			this.setUpKeyBinds();

			// TODO: find out why the code below is not working properly
//		int ordinal = SpeciesContextSpecsTableModel.ColumnType.COLUMN_RULES.ordinal();
//		getScrollPaneTable().getColumnModel().getColumn(ordinal).setCellRenderer(rulesTableCellRenderer);

//		final int rulesWidth = 50;		// fixed max size, there's no point to enlarge some columns
//		int index = SpeciesContextSpecsTableModel.ColumnType.COLUMN_RULES.ordinal();
//		getScrollPaneTable().getColumnModel().getColumn(index).setPreferredWidth(rulesWidth);
//		getScrollPaneTable().getColumnModel().getColumn(index).setMaxWidth(rulesWidth);

		} catch (java.lang.Throwable caughtThrowable) {
			this.handleException(caughtThrowable);
		}
	}

	@Override
	public ActiveViewID getActiveView() {
		return ActiveViewID.species_settings;
	}

	public void setSearchText(String searchText) {
		this.tableModel.setSearchText(searchText);
	}
    
	private javax.swing.JMenuItem getCopyMenuItem() {
        if (this.copyMenuItem != null) return this.copyMenuItem;
		try {
			this.copyMenuItem = new javax.swing.JMenuItem();
			this.copyMenuItem.setName("JMenuItemCopy");
			this.copyMenuItem.setText("Copy");
		} catch (Throwable caughtThrowable) {
			this.handleException(caughtThrowable);
		}
		return this.copyMenuItem;
	}

	private javax.swing.JMenuItem getCopyAllMenuItem() {
        if (this.copyAllMenuItem != null) return this.copyAllMenuItem;
		try {
			this.copyAllMenuItem = new javax.swing.JMenuItem();
			this.copyAllMenuItem.setName("JMenuItemCopyAll");
			this.copyAllMenuItem.setText("Copy All");
		} catch (Throwable caughtThrowable) {
			this.handleException(caughtThrowable);
		}
		return this.copyAllMenuItem;
	}

	private javax.swing.JMenuItem getPasteMenuItem() {
        if (this.pasteMenuItem != null) return this.pasteMenuItem;
		try {
			this.pasteMenuItem = new javax.swing.JMenuItem();
			this.pasteMenuItem.setName("JMenuItemPaste");
			this.pasteMenuItem.setText("Paste");
		} catch (Throwable caughtThrowable) {
			this.handleException(caughtThrowable);
		}
		return this.pasteMenuItem;
	}

	private javax.swing.JMenuItem getPasteAllMenuItem() {
		if (this.pasteAllMenuItem == null) {
			try {
				this.pasteAllMenuItem = new javax.swing.JMenuItem();
				this.pasteAllMenuItem.setName("JMenuItemPasteAll");
				this.pasteAllMenuItem.setText("Paste All");
			} catch (java.lang.Throwable caughtThrowable) {
				this.handleException(caughtThrowable);
			}
		}
		return this.pasteAllMenuItem;
	}

	//added in july 2008, to accommodate two radio buttons with flow layout.
	private JPanel getRadioButtonAndCheckboxPanel() {
        if (this.radioButtonAndCheckboxPanel != null) return this.radioButtonAndCheckboxPanel;

		JLabel label = new JLabel("Initial Condition: ");
		this.radioButtonAndCheckboxPanel = new JPanel(new FlowLayout());
		this.radioButtonAndCheckboxPanel.add(label);
		this.getButtonGroup();
		this.radioButtonAndCheckboxPanel.add(this.getConcentrationRadioButton());
		this.radioButtonAndCheckboxPanel.add(this.getAmountRadioButton());
		this.radioButtonAndCheckboxPanel.add(this.getRandomizeInitCondCheckbox());
		return this.radioButtonAndCheckboxPanel;
	}

	public void triggerUseConcentrationTask() {
        AsynchClientTask task1 = new ConvertToConcentrationAsynchClientTask();
        AsynchClientTask task2 = new InCaseOfFailureUseParticleCountAsynchClientTask();
		ClientTaskDispatcher.dispatch(this, new Hashtable<>(), new AsynchClientTask[]{task1, task2});
	}

	//following functions are added in July 2008. To enable selection of concentration or particles as initial condition
    //for deterministic method the selection should be disabled (use concentration only).
    //for stochastic it should be enabled.
	private JRadioButton getConcentrationRadioButton() {
        if (this.conRadioButton != null) return this.conRadioButton;
		return this.conRadioButton = new JRadioButton("Concentration", true);
	}

	private void triggerUseParticleCountTask() {
        AsynchClientTask task1 = new ConvertToParticleCountAsynchClientTask();
        AsynchClientTask task2 = new InCaseOfFailureUseConcentrationAsynchClientTask();
		ClientTaskDispatcher.dispatch(this, new Hashtable<>(), new AsynchClientTask[]{task1, task2});
	}

	private JRadioButton getAmountRadioButton() {
        if (null != this.particleCountRadioButton) return this.particleCountRadioButton;
		return this.particleCountRadioButton = new JRadioButton("Number of Particles");
	}

	private JCheckBox getRandomizeInitCondCheckbox() {
        if (null != this.randomizeInitCondCheckBox) return this.randomizeInitCondCheckBox;
		return this.randomizeInitCondCheckBox = new JCheckBox("Randomize Initial Condition");
	}

	private ButtonGroup getButtonGroup() {
        if (null != this.radioGroup) return this.radioGroup;
		this.radioGroup = new ButtonGroup();
		this.radioGroup.add(this.getConcentrationRadioButton());
		this.radioGroup.add(this.getAmountRadioButton());
		return this.radioGroup;
	}

	private void updateTopScrollPanel() {
		switch (this.getSimulationContext().getApplicationType()) {
			case NETWORK_STOCHASTIC -> {
				this.getRadioButtonAndCheckboxPanel().setVisible(true);
				boolean bUsingConcentration = this.getSimulationContext().isUsingConcentration();
				this.getConcentrationRadioButton().setSelected(bUsingConcentration);
				this.getAmountRadioButton().setSelected(!bUsingConcentration);
				// ' make randomizeInitialCondition' checkBox visible only if application is non-spatial stochastic
				this.getRandomizeInitCondCheckbox().setVisible(this.getSimulationContext().getGeometry().getDimension() == 0);
				this.getRandomizeInitCondCheckbox().setSelected(this.getSimulationContext().isRandomizeInitCondition());
			}
			case RULE_BASED_STOCHASTIC, SPRINGSALAD -> {
				this.getRadioButtonAndCheckboxPanel().setVisible(true);
				boolean bUsingConcentration = this.getSimulationContext().isUsingConcentration();
				this.getConcentrationRadioButton().setSelected(bUsingConcentration);
				this.getAmountRadioButton().setSelected(!bUsingConcentration);
				// ' make randomizeInitialCondition' checkBox invisible for now
				this.getRandomizeInitCondCheckbox().setVisible(false);
			}
			default -> this.getRadioButtonAndCheckboxPanel().setVisible(false);
		}
	}

	public SpeciesContextSpec[] getInitConditionVars() {
		SpeciesContextSpec[] speciesContextSpecs = this.getSimulationContext().getReactionContext().getSpeciesContextSpecs();
        //	for(int i=0;i<speciesContextSpecs.length;i++) {
        //		System.out.println(i+" "+speciesContextSpecs[i]+" "+speciesContextSpecs[i].getInitialConditionParameter());
        //	}
		return speciesContextSpecs;
		//Object[] initSpecies = new Object[tableModel.getRowCount()];
	}

	private JSortTable getScrollPaneTable() {
        if (this.table != null) return this.table;
		try {
            this.table = new JSortTable();
            this.table.setName("spceciesContextSpecsTable");
            this.tableModel = new SpeciesContextSpecTableModel(this.table, this);
            this.table.setModel(this.tableModel);
            this.table.setScrollTableActionManager(new InitialConditionsScrollTableActionManager(this.table));
            this.table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} catch (Throwable caughtThrowable) {
			this.handleException(caughtThrowable);
		}
		return this.table;
	}

	public SimulationContext getSimulationContext() {
		return this.simulationContext;
	}

    public void setGeneratedFieldData(String speciesContextName, String newValue) throws ExpressionException {
        //this.getSimulationContext().getReactionContext().getSpeciesContextSpec(speciesContext);
        for (SpeciesContextSpec speciesContextSpec : this.getInitConditionVars()) {
            if (!speciesContextName.equals(speciesContextSpec.getSpeciesContext().getName())) continue;
            speciesContextSpec.getInitialConditionParameter().setExpression(new Expression(newValue));
            break;
        }
        //	tableModel.setValueAt("5.0", 3, 5);
    }

	public void setSimulationContext(SimulationContext newValue) {
		SimulationContext oldValue = this.simulationContext;
		if (oldValue != null) oldValue.removePropertyChangeListener(this.eventHandler);

		this.simulationContext = newValue;
		if (newValue != null) newValue.addPropertyChangeListener(this.eventHandler);

		this.tableModel.setSimulationContext(this.simulationContext);
		this.updateTopScrollPanel();
	}

	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		this.tableModel.setIssueManager(issueManager);
	}

	/*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *\
	|   Menu Action Functions                                           |
	\*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */

	private void menuCopyActionPerformed(final boolean shouldCopyAll) {
		final List<SymbolTableEntry> primarySymbolTableEntries = new java.util.ArrayList<>();
		final List<SymbolTableEntry> alternateSymbolTableEntries = new java.util.ArrayList<>();
		final List<Expression> resolvedValues = new java.util.ArrayList<>();
		final StringBuilder stringRepresentationBuilder = new StringBuilder();
		AsynchClientTask task1 = new ValidateAndComputeCopyAsynchClientTask(primarySymbolTableEntries, alternateSymbolTableEntries, resolvedValues, shouldCopyAll, stringRepresentationBuilder);
		AsynchClientTask task2 = new SmartCopyAsynchClientTask(primarySymbolTableEntries, alternateSymbolTableEntries, resolvedValues, stringRepresentationBuilder);
		ClientTaskDispatcher.dispatch(this, new Hashtable<>(), new AsynchClientTask[]{task1, task2});
	}

	private void menuPasteActionPerformed(final boolean shouldPasteAll, boolean triggeredByKeybind) {
		Object pasteThis = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
		if (!(pasteThis instanceof VCellTransferable.ResolvedValuesSelection resolvedValuesSelection)) {
			if (!triggeredByKeybind) PopupGenerator.showInfoDialog(InitialConditionsPanel.this, "No paste items match the destination (no changes made).");
			return;
		}
		final List<PasteOperationDataSource<Expression>> rawDataSource = new ArrayList<>();
		AsynchClientTask task1 = new ValidateAndComputePasteAsynchClientTask(resolvedValuesSelection, rawDataSource, shouldPasteAll);
		AsynchClientTask task2 = new SmartPasteAsynchClientTask(rawDataSource);
		ClientTaskDispatcher.dispatch(this, new Hashtable<>(), new AsynchClientTask[]{task1, task2});
	}

	public void processPerformedAction(JMenuItem menuItemSelected, boolean triggeredByKeybind) {
		if (menuItemSelected == this.getCopyMenuItem()) this.menuCopyActionPerformed(false);
		else if (menuItemSelected == this.getCopyAllMenuItem()) this.menuCopyActionPerformed(true);
		else if (menuItemSelected == this.getPasteMenuItem()) this.menuPasteActionPerformed(false, triggeredByKeybind);
		else if (menuItemSelected == this.getPasteAllMenuItem()) this.menuPasteActionPerformed(true, triggeredByKeybind);
	}

	private void setUpKeyBinds() {
		String COPY_TASK_NAME = "initialConditionPanelCopyAction", PASTE_TASK_NAME = "initialConditionPanelPasteAction";
		ShortcutsWizard wizard = new ShortcutsWizard(this);
		wizard.configureCopy(wizard.createAction(COPY_TASK_NAME, AsynchClientTask.TASKTYPE_SWING_BLOCKING, this::copyKeybindPerformed));
		wizard.configurePaste(wizard.createAction(PASTE_TASK_NAME, AsynchClientTask.TASKTYPE_SWING_BLOCKING, this::pasteKeybindPerformed));
		// default "super + a" behavior is acceptable for select all.
	}

	private synchronized JMenuItem determineMenuAction(ActionType ACTION){
		boolean nothingSelected = (0 == this.table.getSelectedRowCount());
		return switch (ACTION){
			case COPY -> nothingSelected ? this.getCopyAllMenuItem() :  this.getCopyMenuItem();
			case PASTE -> nothingSelected ? this.getPasteAllMenuItem() :  this.getPasteMenuItem();
		};
	}

	private void copyKeybindPerformed(ActionEvent ignored){
		this.processPerformedAction(this.determineMenuAction(ActionType.COPY), true);
	}

	private void pasteKeybindPerformed(ActionEvent ignored){
		this.processPerformedAction(this.determineMenuAction(ActionType.PASTE), true);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		DocumentEditorSubPanel.setTableSelections(selectedObjects, this.getScrollPaneTable(), this.tableModel);
	}

	private void handleException(Throwable exception) {
		lg.error("Unexpected exception caught: ", exception);
	}

	/*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *\
	|   Helper Sub-Classes                                              |
	\*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */

    protected class ConvertToConcentrationAsynchClientTask extends AsynchClientTask {
        public ConvertToConcentrationAsynchClientTask() {
            super("converting to concentration", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
        }

        @Override
        public void run(Hashtable<String, Object> hashTable) throws Exception {
            boolean bUsingConcentration = InitialConditionsPanel.this.getSimulationContext().isUsingConcentration();
            if (bUsingConcentration) return;
            //was using amount, then it's going to change.
            int numSpatialDimensions = InitialConditionsPanel.this.getSimulationContext().getGeometry().getDimension();
            boolean isSizeNotAllPositive = !InitialConditionsPanel.this.getSimulationContext().getGeometryContext().isAllSizeSpecifiedPositive();
            if (0 == numSpatialDimensions && isSizeNotAllPositive) throw new Exception("\nStructure sizes are required to convert number of particles to concentration.\nPlease go to StructureMapping tab to set valid sizes.");

            //set to use concentration
            InitialConditionsPanel.this.getSimulationContext().setUsingConcentration(true, true);
            // force propertyChange(by setting old value to null), inform other listeners that simulation context has changed.
            //firePropertyChange("simulationContext", null, getSimulationContext());
        }
    }

    protected class ConvertToParticleCountAsynchClientTask extends AsynchClientTask {
        public ConvertToParticleCountAsynchClientTask() {
            super("converting to count", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
        }

        @Override
        public void run(Hashtable<String, Object> hashTable) throws Exception {
            boolean bUseConcentration = InitialConditionsPanel.this.getSimulationContext().isUsingConcentration();
            if (!bUseConcentration) return;

            //was using concentration, then it's going to change.
            if (InitialConditionsPanel.this.getSimulationContext().getGeometry().getDimension() == 0 && !InitialConditionsPanel.this.getSimulationContext().getGeometryContext().isAllSizeSpecifiedPositive()) {
                throw new Exception("\nStructure sizes are required to convert concentration to number of particles.\nPlease go to StructureMapping tab to set valid sizes.");
            }
            //set to use number of particles
            InitialConditionsPanel.this.getSimulationContext().setUsingConcentration(false, true);
            // force propertyChange(by setting old value to null), inform other listeners that simulation context has changed.
            //firePropertyChange("simulationContext", null, getSimulationContext());
        }
    }

    protected class InCaseOfFailureUseConcentrationAsynchClientTask extends AsynchClientTask {
        public InCaseOfFailureUseConcentrationAsynchClientTask() {
            super("in case of failure", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, true);
        }

        @Override
        public void run(Hashtable<String, Object> hashTable) throws Exception {
            if (null == hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR)) return;
            InitialConditionsPanel.this.getSimulationContext().setUsingConcentration(true, true);
            InitialConditionsPanel.this.updateTopScrollPanel();
        }
    }

    protected class InCaseOfFailureUseParticleCountAsynchClientTask extends AsynchClientTask {
        public InCaseOfFailureUseParticleCountAsynchClientTask() {
            super("in case of failure", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, true);
        }

        @Override
        public void run(Hashtable<String, Object> hashTable) throws Exception {
            if (null == hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR)) return;
            InitialConditionsPanel.this.getSimulationContext().setUsingConcentration(false, true);
            InitialConditionsPanel.this.updateTopScrollPanel();
        }
    }

    protected class IcpGeneralScrollTableRenderer extends DefaultScrollTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.setIcon(null);
            this.defaultToolTipText = this.processValue(value, row, column);
            if (null != this.defaultToolTipText) {
                this.setText(this.defaultToolTipText);
                this.setToolTipText(this.defaultToolTipText);
            }
            if (!(table.getModel() instanceof SortTableModel sortTableModel)) return this;
            DefaultScrollTableCellRenderer.issueRenderer(this, this.defaultToolTipText, table, row, column, sortTableModel);
            this.setHorizontalTextPosition(JLabel.TRAILING);
            return this;
        }

        private String processValue(Object value, int row, int column) {
            if (value instanceof Species species) {
                return species.getCommonName();
            } else if (value instanceof SpeciesContext speciesContext) {
                return speciesContext.getName();
            } else if (value instanceof Structure structure) {
                return structure.getName();
            } else if (value instanceof ScopedExpression) {
                SpeciesContextSpec scSpec = InitialConditionsPanel.this.tableModel.getValueAt(row);
                VCUnitDefinition unit;
                if (InitialConditionsPanel.this.table.getColumnName(column).equals(SpeciesContextSpecTableModel.ColumnType.INITIAL_CONDITION.label)) {
                    SpeciesContextSpecParameter initialConditionParameter = scSpec.getInitialConditionParameter();
                    unit = initialConditionParameter.getUnitDefinition();
                } else if (InitialConditionsPanel.this.table.getColumnName(column).equals(SpeciesContextSpecTableModel.ColumnType.DIFFUSION_CONSTANT.label)) {
                    SpeciesContextSpecParameter diffusionParameter = scSpec.getDiffusionParameter();
                    unit = diffusionParameter.getUnitDefinition();
                } else {
	                unit = null;
                }

				String symbolUnicode = null == unit ? "1" : unit.getSymbolUnicode();
	            this.setHorizontalTextPosition(JLabel.LEFT);
	            this.setIcon(new TextIcon("[" + symbolUnicode + "]", DefaultScrollTableCellRenderer.uneditableForeground));
                int rgb = 0x00ffffff & DefaultScrollTableCellRenderer.uneditableForeground.getRGB();
                return "<html>" + StringEscapeUtils.escapeHtml4(this.getText()) + " <font color=#" + Integer.toHexString(rgb) + "> [" + symbolUnicode + "] </font></html>";
            } else {
                lg.warn("Unexpected value type provided; unable to correctly process");
                return null;
            }
        }
    }

    protected class RbmIcpSpeciesShapeDepictionCellRenderer extends DefaultScrollTableCellRenderer {
        SpeciesPatternSmallShape spss = null;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (table.getModel() instanceof VCellSortTableModel<?> sortTableModel) {
	            SpeciesContextSpec selectedObject = null;
                if (sortTableModel == InitialConditionsPanel.this.tableModel) {
                    selectedObject = InitialConditionsPanel.this.tableModel.getValueAt(row);
                }

                if (null == selectedObject){
                    this.spss = null;
                } else {
                    SpeciesContext sc = selectedObject.getSpeciesContext();
                    SpeciesPattern sp = sc.getSpeciesPattern();        // sp may be null for "plain" species contexts
                    Graphics panelContext = table.getGraphics();
                    this.spss = new SpeciesPatternSmallShape(4, 2, sp, InitialConditionsPanel.this.shapeManager, panelContext, sc, isSelected, InitialConditionsPanel.this.issueManager);
                }
            }
            this.setText("");
            return this;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.spss != null) {
                this.spss.paintSelf(g);
            }
        }
    }

    protected class RulesTableCellRenderer extends DefaultScrollTableCellRenderer {
        final Color lightBlueBackground = new Color(214, 234, 248);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!(table.getModel() instanceof SpeciesContextSpecTableModel specsTableModel)) return this;

            Icon icon = VCellIcons.issueGoodIcon;
            Object selectedObject = null;
            if (specsTableModel == InitialConditionsPanel.this.tableModel) {
                selectedObject = InitialConditionsPanel.this.tableModel.getValueAt(row);
            }

            if (!(selectedObject instanceof SpeciesContextSpec scs)){
                this.setIcon(icon);
                return this;
            }

	        if (isSelected) this.setBackground(this.lightBlueBackground);

			String speciesContextName = scs.getSpeciesContext().getName();
			// Combine both rate rules and assignment rules into a single data structure to look through.
			RuleVariableAccessible[] rateRules = InitialConditionsPanel.this.simulationContext.getRateRules();
	        RuleVariableAccessible[] assignmentRules = InitialConditionsPanel.this.simulationContext.getAssignmentRules();
            List<? extends RuleVariableAccessible> rulesToSearch = Stream.concat(
					Arrays.stream(null != rateRules ? rateRules : new RuleVariableAccessible[0]),
		            Arrays.stream(null != assignmentRules ? assignmentRules: new RuleVariableAccessible[0])
            ).toList();

			for (RuleVariableAccessible rule : rulesToSearch) { // Set icon based on matching rule
				if (null == rule.getRuleVar()) continue;
				if (speciesContextName.equals(rule.getRuleVar().getName())) continue;
				icon = rule instanceof RateRule ? VCellIcons.ruleRateIcon : VCellIcons.ruleAssignIcon;
				break;
			}

	        this.setIcon(icon);
			return this;
        }
    }

	protected class InitialConditionsScrollTableActionManager extends DefaultScrollTableActionManager {

		InitialConditionsScrollTableActionManager(JTable table) {
			super(table);
		}

		@Override
		protected void constructPopupMenu() {
			if (this.popupMenu == null) {
				super.constructPopupMenu();
				int pos = 0;
				this.popupMenu.insert(InitialConditionsPanel.this.getCopyMenuItem(), pos++);
				this.popupMenu.insert(InitialConditionsPanel.this.getCopyAllMenuItem(), pos++);
				this.popupMenu.insert(InitialConditionsPanel.this.getPasteMenuItem(), pos++);
				this.popupMenu.insert(InitialConditionsPanel.this.getPasteAllMenuItem(), pos++);
				//DocumentEditorSubPanel.addFieldDataMenuItem(getOwnerTable(), popupMenu, pos++); // Disabled while we fix OverlayEditorPanelJAI (7/16/2024)
				this.popupMenu.insert(new JSeparator(), pos++);
			}
			Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
			boolean bPastable = obj instanceof VCellTransferable.ResolvedValuesSelection;
			int[] selectedRows = InitialConditionsPanel.this.getScrollPaneTable().getSelectedRows();
			boolean bSomethingSelected = null != selectedRows && selectedRows.length > 0;
			InitialConditionsPanel.this.getPasteMenuItem().setEnabled(bPastable && bSomethingSelected);
			InitialConditionsPanel.this.getPasteAllMenuItem().setEnabled(bPastable);
			InitialConditionsPanel.this.getCopyMenuItem().setEnabled(bSomethingSelected);
		}
	}

	protected class ValidateAndComputeCopyAsynchClientTask extends AsynchClientTask {
		final List<SymbolTableEntry> primarySymbolTableEntries;
		final List<SymbolTableEntry> alternateSymbolTableEntries;
		final List<Expression> resolvedValues ;
		final boolean shouldCopyAll;
		final StringBuilder stringRepresentationBuilder;

		public ValidateAndComputeCopyAsynchClientTask(final List<SymbolTableEntry> primarySymbolTableEntries,
		                                              final List<SymbolTableEntry> alternateSymbolTableEntries,
		                                              final List<Expression> resolvedValues, final boolean shouldCopyAll,
		                                              final StringBuilder stringRepresentationBuilder){
			super("validating copy request", AsynchClientTask.TASKTYPE_SWING_BLOCKING);
			this.primarySymbolTableEntries = primarySymbolTableEntries;
			this.alternateSymbolTableEntries = alternateSymbolTableEntries;
			this.resolvedValues = resolvedValues;
			this.shouldCopyAll = shouldCopyAll;
			this.stringRepresentationBuilder = stringRepresentationBuilder;
		}

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			//Copy Symbols and Values Init Conditions
			MathSymbolMapping msm = null;
			try {
				msm = InitialConditionsPanel.this.getSimulationContext().createNewMathMapping().getMathSymbolMapping();
			} catch (Exception e) {
				lg.warn("current math not valid, some paste operations will be limited; reason: ", e);
				DialogUtils.showWarningDialog(InitialConditionsPanel.this, "current math not valid, some paste operations will be limited\n\nreason: " + e.getMessage());
			}

			this.stringRepresentationBuilder.append("Initial Conditions Parameters for (BioModel)")
					.append(InitialConditionsPanel.this.getSimulationContext().getBioModel().getName())
					.append(" (App)").append(InitialConditionsPanel.this.getSimulationContext().getName())
					.append("\n");

			IntStream rows = this.shouldCopyAll ? IntStream.range(0, InitialConditionsPanel.this.tableModel.getRowCount()) : Arrays.stream(InitialConditionsPanel.this.getScrollPaneTable().getSelectedRows());
			for (int row : rows.toArray()) {
				SpeciesContextSpec scs = InitialConditionsPanel.this.tableModel.getValueAt(row);
				if (scs.isClamped()) {
					this.primarySymbolTableEntries.add(scs.getInitialConditionParameter()); //need to change
					this.alternateSymbolTableEntries.add(null == msm ? null : msm.getVariable(scs.getSpeciesContext()));
					this.resolvedValues.add(new Expression(scs.getInitialConditionParameter().getExpression()));
					this.stringRepresentationBuilder.append(scs.getSpeciesContext().getName()).append("\t").append(scs.getInitialConditionParameter().getName()).append("\t").append(scs.getInitialConditionParameter().getExpression().infix()).append("\n");
				} else {
					SpeciesContextSpecParameter[] speciesContextSpecParameters = scs.getParameters();
					for (SpeciesContextSpecParameter parameter : speciesContextSpecParameters) {
						if (!ValidateAndComputeCopyAsynchClientTask.isSCSRoleForDimension(parameter.getRole(), InitialConditionsPanel.this.getSimulationContext().getGeometry().getDimension())) continue;
						Expression scspExpression = parameter.getExpression();
						this.stringRepresentationBuilder.append(scs.getSpeciesContext().getName()).append("\t").append(parameter.getName()).append("\t").append(scspExpression != null ? scspExpression.infix() : "").append("\n");
						if (null == scspExpression) continue;

						// "Default" boundary conditions can't be copied
						this.primarySymbolTableEntries.add(parameter);
						this.alternateSymbolTableEntries.add(null == msm ? null : msm.getVariable(parameter));
						this.resolvedValues.add(new Expression(scspExpression));
					}
				}
			}
			if (this.resolvedValues.isEmpty()) this.stringRepresentationBuilder.append("No Resolved Values Found");
		}

		private static boolean isSCSRoleForDimension(int scsRole, int dimension) {
			if (scsRole == SpeciesContextSpec.ROLE_InitialConcentration) return true;
			if (dimension < 1) return false;
			if (scsRole == SpeciesContextSpec.ROLE_DiffusionRate) return true;
			if (scsRole == SpeciesContextSpec.ROLE_BoundaryValueXm) return true;
			if (scsRole == SpeciesContextSpec.ROLE_BoundaryValueXp) return true;
			if (dimension < 2) return false;
			if (scsRole == SpeciesContextSpec.ROLE_BoundaryValueYm) return true;
			if (scsRole == SpeciesContextSpec.ROLE_BoundaryValueYp) return true;
			if (dimension < 3) return false;
			if (scsRole == SpeciesContextSpec.ROLE_BoundaryValueZm) return true;
			return scsRole == SpeciesContextSpec.ROLE_BoundaryValueZp;
		}
	}

	protected class SmartCopyAsynchClientTask extends AsynchClientTask {
		final List<SymbolTableEntry> primarySymbolTableEntries;
		final List<SymbolTableEntry> alternateSymbolTableEntries;
		final List<Expression> resolvedValues;
		final StringBuilder stringRepresentationBuilder;

		public SmartCopyAsynchClientTask(final List<SymbolTableEntry> primarySymbolTableEntries,
		                                 final List<SymbolTableEntry> alternateSymbolTableEntries,
		                                 final List<Expression> resolvedValues, final StringBuilder stringRepresentationBuilder){
			super("copying", AsynchClientTask.TASKTYPE_SWING_BLOCKING);
			this.primarySymbolTableEntries = primarySymbolTableEntries;
			this.alternateSymbolTableEntries = alternateSymbolTableEntries;
			this.resolvedValues = resolvedValues;
			this.stringRepresentationBuilder = stringRepresentationBuilder;
		}

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (this.stringRepresentationBuilder.isEmpty()){
				PopupGenerator.showInfoDialog(InitialConditionsPanel.this, "No items were successfully copied.");
				return;
			}
			//Send to clipboard
			VCellTransferable.ResolvedValuesSelection rvs =
					new VCellTransferable.ResolvedValuesSelection(
							this.primarySymbolTableEntries.toArray(SymbolTableEntry[]::new),
							this.alternateSymbolTableEntries.toArray(SymbolTableEntry[]::new),
							this.resolvedValues.toArray(Expression[]::new),
							this.stringRepresentationBuilder.toString());

			VCellTransferable.sendToClipboard(rvs);
		}
	}

	protected class ValidateAndComputePasteAsynchClientTask extends AsynchClientTask {
		final VCellTransferable.ResolvedValuesSelection resolvedValue;
		final List<PasteOperationDataSource<Expression>> rawDataSource;
		final boolean shouldPasteAll;


		public ValidateAndComputePasteAsynchClientTask(
				final VCellTransferable.ResolvedValuesSelection resolvedValuesSelection,
				final List<PasteOperationDataSource<Expression>> rawDataSource,
				final boolean shouldPasteAll
		) {
			super("validating paste request", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
			this.resolvedValue = resolvedValuesSelection;
			this.rawDataSource = rawDataSource;
			this.shouldPasteAll = shouldPasteAll;
		}

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			MathSymbolMapping mathSymbolMapping;
			Exception mathMappingException;
			try {
				MathMapping mm = InitialConditionsPanel.this.getSimulationContext().createNewMathMapping();
				mathSymbolMapping = mm.getMathSymbolMapping();
				mathMappingException = null;
			} catch (Exception e) {
				mathSymbolMapping = null;
				mathMappingException = e;
				lg.warn("Exception generated while determining MathSymbolMapping:", e);
			}
			StringBuilder errors = new StringBuilder();
			IntStream rows = this.shouldPasteAll ? IntStream.range(0, InitialConditionsPanel.this.getScrollPaneTable().getRowCount()) : Arrays.stream(InitialConditionsPanel.this.getScrollPaneTable().getSelectedRows());

			//Check each row to see if we can paste
			for (int row : rows.toArray()) this.validate(this.resolvedValue, InitialConditionsPanel.this.tableModel.getValueAt(row), errors, mathSymbolMapping, mathMappingException);
			if (!errors.toString().isBlank()) throw new Exception(errors.toString());
		}

		private void validate(final VCellTransferable.ResolvedValuesSelection pasteThis, final SpeciesContextSpec scs, final StringBuilder errors,
		                               final MathSymbolMapping mathSymbolMapping, final Exception mathMappingException){
			try {
				Queue<SymbolTableEntry> primaryEntries = new LinkedList<>(Arrays.stream(pasteThis.getPrimarySymbolTableEntries()).toList());

				SymbolTableEntry[] alternateEntriesArr = pasteThis.getAlternateSymbolTableEntries();
				Queue<SymbolTableEntry> alternateEntries = alternateEntriesArr == null ? new LinkedList<>() : new LinkedList<>(Arrays.stream(pasteThis.getAlternateSymbolTableEntries()).toList());

				Object[] resolvedValues = pasteThis.getValues(); // We only want the expressions from this list
				Queue<Expression> expressionValues = new LinkedList<>(Arrays.stream(resolvedValues).filter((elem)-> elem instanceof Expression).map(Expression.class::cast).toList());

				while(!primaryEntries.isEmpty()){
					SymbolTableEntry primaryEntry = primaryEntries.poll();
					SymbolTableEntry alternateEntry = alternateEntries.poll(); // will return null if empty! That's desired behavior
					Expression correlatedExpression = expressionValues.poll();

					// Determine paste destination
					SpeciesContextSpecParameter pasteDestination = this.determinePasteDestination(scs,
							primaryEntry, alternateEntry, mathSymbolMapping, mathMappingException);

					if (pasteDestination == null) continue;
					if (correlatedExpression == null) throw new NullPointerException("The expression that should be correlated with destination `" + pasteDestination.getName()  + "` is null!");
					this.rawDataSource.add(new PasteOperationScspDataSource(scs.getSpeciesContext(), pasteDestination, correlatedExpression));
				}
			} catch (Throwable e) {
				errors.append(scs.getSpeciesContext().getName()).append(" (").append(e.getClass().getName()).append(") ").append(e.getMessage()).append("\n\n");
			}
		}

		private SpeciesContextSpecParameter determinePasteDestination(final SpeciesContextSpec scs, final SymbolTableEntry primaryEntry, final SymbolTableEntry alternateEntry,
		                                                              final MathSymbolMapping mathSymbolMapping, final Exception mathMappingException) throws Exception {

			if (primaryEntry instanceof SpeciesContextSpecParameter speciesContextSpecParameter) {
				return this.processSpeciesContextSpecParameter(speciesContextSpecParameter, scs);
			} else if (alternateEntry instanceof SpeciesContextSpecParameter speciesContextSpecParameter) {
				return this.processSpeciesContextSpecParameter(speciesContextSpecParameter, scs);
			} else if (primaryEntry instanceof Variable mathVariable){
				if (null == mathSymbolMapping) throw mathMappingException;
				return this.processVariableParameter(mathVariable, mathSymbolMapping, scs);
			} else if (alternateEntry instanceof Variable mathVariable){
				if (null == mathSymbolMapping) throw mathMappingException;
				return this.processVariableParameter(mathVariable, mathSymbolMapping, scs);
			} else {
				return null;
			}
		}

		private SpeciesContextSpecParameter processSpeciesContextSpecParameter(final SpeciesContextSpecParameter clipboardBiologicalParameter, final SpeciesContextSpec scs){
			SpeciesContext speciesContext = ((SpeciesContextSpec) clipboardBiologicalParameter.getNameScope().getScopedSymbolTable()).getSpeciesContext();
			if (!scs.getSpeciesContext().compareEqual(speciesContext)) return null;

			for (SpeciesContextSpecParameter scsp: scs.getParameters()){
				if (scsp.getRole() != clipboardBiologicalParameter.getRole()) continue;
				return scsp;
			}
			return null;
		}

		private SpeciesContextSpecParameter processVariableParameter(final Variable mathVariable, final MathSymbolMapping mathSymbolMapping, final SpeciesContextSpec scs){
			Variable localMathVariable = mathSymbolMapping.findVariableByName(mathVariable.getName());
			if (null == localMathVariable) {
				// try if localMathVariable is a speciesContext init parameter
				String initSuffix = DiffEquMathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX + TokenMangler.fixTokenStrict(scs.getInitialConcentrationParameter().getUnitDefinition().getSymbol());
				localMathVariable = mathSymbolMapping.findVariableByName(mathVariable.getName() + initSuffix);
			}
			if (null == localMathVariable) return null; // Still couldn't find it
			for (SymbolTableEntry symbolTableEntry : mathSymbolMapping.getBiologicalSymbol(localMathVariable)) {
				if (symbolTableEntry instanceof SpeciesContext && scs.getSpeciesContext() == symbolTableEntry) {
					SpeciesContextSpecParameter potentialPasteDestination = scs.getInitialConditionParameter();
					if (null != potentialPasteDestination) return potentialPasteDestination;
				} else if (symbolTableEntry instanceof SpeciesContextSpecParameter speciesContextSpecParameter) {
					for (SymbolTableEntry parameter : scs.getParameters()) {
						if (parameter != symbolTableEntry) continue;
						return speciesContextSpecParameter;
					}
				}
			}
			lg.warn("Was not able to find destination for math variable `{}`.", mathVariable.getName());
			return null;
		}
	}

	protected class SmartPasteAsynchClientTask extends AsynchClientTask {
		final List<PasteOperationDataSource<Expression>> rawChangeData;

		public SmartPasteAsynchClientTask(List<PasteOperationDataSource<Expression>> rawChangeData){
			super("pasting", AsynchClientTask.TASKTYPE_SWING_BLOCKING);
			this.rawChangeData = rawChangeData;
		}

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (this.rawChangeData.isEmpty()){
				PopupGenerator.showInfoDialog(InitialConditionsPanel.this, "No paste items match the destination (no changes made).");
				return;
			}
			VCellCopyPasteHelper.chooseApplyPaste(InitialConditionsPanel.this, this.rawChangeData);
		}
	}

	private class EventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			Object source = e.getSource();

			if (source instanceof JMenuItem menuItemSource){
				InitialConditionsPanel.this.processPerformedAction(menuItemSource, false);
			} else if (e.getSource() == InitialConditionsPanel.this.getAmountRadioButton()) {
				InitialConditionsPanel.this.triggerUseParticleCountTask();
			} else if (e.getSource() == InitialConditionsPanel.this.getConcentrationRadioButton()) {
				InitialConditionsPanel.this.triggerUseConcentrationTask();
			} else if (e.getSource() == InitialConditionsPanel.this.getRandomizeInitCondCheckbox()) {
				// only need to set simContext.isRandomizeInitCondn?
				InitialConditionsPanel.this.getSimulationContext().setRandomizeInitConditions(InitialConditionsPanel.this.getRandomizeInitCondCheckbox().isSelected());
			}
		}

		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() != InitialConditionsPanel.this.getSimulationContext()) return;
			if (!SimulationContext.PROPERTY_NAME_USE_CONCENTRATION.equals(evt.getPropertyName())) return;
			InitialConditionsPanel.this.updateTopScrollPanel();
		}

		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) return;
			if (e.getSource() != InitialConditionsPanel.this.getScrollPaneTable().getSelectionModel()) return;

			int[] row = InitialConditionsPanel.this.getScrollPaneTable().getSelectedRows();
			List<SpeciesContextSpec> selectedSpecies = Arrays.stream(row).mapToObj(InitialConditionsPanel.this.tableModel::getValueAt).toList();
			InitialConditionsPanel.this.selectionManager.setSelectedObjects(selectedSpecies.toArray());
		}
	}

}
