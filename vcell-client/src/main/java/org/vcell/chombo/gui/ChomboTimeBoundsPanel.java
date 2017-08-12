package org.vcell.chombo.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.vcell.chombo.TimeInterval;
import org.vcell.util.Issue;
import org.vcell.util.Issue.Severity;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.sorttable.SortPreference;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.vcell.solver.SolverTaskDescription;

@SuppressWarnings("serial")
public class ChomboTimeBoundsPanel extends CollapsiblePanel {

	private class EventHandler implements ActionListener, PropertyChangeListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if (e.getSource() == addButton)
			{
				addTimeBound();
			}
			else if (e.getSource() == deleteButton)
			{
				deleteTimeBound();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == solverTaskDescription)
			{
				timeIntervalTableModel.fireTableDataChanged();
			}
		}
	}
	
	private class TimeIntervalTableModel extends AbstractTableModel implements SortTableModel
	{
		private final String[] columns = new String[]{"Starting", "Ending", "Time Step", "Output Interval"};
		private final static int COLUMN_Starting = 0;
		private final static int COLUMN_Ending = 1;
		private final static int COLUMN_TimeStep = 2;
		private final static int COLUMN_OutputInterval = 3;
		
		@Override
		public int getRowCount() {
			return solverTaskDescription == null || solverTaskDescription.getChomboSolverSpec() == null ? 0 : solverTaskDescription.getChomboSolverSpec().getTimeIntervalList().size();
		}
		
		@Override
		public int getColumnCount() {
			return columns.length;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			List<TimeInterval> timeIntervalList = solverTaskDescription.getChomboSolverSpec().getTimeIntervalList();
			int numTimeIntervals = timeIntervalList.size();
			if (rowIndex >= numTimeIntervals)
			{
				return null;
			}
			else
			{
				TimeInterval ti = timeIntervalList.get(rowIndex);
				double val = -1;
				switch (columnIndex)
				{
				case COLUMN_Starting:
					val = ti.getStartingTime();
					break;
				case COLUMN_Ending:
					val = ti.getEndingTime();
					break;
				case COLUMN_TimeStep:
					val = ti.getTimeStep();
					break;
				case COLUMN_OutputInterval:
					val = ti.getOutputTimeStep();
					break;
				}
				return val < 0 ? null : val;
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Object.class;
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			try
			{
				List<TimeInterval> timeIntervalList = solverTaskDescription.getChomboSolverSpec().getTimeIntervalList();
				int numTimeIntervals = timeIntervalList.size();
				if (rowIndex < numTimeIntervals)
				{
					TimeInterval ti = timeIntervalList.get(rowIndex);
					double val = -1;
					if (aValue instanceof String && !((String) aValue).trim().isEmpty())
					{
						val = Double.valueOf((String)aValue);
					}
					switch (columnIndex)
					{
					case COLUMN_Ending:
						ti.setEndingTime(val);
						break;
					case COLUMN_TimeStep:
						ti.setTimeStep(val);
						break;
					case COLUMN_OutputInterval:
						ti.setOutputTimeStep(val);
						break;
					}
					fireTableDataChanged();
				}
			}
			catch (Exception ex)
			{
				DialogUtils.showErrorDialog(timeIntervalTable, ex.getMessage(), ex);
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == COLUMN_Starting)
			{
				return false;
			}
			List<TimeInterval> timeIntervalList = solverTaskDescription.getChomboSolverSpec().getTimeIntervalList();
			if (timeIntervalList.size() == 1 || rowIndex == timeIntervalList.size() - 1)
			{
				// if there is only one time interval, or this is the last time interval
				return true;
			}
			if (columnIndex == COLUMN_TimeStep || columnIndex == COLUMN_OutputInterval)
			{
				return true;
			}
			if (columnIndex == COLUMN_Ending && rowIndex >= 0)
			{
				TimeInterval ti = timeIntervalList.get(rowIndex);
				return ti.getEndingTime() < 0;
			}
			return false;
		}

		@Override
		public void setSortPreference(SortPreference sortPreference) {
		}

		@Override
		public SortPreference getSortPreference() {
			return new SortPreference(true, -1);
		}

		@Override
		public boolean isSortable(int col) {
			return false;
		}

		@Override
		public List<Issue> getIssues(int row, int col, Severity severity) {
			return new ArrayList<Issue>();
		}
	}

	private EventHandler eventHandler = new EventHandler();
	private SolverTaskDescription solverTaskDescription;	
	private ScrollTable timeIntervalTable = new ScrollTable();
	private TimeIntervalTableModel timeIntervalTableModel = new TimeIntervalTableModel(); 
	private JButton addButton;
	private JButton deleteButton;

	public ChomboTimeBoundsPanel() {
		super("Time Bounds");
		addButton = new JButton(VCellIcons.addIcon);
		addButton.setToolTipText("Add a Time Bound");
		deleteButton = new JButton(VCellIcons.deleteIcon);
		deleteButton.setToolTipText("Delete a Time Bound");
		addButton.addActionListener(eventHandler);
		deleteButton.addActionListener(eventHandler);
		
		getContentPanel().setLayout(new BorderLayout());
		
		JToolBar toolbar = new JToolBar();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
		toolbar.setFloatable(false);
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(addButton);
		toolbar.add(deleteButton);
		toolbar.add(Box.createHorizontalStrut(10));
		
		timeIntervalTable = new EditorScrollTable();
		timeIntervalTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		timeIntervalTableModel = new TimeIntervalTableModel();
		timeIntervalTable.setModel(timeIntervalTableModel);
		final TableCellRenderer defaultRenderer = timeIntervalTable.getDefaultRenderer(Object.class);
		timeIntervalTable.setDefaultRenderer(Object.class, new TableCellRenderer()
		{

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (c instanceof JLabel && column == TimeIntervalTableModel.COLUMN_Starting && !isSelected)
				{
					// not editable
					c.setForeground(DefaultScrollTableCellRenderer.uneditableForeground);
				}
				return c;
			}
		});
		
		getContentPanel().add(toolbar, BorderLayout.NORTH);
		getContentPanel().add(timeIntervalTable.getEnclosingScrollPane(), BorderLayout.CENTER);
	}

	public void deleteTimeBound() {
		solverTaskDescription.getChomboSolverSpec().removeTimeInterval();
		timeIntervalTableModel.fireTableDataChanged();
		refreshButtons();
	}

	public void addTimeBound() {
		solverTaskDescription.getChomboSolverSpec().addNewTimeInterval();
		timeIntervalTableModel.fireTableDataChanged();
		refreshButtons();
	}
	
	private void refreshButtons()
	{
		deleteButton.setEnabled(solverTaskDescription.getChomboSolverSpec().getTimeIntervalList().size() > 0);
	}

	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
		SolverTaskDescription oldValue = this.solverTaskDescription;
		if (oldValue == newValue)
		{
			return;
		}
		if (oldValue != null)
		{
			oldValue.removePropertyChangeListener(eventHandler);
			if (oldValue.getChomboSolverSpec() != null)
			{
				oldValue.getChomboSolverSpec().removePropertyChangeListener(eventHandler);
			}
		}
		this.solverTaskDescription = newValue;

		if (solverTaskDescription != null)
		{
			solverTaskDescription.addPropertyChangeListener(eventHandler);
			if (solverTaskDescription.getChomboSolverSpec() != null)
			{
				solverTaskDescription.addPropertyChangeListener(eventHandler);
			}
		}
		timeIntervalTableModel.fireTableDataChanged();
	}

}
