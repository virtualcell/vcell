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
import java.util.EnumMap;
import java.util.EnumSet;
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
	private final Map<Approach, JCheckBox> approaches = new EnumMap<>(Approach.class);
	private final Map<Access, JCheckBox> access = new EnumMap<>(Access.class);
	private final JComboBox<Order> order = SearchOptionsPanel.choices(Order.values());
	private final Runnable onChange;
	private boolean resetting = false; // the controls being put back, one by one: report the change once, at the end

	/** A panel of the options, at their defaults; {@code onChange} runs whenever the user changes one. */
	public SearchOptionsPanel(Runnable onChange) {
		super(new BorderLayout(0, 8));
		this.onChange = onChange;
		this.setName("SearchOptionsPanel");
		this.setOpaque(false);

		JButton reset = new JButton("Reset");
		reset.setName("SearchOptionsReset");
		reset.addActionListener(e -> this.reset());
		JPanel heading = new JPanel(new BorderLayout());
		heading.setOpaque(false);
		heading.add(GuiUtils.createBoldJLabel("Search options"), BorderLayout.WEST);
		heading.add(reset, BorderLayout.EAST);

		for (Field field : Field.values()) this.fields.put(field, this.checkBox(field.label, "SearchIn" + field.name()));
		for (Approach approach : Approach.values()) this.approaches.put(approach, this.checkBox(approach.label, "Approach" + approach.name()));
		for (Access permission : Access.values()) this.access.put(permission, this.checkBox(permission.label, "Access" + permission.name()));
		this.watch(this.bioModels, "TypeBIOMODEL");
		this.watch(this.mathModels, "TypeMATHMODEL");
		this.owner.setName("SearchOwner");
		this.owner.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { SearchOptionsPanel.this.changed(); }
			@Override public void removeUpdate(DocumentEvent e) { SearchOptionsPanel.this.changed(); }
			@Override public void changedUpdate(DocumentEvent e) { SearchOptionsPanel.this.changed(); }
		});
		this.saved.setName("SearchSaved");
		this.saved.addActionListener(e -> this.changed());
		this.order.setName("SearchOrder");
		this.order.addActionListener(e -> this.changed());

		JPanel rows = new JPanel(new GridBagLayout());
		rows.setOpaque(false);
		this.row(rows, "Search in", SearchOptionsPanel.inRow(this.fields.values().toArray(new JComponent[0])));
		this.row(rows, "Model type", SearchOptionsPanel.inRow(this.bioModels, this.mathModels));
		this.row(rows, "Approach", SearchOptionsPanel.inGrid(2, this.approaches.values().toArray(new JComponent[0])));
		this.row(rows, "Permissions", SearchOptionsPanel.inGrid(3, this.access.values().toArray(new JComponent[0])));
		this.row(rows, "Owner", this.owner, this.owner);
		this.row(rows, "Saved", SearchOptionsPanel.inRow(this.saved), this.saved);
		this.row(rows, "Order", SearchOptionsPanel.inRow(this.order), this.order);

		JLabel note = new JLabel("The BioModels Database tab searches its models' names and IDs only.");
		VCellFonts.setCategory(note, VCellFonts.Category.CAPTION);
		note.setForeground(VCellColors.getDimForeground());

		this.add(heading, BorderLayout.NORTH);
		this.add(rows, BorderLayout.CENTER);
		this.add(note, BorderLayout.SOUTH);
		this.setControlsToDefaults(); // not reported: nothing has changed yet
	}

	/** The options as the controls set them. */
	public SearchOptions getOptions() {
		Set<VCDocumentType> types = EnumSet.noneOf(VCDocumentType.class);
		if (this.bioModels.isSelected()) types.add(VCDocumentType.BIOMODEL_DOC);
		if (this.mathModels.isSelected()) types.add(VCDocumentType.MATHMODEL_DOC);
		return new SearchOptions(SearchOptionsPanel.selected(this.fields, Field.class), types, this.owner.getText(),
				(Saved) this.saved.getSelectedItem(), SearchOptionsPanel.selected(this.approaches, Approach.class),
				SearchOptionsPanel.selected(this.access, Access.class), (Order) this.order.getSelectedItem());
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
		this.approaches.forEach((approach, box) -> box.setSelected(defaults.getApproaches().contains(approach)));
		this.access.forEach((permission, box) -> box.setSelected(defaults.getAccess().contains(permission)));
		this.order.setSelectedItem(defaults.getOrder());
		this.resetting = false;
	}

	private void changed() {
		if (!this.resetting) this.onChange.run();
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
