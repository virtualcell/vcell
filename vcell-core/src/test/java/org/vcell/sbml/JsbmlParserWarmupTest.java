package org.vcell.sbml;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sbml.jsbml.xml.parsers.ParserManager;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Guards the wiring of {@link JsbmlParserWarmup}, not its logic.
 *
 * <p>The listener is registered by a one-line {@code META-INF/services} file. Delete or misspell it
 * and nothing fails -- the parallel Fast run just goes back to racing into ParserManager, which
 * surfaces later as an unrelated-looking SBML test failing perhaps one run in a few hundred. This
 * test turns that silence into an immediate red.
 *
 * <p>Reads the private singleton rather than calling {@code getManager()}, because calling it would
 * initialize it and the assertion would pass either way.
 */
@Tag("Fast")
public class JsbmlParserWarmupTest {

    @Test
    public void jsbmlParsersAreInitializedBeforeAnyTestRuns() throws Exception {
        Field manager = ParserManager.class.getDeclaredField("manager");
        manager.setAccessible(true);
        assertNotNull(manager.get(null),
                "JSBML's ParserManager singleton is not initialized, so JsbmlParserWarmup did not run. "
                        + "Check vcell-core/src/test/resources/META-INF/services/"
                        + "org.junit.platform.launcher.LauncherSessionListener.");
    }
}
