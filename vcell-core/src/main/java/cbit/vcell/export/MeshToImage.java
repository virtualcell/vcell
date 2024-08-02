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

    public static int newNumElements(int oldNElements){
        return oldNElements == 1 ? 1 : ((oldNElements - 2) * 2) + 2;
    }

    private static ImageFromMesh convert2DMeshIntoImage(double[] data, int sizeX, int sizeY){
        int newWidth = newNumElements(sizeX);
        int newHeight = newNumElements(sizeY);
        int newSize = newWidth * newHeight;
        double[] newData = new double[newSize];
        for (int i =0; i < newWidth; i++){
            for (int j=0; j < newHeight; j++){
                int oldI = (int) Math.ceil(i / 2.0);
                int oldJ = (int) Math.ceil(j / 2.0);
                int oldIndex = BeanUtils.coordinateToIndex(new int[] {oldI, oldJ}, new int[] {sizeX - 1, sizeY - 1});
                int newIndex = BeanUtils.coordinateToIndex(new int[] {i, j}, new int[] {newWidth - 1, newHeight - 1});
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
            for (int j = 0; j < newHeight; j++){
                for (int k = 0; k < newDepth; k++){
                    int oldI = (int) Math.ceil(i / 2.0);
                    int oldJ = (int) Math.ceil(j / 2.0);
                    int oldK = (int) Math.ceil(k / 2.0);
                    int oldIndex = BeanUtils.coordinateToIndex(new int[] {oldI, oldJ, oldK}, new int[] {sizeX - 1, sizeY - 1, sizeZ - 1}); //size is not 0 indexed so -1
                    int newIndex = BeanUtils.coordinateToIndex(new int[] {i, j, k}, new int[] {newWidth - 1, newHeight - 1, newDepth - 1});
                    newData[newIndex] = data[oldIndex];
                }
            }
        }
        return new ImageFromMesh(newData, newWidth, newHeight, newDepth);
    }
}
















