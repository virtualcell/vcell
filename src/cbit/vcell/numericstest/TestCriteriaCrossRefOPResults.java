/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.numericstest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import cbit.vcell.solver.test.VariableComparisonSummary;

public class TestCriteriaCrossRefOPResults extends TestSuiteOPResults implements Serializable{

	private BigDecimal testCriterium;
	private Vector<CrossRefData> crossRefData;
	
	public static class CrossRefData implements Serializable{
		public boolean isBioModel;
		public String tsVersion;
		public BigDecimal tsKey;
		public BigDecimal tcaseKey;
		public BigDecimal tcritKey;
		public String bmName;
		public String bmAppName;
		public String mmName;
		public String simName;
		public double maxAbsErorr;
		public double maxRelErorr;
		public String varName;
		public String tsRefVersion;
		public VariableComparisonSummary varCompSummary;
		public BigDecimal regressionModelID;
		public String tcSolutionType;
		public BigDecimal modelID;
		public BigDecimal regressionModelTSuiteID;
		public BigDecimal regressionModelTCaseID;
		public BigDecimal regressionModelTCritID;
		
		public CrossRefData(ResultSet rset,boolean isBioModel) throws SQLException{
			this.isBioModel = isBioModel;
			tsVersion = rset.getString(1);
			tsKey = rset.getBigDecimal(2);
			tcaseKey = rset.getBigDecimal(3);
			tcritKey = rset.getBigDecimal(4);
			if(isBioModel){
				bmName = rset.getString(5);
				bmAppName = rset.getString(6);
				simName = rset.getString(7);
				maxAbsErorr = rset.getDouble(8);
				maxRelErorr = rset.getDouble(9);
				varName = rset.getString(10);
				if(rset.wasNull()){varName = null;}
				else{
					varCompSummary =
						new VariableComparisonSummary(
							varName,
							rset.getDouble(11),
							rset.getDouble(12),
							rset.getDouble(13),
							rset.getDouble(14),
							rset.getDouble(15),
							rset.getDouble(16),
							rset.getInt(17),
							rset.getDouble(18),
							rset.getInt(19)
					);
				}
				tsRefVersion = rset.getString(20);
				regressionModelID = rset.getBigDecimal(21);
				tcSolutionType = rset.getString(22);
				modelID = rset.getBigDecimal(23);
				regressionModelTSuiteID = rset.getBigDecimal(24);
				regressionModelTCaseID = rset.getBigDecimal(25);
				regressionModelTCritID = rset.getBigDecimal(26);
			}else{
				mmName = rset.getString(5);
				simName = rset.getString(6);				
				maxAbsErorr = rset.getDouble(7);
				maxRelErorr = rset.getDouble(8);
				varName = rset.getString(9);
				if(rset.wasNull()){varName = null;}
				else{
					varCompSummary =
						new VariableComparisonSummary(
							varName,
							rset.getDouble(10),
							rset.getDouble(11),
							rset.getDouble(12),
							rset.getDouble(13),
							rset.getDouble(14),
							rset.getDouble(15),
							rset.getInt(16),
							rset.getDouble(17),
							rset.getInt(18)
					);
				}
				tsRefVersion = rset.getString(19);
				regressionModelID = rset.getBigDecimal(20);
				tcSolutionType = rset.getString(21);
				modelID = rset.getBigDecimal(22);
				regressionModelTSuiteID = rset.getBigDecimal(23);
				regressionModelTCaseID = rset.getBigDecimal(24);
				regressionModelTCritID = rset.getBigDecimal(25);
			}		
		}
	}
	
	public TestCriteriaCrossRefOPResults(BigDecimal argTSKey,BigDecimal argTCritKey,Vector<CrossRefData> crossRefData) {
		super(argTSKey);
		testCriterium = argTCritKey;
		this.crossRefData = crossRefData;
	}

	public BigDecimal getTestCriterium() {
		return testCriterium;
	}

	public Vector<CrossRefData> getCrossRefData() {
		return crossRefData;
	}

}
