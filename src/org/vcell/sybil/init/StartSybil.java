package org.vcell.sybil.init;

/*   StartSybil  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Launches the standalone application. Performs initialization and initial configuration
 */

import org.vcell.sybil.models.specs.SybilSpecs;

public class StartSybil {
	
	public static void main(String[] args) {
		System.out.println(SybilSpecs.longText);
		SybilApplication app = new SybilApplication();
		System.out.println("Starting GUI");
		app.run();
		System.out.println("Launched GUI");
	}

}
	
