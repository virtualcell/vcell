package cbit.vcell.client.laf;

import org.vcell.util.gui.DialogUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * The font dialog: a searchable list of the font families VCell's text can be in, its size, and sample text showing
 * both as they are picked. OK applies them to VCell and remembers them for the next start.
 */
public final class FontChooser {
	private static final String THEME_DEFAULT = "Theme Default"; // the list's entry for the theme's own family
	private static final String SAMPLE_HEADING = "Species and Reactions";
	private static final String SAMPLE_TEXT = """
			The quick brown fox jumps over the lazy dog.
			Reaction r0: RanC_cyt -> Ran_cyt + C_cyt
			Kd = 4.5×10³ µM    D = 10 µm²/s    α, β, γ, Δ
			0123456789""";

	private final JTextField searchField = new JTextField();
	private final DefaultListModel<String> familyListModel = new DefaultListModel<>();
	private final JList<String> familyList = new JList<>(this.familyListModel);
	private final JSpinner sizeSpinner;
	private final JLabel previewHeading = new JLabel(SAMPLE_HEADING);
	private final JTextArea previewText = new JTextArea(SAMPLE_TEXT);
	private final List<String> families;

	private FontChooser(List<String> families) {
		this.families = families;
		this.sizeSpinner = new JSpinner(new SpinnerNumberModel(Math.round(VCellFonts.getSize()), Math.round(VCellFonts.MIN_SIZE), Math.round(VCellFonts.MAX_SIZE), VCellFonts.SIZE_STEP));
	}

	/** Shows the font dialog over {@code requester}; on OK, applies the family and size picked and remembers them. */
	public static void show(Component requester) {
		Cursor cursor = requester.getCursor();
		requester.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // looking at every family takes a moment, once
		List<String> families;
		try {
			families = VCellFonts.getAvailableFamilies();
		} finally {
			requester.setCursor(cursor);
		}
		FontChooser chooser = new FontChooser(families);
		if (DialogUtils.showComponentOKCancelDialog(requester, chooser.createPanel(), "Font") != JOptionPane.OK_OPTION) return;
		try {
			chooser.sizeSpinner.commitEdit(); // a size still being typed
		} catch (java.text.ParseException e) {
			// not a number; the last one stands
		}
		String family = chooser.getChosenFamily();
		if (!Objects.equals(family, VCellFonts.getFamily())) VCellFonts.chooseFamily(family);
		float size = ((Number) chooser.sizeSpinner.getValue()).floatValue();
		if (size != VCellFonts.getSize()) VCellFonts.chooseSize(size);
	}

	private JPanel createPanel() {
		this.searchField.setName("FontSearchField");
		this.familyList.setName("FontFamilyList");
		this.sizeSpinner.setName("FontSizeSpinner");
		this.searchField.putClientProperty("JTextField.variant", "search");
		this.searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { FontChooser.this.filterFamilies(); }
			@Override public void removeUpdate(DocumentEvent e) { FontChooser.this.filterFamilies(); }
			@Override public void changedUpdate(DocumentEvent e) { FontChooser.this.filterFamilies(); }
		});
		JPanel searchRow = new JPanel(new BorderLayout(6, 0));
		searchRow.add(new JLabel("Search"), BorderLayout.WEST);
		searchRow.add(this.searchField, BorderLayout.CENTER);

		// each family shown in itself
		this.familyList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (!THEME_DEFAULT.equals(value)) this.setFont(new Font((String) value, Font.PLAIN, list.getFont().getSize()));
				return this;
			}
		});
		this.familyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.familyList.setVisibleRowCount(12);
		this.familyList.addListSelectionListener(e -> this.updatePreview());
		this.filterFamilies();
		this.familyList.setSelectedValue(null == VCellFonts.getFamily() ? THEME_DEFAULT : VCellFonts.getFamily(), true);

		JPanel sizeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		sizeRow.add(new JLabel("Size"));
		sizeRow.add(this.sizeSpinner);
		sizeRow.add(new JLabel("pt"));
		this.sizeSpinner.addChangeListener(e -> this.updatePreview());
		// a typed size counts as soon as it is a number, so the sample follows the typing and OK sees it
		if (this.sizeSpinner.getEditor() instanceof JSpinner.DefaultEditor editor
				&& editor.getTextField().getFormatter() instanceof javax.swing.text.DefaultFormatter formatter) {
			formatter.setCommitsOnValidEdit(true);
		}

		this.previewText.setEditable(false);
		this.previewText.setOpaque(false);
		this.previewText.setLineWrap(true);
		this.previewText.setWrapStyleWord(true);
		JPanel preview = new WidthTrackingPanel(new BorderLayout(0, 4));
		preview.setBackground(VCellLookAndFeel.getContentBackground()); // like the list beside it, in any theme
		preview.add(this.previewHeading, BorderLayout.NORTH);
		preview.add(this.previewText, BorderLayout.CENTER);
		JScrollPane previewScroller = new JScrollPane(preview, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		previewScroller.setBorder(BorderFactory.createTitledBorder("Sample"));
		previewScroller.setPreferredSize(new Dimension(340, 280)); // fixed, so the dialog doesn't resize as the sample changes
		this.updatePreview();

		// search and size along the top; the families and the sample of the one picked side by side below
		JPanel topRow = new JPanel(new BorderLayout(12, 0));
		topRow.add(searchRow, BorderLayout.CENTER);
		topRow.add(sizeRow, BorderLayout.EAST);
		JPanel choices = new JPanel(new GridLayout(1, 2, 10, 0));
		choices.add(new JScrollPane(this.familyList));
		choices.add(previewScroller);

		JPanel panel = new JPanel(new BorderLayout(0, 8));
		panel.add(topRow, BorderLayout.NORTH);
		panel.add(choices, BorderLayout.CENTER);
		return panel;
	}

	// The families whose names contain the search text; the theme's own always first
	private void filterFamilies() {
		String selected = this.familyList.getSelectedValue();
		String search = this.searchField.getText().trim().toLowerCase(Locale.ROOT);
		this.familyListModel.clear();
		if (search.isEmpty() || THEME_DEFAULT.toLowerCase(Locale.ROOT).contains(search)) this.familyListModel.addElement(THEME_DEFAULT);
		for (String family : this.families) {
			if (family.toLowerCase(Locale.ROOT).contains(search)) this.familyListModel.addElement(family);
		}
		if (null != selected && this.familyListModel.contains(selected)) this.familyList.setSelectedValue(selected, true);
	}

	// A panel as wide as the scroll pane showing it, so the sample wraps rather than scrolling sideways
	private static final class WidthTrackingPanel extends JPanel implements Scrollable {
		WidthTrackingPanel(LayoutManager layout) {
			super(layout);
		}

		@Override public Dimension getPreferredScrollableViewportSize() { return this.getPreferredSize(); }
		@Override public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) { return Math.max(1, visible.height / 10); }
		@Override public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) { return visible.height; }
		@Override public boolean getScrollableTracksViewportWidth() { return true; }
		@Override public boolean getScrollableTracksViewportHeight() { return false; }
	}

	private String getChosenFamily() {
		String selected = this.familyList.getSelectedValue();
		return null == selected ? VCellFonts.getFamily() : THEME_DEFAULT.equals(selected) ? null : selected;
	}

	private void updatePreview() {
		float size = ((Number) this.sizeSpinner.getValue()).floatValue();
		Font font = VCellFonts.getPreviewFont(this.getChosenFamily(), size);
		this.previewHeading.setFont(font.deriveFont(Font.BOLD));
		this.previewText.setFont(font);
	}
}
