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

import java.util.ArrayList;

public class TemplateReaction extends InteractionImpl {
	
	private ArrayList<Dna> productDna = new ArrayList<Dna>();
	private ArrayList<Protein> productProtein = new ArrayList<Protein>();
	private ArrayList<Rna> productRna = new ArrayList<Rna>();
	private String templateDirection;
	private Dna templateDna;
	private DnaRegion templateDnaRegion;
	private Rna templateRna;
	private RnaRegion templateRnaRegion;
	
	public ArrayList<Dna> getProductDna() {
		return productDna;
	}

	public ArrayList<Protein> getProductProtein() {
		return productProtein;
	}

	public ArrayList<Rna> getProductRna() {
		return productRna;
	}

	public String getTemplateDirection() {
		return templateDirection;
	}

	public Dna getTemplateDna() {
		return templateDna;
	}

	public DnaRegion getTemplateDnaRegion() {
		return templateDnaRegion;
	}

	public Rna getTemplateRna() {
		return templateRna;
	}

	public RnaRegion getTemplateRnaRegion() {
		return templateRnaRegion;
	}

	public void setProductDna(ArrayList<Dna> productDna) {
		this.productDna = productDna;
	}

	public void setProductProtein(ArrayList<Protein> productProtein) {
		this.productProtein = productProtein;
	}

	public void setProductRna(ArrayList<Rna> productRna) {
		this.productRna = productRna;
	}

	public void setTemplateDirection(String templateDirection) {
		this.templateDirection = templateDirection;
	}

	public void setTemplateDna(Dna templateDna) {
		this.templateDna = templateDna;
	}

	public void setTemplateDnaRegion(DnaRegion templateDnaRegion) {
		this.templateDnaRegion = templateDnaRegion;
	}

	public void setTemplateRna(Rna templateRna) {
		this.templateRna = templateRna;
	}

	public void setTemplateRnaRegion(RnaRegion templateRnaRegion) {
		this.templateRnaRegion = templateRnaRegion;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "productDna",productDna,level);
		printObjects(sb, "productProtein",productProtein,level);
		printObjects(sb, "productRna",productRna,level);
		printString(sb, "templateDirection",templateDirection,level);
		printObject(sb, "templateDna",templateDna,level);
		printObject(sb, "templateDnaRegion",templateDnaRegion,level);
		printObject(sb, "templateRna",templateRna,level);
		printObject(sb, "templateRnaRegion",templateRnaRegion,level);
	}


}
