package org.vcell.vcellij;


import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.vcell.vcellij.api.SimulationService;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;


public class VCellIJServer {

	/**
	 * Just fork off a daemon thread
	 *
	 */
	public static void startVCellVisitDataServerThread(SimulationService.Iface simService, boolean bDaemon) {
		Thread vcellProxyThread = new SimulationServerThread();
		vcellProxyThread.setDaemon(bDaemon);
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
				InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), 9091);
				TServerTransport serverTransport = new TServerSocket(inetSocketAddress);
				TServer vcellProxyServer = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

				System.out.println("Starting the VCell-ImageJ Data Server thread...");
				vcellProxyServer.serve();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	public static void main(String[] args){
		try {
			PropertyLoader.loadProperties();
			ResourceUtil.setNativeLibraryDirectory();
			NativeLib.HDF5.load();
			SimulationService.Iface simService = new SimulationServiceImpl();
			VCellIJServer.startVCellVisitDataServerThread(simService, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
