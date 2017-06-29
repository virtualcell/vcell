package cbit.vcell.util;

import java.io.File;

import org.vcell.util.Executable2;
import org.vcell.util.ExecutableException;
import org.vcell.util.IExecutable;

public class Executable2Test {
	
	//@Test
	public void test( ) throws ExecutableException {
		IExecutable e2 = new Executable2("cmd", "/c", "dir");
		e2.start();
		System.out.println("Exit code " + e2.getExitValue());
		System.out.println(e2.getStdoutString());
		System.err.println(e2.getStderrString());
	}
	
	//@Test
	public void test2( ) throws ExecutableException {
		IExecutable e2 = new Executable2("C:/cygwin/bin/bash.exe","--verbose");
		System.out.println(e2.getStatus());
		e2.setWorkingDir(new File("C:\\cygwin\\bin"));
		e2.start();
		System.out.println(e2.getStatus());
		System.out.println("Exit code " + e2.getExitValue());
		System.out.println(e2.getStdoutString());
		System.err.println(e2.getStderrString());
		System.out.println(e2.getStatus());
	}

		public static void main(String[] args) {
			try {
		final IExecutable e2 = new Executable2("C:/cygwin/bin/xterm.exe");
		e2.setWorkingDir(new File("C:\\cygwin\\bin"));
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					e2.start();
				} catch (ExecutableException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		System.out.println(e2.getStatus());
		Thread.sleep(2500);
		System.out.println(e2.getStatus());
		e2.stop();
		System.out.println(e2.getStatus());
		e2.stop();
		System.out.println(e2.getStatus());
		System.out.println("Exit code " + e2.getExitValue());
		System.out.println("out: " + e2.getStdoutString());
		System.err.println("err: " + e2.getStderrString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
