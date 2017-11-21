/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.Hashtable;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;

public class PathwayXMLHelper {

//	public static final Namespace vcns = Namespace.getNamespace("vcns", "vcns-something");
	public static final String schemaString = new String("http://www.w3.org/2001/XMLSchema#string");
	public static final String schemaInt = new String("http://www.w3.org/2001/XMLSchema#int");
	public static final String schemaBoolean = new String("http://www.w3.org/2001/XMLSchema#boolean");
	public static final String schemaDouble = new String("http://www.w3.org/2001/XMLSchema#double");
	
	public static final Hashtable<String, String> urlHashtable = new Hashtable<String, String>(){
        {
            put("PUBMED", "http://www.ncbi.nlm.nih.gov/pubmed?term=%");
            put("CPATH", "http://www.pathwaycommons.org/pc/record2.do?id=%");
            put("UNIPROT", "http://www.uniprot.org/uniprot/%");
            put("REF_SEQ", "http://www.ncbi.nlm.nih.gov/protein/%");
            put("GENE_SYMBOL", "http://www.ncbi.nlm.nih.gov/gene?term=%");
            put("ENTREZ_GENE", "http://www.ncbi.nlm.nih.gov/gene/%");
//            put("REACTOME_STID", "http://www.reactome.org/cgi-bin/eventbrowser_st_id?ST_ID=%"); // only works for ST_IDs look like "REACT_12870" in REACTOME 
            put("REACTOME_STID", "http://www.reactome.org/content/detail/%"); // only works for ST_IDs look like "REACT_12870" in REACTOME 
            put("REACTOME_ID", "http://www.reactome.org/cgi-bin/eventbrowser?DB=gk_current&ID=%"); // only works for IDs look like "180523" in REACTOME
            put("ChEBI", "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=%");
            put("GENE_ONTOLOGY", "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=GO:%");
            put("INTERPRO", "http://www.ebi.ac.uk/interpro/DisplayIproEntry?ac=%");
            put("OMIM", "http://www.ncbi.nlm.nih.gov/omim/%");
            put("TAXONOMY", "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=%");
            put("BIOMODELS", "http://www.ebi.ac.uk/biomodels-main/%");
            put("KEGG","http://www.genome.jp/dbget-bin/www_bget?cpd:%");
            put("EC-CODE", "http://www.ebi.ac.uk/intenz/query?cmd=SearchEC&ec=%");
            
            // more
            put("HUMANCYC", "http://biocyc.org/HUMAN/NEW-IMAGE?object=%");
            put("METACYC", "http://biocyc.org/META/NEW-IMAGE?object=%");
            put("PUBCHEM","http://www.ncbi.nlm.nih.gov/sites/entrez?db=pccompound&term=%");
            put("CAS", "http://webbook.nist.gov/cgi/cbook.cgi?ID=%");
            put("PDB", "http://www.pdb.org/pdb/explore/explore.do?structureId=%");
            put("KNAPSACK", "http://kanaya.naist.jp/knapsack_jsp/information.jsp?word=%");
            put("WIKIPEDIA", "http://en.wikipedia.org/wiki/%");
            put("ENTREZ", "http://www.ncbi.nlm.nih.gov/protein/%?report=GenPept");
        }
    };
	
//	private static final String SIM_CONTAINER = XMLTags.SimulationSpecTag;
//	private static final Namespace rdf = Namespace.getNamespace("rdf",NameSpace.RDF.uri);

	private PathwayXMLHelper() {}		//no instances allowed

	private static String getElementPathString(Element childElement) {
		StringBuffer buffer = new StringBuffer();
		Element element = childElement;
		while (element!=null){
			if (buffer.length()==0){
				buffer.append(element.getName());
			}else{
				buffer.insert(0,element.getName()+"/");
			}
			Parent parent = element.getParent();
			if(parent instanceof Element) {
				element = (Element)parent;
			} else if (parent instanceof Document){
				return buffer.toString();
			}  else {
				return buffer.toString();
			}
		}
		return buffer.toString();
	}
	public static void showUnexpected(Attribute attribute, BioPaxObject bpObject) {
		String message = "Unexpected attribute " + getElementPathString(attribute.getParent()) + " << " + attribute.getQualifiedName();
		bpObject.addParserWarning(message);
		//System.out.println(message);
	}
	public static void showUnexpected(Object object, BioPaxObject bpObject) {
		String message = "Unexpected object " + object.toString();
		bpObject.addParserWarning(message);
		//System.out.println(message);
	}
	public static void showIgnored(Attribute attribute, BioPaxObject bpObject) {
		String message = "Ignored attribute " + getElementPathString(attribute.getParent()) + " << " + attribute.getQualifiedName();
		bpObject.addParserWarning(message);
		//System.out.println(message);
	}
	public static void showUnexpected(Element childElement, BioPaxObject bpObject) {
		String message = "Unexpected element " + getElementPathString(childElement);
		bpObject.addParserWarning(message);
		//System.out.println(message);
	}
	public static void showIgnored(Element childElement, String reason, BioPaxObject bpObject) {
//		if (!reason.contains("?")){
//			return;
//		}
		String message = "Ignoring element " + getElementPathString(childElement) + "   " + reason;
		bpObject.addParserWarning(message);
		//System.out.println(message);
	}
	public static void showUnexpected(Attribute attribute) {
		String message = "Unexpected attribute " + getElementPathString(attribute.getParent()) + " << " + attribute.getQualifiedName();
		//System.out.println(message);
	}
	public static void showUnexpected(Object object) {
		String message = "Unexpected object " + object.toString();
		//System.out.println(message);
	}
	public static void showIgnored(Attribute attribute) {
		String message = "Ignored attribute " + getElementPathString(attribute.getParent()) + " << " + attribute.getQualifiedName();
		//System.out.println(message);
	}
	public static void showUnexpected(Element childElement) {
		String message = "Unexpected element " + getElementPathString(childElement);
		//System.out.println(message);
	}
	public static void showIgnored(Element childElement, String reason) {
		String message = "Ignoring element " + getElementPathString(childElement) + "   " + reason;
		//System.out.println(message);
	}
	
}
