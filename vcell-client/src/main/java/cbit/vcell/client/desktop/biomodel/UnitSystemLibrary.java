package cbit.vcell.client.desktop.biomodel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.model.ModelUnitSystem;
import ucar.units_vcell.TokenMgrError;

/**
 * The unit systems a BioModel's units can be chosen from, by name: VCell's two built-in ones, then those the user
 * added. The user's are kept on this computer (Java preferences), so every model window lists them.
 * <br/>
 * A model saves its units, not a name; a listed system is the model's when its units are the same.
 */
public class UnitSystemLibrary {
	private static final Logger logger = LogManager.getLogger(UnitSystemLibrary.class);

	public static final String VCELL_DEFAULT = "VCell Default";
	public static final String SBML_COMPATIBLE = "SBML Compatible";
	static final int MAX_NAME_LENGTH = 60;

	/** One named unit system. */
	public record NamedUnitSystem(String name, ModelUnitSystem unitSystem, boolean builtIn) {
		/** Whether these are exactly the given units. */
		public boolean hasUnits(ModelUnitSystem units) {
			return units != null && this.unitSystem.compareEqual(units);
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	/** Where the user's unit systems are kept, as text. */
	public interface Store {
		String read();
		void write(String text);
	}

	private static final String PREFERENCE_KEY = "userUnitSystems";
	private static UnitSystemLibrary shared = null;

	private final Store store;
	private final List<NamedUnitSystem> userSystems = new ArrayList<>();
	private final List<WeakReference<Runnable>> listeners = new ArrayList<>();

	public UnitSystemLibrary(Store store) {
		this.store = store;
		this.load();
	}

	/** The library every model window shares, kept in this user's preferences. */
	public static synchronized UnitSystemLibrary getShared() {
		if (UnitSystemLibrary.shared == null) {
			Preferences preferences = Preferences.userNodeForPackage(UnitSystemLibrary.class);
			UnitSystemLibrary.shared = new UnitSystemLibrary(new Store() {
				@Override public String read() { return preferences.get(UnitSystemLibrary.PREFERENCE_KEY, ""); }
				@Override public void write(String text) { preferences.put(UnitSystemLibrary.PREFERENCE_KEY, text); }
			});
		}
		return UnitSystemLibrary.shared;
	}

	/** VCell Default, SBML Compatible, then the user's in the order they were made. */
	public List<NamedUnitSystem> getAll() {
		List<NamedUnitSystem> all = new ArrayList<>();
		all.add(new NamedUnitSystem(UnitSystemLibrary.VCELL_DEFAULT, ModelUnitSystem.createDefaultVCModelUnitSystem(), true));
		all.add(new NamedUnitSystem(UnitSystemLibrary.SBML_COMPATIBLE, ModelUnitConverter.createSbmlModelUnitSystem(), true));
		all.addAll(this.userSystems);
		return all;
	}

	/** The listed system with this name, or null. */
	public NamedUnitSystem find(String name) {
		for (NamedUnitSystem system : this.getAll()) {
			if (system.name().equalsIgnoreCase(name)) return system;
		}
		return null;
	}

	/** The first listed system with exactly these units, or null. */
	public NamedUnitSystem findWithUnits(ModelUnitSystem units) {
		for (NamedUnitSystem system : this.getAll()) {
			if (system.hasUnits(units)) return system;
		}
		return null;
	}

	/** Why a name can't be used for a new unit system, or null if it can. */
	public String nameProblem(String name) {
		if (name == null || name.isBlank()) return "Enter a name";
		if (!name.equals(name.strip())) return "Remove the spaces at the start or end";
		if (name.length() > UnitSystemLibrary.MAX_NAME_LENGTH) return "Use at most " + UnitSystemLibrary.MAX_NAME_LENGTH + " characters";
		if (name.chars().anyMatch(Character::isISOControl)) return "Remove the tab, line break or other invisible character";
		if (this.find(name) != null) return "There's already a unit system named “" + this.find(name).name() + "”";
		return null;
	}

	/** Adds a unit system of the user's, saved for every model window. */
	public NamedUnitSystem add(String name, ModelUnitSystem units) {
		String problem = this.nameProblem(name);
		if (problem != null) throw new IllegalArgumentException(problem);
		NamedUnitSystem added = new NamedUnitSystem(name, units, false);
		this.userSystems.add(added);
		this.save();
		return added;
	}

	/** Removes one of the user's unit systems; the built-in ones stay. */
	public void remove(NamedUnitSystem system) {
		if (system.builtIn()) throw new IllegalArgumentException(system.name() + " is built in");
		if (this.userSystems.removeIf(mine -> mine.name().equals(system.name()))) this.save();
	}

	/** Called (on the caller's thread) whenever the list changes; held weakly, so it lasts as long as its owner keeps it. */
	public void addChangeListener(Runnable listener) {
		this.listeners.add(new WeakReference<>(listener));
	}

	// one system per line: its name, then its seven unit symbols, separated by tabs (which names can't contain)
	private void save() {
		StringBuilder text = new StringBuilder();
		for (NamedUnitSystem system : this.userSystems) {
			text.append(system.name());
			for (String symbol : UnitSystemLibrary.symbols(system.unitSystem())) text.append('\t').append(symbol);
			text.append('\n');
		}
		this.store.write(text.toString());
		this.listeners.removeIf(reference -> reference.get() == null);
		for (WeakReference<Runnable> reference : new ArrayList<>(this.listeners)) {
			Runnable listener = reference.get();
			if (listener != null) listener.run();
		}
	}

	private void load() {
		String text = this.store.read();
		if (text == null) return;
		for (String line : text.split("\n")) {
			String[] parts = line.split("\t");
			if (parts.length != 8) continue;
			try {
				ModelUnitSystem units = ModelUnitSystem.createVCModelUnitSystem(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
				if (this.nameProblem(parts[0]) != null) continue; // a duplicate, or edited by hand
				this.userSystems.add(new NamedUnitSystem(parts[0], units, false));
			} catch (RuntimeException | TokenMgrError e) { // the unit parser reports some bad symbols as an Error
				logger.warn("Skipping saved unit system \"" + parts[0] + "\": " + e.getMessage());
			}
		}
	}

	/** Volume substance, membrane substance, lumped reaction substance, volume, area, length, time: as createVCModelUnitSystem takes them. */
	static String[] symbols(ModelUnitSystem units) {
		return new String[] {
				units.getVolumeSubstanceUnit().getSymbol(),
				units.getMembraneSubstanceUnit().getSymbol(),
				units.getLumpedReactionSubstanceUnit().getSymbol(),
				units.getVolumeUnit().getSymbol(),
				units.getAreaUnit().getSymbol(),
				units.getLengthUnit().getSymbol(),
				units.getTimeUnit().getSymbol(),
		};
	}
}
