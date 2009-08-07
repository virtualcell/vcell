package cbit.vcell.biomodel.meta.registry;

import java.util.Collections;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/*   MutableRegistry  --- May 2009
 *   Maintains resources and their relationships to objects
 *   @author: Oliver Ruebenacker
 */

public class OpenRegistry implements Registry {

	public static class OpenEntry implements Entry {

		protected OpenRegistry registry;
		
		public OpenEntry(OpenRegistry registry) { this.registry = registry; }
		public Object object() { return registry.entryToObject.get(this); }
		public Resource resource() { return registry.entryToResource.get(this); }
		public String uri() { return resource().getURI(); }

		public OpenEntry setObject(Object object) { 
			registry.entryToObject.put(this, object);
			registry.objectToEntry.put(object, this);
			return this; 
		}
		
		public OpenEntry setResource(Resource resource) { 
			registry.entryToResource.put(this, resource);
			registry.resourceToEntry.put(resource, this);
			return this; 
		}
		
		public OpenEntry setURI(String uri) {
			setResource(ResourceFactory.createResource(uri));
			return this;
		}
		
		public OpenEntry clearObject(Object object) { 
			registry.entryToObject.remove(this);
			registry.objectToEntry.remove(object);
			return this; 
		}
		
		public OpenEntry clearResource(Resource resource) { 
			registry.entryToResource.remove(this);
			registry.resourceToEntry.remove(resource);
			return this; 
		}
		
		public OpenEntry clearURI(String uri) {
			clearResource(ResourceFactory.createResource(uri));
			return this;
		}
		
		public OpenEntry clearEntry() {
			//
			// if we use OpenEntry within VCMetaData as a key for "other annotation" and "notes"
			// then we have to clear those entries also
			// 
			// maybe we should notify
			Object object = object(); if(object != null) { clearObject(object); }
			Resource resource = resource(); if(resource != null) { clearResource(resource); }
			return this;
		}
		
	}
	
	protected Map<Object, OpenEntry> objectToEntry = 
		Collections.synchronizedMap(new IdentityHashMap<Object, OpenEntry>());
	protected Hashtable<Resource, OpenEntry> resourceToEntry = new Hashtable<Resource, OpenEntry>();
	protected Hashtable<OpenEntry, Object> entryToObject = new Hashtable<OpenEntry, Object>();
	protected Hashtable<OpenEntry, Resource> entryToResource = new Hashtable<OpenEntry, Resource>();
	
	public OpenEntry forObject(Object object) { 
		OpenEntry entry = objectToEntry.get(object); 
		if(entry == null) { 
			entry = new OpenEntry(this).setObject(object);
		}
		return entry;
	}	
	
	public OpenEntry forResource(Resource resource) { 
		OpenEntry entry = resourceToEntry.get(resource); 
		if(entry == null) { entry = new OpenEntry(this).setResource(resource); }
		return entry;
	}	
	
	public OpenEntry forURI(String uri) { 
		Resource resource = ResourceFactory.createResource(uri);
		OpenEntry entry = resourceToEntry.get(resource); 
		if(entry == null) { entry = new OpenEntry(this).setResource(resource); }
		return entry;
	}
	
	public Set<Resource> getResources(){
		return Collections.unmodifiableSet(resourceToEntry.keySet());
	}
	
	public boolean compareEquals(OpenRegistry other) {
		return objectToEntry.equals(other.objectToEntry) && resourceToEntry.equals(other.resourceToEntry) 
		&& entryToObject.equals(other.entryToObject) && entryToResource.equals(other.entryToResource);
	}
	
}
