package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cbit.vcell.client.desktop.biomodel.UnitSystemLibrary.NamedUnitSystem;
import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.units.VCUnitSystem;
import ucar.units_vcell.TokenMgrError;

/**
 * Names and sets the units of a new unit system for the library (unit systems don't change once made). Problems (a name in use,
 * a unit VCell can't read or of the wrong kind) show beneath as they're typed, and keep the dialog from finishing.
 */
@SuppressWarnings("serial")
public class UnitSystemDialog extends JDialog {
	// the rows, top to bottom, and where each one's symbol goes in createVCModelUnitSystem's arguments
	private static final String[] UNIT_LABELS = { "length", "area", "volume", "time", "volume species substance", "membrane species substance", "lumped reaction substance" };
	private static final int[] SYMBOL_INDEX = { 5, 4, 3, 6, 0, 1, 2 };
	private static final int VOLUME_SUBSTANCE_ROW = 4;
	private static final VCUnitSystem UNIT_READER = new VCUnitSystem() {}; // reads unit symbols, as ModelUnitSystem does

	private final UnitSystemLibrary library;
	private final JTextField nameField = new JTextField(24);
	private final JTextField[] unitFields = new JTextField[UnitSystemDialog.UNIT_LABELS.length];
	private final JCheckBox sbmlCheckBox = new JCheckBox("One substance unit for all species (SBML compatible)");
	private final JLabel problemLabel = new JLabel(" ");
	private final JButton finishButton;
	private final JButton cancelButton = new JButton("Cancel");
	private NamedUnitSystem made = null;

	private UnitSystemDialog(Window owner, String title, String finishLabel, UnitSystemLibrary library, String suggestedName, ModelUnitSystem startUnits) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		this.setName("UnitSystemDialog");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.library = library;
		this.finishButton = new JButton(finishLabel);

		JPanel content = new JPanel(new BorderLayout(0, 10));
		content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		content.add(this.buildFields(startUnits), BorderLayout.CENTER);
		content.add(this.buildBottom(), BorderLayout.SOUTH);
		this.setContentPane(content);

		this.nameField.setText(suggestedName);
		this.nameField.selectAll();
		this.wireBehavior();
		this.updateProblem();
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(owner);
	}

	/** Asks for a new unit system, starting from <code>startUnits</code>, and adds it; null if cancelled. */
	public static NamedUnitSystem askToAdd(Component parent, UnitSystemLibrary library, ModelUnitSystem startUnits) {
		UnitSystemDialog dialog = new UnitSystemDialog(UnitSystemDialog.windowOf(parent), "Add Unit System", "Add", library,
				UnitSystemDialog.unusedName(library, "My Unit System"), startUnits);
		dialog.setVisible(true);
		return dialog.made;
	}

	/** <code>base</code>, or <code>base</code> with the first number that makes it unused. */
	static String unusedName(UnitSystemLibrary library, String base) {
		if (library.nameProblem(base) == null) return base;
		for (int i = 2; ; i++) {
			String candidate = base + " " + i;
			if (library.nameProblem(candidate) == null) return candidate;
		}
	}

	private JPanel buildFields(ModelUnitSystem startUnits) {
		JPanel fields = new JPanel(new GridBagLayout());
		JLabel nameLabel = new JLabel("Name");
		nameLabel.setLabelFor(this.nameField);
		this.nameField.setName("UnitSystemNameField");
		fields.add(nameLabel, UnitSystemDialog.cell(0, 0, GridBagConstraints.LINE_END));
		GridBagConstraints nameCell = UnitSystemDialog.cell(1, 0, GridBagConstraints.LINE_START);
		nameCell.gridwidth = 2;
		nameCell.fill = GridBagConstraints.HORIZONTAL;
		fields.add(this.nameField, nameCell);

		// the units, with VCell's defaults beside them for reference
		JLabel unitHeading = new JLabel("unit");
		JLabel defaultHeading = new JLabel("VCell default");
		defaultHeading.setForeground(UIManager.getColor("Label.disabledForeground"));
		fields.add(unitHeading, UnitSystemDialog.cell(1, 1, GridBagConstraints.LINE_START));
		fields.add(defaultHeading, UnitSystemDialog.cell(2, 1, GridBagConstraints.LINE_START));
		String[] startSymbols = UnitSystemLibrary.symbols(startUnits);
		String[] defaultSymbols = UnitSystemLibrary.symbols(ModelUnitSystem.createDefaultVCModelUnitSystem());
		for (int row = 0; row < UnitSystemDialog.UNIT_LABELS.length; row++) {
			JTextField field = new JTextField(startSymbols[UnitSystemDialog.SYMBOL_INDEX[row]], 12);
			field.setName("UnitField_" + UnitSystemDialog.UNIT_LABELS[row].replace(' ', '_'));
			this.unitFields[row] = field;
			JLabel label = new JLabel(UnitSystemDialog.UNIT_LABELS[row]);
			label.setLabelFor(field);
			JLabel defaultLabel = new JLabel("[" + defaultSymbols[UnitSystemDialog.SYMBOL_INDEX[row]] + "]");
			defaultLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
			fields.add(label, UnitSystemDialog.cell(0, row + 2, GridBagConstraints.LINE_END));
			GridBagConstraints fieldCell = UnitSystemDialog.cell(1, row + 2, GridBagConstraints.LINE_START);
			fieldCell.fill = GridBagConstraints.HORIZONTAL;
			fields.add(field, fieldCell);
			fields.add(defaultLabel, UnitSystemDialog.cell(2, row + 2, GridBagConstraints.LINE_START));
		}
		this.sbmlCheckBox.setName("UnitSystemSbmlCheckBox");
		this.sbmlCheckBox.setToolTipText("SBML has one substance unit: membrane and lumped reaction species use the volume species'");
		this.sbmlCheckBox.setSelected(startSymbols[0].equals(startSymbols[1]) && startSymbols[0].equals(startSymbols[2]));
		GridBagConstraints checkCell = UnitSystemDialog.cell(1, UnitSystemDialog.UNIT_LABELS.length + 2, GridBagConstraints.LINE_START);
		checkCell.gridwidth = 2;
		fields.add(this.sbmlCheckBox, checkCell);
		return fields;
	}

	private JPanel buildBottom() {
		this.problemLabel.setName("UnitSystemProblemLabel");
		this.problemLabel.setForeground(VCellColors.getErrorForeground());
		this.finishButton.setName("UnitSystemFinishButton");
		this.cancelButton.setName("UnitSystemCancelButton");
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING, 6, 0));
		buttons.add(this.cancelButton);
		buttons.add(this.finishButton);
		JPanel bottom = new JPanel(new BorderLayout(0, 8));
		bottom.add(this.problemLabel, BorderLayout.NORTH);
		bottom.add(buttons, BorderLayout.SOUTH);
		return bottom;
	}

	private void wireBehavior() {
		DocumentListener recheck = new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { UnitSystemDialog.this.edited(e); }
			@Override public void removeUpdate(DocumentEvent e) { UnitSystemDialog.this.edited(e); }
			@Override public void changedUpdate(DocumentEvent e) { UnitSystemDialog.this.edited(e); }
		};
		this.nameField.getDocument().addDocumentListener(recheck);
		for (JTextField field : this.unitFields) {
			if (field != null) field.getDocument().addDocumentListener(recheck);
		}
		this.sbmlCheckBox.addActionListener(e -> this.followVolumeSubstance());
		this.finishButton.addActionListener(e -> this.finish());
		this.cancelButton.addActionListener(e -> this.dispose());
		this.getRootPane().setDefaultButton(this.finishButton);
		this.getRootPane().registerKeyboardAction(e -> this.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		this.followVolumeSubstance();
	}

	private void edited(DocumentEvent e) {
		// SBML compatible: the other substance units follow the volume species' (later, not inside its own notification)
		if (this.unitFields[0] != null && e.getDocument() == this.unitFields[UnitSystemDialog.VOLUME_SUBSTANCE_ROW].getDocument()) {
			SwingUtilities.invokeLater(this::followVolumeSubstance);
		}
		this.updateProblem();
	}

	private void followVolumeSubstance() {
		if (this.unitFields[0] == null) return;
		boolean follow = this.sbmlCheckBox.isSelected();
		String substance = this.unitFields[UnitSystemDialog.VOLUME_SUBSTANCE_ROW].getText();
		for (int row = UnitSystemDialog.VOLUME_SUBSTANCE_ROW + 1; row < this.unitFields.length; row++) {
			this.unitFields[row].setEnabled(!follow);
			if (follow && !this.unitFields[row].getText().equals(substance)) this.unitFields[row].setText(substance);
		}
	}

	private void updateProblem() {
		String problem = this.problem();
		this.problemLabel.setText(problem == null ? " " : problem);
		this.finishButton.setEnabled(problem == null);
	}

	// the first thing keeping the unit system from being made, or null
	String problem() {
		String nameProblem = this.library.nameProblem(this.nameField.getText());
		if (nameProblem != null) return nameProblem;
		try {
			this.units();
			return null;
		} catch (RuntimeException | TokenMgrError e) { // the unit parser reports some bad symbols as an Error
			String message = e.getMessage();
			return message == null || message.isBlank() ? "VCell can't read one of these units" : UnitSystemDialog.firstLine(message);
		}
	}

	private ModelUnitSystem units() {
		String[] symbols = new String[UnitSystemDialog.UNIT_LABELS.length];
		for (int row = 0; row < this.unitFields.length; row++) {
			String symbol = this.unitFields[row].getText().strip();
			if (symbol.isEmpty()) throw new IllegalArgumentException("Enter a " + UnitSystemDialog.UNIT_LABELS[row] + " unit");
			try {
				UnitSystemDialog.UNIT_READER.getInstance(symbol);
			} catch (RuntimeException | TokenMgrError e) { // the parser's own message is about its grammar
				throw new IllegalArgumentException("VCell can't read the " + UnitSystemDialog.UNIT_LABELS[row] + " unit \u201c" + symbol + "\u201d", e);
			}
			symbols[UnitSystemDialog.SYMBOL_INDEX[row]] = symbol;
		}
		return ModelUnitSystem.createVCModelUnitSystem(symbols[0], symbols[1], symbols[2], symbols[3], symbols[4], symbols[5], symbols[6]);
	}

	private void finish() {
		if (this.problem() != null) return;
		this.made = this.library.add(this.nameField.getText(), this.units());
		this.dispose();
	}

	JTextField getNameField() {
		return this.nameField;
	}

	JTextField getUnitField(int row) {
		return this.unitFields[row];
	}

	NamedUnitSystem getMade() {
		return this.made;
	}

	/** A dialog without showing it, for tests. */
	static UnitSystemDialog forAdding(UnitSystemLibrary library, ModelUnitSystem startUnits) {
		return new UnitSystemDialog(null, "Add Unit System", "Add", library, UnitSystemDialog.unusedName(library, "My Unit System"), startUnits);
	}

	JButton getFinishButton() {
		return this.finishButton;
	}

	private static String firstLine(String message) {
		int end = message.indexOf('\n');
		String line = end < 0 ? message : message.substring(0, end);
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	private static Window windowOf(Component parent) {
		if (parent == null) return null;
		return parent instanceof Window window ? window : SwingUtilities.getWindowAncestor(parent);
	}

	private static GridBagConstraints cell(int x, int y, int anchor) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.anchor = anchor;
		gbc.insets = new Insets(4, 4, 4, 4);
		return gbc;
	}
}
