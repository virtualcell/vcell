/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.bpimport;

/*   ImportMan  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Collection of data related to data import
 */

import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;

public class ImportManager {
	
	public static interface Searcher {
		public void performSearch(PathwayCommonsRequest request, boolean requestsSmelting);
	}
	
	protected Searcher searcher;
	protected FileManager fileManager;
	
	public ImportManager(Searcher searcherNew, FileManager fileManager) { 
		searcher = searcherNew;
		this.fileManager = fileManager; 
	}
	
	public void performSearch(PathwayCommonsRequest request, boolean requestsSmelting) { 
		searcher.performSearch(request, requestsSmelting); 
	}
	
	public FileManager fileManager() { return fileManager; }
}
