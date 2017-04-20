package cbit.vcell.export.server;

import org.vcell.util.Compare;

import cbit.image.DisplayPreferences;

public class PLYSpecs extends FormatSpecificSpecs {
	private boolean bIncludeTextures = false;
	private DisplayPreferences[] displayPreferences;
	public PLYSpecs(boolean bIncludeTextures,DisplayPreferences[] displayPreferences){
		this.bIncludeTextures = bIncludeTextures;
		this.displayPreferences = displayPreferences;
	}
	public boolean incluceTextures(){
		return bIncludeTextures;
	}
	public DisplayPreferences[] getDisplayPreferences(){
		return displayPreferences;
	}
	@Override
	public boolean equals(Object object) {
		if(this==object){
			return true;
		}
		if(object instanceof PLYSpecs){
			if(((PLYSpecs)object).incluceTextures() == this.bIncludeTextures){
				if(Compare.isEqual(((PLYSpecs)object).getDisplayPreferences(), this.getDisplayPreferences())){
					return true;	
				}
			}
		}
		return false;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "includeTextures="+incluceTextures()+" displayPrefCnt="+(getDisplayPreferences()==null?0:getDisplayPreferences().length);
	}
}
