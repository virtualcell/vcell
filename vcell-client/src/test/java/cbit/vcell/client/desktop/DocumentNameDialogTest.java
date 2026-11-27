package cbit.vcell.client.desktop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.client.desktop.DocumentNameDialog.NameProblem;

@Tag("Fast")
public class DocumentNameDialogTest {
	private static final Set<String> TAKEN = Set.of("FRAP model", "frap_v2", "Calcium wave");

	@Test
	public void emptyOrBlankNamesAreRejected() {
		assertEquals(NameProblem.EMPTY, DocumentNameDialog.findProblem(null, DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.EMPTY, DocumentNameDialog.findProblem("", DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.EMPTY, DocumentNameDialog.findProblem("   ", DocumentNameDialogTest.TAKEN));
	}

	@Test
	public void onlyAnExactMatchIsTaken() {
		assertEquals(NameProblem.TAKEN, DocumentNameDialog.findProblem("FRAP model", DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.NONE, DocumentNameDialog.findProblem("frap model", DocumentNameDialogTest.TAKEN), "case differs");
		assertEquals(NameProblem.NONE, DocumentNameDialog.findProblem("FRAP model 2", DocumentNameDialogTest.TAKEN), "longer");
		assertEquals(NameProblem.NONE, DocumentNameDialog.findProblem("FRAP", DocumentNameDialogTest.TAKEN), "shorter");
	}

	@Test
	public void namesVCellCantStoreSafelyAreRejected() {
		assertEquals(NameProblem.INVISIBLE_CHARACTERS, DocumentNameDialog.findProblem("two\nlines", DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.INVISIBLE_CHARACTERS, DocumentNameDialog.findProblem("tab\there", DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.INVISIBLE_CHARACTERS, DocumentNameDialog.findProblem("null\u0000byte", DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.INVISIBLE_CHARACTERS, DocumentNameDialog.findProblem("zero\u200Bwidth", DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.INVISIBLE_CHARACTERS, DocumentNameDialog.findProblem("model\u202Efdp.exe", DocumentNameDialogTest.TAKEN), "right-to-left override");

		assertEquals(NameProblem.MARKUP, DocumentNameDialog.findProblem("<html><img src='http://example.com/x.png'>", DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.MARKUP, DocumentNameDialog.findProblem("a > b", DocumentNameDialogTest.TAKEN));

		assertEquals(NameProblem.SURROUNDING_SPACES, DocumentNameDialog.findProblem(" FRAP", DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.SURROUNDING_SPACES, DocumentNameDialog.findProblem("FRAP ", DocumentNameDialogTest.TAKEN));

		String longest = "a".repeat(DocumentNameDialog.MAX_STORED_NAME_BYTES);
		assertEquals(NameProblem.NONE, DocumentNameDialog.findProblem(longest, DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.TOO_LONG, DocumentNameDialog.findProblem(longest + "a", DocumentNameDialogTest.TAKEN));
		assertEquals(NameProblem.TOO_LONG, DocumentNameDialog.findProblem("'".repeat(43), DocumentNameDialogTest.TAKEN), "each ' is stored as 6 characters");
		assertEquals(NameProblem.TOO_LONG, DocumentNameDialog.findProblem("\u00E9".repeat(128), DocumentNameDialogTest.TAKEN), "an é is 2 bytes");
	}

	@Test
	public void ordinaryNamesAreAccepted() {
		for (String name : new String[] { "Ca2+ wave (v3)", "Moe's model", "FRAP: 2D, 37\u00B0C", "Mod\u00E8le d'\u00E9t\u00E9", "a/b & c; DROP TABLE x" }) {
			assertEquals(NameProblem.NONE, DocumentNameDialog.findProblem(name, DocumentNameDialogTest.TAKEN), name);
		}
	}

	@Test
	public void matchingNamesContainTheTypedTextIgnoringCase() {
		List<String> sortedTaken = List.of("Calcium wave", "FRAP model", "frap_v2");
		assertEquals(List.of("FRAP model", "frap_v2"), DocumentNameDialog.findMatchingNames("frap", sortedTaken));
		assertEquals(List.of("Calcium wave"), DocumentNameDialog.findMatchingNames("WAVE", sortedTaken));
		assertEquals(sortedTaken, DocumentNameDialog.findMatchingNames("", sortedTaken), "all of them before anything is typed");
		assertEquals(List.of(), DocumentNameDialog.findMatchingNames("nothing like it", sortedTaken));
	}

	@Test
	public void matchingNamesAlsoIncludeNamesContainedInTheTypedText() {
		List<String> sortedTaken = List.of("Calcium wave", "FRAP model", "frap_v2");
		assertEquals(List.of("FRAP model"), DocumentNameDialog.findMatchingNames("frap model 2", sortedTaken));
		assertEquals(List.of("Calcium wave", "FRAP model"), DocumentNameDialog.findMatchingNames("FRAP model, after Calcium wave", sortedTaken));
	}

	@Test
	public void saveIsBlockedWhileTheNameIsTakenOrEmpty() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to make a dialog");
		SwingUtilities.invokeAndWait(() -> {
			DocumentNameDialog dialog = new DocumentNameDialog(null, "BioModel", "FRAP model", DocumentNameDialogTest.TAKEN);
			try {
				JTextField nameField = DocumentNameDialogTest.find(dialog, JTextField.class, "DocumentNameField");
				JButton saveButton = DocumentNameDialogTest.find(dialog, JButton.class, "SaveDocumentNameButton");
				JLabel problemLabel = DocumentNameDialogTest.find(dialog, JLabel.class, "DocumentNameProblem");
				@SuppressWarnings("unchecked")
				JList<String> namesList = DocumentNameDialogTest.find(dialog, JList.class, "ExistingDocumentNames");

				assertEquals("FRAP model", nameField.getText(), "starts with the current name");
				assertFalse(saveButton.isEnabled(), "the current name is one of the user's own");
				assertTrue(problemLabel.getText().contains("already have a BioModel"), problemLabel.getText());

				nameField.setText("FRAP");
				assertTrue(saveButton.isEnabled(), "a new name can be saved");
				assertEquals(" ", problemLabel.getText(), "and the message clears");
				assertEquals(2, namesList.getModel().getSize(), "the list narrows to names containing what's typed");

				nameField.setText("FRAP model 2");
				assertTrue(saveButton.isEnabled());
				assertEquals(1, namesList.getModel().getSize(), "it contains \"FRAP model\"");

				nameField.setText("<html>FRAP");
				assertFalse(saveButton.isEnabled());
				assertEquals("Names can't contain < or >.", problemLabel.getText());

				nameField.setText(" ");
				assertFalse(saveButton.isEnabled());
				assertEquals("Enter a name.", problemLabel.getText());
			} finally {
				dialog.dispose();
			}
		});
	}

	private static <T extends Component> T find(Container container, Class<T> type, String name) {
		for (Component child : container.getComponents()) {
			if (type.isInstance(child) && name.equals(child.getName())) return type.cast(child);
			if (!(child instanceof Container childContainer)) continue;
			T found = DocumentNameDialogTest.find(childContainer, type, name);
			if (found != null) return found;
		}
		return null;
	}
}
