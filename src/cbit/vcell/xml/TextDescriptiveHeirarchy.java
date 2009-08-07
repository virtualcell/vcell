/**
 * 
 */
package cbit.vcell.xml;

import org.jdom.Text;

class TextDescriptiveHeirarchy extends DescriptiveHeirarchy{
	private Text text;
	
	public TextDescriptiveHeirarchy(Text text){
		this.text = text;
	}
	public Text getText(){
		return text;
	}
}