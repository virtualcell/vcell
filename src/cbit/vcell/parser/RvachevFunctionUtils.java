package cbit.vcell.parser;

import java.util.HashSet;

import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;

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
	
	public static Expression[] convertAnalyticGeometryToRvachevFunction(GeometrySpec geoSpec) throws ExpressionException {
		SubVolume[] subVolumes = geoSpec.getSubVolumes();
		int numSubVolumes = subVolumes.length;
		Expression[] newExps = new Expression[numSubVolumes];
		if (numSubVolumes == 1) {
			newExps[0] = new Expression(-1.0);			
		} else {
			for (int i = 0; i < numSubVolumes - 1; i ++) {		
				if (!(subVolumes[i] instanceof AnalyticSubVolume)) {
					throw new RuntimeException("Subdomain " + i + " is not a analytic subdomain.");
				}
				AnalyticSubVolume subvolume = (AnalyticSubVolume)subVolumes[i];
				newExps[i] = convertToRvachevFunction(subvolume.getExpression());
				if (newExps[numSubVolumes - 1] == null) {
					newExps[numSubVolumes - 1] = Expression.negate(newExps[i]);
				} else {
					newExps[numSubVolumes - 1] = Expression.max(newExps[numSubVolumes - 1], Expression.negate(newExps[i]));
				}
			}
			for (int i = 1; i < numSubVolumes - 1; i ++) {
				for (int j = 0; j < i; j ++) {
					newExps[i] = Expression.max(newExps[i], Expression.negate(newExps[j]));
				}			
			}
		}		
		return newExps;
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
