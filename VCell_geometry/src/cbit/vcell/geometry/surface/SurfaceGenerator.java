package cbit.vcell.geometry.surface;
import cbit.util.SessionLog;
import cbit.vcell.geometry.RegionImage;
import cbit.image.VCImage;
import cbit.vcell.geometry.Geometry;
import cbit.render.*;
import cbit.render.objects.BoundingBox;
import cbit.render.objects.BoundingIndex;
import cbit.render.objects.Polygon;
import cbit.render.objects.Quadrilateral;
import cbit.render.objects.SurfaceCollection;
/**
 * Insert the type's description here.
 * Creation date: (6/27/2003 10:34:49 PM)
 * @author: John Wagner
 */
public class SurfaceGenerator {
	private SessionLog fieldSessionLog = null;

/**
 * BoundaryGenerator constructor comment.
 */
public SurfaceGenerator(SessionLog sessionLog) {
	super();
	fieldSessionLog = sessionLog;
}


// Returns an array of doubles of dimension 3*surfaceCollection.getNodeCount()
// laid out like X0, Y0, Z0, X1, Y1, Z1, ..., Xn, Yn, Zn.
public static double[] calculateLaplacian(SurfaceCollection surfaceCollection) {
	double[] delta = new double[3*surfaceCollection.getNodeCount()];
	int[] weights = new int[surfaceCollection.getNodeCount()];
	for (int i = 0; i < surfaceCollection.getNodeCount(); i++) {
		delta[3*i] = delta[3*i+1] = delta[3*i+2] = 0.0;
		weights[i] = 0;
	}
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
		Surface surface = surfaceCollection.getSurfaces(i);
		for (int j = 0; j < surface.getPolygonCount(); j++) {
			Polygon polygon = surface.getPolygons(j);
			for (int k = 0; k < polygon.getNodeCount(); k++) {
				Node nodeA = polygon.getNodes(k);
				Node nodeB = polygon.getNodes((k + 1) % polygon.getNodeCount());
				int nA = nodeA.getGlobalIndex();
				int nB = nodeB.getGlobalIndex();
				//
				delta[3*nA  ] += nodeB.getX() - nodeA.getX();
				delta[3*nA+1] += nodeB.getY() - nodeA.getY();
				delta[3*nA+2] += nodeB.getZ() - nodeA.getZ();
				weights[nA] += 1;
				//
				delta[3*nB  ] += nodeA.getX() - nodeB.getX();
				delta[3*nB+1] += nodeA.getY() - nodeB.getY();
				delta[3*nB+2] += nodeA.getZ() - nodeB.getZ();
				weights[nB] += 1;
			}
		}
	}
	//
	for (int i = 0; i < surfaceCollection.getNodeCount(); i++) {
		Node node = surfaceCollection.getNodes(i);
		delta[3*i  ] = delta[3*i  ]/weights[i];
		delta[3*i+1] = delta[3*i+1]/weights[i];
		delta[3*i+2] = delta[3*i+2]/weights[i];
	}
	//
	return(delta);
}


/**
 * This could probably be slightly more efficient, but this is easier...
 */
public Node[] createGlobalNodeList(Node[][][] nodes, SurfaceCollection surfaceCollection, int nX, int nY, int nZ) {
	Node[] nodeList = new Node[nX*nY*nZ];
	//
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
		Surface surface = surfaceCollection.getSurfaces(i);
		for (int j = 0; j < surface.getPolygonCount(); j++) {
			Polygon polygon = surface.getPolygons(j);
			for (int k = 0; k < polygon.getNodeCount(); k++) {
				Node node = polygon.getNodes(k);
				nodeList[node.getGlobalIndex()] = node;
			}
		}
	}
	//
	java.util.Vector globalNodeVector = new java.util.Vector();
	for (int n = 0; n < nX*nY*nZ; n++) if (nodeList[n] != null) {
		nodeList[n].setGlobalIndex(globalNodeVector.size());
		globalNodeVector.add(nodeList[n]);
	}
	//
	return((Node[]) globalNodeVector.toArray(new Node[0]));
}


public static int[][][] createSphere(int I, int J, int K) {
	int[][][] pixels = new int[I][J][K];
	double Dx = 2.0/(I - 1), Dy = 2.0/(J - 1), Dz = 2.0/(K - 1);
	for (int i = 0; i < I; i++) {
		double x = -1.0 + i*Dx;
		for (int j = 0; j < J; j++) {
			double y = -1.0 + j*Dy;
			for (int k = 0; k < K; k++) {
				double z = -1.0 + k*Dz;
				double r = x*x + y*y + z*z;
				pixels[i][j][k] = 0;
				if (r < 0.8) pixels[i][j][k] = 1;
				if (r < 0.6) pixels[i][j][k] = 2;
				if (r < 0.4) pixels[i][j][k] = 3;
				if (r < 0.2) pixels[i][j][k] = 4;
				if (x < 0.0) pixels[i][j][k] = 5;
			}
		}
	}
	return(pixels);
}


public SurfaceCollection generateSurface(Geometry geometry) throws cbit.vcell.geometry.GeometryException, cbit.image.ImageException, cbit.vcell.parser.ExpressionException {
	
	VCImage image = geometry.getGeometrySpec().getSampledImage();
	RegionImage regionImage = new RegionImage(image);

	return generateSurface(regionImage, geometry.getDimension(), geometry.getExtent(), geometry.getOrigin());
}


/**
 * Basic initial algorithm. Assumes all sorts of shit about
 * the coordinates, etc, but those will get fixed later.
 */
private SurfaceCollection generateSurface(RegionImage image, Node[][][] nodes, BoundingIndex index) {
	int dI = index.getHiI() - index.getLoI();
	int dJ = index.getHiJ() - index.getLoJ();
	int dK = index.getHiK() - index.getLoK();
	if (dI == 0 && dJ == 0 && dK == 0) {
		// Do nothing...
	} else if (dI == 1 && dJ == 0 && dK == 0) {
		int i = index.getLoI(), j = index.getLoJ(), k = index.getLoK();
		int offsetL = image.getOffset(i, j, k);   // at (i  ,j  ,k  )
		int offsetR = offsetL + 1;                // at (i+1,j  ,k  )
		RegionImage.RegionInfo regionInfoL = image.getRegionInfoFromOffset(offsetL);
		if (!regionInfoL.isIndexInRegion(offsetR)){ // (i,j,k) not same region as (i+1,j,k)
			RegionImage.RegionInfo regionInfoR = image.getRegionInfoFromOffset(offsetR);
			Node[] n = new Node[] { nodes[i+1][j][k], nodes[i+1][j+1][k], nodes[i+1][j+1][k+1], nodes[i+1][j][k+1] };
			return(new SurfaceCollection(new OrigSurface(regionInfoL.getRegionIndex(), regionInfoR.getRegionIndex(), new Quadrilateral(n,offsetL,offsetR))));
		}
	} else if (dI == 0 && dJ == 1 && dK == 0) {
		int i = index.getLoI(), j = index.getLoJ(), k = index.getLoK();
		int offsetL = image.getOffset(i, j, k);   // at (i  ,j  ,k  )
		int offsetR = offsetL + image.getNumX();  // at (i  ,j+1,k  )
		RegionImage.RegionInfo regionInfoL = image.getRegionInfoFromOffset(offsetL);
		if (!regionInfoL.isIndexInRegion(offsetR)){ // (i,j,k) not same region as (i,j+1,k)
			RegionImage.RegionInfo regionInfoR = image.getRegionInfoFromOffset(offsetR);
			Node[] n = new Node[] { nodes[i][j+1][k], nodes[i][j+1][k+1], nodes[i+1][j+1][k+1], nodes[i+1][j+1][k] };
			return(new SurfaceCollection(new OrigSurface(regionInfoL.getRegionIndex(), regionInfoR.getRegionIndex(), new Quadrilateral(n,offsetL,offsetR))));
		}
	} else if (dI == 0 && dJ == 0 && dK == 1) {
		int i = index.getLoI(), j = index.getLoJ(), k = index.getLoK();
		int offsetL = image.getOffset(i, j, k);   // at (i  ,j  ,k  )
		int offsetR = offsetL + image.getNumXY(); // at (i  ,j  ,k+1)
		RegionImage.RegionInfo regionInfoL = image.getRegionInfoFromOffset(offsetL);
		if (!regionInfoL.isIndexInRegion(offsetR)){ // (i,j,k) not same region as (i,j,k+1)
			RegionImage.RegionInfo regionInfoR = image.getRegionInfoFromOffset(offsetR);
			Node[] n = new Node[] { nodes[i][j][k+1], nodes[i+1][j][k+1], nodes[i+1][j+1][k+1], nodes[i][j+1][k+1] };
			return(new SurfaceCollection(new OrigSurface(regionInfoL.getRegionIndex(), regionInfoR.getRegionIndex(), new Quadrilateral(n,offsetL,offsetR))));
		}
	} else {
		SurfaceCollection surfaceCollection = new SurfaceCollection();
		if (dI == Math.max(dI, Math.max(dJ, dK))) {
			int mI = (index.getHiI() + index.getLoI())/2;
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), mI, index.getLoJ(), index.getHiJ(), index.getLoK(), index.getHiK())));
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(mI + 1, index.getHiI(), index.getLoJ(), index.getHiJ(), index.getLoK(), index.getHiK())));
			for (int j = index.getLoJ(); j <= index.getHiJ(); j++) {
				for (int k = index.getLoK(); k <= index.getHiK(); k++) {
					surfaceCollection.addSurfaceCollection(generateSurface(image, nodes, new BoundingIndex(mI, mI + 1, j, j, k, k)));
				}
			}
		} else if (dJ == Math.max(dI, Math.max(dJ, dK))) {
			int mJ = (index.getHiJ() + index.getLoJ())/2;
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), index.getHiI(), index.getLoJ(), mJ, index.getLoK(), index.getHiK())));
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), index.getHiI(), mJ + 1, index.getHiJ(), index.getLoK(), index.getHiK())));
			for (int i = index.getLoI(); i <= index.getHiI(); i++) {
				for (int k = index.getLoK(); k <= index.getHiK(); k++) {
					surfaceCollection.addSurfaceCollection(generateSurface(image, nodes, new BoundingIndex(i, i, mJ, mJ + 1, k, k)));
				}
			}
		} else {
			int mK = (index.getHiK() + index.getLoK())/2;
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), index.getHiI(), index.getLoJ(), index.getHiJ(), index.getLoK(), mK)));
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), index.getHiI(), index.getLoJ(), index.getHiJ(), mK + 1, index.getHiK())));
			for (int i = index.getLoI(); i <= index.getHiI(); i++) {
				for (int j = index.getLoJ(); j <= index.getHiJ(); j++) {
					surfaceCollection.addSurfaceCollection(generateSurface(image, nodes, new BoundingIndex(i, i, j, j, mK, mK + 1)));
				}
			}
		}
		//  At this point, surfaceCollection contains *all* the Surface objects...First
		//  we go through and make sure they are all in the right direction...
		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
			Surface surface = surfaceCollection.getSurfaces(i);
			if (surface.getInteriorRegionIndex() >= surface.getExteriorRegionIndex()) {
				surface.reverseDirection();
			}
		}
		//  Now we go through and combine all TriangulatedSurfaces
		//  separating the same pixels into one...NOTE THAT we combine
		//  all quads separating each color pair, WITHOUT any concern as
		//  to whether they are contiguous.
		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
			for (int j = i + 1; j < surfaceCollection.getSurfaceCount(); /*NULL*/) {
				// If we can connect i and j, then combine
				// the Boundaries and put them back in i.
				Surface surfaceI = surfaceCollection.getSurfaces(i);
				Surface surfaceJ = surfaceCollection.getSurfaces(j);
				//
				if (surfaceI.getInteriorRegionIndex() == surfaceJ.getInteriorRegionIndex() &&
					surfaceI.getExteriorRegionIndex() == surfaceJ.getExteriorRegionIndex()) {
					surfaceI.addSurface(surfaceJ);
					surfaceCollection.removeSurface(j);
				} else {
					j = j + 1;
				}
			}
		}
		//
		return(surfaceCollection);
	}
	return(new SurfaceCollection());
}


public SurfaceCollection generateSurface(RegionImage regionImage, int dimension, cbit.util.Extent extent, cbit.util.Origin origin) {
	double dX = extent.getX() / (regionImage.getNumX() -1);
	double dY = extent.getY() / (regionImage.getNumY() -1);
	double dZ = extent.getZ() / (regionImage.getNumZ() -1);
	//
	double LoX = origin.getX() - 0.5 * dX;
	double LoY = origin.getY() - 0.5 * dY;
	double LoZ = origin.getZ() - 0.5 * dZ;
	//
	BoundingIndex boundingIndex = new BoundingIndex(
		0, regionImage.getNumX() - 1,
		0, regionImage.getNumY() - 1,
		0, regionImage.getNumZ() - 1);
	//
	if (dimension == 1) {
		throw new RuntimeException("SurfaceGenerator.generateSurface() can't deal with 1D geometries yet...");
	} else if (dimension == 2) {
		throw new RuntimeException("SurfaceGenerator.generateSurface() can't deal with 2D geometries yet...");
	} else if (dimension == 3) {
		Node[][][] nodes = new Node[regionImage.getNumX()+1][regionImage.getNumY()+1][regionImage.getNumZ()+1];
		//
		int n = 0;
		double x,y,z;
		boolean bMvX,bMvY,bMvZ;
		for (int i = 0; i <= regionImage.getNumX(); i++) {
			bMvX = false;
			if (i == 0){
				x = origin.getX();
			}else if(i == regionImage.getNumX()) {
				x = origin.getX()+extent.getX();
			}else{
				x = LoX + i*dX;
				bMvX = true;
			}
			for (int j = 0; j <= regionImage.getNumY(); j++) {
				bMvY = false;
				if (j == 0){
					y = origin.getY();
				}else if(j == regionImage.getNumY()) {
					y = origin.getY()+extent.getY();
				}else{
					y = LoY + j*dY;
					bMvY = true;
				}
				for (int k = 0; k <= regionImage.getNumZ(); k++) {
					bMvZ = false;
					if (k == 0){
						z = origin.getZ();
					}else if(k == regionImage.getNumZ()) {
						z = origin.getZ()+extent.getZ();
					}else{
						z = LoZ + k*dZ;
						bMvZ = true;
					}
					nodes[i][j][k] = new Node(x,y,z);
					nodes[i][j][k].setGlobalIndex(n++);
					nodes[i][j][k].setMoveX(bMvX);
					nodes[i][j][k].setMoveY(bMvY);
					nodes[i][j][k].setMoveZ(bMvZ);
				}
			}
		}
		SurfaceCollection surfaceCollection = generateSurface(regionImage, nodes, boundingIndex);
		surfaceCollection.setNodes(createGlobalNodeList(nodes, surfaceCollection,
			regionImage.getNumX()+1, regionImage.getNumY()+1, regionImage.getNumZ()+1));
		//
		return(surfaceCollection);

	} else {
		throw new IllegalArgumentException("Dimensions are wrong");
	}
}


/**
 * Basic initial algorithm. Assumes all sorts of shit about
 * the coordinates, etc, but those will get fixed later.
 */
private SurfaceCollection generateSurface(cbit.vcell.geometry.RegionImageOrig image, Node[][][] nodes, BoundingIndex index) {
	int dI = index.getHiI() - index.getLoI();
	int dJ = index.getHiJ() - index.getLoJ();
	int dK = index.getHiK() - index.getLoK();
	if (dI == 0 && dJ == 0 && dK == 0) {
		// Do nothing...
	} else if (dI == 1 && dJ == 0 && dK == 0) {
		//
		// make quad perpendicular to X direction
		//
		int i = index.getLoI(), j = index.getLoJ(), k = index.getLoK();
		int offsetL = image.getOffset(i  , j,  k);
		int offsetR = image.getOffset(i+1, j,  k);
		int regionIndexL = 0xFF & ((int) image.getRegionIndex(offsetL));
		int regionIndexR = 0xFF & ((int) image.getRegionIndex(offsetR));
		if (regionIndexL != regionIndexR) {
		//if (pixels[i][j][k] != pixels[i+1][j][k]) {
			Node[] n = new Node[] { nodes[i+1][j][k], nodes[i+1][j+1][k], nodes[i+1][j+1][k+1], nodes[i+1][j][k+1] };
			return(new SurfaceCollection(new OrigSurface(regionIndexL, regionIndexR, new Quadrilateral(n,offsetL,offsetR))));
		}
	} else if (dI == 0 && dJ == 1 && dK == 0) {
		//
		// make quad perpendicular to Y direction
		//
		int i = index.getLoI(), j = index.getLoJ(), k = index.getLoK();
		int offsetL = image.getOffset(i, j  , k);
		int offsetR = image.getOffset(i, j+1, k);
		int regionIndexL = 0xFF & ((int) image.getRegionIndex(offsetL));
		int regionIndexR = 0xFF & ((int) image.getRegionIndex(offsetR));
		if (regionIndexL != regionIndexR) {
		//if (pixels[i][j][k] != pixels[i][j+1][k]) {
			Node[] n = new Node[] { nodes[i][j+1][k], nodes[i][j+1][k+1], nodes[i+1][j+1][k+1], nodes[i+1][j+1][k] };
			return(new SurfaceCollection(new OrigSurface(regionIndexL, regionIndexR, new Quadrilateral(n,offsetL,offsetR))));
		}
	} else if (dI == 0 && dJ == 0 && dK == 1) {
		//
		// make quad perpendicular to  direction
		//
		int i = index.getLoI(), j = index.getLoJ(), k = index.getLoK();
		int offsetL = image.getOffset(i, j, k);
		int offsetR = image.getOffset(i, j, k+1);
		int regionIndexL = 0xFF & ((int) image.getRegionIndex(offsetL));
		int regionIndexR = 0xFF & ((int) image.getRegionIndex(offsetR));
		if (regionIndexL != regionIndexR) {
		//if (pixels[i][j][k] != pixels[i][j][k+1]) {
			Node[] n = new Node[] { nodes[i][j][k+1], nodes[i+1][j][k+1], nodes[i+1][j+1][k+1], nodes[i][j+1][k+1] };
			return(new SurfaceCollection(new OrigSurface(regionIndexL, regionIndexR, new Quadrilateral(n,offsetL,offsetR))));
		}
	} else {
		SurfaceCollection surfaceCollection = new SurfaceCollection();
		if (dI == Math.max(dI, Math.max(dJ, dK))) {
			int mI = (index.getHiI() + index.getLoI())/2;
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), mI, index.getLoJ(), index.getHiJ(), index.getLoK(), index.getHiK())));
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(mI + 1, index.getHiI(), index.getLoJ(), index.getHiJ(), index.getLoK(), index.getHiK())));
			for (int j = index.getLoJ(); j <= index.getHiJ(); j++) {
				for (int k = index.getLoK(); k <= index.getHiK(); k++) {
					surfaceCollection.addSurfaceCollection(generateSurface(image, nodes, new BoundingIndex(mI, mI + 1, j, j, k, k)));
				}
			}
		} else if (dJ == Math.max(dI, Math.max(dJ, dK))) {
			int mJ = (index.getHiJ() + index.getLoJ())/2;
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), index.getHiI(), index.getLoJ(), mJ, index.getLoK(), index.getHiK())));
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), index.getHiI(), mJ + 1, index.getHiJ(), index.getLoK(), index.getHiK())));
			for (int i = index.getLoI(); i <= index.getHiI(); i++) {
				for (int k = index.getLoK(); k <= index.getHiK(); k++) {
					surfaceCollection.addSurfaceCollection(generateSurface(image, nodes, new BoundingIndex(i, i, mJ, mJ + 1, k, k)));
				}
			}
		} else {
			int mK = (index.getHiK() + index.getLoK())/2;
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), index.getHiI(), index.getLoJ(), index.getHiJ(), index.getLoK(), mK)));
			surfaceCollection.addSurfaceCollection(generateSurface(image, nodes,
				new BoundingIndex(index.getLoI(), index.getHiI(), index.getLoJ(), index.getHiJ(), mK + 1, index.getHiK())));
			for (int i = index.getLoI(); i <= index.getHiI(); i++) {
				for (int j = index.getLoJ(); j <= index.getHiJ(); j++) {
					surfaceCollection.addSurfaceCollection(generateSurface(image, nodes, new BoundingIndex(i, i, j, j, mK, mK + 1)));
				}
			}
		}
		//  At this point, surfaceCollection contains *all* the Surface objects...First
		//  we go through and make sure they are all in the right direction...
		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
			Surface surface = surfaceCollection.getSurfaces(i);
			if (surface.getInteriorRegionIndex() >= surface.getExteriorRegionIndex()) {
				surface.reverseDirection();
			}
		}
		//  Now we go through and combine all TriangulatedSurfaces
		//  separating the same pixels into one...NOTE THAT we combine
		//  all quads separating each color pair, WITHOUT any concern as
		//  to whether they are contiguous.
		for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
			for (int j = i + 1; j < surfaceCollection.getSurfaceCount(); /*NULL*/) {
				// If we can connect i and j, then combine
				// the Boundaries and put them back in i.
				Surface surfaceI = surfaceCollection.getSurfaces(i);
				Surface surfaceJ = surfaceCollection.getSurfaces(j);
				//
				if (surfaceI.getInteriorRegionIndex() == surfaceJ.getInteriorRegionIndex() &&
					surfaceI.getExteriorRegionIndex() == surfaceJ.getExteriorRegionIndex()) {
					surfaceI.addSurface(surfaceJ);
					surfaceCollection.removeSurface(j);
				} else {
					j = j + 1;
				}
			}
		}
		//
		return(surfaceCollection);
	}
	return(new SurfaceCollection());
}


public SurfaceCollection generateSurface(cbit.vcell.geometry.RegionImageOrig regionImage, int dimension, cbit.util.Extent extent, cbit.util.Origin origin) {
	double dX = extent.getX() / regionImage.getNumX();
	double dY = extent.getY() / regionImage.getNumY();
	double dZ = extent.getZ() / regionImage.getNumZ();
	//
	double LoX = origin.getX() + 0.5 * dX;
	double LoY = origin.getY() + 0.5 * dY;
	double LoZ = origin.getZ() + 0.5 * dZ;
	//
	double HiX = origin.getX() + extent.getX() - 0.5 * dX;
	double HiY = origin.getY() + extent.getY() - 0.5 * dY;
	double HiZ = origin.getZ() + extent.getZ() - 0.5 * dZ;
	//
	BoundingBox   boundingBox   = new BoundingBox(LoX, HiX, LoY, HiY, LoZ, HiZ);
	BoundingIndex boundingIndex = new BoundingIndex(
		0, regionImage.getNumX() - 1,
		0, regionImage.getNumY() - 1,
		0, regionImage.getNumZ() - 1);
	//
	if (dimension == 1) {
		throw new RuntimeException("SurfaceGenerator.generateSurface() can't deal with 1D geometries yet...");
		//return (generateSurfaceOneDimensions(regionImage, boundingIndex, boundingBox));
	} else if (dimension == 2) {
		throw new RuntimeException("SurfaceGenerator.generateSurface() can't deal with 2D geometries yet...");
		//return (generateSurfaceTwoDimensions(regionImage, boundingIndex, boundingBox));
	} else if (dimension == 3) {
		//return (generateSurfaceThreeDimensions(regionImage, boundingIndex, boundingBox));
		Node[][][] nodes = new Node[regionImage.getNumX()+1][regionImage.getNumY()+1][regionImage.getNumZ()+1];
		//
		for (int i = 0, n = 0; i <= regionImage.getNumX(); i++) {
			for (int j = 0; j <= regionImage.getNumY(); j++) {
				for (int k = 0; k <= regionImage.getNumZ(); k++) {
					nodes[i][j][k] = new Node(LoX + i*dX, LoY + j*dY, LoZ + k*dZ);
					nodes[i][j][k].setGlobalIndex(n++);
					if (i == 0 || i == regionImage.getNumX()) {
						nodes[i][j][k].setMoveX(false);
					} else if (j == 0 || j == regionImage.getNumY()) {
						nodes[i][j][k].setMoveY(false);
					} else if (k == 0 || k == regionImage.getNumZ()) {
						nodes[i][j][k].setMoveZ(false);
					}
				}
			}
		}
		SurfaceCollection surfaceCollection = generateSurface(regionImage, nodes, boundingIndex);
		surfaceCollection.setNodes(createGlobalNodeList(nodes, surfaceCollection,
			regionImage.getNumX()+1, regionImage.getNumY()+1, regionImage.getNumZ()+1));
		//
		return(surfaceCollection);

	} else {
		throw new IllegalArgumentException("Dimensions are wrong");
	}
}


/**
 * LocalComputationalGeometryServer constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
public SessionLog getSessionLog() {
	return (fieldSessionLog);
}


public static void laplacianSmoothing(SurfaceCollection surfaceCollection, double lambda) {
	double[] delta = calculateLaplacian(surfaceCollection);
	//
	for (int i = 0; i < surfaceCollection.getNodeCount(); i++) {
		Node node = surfaceCollection.getNodes(i);
		if (node.getMoveX()) node.setX(node.getX() + lambda*delta[3*i  ]);
		if (node.getMoveY()) node.setY(node.getY() + lambda*delta[3*i+1]);
		if (node.getMoveZ()) node.setZ(node.getZ() + lambda*delta[3*i+2]);
	}
}
}