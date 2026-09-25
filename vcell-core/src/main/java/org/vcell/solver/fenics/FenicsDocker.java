package org.vcell.solver.fenics;

import org.vcell.util.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Runs the FEniCSx solver image with the local Docker CLI (Docker Desktop, or a Linux Docker engine) for
 * a desktop quick run. PR V2 of docs/plan-fenics.md; the container contract is vcell-fenics ADR 011 §5.
 * <p>
 * The simulation directory is bind-mounted into the container: at the same path on macOS and Linux
 * (so no path in the command needs translating), and at {@link #WINDOWS_CONTAINER_DIR} on Windows, where
 * a host path is not a valid container path. On Unix the container runs as the owner of that directory,
 * so the results bundle it writes is the user's.
 */
public final class FenicsDocker {

	private static final Logger lg = LogManager.getLogger(FenicsDocker.class);

	/** the image a client uses unless {@code vcell.fenics.image} names another */
	public static final String DEFAULT_IMAGE = "ghcr.io/virtualcell/vcell-fenics:sha-479d653";
	/** the solver image is built for linux/amd64 first; an arm64 host can run it emulated */
	static final String FALLBACK_PLATFORM = "linux/amd64";
	static final String WINDOWS_CONTAINER_DIR = "/simdata";

	private static final long QUERY_TIMEOUT_SECONDS = 30;

	private final File docker;

	public FenicsDocker(File docker) {
		this.docker = docker;
	}

	public File getDockerExecutable() {
		return docker;
	}

	public static String image() {
		String image = PropertyLoader.getProperty(PropertyLoader.fenicsImage, null);
		return (image == null || image.isBlank()) ? DEFAULT_IMAGE : image.trim();
	}

	/**
	 * Finds the {@code docker} CLI: VCell's configuration, then PATH, then where Docker Desktop and the
	 * package managers install it (a client started from the Finder or the Start menu does not see the
	 * shell's PATH).
	 */
	public static FenicsDocker find() throws FileNotFoundException {
		try {
			return new FenicsDocker(ResourceUtil.getExecutable("docker", false));
		} catch (FileNotFoundException e) {
			// fall through to the usual install locations
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new FileNotFoundException("interrupted while looking for docker");
		}
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		String exeName = ResourceUtil.getExecutableName("docker", false, osi);
		for (String dir : candidateDirectories(osi)) {
			File candidate = new File(dir, exeName);
			if (candidate.isFile() && candidate.canExecute()) {
				return new FenicsDocker(candidate);
			}
		}
		throw new FileNotFoundException("cannot find the docker executable; install Docker Desktop (https://www.docker.com/products/docker-desktop/)");
	}

	static List<String> candidateDirectories(OperatingSystemInfo osi) {
		List<String> dirs = new ArrayList<>();
		String home = System.getProperty("user.home");
		if (osi.isWindows()) {
			dirs.add("C:\\Program Files\\Docker\\Docker\\resources\\bin");
		} else {
			dirs.add("/usr/local/bin");
			dirs.add("/opt/homebrew/bin");
			dirs.add("/usr/bin");
			dirs.add(home + "/.docker/bin");
			if (osi.isMac()) {
				dirs.add("/Applications/Docker.app/Contents/Resources/bin");
			}
		}
		return dirs;
	}

	/**
	 * Environment for every docker invocation: the docker CLI's own directory on PATH, because Docker
	 * Desktop's CLI runs helpers that live beside it (e.g. {@code docker-credential-desktop}).
	 */
	public Map<String, String> environment() {
		Map<String, String> env = new HashMap<>();
		String path = System.getenv("PATH");
		String dockerDir = docker.getAbsoluteFile().getParent();
		env.put("PATH", (path == null || path.isEmpty()) ? dockerDir : dockerDir + File.pathSeparator + path);
		return env;
	}

	/** @return whether the Docker daemon answers (Docker Desktop may be installed but not started) */
	public boolean isDaemonRunning() {
		return query(List.of("info", "--format", "{{.ServerVersion}}")).isPresent();
	}

	/** @return the {@code os/arch} of the local copy of {@code image}, or empty if it has not been pulled */
	public Optional<String> localImagePlatform(String image) {
		return query(List.of("image", "inspect", "--format", "{{.Os}}/{{.Architecture}}", image));
	}

	/**
	 * Pulls {@code image}. If there is no build for this host's architecture it pulls
	 * {@value #FALLBACK_PLATFORM}, which Docker Desktop runs emulated.
	 *
	 * @param progress receives docker's progress lines
	 * @param cancelled polled while pulling; the pull is abandoned when it returns true
	 */
	public void pull(String image, Consumer<String> progress, BooleanSupplier cancelled) throws IOException, InterruptedException {
		StringBuilder output = new StringBuilder();
		int exit = runStreaming(List.of("pull", image), line -> { output.append(line).append('\n'); progress.accept(line); }, cancelled);
		if (exit != 0 && output.toString().contains("no matching manifest")) {
			lg.info("{} has no build for this platform; pulling {}", image, FALLBACK_PLATFORM);
			output.setLength(0);
			exit = runStreaming(List.of("pull", "--platform", FALLBACK_PLATFORM, image), line -> { output.append(line).append('\n'); progress.accept(line); }, cancelled);
		}
		if (exit != 0) {
			throw new IOException("docker pull " + image + " failed (exit " + exit + "):\n" + output.toString().trim());
		}
	}

	/** where {@code hostDir} appears inside the container */
	public static String containerDirectory(File hostDir, OperatingSystemInfo osi) {
		return osi.isWindows() ? WINDOWS_CONTAINER_DIR : hostDir.getAbsolutePath();
	}

	/**
	 * {@code docker run --rm --init --name <name> --platform <platform> -v <hostDir>:<containerDir>
	 * [--user uid:gid] <image>}: the prefix before {@code vcell-fenics ...}.
	 */
	public List<String> runPrefix(String image, String platform, String containerName, File hostDir, String containerDir, String user) {
		List<String> cmd = new ArrayList<>();
		cmd.add(docker.getAbsolutePath());
		cmd.add("run");
		cmd.add("--rm");
		cmd.add("--init");
		cmd.add("--name");
		cmd.add(containerName);
		if (platform != null) {
			cmd.add("--platform");
			cmd.add(platform);
		}
		cmd.add("-v");
		cmd.add(hostDir.getAbsolutePath() + ":" + containerDir);
		if (user != null) {
			cmd.add("--user");
			cmd.add(user);
		}
		cmd.add(image);
		return cmd;
	}

	/** {@code uid:gid} of the owner of {@code dir}, or null where there is none (Windows) */
	public static String ownerOf(File dir) {
		try {
			Object uid = Files.getAttribute(dir.toPath(), "unix:uid");
			Object gid = Files.getAttribute(dir.toPath(), "unix:gid");
			return uid + ":" + gid;
		} catch (UnsupportedOperationException | IllegalArgumentException | IOException e) {
			return null;
		}
	}

	/** removes a container that may still be running (a stopped quick run); failures are only logged */
	public void removeContainer(String containerName) {
		if (query(List.of("rm", "-f", containerName)).isEmpty()) {
			lg.debug("docker rm -f {}: nothing removed", containerName);
		}
	}

	private ProcessBuilder processBuilder(List<String> args) {
		List<String> cmd = new ArrayList<>();
		cmd.add(docker.getAbsolutePath());
		cmd.addAll(args);
		ProcessBuilder pb = new ProcessBuilder(cmd).redirectErrorStream(true);
		pb.environment().putAll(environment());
		return pb;
	}

	/** @return the trimmed output of a short docker command that exited 0, else empty */
	private Optional<String> query(List<String> args) {
		try {
			Process p = processBuilder(args).start();
			byte[] out = p.getInputStream().readAllBytes();
			if (!p.waitFor(QUERY_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				p.destroyForcibly();
				return Optional.empty();
			}
			return p.exitValue() == 0 ? Optional.of(new String(out, StandardCharsets.UTF_8).trim()) : Optional.empty();
		} catch (IOException e) {
			lg.debug("docker {} failed: {}", args, e.getMessage());
			return Optional.empty();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return Optional.empty();
		}
	}

	private int runStreaming(List<String> args, Consumer<String> lines, BooleanSupplier cancelled) throws IOException, InterruptedException {
		Process p = processBuilder(args).start();
		Thread reader = new Thread(() -> {
			try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = r.readLine()) != null) {
					lines.accept(line);
				}
			} catch (IOException e) {
				lg.debug("reading docker output: {}", e.getMessage());
			}
		}, "docker " + args.get(0));
		reader.setDaemon(true);
		reader.start();
		while (!p.waitFor(500, TimeUnit.MILLISECONDS)) {
			if (cancelled.getAsBoolean()) {
				p.destroy();
				p.waitFor(10, TimeUnit.SECONDS);
				throw new InterruptedException("docker " + String.join(" ", args) + " cancelled");
			}
		}
		reader.join(TimeUnit.SECONDS.toMillis(5));
		return p.exitValue();
	}
}
