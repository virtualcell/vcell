package org.vcell.data;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.data.array.ArrayBackend;
import org.vcell.data.array.DoubleArrayND;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class DatasetTest {

	private static final ArrayBackend BACKEND = ArrayBackend.DEFAULT;

	/**
	 * A small spatial-shaped dataset: a volume variable over (time, volume), a membrane variable over
	 * (time, membrane), and a constant over (volume). Values encode their indices: 100*t + i.
	 */
	private static Dataset sample() {
		double[] volume = new double[3 * 4];
		double[] membrane = new double[3 * 2];
		for (int t = 0; t < 3; t++) {
			for (int i = 0; i < 4; i++) volume[t * 4 + i] = 100 * t + i;
			for (int i = 0; i < 2; i++) membrane[t * 2 + i] = 100 * t + i + 0.5;
		}
		return Dataset.builder("sample")
				.dimension(new Dimension.Numeric(Dimension.TIME, new double[]{0, 0.5, 1.0}, "s"))
				.dimension(new Dimension.Indexed("volume", 4))
				.dimension(new Dimension.Indexed("membrane", 2))
				.variable(Variable.data("A", List.of(Dimension.TIME, "volume")), BACKEND.wrap(volume, 3, 4))
				.variable(Variable.data("M", List.of(Dimension.TIME, "membrane")), BACKEND.wrap(membrane, 3, 2))
				.variable(new Variable("k", List.of("volume"), "uM", Variable.Role.DATA, null, "cyt", null), BACKEND.wrap(new double[]{1, 2, 3, 4}, 4))
				.attribute("simulation", "test sim")
				.build();
	}

	@Test
	public void valuesComeBackShaped() {
		Dataset dataset = sample();
		assertArrayEquals(new long[]{3, 4}, dataset.shapeOf("A"));
		assertEquals(203, dataset.values("A").get(2, 3));
		assertEquals(101.5, dataset.values("M").get(1, 1));
		assertEquals("cyt", dataset.variable("k").domain());
		assertEquals("test sim", dataset.attributes().get("simulation"));
		assertEquals(Topology.NONSPATIAL, dataset.topology());
	}

	@Test
	public void selectRangeNarrowsDimensionAndValues() {
		Dataset selected = sample().select(Selection.all().range(Dimension.TIME, 1, 3));
		Dimension.Numeric time = (Dimension.Numeric) selected.dimension(Dimension.TIME);
		assertArrayEquals(new double[]{0.5, 1.0}, time.values());
		assertEquals("s", time.unit());
		assertArrayEquals(new long[]{2, 4}, selected.values("A").shape());
		assertEquals(100, selected.values("A").get(0, 0));
		assertArrayEquals(new double[]{1, 2, 3, 4}, selected.values("k").toArray(), "variables without the dimension are untouched");
	}

	@Test
	public void selectTakeRemembersSourceIndices() {
		Dataset points = sample().select(Selection.all().take("volume", 3, 1));
		Dimension.Indexed volume = (Dimension.Indexed) points.dimension("volume");
		assertArrayEquals(new int[]{3, 1}, volume.sourceIndices());
		assertArrayEquals(new double[]{3, 1, 103, 101, 203, 201}, points.values("A").toArray());

		// a further take indexes into the subset, and source indices still point at the original elements
		Dataset point = points.select(Selection.all().take("volume", 1));
		assertArrayEquals(new int[]{1}, ((Dimension.Indexed) point.dimension("volume")).sourceIndices());
		assertArrayEquals(new double[]{1, 101, 201}, point.values("A").toArray());
	}

	@Test
	public void selectAtRemovesDimension() {
		Dataset snapshot = sample().select(Selection.all().at(Dimension.TIME, 2));
		assertFalse(snapshot.hasDimension(Dimension.TIME));
		assertEquals(List.of("volume"), snapshot.variable("A").dimensions());
		assertArrayEquals(new double[]{200, 201, 202, 203}, snapshot.values("A").toArray());
		assertArrayEquals(new double[]{200.5, 201.5}, snapshot.values("M").toArray());
	}

	@Test
	public void selectVariables() {
		Dataset onlyA = sample().select(Selection.all().variables("A"));
		assertEquals(List.of("A"), onlyA.variables().stream().map(Variable::name).toList());
		assertTrue(onlyA.hasDimension("membrane"), "dimensions are kept even when no variable uses them");

		Dataset reordered = sample().select(Selection.all().variables("k", "A"));
		assertEquals(List.of("k", "A"), reordered.variables().stream().map(Variable::name).toList(), "named variables come out in the order named");
	}

	@Test
	public void selectIsLazyAndPushesSelectionDown() {
		AtomicReference<Selection> seen = new AtomicReference<>();
		DoubleArrayND backing = BACKEND.wrap(new double[]{1, 2, 3, 4}, 4);
		Dataset lazy = Dataset.builder("lazy")
				.dimension(new Dimension.Indexed("x", 4))
				.variable(Variable.data("v", List.of("x")), (dimensions, selection) -> {
					seen.set(selection);
					return Selections.apply(backing, dimensions, selection);
				})
				.build();
		Dataset selected = lazy.select(Selection.all().range("x", 1, 3));
		assertNull(seen.get(), "selecting reads nothing");
		assertArrayEquals(new double[]{2, 3}, selected.values("v").toArray());
		assertEquals(new Selection.Range(1, 3), seen.get().pick("x"), "the source was asked for only the selected range");
	}

	@Test
	public void builderRejectsInconsistency() {
		Dataset.Builder builder = Dataset.builder("bad").dimension(new Dimension.Indexed("x", 3));
		assertThrows(IllegalArgumentException.class, () -> builder.variable(Variable.data("v", List.of("y")), BACKEND.zeros(3)));
		assertThrows(IllegalArgumentException.class, () -> builder.variable(Variable.data("v", List.of("x")), BACKEND.zeros(4)));
		assertThrows(IllegalArgumentException.class, () -> builder.dimension(new Dimension.Indexed("x", 5)));
		builder.variable(Variable.data("v", List.of("x")), BACKEND.zeros(3));
		assertThrows(IllegalArgumentException.class, () -> builder.variable(Variable.data("v", List.of("x")), BACKEND.zeros(3)));
	}

	@Test
	public void sourceProducingWrongShapeIsCaughtOnRead() {
		Dataset dataset = Dataset.builder("liar")
				.dimension(new Dimension.Indexed("x", 3))
				.variable(Variable.data("v", List.of("x")), (dimensions, selection) -> BACKEND.zeros(2))
				.build();
		assertThrows(DataReadException.class, () -> dataset.values("v"));
	}

	@Test
	public void selectRejectsUnknownNamesAndBadIndices() {
		Dataset dataset = sample();
		assertThrows(NoSuchElementException.class, () -> dataset.select(Selection.all().at("nope", 0)));
		assertThrows(NoSuchElementException.class, () -> dataset.select(Selection.all().variables("nope")));
		assertThrows(IndexOutOfBoundsException.class, () -> dataset.select(Selection.all().range(Dimension.TIME, 0, 4)));
		assertThrows(IndexOutOfBoundsException.class, () -> dataset.select(Selection.all().take("volume", 4)));
		assertThrows(IndexOutOfBoundsException.class, () -> dataset.select(Selection.all().at(Dimension.TIME, 3)));
	}

	@Test
	public void stackAddsOuterDimension() {
		Dataset first = sample();
		Dataset second = sample();
		Dataset stacked = DatasetOps.stack("both", new Dimension.Labeled(Dimension.STATISTIC, List.of("a", "b")), List.of(first, second));
		assertEquals(List.of(Dimension.STATISTIC, Dimension.TIME, "volume"), stacked.variable("A").dimensions());
		assertArrayEquals(new long[]{2, 3, 4}, stacked.shapeOf("A"));
		assertEquals(203, stacked.values("A").get(1, 2, 3));
		assertEquals("test sim", stacked.attributes().get("simulation"), "attributes all parts share are kept");
	}

	@Test
	public void stackRejectsMismatchedParts() {
		Dataset fewerTimes = sample().select(Selection.all().range(Dimension.TIME, 0, 2));
		assertThrows(IllegalArgumentException.class,
				() -> DatasetOps.stack("x", new Dimension.Indexed(Dimension.JOB, 2), List.of(sample(), fewerTimes)));
		assertThrows(IllegalArgumentException.class,
				() -> DatasetOps.stack("x", new Dimension.Indexed(Dimension.JOB, 3), List.of(sample(), sample())));
	}
}
