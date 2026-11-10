package cbit.vcell.client.desktop.biomodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.client.desktop.biomodel.UnitSystemLibrary.NamedUnitSystem;
import cbit.vcell.model.ModelUnitSystem;

@Tag("Fast")
public class UnitSystemChooserPanelTest {
	private final UnitSystemLibrary library = new UnitSystemLibrary(new UnitSystemLibraryTest.MemoryStore());
	private final List<ModelUnitSystem> conversions = new ArrayList<>();
	private boolean conversionWorks = true;

	private UnitSystemChooserPanel chooser() {
		return new UnitSystemChooserPanel(this.library, units -> {
			this.conversions.add(units);
			return this.conversionWorks;
		});
	}

	@Test
	public void showsTheSystemWithTheModelsUnits() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			UnitSystemChooserPanel chooser = this.chooser();
			chooser.setModelUnits(ModelUnitSystem.createDefaultVCModelUnitSystem());
			assertEquals("VCell Default", chooser.getChosen().name());
			assertEquals(2, chooser.getComboBox().getItemCount(), "nothing custom to add");
			assertFalse(chooser.getDeleteButton().isEnabled(), "built in");
		});
	}

	@Test
	public void unlistedUnitsAreShownAsTheModelsOwn() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			UnitSystemChooserPanel chooser = this.chooser();
			chooser.setModelUnits(UnitSystemLibraryTest.MILLIMETERS);
			assertEquals(UnitSystemChooserPanel.CUSTOM_NAME, chooser.getChosen().name());
			assertEquals(3, chooser.getComboBox().getItemCount());
			assertFalse(chooser.getDeleteButton().isEnabled(), "nothing saved to delete");
		});
	}

	@Test
	public void choosingAnotherSystemConvertsTheModel() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			UnitSystemChooserPanel chooser = this.chooser();
			chooser.setModelUnits(ModelUnitSystem.createDefaultVCModelUnitSystem());
			chooser.getComboBox().setSelectedIndex(1); // SBML Compatible
			assertEquals(1, this.conversions.size());
			assertTrue(this.library.find("SBML Compatible").hasUnits(this.conversions.get(0)));
		});
	}

	@Test
	public void afterConvertingItShowsTheChosenSystem() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			UnitSystemChooserPanel[] chooser = new UnitSystemChooserPanel[1];
			chooser[0] = new UnitSystemChooserPanel(this.library, units -> {
				chooser[0].setModelUnits(units); // as the model window does once the converted model replaces the open one
				return true;
			});
			chooser[0].setModelUnits(ModelUnitSystem.createDefaultVCModelUnitSystem());
			chooser[0].getComboBox().setSelectedIndex(1);
			assertEquals("SBML Compatible", chooser[0].getChosen().name());
			assertEquals(2, chooser[0].getComboBox().getItemCount());
		});
	}

	@Test
	public void sameUnitsUnderAnotherNameShowTheNameLastChosen() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			this.library.add("Also default", ModelUnitSystem.createDefaultVCModelUnitSystem());
			UnitSystemChooserPanel chooser = this.chooser();
			chooser.setModelUnits(ModelUnitSystem.createDefaultVCModelUnitSystem());
			assertEquals("VCell Default", chooser.getChosen().name(), "the first with the model's units");
			chooser.getComboBox().setSelectedIndex(2);
			assertTrue(this.conversions.isEmpty(), "nothing to convert");
			chooser.setModelUnits(ModelUnitSystem.createDefaultVCModelUnitSystem()); // e.g. the model is reloaded
			assertEquals("Also default", chooser.getChosen().name());
		});
	}

	@Test
	public void aFailedConversionGoesBackToTheModelsUnits() throws Exception {
		this.conversionWorks = false;
		SwingUtilities.invokeAndWait(() -> {
			UnitSystemChooserPanel chooser = this.chooser();
			chooser.setModelUnits(ModelUnitSystem.createDefaultVCModelUnitSystem());
			chooser.getComboBox().setSelectedIndex(1);
			assertEquals("VCell Default", chooser.getChosen().name());
		});
	}

	@Test
	public void theUsersOwnSystemsCanBeDeletedAndTheListFollowsTheLibrary() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			UnitSystemChooserPanel chooser = this.chooser();
			chooser.setModelUnits(UnitSystemLibraryTest.MILLIMETERS);
			NamedUnitSystem mine = this.library.add("Millimeters", UnitSystemLibraryTest.MILLIMETERS);
			assertEquals("Millimeters", chooser.getChosen().name(), "the model's units now have a name");
			assertEquals(3, chooser.getComboBox().getItemCount(), "no custom entry any more");
			assertTrue(chooser.getDeleteButton().isEnabled());
			this.library.remove(mine);
			assertEquals(UnitSystemChooserPanel.CUSTOM_NAME, chooser.getChosen().name());
			assertTrue(this.conversions.isEmpty(), "the model's units never changed");
		});
	}

	@Test
	public void theAddDialogChecksNamesAndUnitsAsTheyreTyped() throws Exception {
		Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to make a dialog");
		SwingUtilities.invokeAndWait(() -> {
			UnitSystemDialog dialog = UnitSystemDialog.forAdding(this.library, ModelUnitSystem.createDefaultVCModelUnitSystem());
			try {
				assertNull(dialog.problem());
				assertTrue(dialog.getFinishButton().isEnabled());

				dialog.getNameField().setText("VCell Default");
				assertTrue(dialog.problem().startsWith("There's already"));
				assertFalse(dialog.getFinishButton().isEnabled());

				dialog.getNameField().setText("Seconds and meters");
				dialog.getUnitField(0).setText("s"); // length
				assertTrue(dialog.problem().toLowerCase().contains("length unit"), dialog.problem());
				assertFalse(dialog.getFinishButton().isEnabled());

				dialog.getUnitField(0).setText("not-a-unit");
				assertEquals("VCell can't read the length unit \u201cnot-a-unit\u201d", dialog.problem());

				dialog.getUnitField(0).setText("mm");
				dialog.getUnitField(1).setText("mm2");
				dialog.getUnitField(2).setText("mm3");
				assertNull(dialog.problem());
				dialog.getFinishButton().doClick();
				assertEquals("Seconds and meters", dialog.getMade().name());
				assertEquals("mm", this.library.find("Seconds and meters").unitSystem().getLengthUnit().getSymbol());
			} finally {
				dialog.dispose();
			}
		});
	}
}
