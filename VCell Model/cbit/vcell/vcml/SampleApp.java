package cbit.vcell.vcml;

import cbit.gui.PropertyLoader;
import cbit.vcell.vcml.Translator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException; 
import java.io.PrintWriter; 
import org.jdom.Document;
/**
 A sample app. using the translation classes. Some of the methods for printing out a JDOM tree where
 adopted from a JDOM tutorial.
 * @author: Rashad Badrawi
 */ 
public class SampleApp {

	public SampleApp(String uri, String mode) {
		
		  try {
		    long stime = System.currentTimeMillis();
		    //can also use a StringInputStream
		    BufferedReader br = new BufferedReader(new FileReader(uri));
		    PrintWriter pw = new PrintWriter(System.out);
		    //requested translation
		    Translator t = null;
		    String reverse = null;
		    boolean validationOn = true;
		    if (mode.startsWith("VCSB")) {
				if (mode.endsWith("1")) {
		    		t = Translator.getTranslator(Translator.VCSB_1);
		    		reverse = Translator.SBVC_1;
				} else {
		    		t = Translator.getTranslator(Translator.VCSB_2);
		    		reverse = Translator.SBVC_2;
				}
		    } else if (mode.startsWith("SBVC")){     
			    if (mode.endsWith("1")) {
		    		t = Translator.getTranslator(Translator.SBVC_1);
		    		reverse = Translator.VCSB_1;
			    } else {
		    		t = Translator.getTranslator(Translator.SBVC_2);
		    		reverse = Translator.VCSB_2;
			    }
		    } else if (mode.startsWith("VCQua")){
				if (mode.endsWith("nCell")) {
		    		t = Translator.getTranslator(Translator.VC_QUAN_CELL);
		    		reverse = Translator.CELL_QUAN_VC;
				} else {                           //qualitative
		    		t = Translator.getTranslator(Translator.VC_QUAL_CELL);
		    		reverse = Translator.CELL_QUAL_VC;
				}
				validationOn = false;
		    } else if (mode.startsWith("CellQua")){
			    if (mode.endsWith("nVC")) {
		    		t = Translator.getTranslator(Translator.CELL_QUAN_VC);
		    		reverse = Translator.VC_QUAN_CELL;
				} else {                            //qualitative
		    		t = Translator.getTranslator(Translator.CELL_QUAL_VC);
		    		reverse = Translator.VC_QUAL_CELL;
				}
				validationOn = false;
		    }
			org.jdom.Document tDoc = t.translate(br, validationOn);
			t.printSource(pw);
			pw.println("");
			t.printTarget(pw);
			pw.println("");
			long mtime = System.currentTimeMillis();
			//now, do the reverse translation
		    t = Translator.getTranslator(reverse);
		    tDoc = t.translate(tDoc, true);
			t.printTarget(pw);
			System.out.println("");
			long etime = System.currentTimeMillis();
			System.out.println("Approximate time: One way:" + (mtime - stime) + "msec. Round trip:" + (etime - stime) + "msec.");
			System.exit(0);
	    } catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
	    }
	}
public static void main(String[] args) {
	try {
		if (args.length < 2) {
			System.out.println("Usage: java SampleApp URI translationMode"); 
			return;
		}
		PropertyLoader.loadProperties();
		new SampleApp(args[0], args[1]);
	}catch (IOException e){
		e.printStackTrace(System.out);
	}
}
}
