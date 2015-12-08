package org.vcell.vis.chombo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vcell.vis.mapping.chombo.ChomboCellIndices;
import org.vcell.vis.mapping.chombo.ChomboVisMembraneIndex;

import cbit.vcell.simdata.SimDataConstants;

public class ChomboMeshData {
	public final static String BUILTIN_VAR_BOXLEVEL = "level";
	public final static String BUILTIN_VAR_BOXNUMBER = "boxnumber";
	public final static String BUILTIN_VAR_BOXINDEX = "boxindex";
	public final static String BUILTIN_VAR_BOX = "box";
	private final static int BOX_LEVEL_GAIN = 50;
	public final static String BUILTIN_VAR_MEMBRANE_INDEX = "membraneIndex";
	
	private final ChomboMesh chomboMesh;
	private ArrayList<ChomboLevelData> chomboLevelDatas = new ArrayList<ChomboLevelData>();
	private ArrayList<String> componentNamesList = new ArrayList<String>();
	private ArrayList<String> builtinNamesList = new ArrayList<String>();
	private List<ChomboMembraneVarData> membraneDataList = new ArrayList<ChomboMembraneVarData>();
	private final double time;
	private int fraction0ComponentIndex = 0;
	
	public ChomboMeshData(ChomboMesh chomboMesh, double time){
		this.chomboMesh = chomboMesh;
		this.builtinNamesList.add(BUILTIN_VAR_BOXLEVEL);
		this.builtinNamesList.add(BUILTIN_VAR_BOX);
		this.builtinNamesList.add(BUILTIN_VAR_BOXNUMBER);
		this.builtinNamesList.add(BUILTIN_VAR_BOXINDEX);
		this.time = time;
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
	public String[] getVolumeDataNames(){
		return componentNamesList.toArray(new String[0]);
	}
	
	// we don't want to show components that are after fraction-0
	public String[] getVisibleVolumeDataNames(){
		String[] names = new String[fraction0ComponentIndex + 1];
		System.arraycopy(getVolumeDataNames(), 0, names, 0, fraction0ComponentIndex + 1);
		return names;
	}
	
	public String[] getVolumeBuiltinNames(){
		return builtinNamesList.toArray(new String[0]);
	}

	private int getVolumeComponentIndex(String name) {
		for (int i=0;i<componentNamesList.size();i++){
			if (componentNamesList.get(i).equals(name)){
				return i;
			}
		}
		throw new RuntimeException("name "+name+" not found in component list");
	}

	public double getTime() {
		return this.time;
	}
		
	public String[] getMembraneDataNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (ChomboMembraneVarData memVarData : membraneDataList){
			names.add(memVarData.getName());
		}
		return names.toArray(new String[names.size()]);
	}

	public String[] getMembraneBuiltinNames() {
		return new String[] { BUILTIN_VAR_MEMBRANE_INDEX };
	}

	public double[] getVolumeCellData(String var, List<? extends ChomboCellIndices> cellIndices) {
		double[] cellData = new double[cellIndices.size()];
		Arrays.fill(cellData, -1);
		if (builtinNamesList.contains(var)){
			if (var.equals(BUILTIN_VAR_BOXINDEX)){
				int i = 0;
				for (ChomboCellIndices cellIndex : cellIndices){
					cellData[i] = cellIndex.getBoxIndex();
					i++;
				}
			}else if (var.equals(BUILTIN_VAR_BOXLEVEL)){
				int i = 0;
				for (ChomboCellIndices cellIndex : cellIndices){
					cellData[i] = cellIndex.getLevel();
					i++;
				}
			}else if (var.equals(BUILTIN_VAR_BOXNUMBER)){
				int i = 0;
				for (ChomboCellIndices cellIndex : cellIndices){
					cellData[i] = cellIndex.getBoxNumber();
					i++;
				}
			}else if (var.equals(BUILTIN_VAR_BOX)){
				int i = 0;
				for (ChomboCellIndices cellIndex : cellIndices){
					cellData[i] = cellIndex.getLevel()*BOX_LEVEL_GAIN + cellIndex.getBoxNumber();
					i++;
				}
			}else{
				throw new RuntimeException("built-in variable "+var+" not yet implemented");
			}
		}else if (componentNamesList.contains(var) || componentNamesList.contains(var+".vol0") || componentNamesList.contains(var+".vol1")){
			int component = -1;
			if (componentNamesList.contains(var)){
				component = getVolumeComponentIndex(var);
			}else if (componentNamesList.contains(var+".vol0")){
				component = getVolumeComponentIndex(var+".vol0");
			}else if (componentNamesList.contains(var+".vol1")){
				component = getVolumeComponentIndex(var+".vol1");
			}
			int i = 0;
			for (ChomboCellIndices cellIndex : cellIndices){
				int levelIndex = cellIndex.getLevel();
				int boxNumber = cellIndex.getBoxNumber();
				int boxIndex = cellIndex.getBoxIndex();
				ChomboLevelData chomboLevelData = getLevelData(levelIndex);
				int boxOffset = chomboLevelData.getOffsets()[boxNumber];
				int boxSize = chomboMesh.getLevel(levelIndex).getBoxes().get(boxNumber).getSize();
				double value = chomboLevelData.getData()[boxOffset+(component*boxSize) + boxIndex];
				cellData[i] = value;
				i++;
			}
		}else{
			throw new RuntimeException("unable to find data for variable "+var);
		}
		return cellData;
	}

	public double[] getMembraneCellData(String var, List<? extends ChomboVisMembraneIndex> cellIndices){
		double[] cellData = new double[cellIndices.size()];
		ChomboMembraneVarData memVarData = null;
		for (ChomboMembraneVarData mvd : this.membraneDataList){
			if (mvd.getName().equals(var)){
				memVarData = mvd;
				break;
			}
		}
		if (memVarData!=null){
			for (int i=0;i<cellData.length;i++){
				int chomboIndex = cellIndices.get(i).getChomboIndex();
				cellData[i] = memVarData.getRawChomboData()[chomboIndex];
				if (cellData[i] == SimDataConstants.BASEFAB_REAL_SETVAL)
				{
					cellData[i] = Double.NaN;
			  }
			}
		}else if (var.equals(BUILTIN_VAR_MEMBRANE_INDEX)){
			for (int i=0;i<cellData.length;i++){
				int chomboIndex = cellIndices.get(i).getChomboIndex();
				cellData[i] = chomboIndex;
			}
		}
		return cellData;
	}

	public void addMembraneVarData(ChomboMembraneVarData membraneVarData){
		membraneDataList.add(membraneVarData);
	}
		
	public List<ChomboMembraneVarData> getMembraneVarData()
	{
		return membraneDataList;
	}

	public void setFraction0ComponentIndex(int fraction0ComponentIndex) {
		this.fraction0ComponentIndex = fraction0ComponentIndex;
	}
}
