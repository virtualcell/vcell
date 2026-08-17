package org.vcell.sbml;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.sbml.jsbml.xml.parsers.ParserManager;

/**
 * Initializes JSBML's parser registry once, on a single thread, before any test runs.
 *
 * <p>JSBML's {@code ParserManager} is not safe to initialize concurrently, and the Fast group runs
 * test classes in parallel (see the {@code junit.jupiter.execution.parallel.*} flags in
 * {@code .github/workflows/ci.yml}). Its static initializer creates two single-use ServiceLoader
 * iterators, and the constructor -- reached through an unsynchronized {@code getManager()} that
 * does a plain null check -- drains them:
 *
 * <pre>
 * static {
 *     readingParserList = ServiceLoader.load(ReadingParser.class).iterator();
 *     writingParserList = ServiceLoader.load(WritingParser.class).iterator();
 * }
 * public static ParserManager getManager() {
 *     if (manager == null) manager = new ParserManager();   // init() drains the statics
 *     return manager;
 * }
 * </pre>
 *
 * <p>Two workers can both see a null {@code manager} and both drain the same iterator. Observed on
 * one commit in two consecutive CI runs, in two different tests, at the same frame: a
 * NullPointerException out of {@code ServiceLoader$LazyClassPathLookupIterator.parse} and a
 * NoSuchElementException out of {@code CompoundEnumeration.nextElement}.
 *
 * <p>It is a crash, not corruption. A second {@code init()} was measured to produce an identical
 * 12-namespace parser map, because JSBML's {@code parserDefaults} fallback re-instantiates by name
 * whatever the drained iterator failed to supply, and that list covers all 17 parsers JSBML ships.
 *
 * <p>Touching {@code getManager()} here collapses the race: the singleton and both iterators are
 * done with before the execution engine starts a second thread, and every later call is a plain
 * static read.
 *
 * <p>This covers vcell-core, which holds every class in the repo that imports {@code org.sbml.jsbml}.
 * A module whose own tests reach JSBML would need its own copy of this listener and its
 * {@code META-INF/services} entry; none do today.
 *
 * <p>This is a workaround in test scope, not a fix. The fix belongs in {@code getManager()} itself,
 * which is reachable: jsbml-core here is our fork, {@code com.github.virtualcell/vcell-jsbml}. Until
 * that lands, production keeps the race -- harmless in a single-threaded importer, not obviously so
 * in a server that can import two documents at once. See the tracking issue.
 *
 * <p>Registered by {@code ServiceLoader} via
 * {@code src/test/resources/META-INF/services/org.junit.platform.launcher.LauncherSessionListener}.
 */
public class JsbmlParserWarmup implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        ParserManager.getManager();
    }
}
