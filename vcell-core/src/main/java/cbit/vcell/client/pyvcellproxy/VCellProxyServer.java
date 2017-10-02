package cbit.vcell.client.pyvcellproxy;


import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;


public class VCellProxyServer {
	
/**
 * Just fork off a daemon thread
 * @param vcellClient
 */
public static void startVCellVisitDataServerThread(VCellClientDataService  vcellClient) {
	Thread vcellProxyThread = new VCellProxyThread(vcellClient) ;
	vcellProxyThread.start();
}

/**
 * start proxy server in daemon thread
 */
private static class VCellProxyThread extends Thread {
	final VCellClientDataService vcellClientDataService;
	VCellProxyThread(VCellClientDataService vcc) {
		super("vcellProxyThread");
		setDaemon(true);
		vcellClientDataService = vcc;
	}
	@Override
	public void run() {
		startSimpleVCellProxyServer(new VCellProxy.Processor<VCellProxyHandler>(new VCellProxyHandler(vcellClientDataService)));	
	}
}

private static void startSimpleVCellProxyServer(VCellProxy.Processor<VCellProxyHandler> processor) {
	try {
		InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), 9090);
		TServerTransport serverTransport = new TServerSocket(inetSocketAddress);
		TServer vcellProxyServer = new TSimpleServer(new org.apache.thrift.server.TServer.Args(serverTransport).processor(processor));
		
		System.out.println("Starting the VCell-VisIt Data Server thread...");
		vcellProxyServer.serve();
	} catch (Exception e) {
		e.printStackTrace();
	}
	
}
}
