package cbit.vcell.math;

import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.springsalad.Colors;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The three comparison tiers, asserted by the consequence each one has for the user.
 *
 * <ul>
 * <li><b>identical</b> ({@code compareEqual}) — the math must be saved, or the edit is lost.</li>
 * <li><b>equivalent</b> — {@code SimulationVersion.parentSimulationReference} is kept, so existing
 *     simulation results stay attached and visible.</li>
 * <li><b>not equivalent</b> — the parent branch is cleared on save and existing results are hidden.</li>
 * </ul>
 *
 * <p>Two cases pin the boundary. A site <b>radius</b> reaches the solver, so changing it must break
 * equivalence — otherwise results computed with the old radius stay attached to the new math.
 * <b>Colour</b> never reaches the solver, so changing it must <em>not</em> break equivalence, or a
 * cosmetic edit throws away the user's results — while still counting as a difference for the
 * identical tier, or the colour change is never persisted.
 *
 * <p>Canonicalisation flattens equations over state variables and neither compares nor preserves
 * particle molecular types, so before this was fixed a changed radius was reported as equivalent.
 */
@Tag("Fast")
public class LangevinEquivalenceTest {

	private static final String MODEL = "Spring_simulation_transition.vcml";

	private static String previousInstallDir = null;

	@BeforeAll
	public static void setup() {
		previousInstallDir = PropertyLoader.getProperty(PropertyLoader.installationRoot, null);
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
	}

	@AfterAll
	public static void tearDown() {
		if (previousInstallDir != null) {
			PropertyLoader.setProperty(PropertyLoader.installationRoot, previousInstallDir);
		}
	}

	private static MathDescription load() throws Exception {
		try (InputStream in = LangevinEquivalenceTest.class.getResourceAsStream("/cbit/vcell/biomodel/" + MODEL)) {
			assertNotNull(in, "test model not found: " + MODEL);
			final MathModel mathModel = XmlHelper.XMLToMathModel(
					new XMLSource(new String(in.readAllBytes(), StandardCharsets.UTF_8)));
			return mathModel.getMathDescription();
		}
	}

	private static LangevinParticleMolecularComponent firstSite(MathDescription math) {
		for (ParticleMolecularType type : math.getParticleMolecularTypes()) {
			for (ParticleMolecularComponent component : type.getComponentList()) {
				if (component instanceof LangevinParticleMolecularComponent) {
					return (LangevinParticleMolecularComponent) component;
				}
			}
		}
		throw new IllegalStateException("no Langevin site in " + MODEL);
	}

	private static MathCompareResults compare(MathDescription before, MathDescription after) {
		return MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), before, after);
	}

	@Test
	public void unchangedMathIsEquivalent() throws Exception {
		assertTrue(compare(load(), load()).isEquivalent(), "a math must be equivalent to itself");
	}

	/** Reaches the solver: results computed with the old value must not stay attached. */
	@Test
	public void aChangedRadiusBreaksEquivalence() throws Exception {
		final MathDescription before = load();
		final MathDescription after = load();
		firstSite(after).setRadius(new Expression(99.0));

		final MathCompareResults results = compare(before, after);
		assertFalse(results.isEquivalent(),
				"a changed site radius must break equivalence, or stale results stay attached: "
						+ results.toDatabaseStatus());
		assertEquals(MathCompareResults.Decision.MathDifferent_DIFFERENT_PARTICLE_MOLECULAR_TYPES, results.decision,
				"expected the molecular-type decision, got " + results.toDatabaseStatus());
	}

	@Test
	public void aChangedDiffusionRateBreaksEquivalence() throws Exception {
		final MathDescription before = load();
		final MathDescription after = load();
		firstSite(after).setDiffusionRate(new Expression(77.0));
		assertFalse(compare(before, after).isEquivalent(), "a changed diffusion rate must break equivalence");
	}

	@Test
	public void aChangedIs2DBreaksEquivalence() throws Exception {
		final MathDescription before = load();
		final MathDescription after = load();
		for (ParticleMolecularType type : after.getParticleMolecularTypes()) {
			if (type instanceof LangevinParticleMolecularType) {
				((LangevinParticleMolecularType) type).setIs2D(true);
				break;
			}
		}
		assertFalse(compare(before, after).isEquivalent(), "a changed is2D flag must break equivalence");
	}

	/**
	 * Never reaches the solver: the edit must be saved, but must not cost the user their results.
	 */
	@Test
	public void aChangedColourSavesButStaysEquivalent() throws Exception {
		final MathDescription before = load();
		final MathDescription after = load();
		final LangevinParticleMolecularComponent site = firstSite(after);
		site.setColor(Colors.BLUE.equals(site.getColor()) ? Colors.RED : Colors.BLUE);

		assertFalse(before.compareEqual(after),
				"a colour change must not be 'identical', or the edit is never saved");
		assertTrue(compare(before, after).isEquivalent(),
				"a colour change must remain equivalent, or a cosmetic edit discards the user's results: "
						+ compare(before, after).toDatabaseStatus());
	}

	/**
	 * A different expression for the same value is not a change: comparison is by functional
	 * equivalence, which is the point of holding math scalars as expressions.
	 */
	@Test
	public void anEquivalentExpressionForTheSameValueIsNotAChange() throws Exception {
		final MathDescription before = load();
		final MathDescription after = load();
		final LangevinParticleMolecularComponent site = firstSite(after);
		site.setRadius(Expression.add(site.getRadius(), new Expression(0.0)));

		assertTrue(compare(before, after).isEquivalent(),
				"'r' and 'r + 0' describe the same radius and must remain equivalent");
	}

	/**
	 * A non-equivalent result must carry the differences, and they must name the field that changed —
	 * a {@code Decision} alone tells you the math changed, not what to go and look at.
	 */
	@Test
	public void aNonEquivalentResultNamesWhatChanged() throws Exception {
		final MathDescription before = load();
		final MathDescription after = load();
		firstSite(after).setRadius(new Expression(99.0));

		final MathCompareResults results = compare(before, after);
		assertTrue(results.hasDifferences(),
				"a non-equivalent result should carry differences, got: " + results.toDatabaseStatus());

		final String report = results.toDifferenceReport();
		assertTrue(report.contains("radius"), "the report should name the radius: " + report);
		assertTrue(report.contains("99.0"), "the report should carry the new value: " + report);
		assertTrue(report.contains("site '"), "the report should identify the site: " + report);

		// the persisted status format must not move - it is written to a length-limited column
		assertTrue(results.toDatabaseStatus().startsWith(results.decision.description),
				"unexpected database status: " + results.toDatabaseStatus());
	}

	/**
	 * The describer never contradicts {@code compareEqual}: it is asked whether objects differ, and
	 * only then descends to name the attribute. So an equivalent math yields no differences at all.
	 */
	@Test
	public void anEquivalentMathYieldsNoDifferences() throws Exception {
		assertTrue(MathDescriptionDifferences.describe(load(), load()).isEmpty(),
				"identical maths must produce no differences");
	}

	/** Matching is by name, so reordering must not be reported as a change. */
	@Test
	public void reorderingIsNotADifference() throws Exception {
		final MathDescription before = load();
		final MathDescription after = load();
		final java.util.List<ParticleMolecularType> types = after.getParticleMolecularTypes();
		if (types.size() >= 2) {
			final ParticleMolecularType first = types.get(0);
			types.set(0, types.get(1));
			types.set(1, first);
		}
		assertTrue(MathDescriptionDifferences.describe(before, after).isEmpty(),
				"reordering molecular types must not be reported as a difference");
	}

	/** Diagnostics must never change an outcome. */
	@Test
	public void describingNeverThrows() {
		assertTrue(MathDescriptionDifferences.describe(null, null).isEmpty(), "null input must be tolerated");
	}
}
