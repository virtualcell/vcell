package cbit.vcell.desktop;

import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.VCellIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Each model in a list of models on two lines: its icon and name, then what it is, whose (if asked for), and when it
 * was saved; a model shared with the user, in a list of the user's own, says so, with the icon VCell's trees give other
 * users. While searching, a model found only by its description says so. Its annotation is the tooltip. Every other entry is striped, as VCell's tables are. Shared by the lists of
 * models the welcome window shows.
 */
@SuppressWarnings("serial")
final class ModelListRenderer extends JPanel implements ListCellRenderer<VCDocumentInfo> {
	private static final Map<VCDocumentType, String> TYPE_NAMES = Map.of(
			VCDocumentType.BIOMODEL_DOC, "BioModel", VCDocumentType.MATHMODEL_DOC, "MathModel", VCDocumentType.GEOMETRY_DOC, "Geometry");
	private static final Map<VCDocumentType, Icon> TYPE_ICONS = Map.of(
			VCDocumentType.BIOMODEL_DOC, Objects.requireNonNull(VCellIcons.makeIcon("/icons/biomodel.png")),
			VCDocumentType.MATHMODEL_DOC, Objects.requireNonNull(VCellIcons.makeIcon("/icons/mathModel.png")),
			VCDocumentType.GEOMETRY_DOC, Objects.requireNonNull(VCellIcons.makeIcon("/images/geometry2_16x16.gif")));
	private static final Icon SHARED_ICON = Objects.requireNonNull(VCellIcons.makeIcon("/icons/aUsers.png"));
	private static final int TOOL_TIP_LINE_LENGTH = 70; // characters
	private static final int DETAILS_INDENT = 22, SHARED_ICON_GAP = 6; // the details under the name, past the icon

	private final boolean showOwner;
	private final Predicate<VCDocumentInfo> isShared;
	private String searchText = "";
	private Set<SearchOptions.Field> searchFields = SearchOptions.defaults().getFields();
	private final JLabel name = new JLabel();
	private final JLabel details = new JLabel();

	/** A renderer whose second line names each model's owner too, if {@code showOwner}. */
	ModelListRenderer(boolean showOwner) {
		this(showOwner, info -> false);
	}

	/** A renderer that marks the models {@code isShared} says someone else shared with the user, naming who. */
	ModelListRenderer(boolean showOwner, Predicate<VCDocumentInfo> isShared) {
		super(new BorderLayout(0, 1));
		this.showOwner = showOwner;
		this.isShared = isShared;
		this.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		this.add(this.name, BorderLayout.NORTH);
		this.add(this.details, BorderLayout.SOUTH);
		this.details.setIconTextGap(SHARED_ICON_GAP);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends VCDocumentInfo> list, VCDocumentInfo info, int index, boolean selected, boolean focused) {
		VCDocumentType type = ModelListRenderer.getDocumentType(info);
		Version version = info.getVersion();
		this.name.setText(version.getName());
		this.name.setIcon(TYPE_ICONS.get(type));
		boolean shared = this.isShared.test(info);
		String owner = null == version.getOwner() ? "" : shared ? "  ·  Shared by " + version.getOwner().getName()
				: this.showOwner ? "  ·  " + version.getOwner().getName() : "";
		// found only by its description: so it's clear why it's listed (early in the line, where it isn't cut off)
		String why = ModelSearch.IN_DESCRIPTION == ModelSearch.rank(info, this.searchText, this.searchFields) ? "  ·  in description" : "";
		this.details.setText(TYPE_NAMES.get(type) + why + owner + "  ·  " + ModelListRenderer.describeDate(version.getDate()));
		// a shared model's mark sits under its type icon; the text lines up under the name either way
		this.details.setIcon(shared ? SHARED_ICON : null);
		int indent = shared ? DETAILS_INDENT - SHARED_ICON.getIconWidth() - SHARED_ICON_GAP : DETAILS_INDENT;
		this.details.setBorder(BorderFactory.createEmptyBorder(0, Math.max(0, indent), 0, 0));
		// read as each is drawn, so they follow the text size and theme
		this.name.setFont(VCellFonts.getFont(VCellFonts.Category.BODY));
		this.details.setFont(VCellFonts.getFont(VCellFonts.Category.CAPTION));
		Color background = 1 == index % 2 ? DefaultScrollTableCellRenderer.getEveryOtherRowColor(list.getBackground()) : list.getBackground();
		this.setBackground(selected ? list.getSelectionBackground() : background);
		this.name.setForeground(selected ? list.getSelectionForeground() : list.getForeground());
		this.details.setForeground(selected ? list.getSelectionForeground() : VCellColors.getDimForeground());
		String annotation = version.getAnnot();
		this.setToolTipText(null == annotation || annotation.isBlank() ? null : ModelListRenderer.wrapForToolTip(annotation));
		return this;
	}

	/** The search the list shows the results of, to say which were found only by their description. */
	void setSearch(String searchText, Set<SearchOptions.Field> fields) {
		this.searchText = null == searchText ? "" : searchText;
		this.searchFields = fields;
	}

	/** Calls {@code open} when the user double-clicks an entry of the list, or presses Enter in it. */
	static void openOnDoubleClickOrEnter(JList<VCDocumentInfo> list, Runnable open) {
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (2 != e.getClickCount() || !SwingUtilities.isLeftMouseButton(e)) return;
				int index = list.locationToIndex(e.getPoint());
				if (index >= 0 && list.getCellBounds(index, index).contains(e.getPoint())) open.run();
			}
		});
		list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openModel");
		list.getActionMap().put("openModel", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				open.run();
			}
		});
	}

	static VCDocumentType getDocumentType(VCDocumentInfo info) {
		if (info instanceof BioModelInfo) return VCDocumentType.BIOMODEL_DOC;
		if (info instanceof MathModelInfo) return VCDocumentType.MATHMODEL_DOC;
		return VCDocumentType.GEOMETRY_DOC;
	}

	// "Today, 2:05 PM", "Yesterday, 9:12 AM", or a date
	static String describeDate(Date date) {
		LocalDate day = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate today = LocalDate.now();
		String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
		if (day.equals(today)) return "Today, " + time;
		if (day.equals(today.minusDays(1))) return "Yesterday, " + time;
		return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
	}

	/** The text as an HTML tooltip, its lines broken between words so none is longer than about 70 characters. */
	static String wrapForToolTip(String text) {
		return "<html>" + ModelListRenderer.wrapLines(text) + "</html>";
	}

	// The text as HTML (escaped), its lines broken between words so none is longer than about 70 characters
	private static String wrapLines(String text) {
		StringBuilder html = new StringBuilder();
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
				html.append(ModelListRenderer.escape(word));
				lineLength += word.length();
			}
		}
		return html.toString();
	}

	private static String escape(String text) {
		return null == text ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
