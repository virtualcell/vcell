/*****************************************************************************
 * Copyright by The HDF Group.                                               *
 * Copyright by the Board of Trustees of the University of Illinois.         *
 * All rights reserved.                                                      *
 *                                                                           *
 * This file is part of the HDF Java Products distribution.                  *
 * The full copyright notice, including terms governing use, modification,   *
 * and redistribution, is contained in the files COPYING and Copyright.html. *
 * COPYING can be found at the root of the source code distribution tree.    *
 * Or, see http://hdfgroup.org/products/hdf-java/doc/Copyright.html.         *
 * If you do not have access to either file, you may request a copy from     *
 * help@hdfgroup.org.                                                        *
 ****************************************************************************/

package cbit.vcell.solvers.mb;

import cbit.vcell.resource.PropertyLoader;
import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import io.jhdf.api.WritiableDataset;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: HDF Object Package (Java) Example
 * </p>
 * <p>
 * Description: this example shows how to retrieve HDF file structure using the
 * "HDF Object Package (Java)". The example created the group structure and
 * datasets, and print out the file structure:
 *
 * <pre>
 *     "/" (root)
 *         integer arrays
 *             2D 32-bit integer 20x10
 *             3D unsigned 8-bit integer 20x10x5
 *         float arrays
 *             2D 64-bit double 20x10
 *             3D 32-bit float  20x10x5
 * </pre>
 *
 * </p>
 *
 * @author Peter X. Cao
 * @version 2.4
 */
@Tag("Fast")
public class MovingBoundaryH5FileStructureTest {
    private static String fname = "nformat2.h5";
    private static int[] dims2D = {20, 10};
    private static int[] dims3D = {20, 10, 5};

    @BeforeAll
    public static void setup() throws Exception {
        PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
        createFile(fname);
    }

    @AfterAll
    public static void tearDown() {
        boolean _b = new File(fname).delete();
    }

    //public static void main(String args[]) throws Exception {
    @Test
    public void run(){
        //createFile();

        try (HdfFile hdfFile = new HdfFile(new File(fname))) {
            List<Node> childGroups = hdfFile.getChildren().values().stream().filter(node -> node instanceof Group).toList();
            for (Node node : childGroups) {
                if (node instanceof Group group) {
                    printGroup(group, "");
                }
            }
        }
    }

    /**
     * Recursively print a group and its members.
      */
    private static void printGroup(Group g, String indent) {
        if(g == null) return;
        System.out.println(indent + g.getName());

        indent += "    ";
        Map<String, Node> members = g.getChildren();
        for(Node node : members.values()) {
            System.out.println(indent + node.getName());
            if(node instanceof Group group){
                printGroup(group, indent);
            }
            if (node instanceof Dataset ds && ds.getName().equals("elements")) {
  //            if (node instanceof Dataset ds && ds.getName().equals("boundaries")) {
                MovingBoundaryVH5Dataset.info(ds);
                MovingBoundaryVH5Dataset.meta(ds);
            }
        }
    }

    private static void createFile(String fname) throws Exception{
        File tempFile;
        WritableHdfFile hdf5File = null;

        try {
            tempFile = new File(fname);
            hdf5File = HdfFile.write(tempFile.toPath());

            // create groups at the root
            Group g1 = hdf5File.putGroup("integer arrays");
            Group g2 = hdf5File.putGroup("float arrays");

            // create 2D 32-bit (4 bytes) integer dataset of 20 by 10
            WritiableDataset ds1 = hdf5File.putDataset(g1.getPath()+"2D 32-bit integer 20x10", new int[dims2D[0]][dims2D[1]]);

            // create 3D 8-bit (1 byte) unsigned integer dataset of 20 by 10 by 5
            WritiableDataset ds2 = hdf5File.putDataset(g1.getPath()+"3D unsigned 8-bit integer 20x10x5", new byte[dims3D[0]][dims3D[1]][dims3D[2]]);

            // create 2D 64-bit (8 bytes) double dataset of 20 by 10
            WritiableDataset ds3 = hdf5File.putDataset(g2.getPath()+"2D 64-bit double 20x10", new double[dims2D[0]][dims2D[1]]);

            // create 3D 32-bit (4 bytes) float dataset of 20 by 10 by 5
            WritiableDataset ds4 = hdf5File.putDataset(g2.getPath()+"3D 32-bit float  20x10x5", new float[dims3D[0]][dims3D[1]][dims3D[2]]);
         } finally {
            if (hdf5File != null)
                hdf5File.close();
        }
    }
}
