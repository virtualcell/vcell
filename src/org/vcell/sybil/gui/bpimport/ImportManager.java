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