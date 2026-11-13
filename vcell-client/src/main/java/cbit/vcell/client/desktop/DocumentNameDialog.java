package cbit.vcell.client.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.laf.VCellColors;

/**
 * Asks for the name to save a document under, in VCell's database.
 * <br/>
 * It starts with the document's current name, lists the user's own documents of that type with names related to what
 * has been typed (so one can be copied into the field with a click, and changed), and checks the name on every
 * change: a name VCell can't store safely, or exactly the name of one of those documents, disables Save and says why,
 * under the field.
 * <br/>
 * These checks are a courtesy to the user, not a security boundary: anything saving through another route skips them,
 * so the server must still escape names (it stores single quotes as <code>&amp;apos;</code>).
 */
@SuppressWarnings("serial")
public class DocumentNameDialog extends JDialog {

	/** What makes a name unusable, if anything; checked in this order, so the first problem found is the one shown. */
	public enum NameProblem {
		NONE,
		EMPTY,
		INVISIBLE_CHARACTERS, // line breaks, tabs, zero-width and direction-changing characters: break lists, or disguise a name
		MARKUP,               // < or >: Swing draws text starting with <html> as HTML, wherever the name is listed
		SURROUNDING_SPACES,   // looks the same as the name without them
		TOO_LONG,             // won't fit the database's name column
		TAKEN                 // exactly the name of one of the user's own documents of this type
	}

	/** The database's name column holds this many bytes, after a ' is stored as &amp;apos; (see VersionTable). */
	public static final int MAX_STORED_NAME_BYTES = 255;

	private final String documentTypeName;
	private final Set<String> takenNames;
	private final JTextField nameField = new JTextField(30);
	private final JLabel problemLabel = new JLabel(" "); // a space keeps its height while there's no problem, so nothing jumps
	private final DefaultListModel<String> matchingNamesModel = new DefaultListModel<>();
	private final JList<String> matchingNamesList = new JList<>(this.matchingNamesModel);
	private final JButton saveButton = new JButton("Save");
	private final JButton cancelButton = new JButton("Cancel");
	private String chosenName = null; // set only when Save closes the dialog

	/**
	 * @param owner the window the dialog belongs to
	 * @param documentTypeName how the document type reads in a sentence, e.g. "BioModel"
	 * @param initialName the name the field starts with, usually the document's current name; may be null
	 * @param takenNames the names the user's own documents of this type already have; a new name must differ from each
	 */
	public DocumentNameDialog(Window owner, String documentTypeName, String initialName, Collection<String> takenNames) {
		super(owner, "Save " + documentTypeName + " As", ModalityType.APPLICATION_MODAL);
		this.documentTypeName = documentTypeName;
		this.takenNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER.thenComparing(name -> name));
		this.takenNames.addAll(takenNames);
		this.setName("DocumentNameDialog");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(this.buildContent());
		this.wireBehavior();
		this.nameField.setText(initialName == null ? "" : initialName);
		this.nameField.selectAll(); // typing replaces the old name; an arrow key keeps it to edit
		this.refresh();
		this.pack();
		this.setMinimumSize(new Dimension(380, 300));
		this.setLocationRelativeTo(owner);
	}

	/** The name chosen with Save, or null if the dialog was cancelled or closed. */
	public String getChosenName() {
		return this.chosenName;
	}

	/** Why a name can't be used, if it can't; the taken check is exact, case and all. */
	public static NameProblem findProblem(String name, Set<String> takenNames) {
		if (name == null || name.isBlank()) return NameProblem.EMPTY;
		if (name.codePoints().anyMatch(DocumentNameDialog::isInvisible)) return NameProblem.INVISIBLE_CHARACTERS;
		if (name.indexOf('<') >= 0 || name.indexOf('>') >= 0) return NameProblem.MARKUP;
		if (!name.equals(name.strip())) return NameProblem.SURROUNDING_SPACES;
		if (DocumentNameDialog.storedNameBytes(name) > DocumentNameDialog.MAX_STORED_NAME_BYTES) return NameProblem.TOO_LONG;
		if (takenNames.contains(name)) return NameProblem.TAKEN;
		return NameProblem.NONE;
	}

	/** How many bytes the name takes in the database: UTF-8, with each ' stored as &amp;apos;. */
	public static int storedNameBytes(String name) {
		return name.replace("'", "&apos;").getBytes(StandardCharsets.UTF_8).length;
	}

	// control characters (line breaks, tabs, NUL...), and the invisible "format" ones: zero-width spaces and joiners,
	// and the direction overrides that can make a name display as something else
	private static boolean isInvisible(int codePoint) {
		if (Character.isISOControl(codePoint)) return true;
		int type = Character.getType(codePoint);
		return type == Character.FORMAT || type == Character.LINE_SEPARATOR || type == Character.PARAGRAPH_SEPARATOR;
	}

	/**
	 * The taken names related to the typed text, ignoring case: those containing it, and those it contains (so typing
	 * "FRAP model 2" lists "FRAP model"). All of them while nothing is typed.
	 */
	public static List<String> findMatchingNames(String typedText, Collection<String> takenNames) {
		String typed = typedText == null ? "" : typedText.strip().toLowerCase(Locale.ROOT);
		List<String> matchingNames = new ArrayList<>();
		for (String takenName : takenNames) {
			String taken = takenName.toLowerCase(Locale.ROOT);
			if (!taken.contains(typed) && !typed.contains(taken)) continue;
			matchingNames.add(takenName);
		}
		return matchingNames;
	}

	private JPanel buildContent() {
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setLabelFor(this.nameField);
		nameLabel.setDisplayedMnemonic(KeyEvent.VK_N);
		this.nameField.setName("DocumentNameField");

		this.problemLabel.setName("DocumentNameProblem");
		this.problemLabel.setForeground(VCellColors.getErrorForeground());

		JLabel matchingNamesLabel = new JLabel("Similarly named " + this.documentTypeName + "s (click one to use its name):");
		matchingNamesLabel.setLabelFor(this.matchingNamesList);
		this.matchingNamesList.setName("ExistingDocumentNames");
		this.matchingNamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.matchingNamesList.setVisibleRowCount(8);
		JScrollPane matchingNamesScroller = new JScrollPane(this.matchingNamesList);

		this.saveButton.setName("SaveDocumentNameButton");
		this.cancelButton.setName("CancelDocumentNameButton");
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING, 6, 0));
		buttons.add(this.cancelButton);
		buttons.add(this.saveButton);

		JPanel content = new JPanel(new GridBagLayout());
		content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 8);
		content.add(nameLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		content.add(this.nameField, gbc);

		gbc.gridy = 1;
		gbc.insets = new Insets(4, 0, 10, 0);
		content.add(this.problemLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 4, 0);
		content.add(matchingNamesLabel, gbc);

		gbc.gridy = 3;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 12, 0);
		content.add(matchingNamesScroller, gbc);

		gbc.gridy = 4;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		content.add(buttons, gbc);

		JPanel root = new JPanel(new BorderLayout());
		root.add(content, BorderLayout.CENTER);
		return root;
	}

	private void wireBehavior() {
		this.nameField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { DocumentNameDialog.this.refresh(); }
			@Override public void removeUpdate(DocumentEvent e) { DocumentNameDialog.this.refresh(); }
			@Override public void changedUpdate(DocumentEvent e) { DocumentNameDialog.this.refresh(); }
		});

		// Down from the field goes into the list of names; Enter or a click there copies the name into the field
		this.nameField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "goToNames");
		this.nameField.getActionMap().put("goToNames", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (DocumentNameDialog.this.matchingNamesModel.isEmpty()) return;
				DocumentNameDialog.this.matchingNamesList.setSelectedIndex(0);
				DocumentNameDialog.this.matchingNamesList.requestFocusInWindow();
			}
		});
		this.matchingNamesList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "useName");
		this.matchingNamesList.getActionMap().put("useName", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				DocumentNameDialog.this.useSelectedName();
			}
		});
		this.matchingNamesList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e)) return;
				DocumentNameDialog.this.useSelectedName();
			}
		});

		this.saveButton.addActionListener(e -> this.save());
		this.cancelButton.addActionListener(e -> this.dispose());
		this.getRootPane().setDefaultButton(this.saveButton); // Enter in the field saves, while Save is enabled
		this.getRootPane().registerKeyboardAction(e -> this.dispose(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	private void useSelectedName() {
		String selectedName = this.matchingNamesList.getSelectedValue();
		if (selectedName == null) return;
		this.nameField.setText(selectedName);
		this.nameField.requestFocusInWindow();
		// after the focus arrives, which selects the whole field: put the caret at the end, ready to add e.g. "_v2"
		SwingUtilities.invokeLater(() -> this.nameField.select(selectedName.length(), selectedName.length()));
	}

	private void save() {
		String name = this.nameField.getText();
		if (DocumentNameDialog.findProblem(name, this.takenNames) != NameProblem.NONE) return;
		this.chosenName = name;
		this.dispose();
	}

	// runs on every change to the field: re-checks the name and narrows the list
	private void refresh() {
		String name = this.nameField.getText();
		NameProblem problem = DocumentNameDialog.findProblem(name, this.takenNames);
		this.saveButton.setEnabled(problem == NameProblem.NONE);
		this.showProblem(problem, name);

		this.matchingNamesModel.clear();
		this.matchingNamesModel.addAll(DocumentNameDialog.findMatchingNames(name, this.takenNames));
	}

	private void showProblem(NameProblem problem, String name) {
		String message = switch (problem) {
			case NONE -> null;
			case EMPTY -> "Enter a name.";
			case INVISIBLE_CHARACTERS -> "Remove line breaks, tabs and other invisible characters.";
			case MARKUP -> "Names can't contain < or >.";
			case SURROUNDING_SPACES -> "Remove the spaces at the start or end.";
			case TOO_LONG -> "Too long to store: shorten it by "
					+ (DocumentNameDialog.storedNameBytes(name) - DocumentNameDialog.MAX_STORED_NAME_BYTES)
					+ " characters or more (a ' counts as 6).";
			case TAKEN -> "You already have a " + this.documentTypeName + " with this name. Choose a different name.";
		};
		this.problemLabel.setText(message == null ? " " : message);
		this.problemLabel.setIcon(problem == NameProblem.NONE || problem == NameProblem.EMPTY ? null : VCellIcons.issueErrorIcon);
		this.nameField.getAccessibleContext().setAccessibleDescription(message); // read out with the field
	}

	/**
	 * Shows the dialog over the given component and waits for it to close.
	 * @return the chosen name, or null if it was cancelled
	 */
	public static String ask(Component requester, String documentTypeName, String initialName, Collection<String> takenNames) throws Exception {
		if (!SwingUtilities.isEventDispatchThread()) { // some saves ask from a background task
			String[] chosenName = new String[1];
			SwingUtilities.invokeAndWait(() -> chosenName[0] = DocumentNameDialog.askOnSwingThread(requester, documentTypeName, initialName, takenNames));
			return chosenName[0];
		}
		return DocumentNameDialog.askOnSwingThread(requester, documentTypeName, initialName, takenNames);
	}

	private static String askOnSwingThread(Component requester, String documentTypeName, String initialName, Collection<String> takenNames) {
		Window owner = requester == null ? null : (requester instanceof Window window ? window : SwingUtilities.getWindowAncestor(requester));
		DocumentNameDialog dialog = new DocumentNameDialog(owner, documentTypeName, initialName, takenNames);
		DialogUtils.showModalJDialogOnTop(dialog, requester); // modal: returns once it closes
		return dialog.getChosenName();
	}
}
