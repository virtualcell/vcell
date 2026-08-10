package cbit.vcell.math;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * The math namespace must not depend on the biological one.
 * <p>
 * A {@link MathDescription} describes what the solver executes. It is generated <em>from</em> an
 * application (SimulationContext, species context specs, reaction rule specs), but must be
 * describable, comparable and persistable without reference to it - the dependency runs
 * biological &rarr; mathematical &rarr; solver, never back.
 * <p>
 * The hazard is that the two layers model the same domain and share vocabulary, so the biological
 * types have names that look right from inside {@code cbit.vcell.math}: {@code MolecularType},
 * {@code MolecularComponent}, {@code ComponentStateDefinition}, {@code Subtype}. One IDE
 * auto-import re-couples the layers, and nothing else in the build notices. Hence this test.
 * <p>
 * The established remedy is to duplicate the concept into the math namespace under a
 * {@code Particle*} name rather than import it - {@code ParticleMolecularType} beside
 * {@code org.vcell.model.rbm.MolecularType}, {@code ParticleBondType} beside {@code BondType},
 * {@code LangevinParticleJumpProcess.ParticleSubtype} beside {@code ReactionRuleSpec.Subtype}.
 * Where a value crosses the boundary, the math mapping translates it.
 *
 * @see <a href="../../../../../../../docs/springsalad-abstractions.md">docs/springsalad-abstractions.md</a>
 */
@Tag("Fast")
public class MathNamespaceSeparationTest {

	private static final Path MATH_SOURCE_DIR = Paths.get("src/main/java/cbit/vcell/math");

	/**
	 * Packages the math namespace must not depend on: the layer above it and the layer below.
	 * <p>
	 * Biological, because a math description must stand alone (architecture P1). Solver, because a
	 * solver's input format is that solver wrapper's concern (P4) - a {@code writeXxx(StringBuilder)}
	 * on a math class emitting solver syntax is how the dependency creeps back in.
	 */
	private static final List<String> FORBIDDEN_PACKAGES = Arrays.asList(
			"cbit.vcell.model.",
			"cbit.vcell.mapping.",
			"cbit.vcell.biomodel.",
			"org.vcell.model.",
			"org.vcell.solver.");

	/**
	 * Known remaining violations. Currently none: the math namespace imports nothing from the
	 * biological or solver layers.
	 * <p>
	 * This list may shrink, never grow. Adding an entry re-couples the layers, and the remedy is
	 * almost always to duplicate the concept into math under a {@code Particle*} name and translate
	 * at the mapping boundary — see the class javadoc and docs/architecture-layers.md.
	 */
	private static final Set<String> ACCEPTED = new LinkedHashSet<>();

	private static final Pattern IMPORT = Pattern.compile("^\\s*import\\s+(?:static\\s+)?([\\w.]+)\\s*;");

	@Test
	public void mathNamespaceDoesNotImportBiologicalTypes() throws IOException {
		assertTrue(Files.isDirectory(MATH_SOURCE_DIR),
				"expected to run with the module directory as the working directory; " + MATH_SOURCE_DIR.toAbsolutePath() + " not found");

		final List<String> found = new ArrayList<>();
		try (Stream<Path> sources = Files.walk(MATH_SOURCE_DIR)) {
			for (Path source : (Iterable<Path>) sources.filter(p -> p.toString().endsWith(".java"))::iterator) {
				for (String line : Files.readAllLines(source, StandardCharsets.UTF_8)) {
					final Matcher matcher = IMPORT.matcher(line);
					if (!matcher.find()) {
						continue;
					}
					final String imported = matcher.group(1);
					if (FORBIDDEN_PACKAGES.stream().anyMatch(imported::startsWith)) {
						found.add(source.getFileName() + " -> " + imported);
					}
				}
			}
		}

		final List<String> unexpected = new ArrayList<>(found);
		unexpected.removeAll(ACCEPTED);

		final List<String> staleExceptions = new ArrayList<>(ACCEPTED);
		staleExceptions.removeAll(found);

		if (!unexpected.isEmpty()) {
			fail("cbit.vcell.math must not import biological or solver types - the math description has to\n"
					+ "stand alone, and solver formats belong to the solver wrapper.\n"
					+ "New violation(s):\n  " + String.join("\n  ", unexpected) + "\n\n"
					+ "Duplicate the concept into cbit.vcell.math under a Particle* name and translate at the\n"
					+ "mapping boundary; see docs/springsalad-abstractions.md. Do not add it to ACCEPTED.");
		}
		if (!staleExceptions.isEmpty()) {
			fail("These accepted violations no longer exist - delete them from ACCEPTED so the list keeps\n"
					+ "reflecting reality:\n  " + String.join("\n  ", staleExceptions));
		}
	}
}
