package org.vcell.data.array;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Compares the {@link ArrayBackend}s on the access patterns the exporters use, over a field shaped like a
 * mid-sized 3D run: 100 time points of a 20 x 50 x 50 mesh (25,000 elements, 2.5M values).
 * <p>
 * Not a unit test (no JUnit annotations, not in any test group). Run it from the test classpath:
 * <pre>
 * mvn -pl vcell-core test-compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt -Dmdep.includeScope=test
 * java -cp vcell-core/target/test-classes:vcell-core/target/classes:$(cat vcell-core/target/cp.txt) org.vcell.data.array.ArrayBackendBenchmark
 * </pre>
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class ArrayBackendBenchmark {

	private static final int TIMES = 100;
	private static final int Z = 20;
	private static final int Y = 50;
	private static final int X = 50;
	private static final int ELEMENTS = Z * Y * X;
	private static final int POINTS = 100;

	@Param({"strided", "java-ndarray"})
	public String backendName;

	private ArrayBackend backend;
	private double[] raw;
	/** (time, element): how a variable over a flat mesh index is held. */
	private DoubleArrayND field;
	/** (time, z, y, x): the same values viewed as a raster, for plane slices. */
	private DoubleArrayND raster;
	private long[] points;
	private long[][] randomIndices;
	private double[] block;

	@Setup(Level.Trial)
	public void setUp() {
		this.backend = "strided".equals(this.backendName) ? new StridedArrayBackend() : new NdArrayBackend();
		Random random = new Random(42);
		this.raw = new double[TIMES * ELEMENTS];
		for (int i = 0; i < this.raw.length; i++) this.raw[i] = random.nextDouble();
		this.field = this.backend.wrap(this.raw, TIMES, ELEMENTS);
		this.raster = this.backend.wrap(this.raw, TIMES, Z, Y, X);
		this.points = random.longs(POINTS, 0, ELEMENTS).toArray();
		this.randomIndices = new long[10_000][];
		for (int i = 0; i < this.randomIndices.length; i++) this.randomIndices[i] = new long[]{random.nextInt(TIMES), random.nextInt(ELEMENTS)};
		this.block = new double[ELEMENTS];
	}

	/** Full-mesh export (N5, VTK, full CSV): every time point's whole block, copied out. */
	@Benchmark
	public void fullBlockPerTime(Blackhole blackhole) {
		for (int t = 0; t < TIMES; t++) {
			this.field.at(0, t).copyTo(this.block, 0);
			blackhole.consume(this.block);
		}
	}

	/** Point export / time plots: the time series at a set of elements. */
	@Benchmark
	public double[] timeSeriesAtPoints() {
		return this.field.take(1, this.points).toArray();
	}

	/** Slice export / image rendering: one z-plane at every time point. */
	@Benchmark
	public void zPlanePerTime(Blackhole blackhole) {
		for (int t = 0; t < TIMES; t++) blackhole.consume(this.raster.at(0, t).at(0, Z / 2).toArray());
	}

	/** Orthogonal slice: one x-plane (strided, not contiguous) at every time point. */
	@Benchmark
	public void xPlanePerTime(Blackhole blackhole) {
		for (int t = 0; t < TIMES; t++) blackhole.consume(this.raster.at(0, t).at(2, X / 2).toArray());
	}

	/** Per-element access overhead, as code that loops with get() would pay it. */
	@Benchmark
	public double randomGets() {
		double sum = 0;
		for (long[] index : this.randomIndices) sum += this.field.get(index);
		return sum;
	}

	/** Cost of putting an existing double[] behind the interface, paid every time a loader hands over a block. */
	@Benchmark
	public DoubleArrayND wrapExisting() {
		return this.backend.wrap(this.raw, TIMES, ELEMENTS);
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder().include(ArrayBackendBenchmark.class.getSimpleName()).build()).run();
	}
}
