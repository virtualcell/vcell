/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import cbit.vcell.mapping.MolecularInternalLinkSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SiteAttributesSpec;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Coordinate;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.mapping.SpeciesContextSpec;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;

import java.util.*;

/**
 * This type was created in VisualAge.
 */
public class SpeciesContextSpecTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_speciescontextspec";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] scspec_table_constraints =
		new String[] {
		"initcndcnstr CHECK (not (initCondExp is null and initCondCountExp is null)  and  not (initCondExp is not null and initCondCountExp is not null))"};

	public final Field specContextRef	= new Field("specContextRef",SQLDataType.integer,		"NOT NULL "+SpeciesContextModelTable.REF_TYPE);
	public final Field simContextRef	= new Field("simContextRef",SQLDataType.integer,		"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field bEnableDif		= new Field("bEnableDif",	SQLDataType.integer,		"NOT NULL");
	public final Field bForceConst		= new Field("bForceConst",	SQLDataType.integer,		"NOT NULL");
	public final Field bForceIndep		= new Field("bForceIndep",	SQLDataType.integer,		"NOT NULL");
	public final Field initCondExp		= new Field("initCondExp",	SQLDataType.varchar_2048,	"");
	public final Field diffRateExp		= new Field("diffRateExp",	SQLDataType.varchar_1024, 	"NOT NULL");
	public final Field boundaryXmExp	= new Field("boundaryXmExp",SQLDataType.varchar_255,	"");
	public final Field boundaryXpExp	= new Field("boundaryXpExp",SQLDataType.varchar_255,	"");
	public final Field boundaryYmExp	= new Field("boundaryYmExp",SQLDataType.varchar_255,	"");
	public final Field boundaryYpExp	= new Field("boundaryYpExp",SQLDataType.varchar_255,	"");
	public final Field boundaryZmExp	= new Field("boundaryZmExp",SQLDataType.varchar_255,	"");
	public final Field boundaryZpExp	= new Field("boundaryZpExp",SQLDataType.varchar_255,	"");
	public final Field initCondCountExp	= new Field("initCondCountExp",	SQLDataType.varchar_1024,"");
	public final Field velocityXExp		= new Field("velocityXExp",	SQLDataType.varchar_1024,	"");
	public final Field velocityYExp		= new Field("velocityYExp",	SQLDataType.varchar_1024,	"");
	public final Field velocityZExp		= new Field("velocityZExp",	SQLDataType.varchar_1024,	"");
	public final Field bWellMixed		= new Field("bWellMixed",	SQLDataType.integer,		"");
	public final Field bForceContinuous	= new Field("bForceContinuous",	SQLDataType.integer,	"");
	public final Field internalLinks	= new Field("internalLinks",	SQLDataType.varchar_4000,		"");
	public final Field siteAttributesSpecs	= new Field("siteAttributesSpecs",SQLDataType.varchar_4000,	"");

	private final Field fields[] = {specContextRef,simContextRef,bEnableDif,bForceConst,bForceIndep,initCondExp,diffRateExp,
											boundaryXmExp,boundaryXpExp,boundaryYmExp,boundaryYpExp,boundaryZmExp,boundaryZpExp,initCondCountExp,
											velocityXExp, velocityYExp, velocityZExp, bWellMixed, bForceContinuous, internalLinks, siteAttributesSpecs};
	
	public static final SpeciesContextSpecTable table = new SpeciesContextSpecTable();
/**
 * ModelTable constructor comment.
 */
private SpeciesContextSpecTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param Key KeyValue
 */
public String getSQLValueList(KeyValue Key, KeyValue simContextKey, SpeciesContextSpec speciesContextSpec, KeyValue scKey) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Key + ",");
	buffer.append(scKey + ",");
	buffer.append(simContextKey + ",");
	
	boolean bForceIndependent = false;

	boolean bEnableDiffusing = true; // speciesContextSpec.isEnableDiffusing()
	buffer.append((bEnableDiffusing ? 1 : 0) + ",");
	buffer.append((speciesContextSpec.isConstant() ? 1 : 0) + ",");
	buffer.append((bForceIndependent ? 1 : 0) + ",");

	if(speciesContextSpec.getInitialConditionParameter().getRole() == SpeciesContextSpec.ROLE_InitialConcentration){
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getInitialConditionParameter().getExpression().infix()) + "'" + ",");		
	}else{
		buffer.append("NULL" + ",");				
	}
	buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getDiffusionParameter().getExpression().infix()) + "'" + ",");

	if (speciesContextSpec.getBoundaryXmParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryXmParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryXpParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryXpParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryYmParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryYmParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryYpParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryYpParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryZmParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryZmParameter().getExpression().infix()) + "'" + ",");
	}

	if (speciesContextSpec.getBoundaryZpParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getBoundaryZpParameter().getExpression().infix()) + "'" + ",");
	}

	if(speciesContextSpec.getInitialConditionParameter().getRole() == SpeciesContextSpec.ROLE_InitialCount){
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getInitialConditionParameter().getExpression().infix()) + "'" + ",");		
	}else{
		buffer.append(" NULL " + ",");
	}
	
	// for velocity x,y,z terms
	if (speciesContextSpec.getVelocityXParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getVelocityXParameter().getExpression().infix()) + "'" + ",");
	}
	if (speciesContextSpec.getVelocityYParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getVelocityYParameter().getExpression().infix()) + "'" + ",");
	}
	if (speciesContextSpec.getVelocityZParameter().getExpression() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append("'" + TokenMangler.getSQLEscapedString(speciesContextSpec.getVelocityZParameter().getExpression().infix()) + "'" + ",");
	}
	if (speciesContextSpec.isWellMixed() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append((speciesContextSpec.isWellMixed() ? 1 : 0) + ",");
	}
	if (speciesContextSpec.isForceContinuous() == null){
		buffer.append(" NULL " + ",");
	}else{
		buffer.append((speciesContextSpec.isForceContinuous() ? 1 : 0) + ",");
	}
	if(speciesContextSpec.getInternalLinkSet() == null || speciesContextSpec.getInternalLinkSet().size() == 0) {
		buffer.append(" NULL " + ",");
	} else {
		buffer.append("'" + getInternalLinksSQL(speciesContextSpec) + "'" + ",");
	}
	if(speciesContextSpec.getSiteAttributesMap() == null || speciesContextSpec.getSiteAttributesMap().size() == 0) {
		buffer.append(" NULL " + ")");
	} else {
		buffer.append("'" + getSiteAttributesSQL(speciesContextSpec) + "'" + ")");
	}
	return buffer.toString();
}

	static String getSiteAttributesSQL(SpeciesContextSpec scs) {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<MolecularComponentPattern, SiteAttributesSpec> entry : scs.getSiteAttributesMap().entrySet()) {
			SiteAttributesSpec sas = entry.getValue();
			sb.append(sas.getMolecularComponentPattern().getMolecularComponent().getName() + ",");
			sb.append(sas.getLocation().getName() + ",");
			sb.append(sas.getRadius() + ",");
			sb.append(sas.getDiffusionRate() +",");
			sb.append(sas.getCoordinate().getX() + ",");
			sb.append(sas.getCoordinate().getY() + ",");
			sb.append(sas.getCoordinate().getZ() + ",");
			sb.append(sas.getColor().getName());
			sb.append(";");
		}
		return sb.toString();
	}

	static Map<MolecularComponentPattern, SiteAttributesSpec> readSiteAttributesSQL(SpeciesContextSpec scs, String siteAttributesMapString) {
		Map<MolecularComponentPattern, SiteAttributesSpec> saMap = new LinkedHashMap<>();
		if(siteAttributesMapString.isEmpty()) {
			return saMap;
		}
		StringTokenizer sat = new StringTokenizer(siteAttributesMapString, ";");
		if(sat.countTokens() == 0) {
			return saMap;
		}

		SpeciesContext sc = scs.getSpeciesContext();
		SpeciesPattern sp = sc.getSpeciesPattern();
		List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
		if(mtpList.size() != 1) {
			throw new RuntimeException("Exactly one MolecularTypePattern expected");
		}
		MolecularTypePattern mtp = mtpList.get(0);

		while(sat.hasMoreTokens()) {
			String saString = sat.nextToken();
			StringTokenizer tokenizer = new StringTokenizer(saString, ",");
			if(tokenizer.countTokens() != 8) {
				return saMap;
			}
			while(tokenizer.hasMoreTokens()) {
				String attribute = tokenizer.nextToken();
				MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(attribute);
				attribute = tokenizer.nextToken();
				Structure structure = scs.getSimulationContext().getModel().getStructure(attribute);
				attribute = tokenizer.nextToken();
				double radius = Double.parseDouble(attribute);
				attribute = tokenizer.nextToken();
				double diffRate = Double.parseDouble(attribute);
				attribute = tokenizer.nextToken();
				double x = Double.parseDouble(attribute);
				attribute = tokenizer.nextToken();
				double y = Double.parseDouble(attribute);
				attribute = tokenizer.nextToken();
				double z = Double.parseDouble(attribute);
				Coordinate coordinate = new Coordinate(x,y,z);
				attribute = tokenizer.nextToken();
				NamedColor color = Colors.getColorByName(attribute);
				SiteAttributesSpec sas = new SiteAttributesSpec(scs, mcp, radius, diffRate, structure, coordinate, color);
				saMap.put(mcp, sas);
			}
		}
		return saMap;	// may be empty but not null
	}
	static String getInternalLinksSQL(SpeciesContextSpec scs) {
		StringBuffer sb = new StringBuffer();
		for( MolecularInternalLinkSpec mils : scs.internalLinkSet) {
			SiteAttributesSpec sas1 = mils.getSite1();
			SiteAttributesSpec sas2 = mils.getSite2();
			sb.append(sas1.getMolecularComponentPattern().getMolecularComponent().getName() + ",");
			sb.append(sas2.getMolecularComponentPattern().getMolecularComponent().getName() + ";");
		}
		return sb.toString();
	}
	static Set<MolecularInternalLinkSpec> readInternalLinksSQL(SpeciesContextSpec scs, String internalLinkSetString) {
		Set<MolecularInternalLinkSpec> ilSet = new LinkedHashSet<>();

		if(internalLinkSetString.isEmpty()) {
			return ilSet;
		}
		StringTokenizer sat = new StringTokenizer(internalLinkSetString, ";");
		if(sat.countTokens() == 0) {
			return ilSet;
		}

		SpeciesContext sc = scs.getSpeciesContext();
		SpeciesPattern sp = sc.getSpeciesPattern();
		List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
		if(mtpList.size() != 1) {
			throw new RuntimeException("Exactly one MolecularTypePattern expected");
		}
		MolecularTypePattern mtp = mtpList.get(0);

		while(sat.hasMoreTokens()) {
			String saString = sat.nextToken();
			StringTokenizer tokenizer = new StringTokenizer(saString, ",");
			if (tokenizer.countTokens() != 2) {
				return ilSet;
			}
			while (tokenizer.hasMoreTokens()) {
				String attribute = tokenizer.nextToken();
				MolecularComponentPattern mcp1 = mtp.getMolecularComponentPattern(attribute);
				attribute = tokenizer.nextToken();
				MolecularComponentPattern mcp2 = mtp.getMolecularComponentPattern(attribute);

				MolecularInternalLinkSpec ils = new MolecularInternalLinkSpec(scs, mcp1, mcp2);
				ilSet.add(ils);
			}
		}
		return ilSet;	// may be empty but never null
	}

}
