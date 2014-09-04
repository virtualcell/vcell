/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.xml.gui;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.vcell.sybil.models.dublincore.DublinCoreDate;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier.DateQualifier;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.document.Identifiable;

import cbit.vcell.biomodel.meta.IdentifiableComparator;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.MiriamManager.DataType;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEvent;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEventListener;
import cbit.vcell.desktop.Annotation;
import cbit.vcell.desktop.BioModelNode;

public class MiriamTreeModel extends DefaultTreeModel implements AnnotationEventListener {
	private VCMetaData vcMetaData = null;
	
	public static class LinkNode extends BioModelNode {
		private MIRIAMQualifier miriamQualifier = null;
		private MiriamResource miriamResource = null;
		
		public LinkNode(MIRIAMQualifier miriamQualifier, MiriamResource miriamResource) {
			super(miriamResource,false);
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
		public MIRIAMQualifier getMiriamQualifier() {
			return miriamQualifier;
		}
	}
	
	public static class IdentifiableNode extends BioModelNode {
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
	
	public static class DateNode extends BioModelNode {
		private DublinCoreQualifier.DateQualifier dateQualifier = null;
		private DublinCoreDate date = null;
		
		public DateNode(DublinCoreQualifier.DateQualifier dateQualifier, DublinCoreDate date) {
			super(date.getDateString(),false);
			this.dateQualifier = dateQualifier;
			this.date = date;
		}
		
		public DublinCoreQualifier.DateQualifier getDateQualifier() {
			return dateQualifier;
		}
		
		public DublinCoreDate getDate() {
			return date;
		}
	}
	
	public MiriamTreeModel(TreeNode root, VCMetaData vcMetaData) {
		super(root);
		this.vcMetaData = vcMetaData;
		vcMetaData.addAnnotationEventListener(this);
		createTree();
	}
	
	private void createTree() {
		MiriamManager miriamManager = vcMetaData.getMiriamManager();
		TreeMap<Identifiable, Map<MiriamRefGroup, MIRIAMQualifier>> miriamDescrHeir = miriamManager.getMiriamTreeMap();
		Map<Identifiable, Map<DateQualifier, Set<DublinCoreDate>>> dateMapMap = miriamManager.getDublinCoreDateMap();
		Set<Identifiable> identifiables = vcMetaData.getIdentifiableProvider().getAllIdentifiables();
		TreeSet<Identifiable> sortedIdentifiables = new TreeSet<Identifiable>(new IdentifiableComparator(vcMetaData.getIdentifiableProvider()));
		sortedIdentifiables.addAll(identifiables);
		((DefaultMutableTreeNode)getRoot()).removeAllChildren();
		for (Identifiable identifiable : sortedIdentifiables){
			Map<MiriamRefGroup, MIRIAMQualifier> refGroupMap = miriamDescrHeir.get(identifiable);
			Map<DateQualifier, Set<DublinCoreDate>> dateMap = dateMapMap.get(identifiable);
			VCID vcid = vcMetaData.getIdentifiableProvider().getVCID(identifiable);
			String modelComponentType = vcid.getClassName();
			String modelComponentName = vcid.getLocalName();			
			IdentifiableNode modelComponentNode = new IdentifiableNode(identifiable, modelComponentType + " : " + modelComponentName);
			String freeTextAnnotation = vcMetaData.getFreeTextAnnotation(identifiable);

			if (freeTextAnnotation!=null){
				modelComponentNode.add(new BioModelNode(new Annotation(freeTextAnnotation),false));
			}else{
				modelComponentNode.add(new BioModelNode(new Annotation(""),false));
			}
			if (refGroupMap!=null){
				for (MiriamRefGroup refGroup : refGroupMap.keySet()){
					MIRIAMQualifier qualifier = refGroupMap.get(refGroup);
					for (MiriamResource miriamResource : refGroup.getMiriamRefs()){
						LinkNode linkNode = new LinkNode(qualifier, miriamResource);
						modelComponentNode.add(linkNode);
					}
				}
			}
			if (dateMap!=null){
				for (DublinCoreQualifier.DateQualifier qualifier : dateMap.keySet()){
					Set<DublinCoreDate> dates = dateMap.get(qualifier);
					for (DublinCoreDate date : dates){
						modelComponentNode.add(new DateNode(qualifier,date));
					}
				}
			}
			((DefaultMutableTreeNode)getRoot()).add(modelComponentNode);
		}
	}

	public void annotationChanged(AnnotationEvent annotationEvent) {
		Identifiable identifiable = annotationEvent.getIdentifiable();
		// if identifiable is not null, then we can repair just the subtree for that identifiable
		createTree();
		fireTreeStructureChanged(this, getPathToRoot(((DefaultMutableTreeNode)getRoot())), null, null);
	}
}
