/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas;

/*   SBPAX  --- by Oliver Ruebenacker, UCHC --- April 2008 to May 2011
 *   The SBPAX Level 3 schema
 */

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.sbpax.impl.HashGraph;
import org.sbpax.impl.IndexedGraph;
import org.sbpax.schemas.util.NameSpace;
import org.sbpax.schemas.util.OntUtil;

public class UOMECore {

	public static final String uri = "http://www.sbpax.org/uome/core.owl";

	public static final NameSpace ns = new NameSpace("uome-core", uri + "#");

	public static final Graph schema = new HashGraph();

	public static final URI ontology = OntUtil.createOntologyNode(schema, uri);

	static {
		schema.add(ontology, OWL.IMPORTS, BioPAX3.ontology);
	}

	public static final URI UnitOfMeasurementVocabulary = 
		OntUtil.createClass(schema, ns + "UnitOfMeasurementVocabulary");

	static {
		schema.add(UnitOfMeasurementVocabulary, RDFS.SUBCLASSOF, BioPAX3.ControlledVocabulary);
	}

	public static final URI UnitOfMeasurement = OntUtil.createClass(schema, ns + "UnitOfMeasurement");
	public static final URI unitTerm = OntUtil.createObjectProperty(schema, ns + "unitTerm");
	public static final URI unitName = OntUtil.createDatatypeProperty(schema, ns + "unitName");
	public static final URI unitSymbol = OntUtil.createDatatypeProperty(schema, ns + "unitSymbol");	
	public static final URI derivedBy = OntUtil.createObjectProperty(schema, ns + "derivedBy");

	public static final URI Expression = OntUtil.createClass(schema, ns + "Expression");

	static {
		schema.add(unitTerm, RDFS.DOMAIN, UnitOfMeasurement);
		schema.add(unitTerm, RDFS.RANGE, UnitOfMeasurementVocabulary);
		schema.add(unitName, RDFS.DOMAIN, UnitOfMeasurement);
		schema.add(unitName, RDFS.RANGE, XMLSchema.STRING);
		schema.add(unitSymbol, RDFS.DOMAIN, UnitOfMeasurement);
		schema.add(unitSymbol, RDFS.RANGE, XMLSchema.STRING);
		schema.add(UnitOfMeasurement, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.MINCARDINALITY, unitName, 1));
		schema.add(UnitOfMeasurement, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.MINCARDINALITY, unitSymbol, 1));
	}

	public static final URI SingleUnitExpression = 
		OntUtil.createClass(schema, ns + "SingleUnitExpression");	
	public static final URI withUnit = OntUtil.createObjectProperty(schema, ns + "withUnit");

	static {
		schema.add(SingleUnitExpression, RDFS.SUBCLASSOF, Expression);
		schema.add(withUnit, RDFS.DOMAIN, SingleUnitExpression);
		schema.add(withUnit, RDFS.RANGE, UnitOfMeasurement);
		schema.add(SingleUnitExpression, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, withUnit, 1));
	}

	public static final URI EquivalenzExpression = OntUtil.createClass(schema, ns + "EquivalenzExpression");	

	static {
		schema.add(EquivalenzExpression, RDFS.SUBCLASSOF, SingleUnitExpression);
	}

	public static final URI ScalingExpression = OntUtil.createClass(schema, ns + "ScalingExpression");	
	public static final URI withFactor = OntUtil.createDatatypeProperty(schema, ns + "withFactor");	

	static {
		schema.add(ScalingExpression, RDFS.SUBCLASSOF, SingleUnitExpression);
		schema.add(withFactor, RDFS.DOMAIN, ScalingExpression);
		schema.add(withFactor, RDFS.RANGE, XMLSchema.DOUBLE);
		schema.add(ScalingExpression, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, withFactor, 1));
	}

	public static final URI OffsetExpression = OntUtil.createClass(schema, ns + "OffsetExpression");	
	public static final URI withOffset = OntUtil.createDatatypeProperty(schema, ns + "withOffset");	

	static {
		schema.add(OffsetExpression, RDFS.SUBCLASSOF, SingleUnitExpression);
		schema.add(withOffset, RDFS.DOMAIN, OffsetExpression);
		schema.add(withOffset, RDFS.RANGE, XMLSchema.DOUBLE);
		schema.add(OffsetExpression, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, withOffset, 1));
	}

	public static final URI ExponentialExpression = OntUtil.createClass(schema, ns + "ExponentialExpression");	
	public static final URI withExponent = 
		OntUtil.createDatatypeProperty(schema, ns + "withExponent");	

	static {
		schema.add(ExponentialExpression, RDFS.SUBCLASSOF, SingleUnitExpression);
		schema.add(withExponent, RDFS.DOMAIN, ExponentialExpression);
		schema.add(withExponent, RDFS.RANGE, XMLSchema.STRING);
		schema.add(ExponentialExpression, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, withExponent, 1));
	}	

	public static final URI BinaryExpression = OntUtil.createClass(schema, ns + "BinaryExpression");	
	public static final URI withUnit1 = OntUtil.createObjectProperty(schema, ns + "withUnit1");
	public static final URI withUnit2 = OntUtil.createObjectProperty(schema, ns + "withUnit2");

	static {
		schema.add(BinaryExpression, RDFS.SUBCLASSOF, Expression);
		schema.add(withUnit1, RDFS.DOMAIN, BinaryExpression);
		schema.add(withUnit1, RDFS.RANGE, UnitOfMeasurement);
		schema.add(withUnit2, RDFS.DOMAIN, BinaryExpression);
		schema.add(withUnit2, RDFS.RANGE, UnitOfMeasurement);
		schema.add(BinaryExpression, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, withUnit1, 1));
		schema.add(BinaryExpression, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, withUnit2, 1));
	}

	public static final URI ProductExpression = OntUtil.createClass(schema, ns + "ProductExpression");	

	static {
		schema.add(ProductExpression, RDFS.SUBCLASSOF, BinaryExpression);
	}

	public static final URI QuotientExpression = OntUtil.createClass(schema, ns + "QuotientExpression");	

	static {
		schema.add(QuotientExpression, RDFS.SUBCLASSOF, BinaryExpression);
	}

	static {

		// OntUtil.addTypedComment(schema, , "");

		OntUtil.addTypedComment(schema, derivedBy, 
				"An expression that can be used to derive this unit.\n" +
				"Example: J can be derived by the product N*m.");

		OntUtil.addTypedComment(schema, BinaryExpression, 
				"An expression deriving a unit involving two other units\n" +
				"Examples: product or quotient of units.");

		OntUtil.addTypedComment(schema, ExponentialExpression, 
				"An expression deriving a unit by exponentiating an original unit by a number.\n" +
				"Example: square meter can be derived by taking meter to the power of two.");

		OntUtil.addTypedComment(schema, Expression, 
				"An expression deriving a unit from other units and numbers.\n" +
				"Example: square meter can be derived by taking meter to the power of two.");

		OntUtil.addTypedComment(schema, EquivalenzExpression, 
				"An expression deriving a unit by declaring it equivalent to another.\n" +
				"Example: radian is equivalent to the dimensionless unit.");

		OntUtil.addTypedComment(schema, OffsetExpression, 
				"An expression deriving a unit offsetting the zero-point of an original unit by a number.\n" +
				"Example: celsius can be derived from kelvin by offsetting the zero-point " + 
				"by about 273.");

		OntUtil.addTypedComment(schema, ProductExpression, 
				"An expression deriving a unit by multiplying two other units\n" +
				"Examples: joule can be derived as the product of newton and metre.");

		OntUtil.addTypedComment(schema, QuotientExpression, 
				"An expression deriving a unit from two other units by dividing one of those by the other.\n" +
				"Examples: watt can be derived as the quotient of joule and second.");

		OntUtil.addTypedComment(schema, ScalingExpression, 
				"An expression deriving a unit by scaling an original unit with a number.\n" +
				"Example: microns can be derived from meter scaled by 1e-3.");

		OntUtil.addTypedComment(schema, SingleUnitExpression, 
				"An expression deriving a unit involving one other unit.\n" +
				"Examples: multiplication, exponentiation and offset of a unit by a number.");

		OntUtil.addTypedComment(schema, unitName, 
				"The name of a unit.\n" +
				"Examples: meter, kelvin, minute.");

		OntUtil.addTypedComment(schema, UnitOfMeasurement, 
				"A unit used to describe anything measurable in the widest sense. " +
				"Includes anything definitely quantifiable.\n" +
				"Example: kilogram, litre, ampere, minute, inch, fluid ounce, psi.");

		OntUtil.addTypedComment(schema, UnitOfMeasurementVocabulary, 
				"Vocabulary to describe a unit of measurement.\n" + 
				"Example: a term form the Unit Ontology.");

		OntUtil.addTypedComment(schema, unitSymbol, 
				"The symbol of a unit.\n" +
				"Examples: m, K, min.");

		OntUtil.addTypedComment(schema, unitTerm, 
				"The controlled vocabulary term describing a unit.\n" +
				"Examples: m, K, min.");

		OntUtil.addTypedComment(schema, withOffset, 
				"The zero-point of the expressed units in terms of the original unit.\n" +
				"Example: to get an expression for celsius, create an offset expression with unit "+
				"Kelvin and an offset " +
				"of about 273, because the zero-point of celsius is at about 273 kelvin");

		OntUtil.addTypedComment(schema, withExponent, 
				"The exponent in an exponent expression.\n" +
				"Example: the 2 in m^2.");

		OntUtil.addTypedComment(schema, withFactor, 
				"The factor in a scaling expression.\n" +
				"Example: the 1000 in 1000*m");

		OntUtil.addTypedComment(schema, withUnit, 
				"The unit in an expression that contains only one unit.\n" +
				"Example: the m in 1000*m");

		OntUtil.addTypedComment(schema, withUnit1, 
				"The first unit in a binary expression of units.\n" +
				"Example: the m in m/s");

		OntUtil.addTypedComment(schema, withUnit2, 
				"The second unit in a binary expression of units.\n" +
				"Example: the s in m/s");

		// OntUtil.addTypedComment(schema, , "");

	}

}
