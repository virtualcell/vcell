/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
