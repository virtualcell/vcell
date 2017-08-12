package cbit.vcell.math;

import org.vcell.util.Issue.IssueSource;


public abstract class DataGenerator extends Variable implements IssueSource {

	protected DataGenerator(String argName, Domain argDomain) {
		super(argName, argDomain);
	}

}
