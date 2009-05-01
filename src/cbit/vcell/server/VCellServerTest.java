package cbit.vcell.server;

import org.vcell.util.document.User;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class VCellServerTest extends cbit.vcell.client.test.ClientTester {
/**
 * This method was created by a SmartGuide.
 * @param form RegistryForm
 */
public static void formatServerStatus(VCellServer vcellServer, java.io.PrintWriter out, String returnLink) {

	if (vcellServer == null){
		throw new IllegalArgumentException("VCellServer is null");
	}
	out.println("<H1>Primary/Slave Server Info</H1>");
	out.println("<H3>Primary Server</H3>");
	ConnectionPoolStatus status = null;
	try {
		status = vcellServer.getConnectionPoolStatus();
		ProcessStatus ps = vcellServer.getProcessStatus();
		org.vcell.util.CacheStatus cs = vcellServer.getCacheStatus();
		out.println("<blockquote>");
		out.println("<LI>CPU:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ps.getNumJobsRunning()+" Jobs, "+ps.getNumProcessors()+" CPU's  ("+((int)(ps.getFractionFreeCPU()*100))+"% Free CPU)</LI>");
		out.println("<LI>Memory:&nbsp;"+(ps.getFreeMemoryBytes()/1024)+"KB</LI>");
		out.println("<LI>Java Memory:&nbsp;current JVM Free="+(ps.getFreeJavaMemoryBytes()/1024)+"KB, JVM Total="+(ps.getTotalJavaMemoryBytes()/1024)+"KB, Max JVM Total="+(ps.getMaxJavaMemoryBytes()/1024)+"KB</LI>");
		out.println("<LI>Cache:&nbsp;&nbsp;&nbsp;&nbsp;"+(cs.getCurrSize()/1024)+"KB of "+(cs.getMaxSize()/1024)+"KB ("+cs.getNumObjects()+") Objects</LI>");
		out.println("</blockquote>");
	}catch (Throwable e){
		e.printStackTrace(System.out);
		out.println("<br>"+e.getMessage());
		out.println("<br>Can't connect to primary server");
		out.println("<br><br>"+returnLink+"<br><br>");
	}

	if (status!=null){
		out.println("<H3>Registered Slave Servers</H3>");
		if (status.getPotentialHosts()!=null){
			for (int i=0;i<status.getPotentialHosts().length;i++){
				out.println("<LI>"+status.getPotentialHosts()[i]+"</LI>");
			}
		}else{
			out.println("none");
		}
	}

	if (status!=null && vcellServer!=null){
		out.println("<H3>Active Slave Servers</H3>");
		if (status.getActiveHosts()!=null){
			for (int i=0;i<status.getActiveHosts().length;i++){
				String activeHost = status.getActiveHosts()[i].getHostName();
				try {
					VCellServer server = vcellServer.getSlaveVCellServer(activeHost);
					ProcessStatus ps = server.getProcessStatus();
					org.vcell.util.CacheStatus cs = server.getCacheStatus();
					out.println("<LI>"+activeHost+"</LI>");
					out.println("<blockquote>");
					out.println("<LI>CPU:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ps.getNumJobsRunning()+" Jobs, "+ps.getNumProcessors()+" CPU's  ("+((int)(ps.getFractionFreeCPU()*100))+"% Free CPU)</LI>");
					out.println("<LI>Memory:&nbsp;"+(ps.getFreeMemoryBytes()/1024)+"KB</LI>");
					out.println("<LI>Java Memory:&nbsp;current JVM Free="+(ps.getFreeJavaMemoryBytes()/1024)+"KB, JVM Total="+(ps.getTotalJavaMemoryBytes()/1024)+"KB, Max JVM Total="+(ps.getMaxJavaMemoryBytes()/1024)+"KB</LI>");
					out.println("<LI>Cache:&nbsp;&nbsp;&nbsp;&nbsp;"+(cs.getCurrSize()/1024)+"KB of "+(cs.getMaxSize()/1024)+"KB ("+cs.getNumObjects()+") Objects</LI>");
					out.println("</blockquote>");
				}catch (Exception e){
					e.printStackTrace(System.out);
					out.println("exception getting remote server at "+activeHost+" exception: "+e.getMessage());
				}
			}
		}else{
			out.println("none");
		}
		out.println("<H3>Next Slave Server</H3>");
		if (status.getNextHost()!=null){
			out.println("<LI>"+status.getNextHost()+"</LI>");
		}else{
			out.println("N/A");
		}
	}

	if (vcellServer!=null){
		out.println("<H1>Active User Info</H1>");
		try {
			User users[] = vcellServer.getConnectedUsers();
			if (users!=null){
				for (int i=0;i<users.length;i++){
					out.println("<H3>user = "+users[i]+"</H3>");
				}
			}else{
				out.println("<br>none");
			}
		}catch (Throwable e){
			e.printStackTrace(System.out);
			out.println("exception getting User info, exception: "+e.getMessage());
		}
	}
	if (vcellServer!=null){
		out.println("<H1>Active Job Info</H1>");
		try {
			cbit.vcell.solvers.SolverControllerInfo scInfos[] = vcellServer.getSolverControllerInfos();
			if (scInfos!=null){
				for (int i=0;i<scInfos.length;i++){
                    out.println("&nbsp;Host:&nbsp;<b>"+scInfos[i].getHost()+"</b>");
                    out.println("&nbsp;<b>&lt;&lt;"+scInfos[i].getSolverStatus()+"&gt;&gt;</b>");
                    out.println("&nbsp;Owner:&nbsp;<b>"+scInfos[i].getSimulationInfo().getVersion().getOwner().getName()+"</b>");
                    out.println("&nbsp;simName:&nbsp;<b>&quot;"+scInfos[i].getSimulationInfo().getVersion().getName()+"&quot;</b>");
                    out.println("&nbsp;SimID:&nbsp;<b>"+scInfos[i].getSimulationInfo().getAuthoritativeVCSimulationIdentifier().getID()+"</b>");
                    out.println("<br>");
				}
			}else{
				out.println("<br>none");
			}
		}catch (Throwable e){
			e.printStackTrace(System.out);
			out.println("exception getting Job info, exception: "+e.getMessage());
		}
	}
}


/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		VCellServerFactory vcellServerFactory = VCellServerFactoryInit(args,"VCellServerTest");
		java.io.CharArrayWriter caWriter = new java.io.CharArrayWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(caWriter,true);
		VCellServer vcellServer = vcellServerFactory.getVCellServer();
		formatServerStatus(vcellServer,pw,"...return link...");
		pw.flush();
		System.out.println(caWriter.toString());
	}catch (Exception e){
		e.printStackTrace(System.out);
	}finally{
		System.exit(0);
	}
}
}