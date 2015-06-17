package org.vcell.model.rbm;

import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;

import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;

public class SeedSpecies extends RbmElementAbstract implements Matchable, IssueSource {

	private SpeciesPattern speciesPattern;
	private Expression initialCondition;
	public static final String PROPERTY_NAME_SPECIES_PATTERN = "speciesPattern";
	public static final String PROPERTY_NAME_INITIAL_CONDITION = "initialCondition";
	
	public SeedSpecies() {
	}
	public SeedSpecies(SpeciesPattern speciesPattern, Expression initCond) {
		this.speciesPattern = speciesPattern;
		this.initialCondition = initCond;
	}
	public final SpeciesPattern getSpeciesPattern() {
		return speciesPattern;
	}
	public final void setSpeciesPattern(SpeciesPattern newValue) {
		SpeciesPattern oldValue = speciesPattern;
		this.speciesPattern = newValue;
		firePropertyChange(PROPERTY_NAME_SPECIES_PATTERN, oldValue, newValue);
		resolveBonds();
	}
	private void resolveBonds() {
		List<MolecularTypePattern> molecularTypePatterns = speciesPattern.getMolecularTypePatterns();
		for (int i = 0; i < molecularTypePatterns.size(); ++ i) {
			molecularTypePatterns.get(i).setIndex(i+1);
		}
		speciesPattern.resolveBonds();
	}

	public final Expression getInitialCondition() {
		return initialCondition;
	}
	public final void setInitialCondition(Expression newValue) {
		Expression oldValue = initialCondition;
		this.initialCondition = newValue;
		firePropertyChange(PROPERTY_NAME_INITIAL_CONDITION, oldValue, newValue);
	}
//	public String getId() {
//		System.err.println("generating unique but useless Species ID");
//		return "Species_"+hashCode();
//	}
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof SeedSpecies)) {
			return false;
		}
		SeedSpecies that = (SeedSpecies)aThat;

		if (!Compare.isEqual(speciesPattern, that.speciesPattern)) {
			return false;
		}
		if (!Compare.isEqual(initialCondition, that.initialCondition)) {
			return false;
		}
		return true;
	}
	
	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		issueContext = issueContext.newChildContext(ContextType.SeedSpecies, this);
		if(speciesPattern == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, SpeciesPattern.typeName + " is null", Issue.SEVERITY_ERROR));
		} else {
			if(initialCondition == null) {
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, SpeciesContext.typeName + " '" + speciesPattern + "' Initial Condition Expression is null", Issue.SEVERITY_WARNING));
			}
			speciesPattern.gatherIssues(issueContext, issueList);
		}
	}

}
