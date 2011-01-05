package cbit.vcell.visit;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import cbit.vcell.visit.VisitPythonCommand;



public class VisitProcess {

	public static Process exportProcess;
	public static ProcessInfo myProcessInfo;
	public static BufferedOutputStream myBOS;
	public static StreamReader myNormalOutput;
	public static StreamReader myErrorOutput;
	private static JTextArea jTextArea;
	public static void setJTextArea(JTextArea jTextArea){
		VisitProcess.jTextArea=jTextArea;
	}
	
	public static class ProcessInfo{
		public StreamReader normalOutput;
		public StreamReader errorOutput;
		public Process execProcess;
	//	public int returnCode;
		public ProcessInfo(StreamReader normalOutput, StreamReader errorOutput, Process execProcess) {
			super();
			this.normalOutput = normalOutput;
			this.errorOutput = errorOutput;
//			this.returnCode = returnCode;
			this.execProcess = execProcess;
		}
		public Process getExecProcess(){
			return execProcess;
		}
	}

	public static class StreamReader extends Thread {
		private InputStream is;
		private StringBuilder stringBuilder;
		private Exception readerException;
		
		public StreamReader(InputStream is) {
			this.is = is;
			stringBuilder = new StringBuilder();
		}

		public synchronized void run() {
			try {
				int c;
				while ((c = is.read()) != -1){
					stringBuilder.append((char)c);
					if (stringBuilder.toString().endsWith("\n")) {
					    if (jTextArea!=null){
					    	final String textToAppend = stringBuilder.toString();
					    	System.out.println(stringBuilder.toString());
					    	stringBuilder.delete(0, stringBuilder.length());
					    	SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									jTextArea.append(textToAppend);}
							});
					    	
					    }
						//stringBuilder = new StringBuilder();
					} 
					//System.out.print((char)c);
				}
			} catch (Exception e) {
				readerException = e;
			}
		}

		public /*synchronized*/ String getString() {
			//System.out.println("Got Here");
			String s = stringBuilder.toString();
			stringBuilder = new StringBuilder();
			return s;
			
		}
		public synchronized Exception getReaderException(){
			return readerException;
		}
	}
	
	

	public static ProcessInfo spawnProcess(String spawnCommand) throws Exception{
		exportProcess = Runtime.getRuntime().exec(spawnCommand);
		//Listen for output
		StreamReader normalOutput = new StreamReader(exportProcess.getInputStream());
		normalOutput.start();
		StreamReader errorOutput = new StreamReader(exportProcess.getErrorStream());
		errorOutput.start();
		return new ProcessInfo(normalOutput, errorOutput, exportProcess);

	}
	
	public static void openDatabaseFile(String dbFileFullPath){
		sendVisitCommand("visit.OpenDatabase(\""+dbFileFullPath+"\")\n");
	}
	
	public static Boolean openMDServer(String machine, String pw){
		
		return false;
	}

	
	public static void sendVisitCommand(String visitCmd){
		try {
			
			myBOS.write(visitCmd.getBytes());
			myBOS.flush();
			
			//myBOS = new BufferedOutputStream(myProcessInfo.getExecProcess().getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return;
	}
	
	
	public static void visitCommand(final String visitCmd){
		sendCommandAndNoteResponse(visitCmd +"\n");
	}
	
	public static String sendAndListen(final String visitCmd){
		sendVisitCommand(visitCmd);
		waitASec();
		return(myProcessInfo.normalOutput.getString());
	}
	
	public static void testSend(final String visitCmd){sendVisitCommand(visitCmd+"\n"); waitASec();}
	
	
	public static void sendCommandAndNoteResponse(final String visitCmd){
		//System.out.println(visitCmd);
		 if (jTextArea!=null){
			 if (!SwingUtilities.isEventDispatchThread()) {
		    	SwingUtilities.invokeLater(
		    			new Runnable() {
		    				public void run() {
		    					jTextArea.append(visitCmd);
		    				}
		    			}
		    	);
		    }else{
				jTextArea.append(visitCmd);
		    }
		 }
		sendVisitCommand(visitCmd);
		waitASec();
		//System.out.println(myProcessInfo.normalOutput.getString());
		//System.out.println(myProcessInfo.errorOutput.getString());
		return;
	}
	
	public static void waitASec(){
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    	}
	}
	
	public static void waitNSecs(int n){for (int i=1;i<n;i++) {waitASec();}}
	
	public VisitProcess(String visitPath) {
		String execCommand = "python -i";
		//String execCommand = "/home/VCELL/eboyce/visit2_1_1/2.1.1/linux-x86_64/bin/cli"; 
		try {
			myProcessInfo = VisitProcess.spawnProcess(execCommand);
		} catch (Exception e) {
			e.printStackTrace();
		}
		myBOS = new BufferedOutputStream(myProcessInfo.getExecProcess().getOutputStream());
        System.out.println("Started Python, made myBOS");
		waitASec();
		sendCommandAndNoteResponse("import sys\n");
		sendCommandAndNoteResponse("import os\n");
		//sendCommandAndNoteResponse("sys.path.append(\"/home/VCELL/eboyce/visit2_1_1/2.1.1/linux-x86_64/lib/\")\n");
		sendCommandAndNoteResponse("sys.path.append(\""+visitPath+"\")\n");
		sendCommandAndNoteResponse("import visit\n");
		sendCommandAndNoteResponse("from visit import *\n");
		//sendCommandAndNoteResponse("visit.AddArgument(\"-gui\"\n");
		waitASec();
		sendCommandAndNoteResponse("Launch()\n");	
		waitNSecs(10);
		sendCommandAndNoteResponse("isCLI = True\n");
	}

	public static void main(String[] args){
        System.out.println("Starting main");
		String execCommand = "python -i";
		//String execCommand = "/home/VCELL/eboyce/visit2_1_1/2.1.1/linux-x86_64/bin/cli"; 
		try {
			myProcessInfo = VisitProcess.spawnProcess(execCommand);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("About to make new BOS");
		myBOS = new BufferedOutputStream(myProcessInfo.getExecProcess().getOutputStream());
 
		waitASec();
		sendCommandAndNoteResponse("import sys\n");
		sendCommandAndNoteResponse("import os\n");
		sendCommandAndNoteResponse("sys.path.append(\"/home/VCELL/eboyce/visit2_1_1/2.1.1/linux-x86_64/lib/\")\n");
		sendCommandAndNoteResponse("import visit\n");
		sendCommandAndNoteResponse("from visit import *\n");
		waitASec();
		testSend(VisitPythonCommand.Launch());
		sendCommandAndNoteResponse("isCLI = True\n");
		waitASec();
		//testSend(VisitPythonCommand.OpenMDServer("10.84.11.40"));
		testSend(VisitPythonCommand.OpenDatabase("/home/VCELL/eboyce/visit2_1_1/data/globe.silo"));
		testSend(VisitPythonCommand.AddPlot("Pseudocolor","u"));
		testSend(VisitPythonCommand.DrawPlots());
		
		
		//System.out.println("About to execute visit.Launch()");
		/*
		sendCommandAndNoteResponse("Launch()\n");
	
		waitNSecs(10);
		sendCommandAndNoteResponse("isCLI = True\n");
		waitASec();
		sendCommandAndNoteResponse("visit.OpenMDServer(\"10.84.11.40\")\n");
		sendCommandAndNoteResponse("visit.OpenDatabase(\"10.84.11.40:/home/VCELL/eboyce/visit2_1_1/data/multi_rect3d.silo\")\n");
		waitNSecs(4);
		sendCommandAndNoteResponse("visit.AddPlot(\"Pseudocolor\", \"u\")\n");
		waitASec();
		sendCommandAndNoteResponse("visit.DrawPlots()\n");
		System.out.println("Done.");
		*/
	}
	}
	

