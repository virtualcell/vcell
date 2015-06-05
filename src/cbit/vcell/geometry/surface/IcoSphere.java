package cbit.vcell.geometry.surface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.vcell.util.ProgrammingException;

/**
 * provide symmetric tessellation of sphere
 * translated from C# implementation http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
 */
public class IcoSphere {
	final Node center;
	final double radius;
	private List<List<Triangle>> trianglesList;
	private static final double T_CONSTANT = (1 + Math.sqrt(5)) / 2;
	
	/**
	 * Define sphere
	 * @param center not null
	 * @param radius
	 */
	IcoSphere(Node center, double radius) {
		super();
		Objects.requireNonNull(center);
		this.center = center;
		this.radius = radius;
		trianglesList = new ArrayList<>();
		
	} 
	
	/**
	 * get tesselation to specified degree of refinement
	 * @param level 0 is coarsest (20-sided), each level provides 4X previous level of triangles
	 * @return unmodifiable list of triangles
	 */
	public List<Triangle> getTessellation(int level) {
		if (level >= trianglesList.size( )) {
			build(level);
		}
		return Collections.unmodifiableList(trianglesList.get(level));
	}
	
	private void build(int level) {
		if (level == 0) {
			trianglesList.add(0,icosahedron());
			return;
		}
		throw new ProgrammingException("in progress");
	}
	
	private List<Triangle> icosahedron( ) {
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
		
		List<Node> nodes = new ArrayList<>( );
		for (int n = 0 ; n < vertexTable.length; n++) {
			nodes.add(n, new Node(vertexTable[n][0],vertexTable[n][1],vertexTable[n][2]));
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
		ArrayList<Triangle> rval = new ArrayList<>(triTable.length);
		for (int t = 0 ; t < triTable.length; t++) {
			Node a = nodes.get(triTable[t][0]);
			Node b = nodes.get(triTable[t][1]);
			Node c = nodes.get(triTable[t][2]);
			Triangle tri = new Triangle(a,b,c);
			rval.add(tri);
		}
		return rval;
		}
}
