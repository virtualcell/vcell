package cbit.vcell.math;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;

import cbit.vcell.field.FieldUtilities;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableFunctionEntry;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry.FunctionArgType;
import cbit.vcell.simdata.VariableType.VariableDomain;
import cbit.vcell.units.VCUnitDefinition;

public class MathFunctionDefinitions {
	public final static String FUNCTION_regionarea_indexed	= "vcRegionArea(StructureName:LITERAL,RegionIndex:NUMERIC)";
	public final static String FUNCTION_regionarea_current	= "vcRegionArea(StructureName:LITERAL)";
	public final static String FUNCTION_regionvolume_indexed	= "vcRegionVolume(StructureName:LITERAL,RegionIndex:NUMERIC)";
	public final static String FUNCTION_regionvolume_current	= "vcRegionVolume(StructureName:LITERAL)";
	public final static String FUNCTION_field				= "vcField(DatasetName:LITERAL,VariableName:LITERAL,Time:NUMERIC,VariableType:LITERAL)";
	
	public final static SymbolTableFunctionEntry Function_regionArea_indexed = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_regionarea_indexed, VCUnitDefinition.UNIT_um2, null);
	public final static SymbolTableFunctionEntry Function_regionArea_current = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_regionarea_current, VCUnitDefinition.UNIT_um2, null);
	public final static SymbolTableFunctionEntry Function_regionVolume_indexed = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_regionvolume_indexed, VCUnitDefinition.UNIT_um3, null);
	public final static SymbolTableFunctionEntry Function_regionVolume_current = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_regionvolume_current, VCUnitDefinition.UNIT_um3, null);
	public final static SymbolTableFunctionEntry Function_field = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_field, VCUnitDefinition.UNIT_TBD, null);
	
	private static SymbolTableFunctionEntry createDeferedFunctionEntry(String formalDefinition, VCUnitDefinition vcUnitDefinition, NameScope nameScope){
		StringTokenizer tokens = new StringTokenizer(formalDefinition,"(),", false);
		String funcName = tokens.nextToken();
		ArrayList<FunctionArgType> argTypeList = new ArrayList<FunctionArgType>();
		ArrayList<String> argNameList = new ArrayList<String>();
		while (tokens.hasMoreElements()){
			String argument = tokens.nextToken();
			StringTokenizer argTokenizer = new StringTokenizer(argument,":");
			String argName = argTokenizer.nextToken();
			String argType = argTokenizer.nextToken();
			argTypeList.add(FunctionArgType.valueOf(argType));
			argNameList.add(argName);
		}
		String[] argNames = argNameList.toArray(new String[argNameList.size()]);
		FunctionArgType[] argTypes = argTypeList.toArray(new FunctionArgType[argTypeList.size()]);
		
		return new SimpleSymbolTableFunctionEntry(funcName, argNames, argTypes, null, vcUnitDefinition, nameScope);
	}
	
	public static Expression substituteSizeFunctions(Expression origExp, VariableDomain variableDomain) throws ExpressionException {
		Expression exp = new Expression(origExp);
		Set<FunctionInvocation> fiSet = FieldUtilities.getSizeFunctionInvocations(exp);
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
	
	
}
