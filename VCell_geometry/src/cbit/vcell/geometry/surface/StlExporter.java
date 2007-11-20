package cbit.vcell.geometry.surface;
import org.vcell.spatial.Polygon;
import org.vcell.spatial.Surface;
import org.vcell.spatial.SurfaceCollection;
import org.vcell.spatial.Triangle;
import org.vcell.spatial.Vect3d;

/**
 * Insert the type's description here.
 * Creation date: (7/19/2004 10:52:10 AM)
 * @author: Jim Schaff
 */
public class StlExporter {
/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 10:54:30 AM)
 * @param geometrySurfaceDescription cbit.vcell.geometry.surface.GeometrySurfaceDescription
 */
public static void writeStl(GeometrySurfaceDescription geometrySurfaceDescription, java.io.Writer writer) throws java.io.IOException {

	GeometricRegion regions[] = geometrySurfaceDescription.getGeometricRegions();
	SurfaceCollection surfaceCollection = geometrySurfaceDescription.getSurfaceCollection();
	//
	// vertices should be all in positive quadrant (no negative coordinates) so have to add offset based on origin
	//
	double ox = geometrySurfaceDescription.getGeometry().getOrigin().getX();
	double oy = geometrySurfaceDescription.getGeometry().getOrigin().getY();
	double oz = geometrySurfaceDescription.getGeometry().getOrigin().getZ();
	
	//
	// for each volume region, collect surfaces and make "solid"
	//
	if (regions==null){
		throw new RuntimeException("Geometric Regions not defined");
	}
	if (surfaceCollection==null){
		throw new RuntimeException("Surfaces not defined");
	}
	for (int i = 0; i < regions.length; i++){
		if (regions[i] instanceof VolumeGeometricRegion){
			writer.write("solid\n");
			VolumeGeometricRegion volRegion = (VolumeGeometricRegion)regions[i];
			//
			// find surfaces that border this region (and invert normals if necessary)
			//
			Vect3d unitNormal = new Vect3d();
			for (int j = 0; j < surfaceCollection.getSurfaceCount(); j++){
				Surface surface = surfaceCollection.getSurfaces(j);
				if (surface.getInteriorRegionIndex() == volRegion.getRegionID()){
					//
					// need Counter Clockwise for "out" with respect to this volume region, (Already OK)
					//
					for (int k = 0; k < surface.getPolygonCount(); k++){
						Polygon polygon = surface.getPolygons(k);
						if (polygon.getNodeCount()==3){
							polygon.getUnitNormal(unitNormal);
							writer.write("facet normal "+unitNormal.getX()+" "+unitNormal.getY()+" "+unitNormal.getZ()+"\n");
							writer.write("  outer loop\n");
							writer.write("    vertex "+(polygon.getNodes(0).getX()-ox)+" "+(polygon.getNodes(0).getY()-oy)+" "+(polygon.getNodes(0).getZ()-oz)+"\n");
							writer.write("    vertex "+(polygon.getNodes(1).getX()-ox)+" "+(polygon.getNodes(1).getY()-oy)+" "+(polygon.getNodes(1).getZ()-oz)+"\n");
							writer.write("    vertex "+(polygon.getNodes(2).getX()-ox)+" "+(polygon.getNodes(2).getY()-oy)+" "+(polygon.getNodes(2).getZ()-oz)+"\n");
							writer.write("  endloop\n");
							writer.write("endfacet\n");
						}else if (polygon.getNodeCount()==4){
							Polygon triangle = new Triangle(polygon.getNodes(0),polygon.getNodes(1),polygon.getNodes(2));
							triangle.getUnitNormal(unitNormal);
							writer.write("facet normal "+unitNormal.getX()+" "+unitNormal.getY()+" "+unitNormal.getZ()+"\n");
							writer.write("  outer loop\n");
							writer.write("    vertex "+(triangle.getNodes(0).getX()-ox)+" "+(triangle.getNodes(0).getY()-oy)+" "+(triangle.getNodes(0).getZ()-oz)+"\n");
							writer.write("    vertex "+(triangle.getNodes(1).getX()-ox)+" "+(triangle.getNodes(1).getY()-oy)+" "+(triangle.getNodes(1).getZ()-oz)+"\n");
							writer.write("    vertex "+(triangle.getNodes(2).getX()-ox)+" "+(triangle.getNodes(2).getY()-oy)+" "+(triangle.getNodes(2).getZ()-oz)+"\n");
							writer.write("  endloop\n");
							writer.write("endfacet\n");
							triangle = new Triangle(polygon.getNodes(0),polygon.getNodes(2),polygon.getNodes(3));
							triangle.getUnitNormal(unitNormal);
							writer.write("facet normal "+unitNormal.getX()+" "+unitNormal.getY()+" "+unitNormal.getZ()+"\n");
							writer.write("  outer loop\n");
							writer.write("    vertex "+(triangle.getNodes(0).getX()-ox)+" "+(triangle.getNodes(0).getY()-oy)+" "+(triangle.getNodes(0).getZ()-oz)+"\n");
							writer.write("    vertex "+(triangle.getNodes(1).getX()-ox)+" "+(triangle.getNodes(1).getY()-oy)+" "+(triangle.getNodes(1).getZ()-oz)+"\n");
							writer.write("    vertex "+(triangle.getNodes(2).getX()-ox)+" "+(triangle.getNodes(2).getY()-oy)+" "+(triangle.getNodes(2).getZ()-oz)+"\n");
							writer.write("  endloop\n");
							writer.write("endfacet\n");
						}
					}
				} else if (surface.getExteriorRegionIndex() == volRegion.getRegionID()){
					//
					// need Counter Clockwise for "out" with respect to this volume region, (MUST FLIP NORMAL AND RE-ORDER VERTICES)
					//
					for (int k = 0; k < surface.getPolygonCount(); k++){
						Polygon polygon = surface.getPolygons(k);
						if (polygon.getNodeCount()==3){
							polygon.getUnitNormal(unitNormal);
							writer.write("facet normal "+(-unitNormal.getX())+" "+(-unitNormal.getY())+" "+(-unitNormal.getZ())+"\n");
							writer.write("  outer loop\n");
							writer.write("    vertex "+(polygon.getNodes(2).getX()-ox)+" "+(polygon.getNodes(2).getY()-oy)+" "+(polygon.getNodes(2).getZ()-oz)+"\n");
							writer.write("    vertex "+(polygon.getNodes(1).getX()-ox)+" "+(polygon.getNodes(1).getY()-oy)+" "+(polygon.getNodes(1).getZ()-oz)+"\n");
							writer.write("    vertex "+(polygon.getNodes(0).getX()-ox)+" "+(polygon.getNodes(0).getY()-oy)+" "+(polygon.getNodes(0).getZ()-oz)+"\n");
							writer.write("  endloop\n");
							writer.write("endfacet\n");
						}else if (polygon.getNodeCount()==4){
							Polygon triangle = new Triangle(polygon.getNodes(0),polygon.getNodes(1),polygon.getNodes(2));
							triangle.getUnitNormal(unitNormal);
							writer.write("facet normal "+(-unitNormal.getX())+" "+(-unitNormal.getY())+" "+(-unitNormal.getZ())+"\n");
							writer.write("  outer loop\n");
							writer.write("    vertex "+(triangle.getNodes(2).getX()-ox)+" "+(triangle.getNodes(2).getY()-oy)+" "+(triangle.getNodes(2).getZ()-oz)+"\n");
							writer.write("    vertex "+(triangle.getNodes(1).getX()-ox)+" "+(triangle.getNodes(1).getY()-oy)+" "+(triangle.getNodes(1).getZ()-oz)+"\n");
							writer.write("    vertex "+(triangle.getNodes(0).getX()-ox)+" "+(triangle.getNodes(0).getY()-oy)+" "+(triangle.getNodes(0).getZ()-oz)+"\n");
							writer.write("  endloop\n");
							writer.write("endfacet\n");
							triangle = new Triangle(polygon.getNodes(0),polygon.getNodes(2),polygon.getNodes(3));
							triangle.getUnitNormal(unitNormal);
							writer.write("facet normal "+(-unitNormal.getX())+" "+(-unitNormal.getY())+" "+(-unitNormal.getZ())+"\n");
							writer.write("  outer loop\n");
							writer.write("    vertex "+(triangle.getNodes(2).getX()-ox)+" "+(triangle.getNodes(2).getY()-oy)+" "+(triangle.getNodes(2).getZ()-oz)+"\n");
							writer.write("    vertex "+(triangle.getNodes(1).getX()-ox)+" "+(triangle.getNodes(1).getY()-oy)+" "+(triangle.getNodes(1).getZ()-oz)+"\n");
							writer.write("    vertex "+(triangle.getNodes(0).getX()-ox)+" "+(triangle.getNodes(0).getY()-oy)+" "+(triangle.getNodes(0).getZ()-oz)+"\n");
							writer.write("  endloop\n");
							writer.write("endfacet\n");
						}
					}
				}
			}
			writer.write("endsolid\n");
		}
	}
}
}