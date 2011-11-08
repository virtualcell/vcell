/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome.publish;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.sbpax.schemas.UOMEList;
import org.sbpax.util.StringUtil;
import org.vcell.sybil.util.comparator.ComparatorByList;
import org.vcell.uome.UOMEObjectPools;
import org.vcell.uome.UOMEParser;
import org.vcell.uome.UOMETextUtil;
import org.vcell.uome.UOMEUnitLists;
import org.vcell.uome.UOMEUnitPool;
import org.vcell.uome.core.UOMEExpression;
import org.vcell.uome.core.UOMEUnit;
import org.vcell.uome.normalform.UOMENormalFormFactory;
import org.vcell.uome.normalform.UOMENormalFormPool;

public class UOMEListHTMLProducer {
	
	public static String symbolWithLocalLink(UOMEUnit unit, UOMEUnitPool pool) {
		String string = UOMETextUtil.buildSymbol(unit);
		Resource primaryResource = pool.getPrimaryResource(unit);
		if(primaryResource instanceof URI) {
			URI uri = (URI) primaryResource;
			string = "<a href=\"#" + uri.getLocalName() + "\">" + string + "</a>";
		}
		return string;
	}
	
	public static void println(String string) {
		System.out.println(string);
	}
	
	public static String prefixOneIfNonDigitStart(String expression) {
		if(StringUtil.notEmpty(expression) && !Character.isDigit(expression.charAt(0))) {
			return "1 " + expression;
		}
		return expression;
	}
	
	public static void main(String[] args) {
		UOMEObjectPools pools = new UOMEObjectPools();
		UOMEParser.parseUOME(UOMEList.schema, pools);
		Map<URI, UOMEUnit> unitDir = new TreeMap<URI, UOMEUnit>(new URIComparator());
		UOMEUnitPool unitPool = pools.getUnitPool();
		for(UOMEUnit unit : unitPool.getObjects()) {
			Resource primaryResource = unitPool.getPrimaryResource(unit);
			if(primaryResource instanceof URI) {				
				unitDir.put((URI) primaryResource, unit);
			}
		}
		println("[%- META title = \"Pre-Defined List of UOME Units (UOME List)\" -%]");
		println("[%- META shorttitle = \"UOME List\" -%]"); 
		println("[%- META id = \"uomeList\" -%]");
		println("");
		println("<h2>Pre-Defined List of UOME Units (UOME List)</h2>");
		println("");
		println("Here is the list of pre-defined UOME units.");
		println("We aim to include every unit in common use.");
		println("To request a unit, notify the <a href=\"../workgroup.html\">SBPAX Workgroup</a>");
		println("or <a href=\"mailto:curoli@gmail.com\">Oliver Ruebenacker</a>.");
		println("");

		for(URI uri : unitDir.keySet()) {
			UOMEUnit unit = unitDir.get(uri);
			String nameString = StringUtil.concat(unit.getNames(), ", ");
			if(StringUtil.isEmpty(nameString)) { nameString = "unnamed unit"; }
			String symbolString = StringUtil.concat(unit.getSymbols(), ", ");
			if(StringUtil.isEmpty(symbolString)) { symbolString = "no symbol"; }
			println("");
			println("<h3 id=\"" + uri.getLocalName() + "\"> Unit: " + 
					nameString + " (" + symbolString + ") </h3>");
			println("<p><strong>URI: <a href=\"" + uri + "\">" + uri + "</a></strong></p>");
			List<String> termStrings = new ArrayList<String>();
			for(String name : unit.getNames()) {
				termStrings.add(prefixOneIfNonDigitStart(name));
			}
			for(String symbol : unit.getSymbols()) {
				if(StringUtil.notEmpty(symbol)) {
					termStrings.add(prefixOneIfNonDigitStart(symbol));
				}
			}
			for(UOMEExpression expression : unit.getExpressions()) {
				List<String> baseUnitStrings = new ArrayList<String>();
				for(UOMEUnit baseUnit : expression.getUnits()) {
					baseUnitStrings.add(symbolWithLocalLink(baseUnit, unitPool));
				}
				termStrings.add(prefixOneIfNonDigitStart(expression.buildString(baseUnitStrings)));
			}
			List<UOMEUnit> baseUnitList = UOMEUnitLists.getUnitList(UOMEList.siBaseUnits, unitPool);
			Set<UOMEUnit> unitsEqualOne = UOMEUnitLists.getUnits(UOMEList.unitsEqualOne, unitPool);
			ComparatorByList<UOMEUnit> unitComparator = new ComparatorByList<UOMEUnit>(baseUnitList);
			UOMENormalFormFactory factory = new UOMENormalFormFactory(unitsEqualOne, unitComparator);
			UOMENormalFormPool formPool = new UOMENormalFormPool(baseUnitList, unitsEqualOne, factory);
			for(UOMEExpression expression : formPool.findNormalForms(unit)) {
				List<String> baseUnitStrings = new ArrayList<String>();
				for(UOMEUnit baseUnit : expression.getUnits()) {
					baseUnitStrings.add(symbolWithLocalLink(baseUnit, unitPool));
				}
				String termString = prefixOneIfNonDigitStart(expression.buildString(baseUnitStrings));
				if(!termStrings.contains(termString)) { termStrings.add(termString); }
			}
			String comment = "";
			if(unit.getExpressions().isEmpty()) { comment = " (basic unit - no derivation)"; }
			println("<p><strong>Definition:</strong> " + StringUtil.concat(termStrings, " = ")
					+ comment + "</p>");
		}
		println("");
		println("To request a unit, notify the <a href=\"../workgroup.html\">SBPAX Workgroup</a>");
		println("or <a href=\"mailto:curoli@gmail.com\">Oliver Ruebenacker</a>.");
		println("");

	}

}
