package cbit.vcell.geometry;

import java.util.ArrayList;

import cbit.vcell.render.Vect3d;

public class CSGSetOperator extends CSGNode {
	
	public static enum OperatorType {
		DIFFERENCE,
		INTERSECTION,
		UNION,
	};
	
	private OperatorType opType = null;

	private ArrayList<CSGNode> children = new ArrayList<CSGNode>();
	
	public CSGSetOperator(String name, OperatorType opType){
		super(name);
		this.opType = opType;
	}
	
	public CSGSetOperator(CSGSetOperator csgSetOperator){
		this(csgSetOperator.getName(), csgSetOperator.opType);
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
		if (children.size() == 0) {
			return false;
		}
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
			if (children.size() == 1) {
				return children.get(0).isInside(point);
			}
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

	public void removeChild(CSGNode csgNode) {
		children.remove(csgNode);
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

	public int indexOf(CSGNode node) {
		for (int i = 0; i < children.size(); i ++) {
			if (node == children.get(i)) {
				return i;
			}
		}
		return -1;
	}
	
	public void setChild(int index, CSGNode node) {
		children.set(index, node);
	}
}
