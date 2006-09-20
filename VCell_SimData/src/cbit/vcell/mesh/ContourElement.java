package cbit.vcell.mesh;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.Coordinate;
/**
 * This type was created in VisualAge.
 */
public class ContourElement implements java.io.Serializable, cbit.util.Matchable {
	private int contourIndex = NULL_INDEX;
	private int volumeIndex = NULL_INDEX;
	private Coordinate begin = null;
	private Coordinate end = null;
	private int neighborPrev = NULL_INDEX;
	private int neighborNext = NULL_INDEX;

	public static final int NULL_INDEX = -1;
/**
 * MembraneElement constructor comment.
 */
public ContourElement(int argIndex, int argVolIndex, Coordinate argBegin, Coordinate argEnd, int argPrevNeighbor, int argNextNeighbor) {
	this.contourIndex = argIndex;
	this.volumeIndex = argVolIndex;
	this.begin = argBegin;
	this.end = argEnd;
	this.neighborPrev = argPrevNeighbor;
	this.neighborNext = argNextNeighbor;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/01 1:53:16 PM)
 * @return boolean
 * @param object cbit.util.Matchable
 */
public boolean compareEqual(cbit.util.Matchable object) {
	if (this == object){
		return true;
	}
	if (!(object instanceof ContourElement)){
		return false;
	}
	ContourElement contourElement = (ContourElement)object;

	if (contourIndex != contourElement.contourIndex){
		return false;
	}
	if (volumeIndex != contourElement.volumeIndex){
		return false;
	}
	if (this.neighborPrev != contourElement.neighborPrev){
		return false;
	}
	if (this.neighborNext != contourElement.neighborNext){
		return false;
	}
	if (!cbit.util.Compare.isEqualOrNull(begin,contourElement.begin)){
		return false;
	}
	if (!cbit.util.Compare.isEqualOrNull(end,contourElement.end)){
		return false;
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 12:42:26 PM)
 * @return cbit.vcell.geometry.Coordinate
 */
public Coordinate getBeginCoordinate() {
	return this.begin;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 12:42:58 PM)
 * @return int
 */
public int getContourIndex() {
	return this.contourIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 12:42:26 PM)
 * @return cbit.vcell.geometry.Coordinate
 */
public Coordinate getEndCoordinate() {
	return this.end;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 12:44:32 PM)
 * @return int
 */
public int getNextNeighborIndex() {
	return this.neighborNext;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 12:44:32 PM)
 * @return int
 */
public int getPrevNeighborIndex() {
	return this.neighborPrev;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 12:42:58 PM)
 * @return int
 */
public int getVolumeIndex() {
	return this.volumeIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2000 4:40:35 PM)
 * @return boolean
 */
public boolean isBegin() {
	return getPrevNeighborIndex() == -1;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2000 4:40:35 PM)
 * @return boolean
 */
public boolean isEnd() {
	return getNextNeighborIndex() == -1;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 10:38:09 AM)
 * @return java.lang.String
 */
public String toString() {
	return	getContourIndex()+" "+
			getVolumeIndex()+" "+
			begin.getX()+" "+
			begin.getY()+" "+
			begin.getZ()+" "+
			end.getX()+" "+
			end.getY()+" "+
			end.getZ()+" "+
			getPrevNeighborIndex()+" "+
			getNextNeighborIndex();
}
}
