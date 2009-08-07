/**
 * 
 */
package cbit.vcell.xml;

import org.jdom.Attribute;


class AttributeDescriptiveHeirarchy extends DescriptiveHeirarchy{
	private Attribute attribute;
	
	public AttributeDescriptiveHeirarchy(Attribute attribute){
		this.attribute = attribute;
	}
	public Attribute getAttribute(){
		return attribute;
	}
}