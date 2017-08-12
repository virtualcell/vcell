/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;

public class OptXmlTags {
	
	public static final String OptProblemDescription_Tag = "optProblemDescription";
	
	public static final String CopasiOptimizationMethod = "CopasiOptimizationMethod";
	public static final String CopasiOptimizationParameter = "CopasiOptimizationParameter";
	public static final String Value_Attr = "value";
	public static final String DataType_Attr = "dataType";
	public static final String Name_Attr = "name";
	
	//for VCell optimization options that are not in copasi method options
	public static final String VCellOptions_Tag = "VCellOptions";
	public static final String NumOptimizationRuns_Tag = "NumberOfOptimizationRuns";
	
	public static final String MathModelSbmlFile_Tag = "MathModelSbmlFile";
	public static final String ExperimentalDataFile_Tag = "ExperimentalDataFile";
	public static final String ExperimentalDataFile_Attr_LastRow = "LastRow";
	
	public static final String ComputeProfileDistributions_Attr = "ComputeProfileDistributions";

	public static final String ObjectiveFunction_Tag = "objectiveFunction";
	public static final String ObjectiveFunctionType_Attr = "type";
	public static final String ObjectiveFunctionType_Attr_Explicit = "explicit";
	public static final String ObjectiveFunctionType_Attr_ExplicitFit = "explicitFit";
	public static final String ObjectiveFunctionType_Attr_ODE = "ode";
	public static final String ObjectiveFunctionType_Attr_PDE = "pde";
	public static final String Model_Tag = "model";
	public static final String ModelType_Attr = "modelType";
	public static final String ModelType_Attr_IDA = "ida";
	public static final String ModelType_Attr_CVODE = "cvode";
	public static final String ModelType_Attr_FVSOLVER = "fvSolver";
	public static final String SimpleReferenceData_Tag = "SimpleReferenceData";
	public static final String SpatialReferenceData_Tag = "SpatialReferenceData";
	public static final String Datarow_Tag = "dataRows";
	public static final String WeightDatarow_Tag = "weightRows";
	public static final String WeightType_Attr = "weightType";
	public static final String WeightType_Attr_Variable = "variableWeights";
	public static final String WeightType_Attr_Time = "timeWeights";
	public static final String WeightType_Attr_Element = "elementWeights";
	public static final String Variable_Tag = "variable";
	public static final String VariableType_Attr = "type";
	public static final String VariableType_Attr_Independent = "independent";
	public static final String VariableType_Attr_Dependent = "dependent";
	public static final String VariableName_Attr = "name";
	public static final String VariableDimension_Attr = "dim";
	public static final String Row_Tag = "row";
	public static final String ModelMapping_Tag = "modelMapping";
	public static final String ModelMappingDataColumn_Attr = "dataColumn";
	public static final String ModelMappingWeight_Attr = "weight";
	public static final String ExpressionList_Tag = "expressionList";
	public static final String Expression_Tag = "expression";
	public static final String ExpRefDataColumnIndex_Attr = "referenceDataColIndex";

	public static final String ParameterDescription_Tag = "parameterDescription";
	public static final String Parameter_Tag = "parameter";
	public static final String ParameterName_Attr = "name";
	public static final String ParameterLow_Attr = "low";
	public static final String ParameterHigh_Attr = "high";
	public static final String ParameterInit_Attr = "init";
	public static final String ParameterScale_Attr = "scale";
	public static final String ParameterBestValue_Attr = "bestValue";

	public static final String ConstraintDescription_Tag = "constraintDescription";
	public static final String Constraint_Tag = "constraint";
	public static final String ConstraintType_Attr = "constraintType";
	public static final String ConstraintType_Attr_LinearEquality = "linearEquality";
	public static final String ConstraintType_Attr_LinearInequality = "linearInquality";
	public static final String ConstraintType_Attr_NonlinearEquality = "nonlinearEquality";
	public static final String ConstraintType_Attr_NonlinearInequality = "nonlinearInequality";

	public static final String OptimizationResultSet_Tag = "OptimizationResultSet";
	public static final String OptSolverResultSet_Tag = "optSolverResultSet";
	public static final String OptSolverResultSetBestObjectiveFunction_Attr = "bestObjectiveFunction";
	public static final String OptSolverResultSetFunctionEvaluations_Tag = "ObjectiveFunctionEvaluations";
	public static final String OptSolverResultSetNumObjectiveFunctionEvaluations_Attr = "numObjectiveFunctionEvaluations";
	public static final String OptSolverResultSetStatus_Attr = "status";
	public static final String OptSolverResultSetStatus_Attr_Unknown = "unknown";
	public static final String OptSolverResultSetStatus_Attr_NormalTermination = "normalTermination";
	public static final String OptSolverResultSetStatus_Attr_NonfeasibleLinear = "nonfeasibleLinear";
	public static final String OptSolverResultSetStatus_Attr_NonfeasibleNonlinear = "nonfeasibleNonlinear";
	public static final String OptSolverResultSetStatus_Attr_NoSolutionIterations = "noSolutionIterations";
	public static final String OptSolverResultSetStatus_Attr_NoSolutionMachinePrecision = "noSolutionMachinePrecision";
	public static final String OptSolverResultSetStatus_Attr_FailedConstructingD0 = "failedConstructingD0";
	public static final String OptSolverResultSetStatus_Attr_FailedConstructingD1 = "failedConstructingD1";
	public static final String OptSolverResultSetStatus_Attr_FailedInconsistentInput = "failedInconsistentInput";
	public static final String OptSolverResultSetStatus_Attr_FailedIteratesStalled = "failedIteratesStalled";
	public static final String OptSolverResultSetStatus_Attr_FailedPenaltyTooLarge = "failedPenaltyTooLarge";
	public static final String OptSolverResultSetStatus_Attr_Failed = "failed";
	public static final String OptSolverResultSetStatus_Attr_StoppedByUser = "stoppedByUser";
	public static final String OptSolverResultSetStatusMessage_Attr = "statusMessage";
	
	public static final String bestOptRunResultSet_Tag = "bestOptRunResultSet";
	public static final String OptRunResultSet_Tag = "OptRunResultSet";
	public static final String ProfileDistributionList_Tag = "ProfileDistibutionList";
	public static final String ProfileDistribution_Tag = "ProfileDistibution";
	public static final String ProfileDistribution_FixedIndex_Attr = "fixedIndex";
	public static final String ProfileDistribution_FixedParameter_Attr = "fixedParameter";

}
