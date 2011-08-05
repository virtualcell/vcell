/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.cellml;
/**
 * This class takes of names and tokens used when trnasfroming a VCML -> CELLML.
 * Creation date: (4/12/2002 11:14:10 AM)
 * @author: Daniel Lucio
 */
public class NameManager {
	private java.util.HashMap specieTable = null;
	//this set is to store unique names when creating unique names!
	//the first field is for the unique name, and the second field is to store the component in the Cell->VCML translation
	private java.util.HashMap uniqueNameMap = null;
	
	//Internal class to store the required information
	public class MyStructure {
		public String mangledName;
		public String compName;
		public String destVarName;

		public MyStructure(String name){
			super();
			mangledName = name;
			compName = null;
			destVarName = null;
		}
		
		public String toString(){
			//return "("+(compName!=null?compName:"null")+","+(destVarName!=null?destVarName:"null")+","+(mangledName!=null?mangledName:"null")+")";
			//This method SHOULD NOT BE MODIFIED! Is used for the VCML -> CELLML translation
			return mangledName!=null?mangledName:"null";
		}
	};

/**
 * NameManager constructor comment.
 */
public NameManager() {
	super();
	specieTable = new java.util.HashMap();
	uniqueNameMap = new java.util.HashMap();
}


/**
 * This method will create just an input of a pair firstKey secondKey.
 * Creation date: (4/15/2002 2:21:36 PM)
 * @param firstKey java.lang.String
 * @param secondKey java.lang.String
 * @param mangledName java.lang.String
 */
public void add(String firstKey, String secondKey) {
	this.add(firstKey, secondKey, null);
}


/**
 * This method will relate the pair firstKey-secondKey to the given mangledName entry.
 * Creation date: (4/15/2002 2:21:36 PM)
 * @param firstKey java.lang.String
 * @param secondKey java.lang.String
 * @param mangledName java.lang.String
 */
public void add(String firstKey, String secondKey, String paramMangledName) {
	if (firstKey==null||secondKey==null) {
		throw new IllegalArgumentException("Received invalid 'null' keys");
	}
	if (firstKey.length()==0 || secondKey.length()==0) {
		throw new IllegalArgumentException("Illegal empties keys!");
	}
	
	//check FIRST that the this specie has an entry on the table!
	if (!specieTable.containsKey(firstKey)){
		this.put(firstKey);
	}
	//add a new entry related to the given specie
	java.util.HashMap prop = (java.util.HashMap)specieTable.get(firstKey);
	MyStructure tempstruct = new MyStructure(paramMangledName);
	 
	prop.put(secondKey, tempstruct);
}


/**
 * This method will connect the two given entries.
 * Creation date: (4/18/2002 11:27:40 AM)
 * @return boolean
 * @param firstKey java.lang.String
 * @param firstSubKey java.lang.String
 * @param secondKey java.lang.String
 * @param secondSubKey java.lang.String
 */
public boolean connect(String firstKey, String firstSubKey, String secondKey, String secondSubKey) {
	if (specieTable.containsKey(firstKey)) {
		java.util.HashMap prop = (java.util.HashMap)specieTable.get(firstKey);

		if (prop.containsKey(firstSubKey)) {
			MyStructure struc = (MyStructure)prop.get(firstSubKey);
			struc.compName = secondKey;
			struc.destVarName = secondSubKey;

			return true;
		}
	}
	
	return false;
}


/**
 * This method checks if the given firstKey is in the table.
 * Creation date: (4/12/2002 2:29:46 PM)
 * @return boolean
 * @param firstKey java.lang.String
 */
public boolean containsKey(String firstKey) {
	return specieTable.containsKey(firstKey);
}


	 //a method for debugging, prints out the contents of this class. 
	 public String dumpNames() {

		 StringBuffer buf = new StringBuffer();

		 buf.append("List of all Names:\n");
		 java.util.Iterator i = specieTable.keySet().iterator();
		 while (i.hasNext()) {
			String firstKey = (String)i.next();
			java.util.HashMap prop = (java.util.HashMap)specieTable.get(firstKey);
			java.util.Iterator j = prop.keySet().iterator();
			while (j.hasNext()) {
				String secondKey = (String)j.next();
				MyStructure struct = (MyStructure)prop.get(secondKey);
				buf.append("firstKey:" + firstKey + " secondKey: " + secondKey + " " + struct.compName + " ");
				buf.append(struct.destVarName + " " + struct.mangledName);
				buf.append("\n");
			}
		 }	 
		 buf.append("Unique Names:\n");
		 i = uniqueNameMap.keySet().iterator();
		 while (i.hasNext()) {
			String key = (String)i.next();
			String value = (String)uniqueNameMap.get(key);
			buf.append(key + " " + value);
			buf.append("\n");
		 }
		 
		 return buf.toString();
	 }


/**
 * This method will parse all the tables and generate the manglednames.
 * Creation date: (4/18/2002 3:21:01 PM)
 */
public void generateMangledNames() {
	//get all the firstKeys
	java.util.Iterator firstIter = ((java.util.HashMap)specieTable.clone()).keySet().iterator();

	while (firstIter.hasNext()) {
		String firstKey = (String)firstIter.next();

		//get second table
		java.util.HashMap prop = (java.util.HashMap)specieTable.get(firstKey);
		java.util.Iterator secondIter = prop.keySet().iterator();
		
		while (secondIter.hasNext()) {
			String secondKey = (String)secondIter.next();
			MyStructure struc = (MyStructure)prop.get(secondKey);

			if (struc.compName== null&&struc.destVarName==null) {
				//if there are no information, then calculate a new unique mangled name
				//Check that the name does not start with a number
				if (Character.isDigit(secondKey.charAt(0))) {
					secondKey = "_"+secondKey;
				}
				//initialize	
				int i =0;
				// String temp = secondKey;
				String temp = firstKey + "." + secondKey;
				
				while (uniqueNameMap.containsKey(temp)) {
					//if the previous name already exists, try to find a unique name
					i++;
					temp = secondKey+i;
				}
				//assign (associate)the mangled name with the original name!!!
				struc.mangledName = temp;

				//create an entry on the unique name table!
				uniqueNameMap.put(temp, firstKey);
				//add(temp);
				if (!specieTable.containsKey(temp)) {
					add(temp, firstKey, secondKey);					
				}
			}
		}
	}
}


/**
 * This method is intented to be used in the script CELLML2VCML.xslt , this method implements another way to get a unique name.
 * Creation date: (8/21/2002 4:32:11 PM)
 * @return java.lang.String
 * @param sourceName java.lang.String
 */
public String generateUnique(String sourceName) {
	String result = sourceName;
	int i = 1;

	//iterate while you can not find a unique name
	while ( specieTable.containsKey("unique_"+result) ) {
		i++;
		result=sourceName+i;
	}
	
	//add the new unique name!!!
	specieTable.put("unique_"+result,null);
	
	return result;
}


/**
 * This method will return the first mangled name associated to the given firstKey.
 * Creation date: (4/18/2002 10:42:10 AM)
 * @return java.lang.String
 * @param firstKey java.lang.String
 */
public String getFirstMangledName(String firstKey) {
	String result = "";
	java.util.Iterator iter = getIterator(firstKey);

	if (iter.hasNext()) {
		result = ((MyStructure)iter.next()).mangledName;
	}

	return result;
}


/**
 * This method will return the first entry related to the given first Key.
 * this method is used in the CELLML -> VCML translation
 * Creation date: (4/18/2002 10:41:38 AM)
 * @return java.lang.String
 * @param firstKey java.lang.String
 */
public String getFirstSecondKey(String firstKey) {
/*	String result = "";
	java.util.HashMap prop = (java.util.HashMap)specieTable.get(firstKey);
	
	if (prop !=null) {
		java.util.Iterator iter = prop.keySet().iterator();

		if (iter.hasNext()) {
			Object firstObject = iter.next();
			result= (String)firstObject;
		}
	}*/
	//try to get the related component to this firstkey
	Object result = uniqueNameMap.get(firstKey);
	if (result != null) {
		return (String)result;
	}
	//if it is empty return an empty string
	return "";
}


/**
 * This methos returns an iterator that points to all the manglednames associeated to the given firstKey.
 * Creation date: (4/16/2002 5:19:08 PM)
 * @return java.util.Iterator
 * @param firstKey java.lang.String
 */
public java.util.Iterator getIterator(String firstKey) {
	//Check that there is an entry for this name!
	if (specieTable.containsKey(firstKey)) {
		
		return ((java.util.HashMap)(specieTable.get(firstKey))).values().iterator();
	} else {
		//Return an empty iterator!
		java.util.List temp = new java.util.LinkedList();
		return temp.iterator();
	}
}


/**
 * This method will search for the related mangledName for the given firstKey-secondKey.
 * Creation date: (4/15/2002 4:06:34 PM)
 * @return java.lang.String
 * @param secondKey java.lang.String
 * @param firstKey java.lang.String
 */
public String getMangledName(String firstKey, String secondKey) {
	if (firstKey==null || secondKey==null) {
		throw new IllegalArgumentException("Received illegal 'null' arguments!");
	}
	if (firstKey.length()==0 || secondKey.length()==0) {
		throw new IllegalArgumentException("Received illegal 'empty' arguments!");
	}
	
	String result = "";
	
	if (specieTable.containsKey(firstKey)) {
		java.util.HashMap prop = (java.util.HashMap)specieTable.get(firstKey);
		
		if (prop.containsKey(secondKey)) {
			MyStructure struc = (MyStructure)prop.get(secondKey);
			
			if (struc.mangledName!=null) {
				result = struc.mangledName;
			} else {
				//keep recursing
				result = getMangledName(struc.compName, struc.destVarName);

				//Check that the name does not start with a number
				if (Character.isDigit(result.charAt(0))) {
					result = "_"+result;
				}
			}
		}
	}
	
	return result;
}


/**
 * This method ensures that it will return a unique name within the table using the given base name.
 * Creation date: (4/12/2002 5:44:51 PM)
 * @return java.lang.String
 * @param baseName java.lang.String
 */
public String getUniqueName(String baseName) {
	String result = baseName;
	int i = 1;

	while ( specieTable.containsKey(result) ) {
		i++;
		result=baseName+i;
	}
	
	return result;
}


/**
 * This method adds only the firstKey. It puts an empty table as the argument.
 * Creation date: (4/12/2002 11:24:06 AM)
 * @param firstKey java.lang.String
 */
public void put(String firstKey) {
	if(!specieTable.containsKey(firstKey)) {
		specieTable.put(firstKey, new java.util.HashMap());
	}
}
}
