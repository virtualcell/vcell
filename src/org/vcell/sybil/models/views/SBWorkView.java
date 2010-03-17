package org.vcell.sybil.models.views;

/*   SBWorkView  --- by Oliver Ruebenacker, UCHC --- October 2009 to February 2010
 *   The main way to stores a view of an sbbox
 */

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sbml.libsbml.SBMLDocument;
import org.vcell.sybil.models.bpimport.edges.MutableEdge;
//import org.vcell.sybil.models.bpimport.table.ProcessTableModel;
import org.vcell.sybil.models.io.Exporter;
import org.vcell.sybil.models.io.Exporter.ExporterSBML;
import org.vcell.sybil.models.io.Exporter.ExporterSBPAX;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.MutableSystemModel;
import org.vcell.sybil.models.sbbox.SBBox.MutableUSTAssumption;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;
import org.vcell.sybil.util.lists.ListOfOne;
import org.vcell.sybil.util.lists.ListOfTwo;
import cbit.vcell.biomodel.BioModel;

public class SBWorkView extends SBBasicView {
	
	protected MutableUSTAssumption ustAssumption;
	protected Set<RDFType> unmodTypes = new HashSet<RDFType>();
	
	protected Set<MutableEdge> edges = new HashSet<MutableEdge>();
	
	protected MutableSystemModel systemModel;
	
	//protected ProcessTableModel tableModel;
	protected SBMLDocument sbmlDoc;
	protected BioModel bioModel;
	
	public SBWorkView(SBBox box, BioModel bioModel) { 
		super(box);
		this.bioModel = bioModel;
		ustAssumption = box.factories().ustSump().createWithDefaultLabel();
	}
	
	public MutableUSTAssumption ustAssumption() { return ustAssumption; }
	public Set<RDFType> unmodTypes() { return unmodTypes; }
	
	public Set<MutableEdge> edges() { return edges; }
	
	public void setSystemModel(MutableSystemModel systemModel) { this.systemModel = systemModel; }
	public MutableSystemModel systemModel() { return systemModel; }
	
	//public void setTableModel(ProcessTableModel tableModel) { this.tableModel = tableModel; }
	//public ProcessTableModel tableModel() { return tableModel; }

	public void setSBMLDoc(SBMLDocument sbmlDoc) { this.sbmlDoc = sbmlDoc; }

	public SBMLDocument sbmlDoc() { return sbmlDoc; }
	public BioModel bioModel() { return bioModel; }
	
	public ExporterList exporterList() {
		List<Exporter> exporters = null;
		Exporter exporter = null;
		ExporterSBPAX exporterSBPAX = new ExporterSBPAX(box.data());
		if(sbmlDoc == null) { 
			exporters = new ListOfOne<Exporter>(exporterSBPAX); 
			exporter = exporterSBPAX; 
		} else {
			ExporterSBML exporterSBML = new ExporterSBML(sbmlDoc);
			exporters = new ListOfTwo<Exporter>(exporterSBML, exporterSBPAX);
			exporter = exporterSBML;
		}
		return new ExporterList(exporters, exporter);
	}
	
}
