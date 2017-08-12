/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.numericstest.gui;

import javax.swing.JLabel;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestCaseNewMathModel;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestSuiteInfoNew;
/**
 * Insert the type's description here.
 * Creation date: (5/8/2003 10:01:34 AM)
 * @author: Anuradha Lakshminarayana
 */
public class NumericsTestCellRenderer extends cbit.vcell.desktop.VCellBasicCellRenderer {

	private static final java.awt.Color SOMETHINGS_WRONG_COLOR =
		new java.awt.Color(128,64,0);
/**
 * NumericsTestCellRenderer constructor comment.
 */
public NumericsTestCellRenderer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2000 6:41:57 PM)
 * @return java.awt.Component
 */
public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	//
	//Determine flags for coloring TestCases in GUI.
	// Default(black): ReportInfo with NO FAILED variables
	// Red: ReportInfo with FAILED variables
	// Magenta: No ReportInfo or some simulations don't have reports
	// Green: No Data in Report (Probably because the simulation has never been run)
	//
	try {
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			if (node.getUserObject() instanceof TestSuiteInfoNew) {
				TestSuiteInfoNew tsn = (TestSuiteInfoNew)node.getUserObject();
				// component.setText(tsn.getTSID()+" "+tsn.getTSVCellBuild()+" "+tsn.getTSNumericsBuild());
			} else if (node.getUserObject() instanceof TestCaseNew) {
				
				TestCaseNew testCase = (TestCaseNew)node.getUserObject();

				boolean hasUnknown = false;
				boolean isSimRunning = false;
				boolean isSimFailed = false;
				boolean isSimNotRunFialOrDone = false;
				boolean hasReportErrors =false;
				//boolean hasResult = false;
				boolean hasNoRefRegr = false;
				boolean hasFailures = false;
				boolean hasNoData = false; // AT LEAST ONE SIMULATION HAS NO DATA ...
				boolean needsReport = false;
				//for(int i=0;i < node.getChildCount();i+= 1){
					//BioModelNode childNode = (BioModelNode)node.getChildAt(i);
					//if(childNode.getUserObject() instanceof TestCriteriaNew){
						//TestCriteriaNew testCriteria = (TestCriteriaNew)childNode.getUserObject();
						//if(testCriteria.getReportStatus() != null &&
							//testCriteria.getReportStatus().startsWith(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_RPERROR)){
								//hasReportErrors = true;
						//}
						//if(testCase.getType().equals(TestCaseNew.REGRESSION) && testCriteria.getRegressionSimInfo() == null){
							//hasNoRefRegr = true;
						//}
						//if(testCriteria.getVarComparisonSummaries() != null && testCriteria.getVarComparisonSummaries().length != 0){
							////hasResult = true;
				            //for (int j = 0; j < testCriteria.getVarComparisonSummaries().length; j++) {
				                //// For each Variable, get the corresponding varComparisonSummary,
								//VariableComparisonSummary var1 = testCriteria.getVarComparisonSummaries()[j];
								//if (testCriteria != null
				                    //&& (testCriteria.getMaxAbsError().doubleValue() < var1.getAbsoluteError().doubleValue()
				                    //|| testCriteria.getMaxRelError().doubleValue() < var1.getRelativeError().doubleValue())) {
				                    //hasFailures = true;
				                //}
				            //}							
						//} else {
							//// At least one child/simulation/testCriterium has no data due to whatever reason
							//hasNoData = true;
						//}
					//}
				//}
				if(testCase.getTestCriterias() != null){
					for(int i=0;i<testCase.getTestCriterias().length;i+= 1){
						TestCriteriaNew tcrit = testCase.getTestCriterias()[i];
						if(tcrit.getReportStatus() != null){
							if(tcrit.getReportStatus().equals(
								cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT)){
									needsReport = true;
							}
							if(tcrit.getReportStatus().equals(
								cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_RPERROR)){
									hasReportErrors = true;
							}
							if(tcrit.getReportStatus().equals(
								cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NOREFREGR)){
									hasNoRefRegr = true;
							}
							if(tcrit.getReportStatus().equals(
								cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_FAILEDVARS)){
									hasFailures = true;
							}
							if(tcrit.getReportStatus().equals(
								cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NODATA)){
									hasNoData = true;
							}
							if(tcrit.getReportStatus().equals(
								cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_SIMRUNNING)){
									isSimRunning = true;
							}
							if(tcrit.getReportStatus().equals(
								cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_SIMFAILED)){
									isSimFailed = true;
							}
							if(tcrit.getReportStatus().equals(
								cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE)){
									isSimNotRunFialOrDone = true;
							}
						}
						
					}
				}
				//if(node.getRenderHint(cbit.vcell.client.desktop.testingframework.TestingFrmwkTreeModel.SIMULATIONS_NO_REPORT) instanceof Boolean){
					//Boolean tc_NR = (Boolean)node.getRenderHint(cbit.vcell.client.desktop.testingframework.TestingFrmwkTreeModel.SIMULATIONS_NO_REPORT);
					//if(tc_NR.booleanValue()){
						//hasResult = false;
					//}
				//}
				setComponentProperties(component, (TestCaseNew)node.getUserObject(),
					/*hasResult,*/ hasFailures, hasNoData, hasNoRefRegr,hasReportErrors,isSimRunning,isSimFailed,needsReport,isSimNotRunFialOrDone);
			} else if (node.getUserObject() instanceof TestCriteriaNew) {
				TestCriteriaNew testCriteria = (TestCriteriaNew)node.getUserObject();				
				String extraText = null;
				if(!testCriteria.getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_PASSED)){
					extraText=testCriteria.getReportStatus()+" "+
					(testCriteria.getReportStatusMessage() != null?" "+testCriteria.getReportStatusMessage().substring(0,Math.min(100,testCriteria.getReportStatusMessage().length())):"");
				}
				component.setText("["+testCriteria.getSimInfo().getVersion().getVersionKey()+"] "+
					testCriteria.getSimInfo().getVersion().getName()+(extraText != null?" ("+extraText+")":""));
				component.setToolTipText(" Test Criteria info");
				if(extraText != null){
					component.setForeground(java.awt.Color.magenta);
				}
				if(testCriteria.getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_FAILEDVARS)){
					component.setForeground(java.awt.Color.red);
				}
					
				////TestCaseNew tcn = null;
				////javax.swing.tree.DefaultMutableTreeNode prevNode = (javax.swing.tree.DefaultMutableTreeNode)node.getParent();
				////if(prevNode != null && prevNode.getUserObject() instanceof cbit.vcell.numericstest.TestCaseNew){
					////tcn = (TestCaseNew)prevNode.getUserObject();
				////}
				//boolean hasResult = false;
				//TestCriteriaNew testCriteria = (TestCriteriaNew)node.getUserObject();
				//if(testCriteria.getVarComparisonSummaries() != null && testCriteria.getVarComparisonSummaries().length != 0){
					//hasResult = true;
				//}
				////boolean hasNoRefRegr = false;
				////if(tcn != null){
					////if(tcn.getType().equals(cbit.vcell.numericstest.TestCaseNew.REGRESSION) &&
						////testCriteria.getRegressionSimInfo() == null){
							////hasNoRefRegr = true;
					////}
				////}	
				//if(!hasResult /*|| hasNoRefRegr*/){
					//component.setForeground(java.awt.Color.magenta);
				//}
				
				//component.setText(testCriteria.getSimInfo().getVersion().getName()+
					//(testCriteria.getReportStatus() != null &&
						//testCriteria.getReportStatus().startsWith(TestCriteriaNew.TCRIT_STATUS_SIMRUNNING)?"(SimRunning)":"")+
					//(testCriteria.getReportStatus() != null &&
						//testCriteria.getReportStatus().startsWith(TestCriteriaNew.TCRIT_STATUS_SIMFAILED)?"(SimFailed)":"")
					///*+(hasNoRefRegr?" (No Ref Regr)":"")*/);
				//component.setToolTipText(" Test Criteria info");

			}else if(node.getRenderHint(cbit.vcell.client.desktop.testingframework.TestingFrmwkTreeModel.FAILED_VARIABLE_MAE_MRE) instanceof Boolean){
				Boolean fv_MAE_MRE = (Boolean)node.getRenderHint(cbit.vcell.client.desktop.testingframework.TestingFrmwkTreeModel.FAILED_VARIABLE_MAE_MRE);
				if(fv_MAE_MRE.booleanValue()){
					component.setForeground(java.awt.Color.red);
				}else{
					component.setForeground(java.awt.Color.black);
				} 
			}
			
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
	//
	return component;
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
private void setComponentProperties(
    JLabel component,
    TestCaseNew testCase,
    /*boolean hasResult,*/
    boolean hasFailures,
    boolean hasNoData,
    boolean hasNoRefRegr,
    boolean hasReportErrors,
    boolean isSimRunning,
    boolean isSimFailed,
    boolean needsReport,
    boolean isSimNotRunFailOrDone) {

  	if(hasFailures || hasNoData || hasNoRefRegr || hasReportErrors || isSimRunning || isSimFailed || needsReport || isSimNotRunFailOrDone){
	  	component.setForeground(java.awt.Color.magenta);
  	}
  	
  	if(hasFailures && !hasNoData && !hasNoRefRegr && !hasReportErrors && !isSimRunning && !isSimFailed && !needsReport && !isSimNotRunFailOrDone){
	  	component.setForeground(java.awt.Color.red);
  	}
 	
    String info = "";
    
	if(isSimRunning){
		info+= "(Sim Running) ";
	}
    if (hasReportErrors){
	    info+= "(Report Error) ";
    }
    //if (!hasReportErrors && !hasNoData && !hasResult) {
		////component.setForeground(java.awt.Color.magenta);
		//info+= "(No Report) ";
	//}
    if (hasFailures) {
		//component.setForeground(java.awt.Color.red);
        info+= "(Failed Vars) ";
	}
    if (hasNoRefRegr) {
		//component.setForeground(java.awt.Color.gray);
		info+= "(No Ref Regr) ";
	}
	// There was at least one simulation/testcriterium that didn't have any data (var comparison summaries), so mark the entire testcase
	// as no data.
	if (hasNoData) {
		//component.setForeground(java.awt.Color.green);
		info+= "(No Data) ";
	}
	if(isSimFailed){
		info+= "(Sim Failed) ";
	}
	if(needsReport){
		info+= "(Needs Report) ";
	}
	if(isSimNotRunFailOrDone){
		info+= "(Sim NotRunFailOrDone) ";
	}
	
    String testCaseName = null;
        String testCaseDate = null;
        if (testCase instanceof TestCaseNewMathModel) {
        testCaseName =
            ((TestCaseNewMathModel) testCase).getMathModelInfo().getVersion().getName();
            testCaseDate =
                ((TestCaseNewMathModel) testCase)
                    .getMathModelInfo()
                    .getVersion()
                    .getDate()
                    .toString();
            } else
        if (testCase instanceof TestCaseNewBioModel) {
            testCaseName =
                ((TestCaseNewBioModel) testCase).getBioModelInfo().getVersion().getName()+" <<<App="+((TestCaseNewBioModel)testCase).getSimContextName()+">>>";
                testCaseDate =
                    ((TestCaseNewBioModel) testCase)
                        .getBioModelInfo()
                        .getVersion()
                        .getDate()
                        .toString();
                }

    String tcKind = "??"; if (testCase instanceof TestCaseNewMathModel) {
        tcKind = "MM"; } else
        if (testCase instanceof TestCaseNewBioModel) {
            tcKind = "BM"; }
    component.setText(
        info
            + " "
            + tcKind
            + " "
            + "\""
            + testCaseName
            + "\" ; "
            + testCaseDate
            + " ; "
            + testCase.getType()
            + " ; ["
            + testCase.getAnnotation()
            + "]");
        component.setToolTipText("Test Case");
        }
/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(javax.swing.JLabel component, Object object) {
	if (object instanceof TestSuiteInfoNew) {
		TestSuiteInfoNew tsn = (TestSuiteInfoNew)object;
		component.setText(
				(tsn.isLocked()?"(Lock) ":"")+"Version No. : "+tsn.getTSID()+
				"; Vcell Version No. : "+tsn.getTSVCellBuild()+
				"; Numerics Build No. : "+tsn.getTSNumericsBuild()+
				" ; ["+(tsn.getTSAnnotation() == null?"":tsn.getTSAnnotation())+"]");
		component.setToolTipText("Test Suite Info");
	}
	//if (object instanceof TestCaseNew){
		//TestCaseNew testCaseInfo = (TestCaseNew)object;
		//component.setText(testCaseInfo.getMathModelInfo().getVersion().getName()+ " " +testCaseInfo.getType());
		//component.setToolTipText("Test Case info");
	//}
	//if (object instanceof TestCriteriaNew){
		//TestCriteriaNew tcInfo = (TestCriteriaNew)object;
		//component.setText(tcInfo.getSimInfo().getVersion().getName());
		//component.setToolTipText(" Test Criteria info");
	//}
}
}
