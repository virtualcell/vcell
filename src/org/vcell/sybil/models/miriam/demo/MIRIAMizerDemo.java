package org.vcell.sybil.models.miriam.demo;

/*   MIRIAMizerImp  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Handling MIRIAM annotations
 */

import java.util.Map;
import java.util.Set;

import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.rdf.RDFBox;
import org.vcell.sybil.rdf.schemas.MIRIAM.BioProperties;
import org.vcell.sybil.rdf.schemas.MIRIAM.ModelProperties;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.registry.Registry;
import cbit.vcell.biomodel.meta.registry.Registry.Entry;
import cbit.vcell.model.Species;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PrefixMapping;

public class MIRIAMizerDemo {

	private static final String nsEx = "http://example.org/";

	public static void setPrefixes(RDFBox.Default box) {
		box.getRdf().setNsPrefixes(PrefixMapping.Standard);
		box.getRdf().setNsPrefix("ex", nsEx);
		box.getRdf().setNsPrefix("bqbiol", BioProperties.ns);
		box.getRdf().setNsPrefix("bqmodel", ModelProperties.ns);
	}
	
	public static void main(String[] args) {
		try {
	//		RDFBox.Default box = new RDFBox.Default();
	//		RDFThing egfr = box.createThing(nsEx + "egfr");
	//		MIRIAMizer miriamizer = new MIRIAMizerImp();
	//		MIRIAMRef refEGFR = new MIRIAMRef("uniprot", "P00533");
	//		miriamizer.newRefGroup(egfr, BioQualifier.is, refEGFR);
	//		setPrefixes(box);
	//		box.getRdf().write(System.out, "N3");
			
		    cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
		        private StringBuffer buffer = new StringBuffer();
		        public void sendMessage(int messageLevel, int messageType) {
		            String message = cbit.util.xml.VCLogger.getDefaultMessage(messageType);
		            sendMessage(messageLevel, messageType, message);	
		        }
		        public void sendMessage(int messageLevel, int messageType, String message) {
		            System.out.println("LOGGER: msgLevel="+messageLevel+", msgType="+messageType+", "+message);
		            if (messageLevel==VCLogger.HIGH_PRIORITY){
		            	throw new RuntimeException("SBML Import Error: "+message);
		            }
		        }
		        public void sendAllMessages() {
		        }
		        public boolean hasMessages() {
		            return false;
		        }
		    };
	
			SBMLImporter importer = new SBMLImporter("C:\\Temp\\BIOMD0000000005.xml", logger);
			BioModel biomodel = importer.getBioModel();
			MiriamManager miriamManager = biomodel.getVCMetaData().getMiriamManager();
			Species species[] = biomodel.getModel().getSpecies();
			biomodel.getVCMetaData().printRdfPretty();
			printRegistry(biomodel.getVCMetaData().getRegistry());
			for (int i = 0; i < species.length; i++) {
				Map<MiriamRefGroup, MIRIAMQualifier> refGroupMap = miriamManager.getAllMiriamRefGroups(species[i]);
	//			System.err.println("RefGps Size : " + refGps.size());
				if (refGroupMap!=null){
					for (MiriamRefGroup refGroup : refGroupMap.keySet()) {
						MIRIAMQualifier qualifier = refGroupMap.get(refGroup);
						Set<MiriamResource> miriamRef = refGroup.getMiriamRefs();
		//				System.err.println("MiriamRefGp Size : " + miriamRef.size());
						for (MiriamResource resource : miriamRef) {
							System.out.println("Species : "+species[i].getCommonName() + "  " + qualifier.property().getLocalName() + "  DataType : " + resource.getDataType().getBaseURN()  + ";\t URN : " + resource.getMiriamURN());
						}
					}
				}else{
					System.out.println("Species : "+species[i].getCommonName() + " has no statements");
				}
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	public static void printRegistry(Registry registry){
		System.out.println("Registry entries---------------------------------------");
		Set<Entry> allEntries = registry.getAllEntries();
		for (Entry entry : allEntries){
			System.out.println(entry);
		}
		System.out.println("done---------------------------------------------------");
	}
	
	public static void printSubjects(Model jenaModel){
		ResIterator resIterator = jenaModel.listSubjects();
		while (resIterator.hasNext()){
			Resource resource = resIterator.next();
			System.out.println("Resource : "+ resource.getURI());
			printStatementsForSubject(jenaModel, resource);
		}
	}
	
	public static void printStatementsForSubject(Model jenaModel, final Resource resource){
		Selector selector = new Selector() {
			public boolean test(Statement arg0) {
				return false;
			}
			public boolean isSimple() {
				return true;
			}
			public Resource getSubject() {
				return resource;
			}
			public Property getPredicate() {
				return null;
			}
			public RDFNode getObject() {
				return null;
			}
		};
		StmtIterator stmtIterator = jenaModel.listStatements(selector);
		while (stmtIterator.hasNext()){
			Statement statement = stmtIterator.next();
			System.out.println("     Statement : "+ statement.getSubject().getURI()+" : " + statement.getPredicate().getURI()+" : "+statement.getObject().toString());
		}
	}
}
