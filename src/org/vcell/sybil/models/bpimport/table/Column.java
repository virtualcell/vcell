package org.vcell.sybil.models.bpimport.table;

/*   Column  --- by Oliver Ruebenacker, UCHC --- July 2008 to July 2009
 *   A column of a process table
 */

import org.vcell.sybil.models.bpimport.table.groups.GroupManager;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class Column {
	
	public static class SelectionMode {}
	public static final SelectionMode BY_CELL = new SelectionMode();
	public static final SelectionMode BY_GROUP = new SelectionMode();
	
	public static final String defaultURI="http://vcell.org/biopax/data/New";
	
	protected String name;
	protected GroupManager groupManager;
	protected SelectionMode mode;
	protected RDFNode sampleNode;
	protected boolean editable;
	
	public Column(String nameNew, GroupManager groupManNew, SelectionMode modeNew, RDFNode sampleNodeNew,
			boolean editable) { 
		name = nameNew; 
		groupManager = groupManNew;
		groupManager.setColumn(this);
		mode = modeNew;
		sampleNode = sampleNodeNew;
		this.editable = editable;
	}
	
	public Class<Cell> colClass() { return Cell.class; }
	public String name() { return name; }
	public GroupManager groupManager() { return groupManager; }
	public SelectionMode mode() { return mode; }
	public RDFNode sampleNode() { return sampleNode; }
	public boolean editable() { return editable; }

}