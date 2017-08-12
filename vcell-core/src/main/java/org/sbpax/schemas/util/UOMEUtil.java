/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas.util;

import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.sbpax.schemas.BioPAX3;
import org.sbpax.schemas.UOMECore;
import org.sbpax.schemas.UOMEList;

public class UOMEUtil {
	
	public static interface UnitExpression {
		public void createFor(Graph schema, Resource unit);
	}
	
	public static class IdentityExpression implements UnitExpression {

		protected final Resource unit1;

		public IdentityExpression(Resource unit1) {
			this.unit1 = unit1;
		}

		public void createFor(Graph schema, Resource unit) {
			BNode expression = OntUtil.createBIndividual(schema, UOMECore.EquivalenzExpression);
			schema.add(unit, UOMECore.derivedBy, expression);
			schema.add(expression, UOMECore.withUnit, unit1);
		};
		
	}
	
	public static abstract class SingleUnitExpression implements UnitExpression {
		protected final Resource unit1;
		protected final double number;
		public SingleUnitExpression(Resource unit1, double number) {
			this.unit1 = unit1;
			this.number = number;
		}
	}
	
	public static class ScalingExpression extends SingleUnitExpression {
		
		public ScalingExpression(Resource unit1, double number) { super(unit1, number); }

		public void createFor(Graph schema, Resource unit) {
			BNode expression = OntUtil.createBIndividual(schema, UOMECore.ScalingExpression);
			schema.add(unit, UOMECore.derivedBy, expression);
			schema.add(expression, UOMECore.withUnit, unit1);
			schema.add(expression, UOMECore.withFactor, schema.getValueFactory().createLiteral(number));
		};
		
	}

	public static class OffsetExpression extends SingleUnitExpression {
		
		public OffsetExpression(Resource unit1, double number) { super(unit1, number); }

		public void createFor(Graph schema, Resource unit) {
			BNode expression = OntUtil.createBIndividual(schema, UOMECore.OffsetExpression);
			schema.add(unit, UOMECore.derivedBy, expression);
			schema.add(expression, UOMECore.withUnit, unit1);
			schema.add(expression, UOMECore.withOffset, schema.getValueFactory().createLiteral(number));
		};
		
	}

	public static class ExponentialExpression extends SingleUnitExpression {
		
		public ExponentialExpression(Resource unit1, double number) { super(unit1, number); }

		public void createFor(Graph schema, Resource unit) {
			BNode expression = OntUtil.createBIndividual(schema, UOMECore.ExponentialExpression);
			schema.add(unit, UOMECore.derivedBy, expression);
			schema.add(expression, UOMECore.withUnit, unit1);
			schema.add(expression, UOMECore.withExponent, schema.getValueFactory().createLiteral(number));
		};
		
	}

	public static abstract class BinaryExpression implements UnitExpression {

		protected final Resource unit1, unit2;

		public BinaryExpression(Resource unit1, Resource unit2) {
			this.unit1 = unit1;
			this.unit2 = unit2;
		}
		
	}
	
	public static class ProductExpression extends BinaryExpression {
		
		public ProductExpression(Resource unit1, Resource unit2) { super(unit1, unit2); }

		public void createFor(Graph schema, Resource unit) {
			BNode expression = OntUtil.createBIndividual(schema, UOMECore.ProductExpression);
			schema.add(unit, UOMECore.derivedBy, expression);
			schema.add(expression, UOMECore.withUnit1, unit1);
			schema.add(expression, UOMECore.withUnit2, unit2);
		}
		
	}
	
	public static class QuotientExpression extends BinaryExpression {
		
		public QuotientExpression(Resource unit1, Resource unit2) { super(unit1, unit2); }

		public void createFor(Graph schema, Resource unit) {
			BNode expression = OntUtil.createBIndividual(schema, UOMECore.QuotientExpression);
			schema.add(unit, UOMECore.derivedBy, expression);
			schema.add(expression, UOMECore.withUnit1, unit1);
			schema.add(expression, UOMECore.withUnit2, unit2);
		}
		
	}
	
	public static URI createUnit(Graph schema, NameSpace ns, String id, String name, 
			String symbol, int uoNumber, UnitExpression ... expressions) {
		URI unit = OntUtil.createURIIndividual(schema, ns + id, UOMECore.UnitOfMeasurement);
		if(name != null) {
			schema.add(unit, UOMECore.unitName, OntUtil.createTypedString(schema, name));			
		}
		if(symbol != null) {
			schema.add(unit, UOMECore.unitSymbol, OntUtil.createTypedString(schema, symbol));			
		}
		if(uoNumber != UOMEList.NONUMBER) {
			URI ref = schema.getValueFactory().createURI(ns + id + "Ref");
			schema.add(ref, RDF.TYPE, BioPAX3.UnificationXref);
			schema.add(ref, BioPAX3.db, OntUtil.createTypedString(schema, "UO"));
			schema.add(ref, BioPAX3.id, OntUtil.createTypedString(schema, "" + uoNumber));
			URI term = schema.getValueFactory().createURI(ns + id + "Term");
			schema.add(term, RDF.TYPE, UOMECore.UnitOfMeasurementVocabulary);
			schema.add(term, BioPAX3.xref, ref);
			schema.add(term, BioPAX3.term, OntUtil.createTypedString(schema, name));
			schema.add(unit, UOMECore.unitTerm, term);
		}
		for(UnitExpression expression : expressions) {
			expression.createFor(schema, unit);
		}
		return unit;
	}
	
}
