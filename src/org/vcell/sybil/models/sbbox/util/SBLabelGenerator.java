package org.vcell.sybil.models.sbbox.util;

/*   SBLabelGenerator  --- by Oliver Ruebenacker, UCHC --- July to September 2009
 *   Creates labels for SBViews
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.util.label.NumberedLabelGenerator;

public class SBLabelGenerator extends NumberedLabelGenerator<SBBox.NamedThing> {

	public static String defaultStarter = "s";
	public static char filler = '_';
	public static int maxBaseLength = 56;
	
	@Override
	public String baseLabel(NamedThing sbView) {
		StringBuffer labelBuffer = new StringBuffer();
		String name = sbView.shortName();
		String starter = defaultStarter;
		boolean addFiller = false;
		for(int i = 0; i < name.length(); ++ i) {
			char character = name.charAt(i);
			if(labelBuffer.length() == 0) {
				if(Character.isJavaIdentifierStart(character)) {
					labelBuffer.append(character);
				} else if (Character.isJavaIdentifierPart(character)) {
					labelBuffer.append(starter);					
					labelBuffer.append(character);					
				}
			} else {
				if(Character.isJavaIdentifierPart(character)) {
					if(addFiller && labelBuffer.length() < maxBaseLength -1) {
						labelBuffer.append(filler);
					}
					labelBuffer.append(character);
					addFiller = false;
				} else {
					addFiller = true;
				}
			}
			if(labelBuffer.length() >= maxBaseLength) { break; }
		}
		if(labelBuffer.length() == 0) { labelBuffer.append(defaultStarter); }
		return labelBuffer.toString();
	}
	
}
