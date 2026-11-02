package cbit.vcell.desktop;

import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.gui.VCellIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

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
	private static final Map<VCDocumentType, String> TYPE_NAMES = Map.of(
			VCDocumentType.BIOMODEL_DOC, "BioModel", VCDocumentType.MATHMODEL_DOC, "MathModel", VCDocumentType.GEOMETRY_DOC, "Geometry");
	private static final Map<VCDocumentType, Icon> TYPE_ICONS = Map.of(
			VCDocumentType.BIOMODEL_DOC, Objects.requireNonNull(VCellIcons.makeIcon("/icons/biomodel.png")),
			VCDocumentType.MATHMODEL_DOC, Objects.requireNonNull(VCellIcons.makeIcon("/icons/mathModel.png")),
			VCDocumentType.GEOMETRY_DOC, Objects.requireNonNull(VCellIcons.makeIcon("/images/geometry2_16x16.gif")));

	private final Set<VCDocumentType> documentTypes;
	private final DefaultListModel<VCDocumentInfo> listModel = new DefaultListModel<>();
	private final JList<VCDocumentInfo> list = new JList<>(this.listModel);
	private final JScrollPane listScroller = new JScrollPane(this.list);
	private final JLabel message = new JLabel("", SwingConstants.CENTER);
	private final CardLayout cards = new CardLayout();
	private final JPanel cardPanel = new JPanel(this.cards);
	private final JButton showMoreButton = new JButton("Show more");
	private final RecentModelRenderer renderer = new RecentModelRenderer();
	private List<VCDocumentInfo> recentModels = List.of(); // all of them, newest first; the list shows the first few
	private int revealedByShowMore = 0; // how many "Show more" brought into the list; it shows at least that many
	private final DatabaseListener databaseListener = new DatabaseListener() {
		@Override public void databaseDelete(DatabaseEvent event) { RecentModelsPanel.this.refreshLater(); }
		@Override public void databaseInsert(DatabaseEvent event) { RecentModelsPanel.this.refreshLater(); }
		@Override public void databaseRefresh(DatabaseEvent event) { RecentModelsPanel.this.refreshLater(); }
		@Override public void databaseUpdate(DatabaseEvent event) { RecentModelsPanel.this.refreshLater(); }
	};
	private DocumentManager documentManager = null;
	private Consumer<VCDocumentInfo> opener = info -> {};

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
		this.list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (2 == e.getClickCount() && SwingUtilities.isLeftMouseButton(e) && this.isOnItem(e)) RecentModelsPanel.this.openSelected();
			}

			private boolean isOnItem(MouseEvent e) {
				int index = RecentModelsPanel.this.list.locationToIndex(e.getPoint());
				return index >= 0 && RecentModelsPanel.this.list.getCellBounds(index, index).contains(e.getPoint());
			}
		});
		this.list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openRecentModel");
		this.list.getActionMap().put("openRecentModel", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				RecentModelsPanel.this.openSelected();
			}
		});
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

	/** The model selected in the list, or null. */
	public VCDocumentInfo getSelectedModel() {
		return this.list.getSelectedValue();
	}

	/** Lists the models again, from what the document manager knows now. */
	public void refresh() {
		User user = null == this.documentManager ? null : this.documentManager.getUser();
		this.recentModels = null == user ? List.of() : this.getRecentModels(user);
		if (null == user) {
			this.showMessage("Log in to see the models you saved recently");
		} else if (this.recentModels.isEmpty()) {
			this.showMessage("No saved models yet");
		} else {
			this.cards.show(this.cardPanel, LIST);
		}
		this.showModels();
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
			String model = RecentModelsPanel.getDocumentType(info) + ":" + (null == version.getBranchID() ? version.getVersionKey() : version.getBranchID());
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

	private static VCDocumentType getDocumentType(VCDocumentInfo info) {
		if (info instanceof BioModelInfo) return VCDocumentType.BIOMODEL_DOC;
		if (info instanceof MathModelInfo) return VCDocumentType.MATHMODEL_DOC;
		return VCDocumentType.GEOMETRY_DOC;
	}

	// "Today, 2:05 PM", "Yesterday, 9:12 AM", or a date
	private static String describeDate(Date date) {
		LocalDate day = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate today = LocalDate.now();
		String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
		if (day.equals(today)) return "Today, " + time;
		if (day.equals(today.minusDays(1))) return "Yesterday, " + time;
		return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
	}

	// Each model on two lines: its icon and name, then what it is and when it was saved
	private static final class RecentModelRenderer extends JPanel implements ListCellRenderer<VCDocumentInfo> {
		private final JLabel name = new JLabel();
		private final JLabel details = new JLabel();

		RecentModelRenderer() {
			super(new BorderLayout(0, 1));
			this.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
			this.add(this.name, BorderLayout.NORTH);
			this.add(this.details, BorderLayout.SOUTH);
			this.details.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 0)); // under the name, past the icon
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends VCDocumentInfo> list, VCDocumentInfo info, int index, boolean selected, boolean focused) {
			VCDocumentType type = RecentModelsPanel.getDocumentType(info);
			Version version = info.getVersion();
			this.name.setText(version.getName());
			this.name.setIcon(TYPE_ICONS.get(type));
			this.details.setText(TYPE_NAMES.get(type) + "  ·  " + RecentModelsPanel.describeDate(version.getDate()));
			// read as each is drawn, so they follow the text size and theme
			this.name.setFont(VCellFonts.getFont(VCellFonts.Category.BODY));
			this.details.setFont(VCellFonts.getFont(VCellFonts.Category.CAPTION));
			this.setBackground(selected ? list.getSelectionBackground() : list.getBackground());
			this.name.setForeground(selected ? list.getSelectionForeground() : list.getForeground());
			this.details.setForeground(selected ? list.getSelectionForeground() : VCellColors.getDimForeground());
			String annotation = version.getAnnot();
			this.setToolTipText(null == annotation || annotation.isBlank() ? null : RecentModelsPanel.wrapForToolTip(annotation));
			return this;
		}
	}

	private static final int TOOL_TIP_LINE_LENGTH = 70; // characters

	/** The text as an HTML tooltip, its lines broken between words so none is longer than about 70 characters. */
	static String wrapForToolTip(String text) {
		StringBuilder html = new StringBuilder("<html>");
		String[] paragraphs = text.strip().split("\\R");
		for (int p = 0; p < paragraphs.length; p++) {
			if (p > 0) html.append("<br>");
			int lineLength = 0;
			for (String word : paragraphs[p].trim().split("\\s+")) {
				if (word.isEmpty()) continue;
				if (lineLength > 0 && lineLength + 1 + word.length() > TOOL_TIP_LINE_LENGTH) {
					html.append("<br>");
					lineLength = 0;
				} else if (lineLength > 0) {
					html.append(' ');
					lineLength++;
				}
				html.append(word.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
				lineLength += word.length();
			}
		}
		return html.append("</html>").toString();
	}
}
