package cbit.vcell.xml.gui;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.RefGroup;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.MiriamManager.DataType;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
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
	private VCMetaData vcMetaData = null;
	
	public class LinkNode extends DefaultMutableTreeNode {
		private MIRIAMQualifier miriamQualifier = null;
		private MiriamResource miriamResource = null;
		
		public LinkNode(MIRIAMQualifier miriamQualifier, MiriamResource miriamResource) {
			super(miriamResource.getMiriamURN());
			this.miriamQualifier = miriamQualifier;
			this.miriamResource = miriamResource;
		}
		public String getLink() {
			DataType dataType = miriamResource.getDataType();
			if (dataType!=null && dataType.getBaseURL()!=null){
				return miriamResource.getDataType().getBaseURL()+miriamResource.getIdentifier();
			}else{
				return null;
			}
		}
		public String getText() {
			DataType dataType = miriamResource.getDataType();
			if (dataType!=null){
				return miriamResource.getDataType().getDataTypeName()+" ("+miriamResource.getIdentifier()+")";
			}else{
				return "UNKNOWN DATA TYPE (urn="+miriamResource.getMiriamURN()+")";
			}
		}
		public String getPredicatePrefix() {
			return miriamQualifier.toString();
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
	
	public MiriamTreeModel(TreeNode root, VCMetaData vcMetaData) {
		super(root);
		this.vcMetaData = vcMetaData;
		createTree();
	}
	
	private void createTree() {
		TreeMap<Identifiable, Map<MiriamRefGroup, MIRIAMQualifier>> miriamDescrHeir = vcMetaData.getMiriamManager().getMiriamTreeMap();
		Set<Identifiable> keys = miriamDescrHeir.keySet();
		Iterator<Identifiable> iter = keys.iterator();
		while(iter.hasNext()){
			Identifiable identifiable = iter.next();
			Map<MiriamRefGroup, MIRIAMQualifier> refGroupMap = miriamDescrHeir.get(identifiable);
			VCID vcid = vcMetaData.getIdentifiableProvider().getVCID(identifiable);
			String modelComponentType = vcid.getClassName();
			String modelComponentName = vcid.getLocalName();			
			int descHeirSize = (refGroupMap == null) ? (0) : refGroupMap.size();
			
			OpenRegistry.OpenEntry registryEntry = vcMetaData.getRegistry().getEntry(identifiable);
			
			IdentifiableNode modelComponentNode = new IdentifiableNode(identifiable, modelComponentType + " : " + modelComponentName);
			// modelComponentNode.add(new DefaultMutableTreeNode(modelComponentName));
			if (descHeirSize == 0) {
				modelComponentNode.add(new IdentifiableNode(identifiable, "None Defined"));
			}
			
			boolean bHasLinkNode = false;
			for (MiriamRefGroup refGroup : refGroupMap.keySet()){
				MIRIAMQualifier qualifier = refGroupMap.get(refGroup);
				for (MiriamResource miriamResource : refGroup.getMiriamRefs()){
					LinkNode linkNode = new LinkNode(qualifier, miriamResource);
					modelComponentNode.add(linkNode);
					bHasLinkNode = true;
				}
			}
			((DefaultMutableTreeNode)getRoot()).add(modelComponentNode);
		}
	}

}
