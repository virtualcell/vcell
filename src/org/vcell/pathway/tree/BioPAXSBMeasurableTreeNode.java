package org.vcell.pathway.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.sbpax.util.StringUtil;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.SBVocabulary;

public class BioPAXSBMeasurableTreeNode extends BioPAXTreeNode {

	public BioPAXSBMeasurableTreeNode(PathwayModel pathwayModel, SBMeasurable measurable,
			TreeNode parent) {
		super(pathwayModel, measurable, parent);
	}

	public SBMeasurable getSBMeasurable() { return (SBMeasurable) getObject(); }

	protected List<BioPAXTreeNode> getNewChildren() {
		List<BioPAXTreeNode> childrenNew = new ArrayList<BioPAXTreeNode>();
//		labelText = trimLabelIfNeeded(
//				BioPAXClassNameDirectory.getNameSingular(getEntity().getClass()) + " " + 
//				StringUtil.concat(getEntity().getName(), ", "));
		labelText = "quantity: " + getSimplifiedLabel();
		return childrenNew;
	}
	
	public String getSimplifiedLabel() {
		String simplifiedLabel = super.getSimplifiedLabel();
////		ArrayList<String> names = getEntity().getName();
//		if(names != null && !names.isEmpty() && StringUtil.notEmpty(names.get(0))) {
//			simplifiedLabel = names.get(0);
//		}
		String betterLabel = "";
		ArrayList<SBVocabulary> sbTerms = getSBMeasurable().getSBTerm();
		if(sbTerms != null && !sbTerms.isEmpty()) {
			SBVocabulary sbTerm = sbTerms.get(0);
			if(sbTerm != null) {
				ArrayList<String> terms = sbTerm.getTerm();
				if(terms != null && !terms.isEmpty()) {
					String term = terms.get(0);
					if(StringUtil.notEmpty(term)) {
						betterLabel = term;
					}
				}
			}
		}
		ArrayList<Double> numbers = getSBMeasurable().getNumber();
		if(numbers != null && !numbers.isEmpty()) {
			Double number = numbers.get(0);
			if(number != null) {
				if(StringUtil.notEmpty(betterLabel)) {
					betterLabel = betterLabel + " ";
				}
				betterLabel = betterLabel + number;
			}
		}
		if(StringUtil.notEmpty(betterLabel)) {
			simplifiedLabel = betterLabel;
		}
		return simplifiedLabel;
	}
	
}
