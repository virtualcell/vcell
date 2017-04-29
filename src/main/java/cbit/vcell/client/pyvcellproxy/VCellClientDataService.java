package cbit.vcell.client.pyvcellproxy;

import java.io.FileNotFoundException;
import java.util.List;

import org.vcell.util.DataAccessException;

import cbit.vcell.simdata.VtkManager;

public interface VCellClientDataService {
	public List<SimulationDataSetRef> getSimsFromOpenModels();
	public VtkManager getVtkManager(SimulationDataSetRef simulationDataSetRef) throws FileNotFoundException, DataAccessException;
	public void displayPostProcessingDataInVCell(SimulationDataSetRef simulationDataSetRef) throws NumberFormatException, DataAccessException;
}