package edu.uchc.vcell.expression.internal;


import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
/**
 * Insert the type's description here.
 * Creation date: (12/27/2002 1:37:29 PM)
 * @author: Jim Schaff
 */
public class ExpressionUtils {
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 3:41:17 PM)
 * @return int
 * @param node cbit.vcell.parser.SimpleNode
 */
private static SimpleNode createNode(java.util.Random random, boolean bIsConstraint) {
	final int AddNode = 0;
	final int AndNode = 1;
	final int FloatNode = 2;
	final int FuncNode = 3;
	final int IDNode = 4;
	final int InvertNode = 5;
	final int MinusNode = 6;
	final int MultNode = 7;
	final int NotNode = 8;
	final int OrNode = 9;
	final int PowerNode = 10;
	final int RelationalNode = 11;

	final double nodeProbabilityNormal[] = {
		0.5,	// ADD
		0.25,	// AND
		0.5,	// FLOAT
		10.0,	// FUNCTION
		0.2,	// ID
		1.0,	// INVERT
		1.0,	// MINUS
		2.0,	// MULT
		0.3,	// NOT
		0.3,	// OR
		1,		// POWER
		0.3,	// RELATIONAL
	};
	final double nodeProbabilityConstraint[] = {
		2.0,	// ADD
		0.0,	// AND
		2.0,	// FLOAT
		1.0,	// FUNCTION
		1.0,	// ID
		1.0,	// INVERT
		1.0,	// MINUS
		2.0,	// MULT
		0.0,	// NOT
		0.0,	// OR
		1,		// POWER
		0.0,	// RELATIONAL
	};
		
	final int[] functionIDs = {
		ASTFuncNode.ABS,
		ASTFuncNode.ACOS,
		ASTFuncNode.ASIN,
		ASTFuncNode.ATAN,
		ASTFuncNode.ATAN2,
		ASTFuncNode.COS,
		ASTFuncNode.EXP,
		ASTFuncNode.LOG,
		ASTFuncNode.MAX,
		ASTFuncNode.MIN,
		ASTFuncNode.POW,
		ASTFuncNode.SIN,
		ASTFuncNode.SQRT,
		ASTFuncNode.TAN,
		ASTFuncNode.CEIL,
		ASTFuncNode.FLOOR,
		ASTFuncNode.CSC,
		ASTFuncNode.COT,
		ASTFuncNode.SEC,
		ASTFuncNode.ACSC,
		ASTFuncNode.ACOT,
		ASTFuncNode.ASEC,
		ASTFuncNode.SINH,
		ASTFuncNode.COSH,
		ASTFuncNode.TANH,
		ASTFuncNode.CSCH,
		ASTFuncNode.COTH,
		ASTFuncNode.SECH,
		ASTFuncNode.ASINH,
		ASTFuncNode.ACOSH,
		ASTFuncNode.ATANH,
		ASTFuncNode.ACSCH,
		ASTFuncNode.ACOTH,
		ASTFuncNode.ASECH,
		ASTFuncNode.FACTORIAL
	};
	double nodeProbability[] = null;
	if (bIsConstraint){
		nodeProbability = nodeProbabilityConstraint;
	}else{
		nodeProbability = nodeProbabilityNormal;
	}
	double totalProb = 0;
	for (int i = 0; i < nodeProbability.length; i++){
		totalProb += nodeProbability[i];
	}
	double f = random.nextDouble()*totalProb;
	int nodeChoice = -1;
	double cumulativeProb = 0;
	for (int i = 0; i < nodeProbability.length; i++){
		cumulativeProb += nodeProbability[i];
		if (f < cumulativeProb){
			nodeChoice = i;
			break;
		}
	}
	switch (nodeChoice){
		case AddNode: {
			return new ASTAddNode();
		}
		case AndNode: {
			return new ASTAndNode();
		}
		case FloatNode: {
			if (random.nextFloat() > 0.1){
				return new ASTFloatNode(random.nextDouble());
			}else{
				return new ASTFloatNode(0.0);
			}
		}
		case FuncNode: {
			ASTFuncNode fn = new ASTFuncNode();
			double ftype = random.nextDouble();
			int index = (int)Math.min(functionIDs.length-1,Math.floor(functionIDs.length*ftype));
			fn.setFunction(functionIDs[index]);
			return fn;
		}
		case IDNode: {
			ASTIdNode idNode = new ASTIdNode();
			idNode.name = "id_"+((int)Math.abs(random.nextInt())%10);
			return idNode;
		}
		case InvertNode: {
			return new ASTInvertTermNode();
		}
		case MinusNode: {
			return new ASTMinusTermNode();
		}
		case MultNode: {
			return new ASTMultNode();
		}
		case NotNode: {
			return new ASTNotNode();
		}
		case OrNode: {
			return new ASTOrNode();
		}
		case PowerNode: {
			return new ASTPowerNode();
		}
		case RelationalNode: {
			ASTRelationalNode relNode = new ASTRelationalNode();
			double rtype = random.nextDouble();
			final String ops[] = {
				">",
				"<",
				"<=",
				">=",
				"==",
				"!="
			};
			relNode.setOperationFromToken(ops[(int)Math.floor(rtype*6)]);
			return relNode;
		}
		default: {
			throw new RuntimeException("f = "+f+", couldn't create a node");
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 4:23:34 PM)
 * @return cbit.vcell.parser.SimpleNode
 */
private static SimpleNode createTerminalNode(java.util.Random random, boolean bIsConstraint) {
	double choice = random.nextDouble();
	double percentIdentifiers = (bIsConstraint)?(0.8):(0.5);

	if (choice < percentIdentifiers){
		ASTIdNode idNode = new ASTIdNode();
		idNode.name = "id_"+((int)Math.abs(random.nextInt())%10);
		return idNode;
	}else if (choice > 0.95){
		return new ASTFloatNode(0.0);
	}else{
		return new ASTFloatNode(random.nextDouble());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 5:02:53 PM)
 * @return cbit.vcell.parser.Expression
 */
public static IExpression generateExpression(java.util.Random random, int maxDepth, boolean bIsConstraint) throws ExpressionException {
	SimpleNode node = generateSubtree(0,maxDepth,random,bIsConstraint);
	if (bIsConstraint){
		node = (SimpleNode)node.copyTreeBinary();
	}
	return ExpressionFactory.createExpression(node.infixString(node.LANGUAGE_DEFAULT,node.NAMESCOPE_DEFAULT));
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 3:16:19 PM)
 * @return cbit.vcell.parser.Expression
 * @param seed long
 */
private static SimpleNode generateSubtree(int depth, int maxDepth, java.util.Random random, boolean bIsConstraint) {
	SimpleNode newNode = null;
	if (depth == 0 && bIsConstraint){
		ASTRelationalNode relNode = new ASTRelationalNode();
		double rtype = random.nextDouble();
		final String ops[] = {
			">",
			"<",
			"<=",
			">=",
			"==",
			//"!="
		};
		relNode.setOperationFromToken(ops[(int)Math.floor(rtype*ops.length)]);
		newNode = relNode;
	}else if (depth >= maxDepth){
		newNode = createTerminalNode(random,bIsConstraint);
	}else{
		newNode = createNode(random,bIsConstraint);
	}
	int numChildren = getNumberOfChildren(newNode);
	for (int i = 0; i < numChildren; i++){
		if (newNode instanceof ASTMultNode && i>0){  // InvertTerm is only ok if it's a second or later child of a mult node
			newNode.jjtAddChild(generateSubtree(depth+1,maxDepth,random,bIsConstraint));
		}else{
			SimpleNode child = null;
			while ((child = generateSubtree(depth+1,maxDepth,random,bIsConstraint)) instanceof ASTInvertTermNode){
			}
			newNode.jjtAddChild(child);
		}
	}
	return newNode;
}
/**
 * Insert the method's description here.
 * Creation date: (12/22/2002 3:41:17 PM)
 * @return int
 * @param node cbit.vcell.parser.SimpleNode
 */
private static int getNumberOfChildren(SimpleNode node) {
	if (node instanceof ASTAddNode){
		return 3;
	}else if (node instanceof ASTAndNode){
		return 2;
	}else if (node instanceof ASTFloatNode){
		return 0;
	}else if (node instanceof ASTFuncNode){
		ASTFuncNode fn = (ASTFuncNode)node;
		switch (fn.getFunction()){
			case ASTFuncNode.ABS: {
				return 1;
			}
			case ASTFuncNode.ACOS: {
				return 1;
			}
			case ASTFuncNode.ASIN: {
				return 1;
			}
			case ASTFuncNode.ATAN: {
				return 1;
			}
			case ASTFuncNode.ATAN2: {
				return 2;
			}
			case ASTFuncNode.COS: {
				return 1;
			}
			case ASTFuncNode.EXP: {
				return 1;
			}
			case ASTFuncNode.LOG: {
				return 1;
			}
			case ASTFuncNode.MAX: {
				return 2;
			}
			case ASTFuncNode.MIN: {
				return 2;
			}
			case ASTFuncNode.POW: {
				return 2;
			}
			case ASTFuncNode.SIN: {
				return 1;
			}
			case ASTFuncNode.SQRT: {
				return 1;
			}
			case ASTFuncNode.TAN: {
				return 1;
			}
			case ASTFuncNode.CEIL: {
				return 1;
			}
			case ASTFuncNode.FLOOR: {
				return 1;
			} 			
			case ASTFuncNode.CSC: {
				return 1;
			}
			case ASTFuncNode.COT: {
				return 1;
			}
			case ASTFuncNode.SEC: {
				return 1;
			}
			case ASTFuncNode.ACSC: {
				return 1;
			}
			case ASTFuncNode.ACOT: {
				return 1;
			}
			case ASTFuncNode.ASEC: {
				return 1;
			}
			case ASTFuncNode.SINH: {
				return 1;
			}
			case ASTFuncNode.COSH: {
				return 1;
			}
			case ASTFuncNode.TANH: {
				return 1;
			}
			case ASTFuncNode.CSCH: {
				return 1;
			}
			case ASTFuncNode.COTH: {
				return 1;
			}
			case ASTFuncNode.SECH: {
				return 1;
			}
			case ASTFuncNode.ASINH: {
				return 1;
			}
			case ASTFuncNode.ACOSH: {
				return 1;
			}
			case ASTFuncNode.ATANH: {
				return 1;
			}
			case ASTFuncNode.ACSCH: {
				return 1;
			}
			case ASTFuncNode.ACOTH: {
				return 1;
			}
			case ASTFuncNode.ASECH: {
				return 1;
			}
			case ASTFuncNode.FACTORIAL: {
				return 1;
			}			
			default:{
				throw new RuntimeException("unknown function type : "+fn.getFunction());
			}
		}
	}else if (node instanceof ASTIdNode){
		return 0;
	}else if (node instanceof ASTInvertTermNode){
		return 1;
	}else if (node instanceof ASTMinusTermNode){
		return 1;
	}else if (node instanceof ASTMultNode){
		return 3;
	}else if (node instanceof ASTNotNode){
		return 1;
	}else if (node instanceof ASTOrNode){
		return 2;
	}else if (node instanceof ASTPowerNode){
		return 2;
	}else if (node instanceof ASTRelationalNode){
		return 2;
	}else{
		throw new RuntimeException("unknown node type");
	}
}
}
