package org.vcell.sybil.models.sbbox.imp;

/*   SBWrapper  --- by Oliver Ruebenacker, UCHC --- June 2009 to April 2010
 *   A view of a resource representing an SBPAX object
 */

import java.util.List;
import java.util.Vector;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.rdf.RDFBox.ResourceWrapper;
import org.vcell.sybil.rdf.schemas.BioPAX2;
import com.hp.hpl.jena.rdf.model.Resource;

public class SBWrapper extends ResourceWrapper implements SBBox.NamedThing {
	
	public SBWrapper(SBBox box, Resource resource) { super(box, resource); }

	protected static List<ResourceNaming> namingListLong = new Vector<ResourceNaming>();
	protected static List<ResourceNaming> namingListShort = new Vector<ResourceNaming>();
	
	static {
		namingListLong.add(new ResourceNaming.StringProperty(BioPAX2.NAME));
		namingListLong.add(new ResourceNaming.StringProperty(BioPAX2.SHORT_NAME));
		namingListLong.add(new ResourceNaming.StringProperty(BioPAX2.SYNONYMS));
		namingListLong.add(new ResourceNaming.StringProperty(BioPAX2.TERM));
		namingListLong.add(new ResourceNaming.StringProperty(BioPAX2.TITLE));
		namingListLong.add(new ResourceNaming.StringProperty(BioPAX2.ID));
		namingListLong.add(new ResourceNaming.LocalName());
		namingListLong.add(new ResourceNaming.FixedName("unnamed"));
		namingListShort.add(new ResourceNaming.StringProperty(BioPAX2.SHORT_NAME));
		namingListShort.add(new ResourceNaming.StringProperty(BioPAX2.NAME));
		namingListShort.add(new ResourceNaming.StringProperty(BioPAX2.SYNONYMS));
		namingListShort.add(new ResourceNaming.StringProperty(BioPAX2.TERM));
		namingListShort.add(new ResourceNaming.StringProperty(BioPAX2.TITLE));
		namingListShort.add(new ResourceNaming.StringProperty(BioPAX2.ID));
		namingListShort.add(new ResourceNaming.LocalName());
		namingListShort.add(new ResourceNaming.FixedName("unnamed"));
	}
	
	public String name() { return nameByList(namingListLong); }
	public String shortName() { return nameByList(namingListShort); }
	public SBBox box() { return (SBBox) super.box(); }


	public String nameByList(List<ResourceNaming> namingList) { 
		String name = null;
		for(ResourceNaming naming : namingList) {
			name = naming.createName(resource(), box().getRdf());
			if(name != null && name.length() > 0) break;
		}
		return name; 
	}
	
	public String label() { return box().labelMan().label(this); }

}