package cbit.vcell.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The five server services now run from one image, and the shared entrypoint names the main
 * class for each. Those names are strings in a shell script: nothing in the Java build refers to
 * them, so renaming or moving a main class compiles cleanly, passes every test, builds a
 * healthy-looking image, and fails at deploy time with NoClassDefFoundError.
 *
 * That was survivable when each name sat in its own Dockerfile beside the service it launched.
 * With one script launching five services it is worth a test, which is what this is: every main
 * class the entrypoint dispatches to must exist in the source tree, and every service must have
 * a matching log4j configuration to be copied into the image.
 */
@Tag("Fast")
public class ServiceEntrypointTest {

	private static final Path ENTRYPOINT = Path.of("..", "docker", "build", "service", "entrypoint.sh");

	/** {@code main_class=cbit.vcell.message.server.db.DatabaseServer} within a {@code case} arm. */
	private static final Pattern SERVICE_ARM = Pattern.compile(
			"^\\s*(\\w+)\\)\\s*$\\s*main_class=([A-Za-z0-9_.]+)", Pattern.MULTILINE);

	private static Map<String, String> mainClassByService() throws IOException {
		Assumptions.assumeTrue(Files.isRegularFile(ENTRYPOINT),
				ENTRYPOINT + " not reachable from " + Path.of("").toAbsolutePath());
		Map<String, String> found = new LinkedHashMap<>();
		Matcher m = SERVICE_ARM.matcher(Files.readString(ENTRYPOINT));
		while (m.find()) {
			found.put(m.group(1), m.group(2));
		}
		return found;
	}

	/** The image is only useful if it can launch every service the deployment asks it for. */
	@Test
	public void theEntrypointDispatchesToAllFiveServices() throws IOException {
		assertEquals(List.of("api", "data", "db", "sched", "submit"),
				new ArrayList<>(mainClassByService().keySet()),
				"these are the five services the Kubernetes Deployments pass as args");
	}

	/**
	 * The point of the test. A main class that no longer exists is invisible until a pod starts.
	 */
	@Test
	public void everyMainClassExistsInTheSourceTree() throws IOException {
		Path repo = Path.of("..");
		List<String> missing = new ArrayList<>();
		for (Map.Entry<String, String> entry : mainClassByService().entrySet()) {
			String relative = entry.getValue().replace('.', '/') + ".java";
			boolean exists;
			try (Stream<Path> modules = Files.list(repo)) {
				exists = modules.filter(Files::isDirectory)
						.map(module -> module.resolve("src/main/java").resolve(relative))
						.anyMatch(Files::isRegularFile);
			}
			if (!exists) {
				missing.add(entry.getKey() + " -> " + entry.getValue());
			}
		}
		assertEquals(List.of(), missing,
				"the entrypoint names main classes that no longer exist; a pod would start and"
						+ " immediately fail with NoClassDefFoundError");
	}

	/** Each service loads its own log4j file by name, so each must actually be there. */
	@Test
	public void everyServiceHasALog4jConfiguration() throws IOException {
		Path dockerBuild = Path.of("..", "docker", "build");
		List<String> missing = new ArrayList<>();
		for (String service : mainClassByService().keySet()) {
			if (!Files.isRegularFile(dockerBuild.resolve("vcell-" + service + ".log4j.xml"))) {
				missing.add("vcell-" + service + ".log4j.xml");
			}
		}
		assertEquals(List.of(), missing, "the entrypoint would start the JVM with a missing"
				+ " log4j.configurationFile, silently losing the service's logging");
	}

	/** And the consolidated Dockerfile has to copy each of those in. */
	@Test
	public void theImageCopiesEveryLog4jConfiguration() throws IOException {
		Path dockerfile = Path.of("..", "docker", "build", "Dockerfile-service-dev");
		Assumptions.assumeTrue(Files.isRegularFile(dockerfile), dockerfile + " not found");
		String text = Files.readString(dockerfile);
		for (String service : mainClassByService().keySet()) {
			assertTrue(text.contains("vcell-" + service + ".log4j.xml"),
					"Dockerfile-service-dev does not copy vcell-" + service + ".log4j.xml");
		}
	}
}
