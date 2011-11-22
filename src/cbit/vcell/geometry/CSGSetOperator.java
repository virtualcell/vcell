package cbit.vcell.geometry;

import java.util.ArrayList;

import cbit.vcell.render.Vect3d;

public class CSGSetOperator extends CSGNode {
	
	public static enum OperatorType {
		UNION,
		DIFFERENCE,
		INTERSECTION
	};
	
	private OperatorType opType = null;

	private ArrayList<CSGNode> children = new ArrayList<CSGNode>();
	
	public CSGSetOperator(OperatorType opType){
		this.opType = opType;
	}
	
	public CSGSetOperator(CSGSetOperator csgSetOperator){
		this.opType = csgSetOperator.opType;
		for (CSGNode child : csgSetOperator.children){
			children.add(child.cloneTree());
		}
	}
	
	@Override
	public CSGNode cloneTree() {
		return new CSGSetOperator(this);
	}
	
	@Override
	public boolean isInside(Vect3d point) {
		switch (opType){
		case UNION:{
			for (CSGNode child : children){
				if (child.isInside(point)){
					return true;
				}
			}
			return false;
		}
		case DIFFERENCE:{
			if (children.get(0).isInside(point) && !children.get(1).isInside(point)){
				return true;
			}else{
				return false;
			}
			
		}
		case INTERSECTION:{
			for (CSGNode child : children){
				if (!child.isInside(point)){
					return false;
				}
			}
			return true;
		}
		}
		// TODO Auto-generated method stub
		return false;
	}

	public void addChild(CSGNode csgNode) {
		children.add(csgNode);
	}

	
	public OperatorType getOpType() {
		return opType;
	}

	public void setOpType(OperatorType opType) {
		this.opType = opType;
	}

	public ArrayList<CSGNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<CSGNode> children) {
		this.children = children;
	}

	
}
