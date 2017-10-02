package org.vcell.vcellij;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.vcell.vcellij.api.SimulationService;

public class SimpleServer {

    public static SimulationServiceImpl handler;

    public static SimulationService.Processor<SimulationServiceImpl> processor;

    public static void main(String [] args) {
        try {
            handler = new SimulationServiceImpl();
            processor = new SimulationService.Processor<SimulationServiceImpl>(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(SimulationService.Processor<SimulationServiceImpl> processor) {
        try {
    		InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), 9091);
    		TServerTransport serverTransport = new TServerSocket(inetSocketAddress);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}