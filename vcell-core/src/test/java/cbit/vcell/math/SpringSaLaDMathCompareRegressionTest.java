package cbit.vcell.math;

import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import cbit.vcell.resource.PropertyLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression corpus for SpringSaLaD math comparison.
 *
 * <h2>What is the oracle?</h2>
 *
 * There are two candidates and they deliberately disagree, which is what makes them useful:
 *
 * <ul>
 * <li><b>The math description XML</b> is the oracle for the <em>identical</em> tier
 *     ({@code compareEqual}). {@code ServerDocumentManager} decides whether to re-save a math from
 *     {@code compareEqual}, so if the serialized form differs and the comparison says "identical",
 *     the change is silently lost. Hence the invariant asserted here: <b>a math that round-trips
 *     through XML must compare equal to itself</b>.</li>
 * <li><b>The solver input file</b> ({@code .lngv}) is the oracle for the <em>equivalent</em> tier.
 *     It decides whether existing simulation results are still valid: identical solver input means
 *     the solver would do the same thing.</li>
 * </ul>
 *
 * They differ precisely on attributes that are persisted but never reach the solver - site colour is
 * the current example. Under the XML oracle a colour change is a difference (and must be saved);
 * under the solver-input oracle it is not (results remain valid). That is why {@code compareEqual}
 * compares colour while an equivalence tier may legitimately ignore it.
 *
 * <h2>Why round-trip rather than expected values</h2>
 *
 * Asserting against checked-in expected output would need regenerating whenever math generation
 * legitimately changes. The round-trip invariant needs no expected values and directly catches the
 * failure mode that matters: a field the comparison examines but serialization drops. Two such bugs
 * existed in {@code is2D} alone - the VCML reader called {@code Boolean.getBoolean} (which reads a
 * system property, not the token) and the XML path omitted the attribute entirely - and each would
 * have shown up here as a model that fails to compare equal with itself.
 */
@Tag("Fast")
public class SpringSaLaDMathCompareRegressionTest {

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

	private static String readResource(String resource) throws Exception {
		try (InputStream in = SpringSaLaDMathCompareRegressionTest.class
				.getResourceAsStream("/cbit/vcell/biomodel/" + resource)) {
			assertNotNull(in, "test model not found on the classpath: " + resource);
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	/**
	 * A stored SpringSaLaD math must compare equal to itself after a round trip through the math XML.
	 * <p>
	 * This is the invariant the identical tier has to satisfy. Any field the comparison examines but
	 * serialization drops shows up here, and in the safe direction: a difference reported that does
	 * not exist, rather than a real one silently missed. Both {@code is2D} bugs - the VCML reader
	 * calling {@code Boolean.getBoolean}, and the XML path omitting the attribute - are exactly this
	 * shape.
	 */
	@ParameterizedTest
	@ValueSource(strings = {"Spring_simulation_transition.vcml"})
	public void storedMathSurvivesXmlRoundTrip(String resource) throws Exception {
		final MathModel original = XmlHelper.XMLToMathModel(new XMLSource(readResource(resource)));
		final MathDescription mathBefore = original.getMathDescription();
		assertNotNull(mathBefore, resource + ": no math description");
		assertTrue(mathBefore.isLangevin(), resource + " is not Langevin math - the corpus is not testing what it claims");

		final MathModel roundTripped = XmlHelper.XMLToMathModel(new XMLSource(XmlHelper.mathModelToXML(original)));
		final MathDescription mathAfter = roundTripped.getMathDescription();
		assertNotNull(mathAfter, resource + ": no math description after round trip");

		assertTrue(mathBefore.compareEqual(mathAfter), resource
				+ ": math does not compare equal to itself after an XML round trip"
				+ " - a field the comparison examines is not being serialized");
		assertTrue(mathAfter.compareEqual(mathBefore), resource + ": comparison is not symmetric");
	}

	/**
	 * Changing one Langevin field must be seen, and must survive serialization.
	 * <p>
	 * Guards the pairing the round-trip test alone cannot: that the field is both compared
	 * <em>and</em> persisted. A field that is neither would pass the round-trip test silently.
	 */
	@ParameterizedTest
	@ValueSource(strings = {"Spring_simulation_transition.vcml"})
	public void aChangedFieldIsSeenAndSurvivesSerialization(String resource) throws Exception {
		final MathModel model = XmlHelper.XMLToMathModel(new XMLSource(readResource(resource)));
		final MathModel mutated = XmlHelper.XMLToMathModel(new XMLSource(readResource(resource)));

		final List<ParticleMolecularType> types = mutated.getMathDescription().getParticleMolecularTypes();
		assertTrue(!types.isEmpty(), resource + ": expected at least one particle molecular type");
		LangevinParticleMolecularComponent site = null;
		for (ParticleMolecularType type : types) {
			for (ParticleMolecularComponent component : type.getComponentList()) {
				if (component instanceof LangevinParticleMolecularComponent) {
					site = (LangevinParticleMolecularComponent) component;
					break;
				}
			}
			if (site != null) {
				break;
			}
		}
		assertNotNull(site, resource + ": expected at least one Langevin site");
		site.setRadius(site.getRadius() + 1.0);

		assertTrue(!model.getMathDescription().compareEqual(mutated.getMathDescription()),
				resource + ": a changed site radius was not detected");

		// and the change must still be visible after a serialization round trip
		final MathModel mutatedRoundTripped =
				XmlHelper.XMLToMathModel(new XMLSource(XmlHelper.mathModelToXML(mutated)));
		assertTrue(!model.getMathDescription().compareEqual(mutatedRoundTripped.getMathDescription()),
				resource + ": the changed radius did not survive serialization");
	}

	/**
	 * {@code is2D} specifically, because it is the field whose serialization was broken in two
	 * different ways and whose default ({@code false}) makes a round-trip test vacuous unless the
	 * value is deliberately flipped.
	 * <p>
	 * Fails if the XML writer omits the attribute, or if the reader parses it with
	 * {@code Boolean.getBoolean} (which reads a system property of that name, not the token).
	 */
	@ParameterizedTest
	@ValueSource(strings = {"Spring_simulation_transition.vcml"})
	public void is2DIsComparedAndSurvivesSerialization(String resource) throws Exception {
		final MathModel model = XmlHelper.XMLToMathModel(new XMLSource(readResource(resource)));
		final MathModel mutated = XmlHelper.XMLToMathModel(new XMLSource(readResource(resource)));

		LangevinParticleMolecularType type = null;
		for (ParticleMolecularType candidate : mutated.getMathDescription().getParticleMolecularTypes()) {
			if (candidate instanceof LangevinParticleMolecularType) {
				type = (LangevinParticleMolecularType) candidate;
				break;
			}
		}
		assertNotNull(type, resource + ": expected a Langevin molecular type");
		assertTrue(!type.getIs2D(), resource + ": expected is2D to start false, so flipping it is a real change");
		type.setIs2D(true);

		assertTrue(!model.getMathDescription().compareEqual(mutated.getMathDescription()),
				resource + ": a changed is2D flag was not detected");

		final MathModel mutatedRoundTripped =
				XmlHelper.XMLToMathModel(new XMLSource(XmlHelper.mathModelToXML(mutated)));
		assertTrue(!model.getMathDescription().compareEqual(mutatedRoundTripped.getMathDescription()),
				resource + ": is2D did not survive serialization - the XML writer or reader is dropping it");
	}
}
