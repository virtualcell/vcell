package cbit.vcell.client.desktop.biomodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.Timer;
import java.util.Vector;

import org.vcell.util.Issue;
import org.vcell.util.document.VCDocument;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mathmodel.MathModel;

@SuppressWarnings("serial")
public class IssueManager {
	private ArrayList<Issue> issueList = new ArrayList<Issue>();
	private VCDocument vcDocument = null;
	private int numErrors, numWarnings;
	private long dirtyTimestamp = System.currentTimeMillis();
	private Timer timer = null;
	
	public IssueManager(){
		
		int delay = 1000; //  check each second ... wait 2 seconds after the last dirty
		
		timer = new Timer(delay,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					updateIssues0();
				}catch (Exception ex){
					ex.printStackTrace(System.out);
				}
			}
		});
		timer.start();
	}

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
	
	private void updateIssues0() {
		if (dirtyTimestamp==0){
			return;
		}
		long elapsedTime = System.currentTimeMillis() - dirtyTimestamp;
		if (elapsedTime<2000) {
			return;
		}
		try {
			numErrors = 0;
			numWarnings = 0;
			ArrayList<Issue> oldIssueList = new ArrayList<Issue>(issueList);
			if (vcDocument instanceof BioModel) {
				Vector<Issue> tempIssueList = new Vector<Issue>();
				((BioModel)vcDocument).gatherIssues(tempIssueList);
				issueList = new ArrayList<Issue>(tempIssueList);
			} else if (vcDocument instanceof MathModel) {
				ArrayList<Issue> tempIssueList = new ArrayList<Issue>();
				((MathModel)vcDocument).gatherIssues(tempIssueList);
				issueList = new ArrayList<Issue>(tempIssueList);
			}
			for (Issue issue: issueList) {
				int severity = issue.getSeverity();
				if (severity == Issue.SEVERITY_ERROR) {
					numErrors ++;
				} else if (severity == Issue.SEVERITY_WARNING) {
					numWarnings ++;
				}
			}
			fireIssueEventListener(new IssueEvent(vcDocument, oldIssueList, issueList));
			System.out.println("\n................... update performed .................." + System.currentTimeMillis());
		} finally {
			dirtyTimestamp = 0;
		}
	}
	public void updateIssues() {
		dirtyTimestamp = System.currentTimeMillis() - 3000; // force update
		updateIssues0();
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
	
	public String getObjectPathDescription(Object object) {
		return vcDocument.getObjectPathDescription(object);
	}
	public String getObjectDescription(Object object) {
		return vcDocument.getObjectDescription(object);
	}
	
	public void setDirty() {
		dirtyTimestamp = System.currentTimeMillis();
	}
}
