/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.util.graph;
/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:00:38 PM)
 * @author: Jim Schaff
 */
public class NodeInfo {
	private int color = 0;
	public final static int WHITE = 0;
	public final static int GREY = 1;
	public final static int BLACK = 2;
	private int discoverTime = -1;
	private int finishTime = -1;
	private int predecessorIndex = -1;
	private int nodeIndex = -1;

	public static class FinishTimeComparator implements java.util.Comparator {
		public int compare(Object obj1, Object obj2){
			NodeInfo n1 = (NodeInfo)obj1;
			NodeInfo n2 = (NodeInfo)obj2;
			if (n1.getFinishTime()<n2.getFinishTime()){
				return -1;
			}else if (n1.getFinishTime()>n2.getFinishTime()){
				return 1;
			}else{
				return 0;
			}
		}
	};

/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:01:53 PM)
 */
public NodeInfo(int argNodeIndex){
	nodeIndex = argNodeIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:02:45 PM)
 * @return int
 */
public int getColor() {
	return color;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:03:49 PM)
 * @return java.lang.String
 */
public String getColorName() {
	if (color == WHITE){
		return "white";
	}else if (color == GREY){
		return "grey";
	}else if (color == BLACK){
		return "black";
	}else{
		throw new RuntimeException("unexpected color "+color);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:02:45 PM)
 * @return int
 */
public int getDiscoverTime() {
	return discoverTime;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:02:45 PM)
 * @return int
 */
public int getFinishTime() {
	return finishTime;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:43:59 PM)
 * @return int
 */
public int getNodeIndex() {
	return nodeIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:02:45 PM)
 * @return int
 */
public int getPredecessorIndex() {
	return predecessorIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:02:45 PM)
 * @param newColor int
 */
public void setColor(int newColor) {
	color = newColor;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:02:45 PM)
 * @param newVisitTime int
 */
public void setDiscoverTime(int newDiscoverTime) {
	discoverTime = newDiscoverTime;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:02:45 PM)
 * @param newFinishTime int
 */
public void setFinishTime(int newFinishTime) {
	finishTime = newFinishTime;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:02:45 PM)
 * @param newPredecessorIndex int
 */
public void setPredecessorIndex(int newPredecessorIndex) {
	predecessorIndex = newPredecessorIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:03:08 PM)
 * @return java.lang.String
 */
public String toString() {
	return "NodeInfo["+nodeIndex+"]{"+getColorName()+"["+getDiscoverTime()+"/"+getFinishTime()+"] predecessor = "+predecessorIndex+"}";
}
}
