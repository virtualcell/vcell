package cbit.vcell.biomodel.meta.registry;

/**
 * maps between resources, identifiables, uris and meta ids.
 * @author ruebenacker
 *
 */

import java.util.Set;

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.registry.OpenRegistry.OpenEntry;

public interface Registry {
	
	public static interface Entry {
		public Identifiable getIdentifiable();
		public NamedThing getNamedThing();
	}
	
	public static interface IdentifiableSBThingFactory {
		public NamedThing createThingWithURI(Identifiable identifiable, String uri);
		public NamedThing createThingAnonymous(Identifiable identifiable);
	}
	
	public OpenEntry newEntry(Identifiable identifiable, NamedThing sbThing);
	public OpenEntry newEntry(Identifiable identifiable, String uri);	

	
	public Registry.Entry getEntry(Identifiable identifiable);
	public Registry.Entry getEntry(Resource resource);
	
	public Set<Entry> getAllEntries();
	
}