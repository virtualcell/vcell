package cbit.vcell.client.desktop;

import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.desktop.SearchOptions;
import cbit.vcell.desktop.SearchOptions.Access;
import cbit.vcell.desktop.SearchOptions.Approach;
import cbit.vcell.desktop.SearchOptions.Field;
import cbit.vcell.desktop.SearchOptions.Order;
import cbit.vcell.desktop.SearchOptions.Saved;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.gui.GuiUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The advanced search's options, for the welcome window's search: where to look for the search text; which models to
 * list (kind, owner, when saved, modeling approach, permissions); and their order. Each change is reported, for the
 * search to run again.
 */
@SuppressWarnings("serial")
public class SearchOptionsPanel extends JPanel {
	private final Map<Field, JCheckBox> fields = new EnumMap<>(Field.class);
	private final JCheckBox bioModels = new JCheckBox("BioModels");
	private final JCheckBox mathModels = new JCheckBox("MathModels");
	private final JTextField owner = new JTextField(12);
	private final JComboBox<Saved> saved = SearchOptionsPanel.choices(Saved.values());
	// Between's first and last days, both included: enabled only for Between
	private final JSpinner savedFrom = SearchOptionsPanel.dateSpinner("SearchSavedFrom", "Saved on or after");
	private final JSpinner savedTo = SearchOptionsPanel.dateSpinner("SearchSavedTo", "Saved on or before");
	private final Map<Approach, JCheckBox> approaches = new EnumMap<>(Approach.class);
	private final Map<Access, JCheckBox> access = new EnumMap<>(Access.class);
	private final JComboBox<Order> order = SearchOptionsPanel.choices(Order.values());
	private final List<List<JCheckBox>> groups = new ArrayList<>(); // each needs one of its boxes checked
	private final Runnable onChange;
	private final JLabel note = new JLabel(" ");
	private String noteText = "BMDB is searched by name and ID only; these options don't apply to it.";
	private boolean resetting = false; // the controls being put back, one by one: report the change once, at the end
	private final JButton reset = new JButton("Reset");
	private final JPanel heading = new JPanel(new BorderLayout()); // the title, and Reset at its right (unless below)
	private final JPanel bottom = new JPanel(new BorderLayout(8, 0)); // the note, and Reset at its right (if below)
	private String noneApply = null; // why none of the options apply (a search of the BioModels Database); null: they all do

	/** A panel of the options, at their defaults; {@code onChange} runs whenever the user changes one. */
	public SearchOptionsPanel(Runnable onChange) {
		super(new BorderLayout(0, 8));
		this.onChange = onChange;
		this.setName("SearchOptionsPanel");
		this.setOpaque(false);

		this.reset.setName("SearchOptionsReset");
		this.reset.addActionListener(e -> this.reset());
		this.heading.setOpaque(false);
		this.heading.add(GuiUtils.createBoldJLabel("Search options"), BorderLayout.WEST);
		this.heading.add(this.reset, BorderLayout.EAST);

		for (Field field : Field.values()) this.fields.put(field, this.checkBox(field.label, "In" + field.name()));
		for (Approach approach : Approach.values()) this.approaches.put(approach, this.checkBox(approach.label, "Approach" + approach.name()));
		for (Access permission : Access.values()) this.access.put(permission, this.checkBox(permission.label, "Access" + permission.name()));
		this.watch(this.bioModels, "TypeBIOMODEL");
		this.watch(this.mathModels, "TypeMATHMODEL");
		this.groups.add(List.copyOf(this.fields.values()));
		this.groups.add(List.of(this.bioModels, this.mathModels));
		this.groups.add(List.copyOf(this.approaches.values()));
		this.groups.add(List.copyOf(this.access.values()));
		this.owner.setName("SearchOwner");
		// Enter applies what's typed, here as in the date fields, rather than reaching the window's default (opening the
		// selected model, in the welcome window): a text field with no action of its own hands Enter on to it
		this.owner.addActionListener(e -> this.changed());
		this.owner.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { SearchOptionsPanel.this.changed(); }
			@Override public void removeUpdate(DocumentEvent e) { SearchOptionsPanel.this.changed(); }
			@Override public void changedUpdate(DocumentEvent e) { SearchOptionsPanel.this.changed(); }
		});
		this.saved.setName("SearchSaved");
		this.saved.addActionListener(e -> this.changed());
		this.savedFrom.addChangeListener(e -> this.changed());
		this.savedTo.addChangeListener(e -> this.changed());
		this.order.setName("SearchOrder");
		this.order.addActionListener(e -> this.changed());

		JPanel rows = new JPanel(new GridBagLayout());
		rows.setOpaque(false);
		this.row(rows, "Search in", SearchOptionsPanel.inRow(this.fields.values().toArray(new JComponent[0])));
		this.row(rows, "Model type", SearchOptionsPanel.inRow(this.bioModels, this.mathModels));
		this.row(rows, "Approach", SearchOptionsPanel.inGrid(2, this.approaches.values().toArray(new JComponent[0])));
		this.row(rows, "Permissions", SearchOptionsPanel.inGrid(3, this.access.values().toArray(new JComponent[0])));
		this.row(rows, "Owner", this.owner, this.owner);
		this.row(rows, "Saved", SearchOptionsPanel.inRow(this.saved, this.savedFrom, new JLabel("and"), this.savedTo), this.saved);
		this.row(rows, "Order", SearchOptionsPanel.inRow(this.order), this.order);

		VCellFonts.setCategory(this.note, VCellFonts.Category.CAPTION);
		this.note.setForeground(VCellColors.getDimForeground());

		this.setNoteShown(true);
		this.bottom.setOpaque(false);
		this.bottom.add(this.note, BorderLayout.CENTER);
		this.add(this.heading, BorderLayout.NORTH);
		this.add(rows, BorderLayout.CENTER);
		this.add(this.bottom, BorderLayout.SOUTH);
		this.setControlsToDefaults(); // not reported: nothing has changed yet
	}

	/**
	 * Reset at the bottom right, rather than the top: next to the Filters button, where the options open upward from it
	 * (the welcome window's).
	 */
	public void setResetBelow(boolean below) {
		(below ? this.bottom : this.heading).add(this.reset, BorderLayout.EAST);
		this.revalidate();
	}

	/** Whether the note under the options shows; hidden, it keeps its line, so the options keep their height. */
	public void setNoteShown(boolean shown) {
		this.noteShown = shown;
		this.note.setText(shown ? this.noteText : " ");
	}

	private boolean noteShown = true;

	/**
	 * None of the options apply (a search of the BioModels Database, which has nothing to narrow by but the search text):
	 * they're disabled, their tooltip saying why ({@code why}); null, they all apply again. Reset stays, for the others.
	 */
	public void setNoneApply(String why) {
		this.noneApply = why;
		for (JComponent control : List.of(this.owner, this.saved, this.order)) {
			control.setEnabled(null == why);
			control.setToolTipText(why);
		}
		this.lockLastChecked();
	}

	/** The note under the options, on what the BioModels Database's search looks at. */
	public void setNote(String text) {
		this.noteText = text;
		this.setNoteShown(this.noteShown);
	}

	/** A Filters button's tooltip: what the options are for, then what's changed from the defaults, if anything. */
	public static String filtersToolTip(SearchOptions options) {
		String about = "More ways to search: where to look, which models, and their order";
		List<String> changes = options.describeChanges();
		if (changes.isEmpty()) return about;
		StringBuilder tip = new StringBuilder("<html>").append(about).append("<br><br><b>Changed from the defaults:</b>");
		for (String change : changes) tip.append("<br>").append(SearchOptionsPanel.escapeHtml(change));
		return tip.append("</html>").toString();
	}

	private static String escapeHtml(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	/** The options as the controls set them. */
	public SearchOptions getOptions() {
		Set<VCDocumentType> types = EnumSet.noneOf(VCDocumentType.class);
		if (this.bioModels.isSelected()) types.add(VCDocumentType.BIOMODEL_DOC);
		if (this.mathModels.isSelected()) types.add(VCDocumentType.MATHMODEL_DOC);
		return new SearchOptions(SearchOptionsPanel.selected(this.fields, Field.class), types, this.owner.getText(),
				(Saved) this.saved.getSelectedItem(), SearchOptionsPanel.day(this.savedFrom), SearchOptionsPanel.day(this.savedTo),
				SearchOptionsPanel.selected(this.approaches, Approach.class), SearchOptionsPanel.selected(this.access, Access.class),
				(Order) this.order.getSelectedItem());
	}

	/** Puts every option back to its default: every model, the search text looked for everywhere, best matches first. */
	public void reset() {
		this.setControlsToDefaults();
		this.changed();
	}

	private void setControlsToDefaults() {
		this.resetting = true;
		SearchOptions defaults = SearchOptions.defaults();
		this.fields.forEach((field, box) -> box.setSelected(defaults.getFields().contains(field)));
		this.bioModels.setSelected(defaults.getTypes().contains(VCDocumentType.BIOMODEL_DOC));
		this.mathModels.setSelected(defaults.getTypes().contains(VCDocumentType.MATHMODEL_DOC));
		this.owner.setText(defaults.getOwner());
		this.saved.setSelectedItem(defaults.getSaved());
		LocalDate today = LocalDate.now(); // Between starts out as the past year
		this.savedFrom.setValue(SearchOptionsPanel.date(today.minusYears(1)));
		this.savedTo.setValue(SearchOptionsPanel.date(today));
		this.approaches.forEach((approach, box) -> box.setSelected(defaults.getApproaches().contains(approach)));
		this.access.forEach((permission, box) -> box.setSelected(defaults.getAccess().contains(permission)));
		this.order.setSelectedItem(defaults.getOrder());
		this.resetting = false;
		this.lockLastChecked();
	}

	private void changed() {
		if (this.resetting) return;
		this.lockLastChecked();
		this.onChange.run();
	}

	// A category with only one box checked can't have it unchecked (it would list nothing): disable that box until
	// another of its category is checked
	private void lockLastChecked() {
		// the dates, as Between needs them: only for Between
		boolean between = null == this.noneApply && Saved.BETWEEN == this.saved.getSelectedItem();
		for (JSpinner dates : List.of(this.savedFrom, this.savedTo)) {
			dates.setEnabled(between);
			dates.setToolTipText(null != this.noneApply ? this.noneApply : between ? null : "Choose Between to search by dates");
		}
		for (List<JCheckBox> group : this.groups) {
			boolean applies = null == this.noneApply;
			long checked = group.stream().filter(JCheckBox::isSelected).count();
			for (JCheckBox box : group) {
				boolean locked = 1 == checked && box.isSelected();
				box.setEnabled(applies && !locked);
				box.setToolTipText(!applies ? this.noneApply : locked ? "At least one must stay checked" : null);
			}
		}
	}

	// A date, typed (year-month-day) or stepped with the arrows, its parts the arrows step chosen by the caret
	private static JSpinner dateSpinner(String name, String accessibleName) {
		JSpinner spinner = new JSpinner(new SpinnerDateModel()) {
			@Override
			public Dimension getMaximumSize() {
				return this.getPreferredSize(); // its date's width, not the row's
			}
		};
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
		editor.getFormat().setLenient(false); // 2026-13-45 isn't a date, rather than 2027-02-14
		spinner.setEditor(editor);
		spinner.setName(name);
		spinner.getAccessibleContext().setAccessibleName(accessibleName);
		editor.getTextField().setName(name + "Field");
		editor.getTextField().getAccessibleContext().setAccessibleName(accessibleName);
		editor.getTextField().setColumns(8);
		// a date counts as soon as it's whole and valid, as typed: the lists follow it, without Enter or leaving the field
		if (editor.getTextField().getFormatter() instanceof javax.swing.text.DefaultFormatter formatter) formatter.setCommitsOnValidEdit(true);
		// Enter commits the date typed (the field's own Enter action, once it has a listener), rather than reaching the
		// window's default: opening the selected model, in the welcome window
		editor.getTextField().addActionListener(e -> {});
		return spinner;
	}

	private static LocalDate day(JSpinner spinner) {
		return ((java.util.Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private static java.util.Date date(LocalDate day) {
		return java.util.Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private JCheckBox checkBox(String label, String name) {
		JCheckBox box = new JCheckBox(label);
		this.watch(box, name);
		return box;
	}

	private void watch(JCheckBox box, String name) {
		box.setName("Search" + name);
		box.setOpaque(false);
		box.addActionListener(e -> this.changed());
	}

	// A label, then its controls, as a row of a two-column grid
	private void row(JPanel rows, String label, JComponent controls) {
		this.row(rows, label, controls, null);
	}

	// As above; the label names the one control (a screen reader reads it out for it), if there is one
	private void row(JPanel rows, String label, JComponent controls, JComponent labelled) {
		JLabel name = new JLabel(label);
		if (null != labelled) name.setLabelFor(labelled);
		GridBagConstraints labelAt = new GridBagConstraints();
		labelAt.gridx = 0;
		labelAt.anchor = GridBagConstraints.FIRST_LINE_START;
		labelAt.insets = new Insets(3, 0, 3, 12);
		rows.add(name, labelAt);
		GridBagConstraints controlsAt = new GridBagConstraints();
		controlsAt.gridx = 1;
		controlsAt.weightx = 1;
		controlsAt.fill = GridBagConstraints.HORIZONTAL;
		controlsAt.anchor = GridBagConstraints.FIRST_LINE_START;
		controlsAt.insets = new Insets(0, 0, 2, 0);
		rows.add(controls, controlsAt);
	}

	// Side by side, from the left edge (no gap before the first, so the rows line up)
	private static JComponent inRow(JComponent... components) {
		JPanel row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		row.setOpaque(false);
		for (int index = 0; index < components.length; index++) {
			if (index > 0) row.add(Box.createHorizontalStrut(8));
			row.add(components[index]);
		}
		row.add(Box.createHorizontalGlue());
		return row;
	}

	private static JComponent inGrid(int columns, JComponent... components) {
		JPanel grid = new JPanel(new GridLayout(0, columns, 8, 0));
		grid.setOpaque(false);
		for (JComponent component : components) grid.add(component);
		return grid;
	}

	// A drop-down of these, its own width rather than its row's
	private static <E> JComboBox<E> choices(E[] values) {
		return new JComboBox<>(values) {
			@Override
			public Dimension getMaximumSize() {
				return this.getPreferredSize();
			}
		};
	}

	private static <E extends Enum<E>> Set<E> selected(Map<E, JCheckBox> boxes, Class<E> type) {
		Set<E> selected = EnumSet.noneOf(type);
		boxes.forEach((value, box) -> {
			if (box.isSelected()) selected.add(value);
		});
		return selected;
	}
}
