package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.sorttable.DefaultSortTableModel;

import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEvent;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEventListener;
import cbit.vcell.math.OutputFunctionContext.OutputFunctionIssueSource;

@SuppressWarnings("serial")
public abstract class VCellSortTableModel<T> extends DefaultSortTableModel<T> implements IssueEventListener {
	protected IssueManager issueManager;
	protected List<Issue> issueList = new ArrayList<Issue>();
	protected ScrollTable ownerTable = null; 

	public VCellSortTableModel(ScrollTable table) {
		this(table, null);
	}
	
	public VCellSortTableModel(ScrollTable table, String[] columnNames) {
		super(columnNames);
		ownerTable = table;
	}
	

	public void issueChange(IssueEvent issueEvent) {		
		//fireTableDataChanged();
		ownerTable.repaint();
	}

	public List<Issue> getIssues(int row, int col) {
		issueList.clear();
		if (row < getDataSize() && issueManager != null) {
			List<Issue> allIssueList = issueManager.getIssueList();
			for (Issue issue: allIssueList) {
				Object source = issue.getSource();
				Object rowAt = getValueAt(row);
				if (issue.getSeverity() == Issue.SEVERITY_ERROR) {
					if (rowAt == source || 
							source instanceof OutputFunctionIssueSource && ((OutputFunctionIssueSource)source).getAnnotatedFunction() == rowAt) {
						issueList.add(issue);
					}
				}
			}
		}
		return issueList;
	}
	
	public final void setIssueManager(IssueManager newValue) {
		if (newValue == issueManager) {
			return;
		}
		IssueManager oldValue = this.issueManager;
		if (oldValue != null) {
			oldValue.removeIssueEventListener(this);
		}
		this.issueManager = newValue;
		if (newValue != null) {
			newValue.addIssueEventListener(this);		
		}
		issueManagerChange(oldValue, newValue);
	}

	void issueManagerChange(IssueManager oldValue, IssueManager newValue) {}
}
