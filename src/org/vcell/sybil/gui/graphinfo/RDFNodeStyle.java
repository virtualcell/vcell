package org.vcell.sybil.gui.graphinfo;

/*   RDFNodeStyle  --- by Oliver Ruebenacker, UCHC --- February to March 2009
 *   Font style and color for RDFNodes
 */

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.vcell.sybil.rdf.schemas.BioPAX2;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class RDFNodeStyle {

	public static class FontStyle {
		protected int styleInt;
		public FontStyle(int styleIntNew) { styleInt = styleIntNew; }
		public int styleInt() { return styleInt; }
		public Font deriveFont(Font font) { return font.deriveFont(styleInt); }
		
	}

	public static FontStyle Plain = new FontStyle(Font.PLAIN);
	public static FontStyle Bold = new FontStyle(Font.BOLD);
	public static FontStyle Italic = new FontStyle(Font.ITALIC);

	protected static Map<Resource, Color> colorMap = new HashMap<Resource, Color>();
	protected static Map<Resource, FontStyle> styleMap = new HashMap<Resource, FontStyle>();
	
	protected static void init(Resource resource, FontStyle style, Color color) {
		styleMap.put(resource, style);
		colorMap.put(resource, color);
	}
	
	static {
		init(RDF.type, Bold, Color.red);
		init(BioPAX2.biochemicalReaction, Bold, Color.red);
		init(BioPAX2.transport, Bold, Color.red);
		init(BioPAX2.transportWithBiochemicalReaction, Bold, Color.red);
		init(BioPAX2.interaction, Bold, Color.red);
		init(BioPAX2.conversion, Bold, Color.red);
		init(BioPAX2.control, Bold, Color.red);
		init(BioPAX2.catalysis, Bold, Color.red);
		init(BioPAX2.modulation, Bold, Color.red);
		init(BioPAX2.physicalEntity, Bold, Color.red);
		init(BioPAX2.protein, Bold, Color.red);
		init(BioPAX2.smallMolecule, Bold, Color.red);
		init(BioPAX2.dna, Bold, Color.red);
		init(BioPAX2.rna, Bold, Color.red);
		init(BioPAX2.complex, Bold, Color.red);
		init(BioPAX2.NAME, Bold, Color.black);
		init(BioPAX2.SHORT_NAME, Bold, Color.black);
		init(BioPAX2.SYNONYMS, Bold, Color.black);
		init(BioPAX2.LEFT, Bold, Color.orange);
		init(BioPAX2.RIGHT, Bold, Color.orange);
		init(BioPAX2.CONTROLLER, Bold, Color.orange);
		init(BioPAX2.CONTROLLED, Bold, Color.orange);
		init(BioPAX2.ORGANISM, Italic, Color.green);
		init(BioPAX2.CELLTYPE, Italic, Color.green);
		init(BioPAX2.CELLULAR_LOCATION, Bold, Color.CYAN);
		init(BioPAX2.STOICHIOMETRIC_COEFFICIENT, Bold, Color.CYAN);
	}
	
	public static FontStyle fontStyle(RDFNode node) {
		if(node.isResource()) {
			FontStyle style = styleMap.get((Resource) node);
			if(style != null) { return style; }
			else { return Bold; }
		}
		return Plain;
	}
	
	public static Color color(RDFNode node) {
		if(node.isResource()) {
			Color color = colorMap.get((Resource) node);
			if(color != null) { return color; }
			else { return Color.blue; }
		}
		return Color.black;
	}
	
}
