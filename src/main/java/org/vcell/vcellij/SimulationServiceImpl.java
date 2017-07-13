package org.vcell.vcellij;


import java.beans.PropertyVetoException;

import org.apache.thrift.TException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.vcellij.api.Dataset;
import org.vcell.vcellij.api.SBMLModel;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationService;
import org.vcell.vcellij.api.SimulationSpec;
import org.vcell.vcellij.api.SimulationStatus;
import org.vcell.vcellij.api.ThriftDataAccessException;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;


/**
 * Created by kevingaffney on 7/12/17.
 */
public class SimulationServiceImpl implements SimulationService.Iface {

    @Override
    public Dataset getDataset(SimulationInfo simInfo) throws ThriftDataAccessException, TException {
        Dataset dataset = new Dataset();
        dataset.setFilepath("filepath");
        return dataset;
    }

    @Override
    public SimulationInfo computeModel(SBMLModel model, SimulationSpec simSpec) throws ThriftDataAccessException, TException {
    	try {
	        cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
	            @Override
				public void sendMessage(Priority p, ErrorType et, String message) {
	                System.err.println("LOGGER: msgLevel="+p+", msgType="+et+", "+message);
	                if (p == VCLogger.Priority.HighPriority) {
	                	throw new RuntimeException("Import failed : " + message);
	                }
	            }
	            public void sendAllMessages() {
	            }
	            public boolean hasMessages() {
	                return false;
	            }
	        };
	    	SBMLImporter importer = new SBMLImporter(model.getFilepath(),logger,true);
	    	BioModel bioModel = importer.getBioModel();
	    	SimulationContext simContext = bioModel.getSimulationContext(0);
	    	ClientTaskStatusSupport statusCallback = new ClientTaskStatusSupport() {
				
				@Override
				public void setProgress(int progress) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setMessage(String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean isInterrupted() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public int getProgress() {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {
					// TODO Auto-generated method stub
					
				}
			};
			MathMappingCallback callback = new MathMappingCallbackTaskAdapter(statusCallback);
			Simulation newsim = simContext.addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX,callback,NetworkGenerationRequirements.AllowTruncatedStandardTimeout);
	    	SimulationInfo simulationInfo = new SimulationInfo();
	        simulationInfo.setId(1);
	        return simulationInfo;
    	} catch (Exception e){
    		e.printStackTrace(System.out);
    		// remember the exceptiopn ... fail the status ... save the error message
    		return new SimulationInfo().setId(1);
    	}
    }

	@Override
	public SimulationStatus getStatus(SimulationInfo simInfo) throws ThriftDataAccessException, TException {
		// TODO Auto-generated method stub
		return null;
	}
}
