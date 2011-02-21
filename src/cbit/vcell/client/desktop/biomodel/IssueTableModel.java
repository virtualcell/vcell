package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEvent;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEventListener;

@SuppressWarnings("serial")
public class IssueTableModel extends VCellSortTableModel<Issue> implements IssueEventListener {

	static final int COLUMN_DESCRIPTION = 0;
	static final int COLUMN_SOURCE = 1;
	static final int COLUMN_PATH = 2;
	private static final String[] labels = {"Description", "Source", "Defined In:"};
	private boolean bShowWarning = true;
	
	public IssueTableModel(ScrollTable table) {
		super(table, labels);
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Issue issue = getValueAt(rowIndex);
		switch (columnIndex) {
		case COLUMN_DESCRIPTION:
			return issue;
		case COLUMN_SOURCE:
			return issueManager == null ? null : issueManager.getObjectDescription(issue.getSource());
		case COLUMN_PATH:
			return issueManager == null ? null : issueManager.getObjectPathDescription(issue.getSource());
		}
		return null;
	}

	@Override
	protected Comparator<Issue> getComparator(final int col, final boolean ascending) {
		return new Comparator<Issue>() {
			public int compare(Issue o1, Issue o2) {
				int scale = ascending ? 1 : -1;
				switch (col) {
				case COLUMN_DESCRIPTION: {
					int s1 = o1.getSeverity();
					int s2 = o2.getSeverity();
					if (s1 == s2) {
						return scale * o1.getMessage().compareTo(o2.getMessage());
					} else {
						return scale * new Integer(s1).compareTo(new Integer(s2));
					}
				}
				case COLUMN_SOURCE:
					return issueManager == null ? 0 : scale * issueManager.getObjectDescription(o1).compareTo(issueManager.getObjectDescription(o2));
				case COLUMN_PATH:
					return issueManager == null ? 0 : scale * issueManager.getObjectPathDescription(o1).compareTo(issueManager.getObjectPathDescription(o2));
				}
				return 0;
			}		
		};
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case COLUMN_DESCRIPTION:
			return Issue.class;
		case COLUMN_SOURCE:
		case COLUMN_PATH:
			return String.class;
		}
		return Object.class;
	}

	void refreshData() {
		List<Issue> issueList = null;
		if (issueManager != null) {
			List<Issue> allIssueList = issueManager.getIssueList();
			issueList = new ArrayList<Issue>();
			for (Issue issue : allIssueList) {
				int severity = issue.getSeverity();
				if (severity == Issue.SEVERITY_ERROR) {
					issueList.add(issue);
				}
			}
			if (bShowWarning) {
				for (Issue issue : allIssueList) {
					int severity = issue.getSeverity();
					if (severity == Issue.SEVERITY_WARNING) {
						issueList.add(issue);
					}
				}
			}
		}
		setData(issueList);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	void issueManagerChange(IssueManager oldValue, IssueManager newValue) {
		if (oldValue != null) {
			oldValue.removeIssueEventListener(this);
		}
		if (newValue != null) {
			newValue.addIssueEventListener(this);		
		}
		refreshData();
	}

	public void issueChange(IssueEvent issueEvent) {
		refreshData();		
	}

	public final void setShowWarning(boolean bShowWarning) {
		if (this.bShowWarning == bShowWarning) {
			return;
		}
		this.bShowWarning = bShowWarning;
		issueManager.updateIssues();
		refreshData();
	}
}
