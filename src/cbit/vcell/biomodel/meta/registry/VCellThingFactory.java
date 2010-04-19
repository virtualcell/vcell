package cbit.vcell.biomodel.meta.registry;

/**
 * creating corresponding sbthings for identifiables
 * @author ruebenacker
 *
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.factories.Factories;
import org.vcell.sybil.models.sbbox.factories.ThingFactory;
import org.vcell.sybil.rdf.RDFBox.RDFThing;

import com.hp.hpl.jena.rdf.model.Resource;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.Structure;

public class VCellThingFactory implements Registry.IdentifiableSBThingFactory {

	protected SBBox box;
	
	public VCellThingFactory(SBBox boxNew) { box = boxNew; }
	
	public ThingFactory<?> getFactoryFor(Identifiable identifiable) {
		Factories factories = box.factories();
		ThingFactory<? extends RDFThing> factory;
		if(identifiable instanceof BioModel) {
			factory = factories.systemModelFactory();
		} else if(identifiable instanceof ReactionStep) {
			factory = factories.processModelFactory();			
		} else if(identifiable instanceof Species) {
			factory = factories.speciesFactory();			
		} else if(identifiable instanceof Structure) {
			factory = factories.locationFactory();			
		} else {
			throw new RuntimeException("Don't know how to create RDFThing for " + identifiable.getClass());
		}		
		return factory;		
	}
	
	public NamedThing createThingWithURI(Identifiable identifiable, String uri) {
		return getFactoryFor(identifiable).createWithURI(uri);
	}

	public NamedThing createThing(Identifiable identifiable, Resource resource) {
		return getFactoryFor(identifiable).create(resource);
	}

	public NamedThing createThingAnonymous(Identifiable identifiable) {
		return getFactoryFor(identifiable).createAnonymous();
	}

}
