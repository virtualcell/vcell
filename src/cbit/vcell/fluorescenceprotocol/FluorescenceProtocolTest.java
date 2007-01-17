package cbit.vcell.fluorescenceprotocol;

import cbit.vcell.model.SpeciesContext;
/**
 * Insert the type's description here.
 * Creation date: (9/5/2006 3:58:57 PM)
 * @author: Anuradha Lakshminarayana
 */
public class FluorescenceProtocolTest {
/**
 * VCellBNGProtocolTest constructor comment.
 */
public static void main(String[] args) {

	try {
		// Create a dummy model with 5 species A, B, C, D, E. Set species A to be the selected species (SpeciesContext(0)).
		cbit.vcell.model.Model inputModel = cbit.vcell.model.ModelTest.getExampleForFluorescenceIndicatorProtocol();
		//cbit.vcell.model.Model inputModel = cbit.vcell.model.ModelTest.getExampleForFluorescenceLabelProtocol();
		cbit.vcell.model.SpeciesContext[] selectedSCs = {inputModel.getSpeciesContext("A_Cytosol"), inputModel.getSpeciesContext("C_Cytosol"), inputModel.getSpeciesContext("E_Cytosol")};

		FluorescenceProtocol fluorescenceProtocol = new FluorescenceProtocol();

		// Try Fluorescent Indicator Protocol
		SpeciesContext indicatorSpeciesContext = inputModel.getSpeciesContext("I_Cytosol");
		FluorescenceIndicatorProtocolSpec fluorescentIndicatorProtocolSpec = new FluorescenceIndicatorProtocolSpec(inputModel, selectedSCs, indicatorSpeciesContext);
		fluorescenceProtocol.setFluorescentProtocolSpec(fluorescentIndicatorProtocolSpec);
		fluorescenceProtocol.fluorescentIndicatorProtocol();

		// Try Fluorescent Label Protocol
		//FluorescenceLabelProtocolSpec fluorescentLabelProtocolSpec = new FluorescenceLabelProtocolSpec(inputModel, selectedSCs);
		//fluorescenceProtocol.setFluorescentProtocolSpec(fluorescentLabelProtocolSpec);
		//fluorescenceProtocol.fluorescentLabelProtocol();
	} catch (Exception e) {
		throw new RuntimeException("Could not create input model or run vcbngProtocol");
	}
}
}
