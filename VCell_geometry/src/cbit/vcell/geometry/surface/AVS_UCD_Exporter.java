package cbit.vcell.geometry.surface;
import org.vcell.spatial.Node;
import org.vcell.spatial.Polygon;
import org.vcell.spatial.Surface;
import org.vcell.spatial.SurfaceCollection;

/**
 * Insert the type's description here.
 * Creation date: (7/19/2004 10:52:10 AM)
 * @author: Jim Schaff
 */
public class AVS_UCD_Exporter {
/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 10:54:30 AM)
 * @param geometrySurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
public static void writeUCD(GeometrySurfaceDescription geometrySurfaceDescription, java.io.Writer writer) throws java.io.IOException {

	final String QUAD_TYPE = "quad";
	
	GeometricRegion regions[] = geometrySurfaceDescription.getGeometricRegions();
	SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
	Node nodes[] = surfaceCollection.getNodes();
	int numNodes = nodes.length;
	int numCells = 0;
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
		numCells += surfaceCollection.getSurfaces(i).getPolygonCount();
	}
	int numNodeData = 0;
	int numCellData = 0;
	int numModelData = 0;
	writer.write(numNodes+" "+numCells+" "+numNodeData+" "+numCellData+" "+numModelData+"\n");
	for (int i = 0; i < nodes.length; i++){
		writer.write(nodes[i].getGlobalIndex()+" "+nodes[i].getX()+" "+nodes[i].getY()+" "+nodes[i].getZ()+"\n");
	}
	//
	// print the "Cells" (polygons) for each surface (each surface has it's own material id).
	//
	int cellID = 0;
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
		Surface surface = surfaceCollection.getSurfaces(i);
		String materialType = Integer.toString(i); // for material now just give it the index (later need to collect these in terms of closed objects).
		for (int j = 0; j < surface.getPolygonCount(); j++){
			Polygon polygon = surface.getPolygons(j);
			int node0Index = polygon.getNodes(0).getGlobalIndex();
			int node1Index = polygon.getNodes(1).getGlobalIndex();
			int node2Index = polygon.getNodes(2).getGlobalIndex();
			int node3Index = polygon.getNodes(3).getGlobalIndex();
			writer.write(cellID+" "+materialType+" "+QUAD_TYPE+" "+node0Index+" "+node1Index+" "+node2Index+" "+node3Index+"\n");
			cellID++;
		}
	}
}
}
