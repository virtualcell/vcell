package cbit.vcell.math;

import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;


public class ParticleComponentStateDefinition implements Serializable, Matchable {		// extends RbmElementAbstract ???

	private String name;   // e.g. Phosphorated, ...
		
	public ParticleComponentStateDefinition(String name) {
		this.name = name;
	}
	
	public final String getName() {
		return name;
	}
		public String getVCML() {
		return name;
	}
		
	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof ParticleComponentStateDefinition){
			ParticleComponentStateDefinition other = (ParticleComponentStateDefinition)obj;
			if (!Compare.isEqual(name, other.name)){
				return false;
			}
			return true;
		}
		return false;
	}
//	@Override
//	public void gatherIssues(List<Issue> issueList) {
//		if(name == null) {
//			issueList.add(new Issue(this, IssueCategory.Identifiers, "Name of State is null", Issue.SEVERITY_ERROR));
//		} else if(name.equals("")) {
//			issueList.add(new Issue(this, IssueCategory.Identifiers, "Name of State is empty", Issue.SEVERITY_WARNING));
//		}
//	}

}
