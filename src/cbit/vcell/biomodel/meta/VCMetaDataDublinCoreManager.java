package cbit.vcell.biomodel.meta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vcell.sybil.models.dublincore.DublinCoreManager;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier;
import org.vcell.sybil.models.dublincore.DublinCoreQualifiers;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier.DateQualifier;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;

import cbit.vcell.biomodel.meta.registry.Registry.Entry;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class VCMetaDataDublinCoreManager implements DublinCoreManager {

	private VCMetaData vcMetaData;
	
	public VCMetaDataDublinCoreManager(VCMetaData vcMetaData) {
		this.vcMetaData = vcMetaData;
	}
	
	public void addDate(Identifiable identifiable, DateQualifier dateQualifier,
			String dateString) {
		NamedThing sbThing = vcMetaData.getRegistry().getEntry(identifiable).getNamedThing();
		Model rdfData = vcMetaData.getRdfData();
		Literal dateLiteral = rdfData.createLiteral(dateString);
		rdfData.add(sbThing.resource(), dateQualifier.property(), dateLiteral);
	}

	public TreeMap<Identifiable, Map<DateQualifier, Set<String>>> getTreeMap() {
		TreeMap<Identifiable, Map<DateQualifier, Set<String>>> treeMap =
			new TreeMap<Identifiable, Map<DateQualifier, Set<String>>>();
		Set<Entry> allEntries = vcMetaData.getRegistry().getAllEntries();
		Model rdfData = vcMetaData.getRdfData();
		for (Entry entry : allEntries){
			NamedThing sbThing = entry.getNamedThing();
			if (sbThing != null){
				Identifiable identifiable = entry.getIdentifiable();
				Map<DateQualifier, Set<String>> qualifierDateMap = new HashMap<DateQualifier, Set<String>>();
				for(DublinCoreQualifier.DateQualifier dateQualifier : 
					DublinCoreQualifiers.dateQualifiers){
					StmtIterator stmtIter = 
						rdfData.listStatements(sbThing.resource(), dateQualifier.property(), 
								(RDFNode) null);
					Set<String> dateStrings = new HashSet<String>();
					while(stmtIter.hasNext()) {
						Statement statement = stmtIter.nextStatement();
						RDFNode dateObject = statement.getObject();
						if(dateObject instanceof Literal) {
							Literal dateLiteral = (Literal) dateObject;
							String dateString = dateLiteral.getLexicalForm();
							dateStrings.add(dateString);
						}
					}
					if(!dateStrings.isEmpty()) {
						qualifierDateMap.put(dateQualifier, dateStrings);
					}
				}
				if(!qualifierDateMap.isEmpty()) {
					treeMap.put(identifiable, qualifierDateMap);
				}
			}
		}

		return treeMap;
	}

}
