package org.vcell.data.array;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The contract every {@link ArrayBackend} must meet; each test runs once per backend.
 */
@Tag("Fast")
public class DoubleArrayNDTest {

	static Stream<ArrayBackend> backends() {
		return Stream.of(new StridedArrayBackend(), new NdArrayBackend());
	}

	/** A 2x3x4 array whose value at (i,j,k) is 100i + 10j + k, so any wrong index shows in the value. */
	private static DoubleArrayND cube(ArrayBackend backend) {
		double[] values = new double[24];
		int n = 0;
		for (int i = 0; i < 2; i++) for (int j = 0; j < 3; j++) for (int k = 0; k < 4; k++) values[n++] = 100 * i + 10 * j + k;
		return backend.wrap(values, 2, 3, 4);
	}

	@ParameterizedTest
	@MethodSource("backends")
	public void wrapIsRowMajor(ArrayBackend backend) {
		DoubleArrayND cube = cube(backend);
		assertArrayEquals(new long[]{2, 3, 4}, cube.shape());
		assertEquals(3, cube.rank());
		assertEquals(24, cube.size());
		assertEquals(123, cube.get(1, 2, 3));
		assertEquals(0, cube.get(0, 0, 0));
		assertEquals(110, cube.get(1, 1, 0));
	}

	@ParameterizedTest
	@MethodSource("backends")
	public void zerosAndSet(ArrayBackend backend) {
		DoubleArrayND array = backend.zeros(2, 2);
		assertArrayEquals(new double[4], array.toArray());
		array.set(7.5, 1, 0);
		assertEquals(7.5, array.get(1, 0));
		assertArrayEquals(new double[]{0, 0, 7.5, 0}, array.toArray());
	}

	@ParameterizedTest
	@MethodSource("backends")
	public void sliceKeepsRankAndSharesStorage(ArrayBackend backend) {
		DoubleArrayND cube = cube(backend);
		DoubleArrayND slice = cube.slice(1, 1, 3);
		assertArrayEquals(new long[]{2, 2, 4}, slice.shape());
		assertEquals(10, slice.get(0, 0, 0));
		assertEquals(123, slice.get(1, 1, 3));
		slice.set(-1, 0, 0, 0);
		assertEquals(-1, cube.get(0, 1, 0), "a slice is a view");
	}

	@ParameterizedTest
	@MethodSource("backends")
	public void atDropsDimension(ArrayBackend backend) {
		DoubleArrayND cube = cube(backend);
		DoubleArrayND plane = cube.at(1, 2);
		assertArrayEquals(new long[]{2, 4}, plane.shape());
		assertArrayEquals(new double[]{20, 21, 22, 23, 120, 121, 122, 123}, plane.toArray());
		DoubleArrayND scalar = plane.at(0, 1).at(0, 3);
		assertEquals(0, scalar.rank());
		assertEquals(1, scalar.size());
		assertEquals(123, scalar.get());
		assertArrayEquals(new double[]{123}, scalar.toArray());
	}

	@ParameterizedTest
	@MethodSource("backends")
	public void takeGathersInOrderWithRepeats(ArrayBackend backend) {
		DoubleArrayND cube = cube(backend);
		DoubleArrayND taken = cube.take(2, new long[]{3, 0, 3});
		assertArrayEquals(new long[]{2, 3, 3}, taken.shape());
		assertArrayEquals(new double[]{3, 0, 3}, taken.at(0, 0).at(0, 0).toArray());
		assertArrayEquals(new double[]{123, 120, 123}, taken.at(0, 1).at(0, 2).toArray());
	}

	@ParameterizedTest
	@MethodSource("backends")
	public void viewsCompose(ArrayBackend backend) {
		DoubleArrayND cube = cube(backend);
		// time-series-at-points style access: fix the outer dimension, take points, then a range
		DoubleArrayND composed = cube.at(0, 1).take(1, new long[]{2, 1}).slice(0, 1, 3);
		assertArrayEquals(new long[]{2, 2}, composed.shape());
		assertArrayEquals(new double[]{112, 111, 122, 121}, composed.toArray());
	}

	@ParameterizedTest
	@MethodSource("backends")
	public void copyToHonoursOffset(ArrayBackend backend) {
		double[] destination = new double[6];
		cube(backend).at(0, 0).at(0, 0).copyTo(destination, 2);
		assertArrayEquals(new double[]{0, 0, 0, 1, 2, 3}, destination);
		assertThrows(IndexOutOfBoundsException.class, () -> cube(backend).at(0, 0).at(0, 0).copyTo(destination, 3));
	}

	@ParameterizedTest
	@MethodSource("backends")
	public void outOfBoundsIsRejected(ArrayBackend backend) {
		DoubleArrayND cube = cube(backend);
		assertThrows(IndexOutOfBoundsException.class, () -> cube.get(2, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> cube.get(0, 0));
		assertThrows(IndexOutOfBoundsException.class, () -> cube.slice(0, 1, 3));
		assertThrows(IndexOutOfBoundsException.class, () -> cube.at(3, 0));
		assertThrows(IndexOutOfBoundsException.class, () -> cube.take(1, new long[]{0, 3}));
		assertThrows(IllegalArgumentException.class, () -> backend.wrap(new double[5], 2, 3));
	}

	@ParameterizedTest
	@MethodSource("backends")
	public void backendsAgree(ArrayBackend backend) {
		DoubleArrayND reference = cube(new StridedArrayBackend()).take(1, new long[]{2, 0}).at(2, 1);
		DoubleArrayND candidate = cube(backend).take(1, new long[]{2, 0}).at(2, 1);
		assertTrue(DoubleArrayND.contentEquals(reference, candidate));
	}
}
