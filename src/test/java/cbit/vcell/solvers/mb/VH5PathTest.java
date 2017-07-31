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

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cern.colt.Arrays;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;

@Ignore
public class VH5PathTest extends H5Client {
    private static String fname  = FILE;
	private FileFormat testFile = null;
	private Group root = null;

    @Before
    public void setup( ) throws Exception {

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

    @After
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
    		VH5Path vpath = new VH5Path(root, "elements" ,"volume");
    		System.out.println(vpath.foundType());
    		VH5TypedPath<double[]> tpath = new VH5TypedPath<double[]>(root, double[].class,"elements" ,"volume");
    		double[] e = tpath.get();
    		System.out.println(e[0]);
    		VH5Path bpPath = new VH5Path(root, "elements" ,"boundaryPosition");
    		Object data = bpPath.getData();
    		System.out.println(data.getClass().getSimpleName());
    		VH5Path vpPath = new VH5Path(root, "elements" ,"volumePoints");
    		data = vpPath.getData();
    		System.out.println(data.getClass().getSimpleName());

//    		VH5TypedPath<String[]> spath = new VH5TypedPath<String[]>(root, String[].class,"elements" ,"front description");
    		VH5TypedPath<String> spath = new VH5TypedPath<String>(root, String.class,"elements" ,"front description");
//    		String[] sdata = spath.get();
//    		System.out.println(sdata[0]);
    		System.out.println(spath.get( ));

    		VH5Path xpath = new VH5Path(root, "elements" ,"front description");
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
			VH5TypedPath<int[]> ipath = new VH5TypedPath<int[]>(root, int[].class,"lastTimeIndex");
    		System.out.println(Arrays.toString(ipath.get()));
			VH5TypedPath<long[]> lpath = new VH5TypedPath<long[]>(root, long[].class,"elements","numX");
    		System.out.println(Arrays.toString(lpath.get()));
//			System.out.println("-------");
//    		VH5TypedPath<H5ScalarDS> spath = new VH5TypedPath<H5ScalarDS>(root, H5ScalarDS.class,"endTime");
//			H5ScalarDS ds = spath.get( );
//			Object o = ds.read();
//			VH5Dataset vds = new VH5Dataset(ds);
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
		VH5TypedPath<double[]> dpath = new VH5TypedPath<double[]>(root, double[].class,name);
		System.out.println(StringUtils.join(name,'/') + ' ' + Arrays.toString(dpath.get()));
    }


    @Test (expected = UnsupportedOperationException.class)
    public void badType () {
		VH5TypedPath<int[]> ipath = new VH5TypedPath<int[]>(root, int[].class,"elements" ,"volume");
		System.out.println(ipath);
    }

    @Test (expected = UnsupportedOperationException.class)
    public void primType () {
		VH5TypedPath<Integer> ipath = new VH5TypedPath<Integer>(root, int.class,"elements" ,"volume");
		System.out.println(ipath);
    }

    @Test (expected = RuntimeException.class)
    public void badPath( ) {
    	dtype("junk","yard");
    }
}
