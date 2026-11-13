package cbit.vcell.desktop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.desktop.ModelExplorerTree.Folder;
import cbit.vcell.desktop.ModelExplorerTree.Model;
import cbit.vcell.desktop.ModelExplorerTree.Owner;
import cbit.vcell.desktop.ModelExplorerTree.Publication;
import cbit.vcell.desktop.ModelExplorerTree.Section;

/**
 * The Model Explorer's tree puts each version in the folders VCell's database tree has always had, each arranged its own
 * way: My Models by model, Shared With Me and Uncurated by owner, Published and Curated by publication (Curated with its
 * Model Bricks), Tutorials by model; a model's versions under it only when there are several.
 */
@Tag("Fast")
public class ModelExplorerTreeTest {
	private static final User ME = new User("me", new KeyValue("1"));
	private static final User ANN = new User("ann", new KeyValue("2"));
	private static final User BOB = new User("Bob", new KeyValue("3"));
	private static final User TUTORIAL = new User(VCDocumentDbTreeModel.USER_tutorial, new KeyValue("4"));
	private static final User MODEL_BRICKS = new User(VCDocumentDbTreeModel.USER_modelBricks, new KeyValue("5"));
	private static final Date NOW = new Date(1_800_000_000_000L);

	private int nextKey;

	@BeforeEach
	public void setUp() {
		this.nextKey = 100;
	}

	@Test
	public void arrangesEachFolderItsOwnWay() {
		BioModelInfo mineOld = this.bioModel("Mine", ME, new GroupAccessNone(), VersionFlag.Current, null, 7, 1);
		BioModelInfo mineNew = this.bioModel("Mine", ME, new GroupAccessNone(), VersionFlag.Current, null, 7, 3); // a later save
		MathModelInfo shared = this.mathModel("Shared", ANN, this.sharedWithMe());
		PublicationInfo paper = this.publication("A paper", "Smith J", 2019);
		BioModelInfo published = this.bioModel("Published", BOB, new GroupAccessAll(), VersionFlag.Published, paper, 8, 1);
		BioModelInfo published2 = this.bioModel("Published too", ANN, new GroupAccessAll(), VersionFlag.Published, paper, 9, 1);
		BioModelInfo curated = this.bioModel("Curated", BOB, new GroupAccessAll(), VersionFlag.Archived, this.publication("Older", "Doe A", 2010), 10, 1);
		BioModelInfo brick = this.bioModel("Brick", MODEL_BRICKS, new GroupAccessAll(), VersionFlag.Current, null, 11, 1);
		BioModelInfo uncurated = this.bioModel("Public", ANN, new GroupAccessAll(), VersionFlag.Current, null, 12, 1);
		BioModelInfo tutorial = this.bioModel("Tutorial", TUTORIAL, new GroupAccessAll(), VersionFlag.Current, null, 13, 1);
		BioModelInfo hidden = this.bioModel("Private", BOB, new GroupAccessNone(), VersionFlag.Current, null, 14, 1);

		DefaultMutableTreeNode root = this.build(List.of(mineOld, mineNew, shared, published, published2, curated, brick, uncurated, tutorial, hidden), "");

		assertEquals(List.of(Section.MY_MODELS, Section.SHARED, Section.PUBLIC, Section.TUTORIALS, Section.GEOMETRIES, Section.BIOMODELS_DATABASE),
				ModelExplorerTreeTest.sections(root));
		// My Models: the model, its two versions under it, newest first
		DefaultMutableTreeNode mine = this.folder(root, Section.MY_MODELS);
		DefaultMutableTreeNode mineModel = (DefaultMutableTreeNode) mine.getChildAt(0);
		assertSame(mineNew, ((Model) mineModel.getUserObject()).latest());
		assertEquals(List.of(mineNew, mineOld), ModelExplorerTreeTest.userObjects(mineModel));
		// Shared With Me: by owner
		DefaultMutableTreeNode sharedFolder = this.folder(root, Section.SHARED);
		assertEquals(new Owner("ann", 1), ((DefaultMutableTreeNode) sharedFolder.getChildAt(0)).getUserObject());
		assertTrue(((DefaultMutableTreeNode) sharedFolder.getChildAt(0).getChildAt(0)).isLeaf()); // one version: no versions under it
		// Public: Published by publication, with both its models; Curated by publication, then its Model Bricks; Uncurated by owner
		DefaultMutableTreeNode publicFolder = this.folder(root, Section.PUBLIC);
		assertEquals(List.of(Section.PUBLISHED, Section.CURATED, Section.UNCURATED), ModelExplorerTreeTest.sections(publicFolder));
		DefaultMutableTreeNode publication = (DefaultMutableTreeNode) this.folder(publicFolder, Section.PUBLISHED).getChildAt(0);
		assertEquals(2, ((Publication) publication.getUserObject()).count());
		assertEquals(List.of("Published", "Published too"), ModelExplorerTreeTest.names(publication));
		DefaultMutableTreeNode curatedFolder = this.folder(publicFolder, Section.CURATED);
		assertTrue(curatedFolder.getChildAt(0) instanceof DefaultMutableTreeNode node && node.getUserObject() instanceof Publication);
		assertEquals(List.of("Brick"), ModelExplorerTreeTest.names(this.folder(curatedFolder, Section.MODEL_BRICKS)));
		assertEquals(2, ((Folder) curatedFolder.getUserObject()).getCount()); // the curated model and the brick
		DefaultMutableTreeNode uncuratedOwner = (DefaultMutableTreeNode) this.folder(publicFolder, Section.UNCURATED).getChildAt(0);
		assertEquals(new Owner("ann", 1), uncuratedOwner.getUserObject());
		assertEquals(5, ((Folder) publicFolder.getUserObject()).getCount());
		// Tutorials: the tutorial users' models, not uncurated
		assertEquals(List.of("Tutorial"), ModelExplorerTreeTest.names(this.folder(root, Section.TUTORIALS)));
		// someone else's private model is nowhere
		assertNull(ModelExplorerTree.find(root, hidden));
	}

	@Test
	public void theTableListsEachModelOnceWithItsFolders() {
		BioModelInfo mineOld = this.bioModel("Mine", ME, new GroupAccessAll(), VersionFlag.Current, null, 7, 1);
		BioModelInfo mineNew = this.bioModel("Mine", ME, new GroupAccessAll(), VersionFlag.Current, null, 7, 3); // public: also uncurated
		BioModelInfo published = this.bioModel("Published", BOB, new GroupAccessAll(), VersionFlag.Published, this.publication("A paper", "Smith J", 2019), 8, 1);

		List<ModelExplorerTree.TableRow> rows = ModelExplorerTree.tableRows(this.build(List.of(mineOld, mineNew, published), ""));

		assertEquals(2, rows.size()); // each model once, not once a folder or a version
		ModelExplorerTree.TableRow mine = rows.get(0);
		assertSame(mineNew, mine.latest());
		assertEquals(List.of(Section.MY_MODELS, Section.UNCURATED), mine.folders());
		assertEquals(List.of(Section.PUBLISHED), rows.get(1).folders()); // its nearest folder: Published, not Public Models

		// each column's value, of the kind it sorts as
		assertEquals("Mine", ModelExplorerTableModel.valueOf(mine, ModelExplorerTableModel.Column.NAME));
		assertEquals("BioModel", ModelExplorerTableModel.valueOf(mine, ModelExplorerTableModel.Column.TYPE));
		assertEquals("My Models, Uncurated", ModelExplorerTableModel.valueOf(mine, ModelExplorerTableModel.Column.FOLDERS));
		assertEquals("me", ModelExplorerTableModel.valueOf(mine, ModelExplorerTableModel.Column.OWNER));
		assertEquals(mineNew.getVersion().getDate(), ModelExplorerTableModel.valueOf(mine, ModelExplorerTableModel.Column.SAVED));
		assertEquals(2, ModelExplorerTableModel.valueOf(mine, ModelExplorerTableModel.Column.VERSIONS));
		assertEquals(Long.valueOf(mineNew.getVersion().getVersionKey().toString()), ModelExplorerTableModel.valueOf(mine, ModelExplorerTableModel.Column.VCELL_ID));
		assertEquals("", ModelExplorerTableModel.valueOf(mine, ModelExplorerTableModel.Column.PUBLICATION));
		assertEquals("Smith (2019) A paper", ModelExplorerTableModel.valueOf(rows.get(1), ModelExplorerTableModel.Column.PUBLICATION));
		for (ModelExplorerTableModel.Column column : ModelExplorerTableModel.Column.values()) {
			Object value = ModelExplorerTableModel.valueOf(mine, column);
			assertTrue(null == value || column.type.isInstance(value), column + " is a " + column.type.getSimpleName());
		}
	}

	@Test
	public void publicationsAreNewestFirst() {
		BioModelInfo older = this.bioModel("Older", BOB, new GroupAccessAll(), VersionFlag.Published, this.publication("Old", "Zed Q", 2001), 8, 1);
		BioModelInfo newer = this.bioModel("Newer", BOB, new GroupAccessAll(), VersionFlag.Published, this.publication("New", "Abe B", 2020), 9, 1);
		DefaultMutableTreeNode published = this.folder(this.folder(this.build(List.of(older, newer), ""), Section.PUBLIC), Section.PUBLISHED);
		assertEquals("Abe (2020) New (1)", published.getChildAt(0).toString());
		assertEquals("Zed (2001) Old (1)", published.getChildAt(1).toString());
	}

	@Test
	public void searchingListsOnlyTheMatchesAndCountsThem() {
		BioModelInfo calcium = this.bioModel("Calcium waves", ANN, new GroupAccessAll(), VersionFlag.Current, null, 8, 1);
		BioModelInfo other = this.bioModel("Something else", ANN, new GroupAccessAll(), VersionFlag.Current, null, 9, 1);
		DefaultMutableTreeNode root = this.build(List.of(calcium, other), "calcium");
		DefaultMutableTreeNode uncurated = this.folder(this.folder(root, Section.PUBLIC), Section.UNCURATED);
		assertEquals(1, ((Folder) uncurated.getUserObject()).getCount());
		assertEquals(List.of("Calcium waves"), ModelExplorerTreeTest.names((DefaultMutableTreeNode) uncurated.getChildAt(0)));
		// the BioModels Database is searched by name or ID
		DefaultMutableTreeNode database = this.folder(root, Section.BIOMODELS_DATABASE);
		assertEquals(1, database.getChildCount());
		assertEquals("BIOMD1", ((BioModelsNetModelInfo) ((DefaultMutableTreeNode) database.getChildAt(0)).getUserObject()).getId());
	}

	@Test
	public void findsAVersionPreferringMyModels() {
		BioModelInfo mine = this.bioModel("Mine", ME, new GroupAccessAll(), VersionFlag.Current, null, 8, 1); // also uncurated
		DefaultMutableTreeNode root = this.build(List.of(mine), "");
		TreePath path = ModelExplorerTree.find(root, mine);
		assertNotNull(path);
		assertEquals(Section.MY_MODELS, ((Folder) ((DefaultMutableTreeNode) path.getPathComponent(1)).getUserObject()).getSection());
	}

	@Test
	public void theBioModelsDatabaseSaysWhileItLoads() {
		DefaultMutableTreeNode root = ModelExplorerTree.build(new ModelExplorerTree.Input(List.of(), List.of(), ME, "", SearchOptions.defaults(), null, NOW));
		assertEquals("BioModels Database (loading...)", this.folder(root, Section.BIOMODELS_DATABASE).toString());
	}

	@Test
	public void comparesTwoDifferentVersionsOfTheSameKind() {
		BioModelInfo first = this.bioModel("First", ME, new GroupAccessNone(), VersionFlag.Current, null, 8, 1);
		BioModelInfo sameModelLater = this.bioModel("First", ME, new GroupAccessNone(), VersionFlag.Current, null, 8, 2);
		MathModelInfo math = this.mathModel("Math", ME, new GroupAccessNone());

		assertNull(ModelExplorerTree.whyNotComparable(first, sameModelLater)); // two versions of one model
		assertEquals("Choose another BioModel: only models of the same kind can be compared", ModelExplorerTree.whyNotComparable(first, math));
		assertEquals("That's the same version: choose another BioModel or version", ModelExplorerTree.whyNotComparable(first, first));
		assertEquals("Select a model to compare with", ModelExplorerTree.whyNotComparable(first, null));
	}

	@Test
	public void namesAPublicationByFirstAuthorAndYear() {
		assertEquals("Smith et al. (2019) A paper", ModelExplorerTree.describe(this.publication("A paper", "Smith J", 2019, "Doe A")));
		assertEquals("Smith (2019) A paper", ModelExplorerTree.describe(this.publication("A paper", "Smith, John", 2019)));
		assertEquals("Smith (2019) A paper", ModelExplorerTree.describe(this.publication("A paper", "John Smith", 2019)));
	}

	// ---- building

	private DefaultMutableTreeNode build(List<VCDocumentInfo> models, String search) {
		List<BioModelsNetModelInfo> database = List.of(new BioModelsNetModelInfo("BIOMD1", "Calcium model", "link", true),
				new BioModelsNetModelInfo("BIOMD2", "Glycolysis", "link", true));
		return ModelExplorerTree.build(new ModelExplorerTree.Input(models, List.of(), ME, search, SearchOptions.defaults(), database, NOW));
	}

	private DefaultMutableTreeNode folder(DefaultMutableTreeNode parent, Section section) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
			if (child.getUserObject() instanceof Folder folder && folder.getSection() == section) return child;
		}
		throw new AssertionError("no " + section + " under " + parent);
	}

	private static List<Section> sections(DefaultMutableTreeNode parent) {
		List<Section> sections = new ArrayList<>();
		for (int i = 0; i < parent.getChildCount(); i++) {
			if (((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject() instanceof Folder folder) sections.add(folder.getSection());
		}
		return sections;
	}

	private static List<String> names(DefaultMutableTreeNode parent) {
		List<String> names = new ArrayList<>();
		for (int i = 0; i < parent.getChildCount(); i++) {
			if (((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject() instanceof Model model) names.add(model.latest().getVersion().getName());
		}
		return names;
	}

	private static List<Object> userObjects(DefaultMutableTreeNode parent) {
		List<Object> objects = new ArrayList<>();
		for (int i = 0; i < parent.getChildCount(); i++) objects.add(((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject());
		return objects;
	}

	private GroupAccessSome sharedWithMe() {
		boolean[] hidden = {false};
		return new GroupAccessSome(BigDecimal.valueOf(42), GroupAccess.calculateHash(new KeyValue[] {ME.getID()}, hidden), new User[] {ME}, hidden);
	}

	private BioModelInfo bioModel(String name, User owner, GroupAccess access, VersionFlag flag, PublicationInfo publication, int branch, int day) {
		BioModelInfo info = new BioModelInfo(this.version(name, owner, access, flag, branch, day), (String) null, null);
		if (null != publication) info.addPublicationInfo(publication);
		return info;
	}

	private MathModelInfo mathModel(String name, User owner, GroupAccess access) {
		return new MathModelInfo(this.version(name, owner, access, VersionFlag.Current, this.nextKey, 1), new KeyValue(String.valueOf(this.nextKey++)), (String) null, null);
	}

	private PublicationInfo publication(String title, String firstAuthor, int year, String... moreAuthors) {
		String[] authors = new String[1 + moreAuthors.length];
		authors[0] = firstAuthor;
		System.arraycopy(moreAuthors, 0, authors, 1, moreAuthors.length);
		@SuppressWarnings("deprecation")
		Date date = new Date(year - 1900, 5, 1);
		return new PublicationInfo(new KeyValue(String.valueOf(this.nextKey++)), new KeyValue(String.valueOf(this.nextKey++)), title, authors,
				"A citation", null, null, null, VCDocumentType.BIOMODEL_DOC, ME, date);
	}

	private Version version(String name, User owner, GroupAccess access, VersionFlag flag, int branch, int day) {
		return new Version(new KeyValue(String.valueOf(this.nextKey++)), name, owner, access, null,
				BigDecimal.valueOf(branch), new Date(1_700_000_000_000L + day * 86_400_000L), flag, null);
	}
}
