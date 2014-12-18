package org.vcell.vmicro.workflow.data;

public enum ModelType {
	DiffOne("Diffusion with One Diffusing Component",0),
	DiffTwoWithPenalty("Diffusion with Two Diffusing Components (with penalty)",1),
	ReactDominant("Reaction Dominant Off Rate",2),
	DiffAndBinding("Diffusion plus Binding",3),
	DiffTwoWithoutPenalty("Diffusion with Two Diffusing Components (without penalty)",4),
	KenworthyUniformDisk2Param("Uniform Disk bleach area: D,amp",5),
	KenworthyUniformDisk3Param("Uniform Disk bleach area: D,amp,bwm",6), 
	KenworthyUniformDisk4Param("Uniform Disk bleach area: D,amp,bwm,timeoffset",7);
	
	public final String description;
	public final int index;
	
	private ModelType(String desc, int index){
		this.description = desc;
		this.index = index;
	}
	
	public static ModelType fromDescription(String desc){
		for (ModelType id : values()){
			if (id.description.equals(desc)){
				return id;
			}
		}
		return null;
	}

	public static ModelType fromIndex(int index){
		for (ModelType id : values()){
			if (id.index == index){
				return id;
			}
		}
		return null;
	}
	
}