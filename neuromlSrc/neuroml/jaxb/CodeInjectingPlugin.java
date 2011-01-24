package neuroml.jaxb;

import java.io.*;
import java.nio.*;

import org.xml.sax.*;

import com.sun.tools.xjc.*;
import com.sun.tools.xjc.outline.*;

/**
 * This class injects code into NeuroML classes
 * while they're being generated. Its used as a
 * plugin for the xjc ant task. This class is
 * based on an example given 
 * <a href="http://weblogs.java.net/blog/kohsuke/archive/2005/06/writing_a_plugi.html">here</a>.
 * 
 * @author mschachter
 */
public class CodeInjectingPlugin extends Plugin
{

	public String getOptionName()
	{
		return "Xneuroml-codeinjector";
	}

	public String getUsage()
	{
		return " -Xneuroml-codeinjector "; 
	}

	public boolean run(Outline model, Options opt, ErrorHandler errorHandler)
	{	
		String wdir = System.getProperty("user.dir");
		String tdir = wdir + File.separator + "conf/jaxb";
		
		System.err.println("NeuroML JAXB Plugin");
		System.err.println("    Working Directory: " + wdir);
		System.err.println("    Template Directory: " + tdir);
		
		String fname, line;
		File f;
		FileReader r;  BufferedReader br;		
		for( ClassOutline co : model.getClasses() ) {		
			
			fname = tdir + File.separator + co.implClass.fullName();
			f = new File(fname);
			if (f.exists()) {
				System.err.println("     ** Injecting code:");
				System.err.println("          File : " + fname);
				System.err.println("          Class: " + co.implClass.fullName());
				
				try {
					r = new FileReader(f);
					br = new BufferedReader(r);					
					co.implClass.direct("\n\n//NEUROML PLUGIN INSERTED CODE\n");
					co.implClass.direct("//   From File : " + fname + "\n\n");
					while ((line = br.readLine()) != null) {
						co.implClass.direct(line + "\n");
					}				
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}		
		return true;
	}
	
	

}
