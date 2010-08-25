package cbit.vcell.model;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.util.*;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
/**
 * This type was created in VisualAge.
 */
// TODO should it be serializable?
@SuppressWarnings("serial")
public class Diagram implements java.io.Serializable, Matchable {
	private Vector<NodeReference> nodeList = new Vector<NodeReference>();
	private Structure structure = null;
	private String name = null;
	/**
	 * Diagram constructor comment.
	 */
	public Diagram(Structure structure, String aName) {
		this.structure = structure;
		this.name = aName;
	}
	/**
	 * This method was created in VisualAge.
	 * @param nodeRef cbit.vcell.model.NodeReference
	 */
	private void addNodeReference(NodeReference nodeRef) {
		nodeList.addElement(nodeRef);
	}
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param object java.lang.Object
	 */
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

		if (!Compare.isEqual(structure,diagram.structure)){
			return false;
		}

		if (!Compare.isEqual(nodeList, diagram.nodeList)){
			return false;
		}

		return true;
	}
	/**
	 * This method was created in VisualAge.
	 * @param tokens java.util.StringTokenizer
	 */
	public void fromTokens(org.vcell.util.CommentStringTokenizer tokens) throws Exception {
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
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String getName() {
		return name;
	}
	/**
	 * This method returns the content of nodeList.
	 * Creation date: (2/27/2001 5:24:31 PM)
	 * @return java.util.Vector
	 */
	public Vector<NodeReference> getNodeList() {
		return nodeList;
	}
	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.model.Structure
	 */
	public Structure getStructure() {
		return structure;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String getVCML() {
		java.io.StringWriter stringWriter = new java.io.StringWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(stringWriter);
		write(pw);
		pw.flush();
		pw.close();
		return stringWriter.getBuffer().toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/14/2003 4:11:58 PM)
	 * @param oldName java.lang.String
	 * @param newName java.lang.String
	 */
	public void renameNode(String oldName, String newName) {
		for (int i = 0; i < nodeList.size(); i++){
			NodeReference nodeRef = nodeList.elementAt(i);
			if (nodeRef.getName().equals(oldName)){
				nodeRef.setName(newName);
			}
		}
	}
	/**
	 * This method was created in VisualAge.
	 * @param name java.lang.String
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/25/01 11:32:06 AM)
	 * @param nodeReferences cbit.vcell.model.NodeReference[]
	 */
	public void setNodeReferences(NodeReference[] nodeReferences) {
		nodeList.clear();
		nodeList.addAll(Arrays.asList(nodeReferences));
	}
	/**
	 * This method was created in VisualAge.
	 * @param structure cbit.vcell.model.Structure
	 */
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/14/2003 5:10:07 PM)
	 * @return java.lang.String
	 */
	@Override
	public String toString() {
		return "Diagram@"+Integer.toHexString(hashCode())+" for "+getStructure();
	}
	/**
	 * This method was created in VisualAge.
	 * @param pw java.io.PrintWriter
	 */
	public void write(java.io.PrintWriter pw) {

		pw.println(VCMODL.Diagram+" \""+getStructure().getName()+"\" { ");
		if (nodeList.size()>0){
			for (int i=0;i<nodeList.size();i++){
				NodeReference node = nodeList.elementAt(i);
				node.write(pw);
			}
		}
		pw.println("} ");
	}
}
