package org.vcell.sybil.models.graphcomponents.tag;

/*   SyCoTagTool  --- by Oliver Ruebenacker, UCHC --- March 2008
 *   SyCoTag for SyCo created to support a tool
 */

public class RDFGraphCompTagTool implements RDFGraphCompTag {

	protected Object tool;
	
	public RDFGraphCompTagTool(Object tool) { this.tool = tool; }
	
	public Object tool() { return tool; }
	
}
