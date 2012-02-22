package cbit.vcell.geometry.surface;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.render.FastMarchingMethod;
import cbit.vcell.render.Vect3d;

public class DistanceMapGenerator {
	
	public static SubvolumeSignedDistanceMap[] computeDistanceMaps(Geometry geometry, VCImage subvolumeHandleImage, boolean bCellCentered) throws ImageException{
		
		double[] samplesX = new double[subvolumeHandleImage.getNumX()];
		double[] samplesY = new double[subvolumeHandleImage.getNumY()];
		double[] samplesZ = new double[subvolumeHandleImage.getNumZ()];
		ISize sampleSize = new ISize(subvolumeHandleImage.getNumX(),subvolumeHandleImage.getNumY(), subvolumeHandleImage.getNumZ());
		byte[] pixels = subvolumeHandleImage.getPixels();
		boolean[] insideMask = new boolean[sampleSize.getXYZ()];
		Origin origin = geometry.getOrigin();
		Extent extent = geometry.getExtent();
		RayCaster.sampleXYZCoordinates(sampleSize, origin, extent, samplesX, samplesY, samplesZ, bCellCentered);
		
		ArrayList<SubvolumeSignedDistanceMap> distanceMaps = new ArrayList<SubvolumeSignedDistanceMap>();
		for (SubVolume subVolume : geometry.getGeometrySpec().getSubVolumes()){
			//
			// find surfaces that bound the current SubVolume
			//
			ArrayList<Surface> surfaces = new ArrayList<Surface>();
			for (GeometricRegion geometricRegion : geometry.getGeometrySurfaceDescription().getGeometricRegions()){
				if (geometricRegion instanceof SurfaceGeometricRegion){
					SurfaceGeometricRegion surfaceGeometricRegion = (SurfaceGeometricRegion)geometricRegion;
					for (GeometricRegion adjacentRegion : ((SurfaceGeometricRegion)geometricRegion).getAdjacentGeometricRegions()){
						if (adjacentRegion instanceof VolumeGeometricRegion && ((VolumeGeometricRegion)adjacentRegion).getSubVolume()==subVolume){
							surfaces.add(geometry.getGeometrySurfaceDescription().getSurfaceCollection().getSurface(surfaceGeometricRegion));
						}
					}
				}
			}
			// find unsigned distances in a narrow band for surfaces that bound this SubVolume (expensive)
			// values outside the band are assumed to be initialized to Double.MAX_VALUE
			double[] distanceMap = localUnsignedDistanceMap(surfaces, samplesX, samplesY, samplesZ, 2);
			
			// extend signed distance map using fast marching method from narrow band to all points.
			// will do it in 2 steps, positive growth first towards inside, then change the sign of the whole distance map, then positive growth towards the exterior
			// this way, the interior distances will end negative and the exterior distances positive
			// 2 step growth saves memory and reduces the number of elements present at any given time in the binary heap (which is the most time consuming factor)
			Arrays.fill(insideMask, false);
			int subvolumeHandle = subVolume.getHandle();
			for (int i = 0; i<insideMask.length; i++){
				if (pixels[i] == subvolumeHandle){
					insideMask[i] = true;
				} else {
					if (distanceMap[i]!=Double.MAX_VALUE){
						distanceMap[i] = -distanceMap[i];
					}
				}
			}

			// step 1, we compute distances for the points "inside"
			// the points outside are cold (we don't compute their distances this step)
			FastMarchingMethod fmm = new FastMarchingMethod(samplesX.length, samplesY.length, samplesZ.length, distanceMap, insideMask);
			fmm.march();
			
			// sign change of the half-completed distance map, the "interior" will become negative as it should be
			subvolumeHandle = subVolume.getHandle();
			for (int i = 0; i<insideMask.length; i++){
				if (pixels[i] == subvolumeHandle){
					if (distanceMap[i]!=Double.MAX_VALUE){
						distanceMap[i] = -distanceMap[i];
					}
				}
			}

			// step 2, we compute distances for the points "outside"
			// no cold points (points we don't care about) this time, they are already frozen
			fmm = new FastMarchingMethod(samplesX.length, samplesY.length, samplesZ.length, distanceMap, null);
			fmm.march();
			
			SubvolumeSignedDistanceMap subvolumeSignedDistanceMap = new SubvolumeSignedDistanceMap(subVolume, samplesX, samplesY, samplesZ, distanceMap);
			distanceMaps.add(subvolumeSignedDistanceMap);
		}
		return distanceMaps.toArray(new SubvolumeSignedDistanceMap[distanceMaps.size()]);
	}

	private static double[] localUnsignedDistanceMap(List<Surface> surfaces, double[] samplesX, double[] samplesY, double[] samplesZ, int padFactor){
		// padFactor is the half-width of local distance map; use big number for full brute-force sampling. (2 is a good number???)
		int numX = samplesX.length;
		int numY = samplesY.length;
		int numZ = samplesZ.length;
		double[] distanceMap = null;
		
		double epsilon = 1e-8; // roundoff factor.
		long t1 = System.currentTimeMillis();
	
		// here we expect either triangles or nonplanar quads which will be broken up into two triangles.
		cbit.vcell.geometry.surface.Triangle triangles[] = new cbit.vcell.geometry.surface.Triangle[2];
		for (Surface surface : surfaces){
			for (int k = 0; k < surface.getPolygonCount(); k++){
				Polygon polygon = surface.getPolygons(k);
				Node[] nodes = polygon.getNodes();
				double p0x = nodes[0].getX();
				double p0y = nodes[0].getY();
				double p0z = nodes[0].getZ();
				double p1x = nodes[1].getX();
				double p1y = nodes[1].getY();
				double p1z = nodes[1].getZ();
				double p2x = nodes[2].getX();
				double p2y = nodes[2].getY();
				double p2z = nodes[2].getZ();
				double minX = Math.min(p0x, Math.min(p1x, p2x));
				double maxX = Math.max(p0x, Math.max(p1x, p2x));
				double minY = Math.min(p0y, Math.min(p1y, p2y));
				double maxY = Math.max(p0y, Math.max(p1y, p2y));
				double minZ = Math.min(p0z, Math.min(p1z, p2z));
				double maxZ = Math.max(p0z, Math.max(p1z, p2z));
				if (polygon.getNodeCount()==4){
					double p3x = nodes[3].getX();
					double p3y = nodes[3].getY();
					double p3z = nodes[3].getZ();
					minX = Math.min(minX,p3x);
					maxX = Math.max(maxX,p3x);
					minY = Math.min(minY,p3y);
					maxY = Math.max(maxY,p3y);
					minZ = Math.min(minZ,p3z);
					maxZ = Math.max(maxZ,p3z);
				}
				
				Vect3d unitNormal = new Vect3d();
				polygon.getUnitNormal(unitNormal);
				// convert quads to triangles if necessary
				int numTriangles;
				if (polygon.getNodeCount()==3){
					numTriangles = 1;
					triangles[0] = new cbit.vcell.geometry.surface.Triangle(polygon.getNodes(0), polygon.getNodes(1), polygon.getNodes(2));
				}else if (polygon.getNodeCount()==4){
					numTriangles = 2;
					triangles[0] = new cbit.vcell.geometry.surface.Triangle(polygon.getNodes(0), polygon.getNodes(1), polygon.getNodes(2));
					triangles[1] = new cbit.vcell.geometry.surface.Triangle(polygon.getNodes(0), polygon.getNodes(2), polygon.getNodes(3));
				}else{
					throw new RuntimeException("polygons with "+polygon.getNodeCount()+" edges are supported");
				}
				
				for (int triIndex = 0; triIndex < numTriangles; triIndex++){
					cbit.vcell.geometry.surface.Triangle triangle = triangles[triIndex];
	
					if (distanceMap==null){
						distanceMap = new double[numX*numY*numZ];
						Arrays.fill(distanceMap, Double.POSITIVE_INFINITY);
					}
					// precompute ray indices that are within the bounding box of the quad.
					double padDistanceX = (samplesX[1]-samplesX[0])*padFactor;
					double padDistanceY = (samplesY[1]-samplesY[0])*padFactor;
					double padDistanceZ = (samplesZ[1]-samplesZ[0])*padFactor;
					int tstartI = numX;
					int tendI = -1;
					for (int ii=0;ii<numX;ii++){
						double sampleX = samplesX[ii];
						if (sampleX >= minX-padDistanceX && sampleX <= maxX+padDistanceX){
							tstartI = Math.min(tstartI, ii);
							tendI = Math.max(tendI,  ii);
						}
					}
					int tstartJ = numY;
					int tendJ = -1;
					for (int jj=0;jj<numY;jj++){
						double sampleY = samplesY[jj];
						if (sampleY >= minY-padDistanceY && sampleY <= maxY+padDistanceY){
							tstartJ = Math.min(tstartJ, jj);
							tendJ = Math.max(tendJ,  jj);
						}
					}
					int tstartK = numZ;
					int tendK = -1;
					for (int kk=0;kk<numZ;kk++){
						double sampleZ = samplesZ[kk];
						if (sampleZ >= minZ-padDistanceZ && sampleZ <= maxZ+padDistanceZ){
							tstartK = Math.min(tstartK, kk);
							tendK = Math.max(tendK,  kk);
						}
					}
					
					Vect3d testPoint = new Vect3d();
					Vect3d tr1 = new Vect3d();
					Vect3d tr2 = new Vect3d();
					Vect3d tr3 = new Vect3d();
					for (int ii=tstartI;ii<=tendI;ii++){
						for (int jj=tstartJ;jj<=tendJ;jj++){
							for (int kk=tstartK;kk<=tendK;kk++){
								testPoint.set(samplesX[ii],samplesY[jj],samplesZ[kk]);
								tr1.set(triangle.getNodes()[0].getX(), triangle.getNodes()[0].getY(), triangle.getNodes()[0].getZ());
								tr2.set(triangle.getNodes()[1].getX(), triangle.getNodes()[1].getY(), triangle.getNodes()[1].getZ());
								tr3.set(triangle.getNodes()[2].getX(), triangle.getNodes()[2].getY(), triangle.getNodes()[2].getZ());
								double distanceToTriangle3d = DistanceMapGenerator.distanceToTriangle3d(testPoint, tr1, tr2, tr3);	// always a positive number
								int distanceMapIndex = ii+jj*numX+kk*numX*numY;
								double bestMatch = Math.min(distanceMap[distanceMapIndex],distanceToTriangle3d);
								distanceMap[distanceMapIndex] = bestMatch;
							}
						}
					}
				}
			}
		}
		return distanceMap;
	}

	
	private static int getX(int position, int numX, int numY) {
		int z = (int)(position/(numX*numY));
		int tmp1 = position - z*numX*numY;
		int x = (int)(tmp1%numX);
		return x;
	}

	private static int getY(int position, int numX, int numY) {
		int z = (int)(position/(numX*numY));
		int tmp1 = position - z*numX*numY;
		int y = (int)(tmp1/numX);
		return y;
	}

	private static int getZ(int position, int numX, int numY) {
		int z = (int)(position/(numX*numY));
		return z;
	}

	//public static double DistanceToPlane(Vect3d point, Vect3d t0, Vect3d t1, Vect3d t2)
	//{
	//	Vect3d n = cross(sub(t1,t0), sub(t2,t0));
	//	n.unit();
	//	return Math.abs(dot(n, point) + dot(n, t0));
	//}
	private static double DistanceToPlane(Vect3d point, Vect3d t0, Vect3d t1, Vect3d t2)
	{
		Vect3d tmp1 = new Vect3d(t1);
		tmp1.sub(t0);
		Vect3d tmp2 = new Vect3d(t2);
		tmp2.sub(t0);
		Vect3d n = tmp1.cross(tmp2);
		n.unit();
		Vect3d disp = new Vect3d(point);
		disp.sub(t0);
		return Math.abs(disp.dot(n));
	}

	public static double distanceToTriangle3d(Vect3d p0, Vect3d t1, Vect3d t2, Vect3d t3) {
		// we allocate these here once and reuse them (through set()) as much as we can
		// because allocations are very expensive and this method is called many many times
		Vect3d tmp = new Vect3d();
		Vect3d tmp1 = new Vect3d();
		
		tmp.set(t2);
		tmp.sub(t1);
		tmp1.set(t3);
		tmp1.sub(t1);
		Vect3d normal = tmp.cross(tmp1);						// 1  - the normal
		double normalLength = normal.length();
	//	System.out.println("the normal = " + normal + ", length = " + normal.length());
	
		tmp.set(p0);
		tmp.sub(t1);
		double tmpLength = tmp.length();
		if(tmpLength == 0 || normalLength == 0) {
			return 0;		// point is in a vertex
		}
		double cosalpha = tmp.dot(normal) / (tmpLength*normalLength);	// 2  - cosalpha  
	//	System.out.println("cos alpha = " + cosalpha);
	
		double projectionLength = tmpLength * cosalpha;			// 3    - projection length
	//	double otherMethod = DistanceToPlane(p0, t1, t2, t3);
	//	System.out.println("projectionLength = " + projectionLength);
					
		Vect3d projection =  new Vect3d(normal);
		projection.uminusFast();
		projection.scale(projectionLength / normalLength);		// 4    - projection vector
	//	System.out.println("projection = " + projection);
			
		Vect3d projected = new Vect3d(p0);
		projected.add(projection);									// 5    - projection of p0 onto the triangle plane
	//	System.out.println("projected = " + projected);
			
		Vect3d v1 = new Vect3d(t1);					// 6a    - t2t1		
		v1.sub(t2);
		v1.unit();
		tmp.set(t1);								// t3t1
		tmp.sub(t3);
		tmp.unit();
		v1.add(tmp);
	//	System.out.println("v1 = " + v1);
			
		Vect3d v2 = new Vect3d(t2);					// 6b    - t3t2
		v2.sub(t3);
		v2.unit();
		tmp.set(t2);								// t1t2
		tmp.sub(t1);
		tmp.unit();
		v2.add(tmp);
	//	System.out.println("v2 = " + v2);
			
		Vect3d v3 = new Vect3d(t3);					// 6c    - t1t3
		v3.sub(t1);
		v3.unit();
		tmp.set(t3);								// t2t3
		tmp.sub(t2);
		tmp.unit();
		v3.add(tmp);
	//	System.out.println("v3 = " + v3);
				
		tmp.set(projected);
		tmp.sub(t1);
		tmp1.set(v1);						// f1v
		tmp1.crossFast(tmp);
		double f1 = tmp1.dot(normal);		// f1 > 0 means projected is anticlockwise of v1
	//	System.out.println("f1 = " + f1);
			
		tmp.set(projected);
		tmp.sub(t2);
		tmp1.set(v2);						// f2v
		tmp1.crossFast(tmp);
		double f2 = tmp1.dot(normal);
	//	System.out.println("f2 = " + f2);
	
		tmp.set(projected);
		tmp.sub(t3);
		tmp1.set(v3);
		tmp1.crossFast(tmp);				// f3v
		double f3 = tmp1.dot(normal);
	//	System.out.println("f3 = " + f3);
	
		boolean bInside = false;
		double distanceToTriangle = 0;
		if(f1 >= 0 && f2 < 0) {
//			System.out.println("Projection of point is inside the t1, t2 quadrant...");
			bInside = isInsideTriangle(projected, normal, t1, t2, tmp, tmp1);
			if(bInside == true) {		// easy case, length of projection already known
//				System.out.println("Distance from point to triangle is " + projectionLength);
				distanceToTriangle = projectionLength;
			} else {
				distanceToTriangle = distanceToTriangleUtil(projected, t1, t2, p0, projectionLength, tmp, tmp1);
			}
		}
		else if(f2 >= 0 && f3 < 0) {
//			System.out.println("Projection of point is inside the t2, t3 quadrant...");
			bInside = isInsideTriangle(projected, normal, t2, t3, tmp, tmp1);
			if(bInside == true) {
//				System.out.println("Distance from point to triangle is " + projectionLength);
				distanceToTriangle = projectionLength;
			} else {
				distanceToTriangle = distanceToTriangleUtil(projected, t2, t3, p0, projectionLength, tmp, tmp1);
			}
		}
		else if(f1 < 0 && f3 >= 0) {
//			System.out.println("Projection of point is inside the t1, t3 quadrant...");
			bInside = isInsideTriangle(projected, normal, t3, t1, tmp, tmp1);
			if(bInside == true) {
//				System.out.println("Distance from point to triangle is " + projectionLength);
				distanceToTriangle = projectionLength;
			} else {
				distanceToTriangle = distanceToTriangleUtil(projected, t3, t1, p0, projectionLength, tmp, tmp1);
			}
		} else if (f1 == 0 && f2 == 0 && f3 == 0) {
			distanceToTriangle = projectionLength;
		} else {
			throw new RuntimeException("Unable to localize projection of point on triangle plane, f1=" + f1 + 
					", f2=" + f2 + ", f3=" + f3);
		}
		return Math.abs(distanceToTriangle);
	}

	private static double distanceToTriangleUtil(Vect3d projected, Vect3d left, Vect3d right,
				Vect3d p0, double projectionLength,
				Vect3d tmp1, Vect3d tmp2) {		// transmitted as working buffers, to avoid expensive allocations
		double distanceToTriangle = 0;
		
		tmp1.set(right);
		tmp1.sub(projected);
		tmp2.set(left);
		tmp2.sub(projected);
		double tmp2Length = tmp2.length();
		Vect3d tmp3 = new Vect3d(right);
		tmp3.sub(left);
		tmp1.crossFast(tmp2);												// 8    - r
		tmp1.crossFast(tmp3);
		
		double len = tmp1.length();
		double cosgamma = tmp2.dot(tmp1) / (tmp2Length * len);				// 9
		double projectionToSegmentLength = tmp2Length * cosgamma;			// 10
																		// projectedToSegment (reuse tmp1 for speed)
		tmp1.scale(projectionToSegmentLength / len);						// 11
		tmp3.set(projected);												// projection of 'projected' onto the segment (reuse tmp3 for speed)
		tmp3.add(tmp1);														// 12
			
		double rx = right.getX();
		double ry = right.getY();
		double rz = right.getZ();
		double lx = left.getX();
		double ly = left.getY();
		double lz = left.getZ();
		double px = p0.getX();
		double py = p0.getY();
		double pz = p0.getZ();
		double tx = tmp3.getX();
		double ty = tmp3.getY();
		double tz = tmp3.getZ();
			
		double d1 = (tx-lx)*(tx-lx) + (ty-ly)*(ty-ly) + (tz-lz)*(tz-lz);	// distance to left
		double d2 = (tx-rx)*(tx-rx) + (ty-ry)*(ty-ry) + (tz-rz)*(tz-rz);	// distance to right
		double d = (rx-lx)*(rx-lx) + (ry-ly)*(ry-ly) + (rz-lz)*(rz-lz);		// distance between left and right
			
		if(d1<=d && d2<=d) {
//			System.out.println(" closest to line ");
			distanceToTriangle = Math.sqrt(projectionToSegmentLength*projectionToSegmentLength +
					projectionLength*projectionLength);
		} else {
			if(d1<d2) {
//				System.out.println(" closest to vertex left " + left);
				distanceToTriangle = Math.sqrt( (lx-px)*(lx-px) + (ly-py)*(ly-py) + (lz-pz)*(lz-pz) );
			} else {
//				System.out.println(" closest to vertex right " + right);
				distanceToTriangle = Math.sqrt( (rx-px)*(rx-px) + (ry-py)*(ry-py) + (rz-pz)*(rz-pz) );
			}
				
		}
		return distanceToTriangle;
	}

	private static boolean isInsideTriangle(Vect3d point, Vect3d normal, Vect3d left, Vect3d right, 
			Vect3d tmp1, Vect3d tmp2) {		// transmitted as working buffers, to avoid expensive allocations
		tmp1 .set(left);
		tmp1.sub(point);
		tmp2.set(right);
		tmp2.sub(point);
		tmp1.crossFast(tmp2);
		double loc = tmp1.dot(normal);		// 7
		if(loc < 0) {
	//		System.out.println("        ... and outside the triangle.");
			return false;
		} else {
	//		System.out.println("        ... and inside the triangle.");
			return true;
		}
	}
	
	public static double distanceToTriangleExperimental(Node p, Node A, Node B, Node C) {
		double d = Double.MAX_VALUE;
		Node closestNode = null;
		
		Random rand = new Random();
		for(int i=0; i<10000; i++) {
			double a = rand.nextDouble();
			double b = rand.nextDouble();
			Node r = PointInTriangle(a, b, A, B, C);
	
			double dd = distanceBetweenPoints(p, r);
			if(dd < d) {
				d = dd;
				closestNode = r;
			}
		}
		
		Node[] NA = new Node[3];		// check the vertexes as well
		NA[0] = A;
		NA[1] = B;
		NA[2] = C;
		for(int i=0; i<3; i++) {
			double dd = distanceBetweenPoints(p, NA[i]);
			if(dd < d) {
				d = dd;
				closestNode = NA[i];
			}
		}
		
	//	System.out.println("closestNode:  " + closestNode.getX() + ", " + closestNode.getY() + ", " + closestNode.getZ());
		return d;	
	}
	public static double distanceToTriangleExperimental(Node p, Node A, Node B, Node C, String fileName) {
		double d = Double.MAX_VALUE;
		Node closestNode = null;
		
		Random rand = new Random();
		try {
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write("x y z value\n");
		out.write(p.getX()+1 + " " + p.getY() + " " + p.getZ() + " 1\n");
		out.write(p.getX() + " " + p.getY()+1 + " " + p.getZ() + " 1\n");
		out.write(p.getX() + " " + p.getY() + " " + p.getZ()+1 + " 1\n");
	
		for(int i=0; i<10000; i++) {
			double a = rand.nextDouble();
			double b = rand.nextDouble();
			Node r = PointInTriangle(a, b, A, B, C);
			String line = new String(r.getX() + " " + r.getY() + " " + r.getZ() + " 2\n");
			out.write(line);
	
			double dd = distanceBetweenPoints(p, r);
			if(dd < d) {
				d = dd;
				closestNode = r;
			}
		}
		
		Node[] NA = new Node[3];		// check the vertexes as well
		NA[0] = A;
		NA[1] = B;
		NA[2] = C;
		for(int i=0; i<3; i++) {
			String line = new String(NA[i].getX() + " " + NA[i].getY() + " " + NA[i].getZ() + " 2\n");
			out.write(line);
			double dd = distanceBetweenPoints(p, NA[i]);
			if(dd < d) {
				d = dd;
				closestNode = NA[i];
			}
		}
		
		String line1 = new String(closestNode.getX() + " " + closestNode.getY() + " " + closestNode.getZ() + " 3\n");
		out.write(line1);
	//	System.out.println("closestNode:  " + closestNode.getX() + ", " + closestNode.getY() + ", " + closestNode.getZ());
		out.close();
		} catch (IOException e) {
		}
		return d;
	}
		private static double distanceBetweenPoints(Node p, Node r) {
		double dd = Math.sqrt(	(p.getX()-r.getX())*(p.getX()-r.getX()) + 
								(p.getY()-r.getY())*(p.getY()-r.getY()) + 
								(p.getZ()-r.getZ())*(p.getZ()-r.getZ()) );
		return dd;
	}

	private static Node PointInTriangle(double a, double b, Node A, Node B, Node C)
	{
		double c = 0;
		double px, py, pz;
		if (a + b > 1)
		{
			a = 1 - a;
			b = 1 - b;
		}
		c = 1 - a - b;
	
		px = (a * A.getX()) + (b * B.getX()) + (c * C.getX());
		py = (a * A.getY()) + (b * B.getY()) + (c * C.getY());
		pz = (a * A.getZ()) + (b * B.getZ()) + (c * C.getZ());
		Node point = new Node(px, py, pz);
		return point;
	}


	public static void main(String args[]){
		try {
			double distanceToTriangle3d = 0;
			
			Node nt1 = new Node(1, 0, 0);
			Node nt2 = new Node(0, 2, 0);
			Node nt3 = new Node(0, 0, 0.05);
			
			Vect3d t1 = new Vect3d(nt1);
			Vect3d t2 = new Vect3d(nt2);
			Vect3d t3 = new Vect3d(nt3);
			
			{
				Vect3d xt1 = new Vect3d(1,0,5);
				Vect3d xt2 = new Vect3d(0,2,5);
				Vect3d xt3 = new Vect3d(0,0,5);
	
				Vect3d p1 = new Vect3d(-1,-1,8);
				Vect3d p2 = new Vect3d(-1,-1,2);
	
				double d1 = DistanceToPlane(p1, xt1, xt2, xt3);
				double d2 = DistanceToPlane(p2, xt1, xt2, xt3);
				System.out.println(d1 + ", " + d2);
				
				d1 = distanceToTriangle3d(p1, xt1, xt2, xt3);
				d2 = distanceToTriangle3d(p2, xt1, xt2, xt3);
				System.out.println(d1 + ", " + d2);
			}
				
			{		// quadrant test
			Vect3d a = new Vect3d(0.5, -1, 3);		// inside t1, t3 quadrant
			Vect3d b = new Vect3d(-1, 1, 3);		// inside t2, t3 quadrant
			Vect3d c = new Vect3d(2, 2, 3);			// inside t1, t2 quadrant
			
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant");
			distanceToTriangle3d = distanceToTriangle3d(b, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant");
			distanceToTriangle3d = distanceToTriangle3d(c, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant");
			}
		
			{
			Vect3d a = new Vect3d();
			a.set(-0.5, -4, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to t3 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(0.5, -1, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(1.2, -2, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to t1 vertex");
			System.out.println(" -------------------------------------------------- ");
	
			a.set(-0.5, -4, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to t3 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(0.5, -1, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(1.2, -2, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t3 quadrant, closest to t1 vertex");
			System.out.println(" ==================================================== ");
			
			a.set(3, 0.5, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to t1 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(2, 2, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(0.5, 4, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to t2 vertex");
			System.out.println(" -------------------------------------------------- ");
	
			a.set(3, 0.5, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to t1 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(2, 2, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(0.5, 4, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t1, t2 quadrant, closest to t2 vertex");
			System.out.println(" ================================================== ");
	
			a.set(-1.5, 3, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to t2 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(-1, 1, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(-3, -0.5, 3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to t3 vertex");
			System.out.println(" -------------------------------------------------- ");
	
			a.set(-1.5, 3, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to t2 vertex");
			System.out.println(" -------------------------------------------------- ");
			a.set(-1, 1, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to line");
			System.out.println(" -------------------------------------------------- ");
			a.set(-3, -0.5, -3);
			distanceToTriangle3d = distanceToTriangle3d(a, t1, t2, t3);
			System.out.println("Should say it's inside t2, t3 quadrant, closest to t3 vertex");
			System.out.println(" ===================================================== ");
			}
				
			{		// exact distance test
			double distanceToTriangleExperimental = 0;
			Node np1 = new Node(0.3, 0.3, 3);	// inside the triangle
			Node np2 = new Node(-1, -1, 3);		// closest to vertex
			Node np3 = new Node(1, 1, 3);		// closest to line
			Node np4 = new Node(0.5, -0.5, 3);	// closest to line
			
			Vect3d p1 = new Vect3d(np1);
			Vect3d p2 = new Vect3d(np2);
			Vect3d p3 = new Vect3d(np3);
			Vect3d p4 = new Vect3d(np4);
	
			distanceToTriangleExperimental = distanceToTriangleExperimental(np1, nt1, nt2, nt3);
			distanceToTriangle3d = distanceToTriangle3d(p1, t1, t2, t3);
			System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (inside the triangle) - should be " + distanceToTriangleExperimental);
			System.out.println(" -------------------------------------------------- ");
			
			distanceToTriangleExperimental = distanceToTriangleExperimental(np2, nt1, nt2, nt3);
			distanceToTriangle3d = distanceToTriangle3d(p2, t1, t2, t3);
			System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to vertex) - should be " + distanceToTriangleExperimental);
			System.out.println(" -------------------------------------------------- ");
			
			distanceToTriangleExperimental = distanceToTriangleExperimental(np3, nt1, nt2, nt3);
			distanceToTriangle3d = distanceToTriangle3d(p3, t1, t2, t3);
			System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to line) - should be " + distanceToTriangleExperimental);
			System.out.println(" -------------------------------------------------- ");
			
			distanceToTriangleExperimental = distanceToTriangleExperimental(np4, nt1, nt2, nt3);
			distanceToTriangle3d = distanceToTriangle3d(p4, t1, t2, t3);
			System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to line) - should be " + distanceToTriangleExperimental);
			System.out.println(" -------------------------------------------------- ");
			}
			
			{	// some particular / unusual conditions
				Node nnt1 = new Node(1, 0, 0); 
				Node nnt2 = new Node(0, 1, 0);
				Node nnt3 = new Node(0, 0, 1);
				
				Vect3d tt1 = new Vect3d(nnt1);
				Vect3d tt2 = new Vect3d(nnt2);
				Vect3d tt3 = new Vect3d(nnt3);

				Node np1 = new Node(1, 1, 1);		// in the center of the triangle
				Node np2 = new Node(1, 0, 0);		// right in 1 vertex
				
				Vect3d p1 = new Vect3d(np1);
				Vect3d p2 = new Vect3d(np2);
		
				double distanceToTriangleExperimental = distanceToTriangleExperimental(np1, nnt1, nnt2, nnt3);
				distanceToTriangle3d = distanceToTriangle3d(p1, tt1, tt2, tt3);
				System.out.println("Distance to triangle = " + distanceToTriangle3d + "  - should be " + distanceToTriangleExperimental);
				System.out.println(" -------------------------------------------------- ");
				
				distanceToTriangleExperimental = distanceToTriangleExperimental(np2, nnt1, nnt2, nnt3);
				distanceToTriangle3d = distanceToTriangle3d(p2, tt1, tt2, tt3);
				System.out.println("Distance to triangle = " + distanceToTriangle3d + "  - should be " + distanceToTriangleExperimental);
				System.out.println(" -------------------------------------------------- ");
			}
				
			{		// an error case, to be fixed
		//				47, 37, 39
		//				44, 44, 38	closest
		//				56, 38, 49
		//				17, 60, 40	test point
		//				- from points: 31.692043572731926
		//				- exact comp : 45.74405664170047
		//				- error: 14.052013068968542
					
				Node ntt1 = new Node(47, 37, 39);
				Node ntt2 = new Node(44, 44, 38);		// closest to this one
				Node ntt3 = new Node(56, 38, 49);
				Node naa = new Node(17, 60, 40);
				
				Vect3d tt1 = new Vect3d(ntt1);
				Vect3d tt2 = new Vect3d(ntt2);
				Vect3d tt3 = new Vect3d(ntt3);
				Vect3d aa = new Vect3d(naa);
				System.out.println("Point: " + aa);
				System.out.println("Node A:  " + ntt1.getX() + ", " + ntt1.getY() + ", " + ntt1.getZ());
				System.out.println("Node B:  " + ntt2.getX() + ", " + ntt2.getY() + ", " + ntt2.getZ());
				System.out.println("Node C:  " + ntt3.getX() + ", " + ntt3.getY() + ", " + ntt3.getZ());

				
				double x1 = distanceBetweenPoints(naa, ntt1);
				double x2 = distanceBetweenPoints(naa, ntt2);
				double x3 = distanceBetweenPoints(naa, ntt3);
				System.out.println("Distance to verteces: " + x1 +", " + x2 + ", " + x3);
				double distanceToTriangleExperimental = distanceToTriangleExperimental(naa, ntt1, ntt2, ntt3);
				distanceToTriangle3d = distanceToTriangle3d(aa, tt1, tt2, tt3);
				System.out.println("Distance to triangle = " + distanceToTriangle3d + "  (closest to vertex t2) - should be " + distanceToTriangleExperimental);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
			
		// we work inside a cube of 100x100x100
		// we pick a triangle inside and we fill it with random points
		// we randomly generate points within the cube and we compute the distance to triangle in 2 ways:
		// using distanceToTriangle3d()
		// we compute the distance from the point to each of the points within the triangle and we keep the smallest result
		// different results mean that there are errors in distanceToTriangle3d()
		double error = 0.05;
		Random rand = new Random();
		Node testPoint = new Node();
		Node A = new Node(30+rand.nextDouble()*30, 30+rand.nextDouble()*30, 30+rand.nextDouble()*30);
		Node B = new Node(30+rand.nextDouble()*30, 30+rand.nextDouble()*30, 30+rand.nextDouble()*30);
		Node C = new Node(30+rand.nextDouble()*30, 30+rand.nextDouble()*30, 30+rand.nextDouble()*30);
		System.out.println(" -------------------------------------------------- ");
		System.out.println("Generate random triangles and compare the computed distance with the smallest of the ");
		System.out.println("pithagorean distances from point to multiple randomly generated points within the triangle.");
		System.out.println("Node A:  " + A.getX() + ", " + A.getY() + ", " + A.getZ());
		System.out.println("Node B:  " + B.getX() + ", " + B.getY() + ", " + B.getZ());
		System.out.println("Node C:  " + C.getX() + ", " + C.getY() + ", " + C.getZ());
		
		Vect3d vTestPoint = new Vect3d();
		Vect3d vA = new Vect3d(A);
		Vect3d vB = new Vect3d(B);
		Vect3d vC = new Vect3d(C);
		
		System.out.println("Display the error information if discrepancy is > " + error);
		int counter = 0;
		for(int i=0; i<2000; i++) {			// randomly generate some points and compute the distance from them to the triangle
		
			testPoint.setX(rand.nextDouble()*100);
			testPoint.setY(rand.nextDouble()*100);
			testPoint.setZ(rand.nextDouble()*100);
			vTestPoint.set(testPoint);
		
			double eD = distanceToTriangleExperimental(testPoint, A, B, C);
			double eE = distanceToTriangle3d(vTestPoint, vA, vB, vC);
			if(Math.abs(eD-eE) > error) {
				System.out.println("testPoint:  " + testPoint.getX() + ", " + testPoint.getY() + ", " + testPoint.getZ());
				System.out.println("distanceToTriangle3d distanc: " + eD + ",   experimental distance: " + eE + ",    error is: " + Math.abs(eD-eE));
				counter++;
			}
		}
		System.out.println(counter + " errors");
	}


}
