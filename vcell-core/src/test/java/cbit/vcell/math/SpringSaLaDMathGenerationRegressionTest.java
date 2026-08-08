package cbit.vcell.math;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * End-to-end regression over SpringSaLaD math <em>generation</em>: BioModel in, MathDescription out.
 *
 * <h2>Why this exists</h2>
 *
 * {@code LangevinMathMapping} is the algorithm that turns a biological application into math, and it
 * is where a mistranslation would actually land. Separating the math namespace from the biological
 * one split types that used to be shared - most consequentially {@code ReactionRuleSpec.Subtype} and
 * {@code TransitionCondition}, which the math layer now owns as
 * {@code LangevinParticleJumpProcess.ParticleSubtype} and {@code ParticleTransitionCondition}, with
 * {@code LangevinMathMapping.toMath(...)} translating at the boundary. A wrong translation there
 * would silently produce a subtly different math, and no amount of comparison testing would notice,
 * because both sides of the comparison would be equally wrong.
 *
 * <h2>The baseline</h2>
 *
 * The checked-in files under {@code springsalad-baseline/} were captured from the code
 * <em>before</em> the separation, and the post-separation output was verified byte-identical for
 * every model here. From now on they are simply the accepted output, and any change to them is a
 * change in generated math that somebody has to justify.
 * <p>
 * The two {@code *_bad} models fail generation by design; their captured validation errors are
 * baselined too, so a change in what SpringSaLaD rejects is caught as well as a change in what it
 * produces.
 *
 * <h2>When generated math legitimately changes</h2>
 *
 * Re-run with {@code -Dspringsalad.baseline.update=true} to rewrite the baselines, then read the
 * resulting diff carefully and commit it as a deliberate change. Do not delete a failing baseline.
 */
@Tag("Fast")
public class SpringSaLaDMathGenerationRegressionTest {

	/** Set to rewrite baselines rather than assert against them. */
	private static final String UPDATE_PROPERTY = "springsalad.baseline.update";

	private static final String BASELINE_RESOURCE_DIR = "/cbit/vcell/math/springsalad-baseline/";
	private static final Path BASELINE_SOURCE_DIR =
			Paths.get("src/test/resources/cbit/vcell/math/springsalad-baseline");

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

	/**
	 * Generate the math exactly as the capture did, including recording a generation failure as text
	 * so that models which are meant to be rejected are covered too.
	 */
	private static String generateMath(String model) throws Exception {
		try {
			final String vcml;
			try (InputStream in = SpringSaLaDMathGenerationRegressionTest.class
					.getResourceAsStream("/cbit/vcell/biomodel/" + model)) {
				assertNotNull(in, "model not on the classpath: " + model);
				vcml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
			}
			final BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcml));
			SimulationContext simContext = null;
			for (SimulationContext candidate : bioModel.getSimulationContexts()) {
				if (candidate.getApplicationType() == SimulationContext.Application.SPRINGSALAD) {
					simContext = candidate;
					break;
				}
			}
			if (simContext == null) {
				simContext = bioModel.addNewSimulationContext("App", SimulationContext.Application.SPRINGSALAD);
			}
			bioModel.updateAll(false);
			final MathDescription math = simContext.getMathDescription();
			return math == null ? "NO_MATH" : math.getVCML_database();
		} catch (Throwable t) {
			return "GENERATION_FAILED: " + t.getClass().getSimpleName() + ": " + t.getMessage();
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Spring_reactions_good.vcml",
			"Spring_transition_free.vcml",
			"Spring_model_bad.vcml",
			"Spring_reactions_bad.vcml"})
	public void generatedMathMatchesBaseline(String model) throws Exception {
		final String generated = generateMath(model);
		final String baselineName = model.replace(".vcml", "") + ".math.txt";

		if (Boolean.getBoolean(UPDATE_PROPERTY)) {
			Files.createDirectories(BASELINE_SOURCE_DIR);
			Files.write(BASELINE_SOURCE_DIR.resolve(baselineName), generated.getBytes(StandardCharsets.UTF_8));
			System.out.println("rewrote baseline " + baselineName + " (" + generated.length() + " chars)");
			return;
		}

		final String baseline;
		try (InputStream in = SpringSaLaDMathGenerationRegressionTest.class
				.getResourceAsStream(BASELINE_RESOURCE_DIR + baselineName)) {
			assertNotNull(in, "baseline not found: " + baselineName
					+ " - capture it with -D" + UPDATE_PROPERTY + "=true and commit it deliberately");
			baseline = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}

		assertEquals(baseline, generated,
				"generated math for " + model + " differs from the accepted baseline.\n"
						+ "If this change is intended, re-run with -D" + UPDATE_PROPERTY + "=true,"
						+ " read the diff, and commit it as a deliberate change to generated math.");
	}
}
