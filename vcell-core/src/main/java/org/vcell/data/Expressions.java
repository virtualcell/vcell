package org.vcell.data;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import org.vcell.data.array.ArrayBackend;
import org.vcell.data.array.DoubleArrayND;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Values computed from other values by a VCell {@link Expression}, position by position.
 */
public final class Expressions {
	private Expressions() { }

	/**
	 * @param expression the expression, in VCell infix syntax
	 * @param symbols    the expression's symbols, in the order {@code arguments} gives their values
	 * @param arguments  each symbol's values, read when the source is; null for a symbol without values. An argument
	 *                   may be smaller than {@code shape} along any dimension (fewer jobs, a shorter time course)
	 * @param shape      the shape of the result; every argument has the same rank
	 * @return a source evaluating {@code expression} at every position of {@code shape}. A symbol with no value at a
	 * position (null, or the argument stops short of it) is NaN there.
	 */
	public static ValueSource evaluate(String expression, List<String> symbols, List<Supplier<DoubleArrayND>> arguments, long[] shape) {
		if (symbols.size() != arguments.size()) throw new IllegalArgumentException("Symbols and arguments differ in number");
		long[] resultShape = shape.clone();
		return (dimensions, selection) -> {
			Expression bound;
			try {
				bound = new Expression(expression);
				bound.bindExpression(new SimpleSymbolTable(symbols.toArray(new String[0])));
			} catch (ExpressionException e) {
				throw new DataReadException("Cannot bind '" + expression + "' to " + symbols, e);
			}
			DoubleArrayND[] values = new DoubleArrayND[arguments.size()];
			for (int i = 0; i < values.length; i++) {
				Supplier<DoubleArrayND> argument = arguments.get(i);
				values[i] = argument == null ? null : argument.get();
				if (values[i] != null && values[i].rank() != resultShape.length)
					throw new DataReadException("Values of '" + symbols.get(i) + "' have rank " + values[i].rank() + ", expected " + resultShape.length);
			}
			DoubleArrayND result = ArrayBackend.DEFAULT.zeros(resultShape);
			long[] position = new long[resultShape.length];
			double[] point = new double[values.length];
			for (long flat = 0; flat < result.size(); flat++) {
				for (int i = 0; i < values.length; i++) point[i] = Expressions.valueAt(values[i], position);
				try {
					result.set(bound.evaluateVector(point), position);
				} catch (ExpressionException e) {
					throw new DataReadException("Evaluating '" + expression + "' at " + Arrays.toString(position) + " failed", e);
				}
				Expressions.advance(position, resultShape);
			}
			return Selections.apply(result, dimensions, selection);
		};
	}

	private static double valueAt(DoubleArrayND values, long[] position) {
		if (values == null) return Double.NaN;
		long[] shape = values.shape();
		for (int d = 0; d < position.length; d++) if (position[d] >= shape[d]) return Double.NaN;
		return values.get(position);
	}

	/** Moves {@code position} to the next one in row-major order. */
	private static void advance(long[] position, long[] shape) {
		for (int d = position.length - 1; d >= 0; d--) {
			if (++position[d] < shape[d]) return;
			position[d] = 0;
		}
	}
}
