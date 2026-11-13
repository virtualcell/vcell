package cbit.vcell.desktop;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;

/**
 * The Model Explorer's tree: the VCell database's BioModels and MathModels together, in the folders VCell's database
 * tree has always had, each arranged its own way; then the database's geometries, and EBI's BioModels Database.
 * <ul>
 * <li>My Models: model, then its versions</li>
 * <li>Shared With Me: owner, model, versions</li>
 * <li>Public Models: Published and Curated by publication (newest first), then model and versions, with Curated's Model
 * Bricks in a group of their own; Uncurated by owner, then model and versions</li>
 * <li>Tutorials: model, then versions</li>
 * <li>Geometries: My Geometries (geometry, versions), Shared With Me and Public Geometries (owner, geometry, versions)</li>
 * <li>BioModels Database: its models, by name</li>
 * </ul>
 * A model's versions are listed under it only when it has more than one to show; its node stands for its latest. Only the
 * versions the search and its options let through are listed, and only the models with any; folders say how many
 * models they hold. Built off the event thread, as a plain tree of nodes whose user objects are the types below.
 */
public final class ModelExplorerTree {

	/** The tree's folders. */
	public enum Section {
		MY_MODELS("My Models"),
		SHARED("Shared With Me"),
		PUBLIC("Public Models"),
		PUBLISHED("Published"),
		CURATED("Curated"),
		MODEL_BRICKS("Model Bricks"),
		UNCURATED("Uncurated"),
		TUTORIALS("Tutorials"),
		GEOMETRIES("Geometries"),
		MY_GEOMETRIES("My Geometries"),
		SHARED_GEOMETRIES("Shared With Me"),
		PUBLIC_GEOMETRIES("Public Geometries"),
		BIOMODELS_DATABASE("BioModels Database");

		public final String title;

		Section(String title) {
			this.title = title;
		}
	}

	/** A folder, and how many models (or geometries) it holds. */
	public static final class Folder {
		private final Section section;
		private int count = 0;
		private String note = null; // in place of a count: "loading...", say

		Folder(Section section) {
			this.section = section;
		}

		public Section getSection() { return this.section; }
		public int getCount() { return this.count; }
		public String getNote() { return this.note; }

		@Override
		public String toString() {
			return this.section.title + " (" + (null != this.note ? this.note : this.count) + ")";
		}
	}

	/** Whose models follow. */
	public record Owner(String name, int count) {
		@Override
		public String toString() {
			return this.name + " (" + this.count + ")";
		}
	}

	/** A model (all its versions shown here, newest first): its node stands for the latest. */
	public record Model(VCDocumentInfo latest, List<VCDocumentInfo> versions) {
		@Override
		public String toString() {
			return this.latest.getVersion().getName();
		}
	}

	/** A publication, and how many of its models are listed under it. */
	public record Publication(PublicationInfo info, int count) {
		@Override
		public String toString() {
			return ModelExplorerTree.describe(this.info) + " (" + this.count + ")";
		}
	}

	/** What the tree is built from. */
	public record Input(Collection<? extends VCDocumentInfo> models, Collection<? extends VCDocumentInfo> geometries, User user,
			String searchText, SearchOptions options, List<BioModelsNetModelInfo> bioModelsDatabase, Date now) {}

	private static final Set<String> TUTORIAL_OWNERS = Set.of(
			VCDocumentDbTreeModel.USER_tutorial, VCDocumentDbTreeModel.USER_tutorial610, VCDocumentDbTreeModel.USER_tutorial611);

	private ModelExplorerTree() {}

	/** The tree for this input; its root (hidden) holds the top folders. */
	public static DefaultMutableTreeNode build(Input input) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Model Explorer");
		List<VCDocumentInfo> found = ModelExplorerTree.found(input.models(), input);
		User user = input.user();
		boolean guest = null == user || User.isGuest(user.getName());

		if (!guest) {
			root.add(ModelExplorerTree.modelsFolder(Section.MY_MODELS, found.stream().filter(info -> ModelExplorerTree.isMine(info, user)).toList(), input));
			root.add(ModelExplorerTree.byOwnerFolder(Section.SHARED, found.stream()
					.filter(info -> !ModelExplorerTree.isMine(info, user) && ModelExplorerTree.isShared(info)).toList(), input));
		}

		DefaultMutableTreeNode publicModels = ModelExplorerTree.folder(Section.PUBLIC);
		List<VCDocumentInfo> published = found.stream().filter(info -> ModelExplorerTree.hasPublication(info) && ModelExplorerTree.isPublished(info)).toList();
		List<VCDocumentInfo> curated = found.stream().filter(info -> ModelExplorerTree.hasPublication(info) && !ModelExplorerTree.isPublished(info)).toList();
		List<VCDocumentInfo> bricks = found.stream().filter(info -> !ModelExplorerTree.hasPublication(info) && ModelExplorerTree.isPublic(info)
				&& VCDocumentDbTreeModel.USER_modelBricks.equals(ModelExplorerTree.ownerName(info))).toList();
		List<VCDocumentInfo> uncurated = found.stream().filter(info -> !ModelExplorerTree.hasPublication(info) && ModelExplorerTree.isPublic(info)
				&& !ModelExplorerTree.TUTORIAL_OWNERS.contains(ModelExplorerTree.ownerName(info))
				&& !VCDocumentDbTreeModel.USER_modelBricks.equals(ModelExplorerTree.ownerName(info))).toList();
		publicModels.add(ModelExplorerTree.byPublicationFolder(Section.PUBLISHED, published, input));
		DefaultMutableTreeNode curatedFolder = ModelExplorerTree.byPublicationFolder(Section.CURATED, curated, input);
		DefaultMutableTreeNode bricksFolder = ModelExplorerTree.modelsFolder(Section.MODEL_BRICKS, bricks, input);
		if (bricksFolder.getChildCount() > 0) {
			curatedFolder.add(bricksFolder);
			((Folder) curatedFolder.getUserObject()).count += ((Folder) bricksFolder.getUserObject()).count;
		}
		publicModels.add(curatedFolder);
		publicModels.add(ModelExplorerTree.byOwnerFolder(Section.UNCURATED, uncurated, input));
		ModelExplorerTree.countModels(publicModels, List.of(published, curated, bricks, uncurated));
		root.add(publicModels);

		root.add(ModelExplorerTree.modelsFolder(Section.TUTORIALS, found.stream()
				.filter(info -> ModelExplorerTree.TUTORIAL_OWNERS.contains(ModelExplorerTree.ownerName(info))).toList(), input));

		List<VCDocumentInfo> geometries = ModelExplorerTree.found(input.geometries(), input);
		DefaultMutableTreeNode geometryFolder = ModelExplorerTree.folder(Section.GEOMETRIES);
		List<VCDocumentInfo> myGeometries = guest ? List.of() : geometries.stream().filter(info -> ModelExplorerTree.isMine(info, user)).toList();
		List<VCDocumentInfo> sharedGeometries = guest ? List.of() : geometries.stream()
				.filter(info -> !ModelExplorerTree.isMine(info, user) && ModelExplorerTree.isShared(info)).toList();
		List<VCDocumentInfo> publicGeometries = geometries.stream()
				.filter(info -> !ModelExplorerTree.isMine(info, user) && ModelExplorerTree.isPublic(info)).toList();
		if (!guest) {
			geometryFolder.add(ModelExplorerTree.modelsFolder(Section.MY_GEOMETRIES, myGeometries, input));
			geometryFolder.add(ModelExplorerTree.byOwnerFolder(Section.SHARED_GEOMETRIES, sharedGeometries, input));
		}
		geometryFolder.add(ModelExplorerTree.byOwnerFolder(Section.PUBLIC_GEOMETRIES, publicGeometries, input));
		ModelExplorerTree.countModels(geometryFolder, List.of(myGeometries, sharedGeometries, publicGeometries));
		root.add(geometryFolder);

		root.add(ModelExplorerTree.bioModelsDatabaseFolder(input));
		return root;
	}

	/** The path to where a model shows: its version, or its model node; in My Models first, if it's there. Null if not shown. */
	public static TreePath find(DefaultMutableTreeNode root, VCDocumentInfo target) {
		if (null == target || null == target.getVersion()) return null;
		DefaultMutableTreeNode sameModel = null;
		for (Enumeration<javax.swing.tree.TreeNode> nodes = root.preorderEnumeration(); nodes.hasMoreElements(); ) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
			Object object = node.getUserObject();
			if (object instanceof VCDocumentInfo info && ModelExplorerTree.sameVersion(info, target)) return new TreePath(node.getPath());
			if (!(object instanceof Model model)) continue;
			// a model node standing for this version (its versions aren't listed under it)
			if (0 == node.getChildCount() && model.versions().stream().anyMatch(info -> ModelExplorerTree.sameVersion(info, target))) {
				return new TreePath(node.getPath());
			}
			if (null == sameModel && ModelExplorerTree.modelKey(model.latest()).equals(ModelExplorerTree.modelKey(target))) sameModel = node;
		}
		return null == sameModel ? null : new TreePath(sameModel.getPath()); // another version of it
	}

	/** "Smith et al. (2019) Title", as the tree names a publication. */
	public static String describe(PublicationInfo info) {
		StringBuilder text = new StringBuilder();
		String[] authors = info.getAuthors();
		if (null != authors && authors.length > 0 && null != authors[0] && !authors[0].isBlank()) {
			text.append(ModelExplorerTree.surname(authors[0]));
			if (authors.length > 1) text.append(" et al.");
			text.append(' ');
		}
		if (null != info.getPubDate()) text.append('(').append(new SimpleDateFormat("yyyy").format(info.getPubDate())).append(") ");
		text.append(null == info.getTitle() || info.getTitle().isBlank() ? "Untitled publication" : info.getTitle().strip());
		return text.toString();
	}

	// "Smith" from "Smith J", "Smith, J." or "J. Smith"
	private static String surname(String author) {
		String name = author.strip();
		int comma = name.indexOf(',');
		if (comma > 0) return name.substring(0, comma).strip();
		String[] words = name.split("\\s+");
		if (words.length < 2) return name;
		// "Smith JA": initials last; "John Smith": the surname last
		String last = words[words.length - 1];
		boolean initials = last.length() <= 3 && last.equals(last.toUpperCase(Locale.ROOT));
		return initials ? String.join(" ", java.util.Arrays.copyOf(words, words.length - 1)) : last;
	}

	// ---- placing

	// The versions the search and its options let through
	private static List<VCDocumentInfo> found(Collection<? extends VCDocumentInfo> infos, Input input) {
		List<VCDocumentInfo> found = new ArrayList<>();
		if (null == infos) return found;
		Set<SearchOptions.Field> fields = input.options().getFields();
		for (VCDocumentInfo info : infos) {
			Version version = info.getVersion();
			if (null == version || null == version.getOwner()) continue;
			if (!input.options().accepts(info, input.now())) continue;
			if (ModelSearch.NO_MATCH == ModelSearch.rank(info, input.searchText(), fields)) continue;
			found.add(info);
		}
		return found;
	}

	// A folder of models: model, then versions
	private static DefaultMutableTreeNode modelsFolder(Section section, List<VCDocumentInfo> versions, Input input) {
		DefaultMutableTreeNode folder = ModelExplorerTree.folder(section);
		List<Model> models = ModelExplorerTree.models(versions, input);
		for (Model model : models) folder.add(ModelExplorerTree.modelNode(model));
		((Folder) folder.getUserObject()).count = models.size();
		return folder;
	}

	// A folder of owners, each with their models: owner, model, versions
	private static DefaultMutableTreeNode byOwnerFolder(Section section, List<VCDocumentInfo> versions, Input input) {
		DefaultMutableTreeNode folder = ModelExplorerTree.folder(section);
		Map<String, List<VCDocumentInfo>> byOwner = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (VCDocumentInfo info : versions) byOwner.computeIfAbsent(ModelExplorerTree.ownerName(info), owner -> new ArrayList<>()).add(info);
		int count = 0;
		for (Map.Entry<String, List<VCDocumentInfo>> entry : byOwner.entrySet()) {
			List<Model> models = ModelExplorerTree.models(entry.getValue(), input);
			DefaultMutableTreeNode owner = new DefaultMutableTreeNode(new Owner(entry.getKey(), models.size()));
			for (Model model : models) owner.add(ModelExplorerTree.modelNode(model));
			folder.add(owner);
			count += models.size();
		}
		((Folder) folder.getUserObject()).count = count;
		return folder;
	}

	// A folder of publications, each with its models (newest publication first, then by first author): publication, model,
	// versions. A version with more than one publication is listed under each.
	private static DefaultMutableTreeNode byPublicationFolder(Section section, List<VCDocumentInfo> versions, Input input) {
		DefaultMutableTreeNode folder = ModelExplorerTree.folder(section);
		Map<String, PublicationInfo> publications = new LinkedHashMap<>();
		Map<String, List<VCDocumentInfo>> byPublication = new LinkedHashMap<>();
		for (VCDocumentInfo info : versions) {
			for (PublicationInfo publication : info.getPublicationInfos()) {
				if (null == publication) continue;
				KeyValue publicationKey = publication.getPublicationKey();
				String key = null != publicationKey ? publicationKey.toString() : "title:" + publication.getTitle();
				publications.putIfAbsent(key, publication);
				byPublication.computeIfAbsent(key, k -> new ArrayList<>()).add(info);
			}
		}
		List<String> order = new ArrayList<>(publications.keySet());
		order.sort(Comparator.comparing((String key) -> publications.get(key).getPubDate(), Comparator.nullsLast(Comparator.reverseOrder()))
				.thenComparing(key -> ModelExplorerTree.firstAuthor(publications.get(key)), String.CASE_INSENSITIVE_ORDER));
		java.util.Set<String> counted = new java.util.HashSet<>();
		for (String key : order) {
			List<Model> models = ModelExplorerTree.models(byPublication.get(key), input);
			DefaultMutableTreeNode publication = new DefaultMutableTreeNode(new Publication(publications.get(key), models.size()));
			for (Model model : models) {
				publication.add(ModelExplorerTree.modelNode(model));
				counted.add(ModelExplorerTree.modelKey(model.latest()));
			}
			folder.add(publication);
		}
		((Folder) folder.getUserObject()).count = counted.size(); // a model under two publications counts once
		return folder;
	}

	private static DefaultMutableTreeNode bioModelsDatabaseFolder(Input input) {
		DefaultMutableTreeNode folder = ModelExplorerTree.folder(Section.BIOMODELS_DATABASE);
		Folder about = (Folder) folder.getUserObject();
		if (null == input.bioModelsDatabase()) {
			about.note = "loading...";
			return folder;
		}
		// only the search text, by name or ID: nothing else is known about these models until they're imported
		String text = null == input.searchText() ? "" : input.searchText().strip().toLowerCase(Locale.ROOT);
		boolean narrowed = !input.options().getTypes().contains(VCDocumentType.BIOMODEL_DOC);
		List<BioModelsNetModelInfo> models = narrowed ? List.of() : input.bioModelsDatabase().stream()
				.filter(info -> text.isEmpty() || ModelExplorerTree.lower(info.getName()).contains(text) || ModelExplorerTree.lower(info.getId()).contains(text))
				.sorted(Comparator.comparing(BioModelsNetModelInfo::getName, String.CASE_INSENSITIVE_ORDER))
				.toList();
		for (BioModelsNetModelInfo info : models) folder.add(new DefaultMutableTreeNode(info, false));
		about.count = models.size();
		return folder;
	}

	// A folder that only holds folders counts the distinct models under them
	private static void countModels(DefaultMutableTreeNode folder, List<List<VCDocumentInfo>> lists) {
		java.util.Set<String> models = new java.util.HashSet<>();
		for (List<VCDocumentInfo> list : lists) for (VCDocumentInfo info : list) models.add(ModelExplorerTree.modelKey(info));
		((Folder) folder.getUserObject()).count = models.size();
	}

	// The versions as models (their versions share a branch), each's versions newest first, in the options' order
	static List<Model> models(List<VCDocumentInfo> versions, Input input) {
		Map<String, List<VCDocumentInfo>> byModel = new LinkedHashMap<>();
		for (VCDocumentInfo info : versions) byModel.computeIfAbsent(ModelExplorerTree.modelKey(info), key -> new ArrayList<>()).add(info);
		Comparator<VCDocumentInfo> newestFirst = Comparator.comparing((VCDocumentInfo info) -> info.getVersion().getDate(), Comparator.nullsLast(Comparator.reverseOrder()));
		List<Model> models = new ArrayList<>();
		for (List<VCDocumentInfo> modelVersions : byModel.values()) {
			List<VCDocumentInfo> sorted = modelVersions.stream().sorted(newestFirst).distinct().toList();
			models.add(new Model(sorted.get(0), sorted));
		}
		Comparator<Model> byName = Comparator.comparing((Model model) -> model.latest().getVersion().getName(), String.CASE_INSENSITIVE_ORDER)
				.thenComparing(model -> ModelListRenderer.getDocumentType(model.latest()).ordinal());
		Comparator<Model> order = switch (input.options().getOrder()) {
			case NAME -> byName;
			case NEWEST -> Comparator.comparing((Model model) -> model.latest().getVersion().getDate(), Comparator.nullsLast(Comparator.reverseOrder()));
			case BEST_MATCH -> ModelSearch.isBlank(input.searchText()) ? byName
					: Comparator.comparingInt((Model model) -> model.versions().stream()
							.mapToInt(info -> ModelSearch.rank(info, input.searchText(), input.options().getFields())).min().orElse(ModelSearch.NO_MATCH))
							.thenComparing(byName);
		};
		models.sort(order);
		return models;
	}

	// A model; its versions under it when there's more than one to show
	private static DefaultMutableTreeNode modelNode(Model model) {
		boolean versioned = model.versions().size() > 1;
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(model, versioned);
		if (versioned) for (VCDocumentInfo version : model.versions()) node.add(new DefaultMutableTreeNode(version, false));
		return node;
	}

	private static DefaultMutableTreeNode folder(Section section) {
		return new DefaultMutableTreeNode(new Folder(section), true);
	}

	// ---- about a version

	/** Which model a version is of: its versions share a branch. */
	static String modelKey(VCDocumentInfo info) {
		Version version = info.getVersion();
		return ModelListRenderer.getDocumentType(info) + ":" + (null == version.getBranchID() ? version.getVersionKey() : version.getBranchID());
	}

	private static boolean sameVersion(VCDocumentInfo a, VCDocumentInfo b) {
		return ModelListRenderer.getDocumentType(a) == ModelListRenderer.getDocumentType(b)
				&& Objects.equals(a.getVersion().getVersionKey(), b.getVersion().getVersionKey());
	}

	private static boolean isMine(VCDocumentInfo info, User user) {
		return null != user && user.compareEqual(info.getVersion().getOwner());
	}

	private static BigDecimal groupId(VCDocumentInfo info) {
		GroupAccess access = info.getVersion().getGroupAccess();
		return null == access ? GroupAccess.GROUPACCESS_NONE : access.getGroupid();
	}

	private static boolean isPublic(VCDocumentInfo info) {
		return GroupAccess.GROUPACCESS_ALL.equals(ModelExplorerTree.groupId(info));
	}

	private static boolean isShared(VCDocumentInfo info) {
		BigDecimal groupId = ModelExplorerTree.groupId(info);
		return !GroupAccess.GROUPACCESS_ALL.equals(groupId) && !GroupAccess.GROUPACCESS_NONE.equals(groupId);
	}

	private static boolean hasPublication(VCDocumentInfo info) {
		PublicationInfo[] publications = info.getPublicationInfos();
		return null != publications && publications.length > 0;
	}

	private static boolean isPublished(VCDocumentInfo info) {
		VersionFlag flag = info.getVersion().getFlag();
		return null != flag && flag.compareEqual(VersionFlag.Published);
	}

	private static String ownerName(VCDocumentInfo info) {
		return info.getVersion().getOwner().getName();
	}

	private static String firstAuthor(PublicationInfo info) {
		String[] authors = info.getAuthors();
		return null == authors || authors.length == 0 || null == authors[0] ? "" : authors[0];
	}

	private static String lower(String text) {
		return null == text ? "" : text.toLowerCase(Locale.ROOT);
	}

	/** The node's model, as it stands for one: a version, or a model's latest; null for anything else. */
	/**
	 * Why these two can't be compared, or null if they can: VCell compares two saved documents of the same kind (two
	 * BioModels, two MathModels or two geometries), and not one version with itself.
	 */
	public static String whyNotComparable(VCDocumentInfo first, VCDocumentInfo second) {
		if (null == first || null == second) return "Select a model to compare with";
		String kind = ModelExplorerTree.kindName(first);
		if (!kind.equals(ModelExplorerTree.kindName(second))) return "Choose another " + kind + ": only models of the same kind can be compared";
		if (first.getVersion().getVersionKey().equals(second.getVersion().getVersionKey())) return "That's the same version: choose another " + kind + " or version";
		return null;
	}

	// "BioModel", "MathModel" or "geometry"
	private static String kindName(VCDocumentInfo info) {
		if (info instanceof BioModelInfo) return "BioModel";
		if (info instanceof MathModelInfo) return "MathModel";
		return "geometry";
	}

	public static VCDocumentInfo documentOf(Object userObject) {
		if (userObject instanceof VCDocumentInfo info) return info;
		if (userObject instanceof Model model) return model.latest();
		return null;
	}

	/** Every model a node holds, latest versions, as a list for the details of a folder, owner or publication. */
	public static List<VCDocumentInfo> modelsUnder(DefaultMutableTreeNode node) {
		Map<String, VCDocumentInfo> models = new LinkedHashMap<>();
		Function<DefaultMutableTreeNode, Void> collect = new Function<>() {
			@Override
			public Void apply(DefaultMutableTreeNode current) {
				if (current.getUserObject() instanceof Model model) {
					models.putIfAbsent(ModelExplorerTree.modelKey(model.latest()), model.latest());
					return null;
				}
				for (int i = 0; i < current.getChildCount(); i++) this.apply((DefaultMutableTreeNode) current.getChildAt(i));
				return null;
			}
		};
		collect.apply(node);
		return new ArrayList<>(models.values());
	}

	/** A model as the Model Explorer's table lists it: once, with the folders the tree lists it in. */
	public record TableRow(Model model, List<Section> folders) {
		public VCDocumentInfo latest() {
			return this.model.latest();
		}
	}

	/**
	 * The tree's models as the rows of a table, each once, in the tree's order, with every folder it's in (the folder
	 * nearest it: a published model's is Published, not Public Models). BioModels Database models aren't among them.
	 */
	public static List<TableRow> tableRows(DefaultMutableTreeNode root) {
		Map<String, Model> models = new LinkedHashMap<>();
		Map<String, java.util.LinkedHashSet<Section>> folders = new LinkedHashMap<>();
		for (Enumeration<javax.swing.tree.TreeNode> nodes = root.preorderEnumeration(); nodes.hasMoreElements(); ) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
			if (!(node.getUserObject() instanceof Model model)) continue;
			String key = ModelExplorerTree.modelKey(model.latest());
			models.putIfAbsent(key, model);
			Section folder = ModelExplorerTree.folderOf(node);
			if (null != folder) folders.computeIfAbsent(key, k -> new java.util.LinkedHashSet<>()).add(folder);
		}
		List<TableRow> rows = new ArrayList<>();
		models.forEach((key, model) -> rows.add(new TableRow(model, List.copyOf(folders.getOrDefault(key, new java.util.LinkedHashSet<>())))));
		return rows;
	}

	// The folder nearest a node, above it
	private static Section folderOf(DefaultMutableTreeNode node) {
		for (javax.swing.tree.TreeNode parent = node.getParent(); null != parent; parent = parent.getParent()) {
			if (((DefaultMutableTreeNode) parent).getUserObject() instanceof Folder folder) return folder.getSection();
		}
		return null;
	}
}
