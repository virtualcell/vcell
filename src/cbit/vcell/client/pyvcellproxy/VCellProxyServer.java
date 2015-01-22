package cbit.vcell.client.pyvcellproxy;


import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;


public class VCellProxyServer {

public static void startSimpleVCellProxyServer(VCellProxy.Processor<VCellProxyHandler> processor) {
	try {
		TServerTransport serverTransport = new TServerSocket(9090);
		TServer vcellProxyServer = new TSimpleServer(new org.apache.thrift.server.TServer.Args(serverTransport).processor(processor));
		
		System.out.println("Starting the VCell-VisIt Data Server thread...");
		vcellProxyServer.serve();
	} catch (Exception e) {
		e.printStackTrace();
	}
	
}

}
