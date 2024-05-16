
package cbit.vcell.solvers.mb;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

@Disabled
@Tag("Fast")
public class MovingBoundaryResultTest {
    private static String fname  = "nformat2.h5";
	MovingBoundaryReader mbr;

	@BeforeAll
	public static void setup() {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
		NativeLib.HDF5.load();
	}
    public MovingBoundaryResultTest() {
		Logger lg = LogManager.getLogger("ncsa");
		mbr = new MovingBoundaryReader(fname);
		mbr.testquery();
	}

    @Test
    public void mesh( ) {
    	MeshInfo mi = mbr.getMeshInfo();
    	System.out.println(mi);
    }
    @Test
    public void ti( ) {
    	TimeInfo ti = mbr.getTimeInfo();
    	System.out.println(ti);
    }
    @Test
    public void tp( ) {
    	Plane p = mbr.getPlane(0);
    	sample(p);
    	Plane p2 = mbr.getPlane(1);
    	sample(p2);
    	showLiveElements(p2);
    }

    @Test
    public void membrane( ) {
    	int[] bnd = mbr.getBoundaryIndexes(0);
    	System.out.println(Arrays.toString(bnd));
    }

    @Test
    public void listPoints( ) {
    	/**
    	 * Points are not indexed until retrieved, so get same data first
    	 */
    	mbr.getBoundaryIndexes(0);
    	mbr.getPlane(0);

    	PointIndex pi = mbr.getPointIndex();
    	for (int i = 0; i < pi.size(); i++) {
    		Vect3Didx v3 = pi.lookup(i);
    		System.out.format("%4d, %f, %f\n", i,v3.x,v3.y);
    	}

    }

    private void show(MovingBoundaryTypes.Element e) {
    	final char space = ' ';
    	System.out.print(e);
    	System.out.print(space);
    	for (Species sp : e.species) {
	    	System.out.print(sp);
	    	System.out.print(space);
    	}
    	System.out.println( );
    }

    private void sample(Plane p) {
    	show( p.get(2,2) );
    	show( p.get(2,3) );
    	show( p.get(2,4) );
    	show( p.get(4,6) );
    }

    private void showLiveElements(Plane p) {
    	for (int x = 0 ; x < p.getSizeX(); x++)
	    	for (int y = 0 ; y < p.getSizeY(); y++) {
	    		Element e = p.get(x, y);
	    		if (e.position != Element.Position.OUTSIDE) {
			    	System.out.format("%2d %2d %-55.55s Boundary indexes: ",x,y,e);
			    	System.out.println("\t" + Arrays.toString(e.boundary()));
			    	System.out.print("\t");
			    	e.species.stream().forEach( (s) -> System.out.print(s + " "));
			    	System.out.println( );
	    		}

	    	}
    }

}