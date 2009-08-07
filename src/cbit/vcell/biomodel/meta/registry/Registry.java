package cbit.vcell.biomodel.meta.registry;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * maps between resources, objects, uris and meta ids.
 * @author ruebenacker
 *
 */

public interface Registry {

	public static interface Entry {
		public Object object();
		public Resource resource();
		public String uri();
	}
	
	public Registry.Entry forObject(Object object);
	public Registry.Entry forResource(Resource resource);
	public Registry.Entry forURI(String uri);
}