package org.vcell.smoldyn.simulationsettings.util;

import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * Smoldyn uses Filehandles to output data.  Filehandles may refer to stdout or stderr, or to an actual file on the file system.
 * A filehandle is associated with a path on the filesystem.  Three names shall have special meaning: stdout, stderr, and stdin.
 * 
 * @author mfenwick
 *
 */
public class Filehandle {

	private String path;
	
	/**
	 * @param path
	 * @throws NullPointerException if path is null
	 */
	public Filehandle(String path) {
		SimulationUtilities.checkForNull("filehandle path", path);
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
}
