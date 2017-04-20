package cbit.vcell.client.pyvcellproxy;


import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import cbit.vcell.client.VCellClient;


public class VCellProxyServer {
	
/**
 * Just fork off a daemon thread
 * @param vcellClient
 */
public static void startVCellVisitDataServerThread(VCellClient  vcellClient) {
	Thread vcellProxyThread = new VCellProxyThread(vcellClient) ;
	vcellProxyThread.start();
}

/**
 * start proxy server in daemon thread
 */
private static class VCellProxyThread extends Thread {
	final VCellClient vcellClient;
	VCellProxyThread(VCellClient vcc) {
		super("vcellProxyThread");
		setDaemon(true);
		vcellClient = vcc;
	}
	@Override
	public void run() {
		startSimpleVCellProxyServer(new VCellProxy.Processor<VCellProxyHandler>(new VCellProxyHandler(vcellClient)));	
	}
}

private static void startSimpleVCellProxyServer(VCellProxy.Processor<VCellProxyHandler> processor) {
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
