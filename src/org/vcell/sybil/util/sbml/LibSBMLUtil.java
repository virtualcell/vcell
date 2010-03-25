package org.vcell.sybil.util.sbml;

/*   LibSBMLUtil  --- by Oliver Ruebenacker, UCHC --- July 2008 to March 2010
 *   Convenience methods for using libSBML
 */

import org.sbml.libsbml.Compartment;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLNamespaces;
import org.sbml.libsbml.libsbml;

import cbit.vcell.resource.ResourceUtil;

public class LibSBMLUtil {

	public static boolean libSBMLIsloaded = false;
	public static class LibSBMLException extends Exception {

		private static final long serialVersionUID = 2633037652156426049L;

		public LibSBMLException(Throwable cause) {
			super("Could not load libSBML", cause);
		}
		
	}
	
	public static void loadLibSBML() throws LibSBMLException {
		try {
			ResourceUtil.loadlibSbmlLibray();
			libSBMLIsloaded = true;			
		} catch(Throwable throwable) { throw new LibSBMLException(throwable); }
	}
	
	public static void loadIfNeeded() throws LibSBMLException {
		if(!libSBMLIsloaded) { loadLibSBML(); }
	}
	
	public static String shortDescription() throws LibSBMLException {
		loadIfNeeded();
		return "LibSBML Version " + libsbml.getLibSBMLDottedVersion();
	}
	
	public static void testXML() throws LibSBMLException, Throwable {
		loadIfNeeded();
		SBMLDocument sbmlDoc = newDoc();
		SBMLNamespaces sbmlns = new SBMLNamespaces();
		Compartment compartment = new Compartment(sbmlns);
		compartment.setId("id12345");
		sbmlDoc.getModel().addCompartment(compartment);
		String sbmlText = libsbml.writeSBMLToString(sbmlDoc);
		libsbml.readSBMLFromString(sbmlText);
	}
	
	public static boolean xmlIsWorking() {
		boolean xmlIsWorking = false;
		try { 
			testXML();
			xmlIsWorking = true;
		} catch (LibSBMLException e) { e.printStackTrace(); } 
		catch (Throwable e) { e.printStackTrace(); }
		return xmlIsWorking;
	}
	
	public static SBMLDocument newDoc() throws LibSBMLException {
		loadIfNeeded();
		SBMLDocument sbml = new SBMLDocument();
		sbml.createModel();
		return sbml;
	}
	
	public static void writeSBML(SBMLDocument sbml, String fileName) throws LibSBMLException {
		loadIfNeeded();
		libsbml.writeSBML(sbml, fileName);
	}
	
	public static String convertSBMLToString(SBMLDocument sbml) throws LibSBMLException {
		loadIfNeeded();
		String string = null;
		try { string = libsbml.writeSBMLToString(sbml);	} 
		catch(Throwable t) { throw new LibSBMLException(t); }
		return string;
	}
	
}
