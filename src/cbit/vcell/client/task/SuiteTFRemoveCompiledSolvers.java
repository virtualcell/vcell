package cbit.vcell.client.task;

import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestCaseNewMathModel;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestCriteriaNewBioModel;
import cbit.vcell.numericstest.TestCriteriaNewMathModel;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import edu.uchc.connjur.wb.LineStringBuilder;

/**
 * Tasks to support removing compiled solvers from test suites
 * @author gweatherby
 */
public class SuiteTFRemoveCompiledSolvers  {
	/**
	 * Messages from attempts to run compiled solvers
	 */
	private static final String COMPILED_MSGS[] = {
		"Compiled solvers no longer supported",
		"The selected Solver Semi-Implicit Finite Volume Compiled, Regular Grid (Fixed Time Step) (DEPRECATED)"	
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
				VCDocument vcDocument = null;
				Simulation[] simArr = null;
				try{
					if(tcn instanceof TestCaseNewBioModel){
						vcDocument = tfwm.getRequestManager().getDocumentManager().getBioModel(tcn.getVersion().getVersionKey());
						simArr = ((BioModel)vcDocument).getSimulations();
					}else if(tcn instanceof TestCaseNewMathModel){
						vcDocument = tfwm.getRequestManager().getDocumentManager().getMathModel(tcn.getVersion().getVersionKey());
						simArr = ((MathModel)vcDocument).getSimulations();
					}else{
						throw new Exception("Error checking compiled solvers for TestCase '"+tcn.getVersion().getName()+"' expecting MathModel or BioModel but got type "+tcn.getClass().getName());
					}
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Failed loading VCDocument for testcase '"+tcn.getVersion().getName()+"', its testcriteria cannot be checked for compiled solvers");
					continue;
				}
				for ( TestCriteriaNew tcc : tcn.getTestCriterias()) {
					for (int i = 0; i < simArr.length; i++) {
						if(simArr[i].getVersion().getVersionKey().equals(tcc.getSimInfo().getVersion().getVersionKey())){
							Boolean isCompiled = simArr[i].getSolverTaskDescription().getSolverDescription() == SolverDescription.FiniteVolume;
							if(isCompiled){
								compiledSolverTests.add(tcc);
								System.out.println("-----removing model '"+vcDocument.getName()+"', sim '"+simArr[i].getName()+"'");
							}
							break;
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
