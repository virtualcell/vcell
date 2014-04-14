/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

import java.util.ArrayList;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
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
			children.add(child.clone());
		}
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!compareEqual0(obj)){
			return false;
		}
		if (!(obj instanceof CSGSetOperator)){
			return false;
		}
		CSGSetOperator csgso = (CSGSetOperator)obj;

		if ((getOpType().compareTo(csgso.getOpType())) != 0){
			return false;
		}

		if (!Compare.isEqualOrNull(children, csgso.children)) {
			return false;
		}

		return true;
	}

	@Override
	public CSGNode clone() {
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
