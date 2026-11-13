package cbit.vcell.desktop;

import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The models the user most recently saved to the VCell database, newest first: the latest version of each, of the
 * document types the panel is made for. Opening one (double-click, or Enter) hands it to whatever shows the panel.
 * <p>
 * It shows as many as fit in its height, and a "Show more" button when there are more; each press adds
 * {@value #SHOW_MORE_COUNT}, and from then on the list scrolls.
 * <p>
 * It lists what the database already told VCell about the user's models, so it needs a login and follows the user
 * between computers; it updates as models are saved.
 */
@SuppressWarnings("serial")
public class RecentModelsPanel extends JPanel {
	/** How many more models "Show more" adds. */
	public static final int SHOW_MORE_COUNT = 5;
	// How many it shows before it has a height to fit them to
	private static final int UNSIZED_COUNT = 10;

	private static final String LIST = "list", MESSAGE = "message";

	private final Set<VCDocumentType> documentTypes;
	private final DefaultListModel<VCDocumentInfo> listModel = new DefaultListModel<>();
	private final JList<VCDocumentInfo> list = new JList<>(this.listModel);
	// always with its scroll bar, so the list keeps its width as "Show more" brings in more than fit
	private final JScrollPane listScroller = new JScrollPane(this.list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	private final JLabel message = new JLabel("", SwingConstants.CENTER);
	private final CardLayout cards = new CardLayout();
	private final JPanel cardPanel = new JPanel(this.cards);
	private final JButton showMoreButton = new JButton("Show more");
	private final ModelListRenderer renderer = new ModelListRenderer(false);
	private List<VCDocumentInfo> allRecentModels = List.of(); // all of them, newest first
	private List<VCDocumentInfo> recentModels = List.of(); // those the search matches; the list shows the first few
	private String searchText = "";
	private SearchOptions searchOptions = SearchOptions.defaults();
	private int revealedByShowMore = 0; // how many "Show more" brought into the list; it shows at least that many
	private final DatabaseListener databaseListener = new DatabaseListener() {
		@Override public void databaseDelete(DatabaseEvent event) { RecentModelsPanel.this.refreshLater(); }
		@Override public void databaseInsert(DatabaseEvent event) { RecentModelsPanel.this.refreshLater(); }
		@Override public void databaseRefresh(DatabaseEvent event) { RecentModelsPanel.this.refreshLater(); }
		@Override public void databaseUpdate(DatabaseEvent event) { RecentModelsPanel.this.refreshLater(); }
	};
	private DocumentManager documentManager = null;
	private Consumer<VCDocumentInfo> opener = info -> {};
	private Function<VCDocumentInfo, JPopupMenu> menuMaker = null; // a listed model's right-click menu, if any

	/**
	 * A panel listing recently saved documents of these types; BioModels and MathModels if none are given (geometries
	 * are mostly saved along with a model, and would crowd the models out).
	 */
	public RecentModelsPanel(VCDocumentType... documentTypes) {
		super();
		this.documentTypes = 0 == documentTypes.length ? EnumSet.of(VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC)
				: EnumSet.copyOf(Arrays.asList(documentTypes));
		this.setName("RecentModelsPanel");
		this.setLayout(new BorderLayout(0, 4));
		this.list.setName("RecentModelsList");
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.setCellRenderer(this.renderer);
		ModelListRenderer.openOnDoubleClickOrEnter(this.list, this::openSelected);
		ModelListRenderer.showMenuOnRightClick(this.list, () -> this.menuMaker);
		this.message.setForeground(VCellColors.getDimForeground());
		this.cardPanel.add(this.listScroller, LIST);
		this.cardPanel.add(this.message, MESSAGE);
		this.showMoreButton.setName("RecentModelsShowMoreButton");
		this.showMoreButton.addActionListener(e -> this.showMore());
		this.add(this.cardPanel, BorderLayout.CENTER);
		this.add(this.showMoreButton, BorderLayout.SOUTH);
		// as many as fit, whatever the panel's size or the text size (a font change makes the entries taller or shorter)
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				RecentModelsPanel.this.showModels();
			}
		});
		this.list.addPropertyChangeListener("font", e -> SwingUtilities.invokeLater(this::showModels));
		this.refresh();
	}

	/** The document manager whose models it lists (null when no one is logged in); it follows that manager's changes. */
	public void setDocumentManager(DocumentManager documentManager) {
		if (null != this.documentManager) this.documentManager.removeDatabaseListener(this.databaseListener);
		this.documentManager = documentManager;
		if (null != this.documentManager) this.documentManager.addDatabaseListener(this.databaseListener);
		this.revealedByShowMore = 0; // a different user's models start from what fits
		this.refresh();
	}

	/** What to do with a model the user opens from the list. */
	public void setOpener(Consumer<VCDocumentInfo> opener) {
		this.opener = Objects.requireNonNull(opener);
	}

	/** What a listed model's right-click menu holds (null for none); it opens the menu for whichever model was clicked. */
	public void setMenuMaker(Function<VCDocumentInfo, JPopupMenu> menuMaker) {
		this.menuMaker = menuMaker;
	}

	/** Called whenever the list's selection changes. */
	public void addSelectionListener(Runnable listener) {
		this.list.addListSelectionListener(e -> listener.run());
	}

	/** The model selected in the list, or null. */
	public VCDocumentInfo getSelectedModel() {
		return this.list.getSelectedValue();
	}

	/** Lists the models again, from what the document manager knows now. */
	public void refresh() {
		User user = null == this.documentManager ? null : this.documentManager.getUser();
		this.allRecentModels = null == user ? List.of() : this.getRecentModels(user);
		this.recentModels = ModelSearch.search(this.allRecentModels, this.searchText, this.searchOptions, new Date()); // newest, if equal
		if (null == user) {
			this.showMessage("Log in to see the models you saved recently");
		} else if (this.allRecentModels.isEmpty()) {
			this.showMessage("No saved models yet");
		} else if (this.recentModels.isEmpty()) {
			this.showMessage(this.searchText.isBlank() ? "No recent models match the filters"
					: "No recent models match \u201C" + this.searchText.strip() + "\u201D");
		} else {
			this.cards.show(this.cardPanel, LIST);
		}
		this.showModels();
	}

	/** Whether the logged-in user has saved any models of the kinds it lists, whatever the search. */
	public boolean hasModels() {
		return !this.allRecentModels.isEmpty();
	}

	/** Lists only the models this search matches, best first (see ModelSearch); all of them, newest first, for none. */
	public void setSearchText(String searchText) {
		this.setSearch(searchText, this.searchOptions);
	}

	/** Lists only the models this search matches and its options let through, in the options' order (see ModelSearch). */
	public void setSearch(String searchText, SearchOptions options) {
		String text = null == searchText ? "" : searchText;
		if (text.strip().equals(this.searchText.strip()) && options.equals(this.searchOptions)) return;
		this.searchText = text;
		this.searchOptions = options;
		this.renderer.setSearch(text, options.getFields());
		this.revealedByShowMore = 0; // a new search starts from what fits
		this.refresh();
	}

	/** Shows {@value #SHOW_MORE_COUNT} more models than now, scrolling to the first of them. */
	public void showMore() {
		int firstNew = this.listModel.getSize();
		this.revealedByShowMore = firstNew + SHOW_MORE_COUNT;
		this.showModels();
		if (firstNew < this.listModel.getSize()) {
			this.list.ensureIndexIsVisible(Math.min(this.listModel.getSize() - 1, firstNew + SHOW_MORE_COUNT - 1));
			this.list.ensureIndexIsVisible(firstNew);
		}
	}

	/** How many models the list shows now. */
	int getShownCount() {
		return this.listModel.getSize();
	}

	/** Whether there are more models than the list shows, so "Show more" is offered. */
	boolean isShowMoreOffered() {
		return this.showMoreButton.isVisible();
	}

	/** The message shown instead of the list (logged out, or no models), or null while the list is shown. */
	String getMessageShown() {
		return this.message.isVisible() ? this.message.getText() : null;
	}

	JList<VCDocumentInfo> getList() {
		return this.list;
	}

	// Shows as many models as fit, or as many as "Show more" brought in if that is more (so a smaller panel scrolls
	// rather than dropping them); offers "Show more" while some are left out
	void showModels() {
		int count = Math.min(this.recentModels.size(), Math.max(this.countThatFit(), this.revealedByShowMore));
		if (count != this.listModel.getSize() || !this.recentModels.subList(0, count).equals(Collections.list(this.listModel.elements()))) {
			VCDocumentInfo selected = this.list.getSelectedValue();
			this.listModel.clear();
			for (VCDocumentInfo info : this.recentModels.subList(0, count)) this.listModel.addElement(info);
			if (null != selected) this.list.setSelectedValue(selected, false);
		}
		boolean more = count < this.recentModels.size();
		if (more != this.showMoreButton.isVisible()) {
			this.showMoreButton.setVisible(more);
			this.revalidate();
		}
	}

	// How many entries fit in the panel's height, leaving room for "Show more" if not all of them fit
	private int countThatFit() {
		if (this.recentModels.isEmpty()) return 0;
		int cellHeight = this.renderer.getListCellRendererComponent(this.list, this.recentModels.get(0), 0, false, false).getPreferredSize().height;
		this.list.setFixedCellHeight(cellHeight); // every entry the same height, measured in the current font
		Insets insets = this.getInsets(), scrollerInsets = this.listScroller.getInsets();
		int height = this.getHeight() - insets.top - insets.bottom - scrollerInsets.top - scrollerInsets.bottom;
		if (this.getHeight() <= 0 || cellHeight <= 0) return UNSIZED_COUNT;
		if (this.recentModels.size() * cellHeight <= height) return this.recentModels.size();
		int buttonHeight = this.showMoreButton.getPreferredSize().height + ((BorderLayout) this.getLayout()).getVgap();
		return Math.max(1, (height - buttonHeight) / cellHeight);
	}

	// The latest version of each of the user's models (a save makes a new version on the model's branch), newest first
	private List<VCDocumentInfo> getRecentModels(User user) {
		List<VCDocumentInfo> infos = new ArrayList<>();
		if (this.documentTypes.contains(VCDocumentType.BIOMODEL_DOC)) infos.addAll(Arrays.asList(this.documentManager.getBioModelInfos()));
		if (this.documentTypes.contains(VCDocumentType.MATHMODEL_DOC)) infos.addAll(Arrays.asList(this.documentManager.getMathModelInfos()));
		if (this.documentTypes.contains(VCDocumentType.GEOMETRY_DOC)) infos.addAll(Arrays.asList(this.documentManager.getGeometryInfos()));
		Map<String, VCDocumentInfo> latestPerModel = new HashMap<>();
		for (VCDocumentInfo info : infos) {
			Version version = info.getVersion();
			if (null == version || null == version.getDate() || !user.compareEqual(version.getOwner())) continue;
			String model = ModelListRenderer.getDocumentType(info) + ":" + (null == version.getBranchID() ? version.getVersionKey() : version.getBranchID());
			latestPerModel.merge(model, info, (a, b) -> a.getVersion().getDate().after(b.getVersion().getDate()) ? a : b);
		}
		return latestPerModel.values().stream()
				.sorted(Comparator.comparing((VCDocumentInfo info) -> info.getVersion().getDate()).reversed())
				.toList();
	}

	private void showMessage(String text) {
		this.message.setText(text);
		this.cards.show(this.cardPanel, MESSAGE);
	}

	private void openSelected() {
		VCDocumentInfo selected = this.list.getSelectedValue();
		if (null != selected) this.opener.accept(selected);
	}

	private void refreshLater() {
		SwingUtilities.invokeLater(this::refresh);
	}
}
