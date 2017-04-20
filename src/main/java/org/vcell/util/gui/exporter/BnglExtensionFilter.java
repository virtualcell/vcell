package org.vcell.util.gui.exporter;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.vcell.model.rbm.RbmNetworkGenerator;
import org.vcell.util.UserCancelException;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class BnglExtensionFilter extends SelectorExtensionFilter {
	public BnglExtensionFilter() {
		super(".bngl", 	"BioNetGen (BNGL) file (*.bngl)",
			Selector.SPATIAL,Selector.NONSPATIAL,Selector.STOCHASTIC,Selector.DETERMINISTIC); //specifies BNGL requires single application of any type
	}

	@Override
	public boolean requiresMoreChoices() {
		return true;  //tells ChooseFile to call askUser
	}

	@Override
	public void askUser(ChooseContext ctx) throws UserCancelException {
		BioModel bioModel = ctx.chosenContext.getBioModel();
		boolean hasReactions = bioModel.getModel().getReactionSteps().length > 0 ? true : false;
		if(hasReactions) {					// mixed
			String errMsg = "Simple Reactions cannot be exported to .bngl format.";
			errMsg += "<br>Some information will be lost.";
			errMsg += "<br><br>Continue anyway?";
			errMsg = "<html>" + errMsg + "</html>";
	        int dialogButton = JOptionPane.YES_NO_OPTION;
	        int returnCode = JOptionPane.showConfirmDialog(ctx.topLevelWindowManager.getComponent(), errMsg, "Exporting to .bngl", dialogButton);
			if (returnCode != JOptionPane.YES_OPTION) {
				throw UserCancelException.CANCEL_FILE_SELECTION;
			}
		}
	}

	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext simulationContext) throws Exception {
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(bnglStringWriter);
		RbmNetworkGenerator.writeBngl(simulationContext, pw, false, true);
		String resultString = bnglStringWriter.toString();
		FileUtils.writeStringToFile(exportFile, resultString);
	}
}
