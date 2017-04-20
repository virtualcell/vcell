package org.vcell.util.gui.exporter;

import java.io.File;
import java.io.PrintWriter;
import java.util.Objects;

import org.vcell.solver.smoldyn.SmoldynFileWriter;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCAssert;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import static cbit.vcell.simdata.SimDataConstants.SMOLDYN_INPUT_FILE_EXTENSION;
@SuppressWarnings("serial")
public class SmoldynExtensionFilter extends SelectorExtensionFilter{
	/**
	 * key for hash
	 */
	public static final String SIM_KEY = "selectedSimulation";
	
	private static final String[] FNAMES = {SMOLDYN_INPUT_FILE_EXTENSION, ".txt"};
	private Simulation selectedSim = null;

	
	public SmoldynExtensionFilter() {
		super(FNAMES,"Smoldyn Input Files (.smoldynInput .txt)",Selector.SPATIAL,Selector.STOCHASTIC);
	}
	/**
	 * @return true
	 */
	@Override
	public boolean requiresMoreChoices() {
		return true; 
	}

	@Override
	public void askUser(ChooseContext ctx) throws UserCancelException {
		SimulationContext chosenSimContext = ctx.chosenContext;
		String[] simNames = new String[chosenSimContext.getSimulations().length];
		Simulation[] sims = chosenSimContext.getSimulations();
		for(int i=0; i< sims.length; i++)
		{
			simNames[i] = sims[i].getName();
		}
		Object choice = PopupGenerator.showListDialog(ctx.topLevelWindowManager, simNames, "Please select " + chosenSimContext.getName() + " simulation to export");
		if(choice == null)
		{
			throw UserCancelException.CANCEL_FILE_SELECTION;
		}
		String chosenSimulationName = (String)choice;
		Simulation chosenSimulation = chosenSimContext.getSimulation(chosenSimulationName);
		Objects.requireNonNull(chosenSimulation);
		ctx.hashTable.put(SIM_KEY, chosenSimulation); //PENDING delete
		selectedSim = chosenSimulation;
	}
	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext ignored) throws Exception {
		Objects.requireNonNull(selectedSim);
		
			int scanCount = selectedSim.getScanCount();
			if(scanCount > 1) // has parameter scan
			{
				String baseExportFileName = exportFile.getPath().substring(0, exportFile.getPath().indexOf("."));
				for(int i=0; i<scanCount; i++)
				{
					SimulationTask simTask = new SimulationTask(new SimulationJob(selectedSim, i, null),0);
					// Need to export each parameter scan into a separate file
					String newExportFileName = baseExportFileName + "_" + i + SMOLDYN_INPUT_FILE_EXTENSION;
					exportFile = new File(newExportFileName);
					
					PrintWriter pw = new PrintWriter(exportFile);
					SmoldynFileWriter smf = new SmoldynFileWriter(pw, true, null, simTask, false);
					smf.write();
					pw.close();	
				}
			}
			else if(scanCount == 1)// regular simulation, no parameter scan
			{
				SimulationTask simTask = new SimulationTask(new SimulationJob(selectedSim, 0, null),0);
				// export the simulation to the selected file
				PrintWriter pw = new PrintWriter(exportFile);
				SmoldynFileWriter smf = new SmoldynFileWriter(pw, true, null, simTask, false);
				smf.write();
				pw.close();
			}
			else
			{
				throw new Exception("Simulation scan count is smaller than 1.");
			}
		
	}
	
}
