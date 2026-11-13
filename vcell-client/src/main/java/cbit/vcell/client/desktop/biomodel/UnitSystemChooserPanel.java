package cbit.vcell.client.desktop.biomodel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import cbit.vcell.client.desktop.biomodel.UnitSystemLibrary.NamedUnitSystem;
import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.model.ModelUnitSystem;

/**
 * Chooses a BioModel's unit system by name, beside the table of its units: choosing one converts the model to it.
 * Add makes a new one from units the user types, starting from the chosen one's, and Delete removes one the user
 * made; a unit system doesn't change once made. A model whose units match none of them lists them as its own, so Add
 * can keep them under a name.
 */
@SuppressWarnings("serial")
public class UnitSystemChooserPanel extends JPanel {
	static final String CUSTOM_NAME = "Custom (this model's own)";

	private final UnitSystemLibrary library;
	private final Predicate<ModelUnitSystem> converter;
	private final JComboBox<NamedUnitSystem> comboBox = new JComboBox<>() {
		@Override
		public void updateUI() { // also when the appearance changes
			super.updateUI();
			// Arrow keys should browse, and only Enter or a click choose (each choice converts the model). Aqua's arrow
			// keys already just open the list; elsewhere this asks for it, but Aqua would draw a table cell's borderless combo.
			boolean aqua = this.getUI().getClass().getName().contains("Aqua");
			this.putClientProperty("JComboBox.isTableCellEditor", Boolean.valueOf(!aqua)); // never null: Aqua reads it unchecked
		}
	};
	private final JButton addButton = new JButton("Add...");
	private final JButton deleteButton = new JButton("Delete...");
	private final Runnable onLibraryChange = this::refresh; // held here: the library holds its listeners weakly
	private ModelUnitSystem modelUnits = null;
	private String preferredName = null; // which name to show when several have the model's units
	private boolean refreshing = false;

	/**
	 * @param converter converts the model to the given units, reporting any failure itself; false if it didn't
	 */
	public UnitSystemChooserPanel(UnitSystemLibrary library, Predicate<ModelUnitSystem> converter) {
		this.library = library;
		this.converter = converter;
		this.setName("UnitSystemChooserPanel");
		this.buildLayout();
		this.comboBox.setName("UnitSystemComboBox");
		this.comboBox.setToolTipText("Choosing a unit system converts this model's values to its units");
		this.addButton.setName("UnitSystemAddButton");
		this.addButton.setToolTipText("Make a unit system of your own, starting from the chosen one's units");
		this.deleteButton.setName("UnitSystemDeleteButton");
		this.comboBox.addActionListener(e -> this.chosen());
		this.addButton.addActionListener(e -> this.add());
		this.deleteButton.addActionListener(e -> this.deleteChosen());
		this.library.addChangeListener(this.onLibraryChange);
		this.refresh();
	}

	/** The model's units changed (another model, or a conversion): show which system they are. */
	public void setModelUnits(ModelUnitSystem units) {
		this.modelUnits = units;
		this.refresh();
	}

	NamedUnitSystem getChosen() {
		return (NamedUnitSystem) this.comboBox.getSelectedItem();
	}

	JComboBox<NamedUnitSystem> getComboBox() {
		return this.comboBox;
	}

	JButton getDeleteButton() {
		return this.deleteButton;
	}

	private void buildLayout() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 10));
		JLabel heading = new JLabel("Unit System");
		VCellFonts.setCategory(heading, VCellFonts.Category.HEADING);
		heading.setLabelFor(this.comboBox);
		JPanel buttons = new JPanel(new GridLayout(1, 2, 4, 0));
		buttons.add(this.addButton);
		buttons.add(this.deleteButton);
		JLabel note = new JLabel("<html>Choosing another unit system converts this model's values to its units.</html>") {
			@Override
			public void updateUI() { // the theme's own secondary text color, again whenever the theme changes
				super.updateUI();
				this.setForeground(UIManager.getColor("Label.disabledForeground"));
			}
		};
		for (JComponent component : new JComponent[] { heading, this.comboBox, buttons, note }) {
			component.setAlignmentX(CENTER_ALIGNMENT); // the title and list centered; the buttons and note fill the width
		}
		buttons.setMaximumSize(new Dimension(Short.MAX_VALUE, buttons.getPreferredSize().height));
		note.setPreferredSize(new Dimension(260, note.getPreferredSize().height * 2));
		note.setMaximumSize(new Dimension(Short.MAX_VALUE, note.getPreferredSize().height));
		this.add(heading);
		this.add(Box.createVerticalStrut(6));
		this.add(this.comboBox);
		this.add(Box.createVerticalStrut(6));
		this.add(buttons);
		this.add(Box.createVerticalStrut(8));
		this.add(note);
		this.add(Box.createVerticalGlue());
	}

	// lists the library, plus the model's own units when no listed system has them, and shows the model's
	private void refresh() {
		this.refreshing = true;
		try {
			List<NamedUnitSystem> systems = this.library.getAll();
			// shown: one of these very entries (the list selects only what it holds), with the model's units
			NamedUnitSystem shown = null;
			for (NamedUnitSystem system : systems) {
				if (!system.hasUnits(this.modelUnits)) continue;
				if (system.name().equals(this.preferredName)) {
					shown = system;
					break;
				}
				if (shown == null) shown = system;
			}
			if (shown == null && this.modelUnits != null) {
				shown = new NamedUnitSystem(UnitSystemChooserPanel.CUSTOM_NAME, this.modelUnits, true);
				systems.add(shown);
			}
			this.comboBox.setModel(new DefaultComboBoxModel<>(systems.toArray(new NamedUnitSystem[0])));
			this.comboBox.setSelectedItem(shown);
			this.comboBox.setMaximumSize(this.comboBox.getPreferredSize()); // its own width, so it's drawn centered
			this.revalidate();
		} finally {
			this.refreshing = false;
		}
		this.updateButtons();
	}

	private void updateButtons() {
		NamedUnitSystem chosen = this.getChosen();
		boolean deletable = chosen != null && !chosen.builtIn();
		this.deleteButton.setEnabled(deletable);
		this.deleteButton.setToolTipText(deletable ? "Remove this unit system from your list (the model keeps its units)"
				: "Only unit systems you added can be deleted");
	}

	private void chosen() {
		if (this.refreshing) return;
		NamedUnitSystem chosen = this.getChosen();
		if (chosen == null) return;
		if (chosen.hasUnits(this.modelUnits)) { // the same units under another name: nothing to convert
			this.preferredName = chosen.name();
			this.updateButtons();
			return;
		}
		this.convertTo(chosen);
	}

	private void convertTo(NamedUnitSystem system) {
		String before = this.preferredName;
		this.preferredName = system.name();
		if (this.converter.test(system.unitSystem())) return; // the model's new units arrive through setModelUnits
		this.preferredName = before;
		this.refresh(); // back to the model's units
	}

	private void add() {
		NamedUnitSystem chosen = this.getChosen();
		ModelUnitSystem start = chosen != null ? chosen.unitSystem() : ModelUnitSystem.createDefaultVCModelUnitSystem();
		NamedUnitSystem added = UnitSystemDialog.askToAdd(this, this.library, start);
		if (added == null) return;
		if (added.hasUnits(this.modelUnits)) {
			this.preferredName = added.name();
			this.refresh();
			return;
		}
		this.convertTo(added);
	}

	private void deleteChosen() {
		NamedUnitSystem chosen = this.getChosen();
		if (chosen == null || chosen.builtIn()) return;
		int answer = JOptionPane.showConfirmDialog(this,
				"Delete the unit system “" + chosen.name() + "” from your list?\nModels keep their units; this can't be undone.",
				"Delete Unit System", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (answer != JOptionPane.OK_OPTION) return;
		this.library.remove(chosen);
	}
}
