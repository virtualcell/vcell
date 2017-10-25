package org.vcell.imagej.services.vcell;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.vcell.vcellij.api.SBMLModel;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationService;
import org.vcell.vcellij.api.SimulationSpec;

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
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    private static void perform(SimulationService.Client client) throws TException
    {
    	SBMLModel model = new SBMLModel();
    	model.setFilepath("filepath");
    	SimulationSpec simSpec = new SimulationSpec();
        SimulationInfo simulationInfo = client.computeModel(model, simSpec);
    }
}
