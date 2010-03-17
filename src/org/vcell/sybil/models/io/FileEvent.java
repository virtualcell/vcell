package org.vcell.sybil.models.io;

import java.util.HashSet;

/*   FileEvent  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2010
 *   Represents types of file operations
 */

public class FileEvent {
	
	public static class Action {}
	public static final FileEvent.Action FileNew = new Action();
	public static final FileEvent.Action FileInherited = new Action();
	public static final FileEvent.Action FileOpen = new Action();
	public static final FileEvent.Action FileSave = new Action();
	public static final FileEvent.Action FileSaveAs = new Action();
	public static final FileEvent.Action FileImport = new Action();
	public static final FileEvent.Action FileExport = new Action();
	
	public static interface Listener { 
		public void fileEvent(FileEvent event); 
		
		public static class FireSet extends HashSet<Listener> implements Listener {
			private static final long serialVersionUID = -3332983392906750839L;
			public void fileEvent(FileEvent event) {
				for(Listener listener : this) { listener.fileEvent(event); }
			}
		}
	}
	
	protected FileManager fileManager;
	protected FileEvent.Action action;
	
	public FileEvent(FileManager fileMan, FileEvent.Action actionNew) {
		this.fileManager = fileMan;
		action = actionNew;
	}
	
	public FileManager fileManager() { return fileManager; }
	public FileEvent.Action action() { return action; }
	
	public boolean modelHasChanged() {
		return FileImport.equals(action) || FileNew.equals(action) || FileInherited.equals(action) || FileOpen.equals(action);
	}
	
	public boolean thereIsNewData() {
		return FileImport.equals(action) || FileInherited.equals(action) || FileOpen.equals(action);
	}
	
}