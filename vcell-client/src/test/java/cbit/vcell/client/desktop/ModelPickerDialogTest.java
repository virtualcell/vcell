package cbit.vcell.client.desktop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.GeometryInfo;

@Tag("Fast")
public class ModelPickerDialogTest {
	private static final User ME = new User("me", new KeyValue("1"));
	private int nextKey = 100;

	@Test
	public void typeNamesReadAsInVCell() {
		assertEquals("BioModel", ModelPickerDialog.typeName(VCDocumentType.BIOMODEL_DOC));
		assertEquals("MathModel", ModelPickerDialog.typeName(VCDocumentType.MATHMODEL_DOC));
		assertEquals("Geometry", ModelPickerDialog.typeName(VCDocumentType.GEOMETRY_DOC));
	}

	@Test
	public void listsOnlyTheRequestedTypeAndChoosesTheSelectedOne() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to make a dialog");
		BioModelInfo frap = new BioModelInfo(this.version("FRAP"), (String) null, null);
		MathModelInfo math = new MathModelInfo(this.version("Math"), new KeyValue("9"), (String) null, null);
		GeometryInfo cell = new GeometryInfo(this.version("Cell"), 2, null, null, null, null);
		DocumentManager documentManager = this.documentManager(List.of(frap), List.of(math), List.of(cell));

		SwingUtilities.invokeAndWait(() -> {
			ModelPickerDialog dialog = new ModelPickerDialog(null, "Select BioModel", "Select", documentManager, VCDocumentType.BIOMODEL_DOC);
			try {
				JButton select = ModelPickerDialogTest.find(dialog, JButton.class, "ModelPickerSelect");
				assertFalse(select.isEnabled(), "nothing chosen yet");

				// the database's Personal list (the user's own): only the BioModel, not the MathModel or Geometry
				ModelPickerDialogTest.find(dialog, JTabbedPane.class, "ModelPickerTabs").setSelectedIndex(1);
				JTabbedPane databaseTabs = ModelPickerDialogTest.find(dialog, JTabbedPane.class, "DatabaseModelsTabs");
				databaseTabs.setSelectedIndex(databaseTabs.indexOfTab("Personal (1)"));
				@SuppressWarnings("unchecked")
				JList<VCDocumentInfo> personal = ModelPickerDialogTest.find(dialog, JList.class, "MINEModelsList");
				assertEquals(List.of(frap), ModelPickerDialogTest.items(personal.getModel()));

				personal.setSelectedIndex(0);
				assertTrue(select.isEnabled(), "a model is selected");
				select.doClick();
				assertEquals(frap, dialog.getChosen());
			} finally {
				dialog.dispose();
			}
		});
	}

	@Test
	public void cancellingChoosesNothing() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to make a dialog");
		DocumentManager documentManager = this.documentManager(List.of(), List.of(), List.of());
		SwingUtilities.invokeAndWait(() -> {
			ModelPickerDialog dialog = new ModelPickerDialog(null, "Select Geometry", "Select", documentManager, VCDocumentType.GEOMETRY_DOC);
			ModelPickerDialogTest.find(dialog, JButton.class, "ModelPickerCancel").doClick();
			assertNull(dialog.getChosen());
			dialog.dispose();
		});
	}

	private static List<VCDocumentInfo> items(ListModel<VCDocumentInfo> model) {
		List<VCDocumentInfo> items = new ArrayList<>();
		for (int i = 0; i < model.getSize(); i++) items.add(model.getElementAt(i));
		return items;
	}

	private static <T extends Component> T find(Container container, Class<T> type, String name) {
		T found = ModelPickerDialogTest.search(container, type, name);
		assertNotNull(found, "no " + type.getSimpleName() + " named " + name);
		return found;
	}

	private static <T extends Component> T search(Container container, Class<T> type, String name) {
		for (Component child : container.getComponents()) {
			if (type.isInstance(child) && name.equals(child.getName())) return type.cast(child);
			if (!(child instanceof Container childContainer)) continue;
			T found = ModelPickerDialogTest.search(childContainer, type, name);
			if (found != null) return found;
		}
		return null;
	}

	private DocumentManager documentManager(List<BioModelInfo> bioModels, List<MathModelInfo> mathModels, List<GeometryInfo> geometries) {
		return (DocumentManager) Proxy.newProxyInstance(DocumentManager.class.getClassLoader(), new Class<?>[] { DocumentManager.class },
				(proxy, method, args) -> switch (method.getName()) {
					case "getUser" -> ModelPickerDialogTest.ME;
					case "getBioModelInfos" -> bioModels.toArray(new BioModelInfo[0]);
					case "getMathModelInfos" -> mathModels.toArray(new MathModelInfo[0]);
					case "getGeometryInfos" -> geometries.toArray(new GeometryInfo[0]);
					case "addDatabaseListener", "removeDatabaseListener" -> null;
					case "hashCode" -> System.identityHashCode(proxy);
					case "equals" -> proxy == args[0];
					default -> throw new UnsupportedOperationException(method.getName());
				});
	}

	private Version version(String name) {
		return new Version(new KeyValue(String.valueOf(this.nextKey++)), name, ModelPickerDialogTest.ME, new GroupAccessNone(), null,
				BigDecimal.valueOf(this.nextKey), new Date(1_700_000_000_000L), VersionFlag.Current, null);
	}
}
