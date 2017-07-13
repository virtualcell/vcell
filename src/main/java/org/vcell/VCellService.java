package org.vcell;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.sbml.jsbml.SBMLDocument;
import org.vcell.vcellij.api.SBMLModel;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationService;
import org.vcell.vcellij.api.SimulationSpec;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.vcell.vcellij.api.Dataset;

import io.scif.services.DatasetIOService;

/**
 * Created by kevingaffney on 7/10/17.
 */
public class VCellService {

    private VCellResultService vCellResultService;
    private DatasetIOService datasetIOService;

    public VCellService(VCellResultService vCellResultService) {
        this.vCellResultService = vCellResultService;
    }
    
    public void runSimulation(SBMLModel sbmlModel, FutureCallback<Dataset> callback) {
    	
    	sbmlModel.setFilepath("filepath");
    	SimulationSpec simSpec = new SimulationSpec();
        TTransport transport = new TSocket("localhost", 9091);
        TProtocol protocol = new  TBinaryProtocol(transport);
        SimulationService.Client client = new SimulationService.Client(protocol);
        ListeningExecutorService listeningExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        
    	ListenableFuture<Dataset> future = listeningExecutor.submit(new Callable<Dataset>() {
			@Override
			public Dataset call() throws Exception {
				transport.open();
				SimulationInfo simInfo = client.computeModel(sbmlModel, simSpec);
	            Dataset dataset = client.getDataset(simInfo);
				return dataset;
			}
    	});
    	
    	Executor executor = Executors.newSingleThreadExecutor();
    	
        Futures.addCallback(future, callback, executor);
        
        transport.close();
    }
}
