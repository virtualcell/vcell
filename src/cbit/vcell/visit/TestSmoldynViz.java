package cbit.vcell.visit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.vcell.util.Executable;
import org.vcell.util.ExecutableStatus;

public class TestSmoldynViz {

	/**
	 * @param args
	 */
	public static void main(String[] a) {

		    
			try {
				ArrayList<String> args = new ArrayList<String>();
				args.add("/home/VCELL/visit/builds/src/bin/visit");//location of visit
				args.add("-nowin");
				args.add("-cli");
				args.add("-s");
				args.add("/share/apps/vcell/visit/convertSmoldyn.py");//location of the script
				args.add("/share/apps/vcell/users/edboyce/SimID_55458019_0__"); //location of the SimID 
				//  /share/apps/vcell/visit/smoldynWorkFiles   
				args.add("/share/apps/vcell/visit/visitframes/");  // where frames are dumped 
				args.add("3"); //dimension
				args.add("0"); // 0 = show all the particles.  >0 == show n different particles, to be listed below
				args.add(""); //specific particle 1
				args.add(""); //specific particle 2 
				args.add(""); // ...
				Executable executable = new Executable(args.toArray(new String[0]));
				executable.start();
				while (!executable.getStatus().equals(ExecutableStatus.COMPLETE) && !executable.getStatus().equals(ExecutableStatus.STOPPED)){
					Thread.sleep(1000);
					System.out.println("waiting");
					
				}
				System.out.println("done : status = " + executable.getStatus());
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}

}
