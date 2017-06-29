package cbit.vcell.parser;

public class RvachevFunctionUtils {

	public static Expression convertToRvachevFunction(Expression exp) throws ExpressionException {
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
	// all factors and addends must be all boolean or non-boolean, not mixed, because we should not change (x>0)*2 to x>0 && 2
	private static SimpleNode fixMultAdd(SimpleNode rootNode) throws ExpressionException {
		boolean bAllBoolean = true;
		SimpleNode newRootNode = rootNode;	
		if (rootNode instanceof ASTMultNode || rootNode instanceof ASTAddNode) {
			boolean bHasBooleanChild = false;
			for (int i = 0; i < rootNode.jjtGetNumChildren(); i ++) {
				if (rootNode.jjtGetChild(i).isBoolean()) {
					bHasBooleanChild = true;
				}
				else
				{
					bAllBoolean = false;
				}
			}
			if (bAllBoolean) {
				newRootNode = rootNode instanceof ASTMultNode ? new ASTAndNode() : new ASTOrNode();
				for (int i = 0; i < rootNode.jjtGetNumChildren(); i ++) {
					newRootNode.jjtAddChild(rootNode.jjtGetChild(i));
				}
			}
			else if (bHasBooleanChild)
			{
				throw new ExpressionException("converting to implicit function does not support mixed boolean and non-boolean expressions in addition or multiplication.\n\nExpression is: \n" + rootNode.infixString(SimpleNode.LANGUAGE_DEFAULT));
			}
		}
		SimpleNode node = (SimpleNode)newRootNode.copyTree();
		for (int i = 0; i < newRootNode.jjtGetNumChildren(); i ++) {
			node.jjtAddChild(fixMultAdd((SimpleNode)newRootNode.jjtGetChild(i)), i);
		}
		
		return node;
	}
}
