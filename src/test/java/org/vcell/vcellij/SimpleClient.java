package org.vcell.vcellij;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.xml.stax.SBMLReader;
import org.vcell.vcellij.api.SBMLModel;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationService;
import org.vcell.vcellij.api.SimulationSpec;
import org.vcell.vcellij.api.SimulationState;
import org.vcell.vcellij.api.VariableInfo;

public class SimpleClient {

    public static void main(String [] args) {

        try {
            TTransport transport;

            transport = new TSocket("localhost", 9091);
            transport.open();

            TProtocol protocol = new  TBinaryProtocol(transport);
            SimulationService.Client client = new SimulationService.Client(protocol);

            perform(client);

            transport.close();
        } catch (Throwable x) {
            x.printStackTrace();
        }
    }

    private static void perform(SimulationService.Client client) throws TException, IOException, XMLStreamException
    {
    	File sbmlSpatialFile = new File("Solver_Suite_6_2.xml");
    	SBMLDocument doc = new SBMLReader().readSBML(sbmlSpatialFile);
    	SBMLModel model = new SBMLModel();
    	model.setFilepath(sbmlSpatialFile.getAbsolutePath());
    	SimulationSpec simSpec = new SimulationSpec();
        SimulationInfo simulationInfo = client.computeModel(model, simSpec);
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        while (client.getStatus(simulationInfo).getSimState()==SimulationState.running){
        	System.out.println("waiting for simulation results");
        	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        
        List<VariableInfo> vars = client.getVariableList(simulationInfo);
        for (VariableInfo var : vars){
        	System.out.println("var = "+var.toString());
        }
        List<Double> times = client.getTimePoints(simulationInfo);
        for (Double time : times){
        	System.out.println("t = "+time.toString());
        }
        List<Double> data = client.getData(simulationInfo, vars.get(0), times.size()/2);
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        for (int i=0;i<data.size();i++){
        	buffer.append(data.get(i)+" ");
        	if (i%40==39){
        		buffer.append("\n");
        	}
        	if (i>400){
        		buffer.append("...");
        		break;
        	}
        	
        }
        buffer.append("]");
        System.out.println(buffer.toString());
    }
}
