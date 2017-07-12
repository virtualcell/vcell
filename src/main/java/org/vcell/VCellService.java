package org.vcell;

import java.io.File;
import java.util.concurrent.Callable;
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

import net.imagej.Dataset;

/**
 * Created by kevingaffney on 7/10/17.
 */
public class VCellService {

    private VCellResultService vCellResultService;

    public VCellService(VCellResultService vCellResultService) {
        this.vCellResultService = vCellResultService;
    }
    
    public org.vcell.vcellij.api.Dataset runSimulation(SBMLModel sbmlModel) {
        try {
            TTransport transport;

            transport = new TSocket("localhost", 9091);
            transport.open();

            TProtocol protocol = new  TBinaryProtocol(transport);
            SimulationService.Client client = new SimulationService.Client(protocol);

        	sbmlModel.setFilepath("filepath");
        	SimulationSpec simSpec = new SimulationSpec();
            SimulationInfo simInfo = client.computeModel(sbmlModel, simSpec);
            org.vcell.vcellij.api.Dataset dataset = client.getDataset(simInfo);
            System.out.println(dataset.toString());

            transport.close();
            
            return dataset;
        } catch (TException x) {
            x.printStackTrace();
        }
		return null;
    }
}
