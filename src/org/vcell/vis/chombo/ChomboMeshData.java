package org.vcell.vis.chombo;

import java.util.ArrayList;
import java.util.List;

import org.vcell.vis.vismesh.VisPolygon;
import org.vcell.vis.vismesh.VisPolyhedron;


public class ChomboMeshData {
	public final static String BUILTIN_VAR_BOXLEVEL = "level";
	public final static String BUILTIN_VAR_BOXNUMBER = "boxnumber";
	public final static String BUILTIN_VAR_BOXINDEX = "boxindex";
	public final static String BUILTIN_VAR_BOX = "box";
	private final static int BOX_LEVEL_GAIN = 50;
	
	private final ChomboMesh chomboMesh;
	private ArrayList<ChomboLevelData> chomboLevelDatas = new ArrayList<ChomboLevelData>();
	private ArrayList<String> componentNamesList = new ArrayList<String>();
	private ArrayList<String> builtinNamesList = new ArrayList<String>();
	private List<VCellSolution> vcellSolutionList = new ArrayList<VCellSolution>();
	
	public ChomboMeshData(ChomboMesh chomboMesh){
		this.chomboMesh = chomboMesh;
		this.builtinNamesList.add(BUILTIN_VAR_BOXLEVEL);
		this.builtinNamesList.add(BUILTIN_VAR_BOX);
		this.builtinNamesList.add(BUILTIN_VAR_BOXNUMBER);
		this.builtinNamesList.add(BUILTIN_VAR_BOXINDEX);
	}
	
	public ChomboMesh getMesh(){
		return chomboMesh;
	}
	
	public void addLevelData(ChomboLevelData chomboLevelData){
		chomboLevelDatas.add(chomboLevelData);
	}
	
	public ChomboLevelData getLevelData(int level){
		return chomboLevelDatas.get(level);
	}

	public void addComponentName(String componentName){
		componentNamesList.add(componentName);
	}
	
	public String[] getAllNames(){
		ArrayList<String> allNames = new ArrayList<String>();
		allNames.addAll(builtinNamesList);
		allNames.addAll(componentNamesList);
		return allNames.toArray(new String[0]);
	}
	
	public String[] getComponentNames(){
		return componentNamesList.toArray(new String[0]);
	}
	
	public String[] getBuiltinNames(){
		return builtinNamesList.toArray(new String[0]);
	}
	
	private int getComponentIndex(String name) {
		for (int i=0;i<componentNamesList.size();i++){
			if (componentNamesList.get(i).equals(name)){
				return i;
			}
		}
		throw new RuntimeException("name "+name+" not found in component list");
	}

	public double getTime() {
		return chomboMesh.getTime();
	}

	public double[] getPolygonData(String var, List<VisPolygon> polygons) {
		double[] cellData = new double[polygons.size()];
		if (builtinNamesList.contains(var)){
			if (var.equals(BUILTIN_VAR_BOXINDEX)){
				int i = 0;
				for (VisPolygon visPolygon : polygons){
					cellData[i] = visPolygon.getBoxIndex();
					i++;
				}
			}else if (var.equals(BUILTIN_VAR_BOXLEVEL)){
				int i = 0;
				for (VisPolygon visPolygon : polygons){
					cellData[i] = visPolygon.getLevel();
					i++;
				}
			}else if (var.equals(BUILTIN_VAR_BOXNUMBER)){
				int i = 0;
				for (VisPolygon visPolygon : polygons){
					cellData[i] = visPolygon.getBoxNumber();
					i++;
				}
			}else if (var.equals(BUILTIN_VAR_BOX)){
				int i = 0;
				for (VisPolygon visPolygon : polygons){
					cellData[i] = visPolygon.getLevel()*BOX_LEVEL_GAIN + visPolygon.getBoxNumber();
					i++;
				}
			}else{
				throw new RuntimeException("built-in variable "+var+" not yet implemented");
			}
		}else if (componentNamesList.contains(var)){
			int component = getComponentIndex(var);
			int i = 0;
			for (VisPolygon visPolygon : polygons){
				int levelIndex = visPolygon.getLevel();
				int boxNumber = visPolygon.getBoxNumber();
				int boxIndex = visPolygon.getBoxIndex();
				ChomboLevelData chomboLevelData = getLevelData(levelIndex);
				int boxOffset = chomboLevelData.getOffsets()[boxNumber];
				int boxSize = chomboMesh.getLevel(levelIndex).getBoxes().get(boxNumber).getSize();
				double value = chomboLevelData.getData()[boxOffset+(component*boxSize) + boxIndex];
				cellData[i] = value;
				i++;
			}
		}
		return cellData;
	}

	public double[] getPolyhedaData(String var, List<VisPolyhedron> polyhedra) {
		double[] cellData = new double[polyhedra.size()];
		if (builtinNamesList.contains(var)){
			if (var.equals(BUILTIN_VAR_BOXINDEX)){
				int i = 0;
				for (VisPolyhedron visVoxel : polyhedra){
					cellData[i] = visVoxel.getBoxIndex();
					i++;
				}
			}else if (var.equals(BUILTIN_VAR_BOXLEVEL)){
				int i = 0;
				for (VisPolyhedron visVoxel : polyhedra){
					cellData[i] = visVoxel.getLevel();
					i++;
				}
			}else if (var.equals(BUILTIN_VAR_BOXNUMBER)){
				int i = 0;
				for (VisPolyhedron visVoxel : polyhedra){
					cellData[i] = visVoxel.getBoxNumber();
					i++;
				}
			}else if (var.equals(BUILTIN_VAR_BOX)){
				int i = 0;
				for (VisPolyhedron visVoxel : polyhedra){
					cellData[i] = visVoxel.getLevel()*BOX_LEVEL_GAIN + visVoxel.getBoxNumber();
					i++;
				}
			}else{
				throw new RuntimeException("built-in variable "+var+" not yet implemented");
			}
		}else if (componentNamesList.contains(var)){
			int component = getComponentIndex(var);
			int i = 0;
			for (VisPolyhedron visVoxel : polyhedra){
				int levelIndex = visVoxel.getLevel();
				int boxNumber = visVoxel.getBoxNumber();
				int boxIndex = visVoxel.getBoxIndex();
				ChomboLevelData chomboLevelData = getLevelData(levelIndex);
				int boxOffset = chomboLevelData.getOffsets()[boxNumber];
				int boxSize = chomboMesh.getLevel(levelIndex).getBoxes().get(boxNumber).getSize();
				double value = chomboLevelData.getData()[boxOffset+(component*boxSize) + boxIndex];
				cellData[i] = value;
				i++;
			}
		}
		return cellData;
	}

	public void addVCellSolution(VCellSolution vcellSolution){
		vcellSolutionList.add(vcellSolution);
	}
		
	public List<VCellSolution> getVCellSolutions()
	{
		return vcellSolutionList;
	}
}
