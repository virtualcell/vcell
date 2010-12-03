package cbit.vcell.math;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;

import org.vcell.util.CommentStringTokenizer;

import cbit.vcell.field.FieldFunctionDefinition;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableFunctionEntry;
import cbit.vcell.parser.Expression.FunctionFilter;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableFunctionEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry.FunctionArgType;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.simdata.VariableType.VariableDomain;
import cbit.vcell.units.VCUnitDefinition;

public class MathFunctionDefinitions {
	public final static String FUNCTION_regionarea_indexed	= "vcRegionArea(StructureName:LITERAL,RegionIndex:NUMERIC)";
	public final static String FUNCTION_regionarea_current	= "vcRegionArea(StructureName:LITERAL)";
	public final static String FUNCTION_regionvolume_indexed	= "vcRegionVolume(StructureName:LITERAL,RegionIndex:NUMERIC)";
	public final static String FUNCTION_regionvolume_current	= "vcRegionVolume(StructureName:LITERAL)";
	public final static String FUNCTION_convolution			= "vcConv(Function:NUMERIC,Kernel:NUMERIC)";
	public final static String FunctionName_field_old		= "field";
	public final static String FunctionName_grad_old		= "grad";
	
	public final static SymbolTableFunctionEntry Function_regionArea_indexed = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_regionarea_indexed, VCUnitDefinition.UNIT_um2, null);
	public final static SymbolTableFunctionEntry Function_regionArea_current = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_regionarea_current, VCUnitDefinition.UNIT_um2, null);
	public final static SymbolTableFunctionEntry Function_regionVolume_indexed = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_regionvolume_indexed, VCUnitDefinition.UNIT_um3, null);
	public final static SymbolTableFunctionEntry Function_regionVolume_current = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_regionvolume_current, VCUnitDefinition.UNIT_um3, null);
	public final static SymbolTableFunctionEntry Function_convolution = createDeferedFunctionEntry(MathFunctionDefinitions.FUNCTION_convolution, VCUnitDefinition.UNIT_DIMENSIONLESS, null);
	
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
	
	
	public static Expression fixFunctionSyntax(CommentStringTokenizer tokens) throws ExpressionException {
		return fixFunctionSyntax(new Expression(tokens.readToSemicolon()));
	}
	
	
	public static Expression fixFunctionSyntax(Expression exp) throws ExpressionException {
		FunctionInvocation[] functionInvocations = exp.getFunctionInvocations(new FunctionFilter() {
			public boolean accept(String functionName) {
				return (functionName.equals(FunctionName_field_old)  || 
						functionName.equals(FunctionName_grad_old));
			}
		});
		for (int i = 0;functionInvocations!=null && i < functionInvocations.length; i++) {
			Expression[] argExpressions = functionInvocations[i].getArguments();
			if (functionInvocations[i].getFunctionName().equals(FunctionName_field_old)){
				if (argExpressions.length>=2 && argExpressions.length<=4){
					StringBuffer functionBuffer = new StringBuffer(FieldFunctionDefinition.FUNCTION_name+"(");

					if (argExpressions[0].isIdentifier()){
						functionBuffer.append("'"+argExpressions[0].infix()+"'");
					}else{
						throw new ExpressionException("unexpected function format '"+functionInvocations[i].getFunctionExpression()+"'");
					}
					functionBuffer.append(",");
					
					if (argExpressions[1].isIdentifier()){
						functionBuffer.append("'"+argExpressions[1].infix()+"'");
					}else{
						throw new ExpressionException("unexpected function format '"+functionInvocations[i].getFunctionExpression()+"'");
					}
					functionBuffer.append(",");
					
					if (argExpressions.length>=3){
						functionBuffer.append(argExpressions[2].infix());
					}else{
						functionBuffer.append("0.0");
					}
					functionBuffer.append(",");
					
					if (argExpressions.length>=4){
						if (argExpressions[3].isIdentifier()){
							VariableType varType = VariableType.getVariableTypeFromVariableTypeNameIgnoreCase(argExpressions[3].infix());
							functionBuffer.append("'"+varType.getTypeName()+"'");
						}else{
							throw new ExpressionException("unexpected function format '"+functionInvocations[i].getFunctionExpression()+"'");
						}
					}else{
						functionBuffer.append("'"+VariableType.VOLUME.getTypeName()+"'");
					}
					functionBuffer.append(")");
					
					exp = exp.getSubstitutedExpression(functionInvocations[i].getFunctionExpression(), new Expression(functionBuffer.toString()));
				}
			}else if (functionInvocations[i].getFunctionName().equals(FunctionName_grad_old)){
				if (argExpressions.length==2 && !argExpressions[1].isLiteral()){
					// grad(x,calcium) ==> vcGrad(calcium,'x') ... where x is [x y z m]
					StringBuffer functionBuffer = new StringBuffer(GradientFunctionDefinition.FUNCTION_name+"(");
					Expression componentArg = argExpressions[0];
					Expression variableArg = argExpressions[1];
					
					functionBuffer.append(variableArg.infix());
					
					functionBuffer.append(",");

					functionBuffer.append("'"+componentArg.infix()+"'");
					
					functionBuffer.append(")");

					exp = exp.getSubstitutedExpression(functionInvocations[i].getFunctionExpression(), new Expression(functionBuffer.toString()));
				}
			}
		}
		return exp;
	}
	
}
