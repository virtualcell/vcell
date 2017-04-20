package org.vcell.vis.chombo;

import java.util.ArrayList;

import org.vcell.util.DimensionalIndex;
import org.vcell.util.ISize;

public class ChomboLevel {
	private ArrayList<ChomboBox> boxes = new ArrayList<ChomboBox>();
	private final ChomboMesh chomboMesh;
	private final int level;
	private final int refinement;
	private Covering covering;
	
	public ChomboLevel(ChomboMesh chomboMesh, int level, int refinement){
		this.chomboMesh = chomboMesh;
		this.level = level;
		this.refinement = refinement;
	}
	
	public String toString(){
		return "MeshLevel@"+hashCode()+": level="+level+", refinement="+refinement+", absRefinement="+getAbsoluteRefinement();
	}

	public ArrayList<ChomboBox> getBoxes() {
		return boxes;
	}

	public ChomboMesh getMesh() {
		return chomboMesh;
	}

	public int getLevel() {
		return level;
	}

	public int getRefinement() {
		return refinement;
	}
	
	public int getNumBoxes(){
		return boxes.size();
	}
	
	public void addBox(ChomboBox chomboBox){
		boxes.add(chomboBox);
	}

	public int getAbsoluteRefinement(){
		
		//
		// this.refinement stores the refinement relative to the previous MeshLevel
		// this method returns this level's refinement with respect to level 0
		//
		int absRefinementThisLevel = 1;
		for (int i=0;i<=level;i++){
			ChomboLevel chomboLevel = chomboMesh.getLevel(i);
			if (chomboLevel.getLevel() <= level){
				absRefinementThisLevel *= chomboLevel.getRefinement();
			}
		}
		return absRefinementThisLevel;
	}
	
// Fei's suggested version
//	public ISize getSize(){
//		ChomboLevel level0 = chomboMesh.getLevel(0);
//		int maxX = 0, maxY = 0, maxZ = 0;
//		for (ChomboBox box: level0.getBoxes())
//		{
//			maxX = Math.max(maxX, box.getMaxX());
//			maxY = Math.max(maxY, box.getMaxY());
//			maxZ = Math.max(maxZ, box.getMaxZ());
//		}
//		int numX_level0 = maxX+1;
//		int numY_level0 = maxY+1;
//
//		int absRefinementThisLevel = getAbsoluteRefinement();
//
//		int numX = numX_level0 * absRefinementThisLevel;
//		int numY = numY_level0 * absRefinementThisLevel;
//		if (chomboMesh.getDimension()==2){
//			return new ISize(numX,numY,1);
//		}else{
//			int numZ_level0 = maxZ+1;
//			int numZ = numZ_level0 * absRefinementThisLevel;
//			return new ISize(numX,numY,numZ);
//		}
//	}
	
	/**
	 * get size based on max box values from level0
	 */
	public ISize getSize(){
		ChomboLevel level0 = chomboMesh.getLevel(0);

		final int absRefinementThisLevel = getAbsoluteRefinement();
		
		BoxMaxes bMaxes = level0.getBoxMaxes(); 
		bMaxes.addToAllDimensions(1); //suggested by fgao 6/8/14
		bMaxes.multiplyAllDimensionsBy(absRefinementThisLevel);

		final int numZ = ( chomboMesh.getDimension() == 2 ) ? 1 : bMaxes.get(BoxMaxes.Z);
		return new ISize(bMaxes.get(BoxMaxes.X), bMaxes.get(BoxMaxes.Y), numZ);
	}

	public BoxMaxes getBoxMaxes( ) {
		BoxMaxes bMaxes = new BoxMaxes(chomboMesh.getDimension() == 3);
		for (ChomboBox b: getBoxes()) {
			bMaxes.read(b);
		}
	
		return bMaxes;
	}
	
	/**
	 * find max values from one or more {@link ChomboBox}es
	 */
	private static class BoxMaxes {
		private final int maxValues[];
		final boolean is3D;
		public final static int X = 0;
		public final static int Y = 1;
		public final static int Z = 2;
		
		public BoxMaxes(boolean is3D) {
			super();
			this.is3D = is3D;
			maxValues = new int[is3D ? 3: 2];
		}
		
		/**
		 * get specified max
		 * @param d should be {@link #X}, {@link #Y}, or {@link #Z}
		 * @return desired max
		 * @throws ArrayIndexOutOfBoundsException if d < 0 or > max value based on {@link #is3D}
		 */
		public int get(int d) {
			return maxValues[d];
		}
		
		public void read(ChomboBox box) {
			maxValues[X] = Math.max(maxValues[X], box.getMaxX());
			maxValues[Y] = Math.max(maxValues[Y], box.getMaxY());
			if (is3D) {
				maxValues[Z] = Math.max(maxValues[Z], box.getMaxZ());
			}
		}
		/**
		 *  add value to all dimensions
		 */
		public void addToAllDimensions(int a) {
			for (int i = 0; i < maxValues.length;i++) {
				maxValues[i] += a;
			}
		}
		/**
		 *  multiply value to all dimensions
		 */
		public void multiplyAllDimensionsBy(int m) {
			for (int i = 0; i < maxValues.length;i++) {
				maxValues[i] *= m;
			}
		}
	}
	
	public static class Covering {
		private final int level;
		private final int[] levelMap;
		private final int[] boxNumberMap;
		private final int[] boxIndexMap;
		
		public Covering(int level, int[] levelMap, int[] boxNumberMap, int[] boxIndexMap) {
			super();
			this.level = level;
			this.levelMap = levelMap;
			this.boxNumberMap = boxNumberMap;
			this.boxIndexMap = boxIndexMap;
		}
		
		public int getLevel(){
			return level;
		}

		public int[] getLevelMap() {
			return levelMap;
		}

		public int[] getBoxNumberMap() {
			return boxNumberMap;
		}

		public int[] getBoxIndexMap() {
			return boxIndexMap;
		}
		
	}
	public Covering getCovering(){
		if (covering==null){
			covering = computeCovering();
		}
		return covering;
	}
	private Covering computeCovering(){
		//
		//      level 0 (absRef=1)  level 1 (absRef=2)           level 2 (absRef=4)
		//      box [(0,3),(0,3)]   box [(2,5),(2,5)]            box [(8,9),(6,9)]
		//      0's --> grid cells  1's --> grid cells           2's --> grid cells
		//
		//      0 0 0 0             0 0 0 0 0 0 0 0        0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		//      0 1 2 0             0 0 0 0 0 0 0 0        0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		//      0 1 2 0             0 0 1 1 1 1 0 0        0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		//      0 0 0 0             0 0 1 1 2 1 0 0        0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		//                          0 0 1 1 2 1 0 0        0 0 0 0 1 1 1 1 1 1 1 1 0 0 0 0
		//                          0 0 1 1 1 1 0 0        0 0 0 0 1 1 1 1 1 1 1 1 0 0 0 0
		//                          0 0 0 0 0 0 0 0        0 0 0 0 1 1 1 1 2 2 1 1 0 0 0 0
		//                          0 0 0 0 0 0 0 0        0 0 0 0 1 1 1 1 2 2 1 1 0 0 0 0
		//                                                 0 0 0 0 1 1 1 1 2 2 1 1 0 0 0 0
		//                                                 0 0 0 0 1 1 1 1 2 2 1 1 0 0 0 0
		//                                                 0 0 0 0 1 1 1 1 1 1 1 1 0 0 0 0
		//                                                 0 0 0 0 1 1 1 1 1 1 1 1 0 0 0 0
		//                                                 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		//                                                 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		//                                                 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		//                                                 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		//
		System.out.println("computeCovering for level "+level);

		//
		// get absolute scale (absolute refinement) and size of this level
		//
		int absRefinementThisLevel = getAbsoluteRefinement();
		ISize size = getSize();
		
		DimensionalIndex dimIndex = new DimensionalIndex(size);

		//
		// 1) allocate a 1-D array sized at this level whose value
		// 2) store the highest level which covers any portion of each element (see comments above)
		//
	    int[] levelMap = new int[size.getXYZ()];
	    int[] boxNumberMap = new int[size.getXYZ()];
	    int[] boxIndexMap = new int[size.getXYZ()];
	    
		//
		// go through each level (starting at level 0) and assign the level id to the map elements covered by each box in that level
		//
	    for (ChomboLevel chomboLevel : chomboMesh.getLevels()){
	    	int absRefinement = chomboLevel.getAbsoluteRefinement();
	    	int boxNumber = 0;
         	// System.out.println("print('level='+str(level.getLevel())+', totalRefinement='+str(absRefinement)+', totalRefinementThisLevel='+str(absRefinementThisLevel))
	    	for (ChomboBox chomboBox : chomboLevel.getBoxes()){
	    		//
	    		// determine the box starting and ending indices with respect to the coordinates of this level (self._level)
	    		//
	    		ChomboBox projectedBox = chomboBox.getProjectedBox(absRefinement,absRefinementThisLevel);
				//
				// assign the level id into the map for those elements which the box covers (higher levels will overwrite lower levels)
				//
	    		int boxIndex = 0;
	    		for (int z=projectedBox.getMinZ();z<=projectedBox.getMaxZ();z++){
		    		for (int y=projectedBox.getMinY();y<=projectedBox.getMaxY();y++){
		    			for (int x=projectedBox.getMinX();x<=projectedBox.getMaxX();x++){
		    				int index = dimIndex.rollup(x,y,z);
		    				levelMap[index] = chomboLevel.getLevel();
		    				boxNumberMap[index] = boxNumber;
		    				boxIndexMap[index] = boxIndex;
		    				boxIndex += 1;
		    			}
	    			}
	    		}
	    		boxNumber += 1;
	    	}
	    }
		//
		// display map as a 2D array
		//
		System.out.println();
		int index = 0;
		for (int y=0;y<size.getY();y++){
			for (int x=0;x<size.getX();x++){
				System.out.print(levelMap[index++]);
			}
			System.out.println();
		}
		System.out.println();

        return new Covering(getLevel(),levelMap,boxNumberMap,boxIndexMap);
	}

}
