package org.vcell.pathway;

import java.util.ArrayList;

public class TemplateReaction extends InteractionImpl {
	
	private ArrayList<Dna> productDna;
	private ArrayList<Protein> productProtein;
	private ArrayList<Rna> productRna;
	private String templateDirection;
	private Dna templateDna;
	private DnaRegion templateDnaRegion;
	private Rna templateRna;
	private RnaRegion templateRnaRegion;
	
	public TemplateReaction(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

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

}
