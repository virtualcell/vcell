package cbit.vcell.message.server.htc.slurm;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solvers.ExecutableCommand;
import cbit.vcell.solvers.ExecutableCommand.Container;

@Ignore
public class SlurmProxyTest {
	@Before
	public void setEnv() {
		//System.setProperty(PropertyLoader.htcSlurmHome,"/opt/slurm/");
		System.setProperty( PropertyLoader.htcLogDirExternal,"/home/htcLogs");
		System.setProperty( PropertyLoader.MPI_HOME_EXTERNAL,"/opt/mpich/");
	}
	
	private void write(String name, String text) {
		try { 
			File f = new File(name);
			HtcProxy.writeUnixStyleTextFile(f, text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addExit(Container ctn) {
		final String eToken = "yada-yada";
		ExecutableCommand exitC = new ExecutableCommand(null,"echo",eToken);
		exitC.setExitCodeToken(eToken);
		ctn.add(exitC);
		
	}
	
	@Test
	public void tryIt( ) {
		Container ctn = new ExecutableCommand.Container();
		ExecutableCommand listdog = new ExecutableCommand(null,"ls");
		listdog.addArgument("dog");
		ctn.add(listdog);
		ctn.add(new ExecutableCommand(null,"wc","dog"));
		SlurmProxy spProxy = new SlurmProxy(null, "gerard");
		String text = spProxy.generateScript("Q_3", ctn, 1, 10.0, null);
		write("out.sh",text);
	}
	
	@Test
	public void tryItWithExit( ) {
		Container ctn = new ExecutableCommand.Container();
		ctn.add(new ExecutableCommand(null,"ls","dog"));
		ctn.add(new ExecutableCommand(null,"wc","dog"));
		addExit(ctn);
		
		SlurmProxy spProxy = new SlurmProxy(null, "gerard");
		String text = spProxy.generateScript("Q_3", ctn, 1, 10.0, null);
		write("outexit.sh",text);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void tryParallelBad( ) {
		Container ctn = new ExecutableCommand.Container();
		ctn.add(new ExecutableCommand(null,true,true,"wc","dog"));
		SlurmProxy spProxy = new SlurmProxy(null, "gerard");
		spProxy.generateScript("Q_3", ctn, 1, 10.0, null);
	}
	
	@Test
	public void tryParallel( ) {
		Container ctn = new ExecutableCommand.Container();
		ctn.add(new ExecutableCommand(null,"ls","dog"));
		ctn.add(new ExecutableCommand(null,true,true,"wc","dog"));
		SlurmProxy spProxy = new SlurmProxy(null, "gerard");
		String text = spProxy.generateScript("Q_3", ctn, 4, 10.0, null);
		write("par.sh",text);
	}
	@Test
	public void tryParallelExit( ) {
		Container ctn = new ExecutableCommand.Container();
		addExit(ctn);
		ctn.add(new ExecutableCommand(null,"ls","dog"));
		ctn.add(new ExecutableCommand(null,true,true,"wc","dog"));
		SlurmProxy spProxy = new SlurmProxy(null, "gerard");
		String text = spProxy.generateScript("Q_3", ctn, 4, 10.0, null);
		write("parexit.sh",text);
	}

}
