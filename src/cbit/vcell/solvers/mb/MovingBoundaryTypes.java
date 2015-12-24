package cbit.vcell.solvers.mb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public interface MovingBoundaryTypes {

	public static class DimensionInfo {
		final double start;
		final double end;
		final double delta;
		/**
		 * currently all zeros due to bug in attribute reading of jhdf version
		 * we're using
		 */
		final List<Double> values;

		DimensionInfo(double start, double end, double delta, double[] values) {
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

		MeshInfo(double precision, double scaleFactor, DimensionInfo xinfo, DimensionInfo yinfo, int depth) {
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

	public static class Species {
		final double mass;
		final double concentration;
		Species(double mass, double concentration) {
			this.mass = mass;
			this.concentration = concentration;
		}
		@Override
		public String toString() {
			return "(mass=" + mass + ", concentration=" + concentration + ")";
		}
	}

	public static class Element {
		enum Position {
			INSIDE,
			BOUNDARY,
			OUTSIDE;
			@Override
			public String toString( ) {
				return name().toLowerCase();
			}
		}
		final List<Species> species;
		final double volume;
		final Position position;
		private final int[] boundaryIndexes;

		Element(double volume, byte poz, int[] boundaryIndexes) {
			species = new ArrayList<>();
			this.volume = volume;
			this.boundaryIndexes = boundaryIndexes;
			switch(poz) {
			case 'B':
				position = Position.BOUNDARY;
				break;
			case 'I':
				position = Position.INSIDE;
				break;
			default:
				position = Position.OUTSIDE;
				break;
			}
		}
		public double getVolume() {
			return volume;
		}
		public Position getPosition() {
			return position;
		}

		/**
		 * integers are indexes into a corresponding {@link PointIndex}
		 * @return index points of boundary
		 */
		public int[] boundary( ) {
			return boundaryIndexes;

		}
		@Override
		public String toString() {
			return "Element [volume=" + volume + ", position=" + position + "]";
		}

	}

	public interface Plane {
		double getTime( );
		int getSizeX();
		int getSizeY();
		Element get(int x, int y);
	}

	public static class TimeInfo {
		static class TimeStep {
			final double time;
			final double step;
			TimeStep(double time, double step) {
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
		TimeInfo(double requestedTimeStep, double endTime, double runTime, double[] generationTimes,
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
	/**
	 * @param values
	 * @return values as Unmodifiable list
	 */
	public static List<Double> toUnmodifiableList(double []values) {
		Double[] v = ArrayUtils.toObject(values);
		List<Double> r = Collections.unmodifiableList(Arrays.asList(v));
		return r;
	}

}
