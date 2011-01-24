package neuroml.test;

import java.io.*;
import java.util.*;


public class AllTests
{
	public static final String PROP_CONFDIR = "confdir";
	public static final String PROP_TEMPDIR = "tempdir";
	
	public static Map<String, String> properties;
		
	public static void main(String args[]) {
		
		if (args.length < 2) {
			System.err.println("[AllTests] usage: java neuroml.test.AllTests <confdir> <tempdir>");
			System.exit(0);
		}
		
		properties = new HashMap<String,String>();		
		String wdir = System.getProperty("user.dir");
		
		String confdir = wdir + File.separator + args[0];
		properties.put(PROP_CONFDIR, confdir);		
		System.err.println("Using conf directory '" + confdir + "'");
		
		String tempdir = wdir + File.separator + args[1];
		properties.put(PROP_TEMPDIR, tempdir);		
		System.err.println("Using temp directory '" + tempdir + "'");
		
		String[] tests = {"neuroml.test.MetaTest",
				          "neuroml.test.MorphTest",
				          "neuroml.test.ChannelTest",
				          "neuroml.test.NetworkTest"};
		org.junit.runner.JUnitCore.main(tests);
		System.exit(0);
	}
	
}
