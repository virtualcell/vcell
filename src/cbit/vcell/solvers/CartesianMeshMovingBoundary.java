package cbit.vcell.solvers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import ncsa.hdf.hdf5lib.H5;
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
import org.vcell.util.Origin;


public class CartesianMeshMovingBoundary extends CartesianMesh 
{
	private static Logger logger = Logger.getLogger(CartesianMeshMovingBoundary.class);
	private static final String Group_Mesh = "Mesh";
	private int dimension;

	public enum MBSDataGroup
	{
		Mesh,
		Solution,
	}
	
	public enum MSBDataAttribute
	{
		name,
		time,
		size,
		type,
	}
	
	public enum MSBDataAttributeValue
	{
		Point,
		Volume,
		PointSubDomain,
	}
	
	private enum MeshDataset
	{
		dimension,
		size,
		extent,
		origin,
	}
	
	public static CartesianMeshMovingBoundary readMeshFile(File meshFile) throws Exception {
		CartesianMeshMovingBoundary mesh = new CartesianMeshMovingBoundary();
		if(H5.H5open() < 0){
			throw new Exception("H5.H5open() failed");
		}
		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
		if(fileFormat == null){
			throw new Exception("FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5) failed, returned null.");
		}
		FileFormat meshH5File = null;
		try
		{
			meshH5File = fileFormat.createInstance(meshFile.getAbsolutePath(), FileFormat.READ);
			meshH5File.open();
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)meshH5File.getRootNode();
			Group rootGroup = (Group)rootNode.getUserObject();
			Group meshGroup = null;
			for (HObject member : rootGroup.getMemberList())
			{
				if (member instanceof Group)
				{
					Group g = (Group)member;
					if (g.getName().equals(Group_Mesh));
					{
						meshGroup = g;
						break;
					}
				}
			}
			
			if (meshGroup == null)
			{
				throw new Exception(Group_Mesh + " group not found in mesh");
			}
			for (HObject member : meshGroup.getMemberList())
			{
				if (member instanceof Dataset)
				{
					Dataset ds = (Dataset)member;
					Object data = ds.getData();
					MeshDataset mds = MeshDataset.valueOf(ds.getName());
					switch(mds)
					{
						case dimension:
							mesh.dimension = ((int[])data)[0];
							break;
						case extent:
						{
							double[] darr = (double[])data;
							mesh.extent = new Extent(darr[0], darr[1], 0.5);
							break;
						}
						case origin:
						{
							double[] darr = (double[])data;
							mesh.origin = new Origin(darr[0], darr[1], 0.5);
							break;
						}
						case size:
						{
							int[] iarr = (int[])data;
							mesh.size = new ISize(iarr[0], iarr[1], 1);
							break;
						}
					}
				}
			}
		}
		finally
		{
			if (meshH5File != null)
			{
				meshH5File.close();
			}
		}
		return mesh;
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
			compressedBytes = null;
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			throw new RuntimeException(ex.getMessage());
		}
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
	
	@Override
	public int getVolumeRegionIndex(int volumeIndex) {
		return 0;
	}
	
	@Override
	public int getSubVolumeFromVolumeIndex(int volIndex) {
		return 0;
	}

}
