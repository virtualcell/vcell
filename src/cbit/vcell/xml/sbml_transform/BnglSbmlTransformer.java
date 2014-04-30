/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package cbit.vcell.xml.sbml_transform;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cbit.vcell.server.bionetgen.BNGOutput;

/**
 * @author mlevin
 *
 */
public class BnglSbmlTransformer {
	private SbmlTransformer trans;
	
	public static final String VC_Tag = "#%VC%";
	
	/** Transform SBML output from BioNetGen according to processing instructions
	 * in *.bng file
	 * @param bngOutput
	 */
	public static String transformSBML(BNGOutput bngOutput) {
		final String XML_SUFFIX = ".xml";
		final String BNGL_SUFFIX = ".bngl";
		String sbml = null;
		String bngl = "";
		
		//find sbml and bngl
		String [] fileNames = bngOutput.getBNGFilenames();
		for( int i = 0; i < fileNames.length; ++i ) {
			String fn = fileNames[i];
			if (fn==null) {
				continue;
			}
			if( fn.endsWith(XML_SUFFIX) ) {
				sbml = bngOutput.getBNGFileContent(i);
			}
			
			if( fn.endsWith(BNGL_SUFFIX) ) {
				bngl = bngOutput.getBNGFileContent(i);
			}
			
		}
		ByteArrayInputStream baisSbml = new ByteArrayInputStream(sbml.getBytes());
		ByteArrayInputStream baisBngl = new ByteArrayInputStream(bngl.getBytes());
		BnglSbmlTransformer bsi = new BnglSbmlTransformer(baisSbml, baisBngl);
		bsi.transform();
		sbml = bsi.getSbmlString();
		return sbml;
	}
	
	/** Extracts SBML processing information from bngl file and passes it to SbmlTransformer
	 * @param bngl
	 * @param trans
	 */
	private static void compileProcessingTags(Reader bngl, SbmlTransformer trans) {
		String pattStr = Pattern.quote(VC_Tag) + "[ \\t]*([a-zA-Z_].*?)$";
		final Pattern patt1 = Pattern.compile(pattStr, Pattern.MULTILINE);

		BufferedReader br = new BufferedReader(bngl);
		String line;
		int i = 0;
		try {
			for( i = 0; (line = br.readLine()) != null; ++i ) {
				Matcher match = patt1.matcher(line);
				if( ! match.find() ) continue;
				String str2 = match.group(1);
				if( null == str2 ) continue;
				parseProcessingTags(str2, trans);
			}
		} catch( IOException e) {
			String msg = "error reading *.bngl file";
			throw new SbmlTransformException(msg, e);
			
		} catch( Exception e ) {
			String msg = "processing instruction error on line " + (i + 1);
			throw new SbmlTransformException(msg, e);
		}
	}
	
	private static void parseProcessingTags(String str, SbmlTransformer trans) {

		//check overal structure: functionName123  ( "dfjale-1938" , "-294sdljf", "" ) #comment
		final String patt0 = 
			"([a-zA-Z_]\\w*)[ \\t]*"	//function name
			+ "(\\(([ \\t]*\"[^\"]*\"[ \\t]*(,[ \\t]*\"[^\"]*\"[ \\t]*)*)\\))?"	//optional function arguments
			+ "[ \\t]*(#.*)?"		//followed by space and comments
			;

		final Pattern whole = Pattern.compile(patt0);
		Matcher m1 = whole.matcher(str);

		if( ! m1.lookingAt() ) {
			String msg = "no processing instruction found: \"" + str + "\"";
			throw new SbmlTransformException(msg);
		}

		do {
			String fName = m1.group(1);	// represents the command name (eg., speciesRenamePattern'
			
			if( null == fName ) break;
			
			List<String> tags = new ArrayList<String>();
			tags.add(fName);

			String args = m1.group(2);
			if( args != null ) {
				//parse arguments
				final String patt1 = "\"([^\"]*)\"";
				final Pattern arg = Pattern.compile(patt1);
				Matcher m2 = arg.matcher(args);

				while( m2.find() ) {
					tags.add(m2.group(1));
				}
			}
			trans.addTransformation(tags);


		} while( m1.find() );
		
		if( ! m1.hitEnd() ) {
			int e = m1.end();
			String msg = "unknown instruction \"" + str.substring(e) + "\"";
			throw new SbmlTransformException(msg);
		}
	}

	BnglSbmlTransformer(InputStream sbml, InputStream bngl) {
		this.trans = new SbmlTransformer(sbml);
		InputStreamReader isr = new InputStreamReader(bngl);
		compileProcessingTags(isr, trans);
	}
	
	void transform() {
		
		trans.transform();
	}
	
	String getSbmlString() {
		String sbml = trans.getXmlString();
		return sbml;
	}
	
	public void writeSbml(OutputStream os) {
		trans.writeXml(os);
	}
	
	public static void main(String [] args) {
		File bnglF = new File("D:/vcell_workspace/VCell/nuclear_transport_delta.bngl");
		File sbmlF = new File("D:/vcell_workspace/VCell/nuclear_transport_gamma.xml");
		File out = new File("D:/vcell_workspace/VCell/out.xml");
		try {
			BnglSbmlTransformer bsi = 
				new BnglSbmlTransformer(new FileInputStream(sbmlF), new FileInputStream(bnglF));
			bsi.transform();
			
			OutputStream os = new FileOutputStream(out);
			bsi.writeSbml(os);
			os.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
