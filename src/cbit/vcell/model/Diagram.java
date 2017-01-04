/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;


import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

@SuppressWarnings("serial")
public class Diagram implements Serializable, Matchable {
	
	public static interface Key { }
	
	public static class StructureKey implements Key, Serializable {
		
		protected final Structure structure;
		
		public StructureKey(Structure structure) { this.structure = structure; }
		
		public Structure getStructure() { return structure; }

		@Override
		public boolean equals(Object object) {
			return (object instanceof StructureKey) && 
			(((StructureKey) object).getStructure().equals(structure));
		}
		
		@Override
		public int hashCode() { return structure.hashCode(); }
		
	}
	
	private List<NodeReference> nodeList = new ArrayList<NodeReference>();
	private StructureKey key = null;
	private String name = null;

	public Diagram(Structure structure, String aName) {
		this.key = new StructureKey(structure);
		this.name = aName;
	}
	
	public StructureKey getKey() { return key; }

	private void addNodeReference(NodeReference nodeRef) {
		nodeList.add(nodeRef);
	}

	public boolean compareEqual(Matchable object) {

		Diagram diagram = null;
		if (object == null){
			return false;
		}
		if (!(object instanceof Diagram)){
			return false;
		}else{
			diagram = (Diagram)object;
		}

		if (!Compare.isEqual(name,diagram.name)){
			return false;
		}

		if (!Compare.isEqual(key.getStructure(), diagram.key.getStructure())){
			return false;
		}

		if (!Compare.isEqual(nodeList, diagram.nodeList)){
			return false;
		}

		return true;
	}

	public void fromTokens(CommentStringTokenizer tokens) throws Exception {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCMODL.Diagram)){
			token = tokens.nextToken();  // get StructureName (and discard)
			token = tokens.nextToken();  // read '{'
		}			
		if (!token.equalsIgnoreCase(VCMODL.BeginBlock)){
			throw new Exception("unexpected token "+token+" expecting "+VCMODL.BeginBlock);
		}			
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCMODL.EndBlock)){
				break;
			}			
			if (token.equalsIgnoreCase(VCMODL.SimpleReaction) ||
					token.equalsIgnoreCase(VCMODL.FluxStep) ||
					token.equalsIgnoreCase(VCMODL.ReactionRule) ||
					token.equalsIgnoreCase(VCMODL.RuleParticipantFullSignature) ||
					token.equalsIgnoreCase(VCMODL.RuleParticipantShortSignature) ||
					token.equalsIgnoreCase(VCMODL.SpeciesContextSpec)){
				String nodeType = token;
				String nodeName = tokens.nextToken();
				String xPos = tokens.nextToken();
				String yPos = tokens.nextToken();
				java.awt.Point pos = null;
				boolean bFound = false;
				while (!bFound){
					//
					// Read tokens until both integers parse.
					// Take extra tokens and append them to
					// ReactionStep name (with spaces).
					//
					try {
						int x = Integer.parseInt(xPos);
						int y = Integer.parseInt(yPos);
						pos = new java.awt.Point(x,y);
						addNodeReference(new NodeReference(nodeType,nodeName,pos));
						bFound = true;
					}catch (NumberFormatException e){
						//
						// if number format exception, then ReactionStep.name probably has a space (' ') embedded.
						// so append tokens to name and keep going
						//
						nodeName = nodeName + " " + xPos;
						xPos = yPos;
						yPos = tokens.nextToken();
						System.out.println("WARNING: space in ReactionStep("+nodeType+") trying name '"+nodeName+"'");
					}
				}
				continue;
			}
			throw new Exception("Diagram.fromTokens(), unexpected identifier "+token);
		}	
	}

	public String getName() {
		return name;
	}

	public List<NodeReference> getNodeList() {
		return nodeList;
	}

	public Structure getStructure() {
		return key.getStructure();
	}

	public String getVCML() {
		java.io.StringWriter stringWriter = new java.io.StringWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(stringWriter);
		write(pw);
		pw.flush();
		pw.close();
		return stringWriter.getBuffer().toString();
	}

	public void renameNode(String oldName, String newName) {
		for(NodeReference nodeRef : nodeList) {
			if (nodeRef.getName().equals(oldName)){
				nodeRef.setName(newName);
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeReferences(NodeReference[] nodeReferences) {
		nodeList.clear();
		nodeList.addAll(Arrays.asList(nodeReferences));
	}
	public void removeNodeReferences(List<NodeReference> nodes) {
		nodeList.removeAll(nodes);
	}

	public void setStructure(Structure structure) {
		this.key = new StructureKey(structure);
	}

	@Override
	public String toString() {
		return "Diagram@"+Integer.toHexString(hashCode())+" for "+getStructure();
	}

	public void write(PrintWriter pw) {
		pw.println(VCMODL.Diagram+" \""+getStructure().getName()+"\" { ");
		for(NodeReference node : nodeList) {
			node.write(pw);
		}
		pw.println("} ");
	}

}
