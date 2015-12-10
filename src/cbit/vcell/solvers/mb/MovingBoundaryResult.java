package cbit.vcell.solvers.mb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

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
	private PlaneNodes pnodes;

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

	private PlaneNodes planeNode( ) throws HDF5Exception {
		if (pnodes == null) {
			pnodes = new PlaneNodes( );
		}
		return pnodes;
	}

	/**
	 * select plane based on first dimension
	 * @param ds
	 * @param first
	 */
	private void selectPlane(H5CompoundDS ds,int d1, int d2, int first) {
		ds.clearData();
		long[] selected = ds.getSelectedDims();
		long[] start = ds.getStartDims();
		long[] stride = ds.getStride( );
		//long[] d = en.getDims( );
		//long[] m = en.getMaxDims( );
		int[] selectedIndex = ds.getSelectedIndex( );
		Arrays.fill(start, 0);
		Arrays.fill(stride, 1);
		selected[0] = 1;
		selected[1] = d1;
		selected[2] = d2;
		start[0] = first;
		selectedIndex[0] = 1;
		selectedIndex[1] = 2;
		selectedIndex[2] = 0;
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
			double[] vols;
			byte[] poz;
			{
				H5CompoundDS en = planeNode( ).elements;
				selectPlane(en,numX,numY,timeIndex);
				Vector<?> data= safeCast(Vector.class,en.getData(),"elements");
				String[] dn = en.getMemberNames();
				vols = select(double[].class,data,dn,"elements","volume");
				poz = select(byte[].class,data,dn,"elements","boundaryPosition");
			}
			double mass[][];
			double conc[][];
			{
				//will need to be a loop later
				mass = new double[1][];
				conc = new double[1][];
				H5CompoundDS sp = planeNode( ).species;
				selectPlane(sp,numX,numY,timeIndex);
				Vector<?> data= safeCast(Vector.class,sp.getData(),"species");
				String[] dn = sp.getMemberNames();
				mass[0] = select(double[].class,data,dn,"species","mass");
				conc[0]= select(double[].class,data,dn,"species","uNumeric");
			}


			int i = 0;
			for (int x = 0; x < numX; x++) {
				for (int y = 0; y < numY; y++) {
					Element e = new Element(vols[i],poz[i]);
					for (int sc = 0; sc < mass.length; sc++) {
						Species sp = new Species(mass[sc][i], conc[sc][i]);
						e.species.add(sp);
					}

					elements[x][y] = e;
					i++;
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

	/**
	 * @param clzz return type
	 * @param v input
	 * @param names available names
	 * @param path info for exception message
	 * @param childName to select
	 * @return requested data
	 * @throws ProgrammingException if wrong type or childName name not in names
	 */
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
	 * HDF data nodes which support planes
	 */
	private class PlaneNodes {
		final H5CompoundDS elements;
		final H5CompoundDS species;
		PlaneNodes() throws HDF5Exception {
			VH5TypedPath<H5CompoundDS> dpath = new VH5TypedPath<H5CompoundDS>(root, H5CompoundDS.class,"elements");
			elements = dpath.get();
			elements.read();
			dpath = new VH5TypedPath<H5CompoundDS>(root, H5CompoundDS.class,"species");
			species = dpath.get();
			species.read( );
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
