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
		if (fromRefinement == toRefinement){
			return this;
		}
		double refinementFactor = 1.0*toRefinement/fromRefinement;
		if (fromRefinement > toRefinement){
			//
			// from finer to coarser
			//
	        int newMinX = (int)Math.floor(minX * refinementFactor);
	        int newMaxX = (int)Math.ceil(maxX * refinementFactor)-1;
	        int newMinY = (int)Math.floor(minY * refinementFactor);
	        int newMaxY = (int)Math.ceil(maxY * refinementFactor)-1;
	        if (dimension==2){
	        	return new ChomboBox(chomboLevel,newMinX,newMaxX,newMinY,newMaxY,minZ,maxZ,dimension);
	        }else{
	            int newMinZ = (int)Math.floor(minZ * refinementFactor);
	            int newMaxZ = (int)Math.ceil(maxZ * refinementFactor)-1;
	            return new ChomboBox(chomboLevel,newMinX,newMaxX,newMinY,newMaxY,newMinZ,newMaxZ,dimension);
	        }
		}else{
			//
			// from coarser to finer
			//
	        int newMinX = (int)Math.floor(minX * refinementFactor);
	        int newMaxX = (int)Math.ceil(maxX * refinementFactor)+(((int)refinementFactor)-1);
	        int newMinY = (int)Math.floor(minY * refinementFactor);
	        int newMaxY = (int)Math.ceil(maxY * refinementFactor)+(((int)refinementFactor)-1);
	        if (dimension==2){
	        	return new ChomboBox(chomboLevel,newMinX,newMaxX,newMinY,newMaxY,minZ,maxZ,dimension);
	        }else{
	            int newMinZ = (int)Math.floor(minZ * refinementFactor);
	            int newMaxZ = (int)Math.ceil(maxZ * refinementFactor)+(((int)refinementFactor)-1);
	            return new ChomboBox(chomboLevel,newMinX,newMaxX,newMinY,newMaxY,newMinZ,newMaxZ,dimension);
	        }
		}
	}
}
