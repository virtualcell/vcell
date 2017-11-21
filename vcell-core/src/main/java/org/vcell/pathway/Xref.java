/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import static org.vcell.pathway.PathwayXMLHelper.urlHashtable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class Xref extends BioPaxObjectImpl implements UtilityClass {
	private String db;
	private String dbVersion;
	private String id;
	private String idVersion;
	
	public String getDb() {
		return db;
	}
	public String getDbVersion() {
		return dbVersion;
	}
	public String getId() {
		return id;
	}
	public String getIdVersion() {
		return idVersion;
	}
	public String getURL(){
		if(db == null) {
			return null;
		}
		String db_id = db;
		String entity_id = id;
		if(db.equals("REACTOME")){
			if(id.contains("REACT")) {
				db_id = "REACTOME_STID";
				entity_id = translateReactomeId(id);
				
			} else {
				db_id = "REACTOME_ID";
			}
		}else if(db.contains("KEGG"))
			db_id = "KEGG";
		else if(db.contains("BIOMODELS"))
			db_id = "BIOMODELS";
		else if(db.contains("CHEBI"))
			db_id = "ChEBI";
		if(urlHashtable.get(db_id) == null){
//			System.err.println(db);
			return null;
		}
		return urlHashtable.get(db_id).replace("%", entity_id);
	}
	
	@SuppressWarnings("finally")
	private File getFileFromURL(String path) {
	    URL url = this.getClass().getClassLoader().getResource(path);
	    File file = null;
	    try {
	        file = new File(url.toURI());
	    } catch (URISyntaxException e) {
	        file = new File(url.getPath());
	    } finally {
	        return file;
	    }
	}
	
	// the reactome web site uses a new format for the entities ID, while we're using the old
	// need to use the old-new map below to translate
	private String translateReactomeId(String from_id) {
//		String fileName = "G:\\dan\\projects\\svn\\vcell_6.1\\resources\\reactome\\reactome_oldnew_idmap.txt";
//		File file = new File(fileName);
		String path = "reactome_oldnew_idmap.txt";
		File file = getFileFromURL(path);
		BufferedReader buffReader = null;
		try {
			FileReader reader = new FileReader(file);
			buffReader = new BufferedReader(reader);
			String line;	// ex: REACT_277613	R-CEL-445072
			while((line = buffReader.readLine()) != null) {
				StringTokenizer tokens = new StringTokenizer(line, " \t");
				String from_candidate = tokens.nextToken();		// old ID
				String to_candidate = tokens.nextToken();		// new ID
				if(from_id.equals("REACT_" + from_candidate)) {
					return to_candidate;
				}
			}
		} catch(IOException e) {
			throw new RuntimeException("Unable to translate Reactome ID: " + from_id);
		} finally {
			try {
				if (buffReader != null) {
					buffReader.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}
		}
		return from_id;	// we don't make a fuss, the browser will probably display a 404 error
	}	public void setDb(String db) {
		this.db = db;
	}
	public void setDbVersion(String dbVersion) {
		this.dbVersion = dbVersion;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setIdVersion(String idVersion) {
		this.idVersion = idVersion;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "db",db,level);
		printString(sb, "dbVersion",dbVersion,level);
		printString(sb, "id",id,level);
		printString(sb, "idVersion",idVersion,level);
	}
	
	// this will parse the reactome map of new -> old IDs and produce a smaller map of old->new IDs
	// Only the entries starting with "REACT_" are taken into consideration
	// location of original file:  https://reactome.org/download/current/reactome_stable_ids.txt
	public static void main(String[] argv)
	{
		FileReader reader = null;
		FileWriter writer = null;
		try {
			// TODO: download the original file from reactome and choose your own locations
			String inFileName = "...reactome_stable_ids.txt";
			String outFileName = "...reactome_oldnew_idmap.txt";
			reader = new FileReader(inFileName);
			writer = new FileWriter(outFileName);
			String newLine = System.getProperty("line.separator");
			Set<String> keys = new HashSet<>();

			BufferedReader buffReader = new BufferedReader(reader);
			String inLine;	// ex: R-CEL-445072	R-SSC-3323186,R-SSC-8855579,REACT_257848,REACT_277613
			while((inLine = buffReader.readLine()) != null) {
				if(inLine.startsWith("#")) {
					continue;
				}
				if(!inLine.contains("REACT_")) {
					continue;
				}
				StringTokenizer tokens = new StringTokenizer(inLine, "\t");
				String new_id = tokens.nextToken();				// new ID
				String old_ids = tokens.nextToken();		// may be comma separated old IDs
				
				StringTokenizer tokenCandidates = new StringTokenizer(old_ids, " ,");
				while (tokenCandidates.hasMoreTokens()) {
					String candidate = tokenCandidates.nextToken();
					if(candidate.startsWith("REACT_")) {
						String str = candidate.substring("REACT_".length());
						writer.write(str + "\t" +new_id + newLine);
//						if(keys.add(candidate) == false) {	// may be multiple variants (human, fly, mouse, worm)
//							System.out.println("Already found " + candidate);
//						}
					}
				}
			}
		} catch (Throwable e) {
			System.out.println("Uncaught exception in reactome old-new conversion main()");
			e.printStackTrace(System.out);
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
