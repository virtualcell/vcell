package cbit.vcell.geometry.surface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import org.vcell.util.VCAssert;

import cbit.vcell.geometry.concept.PolygonImmutable;
import cbit.vcell.geometry.concept.ThreeSpacePoint;

/**
 * provide symmetric tessellation of sphere. Produces a unit sphere centered at 0,0,0
 * translated from C# implementation http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
 * used by permission (MIT license)
 */
public class IcoSphere {
	private static final double T_CONSTANT = (1 + Math.sqrt(5)) / 2;
	
	private final List<List<PolygonImmutable>> trianglesList;
	private final Map<NodePair,ThreeSpacePoint> middleMap;
	private static WeakReference<IcoSphere> ref = new WeakReference<>(null);
	
	public static IcoSphere get( ) {
		IcoSphere current = ref.get();
		if (current != null) {
			return current;
		}
		current = new IcoSphere();
		ref = new WeakReference<IcoSphere>(current);
		return current;
	}

	/**
	 * Define sphere
	 */
	private IcoSphere() {
		super();
		trianglesList = new ArrayList<>();
		middleMap = new HashMap<>( );
	} 

	/**
	 * get tessellation to specified degree of refinement
	 * @param level 0 is coarsest (20-sided), each level provides 4X previous level of triangles
	 * @return unmodifiable list of triangles
	 */
	public List<PolygonImmutable> getTessellation(int level) {
		final int maxBuild = trianglesList.size( ) - 1;
		if (level > maxBuild) {
			for (int i = maxBuild + 1; i <=level; i++) {
				build(i);
			}
		}
		return Collections.unmodifiableList(trianglesList.get(level));
	}
	
	
	

	/**
	 * build a level
	 * @param level non-negative
	 */
	private void build(int level) {
		VCAssert.assertTrue(level >= 0, "non-negative");
		VCAssert.assertTrue(trianglesList.size( ) == level,"wrong level");
		if (level == 0) {
			trianglesList.add(0,icosahedron());
			return;
		}
		List<PolygonImmutable> prior = trianglesList.get(level - 1);
		List<PolygonImmutable> refined = refine(prior);
		trianglesList.add(level,refined);
	}

	private List<PolygonImmutable> icosahedron( ) {
		double vertexTable[][] = { 
				{ -1,  T_CONSTANT, 0}, //0
				{  1,  T_CONSTANT, 0}, //1
				{ -1, -T_CONSTANT, 0}, //2
				{  1, -T_CONSTANT, 0}, //3

				{  0, -1,  T_CONSTANT}, //4
				{  0,  1,  T_CONSTANT}, //5
				{  0, -1, -T_CONSTANT}, //6
				{  0,  1, -T_CONSTANT}, //7

				{  T_CONSTANT, 0, -1 },
				{  T_CONSTANT, 0,  1 },
				{ -T_CONSTANT, 0, -1 },
				{ -T_CONSTANT, 0,  1 },
		};

		List<ThreeSpacePoint> nodes = new ArrayList<>( );
		for (int n = 0 ; n < vertexTable.length; n++) {
			nodes.add(n, unitNode(vertexTable[n][0],vertexTable[n][1],vertexTable[n][2]));
		}

		int triTable[][] = {
				{0, 11, 5},
				{0, 5, 1},
				{0, 1, 7},
				{0, 7, 10},
				{0, 10, 11},

				{1, 5, 9},
				{5, 11, 4},
				{11, 10, 2},
				{10, 7, 6},
				{7, 1, 8},

				{3, 9, 4},
				{3, 4, 2},
				{3, 2, 6},
				{3, 6, 8},
				{3, 8, 9},

				{4, 9, 5},
				{2, 4, 11},
				{6, 2, 10},
				{8, 6, 7},
				{9, 8, 1},
		};
		ArrayList<PolygonImmutable> rval = new ArrayList<>(triTable.length);
		for (int t = 0 ; t < triTable.length; t++) {
			ThreeSpacePoint a = nodes.get(triTable[t][0]);
			ThreeSpacePoint b = nodes.get(triTable[t][1]);
			ThreeSpacePoint c = nodes.get(triTable[t][2]);
			PolygonImmutable tri = new PolygonImmutable(a,b,c);
			rval.add(tri);
		}
		return rval;
	}
	
	/**
	 * move point that is unit distance away from origin
	 * @param x
	 * @param y
	 * @param z
	 * @return new Node, distance 1 from origin
	 */
	private ThreeSpacePoint unitNode(double x, double y, double z) {
		double length = Math.sqrt(x * x + y * y + z * z);
		x /= length;
		y /= length;
		z /= length;
		return new Node(x,y,z);
	}
	
	private ThreeSpacePoint middleOf(ThreeSpacePoint v1, ThreeSpacePoint v2) {
		NodePair np = new NodePair(v1, v2);
		ThreeSpacePoint n = middleMap.get(np);
		if (n != null) {
			return n;
		}
		double x = average(v1,v2,ThreeSpacePoint::getX);
		double y = average(v1,v2,ThreeSpacePoint::getY);
		double z = average(v1,v2,ThreeSpacePoint::getZ);
		ThreeSpacePoint mid = unitNode(x,y,z);
		middleMap.put(np, mid);
		return mid;
	}
	
	/**
	 * refine tessellation by breaking each triangle into 4 
	 * @param in
	 * @return new list
	 */
	List<PolygonImmutable> refine(List<PolygonImmutable> in) {
		ArrayList<PolygonImmutable> rval = new ArrayList<>(in.size( ) * 4);
		for (PolygonImmutable t: in) {
			ThreeSpacePoint v1 = t.getNodes(0);
			ThreeSpacePoint v2 = t.getNodes(1);
			ThreeSpacePoint v3 = t.getNodes(2);
			ThreeSpacePoint a = middleOf(v1,v2);
			ThreeSpacePoint  b = middleOf(v2,v3);
			ThreeSpacePoint c = middleOf(v3,v1);
			rval.add(new PolygonImmutable(v1,a,c));
			lastTriEdge(rval);
			rval.add(new PolygonImmutable(v2,b,a));
			lastTriEdge(rval);
			rval.add(new PolygonImmutable(v3,c,b));
			lastTriEdge(rval);
			rval.add(new PolygonImmutable(a,b,c));
			lastTriEdge(rval);
		}
		return rval;
	}

	void lastTriEdge(List<PolygonImmutable> list) {
		
	}
		/*
		int i = list.size() - 1;
		PolygonImmutable t = list.get(i);
		double[] el = edgeLengths(t);
		System.out.println(Arrays.toString(el));
	}
	
	
	double[] edgeLengths(PolygonImmutable t) {
		double rval[] = new double[3];
		Node v1 = t.getNodes(0);
		Node v2 = t.getNodes(1);
		Node v3 = t.getNodes(2);
		rval[0] = v1.distance(v2);
		rval[1] = v1.distance(v3);
		rval[2] = v3.distance(v2);
		return rval;
	}
		*/
	
	/**
	 * take average using specified accessor
	 * @param a
	 * @param b
	 * @param accessor
	 * @return average of a.accessor( ) and b.accessor( ) and
	 */
	public static double average(ThreeSpacePoint a, ThreeSpacePoint b, ToDoubleFunction<ThreeSpacePoint> accessor) {
		return (accessor.applyAsDouble(a) + accessor.applyAsDouble(b) ) / 2;
	}
	
	
	/**
	 * class to store pairs of nodes, treating
	 * both permutations as the same 
	 */
	private static class NodePair {
		final ThreeSpacePoint one;
		final ThreeSpacePoint other;
		NodePair(ThreeSpacePoint v1, ThreeSpacePoint v2) {
			int cmp =(delta(v1,v2,ThreeSpacePoint::getX));
			if (cmp == 0) {
				cmp =(delta(v1,v2,ThreeSpacePoint::getY));
			}
			if (cmp == 0) {
				cmp =(delta(v1,v2,ThreeSpacePoint::getZ));
			}
			if (cmp > 0) {
			    one = v1;
				other = v2;
			}
			else {
			    one = v2;
				other = v1;
			}
		}
		
		@Override
		public int hashCode() {
			return one.hashCode() ^ other.hashCode(); 
		}


		@Override
		public boolean equals(Object obj) {
			if (obj ==null || !(obj instanceof NodePair)) {
				return false;
				
			}
			NodePair rhs = (NodePair) obj;
			return one.equals(rhs.one)  && other.equals(rhs.other);
		}


		/**
		 * compare values using specified method function
		 * @param v1
		 * @param v2
		 * @param comp
		 * @return -1, 0, or 1
		 */
		private static int delta(ThreeSpacePoint v1, ThreeSpacePoint v2,ToDoubleFunction<ThreeSpacePoint> comp) {
			double da = comp.applyAsDouble(v1);
			double db = comp.applyAsDouble(v2);
			return Double.compare(da,db);
		}
	}

}
