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
	
	
	private List<NodeReference> nodeFullList = new ArrayList<NodeReference>();
	private List<NodeReference> nodeMoleculeList = new ArrayList<NodeReference>();
	private List<NodeReference> nodeRuleList = new ArrayList<NodeReference>();
	private StructureKey key = null;
	private String name = null;

	public Diagram(Structure structure, String aName) {
		this.key = new StructureKey(structure);
		this.name = aName;
	}
	
	public StructureKey getKey() { return key; }

	private void addNodeReference(NodeReference.Mode mode, NodeReference nodeRef) {
		
		switch (mode) {		// at this point the mode cannot be 'none' anymore
		case full:
			nodeFullList.add(nodeRef);
			break;
		case molecule:
			nodeMoleculeList.add(nodeRef);
			break;
		case rule:
			nodeRuleList.add(nodeRef);
			break;
		case none:
		default:
			throw new RuntimeException("Add NodeReference, the Mode must be fully explicit.");
		}
	}

	public boolean compareEqual(Matchable object) {

		Diagram diagram = null;
		if (object == null){
			return false;
		}
		if (!(object instanceof Diagram)) {
			return false;
		} else {
			diagram = (Diagram)object;
		}
		if (!Compare.isEqual(name,diagram.name)) {
			return false;
		}
		if (!Compare.isEqual(key.getStructure(), diagram.key.getStructure())) {
			return false;
		}
		if (!Compare.isEqual(nodeFullList, diagram.nodeFullList)) {
			return false;
		}
		if (!Compare.isEqual(nodeMoleculeList, diagram.nodeMoleculeList)) {
			return false;
		}
		if (!Compare.isEqual(nodeRuleList, diagram.nodeRuleList)) {
			return false;
		}
		return true;
	}

	
	public void fromTokens(CommentStringTokenizer tokens) throws Exception {
		String token = tokens.nextToken();
		boolean oldMode = true;
		if (token.equalsIgnoreCase(VCMODL.Diagram)){
			token = tokens.nextToken();  // get StructureName (and discard)
			token = tokens.nextToken();  // read '{'
			if (!token.equalsIgnoreCase(VCMODL.BeginBlock)) {
				throw new Exception("unexpected token "+token+" expecting "+VCMODL.BeginBlock);
			}			
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCMODL.BeginBlock)) {
				tokens.pushToken(token);	// old style, single block   { ... }
				oldMode = true;
			} else {
				oldMode = false;			// new style, 3 blocks inside another block  { { ... } { ... } { ... } }
			}
		}
		if(oldMode) {
			processBlock(NodeReference.Mode.none, tokens);
		} else {
			tokens = processBlock(NodeReference.Mode.full, tokens);
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCMODL.BeginBlock)) {
				throw new Exception("unexpected token "+token+" expecting "+VCMODL.BeginBlock);
			}			
			tokens = processBlock(NodeReference.Mode.molecule, tokens);
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCMODL.BeginBlock)) {
				throw new Exception("unexpected token "+token+" expecting "+VCMODL.BeginBlock);
			}			
			tokens = processBlock(NodeReference.Mode.rule, tokens);
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCMODL.EndBlock)) {
				throw new Exception("unexpected token "+token+" expecting "+VCMODL.BeginBlock);
			}			
		}
	}
	private CommentStringTokenizer processBlock(NodeReference.Mode mode, CommentStringTokenizer tokens) {
		
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCMODL.EndBlock)) {
				return tokens;		// the only 'legal' way to exit through here
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
				while (!bFound) {
					//
					// Read tokens until both integers parse.
					// Take extra tokens and append them to
					// ReactionStep name (with spaces).
					//
					try {
						int x = Integer.parseInt(xPos);
						int y = Integer.parseInt(yPos);
						pos = new java.awt.Point(x,y);
						
						switch (mode) {
						case none:
							addNodeReference(NodeReference.Mode.full, new NodeReference(NodeReference.Mode.full, nodeType,nodeName,pos));
							addNodeReference(NodeReference.Mode.molecule, new NodeReference(NodeReference.Mode.molecule, nodeType,nodeName,pos));
							addNodeReference(NodeReference.Mode.rule, new NodeReference(NodeReference.Mode.rule, nodeType,nodeName,pos));
							break;
						case full:
						case molecule:
						case rule:
							addNodeReference(mode, new NodeReference(mode, nodeType, nodeName, pos));
							break;
						default:
							throw new RuntimeException("Diagram.fromTokens(), unexpected Node.Mode attribute");
						}
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
			throw new RuntimeException("Diagram.fromTokens(), unexpected identifier "+token);
		}	
		throw new RuntimeException("Diagram.fromTokens(), end block token missing");
	}
	
	public String getName() {
		return name;
	}

	public List<NodeReference> getNodeFullList() {
		return nodeFullList;
	}
	public List<NodeReference> getNodeMoleculeList() {
		return nodeMoleculeList;
	}
	public List<NodeReference> getNodeRuleList() {
		return nodeRuleList;
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
		for(NodeReference nodeRef : nodeFullList) {
			if (nodeRef.getName().equals(oldName)){
				nodeRef.setName(newName);
			}
		}
		for(NodeReference nodeRef : nodeMoleculeList) {
			if (nodeRef.getName().equals(oldName)){
				nodeRef.setName(newName);
			}
		}
		for(NodeReference nodeRef : nodeRuleList) {
			if (nodeRef.getName().equals(oldName)){
				nodeRef.setName(newName);
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeReferences(List<NodeReference> list) {
		nodeFullList.clear();
		nodeMoleculeList.clear();
		nodeRuleList.clear();
		
		for(NodeReference nr : list) {
			switch(nr.mode) {
			case none:	// convert old style single list to multiple lists
				nodeFullList.add(new NodeReference(NodeReference.Mode.full, nr));
				nodeMoleculeList.add(new NodeReference(NodeReference.Mode.molecule, nr));
				nodeRuleList.add(new NodeReference(NodeReference.Mode.rule, nr));
				break;
			case full:
				nodeFullList.add(nr);	// the node is already well defined
				break;
			case molecule:
				nodeMoleculeList.add(nr);
				break;
			case rule:
				nodeRuleList.add(nr);
				break;
			}
		}
	}
	public void setNodeReferences(NodeReference.Mode mode, List<NodeReference> from) {
		
		List<NodeReference> to;
		switch(mode) {
		case full:
			to = nodeFullList;
			break;
		case molecule:
			to = nodeMoleculeList;
			break;
		case rule:
			to = nodeRuleList;
			break;
		case none:
		default:
			throw new RuntimeException("setNodeReferences, the Mode must be fully explicit.");
		}
		to.clear();
		for(NodeReference nr : from) {
			to.add(nr);
		}
	}
	
	public void removeNodeReferences(NodeReference.Mode mode, List<NodeReference> nodes) {
		for(NodeReference nr : nodes) {		// sanity check
			if(mode != nr.mode) {
				throw new RuntimeException("removeNodeReferences, NodeReference mode does not match List mode.");
			}
		}
		switch(mode) {
		case full:
			nodeFullList.removeAll(nodes);
			break;
		case molecule:
			nodeMoleculeList.removeAll(nodes);
			break;
		case rule:
			nodeRuleList.removeAll(nodes);
			break;
		case none:
		default:
			throw new RuntimeException("removeNodeReferences, the Mode must be explicit.");
		}
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
		pw.println("{ ");
		for(NodeReference node : nodeFullList) {
			node.write(pw);
		}
		pw.println("} ");
		pw.println("{ ");
		for(NodeReference node : nodeMoleculeList) {
			node.write(pw);
		}
		pw.println("} ");
		pw.println("{ ");
		for(NodeReference node : nodeRuleList) {
			node.write(pw);
		}
		pw.println("} ");
		pw.println("} ");
	}

}
