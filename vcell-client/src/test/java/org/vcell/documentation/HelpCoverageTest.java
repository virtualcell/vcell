package org.vcell.documentation;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Every window VCell opens links to a help page, so Help &gt; VCell Help, F1 and the window's "?"
 * open the page about it rather than the help's contents.
 *
 * <p>It reads the client's source for the two ways VCell makes a window:
 * <ul>
 * <li><b>a window class</b> (extends JFrame, JDialog or one of VCell's LW windows): its file says
 * its page with {@code HelpContext.setPage}, usually on its root pane;</li>
 * <li><b>a child window</b> ({@code addChildWindow(...)}): the line opening it, or one of the
 * three lines above, says the page of what it shows with {@code HelpContext.setPage}, or says
 * where that is done with a {@code // help:} comment (the data viewers say their own pages).</li>
 * </ul>
 * A window that no page should explain (VCell's window base classes, progress popups, developer
 * tools) is listed in {@link #NOT_FOR_READERS} with the reason. Where no page exists yet, write
 * one: VCell's AI-drafted pages, awaiting curation (issue #2226), show the form.
 *
 * <p>Dialogs made on the fly (DialogUtils.showComponentOKCancelDialog and friends) are not checked:
 * most are small prompts. A large one says its page on its panel, and the dialog's "?" follows.
 */
@Tag("Fast")
public class HelpCoverageTest {
	private static final Path SOURCE = Path.of("src/main/java");

	private static final Pattern WINDOW_CLASS = Pattern.compile(
			"\\bclass\\s+(\\w+)\\s+extends\\s+(JFrame|JDialog|JWindow|LWTopFrame|LWChildFrame|LWTitledChildFrame|LWDialog|LWTitledDialog"
					+ "|LWChildDialog|LWOptionPaneDialog|LWTitledOptionPaneDialog|LWOptionPaneFrame|LWOkCancelDialog)\\b");

	private static final String CHILD_WINDOW = "addChildWindow(";

	private static final int LINES_ABOVE = 3;

	/** Windows no help page explains, by "path#Class" or "path#child" (all its child windows), and why. */
	private static final Map<String, String> NOT_FOR_READERS = Map.ofEntries(
			Map.entry("org/vcell/client/logicalwindow/", "VCell's window base classes; the windows made from them are checked"),
			Map.entry("cbit/vcell/client/ChildWindowManager.java#", "the child window classes themselves; what they show is checked where it is opened"),
			Map.entry("org/vcell/documentation/HelpViewer.java#HelpWindow", "the help itself"),
			Map.entry("org/vcell/util/gui/ProgressPopup.java#ProgressPopup", "a progress popup"),
			Map.entry("org/vcell/util/gui/ProgressDialog.java#ProgressDialog", "a progress dialog"),
			Map.entry("cbit/vcell/microscopy/gui/SplashWindow.java#SplashWindow", "a splash screen"),
			Map.entry("cbit/xml/viewer/XmlTreeViewerApplicationSplashScreen.java#XmlTreeViewerApplicationSplashScreen", "a standalone developer tool's splash screen"),
			Map.entry("cbit/vcell/microscopy/gui/DropTest2.java#DropTest2", "a developer's drag-and-drop test"),
			Map.entry("cbit/vcell/client/desktop/TestingFrameworkWindow.java#TestingFrameworkWindow", "the developers' testing framework"),
			Map.entry("cbit/vcell/client/TestingFrameworkWindowManager.java#child", "the developers' testing framework"),
			Map.entry("cbit/vcell/client/desktop/BNGWindow.java#BNGWindow", "no longer opened (its creation is commented out)"),
			Map.entry("cbit/xml/viewer/XmlTreeViewerApplication.java#XmlTreeViewerApplication", "a standalone developer tool"),
			Map.entry("cbit/xml/viewer/XmlTreeViewerApplicationAboutBox.java#XmlTreeViewerApplicationAboutBox", "a standalone developer tool")
	);

	@Test
	public void everyWindowLinksToAHelpPage() throws IOException {
		List<String> missing = new ArrayList<>();
		TreeSet<String> exemptionsUsed = new TreeSet<>();
		try (Stream<Path> files = Files.walk(HelpCoverageTest.SOURCE)) {
			for (Path file : (Iterable<Path>) files.filter(p -> p.toString().endsWith(".java")).sorted()::iterator) {
				String path = HelpCoverageTest.SOURCE.relativize(file).toString().replace('\\', '/');
				List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
				String text = String.join("\n", lines);
				boolean fileSaysPage = text.contains("HelpContext.") || text.contains("// help:");
				Matcher window = HelpCoverageTest.WINDOW_CLASS.matcher(text);
				while (window.find()) {
					String key = path + "#" + window.group(1);
					String exemption = HelpCoverageTest.exemption(key);
					if (exemption != null) {
						exemptionsUsed.add(exemption);
						continue;
					}
					if (!fileSaysPage) missing.add(key + "  (window class: say its page with HelpContext.setPage(this.getRootPane(), HelpIds.X))");
				}
				for (int index = 0; index < lines.size(); index++) {
					String line = lines.get(index).strip();
					if (!line.contains(HelpCoverageTest.CHILD_WINDOW) || line.startsWith("//") || line.startsWith("*")) continue;
					String key = path + "#child";
					String exemption = HelpCoverageTest.exemption(key);
					if (exemption != null) {
						exemptionsUsed.add(exemption);
						continue;
					}
					if (HelpCoverageTest.saysPageNear(lines, index)) continue;
					missing.add(path + ":" + (index + 1) + "  (child window: HelpContext.setPage(<what it shows>, HelpIds.X) just above, or a // help: comment saying where)");
				}
			}
		}
		Assertions.assertEquals("", String.join("\n", missing), "windows with no help page linked");
		// an exemption that matches nothing is stale: the window went away, or was renamed
		TreeSet<String> stale = new TreeSet<>(HelpCoverageTest.NOT_FOR_READERS.keySet());
		stale.removeAll(exemptionsUsed);
		Assertions.assertEquals(new TreeSet<String>(), stale, "exemptions in NOT_FOR_READERS that match no window");
	}

	private static String exemption(String key) {
		for (String exempt : HelpCoverageTest.NOT_FOR_READERS.keySet()) {
			if (exempt.endsWith("/") || exempt.endsWith("#") ? key.startsWith(exempt) : key.equals(exempt)) return exempt;
		}
		return null;
	}

	private static boolean saysPageNear(List<String> lines, int callIndex) {
		for (int index = Math.max(0, callIndex - HelpCoverageTest.LINES_ABOVE); index <= callIndex; index++) {
			String line = lines.get(index);
			if (line.contains("HelpContext.setPage(") || line.contains("// help:")) return true;
		}
		return false;
	}
}
