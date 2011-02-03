package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JTable;

import org.vcell.util.Issue;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;

import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEvent;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEventListener;

@SuppressWarnings("serial")
public class IssueTableModel extends DefaultSortTableModel<Issue> implements IssueEventListener {

	static final int COLUMN_DESCRIPTION = 0;
	static final int COLUMN_SOURCE = 1;
	static final int COLUMN_CONTEXT = 2;
	static final int COLUMN_CATEGORY = 3;
	private static final String[] labels = {"Description", "Source", "Context", "Category"};
	private IssueManager issueManager = null;
	private JTable ownerTable = null;
	private boolean bShowWarning = false;
	
	public IssueTableModel(JTable table) {
		super(labels);
		ownerTable = table;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Issue issue = getValueAt(rowIndex);
		switch (columnIndex) {
		case COLUMN_DESCRIPTION:
			return issue;
		case COLUMN_SOURCE:
			return issue.getSourceDescription();
		case COLUMN_CONTEXT:
			return issue.getSourceContextDescription();
		case COLUMN_CATEGORY:
			return issue.getCategory().name();
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
				case COLUMN_CATEGORY:
					return scale * o1.getCategory().compareTo(o2.getCategory());
				case COLUMN_SOURCE:
					return scale * o1.getSourceDescription().compareTo(o2.getSourceDescription());
				case COLUMN_CONTEXT:
					return scale * o1.getSourceContextDescription().compareTo(o2.getSourceContextDescription());
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
		case COLUMN_CONTEXT:
			return String.class;
		case COLUMN_CATEGORY:
			return String.class;
		}
		return Object.class;
	}

	void refreshData() {
		ArrayList<Issue> issueList = null;
		if (issueManager != null) {
			ArrayList<Issue> allIssueList = issueManager.getIssueList();
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

	public void setIssueManager(IssueManager newValue) {
		IssueManager oldValue = this.issueManager;
		if (oldValue != null) {
			oldValue.removeIssueEventListener(this);
		}
		this.issueManager = newValue;
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
