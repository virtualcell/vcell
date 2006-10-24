package org.vcell.ncbc.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (1/12/2004 1:35:08 AM)
 * @author: Jim Schaff
 */
public class PhysicalModelTest {
/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 1:35:34 AM)
 * @return ncbc.physics.component.PhysicalModel
 */
public static PhysicalModel getExample() {
	try {
		PhysicalModel model = new PhysicalModel();

		VolumeLocation cytosol = new UnresolvedVolumeLocation("cyt");
		SurfaceLocation mem = new UnresolvedSurfaceLocation("pm");
		VolumeLocation extracellular = new UnresolvedVolumeLocation("ec");
		SurfaceLocation ermem = new UnresolvedSurfaceLocation("ermem");
		VolumeLocation erlumen = new UnresolvedVolumeLocation("erlumen");
		
		LumpedCapacitor Cpm = new LumpedCapacitor("Cpm",mem);
		LumpedCapacitor Cer = new LumpedCapacitor("Cer",ermem);
		Connection conn1 = new Connection(Cpm.getNegativeConnector(),Cer.getPositiveConnector());

		
		model.setLocations(new Location[] { erlumen, ermem, cytosol, mem, extracellular });
		model.setDevices(new Device[] { Cpm, Cer });
		model.setConnections(new Connection[] { conn1 });
		
		return model;
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		PhysicalModel model = getExample();
		System.out.println(model);
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
	// Insert code to start the application here.
}
}