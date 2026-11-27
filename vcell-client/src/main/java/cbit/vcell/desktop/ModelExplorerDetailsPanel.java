package cbit.vcell.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelChildSummary;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ShortcutsWizard;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.desktop.ModelExplorerTree.Folder;
import cbit.vcell.desktop.ModelExplorerTree.Model;
import cbit.vcell.desktop.ModelExplorerTree.Owner;
import cbit.vcell.desktop.ModelExplorerTree.Publication;
import cbit.vcell.geometry.GeometryInfo;

/**
 * What the Model Explorer shows of whatever is selected in its tree. A model (or one of its versions): its name and
 * kind, with a button to open it; then its Document (owner, when saved, permissions, VCell ID, versions, description),
 * its Model (a BioModel's applications with their geometries and simulations; a MathModel's kind, geometry and
 * simulations; a geometry's size), and its Publications (title, authors, citation and links). A publication: its
 * details and the models listed under it. A folder or owner: how many models it holds. A BioModels Database model: its
 * ID and page, and a button to import it.
 */
@SuppressWarnings("serial")
public class ModelExplorerDetailsPanel extends JPanel {
	private static final Icon PUBLICATION_ICON = VCellIcons.makeIcon("/icons/publishedPublication.png");
	private static final Icon BMDB_ICON = VCellIcons.makeIcon("/icons/biomodel.png");
	private static final String SELECTABLE = "ModelExplorerDetails.selectableText"; // marks text that Tab skips
	private static final String COPYABLE = "ModelExplorerDetails.copyable"; // marks what Select All and Copy take
	private static final String COPY_TEXT = "ModelExplorerDetails.copyText"; // what's copied of a link or note
	private static final String ROW_LABEL = "ModelExplorerDetails.rowLabel"; // a row's label: copied before its value
	private static final String SECTION_HEADING = "ModelExplorerDetails.sectionHeading"; // copied before its rows
	private static final String UNSELECTED_TEXT = "ModelExplorerDetails.unselectedText"; // a link or note's own text, while Select All marks it

	private final JPanel content = new JPanel(); // the sections, top to bottom
	private final JScrollPane scroll = new JScrollPane(new Stack(this.content));
	private Consumer<VCDocumentInfo> opener = info -> {};
	private Consumer<BioModelsNetModelInfo> importer = info -> {};
	private Consumer<VCDocumentInfo> selector = info -> {}; // shows a listed model in the tree
	private Object shown = null;
	private DefaultMutableTreeNode shownNode = null;
	private boolean allSelected = false; // Select All: every value, link and note is copied
	// the shown BioModel's reaction diagram: loaded once the selection settles, the loading wheel turning meanwhile
	private static final int DIAGRAM_HEIGHT = 300;
	private static final int LOADING_HEIGHT = DIAGRAM_HEIGHT / 3; // the wheel's room, until the diagram needs the rest
	private ModelLoader modelLoader = null;
	private BioModelInfo diagramFor = null; // the model whose diagram is wanted now; a load for another is dropped
	private javax.swing.Timer diagramPause = null; // the wait before loading, so stepping through the tree loads nothing
	private javax.swing.Timer wheelTurning = null;

	public ModelExplorerDetailsPanel() {
		super(new BorderLayout());
		this.setName("ModelExplorerDetails");
		this.content.setLayout(new BoxLayout(this.content, BoxLayout.Y_AXIS));
		this.content.setOpaque(false);
		this.content.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
		this.scroll.setBorder(BorderFactory.createEmptyBorder());
		this.scroll.getVerticalScrollBar().setUnitIncrement(16);
		this.add(this.scroll, BorderLayout.CENTER);
		// the text can be selected and copied, but isn't a Tab stop: Tab goes from button to link, not into each value
		this.setFocusTraversalPolicyProvider(true);
		this.setFocusTraversalPolicy(new javax.swing.LayoutFocusTraversalPolicy() {
			@Override
			protected boolean accept(Component component) {
				return super.accept(component) && !(component instanceof JComponent text && Boolean.TRUE.equals(text.getClientProperty(SELECTABLE)));
			}
		});
		// Select All and Copy, from anywhere in the details (the values pass them on, see selectable)
		ShortcutsWizard shortcuts = new ShortcutsWizard(this);
		shortcuts.configureSelectAll(shortcuts.createAction("Select All", AsynchClientTask.TASKTYPE_SWING_NONBLOCKING, e -> this.selectAllText()));
		shortcuts.configureCopy(shortcuts.createAction("Copy", AsynchClientTask.TASKTYPE_SWING_NONBLOCKING, e -> this.copySelected()));
		this.setFocusable(false); // the wizard makes it focusable; it isn't a Tab stop itself
		this.show(null, null);
	}

	// ---- selecting and copying: every value can be selected; Cmd/Ctrl-A selects all of them, and Cmd/Ctrl-C copies what's
	// highlighted, a line each

	// Text that can be selected (dragged across, double-clicked) and copied, with a right-click menu; skipped by Tab
	private void selectable(JTextArea text) {
		text.setFocusable(true); // a text area shows its selection only once it has had the focus
		text.putClientProperty(SELECTABLE, Boolean.TRUE);
		text.putClientProperty(COPYABLE, Boolean.TRUE);
		// Tab and Shift-Tab move on, as from any control, rather than staying in the text (a text area's own default)
		text.setFocusTraversalKeys(java.awt.KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		text.setFocusTraversalKeys(java.awt.KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
		// Select All and Copy are the whole panel's (see the constructor), not this one value's
		for (ShortcutsWizard.Shortcut shortcut : new ShortcutsWizard.Shortcut[] { ShortcutsWizard.Shortcut.SELECT_ALL, ShortcutsWizard.Shortcut.COPY }) {
			KeyStroke pressed = ShortcutsWizard.keyStrokeFor(shortcut);
			text.getInputMap(JComponent.WHEN_FOCUSED).put(pressed, "none");
			text.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(pressed.getKeyCode(), pressed.getModifiers(), true), "none");
		}
		text.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		text.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (this.showMenu(e)) return;
				if (SwingUtilities.isLeftMouseButton(e)) ModelExplorerDetailsPanel.this.clearSelections(text); // a new selection, here
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				this.showMenu(e); // the menu's trigger is on release on Windows
			}

			private boolean showMenu(MouseEvent e) {
				if (!e.isPopupTrigger()) return false;
				ModelExplorerDetailsPanel.this.copyMenu(text).show(text, e.getX(), e.getY());
				return true;
			}
		});
		text.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusLost(java.awt.event.FocusEvent e) { // gone elsewhere in the window: no selection left behind here
				if (e.isTemporary()) return; // a menu opening
				if (null != e.getOppositeComponent() && SwingUtilities.isDescendingFrom(e.getOppositeComponent(), ModelExplorerDetailsPanel.this)) return;
				ModelExplorerDetailsPanel.this.clearSelections(null);
			}
		});
	}

	// Something to copy that can't be selected (a link, a note, the kind of model): copied with the rest by Select All
	private static void copyable(JComponent component, String text) {
		component.putClientProperty(COPYABLE, Boolean.TRUE);
		component.putClientProperty(COPY_TEXT, text);
	}

	private javax.swing.JPopupMenu copyMenu(JTextArea text) {
		javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
		menu.setName("ModelExplorerDetailsCopyMenu");
		javax.swing.JMenuItem copy = new javax.swing.JMenuItem("Copy");
		copy.setName("CopyMenuItem");
		copy.setAccelerator(ShortcutsWizard.keyStrokeFor(ShortcutsWizard.Shortcut.COPY));
		copy.addActionListener(e -> {
			if (this.copyLines().isEmpty()) { // nothing selected: this whole value
				this.clearSelections(text);
				text.selectAll();
			}
			this.copySelected();
		});
		javax.swing.JMenuItem selectAll = new javax.swing.JMenuItem("Select All");
		selectAll.setName("SelectAllMenuItem");
		selectAll.setAccelerator(ShortcutsWizard.keyStrokeFor(ShortcutsWizard.Shortcut.SELECT_ALL));
		selectAll.addActionListener(e -> this.selectAllText());
		menu.add(copy);
		menu.add(selectAll);
		return menu;
	}

	/** Selects every value shown (and marks the labels, headings, links, notes and kind of model, copied with them). */
	public void selectAllText() {
		this.allSelected = true;
		for (JComponent component : this.highlightables()) {
			if (component instanceof JTextArea text) {
				text.selectAll();
				text.getCaret().setSelectionVisible(true); // shown though only one value can have the focus
			} else if (component instanceof JLabel label && null == label.getClientProperty(UNSELECTED_TEXT)) { // a link or note: its text marked too
				label.putClientProperty(UNSELECTED_TEXT, label.getText());
				label.setText(ModelExplorerDetailsPanel.highlighted(label.getText()));
			}
		}
	}

	/** Copies what's selected: see {@link #copyLines()}. */
	public void copySelected() {
		List<String> lines = this.copyLines();
		if (lines.isEmpty()) return;
		java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(String.join(System.lineSeparator(), lines));
		java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
	}

	/**
	 * What's highlighted, as the lines to copy, top to bottom. One value's selection: just its text. After Select All,
	 * every line of the details: the title and kind of model, then each section's heading and its rows, "Label: value"
	 * (the value alone, for a row without a label: a note, a publication's title).
	 */
	List<String> copyLines() {
		List<String> lines = new ArrayList<>();
		if (!this.allSelected) {
			for (JComponent component : this.copyables()) {
				if (component instanceof JTextArea area && null != area.getSelectedText() && !area.getSelectedText().isBlank()) lines.add(area.getSelectedText());
			}
			return lines;
		}
		String rowLabel = null; // the label of the row whose value comes next
		for (JComponent component : this.highlightables()) {
			String text = component instanceof JTextArea area ? area.getText() : (String) component.getClientProperty(COPY_TEXT);
			if (null == text || text.isBlank()) continue;
			text = text.strip();
			if (Boolean.TRUE.equals(component.getClientProperty(SECTION_HEADING))) {
				if (!lines.isEmpty()) lines.add("");
				lines.add(text);
			} else if (Boolean.TRUE.equals(component.getClientProperty(ROW_LABEL))) {
				rowLabel = text;
			} else {
				lines.add(null == rowLabel ? text : rowLabel + ": " + text);
				rowLabel = null;
			}
		}
		return lines;
	}

	// Clears every value's selection but this one's (null: all), and Select All's marks on the labels, links and notes
	private void clearSelections(JTextArea keep) {
		this.allSelected = false;
		for (JComponent component : this.highlightables()) {
			if (component == keep) continue;
			if (component instanceof JTextArea text) {
				text.select(0, 0);
			} else if (component instanceof JLabel label && null != label.getClientProperty(UNSELECTED_TEXT)) {
				label.setText((String) label.getClientProperty(UNSELECTED_TEXT));
				label.putClientProperty(UNSELECTED_TEXT, null);
			}
		}
	}

	// A label's text with the background of selected text that hasn't the focus, as the values not focused show theirs
	private static String highlighted(String text) {
		java.awt.Color color = UIManager.getColor("TextArea.selectionInactiveBackground");
		if (null == color) color = UIManager.getColor("TextComponent.selectionBackgroundInactive");
		if (null == color) color = UIManager.getColor("TextArea.selectionBackground");
		String background = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		String inner = text.startsWith("<html>") ? text.substring(6, text.length() - (text.endsWith("</html>") ? 7 : 0))
				: text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
		return "<html><span style='background-color:" + background + "'>" + inner + "</span></html>";
	}

	// What can be copied, top to bottom
	private List<JComponent> copyables() {
		List<JComponent> found = new ArrayList<>();
		ModelExplorerDetailsPanel.collectCopyables(this.content, found);
		return found;
	}

	// What Select All marks, top to bottom: what can be copied, and the row labels and section headings that go with it
	private List<JComponent> highlightables() {
		List<JComponent> found = new ArrayList<>();
		ModelExplorerDetailsPanel.collect(this.content, found, true);
		return found;
	}

	private static void collectCopyables(java.awt.Container container, List<JComponent> found) {
		ModelExplorerDetailsPanel.collect(container, found, false);
	}

	private static void collect(java.awt.Container container, List<JComponent> found, boolean withLabels) {
		for (Component child : container.getComponents()) {
			if (child instanceof JComponent component && (Boolean.TRUE.equals(component.getClientProperty(COPYABLE))
					|| withLabels && (Boolean.TRUE.equals(component.getClientProperty(ROW_LABEL)) || Boolean.TRUE.equals(component.getClientProperty(SECTION_HEADING))))) {
				found.add(component);
			}
			if (child instanceof java.awt.Container inner) ModelExplorerDetailsPanel.collect(inner, found, withLabels);
		}
	}

	/**
	 * Loads a saved BioModel, whole (not just its summary), for its reaction diagram: off the event thread, then
	 * {@code done} or {@code failed} (with why) on it.
	 */
	public interface ModelLoader {
		void load(BioModelInfo info, Consumer<cbit.vcell.biomodel.BioModel> done, Consumer<String> failed);

		/** Why no model can be loaded now, shown in the diagram's place without trying (null: they can be). */
		default String unavailable() { return null; }
	}

	/** How a BioModel's reaction diagram is loaded (none: no Reaction Diagram section). */
	public void setModelLoader(ModelLoader modelLoader) { this.modelLoader = modelLoader; }

	/** What Open does with a model. */
	public void setOpener(Consumer<VCDocumentInfo> opener) { this.opener = opener; }

	/** What Import does with a BioModels Database model. */
	public void setImporter(Consumer<BioModelsNetModelInfo> importer) { this.importer = importer; }

	/** What clicking a model listed under a publication does: shows it in the tree. */
	public void setSelector(Consumer<VCDocumentInfo> selector) { this.selector = selector; }

	@Override
	public void updateUI() { // colors and fonts are the theme's: build it again in the new one
		super.updateUI();
		if (null != this.content) this.show(this.shown, this.shownNode);
	}

	/** Shows the details of a tree node's object (null: a prompt to select something). */
	public void show(Object object, DefaultMutableTreeNode node) {
		this.shown = object;
		this.shownNode = node;
		this.allSelected = false;
		this.stopDiagramLoading();
		this.content.removeAll();
		if (object instanceof Model model) this.showDocument(model.latest(), model.versions());
		else if (object instanceof VCDocumentInfo info) this.showDocument(info, null);
		else if (object instanceof Publication publication) this.showPublication(publication, node);
		else if (object instanceof Folder folder) this.showFolder(folder);
		else if (object instanceof Owner owner) this.showOwner(owner);
		else if (object instanceof BioModelsNetModelInfo info) this.showBioModelsDatabase(info);
		else this.showNothing();
		this.content.revalidate();
		this.content.repaint();
		// from the top, once laid out (asked before then, a taller model's details could open part-way down)
		SwingUtilities.invokeLater(() -> this.scroll.getViewport().setViewPosition(new java.awt.Point(0, 0)));
	}

	// ---- what each shows

	private void showNothing() {
		JLabel prompt = new JLabel("<html><div style='text-align:center'>Select a model to see its details.<br>Double-click one to open it.</div></html>", SwingConstants.CENTER);
		prompt.setName("ModelExplorerDetailsPrompt");
		prompt.setForeground(VCellColors.getDimForeground());
		prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
		prompt.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
		JPanel centered = new JPanel(new BorderLayout());
		centered.setOpaque(false);
		centered.add(prompt, BorderLayout.CENTER);
		this.content.add(centered);
	}

	private void showDocument(VCDocumentInfo info, List<VCDocumentInfo> versions) {
		Version version = info.getVersion();
		VCDocumentType type = ModelListRenderer.getDocumentType(info);
		String typeName = ModelListRenderer.typeName(type);
		JButton open = new JButton("Open " + typeName);
		open.setName("ModelExplorerOpen");
		open.setToolTipText("Opens this " + typeName + (null != versions && versions.size() > 1 ? " (its latest version)" : "") + " in a window of its own");
		open.addActionListener(e -> this.opener.accept(info));
		String subtitle = typeName + (null != version.getDate() ? ", saved " + ModelListRenderer.describeDate(version.getDate()) : "");
		// a BioModel's reaction diagram first, under its name: what the model is, at a glance; Open sits over it, in its
		// title row, rather than a second Open there and one in the header
		boolean diagram = info instanceof BioModelInfo && null != this.modelLoader;
		this.content.add(this.header(ModelListRenderer.iconFor(type), version.getName(), subtitle, diagram ? null : open));
		if (diagram) this.content.add(this.reactionDiagram((BioModelInfo) info, open));

		Rows document = this.section("Document");
		document.add("Owner", null == version.getOwner() ? "" : version.getOwner().getName());
		if (null != version.getDate()) document.add("Saved", DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(version.getDate()));
		document.add("Permissions", ModelListRenderer.permissionsOf(info));
		if (null != version.getVersionKey()) document.add("VCell ID", version.getVersionKey().toString());
		if (null != versions && versions.size() > 1) {
			document.add("Versions",  Integer.toString(versions.size()));
		}
		if (null != info.getSoftwareVersion()) document.add("Saved with", "VCell " + info.getSoftwareVersion().getSoftwareVersionString());
		String annotation = version.getAnnot();
		document.addText("Description", null == annotation || annotation.isBlank() ? null : annotation.strip());

		Rows model = this.section(typeName);
		if (info instanceof BioModelInfo bioModel) this.describe(bioModel, model);
		else if (info instanceof MathModelInfo mathModel) this.describe(mathModel, model);
		else if (info instanceof GeometryInfo geometry) this.describe(geometry, model);

		Rows publications = this.section("Publications");
		PublicationInfo[] infos = info.getPublicationInfos();
		if (null == infos || infos.length == 0) publications.addNote("Not published with a paper");
		else for (PublicationInfo publication : infos) this.describe(publication, publications, true);
	}

	private void describe(BioModelInfo info, Rows rows) {
		BioModelChildSummary summary = info.getBioModelChildSummary();
		String[] applications = null == summary ? null : summary.getSimulationContextNames();
		if (null == applications) {
			rows.addNote("Its applications aren't known until it's opened");
			return;
		}
		if (applications.length == 0) {
			rows.addNote("No applications: a physiology only");
			return;
		}
		rows.add("Applications", String.valueOf(applications.length));
		// every application has a named geometry: none named means the list didn't carry geometries
		boolean geometriesKnown = !ModelExplorerDetailsPanel.blank(summary.getGeometryNames());
		int[] dimensions = summary.getGeometryDimensions();
		// the server's list has the simulations' keys, but not their names, nor which application each is in
		boolean simulationsKnown = false;
		for (String application : applications) {
			String[] names = summary.getSimulationNames(application);
			simulationsKnown |= null != names && names.length > 0;
		}
		String[] simulationKeys = null == summary.getSimKeys() ? new String[0] : summary.getSimKeys();
		for (int i = 0; i < applications.length; i++) {
			MathType type = null != summary.getAppTypes() && i < summary.getAppTypes().length ? summary.getAppTypes()[i] : null;
			int dimension = geometriesKnown && null != dimensions && i < dimensions.length ? dimensions[i] : -1;
			List<String> about = new ArrayList<>();
			if (null != type) about.add(type.name() + (dimension < 0 ? "" : dimension > 0 ? ", spatial" : ", non-spatial"));
			if (geometriesKnown) about.add(ModelExplorerDetailsPanel.geometry(summary.getGeometryNames()[i], dimension));
			if (simulationsKnown || 0 == simulationKeys.length) about.add(ModelExplorerDetailsPanel.simulations(summary.getSimulationNames(applications[i])));
			rows.addText(applications[i], about.isEmpty() ? null : String.join("\n", about), ModelExplorerDetailsPanel.applicationIcon(type, dimension));
		}
		if (!simulationsKnown && simulationKeys.length > 0) {
			rows.add("Simulations", String.valueOf(simulationKeys.length));
			rows.addNote(geometriesKnown ? "Open this model to see its simulations' names and applications"
					: "Open this model to see its geometries, and its simulations' names and applications");
		} else if (!geometriesKnown) {
			rows.addNote("Open this model to see its geometries");
		}
	}

	/**
	 * An application's icon, as VCell's database tree has always drawn it: its modeling approach, spatial (a geometry of
	 * one dimension or more) or not; null if neither is known.
	 */
	static Icon applicationIcon(MathType type, int dimension) {
		if (null == type) return null;
		boolean spatial = dimension > 0;
		return switch (type) {
			case RuleBased -> VCellIcons.appRbmNonspIcon;
			case SpringSaLaD -> VCellIcons.appSpringSaLaDSpatialIcon;
			case Stochastic -> dimension < 0 ? null : spatial ? VCellIcons.appStoSpatialIcon : VCellIcons.appStoNonspIcon;
			case Deterministic -> dimension < 0 ? null : spatial ? VCellIcons.appDetSpatialIcon : VCellIcons.appDetNonspIcon;
			default -> null;
		};
	}

	private void describe(MathModelInfo info, Rows rows) {
		MathModelChildSummary summary = info.getMathModelChildSummary();
		if (null == summary) {
			rows.addNote("Its details aren't known until it's opened");
			return;
		}
		if (null != summary.getModelType()) rows.add("Kind", summary.getModelType().name());
		if (ModelExplorerDetailsPanel.blank(new String[] {summary.getGeometryName()})) {
			rows.addNote("Its geometry and simulations aren't in the database's list; open it to see them");
			return;
		}
		rows.add("Geometry", ModelExplorerDetailsPanel.geometry(summary.getGeometryName(), summary.getGeometryDimension()));
		rows.addText("Simulations", ModelExplorerDetailsPanel.simulations(summary.getSimulationNames()));
	}

	private void describe(GeometryInfo info, Rows rows) {
		rows.add("Dimension", 0 == info.getDimension() ? "Compartmental" : info.getDimension() + "D");
		if (null != info.getExtent()) rows.add("Size", info.getExtent().getX() + " × " + info.getExtent().getY() + " × " + info.getExtent().getZ() + " µm");
		if (null != info.getOrigin()) rows.add("Origin", "(" + info.getOrigin().getX() + ", " + info.getOrigin().getY() + ", " + info.getOrigin().getZ() + ")");
		rows.add("Defined by", null == info.getImageRef() ? "Analytic shapes" : "An image");
	}

	private void describe(PublicationInfo publication, Rows rows, boolean titled) {
		if (titled) rows.addHeading(null == publication.getTitle() || publication.getTitle().isBlank() ? "Untitled publication" : publication.getTitle().strip());
		String[] authors = publication.getAuthors();
		if (null != authors && authors.length > 0) rows.addText("Authors", String.join(", ", authors));
		if (null != publication.getCitation() && !publication.getCitation().isBlank()) rows.addText("Citation", publication.getCitation().strip());
		if (null != publication.getPubDate()) rows.add("Published", DateFormat.getDateInstance(DateFormat.LONG).format(publication.getPubDate()));
		if (ModelExplorerDetailsPanel.present(publication.getDoi())) {
			String doi = publication.getDoi().strip();
			rows.addLink("DOI", doi, doi.startsWith("http") ? doi : "https://doi.org/" + doi);
		}
		if (ModelExplorerDetailsPanel.present(publication.getPubmedid())) {
			String id = publication.getPubmedid().strip();
			rows.addLink("PubMed", id, "https://pubmed.ncbi.nlm.nih.gov/" + id);
		}
		if (ModelExplorerDetailsPanel.present(publication.getUrl())) rows.addLink("Web page", publication.getUrl().strip(), publication.getUrl().strip());
	}

	private void showPublication(Publication publication, DefaultMutableTreeNode node) {
		PublicationInfo info = publication.info();
		this.content.add(this.header(ModelExplorerDetailsPanel.PUBLICATION_ICON,
				null == info.getTitle() || info.getTitle().isBlank() ? "Untitled publication" : info.getTitle().strip(), "Publication", null));
		this.describe(info, this.section("Publication"), false);
		Rows models = this.section("Models (" + publication.count() + ")");
		for (VCDocumentInfo model : null == node ? List.<VCDocumentInfo>of() : ModelExplorerTree.modelsUnder(node)) models.addModel(model);
	}

	private void showFolder(Folder folder) {
		this.content.add(this.header(ModelExplorerTreeRenderer.folderIcon(folder.getSection()), folder.getSection().title,
				null != folder.getNote() ? folder.getNote() : ModelExplorerDetailsPanel.count(folder.getCount(), folder.getSection()), null));
		Rows about = this.section("About");
		about.addText(null, ModelExplorerDetailsPanel.aboutFolder(folder.getSection()));
	}

	private void showOwner(Owner owner) {
		this.content.add(this.header(VCellIcons.makeIcon("/icons/aUser.png"), owner.name(), NumberFormat.getIntegerInstance().format(owner.count())
				+ (owner.count() == 1 ? " model listed" : " models listed"), null));
	}

	private void showBioModelsDatabase(BioModelsNetModelInfo info) {
		JButton importButton = new JButton("Import");
		importButton.setName("ModelExplorerImport");
		importButton.setToolTipText(info.isSupported() ? "Downloads this model's SBML and opens it as a new BioModel" : "VCell can't import this model");
		importButton.setEnabled(info.isSupported());
		importButton.addActionListener(e -> this.importer.accept(info));
		this.content.add(this.header(ModelExplorerDetailsPanel.BMDB_ICON, info.getName(), "BioModels Database (EBI)", importButton));
		Rows document = this.section("Document");
		document.add("BioModels ID", info.getId());
		if (ModelExplorerDetailsPanel.present(info.getLink())) document.addLink("Web page", info.getLink(), info.getLink());
		document.add("In VCell", info.isSupported() ? "Can be imported, as a new BioModel from its SBML" : "Not supported: VCell can't import it");
	}

	// ---- wording

	private static String geometry(String name, int dimension) {
		String shape = dimension < 0 ? "" : 0 == dimension ? "compartmental" : dimension + "D";
		if (null == name || name.isBlank()) return shape.isEmpty() ? "No geometry" : "Geometry: " + shape;
		return "Geometry: " + name + (shape.isEmpty() ? "" : " (" + shape + ")");
	}

	private static String simulations(String[] names) {
		if (null == names || names.length == 0) return "No simulations";
		return names.length + (names.length == 1 ? " simulation: " : " simulations: ") + String.join(", ", names);
	}

	private static String count(int count, ModelExplorerTree.Section section) {
		boolean geometries = section.name().contains("GEOMETR");
		String noun = geometries ? (count == 1 ? "geometry" : "geometries") : (count == 1 ? "model" : "models");
		return NumberFormat.getIntegerInstance().format(count) + " " + noun + " listed";
	}

	private static String aboutFolder(ModelExplorerTree.Section section) {
		return switch (section) {
			case MY_MODELS -> "The BioModels and MathModels you saved, each with its versions.";
			case SHARED -> "Models others have shared with you, by owner.";
			case PUBLIC -> "Models anyone can see: published, curated, and the rest (uncurated).";
			case PUBLISHED -> "Models published with a paper, by publication, newest first.";
			case CURATED -> "Models curated by the VCell team, by publication, newest first; and the Model Bricks, reusable model parts.";
			case MODEL_BRICKS -> "Model Bricks: small curated models, made to be reused as parts of others.";
			case UNCURATED -> "Public models that aren't published or curated, by owner.";
			case TUTORIALS -> "The models VCell's tutorials use.";
			case GEOMETRIES -> "Geometries saved to the VCell database: your own, those shared with you, and public ones.";
			case MY_GEOMETRIES -> "The geometries you saved.";
			case SHARED_GEOMETRIES -> "Geometries others have shared with you, by owner.";
			case PUBLIC_GEOMETRIES -> "Geometries anyone can see, by owner.";
			case BIOMODELS_DATABASE -> "EBI's BioModels Database. Its models are SBML: importing one makes a new BioModel. The search looks at their names and IDs only.";
		};
	}

	// no names at all (missing, or all blank)
	private static boolean blank(String[] names) {
		if (null == names) return true;
		for (String name : names) if (ModelExplorerDetailsPanel.present(name)) return false;
		return true;
	}

	private static boolean present(String text) {
		return null != text && !text.isBlank();
	}

	// ---- building blocks

	// The name in the title font, with its icon, what it is under it, and a button at the right
	// ---- the reaction diagram

	// A section of its own, a fixed height: the loading wheel until the model is loaded, then its reaction diagram
	private JComponent reactionDiagram(BioModelInfo info, JButton open) {
		JPanel section = new JPanel(new BorderLayout());
		section.setName("ModelExplorerReactionDiagram");
		section.setOpaque(false);
		section.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); // close under the model's name
		section.setAlignmentX(Component.LEFT_ALIGNMENT);
		ModelExplorerDetailsPanel.setSectionHeight(section, LOADING_HEIGHT);

		org.vcell.util.gui.LoadingWheel wheel = new org.vcell.util.gui.LoadingWheel(40);
		JLabel loading = new JLabel("Loading the model's reaction diagram", wheel, SwingConstants.CENTER);
		loading.setName("ModelExplorerReactionDiagramLoading");
		loading.setHorizontalTextPosition(SwingConstants.CENTER);
		loading.setVerticalTextPosition(SwingConstants.BOTTOM);
		loading.setForeground(VCellColors.getDimForeground());
		JPanel titleRow = new JPanel(new BorderLayout());
		titleRow.setOpaque(false);
		titleRow.add(GuiUtils.createBoldJLabel("Reaction Diagram"), BorderLayout.WEST);
		titleRow.add(open, BorderLayout.EAST); // the model's Open: at the top of the diagram, which opening shows
		section.add(titleRow, BorderLayout.NORTH);
		String unavailable = this.modelLoader.unavailable();
		if (null != unavailable) { // known not to load (a guest): say why, rather than trying and showing an error
			JLabel warning = new JLabel("<html><div style='text-align:center'>" + unavailable + "</div></html>", SwingConstants.CENTER);
			warning.setName("ModelExplorerReactionDiagramUnavailable");
			warning.setForeground(VCellColors.getWarningForeground());
			ModelExplorerDetailsPanel.copyable(warning, unavailable);
			section.add(warning, BorderLayout.CENTER);
			return section;
		}
		section.add(loading, BorderLayout.CENTER);
		this.wheelTurning = wheel.animate(loading);
		this.wheelTurning.start();

		this.diagramFor = info;
		this.diagramPause = new javax.swing.Timer(350, e -> this.modelLoader.load(info,
				model -> this.showReactionDiagram(section, info, model),
				why -> this.showDiagramProblem(section, info, why)));
		this.diagramPause.setRepeats(false);
		this.diagramPause.start();
		return section;
	}

	// The loaded model's reaction diagram in the section, if it's still the model shown
	private void showReactionDiagram(JPanel section, BioModelInfo info, cbit.vcell.biomodel.BioModel bioModel) {
		if (info != this.diagramFor || !SwingUtilities.isDescendingFrom(section, this)) return; // moved on meanwhile
		this.stopDiagramLoading();
		// as the model window's Reaction Diagram first shows a model: grouped by molecule when it has reaction rules
		boolean rules = !bioModel.getModel().getRbmModelContainer().getReactionRuleList().isEmpty();
		cbit.vcell.graph.ReactionCartoon reactions = rules ? new cbit.vcell.graph.ReactionCartoonMolecule() : new cbit.vcell.graph.ReactionCartoonFull();
		reactions.setStructureSuite(new cbit.vcell.graph.structures.AllStructureSuite(bioModel::getModel));
		reactions.setModel(bioModel.getModel());
		cbit.vcell.client.desktop.biomodel.DiagramPreview preview = new cbit.vcell.client.desktop.biomodel.DiagramPreview(
				"ModelExplorerReactionDiagramPreview", "Reaction Diagram", reactions, () -> this.opener.accept(info),
				"No structures", () -> bioModel.getModel().getStructures().length == 0);
		preview.setOpaque(false);
		preview.setTitleShown(false); // the section's own title row, with the model's Open, stays over it
		// the details stay where they were scrolled to: the diagram arriving doesn't scroll them (its pane would, to show itself)
		java.awt.Point scrolledTo = this.scroll.getViewport().getViewPosition();
		Component loading = ((BorderLayout) section.getLayout()).getLayoutComponent(BorderLayout.CENTER);
		if (null != loading) section.remove(loading);
		section.add(preview, BorderLayout.CENTER);
		ModelExplorerDetailsPanel.setSectionHeight(section, DIAGRAM_HEIGHT); // the diagram's room, now it's here
		section.revalidate();
		section.repaint();
		preview.refresh(null); // its positions are the model's own, as saved
		SwingUtilities.invokeLater(() -> SwingUtilities.invokeLater(() -> this.scroll.getViewport().setViewPosition(scrolledTo))); // after its fit
	}

	private void showDiagramProblem(JPanel section, BioModelInfo info, String why) {
		if (info != this.diagramFor || !SwingUtilities.isDescendingFrom(section, this)) return;
		this.stopDiagramLoading();
		BorderLayout layout = (BorderLayout) section.getLayout();
		Component loading = layout.getLayoutComponent(BorderLayout.CENTER);
		if (null != loading) section.remove(loading);
		JLabel problem = new JLabel("Couldn't load its reaction diagram" + (null == why || why.isBlank() ? "" : ": " + why), SwingConstants.CENTER);
		problem.setForeground(VCellColors.getDimForeground());
		section.add(problem, BorderLayout.CENTER);
		section.revalidate();
		section.repaint();
	}

	private static void setSectionHeight(JPanel section, int height) {
		section.setPreferredSize(new Dimension(100, height));
		section.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
	}

	// Stops waiting to load, and the wheel turning: the model shown has changed, or its diagram is here
	private void stopDiagramLoading() {
		if (null != this.diagramPause) this.diagramPause.stop();
		if (null != this.wheelTurning) this.wheelTurning.stop();
		this.diagramPause = null;
		this.wheelTurning = null;
	}

	// "⌘C" on macOS, "Ctrl+C" elsewhere
	private static String shortcutText(ShortcutsWizard.Shortcut shortcut) {
		KeyStroke stroke = ShortcutsWizard.keyStrokeFor(shortcut);
		String modifiers = java.awt.event.InputEvent.getModifiersExText(stroke.getModifiers());
		return modifiers + (modifiers.endsWith("+") || modifiers.length() == 1 ? "" : "+") + java.awt.event.KeyEvent.getKeyText(stroke.getKeyCode());
	}

	private JComponent header(Icon icon, String title, String subtitle, JButton action) {
		// the name wraps, rather than ending in "...": a publication's title is often long
		JTextArea name = new JTextArea(title);
		name.setName("ModelExplorerDetailsTitle");
		name.setEditable(false);
		name.setLineWrap(true);
		name.setWrapStyleWord(true);
		name.setOpaque(false);
		name.setBorder(BorderFactory.createEmptyBorder());
		name.setFont(UIManager.getFont("Label.font"));
		this.selectable(name);
		VCellFonts.setCategory(name, VCellFonts.Category.TITLE);
		JLabel iconLabel = new JLabel(icon);
		iconLabel.setVerticalAlignment(SwingConstants.TOP);
		iconLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		JLabel kind = new JLabel(subtitle);
		ModelExplorerDetailsPanel.copyable(kind, subtitle);
		kind.setForeground(VCellColors.getDimForeground());
		JPanel text = new JPanel(new BorderLayout(0, 2));
		text.setOpaque(false);
		text.add(name, BorderLayout.NORTH);
		text.add(kind, BorderLayout.SOUTH);
		JPanel named = new JPanel(new BorderLayout(8, 0));
		named.setOpaque(false);
		named.add(iconLabel, BorderLayout.WEST);
		named.add(text, BorderLayout.CENTER);
		JPanel header = new JPanel(new BorderLayout(12, 0));
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
		header.add(named, BorderLayout.CENTER);
		// Select All, for copying all of the details (Cmd/Ctrl-C, or Copy in a value's right-click menu); then its own action
		JButton selectAll = new JButton("Select All");
		selectAll.setName("ModelExplorerSelectAll");
		selectAll.setToolTipText("Selects all of these details, to copy them (" + ModelExplorerDetailsPanel.shortcutText(ShortcutsWizard.Shortcut.COPY) + ")");
		selectAll.addActionListener(e -> this.selectAllText());
		JPanel holder = new JPanel(new FlowLayout(FlowLayout.TRAILING, 6, 0));
		holder.setOpaque(false);
		holder.add(selectAll);
		if (null != action) holder.add(action);
		header.add(holder, BorderLayout.EAST);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		return header;
	}

	// A titled section, its rows of a label and a value added as they come
	private Rows section(String title) {
		JLabel heading = new JLabel(title);
		heading.putClientProperty(SECTION_HEADING, Boolean.TRUE);
		heading.putClientProperty(COPY_TEXT, title);
		VCellFonts.setCategory(heading, VCellFonts.Category.HEADING);
		heading.setForeground(VCellColors.getTitleForeground());
		JPanel titleRow = new JPanel(new BorderLayout(8, 0));
		titleRow.setOpaque(false);
		titleRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
		titleRow.add(heading, BorderLayout.WEST);
		JSeparator line = new JSeparator();
		JPanel lineHolder = new JPanel(new GridBagLayout()); // the line through the heading's middle
		lineHolder.setOpaque(false);
		GridBagConstraints lineAt = new GridBagConstraints();
		lineAt.weightx = 1;
		lineAt.fill = GridBagConstraints.HORIZONTAL;
		lineHolder.add(line, lineAt);
		titleRow.add(lineHolder, BorderLayout.CENTER);
		titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.content.add(titleRow);
		Rows rows = new Rows();
		rows.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.content.add(rows);
		return rows;
	}

	/** A section's rows: a label, and its value, which wraps. */
	private final class Rows extends JPanel {
		private int row = 0;

		Rows() {
			super(new GridBagLayout());
			this.setOpaque(false);
			this.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		}

		void add(String label, String value) {
			this.addText(label, value);
		}

		void addText(String label, String value) {
			this.addText(label, value, null);
		}

		// As above, the label with an icon before it (an application's, say)
		void addText(String label, String value, Icon icon) {
			JTextArea text = new JTextArea(null == value ? "None" : value);
			text.setEditable(false);
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			text.setOpaque(false);
			text.setBorder(BorderFactory.createEmptyBorder());
			text.setFont(UIManager.getFont("Label.font"));
			ModelExplorerDetailsPanel.this.selectable(text);
			if (null == value) text.setForeground(VCellColors.getDimForeground());
			this.addRow(label, text, icon);
		}

		void addNote(String note) {
			JLabel label = new JLabel(note);
			ModelExplorerDetailsPanel.copyable(label, note);
			label.setForeground(VCellColors.getDimForeground());
			this.addRow(null, label);
		}

		void addHeading(String heading) {
			JTextArea text = new JTextArea(heading);
			text.setEditable(false);
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			text.setOpaque(false);
			text.setBorder(BorderFactory.createEmptyBorder(this.row > 0 ? 8 : 0, 0, 2, 0));
			text.setFont(UIManager.getFont("Label.font").deriveFont(java.awt.Font.BOLD));
			ModelExplorerDetailsPanel.this.selectable(text);
			this.addRow(null, text);
		}

		void addLink(String label, String text, String url) {
			JLabel link = new JLabel("<html><u>" + text.replace("&", "&amp;").replace("<", "&lt;") + "</u></html>");
			link.setForeground(VCellColors.getValueForeground());
			link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			link.setToolTipText(url);
			ModelExplorerDetailsPanel.copyable(link, text);
			link.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					DialogUtils.browserLauncher(link, url, "Please visit " + url);
				}
			});
			this.addRow(label, link);
		}

		void addModel(VCDocumentInfo model) {
			VCDocumentType type = ModelListRenderer.getDocumentType(model);
			JLabel link = new JLabel("<html><u>" + model.getVersion().getName().replace("&", "&amp;").replace("<", "&lt;") + "</u></html>",
					ModelListRenderer.iconFor(type), SwingConstants.LEADING);
			link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			link.setToolTipText("Shows it in the tree; double-click to open it");
			ModelExplorerDetailsPanel.copyable(link, model.getVersion().getName());
			link.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) ModelExplorerDetailsPanel.this.opener.accept(model);
					else ModelExplorerDetailsPanel.this.selector.accept(model);
				}
			});
			this.addRow(null, link);
		}

		private void addRow(String label, JComponent value) {
			this.addRow(label, value, null);
		}

		private void addRow(String label, JComponent value, Icon icon) {
			GridBagConstraints valueAt = new GridBagConstraints();
			valueAt.gridy = this.row;
			valueAt.weightx = 1;
			valueAt.fill = GridBagConstraints.HORIZONTAL;
			valueAt.anchor = GridBagConstraints.FIRST_LINE_START;
			valueAt.insets = new Insets(1, 0, 3, 0);
			if (null != label) {
				JLabel name = new JLabel(label, icon, SwingConstants.LEADING);
				name.putClientProperty(ROW_LABEL, Boolean.TRUE);
				name.putClientProperty(COPY_TEXT, label);
				name.setForeground(VCellColors.getDimForeground());
				GridBagConstraints labelAt = new GridBagConstraints();
				labelAt.gridy = this.row;
				labelAt.gridx = 0;
				labelAt.anchor = GridBagConstraints.FIRST_LINE_START;
				labelAt.insets = new Insets(1, 0, 3, 14);
				this.add(name, labelAt);
				valueAt.gridx = 1;
			} else {
				valueAt.gridx = 0;
				valueAt.gridwidth = 2;
			}
			this.add(value, valueAt);
			this.row++;
		}
	}

	// The sections at the top, as wide as the view (text wraps rather than scrolling sideways), and at least as tall
	private static final class Stack extends JPanel implements Scrollable {
		Stack(JComponent sections) {
			super(new BorderLayout());
			this.add(sections, BorderLayout.NORTH);
		}

		@Override public Dimension getPreferredScrollableViewportSize() { return this.getPreferredSize(); }
		@Override public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) { return 16; }
		@Override public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) { return visible.height - 16; }
		@Override public boolean getScrollableTracksViewportWidth() { return true; }
		@Override
		public boolean getScrollableTracksViewportHeight() { // at least the view's height: no bare viewport under short details
			return this.getParent() instanceof javax.swing.JViewport viewport && viewport.getHeight() > this.getPreferredSize().height;
		}
	}
}
