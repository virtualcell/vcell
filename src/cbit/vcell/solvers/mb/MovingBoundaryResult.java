package cbit.vcell.solvers.mb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.vcell.util.BeanUtils;
import org.vcell.util.BeanUtils.CastInfo;
import org.vcell.util.ProgrammingException;
import org.vcell.util.VCAssert;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5CompoundDS;

public class MovingBoundaryResult implements MovingBoundaryTypes {
	private static final String ELEM = "elements";

	private final String filename;
	private Group root;
	private MeshInfo meshInfo;
	private int lastTimeIndex_;

	private TimeInfo timeInfo;
	private H5CompoundDS elementNode_;

	public MeshInfo getMeshInfo( ) {
		if (meshInfo == null) {
			double p = singleDouble("precision");
			double sf = singleDouble("scaleFactor");
			DimensionInfo xdim = getDimInfo('x');
			DimensionInfo ydim = getDimInfo('y');
			int d = lastTimeIndex();
			meshInfo = new MeshInfo(p,sf,xdim,ydim,d);
		}
		return meshInfo;
	}

	public int lastTimeIndex( ) {
		return lastTimeIndex_;
	}

	public MovingBoundaryResult(String filename) {
		this.filename = filename;
		meshInfo = null;
		timeInfo = null;
		try {
			// retrieve an instance of H5File
			FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

			if (fileFormat == null) {
				System.err.println("Cannot find HDF5 FileFormat.");
				return;
			}

			// open the file with read-only access
			FileFormat testFile = fileFormat.createInstance(filename, FileFormat.READ);

			if (testFile == null) {
				System.err.println("Failed to open file: " + filename);
				return;
			}

			// open the file and retrieve the file structure
			testFile.open();
			root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) testFile.getRootNode()).getUserObject();
			lastTimeIndex_ = singleInt("lastTimeIndex");
		} catch (Exception e) {
			throw new MovingBoundaryResultException("exception reading moving boundary result file " + filename,e);
		}
	}

	private double[] getDoubleArray(String ...names) {
		VH5TypedPath<double[]> dpath = new VH5TypedPath<double[]>(root, double[].class,names);
		return dpath.get();
	}

	private double singleDouble(String ...names) {
		double[] a = getDoubleArray(names);
		if (a.length != 1) {
			throw new MovingBoundaryResultException(VH5Path.concat(names) + " is not single element array");
		}
		return a[0];
	}

	private long[] getLongArray(String ...names) {
		VH5TypedPath<long[]> dpath = new VH5TypedPath<long[]>(root, long[].class,names);
		return dpath.get();
	}

	private long singleLong(String ...names) {
		long[] a = getLongArray(names);
		if (a.length != 1) {
			throw new MovingBoundaryResultException(VH5Path.concat(names) + " is not single element array");
		}
		return a[0];
	}

	private int[] getIntArray(String ...names) {
		VH5TypedPath<int[]> dpath = new VH5TypedPath<int[]>(root, int[].class,names);
		return dpath.get();
	}

	private int singleInt(String ...names) {
		int[] a = getIntArray(names);
		if (a.length != 1) {
			throw new MovingBoundaryResultException(VH5Path.concat(names) + " is not single element array");
		}
		return a[0];
	}

//	private int checkedConvert(long a) {
//		if (Math.abs(a) < Integer.MAX_VALUE) {
//			return (int) a;
//		}
//		throw new MovingBoundaryResultException("Long " + a + " invalid integer, too big");
//	}

	private DimensionInfo getDimInfo(char dim) {
		char upper = Character.toUpperCase(dim);
		char lower = Character.toLowerCase(dim);
		double st = singleDouble(ELEM,"start" + upper);
		double end = singleDouble(ELEM,"end" + upper);
		double delta = singleDouble(ELEM,"h" + lower);
		String cstr = "num" + upper;
		long c  = singleLong(ELEM,cstr);
		String vstr = lower + "values";
		double[] values = getDoubleArray(ELEM,vstr);
		if (values.length != c) {
			throw new MovingBoundaryResultException(cstr + " value " + c + " does not match " + vstr + " array length");
		}
		return new DimensionInfo(st, end, delta, values);
	}

	public TimeInfo getTimeInfo( ) {
		if (timeInfo == null) {
			double rts = singleDouble("requestedTimeStep");
			double et = singleDouble("endTime");
			double rt = singleDouble("runTime");
			double[] gt = getDoubleArray("generationTimes");
			double[] mt = getDoubleArray("moveTimes");
			ArrayList<TimeInfo.TimeStep> tsa = new ArrayList<>();
			{
				double[] steps = getDoubleArray("timeStep");
				double[] times = getDoubleArray("timeStepTimes");
				if (steps.length != times.length) {
					throw new MovingBoundaryResultException("timeStep length " + steps.length + " does not match timesStepTimes length " + times.length);
				}
				for (int i = 0; i < steps.length; i++) {
					TimeInfo.TimeStep ts = new TimeInfo.TimeStep(times[i], steps[i]);
					tsa.add(ts);
				}
			}

			timeInfo = new TimeInfo(rts, et, rt, gt, mt, tsa);
		}

		return timeInfo;
	}

	private void validateTimeIndex(int t) {
		if (t > lastTimeIndex_) {
			throw new IndexOutOfBoundsException("time index " + t + " greater than max index " + lastTimeIndex_);
		}
	}

	private H5CompoundDS elementNode( ) throws HDF5Exception {
		if (elementNode_ == null) {
			VH5TypedPath<H5CompoundDS> dpath = new VH5TypedPath<H5CompoundDS>(root, H5CompoundDS.class,"elements");
			elementNode_ = dpath.get();
			elementNode_.read();
		}
		return this.elementNode_;
	}

	/**
	 * @param timeIndex >= 0 and <= {@link #lastTimeIndex()}
	 * @return
	 */
	public Plane getPlane(int timeIndex) {
		VCAssert.assertTrue(timeIndex >= 0, "negative time index");
		validateTimeIndex(timeIndex);
		try {
			MeshInfo mi = getMeshInfo();
			final int numX = mi.xinfo.number();
			final int numY = mi.yinfo.number();
			Element elements[][] = new Element[numX][numY];
			H5CompoundDS en = elementNode();
			en.clearData();
			long[] selected = en.getSelectedDims();
			long[] start = en.getStartDims();
			long[] stride = en.getStride( );
			long[] d = en.getDims( );
			long[] m = en.getMaxDims( );
			int[] selectedIndex = en.getSelectedIndex( );
			Arrays.fill(start, 0);
			Arrays.fill(stride, 1);
			selected[0] = 1;
			start[0] = timeIndex;
			selectedIndex[0] = 1;
			selectedIndex[1] = 2;
			selectedIndex[2] = 0;
			Vector<?> data= safeCast(Vector.class,en.getData(),"elements");
			String[] dn = en.getMemberNames();
			double[] vols = select(double[].class,data,dn,"elements","volume");
			int i = 0;
			for (int x = 0; x < numX; x++) {
				for (int y = 0; y < numY; y++) {
					elements[x][y] = new Element(vols[i++]);
				}
			}

			PlaneI plane = new PlaneI();
			plane.elements = elements;
			plane.time = getTimeInfo().generationTimes.get(timeIndex);
			return plane;
		} catch (Exception e) {
			throw new RuntimeException("Can't read plane for time index " + timeIndex,e);
		}
	}

	private static class PlaneI implements Plane {
		Element elements[][];
		double time;

		@Override
		public double getTime() {
			return time;
		}

		@Override
		public int getSizeX() {
			return elements.length;
		}
		@Override
		public int getSizeY() {
			return elements[0].length;
		}

		@Override
		public Element get(int x, int y) {
			return elements[x][y];
		}
	}

	private static<T> T select(Class<T> clzz, Vector<?> v, String[] names,String path, String childName) {
		for (int i = 0; i < names.length; i++) {
			if (childName.equals(names[i])) {
				return safeCast(clzz,v.get(i), path + "/" + childName);
			}
		}
		throw new ProgrammingException("No " + childName + " in " + StringUtils.join(names,",") + " children of " + path);
	}

	private static<T> T safeCast(Class<T> clzz, Object obj, String path) {
		CastInfo<T> ci = BeanUtils.attemptCast(clzz, obj);
		if (ci.isGood()) {
			return ci.get();
		}
		throw new ProgrammingException(ci.castMessage() + " failed for " + path);
	}

	/**
	 * moving boundary result exception construct
	 */
	private String mbrec(String message) {
		return "Reading " + filename + ": " + message;
	}

	@SuppressWarnings("serial")
	private class MovingBoundaryResultException extends RuntimeException {

		MovingBoundaryResultException(String message) {
			super(mbrec(message));
		}

		protected MovingBoundaryResultException(String message, Throwable cause) {
			super(mbrec(message), cause);
		}
	}
}
