package cbit.vcell.client.desktop.biomodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.EditableSymbolTableEntry;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;

@Tag("Fast")
public class ParameterOriginTest {

	@Test
	public void eachEntryIsClassedByWhereItCameFrom() throws Exception {
		BioModel bioModel = new BioModel(null);
		Feature cytosol = bioModel.getModel().addFeature("cyt");
		SpeciesContext species = bioModel.getModel().createSpeciesContext(cytosol);
		SimpleReaction reaction = bioModel.getModel().createSimpleReaction(cytosol);
		SimulationContext application = bioModel.addNewSimulationContext("App", SimulationContext.Application.NETWORK_DETERMINISTIC);

		assertEquals(ParameterOrigin.USER_DECLARED, ParameterOrigin.of(bioModel.getModel().createModelParameter()), "a global parameter");
		assertEquals(ParameterOrigin.USER_DECLARED, ParameterOrigin.of(application.createSimulationContextParameter()), "an application parameter");
		assertEquals(ParameterOrigin.AUTO_GENERATED, ParameterOrigin.of(species), "made with its species");
		assertEquals(ParameterOrigin.AUTO_GENERATED, ParameterOrigin.of(cytosol.getStructureSize()), "made with its structure");
		for (KineticsParameter parameter : reaction.getKinetics().getKineticsParameters()) {
			assertEquals(ParameterOrigin.AUTO_GENERATED, ParameterOrigin.of(parameter), "made with its kinetic law: " + parameter.getName());
		}
		assertEquals(ParameterOrigin.PREEXISTING, ParameterOrigin.of(bioModel.getModel().getReservedSymbolByName("KMOLE")));
	}

	@Test
	public void preexistingHoldsVCellsOwnSymbolsAndFunctions() throws Exception {
		BioModel bioModel = new BioModel(null);
		List<EditableSymbolTableEntry> preexisting = ParameterOrigin.preexisting(bioModel.getModel());
		List<String> names = preexisting.stream().map(EditableSymbolTableEntry::getName).toList();
		assertTrue(names.contains("_F_"), "a reserved constant");
		assertTrue(names.contains("t"), "a reserved symbol");
		assertTrue(names.stream().anyMatch(name -> name.startsWith("sin(")), "a math function, by its declaration: " + names);
		assertTrue(preexisting.stream().allMatch(entry -> ParameterOrigin.of(entry) == ParameterOrigin.PREEXISTING));
		assertTrue(ParameterOrigin.preexisting(null).size() > 0, "the math functions need no model");
	}

	@Test
	public void predefinedFunctionsCantBeEdited() {
		ParameterOrigin.PredefinedEntry function = (ParameterOrigin.PredefinedEntry) ParameterOrigin.preexisting(null).get(0);
		assertFalse(function.isNameEditable() || function.isExpressionEditable() || function.isDescriptionEditable() || function.isUnitEditable());
		assertThrows(UnsupportedOperationException.class, () -> function.setName("mine"));
		assertEquals("Predefined", BioModelParametersTableModel.definedIn(function));
	}

	@Test
	public void filtersCountWhatTheyNarrow() {
		ParametersFilterButton.Filter defaults = ParametersFilterButton.Filter.defaults();
		assertEquals(0, defaults.changes());
		assertFalse(defaults.preexisting(), "VCell's own aren't listed at first");
		assertNull(defaults.application(), "every application");
		ParametersFilterButton.Filter narrowed = new ParametersFilterButton.Filter(true, false, true, null, true, false, true, true, true);
		assertEquals(3, narrowed.changes(), "no reactions, no functions, and the preexisting shown");
	}
}
