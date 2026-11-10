package cbit.vcell.client.desktop.biomodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.MathematicsSummary.Group;
import cbit.vcell.client.desktop.biomodel.MathematicsSummary.Where;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;

@Tag("Fast")
public class MathematicsSummaryTest {

	private static BioModel model() throws Exception {
		BioModel bioModel = new BioModel(null);
		bioModel.getModel().addFeature("c0");
		ModelParameter number = bioModel.getModel().createModelParameter(); // g0 = 0
		ModelParameter function = bioModel.getModel().createModelParameter(); // g1 = g0 * 2
		function.setExpression(new Expression(number.getName() + " * 2"));
		bioModel.addNewSimulationContext("First", SimulationContext.Application.NETWORK_DETERMINISTIC);
		bioModel.addNewSimulationContext("Second", SimulationContext.Application.NETWORK_DETERMINISTIC);
		return bioModel;
	}

	private static Group find(List<Group> groups, String label) {
		return groups.stream().filter(group -> group.label().equals(label)).findFirst().orElseThrow(() -> new AssertionError("no " + label + " in " + groups));
	}

	@Test
	public void countsParametersAndFunctionsWhereTheyreDefined() throws Exception {
		List<Group> groups = MathematicsSummary.of(MathematicsSummaryTest.model());

		Group globalParameters = MathematicsSummaryTest.find(groups, "Global parameters");
		assertEquals(1, globalParameters.parameters(), "g0 is a number");
		assertEquals(1, globalParameters.functions(), "g1 is an expression of g0");
		assertEquals(Where.GLOBAL, globalParameters.where());
		assertEquals(1, MathematicsSummaryTest.find(groups, "Structure sizes and voltages").total(), "c0's size");
		assertEquals(0, MathematicsSummaryTest.find(groups, "Reactions (0)").total());

		Group first = MathematicsSummaryTest.find(groups, "“First”");
		assertEquals(Where.APPLICATION, first.where());
		assertEquals("First", first.application().getName());
		assertTrue(first.total() > 0, "its compartment's size, at least");
	}

	@Test
	public void eachHeadingHoldsWhatTheTablesFilterListsForIt() throws Exception {
		BioModel bioModel = MathematicsSummaryTest.model();
		List<Group> groups = MathematicsSummary.of(bioModel);

		Group global = MathematicsSummaryTest.find(groups, "Global");
		assertTrue(global.heading());
		assertEquals(BioModelParametersTableModel.getGlobalEntries(bioModel).size(), global.total());
		Group applications = MathematicsSummaryTest.find(groups, "Applications (2)");
		assertTrue(applications.heading());
		assertNull(applications.application(), "all of them");
		int listed = 0;
		for (SimulationContext application : bioModel.getSimulationContexts()) listed += BioModelParametersTableModel.getApplicationEntries(application).size();
		assertEquals(listed, applications.total());

		// a heading's numbers are its groups'
		for (int i = 0; i < groups.size(); i++) {
			Group heading = groups.get(i);
			if (!heading.heading()) continue;
			int sum = 0;
			for (int j = i + 1; j < groups.size() && !groups.get(j).heading(); j++) sum += groups.get(j).total();
			assertEquals(heading.total(), sum, heading.label());
		}
		assertEquals(List.of("Global", "Reactions and Rules", "Applications (2)"), groups.stream().filter(Group::heading).map(Group::label).toList());
	}

	@Test
	public void noModelHasNothingToCount() {
		assertTrue(MathematicsSummary.of(null).isEmpty());
	}

	@Test
	public void listsTheMeasuredConstantsWithTheModelsValues() throws Exception {
		BioModel bioModel = MathematicsSummaryTest.model();
		List<MathematicsSummary.Constant> constants = MathematicsSummary.measuredConstants(bioModel);
		assertEquals(List.of("_F_", "_F_nmol_", "_N_pmol_", "_R_", "KMOLE"), constants.stream().map(MathematicsSummary.Constant::symbol).toList());
		for (MathematicsSummary.Constant constant : constants) {
			double modelValue = bioModel.getModel().getReservedSymbolByName(constant.symbol()).getExpression().evaluateConstant();
			assertEquals(modelValue, constant.value(), constant.symbol());
			assertFalse(constant.about().isBlank(), constant.symbol());
		}
		assertTrue(MathematicsSummary.measuredConstants(null).isEmpty());
	}

	@Test
	public void valuesReadPlainlyOrAsPowersOfTen() {
		assertEquals("96485.3321", MathematicsSummary.formatValue(9.64853321e4));
		assertEquals("8314.462618", MathematicsSummary.formatValue(8314.46261815), "to ten significant figures");
		assertEquals("0.001660538783", MathematicsSummary.formatValue(1.0 / 602.214179));
		assertEquals("6.02214179 \u00d7 10\u00b9\u00b9", MathematicsSummary.formatValue(6.02214179e11));
		assertEquals("9.64853321 \u00d7 10\u207b\u2075", MathematicsSummary.formatValue(9.64853321e-5));
		assertEquals("0", MathematicsSummary.formatValue(0));
	}
}
