package cbit.vcell.desktop;

import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

import org.vcell.util.document.VCDocument.VCDocumentType;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The VCell database's BioModels and MathModels, together, in tabs as the database tree's folders sort them:
 * Community (its own tabs: Published, Curated, Uncurated), Personal (the user's own, and those shared with the user,
 * marked as shared; not for a guest, who can't save models), and Tutorial. Each lists the latest version of each model that belongs there, by name, as
 * Recent Models lists the user's; each tab's name says how many it has. Opening one (double-click, or Enter) hands it
 * to whatever shows the panel.
 */
@SuppressWarnings("serial")
public class DatabaseModelsPanel extends JPanel {
	/** The lists, in the database tree's terms. */
	enum ModelList {
		PUBLISHED("Published", "No published models"),
		CURATED("Curated", "No curated models"),
		UNCURATED("Uncurated", "No uncurated models"),
		MINE("Personal", "You have no saved models, and none are shared with you"), // and those shared with the user
		TUTORIAL("Tutorial", "No tutorial models");

		final String title;
		final String emptyMessage;

		ModelList(String title, String emptyMessage) {
			this.title = title;
			this.emptyMessage = emptyMessage;
		}
	}

	private static final List<ModelList> COMMUNITY = List.of(ModelList.PUBLISHED, ModelList.CURATED, ModelList.UNCURATED);
	private static final List<ModelList> BESIDE_COMMUNITY = List.of(ModelList.MINE, ModelList.TUTORIAL);
	private static final Set<String> TUTORIAL_OWNERS = Set.of(
			VCDocumentDbTreeModel.USER_tutorial, VCDocumentDbTreeModel.USER_tutorial610, VCDocumentDbTreeModel.USER_tutorial611);

	private final Map<ModelList, ModelListView> views = new EnumMap<>(ModelList.class);
	private final JTabbedPane tabs = new JTabbedPane();
	private final JTabbedPane communityTabs = new JTabbedPane();
	private final DatabaseListener databaseListener = new DatabaseListener() {
		@Override public void databaseDelete(DatabaseEvent event) { DatabaseModelsPanel.this.refreshLater(); }
		@Override public void databaseInsert(DatabaseEvent event) { DatabaseModelsPanel.this.refreshLater(); }
		@Override public void databaseRefresh(DatabaseEvent event) { DatabaseModelsPanel.this.refreshLater(); }
		@Override public void databaseUpdate(DatabaseEvent event) { DatabaseModelsPanel.this.refreshLater(); }
	};
	private DocumentManager documentManager = null;
	private Consumer<VCDocumentInfo> opener = info -> {};
	private Function<VCDocumentInfo, JPopupMenu> menuMaker = null; // a listed model's right-click menu, if any
	private Map<ModelList, List<VCDocumentInfo>> allModels = new EnumMap<>(ModelList.class); // each list's, whatever the search
	private String searchText = "";
	private SearchOptions searchOptions = SearchOptions.defaults();
	private boolean counted = false; // someone is logged in, so the tabs say how many
	private final Set<VCDocumentType> documentTypes;

	/** BioModels and MathModels, as the welcome window lists. */
	public DatabaseModelsPanel() {
		this(VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC);
	}

	/** Only documents of these types (e.g. geometries, for choosing one). */
	public DatabaseModelsPanel(VCDocumentType... documentTypes) {
		super(new BorderLayout());
		this.documentTypes = EnumSet.copyOf(Arrays.asList(documentTypes));
		this.setName("DatabaseModelsPanel");
		for (ModelList modelList : ModelList.values()) {
			this.views.put(modelList, new ModelListView(modelList));
		}
		this.communityTabs.setName("CommunityModelsTabs");
		for (ModelList modelList : COMMUNITY) {
			this.communityTabs.addTab(modelList.title, this.views.get(modelList));
		}
		this.tabs.setName("DatabaseModelsTabs");
		this.tabs.addTab("Community", this.communityTabs);
		for (ModelList modelList : BESIDE_COMMUNITY) {
			this.tabs.addTab(modelList.title, this.views.get(modelList));
		}
		this.add(this.tabs, BorderLayout.CENTER);
	}

	/** The document manager whose models it lists (null when no one is logged in); it follows that manager's changes. */
	public void setDocumentManager(DocumentManager documentManager) {
		if (null != this.documentManager) this.documentManager.removeDatabaseListener(this.databaseListener);
		this.documentManager = documentManager;
		if (null != this.documentManager) this.documentManager.addDatabaseListener(this.databaseListener);
		this.refresh();
	}

	/** What to do with a model the user opens from a list. */
	public void setOpener(Consumer<VCDocumentInfo> opener) {
		this.opener = Objects.requireNonNull(opener);
	}

	/** What a listed model's right-click menu holds (null for none); it opens the menu for whichever model was clicked. */
	public void setMenuMaker(Function<VCDocumentInfo, JPopupMenu> menuMaker) {
		this.menuMaker = menuMaker;
	}

	/** The model selected in the list showing now, or null. */
	public VCDocumentInfo getSelectedModel() {
		Component current = this.tabs.getSelectedComponent();
		if (current == this.communityTabs) current = this.communityTabs.getSelectedComponent(); // the list chosen within Community
		return current instanceof ModelListView view ? view.list.getSelectedValue() : null;
	}

	/** Called whenever a list's selection, or the list showing, changes. */
	public void addSelectionListener(Runnable listener) {
		for (ModelListView view : this.views.values()) {
			view.list.addListSelectionListener(e -> listener.run());
		}
		this.tabs.addChangeListener(e -> listener.run());
		this.communityTabs.addChangeListener(e -> listener.run());
	}

	/** Lists the models again, from what the document manager knows now. */
	public void refresh() {
		User user = null == this.documentManager ? null : this.documentManager.getUser();
		List<VCDocumentInfo> infos = new ArrayList<>();
		if (null != user) {
			if (this.documentTypes.contains(VCDocumentType.BIOMODEL_DOC)) infos.addAll(Arrays.asList(this.documentManager.getBioModelInfos()));
			if (this.documentTypes.contains(VCDocumentType.MATHMODEL_DOC)) infos.addAll(Arrays.asList(this.documentManager.getMathModelInfos()));
			if (this.documentTypes.contains(VCDocumentType.GEOMETRY_DOC)) infos.addAll(Arrays.asList(this.documentManager.getGeometryInfos()));
		}
		this.allModels = null == user ? new EnumMap<>(ModelList.class) : DatabaseModelsPanel.sortIntoLists(infos, user);
		this.counted = null != user;
		this.showPersonalTab(null == user || !User.isGuest(user.getName()));
		this.showSearched();
	}

	/** Lists only the models this search matches, best first (see ModelSearch); all of them, by name, for none. */
	public void setSearchText(String searchText) {
		this.setSearch(searchText, this.searchOptions);
	}

	/** Lists only the models this search matches and its options let through, in the options' order (see ModelSearch). */
	public void setSearch(String searchText, SearchOptions options) {
		String text = null == searchText ? "" : searchText;
		if (text.strip().equals(this.searchText.strip()) && options.equals(this.searchOptions)) return;
		this.searchText = text;
		this.searchOptions = options;
		this.showSearched();
	}

	// Each list's models the search matches, and the tabs' counts of them
	private void showSearched() {
		boolean filtering = 0 < this.searchOptions.changedCount();
		String noMatches = !this.searchText.isBlank() ? "No models match \u201C" + this.searchText.strip() + "\u201D"
				: filtering ? "No models match the filters" : null;
		Date now = new Date();
		for (ModelList modelList : ModelList.values()) {
			List<VCDocumentInfo> models = ModelSearch.search(this.allModels.getOrDefault(modelList, List.of()), this.searchText, this.searchOptions, now);
			this.views.get(modelList).renderer.setSearch(this.searchText, this.searchOptions.getFields());
			this.views.get(modelList).setModels(models, null != noMatches ? noMatches : modelList.emptyMessage);
		}
		this.showCounts(this.counted);
	}

	// A guest can't save models, and nothing is shared with the guest account: no Personal tab, which would be empty
	private void showPersonalTab(boolean shown) {
		ModelListView personal = this.views.get(ModelList.MINE);
		int index = this.tabs.indexOfComponent(personal);
		if (shown && index < 0) this.tabs.insertTab(ModelList.MINE.title, null, personal, null, 1); // after Community
		if (!shown && index >= 0) this.tabs.removeTabAt(index);
	}

	// Each tab's name with how many models it has (logged in); Community's counts a model in more than one of its lists once
	private void showCounts(boolean counted) {
		Set<String> communityModels = new HashSet<>();
		for (int index = 0; index < COMMUNITY.size(); index++) {
			List<VCDocumentInfo> models = this.views.get(COMMUNITY.get(index)).getModels();
			models.forEach(info -> communityModels.add(DatabaseModelsPanel.modelKey(info)));
			this.communityTabs.setTitleAt(index, DatabaseModelsPanel.withCount(COMMUNITY.get(index).title, models.size(), counted));
		}
		this.tabs.setTitleAt(0, DatabaseModelsPanel.withCount("Community", communityModels.size(), counted));
		for (ModelList modelList : BESIDE_COMMUNITY) {
			int index = this.tabs.indexOfComponent(this.views.get(modelList)); // Personal is left out for a guest
			if (index >= 0) this.tabs.setTitleAt(index, DatabaseModelsPanel.withCount(modelList.title, this.views.get(modelList).getModels().size(), counted));
		}
	}

	private static String withCount(String title, int count, boolean counted) {
		return counted ? title + " (" + NumberFormat.getIntegerInstance().format(count) + ")" : title;
	}

	/** The tabs' names, as shown: Community's own tabs, then the top row's. */
	List<String> getTabTitles() {
		List<String> titles = new ArrayList<>();
		for (int index = 0; index < this.communityTabs.getTabCount(); index++) titles.add(this.communityTabs.getTitleAt(index));
		for (int index = 0; index < this.tabs.getTabCount(); index++) titles.add(this.tabs.getTitleAt(index));
		return titles;
	}

	// Which model a version is of: its versions share a branch
	private static String modelKey(VCDocumentInfo info) {
		Version version = info.getVersion();
		return ModelListRenderer.getDocumentType(info) + ":" + (null == version.getBranchID() ? version.getVersionKey() : version.getBranchID());
	}

	/** The models a list shows now. */
	List<VCDocumentInfo> getShown(ModelList modelList) {
		return this.views.get(modelList).getModels();
	}

	/**
	 * Each list's models: the latest version of each model (its versions share a branch) among the versions that
	 * belong in that list, by name.
	 */
	static Map<ModelList, List<VCDocumentInfo>> sortIntoLists(Collection<VCDocumentInfo> infos, User user) {
		Map<ModelList, Map<String, VCDocumentInfo>> latestPerModel = new EnumMap<>(ModelList.class);
		for (VCDocumentInfo info : infos) {
			Version version = info.getVersion();
			if (null == version || null == version.getDate() || null == version.getOwner()) continue;
			String model = DatabaseModelsPanel.modelKey(info);
			for (ModelList modelList : DatabaseModelsPanel.listsFor(info, user)) {
				latestPerModel.computeIfAbsent(modelList, list -> new HashMap<>())
						.merge(model, info, (a, b) -> a.getVersion().getDate().after(b.getVersion().getDate()) ? a : b);
			}
		}
		Comparator<VCDocumentInfo> byName = Comparator.comparing((VCDocumentInfo info) -> info.getVersion().getName(), String.CASE_INSENSITIVE_ORDER)
				.thenComparing(info -> ModelListRenderer.getDocumentType(info).ordinal());
		Map<ModelList, List<VCDocumentInfo>> lists = new EnumMap<>(ModelList.class);
		latestPerModel.forEach((modelList, models) -> lists.put(modelList, models.values().stream().sorted(byName).toList()));
		return lists;
	}

	/**
	 * Which lists a version belongs in, by the database tree's rules: the user's own, and those shared with chosen users
	 * by someone else; the tutorial users'; published (with a publication, and flagged Published) or curated (with a
	 * publication otherwise, or a public Model Brick); uncurated (public, with no publication, and not a tutorial's or a
	 * Model Brick).
	 */
	static Set<ModelList> listsFor(VCDocumentInfo info, User user) {
		Version version = info.getVersion();
		String owner = version.getOwner().getName();
		boolean mine = user.compareEqual(version.getOwner());
		BigDecimal access = null == version.getGroupAccess() ? GroupAccess.GROUPACCESS_NONE : version.getGroupAccess().getGroupid();
		boolean isPublic = GroupAccess.GROUPACCESS_ALL.equals(access);
		boolean isShared = !isPublic && !GroupAccess.GROUPACCESS_NONE.equals(access);
		PublicationInfo[] publications = info.getPublicationInfos();
		boolean hasPublication = null != publications && publications.length > 0;

		Set<ModelList> lists = EnumSet.noneOf(ModelList.class);
		if (mine) lists.add(ModelList.MINE);
		if (hasPublication) lists.add(null != version.getFlag() && version.getFlag().compareEqual(VersionFlag.Published) ? ModelList.PUBLISHED : ModelList.CURATED);
		if (DatabaseModelsPanel.TUTORIAL_OWNERS.contains(owner)) {
			lists.add(ModelList.TUTORIAL);
			return lists;
		}
		if (VCDocumentDbTreeModel.USER_modelBricks.equals(owner)) {
			if (isPublic) lists.add(ModelList.CURATED);
			if (isShared) lists.add(ModelList.MINE);
			return lists;
		}
		if (isPublic && !hasPublication) lists.add(ModelList.UNCURATED);
		if (isShared) lists.add(ModelList.MINE);
		return lists;
	}

	// Someone else's model, in the user's list: shared with them
	private boolean isSharedWithUser(VCDocumentInfo info) {
		User user = null == this.documentManager ? null : this.documentManager.getUser();
		return null != user && null != info.getVersion().getOwner() && !user.compareEqual(info.getVersion().getOwner());
	}

	private void openSelected(JList<VCDocumentInfo> list) {
		VCDocumentInfo selected = list.getSelectedValue();
		if (null != selected) this.opener.accept(selected);
	}

	private void refreshLater() {
		SwingUtilities.invokeLater(this::refresh);
	}

	// One list's models, scrolling, or a message when it has none
	private final class ModelListView extends JPanel {
		private static final String LIST = "list", MESSAGE = "message";
		private final DefaultListModel<VCDocumentInfo> listModel = new DefaultListModel<>();
		private final JList<VCDocumentInfo> list = new JList<>(this.listModel) {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true; // as wide as it shows: a long name ends in "...", rather than scrolling sideways
			}
		};
		private final ModelListRenderer renderer;
		private final JLabel message;
		private final CardLayout cards = new CardLayout();

		ModelListView(ModelList modelList) {
			super();
			this.setLayout(this.cards);
			this.setName(modelList.name() + "ModelsView");
			// whose, unless they are the user's own; among those, the ones shared with the user marked as such
			this.renderer = ModelList.MINE == modelList ? new ModelListRenderer(false, DatabaseModelsPanel.this::isSharedWithUser) : new ModelListRenderer(true);
			this.message = new JLabel(modelList.emptyMessage, SwingConstants.CENTER);
			this.message.setForeground(VCellColors.getDimForeground());
			this.list.setName(modelList.name() + "ModelsList");
			this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.list.setCellRenderer(this.renderer);
			ModelListRenderer.openOnDoubleClickOrEnter(this.list, () -> DatabaseModelsPanel.this.openSelected(this.list));
			ModelListRenderer.showMenuOnRightClick(this.list, () -> DatabaseModelsPanel.this.menuMaker);
			// every entry the same height, measured in the current font: quick to lay out, however long the list
			this.list.addPropertyChangeListener("font", e -> SwingUtilities.invokeLater(this::fixCellHeight));
			// always with its scroll bar, as Recent Models has: the same width however many match
			this.add(new JScrollPane(this.list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), LIST);
			this.add(this.message, MESSAGE);
		}

		void setModels(List<VCDocumentInfo> models, String emptyMessage) {
			this.message.setText(emptyMessage);
			this.cards.show(this, models.isEmpty() ? MESSAGE : LIST);
			if (models.equals(this.getModels())) return; // database events come often; keep the scroll and selection
			VCDocumentInfo selected = this.list.getSelectedValue();
			this.listModel.clear();
			this.listModel.addAll(models);
			if (null != selected) this.list.setSelectedValue(selected, false);
			this.fixCellHeight();
		}

		List<VCDocumentInfo> getModels() {
			return Collections.list(this.listModel.elements());
		}

		private void fixCellHeight() {
			if (this.listModel.isEmpty()) return;
			this.list.setFixedCellHeight(this.renderer.getListCellRendererComponent(this.list, this.listModel.get(0), 0, false, false).getPreferredSize().height);
		}
	}
}
