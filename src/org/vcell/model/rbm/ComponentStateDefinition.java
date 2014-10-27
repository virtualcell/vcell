package org.vcell.model.rbm;

import java.beans.PropertyVetoException;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.document.PropertyConstants;

public class ComponentStateDefinition extends RbmElementAbstract implements Matchable {
	private String name;   // e.g. Phosphorated, ...
	
	public ComponentStateDefinition(String name) {
		this.name = name;
	}
	
	public final String getName() {
		return name;
	}
	
	public void setName(String newValue) throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
		name = newValue;
		firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof ComponentStateDefinition)) {
			return false;
		}
		ComponentStateDefinition that = (ComponentStateDefinition)aThat;
		
		if (!Compare.isEqual(name, that.name)) {
			return false;
		}
		return true;
	}

	@Override
	public void gatherIssues(IssueContext issueContet, List<Issue> issueList) {
		if(name == null) {
			issueList.add(new Issue(this, issueContet, IssueCategory.Identifiers, "Name of Molecular Component State is null", Issue.SEVERITY_ERROR));
		} else if(name.equals("")) {
			issueList.add(new Issue(this, issueContet, IssueCategory.Identifiers, "Name of Molecular Component State is empty", Issue.SEVERITY_WARNING));
//		} else {
//			issueList.add(new Issue(this, issueContet, IssueCategory.Identifiers, name, Issue.SEVERITY_WARNING));
		}
	}
}
