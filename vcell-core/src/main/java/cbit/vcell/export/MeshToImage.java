package cbit.vcell.export;

import cbit.vcell.geometry.GeometryException;
import cbit.vcell.solvers.CartesianMesh;
import org.vcell.util.BeanUtils;

public class MeshToImage {

    public record ImageFromMesh(double[] data, int sizeX, int sizeY, int sizeZ){

    }

    /**
     * When a simulation is executed, the borders of this simulation world abruptly end, and because of that
     * some elements don't occupy the same region of space as other elements not on the border. For these meshes to be
     * considered as images, this can not happen and some atomic length of space must be recognized while still keeping
     * the same ratio of space taken.
     * @return double[]
     */
    public static ImageFromMesh convertMeshIntoImage(double[] data, CartesianMesh mesh) throws GeometryException {
        int dimensions = mesh.getGeometryDimension();
        if (dimensions == 2){
            return convert2DMeshIntoImage(data, mesh.getSizeX(), mesh.getSizeY());
        }
        else if (dimensions == 3) {
            return convert3DMeshIntoImage(data, mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ());
        }
        throw new GeometryException("Does not support 1D mesh to image conversions");
    }

    public static ImageFromMesh convertMeshIntoImage(double[] data, int sizeX, int sizeY, int sizeZ, boolean threeD){
        if(threeD){
            return convert3DMeshIntoImage(data, sizeX, sizeY, sizeZ);
        }
        else{
            return convert2DMeshIntoImage(data, sizeX, sizeY);
        }
    }

    public static ImageFromMesh convertImageIntoMesh(double[] data, int sizeX, int sizeY, int sizeZ, boolean threeD){
        if(threeD){
            return null;
        }else {
            return convert2DImageIntoMesh(data, sizeX, sizeY);
        }
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

    private static ImageFromMesh convert2DMeshIntoImage(double[] data, int sizeX, int sizeY){
        int newWidth = newNumElements(sizeX);
        int newHeight = newNumElements(sizeY);
        int newSize = newWidth * newHeight;
        double[] newData = new double[newSize];
        for (int i =0; i < newWidth; i++){
            int oldI = (int) Math.ceil(i / 2.0);
            for (int j=0; j < newHeight; j++){
                int oldJ = (int) Math.ceil(j / 2.0);
                int oldIndex = coordinateToIndex2D(oldI, oldJ, sizeX, sizeY);
                int newIndex = coordinateToIndex2D(i, j, newWidth, newHeight);
                newData[newIndex] = data[oldIndex];
            }
        }
        return new ImageFromMesh(newData, newWidth, newHeight, 1);
    }

    private static ImageFromMesh convert3DMeshIntoImage(double[] data, int sizeX, int sizeY, int sizeZ){
        int newWidth = newNumElements(sizeX), newHeight = newNumElements(sizeY), newDepth = newNumElements(sizeZ);
        int newSize = newWidth * newHeight * newDepth;
        double[] newData = new double[newSize];
        for (int i = 0; i < newWidth; i++){
            int oldI = (int) Math.ceil(i / 2.0);
            for (int j = 0; j < newHeight; j++){
                int oldJ = (int) Math.ceil(j / 2.0);
                for (int k = 0; k < newDepth; k++){
                    int oldK = (int) Math.ceil(k / 2.0);

                    int oldIndex = coordinateToIndex3D(oldI, oldJ, oldK, sizeX, sizeY);
                    int newIndex = coordinateToIndex3D(i, j, k, newWidth, newHeight);
                    newData[newIndex] = data[oldIndex];
                }
            }
        }
        return new ImageFromMesh(newData, newWidth, newHeight, newDepth);
    }

    public static int oldNumElements(int newNumElements){
        return ((newNumElements - 2) / 2) + 2;
    }

    private static ImageFromMesh convert2DImageIntoMesh(double[] data, int sizeX, int sizeY){
        int meshWidth = oldNumElements(sizeX);
        int meshHeight = oldNumElements(sizeY);
        double[] newData = new double[meshHeight * meshWidth];
        for (int i = 0; i < sizeX; i++){
            int meshI = (int) Math.ceil(i / 2.0);
            for (int j = 0; j < sizeY; j++){
                int meshJ = (int) Math.ceil(j / 2.0);

                int meshIndex = coordinateToIndex2D(meshI, meshJ, meshWidth, meshHeight);
                int imageIndex = coordinateToIndex2D(i, j, sizeX, sizeY);
                newData[meshIndex] = data[imageIndex];
            }
        }
        return new ImageFromMesh(newData, meshWidth, meshHeight, 1);
    }
}
















