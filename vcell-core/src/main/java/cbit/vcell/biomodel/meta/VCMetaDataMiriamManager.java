/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel.meta;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.sbpax.schemas.MIRIAM;
import org.sbpax.schemas.ProtegeDC;
import org.vcell.model.rbm.MolecularType;
import org.vcell.pathway.BioPaxObject;
import org.vcell.sybil.models.AnnotationQualifiers;
import org.vcell.sybil.models.dublincore.DublinCoreDate;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier.DateQualifier;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.models.miriam.MIRIAMizer;
import org.vcell.sybil.models.miriam.RefGroup;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.Identifiable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.registry.Registry.Entry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class VCMetaDataMiriamManager implements MiriamManager, Serializable {
	public static final String biomodelsDbWebAccessBaseURL = "http://identifiers.org/biomodels.db/";					// used to open model in browser

	public static class VCMetaDataDataType implements DataType {
		
		private final static HashMap<String,DataType> all = new HashMap<String,DataType>();
		
		public final static DataType DataType_PIR 			= new VCMetaDataDataType("PIRSF",
				"http://pir.georgetown.edu/pirsf/",
				"https://pir.georgetown.edu/cgi-bin/ipcSF?id=",												//  ^PIRSF\d{6}$
				"urn:miriam:pirsf",
				"The PIR SuperFamily concept is being used as a guiding principle to provide comprehensive and non-overlapping clustering of UniProtKB sequences into a hierarchical order to reflect their evolutionary relationships.",
				"PIRSF000100");
		public final static DataType DataType_DOI 			= new VCMetaDataDataType("DOI",
				"http://www.doi.org/",
				"http://www.doi.org/",																						// null/^(doi\:)?\d{2}\.\d{4}.*$
				"urn:miriam:doi",
				"The Digital Object Identifier System is for identifying content objects in the digital environment.",
				"10.1038/nbt1156");
		public final static DataType DataType_BIOMODELS 	= new VCMetaDataDataType("BioModels Database",
				"http://www.ebi.ac.uk/biomodels/",
				biomodelsDbWebAccessBaseURL,																// ^((BIOMD|MODEL)\d{10})|(BMID\d{12})$
				"urn:miriam:biomodels.db",
				"BioModels Database is a data resource that allows biologists to store, search and retrieve published mathematical models of biological interests.",
				"BIOMD0000000048");
		public final static DataType DataType_Chebi 		= new VCMetaDataDataType("ChEBI",
				"http://www.ebi.ac.uk/chebi/",
//				"http://www.ebi.ac.uk/chebi/searchFreeText.do?searchString=",								// old
				"https://www.ebi.ac.uk/ols/ontologies/chebi/terms?obo_id=",									// ^CHEBI:\d+$
				"urn:miriam:chebi",
				"Chemical Entities of Biological Interest (ChEBI) is a freely available dictionary of molecular entities focused on 'small' chemical compounds.",
				"CHEBI:36927");																				// CHEBI:36927
		public final static DataType DataType_IntAct 		= new VCMetaDataDataType("IntAct",
				"http://www.ebi.ac.uk/intact/",
				"https://www.ebi.ac.uk/intact/interaction/",												// ^EBI\-[0-9]+$
				"urn:miriam:intact",
				"IntAct provides a freely available, open source database system and analysis tools for protein interaction data.",
				"EBI-2307691");
		public final static DataType DataType_InterPro 		= new VCMetaDataDataType("InterPro",
				"http://www.ebi.ac.uk/interpro/",
//				"http://www.ebi.ac.uk/interpro/DisplayIproEntry?ac=",										// old
				"https://www.ebi.ac.uk/interpro/entry/",													// ^IPR\d{6}$
				"urn:miriam:interpro",
				"InterPro is a database of protein families, domains and functional sites in which identifiable features found in known proteins can be applied to unknown protein sequences.",
				"IPR000100");
		public final static DataType DataType_ECCODE 		= new VCMetaDataDataType("Enzyme Nomenclature",
				"http://www.ec-code.org/",
				"http://www.ebi.ac.uk/intenz/query?cmd=SearchEC&ec=",										// ^\d+\.-\.-\.-|\d+\.\d+\.-\.-|\d+\.\d+\.\d+\.-|\d+\.\d+\.\d+\.(n)?\d+$
				"urn:miriam:ec-code",
				"The Enzyme Classification contains the recommendations of the Nomenclature Committee of the International Union of Biochemistry and Molecular Biology on the nomenclature and classification of enzyme-catalysed reactions.",
				"1.1.1.1");
		public final static DataType DataType_ENSEMBLE 		= new VCMetaDataDataType("Ensembl",
				"http://www.ensembl.org/",
				"“https://ensembl.org/Homo_sapiens/Gene/Summary?g=",										// ^((ENS[FPTG][file:///\\d%7b11%7d(\d+)%3f)|(FB\w%7b2%7d\d%7b7%7d)|(Y%5bA-Z%5d%7b2%7d\d%7b3%7d%5ba-zA-Z%5d(\-%5bA-Z%5d)%3f)|(%5bA-Z_a-z0-9%5d+(\.)%3f(t)%3f(\d+)%3f(%5ba-z%5d)%3f))$]\\d{11}(\\.\\d+)?)|(FB\\w{2}\\d{7})|(Y[A-Z]{2}\\d{3}[a-zA-Z](\\-[A-Z])?)|([A-Z_a-z0-9]+(\\.)?(t)?(\\d+)?([a-z])?))$
				"urn:miriam:ensembl",
				"Ensembl is a joint project between EMBL - EBI and the Sanger Institute to develop a software system which produces and maintains automatic annotation on selected eukaryotic genomes.",
				"ENSG00000139618");
		public final static DataType DataType_GO 			= new VCMetaDataDataType("Gene Ontology",
				"http://www.geneontology.org/",
				//"http://www.ebi.ac.uk/ego/GTerm?id=",														// old
				"https://www.ebi.ac.uk/ols/ontologies/go/terms?obo_id=",									// ^GO:\d{7}$
				"urn:miriam:obo.go",
				"The Gene Ontology project provides a controlled vocabulary to describe gene and gene product attributes in any organism.",
				"GO:0006915");																				// GO:0006915
		public final static DataType DataType_KEGGCOMPOUND 	= new VCMetaDataDataType("KEGG Compound",
				"http://www.genome.jp/kegg/compound/",
//				"http://www.genome.jp/dbget-bin/www_bget?cpd:",												// old
				"https://www.kegg.jp/entry/",																// ^C\d+$
				"urn:miriam:kegg.compound",
				"KEGG compound contains our knowledge on the universe of chemical substances that are relevant to life.",
				"C12345");
		public final static DataType DataType_KEGGPATHWAY 	= new VCMetaDataDataType("KEGG Pathway",
				"http://www.genome.jp/kegg/pathway/",
//				"http://www.genome.ad.jp/dbget-bin/www_bget?pathway+",										// old
				"https://www.kegg.jp/entry/",																// ^\w{2,4}\d{5}$
				"urn:miriam:kegg.pathway",
				"KEGG PATHWAY is a collection of manually drawn pathway maps representing our knowledge on the molecular interaction and reaction networks.",
				"hsa00620");
		public final static DataType DataType_KEGGORTHOLOGY = new VCMetaDataDataType("KEGG Orthology",
				"http://www.genome.jp/kegg/orthology/",
				"https://www.kegg.jp/entry/",
				"urn:miriam:kegg.orthology",
				"KEGG ORTHOLOGY is undefined.",
				"KO5079");
		public final static DataType DataType_KEGGREACTION 	= new VCMetaDataDataType("KEGG Reaction",
				"http://www.genome.jp/kegg/reaction/",
				"https://www.kegg.jp/entry/",																// ^R\d+$
				"urn:miriam:kegg.reaction",
				"KEGG reaction contains our knowledge on the universe of reactions that are relevant to life.",
				"R00100");
		public final static DataType DataType_PUBMED 		= new VCMetaDataDataType("PubMed",
				"http://www.pubmed.gov/",
				"http://www.ncbi.nlm.nih.gov/pubmed/",														// ^\d+$
				"urn:miriam:pubmed",
				"PubMed is a service of the U.S. National Library of Medicine that includes citations from MEDLINE and other life science journals for biomedical articles back to the 1950s.",
				"16333295");
		public final static DataType DataType_TAXONOMY 		= new VCMetaDataDataType("Taxonomy",
				"http://www.taxonomy.org/",
				"http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=",					// ^\d+$
				"urn:miriam:taxonomy",
				"The taxonomy contains the relationships between all living forms for which nucleic acid or protein sequence have been determined.",
				"9606");
		public final static DataType DataType_REACTOME 		= new VCMetaDataDataType("Reactome",
				"http://www.reactome.org/",
//				"http://www.reactome.org/cgi-bin/eventbrowser_st_id?FROM_REACTOME=1&ST_ID=",				// old
				"https://reactome.org/PathwayBrowser/#/",													// (^R-[A-Z]{3}-\d+(-\d+)?(\.\d+)?$)|(^REACT_\d+(\.\d+)?$)
				"urn:miriam:reactome",
				"The Reactome project is a collaboration to develop a curated resource of core pathways and reactions in human biology.",
				"R-HSA-201451");																			// https://reactome.org/content/detail/R-HSA-50825
		public final static DataType DataType_UNIPROT 		= new VCMetaDataDataType("UniProt",
				"http://www.uniprot.org/",
//				"http://www.ebi.uniprot.org/entry/",														// old
				"https://www.uniprot.org/uniprot/",															// ^([A-N,R-Z][0-9]([A-Z][A-Z, 0-9][A-Z, 0-9][0-9]){1,2})|([O,P,Q][0-9][A-Z, 0-9][A-Z, 0-9][A-Z, 0-9][0-9])(\.\d+)?$
				"urn:miriam:uniprot",
				"UniProt (Universal Protein Resource) is the world's most comprehensive catalog of information on proteins. It is a central repository of protein sequence and function created by joining the information contained in Swiss-Prot, TrEMBL, and PIR.",
				"P00533");
		public final static DataType DataType_ICD 			= new VCMetaDataDataType("ICD",	
				"http://www.who.int/classifications/icd/",
				"http://apps.who.int/classifications/icd10/browse/2010/en#/",								// ^[A-Z]\d+(\.[-\d+])?$
				"urn:miriam:icd",
				"The International Classification of Diseases is the international standard diagnostic classification for all general epidemiological and many health management purposes.",
				"C34");
		public final static DataType DataType_NeuronDB		= new VCMetaDataDataType("NeuronDB",
				"https://senselab.med.yale.edu/neurondb/",
				"http://senselab.med.yale.edu/NeuronDB/NeuronProp?id=",
				"urn:miriam:neurondb",
				"NeuronDB provides a dynamically searchable database of voltage gated conductances, neurotransmitter receptors, and neurotransmitter substances. It provides integration of these properties in a given type of neuron and compartment",
				"265");
		public final static DataType DataType_SenseLab		= new VCMetaDataDataType("ModelDB at SenseLab",
				"https://senselab.med.yale.edu/modeldb/",
			    "http://senselab.med.yale.edu/ModelDB/ShowModel.asp?model=",
			    "urn:miriam:neurondb",
			    "ModelDB is a curated, searchable database of published models in the computational neuroscience domain ",
				"45539");
//		public final static DataType DataType_CellML 			= new VCMetaDataDataType("Physiome Repository (CellML)",
//				"https://models.physiomeproject.org/welcome",
//				null,
//				"urn:miriam:",
//				"CellML Description",
//				null);
		public final static DataType DataType_Brenda 			= new VCMetaDataDataType("Brenda",
				"https://www.brenda-enzymes.org/",
				"https://www.brenda-enzymes.org/enzyme.php?ecno=",											// ^((\d+\.-\.-\.-)|(\d+\.\d+\.-\.-)|(\d+\.\d+\.\d+\.-)|(\d+\.\d+\.\d+\.\d+))$
				"urn:miriam:brenda",
				"BRENDA is a collection of enzyme functional data available to the scientific community. Data on enzyme function are extracted directly from the primary literature",
				"1.1.1.1");
//		public final static DataType DataType_Bind 			= new VCMetaDataDataType("BindingDB",
//				"https://www.bindingdb.org/bind/index.jsp",
//				null,
//				"urn:miriam:",
//				"BindingDB Description",
//				null);
		public final static DataType DataType_Mamo 			= new VCMetaDataDataType("Mamo",
				"http://bioportal.bioontology.org/ontologies/MAMO/",
				"http://identifiers.org/mamo/",
				"urn:miriam:mamo",
				"Mathematical Modelling Ontology (MAMO) is a classification of the types of mathematical models used  in the life sciences.",
				"MAMO_0000046");		// 7 digits
		public final static DataType DataType_Teddy			= new VCMetaDataDataType("Teddy",
				"http://co.mbine.org/standards/teddy/",
				"https://www.ebi.ac.uk/ols/ontologies/teddy/terms?short_form=",
				"urn:miriam:biomodels.teddy",
				"The Terminology for Description of Dynamics (TEDDY) is an ontology for dynamical behaviours, observable dynamical phenomena, and control elements of bio-models and biological systems in Systems Biology and Synthetic Biology.",
				"TEDDY_0000035");
		public final static DataType DataType_PubChem_Compound	= new VCMetaDataDataType("PubChem Compound",
				"https://pubchem.ncbi.nlm.nih.gov/",
				"https://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=",
				"urn:miriam:pubchem.compound",
				"PubChem is the collection of freely accessible chemical information.",
				"24794350");
		public final static DataType DataType_PubChem_Substance	= new VCMetaDataDataType("PubChem Substance",
				"https://pubchem.ncbi.nlm.nih.gov/",
				"https://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=",
				"urn:miriam:pubchem.substance",
				"PubChem is the collection of freely accessible chemical information.",
				"3324");
		public final static DataType DataType_Doid			= new VCMetaDataDataType("Doid",
				"http://disease-ontology.org/",
				"http://purl.bioontology.org/ontology/DOID/",		// http://disease-ontology.org/term/
				"urn:miriam:doid",
				"The Human Disease Ontology is a standardized ontology for human disease disease terms, phenotype characteristics and related medical vocabulary disease concepts.",
				"DOID:417");
		public final static DataType DataType_Omim			= new VCMetaDataDataType("Omim",
				"https://www.omim.org/",
				"https://www.omim.org/entry/",
				"urn:miriam:omim",
				"OMIM is a comprehensive, authoritative compendium of human genes and genetic phenotypes.",
				"601997");
		public final static DataType DataType_3dmet	= new VCMetaDataDataType("3Dmet",
				"http://www.3dmet.dna.affrc.go.jp/",
				"http://www.3dmet.dna.affrc.go.jp/cgi/show_data.php?acc=",
				"urn:miriam:3dmet",
				"3DMET is a database collecting three-dimensional structures of natural metabolites.",
				"B00006");
		public final static DataType DataType_CL			= new VCMetaDataDataType("Cell Ontology",
				"http://bioportal.bioontology.org/ontologies/CL/",
				"http://purl.bioontology.org/ontology/CL/",
				"urn:miriam:cl",
				"The Cell Ontology is a structured controlled vocabulary for cell types.",
				"CL:0000145");
		public final static DataType DataType_Cas			= new VCMetaDataDataType("Cas",
				"http://commonchemistry.org/",
				"http://commonchemistry.org/ChemicalDetail.aspx?ref=",
				"urn:miriam:cas",
				"CAS (Chemical Abstracts Service) is a comprehensive database of chemical information.",
				"50-78-2");
		public final static DataType DataType_BTO			= new VCMetaDataDataType("BTO",
				"http://bioportal.bioontology.org/ontologies/BTO/",
				"http://purl.bioontology.org/ontology/BTO/",
				"urn:miriam:bto",
				"The Brenda Tissue Ontology is a structured controlled vocabulary used to identify the source of an enzyme cited in the Brenda enzyme database. It comprises terms of tissues, cell lines, cell types and cell cultures.",
				"BTO:0000028");
		public final static DataType DataType_Pato			= new VCMetaDataDataType("Pato",
				"http://bioportal.bioontology.org/ontologies/PATO/",
				"http://purl.bioontology.org/ontology/PATO/",
				"urn:miriam:pato",
				"PATO is an ontology of phenotypic qualities.",
				"PATO:0002220");
		public final static DataType DataType_PW			= new VCMetaDataDataType("Pw",
				"http://rgd.mcw.edu/rgdweb/ontology/",
				"http://rgd.mcw.edu/rgdweb/ontology/annot.html?acc_id=",
				"urn:miriam:pw",
				"The Pathway Ontology captures information on biological networks, the relationships between networks and the alterations or malfunctioning of such networks within a hierarchical structure.",
				"PW:0000703");
		public final static DataType DataType_Mod			= new VCMetaDataDataType("Mod",
				"https://www.ebi.ac.uk/ols/ontologies/mod/",
				"https://www.ebi.ac.uk/ols/ontologies/mod/terms?obo_id=",
				"urn:miriam:mod",											// or urn:miriam:​obo.psi-mod
				"The Proteomics Standards Initiative modification ontology (PSI-MOD, MOD) aims to define a concensus nomenclature and ontology reconciling, in a hierarchical representation, the complementary descriptions of residue modifications.",
				"MOD:00046");												// or  MOD:00696
		public final static DataType DataType_Fma			= new VCMetaDataDataType("Fma",
				"http://purl.bioontology.org/ontology/FMA_subset/",
				"https://www.ebi.ac.uk/ols/ontologies/fma/terms?obo_id=",
				"urn:miriam:fma",
				"The Foundational Model of Anatomy Ontology (FMA) represents a coherent body of explicit declarative knowledge about human anatomy.",
				"FMA:74531");
		public final static DataType DataType_Pr			= new VCMetaDataDataType("Pr",
				"https://proconsortium.org/",
				"http://purl.bioontology.org/ontology/PR/",	// or https://proconsortium.org/app/entry/  or  https://www.ebi.ac.uk/ols/ontologies/pr/terms?obo_id=
				"urn:miriam:pr",
				"The PRotein Ontology (PRO) describes the relationships of proteins and protein evolutionary classes, to delineate the multiple protein forms of a gene locus.",
				"PR:000007300");
		public final static DataType DataType_Ncit			= new VCMetaDataDataType("Ncit",
				"http://www.ontobee.org/ontology/NCIT/",
				"http://purl.obolibrary.org/obo/NCIT_",
				"urn:miriam:ncit",
				"NCIt is a reference terminology that includes broad coverage of the cancer domain, including cancer related diseases, findings and abnormalities.",
				"C67381");
		public final static DataType DataType_So			= new VCMetaDataDataType("So",
				"http://www.sequenceontology.org/",
				"http://www.sequenceontology.org/browser/current_release/term/",
				"urn:miriam:so",
				"SO is a collaborative ontology project for the definition of sequence features used in biological sequence annotation.",
				"SO:0000234");
		public final static DataType DataType_SBO			= new VCMetaDataDataType("SBO",
				"https://www.ebi.ac.uk/sbo/",
				"https://www.ebi.ac.uk/ols/ontologies/sbo/terms?obo_id=",
				"urn:miriam:sbo",
				"The Systems Biology Ontology maintains controlled vocabularies and ontologies tailored specifically for the kinds of problems being faced in Systems Biology, especially in the context of computational modeling.",
				"SBO:0000170");
		public final static DataType DataType_EFO			= new VCMetaDataDataType("EFO",
				"https://www.ebi.ac.uk/efo/",
				"https://www.ebi.ac.uk/efo/EFO_",
				"urn:miriam:efo",
				"The Experimental Factor Ontology (EFO) provides a systematic description of experimental variables available in EBI databases.",
				"0009534");
		public final static DataType DataType_OMIT			= new VCMetaDataDataType("OMIT",		// used for species
				"https://www.ebi.ac.uk/ols/ontologies/omit/",
				"http://purl.obolibrary.org/obo/",		//   http://purl.obolibrary.org/obo/OMIT_0024493  note the _ in the id
				"urn:miriam:omit",
				"Ontology to establish data exchange standards and common data elements in the microRNA (miR) domain.",
				"OMIT_0024493");
		public final static DataType DataType_NcbiGene		= new VCMetaDataDataType("NcbiGene",	// used for species
				"https://www.ncbi.nlm.nih.gov/gene/",
				"http://identifiers.org/ncbigene/",
				"urn:miriam:ncbigene",
				"NCBI Gene integrates information from a wide range of species. A record may include nomenclature, Reference Sequences (RefSeqs), maps, pathways, variations, phenotypes, and links to genome-, phenotype-, and locus-specific resources worldwide.",
				"852125");

		
		private String name = null;
		private String siteUrl = null;
		private String urlPrefix = null;
		private String urnPrefix = null;
		private String description = null;
		private String example = null;
		
		public VCMetaDataDataType(String name, String siteUrl, String urlPrefix, String urnPrefix, String description, String example) {
			this.name = name;
			this.siteUrl = siteUrl;
			this.urlPrefix = urlPrefix;
			this.urnPrefix = urnPrefix;
			this.description = description;
			this.example = example;
			all.put(urnPrefix,this);
		}
		
		@Override
		public int hashCode(){
			return urnPrefix.hashCode();
		}
		
		@Override
		public boolean equals(Object object){
			if (object instanceof VCMetaDataDataType){
				VCMetaDataDataType dataType = (VCMetaDataDataType)object;
				if (urnPrefix.equals(dataType.urnPrefix)){
					return true;
				}
			}
			return false;
		}
		
		public String getBaseURN() {
			return urnPrefix;
		}
		
		public String getBaseURL() {
			return urlPrefix;
		}
		
		public String getDataTypeName() {
			return name;
		}
		
		public String getDataTypeURL() {
			return siteUrl;
		}
		
		public String getDescription() {
			return description;
		}
		public String getExample() {
			return example;
		}
	}
	
	private class VCMetaDataMiriamResource implements MiriamResource {
		private MIRIAMRef sybilRef = null;
		public VCMetaDataMiriamResource(MIRIAMRef argSybilRef) {
			this.sybilRef = argSybilRef;
		}
		public DataType getDataType() {
			String key = "urn:miriam:"+sybilRef.type();
			DataType dataType = VCMetaDataDataType.all.get(key);
			return dataType;
		}
		public String getMiriamURN() {
			return sybilRef.urn();
		}
		public String getIdentifier() {
			return sybilRef.id();
		}
	}
	
	private class VCMetaDataMiriamRefGroup implements MiriamRefGroup {
		private RefGroup sybilRefGroup = null;
		
		public VCMetaDataMiriamRefGroup(RefGroup argSybilRefGroup){
			this.sybilRefGroup = argSybilRefGroup;
		}
		public Set<MiriamResource> getMiriamRefs() {
			HashSet<MiriamResource> set = new HashSet<MiriamResource>();
			Graph rdfData = vcMetaData.getRdfData();
			Set<MIRIAMRef> miriamRefs = sybilRefGroup.refs(rdfData);
			for (MIRIAMRef miriamRef : miriamRefs){
				set.add(new VCMetaDataMiriamResource(miriamRef));
			}
			return set;
		}
	}
	

	private final VCMetaData vcMetaData;
	private TreeMap<Identifiable,Map<MiriamRefGroup,MIRIAMQualifier>> miriamTreeMap = null;
	
	public VCMetaDataMiriamManager(VCMetaData arg_vcmetadata){
		this.vcMetaData = arg_vcmetadata;
	}

	public void addMiriamRefGroup(Identifiable identifiable, MIRIAMQualifier miriamQualifier, Set<MiriamResource> miriamResources) throws URNParseFailureException {
		Entry entry = vcMetaData.getRegistry().getEntry(identifiable);
		Resource resource = entry.getResource();
		if (resource == null){
			String newURIString = vcMetaData.getRegistry().generateFreeURI(identifiable);
			resource = entry.setURI(vcMetaData.getRdfData(), newURIString);
		}
		MIRIAMizer miriamizer = new MIRIAMizer();
		Graph rdfData = vcMetaData.getRdfData();
		RefGroup sybilRefGroup = miriamizer.newRefGroup(rdfData, resource, miriamQualifier);
		Set<MIRIAMRef> sybilMiriamRefs = new HashSet<MIRIAMRef>();
		for(MiriamResource miriamResource : miriamResources) {
			String miriamURN = miriamResource.getMiriamURN();
			MIRIAMRef miriamRef = MIRIAMRef.createFromURN(miriamURN);
			sybilMiriamRefs.add(miriamRef);
		}
		for(MIRIAMRef sybilMiriamRef : sybilMiriamRefs) {
			rdfData = vcMetaData.getRdfData();				// is this needed?
			sybilRefGroup.add(rdfData, sybilMiriamRef);			
		}
		invalidateCache(identifiable);
		vcMetaData.fireAnnotationEventListener(new VCMetaData.AnnotationEvent(identifiable));
	}
	
	public Map<String,DataType> getAllDataTypes() {
		return Collections.unmodifiableMap(VCMetaDataDataType.all);
	}
	
	public static List<DataType> getSpecificDataTypes(Identifiable entity) {
		List<DataType> list = new ArrayList<>();
		list.add(VCMetaDataDataType.DataType_PUBMED);
		list.add(VCMetaDataDataType.DataType_DOI);
		list.add(VCMetaDataDataType.DataType_Ncit);
		list.add(VCMetaDataDataType.DataType_GO);
		
		if(entity instanceof BioModel) {
			list.add(VCMetaDataDataType.DataType_BIOMODELS);
			list.add(VCMetaDataDataType.DataType_REACTOME);
			list.add(VCMetaDataDataType.DataType_NeuronDB);
			list.add(VCMetaDataDataType.DataType_SenseLab);
			list.add(VCMetaDataDataType.DataType_Mamo);
			list.add(VCMetaDataDataType.DataType_EFO);
			list.add(VCMetaDataDataType.DataType_Doid);

		} else if(entity instanceof Species || entity instanceof MolecularType || entity instanceof RbmObservable || entity instanceof Model.ModelParameter) {
			list.add(VCMetaDataDataType.DataType_Chebi);
			list.add(VCMetaDataDataType.DataType_KEGGCOMPOUND);
			list.add(VCMetaDataDataType.DataType_UNIPROT);
			list.add(VCMetaDataDataType.DataType_ECCODE);
			list.add(VCMetaDataDataType.DataType_REACTOME);
			list.add(VCMetaDataDataType.DataType_CL);
			list.add(VCMetaDataDataType.DataType_EFO);
			list.add(VCMetaDataDataType.DataType_BTO);
			list.add(VCMetaDataDataType.DataType_TAXONOMY);
			list.add(VCMetaDataDataType.DataType_Pato);
			list.add(VCMetaDataDataType.DataType_Pr);
			
		} else if(entity instanceof ReactionRule || entity instanceof ReactionStep) {
			list.add(VCMetaDataDataType.DataType_KEGGPATHWAY);
			list.add(VCMetaDataDataType.DataType_KEGGREACTION);
			list.add(VCMetaDataDataType.DataType_REACTOME);
			list.add(VCMetaDataDataType.DataType_Brenda);
			list.add(VCMetaDataDataType.DataType_IntAct);
			list.add(VCMetaDataDataType.DataType_SBO);
			
		} else if(entity instanceof Structure) {
			list.add(VCMetaDataDataType.DataType_Brenda);
			list.add(VCMetaDataDataType.DataType_BTO);
			list.add(VCMetaDataDataType.DataType_EFO);
			
		} else if(entity instanceof SimulationContext) {
			;
		} else if(entity instanceof BioPaxObject) {
			;
		}
		
		return list;
	}


	public synchronized TreeMap<Identifiable,Map<MiriamRefGroup,MIRIAMQualifier>> getMiriamTreeMap(){
		if (miriamTreeMap==null){
			final IdentifiableProvider identifiableProvider = vcMetaData.getIdentifiableProvider();
			miriamTreeMap = new TreeMap<Identifiable,Map<MiriamRefGroup,MIRIAMQualifier>>(
				new IdentifiableComparator(identifiableProvider)
			);
			Set<Entry> allEntries = vcMetaData.getRegistry().getAllEntries();
			for (Entry entry : allEntries){
				if (entry.getResource() != null){
					Map<MiriamRefGroup,MIRIAMQualifier> refGroupMap = queryAllMiriamRefGroups(entry.getIdentifiable());
					if (refGroupMap.size()>0){
						miriamTreeMap.put(entry.getIdentifiable(), refGroupMap);
					}
				}
			}
		}
		return miriamTreeMap;
	}

	public Map<MiriamRefGroup,MIRIAMQualifier> getAllMiriamRefGroups(Identifiable identifiable) {
		return getMiriamTreeMap().get(identifiable);
	}
	

	public Set<MiriamRefGroup> getMiriamRefGroups(Identifiable identifiable, MIRIAMQualifier miriamQualifier) {
		// get from cache.
		Map<MiriamRefGroup,MIRIAMQualifier> map = getMiriamTreeMap().get(identifiable);
		HashSet<MiriamRefGroup> matchingRefGroups = new HashSet<MiriamRefGroup>();
		if (map!=null && map.size()>0){
			Set<MiriamRefGroup> allStoredRefGroups = map.keySet();
			if (map!=null && map.size()>0){
				for (MiriamRefGroup storedRefGroup : allStoredRefGroups){
					MIRIAMQualifier storedQualifier = map.get(storedRefGroup);
					if (storedQualifier.equals(miriamQualifier)){
						matchingRefGroups.add(storedRefGroup);
					}
				}
			}
		}
		return matchingRefGroups;
	}
		
	private Map<MiriamRefGroup,MIRIAMQualifier> queryAllMiriamRefGroups(Identifiable identifiable) {
		Entry entry = vcMetaData.getRegistry().getEntry(identifiable);
		if (entry.getResource() == null){
			return null;
		}
		MIRIAMizer miriamizer = new MIRIAMizer();
		Map<RefGroup, MIRIAMQualifier> allRefGroups = miriamizer.getAllRefGroups(vcMetaData.getRdfData(), entry.getResource());
		Map<MiriamRefGroup,MIRIAMQualifier> wrappedRefGroups = new HashMap<MiriamRefGroup,MIRIAMQualifier>();
		for (RefGroup refGroup : allRefGroups.keySet()){
			MiriamRefGroup miriamRefGroup = new VCMetaDataMiriamRefGroup(refGroup);
			MIRIAMQualifier miriamQualifier = allRefGroups.get(refGroup);
			wrappedRefGroups.put(miriamRefGroup, miriamQualifier);
		}
		return wrappedRefGroups;
	}

	public void remove(Identifiable identifiable, MIRIAMQualifier miriamQualifier, MiriamResource miriamResource) throws URNParseFailureException {
		// already done
		invalidateCache(identifiable);
		vcMetaData.fireAnnotationEventListener(new VCMetaData.AnnotationEvent(identifiable));
	}
	public void remove(Identifiable identifiable, MIRIAMQualifier miriamQualifier, MiriamRefGroup miriamRefGroup) throws URNParseFailureException {
		Entry openEntry = vcMetaData.getRegistry().getEntry(identifiable);		// pairing of identifiable and its resource
		Resource resource = openEntry.getResource();	// ex: http://sourceforge.net/...Feature/771568723 	(resource for compartment c0)
														// metaData works with the resource; has nothing to do with the MiriamResource
		MIRIAMizer miriamizer = new MIRIAMizer();
		RefGroup sybilRefGroup = miriamizer.newRefGroup(vcMetaData.getRdfData(), resource, miriamQualifier);
		System.out.println(vcMetaData.printRdfStatements());
		// at this point, the vcMetaData contains an extra species entry pointing to the new bag (sybilRefGroup), and the empty bag

		Set<MIRIAMRef> sybilMiriamRefs = new HashSet<MIRIAMRef>();
		Set<MiriamResource> miriamResources = miriamRefGroup.getMiriamRefs();	// resources in the group we want to delete
		for(MiriamResource miriamResource : miriamResources) {					// we make a new MIRIAMRef for each of these resources
			String miriamURN = miriamResource.getMiriamURN();			// ex: urn:miriam:pubmed:22222222
			MIRIAMRef newRef = MIRIAMRef.createFromURN(miriamURN);		// keyOfTwo(type, id)	ex: pubmed, 22222222
			sybilMiriamRefs.add(newRef);		// we just populate the sybilMiriamRefs in memory, without touching the metaData
		}
		
		for(MIRIAMRef sybilMiriamRef : sybilMiriamRefs) {
			sybilRefGroup.add(vcMetaData.getRdfData(), sybilMiriamRef);	// put in the metadata (the new bag) the content of sybilMiriamRefs, populated above
			System.out.println(vcMetaData.printRdfStatements());
		}
		// at this point the new bag is populated with the resources from the old one
		
		miriamizer.deleteRefGroup(vcMetaData, resource, sybilRefGroup);
		System.out.println(vcMetaData.printRdfStatements());
		// the extra species and its new bag are gone, everything is the way it was to begin with
		// the miriamRefGroup we wanted to delete is still there

		invalidateCache(identifiable);
		vcMetaData.fireAnnotationEventListener(new VCMetaData.AnnotationEvent(identifiable));
	}
	public void remove2(Identifiable identifiable, MIRIAMQualifier miriamQualifier, MiriamRefGroup miriamRefGroup) throws URNParseFailureException {
		
		if(!(miriamRefGroup instanceof VCMetaDataMiriamRefGroup)) {
			return;
		}
		VCMetaDataMiriamRefGroup vcmdmrf = (VCMetaDataMiriamRefGroup)miriamRefGroup;
		Entry openEntry = vcMetaData.getRegistry().getEntry(identifiable);		// pairing of identifiable and its resource
		Resource resource = openEntry.getResource();	// ex: http://sourceforge.net/...Feature/771568723 	(resource for compartment c0)
														// metaData works with the resource; has nothing to do with the MiriamResource
		MIRIAMizer miriamizer = new MIRIAMizer();
		miriamizer.deleteRefGroup(vcMetaData.getRdfData(), resource, vcmdmrf.sybilRefGroup);
//		System.out.println(vcMetaData.printRdfStatements());

		invalidateCache(identifiable);
		vcMetaData.fireAnnotationEventListener(new VCMetaData.AnnotationEvent(identifiable));
	}

	public List<URL> getStoredCrossReferencedLinks(MiriamResource miriamResource) throws MalformedURLException {
		URI resource = vcMetaData.getRdfData().getValueFactory().createURI(miriamResource.getMiriamURN());
		Iterator<Statement> iter = 
			vcMetaData.getRdfData().match(resource, MIRIAM.BioProperties.isDescribedBy, null);
		List<URL> urlList = new ArrayList<URL>();
		while (iter.hasNext()){
			Statement statement = iter.next();
			URL url = new URL(statement.getObject().toString());
			urlList.add(url);
		}
		return urlList;
	}
	
	public void addStoredCrossReferencedLink(MiriamResource miriamResource, URL url) {
		ValueFactory valueFactory = vcMetaData.getRdfData().getValueFactory();
		URI resource = valueFactory.createURI(miriamResource.getMiriamURN());
		// TODO: fix this. literally translated from the Jena-based code, but not correct in 
		// terms of MIRIAM
		vcMetaData.getRdfData().add(resource, MIRIAM.BioProperties.isDescribedBy, 
				valueFactory.createLiteral(url.toString()));
	}

	public void removeStoredCrossReferencedLink(MiriamResource miriamResource, URL url) {
		ValueFactory valueFactory = vcMetaData.getRdfData().getValueFactory();
		Resource resource = valueFactory.createURI(miriamResource.getMiriamURN());
		Iterator<Statement> iter = 
			vcMetaData.getRdfData().match(resource, MIRIAM.BioProperties.isDescribedBy, 
					valueFactory.createLiteral(url.toString()));
		while (iter.hasNext()){
			iter.next();
			iter.remove();
		}
	}

	public MiriamResource createMiriamResource(String urnString) throws URNParseFailureException {
		return new VCMetaDataMiriamResource(MIRIAMRef.createFromURN(urnString));
	}

	void invalidateCache() {
		miriamTreeMap = null;
	}

	private void invalidateCache(Identifiable identifiable) {
		if (miriamTreeMap==null){
			return; 
		}else{
			miriamTreeMap.put(identifiable, queryAllMiriamRefGroups(identifiable));
		}
	}

	public void addDate(Identifiable identifiable, DateQualifier dateQualifier,	DublinCoreDate date) {
		Entry entry = vcMetaData.getRegistry().getEntry(identifiable);
		if (entry.getResource() == null){
			String newURI = vcMetaData.getRegistry().generateFreeURI(identifiable);
			entry.setURI(vcMetaData.getRdfData(), newURI);
		}
		Graph rdfData = vcMetaData.getRdfData();
		Literal dateLiteral = rdfData.getValueFactory().createLiteral(date.getDateString());
		rdfData.add(entry.getResource(), dateQualifier.getProperty(), dateLiteral);
		vcMetaData.fireAnnotationEventListener(new VCMetaData.AnnotationEvent(identifiable));
	}

	// this is not yet cached.
	public Map<Identifiable, Map<DateQualifier, Set<DublinCoreDate>>> getDublinCoreDateMap() {
		Map<Identifiable, Map<DateQualifier, Set<DublinCoreDate>>> map =
			new HashMap<Identifiable, Map<DateQualifier, Set<DublinCoreDate>>>();
		Set<Entry> allEntries = vcMetaData.getRegistry().getAllEntries();
		Graph rdfData = vcMetaData.getRdfData();
		for (Entry entry : allEntries){
			Resource resource = entry.getResource();
			if (resource != null){
				Identifiable identifiable = entry.getIdentifiable();
				Map<DateQualifier, Set<DublinCoreDate>> qualifierDateMap = new HashMap<DateQualifier, Set<DublinCoreDate>>();
				for(DublinCoreQualifier.DateQualifier dateQualifier : AnnotationQualifiers.DC_date_all){
					Set<DublinCoreDate> dateStrings = new HashSet<DublinCoreDate>();
					Iterator<Statement> stmtIter = rdfData.match(resource, dateQualifier.getProperty(), null);
					while(stmtIter.hasNext()) {
						Statement statement = stmtIter.next();
						Value dateObject = statement.getObject();
						if(dateObject instanceof Literal) {
							Literal dateLiteral = (Literal) dateObject;
							String dateString = dateLiteral.stringValue();
							dateStrings.add(new DublinCoreDate(dateString));
						}
					}
					if(!dateStrings.isEmpty()) {
						qualifierDateMap.put(dateQualifier, dateStrings);
					}
				}
				if(!qualifierDateMap.isEmpty()) {
					map.put(identifiable, qualifierDateMap);
				}
			}
		}

		return map;
	}

	public void addCreatorToAnnotation(Identifiable identifiable, String familyName, String givenName, String email, String organization) {
		throw new RuntimeException("support for 'Creator' annotation not yet implemented");
		//vcMetaData.fireAnnotationEventListener(new AnnotationEvent(identifiable));
	}

	public void setPrettyName(MiriamResource miriamResource, String prettyName) {
		ValueFactory valueFactory = vcMetaData.getRdfData().getValueFactory();
		Resource resource = valueFactory.createURI(miriamResource.getMiriamURN());
		Literal prettyNameLiteral = valueFactory.createLiteral(prettyName);
		vcMetaData.getRdfData().add(resource, ProtegeDC.description, prettyNameLiteral);		
	}

	public String getPrettyName(MiriamResource miriamResource) {
		Resource resource = vcMetaData.getRdfData().getValueFactory().createURI(miriamResource.getMiriamURN());
		Iterator<Statement> iter = vcMetaData.getRdfData().match(resource, ProtegeDC.description, null);
		while (iter.hasNext()){
			Statement statement = iter.next();
			return statement.getObject().toString();
		}
		return null;
	}

}
