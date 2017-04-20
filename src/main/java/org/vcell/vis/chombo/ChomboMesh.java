package org.vcell.vis.chombo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.vcell.vis.core.Vect3D;

public class ChomboMesh {

	private int dimension;
	private final ChomboBoundaries chomboBoundaries = new ChomboBoundaries();
//	private double time;
	private Vect3D origin;
	private Vect3D extent;
	private final ArrayList<ChomboLevel> chomboLevels = new ArrayList<ChomboLevel>();
	private Map<String, Integer> featurePhaseMap = new HashMap<String, Integer>();
	
    public ChomboMesh(){
    }

    public int getDimension(){
    	return dimension;
    }

	public ChomboLevel getLevel(int levelIndex) {
		for (ChomboLevel chomboLevel : chomboLevels){
			if (chomboLevel.getLevel() == levelIndex){
				return chomboLevel;
			}
		}
		return null;
	}


	public ChomboBoundaries getBoundaries() {
		return chomboBoundaries;
	}


	public Vect3D getOrigin() {
		return origin;
	}

	public Vect3D getExtent() {
		return extent;
	}

//	public double getTime() {
//		return time;
//	}

	public ArrayList<ChomboLevel> getLevels() {
		return chomboLevels;
	}
	
	public void addLevel(ChomboLevel chomboLevel){
		chomboLevels.add(chomboLevel);
	}
	
	public int getNumLevels(){
		return chomboLevels.size();
	}

	public static ChomboMesh getExampleMesh(){
		ChomboMesh chomboMesh = new ChomboMesh();
		int dimension = 2;
		chomboMesh.setDimension(dimension);
		chomboMesh.setOrigin(new Vect3D(0,0,0));
		chomboMesh.setExtent(new Vect3D(10,10,1));
	    ChomboLevel level0 = new ChomboLevel(chomboMesh,0,1);
	    ChomboLevel level1 = new ChomboLevel(chomboMesh,1,2);
	    ChomboLevel level2 = new ChomboLevel(chomboMesh,2,2);
	    int scale = 1;
	    ChomboBox box0 = new ChomboBox(level0,0*scale,(3+1)*scale-1,0*scale,(3+1)*scale-1,0,0,dimension);
	    ChomboBox box1a = new ChomboBox(level1,2*scale,(3+1)*scale-1,2*scale,(3+1)*scale-1,0,0,dimension);
	    ChomboBox box1b = new ChomboBox(level1,4*scale,(5+1)*scale-1,4*scale,(5+1)*scale-1,0,0,dimension);
	    ChomboBox box2 = new ChomboBox(level2,6*scale,(7+1)*scale-1,6*scale,(7+1)*scale-1,0,0,dimension);
	    chomboMesh.addLevel(level0);
	    chomboMesh.addLevel(level1);
	    chomboMesh.addLevel(level2);
	    level0.addBox(box0);
	    level1.addBox(box1a);
	    level1.addBox(box1b);
	    level2.addBox(box2);
	    return chomboMesh;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public void setOrigin(Vect3D origin) {
		this.origin = origin;
	}

	public void setExtent(Vect3D extent) {
		this.extent = extent;
	}

	public void addFeaturePhase(String feature, int phase)
	{
		featurePhaseMap.put(feature, phase);
	}
	
	public Integer getPhase(String feature) throws RuntimeException
	{
		if (featurePhaseMap.size() == 0)
		{
			return null;
		}
		Integer iphase = featurePhaseMap.get(feature);
		if (iphase == null)
		{
			throw new RuntimeException("phase for feature " + feature + " not found");
		}
		return iphase;
	}
	
//	public void setTime(double time) {
//		this.time = time;
//	}

}
