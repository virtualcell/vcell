package cbit.vcell.solvers;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;

import org.apache.log4j.Logger;
import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.NumberUtils;
import org.vcell.util.Origin;

import cbit.vcell.math.MathFormatException;
import cbit.vcell.simdata.DataIdentifier;


public class CartesianMeshChombo extends CartesianMesh {
	private static final long serialVersionUID = -6830066634726835851L;
	protected static Logger logger = Logger.getLogger(CartesianMeshChombo.class);
	
	public static class FeaturePhaseVol implements Serializable
	{
		public String feature;
		public int iphase;
		public int ivol;
		
		FeaturePhaseVol(String feature, int iphase, int ivol)
		{
			this.feature = feature;
			this.iphase = iphase;
			this.ivol = ivol;
		}
	}
	
	private static class MembraneId implements Serializable
	{
		public int id;
		public String membrane;
		
		MembraneId(int id, String membrane)
		{
			this.id = id;
			this.membrane = membrane;
		}
	}
	
	private static class SurfaceTriangleEntry3D implements Serializable {
		public int memIndex;
		public int faceIndex = 0;
		public Coordinate p0;
		public Coordinate p1;
		public Coordinate p2;
	}
	
	public static class Segment2D implements Serializable {
		public int memIndex;
		public int prevVertex;
		public int nextVertex;
		public int prevNeigbhor;
		public int nextNeigbhor;
		
		private Segment2D(int memIndex, int prevVertex, int nextVertex,
				int prevNeigbhor, int nextNeigbhor) {
			super();
			this.memIndex = memIndex;
			this.prevVertex = prevVertex;
			this.nextVertex = nextVertex;
			this.prevNeigbhor = prevNeigbhor;
			this.nextNeigbhor = nextNeigbhor;
		}
		
	}
	
	private static class SliceViewEntry implements Serializable {
		public int memIndex;
		public double[] crossPoints;
	}
	
	private static class MembraneElementMetricsEntry implements Serializable
	{
		public int index, level, i,j,k;
//		public double x,y,z;
//		public double normalx, normaly, normalz;
//		double volFrac;
//		double areaFrac;
//		public int membraneId;
//		private int cornerPhaseMask;
		private MembraneElementMetricsEntry(int index, int level, int i, int j, int k)
		{
			super();
			this.index = index;
			this.level = level;
			this.i = i;
			this.j = j;
			this.k = k;
		}		
	}
	
	private enum StructureType
	{
		feature,
		membrane,
	}
	
	public static class StructureMetricsEntry implements Serializable
	{
		private String name;
		private StructureType type;
		private double size;
		private int numPoints;
		private StructureMetricsEntry(String name, StructureType type, double size, int numPoints) {
			super();
			this.name = name;
			this.type = type;
			this.size = size;
			this.numPoints = numPoints;
		}
		public String getDisplayLabel()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(name)
			.append(" size=" + NumberUtils.formatNumber(size))
			.append(" #points=" + numPoints);
			return sb.toString();
		}
	}
		
	private int dimension = 0;
	private int viewLevel = 0;
	private int numLevels = 0;
	private int[] refineRatios;

	private transient double[] dx;
	private transient MembraneElementMetricsEntry[] membraneElementMetrics = null;
	private transient StructureMetricsEntry[] structureMetrics = null;
	private transient FeaturePhaseVol[] featurePhaseVols = null;
	private transient MembraneId[] membraneIds = null;
	
	// 3D
	private transient Map<Integer, List<SurfaceTriangleEntry3D>> surfaceTriangleMap = new HashMap<Integer, List<SurfaceTriangleEntry3D>>();
//	private transient List<MeshMetricsEntry> metrics = new ArrayList<MeshMetricsEntry>();
	private transient List<SliceViewEntry> sliceViewList = new ArrayList<SliceViewEntry>();
	
	// 2D
	private transient Coordinate[] vertices = null;
	private transient Segment2D[] segments = null;
	
//	private static final String ROOT_GROUP = "/";
//	private static final String MESH_GROUP = "/mesh";
	private enum MeshDataSet
	{
		boxes,
		featurephasevols,
		membrane_elements_old("metrics"),   // old name of membrane_elements
		membrane_elements("membrane elements"),
		membraneids,
		segments,
		slice_view("slice view"),
		structures,
		surface_triangles("surface triangles"),
		vertices;
		
		String name;
		MeshDataSet() {
			this.name = name();
		}
		MeshDataSet(String name) {
			this.name = name;
		}
		static MeshDataSet valueOfName(String n)
		{
			for (MeshDataSet ds : values())
			{
				if (ds.name.equals(n))
				{
					return ds;
				}
			}
			return null;
		}
	}
	
	private enum MeshAttribute
	{
		dimension,
		Dx,
		extent,
		numLevels,
		Nx,
		origin,
		refineRatios,
		viewLevel,
	}
	
	public static CartesianMeshChombo readMeshFile(File chomboMeshFile) throws Exception {
		CartesianMeshChombo chomboMesh = new CartesianMeshChombo();
		if(H5.H5open() < 0){
			throw new Exception("H5.H5open() failed");
		}
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		if(fileFormat == null){
			throw new Exception("FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5) failed, returned null.");
		}
		FileFormat meshFile = null;
		try
		{
			meshFile = fileFormat.createInstance(chomboMeshFile.getAbsolutePath(), FileFormat.READ);
			meshFile.open();
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)meshFile.getRootNode();
			Group rootGroup = (Group)rootNode.getUserObject();
			Group meshGroup = (Group)rootGroup.getMemberList().get(0);
			List<Attribute> meshAttrList = meshGroup.getMetadata();
			
			for (Attribute attr : meshAttrList)
			{
				String attrName = attr.getName();
				MeshAttribute mattr = null;
				try 
				{
					mattr = MeshAttribute.valueOf(attrName);
				}
				catch (IllegalArgumentException ex)
				{
				}
				if (mattr == null)
				{
					// if not found, then we don't care about this attribute
					logger.debug("mesh attribute " + attrName + " is not defined in Java");
					continue;
				}
				Object value = attr.getValue();
				switch (mattr)
				{
				case dimension:
					chomboMesh.dimension = ((int[])value)[0];
					break;
				case numLevels:
					chomboMesh.numLevels = ((int[])value)[0];
					break;
				case viewLevel:
					chomboMesh.viewLevel = ((int[])value)[0];
					break;
				case refineRatios:
					chomboMesh.refineRatios = (int[])value;
					break;
				case Dx:
				case extent:
				case Nx:
				case origin:
					// these 4 has format of {};
					String[] valueStrArray = (String[])value;
					String value0 = valueStrArray[0];
					StringTokenizer st = new StringTokenizer(value0, "{,} ");
					int numTokens = st.countTokens();
					double[] values = new double[Math.max(3, numTokens)]; // we need 3 for 3d
					for (int i = 0; i < Math.min(3,  numTokens); ++ i)
					{
						String token = st.nextToken();
						values[i] = Double.parseDouble(token);
					}
					switch (mattr)
					{
						case Dx:
							chomboMesh.dx = new double[3];
							System.arraycopy(values, 0, chomboMesh.dx, 0, values.length);
							break;
						case extent:
							chomboMesh.extent = new Extent(values[0], values[1], values[2] == 0 ? 1 : values[2]);
							break;
						case Nx:
							chomboMesh.size = new ISize((int)values[0], (int)values[1], values[2] == 0 ? 1 : (int)values[2]);
							break;
						case origin:
							chomboMesh.origin = new Origin(values[0], values[1], values[2]);
							break;
					}
					break;
				}
			}
			
			List<HObject> memberList = meshGroup.getMemberList();
			for (HObject member : memberList)
			{
				if (!(member instanceof Dataset))
				{
					continue;
				}
				Dataset dataset = (Dataset)member;
				Vector vectValues = (Vector)dataset.read();
				String name = dataset.getName();
				MeshDataSet mdataset = null;
				
				try
				{
					mdataset = MeshDataSet.valueOfName(name);
				}
				catch (IllegalArgumentException ex)
				{
					logger.debug("mesh dataset " + name + " is not defined in Java");
				}
				if (mdataset == null)
				{
					// if not found, then we don't care about this dataset
					continue;
				}
				switch(mdataset)
				{
					case vertices:
						collectVertices(chomboMesh, vectValues);
						break;
					case segments:
						collect2dSegments(chomboMesh, vectValues);
						break;
					case structures:
						collectStructures(chomboMesh, vectValues);
						break;
					case featurephasevols:
						collectFeaturePhaseVols(chomboMesh, vectValues);
						break;
					case membraneids:
						collectMembraneIds(chomboMesh, vectValues);
						break;
					case membrane_elements:
					case membrane_elements_old:
						collectMembraneElements(chomboMesh, vectValues);
						break;
					case surface_triangles:
						collect3dSurfaceTriangles(chomboMesh, vectValues);
						break;
					case slice_view:
						collect3dSliceView(chomboMesh, vectValues);
						break;
				}
			}
		}
		finally
		{
			if (meshFile != null)
			{
				meshFile.close();
			}
		}
		// set neighbors to membrane elements
		if (chomboMesh.dimension == 2 && chomboMesh.membraneElements!=null)
		{
			for (int i = 0; i < chomboMesh.membraneElements.length; ++ i)
			{
				MembraneElement me = chomboMesh.membraneElements[i];
				me.setConnectivity(chomboMesh.segments[i].prevNeigbhor, chomboMesh.segments[i].nextNeigbhor, -1, -1);
			}
		}
		return chomboMesh;
	}

	private static void collectMembraneIds(CartesianMeshChombo chomboMesh, Vector vectValues) {
		int c = -1;
		int[] id = (int[]) vectValues.get(++ c);
		String[] membrane = (String[]) vectValues.get(++ c);
		chomboMesh.membraneIds = new MembraneId[id.length];
		for (int n = 0; n < id.length; ++ n)
		{
			chomboMesh.membraneIds[n] = new MembraneId(id[n], membrane[n]);
		}
	}

	private static void collectVertices(CartesianMeshChombo chomboMesh, Vector vectValues) {
		int c = -1;
		double[] x = (double[]) vectValues.get(++ c);
		double[] y = (double[]) vectValues.get(++ c);
		double[] z = null;
		if (chomboMesh.dimension == 3)
		{
			z = (double[]) vectValues.get(++ c);
		}
		chomboMesh.vertices = new Coordinate[x.length];
		for (int i = 0; i < x.length; ++ i)
		{
			chomboMesh.vertices[i] = new Coordinate(x[i], y[i], z == null ? 0 : z[i]);
		}
	}

	private static void collect2dSegments(CartesianMeshChombo chomboMesh, Vector vectValues) {
		int c = -1;
		int[] index = (int[]) vectValues.get(++ c);
		int[] prevVertex = (int[]) vectValues.get(++ c);
		int[] nextVertex = (int[]) vectValues.get(++ c);
		int[] prevNeighbor = (int[]) vectValues.get(++ c);
		int[] nextNeighbor = (int[]) vectValues.get(++ c);
		double[] z = null;
		if (chomboMesh.dimension == 3)
		{
			z = (double[]) vectValues.get(++ c);
		}
		chomboMesh.segments = new Segment2D[index.length];
		for (int i = 0; i < index.length; ++ i)
		{
			chomboMesh.segments[i] = new Segment2D(index[i], prevVertex[i], nextVertex[i], prevNeighbor[i], nextNeighbor[i]);
		}
	}

	private static void collectStructures(CartesianMeshChombo chomboMesh, Vector vectValues) {
		int c = -1;
		String[] sname = (String[]) vectValues.get(++ c);
		String[] type = (String[]) vectValues.get(++ c);
		double[] size = (double[]) vectValues.get(++ c);
		int[] numPoints = (int[]) vectValues.get(++ c);
		chomboMesh.structureMetrics = new StructureMetricsEntry[sname.length];
		for (int n = 0; n < sname.length; ++ n)
		{
			chomboMesh.structureMetrics[n] = new StructureMetricsEntry(sname[n], StructureType.valueOf(type[n]), size[n], numPoints[n]);
		}
	}

	private static void collectFeaturePhaseVols(CartesianMeshChombo chomboMesh, Vector vectValues) {
		int c = -1;
		String[] feature = (String[]) vectValues.get(++ c);
		int[] iphase = (int[]) vectValues.get(++ c);
		int[] ivol = (int[]) vectValues.get(++ c);
		chomboMesh.featurePhaseVols = new FeaturePhaseVol[feature.length];
		for (int n = 0; n < feature.length; ++ n)
		{
			chomboMesh.featurePhaseVols[n] = new FeaturePhaseVol(feature[n], iphase[n], ivol[n]);
		}
	}
	
	private static void collect3dSliceView(CartesianMeshChombo chomboMesh, Vector vectValues) {
		int c = 0;
		int[] index = (int[]) vectValues.get(c);
		double[][] coords = new double[12][index.length];
		for (int n = 0; n < 12; ++ n) {
			coords[n] = (double[])vectValues.get(++ c);
		}					
		for (int n = 0; n < index.length; ++ n) {
			SliceViewEntry sve = new SliceViewEntry();
			sve.memIndex = index[n];
			sve.crossPoints = new double[12];
			for (int m = 0; m < 12; ++ m)
			{
				sve.crossPoints[m] = coords[m][n];							
			}
			chomboMesh.sliceViewList.add(sve);
		}
	}

	private static void collectMembraneElements(CartesianMeshChombo chomboMesh, Vector vectValues) throws MathFormatException {
		int c = -1;
		int[] index = (int[]) vectValues.get(++ c);
		int[] level = (int[]) vectValues.get(++ c);
		int[] i = (int[]) vectValues.get(++ c);
		int[] j = (int[]) vectValues.get(++ c);
		int[] k = null;
		if (chomboMesh.dimension == 3)
		{
			k = (int[]) vectValues.get(++ c);
		}
		double[] x = (double[])vectValues.get(++ c);
		double[] y = (double[])vectValues.get(++ c);
		double[] z = null;
		if (chomboMesh.dimension == 3)
		{
			z = (double[])vectValues.get(++ c);
		}
		double[] normalx = (double[])vectValues.get(++ c);
		double[] normaly = (double[])vectValues.get(++ c);
		double[] normalz = null;
		if (chomboMesh.dimension == 3)
		{
			normalz = (double[])vectValues.get(++ c);
		}
		double[] volFrac = (double[])vectValues.get(++ c);
		double[] areaFrac = (double[])vectValues.get(++ c);
		//int[] membraneId = (int[]) vectValues.get(++ c);
		//int[] cornerPhaseMask = (int[]) vectValues.get(++ c);
		double unitVol = chomboMesh.dx[0] * chomboMesh.dx[1] * chomboMesh.dx[2];
		chomboMesh.membraneElements = new MembraneElement[index.length];
		chomboMesh.membraneElementMetrics = new MembraneElementMetricsEntry[index.length];
		for (int n = 0; n < index.length; ++ n)
		{
			int insideIndex = (k == null ? 0 : k[n] * chomboMesh.size.getY() * chomboMesh.size.getX())
					+ j[n] * chomboMesh.size.getX() + i[n];
			int outsideIndex = insideIndex;
			double area = areaFrac[n] * unitVol;
			chomboMesh.membraneElementMetrics[n] = new MembraneElementMetricsEntry(index[n], level[n], i[n], j[n], k == null ? 0 : k[n]);
			chomboMesh.membraneElements[n] = new MembraneElement(index[n], insideIndex, outsideIndex,
					-1, -1, -1, -1,
					(float)area,
					(float)normalx[n],
					(float)normaly[n],
					(float)(normalz == null ? 0 : normalz[n]),
					(float)x[n],
					(float)y[n],
					(float)(z == null ? 0 : z[n]));
		}
	}

	private static void collect3dSurfaceTriangles(CartesianMeshChombo chomboMesh, Vector vectValues)
	{
		int c = 0;
		int[] index = (int[]) vectValues.get(c);
		int[] faceIndex = (int[]) vectValues.get(++ c);
		int[] neighbor = (int[])vectValues.get(++ c);
		double[] x0 = (double[])vectValues.get(++ c);
		double[] y0 = (double[])vectValues.get(++ c);
		double[] z0 = null;
		if (chomboMesh.dimension == 3)
		{
			z0 = (double[])vectValues.get(++ c);
		}
		double[] x1 = (double[])vectValues.get(++ c);
		double[] y1 = (double[])vectValues.get(++ c);
		double[] z1 = null;
		if (chomboMesh.dimension == 3)
		{
			z1 = (double[])vectValues.get(++ c);
		}
		double[] x2 = (double[])vectValues.get(++ c);
		double[] y2 = (double[])vectValues.get(++ c);
		double[] z2 = null;
		if (chomboMesh.dimension == 3)
		{
			z2 = (double[])vectValues.get(++ c);
		}
		for (int n = 0; n < index.length; ++ n)
		{
			int memIndex = index[n];
			List<SurfaceTriangleEntry3D> edgeList = chomboMesh.surfaceTriangleMap.get(memIndex);
			if (edgeList == null)
			{
				edgeList = new ArrayList<SurfaceTriangleEntry3D>();
				chomboMesh.surfaceTriangleMap.put(index[n], edgeList);
			}
			SurfaceTriangleEntry3D mp = new SurfaceTriangleEntry3D();
			mp.memIndex = index[n];
			mp.faceIndex = faceIndex[n];
			mp.p0 = new Coordinate(x0[n], y0[n], z0 == null ? 0 : z0[n]);
			mp.p1 = new Coordinate(x1[n], y1[n], z1 == null ? 0 : z1[n]);
			mp.p2 = new Coordinate(x2[n], y2[n], z2 == null ? 0 : z2[n]);
			edgeList.add(mp);
		}
	}
	
	public int getDimension() {
		return dimension;
	}

	@Override
	public int getGeometryDimension() {
		return dimension;
	}
	
	@Override
	protected Object[] getOutputFields() throws IOException {
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(dimension);
		objectList.add(size);
		objectList.add(origin);
		objectList.add(extent);
		objectList.add(numLevels);
		objectList.add(viewLevel);
		objectList.add(refineRatios);
		objectList.add(membraneElements);
		objectList.add(membraneElementMetrics);
		objectList.add(structureMetrics);
		objectList.add(vertices);
		if (dimension == 2)
		{
			objectList.add(segments);
		}
		else if (dimension == 3)
		{
			objectList.add(surfaceTriangleMap);
			objectList.add(sliceViewList);
		}
		return objectList.toArray(new Object[0]);
	}

	@Override
	protected void inflate() {
		if (compressedBytes == null) {
			return;
		}

		try {
			Object objArray[] = (Object[])BeanUtils.fromCompressedSerialized(compressedBytes);
			int index = 0;
			dimension = (Integer)objArray[index];
			size = (ISize)objArray[++ index];
			origin = (Origin)objArray[++ index];
			extent = (Extent)objArray[++ index];
			numLevels = (Integer)objArray[++ index];
			viewLevel = (Integer)objArray[++ index];
			refineRatios = (int[])objArray[++ index];
			membraneElements = (MembraneElement[])objArray[++ index];
			membraneElementMetrics = (MembraneElementMetricsEntry[])objArray[++ index];
			structureMetrics = (StructureMetricsEntry[])objArray[++ index];
			vertices = (Coordinate[])objArray[++ index];
			if (dimension == 2)
			{
				segments = (Segment2D[])objArray[++ index];
			}
			else if (dimension == 3)
			{
				surfaceTriangleMap = (Map<Integer, List<SurfaceTriangleEntry3D>>)objArray[++ index];
				sliceViewList = (List<SliceViewEntry>)objArray[++ index];
			}

			compressedBytes = null;
			
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	public Map<Integer, List<SurfaceTriangleEntry3D>> getSurfaceMap() {
		return surfaceTriangleMap;
	}
	
	public Segment2D[] get2DSegments() {
		return segments;
	}

	public Coordinate[] getVertices() {
		return vertices;
	}
	
//	public List<MeshMetricsEntry> getMetrics() {
//		return metrics;
//	}
//	
	@Override
	public int getMembraneRegionIndex(int membraneIndex) {
		return 0;
	}
	
	@Override
	public int getVolumeRegionIndex(int volumeIndex) {
		return 0;
	}
	
	public int getSubVolumeFromVolumeIndex(int volIndex) {
		return 0;
	}
	
	@Override
	public Coordinate getCoordinateFromMembraneIndex(int membraneIndex) {
	    MembraneElement me = getMembraneElements()[membraneIndex];
	    return (new Coordinate(me.centroidX, me.centroidY, me.centroidZ));
	}
	
	@Override
	public boolean isChomboMesh()
	{
		return true;
	}
	
	private CoordinateIndex convertDownToLevel(CoordinateIndex ci, int startLevel, int stopLevel)
	{
		int ratio = 1;
		for (int i = startLevel; i < stopLevel; ++ i)
		{
			ratio *= refineRatios[i];
		}
		return new CoordinateIndex(ci.x / ratio, ci.y / ratio, ci.z / ratio);
	}
	
	public int findMembraneIndexFromVolumeIndex(CoordinateIndex ci)
	{
		for (MembraneElementMetricsEntry me : membraneElementMetrics)
		{
			CoordinateIndex ci0 = new CoordinateIndex(me.i, me.j, me.k);
			CoordinateIndex ci1 = ci;
			if (me.level < viewLevel)
			{
				ci1 = convertDownToLevel(ci1, me.level, viewLevel);
			}
			else if (me.level > viewLevel)
			{
			  // in this case we need to convert the coordinates to the fine mesh to find the correct membrane element in me.level
				// we will worry about this later.
				return -1;
			}
			if (ci0.x == ci1.x && ci0.y == ci1.y && (dimension == 2 || ci0.z == ci1.z))
			{
				return me.index;
			}
		}
		return -1;
	}

	public int findMembraneIndexFromVolumeCoordinate(Coordinate wc) 
	{
		if (dx == null)
		{
			dx = new double[3];
			dx[0] = getExtent().getX()/getSizeX();
			dx[1] = getExtent().getY()/getSizeY();
			dx[2] = getExtent().getZ()/getSizeZ();
		}
		int i = (int)((wc.getX() - getOrigin().getX()) / dx[0]);
		int j = (int)((wc.getY() - getOrigin().getY()) / dx[1]);
		int k = dimension < 3 ? 0 : (int)((wc.getZ() - getOrigin().getZ()) / dx[2]);
		return findMembraneIndexFromVolumeIndex(new CoordinateIndex(i, j, k));
	}

	public StructureMetricsEntry getStructureInfo(DataIdentifier dataIdentifier) {
		if (structureMetrics != null && dataIdentifier != null && dataIdentifier.getDomain() != null)
		{
			for (StructureMetricsEntry sme : structureMetrics)
			{
				if (sme.name != null && sme.name.equals(dataIdentifier.getDomain().getName()))
				{
					return sme;
				}
			}
		}
		return null;
	}

	@Override
	public Coordinate getCoordinate(CoordinateIndex coordIndex) {
		double x = Coordinate.coordComponentFromSinglePlanePolicy(origin,extent,Coordinate.X_AXIS);
		if (getSizeX() > 1){
			x = (coordIndex.x + 0.5) * extent.getX()/getSizeX() + origin.getX();
		}
		double y = Coordinate.coordComponentFromSinglePlanePolicy(origin,extent,Coordinate.Y_AXIS);
		if (getSizeY() > 1){
			y = (coordIndex.y + 0.5) * extent.getY()/getSizeY() + origin.getY();
		}
		double z = Coordinate.coordComponentFromSinglePlanePolicy(origin,extent,Coordinate.Z_AXIS);
		if (getSizeZ() > 1){
			z = (coordIndex.z + 0.5) * extent.getZ()/getSizeZ() + origin.getZ();
		}
		return (new Coordinate(x, y, z));
	}

	public FeaturePhaseVol[] getFeaturePhaseVols()
	{
		return featurePhaseVols;
	}
}
