package cbit.vcell.client.task;

import java.util.Hashtable;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestCriteriaNew;

public class TFRemoveTestCriteria extends AsynchClientTask {
	private TestingFrameworkWindowManager tfwm = null;
	private TestCriteriaNew[] removeTheseTestCriterias = null;
	public static final String REMOVE_THESE_TESTCRITERIAS = "REMOVE_THESE_TESTCRITERIAS";

	public TFRemoveTestCriteria(TestingFrameworkWindowManager tfwm, TestCriteriaNew[] removeTheseTestCriterias) {
		super("Removing TestCriteria...", TASKTYPE_NONSWING_BLOCKING);
		this.tfwm = tfwm;
		this.removeTheseTestCriterias = removeTheseTestCriterias;
	}
	public TFRemoveTestCriteria(TestingFrameworkWindowManager tfwm) {
		this(tfwm,null);
	}

	@Override
	public void run(Hashtable<String, Object> hashTable) throws Exception {
		if((removeTheseTestCriterias == null && hashTable.get(REMOVE_THESE_TESTCRITERIAS) == null) ||
				(removeTheseTestCriterias != null && hashTable.get(REMOVE_THESE_TESTCRITERIAS) != null)){
			throw new Exception("One and only one TestCriteria array allowed for removal.");
		}
		if(removeTheseTestCriterias == null){
			removeTheseTestCriterias = (TestCriteriaNew[])hashTable.get(REMOVE_THESE_TESTCRITERIAS);
		}
		if (removeTheseTestCriterias.length > 0) {
			tfwm.removeTestCriteria(removeTheseTestCriterias);
		}
	}

}
