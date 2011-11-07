/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.PathwayModel;

public class PathwayGrouping {
	
	// get the neighbor of a given groupObject
	public HashSet<BioPaxObject> computeNeighbors(PathwayModel pathwayModel, BioPaxObject bioPaxObject){
		Map<BioPaxObject, HashSet<BioPaxObject>> parentMap = pathwayModel.refreshParentMap();
		HashSet<BioPaxObject> neighbors_temp = new HashSet<BioPaxObject>();
		HashSet<BioPaxObject> components = new HashSet<BioPaxObject> ();
		if(bioPaxObject instanceof GroupObject){
			components = ((GroupObject)bioPaxObject).getGroupedObjects();
			for(BioPaxObject bpObject : components){
				if(bpObject instanceof GroupObject){
					// compute neighbors for group object
					neighbors_temp.addAll(computeNeighbors(pathwayModel, parentMap,(GroupObject)bpObject));
				}else{
					if(parentMap.get(bpObject) != null){
						neighbors_temp.addAll(parentMap.get(bpObject));
					}
					neighbors_temp.addAll(pathwayModel.getBioPaxElements(bpObject));
				}
			}
		}else{
			neighbors_temp.addAll(pathwayModel.getBioPaxElements(bioPaxObject));
			if(parentMap.get(bioPaxObject) != null){
				neighbors_temp.addAll(parentMap.get(bioPaxObject));
			}
			
		}
		Map<BioPaxObject, BioPaxObject> groupMap = pathwayModel.getGroupMap();
		HashSet<BioPaxObject> neighbors = new HashSet<BioPaxObject>();
		for(BioPaxObject bpo : neighbors_temp){
			neighbors.add(findGroupAncestor(groupMap, bpo));
		}
		neighbors.removeAll(components);
		neighbors.remove(null);
		neighbors.remove(bioPaxObject);
		HashSet<BioPaxObject> notImported = new HashSet<BioPaxObject>();
		for(BioPaxObject bpo : neighbors){
			if(!pathwayModel.getBiopaxObjects().contains(bpo)){
				notImported.add(bpo);
			}
		}
		neighbors.remove(notImported);
		return neighbors;
	}

	private HashSet<BioPaxObject> computeNeighbors(PathwayModel pathwayModel, Map<BioPaxObject, HashSet<BioPaxObject>> parentMap, GroupObject groupObject){
		HashSet<BioPaxObject> neighbors = new HashSet<BioPaxObject>();
		for(BioPaxObject bpObject : groupObject.getGroupedObjects()){
			if(bpObject instanceof GroupObject){
				// recursively call the computerNeighbors() function to get neighbors of a groupObject
				neighbors.addAll(computeNeighbors(pathwayModel, parentMap,(GroupObject)bpObject));
			}else{
				// use parentMap to find neighbors 
				if(parentMap.get(bpObject) != null){
					neighbors.addAll(parentMap.get(bpObject));
				}
				neighbors.addAll(pathwayModel.getBioPaxElements(bpObject));
			}
		}
		return neighbors;
	}
	
	// create a group object in pathway model
	public GroupObject createGroupObject(PathwayModel pathwayModel, ArrayList<String> names, String id, HashSet<BioPaxObject> bpObjects, GroupObject.Type newType){
		HashSet<BioPaxObject> groupable = updateGroupableList(pathwayModel, bpObjects);
		if(groupable.size() <= 1) return null;
		GroupObject gObject = new GroupObject();
		gObject.setName(names);
		gObject.setID(id);
		gObject.setGroupedeObjects(groupable);
		gObject.setType(newType);
		return gObject;
	}
	
	private HashSet<BioPaxObject> updateGroupableList(PathwayModel pathwayModel, HashSet<BioPaxObject> bpObjects){
		HashSet<BioPaxObject> groupable = new HashSet<BioPaxObject>();
		Map<BioPaxObject, BioPaxObject> groupMap = pathwayModel.getGroupMap();
		// the group object should be a tree structure. so we will group the parents of the selected objects together.
		for(BioPaxObject bpObject : bpObjects){
			groupable.add(findGroupAncestor(groupMap, bpObject));
		}
		return groupable;
	}
	
	
	public BioPaxObject findGroupAncestor(Map<BioPaxObject, BioPaxObject> groupMap, BioPaxObject bpObject){
		BioPaxObject ancestor = groupMap.get(bpObject);
		if(ancestor == null) return bpObject;
		else{
			BioPaxObject temp = groupMap.get(ancestor);
			while (temp != null){
				ancestor = temp;
				temp = groupMap.get(ancestor);
			}
			return ancestor;
		}
	}

	// get a list of group objects in a pathway model
	public Set<BioPaxObject> updateGroupObjectList(PathwayModel pathwayModel){
		Set<BioPaxObject> groupList = new HashSet<BioPaxObject>();
		Set<BioPaxObject> originalList = pathwayModel.getBiopaxObjects();
		for(BioPaxObject bpObject : originalList){
			if(bpObject instanceof GroupObject){
				groupList.add(bpObject);
			}
		}
		return groupList;
	}
	
	// get a list of independent biopax Objects including groupObjects
	public Set<BioPaxObject> updateBioPaxObjectList(PathwayModel pathwayModel){
		Set<BioPaxObject> clonedOriginalList = new HashSet<BioPaxObject>(pathwayModel.getBiopaxObjects());
		Set<BioPaxObject> removedList = new HashSet<BioPaxObject> ();
		for(BioPaxObject bpObject : clonedOriginalList){
			if(bpObject instanceof GroupObject){
				GroupObject gObject = (GroupObject)bpObject;
				for(BioPaxObject bpo : gObject.getGroupedObjects()){
					removedList.add(bpo);
				}
			}
		}
		clonedOriginalList.removeAll(removedList);
		return clonedOriginalList;
	} 
	
	// generate auto-ID for groupObject
	public String groupIdGenerator(PathwayModel pathwayModel){
		String groupId = "GROUP_";
		int idx = 0;
		for(BioPaxObject bpo : updateGroupObjectList(pathwayModel)){
			int i = Integer.parseInt(bpo.getID().substring(6));
			if(idx < i){
				idx = i;
			}
		}
		groupId += (idx+1);
		return groupId;
	}
	

}
