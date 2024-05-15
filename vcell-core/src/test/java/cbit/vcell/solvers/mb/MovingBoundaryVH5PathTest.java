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

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cern.colt.Arrays;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
@Tag("Fast")
public class MovingBoundaryVH5PathTest {
    private static String fname  = "nformat2.h5";
	private FileFormat testFile = null;
	private Group root = null;

    @BeforeEach
    public void setup( ) throws Exception {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
		NativeLib.HDF5.load();


		// retrieve an instance of H5File
    		FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

    		if (fileFormat == null) {
    			System.err.println("Cannot find HDF5 FileFormat.");
    			return;
    		}

    		// open the file with read-only access
    		testFile = fileFormat.createInstance(fname, FileFormat.READ);

    		if (testFile == null) {
    			System.err.println("Failed to open file: " + fname);
    			return;
    		}

    		// open the file and retrieve the file structure
    		testFile.open();
    		root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) testFile.getRootNode()).getUserObject();
    }

    @AfterEach
    public void close( ) throws Exception {
    	if (testFile != null) {
    		testFile.close();
    	}

    }
    @Test
    public void run()  {
    	// create the file and add groups ans dataset into the file
    	try {
    		Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) testFile.getRootNode()).getUserObject();
    		MovingBoundaryVH5Path vpath = new MovingBoundaryVH5Path(root, "elements" ,"volume");
    		System.out.println(vpath.foundType());
    		MovingBoundaryVH5TypedPath<double[]> tpath = new MovingBoundaryVH5TypedPath<double[]>(root, double[].class,"elements" ,"volume");
    		double[] e = tpath.get();
    		System.out.println(e[0]);
    		MovingBoundaryVH5Path bpPath = new MovingBoundaryVH5Path(root, "elements" ,"boundaryPosition");
    		Object data = bpPath.getData();
    		System.out.println(data.getClass().getSimpleName());
    		MovingBoundaryVH5Path vpPath = new MovingBoundaryVH5Path(root, "elements" ,"volumePoints");
    		data = vpPath.getData();
    		System.out.println(data.getClass().getSimpleName());

//    		MovingBoundardyVH5TypedPath<String[]> spath = new MovingBoundardyVH5TypedPath<String[]>(root, String[].class,"elements" ,"front description");
    		MovingBoundaryVH5TypedPath<String> spath = new MovingBoundaryVH5TypedPath<String>(root, String.class,"elements" ,"front description");
//    		String[] sdata = spath.get();
//    		System.out.println(sdata[0]);
    		System.out.println(spath.get( ));

    		MovingBoundaryVH5Path xpath = new MovingBoundaryVH5Path(root, "elements" ,"front description");
    		Object o = xpath.getData();
    		System.out.println(o);
    		dtype("elements","endX");
    		dtype("endTime");
    		dtype("generationTimes");
    		dtype("moveTimes");
    		dtype("runTime");
    		dtype("solverTimeStep");
    		dtype("timeStep");
    		dtype("timeStepTimes");
			MovingBoundaryVH5TypedPath<int[]> ipath = new MovingBoundaryVH5TypedPath<int[]>(root, int[].class,"lastTimeIndex");
    		System.out.println(Arrays.toString(ipath.get()));
			MovingBoundaryVH5TypedPath<long[]> lpath = new MovingBoundaryVH5TypedPath<long[]>(root, long[].class,"elements","numX");
    		System.out.println(Arrays.toString(lpath.get()));
//			System.out.println("-------");
//    		MovingBoundardyVH5TypedPath<H5ScalarDS> spath = new MovingBoundardyVH5TypedPath<H5ScalarDS>(root, H5ScalarDS.class,"endTime");
//			H5ScalarDS ds = spath.get( );
//			Object o = ds.read();
//			MovingBoundaryVH5Dataset vds = new MovingBoundaryVH5Dataset(ds);
//			vds.info();

    		/*
    		Next to do; use lastTimeIndex as maximum index into elements and species data sets. Select one X-Y plane at a time and read.
    		*/

    		// close file resource
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }

    private void dtype(String ...name) {
		MovingBoundaryVH5TypedPath<double[]> dpath = new MovingBoundaryVH5TypedPath<double[]>(root, double[].class,name);
		System.out.println(StringUtils.join(name,'/') + ' ' + Arrays.toString(dpath.get()));
    }


    @Test
    public void badType () {
		assertThrows(UnsupportedOperationException.class, () -> {
			MovingBoundaryVH5TypedPath<int[]> ipath = new MovingBoundaryVH5TypedPath<int[]>(root, int[].class,"elements" ,"volume");
			System.out.println(ipath);
		});
	}

    @Test
    public void primType () {
		assertThrows(UnsupportedOperationException.class, () -> {
			MovingBoundaryVH5TypedPath<Integer> ipath = new MovingBoundaryVH5TypedPath<Integer>(root, int.class,"elements" ,"volume");
			System.out.println(ipath);
		});
	}

    @Test
    public void badPath( ) {
		assertThrows(RuntimeException.class, () -> {
			dtype("junk", "yard");
		});
    }
}
