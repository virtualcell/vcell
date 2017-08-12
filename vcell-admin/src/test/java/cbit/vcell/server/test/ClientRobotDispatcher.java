package cbit.vcell.server.test;

import java.io.File;
import java.util.Vector;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.StdoutSessionLog;
/**
 * Insert the type's description here.
 * Creation date: (3/8/01 3:01:11 PM)
 * @author: Jim Schaff
 */
public class ClientRobotDispatcher {
	private Vector clientRobotList = new Vector();
/**
 * ClientRobotDispatcher constructor comment.
 */
public ClientRobotDispatcher() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/01 5:31:10 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		if (args.length!=5){
			System.out.println("usage:\n  ClientRobotDispatcher  numRobots  serverHost userid password biomodelfile");
			System.exit(1);
		}
		int numRobots = Integer.parseInt(args[0]);
		String host = args[1];
		String userid = args[2];
		String password = args[3];
		File biomodelfile = new File(args[4]);

		PropertyLoader.loadProperties();
		
		ClientRobotDispatcher clientRobotDispatcher = new ClientRobotDispatcher();
		
		clientRobotDispatcher.test(numRobots,host,userid,password,biomodelfile);

	}catch (Throwable e){
		e.printStackTrace(System.out);
	}finally{
		System.exit(0);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/01 5:50:53 PM)
 * @param numRobots int
 * @param host java.lang.String
 * @param userid java.lang.String
 * @param password java.lang.String
 */
private void test(int numRobots, String host, String userid, String password, File biomodelfile) {
	
	StdoutSessionLog globalLog = new StdoutSessionLog("globalLog");
	Thread threads[] = new Thread[numRobots];

	for (int i=0;i<numRobots;i++){
		String robotName = "robot"+i;
		MixedSessionLog robotLog = new MixedSessionLog(new StringSessionLog(robotName),globalLog);
		this.clientRobotList.addElement(new ClientRobot(robotName,host,userid,password,biomodelfile,robotLog));
	}

	for (int i=0;i<clientRobotList.size();i++){
		ClientRobot clientRobot = (ClientRobot)clientRobotList.elementAt(i);
		threads[i] = new Thread(clientRobot);
		threads[i].run();
	}

	//
	// wait for robots to finish
	//
	for (int i=0;i<numRobots;i++){
		try {
			threads[i].wait();
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	System.out.println("printing Robot Summaries by robot");
	
	for (int i=0;i<clientRobotList.size();i++){
		ClientRobot clientRobot = (ClientRobot)clientRobotList.elementAt(i);
		System.out.println(clientRobot.getActivityLog());
	}
	
}
}
