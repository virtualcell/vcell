package cbit.vcell.client.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.DatabaseModelsPanel;
import cbit.vcell.desktop.RecentModelsPanel;

/**
 * Chooses a saved document of one type from the VCell database, the way the welcome window lists models: a search
 * over everything, the user's recent ones, and the database's Community, Personal and Tutorial lists. It replaces the
 * old database tree dialogs (File &gt; Open, Compare With Another Model, choosing a model's geometry, and so on).
 * <br/>
 * Double-click, Enter or Select chooses the highlighted one; Cancel or Esc gives up. Unlike the welcome window, it has
 * no right-click menu: here a model is only being chosen.
 */
@SuppressWarnings("serial")
public class ModelPickerDialog extends JDialog {
	private final WelcomePanel.SearchField searchField;
	private final Timer searchPause = new Timer(150, e -> this.search()); // searching once typing pauses, not at each key
	private final RecentModelsPanel recentModels;
	private final DatabaseModelsPanel databaseModels;
	private final JTabbedPane tabs = new JTabbedPane();
	private final JButton selectButton;
	private final JButton cancelButton = new JButton("Cancel");
	private VCDocumentInfo chosen = null;

	/**
	 * @param title what's being chosen for, e.g. "Open BioModel" or "Compare With Another BioModel"
	 * @param selectLabel the choosing button's text, e.g. "Open" or "Select"
	 */
	public ModelPickerDialog(Window owner, String title, String selectLabel, DocumentManager documentManager, VCDocumentType documentType) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		this.setName("ModelPickerDialog");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		String typeName = ModelPickerDialog.typeName(documentType);
		this.searchField = new WelcomePanel.SearchField("Search " + typeName + "s");
		this.recentModels = new RecentModelsPanel(documentType);
		this.databaseModels = new DatabaseModelsPanel(documentType);
		this.selectButton = new JButton(selectLabel);

		this.recentModels.setDocumentManager(documentManager);
		this.databaseModels.setDocumentManager(documentManager);
		this.recentModels.setOpener(this::choose); // double-click or Enter
		this.databaseModels.setOpener(this::choose);
		this.recentModels.addSelectionListener(this::updateSelectButton);
		this.databaseModels.addSelectionListener(this::updateSelectButton);

		this.setContentPane(this.buildContent(typeName));
		this.wireBehavior();
		// the user's recent ones first, when they have some of this type
		this.tabs.setSelectedComponent(this.recentModels.hasModels() ? this.recentModels : this.databaseModels);
		this.updateSelectButton();
		this.pack();
		this.setMinimumSize(new Dimension(420, 420));
		this.setLocationRelativeTo(owner);
	}

	/**
	 * Shows the picker over the requester and waits for a choice.
	 * @throws UserCancelException if the user cancels, as the old database tree dialogs did
	 */
	public static VCDocumentInfo pick(Component requester, String title, String selectLabel, DocumentManager documentManager, VCDocumentType documentType) throws UserCancelException {
		VCDocumentInfo[] chosen = new VCDocumentInfo[1];
		Runnable ask = () -> {
			Window owner = requester == null ? null : (requester instanceof Window window ? window : SwingUtilities.getWindowAncestor(requester));
			ModelPickerDialog dialog = new ModelPickerDialog(owner, title, selectLabel, documentManager, documentType);
			DialogUtils.showModalJDialogOnTop(dialog, requester); // modal: returns once it closes
			chosen[0] = dialog.chosen;
		};
		if (SwingUtilities.isEventDispatchThread()) {
			ask.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(ask); // some features ask from a background task
			} catch (Exception e) {
				throw new RuntimeException("Couldn't show the " + title + " dialog", e);
			}
		}
		if (chosen[0] == null) throw UserCancelException.CANCEL_GENERIC;
		return chosen[0];
	}

	/** The document chosen, once the dialog has closed; null if it was cancelled. */
	VCDocumentInfo getChosen() {
		return this.chosen;
	}

	/** "BioModel", "MathModel" or "Geometry". */
	public static String typeName(VCDocumentType documentType) {
		return switch (documentType) {
			case BIOMODEL_DOC -> "BioModel";
			case MATHMODEL_DOC -> "MathModel";
			case GEOMETRY_DOC -> "Geometry";
			default -> "Model";
		};
	}

	private JPanel buildContent(String typeName) {
		this.searchField.setName("ModelPickerSearch");
		this.searchField.setToolTipText("Finds " + typeName + "s by name, owner or description, best matches first");
		this.searchField.putClientProperty("JTextField.variant", "search");

		this.tabs.setName("ModelPickerTabs");
		this.tabs.addTab("Recent", this.recentModels);
		this.tabs.addTab("VCell Database", this.databaseModels);
		this.tabs.setPreferredSize(new Dimension(460, 420));

		this.selectButton.setName("ModelPickerSelect");
		this.cancelButton.setName("ModelPickerCancel");
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING, 6, 0));
		buttons.add(this.cancelButton);
		buttons.add(this.selectButton);

		JPanel content = new JPanel(new BorderLayout(0, 10));
		content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		content.add(this.searchField, BorderLayout.NORTH);
		content.add(this.tabs, BorderLayout.CENTER);
		content.add(buttons, BorderLayout.SOUTH);
		return content;
	}

	private void wireBehavior() {
		this.searchPause.setRepeats(false);
		this.searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { ModelPickerDialog.this.searchPause.restart(); }
			@Override public void removeUpdate(DocumentEvent e) { ModelPickerDialog.this.searchPause.restart(); }
			@Override public void changedUpdate(DocumentEvent e) { ModelPickerDialog.this.searchPause.restart(); }
		});
		this.tabs.addChangeListener(e -> this.updateSelectButton());
		this.selectButton.addActionListener(e -> this.choose(this.selectedModel()));
		this.cancelButton.addActionListener(e -> this.dispose());
		this.getRootPane().setDefaultButton(this.selectButton); // Enter, wherever focus is, while something is selected
		this.getRootPane().registerKeyboardAction(e -> this.dispose(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	private void search() {
		String text = this.searchField.getText();
		this.recentModels.setSearchText(text);
		this.databaseModels.setSearchText(text);
	}

	// the model highlighted in the tab showing now
	private VCDocumentInfo selectedModel() {
		return this.tabs.getSelectedComponent() == this.recentModels ? this.recentModels.getSelectedModel() : this.databaseModels.getSelectedModel();
	}

	private void updateSelectButton() {
		this.selectButton.setEnabled(this.selectedModel() != null);
	}

	private void choose(VCDocumentInfo model) {
		if (model == null) return;
		this.chosen = model;
		this.dispose();
	}
}
