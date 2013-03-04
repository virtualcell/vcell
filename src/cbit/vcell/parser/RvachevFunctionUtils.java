package cbit.vcell.parser;

import java.util.HashSet;


public class RvachevFunctionUtils {

	public static Expression convertToRvachevFunction(Expression exp) {
		 Expression newExp = new Expression((SimpleNode)exp.getRootNode());
		 SimpleNode rootNode = newExp.getRootNode();
		 rootNode = fixMultAdd(rootNode);
		 if (rootNode instanceof ASTExpression){
			if (rootNode.jjtGetNumChildren() == 1){
				rootNode = (SimpleNode)rootNode.jjtGetChild(0);
				rootNode.jjtSetParent(null);
			}
		 }		 
		 newExp = new Expression(rootNode);
//		 System.out.println(newExp.infix());
		 newExp = newExp.convertToRvachevFunction();
		 return newExp;
	}
	
	// change use of * and + to && and ||
	private static SimpleNode fixMultAdd(SimpleNode rootNode) {
		SimpleNode newRootNode = rootNode;	
		if (rootNode instanceof ASTMultNode || rootNode instanceof ASTAddNode) {
			boolean bHasBooleanChild = false;
			for (int i = 0; i < rootNode.jjtGetNumChildren(); i ++) {
				if (rootNode.jjtGetChild(i).isBoolean()) {
					bHasBooleanChild = true;
					break;
				}
			}
			if (bHasBooleanChild) {
				newRootNode = rootNode instanceof ASTMultNode ? new ASTAndNode() : new ASTOrNode();
				for (int i = 0; i < rootNode.jjtGetNumChildren(); i ++) {
					newRootNode.jjtAddChild(rootNode.jjtGetChild(i));
				}
			}
		}
		SimpleNode node = (SimpleNode)newRootNode.copyTree();
		for (int i = 0; i < newRootNode.jjtGetNumChildren(); i ++) {
			node.jjtAddChild(fixMultAdd((SimpleNode)newRootNode.jjtGetChild(i)), i);
		}
		
		return node;
	}
}
