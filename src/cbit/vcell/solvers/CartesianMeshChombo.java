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

import org.vcell.util.BeanUtils;
import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

@SuppressWarnings("serial")
public class CartesianMeshChombo extends CartesianMesh {

	public static class SurfaceEntry implements Serializable {
		public int memIndex;
		public int faceIndex = 0;
		public Coordinate p0;
		public Coordinate p1;
		public Coordinate p2;
	}
	
	public static class SliceViewEntry implements Serializable {
		public int memIndex;
		public double[] crossPoints;
	}
	
//	public static class MeshMetricsEntry implements Serializable
//	{
//		public int index,i,j,k;
//		//public double x,y,z;
//		//public double normalx, normaly, normalz;
//		//double volFrac;
//		//double areaFrac;
//		public MeshMetricsEntry(int index, int i, int j, int k
////				, double x, double y, double z, double normalx, double normaly,
////				double normalz, double volFrac, double areaFrac
//				) 
//		{
//			super();
//			this.index = index;
//			this.i = i;
//			this.j = j;
//			this.k = k;
////			this.x = x;
////			this.y = y;
////			this.z = z;
////			this.normalx = normalx;
////			this.normaly = normaly;
////			this.normalz = normalz;
////			this.volFrac = volFrac;
////			this.areaFrac = areaFrac;
//		}		
//	}
		
	private int dimension = 0;
	private double[] dx;
//	private transient List<ChomboBox> boxList = new ArrayList<ChomboBox>();
	private transient Map<Integer, List<SurfaceEntry>> surfaceMap = new HashMap<Integer, List<SurfaceEntry>>();
//	private transient List<MeshMetricsEntry> metrics = new ArrayList<MeshMetricsEntry>();
	private transient List<SliceViewEntry> sliceViewList = new ArrayList<SliceViewEntry>();
	
//	private static final String ROOT_GROUP = "/";
//	private static final String MESH_GROUP = "/mesh";
	private static final String BOXES_DATASET = "boxes";
	private static final String METRICS_DATASET = "metrics";
	private static final String SURFACE_DATASET = "surface";
	private static final String SLICE_VIEW_DATASET = "slice view";
	private static final String MESH_ATTR_DIMENSION = "dimension";
	private static final String MESH_ATTR_ORIGIN = "origin";
	private static final String MESH_ATTR_EXTENT = "extent";
	private static final String MESH_ATTR_NX = "Nx";
	private static final String MESH_ATTR_DX = "Dx";
	
	public static CartesianMeshChombo readMeshFile(File chomboMeshFile) throws Exception {
		CartesianMeshChombo chomboMesh = new CartesianMeshChombo();
		if(H5.H5open() < 0){
			throw new Exception("H5.H5open() failed");
		}
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		if(fileFormat == null){
			throw new Exception("FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5) failed, returned null.");
		}
		FileFormat meshFile = fileFormat.createInstance(chomboMeshFile.getAbsolutePath(), FileFormat.READ);
		meshFile.open();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)meshFile.getRootNode();
		Group rootGroup = (Group)rootNode.getUserObject();
		Group meshGroup = (Group)rootGroup.getMemberList().get(0);
		List<Attribute> meshAttrList = meshGroup.getMetadata();
		
		int[] nx = new int[3];
		Arrays.fill(nx, 1);
		chomboMesh.dx = new double[3];
		double[] extent = new double[3];
		Arrays.fill(extent, 1);
		double[] origin = new double[3];
		
		for (Attribute attr : meshAttrList)
		{
			String attrName = attr.getName();
			Object value = attr.getValue();
			if (attrName.equals(MESH_ATTR_DIMENSION))
			{
				chomboMesh.dimension = ((int[])value)[0];
			}
			else
			{
				String[] valueStrArray = (String[])value;
				String value0 = valueStrArray[0];
				StringTokenizer st = new StringTokenizer(value0, "{,} ");
				List<Double> valueList = new ArrayList<Double>();
				while (st.hasMoreTokens())
				{
					String token = st.nextToken();
					valueList.add(Double.parseDouble(token));
				}
				if (attrName.equals(MESH_ATTR_DX))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						chomboMesh.dx[i] = valueList.get(i);
					}
				}
				else if (attrName.equals(MESH_ATTR_EXTENT))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						extent[i] = valueList.get(i);
					}
					chomboMesh.extent = new Extent(extent[0], extent[1], extent[2]);
				}
				else if (attrName.equals(MESH_ATTR_NX))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						nx[i] = valueList.get(i).intValue();
					}
					chomboMesh.size = new ISize(nx[0], nx[1], nx[2]);
				}
				else if (attrName.equals(MESH_ATTR_ORIGIN))
				{
					for (int i = 0; i < valueList.size(); ++ i)
					{
						origin[i] = valueList.get(i);
					}
					chomboMesh.origin = new Origin(origin[0], origin[1], origin[2]);
				}
			}
		}
		
		List<HObject> memberList = meshGroup.getMemberList();
		for (HObject member : memberList)
		{
			if (member instanceof Dataset)
			{
				Dataset dataset = (Dataset)member;
				Vector vectValues = (Vector)dataset.read();
				String name = dataset.getName();
				if (name.equals(BOXES_DATASET))
				{
					// not needed right now
				}
				else if (name.equals(METRICS_DATASET))
				{
					int c = -1;
					int[] index = (int[]) vectValues.get(++ c);
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
					chomboMesh.membraneElements = new MembraneElement[index.length];
					for (int n = 0; n < index.length; ++ n)
					{
//						MeshMetricsEntry entry = new MeshMetricsEntry(index[n], i[n], j[n], k == null ? 0 : k[n]
////								, x[n], y[n], z == null ? 0 : z[n]
////								, normalx[n], normaly[n], normalz == null ? 0 : normalz[n],
////										volFrac[n], areaFrac[n]
//							);
//						chomboMesh.metrics.add(entry);
						int insideIndex = (k == null ? 0 : k[n] * chomboMesh.size.getY() * chomboMesh.size.getX())
								+ j[n] * chomboMesh.size.getX() + i[n];
						int outsideIndex = insideIndex;
						int neighbor1 = -1;
						int neighbor2 = -1;
						int neighbor3 = -1;
						int neighbor4 = -1;
						double area = areaFrac[n] * chomboMesh.dx[0] * chomboMesh.dx[1] * chomboMesh.dx[2];
						chomboMesh.membraneElements[n] = new MembraneElement(index[n], insideIndex, outsideIndex,
								neighbor1, neighbor2, neighbor3, neighbor4,
								(float)area,
								(float)normalx[n],
								(float)normaly[n],
								(float)(normalz == null ? 0 : normalz[n]),
								(float)x[n],
								(float)y[n],
								(float)(z == null ? 0 : z[n]));
					}
				}
				else if (name.equals(SURFACE_DATASET))
				{
					int c = 0;
					int[] index = (int[]) vectValues.get(c);
					int[] faceIndex = null;
					if (chomboMesh.dimension == 3)
					{
						faceIndex = (int[]) vectValues.get(++ c);
					}
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
						List<SurfaceEntry> edgeList = chomboMesh.surfaceMap.get(index[n]);
						if (edgeList == null)
						{
							edgeList = new ArrayList<SurfaceEntry>();
							chomboMesh.surfaceMap.put(index[n], edgeList);
						}
						SurfaceEntry mp = new SurfaceEntry();
						mp.memIndex = index[n];
						if (chomboMesh.dimension == 3)
						{
							mp.faceIndex = faceIndex[n];
						}
						mp.p0 = new Coordinate(x0[n], y0[n], z0 == null ? 0 : z0[n]);
						mp.p1 = new Coordinate(x1[n], y1[n], z1 == null ? 0 : z1[n]);
						mp.p2 = new Coordinate(x2[n], y2[n], z2 == null ? 0 : z2[n]);
						edgeList.add(mp);
					}
				}
				else if (name.equals(SLICE_VIEW_DATASET))
				{
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
			}
		}
		return chomboMesh;
	}

	@Override
	public int getGeometryDimension() {
		return dimension;
	}
	
	@Override
	protected Object[] getOutputFields() throws IOException {
		return new Object[] {dimension, size, origin, extent, membraneElements, /*metrics, */surfaceMap, sliceViewList};
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
			membraneElements = (MembraneElement[])objArray[++ index];
			surfaceMap = (Map<Integer, List<SurfaceEntry>>)objArray[++ index];
			sliceViewList = (List<SliceViewEntry>)objArray[++ index];

			compressedBytes = null;
			
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	public Map<Integer, List<SurfaceEntry>> getSurfaceMap() {
		return surfaceMap;
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
	
	public int findMembraneIndexForVolumeIndex(CoordinateIndex ci)
	{
		int volIndex = getVolumeIndex(ci);
		for (MembraneElement me : membraneElements)
		{
			if (me.getInsideVolumeIndex() == volIndex)
			{
				return me.getMembraneIndex();
			}
		}
		return -1;
	}
}
