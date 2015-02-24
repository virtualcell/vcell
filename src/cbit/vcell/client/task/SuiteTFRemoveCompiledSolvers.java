package cbit.vcell.client.task;

import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import edu.uchc.connjur.wb.LineStringBuilder;

/**
 * Tasks to support removing compiled solvers from test suites
 * @author gweatherby
 */
public class SuiteTFRemoveCompiledSolvers  {
	
	private static final String compiledStatusMessageStubs[] =
		{
			"Compiled solvers no longer supported",
			"Semi-Implicit Finite Volume Compiled",
			"Failed to compile your simulation"
		};

	/**
	 * create required tasks
	 * @param tfwm not null
	 * @param tsin not null
	 * @return array of tasks 
	 */
	public static AsynchClientTask[]  createTasks(TestingFrameworkWindowManager tfwm,  TestSuiteInfoNew tsin) {
		AsynchClientTask rval []  = { 
				new Collect(tfwm,tsin),
				new Prompt(tfwm),
				new TFRemoveTestCriteria(tfwm)
		}; 

		return rval;
	}

	/**
	 * collect all test criteria that failed due to Compiled solvers
	 */
	private static class Collect extends AsynchClientTask {
		private TestingFrameworkWindowManager tfwm = null;
		private final TestSuiteInfoNew tsInfo;

		/**
		 * @param tfwm not null
		 * @param tsin not null
		 */
		public Collect(TestingFrameworkWindowManager tfwm,  TestSuiteInfoNew tsin) {
			super("Collecting compiled test critera...", TASKTYPE_NONSWING_BLOCKING);
			this.tfwm = tfwm;
			this.tsInfo = tsin;
		}

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			ArrayList<TestCriteriaNew> compiledSolverTests = new ArrayList<>();
			TestSuiteNew ts = tfwm.getRequestManager().getDocumentManager().getTestSuite(tsInfo.getTSKey());
			for (TestCaseNew tcn : ts.getTestCases()) {
				for ( TestCriteriaNew tcc : tcn.getTestCriterias()) {
					if (tcc.getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_SIMFAILED)) {
						String statusMessage = tcc.getReportStatusMessage();
						for (String stub : compiledStatusMessageStubs) {
							if (statusMessage.contains(stub)) {
								compiledSolverTests.add(tcc);
							}
						}
					}
				}
			}
			TestCriteriaNew[] tca = compiledSolverTests.toArray(new TestCriteriaNew[compiledSolverTests.size()]);
			hashTable.put(TFRemoveTestCriteria.REMOVE_THESE_TESTCRITERIAS, tca); 
		}
	}

	/**
	 * prompt user to review criteria to be deleted
	 */
	private static class Prompt extends AsynchClientTask {
		private final TestingFrameworkWindowManager tfwm; 

		/**
		 * @param tfwm not null
		 */
		public Prompt(TestingFrameworkWindowManager tfwm) {
			super("Review compiled test critera...", TASKTYPE_SWING_BLOCKING);
			this.tfwm = tfwm;
		}


		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Object obj = hashTable.get(TFRemoveTestCriteria.REMOVE_THESE_TESTCRITERIAS);
			if (obj instanceof TestCriteriaNew[]) {
				//if (true) {
				TestCriteriaNew[] tcArray = (TestCriteriaNew[]) obj;
				if (tcArray.length > 0) {
					LineStringBuilder lsb = new LineStringBuilder("Remove tests "); 
					for (TestCriteriaNew tcc :tcArray) {
						lsb.append(tcc.describe());
						lsb.append(' ');
						lsb.write(tcc.getReportStatusMessage());
					}
					final String DELETE = "Delete";
					String response = DialogUtils.showWarningDialog(tfwm.getComponent(), lsb.toString(),new String[] {DELETE,"Cancel"},DELETE);
					if (response == null || !response.equals(DELETE)) {
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
			}
		}
	}
}
