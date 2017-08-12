package org.vcell.model.rbm;

import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

public class ComponentStatePattern extends RbmElementAbstract implements Matchable, Displayable {
	
	private final ComponentStateDefinition componentStateDefinition;
	private final boolean bAny;
	
	public static final String strAny = "not specified";
	
	/* Example:

	S(tyr~Y)	// Y is the component state definition, the pattern points to it
	S(tyr)		// the pattern is in any state (no specified state chosen)

	 */
	
	public ComponentStatePattern() {
		this.bAny = true;
		this.componentStateDefinition = null;
	}
	public ComponentStatePattern(ComponentStateDefinition componentStateDefinition) {
		this.bAny = false;
		this.componentStateDefinition = componentStateDefinition;
	}
	
	public ComponentStatePattern(MolecularComponent thisMc, ComponentStatePattern that) {
		// copy constructor
		if(that == null || that.componentStateDefinition == null) {
			this.bAny = true;
			this.componentStateDefinition = null;
		} else {
			this.bAny = that.bAny;
			ComponentStateDefinition thatCsd = that.componentStateDefinition;
			ComponentStateDefinition thisCsd = thisMc.getComponentStateDefinition(thatCsd.getName());
			this.componentStateDefinition = thisCsd;
		}
	}

	public boolean isAny(){
		return bAny;
	}
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof ComponentStatePattern)) {
			return false;
		}
		ComponentStatePattern that = (ComponentStatePattern)aThat;

		if(!(bAny == that.bAny)) {
			return false;
		}
		if (!Compare.isEqual(componentStateDefinition, that.componentStateDefinition)){
			return false;
		}
		return true;
	}

	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {

	}

	public ComponentStateDefinition getComponentStateDefinition() {
		return componentStateDefinition;
	}
	
	public static final String typeName = "State";
	@Override
	public String getDisplayName() {
			return "";
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}
}

