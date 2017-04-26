package org.vcell.chombo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.vcell.chombo.ChomboSolverSpec;
import org.vcell.chombo.RefinementRoi;
import org.vcell.chombo.RefinementRoi.RoiType;
import org.vcell.util.ISize;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.VCellIcons;

import com.lowagie.text.Font;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverTaskDescription;

@SuppressWarnings("serial")
public class ChomboSolverSpecPanel extends CollapsiblePanel {
	
	public class RefinementRoiPanel extends JPanel 
	{	
		private JButton addButton;
		private JButton deleteButton;
		private RoiType roiType;
		
		private class InternalEventHandler implements ActionListener, ListSelectionListener {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == getAddLevelButton()) {
					addButton_actionPerformed();
				} else if (e.getSource() == getDeleteLevelButton()) {
					deleteButton_actionPerformed();
				} 
			}

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int[] rows = roiTable.getSelectedRows();
				if (rows.length > 0)
				{
					enableDeleteButton();
				}
			}
		}
		
		private class RoiTableModel extends AbstractTableModel
		{
			private final String[] columns = new String[]{"ROI", "Refinement levels", "\u0394x"};
			private final int COLUMN_ROI = 0;
			private final int COLUMN_numLevels = 1;
			private final int COLUMN_dx = 2;
			private AutoCompleteSymbolFilter autoCompleteSymbolFilter = new AutoCompleteSymbolFilter() {
				public boolean accept(SymbolTableEntry ste) {
					int dimension = simulation.getMathDescription().getGeometry().getDimension();
					if (ste.equals(ReservedVariable.X) || dimension > 1 && ste.equals(ReservedVariable.Y) || dimension > 2 && ste.equals(ReservedVariable.Z)) {
						return true;
					}
					return false;
				}
				public boolean acceptFunction(String funcName) {
					return false;
				}	   
			};
			
			@Override
			public int getRowCount() {
				if (isSimulationChombo())
				{
					List<RefinementRoi> roiList = getRefinementRoiList();
					return roiList.size();
				}
				return 0;
			}

			@Override
			public int getColumnCount() {
				return columns.length;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (isSimulationChombo()) 
				{
					List<RefinementRoi> roiList = getRefinementRoiList();
					if (roiList.size() == 0 || rowIndex >= roiList.size())
					{
						return null;
					}
					RefinementRoi roi = roiList.get(rowIndex);
					switch (columnIndex)
					{
					case COLUMN_numLevels:
						return roi.getLevel();
					case COLUMN_ROI:
						if (roi.getRoiExpression() == null)
						{
							return null;
						}
						return new ScopedExpression(roi.getRoiExpression(), ReservedVariable.X.getNameScope(), true, true, autoCompleteSymbolFilter);
					case COLUMN_dx:
						return roi.getDx(simulation);
					}
				}
				return null;
			}
			
			private List<RefinementRoi> getRefinementRoiList()
			{
				ChomboSolverSpec chomboSolverSpec = simulation.getSolverTaskDescription().getChomboSolverSpec();
				return chomboSolverSpec.getRefinementRois(roiType);
			}

			@Override
			public String getColumnName(int column) {
				return columns[column];
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex)
				{
				case COLUMN_numLevels:
					return Integer.class;
				case COLUMN_ROI:
					return ScopedExpression.class;
				case COLUMN_dx:
					return Double.class;
				}
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				try
				{
					List<RefinementRoi> roiList = getRefinementRoiList();
					RefinementRoi roi = roiList.get(rowIndex);
					switch (columnIndex)
					{
					case COLUMN_numLevels:
						roi.setLevel((Integer)aValue);
						break;
					case COLUMN_ROI:
						if (aValue instanceof String) {
							final String str = (String)aValue;
							roi.setRoiExpression(str);
						}
						break;
					}
					fireTableDataChanged();
					updateDisplay();
				}
				catch (Exception ex)
				{
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(roiTable, ex.getMessage(), ex);
				}
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return isSimulationChombo() && columnIndex != COLUMN_dx 
						&& simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels() > 0;
			}
			
			/**
			 * is current simulation chombo solver?
			 * @return true if is
			 */
			private boolean isSimulationChombo() {
				return simulation != null && simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver();
			}
		}
		
		private ScrollTable roiTable;
		private RoiTableModel roiTableModel;
		private JComboBox<Integer> refinementRatioComboBox;
		private InternalEventHandler internalEventHandler = new InternalEventHandler();
		
		public RefinementRoiPanel(RoiType roiType)
		{
			this.roiType = roiType;
			initialize();
		}
		
		private void initialize()
		{
			roiTable = new ScrollTable();
			roiTable.getSelectionModel().addListSelectionListener(internalEventHandler);
			roiTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			roiTableModel = new RoiTableModel();
			roiTable.setModel(roiTableModel);
			refinementRatioComboBox = new JComboBox<Integer>();
			for (int i = 1; i < 8; ++ i)
			{
				refinementRatioComboBox.addItem(i);
			}
			DefaultCellEditor cellEditor = new DefaultCellEditor(refinementRatioComboBox);
			cellEditor.setClickCountToStart(2);
			roiTable.getColumnModel().getColumn(roiTableModel.COLUMN_numLevels).setCellEditor(cellEditor);

			JToolBar toolbar = new JToolBar();
			toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
			toolbar.setFloatable(false);
			JLabel label = new JLabel("ROIs");
			toolbar.add(label);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			toolbar.add(Box.createHorizontalStrut(10));
			toolbar.add(getAddLevelButton());
			toolbar.add(getDeleteLevelButton());
			toolbar.add(Box.createHorizontalGlue());
			if (roiType == RoiType.Membrane)
			{
				label = new JLabel("(Set ROI to \"1.0\" to refine all membrane elements.)");
				label.setFont(label.getFont().deriveFont(Font.BOLDITALIC, label.getFont().getSize() - 1));
				toolbar.add(label);
				toolbar.add(Box.createHorizontalStrut(10));
			}
					
			setLayout(new GridBagLayout());		
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(0, 5, 0, 0);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			add(toolbar, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.PAGE_START;
			add(roiTable.getEnclosingScrollPane(), gbc);
			
			getAddLevelButton().addActionListener(internalEventHandler);
			getDeleteLevelButton().addActionListener(internalEventHandler);
		}

		private void addButton_actionPerformed() 
		{	
			try {
				RefinementRoi roi = RefinementRoi.createNewRoi(roiType, simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels() + 1);
				simulation.getSolverTaskDescription().getChomboSolverSpec().addRefinementRoi(roi);
				roiTableModel.fireTableDataChanged();
			} catch (ExpressionException e) {
				
			}
		}

		private void deleteButton_actionPerformed() 
		{
			int rowIndex = roiTable.getSelectedRow();
			simulation.getSolverTaskDescription().getChomboSolverSpec().deleteRefinementRoi(roiType, rowIndex);
			roiTableModel.fireTableDataChanged();
			enableDeleteButton();
		}

		private void enableDeleteButton()
		{
			deleteButton.setEnabled(roiTableModel.getRefinementRoiList().size() > 0);
		}
		
		private JButton getAddLevelButton() {
			if (addButton == null) {
				try {
					addButton = new JButton(VCellIcons.refLevelNewIcon);
					addButton.setToolTipText("Add a Refinement Level");
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}	
			}
			return addButton;
		}

		private JButton getDeleteLevelButton() {
			if (deleteButton == null) {
				try {
					deleteButton = new JButton(VCellIcons.refLevelDeleteIcon);
					deleteButton.setToolTipText("Delete a Refinement Level");
					deleteButton.setEnabled(false);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
			return deleteButton;
		}

		private void handleException(Throwable exception) {
			System.out.println("--------- UNCAUGHT EXCEPTION --------- in SolverTaskDescriptionPanel");
			exception.printStackTrace(System.out);
		}	
	}
	private class EventHandler implements ActionListener, FocusListener, PropertyChangeListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == maxBoxSizeComboBox)
			{
				setMaxBoxSize();
			}
			else if (e.getSource() == blockFactorComboBox)
			{
				setBlockFactor();
			}
			else if (e.getSource() == viewLevelComboBox)
			{
				if (viewLevelComboBox.getItemCount() > 0)
				{
					setViewLevel();
				}
			}
			else if (e.getSource() == viewLevelFinestRadioButton)
			{
				simulation.getSolverTaskDescription().getChomboSolverSpec().setFinestViewLevel(viewLevelFinestRadioButton.isSelected());
			}
			else if (e.getSource() == viewLevelUserSelectRadioButton)
			{
				viewLevelComboBox.setEnabled(true);
				setViewLevel();
			}
			else if (e.getSource() == checkBoxTagsGrow)
			{
				simulation.getSolverTaskDescription().getChomboSolverSpec().enableTagsGrow(checkBoxTagsGrow.isSelected());
			}
		}
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == simulation.getSolverTaskDescription() && evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION)) {
				// solver changed, reset simulation
				setSimulation(simulation);
			}
			else if (evt.getSource() == simulation.getSolverTaskDescription().getChomboSolverSpec() || evt.getSource() == simulation.getMeshSpecification())
			{
				updateDisplay();
			}
			else if (evt.getSource() == simulation.getMeshSpecification())
			{
				updateFinestInfoPanel();
			}
		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == fillRatioTextField)
			{
				if (fillRatioTextField.getText() == null || fillRatioTextField.getText().trim().length() == 0)
				{
					fillRatioTextField.setText(simulation.getSolverTaskDescription().getChomboSolverSpec().getFillRatio() + "");
				}
				else
				{
					setFillRatio();
				}
			}
		}
	}
	
	private EventHandler eventHandler = new EventHandler();
	private Simulation simulation = null;
	private JComboBox<Integer> maxBoxSizeComboBox = null;
	private JTextField fillRatioTextField;
	private JComboBox<Integer> blockFactorComboBox = null;
	private JCheckBox checkBoxTagsGrow = null;
	private JPanel finestInfoPanel;
	private JTextField finestSizeTextField;
	private JPanel refinementPanel;
	private JComboBox<Integer> viewLevelComboBox = null;
	private RefinementRoiPanel membraneRoiPanel = null;
	private RefinementRoiPanel volumeRoiPanel = null;
	private JRadioButton viewLevelFinestRadioButton = null;
	private JRadioButton viewLevelUserSelectRadioButton = null;

	public ChomboSolverSpecPanel() {
		super("Mesh Refinement");
		initialize();
	}

	private JPanel getFinestInfoPanel()
	{
		if (finestInfoPanel == null)
		{
		  finestInfoPanel  = new JPanel(new GridBagLayout());
		  finestInfoPanel.setVisible(false);
		  
		  finestSizeTextField = new JTextField(40);
		  finestSizeTextField.setForeground(Color.blue);
		  finestSizeTextField.setBorder(null);
		  finestSizeTextField.setEditable(false);
		  
		  int gridx = 0;
		  GridBagConstraints gbc = new GridBagConstraints();
		  gbc.gridx = gridx;
		  gbc.gridy = 0;
		  gbc.insets = new Insets(1, 5, 1, 2);
		  JLabel label = new JLabel("Finest Mesh");
		  label.setFont(label.getFont().deriveFont(Font.BOLD));
			finestInfoPanel.add(label, gbc);
		  
			++ gridx;
		  gbc = new GridBagConstraints();
		  gbc.gridx = gridx;
		  gbc.gridy = 0;
		  gbc.weightx = 0.5;
		  gbc.insets = new Insets(1, 2, 1, 2);
		  gbc.fill = GridBagConstraints.HORIZONTAL;
		  finestInfoPanel.add(finestSizeTextField, gbc);
		  
		  ++ gridx;
		  gbc = new GridBagConstraints();
		  gbc.gridx = gridx;
		  gbc.gridy = 0;
		  gbc.weightx = 1.0;
		  gbc.insets = new Insets(1, 5, 5, 10);
		  gbc.fill = GridBagConstraints.HORIZONTAL;
		  gbc.anchor = GridBagConstraints.EAST;
		  label = new JLabel("(Refinement ratio is 2 per level.)");
		  label.setHorizontalAlignment(SwingConstants.RIGHT);
			label.setFont(label.getFont().deriveFont(Font.BOLDITALIC, label.getFont().getSize() - 1));
			finestInfoPanel.add(label, gbc);
		}
		return finestInfoPanel;
	}
	
	private void initialize() 
	{
		setMaximumSize(new Dimension(500, 150));
		setPreferredSize(new Dimension(500, 200));
		
		maxBoxSizeComboBox = new JComboBox<Integer>();
		int start = 8;
		for (int i = 0; i < 9; ++ i)
		{
			maxBoxSizeComboBox.addItem(start);
			start *= 2;
		}
		maxBoxSizeComboBox.addItem(0);
		
		blockFactorComboBox = new JComboBox<Integer>();
		start = 4;
		for (int i = 0; i < 4; ++ i)
		{
			blockFactorComboBox.addItem(start);
			start *= 2;
		}
		blockFactorComboBox.setEnabled(false);
		
		checkBoxTagsGrow = new JCheckBox("Tags Grow");
		
		viewLevelFinestRadioButton = new JRadioButton("Finest");
		viewLevelUserSelectRadioButton = new JRadioButton("Select");
		viewLevelFinestRadioButton.addActionListener(eventHandler);
		viewLevelUserSelectRadioButton.addActionListener(eventHandler);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(viewLevelFinestRadioButton);
		bg.add(viewLevelUserSelectRadioButton);
		
		viewLevelComboBox = new JComboBox<Integer>();
		viewLevelComboBox.setEnabled(false);
		viewLevelComboBox.setRenderer(new DefaultListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value != null && (Integer)value <= simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels())
				{
					int dim = simulation.getMathDescription().getGeometry().getDimension();
					ISize xyz = simulation.getSolverTaskDescription().getChomboSolverSpec().getLevelSamplingSize(simulation.getMeshSpecification().getSamplingSize(), (Integer)value);
					setText(GuiUtils.getMeshSizeText(dim, xyz, false));
				}
				return this;
			}
		});
		
		fillRatioTextField = new JTextField();
		fillRatioTextField.addFocusListener(eventHandler);
		
		CollapsiblePanel southPanel = new CollapsiblePanel("Advanced", false);
		southPanel.getContentPanel().setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 1);		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		southPanel.getContentPanel().add(new JLabel("Max Box Size"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 1, 4, 4);
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 0.1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		southPanel.getContentPanel().add(maxBoxSizeComboBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 1);		
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		southPanel.getContentPanel().add(new JLabel("Fill Ratio"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 1, 4, 4);
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.weightx = 0.1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		southPanel.getContentPanel().add(fillRatioTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 1);		
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		southPanel.getContentPanel().add(new JLabel("Block Factor"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 1, 4, 4);
		gbc.gridx = 5;
		gbc.gridy = gridy;
		gbc.weightx = 0.1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		southPanel.getContentPanel().add(blockFactorComboBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 1, 4, 4);
		gbc.gridx = 6;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		southPanel.getContentPanel().add(checkBoxTagsGrow, gbc);
		
		JPanel northPanel = new JPanel(new GridBagLayout());
		JLabel lbl = new JLabel("<html>Enter Boolean expressions for regions of interest to refine, and set max refinement level desired for each ROI, where each level doubles the mesh resolution.</html>");
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		northPanel.add(lbl, gbc);
		
		getContentPanel().setLayout(new BorderLayout(0, 2));
		getContentPanel().add(southPanel, BorderLayout.SOUTH);
		getContentPanel().add(northPanel, BorderLayout.NORTH);
		getContentPanel().add(getRefinementPanel(), BorderLayout.CENTER);
		
		maxBoxSizeComboBox.addActionListener(eventHandler);
		blockFactorComboBox.addActionListener(eventHandler);
		checkBoxTagsGrow.addActionListener(eventHandler);
	}

	private JPanel getRefinementPanel() {
		if (refinementPanel == null)
		{
			membraneRoiPanel = new RefinementRoiPanel(RoiType.Membrane);
			volumeRoiPanel = new RefinementRoiPanel(RoiType.Volume);
			JTabbedPane tabbedPanel = new JTabbedPane();
			tabbedPanel.addTab("Refine Near Membrane", membraneRoiPanel);
			tabbedPanel.addTab("Refine Interior Region", volumeRoiPanel);
			refinementPanel = new JPanel();
			refinementPanel.setLayout(new GridBagLayout());		
			refinementPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(5, 5, 0, 0);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridwidth = 4;
			gbc.fill = GridBagConstraints.BOTH;
			refinementPanel.add(tabbedPanel, gbc);
			
			gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(0, 5, 0, 0);
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth = 4;
			refinementPanel.add(getFinestInfoPanel(), gbc);
			
			gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(4, 10, 4, 1);		
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.EAST;
			JLabel lbl = new JLabel("View Level");
			lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
			refinementPanel.add(lbl, gbc);
			
			gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(4, 1, 4, 1);
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			refinementPanel.add(viewLevelFinestRadioButton, gbc);
			
			gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(4, 1, 4, 1);
			gbc.gridx = 2;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			refinementPanel.add(viewLevelUserSelectRadioButton, gbc);
			
			gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(4, 1, 4, 1);
			gbc.gridx = 3;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.WEST;
			refinementPanel.add(viewLevelComboBox, gbc);
		}
		return refinementPanel;
	}

	private void updateFinestInfoPanel()
	{
		getFinestInfoPanel().setVisible(true);
		
		ISize finestSize = simulation.getSolverTaskDescription().getChomboSolverSpec().getFinestSamplingSize(simulation.getMeshSpecification().getSamplingSize());
		String sizeDisplay = finestSize.getX() + " x " + finestSize.getY();
		if (simulation.getMathDescription().getGeometry().getDimension() > 2)
		{
			sizeDisplay += " x " + finestSize.getZ();
		}
		
		finestSizeTextField.setText(sizeDisplay);
		finestSizeTextField.setCaretPosition(0);
		finestSizeTextField.setToolTipText(sizeDisplay);
	}
	
	private void updateDisplay() {
		if (!simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver()) 
		{
			setVisible(false);
			return;
		}
		setVisible(true);
		ChomboSolverSpec chomboSolverSpec = simulation.getSolverTaskDescription().getChomboSolverSpec();
		maxBoxSizeComboBox.setSelectedItem(chomboSolverSpec.getMaxBoxSize());
		blockFactorComboBox.setSelectedItem(chomboSolverSpec.getBlockFactor());
		fillRatioTextField.setText(chomboSolverSpec.getFillRatio() + "");
		checkBoxTagsGrow.setSelected(chomboSolverSpec.isTagsGrowEnabled());
		
		updateViewLevel();
		updateFinestInfoPanel();
	}
	
	private void updateViewLevel()
	{
		ChomboSolverSpec chomboSolverSpec = simulation.getSolverTaskDescription().getChomboSolverSpec();
		viewLevelComboBox.removeActionListener(eventHandler);
		viewLevelComboBox.removeAllItems();
		viewLevelComboBox.addItem(new Integer(0));
		int numLevels = chomboSolverSpec.getNumRefinementLevels();
		for (int i = 0; i < numLevels; ++ i) 
		{
			viewLevelComboBox.addItem(i + 1);
		}
		if (chomboSolverSpec.isFinestViewLevel())
		{
			viewLevelFinestRadioButton.setSelected(chomboSolverSpec.isFinestViewLevel());
			viewLevelComboBox.setEnabled(false);
		}
		else
		{
			viewLevelUserSelectRadioButton.setSelected(!chomboSolverSpec.isFinestViewLevel());
			viewLevelComboBox.setEnabled(true);
		
			int viewLevel = chomboSolverSpec.getViewLevel();
			viewLevelComboBox.setSelectedItem(viewLevel);
		}
		viewLevelComboBox.addActionListener(eventHandler);
	}
	
	public final void setSimulation(Simulation newValue) {
		Simulation oldValue = this.simulation;
		/* Stop listening for events from the current object */
		if (oldValue != null) {
			oldValue.getSolverTaskDescription().removePropertyChangeListener(eventHandler);
			if (oldValue.getSolverTaskDescription().getChomboSolverSpec() != null)
			{
				oldValue.getMeshSpecification().removePropertyChangeListener(eventHandler);
				oldValue.getSolverTaskDescription().getChomboSolverSpec().removePropertyChangeListener(eventHandler);
				oldValue.getMeshSpecification().removePropertyChangeListener(eventHandler);
			}
		}
		this.simulation = newValue;
		if (newValue != null)
		{
			newValue.getSolverTaskDescription().addPropertyChangeListener(eventHandler);
			if (newValue.getSolverTaskDescription().getChomboSolverSpec() != null)
			{
				newValue.getMeshSpecification().removePropertyChangeListener(eventHandler);
				newValue.getSolverTaskDescription().getChomboSolverSpec().addPropertyChangeListener(eventHandler);
				newValue.getMeshSpecification().addPropertyChangeListener(eventHandler);
			}
		}
		updateDisplay();		
	}
	
	private void setMaxBoxSize() {
		try
		{
			simulation.getSolverTaskDescription().getChomboSolverSpec().setMaxBoxSize((Integer)maxBoxSizeComboBox.getSelectedItem());
		}
		catch (PropertyVetoException ex)
		{
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
	}
	
	private void setBlockFactor() {
		simulation.getSolverTaskDescription().getChomboSolverSpec().setBlockFactor((Integer)blockFactorComboBox.getSelectedItem());
	}
	
	private void setViewLevel() {
		simulation.getSolverTaskDescription().getChomboSolverSpec().setViewLevel((Integer)viewLevelComboBox.getSelectedItem());
	}
	
	private void setFillRatio() {
		double fillRatio = Double.parseDouble(fillRatioTextField.getText());
		simulation.getSolverTaskDescription().getChomboSolverSpec().setFillRatio(fillRatio);
	}

}
