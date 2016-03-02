package org.vcell.model.rbm;

import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Map;

import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Pair;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.document.PropertyConstants;

public class ComponentStateDefinition extends RbmElementAbstract implements Matchable, IssueSource,
	Displayable
{
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
	
	public String dependenciesToHtml(Map<String, Pair<Displayable, SpeciesPattern>> usedHere) {
		String errMsg = "State '<b>" + getDisplayName() + "'</b> is already being used by:<br>";
		final int MaxListSize = 7;
		int count = 0;
		for(String key : usedHere.keySet()) {
			System.out.println(key);
			if(count >= MaxListSize) {
				errMsg += "<br> ... and more.";
				break;
			}
			Pair<Displayable, SpeciesPattern> o = usedHere.get(key);
			Displayable e = o.one;
			SpeciesPattern sp = o.two;
			errMsg += "<br> - " + e.getDisplayType().toLowerCase() + " <b>" + e.getDisplayName() + "</b>";
			errMsg += ", " + sp.getDisplayType().toLowerCase() + " " + " <b>" + sp.getDisplayName() + "</b>";
			count++;
		}
		return errMsg;
	}


	@Override
	public void gatherIssues(IssueContext issueContet, List<Issue> issueList) {
		if(name == null) {
			issueList.add(new Issue(this, issueContet, IssueCategory.Identifiers, "Name of " + typeName + " is null", Issue.SEVERITY_ERROR));
		} else if(name.equals("")) {
			issueList.add(new Issue(this, issueContet, IssueCategory.Identifiers, "Name of " + typeName + " is empty", Issue.SEVERITY_WARNING));
//		} else {
//			issueList.add(new Issue(this, issueContet, IssueCategory.Identifiers, name, Issue.SEVERITY_WARNING));
		}
	}
	
	public static final String typeName = "State";
	@Override
	public final String getDisplayName() {
		return getName();
	}
	@Override
	public final String getDisplayType() {
		return typeName;
	}
}
