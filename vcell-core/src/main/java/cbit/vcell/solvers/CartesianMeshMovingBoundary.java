package cbit.vcell.solvers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.*;

import java.lang.reflect.Array;

import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;


public class CartesianMeshMovingBoundary extends CartesianMesh {
    private static Logger logger = LogManager.getLogger(CartesianMeshMovingBoundary.class);
    private static final String Group_Mesh = "Mesh";
    private int dimension;

    public enum MBSDataGroup {
        Mesh,
        Solution,
    }

    public enum MSBDataAttribute {
        name,
        time,
        size,
        type,
    }

    public enum MSBDataAttributeValue {
        Point,
        Volume,
        PointSubDomain,
    }

    private enum MeshDataset {
        dimension,
        size,
        extent,
        origin,
    }

    public static CartesianMeshMovingBoundary readMeshFile(File meshFile) throws Exception{
        CartesianMeshMovingBoundary mesh = new CartesianMeshMovingBoundary();
        try (HdfFile meshH5File = new HdfFile(meshFile.toPath())) {
            Node meshNode = meshH5File.getChildren().get(Group_Mesh);
            if(!(meshNode instanceof Group)){
                throw new Exception(Group_Mesh + " group not found in mesh");
            }
            for(Node member : ((Group) meshNode).getChildren().values()){
                if(member instanceof Dataset){
                    Dataset ds = (Dataset) member;
                    // extent, origin and size are written as 1x2, so unwrap the outer dimension
                    Object data = ds.getDimensions().length > 1 ? Array.get(ds.getData(), 0) : ds.getData();
                    MeshDataset mds = MeshDataset.valueOf(ds.getName());
                    switch(mds){
                        case dimension:
                            mesh.dimension = ((int[]) data)[0];
                            break;
                        case extent:{
                            double[] darr = (double[]) data;
                            mesh.extent = new Extent(darr[0], darr[1], 0.5);
                            break;
                        }
                        case origin:{
                            double[] darr = (double[]) data;
                            mesh.origin = new Origin(darr[0], darr[1], 0.5);
                            break;
                        }
                        case size:{
                            int[] iarr = (int[]) data;
                            mesh.size = new ISize(iarr[0], iarr[1], 1);
                            break;
                        }
                    }
                }
            }
        }
        return mesh;
    }

    public int getDimension(){
        return dimension;
    }

    @Override
    public int getGeometryDimension(){
        return dimension;
    }

    @Override
    protected Object[] getOutputFields() throws IOException{
        List<Object> objectList = new ArrayList<Object>();
        objectList.add(dimension);
        objectList.add(size);
        objectList.add(origin);
        objectList.add(extent);
        return objectList.toArray(new Object[0]);
    }

    @Override
    protected void inflate(){
        if(compressedBytes == null){
            return;
        }

        try {
            Object[] objArray = (Object[]) CompressionUtils.fromCompressedSerialized(compressedBytes);
            int index = 0;
            dimension = (Integer) objArray[index];
            size = (ISize) objArray[++index];
            origin = (Origin) objArray[++index];
            extent = (Extent) objArray[++index];
            compressedBytes = null;
        } catch(Exception ex){
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Coordinate getCoordinate(CoordinateIndex coordIndex){
        double x = Coordinate.coordComponentFromSinglePlanePolicy(origin, extent, Coordinate.X_AXIS);
        if(getSizeX() > 1){
            x = (coordIndex.x + 0.5) * extent.getX() / getSizeX() + origin.getX();
        }
        double y = Coordinate.coordComponentFromSinglePlanePolicy(origin, extent, Coordinate.Y_AXIS);
        if(getSizeY() > 1){
            y = (coordIndex.y + 0.5) * extent.getY() / getSizeY() + origin.getY();
        }
        double z = Coordinate.coordComponentFromSinglePlanePolicy(origin, extent, Coordinate.Z_AXIS);
        if(getSizeZ() > 1){
            z = (coordIndex.z + 0.5) * extent.getZ() / getSizeZ() + origin.getZ();
        }
        return (new Coordinate(x, y, z));
    }

    @Override
    public int getVolumeRegionIndex(int volumeIndex){
        return 0;
    }

    @Override
    public int getSubVolumeFromVolumeIndex(int volIndex){
        return 0;
    }

}
