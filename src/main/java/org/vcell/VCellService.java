package org.vcell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.xml.stax.SBMLReader;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.vcell.vcellij.api.SBMLModel;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationService;
import org.vcell.vcellij.api.SimulationSpec;
import org.vcell.vcellij.api.SimulationState;
import org.vcell.vcellij.api.ThriftDataAccessException;
import org.vcell.vcellij.api.VariableInfo;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * Created by kevingaffney on 7/10/17.
 */
@Plugin(type = Service.class)
public class VCellService extends AbstractService {
	
	@Parameter
	private OpService opService;
	
	@Parameter
	private DatasetService datasetService;
    
    public Task<List<Dataset>, SimulationState> runSimulation(VCellModel model, SimulationSpec simSpec,
    		List<Species> outputSpecies, boolean shouldCreateIndividualDatasets) {
    	
		try {
			TTransport transport = setupTransport();
	    	SimulationService.Client client = setupClient(transport);
	        Task<List<Dataset>, SimulationState> task = runSimulation(
	        		client, model, simSpec, outputSpecies, shouldCreateIndividualDatasets, opService, datasetService);
	        task.addPropertyChangeListener(propertyChangeEvent -> {
	        	if (propertyChangeEvent.getPropertyName() == Task.STATE 
	        			&& propertyChangeEvent.getNewValue() == SwingWorker.StateValue.DONE) {
	        		transport.close();
	        	}
	        });
	        return task;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public SBMLDocument getSBML(String vcml, String applicationName) {
    	try {
			TTransport transport = setupTransport();
	    	SimulationService.Client client = setupClient(transport);
	        SBMLDocument document = getSBML(client, vcml, applicationName);
	        transport.close();
	        return document;
		} catch (Throwable e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    private TTransport setupTransport() throws TTransportException {
    	TTransport transport;
        transport = new TSocket("localhost", 9091);
        transport.open();
        return transport;
    }
    
    private SimulationService.Client setupClient(TTransport transport) {
        TProtocol protocol = new  TBinaryProtocol(transport);
    	SimulationService.Client client = new SimulationService.Client(protocol);
        return client;
    }
    
    private static SBMLDocument getSBML(SimulationService.Client client, String vcml, String applicationName) throws ThriftDataAccessException, TException, XMLStreamException, IOException {
    	String sbml = client.getSBML(vcml, applicationName);
    	SBMLReader reader = new SBMLReader();
    	SBMLDocument document = reader.readSBMLFromString(sbml);
    	return document;
    }
    
    private static Task<List<Dataset>, SimulationState> runSimulation(
    		SimulationService.Client client, VCellModel vCellModel, SimulationSpec simSpec, List<Species> outputSpecies, 
    		boolean shouldCreateIndividualDatasets, OpService opService, DatasetService datasetService) 
    		throws TException, IOException, XMLStreamException {
    	
    	final Task<List<Dataset>, SimulationState> task = new Task<List<Dataset>, SimulationState>() {
    		
			@Override
			protected List<Dataset> doInBackground() throws Exception {
				
				setSubtask(SimulationState.notRun);
				
				File sbmlSpatialFile = new File(vCellModel.getName() + ".xml");
		    	new SBMLWriter().write(vCellModel.getSbmlDocument(), sbmlSpatialFile);

		    	SBMLModel model = new SBMLModel();
		    	model.setFilepath(sbmlSpatialFile.getAbsolutePath());
		        SimulationInfo simulationInfo = client.computeModel(model, simSpec);
		        try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		        setSubtask(SimulationState.running);
		        while (client.getStatus(simulationInfo).getSimState()==SimulationState.running){
		        	System.out.println("waiting for simulation results");
		        	try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		        }
		        
		        if (client.getStatus(simulationInfo).getSimState() == SimulationState.failed) {
		        	setSubtask(SimulationState.failed);
		        	return null;
		        }
		        
		        List<Dataset> results = new ArrayList<>();
		        List<VariableInfo> vars = client.getVariableList(simulationInfo);
		        List<Double> times = client.getTimePoints(simulationInfo);
		        
		        for (VariableInfo var : vars) {
		        	
		        	if (outputSpecies.stream()
		        			.anyMatch(species -> species.getId().equals(var.getVariableVtuName()))) {
		        		
		        		// Get data for first time point and determine dimensions
		        		List<Double> data = client.getData(simulationInfo, var, 0);
		        		int[] dimensions = getDimensions(data, times);
		        		Img<DoubleType> img = opService.create().img(dimensions);
		        		RandomAccess<DoubleType> imgRA = img.randomAccess();
		        		
		        		// Copy data to the ImgLib2 Img
		        		for (int t = 0; t < times.size(); t++) {
		        			data = client.getData(simulationInfo, var, t);
		        			for (int d = 0; d < data.size(); d++) {
		        				imgRA.setPosition(new int[] {d, t});
		        				imgRA.get().set(data.get(d));
		        			}
		        		}
		        		
		        		// Create ImageJ Dataset and add to results
		        		Dataset dataset = datasetService.create(img);
		        		dataset.setName(var.getVariableVtuName());
		        		results.add(dataset);
		        	}
		        }
		        
		        
		        // If desired, add all datasets with the same dimensions
		        if (!shouldCreateIndividualDatasets && !results.isEmpty()) {
		        	
		        	// First, group datasets according to dimensions
		        	List<List<Dataset>> datasetGroups = new ArrayList<>();
		        	List<Dataset> initialGroup = new ArrayList<>();
		        	initialGroup.add(results.get(0));
		        	datasetGroups.add(initialGroup);

		        	for (int i = 1; i < results.size(); i++) {
		        		Dataset result = results.get(i);
		        		for (List<Dataset> datasetGroup : datasetGroups) {
		        			Dataset[] datasets = new Dataset[] {
		        					datasetGroup.get(0),
		        					result
		        			}; 
		        			if (Datasets.areSameSize(datasets, 0, 1)) {
		        				datasetGroup.add(result);
		        			} else {
		        				List<Dataset> newGroup = new ArrayList<>();
		        				newGroup.add(result);
		        				datasetGroups.add(newGroup);
		        			}
		        		}
		        	}
		        	
		        	List<Dataset> summedResults = new ArrayList<>();
		        	for (List<Dataset> datasetGroup : datasetGroups) {
		        		Img<DoubleType> sum = opService.create().img(datasetGroup.get(0));
		        		for (Dataset dataset : datasetGroup) {
		        			@SuppressWarnings("unchecked")
							RandomAccessibleInterval<DoubleType> current = (Img<DoubleType>) dataset.getImgPlus().getImg();
		        			opService.math().add(sum, sum, current);
		        		}
		        		Dataset result = datasetService.create(sum);
		        		result.setName(datasetGroup.stream()
		        				.map(d -> d.getName())
		        				.collect(Collectors.joining("+")));
		        		summedResults.add(result);
		        	}
		        	return summedResults;
		        }
		        
		        setSubtask(SimulationState.done);
				return results;
			}
    	};
    	
    	return task;
    }
    
    private static int[] getDimensions(List<Double> data, List<Double> times) {
		int[] dimensions = null;
		if (data.size() == 1) {
			dimensions = new int[] {
					times.size()
			};
		} else {
			dimensions = new int[] {
					data.size(),
					times.size()
			};
		}
		return dimensions;
    }
}
