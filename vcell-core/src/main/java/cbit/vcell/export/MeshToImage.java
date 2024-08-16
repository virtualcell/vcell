package cbit.vcell.export;

import cbit.vcell.geometry.GeometryException;
import cbit.vcell.solvers.CartesianMesh;

public class MeshToImage {

    public record ImageData(double[] data, int sizeX, int sizeY, int sizeZ){

    }

    public record MeshData(double[] data, int sizeX, int sizeY, int sizeZ){

    }

    /**
     * When a simulation is executed, the borders of this simulation world abruptly end, and because of that
     * some elements don't occupy the same region of space as other elements not on the border. For these meshes to be
     * considered as images, this can not happen and some atomic length of space must be recognized while still keeping
     * the same ratio of space taken.
     * @return double[]
     */
    public static ImageData convertMeshIntoImage(double[] data, CartesianMesh mesh) throws GeometryException {
        int dimensions = mesh.getGeometryDimension();
        if (dimensions == 1){
            throw new GeometryException("Does not support 1D mesh to image conversions");
        }
        return convertMeshIntoImage(data, mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ(), dimensions > 2);
    }

    public static MeshData convertImageIntoMesh(double[] data, CartesianMesh mesh) throws GeometryException {
        return convertImageIntoMesh(data, mesh, mesh.getGeometryDimension() > 2);
    }

    public static MeshData convertImageIntoMesh(double[] data, CartesianMesh mesh, boolean overRideThreeD) throws GeometryException {
        int dim = mesh.getGeometryDimension();
        if (dim == 1){
            throw new GeometryException("Does not support 1D image to mesh conversion");
        }
        return convertImageIntoMesh(data, mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ(), dim > 2 && overRideThreeD);
    }

    public static ImageData convertMeshIntoImage(double[] data, int sizeX, int sizeY, int sizeZ, boolean threeD){
        int newDepth = threeD ? newNumElements(sizeZ) : 1;
        int newWidth = newNumElements(sizeX), newHeight = newNumElements(sizeY);
        int newSize = newWidth * newHeight * newDepth;
        double[] newData = new double[newSize];
        for (int i = 0; i < newWidth; i++){
            int oldI = (int) Math.ceil(i / 2.0);
            for (int j = 0; j < newHeight; j++){
                int oldJ = (int) Math.ceil(j / 2.0);
                for (int k = 0; k < newDepth; k++){
                    int oldK = (int) Math.ceil(k / 2.0);

                    int oldIndex = threeD ? coordinateToIndex3D(oldI, oldJ, oldK, sizeX, sizeY) : coordinateToIndex2D(oldI, oldJ, sizeX, sizeY);
                    int newIndex = threeD ? coordinateToIndex3D(i, j, k, newWidth, newHeight) : coordinateToIndex2D(i, j, newWidth, newHeight);
                    newData[newIndex] = data[oldIndex];
                }
            }
        }
        return new ImageData(newData, newWidth, newHeight, newDepth);
    }

    public static MeshData convertImageIntoMesh(double[] data, int sizeX, int sizeY, int sizeZ, boolean threeD){
        int meshWidth = oldNumElements(sizeX);
        int meshHeight = oldNumElements(sizeY);
        int meshDepth = threeD ? oldNumElements(sizeZ) : 1;
        double[] newData = new double[meshHeight * meshWidth * meshDepth];
        for (int i = 0; i < sizeX; i++){
            int meshI = (int) Math.ceil(i / 2.0);
            for (int j = 0; j < sizeY; j++){
                int meshJ = (int) Math.ceil(j / 2.0);
                for(int k = 0; k < sizeZ; k++){
                    int meshK = (int) Math.ceil(k / 2.0);

                    int meshIndex = threeD ? coordinateToIndex3D(meshI, meshJ, meshK, meshWidth, meshHeight) : coordinateToIndex2D(meshI, meshJ, meshWidth, meshHeight);
                    int imageIndex = threeD ?  coordinateToIndex3D(i, j, k, sizeX, sizeY) : coordinateToIndex2D(i, j, sizeX, sizeY);
                    newData[meshIndex] = data[imageIndex];
                }
            }
        }
        return new MeshData(newData, meshWidth, meshHeight, 1);
    }


    public static int coordinateToIndex2D(int x, int y, int width, int height){
        return (y * width) + x;
    }

    public static int coordinateToIndex3D(int x, int y, int z, int width, int height){
        return (z*(height * width)) + (y * width) + x;
    }

    public static int newNumElements(int oldNElements){
        return oldNElements == 1 ? 1 : ((oldNElements - 2) * 2) + 2;
    }

    public static int oldNumElements(int newNumElements){
        return ((newNumElements - 2) / 2) + 2;
    }

    public static double[] getXYFromXYZArray(double[] xyzArray, CartesianMesh mesh, int zSlice){
        return getXYFromXYZArray(xyzArray, mesh.getSizeX(), mesh.getSizeY(), zSlice);
    }

    public static double[] getXYFromXYZArray(double[] xyzArray, int sizeX, int sizeY, int zSlice){
        double[] xyArray = new double[sizeX * sizeY];
        int xyIndex = 0;
        for (int xyzIndex = (zSlice * sizeX * sizeY); xyzIndex < ((zSlice + 1) * sizeX * sizeY); xyzIndex++){
            xyArray[xyIndex] = xyzArray[xyzIndex];
            xyIndex++;
        }
        return xyArray;
    }
}
















