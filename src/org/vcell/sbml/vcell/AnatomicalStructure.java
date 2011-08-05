/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml.vcell;

import java.util.HashMap;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.util.xml.XmlUtil;

public class AnatomicalStructure {
	public final static int GROUP_EXTRACELLULAR = 0;
	public final static int GROUP_CELL = 1;
	public final static int GROUP_CELL_MEMBRANE = 2;
	public final static int GROUP_CYTOSOL = 3;
	public final static int GROUP_NUCLEUS = 4;
	public final static int GROUP_ER = 5;
	public final static int GROUP_MITOCHONDRION = 6;
	public final static int GROUP_INTRA_MITOCHONDRIAL_SPACE = 7;
	public final static int GROUP_ENDOSOME = 8;
	
	public final static String[] groupNames = {
		"extracellular",
		"cell",
		"cellMembrane",
		"cytosol",
		"nucleus",
		"er",
		"mitochondrion",
		"intraMitoSpace",
		"endosome"
	};
	
	private final static Graph graph;
	
	static {
		graph = new Graph();
		Node extraNode = new Node(Integer.toString(GROUP_EXTRACELLULAR));
		Node cellNode = new Node(Integer.toString(GROUP_CELL));
		Node cellMemNode = new Node(Integer.toString(GROUP_CELL_MEMBRANE));
		Node cytosolNode = new Node(Integer.toString(GROUP_CYTOSOL));
		Node nucNode = new Node(Integer.toString(GROUP_NUCLEUS));
		Node erNode = new Node(Integer.toString(GROUP_ER));
		Node mitoNode = new Node(Integer.toString(GROUP_MITOCHONDRION));
		Node imsNode = new Node(Integer.toString(GROUP_INTRA_MITOCHONDRIAL_SPACE));
		Node endosomeNode = new Node(Integer.toString(GROUP_ENDOSOME));
		graph.addNode(extraNode);
		graph.addNode(cellNode);
		graph.addNode(cellMemNode);
		graph.addNode(cytosolNode);
		graph.addNode(nucNode);
		graph.addNode(erNode);
		graph.addNode(mitoNode);
		graph.addNode(imsNode);
		graph.addNode(endosomeNode);
		graph.addEdge(new Edge(cellNode,extraNode));
		graph.addEdge(new Edge(cellMemNode,extraNode));
		graph.addEdge(new Edge(cytosolNode,cellMemNode));
		graph.addEdge(new Edge(nucNode,cytosolNode));
		graph.addEdge(new Edge(erNode,cytosolNode));
		graph.addEdge(new Edge(mitoNode,cytosolNode));
		graph.addEdge(new Edge(imsNode,mitoNode));
		graph.addEdge(new Edge(endosomeNode,cytosolNode));
	}
	
	private String GO_TERM_NAME;
	private String GO_TERM_ID;
	private int group;
	
	private AnatomicalStructure(String GoTermID, String GoTermName, int Group){
		this.GO_TERM_ID = GoTermID;
		this.GO_TERM_NAME = GoTermName;
		this.group = Group;
	}
	public final static AnatomicalStructure[] terms = {
		new AnatomicalStructure("0005576", "extracellular region", GROUP_EXTRACELLULAR), 
		new AnatomicalStructure("0005615", "extracellular space", GROUP_EXTRACELLULAR),
		new AnatomicalStructure("0005623", "cell", GROUP_CELL),
		new AnatomicalStructure("0016020", "membrane", GROUP_CELL_MEMBRANE),
		new AnatomicalStructure("0005886", "plasma membrane", GROUP_CELL_MEMBRANE),
		new AnatomicalStructure("0009898", "internal side of plasma membrane", GROUP_CELL_MEMBRANE),
		new AnatomicalStructure("0005622", "intracellular", GROUP_CYTOSOL),
		new AnatomicalStructure("0005737", "cytoplasm", GROUP_CYTOSOL),
		new AnatomicalStructure("0005829", "cytosol", GROUP_CYTOSOL),
		new AnatomicalStructure("0005783", "endoplasmic reticulum", GROUP_ER),
		new AnatomicalStructure("0005790", "smooth endoplasmic reticulum", GROUP_ER),
		new AnatomicalStructure("0005634", "nucleus", GROUP_NUCLEUS),
		new AnatomicalStructure("0005768", "endosome", GROUP_ENDOSOME),
		new AnatomicalStructure("0020015", "glycosome", GROUP_ENDOSOME),
		new AnatomicalStructure("0005739", "mitochondrion", GROUP_MITOCHONDRION),
		new AnatomicalStructure("0005758", "mitochondrial intermembrane space", GROUP_INTRA_MITOCHONDRIAL_SPACE),
	};
	public static AnatomicalStructure fromGOTerm(String GoTERM){
		for (int i = 0; i < terms.length; i++) {
			if (terms[i].GO_TERM_ID.equals(GoTERM)){
				return terms[i];
			}
		}
		return null;
	}
	public boolean equals(Object obj){
		if (obj instanceof AnatomicalStructure){
			AnatomicalStructure other = (AnatomicalStructure)obj;
			if (other.GO_TERM_ID.equals(GO_TERM_ID)){
				return true;
			}
		}
		return false;
	}
	public int hashCode(){
		return GO_TERM_ID.hashCode();
	}
	
	public String getGOTermID() {
		return GO_TERM_ID;
	}
	public String getGOTermName() {
		return GO_TERM_NAME;
	}
	public String toString(){
		return "AnatomicalStructure("+GO_TERM_ID+","+GO_TERM_NAME+","+groupNames[group]+")";
	}
	public boolean encloses(AnatomicalStructure anatStruct){
		if (this.group==anatStruct.group){
			return false;
		}
		Node[] reachableSet = graph.getDigraphReachableSet(graph.getNode(Integer.toString(anatStruct.group)));
		for (int i = 0; i < reachableSet.length; i++) {
			if (reachableSet[i].getName().equals(Integer.toString(this.group))){
				return true;
			}
		}
		return false;
	}
	
	public static HashMap<String, AnatomicalStructure> getCompartmentGoTerms(String sbmlText){
		
		HashMap<String, AnatomicalStructure> compartmentAnatomyMap = new HashMap<String, AnatomicalStructure>();
		
		Namespace sbmlNamespace = Namespace.getNamespace("http://www.sbml.org/sbml/level2");
		Namespace rdfNamespace = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		Namespace bioqualifierNamespace = Namespace.getNamespace("http://biomodels.net/biology-qualifiers/");
		
		Element rootSBML = (XmlUtil.stringToXML(sbmlText,null)).getRootElement();
		Element sbmlModelElement = rootSBML.getChild("model",sbmlNamespace);
		Element listOfCompartments = sbmlModelElement.getChild("listOfCompartments",sbmlNamespace);
		List<Element> compartmentElementList = listOfCompartments.getChildren("compartment", sbmlNamespace);
		for (Element compartmentElement : compartmentElementList){
			AnatomicalStructure anatomicalStructure = null;
			String compartmentID = compartmentElement.getAttributeValue("id");
			String goPrefix = "http://www.geneontology.org/#GO:";
			
			String compartmentOutside = compartmentElement.getAttributeValue("outside");
			Element annotationElement = compartmentElement.getChild("annotation",sbmlNamespace);
			if (annotationElement!=null){
				Element rdfElement = annotationElement.getChild("RDF",rdfNamespace);
				Element descriptionElement = rdfElement.getChild("Description",rdfNamespace);
				Element isElement = descriptionElement.getChild("is",bioqualifierNamespace);
				if (isElement==null){
					isElement = descriptionElement.getChild("isVersionOf",bioqualifierNamespace);
				}
				if (isElement!=null){
					Element bagElement = isElement.getChild("Bag",rdfNamespace);
					Element liElement = bagElement.getChild("li",rdfNamespace);
					String compartmentRdfResource = liElement.getAttributeValue("resource",rdfNamespace);
					if (compartmentRdfResource.startsWith(goPrefix)){
						String GoTerm = compartmentRdfResource.substring(goPrefix.length(), compartmentRdfResource.length());
						anatomicalStructure = AnatomicalStructure.fromGOTerm(GoTerm);
					}
					if (compartmentOutside!=null){
						System.out.println(compartmentOutside+","+compartmentID);
					}
				}
				compartmentAnatomyMap.put(compartmentID, anatomicalStructure);
			}
		}
		return compartmentAnatomyMap;
	}
	
	public static void main(String[] args){
		try {
			for (int i = 0; i < terms.length; i++) {
				System.out.print("\""+terms[i].getGOTermName()+"\" encloses: ");
				for (int j = 0; j < terms.length; j++) {
					if (terms[i].encloses(terms[j])){
						System.out.print("\""+terms[j].getGOTermName()+"\", ");
					}
				}
				System.out.println();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
