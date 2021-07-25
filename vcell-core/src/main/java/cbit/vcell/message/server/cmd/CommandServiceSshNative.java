package cbit.vcell.message.server.cmd;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import org.vcell.util.TimeWrapper;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.resource.PropertyLoader;


public class CommandServiceSshNative extends CommandService {
	private class HostNameAgeTuple {
		public String hostName;
		public Long age;
		public HostNameAgeTuple(String hostName, Long age) {
			super();
			this.hostName = hostName;
			this.age = age;
		}
	}
	private final String[] remoteHostNames;
	private TreeSet<HostNameAgeTuple> problemHosts = new TreeSet<HostNameAgeTuple>(new Comparator<HostNameAgeTuple>() {
		@Override
		public int compare(HostNameAgeTuple o1, HostNameAgeTuple o2) {
			return o1.hostName.compareToIgnoreCase(o2.hostName);
		}});
	private final String username;
	private final File installedKeyFile;
	CommandServiceLocal cmdServiceLocal = new CommandServiceLocal();
	private final Random rand = new Random();
	
	public CommandServiceSshNative(String[] remoteHostNames, String username, File secretKeyFile, File homeDir) throws IOException{
		super();
		this.remoteHostNames = remoteHostNames;
		this.username = username;
		//
		// assumes that the keyFile is supplied as a secret within a container running as user root
		//
		this.installedKeyFile = new File(homeDir.getAbsolutePath()+"/.ssh/"+username+"_rsa");
		if (!secretKeyFile.getAbsolutePath().equals(installedKeyFile.getAbsolutePath())) {
			try {
				cmdServiceLocal.command(new String[] { "mkdir", "-p", homeDir.getAbsolutePath()+"/.ssh" });
				cmdServiceLocal.command(new String[] { "chmod", "700", homeDir.getAbsolutePath()+"/.ssh" });
				cmdServiceLocal.command(new String[] { "cp", secretKeyFile.getAbsolutePath(), installedKeyFile.getAbsolutePath()}, new int [] { 0 });
				cmdServiceLocal.command(new String[] { "chmod", "400", installedKeyFile.getAbsolutePath() }, new int[] { 0 });
			} catch (ExecutableException e) {
				lg.error("failed to install keyFile "+secretKeyFile.getAbsolutePath()+" for ssh user "+username+" into "+installedKeyFile.getAbsolutePath(), e);
			}
		}
	}
		
	public CommandServiceSshNative(String[] remoteHostNames, String username, File installedKeyFile) throws IOException{
		super();
		this.remoteHostNames = remoteHostNames;
		this.username = username;
		this.installedKeyFile = installedKeyFile;
	}
	
	@Override
	public CommandOutput command(String[] commandStrings, int[] allowableReturnCodes) throws ExecutableException {
		if (allowableReturnCodes == null){
			throw new IllegalArgumentException("allowableReturnCodes must not be null");
		}
		//Map hostname to command results (each hostname has it's own CommandOutput for any command it runs)
		TreeMap<String,CommandOutput> commandOutputHolder = new TreeMap<String,CommandOutput>();
		//Make modifiable list of the hostnames where commands are sent
		ArrayList<String> tryTheseHosts = new ArrayList<String>(Arrays.asList(remoteHostNames));
		//Remove any hosts that have had problems returning command results
		//Restore problem hosts after a set period of time (maybe they recovered so allow them to be tried again)
		Iterator<HostNameAgeTuple> iterator = problemHosts.iterator();
		while(iterator.hasNext()) {
			HostNameAgeTuple hostNameAgeTuple = iterator.next();
			synchronized (this) {
				if((System.currentTimeMillis()-hostNameAgeTuple.age) > Double.parseDouble(System.getProperty(PropertyLoader.cmdSrvcSshCmdRestoreTimeoutFactor, 5.0+""))*60*1000) {
					iterator.remove();//Remove from problemHosts penalty box
				}else {
					tryTheseHosts.remove(hostNameAgeTuple.hostName);//Remove from available while in problemHosts penalty box
				}
			}
		}
		//If all commandHosts are in the penalty box, then restore them all
		if(tryTheseHosts.size()==0) {
			synchronized (this) {
				tryTheseHosts = new ArrayList<String>(Arrays.asList(remoteHostNames));
				problemHosts.clear();
			}
		}
		//Try to give the command and return the results, randomly try available commandHosts until success
		while(!tryTheseHosts.isEmpty()) {
			String tryThisHost =  null;
			synchronized (this) {
				//Randomly select a commandHost to send a command
				tryThisHost = tryTheseHosts.remove(rand.nextInt(tryTheseHosts.size()) );
				commandOutputHolder.put(tryThisHost, null);
			}
			//Setup ssh with persistent connection reuse using the currently selected commandHost
			String[] sshCmdPrefix = new String[] { "ssh", "-i", installedKeyFile.getAbsolutePath(), "-o", "StrictHostKeyChecking=No", "-o", "ControlMaster=auto", "-o", "ControlPath=~/.ssh/%r@%h:%p", "-o", "ControlPersist=1m", username+"@"+tryThisHost };
			ArrayList<String> cmdList = new ArrayList<String>();
			cmdList.addAll(Arrays.asList(sshCmdPrefix));
			cmdList.add(String.join(" ", Arrays.asList(commandStrings)));
			String[] cmd = cmdList.toArray(new String[0]);
			try {
				CommandOutput tempCommandOutput = cmdServiceLocal.commandWithTimeout(cmd, allowableReturnCodes,Integer.parseInt(System.getProperty(PropertyLoader.cmdSrvcSshCmdTimeoutMS, 10000+"")));
				synchronized (this) {
					commandOutputHolder.put(tryThisHost,tempCommandOutput);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("failed execution, using "+tryThisHost);
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				pw.close();
				synchronized (this) {
					commandOutputHolder.put(tryThisHost,new CommandOutput(cmd, "", sw.toString(), -200, 0));
				}
			}
			//Check the results are as expected
			for (int expectedReturnCode : allowableReturnCodes){
				if (expectedReturnCode == commandOutputHolder.get(tryThisHost).getExitStatus()){
					//Return results if everything is OK
					return commandOutputHolder.get(tryThisHost);
				}
			}
			//If we get here the commandOutput is bad, put the commandHost in the penalty box
			synchronized (this) {
				problemHosts.add(new HostNameAgeTuple(tryThisHost,System.currentTimeMillis()));
			}
			if(tryTheseHosts.isEmpty()) {
				System.out.println("failed, exhausted all hostnames");
				//If any commandHosts are working we should never get here
				//All available command hosts failed to return expected results
				return commandOutputHolder.get(tryThisHost);
			}
			//We get to this point if the selected commandHost failed to return expected results
			//but there are still other commandHosts available so loop and try again with the next commandHost
			System.out.println("failed returnCode test, using "+tryThisHost);
		}
		//Should never get here
		throw new ExecutableException("No suitable result");
	}

	@Override
	public CommandService clone() {
		try {
			return new CommandServiceSshNative(remoteHostNames, username, this.installedKeyFile);
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public void close() {
	}
	
}
