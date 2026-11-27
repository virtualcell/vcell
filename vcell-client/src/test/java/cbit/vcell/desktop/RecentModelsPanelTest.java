package cbit.vcell.desktop;

import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.client.laf.VCellLookAndFeel;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.clientdb.DocumentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.*;
import org.vcell.util.document.VCDocument.VCDocumentType;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The `RecentModelsPanel` lists the user's own most recently saved models, the latest version of each, newest first;
 * shows as many as fit in its height, with "Show more" adding five at a time; and opens the one picked.
 */
@Tag("Fast")
public class RecentModelsPanelTest {
	private static final User ME = new User("me", new KeyValue("1"));
	private static final User SOMEONE_ELSE = new User("someone", new KeyValue("2"));

	private final List<BioModelInfo> bioModels = new ArrayList<>();
	private final List<MathModelInfo> mathModels = new ArrayList<>();
	private final List<GeometryInfo> geometries = new ArrayList<>();
	private final List<DatabaseListener> listeners = new ArrayList<>();
	private User user;
	private int nextKey;

	@BeforeEach
	public void setUp() {
		this.user = ME;
		this.nextKey = 100;
	}

	@Test
	public void asksForALoginWithoutOne() throws Exception {
		RecentModelsPanel panel = this.get(RecentModelsPanel::new);
		assertEquals(0, panel.getShownCount());
		assertEquals("Log in to see the models you saved recently", panel.getMessageShown());
		this.user = null;
		this.onEdt(() -> panel.setDocumentManager(this.documentManager()));
		assertEquals("Log in to see the models you saved recently", panel.getMessageShown());
	}

	@Test
	public void saysSoWhenThereAreNone() throws Exception {
		RecentModelsPanel panel = this.panelFor();
		assertEquals("No saved models yet", panel.getMessageShown());
		assertFalse(panel.isShowMoreOffered());
		assertFalse(this.get(panel::hasModels)); // a new user: the welcome window starts at the database's models
	}

	@Test
	public void listsTheLatestVersionOfEachOwnModelNewestFirst() throws Exception {
		this.bioModels.add(this.bioModel("Signalling", ME, 1, 10));
		BioModelInfo signallingLatest = this.bioModel("Signalling", ME, 1, 30); // a later save of the same model
		this.bioModels.add(signallingLatest);
		MathModelInfo diffusion = this.mathModel("Diffusion", ME, 2, 20);
		this.mathModels.add(diffusion);
		this.bioModels.add(this.bioModel("Shared with me", SOMEONE_ELSE, 3, 40)); // not mine
		this.geometries.add(this.geometry("Cell shape", ME, 4, 50)); // geometries only when asked for

		RecentModelsPanel panel = this.panelFor();
		assertNull(panel.getMessageShown());
		assertEquals(List.of(signallingLatest, diffusion), this.shown(panel));
		assertTrue(this.get(panel::hasModels));
	}

	@Test
	public void listsGeometriesWhenAskedFor() throws Exception {
		this.bioModels.add(this.bioModel("Signalling", ME, 1, 10));
		GeometryInfo shape = this.geometry("Cell shape", ME, 2, 20);
		this.geometries.add(shape);
		RecentModelsPanel panel = this.panelFor(VCDocumentType.GEOMETRY_DOC);
		assertEquals(List.of(shape), this.shown(panel));
	}

	@Test
	public void showsAsManyAsFitThenFiveMoreAtATime() throws Exception {
		for (int i = 0; i < 20; i++) this.bioModels.add(this.bioModel("Model " + i, ME, i, i));
		RecentModelsPanel panel = this.panelFor();

		// tall enough for all of them: all shown, nothing more to offer
		this.onEdt(() -> panel.setSize(300, 3000));
		this.onEdt(panel::showModels);
		assertEquals(20, panel.getShownCount());
		assertFalse(panel.isShowMoreOffered());
		int entryHeight = panel.getList().getFixedCellHeight();
		assertTrue(entryHeight > 0);

		// room for about six: fewer than six shown (leaving room for the button), and "Show more" offered
		this.onEdt(() -> panel.setSize(300, 6 * entryHeight + 2));
		this.onEdt(panel::showModels);
		int fit = panel.getShownCount();
		assertTrue(fit >= 1 && fit < 6, "shown " + fit);
		assertTrue(panel.isShowMoreOffered());
		assertEquals(this.newestFirst(19, fit), this.names(panel));

		// each press adds five, until all are shown
		this.onEdt(panel::showMore);
		assertEquals(fit + 5, panel.getShownCount());
		List<String> steps = new ArrayList<>();
		while (panel.isShowMoreOffered()) {
			this.onEdt(panel::showMore);
			steps.add(panel.getShownCount() + (panel.isShowMoreOffered() ? "+" : ""));
		}
		assertEquals(20, panel.getShownCount(), "fit " + fit + ", then " + steps);

		// the ones added stay added as the panel is resized
		this.onEdt(() -> panel.setSize(300, 3 * entryHeight));
		this.onEdt(panel::showModels);
		assertEquals(20, panel.getShownCount());
	}

	@Test
	public void startsFromWhatFitsForANewLogin() throws Exception {
		for (int i = 0; i < 20; i++) this.bioModels.add(this.bioModel("Model " + i, ME, i, i));
		RecentModelsPanel panel = this.panelFor();
		this.onEdt(() -> panel.setSize(300, 3000));
		this.onEdt(panel::showModels);
		int entryHeight = panel.getList().getFixedCellHeight();
		this.onEdt(() -> panel.setSize(300, 6 * entryHeight + 2));
		this.onEdt(panel::showModels);
		int fit = panel.getShownCount();
		this.onEdt(panel::showMore);
		this.onEdt(() -> panel.setDocumentManager(this.documentManager()));
		assertEquals(fit, panel.getShownCount());
	}

	@Test
	public void opensTheModelPicked() throws Exception {
		this.bioModels.add(this.bioModel("Older", ME, 1, 10));
		BioModelInfo newer = this.bioModel("Newer", ME, 2, 20);
		this.bioModels.add(newer);
		RecentModelsPanel panel = this.panelFor();
		List<VCDocumentInfo> opened = new ArrayList<>();
		this.onEdt(() -> {
			panel.setOpener(opened::add);
			panel.getList().setSelectedIndex(0);
			panel.getList().getActionMap().get("openModel").actionPerformed(null); // Enter
		});
		assertEquals(List.of(newer), opened);
	}

	@Test
	public void searchingListsOnlyTheModelsWhoseNameMatches() throws Exception {
		BioModelInfo calcium = this.bioModel("Calcium Waves", ME, 1, 10);
		this.bioModels.add(calcium);
		this.bioModels.add(this.bioModel("Ran Transport", ME, 2, 20));
		RecentModelsPanel panel = this.panelFor();

		this.onEdt(() -> panel.setSearchText("CALCIUM")); // ignoring case
		assertEquals(List.of(calcium), this.shown(panel));
		this.onEdt(() -> panel.setSearchText("glycolysis"));
		assertEquals("No recent models match \u201Cglycolysis\u201D", panel.getMessageShown());
		assertTrue(this.get(panel::hasModels)); // the user has models, whatever the search
		this.onEdt(() -> panel.setSearchText(""));
		assertEquals(2, panel.getShownCount());
	}

	@Test
	public void followsChangesToTheDatabase() throws Exception {
		this.bioModels.add(this.bioModel("Older", ME, 1, 10));
		RecentModelsPanel panel = this.panelFor();
		assertEquals(1, this.listeners.size());

		BioModelInfo justSaved = this.bioModel("Just saved", ME, 2, 20);
		this.bioModels.add(justSaved);
		this.listeners.get(0).databaseInsert(null);
		this.onEdt(() -> {}); // the panel updates on the Swing thread
		assertEquals(justSaved, this.shown(panel).get(0));

		this.onEdt(() -> panel.setDocumentManager(null)); // logging out stops it listening
		assertTrue(this.listeners.isEmpty());
	}

	@Test
	public void breaksLongAnnotationsIntoLinesForTheirToolTip() {
		String annotation = "word ".repeat(40) + "\nA <b> & c";
		String toolTip = ModelListRenderer.wrapForToolTip(annotation);
		assertTrue(toolTip.startsWith("<html>") && toolTip.endsWith("</html>"));
		String[] lines = toolTip.substring("<html>".length(), toolTip.length() - "</html>".length()).split("<br>");
		assertEquals(4, lines.length); // 200 characters in lines of at most 70, then the annotation's own second line
		for (String line : lines) assertTrue(line.length() <= 70, line);
		assertEquals("A &lt;b&gt; &amp; c", lines[3]); // shown as typed, not as markup
	}

	private RecentModelsPanel panelFor(VCDocumentType... types) throws Exception {
		RecentModelsPanel panel = this.get(() -> new RecentModelsPanel(types));
		this.onEdt(() -> panel.setDocumentManager(this.documentManager()));
		return panel;
	}

	private List<VCDocumentInfo> shown(RecentModelsPanel panel) throws Exception {
		return this.get(() -> IntStream.range(0, panel.getShownCount()).mapToObj(i -> panel.getList().getModel().getElementAt(i)).collect(Collectors.toList()));
	}

	private List<String> names(RecentModelsPanel panel) throws Exception {
		return this.shown(panel).stream().map(info -> info.getVersion().getName()).toList();
	}

	private List<String> newestFirst(int newest, int count) {
		return IntStream.range(0, count).mapToObj(i -> "Model " + (newest - i)).toList();
	}

	private BioModelInfo bioModel(String name, User owner, int branch, int day) {
		return new BioModelInfo(this.version(name, owner, branch, day), (String) null, null);
	}

	private MathModelInfo mathModel(String name, User owner, int branch, int day) {
		return new MathModelInfo(this.version(name, owner, branch, day), new KeyValue(String.valueOf(this.nextKey++)), (String) null, null);
	}

	private GeometryInfo geometry(String name, User owner, int branch, int day) {
		return new GeometryInfo(this.version(name, owner, branch, day), 2, null, null, null, null);
	}

	// A save of a model: a new version key, on the model's branch, made on the given day
	private Version version(String name, User owner, int branch, int day) {
		return this.version(name, owner, branch, new Date(1_700_000_000_000L + day * 86_400_000L), null);
	}

	private Version version(String name, User owner, int branch, Date saved, String annotation) {
		return new Version(new KeyValue(String.valueOf(this.nextKey++)), name, owner, new GroupAccessNone(), null,
				BigDecimal.valueOf(branch), saved, VersionFlag.Current, annotation);
	}

	public static void main(String[] args) throws Exception {
		VCellLookAndFeel.setPlatformProperties();
		VCellFonts.setSize(VCellFonts.DEFAULT_SIZE);
		VCellLookAndFeel.initializeVCellLookAndFeel();
		RecentModelsPanel showModels = new RecentModelsPanel();
		JFrame frame = new JFrame("Recent models panel");
		frame.add(showModels);
		frame.setVisible(true);
	}

	// Just what the panel uses of a document manager
	private DocumentManager documentManager() {
		return (DocumentManager) Proxy.newProxyInstance(DocumentManager.class.getClassLoader(), new Class<?>[] {DocumentManager.class}, (proxy, method, args) -> switch (method.getName()) {
			case "getUser" -> this.user;
			case "getBioModelInfos" -> this.bioModels.toArray(new BioModelInfo[0]);
			case "getMathModelInfos" -> this.mathModels.toArray(new MathModelInfo[0]);
			case "getGeometryInfos" -> this.geometries.toArray(new GeometryInfo[0]);
			case "addDatabaseListener" -> { this.listeners.add((DatabaseListener) args[0]); yield null; }
			case "removeDatabaseListener" -> { this.listeners.remove((DatabaseListener) args[0]); yield null; }
			case "hashCode" -> System.identityHashCode(proxy);
			case "equals" -> proxy == args[0];
			default -> throw new UnsupportedOperationException(method.getName());
		});
	}

	private <T> T get(java.util.concurrent.Callable<T> work) throws Exception {
		Object[] result = new Object[1];
		Exception[] failure = new Exception[1];
		SwingUtilities.invokeAndWait(() -> {
			try {
				result[0] = work.call();
			} catch (Exception e) {
				failure[0] = e;
			}
		});
		if (null != failure[0]) throw failure[0];
		@SuppressWarnings("unchecked") T value = (T) result[0];
		return value;
	}

	private void onEdt(Runnable work) throws Exception {
		SwingUtilities.invokeAndWait(work);
	}
}
