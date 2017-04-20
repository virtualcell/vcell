/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.sbpax.util.StringUtil;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBEntityImpl;
import org.vcell.pathway.sbpax.SBVocabulary;
import org.vcell.sybil.util.text.NumberText;

public class BioPAXSBEntityTreeNode extends BioPAXTreeNode {

	public BioPAXSBEntityTreeNode(PathwayModel pathwayModel, SBEntity entity,
			TreeNode parent) {
		super(pathwayModel, entity, parent);
	}

	public SBEntity getSBEntity() { return (SBEntity) getObject(); }

	public List<String> getTermsForSBTerm() {
		List<String> termsForSBTerm = new ArrayList<String>();
		List<SBVocabulary> sbTerms = getSBEntity().getSBTerm();
		if(sbTerms != null && sbTerms.size() > 0) {
			for(SBVocabulary sbTerm : sbTerms) {
				List<String> terms = sbTerm.getTerm();
				for(String term : terms) {
					if(StringUtil.notEmpty(term) && !termsForSBTerm.contains(term)) {
						termsForSBTerm.add(term);
					}
				}
			}
		}
		return termsForSBTerm;
	}
	
	protected List<BioPAXTreeNode> getNewChildren() {
		List<BioPAXTreeNode> childrenNew = new ArrayList<BioPAXTreeNode>();
		List<SBEntity> sbSubEntities = getSBEntity().getSBSubEntity();
		if(sbSubEntities != null && sbSubEntities.size() > 0) {
			childrenNew.add(new BioPAXObjectListTreeNode<SBEntity>(getPathwayModel(), 
					sbSubEntities, SBEntity.class, this));			
		}
		String name = BioPAXClassNameDirectory.getNameSingular(getSBEntity().getClass());
		List<String> termsForSBTerm = getTermsForSBTerm();
		if(termsForSBTerm.size() > 0) {
			name = StringUtil.concat(termsForSBTerm, ", ");
		}
		labelText = trimLabelIfNeeded(
				name + ", " + 
				NumberText.soMany(sbSubEntities.size(), "sub-entry"));
		return childrenNew;
	}
	
	public String getSimplifiedLabel() {
		String simplifiedLabel = super.getSimplifiedLabel();
		List<String> termsForSBTerm = getTermsForSBTerm();
		if(termsForSBTerm.size() > 0) {
			simplifiedLabel = StringUtil.concat(termsForSBTerm, ", ");
		}
		return simplifiedLabel;
	}
	
}
