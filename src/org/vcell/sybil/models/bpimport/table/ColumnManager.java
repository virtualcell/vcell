package org.vcell.sybil.models.bpimport.table;

/*   Cols  --- by Oliver Ruebenacker, UCHC --- July 2008 to November 2009
 *   Collections and Maps for columns
 */

import java.util.List;
import java.util.Vector;

import org.vcell.sybil.models.bpimport.table.groups.GroupByColumn;
import org.vcell.sybil.models.bpimport.table.groups.GroupByResource;
import org.vcell.sybil.models.bpimport.table.groups.GroupCollective;
import org.vcell.sybil.models.bpimport.table.groups.GroupOntoColumn;
import org.vcell.sybil.models.bpimport.table.options.CellNewLiteralOption;
import org.vcell.sybil.models.bpimport.table.options.CellNewThingOption;
import org.vcell.sybil.models.bpimport.table.options.CellNoneOption;
import org.vcell.sybil.models.bpimport.table.options.CellOption;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.ThingFactory;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ColumnManager {

	protected SBBox box;
	
	public final Vector<Column> allColumns = new Vector<Column>();
	
	protected Column colProcess;
	protected Column colParticipant;
	protected Column colEntity;
	protected Column colEntityType;
	protected Column colStoichCoeff;
	protected Column colSubstance;
	protected Column colLocation;

	public SBBox box() { return box; }
	
	public ColumnManager(SBBox box) { 
		this.box = box;
		initCols();
		add(colProcess);
		add(colParticipant);
		add(colEntity);	
		add(colEntityType);
		add(colStoichCoeff);	
		add(colSubstance);
		add(colLocation);
	}

	public Column colProcess() { return colProcess; }
	public Column colParticipant() { return colParticipant; }
	public Column colEntity() { return colEntity; }
	public Column colEntityType() { return colEntityType; }
	public Column colStoichCoeff() { return colStoichCoeff; }
	public Column colSubstance() { return colSubstance; }
	public Column colLocation() { return colLocation; }
	
	public Resource colResource(String localName) { 
		return ResourceFactory.createResource(Column.defaultURI + localName); 
	}
	
	public Literal colLiteral(float value) {
		return ResourceFactory.createTypedLiteral(value);
	}
	
	protected void initCols() {
		colProcess = new Column("<html><font color=red>Process (BP)</font>/<br> " +
				"<font color=blue>Reaction ID (SB)</font>", 
				new GroupByResource(defaultOptions(box.factories().processFactory())), Column.BY_GROUP, colResource("Process"), 
				false);
		colParticipant = new Column("<html><font color=red>Participants <br> in BioPAX", 
				new GroupByResource(defaultOptions(box.factories().participantFactory())), Column.BY_GROUP, colResource("Part"), 
				false);
		colEntity = new Column("<html><font color=red>Entity (BP)", 
				new GroupByResource(defaultOptions(box.factories().substanceFactory())), Column.BY_GROUP, colResource("Entity"), false);
		colEntityType = new Column("<html><font color=red>Entity Class<br> in BioPAX", 
				new GroupByColumn(colEntity, defaultOptions(box.factories().typeFactory())), Column.BY_GROUP, 
				colResource("EntityType"), false);
		colStoichCoeff = new Column("<html>Stoich. Coef.<br> BP/SBML", 
				new GroupCollective(literalOptions()), Column.BY_CELL, colLiteral((float)1.0), true);
		colSubstance = new Column("<html><font color=blue>SpeciesType ID <br>(SBML)", 
				new GroupOntoColumn(colEntity, defaultOptions(box.factories().substanceFactory())), Column.BY_CELL, 
				colResource("Substance"), true);
		colLocation = new Column("<html><font color=red>Location (BP)</font>/<br>" + 
				"<font color=blue>Compartment (SBML)", 
				new GroupCollective(defaultOptions(box.factories().locationFactory())), Column.BY_CELL, colResource("Location"), true);
	}
	
	protected void add(Column colNew) { allColumns.add(colNew); }

	public <V extends SBBox.NamedThing> List<CellOption> defaultOptions(ThingFactory<V> factory) {
		Vector<CellOption> defaultOptions = new Vector<CellOption>();
		defaultOptions.add(new CellNewThingOption<V>(factory));
		defaultOptions.add(new CellNoneOption());
		return defaultOptions;
	}

	public List<CellOption> literalOptions() {
		Vector<CellOption> defaultOptions = new Vector<CellOption>();
		defaultOptions.add(new CellNewLiteralOption());
		defaultOptions.add(new CellNoneOption());
		return defaultOptions;
	}
	
	
	public List<Column> cols() { return allColumns; }
	public int numCols() { return cols().size(); }	
	
}
