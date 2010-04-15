package org.vcell.sybil.rdf.reason.rules;

import java.util.List;

import org.vcell.sybil.util.lists.ListUtil;

import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class OWLRules {

	public static Rule axiom = 
		Rule.parseRule("[ -> (rdf:type rdfs:range rdfs:Class) ]");
	public static Rule axiom2 = 
		Rule.parseRule("[ -> (rdfs:Resource rdf:type rdfs:Class) ]");
	public static Rule axiom3 = 
		Rule.parseRule("[ -> (rdfs:Literal rdf:type rdfs:Class) ]");
	public static Rule axiom4 = 
		Rule.parseRule("[ -> (rdf:Statement rdf:type rdfs:Class) ]");
	public static Rule axiom5 = 
		Rule.parseRule("[ -> (rdf:nil rdf:type rdf:List) ]");
	public static Rule axiom6 = 
		Rule.parseRule("[ -> (rdf:subject rdf:type rdf:Property) ]");
	public static Rule axiom7 = 
		Rule.parseRule("[ -> (rdf:object rdf:type rdf:Property) ]");
	public static Rule axiom8 = 
		Rule.parseRule("[ -> (rdf:predicate rdf:type rdf:Property) ]");
	public static Rule axiom9 = 
		Rule.parseRule("[ -> (rdf:first rdf:type rdf:Property) ]");
	public static Rule axiom10 = 
		Rule.parseRule("[ -> (rdf:rest rdf:type rdf:Property) ]");
	public static Rule axiom11 = 
		Rule.parseRule("[ -> (rdfs:subPropertyOf rdfs:domain rdf:Property) ]");
	public static Rule axiom12 = 
		Rule.parseRule("[ -> (rdfs:subClassOf rdfs:domain rdfs:Class) ]");
	public static Rule axiom13 = 
		Rule.parseRule("[ -> (rdfs:domain rdfs:domain rdf:Property) ]");
	public static Rule axiom14 = 
		Rule.parseRule("[ -> (rdfs:range rdfs:domain rdf:Property) ]");
	public static Rule axiom15 = 
		Rule.parseRule("[ -> (rdf:subject rdfs:domain rdf:Statement) ]");
	public static Rule axiom16 = 
		Rule.parseRule("[ -> (rdf:predicate rdfs:domain rdf:Statement) ]");
	public static Rule axiom17 = 
		Rule.parseRule("[ -> (rdf:object rdfs:domain rdf:Statement) ]");
	public static Rule axiom18 = 
		Rule.parseRule("[ -> (rdf:first rdfs:domain rdf:List) ]");
	public static Rule axiom19 = 
		Rule.parseRule("[ -> (rdf:rest rdfs:domain rdf:List) ]");
	public static Rule axiom20 = 
		Rule.parseRule("[ -> (rdfs:subPropertyOf rdfs:range rdf:Property) ]");
	public static Rule axiom21 = 
		Rule.parseRule("[ -> (rdfs:subClassOf rdfs:range rdfs:Class) ]");
	public static Rule axiom22 = 
		Rule.parseRule("[ -> (rdfs:domain rdfs:range rdfs:Class) ]");
	public static Rule axiom23 = 
		Rule.parseRule("[ -> (rdfs:range rdfs:range rdfs:Class) ]");
	public static Rule axiom24 = 
		Rule.parseRule("[ -> (rdfs:comment rdfs:range rdfs:Literal) ]");
	public static Rule axiom25 = 
		Rule.parseRule("[ -> (rdfs:label rdfs:range rdfs:Literal) ]");
	public static Rule axiom26 = 
		Rule.parseRule("[ -> (rdf:rest rdfs:range rdf:List) ]");
	public static Rule axiom27 = 
		Rule.parseRule("[ -> (rdf:Alt rdfs:subClassOf rdfs:Container) ]");
	public static Rule axiom28 = 
		Rule.parseRule("[ -> (rdf:Bag rdfs:subClassOf rdfs:Container) ]");
	public static Rule axiom29 = 
		Rule.parseRule("[ -> (rdf:Seq rdfs:subClassOf rdfs:Container) ]");
	public static Rule axiom30 = 
		Rule.parseRule("[ -> (rdfs:ContainerMembershipProperty rdfs:subClassOf rdf:Property) ]");
	public static Rule axiom31 = 
		Rule.parseRule("[ -> (rdfs:isDefinedBy rdfs:subPropertyOf rdfs:seeAlso) ]");
	public static Rule axiom32 = 
		Rule.parseRule("[ -> (rdf:XMLLiteral rdf:type rdfs:Datatype) ]");
	public static Rule axiom33 = 
		Rule.parseRule("[ -> (rdfs:Datatype rdfs:subClassOf rdfs:Class) ]");
	public static Rule rdf1and4 = 
		Rule.parseRule("[ rdf1and4: (?x ?p ?y) -> (?p rdf:type rdf:Property) ]");
	public static Rule rdfs7b = 
		Rule.parseRule("[ rdfs7b: (?a rdf:type rdfs:Class) -> (?a rdfs:subClassOf rdfs:Resource) ]");
	public static Rule rdfs2 = 
		Rule.parseRule("[ rdfs2: (?p rdfs:domain ?c) (?x ?p ?y) -> (?x rdf:type ?c) ]");
	public static Rule rdfs3 = 
		Rule.parseRule("[ rdfs3: (?p rdfs:range ?c) (?x ?p ?y) -> (?y rdf:type ?c) ]");
	public static Rule rdfs5b = 
		Rule.parseRule("[ rdfs5b: (?a rdf:type rdf:Property) -> (?a rdfs:subPropertyOf ?a) ]");
	public static Rule rdfs6 = 
		Rule.parseRule("[ rdfs6: (?p rdfs:subPropertyOf ?q) notEqual(?p ?q) (?a ?p ?b) -> (?a ?q ?b) ]");
	public static Rule rdfs7 = 
		Rule.parseRule("[ rdfs7: (?a rdf:type rdfs:Class) -> (?a rdfs:subClassOf ?a) ]");
	public static Rule rdfs10 = 
		Rule.parseRule("[ rdfs10: (?x rdf:type rdfs:ContainerMembershipProperty) " + 
				"-> (?x rdfs:subPropertyOf rdfs:member) ]");
	public static Rule rdfs2_partial = 
		Rule.parseRule("[ rdfs2-partial: (?p rdfs:domain ?c) -> (?c rdf:type rdfs:Class) ]");
	public static Rule rdfs3_partial = 
		Rule.parseRule("[ rdfs3-partial: (?p rdfs:range ?c) -> (?c rdf:type rdfs:Class) ]");
	public static Rule rdfs9_alt = 
		Rule.parseRule("[ rdfs9-alt: (?x rdfs:subClassOf ?y) (?a rdf:type ?x) -> (?a rdf:type ?y) ]");
	public static Rule rdfs9_alt2 = 
		Rule.parseRule("[ rdfs9-alt: (?a rdf:type ?x) (?x rdfs:subClassOf ?y) -> (?a rdf:type ?y) ]");
	public static Rule rdfs2a = 
		Rule.parseRule("[ rdfs2a: (?x rdfs:domain ?y) (?y rdfs:subClassOf ?z) -> (?x rdfs:domain ?z) ]");
	public static Rule rdfs2a2 = 
		Rule.parseRule("[ rdfs2a: (?y rdfs:subClassOf ?z) (?x rdfs:domain ?y) -> (?x rdfs:domain ?z) ]");
	public static Rule rdfs3a = 
		Rule.parseRule("[ rdfs3a: (?x rdfs:range ?y) (?y rdfs:subClassOf ?z) -> (?x rdfs:range ?z) ]");
	public static Rule rdfs3a2 = 
		Rule.parseRule("[ rdfs3a: (?y rdfs:subClassOf ?z) (?x rdfs:range ?y) -> (?x rdfs:range ?z) ]");
	public static Rule rdfs12a = 
		Rule.parseRule("[ rdfs12a: (rdf:type rdfs:subPropertyOf ?z) (?z rdfs:domain ?y) " + 
				"-> (rdfs:Resource rdfs:subClassOf ?y) ]");
	public static Rule rdfs12a2 = 
		Rule.parseRule("[ rdfs12a: (rdfs:subClassOf rdfs:subPropertyOf ?z) (?z rdfs:domain ?y) " + 
				"-> (rdfs:Class rdfs:subClassOf ?y) ]");
	public static Rule rdfs12a3 = 
		Rule.parseRule("[ rdfs12a: (rdfs:subPropertyOf rdfs:subPropertyOf ?z) (?z rdfs:domain ?y) " + 
				"-> (rdf:Property rdfs:subClassOf ?y) ]");
	public static Rule rdfs12b = 
		Rule.parseRule("[ rdfs12b: (rdfs:subClassOf rdfs:subPropertyOf ?z) (?z rdfs:range ?y) " + 
				"-> (rdfs:Class rdfs:subClassOf ?y) ]");
	public static Rule rdfs12b2 = 
		Rule.parseRule("[ rdfs12b: (rdfs:subPropertyOf rdfs:subPropertyOf ?z) (?z rdfs:range ?y) " + 
				"-> (rdf:Property rdfs:subClassOf ?y) ]");
	public static Rule rdfsder1 = 
		Rule.parseRule("[ rdfsder1: (?p rdfs:subPropertyOf ?q) notEqual(?p ?q) (?q rdfs:range ?z) " + 
				"-> (?p rdfs:range ?z) ]");
	public static Rule rdfsder2 = 
		Rule.parseRule("[ rdfsder2: (?p rdfs:subPropertyOf ?q) notEqual(?p ?q) (?q rdfs:domain ?z) " + 
				"-> (?p rdfs:domain ?z) ]");
	public static Rule axiom34 = 
		Rule.parseRule("[ -> (owl:FunctionalProperty rdfs:subClassOf rdf:Property) ]");
	public static Rule axiom35 = 
		Rule.parseRule("[ -> (owl:ObjectProperty rdfs:subClassOf rdf:Property) ]");
	public static Rule axiom36 = 
		Rule.parseRule("[ -> (owl:DatatypeProperty rdfs:subClassOf rdf:Property) ]");
	public static Rule axiom37 = 
		Rule.parseRule("[ -> (owl:InverseFunctionalProperty rdfs:subClassOf owl:ObjectProperty) ]");
	public static Rule axiom38 = 
		Rule.parseRule("[ -> (owl:TransitiveProperty rdfs:subClassOf owl:ObjectProperty) ]");
	public static Rule axiom39 = 
		Rule.parseRule("[ -> (owl:SymmetricProperty rdfs:subClassOf owl:ObjectProperty) ]");
	public static Rule axiom40 = 
		Rule.parseRule("[ -> (rdf:first rdf:type owl:FunctionalProperty) ]");
	public static Rule axiom41 = 
		Rule.parseRule("[ -> (rdf:rest rdf:type owl:FunctionalProperty) ]");
	public static Rule axiom42 = 
		Rule.parseRule("[ -> (owl:oneOf rdfs:domain owl:Class) ]");
	public static Rule axiomOWLClassIsRDFSClass = 
		Rule.parseRule("[ -> (owl:Class rdfs:subClassOf rdfs:Class) ]");
	public static Rule axiom44 = 
		Rule.parseRule("[ -> (owl:Restriction rdfs:subClassOf owl:Class) ]");
	public static Rule axiom45 = 
		Rule.parseRule("[ -> (owl:Thing rdf:type owl:Class) ]");
	public static Rule axiom46 = 
		Rule.parseRule("[ -> (owl:Nothing rdf:type owl:Class) ]");
	public static Rule axiom47 = 
		Rule.parseRule("[ -> (owl:equivalentClass rdfs:domain owl:Class) ]");
	public static Rule axiom48 = 
		Rule.parseRule("[ -> (owl:equivalentClass rdfs:range owl:Class) ]");
	public static Rule axiom49 = 
		Rule.parseRule("[ -> (owl:disjointWith rdfs:domain owl:Class) ]");
	public static Rule axiom50 = 
		Rule.parseRule("[ -> (owl:disjointWith rdfs:range owl:Class) ]");
	public static Rule axiom51 = 
		Rule.parseRule("[ -> (owl:sameAs rdf:type owl:SymmetricProperty) ]");
	public static Rule axiom52 = 
		Rule.parseRule("[ -> (owl:onProperty rdfs:domain owl:Restriction) ]");
	public static Rule axiom53 = 
		Rule.parseRule("[ -> (owl:onProperty rdfs:range owl:Property) ]");
	public static Rule axiom54 = 
		Rule.parseRule("[ -> (owl:OntologyProperty rdfs:subClassOf rdf:Property) ]");
	public static Rule axiom55 = 
		Rule.parseRule("[ -> (owl:imports rdf:type owl:OntologyProperty) ]");
	public static Rule axiom56 = 
		Rule.parseRule("[ -> (owl:imports rdfs:domain owl:Ontology) ]");
	public static Rule axiom57 = 
		Rule.parseRule("[ -> (owl:imports rdfs:range owl:Ontology) ]");
	public static Rule axiom58 = 
		Rule.parseRule("[ -> (owl:priorVersion rdfs:domain owl:Ontology) ]");
	public static Rule axiom59 = 
		Rule.parseRule("[ -> (owl:priorVersion rdfs:range owl:Ontology) ]");
	public static Rule axiom60 = 
		Rule.parseRule("[ -> (owl:backwardCompatibleWith rdfs:domain owl:Ontology) ]");
	public static Rule axiom61 = 
		Rule.parseRule("[ -> (owl:backwardCompatibleWith rdfs:range owl:Ontology) ]");
	public static Rule axiom62 = 
		Rule.parseRule("[ -> (owl:incompatibleWith rdfs:domain owl:Ontology) ]");
	public static Rule axiom63 = 
		Rule.parseRule("[ -> (owl:incompatibleWith rdfs:range owl:Ontology) ]");
	public static Rule axiom64 = 
		Rule.parseRule("[ -> (owl:versionInfo rdf:type owl:AnnotationProperty) ]");
	public static Rule axiom65 = 
		Rule.parseRule("[ -> (owl:differentFrom rdf:type owl:SymmetricProperty) ]");
	public static Rule axiom66 = 
		Rule.parseRule("[ -> (owl:disjointWith rdf:type owl:SymmetricProperty) ]");
	public static Rule axiom67 = 
		Rule.parseRule("[ -> (owl:intersectionOf rdfs:domain owl:Class) ]");
	public static Rule thing1 = 
		Rule.parseRule("[ thing1: (?C rdf:type owl:Class) " + 
				"-> (?C rdfs:subClassOf owl:Thing) (owl:Nothing rdfs:subClassOf ?C) ]");
	public static Rule equivalentClass1 = 
		Rule.parseRule("[ equivalentClass1: (?P owl:equivalentClass ?Q) " + 
				"-> (?P rdfs:subClassOf ?Q) (?Q rdfs:subClassOf ?P) ]");
	public static Rule equivalentClass2 = 
		Rule.parseRule("[ equivalentClass2: (?P rdfs:subClassOf ?Q) (?Q rdfs:subClassOf ?P) " + 
				"-> (?P owl:equivalentClass ?Q) ]");
	public static Rule equivalentClass3 = 
		Rule.parseRule("[ equivalentClass3: (?P owl:sameAs ?Q) (?P rdf:type owl:Class) " + 
				"(?Q rdf:type owl:Class) " + 
				"-> (?P owl:equivalentClass ?Q) ]");
	public static Rule equivalentProperty1 = 
		Rule.parseRule("[ equivalentProperty1: (?P owl:equivalentProperty ?Q) " + 
				"-> (?P rdfs:subPropertyOf ?Q) (?Q rdfs:subPropertyOf ?P) ]");
	public static Rule equivalentProperty2 = 
		Rule.parseRule("[ equivalentProperty2: (?P rdfs:subPropertyOf ?Q) (?Q rdfs:subPropertyOf ?P) " + 
				"-> (?P owl:equivalentProperty ?Q) ]");
	public static Rule equivalentProperty3 = 
		Rule.parseRule("[ equivalentProperty3: (?P owl:sameAs ?Q) (?P rdf:type rdf:Property) " + 
				"(?Q rdf:type rdf:Property) " + 
				"-> (?P owl:equivalentProperty ?Q) ]");
	public static Rule symmetricProperty1b = 
		Rule.parseRule("[ symmetricProperty1b: (?P rdf:type owl:SymmetricProperty) (?Y ?P ?X) " + 
				"-> (?X ?P ?Y) ]");
	public static Rule inverseOf1 = 
		Rule.parseRule("[ inverseOf1: (?P owl:inverseOf ?Q) -> (?Q owl:inverseOf ?P) ]");
	public static Rule inverseOf2b = 
		Rule.parseRule("[ inverseOf2b: (?P owl:inverseOf ?Q) (?Y ?Q ?X) -> (?X ?P ?Y) ]");
	public static Rule inverseOf3 = 
		Rule.parseRule("[ inverseOf3: (?P owl:inverseOf ?Q) (?P rdf:type owl:FunctionalProperty) " + 
				"-> (?Q rdf:type owl:InverseFunctionalProperty) ]");
	public static Rule inverseOf4 = 
		Rule.parseRule("[ inverseOf4: (?P owl:inverseOf ?Q) " + 
				"(?P rdf:type owl:InverseFunctionalProperty) " + 
				"-> (?Q rdf:type owl:FunctionalProperty) ]");
	public static Rule inverseof5 = 
		Rule.parseRule("[ inverseof5: (?P owl:inverseOf ?Q) (?P rdfs:range ?C) -> (?Q rdfs:domain ?C) ]");
	public static Rule inverseof6 = 
		Rule.parseRule("[ inverseof6: (?P owl:inverseOf ?Q) (?P rdfs:domain ?C) -> (?Q rdfs:range ?C) ]");
	public static Rule transitiveProperty1b = 
		Rule.parseRule("[ transitiveProperty1b: (?P rdf:type owl:TransitiveProperty) (?B ?P ?C) " + 
				"(?A ?P ?B) " + 
				"-> (?A ?P ?C) ]");
	public static Rule transitiveProperty1b2 = 
		Rule.parseRule("[ transitiveProperty1b: (?P rdf:type owl:TransitiveProperty) (?A ?P ?B) " + 
				"(?B ?P ?C) " + 
				"-> (?A ?P ?C) ]");
	public static Rule objectProperty = 
		Rule.parseRule("[ objectProperty: (?P rdf:type owl:ObjectProperty) " + 
				"-> (?P rdfs:domain owl:Thing) (?P rdfs:range owl:Thing) ]");
	public static Rule axiom68 = 
		Rule.parseRule("[ -> (xsd:float rdf:type rdfs:Datatype) ]");
	public static Rule axiom69 = 
		Rule.parseRule("[ -> (xsd:double rdf:type rdfs:Datatype) ]");
	public static Rule axiom70 = 
		Rule.parseRule("[ -> (xsd:int rdf:type rdfs:Datatype) ]");
	public static Rule axiom71 = 
		Rule.parseRule("[ -> (xsd:long rdf:type rdfs:Datatype) ]");
	public static Rule axiom72 = 
		Rule.parseRule("[ -> (xsd:short rdf:type rdfs:Datatype) ]");
	public static Rule axiom73 = 
		Rule.parseRule("[ -> (xsd:byte rdf:type rdfs:Datatype) ]");
	public static Rule axiom74 = 
		Rule.parseRule("[ -> (xsd:unsignedByte rdf:type rdfs:Datatype) ]");
	public static Rule axiom75 = 
		Rule.parseRule("[ -> (xsd:unsignedShort rdf:type rdfs:Datatype) ]");
	public static Rule axiom76 = 
		Rule.parseRule("[ -> (xsd:unsignedInt rdf:type rdfs:Datatype) ]");
	public static Rule axiom77 = 
		Rule.parseRule("[ -> (xsd:unsignedLong rdf:type rdfs:Datatype) ]");
	public static Rule axiom78 = 
		Rule.parseRule("[ -> (xsd:decimal rdf:type rdfs:Datatype) ]");
	public static Rule axiom79 = 
		Rule.parseRule("[ -> (xsd:integer rdf:type rdfs:Datatype) ]");
	public static Rule axiom80 = 
		Rule.parseRule("[ -> (xsd:nonPositiveInteger rdf:type rdfs:Datatype) ]");
	public static Rule axiom81 = 
		Rule.parseRule("[ -> (xsd:nonNegativeInteger rdf:type rdfs:Datatype) ]");
	public static Rule axiom82 = 
		Rule.parseRule("[ -> (xsd:positiveInteger rdf:type rdfs:Datatype) ]");
	public static Rule axiom83 = 
		Rule.parseRule("[ -> (xsd:negativeInteger rdf:type rdfs:Datatype) ]");
	public static Rule axiom84 = 
		Rule.parseRule("[ -> (xsd:boolean rdf:type rdfs:Datatype) ]");
	public static Rule axiom85 = 
		Rule.parseRule("[ -> (xsd:string rdf:type rdfs:Datatype) ]");
	public static Rule axiom86 = 
		Rule.parseRule("[ -> (xsd:anyURI rdf:type rdfs:Datatype) ]");
	public static Rule axiom87 = 
		Rule.parseRule("[ -> (xsd:hexBinary rdf:type rdfs:Datatype) ]");
	public static Rule axiom88 = 
		Rule.parseRule("[ -> (xsd:base64Binary rdf:type rdfs:Datatype) ]");
	public static Rule axiom89 = 
		Rule.parseRule("[ -> (xsd:date rdf:type rdfs:Datatype) ]");
	public static Rule axiom90 = 
		Rule.parseRule("[ -> (xsd:time rdf:type rdfs:Datatype) ]");
	public static Rule axiom91 = 
		Rule.parseRule("[ -> (xsd:dateTime rdf:type rdfs:Datatype) ]");
	public static Rule axiom92 = 
		Rule.parseRule("[ -> (xsd:duration rdf:type rdfs:Datatype) ]");
	public static Rule axiom93 = 
		Rule.parseRule("[ -> (xsd:gDay rdf:type rdfs:Datatype) ]");
	public static Rule axiom94 = 
		Rule.parseRule("[ -> (xsd:gMonth rdf:type rdfs:Datatype) ]");
	public static Rule axiom95 = 
		Rule.parseRule("[ -> (xsd:gYear rdf:type rdfs:Datatype) ]");
	public static Rule axiom96 = 
		Rule.parseRule("[ -> (xsd:gYearMonth rdf:type rdfs:Datatype) ]");
	public static Rule axiom97 = 
		Rule.parseRule("[ -> (xsd:gMonthDay rdf:type rdfs:Datatype) ]");
	public static Rule axiom98 = 
		Rule.parseRule("[ -> hide(rb:xsdBase) ]");
	public static Rule axiom99 = 
		Rule.parseRule("[ -> hide(rb:xsdRange) ]");
	public static Rule axiom100 = 
		Rule.parseRule("[ -> hide(rb:prototype) ]");
	public static Rule axiom101 = 
		Rule.parseRule("[ -> (xsd:byte rb:xsdBase xsd:decimal) ]");
	public static Rule axiom102 = 
		Rule.parseRule("[ -> (xsd:short rb:xsdBase xsd:decimal) ]");
	public static Rule axiom103 = 
		Rule.parseRule("[ -> (xsd:int rb:xsdBase xsd:decimal) ]");
	public static Rule axiom104 = 
		Rule.parseRule("[ -> (xsd:long rb:xsdBase xsd:decimal) ]");
	public static Rule axiom105 = 
		Rule.parseRule("[ -> (xsd:unsignedByte rb:xsdBase xsd:decimal) ]");
	public static Rule axiom106 = 
		Rule.parseRule("[ -> (xsd:unsignedShort rb:xsdBase xsd:decimal) ]");
	public static Rule axiom107 = 
		Rule.parseRule("[ -> (xsd:unsignedInt rb:xsdBase xsd:decimal) ]");
	public static Rule axiom108 = 
		Rule.parseRule("[ -> (xsd:unsignedLong rb:xsdBase xsd:decimal) ]");
	public static Rule axiom109 = 
		Rule.parseRule("[ -> (xsd:integer rb:xsdBase xsd:decimal) ]");
	public static Rule axiom110 = 
		Rule.parseRule("[ -> (xsd:nonNegativeInteger rb:xsdBase xsd:decimal) ]");
	public static Rule axiom111 = 
		Rule.parseRule("[ -> (xsd:nonPositiveInteger rb:xsdBase xsd:decimal) ]");
	public static Rule axiom112 = 
		Rule.parseRule("[ -> (xsd:float rb:xsdBase xsd:float) ]");
	public static Rule axiom113 = 
		Rule.parseRule("[ -> (xsd:decimal rb:xsdBase xsd:decimal) ]");
	public static Rule axiom114 = 
		Rule.parseRule("[ -> (xsd:string rb:xsdBase xsd:string) ]");
	public static Rule axiom115 = 
		Rule.parseRule("[ -> (xsd:boolean rb:xsdBase xsd:boolean) ]");
	public static Rule axiom116 = 
		Rule.parseRule("[ -> (xsd:date rb:xsdBase xsd:date) ]");
	public static Rule axiom117 = 
		Rule.parseRule("[ -> (xsd:time rb:xsdBase xsd:time) ]");
	public static Rule axiom118 = 
		Rule.parseRule("[ -> (xsd:dateTime rb:xsdBase xsd:dateTime) ]");
	public static Rule axiom119 = 
		Rule.parseRule("[ -> (xsd:duration rb:xsdBase xsd:duration) ]");
	public static Rule axiom120 = 
		Rule.parseRule("[ -> (xsd:byte rb:xsdRange xsd(xsd:integer, 1, 8)) ]");
	public static Rule axiom121 = 
		Rule.parseRule("[ -> (xsd:short rb:xsdRange xsd(xsd:integer, 1, 16)) ]");
	public static Rule axiom122 = 
		Rule.parseRule("[ -> (xsd:int rb:xsdRange xsd(xsd:integer, 1, 32)) ]");
	public static Rule axiom123 = 
		Rule.parseRule("[ -> (xsd:long rb:xsdRange xsd(xsd:integer, 1, 64)) ]");
	public static Rule axiom124 = 
		Rule.parseRule("[ -> (xsd:integer rb:xsdRange xsd(xsd:integer, 1 ,65)) ]");
	public static Rule axiom125 = 
		Rule.parseRule("[ -> (xsd:unsignedByte rb:xsdRange xsd(xsd:integer, 0, 8)) ]");
	public static Rule axiom126 = 
		Rule.parseRule("[ -> (xsd:unsignedShort rb:xsdRange xsd(xsd:integer 0, 16)) ]");
	public static Rule axiom127 = 
		Rule.parseRule("[ -> (xsd:unsignedInt rb:xsdRange xsd(xsd:integer 0, 32)) ]");
	public static Rule axiom128 = 
		Rule.parseRule("[ -> (xsd:unsignedLong rb:xsdRange xsd(xsd:integer 0, 64)) ]");
	public static Rule axiom129 = 
		Rule.parseRule("[ -> (xsd:nonNegativeInteger rb:xsdRange xsd(xsd:integer, 0, 65)) ]");
	public static Rule restriction1 = 
		Rule.parseRule("[ restriction1: (?C owl:onProperty ?P) (?C owl:someValuesFrom ?D) " + 
				"-> (?C owl:equivalentClass some(?P ?D)) ]");
	public static Rule restriction2 = 
		Rule.parseRule("[ restriction2: (?C owl:onProperty ?P) (?C owl:allValuesFrom ?D) " + 
				"-> (?C owl:equivalentClass all(?P ?D)) ]");
	public static Rule restriction3 = 
		Rule.parseRule("[ restriction3: (?C owl:onProperty ?P) (?C owl:minCardinality ?X) " + 
				"-> (?C owl:equivalentClass min(?P ?X)) ]");
	public static Rule restriction4 = 
		Rule.parseRule("[ restriction4: (?C owl:onProperty ?P) (?C owl:maxCardinality ?X) " + 
				"-> (?C owl:equivalentClass max(?P ?X)) ]");
	public static Rule restriction5 = 
		Rule.parseRule("[ restriction5: (?C owl:onProperty ?P) (?C owl:cardinality ?X) " + 
				"-> (?C owl:equivalentClass card(?P ?X)) (?C rdfs:subClassOf min(?P ?X)) " + 
				"(?C rdfs:subClassOf max(?P ?X)) ]");
	public static Rule restriction6 = 
		Rule.parseRule("[ restriction6: (?C rdfs:subClassOf min(?P ?X)) " + 
				"(?C rdfs:subClassOf max(?P ?X)) " + 
				"-> (?C rdfs:subClassOf card(?P ?X)) ]");
	public static Rule hasValueRec = 
		Rule.parseRule("[ hasValueRec: (?C owl:onProperty ?P) (?C owl:hasValue ?V) " + 
				"-> (?C owl:equivalentClass hasValue(?P ?V)) ]");
	public static Rule restrictionEq1 = 
		Rule.parseRule("[ restrictionEq1: (?R1 owl:equivalentClass some(?P ?C)) " + 
				"(?R2 owl:equivalentClass some(?P ?C)) notEqual(?R1 ?R2) " + 
				"-> (?R1 owl:equivalentClass ?R2) ]");
	public static Rule restrictionEq2 = 
		Rule.parseRule("[ restrictionEq2: (?R1 owl:equivalentClass all(?P ?C)) " + 
				"(?R2 owl:equivalentClass all(?P ?C)) notEqual(?R1 ?R2) " + 
				"-> (?R1 owl:equivalentClass ?R2) ]");
	public static Rule restrictionEq3 = 
		Rule.parseRule("[ restrictionEq3: (?R1 owl:equivalentClass min(?P ?C)) " + 
				"(?R2 owl:equivalentClass min(?P ?C)) notEqual(?R1 ?R2) " + 
				"-> (?R1 owl:equivalentClass ?R2) ]");
	public static Rule restrictionEq4 = 
		Rule.parseRule("[ restrictionEq4: (?R1 owl:equivalentClass max(?P ?C)) " + 
				"(?R2 owl:equivalentClass max(?P ?C)) notEqual(?R1 ?R2) " + 
				"-> (?R1 owl:equivalentClass ?R2) ]");
	public static Rule restrictionEq5 = 
		Rule.parseRule("[ restrictionEq5: (?R1 owl:equivalentClass card(?P ?C)) " + 
				"(?R2 owl:equivalentClass card(?P ?C)) notEqual(?R1 ?R2) " + 
				"-> (?R1 owl:equivalentClass ?R2) ]");
	public static Rule restrictionEq6 = 
		Rule.parseRule("[ restrictionEq6: (?R1 owl:equivalentClass hasValue(?P ?C)) " + 
				"(?R2 owl:equivalentClass hasValue(?P ?C)) notEqual(?R1 ?R2) " + 
				"-> (?R1 owl:equivalentClass ?R2) ]");
	public static Rule unionOf1 = 
		Rule.parseRule("[ unionOf1: (?C owl:unionOf ?L) -> listMapAsSubject(?L rdfs:subClassOf ?C) ]");
	public static Rule intersectionOf1 = 
		Rule.parseRule("[ intersectionOf1: (?C owl:intersectionOf ?L) " + 
				"-> listMapAsObject(?C rdfs:subClassOf ?L) ]");
	public static Rule someRec2b = 
		Rule.parseRule("[ someRec2b: (?C owl:equivalentClass some(?P ?D)) (?X ?P ?A) (?A rdf:type ?D) " + 
				"-> (?X rdf:type ?C) ]");
	public static Rule someRec2b2 = 
		Rule.parseRule("[ someRec2b: (?C owl:equivalentClass some(?P ?D)) (?D rdf:type rdfs:Datatype) " + 
				"(?X ?P ?A) isDType(?A ?D) " + 
				"-> (?X rdf:type ?C) ]");
	public static Rule restriction_inter_MnS = 
		Rule.parseRule("[ restriction-inter-MnS: (?P rdfs:range ?D) (?C rdfs:subClassOf min(?P, 1)) " + 
				"-> (?C rdfs:subClassOf some(?P ?D)) ]");
	public static Rule allRec1 = 
		Rule.parseRule("[ allRec1: (?C rdfs:subClassOf max(?P, 1)) (?C rdfs:subClassOf some(?P ?D)) " + 
				"-> (?C rdfs:subClassOf all(?P ?D)) ]");
	public static Rule allRec2 = 
		Rule.parseRule("[ allRec2: (?P rdf:type owl:FunctionalProperty) " + 
				"(?C rdfs:subClassOf some(?P ?C)) " + 
				"-> (?C rdfs:subClassOf all(?P ?C)) ]");
	public static Rule allRec4 = 
		Rule.parseRule("[ allRec4: (?P rdf:type owl:FunctionalProperty) " + 
				"(?C owl:equivalentClass all(?P ?D)) (?X ?P ?Y) (?Y rdf:type ?D) " + 
				"-> (?X rdf:type ?C) ]");
	public static Rule allRec5 = 
		Rule.parseRule("[ allRec5: (?C rdfs:subClassOf max(?P, 1)) " + 
				"(?C owl:equivalentClass all(?P ?D)) (?X ?P ?Y) (?Y rdf:type ?D) " + 
				"-> (?X rdf:type ?C) ]");
	public static Rule restriction_inter_RA_T = 
		Rule.parseRule("[ restriction-inter-RA-T: (?P rdfs:range ?C) " + 
				"(?D owl:equivalentClass all(?P ?C)) " + 
				"-> (owl:Thing rdfs:subClassOf ?D) ]");
	public static Rule restriction_inter_AT_R = 
		Rule.parseRule("[ restriction-inter-AT-R: (owl:Thing rdfs:subClassOf all(?P ?C)) " + 
				"-> (?P rdfs:range ?C) (?P rdf:type owl:ObjectProperty) ]");
	public static Rule hasValueIF = 
		Rule.parseRule("[ hasValueIF: (?C owl:equivalentClass hasValue(?P ?V)) (?x rdf:type ?C) " + 
				"-> (?x ?P ?V) ]");
	public static Rule hasValueIF2 = 
		Rule.parseRule("[ hasValueIF: (?C owl:equivalentClass hasValue(?P ?V)) (?x ?P ?V) " + 
				"-> (?x rdf:type ?C) ]");
	public static Rule nothing1 = 
		Rule.parseRule("[ nothing1: (?C rdfs:subClassOf min(?P ?n)) (?C rdfs:subClassOf max(?P ?x)) " + 
				"lessThan(?x ?n) " + 
				"-> (?C owl:equivalentClass owl:Nothing) ]");
	public static Rule nothing3 = 
		Rule.parseRule("[ nothing3: (?C rdfs:subClassOf owl:Nothing) " + 
				"-> (?C owl:equivalentClass owl:Nothing) ]");
	public static Rule nothing4 = 
		Rule.parseRule("[ nothing4: (?C owl:oneOf rdf:nil) -> (?C owl:equivalentClass owl:Nothing) ]");
	public static Rule distinct1 = 
		Rule.parseRule("[ distinct1: (?C owl:disjointWith ?D) (?X rdf:type ?C) (?Y rdf:type ?D) " + 
				"-> (?X owl:differentFrom ?Y) ]");
	public static Rule distinct2 = 
		Rule.parseRule("[ distinct2: (?w owl:distinctMembers ?L) " + 
				"-> assertDisjointPairs(?L) ]");
	public static Rule min2b = 
		Rule.parseRule("[ min2b: (?C owl:equivalentClass min(?P, 1)) notEqual(?P rdf:type) " + 
				"(?X ?P ?Y) " + 
				"-> (?X rdf:type ?C) ]");
	public static Rule maxRec = 
		Rule.parseRule("[ maxRec: (?C owl:equivalentClass max(?P, 1)) " + 
				"(?P rdf:type owl:FunctionalProperty) " + 
				"-> (owl:Thing rdfs:subClassOf ?C) ]");
	public static Rule maxRec2 = 
		Rule.parseRule("[ maxRec2: (?C owl:equivalentClass max(?P, 0)) (?P rdfs:domain ?D) " + 
				"(?E owl:disjointWith ?D) " + 
				"-> (?E owl:equivalentClass ?C) ]");
	public static Rule cardRec1 = 
		Rule.parseRule("[ cardRec1: (?C owl:equivalentClass card(?P, 0)) (?P rdfs:domain ?D) " + 
				"(?E owl:disjointWith ?D) " + 
				"-> (?E owl:equivalentClass ?C) ]");
	public static Rule restriction_inter_CFP = 
		Rule.parseRule("[ restriction-inter-CFP: (?C owl:equivalentClass card(?P, 1)) " + 
				"(?P rdf:type owl:FunctionalProperty) " + 
				"-> (?C owl:equivalentClass min(?P, 1)) ]");
	public static Rule restriction62 = 
		Rule.parseRule("[ restriction6: (?C owl:equivalentClass min(?P, ?X)) " + 
				"(?C owl:equivalentClass max(?P, ?X)) -> (?C owl:equivalentClass card(?P ?X)) ]");
	public static Rule validationDomainMax0 = 
		Rule.parseRule("[ validationDomainMax0: (?v rb:validation on()) " + 
				"(?C rdfs:subClassOf max(?P, 0)) (?P rdfs:domain ?C) " + 
				"-> (?P rb:violation error('inconsistent property definition', " + 
				"'Property defined with domain which has a max(0) restriction for that " + 
				"property (domain)', ?C)) ]");
	public static Rule max2b = 
		Rule.parseRule("[ max2b: (?v rb:validation on()) (?C rdfs:subClassOf max(?P, 0)) " + 
				"(?X rdf:type ?C) (?X ?P ?Y) -> (?X rb:violation error('too many values', " + 
				"'Value for max-0 property (prop, class)', ?P, ?C)) ]");
	public static Rule max2b2 = 
		Rule.parseRule("[ max2b: (?v rb:validation on()) (?C rdfs:subClassOf max(?P ?N)) " + 
				"(?X rdf:type ?C) countLiteralValues(?X ?P ?M) lessThan(?N ?M) " + 
				"-> (?X rb:violation error('too many values' " + 
				"'Too many values on max-N property (prop, class)' ?P ?C)) ]");
	public static Rule validationIndiv = 
		Rule.parseRule("[ validationIndiv: (?v rb:validation on()) (?X owl:differentFrom ?Y) " + 
				"(?X owl:sameAs ?Y) " + 
				"-> (?X rb:violation error('conflict' " + 
				"'Two individuals both same and different, may be due to disjoint classes or " + 
				"functional properties' ?Y)) ]");
	public static Rule validationIndiv2 = 
		Rule.parseRule("[ validationIndiv: (?v rb:validation on()) (?X owl:disjointWith ?Y) " + 
				"(?I rdf:type ?X) (?I rdf:type ?Y) " + 
				"-> (?I rb:violation error('conflict' 'Individual a member of disjoint classes' " + 
				"?X ?Y)) ]");
	public static Rule validationIndiv3 = 
		Rule.parseRule("[ validationIndiv: (?v rb:validation on()) (?I rdf:type owl:Nothing) " + 
				"-> (?I rb:violation error('conflict' 'Individual a member of Nothing' ?I)) ]");
	public static Rule validationIndiv4 = 
		Rule.parseRule("[ validationIndiv: (?v rb:validation on()) (?X owl:disjointWith ?Y) " + 
				"(?X owl:disjointWith ?Y) (?X rdfs:subClassOf ?Y) " + 
				"-> (?X rb:violation warn('Inconsistent class' " + 
				"'Two classes related by both subclass and disjoint relations' ?Y)) ]");
	public static Rule validationIndiv5 = 
		Rule.parseRule("[ validationIndiv: (?v rb:validation on()) (?X owl:disjointWith ?Y) " + 
				"(?X owl:disjointWith ?Y) (?C rdfs:subClassOf ?X) (?C rdfs:subClassOf ?Y) " + 
				"notEqual(?C owl:Nothing) " + 
				"-> (?C rb:violation warn('Inconsistent class' 'subclass of two disjoint classes' " + 
				"?X ?Y)) ]");
	public static Rule validationDTP = 
		Rule.parseRule("[ validationDTP: (?v rb:validation on()) (?P rdf:type owl:DatatypeProperty) " + 
				"(?X ?P ?V) notLiteral(?V) notBNode(?V) " + 
				"-> (?X rb:violation error('range check' " + 
				"'Object value for datatype property (prop, value)' ?P ?V)) ]");
	public static Rule validationDTP2 = 
		Rule.parseRule("[ validationDTP: (?v rb:validation on()) (?P rdf:type owl:ObjectProperty) " + 
				"(?X ?P ?V) isLiteral(?V) " + 
				"-> (?X rb:violation warn('range check' " + 
				"'Literal value for object property (prop, value)' ?P ?V)) ]");
	public static Rule validationDTRange = 
		Rule.parseRule("[ validationDTRange: (?v rb:validation on()) (?P rdfs:range ?R) " + 
				"(?R rdf:type rdfs:Datatype) (?X ?P ?V) notDType(?V ?R) " + 
				"-> (?X rb:violation error('range check' 'Incorrectly typed literal due to " + 
				"range (prop, value)' ?P ?V)) ]");
	public static Rule validationDTRange2 = 
		Rule.parseRule("[ validationDTRange: (?v rb:validation on()) (?P rdfs:range rdfs:Literal) " + 
				"(?X ?P ?V) notLiteral(?V) notBNode(?V) " + 
				"-> (?X rb:violation error('range check' 'Incorrectly typed literal due to " + 
				"range rdsf:Literal (prop, value)' ?P ?V)) ]");
	public static Rule validationDTRange3 = 
		Rule.parseRule("[ validationDTRange: (?v rb:validation on()) (?C rdfs:subClassOf all(?P ?R)) " + 
				"(?R rdf:type rdfs:Datatype) (?X ?P ?V) (?X rdf:type ?C) notDType(?V ?R) " + 
				"-> (?X rb:violation error('range check' 'Incorrectly typed literal due to " + 
				"allValuesFrom (prop, value)' ?P ?V)) ]");
	public static Rule validationDTRange4 = 
		Rule.parseRule("[ validationDTRange: (?v rb:validation on()) " + 
				"(?C owl:equivalentClass all(?P rdfs:Literal)) (?X ?P ?V) (?X rdf:type ?C) " + 
				"notDType(?V rdfs:Literal) " + 
				"-> (?X rb:violation error('range check' 'Incorrectly typed literal due to " + 
				"allValuesFrom rdfs:Literal (prop, value)' ?P ?V)) ]");
	public static Rule validationNothing = 
		Rule.parseRule("[ validationNothing: (?v rb:validation on()) " + 
				"(?C owl:equivalentClass owl:Nothing) notEqual(?C owl:Nothing) " + 
				"-> (?C rb:violation warn('Inconsistent class' 'Class cannot be instantiated, " + 
				"probably subclass of a disjoint classes or of an empty restriction')) ]");
	public static Rule validationRangeNothing = 
		Rule.parseRule("[ validationRangeNothing: (?v rb:validation on()) (?P rdfs:range owl:Nothing) " + 
				"-> (?C rb:violation warn('Inconsistent property' 'Property cannot be instantiated, " + 
				"probably due to multiple disjoint range declarations')) ]");
	public static Rule validationIndiv6 = 
		Rule.parseRule("[ validationIndiv: (?v rb:validation on()) (?C owl:oneOf ?L) " + 
				"(?X rdf:type ?C) notBNode(?X) listNotContains(?L ?X) " + 
				"-> (?X rb:violation warn('possible oneof violation' 'Culprit is deduced to be " + 
				"of enumerated type (implicicated class) but is not one of the enumerations\n " + 
				"This may be due to aliasing.' ?Y)) ]");
	public static final List<Rule> rulesRDFSAxioms = 
		ListUtil.newList(axiom, axiom2, axiom3, axiom4, axiom5, axiom6, axiom7, axiom8, axiom9, axiom10, 
				axiom11, axiom12, axiom13, axiom14, axiom15, axiom16, axiom17, axiom18, axiom19, axiom20, 
				axiom21, axiom22, axiom23, axiom24, axiom25, axiom26, axiom27, axiom28, axiom29, axiom30, 
				axiom31, axiom32, axiom33);
	public static final List<Rule> rulesRDFSCore = 
		ListUtil.newList(rdf1and4, rdfs7b, rdfs2, rdfs3, rdfs5b, rdfs6, rdfs7, rdfs10, rdfs2_partial, 
				rdfs3_partial, rdfs9_alt, rdfs9_alt2, rdfs2a, rdfs2a2, rdfs3a, rdfs3a2, rdfs12a, rdfs12a2, 
				rdfs12a3, rdfs12b, rdfs12b2, rdfsder1, rdfsder2);
	public static final List<Rule> rulesOWLAxioms = 
		ListUtil.newList(axiom34, axiom35, axiom36, axiom37, axiom38, axiom39, axiom40, axiom41, axiom42, 
				axiomOWLClassIsRDFSClass, axiom44, axiom45, axiom46, axiom47, axiom48, axiom49, axiom50, 
				axiom51, axiom52, axiom53, axiom54, axiom55, axiom56, axiom57, axiom58, axiom59, axiom60, 
				axiom61, axiom62, axiom63, axiom64, axiom65, axiom66, axiom67);
	public static final List<Rule> rulesEquivalences = 
		ListUtil.newList(thing1, equivalentClass1, equivalentClass2, equivalentClass3, 
				equivalentProperty1, equivalentProperty2, equivalentProperty3); 
	public static final List<Rule> rulesPropertyRelations = 
		ListUtil.newList(symmetricProperty1b, inverseOf1, inverseOf2b, inverseOf3, inverseOf4, inverseof5, 
				inverseof6, transitiveProperty1b, transitiveProperty1b2, objectProperty);
	public static final List<Rule> rulesDatatypeAxioms = 
		ListUtil.newList(axiom68, axiom69, axiom70, axiom71, axiom72, axiom73, axiom74, axiom75, axiom76, 
				axiom77, axiom78, axiom79, axiom80, axiom81, axiom82, axiom83, axiom84, axiom85, axiom86, 
				axiom87, axiom88, axiom89, axiom90, axiom91, axiom92, axiom93, axiom94, axiom95, axiom96, 
				axiom97, axiom98, axiom99, axiom100, axiom101, axiom102, axiom103, axiom104, axiom105, 
				axiom106, axiom107, axiom108, axiom109, axiom110, axiom111, axiom112, axiom113, axiom114, 
				axiom115, axiom116, axiom117, axiom118, axiom119, axiom120, axiom121, axiom122, axiom123, 
				axiom124, axiom125, axiom126, axiom127, axiom128, axiom129);
	public static final List<Rule> rulesRestrictions = 
		ListUtil.newList(restriction1, restriction2, restriction3, restriction4, restriction5, 
				restriction6, hasValueRec, restrictionEq1, restrictionEq2, restrictionEq3, 
				restrictionEq4, restrictionEq5, restrictionEq6);
	public static final List<Rule> rulesClassRelations = 
		ListUtil.newList(unionOf1, intersectionOf1, someRec2b, someRec2b2, restriction_inter_MnS, 
				allRec1, allRec2, allRec4, allRec5, restriction_inter_RA_T, restriction_inter_AT_R, 
				hasValueIF, hasValueIF2, nothing1, nothing3, nothing4, distinct1, distinct2);
	public static final List<Rule> rulesCardinality = 
		ListUtil.newList(min2b, maxRec, maxRec2, cardRec1, restriction_inter_CFP, restriction62);
	public static final List<Rule> rulesValidation = 
		ListUtil.newList(validationDomainMax0, max2b, max2b2, validationIndiv, validationIndiv2, 
				validationIndiv3, validationIndiv4, validationIndiv5, validationDTP, validationDTP2, 
				validationDTRange, validationDTRange2, validationDTRange3, validationDTRange4, 
				validationNothing, validationRangeNothing, validationIndiv6);

	public static final List<Rule> rulesAll = 
		ListUtil.concatedList(rulesRDFSAxioms, rulesRDFSCore, rulesOWLAxioms, rulesEquivalences, 
				rulesPropertyRelations, rulesDatatypeAxioms, rulesRestrictions, rulesClassRelations, 
				rulesCardinality, rulesValidation);
	
	public static void main(String[] args) {
		for(Rule rule : rulesAll) {
			System.out.println(rule);
		}
	}
}
