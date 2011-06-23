package org.vcell.sybil.models.sbbox.factories;

/*   ThingFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for NamedThings
 */

import java.io.Serializable;

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;

@SuppressWarnings("serial")
public abstract class ThingFactory<T extends NamedThing> implements Serializable{

	protected SBBox box;
	
	public ThingFactory(SBBox box) { 
		this.box = box;
	}
	
	public abstract T newThing(Resource node);
	
	public T createWithURI(String uri) { return create(box.getRdf().getValueFactory().createURI(uri)); }
	
	public T createAnonymous() { return create(box.getRdf().getValueFactory().createBNode()); }
	
	public T create(Resource node) {
		return newThing(node);
	}
	
	public T open(Resource node) { return newThing(node); }
	
}
