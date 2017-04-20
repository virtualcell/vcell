package cbit.vcell.numericstest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

import cbit.vcell.client.ClientFactory;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.solver.test.VariableComparisonSummary;

public class MathTestingReportGenerator {

	public static void main(String[] args) {
		ClientServerManager clientServerManager = null;
		try {
			if (args.length!=2){
				System.out.println("usage: MathTestingReportGenerator host password");
				System.exit(1);
			}
			String userid = "vcelltestaccount";
			String host = args[0];
			String password = args[1];
			clientServerManager = ClientFactory.createRemoteClientServerManager(new String[] { host }, userid, password);
			DocumentManager documentManager = clientServerManager.getDocumentManager();
			TestSuiteInfoNew[] testSuiteInfos = documentManager.getTestSuiteInfos();
			for (TestSuiteInfoNew testSuiteInfo : testSuiteInfos){
				System.out.println(testSuiteInfo.toShortString());
			}
			
			//
			// sorted by TSKey
			//
			Arrays.sort(testSuiteInfos,new Comparator<TestSuiteInfoNew>() {
				@Override
				public int compare(TestSuiteInfoNew o1, TestSuiteInfoNew o2) {
					return o1.getTSKey().compareTo(o2.getTSKey());
				}
			});
			
			//
			// print out details of the last one.
			//
			TestSuiteNew testSuite = documentManager.getTestSuite(testSuiteInfos[testSuiteInfos.length-1].getTSKey());
			for (TestCaseNew testCase : testSuite.getTestCases()){
				System.out.println("TEST CASE: "+testCase.getAnnotation());
				TestCriteriaNew[] testCriterias = testCase.getTestCriterias();
				for (TestCriteriaNew testCriteria : testCriterias){
					System.out.println("   CRITERIA: "+testCriteria.getReportStatusMessage());
					for (VariableComparisonSummary summary : testCriteria.getVarComparisonSummaries()){
						System.out.println("        SUMMARY: "+summary.toShortString());
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		} finally {
			if (clientServerManager!=null){
				clientServerManager.cleanup();
			}
		}
	}

}
