package org.vcell.sybil.rdf;

/*   StringUtil  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   Utilities for Jena model input and output
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JenaIOUtil {
	
	public static Model modelFromText(String text) {
		Model model = ModelFactory.createDefaultModel();
		byte[] textBytes = text.getBytes();
		ByteArrayInputStream stream = new ByteArrayInputStream(textBytes);
		model.read(stream, "");
		return model;
	}
	
	public static Model modelFromText(Model model, String text) {
		byte[] textBytes = text.getBytes();
		ByteArrayInputStream stream = new ByteArrayInputStream(textBytes);
		model.read(stream, "");
		return model;
	}
	
	public static class Style {
		protected final String name;
		public Style(String nameNew) { name = nameNew; }
		public String toString() { return name; }
	}
	
	public static final Style RDF_XML = new Style("RDF/XML");
	public static final Style RDF_XML_ABBREV = new Style("RDF/XML-ABBREV");
	public static final Style N_TRIPLE = new Style("N-TRIPLE");
	public static final Style N3 = new Style("N3");

	public static void writeModel(Model model, OutputStream out, Style style) {
		model.write(out, style.toString());
	}

	public static String textFromModel(Model model, Style style) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		model.write(out, style.toString());
		return out.toString();
	}

}
