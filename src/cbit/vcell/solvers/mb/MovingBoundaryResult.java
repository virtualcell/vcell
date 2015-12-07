package cbit.vcell.solvers.mb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;

public class MovingBoundaryResult {
	private static final String ELEM = "elements";

	private final String filename;
	private Group root;
	private MeshInfo meshInfo;
	private int lastTimeIndex_;

	private TimeInfo timeInfo;

	public static class DimensionInfo {
		final double start;
		final double end;
		final double delta;
		/**
		 * currently all zeros due to bug in attribute reading of jhdf version
		 * we're using
		 */
		final List<Double> values;

		private DimensionInfo(double start, double end, double delta, double[] values) {
			super();
			this.start = start;
			this.end = end;
			this.delta = delta;
			this.values = toUnmodifiableList(values);
		}

		/**
		 * this correct despite values being wrong
		 * @return number of mesh centers
		 */
		public int number( ) {
			return values.size();
		}

		@Override
		public String toString() {
			return "DimensionInfo [start=" + start + ", end=" + end + ", delta=" + delta + ", values=" + values + "]";
		}
	}

	public static class MeshInfo {
		final double precision;
		final double scaleFactor;
		final DimensionInfo xinfo;
		final DimensionInfo yinfo;
		final int depth;

		private MeshInfo(double precision, double scaleFactor, DimensionInfo xinfo, DimensionInfo yinfo, int depth) {
			super();
			this.precision = precision;
			this.scaleFactor = scaleFactor;
			this.xinfo = xinfo;
			this.yinfo = yinfo;
			this.depth = depth;
		}

		@Override
		public String toString() {
			return "MeshInfo [precision=" + precision + ", scaleFactor=" + scaleFactor + ", xinfo=" + xinfo + ", yinfo="
					+ yinfo + ", depth=" + depth + "]";
		}
	}

	public static class TimeInfo {
		static class TimeStep {
			final double time;
			final double step;
			private TimeStep(double time, double step) {
				this.time = time;
				this.step = step;
			}
			@Override
			public String toString() {
				return  "[" + time + ", " + step + "]";
			}

		}
		final double requestedTimeStep;
		final double endTime;
		final double runTime;
		final List<Double> generationTimes;
		final List<Double> moveTimes;
		final List<TimeStep> timeSteps;
		private TimeInfo(double requestedTimeStep, double endTime, double runTime, double[] generationTimes,
				double[] moveTimes, List<TimeStep> timeSteps) {
			super();
			this.requestedTimeStep = requestedTimeStep;
			this.endTime = endTime;
			this.runTime = runTime;
			this.generationTimes = toUnmodifiableList(generationTimes);
			this.moveTimes = toUnmodifiableList(moveTimes);
			this.timeSteps = timeSteps;
		}
		@Override
		public String toString() {
			return "TimeInfo [requestedTimeStep=" + requestedTimeStep + ", endTime=" + endTime + ", runTime=" + runTime
					+ ", generationTimes=" + generationTimes + ", moveTimes=" + moveTimes + ", timeSteps=" + timeSteps
					+ "]";
		}

	}

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
		if (lastTimeIndex_ >= 0) {
			return lastTimeIndex_;
		}
		lastTimeIndex_ = singleInt("lastTimeIndex");
		return lastTimeIndex_;
	}

	public MovingBoundaryResult(String filename) {
		this.filename = filename;
		meshInfo = null;
		timeInfo = null;
		lastTimeIndex_ = -1;
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

	/**
	 * @param values
	 * @return values as Unmodifiable list
	 */
	private static List<Double> toUnmodifiableList(double []values) {
		Double[] v = ArrayUtils.toObject(values);
		List<Double> r = Collections.unmodifiableList(Arrays.asList(v));
		return r;
	}

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
