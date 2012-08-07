package cbit.vcell.units.parser;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import cbit.vcell.matrix.RationalExp;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.RationalExpUtils;
import cbit.vcell.units.parser.Node.UnitTextFormat;

public class UnitSymbol implements Serializable {

	private ASTUnitSymbol rootNode = null;

	public UnitSymbol(String unitStr) {
		super();
		parseUnits(unitStr);
	}
	
	private void parseUnits(String unitString) {
		try {
			String unit = unitString.trim();
			if (!unit.endsWith(";")){
				unit = unit + ";";
			}
			UnitSymbolParser unitParser;
			unitParser = new UnitSymbolParser(new ByteArrayInputStream(unit.getBytes()));
			rootNode = unitParser.UnitSymbol();
			double numericScale = rootNode.getNumericScale();
			String infixWithoutNumericScale = rootNode.toInfixWithoutNumericScale();
			Expression exp = null;
			RationalExp rationalExp = null;
			try {
				exp = new Expression(infixWithoutNumericScale);
				rationalExp = RationalExpUtils.getRationalExp(exp);
				// Converting rationalExp back to expression since RationalExp represents power (^) of 'x' as multiple 'x's (e.g., x^3 == x.x.x) 
				exp = rationalExp.simplifyAsExpression();
				// Converting rationalExp to exp will add parentheses and spaces in the expression. 
				// These need to be removed from expression infix string for unitParser to parse expression infix. 
				String infix2 = exp.infix_UNITS();
				// remove start "(" and end ")" if they exist
				if (infix2.contains("(") && infix2.contains(")") && infix2.startsWith("(") && infix2.endsWith(")")) { 
					// remove start "("
					infix2 = infix2.substring(1);
					// remove last ")"
					infix2 = infix2.substring(0, infix2.length()-1);
				}
				// replace blank spaces " " with no space ""
				infix2 = infix2.replace(" ", "");
				// add ";" to end of infix string for unitParser 
				if (numericScale!=1.0){
					infix2 = numericScale + " " + infix2;
				}
				infix2 = infix2 + ";";
				unitParser = new UnitSymbolParser(new ByteArrayInputStream(infix2.getBytes()));
				rootNode = unitParser.UnitSymbol();
			} catch (ExpressionException e) {
				e.printStackTrace(System.out);
				System.out.println("Failed to simply unitsymbol '" + rootNode.toInfix() + "'" + e.getMessage());
			} catch (jscl.text.ParseException e) {
				e.printStackTrace(System.out);
				System.out.println("Failed to simply unitsymbol '" + rootNode.toInfix() + "'" + e.getMessage());
			}
		} catch (ParseException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Parse Error while parsing expression '" + unitString + "'.\n " + e.getMessage());
		} catch (TokenMgrError e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Parse Error while parsing expression '" + unitString + "'.\n " + e.getMessage());
		}
	}

	public String getUnitSymbolAsInfix() {
		return rootNode.toInfix();
	}

	public String getUnitSymbolAsInfixWithoutFloatScale() {
		return rootNode.toInfixWithoutNumericScale();
	}

	public double getNumericScale() {
		return rootNode.getNumericScale();
	}

	public String getUnitSymbol() {
		String unitString = rootNode.toSymbol(UnitTextFormat.plain);
		return unitString;
	}

	public String getUnitSymbolUnicode() {
		String unitString = rootNode.toSymbol(UnitTextFormat.unicode);
		return unitString;
	}
	
	public String getUnitSymbolHtml() {
		String unitString = rootNode.toSymbol(UnitTextFormat.html);
		return unitString;
	}
	
	void dump(String prefix){
		rootNode.dump(prefix);
	}

}
