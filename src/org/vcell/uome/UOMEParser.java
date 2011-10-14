/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Graph;
import org.sbpax.schemas.UOMECore;
import org.sbpax.util.sets.SetOfOne;
import org.vcell.sybil.rdf.RDFEvaluator;
import org.vcell.sybil.rdf.nodestore.RDFValueSets;
import org.vcell.sybil.rdf.pool.RDFObjectSets;
import org.vcell.uome.core.UOMEBinaryExpression;
import org.vcell.uome.core.UOMEExponentialExpression;
import org.vcell.uome.core.UOMEExpression;
import org.vcell.uome.core.UOMEOffsetExpression;
import org.vcell.uome.core.UOMEScalingExpression;
import org.vcell.uome.core.UOMESingleUnitExpression;
import org.vcell.uome.core.UOMEUnit;

public class UOMEParser {

	public static Set<UOMEParserIssue> parseUOME(Graph graph, UOMEObjectPools objectPools) {
		return parseUOME(new SetOfOne<Graph>(graph), objectPools);
	}

	public static Set<UOMEParserIssue> parseUOME(Set<Graph> graphs, UOMEObjectPools objectPools) {
		Set<UOMEParserIssue> issues = new HashSet<UOMEParserIssue>();
		RDFEvaluator rdfEval = new RDFEvaluator(graphs);
		UOMEUnitPool unitPool = objectPools.getUnitPool();
		Set<UOMEUnit> units = rdfEval.createAllObjectsForPool(unitPool);
		for(UOMEUnit unit : units) {
			Set<String> names = 
				rdfEval.getProperties(unit, unitPool, UOMECore.unitName).getStrings();
			unit.getNames().addAll(names);
			Set<String> symbols = 
				rdfEval.getProperties(unit, unitPool, UOMECore.unitSymbol).getStrings();
			unit.getSymbols().addAll(symbols);
			UOMEExpressionPool expressionPool = objectPools.getExpressionPool();
			Set<UOMEExpression> expressions = 
				rdfEval.getProperties(unit, unitPool, UOMECore.derivedBy, expressionPool).getObjects();
			unit.getExpressions().addAll(expressions);
			for(UOMEExpression expression : unit.getExpressions()) {
				UOMEParserIssue.checkExpressionClass(issues, expressionPool, expression);
				if(expression instanceof UOMESingleUnitExpression) {
					UOMESingleUnitExpression singleUnitExpression = (UOMESingleUnitExpression) expression;
					RDFObjectSets<UOMEUnit> propertiesUnit = 
						rdfEval.getProperties(singleUnitExpression, expressionPool, UOMECore.withUnit, unitPool);
					UOMEParserIssue.checkSingularCardinality(issues, singleUnitExpression, UOMECore.withUnit, propertiesUnit.getObjects());
					if(!propertiesUnit.getObjects().isEmpty()) {
						singleUnitExpression.setUnit(propertiesUnit.getObject());						
					}
					if(singleUnitExpression instanceof UOMEScalingExpression) {
						UOMEScalingExpression scalingExpression = (UOMEScalingExpression) singleUnitExpression;
						RDFValueSets properties = rdfEval.getProperties(scalingExpression, expressionPool, UOMECore.withFactor);
						UOMEParserIssue.checkSingularCardinality(issues, singleUnitExpression, UOMECore.withFactor, properties.getDoubles());
						if(!properties.getDoubles().isEmpty()) {
							scalingExpression.setFactor(properties.getDoubleValue());							
						}
					} else if(singleUnitExpression instanceof UOMEOffsetExpression) {
						UOMEOffsetExpression offsetExpression = (UOMEOffsetExpression) singleUnitExpression;
						RDFValueSets properties = rdfEval.getProperties(offsetExpression, expressionPool, UOMECore.withOffset);
						UOMEParserIssue.checkSingularCardinality(issues, singleUnitExpression, UOMECore.withOffset, properties.getDoubles());
						if(!properties.getDoubles().isEmpty()) {
							offsetExpression.setOffset(properties.getDoubleValue());							
						}
					} else if(singleUnitExpression instanceof UOMEExponentialExpression) {
						UOMEExponentialExpression offsetExpression = (UOMEExponentialExpression) singleUnitExpression;
						RDFValueSets properties = rdfEval.getProperties(offsetExpression, expressionPool, UOMECore.withExponent);
						UOMEParserIssue.checkSingularCardinality(issues, singleUnitExpression, UOMECore.withExponent, properties.getDoubles());
						if(!properties.getDoubles().isEmpty()) {
							offsetExpression.setExponent(properties.getDoubleValue());							
						}
					}
				} else if(expression instanceof UOMEBinaryExpression) {
					UOMEBinaryExpression binaryExpression = (UOMEBinaryExpression) expression;
					RDFObjectSets<UOMEUnit> propertiesUnit1 = 
						rdfEval.getProperties(binaryExpression, expressionPool, UOMECore.withUnit1, unitPool);
					UOMEParserIssue.checkSingularCardinality(issues, binaryExpression, UOMECore.withUnit1, propertiesUnit1.getObjects());
					if(!propertiesUnit1.getObjects().isEmpty()) {
						binaryExpression.setUnit1(propertiesUnit1.getObject());						
					}
					RDFObjectSets<UOMEUnit> propertiesUnit2 = 
						rdfEval.getProperties(binaryExpression, expressionPool, UOMECore.withUnit2, unitPool);
					UOMEParserIssue.checkSingularCardinality(issues, binaryExpression, UOMECore.withUnit2, propertiesUnit2.getObjects());
					if(!propertiesUnit2.getObjects().isEmpty()) {
						binaryExpression.setUnit2(propertiesUnit2.getObject());						
					}
				}
			}
			// TODO to be continued
		}
		return issues;
	}

}
