package org.vcell.sybil.models.tree;

/*   NodeDataWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   A tree node with a certain type of user object and choosing a child node
 */

import java.util.Collection;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.vcell.sybil.util.text.NumberText;

public class NodeDataWrapper<T>  {

	protected T data;
	protected MutableTreeNode node;
	
	public NodeDataWrapper(T data, MutableTreeNode node) {
		this.data = data;
		this.node = node;
		node.setUserObject(this);
	}
	
	public NodeDataWrapper(T data) {
		this.data = data;
		this.node = new DefaultMutableTreeNode(this);
	}
	
	public NodeDataWrapper(T data, boolean allowsChildren) {
		this.data = data;
		this.node = new DefaultMutableTreeNode(this, allowsChildren);
	}
	
	public T data() { return data; }
	public MutableTreeNode node() { return node; }
	public String toString() { return data.toString(); }
	public int hashCode() { return data.hashCode() + node.hashCode(); }
	
	public boolean equals(Object o) {
		boolean equals = false;
		if(o instanceof NodeDataWrapper<?>) {
			NodeDataWrapper<?> oNDW = (NodeDataWrapper<?>) o;
			equals = data.equals(oNDW.data()) && node.equals(oNDW.node());
		}
		return equals;
	}
	
	public void append(MutableTreeNode childNode) {
		int nChildren = node().getChildCount();
		node().insert(childNode, nChildren);
	}
	
	public void append(NodeDataWrapper<?> childWrapper) { append(childWrapper.node()); }
	
	public void append(String nodeText) { append(new DefaultMutableTreeNode(nodeText)); }
	
	public void append(Collection<String> entries, String singular) {
		int nEntries = entries.size();
		String rootText = NumberText.soMany(nEntries, singular);
		if(nEntries > 0) {
			rootText = rootText + ": " + entries.iterator().next();
			if(nEntries > 1) {
				rootText = rootText + ", ...";
			}
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootText);
			for(String entry : entries) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(entry);
				int iChild = rootNode.getChildCount();
				rootNode.insert(childNode, iChild);
			}
			append(rootNode);
		}
	}
	
}
