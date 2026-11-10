package cbit.vcell.client.desktop.biomodel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.EditableSymbolTableEntry;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;

/**
 * What a BioModel's Mathematics &amp; Units holds, counted by where it's defined: the same entries, grouped the same
 * way, as the Parameters and Functions table lists them, so a group's numbers are what that table shows for it.
 */
public final class MathematicsSummary {
	private MathematicsSummary() {}

	/** Where a group's entries are defined, as the table's "Defined In" filter chooses them. */
	public enum Where { GLOBAL, REACTIONS, APPLICATION }

	/**
	 * One group's counts. A heading is one of the table's "Defined In" choices, with all it lists; the groups under it
	 * break it down. <code>application</code> is set for one application's group (a heading's is null: all of them).
	 * A parameter is a number; a function, an expression of other symbols (the table's Type filter).
	 */
	public record Group(String label, Where where, SimulationContext application, boolean heading, int parameters, int functions) {
		public int total() {
			return this.parameters + this.functions;
		}
	}

	/**
	 * One of the measured physical constants VCell's equations use, with its value and unit in the model's unit system.
	 * <code>about</code> says what it is.
	 */
	public record Constant(String name, String symbol, double value, String unit, String about) {}

	// the measured constants, by role: their names, and what they are (pi and the pure scale factors aren't measured)
	private static final Object[][] MEASURED = {
			{ Model.ReservedSymbolRole.FARADAY_CONSTANT, "Faraday constant", "The charge of a mole of electrons: Avogadro's number \u00d7 the elementary charge" },
			{ Model.ReservedSymbolRole.FARADAY_CONSTANT_NMOLE, "Faraday constant (nmol)", "The Faraday constant, per nanomole" },
			{ Model.ReservedSymbolRole.N_PMOLE, "Avogadro's number (pmol)", "Avogadro's number, per picomole: the molecules in a picomole" },
			{ Model.ReservedSymbolRole.GAS_CONSTANT, "Gas constant", "The molar gas constant: Avogadro's number \u00d7 the Boltzmann constant" },
			{ Model.ReservedSymbolRole.KMOLE, "KMOLE", "1 / the molecules in 1 \u00b5M\u00b7\u00b5m\u00b3 (from Avogadro's number): converts fluxes between molecules and \u00b5M" },
	};

	/** The model's measured physical constants, whose accepted values science refines over time. */
	public static List<Constant> measuredConstants(BioModel bioModel) {
		List<Constant> constants = new ArrayList<>();
		if (bioModel == null) return constants;
		for (Object[] measured : MathematicsSummary.MEASURED) {
			Model.ReservedSymbol symbol = bioModel.getModel().getReservedSymbolByRole((Model.ReservedSymbolRole) measured[0]);
			if (symbol == null || symbol.getExpression() == null) continue;
			try {
				constants.add(new Constant((String) measured[1], symbol.getName(), symbol.getExpression().evaluateConstant(),
						symbol.getUnitDefinition().getSymbolUnicode(), (String) measured[2]));
			} catch (ExpressionException e) {
				// a constant is a number; one that isn't is left out
			}
		}
		return constants;
	}

	/** A constant's value as it reads best: plainly between 0.001 and a million, else as "6.02214179 \u00d7 10\u00b9\u00b9". */
	public static String formatValue(double value) {
		double size = Math.abs(value);
		if (size == 0 || size >= 1e-3 && size < 1e6) return new BigDecimal(value).round(new MathContext(10)).stripTrailingZeros().toPlainString();
		String[] parts = String.format("%.9E", value).split("E");
		String mantissa = new BigDecimal(parts[0]).stripTrailingZeros().toPlainString();
		return mantissa + " \u00d7 10" + MathematicsSummary.superscript(Integer.parseInt(parts[1]));
	}

	private static String superscript(int exponent) {
		String digits = "\u2070\u00b9\u00b2\u00b3\u2074\u2075\u2076\u2077\u2078\u2079";
		StringBuilder text = new StringBuilder(exponent < 0 ? "\u207b" : "");
		for (char digit : String.valueOf(Math.abs(exponent)).toCharArray()) text.append(digits.charAt(digit - '0'));
		return text.toString();
	}

	/**
	 * The groups, in the table's order: Global, with the model's own symbols by kind; Reactions and Rules, with its
	 * reactions (and reaction rules, if it has any); then Applications, with each application.
	 */
	public static List<Group> of(BioModel bioModel) {
		List<Group> groups = new ArrayList<>();
		if (bioModel == null) return groups;

		List<EditableSymbolTableEntry> globalParameters = new ArrayList<>();
		List<EditableSymbolTableEntry> species = new ArrayList<>();
		List<EditableSymbolTableEntry> structures = new ArrayList<>();
		List<EditableSymbolTableEntry> others = new ArrayList<>();
		for (EditableSymbolTableEntry entry : BioModelParametersTableModel.getGlobalEntries(bioModel)) {
			if (entry instanceof Model.ModelParameter) globalParameters.add(entry);
			else if (entry instanceof SpeciesContext) species.add(entry);
			else if (entry instanceof Structure.StructureSize || entry instanceof Membrane.MembraneVoltage) structures.add(entry);
			else others.add(entry);
		}
		List<Group> global = new ArrayList<>();
		global.add(MathematicsSummary.group("Global parameters", Where.GLOBAL, null, globalParameters));
		if (!species.isEmpty()) global.add(MathematicsSummary.group("Species", Where.GLOBAL, null, species));
		if (!structures.isEmpty()) global.add(MathematicsSummary.group("Structure sizes and voltages", Where.GLOBAL, null, structures));
		if (!others.isEmpty()) global.add(MathematicsSummary.group("Other model symbols", Where.GLOBAL, null, others));
		MathematicsSummary.addWithHeading(groups, "Global", Where.GLOBAL, global);

		List<Group> reactions = new ArrayList<>();
		int reactionCount = bioModel.getModel().getReactionSteps().length;
		reactions.add(MathematicsSummary.group(MathematicsSummary.counted(reactionCount, "Reaction", "Reactions"), Where.REACTIONS, null,
				BioModelParametersTableModel.getReactionEntries(bioModel, true)));
		if (!bioModel.getModel().getRbmModelContainer().isEmpty()) {
			int ruleCount = bioModel.getModel().getRbmModelContainer().getReactionRuleList().size();
			reactions.add(MathematicsSummary.group(MathematicsSummary.counted(ruleCount, "Reaction rule", "Reaction rules"), Where.REACTIONS, null,
					BioModelParametersTableModel.getReactionEntries(bioModel, false)));
		}
		MathematicsSummary.addWithHeading(groups, "Reactions and Rules", Where.REACTIONS, reactions);

		List<Group> applications = new ArrayList<>();
		for (SimulationContext application : bioModel.getSimulationContexts()) {
			applications.add(MathematicsSummary.group("“" + application.getName() + "”", Where.APPLICATION, application,
					BioModelParametersTableModel.getApplicationEntries(application)));
		}
		MathematicsSummary.addWithHeading(groups, MathematicsSummary.counted(applications.size(), "Application", "Applications"), Where.APPLICATION, applications);
		return groups;
	}

	private static Group group(String label, Where where, SimulationContext application, List<EditableSymbolTableEntry> entries) {
		int functions = (int) entries.stream().filter(BioModelParametersTableModel::isFunction).count();
		return new Group(label, where, application, false, entries.size() - functions, functions);
	}

	// the heading, with its groups' sums, then the groups
	private static void addWithHeading(List<Group> groups, String label, Where where, List<Group> parts) {
		int parameters = parts.stream().mapToInt(Group::parameters).sum();
		int functions = parts.stream().mapToInt(Group::functions).sum();
		groups.add(new Group(label, where, null, true, parameters, functions));
		groups.addAll(parts);
	}

	private static String counted(int count, String one, String many) {
		return (count == 1 ? one : many) + " (" + count + ")";
	}
}
