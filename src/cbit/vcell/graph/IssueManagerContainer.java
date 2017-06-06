package cbit.vcell.graph;

import java.util.List;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;

import cbit.vcell.client.desktop.biomodel.IssueManager;

public class IssueManagerContainer {

	protected IssueManager issueManager;	// used to colorize components with issues
	
	public IssueManagerContainer(IssueManager issueManager) {
		this.issueManager = issueManager;
	}
	
	public boolean hasErrorIssues(Displayable owner, MolecularType mt) {
		if(issueManager == null) {
			return false;
		}
		if(owner == null) {
			return false;
		}
		List<Issue> allIssueList = issueManager.getIssueList();
		for (Issue issue: allIssueList) {
			if(issue.getSeverity() != Issue.Severity.ERROR) {
				continue;
			}
			Object source = issue.getSource();
			Object detailedSource = issue.getDetailedSource();
			if(mt != null & source == owner && detailedSource == mt) {
				return true;
			}
		}
		return false;
	}
	public boolean hasErrorIssues(Displayable owner, MolecularComponentPattern mcp, MolecularComponent mc) {
		if(issueManager == null) {
			return false;
		}
		if(owner == null) {
			return false;
		}
		
		List<Issue> allIssueList = issueManager.getIssueList();
		for (Issue issue: allIssueList) {
			if(issue.getSeverity() != Issue.Severity.ERROR) {
				continue;
			}
			Object source = issue.getSource();
			Object detailedSource = issue.getDetailedSource();
			if(mcp != null && source == owner && detailedSource == mcp) {
				return true;
			} else if(mc != null & source == owner && detailedSource == mc) {
				return true;
			}
		}
		return false;
	}
	public boolean hasErrorIssues(Displayable owner, ComponentStatePattern csp, ComponentStateDefinition csd) {
		if(issueManager == null) {
			return false;
		}
		if(owner == null) {
			return false;
		}
		
		List<Issue> allIssueList = issueManager.getIssueList();
		for (Issue issue: allIssueList) {
			if(issue.getSeverity() != Issue.Severity.ERROR) {
				continue;
			}
			Object source = issue.getSource();
			Object detailedSource = issue.getDetailedSource();
			if(csp != null && source == owner && detailedSource == csp) {
				return true;
			} else if(csd != null & source == owner && detailedSource == csd) {
				return true;
			}
		}
		return false;
	}
	public boolean hasErrorIssues(Displayable owner, MolecularComponent mc) {
		if(issueManager == null) {
			return false;
		}
		if(owner == null) {
			return false;
		}
		
		List<Issue> allIssueList = issueManager.getIssueList();
		for (Issue issue: allIssueList) {
			if(issue.getSeverity() != Issue.Severity.ERROR) {
				continue;
			}
			Object source = issue.getSource();
			Object detailedSource = issue.getDetailedSource();
			if(mc != null && source == owner && mc.getComponentStateDefinitions().size() > 0) {
				for(ComponentStateDefinition csd : mc.getComponentStateDefinitions()) {
					if(detailedSource == csd) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
