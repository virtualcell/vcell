package org.vcell.ncbc.physics.engine;
import ucar.units.Unit;
import cbit.vcell.model.Model;
import java.beans.PropertyVetoException;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Feature;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.geometry.GeometryException;
import cbit.image.ImageException;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.Geometry;
import cbit.image.VCImage;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.modelapp.FeatureMapping;
import cbit.vcell.modelapp.MembraneMapping;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.SimulationContextTest;
import cbit.vcell.modelapp.StructureMapping;

import java.util.Vector;

import org.vcell.ncbc.physics.component.Connection;
import org.vcell.ncbc.physics.component.CurrentSource;
import org.vcell.ncbc.physics.component.ElectricalDevice;
import org.vcell.ncbc.physics.component.Location;
import org.vcell.ncbc.physics.component.LumpedCapacitor;
import org.vcell.ncbc.physics.component.MembraneSpecies;
import org.vcell.ncbc.physics.component.PhysicalModel;
import org.vcell.ncbc.physics.component.ResolvedSurfaceLocation;
import org.vcell.ncbc.physics.component.SurfaceElectricalMaterial;
import org.vcell.ncbc.physics.component.SurfaceLocation;
import org.vcell.ncbc.physics.component.UnresolvedSurfaceLocation;
import org.vcell.ncbc.physics.component.UnresolvedVolumeLocation;
import org.vcell.ncbc.physics.component.VolumeElectricalMaterial;
import org.vcell.ncbc.physics.component.VolumeLocation;
import org.vcell.ncbc.physics.component.VolumeReaction;
import org.vcell.ncbc.physics.component.VolumeSpecies;
/**
 * Insert the type's description here.
 * Creation date: (1/12/2004 1:35:08 AM)
 * @author: Jim Schaff
 */
public class MappingUtilitiesTest {
/**
 * Insert the method's description here.
 * Creation date: (1/9/2006 8:11:18 PM)
 * @return ncbc_old.physics.component.PhysicalModel
 */
public static PhysicalModel getExample() {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 3:25:48 PM)
 * @param args java.lang.String[]
 */

public static void main(String[] args) {
	try {
		SimulationContext simContext = SimulationContextTest.getExampleElectrical(3);
		org.vcell.ncbc.physics.component.PhysicalModel physicalModel = MappingUtilities.createFromSimulationContext(simContext);
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}
}