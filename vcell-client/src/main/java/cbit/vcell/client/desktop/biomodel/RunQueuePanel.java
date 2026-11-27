package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.SimulationRunQueue;
import cbit.vcell.client.SimulationRunQueue.Run;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;

/**
 * The model window's simulation runs, newest first: each simulation's latest run on the VCell server, and each run
 * started on this computer, with when it was dispatched, where it runs, and how it's going. Double-click (or Enter) on
 * a run opens its application's simulations, with it chosen.
 */
@SuppressWarnings("serial")
public class RunQueuePanel extends JPanel {
	private static final String TABLE = "table";
	private static final String EMPTY = "empty";

	private final Consumer<Run> open;
	private final RunsTableModel model = new RunsTableModel();
	private final JTable table = new JTable(this.model);
	private final JLabel title = GuiUtils.createBoldJLabel("Run Queue");
	private final JButton serverJobsButton = new JButton("Open Detailed View..."); // the account's server jobs, every model's
	private boolean serverJobsBound = false;
	private final CardLayout cards = new CardLayout();
	private final JPanel body = new JPanel(this.cards);
	private final Runnable onChange = this::refresh;
	private SimulationRunQueue queue = null;

	/** @param open opens a run's simulation, in its application */
	public RunQueuePanel(Consumer<Run> open) {
		super(new BorderLayout(0, 6));
		this.setName("RunQueuePanel");
		this.open = open;
		this.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
		this.title.setName("RunQueueTitle");
		this.serverJobsButton.setName("ViewSimulationJobsButton");
		this.serverJobsButton.setToolTipText("Your simulation jobs on the server, from every model (as in Account > View Simulation Jobs)");
		this.serverJobsButton.setEnabled(false); // until it's bound to the window's: while connected
		JPanel header = new JPanel(new BorderLayout(8, 0));
		header.add(this.title, BorderLayout.WEST);
		header.add(this.serverJobsButton, BorderLayout.EAST);
		this.add(header, BorderLayout.NORTH);
		this.configureTable();
		JLabel empty = new JLabel("<html><div style='text-align:center'>No runs yet.<br>Runs started from an application's Simulations,"
				+ " on the server or on this computer, are listed here, newest first.</div></html>", SwingConstants.CENTER) {
			@Override
			public void updateUI() { // the theme's secondary text color, again whenever the theme changes
				super.updateUI();
				this.setForeground(UIManager.getColor("Label.disabledForeground"));
			}
		};
		empty.setName("RunQueueEmpty");
		this.body.add(new JScrollPane(this.table), RunQueuePanel.TABLE);
		this.body.add(empty, RunQueuePanel.EMPTY);
		this.add(this.body, BorderLayout.CENTER);
		this.refresh();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		if (this.serverJobsBound) return;
		// the button does what Account > View Simulation Jobs does, in the window this is shown in
		DocumentWindow window = (DocumentWindow) SwingUtilities.getAncestorOfClass(DocumentWindow.class, this);
		if (window == null) return;
		window.bindToViewJobs(this.serverJobsButton);
		this.serverJobsBound = true;
	}

	/** The queue shown: the model window's. */
	public void setQueue(SimulationRunQueue queue) {
		if (this.queue == queue) return;
		if (this.queue != null) this.queue.removeChangeListener(this.onChange);
		this.queue = queue;
		if (queue != null) queue.addChangeListener(this.onChange);
		this.refresh();
	}

	JTable getTable() {
		return this.table;
	}

	void refresh() {
		Run chosen = this.chosen();
		List<Run> runs = this.queue == null ? new ArrayList<>() : this.queue.getRuns();
		this.model.setRuns(runs);
		if (chosen != null && runs.contains(chosen)) {
			int row = runs.indexOf(chosen);
			this.table.setRowSelectionInterval(row, row);
		}
		long active = runs.stream().filter(Run::isActive).count();
		this.title.setText("Run Queue" + (runs.isEmpty() ? "" : " (" + runs.size() + (active > 0 ? ", " + active + " active" : "") + ")"));
		this.cards.show(this.body, runs.isEmpty() ? RunQueuePanel.EMPTY : RunQueuePanel.TABLE);
	}

	private Run chosen() {
		int row = this.table.getSelectedRow();
		return row >= 0 && row < this.model.runs.size() ? this.model.runs.get(row) : null;
	}

	private void openChosen() {
		Run run = this.chosen();
		if (run != null) this.open.accept(run);
	}

	private void configureTable() {
		this.table.setName("RunQueueTable");
		this.table.getAccessibleContext().setAccessibleName("Simulation runs, newest first");
		VCellFonts.setCategory(this.table, VCellFonts.Category.BODY);
		VCellFonts.setCategory(this.table.getTableHeader(), VCellFonts.Category.BODY);
		VCellFonts.followRowHeight(this.table);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setFillsViewportHeight(true);
		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null); // Tab leaves the table,
		this.table.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null); // not just its cells
		int[] widths = { 110, 160, 130, 60, 200 };
		for (int i = 0; i < widths.length; i++) this.table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		this.table.getColumnModel().getColumn(RunsTableModel.STATUS).setCellRenderer(new StatusRenderer());
		this.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) RunQueuePanel.this.openChosen();
			}
		});
		this.table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openRun");
		this.table.getActionMap().put("openRun", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RunQueuePanel.this.openChosen();
			}
		});
	}

	/** When a run was dispatched, as it reads best: the time today, else the day and time. */
	static String describeDispatch(Date date) {
		if (date == null) return "";
		Calendar then = Calendar.getInstance();
		then.setTime(date);
		Calendar now = Calendar.getInstance();
		boolean today = then.get(Calendar.YEAR) == now.get(Calendar.YEAR) && then.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);
		DateFormat format = today ? DateFormat.getTimeInstance(DateFormat.SHORT) : DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
		return format.format(date);
	}

	private static final class RunsTableModel extends AbstractTableModel {
		static final int DISPATCHED = 0, SIMULATION = 1, APPLICATION = 2, WHERE = 3, STATUS = 4;
		private static final String[] COLUMNS = { "Dispatched", "Simulation", "Application", "Where", "Status" };
		private List<Run> runs = new ArrayList<>();

		void setRuns(List<Run> runs) {
			this.runs = runs;
			this.fireTableDataChanged();
		}

		@Override public int getRowCount() { return this.runs.size(); }
		@Override public int getColumnCount() { return RunsTableModel.COLUMNS.length; }
		@Override public String getColumnName(int column) { return RunsTableModel.COLUMNS[column]; }

		@Override
		public Object getValueAt(int row, int column) {
			Run run = this.runs.get(row);
			return switch (column) {
				case DISPATCHED -> RunQueuePanel.describeDispatch(run.getDispatched());
				case SIMULATION -> run.getSimulation().getName();
				case APPLICATION -> run.getOwnerName();
				case WHERE -> run.getWhere() == SimulationRunQueue.Where.SERVER ? "Server" : "Local";
				default -> run;
			};
		}
	}

	// a running run's progress as a bar with its status on it; otherwise its status, failures in the error color
	private static final class StatusRenderer implements TableCellRenderer {
		private final JProgressBar bar = new JProgressBar(0, 100);
		private final DefaultTableCellRenderer text = new DefaultTableCellRenderer();

		StatusRenderer() {
			this.bar.setStringPainted(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (!(value instanceof Run run)) return this.text.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (run.isActive() && run.getProgress() != null) {
				this.bar.setValue((int) Math.round(run.getProgress() * 100));
				this.bar.setString(run.getStatus());
				this.bar.setToolTipText(run.getStatus());
				return this.bar;
			}
			JLabel label = (JLabel) this.text.getTableCellRendererComponent(table, run.getStatus(), isSelected, hasFocus, row, column);
			label.setToolTipText(run.getStatus());
			if (run.isFailed() && !isSelected) label.setForeground(VCellColors.getErrorForeground());
			else if (!isSelected) label.setForeground(table.getForeground());
			return label;
		}
	}
}
