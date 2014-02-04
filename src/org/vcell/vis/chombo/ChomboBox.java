package org.vcell.vis.chombo;

public class ChomboBox {
	private final ChomboLevel chomboLevel;
	private final int size;
	private final int minX;
	private final int maxX;
	private final int minY;
	private final int maxY;
	private final int minZ;
	private final int maxZ;
	private final int dimension;
	
	public ChomboBox(ChomboLevel chomboLevel, int minX, int maxX, int minY, int maxY, int minZ, int maxZ, int dimension){
		this.chomboLevel = chomboLevel;
		if (minX>maxX || minY>maxY || minZ>maxZ || minX<0 || minY<0 || minZ<0){
			throw new RuntimeException("min/max x,y,z must all be positive and min<=max for each");
		}
		if (dimension!=2 && dimension!=3){
			throw new RuntimeException("dimension must be either 2 or 3");
		}
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.minZ = minZ;
		this.maxZ = maxZ;
		this.dimension = dimension;
		this.size = (maxX-minX+1)*(maxY-minY+1)*(maxZ-minZ+1);
	}
	
	public ChomboLevel getMeshLevel() {
		return chomboLevel;
	}

	public int getSize() {
		return size;
	}

	public int getMinX() {
		return minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMinZ() {
		return minZ;
	}

	public int getMaxZ() {
		return maxZ;
	}
	
	public String toString() {
		return "Box@"+hashCode()+": level="+chomboLevel.getLevel()+", ("+minX+","+minY+","+minZ+") to ("+maxX+","+maxY+","+maxZ+")";
	}

	public ChomboBox getProjectedBox(int fromRefinement, int toRefinement){
		//
        // create a new box which covers the same area on a different level (identified by the refinement scaling)
        //
        int newMinX = minX * toRefinement / fromRefinement;
        int newMaxX = (maxX + 1) * toRefinement / fromRefinement - 1;
        int newMinY = minY * toRefinement / fromRefinement;
        int newMaxY = (maxY + 1) * toRefinement / fromRefinement - 1;
              //  print('boxSize orig (xmin,xmax,ymin,ymax) = ('+str(box.getMinX())+','+str(box.getMaxX())+','+str(box.getMinY())+','+str(box.getMaxY())+')')
              //  print('boxSize adjusted (xmin,xmax,ymin,ymax) = ('+str(startX)+','+str(endX)+','+str(startY)+','+str(endY)+')')
        if (dimension==2){
        	return new ChomboBox(chomboLevel,newMinX,newMaxX,newMinY,newMaxY,minZ,maxZ,dimension);
        }else{
            int newMinZ = minZ * toRefinement / fromRefinement;
            int newMaxZ = (maxZ + 1) * toRefinement / fromRefinement - 1;
            return new ChomboBox(chomboLevel,newMinX,newMaxX,newMinY,newMaxY,newMinZ,newMaxZ,dimension);
        }
	}
}
