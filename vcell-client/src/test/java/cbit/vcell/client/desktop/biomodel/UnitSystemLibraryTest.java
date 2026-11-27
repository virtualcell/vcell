package cbit.vcell.client.desktop.biomodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.client.desktop.biomodel.UnitSystemLibrary.NamedUnitSystem;
import cbit.vcell.model.ModelUnitSystem;

@Tag("Fast")
public class UnitSystemLibraryTest {

	/** Keeps the saved text in memory, as preferences would. */
	static final class MemoryStore implements UnitSystemLibrary.Store {
		String text = "";
		@Override public String read() { return this.text; }
		@Override public void write(String text) { this.text = text; }
	}

	static final ModelUnitSystem MILLIMETERS = ModelUnitSystem.createVCModelUnitSystem("uM.um3", "molecules", "molecules", "mm3", "mm2", "mm", "s");

	@Test
	public void startsWithTheTwoBuiltInSystems() {
		List<NamedUnitSystem> all = new UnitSystemLibrary(new MemoryStore()).getAll();
		assertEquals(List.of("VCell Default", "SBML Compatible"), all.stream().map(NamedUnitSystem::name).toList());
		assertTrue(all.stream().allMatch(NamedUnitSystem::builtIn));
		assertTrue(all.get(0).hasUnits(ModelUnitSystem.createDefaultVCModelUnitSystem()));
	}

	@Test
	public void addedSystemsAreSavedAndComeBack() {
		MemoryStore store = new MemoryStore();
		new UnitSystemLibrary(store).add("Millimeters", UnitSystemLibraryTest.MILLIMETERS);

		UnitSystemLibrary reloaded = new UnitSystemLibrary(store);
		NamedUnitSystem millimeters = reloaded.find("Millimeters");
		assertNotNull(millimeters);
		assertTrue(millimeters.hasUnits(UnitSystemLibraryTest.MILLIMETERS));
		assertEquals("Millimeters", reloaded.getAll().get(2).name(), "after the built-in ones");
		assertEquals(millimeters, reloaded.findWithUnits(UnitSystemLibraryTest.MILLIMETERS));
	}

	@Test
	public void onlyTheUsersSystemsCanBeRemoved() {
		MemoryStore store = new MemoryStore();
		UnitSystemLibrary library = new UnitSystemLibrary(store);
		NamedUnitSystem mine = library.add("Mine", UnitSystemLibraryTest.MILLIMETERS);
		assertThrows(IllegalArgumentException.class, () -> library.remove(library.find("VCell Default")));
		library.remove(mine);
		assertNull(library.find("Mine"));
		assertNull(new UnitSystemLibrary(store).find("Mine"), "and stays removed");
	}

	@Test
	public void namesMustBeNewVisibleAndShort() {
		UnitSystemLibrary library = new UnitSystemLibrary(new MemoryStore());
		library.add("Mine", UnitSystemLibraryTest.MILLIMETERS);
		assertEquals("Enter a name", library.nameProblem("  "));
		assertTrue(library.nameProblem("vcell default").startsWith("There's already"), "case doesn't make a new name");
		assertTrue(library.nameProblem("MINE").startsWith("There's already"));
		assertEquals("Remove the spaces at the start or end", library.nameProblem(" Other"));
		assertTrue(library.nameProblem("tab\there").startsWith("Remove the tab"));
		assertTrue(library.nameProblem("x".repeat(61)).startsWith("Use at most"));
		assertNull(library.nameProblem("Other"));
	}

	@Test
	public void unreadableSavedLinesAreSkipped() {
		MemoryStore store = new MemoryStore();
		store.text = "Broken\tnot-a-unit\tmolecules\tmolecules\tum3\tum2\tum\ts\nshort line\n";
		UnitSystemLibrary library = new UnitSystemLibrary(store);
		assertEquals(2, library.getAll().size(), "only the built-in ones");
	}

	@Test
	public void listenersHearChanges() {
		UnitSystemLibrary library = new UnitSystemLibrary(new MemoryStore());
		int[] heard = { 0 };
		Runnable listener = () -> heard[0]++;
		library.addChangeListener(listener);
		NamedUnitSystem mine = library.add("Mine", UnitSystemLibraryTest.MILLIMETERS);
		library.remove(mine);
		assertEquals(2, heard[0]);
	}

	@Test
	public void unusedNamesGetANumber() {
		UnitSystemLibrary library = new UnitSystemLibrary(new MemoryStore());
		assertEquals("Mine", UnitSystemDialog.unusedName(library, "Mine"));
		library.add("Mine", UnitSystemLibraryTest.MILLIMETERS);
		assertEquals("Mine 2", UnitSystemDialog.unusedName(library, "Mine"));
	}
}
