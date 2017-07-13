package org.vcell.vcellij;


import cbit.vcell.client.pyvcellproxy.VCellClientDataService;
import cbit.vcell.client.pyvcellproxy.VCellProxy;
import cbit.vcell.client.pyvcellproxy.VCellProxyHandler;
import cbit.vcell.message.server.dispatcher.SimulationDispatcher;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.vcell.vcellij.api.SimulationService;


public class VCellIJServer {

	/**
	 * Just fork off a daemon thread
	 *
	 */
	public static void startVCellVisitDataServerThread(SimulationService simService) {
		Thread vcellProxyThread = new SimulationServerThread();
		vcellProxyThread.start();
	}

	/**
	 * start proxy server in daemon thread
	 */
	private static class SimulationServerThread extends Thread {
		SimulationServerThread() {
			super("SimulationServerThread");
			setDaemon(true);
		}

		@Override
		public void run() {
			startSimulationServer(new SimulationService.Processor<SimulationServiceImpl>(new SimulationServiceImpl()));
		}

		private static void startSimulationServer(SimulationService.Processor<SimulationServiceImpl> processor) {
			try {
				TServerTransport serverTransport = new TServerSocket(9091);
				TServer vcellProxyServer = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

				System.out.println("Starting the VCell-ImageJ Data Server thread...");
				vcellProxyServer.serve();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
