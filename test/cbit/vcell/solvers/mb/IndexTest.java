package cbit.vcell.solvers.mb;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.vcell.vis.core.Vect3D;

/**
 * test {@link PointIndexTreeAndList} implementation
 */
public class IndexTest {
	private static final double VALUES[] = {
			0.3954988527,
			0.7682944498,
			0.1318576462,
			0.4197189663,
			0.5720694098,
			0.5135650638,
			0.9398043554,
			0.7520614140,
			0.5140384946,
			0.1650380409,
			0.3111336429,
			0.4890404069,
			0.4268722905,
			0.1832015690,
			0.1067081606,
			0.1224340876,
			0.4381405249,
			0.1403430430,
			0.2109347381,
			0.4482176848,
			0.7427636554,
			0.5474382703,
			0.8761897520,
			0.1328541271,
			0.4810696728,
			0.4880577203,
			0.0738330247,
			0.3409806124,
			0.6025127344,
			0.9826325992
	};

	private ArrayList<Vect3D> raw = new ArrayList<>( );


	public IndexTest() {
		for (int i = 0; i < VALUES.length - 2; i+=3) {
			Vect3D v = new Vect3D(VALUES[i], VALUES[i+1] ,VALUES[i+2]);
			raw.add(v);
		}
	}

	@Test
	public void simple( ) {
		PointIndexTreeAndList pitl = new PointIndexTreeAndList();
		Map<Vect3D, Integer> check = new HashMap<Vect3D, Integer>( );
		for (Vect3D v : raw) {
			Vect3Didx iv = pitl.index(v.x, v.y,v.z);
			check.put(v, iv.index);
		}
		PointIndex pi = pitl;

		for (Vect3D v : raw) {
			int ci = check.get(v);
			Vect3Didx iv = pitl.index(v.x, v.y,v.z);
			assertTrue(ci == iv.index);
			Vect3Didx rb = pi.lookup(ci);
			assertTrue(iv == rb);

		}
	}

}
