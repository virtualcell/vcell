package org.vcell.chombo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import org.vcell.chombo.ChomboSolverSpec;
import org.vcell.chombo.RefinementLevel;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.VCellIcons;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverTaskDescription;

import com.lowagie.text.Font;

@SuppressWarnings("serial")
public class ChomboSolverSpecPanel extends CollapsiblePanel {

	private class IvjEventHandler implements ActionListener, FocusListener, PropertyChangeListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getAddLevelButton()) {
				addLevelButton_actionPerformed();
			} else if (e.getSource() == getDeleteLevelButton()) {
				deleteLevelButton_actionPerformed();
			} 
			else if (e.getSource() == maxBoxSizeComboBox)
			{
				setMaxBoxSize();
			}
			else if (e.getSource() == viewLevelComboBox)
			{
				setViewLevel();
			}
		};
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == simulation.getSolverTaskDescription() && evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION)) {
				updateDisplay();
			}
			else if (evt.getSource() == simulation.getSolverTaskDescription().getChomboSolverSpec())
			{
				if (evt.getPropertyName().equals(ChomboSolverSpec.PROPERTY_NAME_MAX_BOX_SIZE))
				{
					maxBoxSizeComboBox.setSelectedItem(simulation.getSolverTaskDescription().getChomboSolverSpec().getMaxBoxSize());
				}
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
	
	private class LevelTableModel extends AbstractTableModel
	{
		private final String[] columns = new String[]{"Level", "Refinement Ratio", "ROI", "Tags Grow"};
		private final int COLUMN_Level = 0;
		private final int COLUMN_RefinementRatio = 1;
		private final int COLUMN_ROI = 2;
		private final int COLUMN_TagsGrow = 3;
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
				return Math.max(simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels(), 1);
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
				int numRefinementLevels = simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels();
				if (columnIndex == COLUMN_Level)
				{
					return numRefinementLevels == 0 ? "" : rowIndex + 1;
				}
				if (numRefinementLevels == 0 || rowIndex >= numRefinementLevels)
				{
					return null;
				}
				RefinementLevel level = simulation.getSolverTaskDescription().getChomboSolverSpec().getRefinementLevel(rowIndex);
				switch (columnIndex)
				{
				case COLUMN_RefinementRatio:
					return level.getRefineRatio();
				case COLUMN_ROI:
					if (level.getRoiExpression() == null)
					{
						return null;
					}
					return new ScopedExpression(level.getRoiExpression(), ReservedVariable.X.getNameScope(), true, true, autoCompleteSymbolFilter);
				case COLUMN_TagsGrow:
					return level.isTagsGrowEnabled();
				}
			}
			return null;
		}

		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex)
			{
			case COLUMN_Level:
			case COLUMN_RefinementRatio:
				return Integer.class;
			case COLUMN_ROI:
				return ScopedExpression.class;
			case COLUMN_TagsGrow:
				return Boolean.class;
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			try
			{
				RefinementLevel level = simulation.getSolverTaskDescription().getChomboSolverSpec().getRefinementLevel(rowIndex);
				switch (columnIndex)
				{
				case COLUMN_RefinementRatio:
					level.setRefineRatio((Integer)aValue);
					break;
				case COLUMN_ROI:
					if (aValue instanceof String) {
						final String str = (String)aValue;
						level.setRoiExpression(str);
					}
					break;
				case COLUMN_TagsGrow:
					level.enableTagsGrow((Boolean)aValue);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace(System.out);
				DialogUtils.showErrorDialog(levelTable, ex.getMessage(), ex);
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return isSimulationChombo() && columnIndex != COLUMN_Level 
					&& simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels() > 0;
		}
		
		/**
		 * is current simulation chombo solver?
		 * @return true if is
		 */
		private boolean isSimulationChombo() {
			return simulation.getSolverTaskDescription().getChomboSolverSpec() != null;
		}
	}
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton addLevelButton;
	private JButton deleteLevelButton;
	private Simulation simulation = null;
	private JComboBox maxBoxSizeComboBox = null;
	private JTextField fillRatioTextField;
	private JPanel finestInfoPanel;
	private JTextField finestSizeTextField;
	private JTextField finestDxTextField;
	private CollapsiblePanel refinementPanel;
	private JComboBox viewLevelComboBox = null;
	private ScrollTable levelTable;
	private LevelTableModel levelTableModel;
	private JComboBox refinementRatioComboBox;

	public ChomboSolverSpecPanel() {
		super("EBChombo Options");
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
		  
		  finestDxTextField = new JTextField(40);
		  finestDxTextField.setEditable(false);
		  finestDxTextField.setForeground(Color.blue);
		  finestDxTextField.setBorder(null);
		  
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
		  gbc.insets = new Insets(1, 5, 1, 2);
		  label = new JLabel("Finest Spatial Step");
		  label.setFont(label.getFont().deriveFont(Font.BOLD));
			finestInfoPanel.add(label, gbc);
		  
			++ gridx;
			gbc = new GridBagConstraints();
			gbc.gridx = gridx;
			gbc.gridy = 0;
			gbc.insets = new Insets(1, 5, 1, 0);
			label = new JLabel("(");
			finestInfoPanel.add(label, gbc);
			
			++ gridx;
		  gbc = new GridBagConstraints();
		  gbc.gridx = gridx;
		  gbc.gridy = 0;
		  gbc.weightx = 0.5;
		  gbc.insets = new Insets(0, 2, 1, 0);
		  gbc.anchor = GridBagConstraints.LINE_END;
		  gbc.fill = GridBagConstraints.HORIZONTAL;
		  finestDxTextField.setHorizontalAlignment(SwingConstants.CENTER);
		  finestInfoPanel.add(finestDxTextField, gbc);
		  
			++ gridx;
			gbc = new GridBagConstraints();
			gbc.gridx = gridx;
			gbc.gridy = 0;
			gbc.insets = new Insets(1, 0, 1, 2);
			label = new JLabel(")");
			finestInfoPanel.add(label, gbc);
		}
		return finestInfoPanel;
	}
	
	private void initialize() 
	{
		setMaximumSize(new Dimension(500, 150));
		setPreferredSize(new Dimension(500, 200));
		
		maxBoxSizeComboBox = new JComboBox();
		int start = 8;
		for (int i = 0; i < 10; ++ i)
		{
			maxBoxSizeComboBox.addItem(new Integer(start));
			start *= 2;
		}
		viewLevelComboBox = new JComboBox();
		viewLevelComboBox.setRenderer(new DefaultListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if ((Integer)value <= simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels())
				{
					int dim = simulation.getMathDescription().getGeometry().getDimension();
					ISize xyz = simulation.getSolverTaskDescription().getChomboSolverSpec().getLevelSamplingSize(simulation.getMeshSpecification().getSamplingSize(), (Integer)value);
					setText(GuiUtils.getMeshSizeText(dim, xyz, false));
				}
				return this;
			}
		});
		
		fillRatioTextField = new JTextField(10);
		fillRatioTextField.addFocusListener(ivjEventHandler);
		
		JPanel northPanel = new JPanel(new GridBagLayout());
		northPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		northPanel.add(new JLabel("Max Box Size"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		northPanel.add(maxBoxSizeComboBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);		
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		northPanel.add(new JLabel("Fill Ratio"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		northPanel.add(fillRatioTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);		
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		northPanel.add(new JLabel("View Level"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.gridx = 5;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		northPanel.add(viewLevelComboBox, gbc);
		
		getContentPanel().setLayout(new BorderLayout(0, 2));
		getContentPanel().add(northPanel, BorderLayout.NORTH);
		getContentPanel().add(getRefinementPanel(), BorderLayout.CENTER);
		
		maxBoxSizeComboBox.addActionListener(ivjEventHandler);
		getAddLevelButton().addActionListener(ivjEventHandler);
		getDeleteLevelButton().addActionListener(ivjEventHandler);
	}

	private CollapsiblePanel getRefinementPanel() {
		if (refinementPanel == null)
		{
			levelTable = new ScrollTable();
			levelTableModel = new LevelTableModel();
			levelTable.setModel(levelTableModel);
			refinementRatioComboBox = new JComboBox();
			refinementRatioComboBox.addItem(new Integer(2));
			refinementRatioComboBox.addItem(new Integer(4));
			refinementRatioComboBox.addItem(new Integer(8));
			refinementRatioComboBox.addItem(new Integer(16));
			DefaultCellEditor cellEditor = new DefaultCellEditor(refinementRatioComboBox);
			cellEditor.setClickCountToStart(2);
			levelTable.getColumnModel().getColumn(levelTableModel.COLUMN_RefinementRatio).setCellEditor(cellEditor);

			JToolBar toolbar = new JToolBar();
			toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
			toolbar.setFloatable(false);
			JLabel label = new JLabel("Levels");
			toolbar.add(label);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			toolbar.add(Box.createHorizontalStrut(10));
			toolbar.add(getAddLevelButton());
			toolbar.add(getDeleteLevelButton());
			toolbar.add(Box.createHorizontalGlue());
			label = new JLabel("(Leave all ROIs emtpy to refine all membrane elements.)");
			label.setFont(label.getFont().deriveFont(Font.BOLDITALIC, label.getFont().getSize() - 1));
			toolbar.add(label);
			toolbar.add(Box.createHorizontalStrut(10));
					
			refinementPanel = new CollapsiblePanel("Mesh Refinement");
			refinementPanel.getContentPanel().setLayout(new GridBagLayout());		
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(0, 5, 0, 0);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			refinementPanel.getContentPanel().add(toolbar, gbc);
			
			JScrollPane scroll = new JScrollPane(levelTable);
			scroll.setBorder(null);
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.PAGE_START;
			refinementPanel.getContentPanel().add(scroll, gbc);
		  
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new java.awt.Insets(5, 0, 0, 0);
			refinementPanel.getContentPanel().add(getFinestInfoPanel(), gbc);
		}
		return refinementPanel;
	}
	
	private JButton getAddLevelButton() {
		if (addLevelButton == null) {
			try {
				addLevelButton = new JButton(VCellIcons.refLevelNewIcon);
				addLevelButton.setToolTipText("Add a Refinement Level");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}	
		}
		return addLevelButton;
	}

	private JButton getDeleteLevelButton() {
		if (deleteLevelButton == null) {
			try {
				deleteLevelButton = new JButton(VCellIcons.refLevelDeleteIcon);
				deleteLevelButton.setToolTipText("Delete a Refinement Level");
				deleteLevelButton.setEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return deleteLevelButton;
	}
	
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION --------- in SolverTaskDescriptionPanel");
		exception.printStackTrace(System.out);
	}	

	private void addLevelButton_actionPerformed() {	
		simulation.getSolverTaskDescription().getChomboSolverSpec().addRefinementLevel(new RefinementLevel());
		int levelIndex = simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels();
		viewLevelComboBox.addItem(levelIndex);
		viewLevelComboBox.setSelectedItem(simulation.getSolverTaskDescription().getChomboSolverSpec().getViewLevel());

		updateFinestInfoPanel();
	}

	private void updateFinestInfoPanel()
	{
		boolean bHasRefinement = simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels() > 0;
		finestInfoPanel.setVisible(bHasRefinement);
		getDeleteLevelButton().setEnabled(bHasRefinement);
		
		levelTableModel.fireTableDataChanged();

		if (bHasRefinement)
		{
			Extent extent = simulation.getMathDescription().getGeometry().getExtent();
			ISize finestSize = simulation.getSolverTaskDescription().getChomboSolverSpec().getFinestSamplingSize(simulation.getMeshSpecification().getSamplingSize());
			String sizeDisplay = finestSize.getX() + " x " + finestSize.getY();
			String dxDisplay = (extent.getX() / finestSize.getX()) +  ", " + (extent.getY() / finestSize.getY());
			if (simulation.getMathDescription().getGeometry().getDimension() > 2)
			{
				sizeDisplay += " x " + finestSize.getZ();
				dxDisplay += ", " + (extent.getZ() / finestSize.getZ());
			}
			
			finestSizeTextField.setText(sizeDisplay);
			finestSizeTextField.setCaretPosition(0);
			finestSizeTextField.setToolTipText(sizeDisplay);
			finestDxTextField.setText(dxDisplay);
			finestDxTextField.setCaretPosition(0);
			finestDxTextField.setToolTipText(dxDisplay);
		}
	}
	
	private void deleteLevelButton_actionPerformed() {
		int lastIndex = simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels();

		viewLevelComboBox.removeItem(lastIndex);
		simulation.getSolverTaskDescription().getChomboSolverSpec().deleteRefinementLevel();
		
		viewLevelComboBox.setSelectedItem(simulation.getSolverTaskDescription().getChomboSolverSpec().getViewLevel());

		updateFinestInfoPanel();
		updateUI();
	}

	private void updateDisplay() {
		if (!simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver()) 
		{
			setVisible(false);
			return;
		}
		setVisible(true);
		maxBoxSizeComboBox.setSelectedItem(simulation.getSolverTaskDescription().getChomboSolverSpec().getMaxBoxSize());
		fillRatioTextField.setText(simulation.getSolverTaskDescription().getChomboSolverSpec().getFillRatio() + "");
		
		viewLevelComboBox.addItem(new Integer(0));
		if (simulation.getSolverTaskDescription().getChomboSolverSpec() != null) {
			int numLevels = simulation.getSolverTaskDescription().getChomboSolverSpec().getNumRefinementLevels();
			for (int i = 0; i < numLevels; ++ i) 
			{
				viewLevelComboBox.addItem(i + 1);
			}
		}
		viewLevelComboBox.setSelectedItem(simulation.getSolverTaskDescription().getChomboSolverSpec().getViewLevel());
		viewLevelComboBox.addActionListener(ivjEventHandler);
		updateFinestInfoPanel();
	}

	public final void setSimulation(Simulation newValue) {
		Simulation oldValue = this.simulation;
		/* Stop listening for events from the current object */
		if (oldValue != null) {
			oldValue.getSolverTaskDescription().removePropertyChangeListener(ivjEventHandler);
			if (oldValue.getSolverTaskDescription().getChomboSolverSpec() != null)
			{
				oldValue.getSolverTaskDescription().getChomboSolverSpec().removePropertyChangeListener(ivjEventHandler);
				oldValue.getMeshSpecification().removePropertyChangeListener(ivjEventHandler);
			}
		}
		this.simulation = newValue;
		if (newValue != null)
		{
			newValue.getSolverTaskDescription().addPropertyChangeListener(ivjEventHandler);
			if (newValue.getSolverTaskDescription().getChomboSolverSpec() != null)
			{
				newValue.getSolverTaskDescription().getChomboSolverSpec().addPropertyChangeListener(ivjEventHandler);
				newValue.getMeshSpecification().addPropertyChangeListener(ivjEventHandler);
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
	
	private void setViewLevel() {
		simulation.getSolverTaskDescription().getChomboSolverSpec().setViewLevel((Integer)viewLevelComboBox.getSelectedItem());
	}
	
	private void setFillRatio() {
		double fillRatio = Double.parseDouble(fillRatioTextField.getText());
		simulation.getSolverTaskDescription().getChomboSolverSpec().setFillRatio(fillRatio);
	}

}
