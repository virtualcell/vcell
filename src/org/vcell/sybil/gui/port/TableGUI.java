package org.vcell.sybil.gui.port;

/*   TableGUI  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   Graphical user interface support for a process table
 */

import javax.swing.JOptionPane;

import org.vcell.sybil.models.bpimport.table.TableUI;
import org.vcell.sybil.models.bpimport.table.options.CellLiteralOption;
import org.vcell.sybil.models.bpimport.table.options.CellThingOption;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.ThingFactory;
import org.vcell.sybil.rdf.NodeUtil;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class TableGUI implements TableUI {

	protected ProcessTable table;

	public TableGUI(ProcessTable tableNew) { table = tableNew; }

	public <V extends SBBox.NamedThing> CellThingOption<V> 
	askForCellResourceOption(ThingFactory<? extends V> factory, SBBox box, Resource sampleNode) 
	throws NoProperInputException {
		return new CellThingOption<V>(factory.create(askForResource(sampleNode)));			
	}

	public CellLiteralOption askForCellLiteralOption(Literal sampleNode) throws NoProperInputException {
		return new CellLiteralOption(askForLiteral(sampleNode));			
	}

	protected Resource askForResource(Resource sampleNode) 
	throws NoProperInputException {
		String sampleURI = sampleNode != null ? sampleNode.getURI() : "";
		String newURI = (String) JOptionPane.showInputDialog(table, 
				"Please type the URI of a new resource", "Create new resource", 
				JOptionPane.PLAIN_MESSAGE, null, null, sampleURI);
		if(newURI == null || newURI.length() < 2) { throw new NoProperInputException(); }
		return NodeUtil.fromString(newURI);
	}

	protected Literal askForLiteral(Literal sampleNode) {
		String sampleValue = sampleNode != null ? sampleNode.getLexicalForm() : "";
		RDFDatatype datatype = sampleNode.getDatatype();
		String newValue = (String) JOptionPane.showInputDialog(table, 
				"Please type the value of a new literal", "Create new literal", 
				JOptionPane.PLAIN_MESSAGE, null, null, sampleValue);
		return datatype != null ? ResourceFactory.createTypedLiteral(newValue, datatype) : 
			ResourceFactory.createPlainLiteral(newValue);
	}

}
