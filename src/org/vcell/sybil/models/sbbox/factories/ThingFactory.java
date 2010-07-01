package org.vcell.sybil.models.sbbox.factories;

/*   ThingFactory  --- by Oliver Ruebenacker, UCHC --- June to December 2009
 *   A factory for NamedThings
 */

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;
import org.vcell.sybil.models.sbbox.imp.RDFTypeImp;
import org.vcell.sybil.rdf.crawl.TypeCrawler;
import org.vcell.sybil.rdf.crawl.TypeCrawler.InstanceWithOntclass;
import org.vcell.sybil.util.keys.KeyOfTwo;

import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class ThingFactory<T extends NamedThing> implements Serializable{

	protected SBBox box;
	protected Resource ontClass;
	protected RDFType baseType;
	
	public ThingFactory(SBBox box, Resource ontClass) { 
		this.box = box;
		this.ontClass = ontClass;
		baseType = new RDFTypeImp(box, ontClass());
	}
	
	public final Resource ontClass() { return ontClass; };
	public final RDFType baseType() { return baseType; };
	public abstract T newThing(Resource node);
	public abstract String baseLabel();
	
	public SBBox box() { return box; }
	
	public boolean test(Resource node) { return box.getRdf().contains(node, RDF.type, ontClass()); }
	public boolean test(NamedThing thing) { return test(thing.resource()); }
	
	public T insert(T thing) { return create(thing.resource()); }
	
	public T createWithDefaultLabel() {
		String label = box.labelMan().label(baseLabel());
		String uri = box.baseURI() + label;
		T thing = createWithURI(uri);
		box.labelMan().put(thing, label);
		return thing;
	}
	
	public T createWithURI(String uri) { return create(box.getRdf().createResource(uri)); }
	
	public T createAnonymous() { return create(box.getRdf().createResource()); }
	
	public T create(Resource node) {
		box.getRdf().add(node, RDF.type, ontClass());
		return newThing(node);
	}
	
	public T create(NamedThing thing) { return create(thing.resource()); }
	
	public T open(Resource node) { return newThing(node); }
	
	public Set<T> openAll() {
		Set<T> things = new HashSet<T>();
		ResIterator nodeIter = box.getRdf().listSubjectsWithProperty(RDF.type, ontClass());
		while(nodeIter.hasNext()) { 
			Resource spec = nodeIter.nextResource();
			things.add(newThing(spec));
		}
		return things;
	}
	
	public boolean makesTypeOf(NamedThing view) {
		return box.getRdf().contains(view.resource(), RDF.type, ontClass());
	}
	
	public Set<T> openThings() {
		Set<T> things = new HashSet<T>();
		Set<Resource> instances = TypeCrawler.instances(box.getRdf(), ontClass);
		for(Resource instance : instances) { things.add(open(instance)); }
		return things;
	}
		
	public static interface ThingWithType<T> {
		public T thing();
		public RDFType type();
		public int typeRank();
	}
	
	public static class ThingWithTypeImp<T> extends KeyOfTwo<T, RDFType> 
	implements ThingWithType<T> {
		protected int typeRank;

		public ThingWithTypeImp(T thing, RDFType type, int typeRank) { 
			super(thing, type); 
			this.typeRank = typeRank;
		}
		
		public T thing() { return a(); }
		public RDFType type() { return b(); }
		public int typeRank() { return typeRank; }
	}
	
	public Set<ThingWithType<T>> openThingsWithTypes() {
		Set<ThingWithType<T>> thingsWithTypes = new HashSet<ThingWithType<T>>();
		Set<InstanceWithOntclass> instancesWithClass = 
			TypeCrawler.instancesWithClass(box.getRdf(), ontClass, box.getSbpax());
		for(TypeCrawler.InstanceWithOntclass instanceWithClass : instancesWithClass) {
			T thing = open(instanceWithClass.instance());
			RDFType type = new RDFTypeImp(box, instanceWithClass.ontclass());
			int typeRank = instanceWithClass.ontClassRank();
			thingsWithTypes.add(new ThingWithTypeImp<T>(thing, type, typeRank));
		}
		return thingsWithTypes;
	}
	
	
}
