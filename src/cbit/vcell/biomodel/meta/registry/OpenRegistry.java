package cbit.vcell.biomodel.meta.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox.NamedThing;

import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;

import com.hp.hpl.jena.rdf.model.Resource;

/*   OpenRegistry  --- May 2009
 *   Maintains resources and their relationships to objects
 *   @author: Oliver Ruebenacker
 */

@SuppressWarnings("serial")
public class OpenRegistry implements Registry, Serializable {

	public class OpenEntry implements Entry, Serializable {
		private final Identifiable identifiable;
		private NamedThing namedThing = null;
		
		public OpenEntry(Identifiable identifiable) {
			this.identifiable = identifiable;
		}
		
		public OpenRegistry getRegistry() { return OpenRegistry.this; }
		public Identifiable getIdentifiable() { return identifiable; }

		public NamedThing getNamedThing() {
			return namedThing;
		}
		
		private void setNamedThing(NamedThing namedThing) {
			if (this.namedThing!=null){
				namedThingToEntry.remove(namedThing);
				resourceToEntry.remove(namedThing.resource());
				uriToEntry.remove(namedThing.resource().getURI());
			}
			this.namedThing = namedThing;
			if (namedThing!=null){
				namedThingToEntry.put(namedThing, this);
				resourceToEntry.put(namedThing.resource(), this);
				uriToEntry.put(namedThing.resource().getURI(), this);
			}
		}
		
		public NamedThing setNamedThingFromResource(Resource resource) {
			if (resource.getURI().contains("#")){
				throw new RuntimeException("unexpected character, '#' in uri");
			}
			NamedThing newNamedThing = OpenRegistry.this.sbThingFactory.createThing(identifiable, resource);
			if (newNamedThing==null){
				throw new RuntimeException("Metadata exception: could not create a namedThing for resource '"+resource.getURI()+"' and object '"+identifiable.toString()+"'");
			}
			setNamedThing(newNamedThing);
			return newNamedThing;
		}
		
		public NamedThing setNamedThingFromURI(String uri) {
			if (uri.contains("#")){
				throw new RuntimeException("unexpected character, '#' in uri");
			}
			NamedThing newNamedThing = OpenRegistry.this.sbThingFactory.createThingWithURI(identifiable, uri);
			if (newNamedThing==null){
				throw new RuntimeException("Metadata exception: could not create a namedThing for URI '"+uri+"' and object '"+identifiable.toString()+"'");
			}
			setNamedThing(newNamedThing);
			return newNamedThing;
		}
		@Override
		public String toString(){
			return getIdentifiable().toString()+" <==> " + getNamedThing();
		}
	}
	
	protected IdentifiableSBThingFactory sbThingFactory;
	
	protected ArrayList<OpenEntry> entryList = new ArrayList<OpenEntry>(); // here for ease of debugging
	protected Map<Identifiable, OpenEntry> identifiableToEntry = 
		Collections.synchronizedMap(new IdentityHashMap<Identifiable, OpenEntry>());
	protected Hashtable<NamedThing, OpenEntry> namedThingToEntry = new Hashtable<NamedThing, OpenEntry>();
	protected Hashtable<Resource, OpenEntry> resourceToEntry = new Hashtable<Resource, OpenEntry>();
	protected Hashtable<String, OpenEntry> uriToEntry = new Hashtable<String, OpenEntry>();
	protected IdentifiableProvider identifiableProvider = null;
	
	public OpenRegistry(IdentifiableSBThingFactory thingFactoryNew, IdentifiableProvider identifiableProvider) {
		sbThingFactory = thingFactoryNew;	
		this.identifiableProvider = identifiableProvider;
	}
	
	public String generateFreeURI(Identifiable identifiable) {
		while (true){
			String uri = VCMetaData.nsVCML + "/" + identifiable.getClass().getName() + "/" + (Math.abs((new Random()).nextInt()));
			if (uriToEntry.get(uri)==null){
				return uri;
			}
		}
	}
	
	public OpenEntry newEntry(Identifiable identifiable, NamedThing namedThing) {
		if (identifiable==null){
			throw new RuntimeException("cannot create a new metadata entry with a null identifiable object");
		}
		if (namedThing==null){
			throw new RuntimeException("cannot create a new metadata entry with a null named thing");
		}
		if (namedThing.resource().getURI().contains("#")){
			throw new RuntimeException("unexpected character, '#' in uri");
		}
		if (identifiableToEntry.get(identifiable)!=null){
			throw new RuntimeException("entry for '"+identifiable+"' already exists");
		}
		if (namedThingToEntry.get(namedThing)!=null){
			throw new RuntimeException("entry for named thing '"+namedThing.resource()+"' already exists");
		}
		if (resourceToEntry.get(namedThing.resource())!=null){
			throw new RuntimeException("entry for resource '"+namedThing.resource()+"' already exists");
		}
		if (uriToEntry.get(namedThing.resource().getURI())!=null){
			throw new RuntimeException("entry for uri '"+namedThing.resource().getURI()+"' already exists");
		}
		OpenEntry newEntry = new OpenEntry(identifiable);
		identifiableToEntry.put(identifiable,newEntry);
		entryList.add(newEntry);
		newEntry.setNamedThing(namedThing);
		return newEntry;
	}
	
	public OpenEntry newEntry(Identifiable identifiable, String uri) {
		if (identifiable==null){
			throw new RuntimeException("cannot create a new metadata entry with a null identifiable object");
		}
		if (uri==null){
			throw new RuntimeException("cannot create a new metadata entry with a null URI");
		}
		if (identifiableToEntry.get(identifiable)!=null){
			throw new RuntimeException("entry for '"+identifiable+"' already exists");
		}
		if (uriToEntry.get(uri)!=null){
			throw new RuntimeException("entry for uri '"+uri+"' already exists");
		}
		OpenEntry newEntry = new OpenEntry(identifiable);
		identifiableToEntry.put(identifiable,newEntry);
		entryList.add(newEntry);
		newEntry.setNamedThingFromURI(uri);
		return newEntry;
	}
	
	public OpenEntry getEntry(Identifiable object) { 
		OpenEntry entry = identifiableToEntry.get(object); 
		if(entry == null) { 
			entry = new OpenEntry(object);
			identifiableToEntry.put(object, entry);
			entryList.add(entry);
		}
		return entry;
	}	
	
	public Entry getEntry(Resource resource) {
		return resourceToEntry.get(resource);
	}
	
	public Set<Resource> getResources(){
		return resourceToEntry.keySet();
	}
	
	public Set<Entry> getAllEntries() {
		HashSet<Entry> entries = new HashSet<Entry>();
		entries.addAll(identifiableToEntry.values());
		return entries;		
	}
	
	public boolean compareEquals(OpenRegistry other) {
		Set<Resource> resSet = resourceToEntry.keySet();
		Set<Resource> otherResSet = other.resourceToEntry.keySet();
		if (!resSet.equals(otherResSet)) {
			return false;
		}
		for (Resource res : resSet) {
			OpenEntry oe = resourceToEntry.get(res);
			OpenEntry otherOe = other.resourceToEntry.get(res);
			final VCID vcid = identifiableProvider.getVCID(oe.getIdentifiable());
			final VCID otherVcid = other.identifiableProvider.getVCID(otherOe.getIdentifiable());
			if (!vcid.toASCIIString().equals(otherVcid.toASCIIString())) {
				return false;
			}
		}
		return true;
	}
}
