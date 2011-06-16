package org.vcell.pathway.commons;

import java.util.HashSet;
import java.util.Set;

public class PathwayEntryModel {

	private HashSet<PathwayEntryObject> pathwayEntryObjects = new HashSet<PathwayEntryObject>();

	public Set<PathwayEntryObject> getPathwayEntryObjects(){
		return pathwayEntryObjects;
	}

	public PathwayEntryObject add(PathwayEntryObject pathwayEntryObject){
		if (pathwayEntryObject==null){
			throw new RuntimeException("added a null object to pathway entry model");
		}
		pathwayEntryObjects.add(pathwayEntryObject);
		return pathwayEntryObject;
	}

	public boolean contains(PathwayEntryObject candidate){
		for (PathwayEntryObject peObject : pathwayEntryObjects){
			if (peObject.getID()!=null){
				if (peObject.getID().equals(candidate.getID())){
					return true;
				}
			}
		}
		return false;
	}
	
	public PathwayEntryObject find(String ID) {
		for (PathwayEntryObject peObject : pathwayEntryObjects){
			if (peObject.getID()!=null){
				if (peObject.getID().equals(ID)){
					return peObject;
				}
			}
		}
		return null;
	}
	
	public String show(boolean bIncludeChildren) {
		StringBuffer stringBuffer = new StringBuffer();
		for (PathwayEntryObject peObject : pathwayEntryObjects){
			if (bIncludeChildren){
				peObject.show(stringBuffer);
			}else{
				stringBuffer.append(peObject.toString()+"\n");
			}
		}
		return stringBuffer.toString();
	}

}
