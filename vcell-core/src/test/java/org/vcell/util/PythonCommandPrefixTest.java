package org.vcell.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.resource.PropertyLoader;

/**
 * Which interpreter runs our Python is a deployment decision, and it now travels through
 * {@code vcell.python.executable} rather than being fixed in Java as {@code poetry run python}.
 *
 * The property already existed and was already being passed to the sim-data service; nothing read
 * it. That was not merely untidy: in the deployed container {@code poetry run} fails, because the
 * image runs as uid 10001 while {@code poetry.toml} is {@code -rw------- root}, so Poetry cannot
 * read its own configuration. The interpreter the property names imports the same packages.
 *
 * Both branches matter, so both are pinned here: a deployment that sets the property gets it, and
 * a developer's machine that does not keeps the behaviour it has always had.
 */
@Tag("Fast")
public class PythonCommandPrefixTest {

	@AfterEach
	public void clearProperty() {
		System.clearProperty(PropertyLoader.pythonExe);
	}

	/** Unset is the developer's machine and the CLI run from a checkout. Unchanged behaviour. */
	@Test
	public void withoutTheProperty_theCommandIsStillPoetry() {
		System.clearProperty(PropertyLoader.pythonExe);
		assertEquals(List.of("poetry", "run", "python"), PythonUtils.pythonCommandPrefix());
	}

	/** Set is a deployment opting in -- the sim-data service already passes this. */
	@Test
	public void withTheProperty_theConfiguredInterpreterIsUsed() {
		System.setProperty(PropertyLoader.pythonExe, "/usr/local/bin/python");
		assertEquals(List.of("/usr/local/bin/python"), PythonUtils.pythonCommandPrefix());
	}

	/**
	 * An empty value is what an unset environment variable looks like once it has been through a
	 * shell -- {@code -Dvcell.python.executable=} is not null and would otherwise be run as the
	 * command, failing with something unhelpful.
	 */
	@Test
	public void anEmptyValueFallsBackRatherThanRunningNothing() {
		System.setProperty(PropertyLoader.pythonExe, "   ");
		assertEquals(List.of("poetry", "run", "python"), PythonUtils.pythonCommandPrefix());
	}

	/** The caller appends to the prefix, so it must be mutable and must not be shared. */
	@Test
	public void theReturnedListIsMutableAndFresh() {
		System.clearProperty(PropertyLoader.pythonExe);
		List<String> first = PythonUtils.pythonCommandPrefix();
		first.add("-m");
		assertEquals(List.of("poetry", "run", "python"), PythonUtils.pythonCommandPrefix(),
				"a caller appending its own arguments must not affect the next caller");
	}
}
