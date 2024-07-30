package cbit.vcell.export;

import cbit.vcell.solvers.CartesianMesh;

public class MeshToImage {

    /**
     * When a simulation is executed, the borders of this simulation world abruptly end, and because of that
     * some elements don't occupy the same region of space as other elements not on the border. For these meshes to be
     * considered as images, this can not happen and some atomic length of space must be recognized while still keeping
     * the same ratio of space taken.
     * @return double[]
     */
    public static double[] convertMeshIntoImage(double[] data, CartesianMesh mesh){
        int dimensions = mesh.getGeometryDimension();
        if (dimensions == 2){
            return convert2DMeshIntoImage(data, mesh);
        }
        else if (dimensions == 3) {
            return convert3DMeshIntoImage(data, mesh);
        }
        return new double[] {};
    }

    private static double[] convert2DMeshIntoImage(double[] data, CartesianMesh mesh){
        int newSize = ((mesh.getSizeX() - 2) * (mesh.getSizeY() - 2) * 4) + // internal rectangle
                ((mesh.getSizeY() - 2) * 2 * 2) + ((mesh.getSizeX() - 2) * 2 * 2) + // sides
                4; //corners
        double[] newData = new double[newSize];
        int newWidth = ((mesh.getSizeX() - 2) * 2) + 2;
        int newHeight = ((mesh.getSizeY() - 2) * 2) + 2;
        newSize = newWidth * newHeight;
        IndexConversion indexConversion = new IndexConversion(false);
        for (int i =0; i < data.length; i++){
            int[] cords = indexConversion.rowToIndex(i, mesh.getSizeX());
            int x = cords[0], y = cords[1];
            boolean topOrBottom = y == 0 || y == mesh.getSizeY() - 1;
            boolean leftOrRight = x == 0 || x == mesh.getSizeX() - 1;
            if (leftOrRight && topOrBottom){
                int i1 = indexConversion.rowMajorIndex(x, y, 0, newWidth);
                newData[i1] = data[i];
            }
            else if (leftOrRight){
                int i1 = indexConversion.rowMajorIndex(x, y, 0, newWidth), i2 = indexConversion.rowMajorIndex(x, y + 1, 0, newWidth);
                newData[i1] = data[i];
                newData[i2] = data[i];
            }
            else if (topOrBottom){
                int i1 = indexConversion.rowMajorIndex(x, y, 0, newWidth), i2 = indexConversion.rowMajorIndex(x + 1, y, 0, newWidth);
                newData[i1] = data[i];
                newData[i2] = data[i];
            } else{
                int i1 = indexConversion.rowMajorIndex(x,y,0,newWidth), i2 = indexConversion.rowMajorIndex(x, y + 1, 0, newWidth);
                int i3 = indexConversion.rowMajorIndex(x + 1, y, 0, newWidth), i4 = indexConversion.rowMajorIndex(x + 1, y + 1, 0, newWidth);
                newData[i1] = data[i];
                newData[i2] = data[i];
                newData[i3] = data[i];
                newData[i4] = data[i];
            }

        }
        return newData;
    }

    private static double[] convert3DMeshIntoImage(double[] data, CartesianMesh mesh){
        int newSize = ((mesh.getSizeX() - 2) * (mesh.getSizeY() - 2) * (mesh.getSizeZ() - 2) * 8) + // internal
                ((mesh.getSizeY() - 2) * 2 * 4) + ((mesh.getSizeX() - 2) * 2 * 4) + ((mesh.getSizeZ() - 2) * 2 * 4) + //sides
                + 4;
        double[] newData = new double[newSize];
        int newWidth = (mesh.getSizeX() - 2) * 2 + 2;
        int newHeight = (mesh.getSizeY() - 2) * 2 + 2;
        int newDepth = (mesh.getSizeZ());
        IndexConversion indexConversion = new IndexConversion(true);
        for (int i = 0; i < data.length; i++){
            int[] cords = indexConversion.rowToIndex(i, mesh.getSizeX());
            int x = cords[0], y = cords[1], z = cords[2];
            boolean topOrBottom = (y == 0 || y == mesh.getSizeY() - 1);
            boolean leftOrRight = (x == 0 || x == mesh.getSizeX() - 1);
            boolean zCorners = (z == 0 || z == mesh.getSizeZ() - 1);
            if (topOrBottom && leftOrRight && zCorners){
                int i1 = indexConversion.rowMajorIndex(x, y, z, newWidth);
                newData[i1] = data[i];
            } else if (topOrBottom && leftOrRight && !zCorners) {
                int i1 = indexConversion.rowMajorIndex(x, y, z, newWidth);
            }
        }
    }


    static class IndexConversion{
        private final boolean threeD;
        public IndexConversion(boolean threeD){
            this.threeD = threeD;
        }

        public int[] rowToIndex(int i, int width){
            if (threeD){

            }
            int y = i % width;
            return new int[] {(i - y) / width, y};
        }

        public int rowMajorIndex(int x, int y, int z, int width){
            if (threeD){

            }
            return (x * width) + y;
        }

        public int colMajorIndex(int x, int y, int z, int height){
            if (threeD){

            }
            return (y * height) + x;
        }
    }
}
















