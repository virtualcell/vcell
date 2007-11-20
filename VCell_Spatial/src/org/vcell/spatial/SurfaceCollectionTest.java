package org.vcell.spatial;

public class SurfaceCollectionTest {
	
public static SurfaceCollection getCubeExample(){
	SurfaceCollection surfaceCollection = new SurfaceCollection();
//	FastSurface surface = new FastSurface(0,1);
	//
	//        Y
	//        |
	//        |
	//        2-----------6
	//       /|          /|
	//      3-----------7 |
	//      | |         | |
	//      | |         | |
	//      | 0---------|-4-----X
	//      |/          |/
	//      1-----------5
	//     /
	//    Z
	//
	Node nodes[] = new Node[] {
			new Node(0,0,0),  // 0
			new Node(0,0,1),  // 1
			new Node(0,1,0),  // 2
			new Node(0,1,1),  // 3
			new Node(1,0,0),  // 4
			new Node(1,0,1),  // 5
			new Node(1,1,0),  // 6
			new Node(1,1,1)   // 7
	};
	
	OrigSurface surface = new OrigSurface(0,1);
	// x-
	surface.addPolygon(new Quadrilateral(nodes[0],nodes[1],nodes[3],nodes[2]));
	// x+
	surface.addPolygon(new Quadrilateral(nodes[6],nodes[7],nodes[5],nodes[4]));
	// y-
	surface.addPolygon(new Quadrilateral(nodes[0],nodes[4],nodes[5],nodes[1]));
	// y+
	surface.addPolygon(new Quadrilateral(nodes[2],nodes[3],nodes[7],nodes[6]));
	// z-
	surface.addPolygon(new Quadrilateral(nodes[0],nodes[2],nodes[6],nodes[4]));
	// z+
	surface.addPolygon(new Quadrilateral(nodes[1],nodes[5],nodes[7],nodes[3]));

	surfaceCollection.addSurface(surface);
	
	return surfaceCollection;
}

}
