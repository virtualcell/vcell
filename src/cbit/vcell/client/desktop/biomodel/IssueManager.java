package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import org.vcell.util.Issue;
import org.vcell.util.document.VCDocument;

import cbit.vcell.biomodel.BioModel;

@SuppressWarnings("serial")
public class IssueManager {
	private ArrayList<Issue> issueList = new ArrayList<Issue>();
	private VCDocument vcDocument = null;
	private int numErrors, numWarnings;

	public interface IssueEventListener {
		void issueChange(IssueEvent issueEvent);
	}
	public static class IssueEvent extends EventObject {
		private ArrayList<Issue> oldValue;
		private ArrayList<Issue> newValue;
		public IssueEvent(Object source, ArrayList<Issue> oldValue, ArrayList<Issue> newValue) {
			super(source);
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		public final ArrayList<Issue> getOldValue() {
			return oldValue;
		}
		public final ArrayList<Issue> getNewValue() {
			return newValue;
		}
		
	}
	public void addIssueEventListener(IssueEventListener listener){
		if (issueEventListeners.contains(listener)){
			return;
		}
		this.issueEventListeners.add(listener);
	}
	public void removeIssueEventListener(IssueEventListener listener){
		issueEventListeners.remove(listener);
	}
	void fireIssueEventListener(IssueEvent issueEvent){
		for (IssueEventListener listener : issueEventListeners){
			listener.issueChange(issueEvent);
		}
	}
	
	private List<IssueEventListener> issueEventListeners = new ArrayList<IssueEventListener>();
	
	public void updateIssues() {
		if (vcDocument instanceof BioModel) {
			numErrors = 0;
			numWarnings = 0;
			ArrayList<Issue> oldIssueList = new ArrayList<Issue>(issueList);
			Vector<Issue> tempIssueList = new Vector<Issue>();
			((BioModel)vcDocument).gatherIssues(tempIssueList);
			issueList = new ArrayList<Issue>(tempIssueList);
			for (Issue issue: issueList) {
				int severity = issue.getSeverity();
				if (severity == Issue.SEVERITY_ERROR) {
					numErrors ++;
				} else if (severity == Issue.SEVERITY_WARNING) {
					numWarnings ++;
				}
			}
			fireIssueEventListener(new IssueEvent(vcDocument, oldIssueList, issueList));
		}
	}
	public void setVCDocument(VCDocument newValue) {
		if (newValue == vcDocument) {
			return;
		}
		vcDocument = newValue;
		updateIssues();
	}
	public final ArrayList<Issue> getIssueList() {
		return issueList;
	}
	public final int getNumErrors() {
		return numErrors;
	}
	public final int getNumWarnings() {
		return numWarnings;
	}
}
