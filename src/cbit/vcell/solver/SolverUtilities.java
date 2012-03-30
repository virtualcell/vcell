package cbit.vcell.solver;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.solvers.CartesianMesh;

public class SolverUtilities {

	public static Expression substituteSizeFunctions(Expression origExp, VariableDomain variableDomain) throws ExpressionException {
		Expression exp = new Expression(origExp);
		Set<FunctionInvocation> fiSet = MathFunctionDefinitions.getSizeFunctionInvocations(exp);
		for(FunctionInvocation fi : fiSet) {
			String functionName = fi.getFunctionName();
			// replace vcRegionArea('domain') and vcRegionVolume('domain') with vcRegionArea or vcRegionVolume or vcRegionVolume_domain
			// the decision is based on variable domain
			if (variableDomain.equals(VariableDomain.VARIABLEDOMAIN_VOLUME)) {
				exp.substituteInPlace(fi.getFunctionExpression(), new Expression(functionName));
			} else if (variableDomain.equals(VariableDomain.VARIABLEDOMAIN_MEMBRANE)) {
				if (functionName.equals(MathFunctionDefinitions.Function_regionArea_current.getFunctionName())) {
					exp.substituteInPlace(fi.getFunctionExpression(), new Expression(functionName));
				} else {
					String domainName = fi.getArguments()[0].infix();
					// remove single quote
					domainName = domainName.substring(1, domainName.length() - 1);
					exp.substituteInPlace(fi.getFunctionExpression(), new Expression(functionName + "_" + domainName));
				}
			}
		}
		return exp;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/28/2005 11:44:25 AM)
	 */
	public static DataIdentifier[] collectSimilarDataTypes(DataIdentifier variable, DataIdentifier[] dataIDs){
	
		//Sort variable names, ignore case
		java.util.TreeSet<DataIdentifier> treeSet = new java.util.TreeSet<DataIdentifier>(
			new java.util.Comparator<DataIdentifier>(){
				public int compare(DataIdentifier o1, DataIdentifier o2){
					int ignoreCaseB = o1.getName().compareToIgnoreCase(o2.getName());
					if(ignoreCaseB == 0){
						return o1.getName().compareTo(o2.getName());
					}
					return ignoreCaseB;
				}
			}
		);
		for(int i = 0; i <dataIDs.length; i += 1){
			if (variable.getVariableType().getVariableDomain().equals(dataIDs[i].getVariableType().getVariableDomain())) {
				treeSet.add(dataIDs[i]);
			}
		}
	
		DataIdentifier[] results = new DataIdentifier[treeSet.size()];
		treeSet.toArray(results);
		return results;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/00 2:48:55 PM)
	 * @return cbit.vcell.simdata.PDEVariableType
	 * @param mesh cbit.vcell.solvers.CartesianMesh
	 * @param dataLength int
	 */
	public static final VariableType getVariableTypeFromLength(CartesianMesh mesh, int dataLength) {
		VariableType result = null;
		if (mesh.getDataLength(VariableType.VOLUME) == dataLength) {
			result = VariableType.VOLUME;
		} else if (mesh.getDataLength(VariableType.MEMBRANE) == dataLength) {
			result = VariableType.MEMBRANE;
		} else if (mesh.getDataLength(VariableType.CONTOUR) == dataLength) {
			result = VariableType.CONTOUR;
		} else if (mesh.getDataLength(VariableType.VOLUME_REGION) == dataLength) {
			result = VariableType.VOLUME_REGION;
		} else if (mesh.getDataLength(VariableType.MEMBRANE_REGION) == dataLength) {
			result = VariableType.MEMBRANE_REGION;
		} else if (mesh.getDataLength(VariableType.CONTOUR_REGION) == dataLength) {
			result = VariableType.CONTOUR_REGION;
		}
		return result;
	}

}
