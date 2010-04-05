package cbit.vcell.math;

import java.util.ArrayList;
import java.util.StringTokenizer;

import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableFunctionEntry;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry.FunctionArgType;
import cbit.vcell.units.VCUnitDefinition;

public class MathFunctionDefinitions {
	public final static String FUNCTION_regionarea_indexed	= "vcRegionArea(StructureName:LITERAL,RegionIndex:NUMERIC)";
	public final static String FUNCTION_regionarea_current	= "vcRegionArea(StructureName:LITERAL)";
	public final static String FUNCTION_regionvolume_indexed	= "vcRegionVolume(StructureName:LITERAL,RegionIndex:NUMERIC)";
	public final static String FUNCTION_regionvolume_current	= "vcRegionVolume(StructureName:LITERAL)";
	public final static String FUNCTION_field				= "vcField(DatasetName:LITERAL,VariableName:LITERAL,Time:NUMERIC,VariableType:LITERAL)";
	
	public static SymbolTableFunctionEntry createDeferedFunctionEntry(String formalDefinition, VCUnitDefinition vcUnitDefinition, NameScope nameScope){
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
	
}
