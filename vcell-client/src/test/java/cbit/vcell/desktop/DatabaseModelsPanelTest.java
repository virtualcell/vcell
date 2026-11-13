package cbit.vcell.desktop;

import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.DatabaseModelsPanel.ModelList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.*;
import org.vcell.util.document.VCDocument.VCDocumentType;

import javax.swing.*;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The `DatabaseModelsPanel` sorts the database's BioModels and MathModels into lists as the database tree's folders
 * do, listing the latest version of each model in a list, by name.
 */
@Tag("Fast")
public class DatabaseModelsPanelTest {
	private static final User ME = new User("me", new KeyValue("1"));
	private static final User SOMEONE_ELSE = new User("someone", new KeyValue("2"));
	private static final User TUTORIAL = new User(VCDocumentDbTreeModel.USER_tutorial, new KeyValue("3"));
	private static final User MODEL_BRICKS = new User(VCDocumentDbTreeModel.USER_modelBricks, new KeyValue("4"));

	private final List<BioModelInfo> bioModels = new ArrayList<>();
	private final List<MathModelInfo> mathModels = new ArrayList<>();
	private int nextKey;

	@BeforeEach
	public void setUp() {
		this.nextKey = 100;
	}

	@Test
	public void sortsVersionsAsTheDatabaseTreeDoes() {
		BioModelInfo mine = this.bioModel("Mine", ME, new GroupAccessNone(), VersionFlag.Current, false);
		BioModelInfo minePublic = this.bioModel("Mine, public", ME, new GroupAccessAll(), VersionFlag.Current, false);
		BioModelInfo shared = this.bioModel("Shared", SOMEONE_ELSE, this.sharedWithMe(), VersionFlag.Current, false);
		BioModelInfo theirsPrivate = this.bioModel("Private", SOMEONE_ELSE, new GroupAccessNone(), VersionFlag.Current, false);
		BioModelInfo uncurated = this.bioModel("Public", SOMEONE_ELSE, new GroupAccessAll(), VersionFlag.Current, false);
		BioModelInfo published = this.bioModel("Published", SOMEONE_ELSE, new GroupAccessAll(), VersionFlag.Published, true);
		BioModelInfo curated = this.bioModel("Curated", SOMEONE_ELSE, new GroupAccessAll(), VersionFlag.Archived, true);
		BioModelInfo tutorial = this.bioModel("Tutorial", TUTORIAL, new GroupAccessAll(), VersionFlag.Current, false);
		BioModelInfo brick = this.bioModel("Brick", MODEL_BRICKS, new GroupAccessAll(), VersionFlag.Current, false);

		assertEquals(EnumSet.of(ModelList.MINE), DatabaseModelsPanel.listsFor(mine, ME));
		assertEquals(EnumSet.of(ModelList.MINE, ModelList.UNCURATED), DatabaseModelsPanel.listsFor(minePublic, ME)); // the user's public models are the community's too
		assertEquals(EnumSet.of(ModelList.MINE), DatabaseModelsPanel.listsFor(shared, ME)); // the user's list has what is shared with them
		assertEquals(EnumSet.noneOf(ModelList.class), DatabaseModelsPanel.listsFor(theirsPrivate, ME));
		assertEquals(EnumSet.of(ModelList.UNCURATED), DatabaseModelsPanel.listsFor(uncurated, ME));
		assertEquals(EnumSet.of(ModelList.PUBLISHED), DatabaseModelsPanel.listsFor(published, ME));
		assertEquals(EnumSet.of(ModelList.CURATED), DatabaseModelsPanel.listsFor(curated, ME));
		assertEquals(EnumSet.of(ModelList.TUTORIAL), DatabaseModelsPanel.listsFor(tutorial, ME)); // a tutorial's, not uncurated
		assertEquals(EnumSet.of(ModelList.CURATED), DatabaseModelsPanel.listsFor(brick, ME)); // a public Model Brick is curated
	}

	@Test
	public void listsTheLatestVersionOfEachModelByNameBioModelsAndMathModelsTogether() {
		BioModelInfo zebraOld = this.bioModel("zebra", SOMEONE_ELSE, new GroupAccessAll(), VersionFlag.Current, false, 7, 1);
		BioModelInfo zebraNew = this.bioModel("zebra", SOMEONE_ELSE, new GroupAccessAll(), VersionFlag.Current, false, 7, 5); // a later save of it
		MathModelInfo apple = this.mathModel("Apple", SOMEONE_ELSE, new GroupAccessAll());
		BioModelInfo mango = this.bioModel("mango", SOMEONE_ELSE, new GroupAccessAll(), VersionFlag.Current, false);

		Map<ModelList, List<VCDocumentInfo>> lists = DatabaseModelsPanel.sortIntoLists(List.of(zebraOld, zebraNew, apple, mango), ME);
		assertEquals(List.of(apple, mango, zebraNew), lists.get(ModelList.UNCURATED)); // by name, whatever the case
		assertNull(lists.get(ModelList.MINE));
	}

	@Test
	public void showsEachListFromTheDocumentManager() throws Exception {
		BioModelInfo mine = this.bioModel("Mine", ME, new GroupAccessNone(), VersionFlag.Current, false);
		MathModelInfo shared = this.mathModel("Shared", SOMEONE_ELSE, this.sharedWithMe());
		this.bioModels.add(mine);
		this.mathModels.add(shared);

		DatabaseModelsPanel panel = this.get(DatabaseModelsPanel::new);
		this.onEdt(() -> panel.setDocumentManager(this.documentManager(ME)));
		assertEquals(List.of(mine, shared), this.get(() -> panel.getShown(ModelList.MINE))); // the user's own, and shared with them
		assertEquals(List.of(), this.get(() -> panel.getShown(ModelList.PUBLISHED)));
		assertEquals(List.of("Personal (2)", "Published (0)", "Curated (0)", "Uncurated (0)", "Tutorials (0)"),
				this.get(panel::getTabTitles));

		this.onEdt(() -> panel.setDocumentManager(null)); // logged out: nothing listed, and no counts
		assertEquals(List.of(), this.get(() -> panel.getShown(ModelList.MINE)));
		assertEquals(List.of("Personal", "Published", "Curated", "Uncurated", "Tutorials"), this.get(panel::getTabTitles));
	}

	@Test
	public void searchingListsOnlyMatchingModelsByNameOrOwner() throws Exception {
		BioModelInfo calcium = this.bioModel("Calcium Waves", SOMEONE_ELSE, new GroupAccessAll(), VersionFlag.Current, false);
		BioModelInfo mine = this.bioModel("Ran Transport", ME, new GroupAccessNone(), VersionFlag.Current, false);
		this.bioModels.add(calcium);
		this.bioModels.add(mine);
		this.bioModels.add(this.bioModel("Glycolysis", SOMEONE_ELSE, new GroupAccessAll(), VersionFlag.Current, false));

		DatabaseModelsPanel panel = this.get(DatabaseModelsPanel::new);
		this.onEdt(() -> panel.setDocumentManager(this.documentManager(ME)));
		this.onEdt(() -> panel.setSearchText("calcium"));
		assertEquals(List.of(calcium), this.get(() -> panel.getShown(ModelList.UNCURATED)));
		assertEquals(List.of(), this.get(() -> panel.getShown(ModelList.MINE)));
		assertEquals(List.of("Personal (0)", "Published (0)", "Curated (0)", "Uncurated (1)", "Tutorials (0)"),
				this.get(panel::getTabTitles)); // the counts are of the matches

		this.onEdt(() -> panel.setSearchText("ME")); // an owner's name, ignoring case
		assertEquals(List.of(mine), this.get(() -> panel.getShown(ModelList.MINE)));
		this.onEdt(() -> panel.setSearchText(""));
		assertEquals(2, this.get(() -> panel.getShown(ModelList.UNCURATED)).size());
	}

	@Test
	public void leavesOutPersonalForAGuest() throws Exception {
		User guest = new User(User.VCELL_GUEST_NAME, new KeyValue("5"));
		this.bioModels.add(this.bioModel("Tutorial", TUTORIAL, new GroupAccessAll(), VersionFlag.Current, false));

		DatabaseModelsPanel panel = this.get(DatabaseModelsPanel::new);
		this.onEdt(() -> panel.setDocumentManager(this.documentManager(guest)));
		assertEquals(List.of("Published (0)", "Curated (0)", "Uncurated (0)", "Tutorials (1)"), this.get(panel::getTabTitles));

		this.onEdt(() -> panel.setDocumentManager(this.documentManager(ME))); // someone else logs in: it's back, first
		assertEquals(List.of("Personal (0)", "Published (0)", "Curated (0)", "Uncurated (0)", "Tutorials (1)"),
				this.get(panel::getTabTitles));
	}

	@Test
	public void tutorialsCanBeShownElsewhereStillCounted() throws Exception {
		this.bioModels.add(this.bioModel("Tutorial", TUTORIAL, new GroupAccessAll(), VersionFlag.Current, false));
		this.bioModels.add(this.bioModel("Other", SOMEONE_ELSE, new GroupAccessAll(), VersionFlag.Current, false));
		List<String> tutorialsTitles = new java.util.ArrayList<>();

		DatabaseModelsPanel panel = this.get(DatabaseModelsPanel::new);
		javax.swing.JComponent tutorials = this.get(() -> panel.detachTutorials(tutorialsTitles::add));
		this.onEdt(() -> panel.setDocumentManager(this.documentManager(ME)));

		assertEquals(List.of("Personal (0)", "Published (0)", "Curated (0)", "Uncurated (1)"), this.get(panel::getTabTitles)); // no Tutorials tab
		assertEquals("Tutorials (1)", tutorialsTitles.get(tutorialsTitles.size() - 1));
		this.onEdt(() -> panel.setSearchText("other")); // still searched
		assertEquals("Tutorials (0)", tutorialsTitles.get(tutorialsTitles.size() - 1));
		assertEquals(List.of(), this.get(() -> panel.getShown(ModelList.TUTORIAL)));
		assertNull(tutorials.getParent()); // the caller's to place
	}

	private GroupAccessSome sharedWithMe() {
		boolean[] hidden = {false};
		return new GroupAccessSome(BigDecimal.valueOf(42), GroupAccess.calculateHash(new KeyValue[] {ME.getID()}, hidden), new User[] {ME}, hidden);
	}

	private BioModelInfo bioModel(String name, User owner, GroupAccess access, VersionFlag flag, boolean withPublication) {
		return this.bioModel(name, owner, access, flag, withPublication, this.nextKey, 1);
	}

	private BioModelInfo bioModel(String name, User owner, GroupAccess access, VersionFlag flag, boolean withPublication, int branch, int day) {
		Version version = this.version(name, owner, access, flag, branch, day);
		BioModelInfo info = new BioModelInfo(version, (String) null, null);
		if (withPublication) {
			info.addPublicationInfo(new PublicationInfo(new KeyValue(String.valueOf(this.nextKey++)), version.getVersionKey(), "A paper",
					new String[] {"An author"}, "A citation", null, null, null, VCDocumentType.BIOMODEL_DOC, owner, version.getDate()));
		}
		return info;
	}

	private MathModelInfo mathModel(String name, User owner, GroupAccess access) {
		return new MathModelInfo(this.version(name, owner, access, VersionFlag.Current, this.nextKey, 1), new KeyValue(String.valueOf(this.nextKey++)), (String) null, null);
	}

	// A save of a model: a new version key, on the model's branch, made on the given day
	private Version version(String name, User owner, GroupAccess access, VersionFlag flag, int branch, int day) {
		return new Version(new KeyValue(String.valueOf(this.nextKey++)), name, owner, access, null,
				BigDecimal.valueOf(branch), new Date(1_700_000_000_000L + day * 86_400_000L), flag, null);
	}

	// Just what the panel uses of a document manager
	private DocumentManager documentManager(User user) {
		return (DocumentManager) Proxy.newProxyInstance(DocumentManager.class.getClassLoader(), new Class<?>[] {DocumentManager.class}, (proxy, method, args) -> switch (method.getName()) {
			case "getUser" -> user;
			case "getBioModelInfos" -> this.bioModels.toArray(new BioModelInfo[0]);
			case "getMathModelInfos" -> this.mathModels.toArray(new MathModelInfo[0]);
			case "addDatabaseListener", "removeDatabaseListener" -> null;
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
