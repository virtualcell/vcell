package org.vcell.sybil.rdf.schemas;

/*   SBPAX  --- by Oliver Ruebenacker, UCHC --- April 2008
 *   The SBPAX schema
 */

import java.util.List;

import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.OntUtil;
import org.vcell.sybil.util.lists.ListOfNone;
import org.vcell.sybil.util.sets.SetUtil;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class BFO {
	
	public static final OntologyBox box = new OntologyBox() {
		public Model getRdf() { return schema; }
		public String uri() { return URI; }
		public NameSpace ns() { return ns; }
		public String label() { return label; }
		public List<DatatypeProperty> labelProperties() { return labelProperties; }
	};
	
	public static final String label = "Basic Formal Ontology";
	public static final List<DatatypeProperty> labelProperties = new ListOfNone<DatatypeProperty>();
	
	public static final String URI = "http://www.ifomis.org/bfo/1.1";
	
	public static final NameSpace ns = new NameSpace("bfo", URI + "#");
	public static final NameSpace nsSnap = new NameSpace("snap", URI + "/snap#");
	public static final NameSpace nsSpan = new NameSpace("bfo", URI + "/span#");	
	public static final NameSpace nsDC = new NameSpace("dc", "http://purl.org/dc/elements/1.1/");	
	public static final NameSpace nsProtegeDC = 
		new NameSpace("protege-dc", "http://protege.stanford.edu/plugins/owl/dc/protege-dc.owl");	
	public static final NameSpace nsXSD = new NameSpace("dc", "http://www.w3.org/2001/XMLSchema#");	
	
	public static final OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	static {
		OntUtil.emptyPrefixMapping(schema);
		schema.setNsPrefix("", ns.uri);
		schema.setNsPrefix("dc", nsDC.uri);
		schema.setNsPrefix("protege-dc", nsProtegeDC.uri);
		schema.setNsPrefix("snap", nsSnap.uri);
		schema.setNsPrefix("span", nsSpan.uri);
		schema.setNsPrefix("rdfs", RDFS.getURI());
		schema.setNsPrefix("xsd", nsXSD.uri);
		schema.setNsPrefix("owl", OWL.NS);
		schema.setNsPrefix("rdf", RDF.getURI());
	}
	
	public static final Ontology ontology = schema.createOntology(URI);
	
	public static final OntClass ConnectedSpatiotemporalRegion = 
		schema.createClass(nsSpan + "ConnectedSpatiotemporalRegion");
	public static final OntClass ConnectedTemporalRegion = 
		schema.createClass(nsSpan + "ConnectedTemporalRegion");
	public static final OntClass Continuant = schema.createClass(nsSnap + "Continuant");
	public static final OntClass DependentContinuant = schema.createClass(nsSnap + "DependentContinuant");
	public static final OntClass Disposition = schema.createClass(nsSnap + "Disposition");
	public static final OntClass Entity = schema.createClass(ns + "Entity");
	public static final OntClass FiatObjectPart = schema.createClass(nsSnap + "FiatObjectPart");
	public static final OntClass FiatProcessPart = schema.createClass(nsSpan + "FiatProcessPart");
	public static final OntClass Function = schema.createClass(nsSnap + "Function");
	public static final OntClass GenericallyDependentContinuant = 
		schema.createClass(nsSnap + "GenericallyDependentContinuant");
	public static final OntClass IndependentContinuant = 
		schema.createClass(nsSnap + "IndependentContinuant");
	public static final OntClass OneDimensionalRegion = 
		schema.createClass(nsSnap + "OneDimensionalRegion");
	public static final OntClass Process = schema.createClass(nsSpan + "Process");
	public static final OntClass ProcessAggregate = schema.createClass(nsSpan + "ProcessAggregate");
	public static final OntClass ProcessBoundary = schema.createClass(nsSpan + "ProcessBoundary");
	public static final OntClass ProcessualContext = schema.createClass(nsSpan + "ProcessualContext");
	public static final OntClass ProcessualEntity = schema.createClass(nsSpan + "ProcessualEntity");
	public static final OntClass Object = schema.createClass(nsSnap + "Object");
	public static final OntClass ObjectAggregate = schema.createClass(nsSnap + "ObjectAggregate");
	public static final OntClass ObjectBoundary = schema.createClass(nsSnap + "ObjectBoundary");
	public static final OntClass Occurrent = schema.createClass(nsSpan + "Occurrent");
	public static final OntClass Quality = schema.createClass(nsSnap + "Quality");
	public static final OntClass RealizableEntity = schema.createClass(nsSnap + "RealizableEntity");
	public static final OntClass Role = schema.createClass(nsSnap + "Role");
	public static final OntClass ScatteredSpatiotemporalRegion = 
		schema.createClass(nsSpan + "ScatteredSpatiotemporalRegion");
	public static final OntClass ScatteredTemporalRegion = 
		schema.createClass(nsSpan + "ScatteredTemporalRegion");
	public static final OntClass Site = schema.createClass(nsSnap + "Site");
	public static final OntClass SpatialRegion = schema.createClass(nsSnap + "SpatialRegion");
	public static final OntClass SpatiotemporalInstant = 
		schema.createClass(nsSpan + "SpatiotemporalInstant");
	public static final OntClass SpatiotemporalInterval = 
		schema.createClass(nsSpan + "SpatiotemporalInterval");
	public static final OntClass SpatiotemporalRegion = 
		schema.createClass(nsSpan + "SpatiotemporalRegion");
	public static final OntClass SpecificallyDependentContinuant = 
		schema.createClass(nsSnap + "SpecificallyDependentContinuant");
	public static final OntClass TemporalInstant = schema.createClass(nsSpan + "TemporalInstant");
	public static final OntClass TemporalInterval = schema.createClass(nsSpan + "TemporalInterval");
	public static final OntClass TemporalRegion = schema.createClass(nsSpan + "TemporalRegion");
	public static final OntClass ThreeDimensionalRegion = 
		schema.createClass(nsSnap + "ThreeDimensionalRegion");
	public static final OntClass TwoDimensionalRegion = 
		schema.createClass(nsSnap + "TwoDimensionalRegion");
	public static final OntClass ZeroDimensionalRegion = 
		schema.createClass(nsSnap + "ZeroDimensionalRegion");
	
	static {
		OntUtil.classify(ConnectedSpatiotemporalRegion, 
				schema.createList().with(SpatiotemporalInstant).with(SpatiotemporalInterval), 
				OntUtil.EQUIV);
		OntUtil.classify(ConnectedTemporalRegion, 
				schema.createList().with(TemporalInstant).with(TemporalInterval), OntUtil.EQUIV);
		OntUtil.classify(Continuant, schema.createList().with(DependentContinuant)
				.with(IndependentContinuant).with(SpatialRegion), OntUtil.EQUIV);
		OntUtil.classify(DependentContinuant, 
				schema.createList().with(GenericallyDependentContinuant)
				.with(SpecificallyDependentContinuant), OntUtil.EQUIV);
		OntUtil.classify(Entity, schema.createList().with(Continuant).with(Occurrent), OntUtil.SAME);
		OntUtil.classify(IndependentContinuant, 
				schema.createList().with(FiatObjectPart).with(Object).with(ObjectAggregate)
				.with(ObjectBoundary).with(Site), OntUtil.EQUIV);
		OntUtil.classify(Occurrent, 
				schema.createList().with(ProcessualEntity).with(SpatiotemporalRegion)
				.with(TemporalRegion), OntUtil.EQUIV);
		OntUtil.classify(ProcessualEntity, 
				schema.createList().with(FiatProcessPart).with(Process).with(ProcessAggregate)
				.with(ProcessBoundary).with(ProcessualContext), OntUtil.EQUIV);
		OntUtil.classify(SpatialRegion, 
				schema.createList().with(OneDimensionalRegion).with(ThreeDimensionalRegion)
				.with(TwoDimensionalRegion).with(ZeroDimensionalRegion), 
				OntUtil.EQUIV);
		OntUtil.classify(SpatiotemporalRegion, 
				schema.createList().with(ConnectedSpatiotemporalRegion)
				.with(ScatteredSpatiotemporalRegion), OntUtil.EQUIV);
		OntUtil.classify(SpecificallyDependentContinuant, 
				schema.createList().with(Quality).with(RealizableEntity), OntUtil.EQUIV);
		OntUtil.classify(TemporalRegion, 
				schema.createList().with(ConnectedTemporalRegion).with(ScatteredTemporalRegion), 
				OntUtil.EQUIV);
		
		OntUtil.makeDisjointSubClasses(RealizableEntity, 
				SetUtil.chain(Disposition).plus(Function).plus(Role));
		
	}
	
	private static void setLabel(OntResource resource, String comment) {
		schema.add(resource, RDFS.label, schema.createTypedLiteral(comment));
	}

	static {
		setLabel(ConnectedSpatiotemporalRegion, "connected_spatiotemporal_region");
		setLabel(ConnectedTemporalRegion, "connected_temporal_region");
		setLabel(Continuant, "continuant");
		setLabel(DependentContinuant, "dependent_continuant");
		setLabel(Disposition, "disposition");
		setLabel(Entity, "entity");
		setLabel(FiatObjectPart, "fiat_object_part");
		setLabel(FiatProcessPart, "fiat_process_part");
		setLabel(Function, "function");
		setLabel(GenericallyDependentContinuant, "generically_dependent_continuant");
		setLabel(IndependentContinuant, "independent_continuant");
		setLabel(Object, "object");
		setLabel(ObjectAggregate, "object_aggregate");
		setLabel(ObjectBoundary, "object_boundary");
		setLabel(Occurrent, "occurrent");
		setLabel(OneDimensionalRegion, "one_dimensional_region");
		setLabel(Process, "process");
		setLabel(ProcessAggregate, "process_aggregate");
		setLabel(ProcessBoundary, "process_boundary");
		setLabel(ProcessualContext, "processual_context");
		setLabel(ProcessualEntity, "processual_entity");
		setLabel(Quality, "quality");
		setLabel(RealizableEntity, "realizable_entity");
		setLabel(Role, "role");
		setLabel(ScatteredSpatiotemporalRegion, "scattered_spatiotemporal_region");
		setLabel(ScatteredTemporalRegion, "scattered_temporal_region");
		setLabel(Site, "site");
		setLabel(SpatialRegion, "spatial_region");
		setLabel(SpatiotemporalInstant, "spatiotemporal_instant");
		setLabel(SpatiotemporalInterval, "spatiotemporal_interval");
		setLabel(SpatiotemporalRegion, "spatiotemporal_region");
		setLabel(SpecificallyDependentContinuant, "specifically_dependent_continuant");
		setLabel(TemporalInterval, "temporal_interval");
		setLabel(TemporalInstant, "temporal_instant");
		setLabel(TemporalRegion, "temporal_region");
		setLabel(ThreeDimensionalRegion, "three_dimensional_region");
		setLabel(TwoDimensionalRegion, "two_dimensional_region");
		setLabel(ZeroDimensionalRegion, "zero_dimensional_region");
	}
	
	private static void addTypedComment(OntResource resource, String comment) {
		schema.add(resource, RDFS.comment, schema.createTypedLiteral(comment));
	}

	static {

		//addTypedComment(null, "");
		
		addTypedComment(ConnectedSpatiotemporalRegion, 
				"Examples: the spatial and temporal location of an individual organism's life, " + 
				"the spatial and temporal location of the development of a fetus");
		
		addTypedComment(ConnectedSpatiotemporalRegion, 
				"Definition: A space time region [span:SpaceTimeRegion] that has temporal and " + 
				"spatial dimensions such that all points within the spatiotemporal region are " + 
				"mediately or immediately connected to all other points within the same " + 
				"space time region [span:SpaceTimeRegion].");
		
		addTypedComment(ConnectedTemporalRegion, 
				"Examples: the 1970s years, the time from the beginning to the end of a heart attack, " + 
				"the time taken up by cellular meiosis");
		
		addTypedComment(ConnectedTemporalRegion, 
				"Definition: A temporal region [span:TemporalRegion] every point of which is " + 
				"mediately or immediately connected with every other point of which.");		
		addTypedComment(Continuant, "Synonyms: endurant");
		
		addTypedComment(Continuant, 
				"Examples: a heart, a person, the color of a tomato, the mass of a cloud, " + 
				"a symphony orchestra, the disposition of blood to coagulate, the lawn and " + 
				"atmosphere in front of our building");
		
		addTypedComment(Continuant, 
				"Definition: An entity [bfo:Entity] that exists in full at any time in which it " + 
				"exists at all, persists through time while maintaining its identity and has no " + 
				"temporal parts.");
		
		addTypedComment(DependentContinuant, 
				"Definition: A continuant [snap:Continuant] that is either dependent on one or " + 
				"other independent continuant [snap:IndependentContinuant] bearers or inheres in " + 
				"or is borne by other entities.");
		
		addTypedComment(Disposition, 
				"Definition: A realizable entity [snap:RealizableEntity] " + 
				"that essentially causes a specific process or transformation in the object " + 
				"[snap:Object] in which it inheres, under specific circumstances and in conjunction " + 
				"with the laws of nature. A general formula for dispositions is: X (object " + 
				"[snap:Object] has the disposition D to (transform, initiate a process) R under " + 
				"conditions C.");
		
		addTypedComment(Disposition, 
				"Examples: the disposition of vegetables to decay when not refrigerated, " + 
				"the disposition of a vase to brake if dropped, the disposition of blood " + 
				"to coagulate, the disposition of a patient with a weakened immune system " + 
				"to contract disease, the disposition of metal to conduct electricity.");
		
		addTypedComment(FiatObjectPart, "Synonyms: fiat substance part");
		
		addTypedComment(FiatObjectPart, 
				"Examples: upper and lower lobes of the left lung, the dorsal and ventral surfaces " + 
				"of the body, the east side of Saarbruecken, the lower right portion of a human torso");
		
		addTypedComment(FiatObjectPart, 
				"Definition: An independent continuant [snap:IndependentContinuant] that is part " + 
				"of an object [snap:Object] but is not demarcated by any physical discontinuities.");
		
		addTypedComment(FiatProcessPart, 
				"Examples: chewing during a meal, the middle part of a rainstorm, the worst part " + 
				"of a heart-attack, the most interesting part of Van Gogh's life");
		
		addTypedComment(FiatProcessPart, 
				"Definition: A processual entity [span:ProcessualEntity] that is part of a process " + 
				"but that does not have bona fide beginnings and endings corresponding to " + 
				"real discontinuities.");
		
		addTypedComment(Function, 
				"Definition: A realizable entity [snap:RealizableEntity] the manifestation of which " + 
				"is an essentially end-directed activity of a continuant [snap:Continuant] entity " + 
				"in virtue of that continuant [snap:Continuant] entity being a specific kind of " + 
				"entity in the kind or kinds of contexts that it is made for.");
		
		addTypedComment(Function, 
				"Examples: the function of a birth canal to enable transport, the function of the " + 
				"heart in the body: to pump blood, to receive de-oxygenated and oxygenated blood, " + 
				"etc., the function of reproduction in the transmission of genetic material, " + 
				"the digestive function of the stomach to nutriate the body, the function of " + 
				"a hammer to drive in nails, the function of a computer program to compute " + 
				"mathematical equations, the function of an automobile to provide transportation, " + 
				"the function of a judge in a court of law"); 
		
		addTypedComment(GenericallyDependentContinuant, 
				"Examples: a certain PDF file that exists in different and in several hard drives");
		
		addTypedComment(GenericallyDependentContinuant, 
				"Definition: A continuant [snap:Continuant] that is dependent on one or other " + 
				"independent continuant [snap:IndependentContinuant] bearers. For every instance " + 
				"of A requires some instance of (an independent continuant " + 
				"[snap:IndependentContinuant] type) B but which instance of B serves can change " + 
				"from time to time.");
		
		addTypedComment(IndependentContinuant, 
				"Definition: A continuant [snap:Continuant] that is a bearer of " + 
				"quality [snap:Quality] and realizable entity [snap:RealizableEntity] entities, " + 
				"in which other entities inhere and which itself cannot inhere in anything.");
		
		addTypedComment(IndependentContinuant, 
				"Examples: an organism, a heart, a leg, a person, a symphony orchestra, a chair, " + 
				"the bottom right portion of a human torso, the lawn and atmosphere in front of " + 
				"our building");
		
		addTypedComment(IndependentContinuant, "Synonyms: substantial entity");
		
		addTypedComment(Object, "Examples: an organism, a heart, a chair, a lung, an apple");
		
		addTypedComment(Object, 
				"Definition: An independent continuant [snap:IndependentContinuant] that is " + 
				"spatially extended, maximally self-connected and self-contained (the parts of " + 
				"a substance are not separated from each other by spatial gaps) and possesses " + 
				"an internal unity. The identity of substantial object [snap:Object] entities " + 
				"is independent of that of other entities and can be maintained through time.");
		
		addTypedComment(Object, "Synonyms: substance");
		
		addTypedComment(ObjectAggregate, 
				"Examples: a heap of stones, a group of commuters on the subway, a collection of " + 
				"random bacteria, a flock of geese, the patients in a hospital");

		addTypedComment(ObjectAggregate, 
				"Definition: An independent continuant [snap:IndependentContinuant] that is a " + 
				"mereological sum of separate object [snap:Object] entities and possesses " + 
				"non-connected boundaries."); 

		addTypedComment(ObjectAggregate, "Synonyms: substance aggregate"); 
		
		addTypedComment(ObjectBoundary, 
				"Definition: An independent continuant [snap:IndependentContinuant] that is a " + 
				"lower dimensional part of a spatial entity, normally a closed two-dimensional " + 
				"surface. Boundaries are those privileged parts of object [snap:Object] entities " + 
				"that exist at exactly the point where the object [snap:Object] is separated off " + 
				"from the rest of the existing entities in the world.");
		
		addTypedComment(ObjectBoundary, 
				"Examples: the surface of the skin, the surface of the earth, the surface of " + 
				"the interior of the stomach, the outer surface of a cell or cell wall");
		
		addTypedComment(ObjectBoundary, 
				"Comment: Boundaries are theoretically difficult entities to account for, " + 
				"however the intuitive notion of a physical boundary as a surface of some sort " + 
				"(whether inside or outside of a thing) will generally serve as a good guide for " + 
				"the use of this universal.");
		
		addTypedComment(ObjectBoundary, "Synonyms: substance boundary");
		
		addTypedComment(Occurrent, "Synonyms: perdurant");
		
		addTypedComment(Occurrent, 
				"Examples: the life of an organism, a surgical operation as processual context for " + 
				"a nosocomical infection, the spatiotemporal context occupied by a process of " + 
				"cellular meiosis, the most interesting part of Van Gogh's life, the " + 
				"spatiotemporal region occupied by the development of a cancer tumor");
		
		addTypedComment(Occurrent, 
				"Definition: An entity [bfo:Entity] that has temporal parts and that happens, " + 
				"unfolds or develops through time. Sometimes also called perdurants.");
		
		addTypedComment(OneDimensionalRegion, 
				"Examples: the part of space that is a line stretching from one end of absolute " + 
				"space to the other, an edge of a cube-shaped part of space");
		
		addTypedComment(OneDimensionalRegion, 
				"Definition: A spatial region [snap:SpatialRegion] with one dimension.");
		
		addTypedComment(Process, 
				"Definition: A processual entity [span:ProcessualEntity] that is a maximally " + 
				"connected spatiotemporal whole and has bona fide beginnings and endings " + 
				"corresponding to real discontinuities.");
		
		addTypedComment(Process, "Examples: the life of an organism, the process of sleeping, " + 
				"the process of cell-division");
		
		addTypedComment(ProcessAggregate, 
				"Examples: the beating of the hearts of each of seven individuals in the room, " + 
				"the playing of each of the members of an orchestra, a process of digestion and " + 
				"a process of thinking taken together");
		
		addTypedComment(ProcessAggregate, 
				"Definition: A processual entity [span:ProcessualEntity] that is a mereological " + 
				"sum of process [span:Process] entities and possesses non-connected boundaries.");
		
		addTypedComment(ProcessBoundary, 
				"Examples: birth, death, the forming of a synapse, the onset of REM sleep, " + 
				"the detaching of a finger in an industrial accident, the final separation of " + 
				"two cells at the end of cell-division, the incision at the beginning of a surgery");
		
		addTypedComment(ProcessBoundary, 
				"Definition: A processual entity [span:ProcessualEntity] that is the fiat or " + 
				"bona fide instantaneous temporal process boundary.");
		
		addTypedComment(ProcessualContext, 
				"Comment: An instance of a processual context [span:ProcessualContext] is a " + 
				"mixture of processual entity [span:ProcessualEntity] which stand as surrounding " + 
				"environments for other processual entity [span:ProcessualEntity] entities. " + 
				"The class processual context [span:ProcessualContext] is the analogous among " + 
				"occurrent [span:Occurrent] entities to the class site [snap:Site] among " + 
				"continuant [snap:Continuant] entities.");
		
		addTypedComment(ProcessualContext, 
				"Definition: An occurrent [span:Occurrent] consisting of a characteristic spatial " + 
				"shape inhering in some arrangement of other occurrent [span:Occurrent] entities. " + 
				"processual context [span:ProcessualContext] entities are characteristically " + 
				"entities at or in which other occurrent [span:Occurrent] entities can be located " + 
				"or occur.");
		
		addTypedComment(ProcessualContext, 
				"Examples: The processual context for a given manipulation occurring as part of an " + 
				"experiment is made of processual entities which occur in parallel, are not " + 
				"necessarily all parts of the experiment themselves and may involve continuant " + 
				"[snap:Continuant] entities which are in the spatial vicinity of the participants " + 
				"in the experiment.");
		
		addTypedComment(ProcessualEntity, 
				"Definition: An occurrent [span:Occurrent] that exists in time by occurring or " + 
				"happening, has temporal parts and always involves and depends on some entity.");
		
		addTypedComment(ProcessualEntity, 
				"Examples: the life of an organism, the process of meiosis, the course of a disease, " + 
				"the flight of a bird");
		
		addTypedComment(Quality, "Definition: A specifically dependent continuant " + 
				"[snap:SpecificallyDependentContinuant] that is exhibited if it inheres in an " + 
				"entity or entities at all (a categorical property).");
		
		addTypedComment(Quality, "Examples: the color of a tomato, the ambient temperature of air, " + 
				"the circumference of a waist, the shape of a nose, the mass of a piece of gold, " + 
				"the weight of a chimpanzee");
		
		addTypedComment(RealizableEntity, 
				"Definition: A specifically dependent continuant " + 
				"[snap:SpecificallyDependentContinuant] that inheres in continuant " + 
				"[snap:Continuant] entities and are not exhibited in full at every time in which " + 
				"it inheres in an entity or group of entities. The exhibition or actualization of " + 
				"a realizable entity is a particular manifestation, functioning or process that " + 
				"occurs under certain circumstances.");
		
		addTypedComment(RealizableEntity, 
				"Comment: If a realizable entity [snap:RealizableEntity] inheres in a continuant " + 
				"[snap:Continuant], this does not imply that it is actually realized.");
		
		addTypedComment(RealizableEntity, 
				"Examples: the role of being a doctor, the function of the reproductive organs, " + 
				"the disposition of blood to coagulate, the disposition of metal to conduct electricity");
		
		addTypedComment(Role, "Definition: A realizable entity [snap:RealizableEntity] the " + 
				"manifestation of which brings about some result or end that is not essential to a " + 
				"continuant [snap:Continuant] in virtue of the kind of thing that it is but that can " + 
				"be served or participated in by that kind of continuant [snap:Continuant] in some " + 
				"kinds of natural, social or institutional contexts.");
		
		addTypedComment(Role, "Examples: the role of a person as a surgeon, the role of a chemical " + 
				"compound in an experiment, the role of a patient relative as defined by a hospital " + 
				"administrative form, the role of a woman as a legal mother in the context of " + 
				"system of laws, the role of a biological grandfather as legal guardian in the " + 
				"context of a system of laws, the role of ingested matter in digestion, the role of " + 
				"a student in a university");
		
		addTypedComment(ScatteredSpatiotemporalRegion, 
				"Definition: A space time region [span:SpaceTimeRegion] that has spatial and " + 
				"temporal dimensions and every spatial and temporal point of which is not " + 
				"connected with every other spatial and temporal point of which.");
		
		addTypedComment(ScatteredSpatiotemporalRegion, 
				"Examples: the space and time occupied by the individual games of the World Cup, " + 
				"the space and time occupied by the individual liaisons in a romantic affair");
		
		addTypedComment(ScatteredTemporalRegion, 
				"Examples: the time occupied by the individual games of the World Cup, " + 
				"the time occupied by the individual liaisons in a romantic affair");
		
		addTypedComment(ScatteredTemporalRegion, 
				"Definition: A temporal region [span:TemporalRegion] every point of which is " + 
				"not mediately or immediately connected with every other point of which.");
		
		addTypedComment(Site, 
				"Comment: An instance of Site [snap:Site] is a mixture of " + 
				"independent continuant [snap:IndependentContinuant] entities which act as " + 
				"surrounding environments for other " + 
				"independent continuant [snap:IndependentContinuant] entities, most importantly " + 
				"for instances of object [snap:Object]. A site [snap:Site] is typically made of " + 
				"object [snap:Object] or fiat object part [snap:FiatObjectPart] entities and a " + 
				"surrounding medium in which is found an object [snap:Object] occupying the " + 
				"site [snap:Site]. Independent continuant [snap:IndependentContinuant] entities " + 
				"may be associated with others (which, then, are sites) through a relation " + 
				"of \"occupation\". That relation is connected to, but distinct from, the relation " + 
				"of spatial location. Site [snap:Site] entities are not to be confused with " + 
				"spatial region [snap:SpatialRegion] entities. In BFO, site [snap:Site] allows for " + 
				"a so-called relational view of space which is different from the view corresponding " + 
				"to the class spatial region [snap:SpatialRegion] (see the comment on this class).");
		
		addTypedComment(Site, 
				"Examples: a particular room in a particular hospital, Maria's nostril or her " + 
				"intestines for a variety of bacteria.");
		
		addTypedComment(Site, 
				"Definition: An independent continuant [snap:IndependentContinuant] consisting of " + 
				"a characteristic spatial shape in relation to some arrangement of other " + 
				"continuant [snap:Continuant] entities and of the medium which is enclosed in whole " + 
				"or in part by this characteristic spatial shape. Site [snap:Site] entities are " + 
				"entities that can be occupied by other continuant [snap:Continuant] entities.");
		
		addTypedComment(SpatiotemporalInstant, 
				"Definition: A connected space time region [span:ConnectedSpaceTimeRegion] at a " + 
				"specific moment.");
		
		addTypedComment(SpatiotemporalInstant, 
				"Examples: the space time region occupied by a single instantaneous temporal " + 
				"slice (part) of a process");
		
		addTypedComment(SpatiotemporalInterval, 
				"Examples: the space time region occupied by a process or by a fiat processual part");
		
		addTypedComment(SpatiotemporalInterval, 
				"Definition: A connected space time region [span:ConnectedSpaceTimeRegion] that " + 
				"endures for more than a single moment of time.");
		
		addTypedComment(SpatialRegion, 
				"Comment: Space and spatial region [snap:SpatialRegion] entities are entities in " + 
				"their own rights which exist independently of any entities which can be located " + 
				"at them. This view of space is sometimes " + 
				"called \"absolutist\" or \"the container view\". In BFO, the class " + 
				"site [snap:Site] allows for a so-called relational view of space, that is to say, " + 
				"a view according to which spatiality is a matter of relative location between " + 
				"entities and not a matter of being tied to space. The bridge between these two " + 
				"views is secured through the fact that while instances of site [snap:Site] are " + 
				"not spatial region [snap:SpatialRegion] entities, they are nevertheless " + 
				"spatial entities.");
		
		addTypedComment(SpatialRegion, 
				"Definition: A continuant [snap:Continuant] that is neither bearer of " + 
				"quality [snap:Quality] entities nor inheres in any other entities.");
		
		addTypedComment(SpatialRegion, 
				"Comment: All instances of continuant [snap:Continuant] are spatial entities, " + 
				"that is, they enter in the relation of (spatial) location with spatial " + 
				"region [snap:SpatialRegion] entities. As a particular case, the exact " + 
				"spatial location of a spatial region [snap:SpatialRegion] is this region itself.");
		
		addTypedComment(SpatialRegion, 
				"Examples: the sum total of all space in the universe, parts of the sum total " + 
				"of all space in the universe");
		
		addTypedComment(SpatialRegion, 
				"Comment: An instance of spatial region [snap:SpatialRegion] is a part of space. " + 
				"All parts of space are spatial region [snap:SpatialRegion] entities and " + 
				"only spatial region [snap:SpatialRegion] entities are parts of space. " + 
				"Space is the entire extent of the spatial universe, a designated individual, " + 
				"which is thus itself a spatial region [snap:SpatialRegion].");
		
		addTypedComment(SpatiotemporalRegion, 
				"Comment: All instances of occurrent [span:Occurrent] are spatiotemporal entities, " + 
				"that is, they enter in the relation of (spatiotemporal) location with " + 
				"spatiotemporal region [span:SpatiotemporalRegion] entities. As a particular case, " + 
				"the exact spatiotemporal location of a spatiotemporal region " + 
				"[span:SpatiotemporalRegion] is this region itself.");
		
		addTypedComment(SpatiotemporalRegion, 
				"Definition: An occurrent [span:Occurrent] at or in which processual entity " + 
				"[span:ProcessualEntity] entities can be located.");
		
		addTypedComment(SpatiotemporalRegion, 
				"Comment: Spacetime and spatiotemporal region [span:SpatiotemporalRegion] entities " + 
				"are entities in their own rights which exist independently of any entities which " + 
				"can be located at them. This view of spacetime can be " + 
				"called \"absolutist\" or \"the container view\". In BFO, the class " + 
				"processual context [span:ProcessualContext] allows for a so-called relational " + 
				"view of spacetime, that is to say, a view according to which spatiotemporality is " + 
				"a matter of relative location between entities and not a matter of being tied " + 
				"to spacetime. In BFO, the bridge between these two views is secured through the " + 
				"fact that instances of processual context [span:ProcessualContext] are " + 
				"too spatiotemporal entities.");
		
		addTypedComment(SpatiotemporalRegion, 
				"Comment: An instance of the spatiotemporal region [span:SpatiotemporalRegion] " + 
				"is a part of spacetime. All parts of spacetime are spatiotemporal region " + 
				"[span:SpatiotemporalRegion] entities and only spatiotemporal region " + 
				"[span:SpatiotemporalRegion] entities are parts of spacetime. In particular, " + 
				"neither spatial region [snap:SpatialRegion] entities nor temporal region " + 
				"[span:TemporalRegion] entities are in BFO parts of spacetime. Spacetime is the " + 
				"entire extent of the spatiotemporal universe, a designated individual, which is " + 
				"thus itself a spatiotemporal region [span:SpatiotemporalRegion]. " + 
				"Spacetime is among occurrents the analogous of space among continuant " + 
				"[snap:Continuant] entities.");
		
		addTypedComment(SpatiotemporalRegion, 
				"Examples: the spatiotemporal region occupied by a human life, " + 
				"the spatiotemporal region occupied by the development of a cancer tumor, " + 
				"the spatiotemporal context occupied by a process of cellular meiosis");
		
		addTypedComment(SpecificallyDependentContinuant, "Synonyms: property, trope, mode");
		
		addTypedComment(SpecificallyDependentContinuant, 
				"Definition: A continuant [snap:Continuant] that inheres in or is borne by " + 
				"other entities. Every instance of A requires some specific instance of B which " + 
				"must always be the same.");
		
		addTypedComment(SpecificallyDependentContinuant, 
				"Examples: the mass of a cloud, the smell of mozzarella, the liquidity of blood, " + 
				"the color of a tomato, the disposition of fish to decay, the role of being a doctor, " + 
				"the function of the heart in the body: to pump blood, to receive de-oxygenated " + 
				"and oxygenated blood, etc.");
		
		addTypedComment(TemporalInterval, 
				"Examples: any continuous temporal duration during which a process occurs");
		
		addTypedComment(TemporalInterval, 
				"Definition: A connected temporal region [span:ConnectedTemporalRegion] lasting " + 
				"for more than a single moment of time.");
		
		addTypedComment(TemporalInstant, 
				"Examples: right now, the moment at which a finger is detached in an industrial " + 
				"accident, the moment at which a child is born, the moment of death");
		
		addTypedComment(TemporalInstant, 
				"Definition: A connected temporal region [span:ConnectedTemporalRegion] " + 
				"comprising a single moment of time.");
		
		addTypedComment(TemporalRegion, 
				"Comment: An instance of temporal region [span:TemporalRegion] is a part of time. " + 
				"All parts of time are temporal region [span:TemporalRegion] entities and only " + 
				"temporal region [span:TemporalRegion] entities are parts of time. Time is the " + 
				"entire extent of the temporal universe, a designated individual, which is thus a " + 
				"temporal region itself.");
		
		addTypedComment(TemporalRegion, 
				"Examples: the time it takes to run a marathon, the duration of a surgical procedure, " + 
				"the moment of death");
		
		addTypedComment(TemporalRegion, 
				"Definition: An occurrent [span:Occurrent] that is part of time.");
		
		addTypedComment(TemporalRegion, 
				"Comment: All instances of occurrent [span:Occurrent] are temporal entities, " + 
				"that is, they enter in the relation of (temporal) location with temporal " + 
				"region [span:TemporalRegion] entities. As a particular case, the exact " + 
				"spatiotemporal location of a temporal region [span:TemporalRegion] is this " + 
				"region itself. Continuant [snap:Continuant] entities are not temporal entities in " + 
				"the technical sense just explained; they are related to time in a different way, " + 
				"not through temporal location but through a relation of existence at a time or " + 
				"during a period of time (see continuant [snap:Continuant].");
		
		addTypedComment(TemporalRegion, 
				"Comment: Time and temporal region [span:TemporalRegion] entities are entities in " + 
				"their own rights which exist independently of any entities which can be located at " + 
				"them. This view of time can be called \"absolutist\" or \"the container view\" in " + 
				"analogy to what is traditionally the case with space (see spatial region " + 
				"[snap:SpatialRegion].");
		
		addTypedComment(ThreeDimensionalRegion, 
				"Definition: A spatial region [snap:SpatialRegion] with three dimensions.");
		
		addTypedComment(ThreeDimensionalRegion, 
				"Examples: a cube-shaped part of space, a sphere-shaped part of space");
		
		addTypedComment(TwoDimensionalRegion, 
				"Definition: A spatial region [snap:SpatialRegion] with two dimensions.");

		addTypedComment(TwoDimensionalRegion, 
		"Examples: the surface of a cube-shaped part of space, the surface of a " + 
		"sphere-shaped part of space, the surface of a rectilinear planar figure-shaped " + 
		"part of space");

		addTypedComment(ZeroDimensionalRegion, "Examples: a point");
		
		addTypedComment(ZeroDimensionalRegion, 
				"Definition: A spatial region [snap:SpatialRegion] with no dimensions.");
	}

}
