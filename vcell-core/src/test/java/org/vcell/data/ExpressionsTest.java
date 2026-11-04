package org.vcell.data;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.data.array.ArrayBackend;
import org.vcell.data.array.DoubleArrayND;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class ExpressionsTest {

	private static final ArrayBackend BACKEND = ArrayBackend.DEFAULT;

	private static double[] evaluate(String expression, List<String> symbols, List<Supplier<DoubleArrayND>> arguments, long... shape) {
		return Expressions.evaluate(expression, symbols, arguments, shape).read(List.of("job", "time"), Selection.all()).toArray();
	}

	@Test
	public void evaluatesPositionByPosition() {
		double[] result = evaluate("a + 2 * b", List.of("a", "b"),
				List.of(() -> BACKEND.wrap(new double[]{1, 2, 3, 4}, 2, 2), () -> BACKEND.wrap(new double[]{10, 20, 30, 40}, 2, 2)), 2, 2);
		assertArrayEquals(new double[]{21, 42, 63, 84}, result);
	}

	@Test
	public void missingValuesAreNaN() {
		// b has one job and a shorter time course; c has no values at all
		double[] result = evaluate("a + b", List.of("a", "b"),
				List.of(() -> BACKEND.wrap(new double[]{1, 2, 3, 4}, 2, 2), () -> BACKEND.wrap(new double[]{10}, 1, 1)), 2, 2);
		assertEquals(11, result[0]);
		assertTrue(Arrays.stream(result, 1, 4).allMatch(Double::isNaN), Arrays.toString(result));
		assertTrue(Double.isNaN(evaluate("c", List.of("c"), java.util.Collections.singletonList(null), 1, 1)[0]));
	}

	@Test
	public void constantsNeedNoArguments() {
		assertArrayEquals(new double[]{3, 3}, evaluate("1 + 2", List.of(), List.of(), 1, 2));
	}

	@Test
	public void failuresAreReadExceptions() {
		assertThrows(DataReadException.class, () -> evaluate("a / 0", List.of("a"), List.of(() -> BACKEND.wrap(new double[]{1}, 1, 1)), 1, 1));
		assertThrows(DataReadException.class, () -> evaluate("unknown", List.of("a"), List.of(() -> BACKEND.wrap(new double[]{1}, 1, 1)), 1, 1));
	}
}
