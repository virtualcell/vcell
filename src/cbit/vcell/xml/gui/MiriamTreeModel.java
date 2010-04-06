package cbit.vcell.xml.gui;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.registry.OpenRegistry;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.Structure;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class MiriamTreeModel extends DefaultTreeModel {
	private TreeMap<Identifiable, List<Statement>> miriamDescrHeir = null;
	private VCMetaData vcMetaData = null;
	
	public class LinkNode extends DefaultMutableTreeNode {
		private String link = null;
		private String text = null;
		private String predicatePrefix = null;
		
		public LinkNode(String prefix, String link, String text) {
			super(text);
			this.predicatePrefix = prefix;
			this.link = link;
			this.text = text;
		}
		public String getLink() {
			return link;
		}
		public String getText() {
			return text;
		}
		public String getPredicatePrefix() {
			return predicatePrefix;
		}
	}
	
	public class IdentifiableNode extends DefaultMutableTreeNode {
		private Identifiable identifiable = null;
		// private String identifiableName = null;
		
		public IdentifiableNode(Identifiable identifiable, String identifiableName) {
			super(identifiableName);
			this.identifiable = identifiable;
		}
		public Identifiable getIdentifiable() {
			return identifiable;
		}
	}
	
	public MiriamTreeModel(TreeNode root, TreeMap<Identifiable, List<Statement>> miriamDescrHeir, VCMetaData vcMetaData) {
		super(root);
		this.miriamDescrHeir = miriamDescrHeir;
		this.vcMetaData = vcMetaData;
		createTree();
	}
	
	private void createTree() {
		Set<Identifiable> keys = miriamDescrHeir.keySet();
		Iterator<Identifiable> iter = keys.iterator();
		while(iter.hasNext()){
			Identifiable identifiable = iter.next();
			List<Statement> identifiableStatements = miriamDescrHeir.get(identifiable);
			String modelComponentType = 
				(identifiable instanceof BioModel?"BioModel":"")+
				(identifiable instanceof Species?"Species":"")+
				(identifiable instanceof Structure?"Structure":"")+
				(identifiable instanceof ReactionStep?"ReactionStep":"");
			String modelComponentName =
				(identifiable instanceof BioModel?((BioModel)identifiable).getName():"")+
				(identifiable instanceof Species?((Species)identifiable).getCommonName():"")+
				(identifiable instanceof Structure?((Structure)identifiable).getName():"")+
				(identifiable instanceof ReactionStep?((ReactionStep)identifiable).getName():"");
			int descHeirSize = (identifiableStatements == null) ? (0) : identifiableStatements.size();
			
			OpenRegistry.OpenEntry registryEntry = vcMetaData.getRegistry().forObject(identifiable);
			Resource resource = registryEntry.resource();
			
			IdentifiableNode modelComponentNode = new IdentifiableNode(identifiable, modelComponentType + " : " + modelComponentName);
			// modelComponentNode.add(new DefaultMutableTreeNode(modelComponentName));
			if (descHeirSize == 0) {
				modelComponentNode.add(new IdentifiableNode(identifiable, "None Defined"));
			}
			
			Model rdfModel = vcMetaData.getRdfData();
			for (int i = 0; i < descHeirSize; i++) {
				Statement statement = identifiableStatements.get(i);
				Triple triple = statement.asTriple();
				String predicateStr = triple.getPredicate().toString().substring(triple.getPredicate().getNameSpace().length());

				Node objectNode = triple.getObject();
				RDFNode objectRDFNode = statement.getObject();
//				System.out.println("object = "+objectNode.toString());
//				System.out.println("isBlank() "+objectNode.isBlank());
//				System.out.println("isConcrete() "+objectNode.isConcrete());
//				System.out.println("isLiteral() "+objectNode.isLiteral());
//				System.out.println("isURI() "+objectNode.isURI());
//				System.out.println("isVariable() "+objectNode.isVariable());
//				System.out.println("instanceof Resource "+(objectNode instanceof Resource));
//				int count = 0;
				boolean bHasLinkNode = false;
				if (!(objectNode.isURI() || objectNode.isLiteral())){
					StmtIterator bagQueryIter = rdfModel.listStatements(
							(Resource)objectRDFNode, 
							rdfModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
							rdfModel.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag"));
					
					if (bagQueryIter.hasNext()){ // this is a bag, get contents of bag
						final Resource finalObject = (Resource)objectRDFNode; 
						Selector bagContentsSelector = new Selector() {
							public RDFNode getObject() { return null; }
							public Property getPredicate() { return null; }
							public Resource getSubject() { return null; }
							public boolean isSimple() { return false; }
							public boolean test(Statement arg0) {
								if (!arg0.getSubject().equals(finalObject)) { return false; }
								Property property = arg0.getPredicate();
								String propertyString = property.toString();
								final String BagMemberPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#_";
								if (propertyString.startsWith(BagMemberPrefix)){
									try {
										String rest = propertyString.substring(BagMemberPrefix.length());
										int index = Integer.parseInt(rest);
										return true;
									}catch (NumberFormatException e){
									}
								}
								return false;
							}	
						};
						StmtIterator bagIter = rdfModel.listStatements(bagContentsSelector);						
						int bagCount=0;
						while (bagIter.hasNext()){							
							Statement stmt = bagIter.nextStatement();
							String urn = stmt.getObject().toString();							
							bagCount++;
							LinkNode linkNode = createLinkNode(predicateStr, urn);
							modelComponentNode.add(linkNode);
							bHasLinkNode = true;
						}
					}else{ // not a bag
						StmtIterator iter2 = rdfModel.listStatements((Resource)objectRDFNode, null, (RDFNode)null);
						if (iter2.hasNext()){
							Statement stmt = iter2.nextStatement();
							objectNode = stmt.asTriple().getObject();
							objectRDFNode = stmt.getObject();
						}
					}
				} else {
					// the object node is a URI, so separate it to a 'link' component and a 'text' component to display in the tree.
//					String link = objectNode.getURI();
//					String text = link.substring(link.lastIndexOf("/")+1); 
					LinkNode linkNode = createLinkNode(predicateStr, statement.getObject().toString());
					modelComponentNode.add(linkNode);
					bHasLinkNode = true;
				}
				if (!bHasLinkNode){
					modelComponentNode.add(new IdentifiableNode(identifiable, predicateStr + "  :  " + objectNode.toString()));
				}
			}
			((DefaultMutableTreeNode)getRoot()).add(modelComponentNode);
		}
	}

	private LinkNode createLinkNode(String predicateStr, String urn) {
		String link = null;
		String text = null;
		if (urn.startsWith("urn:miriam:biomodels.db:")) {
			link = urn.replaceFirst("urn:miriam:biomodels.db:", "http://www.ebi.ac.uk/biomodels-main/");
			text = urn.replaceFirst("urn:miriam:biomodels.db:", "");
		} else if (urn.startsWith("urn:miriam:pubmed:")){
			link = urn.replaceFirst("urn:miriam:pubmed:", "http://www.ncbi.nlm.nih.gov/pubmed/");
			text = urn.replaceFirst("urn:miriam:", "");
		} else if (urn.startsWith("urn:miriam:obo.go:")) {
			link = urn.replaceFirst("urn:miriam:obo.go:", "http://www.ebi.ac.uk/ego/GTerm?id=");
			text = urn.replaceFirst("urn:miriam:obo.go:", "");
		} else if (urn.startsWith("urn:miriam:reactome:")) {
			link = urn.replaceFirst("urn:miriam:reactome:", "http://www.reactome.org/cgi-bin/eventbrowser_st_id?FROM_REACTOME=1&ST_ID=");
			text = urn.replaceFirst("urn:miriam:reactome:", "");
		} else if (urn.startsWith("urn:miriam:ec-code:")) {
			link = urn.replaceFirst("urn:miriam:ec-code:", "http://www.ebi.ac.uk/intenz/query?cmd=SearchEC&ec=");
			text = urn.replaceFirst("urn:miriam:", "");
		} else if (urn.startsWith("urn:miriam:taxonomy:")) {
			link = urn.replaceFirst("urn:miriam:taxonomy:", "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=");
			text = urn.replaceFirst("urn:miriam:", "");
		} else if (urn.startsWith("urn:miriam:interpro:")) {
			link = urn.replaceFirst("urn:miriam:interpro:", "http://www.ebi.ac.uk/interpro/DisplayIproEntry?ac=");
			text = urn.replaceFirst("urn:miriam:", "");
		} else if (urn.startsWith("urn:miriam:kegg.pathway:")) {
			link = urn.replaceFirst("urn:miriam:kegg.pathway:", "http://www.genome.ad.jp/dbget-bin/www_bget?pathway+");
			text = urn.replaceFirst("urn:miriam:", "");
		} else if (urn.startsWith("urn:miriam:kegg.compound:")) {
			link = urn.replaceFirst("urn:miriam:kegg.compound:", "http://www.genome.jp/dbget-bin/www_bget?cpd:");
			text = urn.replaceFirst("urn:miriam:", "");
		} else if (urn.startsWith("urn:miriam:uniprot:")) {
			link = urn.replaceFirst("urn:miriam:uniprot:", "http://www.ebi.uniprot.org/entry/");
			text = urn.replaceFirst("urn:miriam:", "");
		} else if (urn.startsWith("urn:miriam:obo.chebi:")) {
			link = urn.replaceFirst("urn:miriam:obo.chebi:", "http://www.ebi.ac.uk/chebi/searchFreeText.do?searchString=");
			text = urn.replaceFirst("urn:miriam:obo.chebi:", "");
		} else {
			text = urn;
		}
		LinkNode linkNode = new LinkNode(predicateStr, link, text);
		return linkNode;
	}
}
