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
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import org.vcell.db.DatabaseSyntax;
import org.vcell.sbml.vcell.StructureSizeSolver;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.QueryHashtable;
import cbit.sql.RecordChangedException;
import cbit.sql.Table;
import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.mapping.CurrentDensityClampStimulus;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.Electrode;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.IllegalMappingException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.TotalCurrentClampStimulus;
import cbit.vcell.mapping.VoltageClampStimulus;
import cbit.vcell.math.BoundaryConditionType;
import cbit.vcell.math.MathException;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.modeldb.DatabasePolicySQL.OuterJoin;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
/**
 * This type was created in VisualAge.
 */
public class SimulationContextDbDriver extends DbDriver {
	public static final SimContextTable simContextTable = SimContextTable.table;
	public static final StructureMappingTable structureMappingTable = StructureMappingTable.table;
	public static final SpeciesContextSpecTable speciesContextSpecTable = SpeciesContextSpecTable.table;
	public static final ReactionSpecTable reactionSpecTable = ReactionSpecTable.table;
	public static final UserTable userTable = UserTable.table;
	public static final StimulusTable stimulusTable = StimulusTable.table;
	private GeomDbDriver geomDB = null;
	private ModelDbDriver modelDB = null;
	private MathDescriptionDbDriver mathDescDB = null;

/**
 * SimContextDbDriver constructor comment.
 * @param connectionFactory cbit.sql.ConnectionFactory
 * @param sessionLog cbit.vcell.server.SessionLog
 */
public SimulationContextDbDriver(GeomDbDriver argGeomDB,ModelDbDriver argModelDB,
		MathDescriptionDbDriver argMathDescDB) {
	super(argGeomDB.dbSyntax, argGeomDB.keyFactory);
	this.geomDB = argGeomDB;
	this.modelDB = argModelDB;
	this.mathDescDB = argMathDescDB;
}


/**
 * This method was created in VisualAge.
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignAnalysisTasksSQL(Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	AnalysisTaskXMLTable analysisTaskTable = AnalysisTaskXMLTable.table;
	
	sql = 	" SELECT " + analysisTaskTable.analysisTaskXML.toString() + 
			" FROM " + analysisTaskTable.getTableName() + 
			" WHERE " + analysisTaskTable.simContextRef + " = " + simContextKey +
			" ORDER BY " + analysisTaskTable.insertDate; // order-by to maintain order ... not really necessary
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			String analysisXML = (String)getLOB(rset,analysisTaskTable.analysisTaskXML,dbSyntax);
			org.jdom.Element rootElement = XmlUtil.stringToXML(analysisXML,null).getRootElement();
			cbit.vcell.modelopt.ParameterEstimationTask parameterEstimationTask = cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence.getParameterEstimationTask(rootElement,simContext);
			simContext.addAnalysisTask(parameterEstimationTask);
		}
	}catch (MathException | MappingException | PropertyVetoException | ExpressionException e){
		lg.error(e.getMessage(),e);
		throw new DataAccessException(e.getMessage());
	} finally {
		if(stmt != null){
			stmt.close();
		}
	}
}


/**
 * This method was created in VisualAge.
 * @param simContextKey cbit.sql.KeyValue
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignReactionSpecsSQL(Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	sql = 	" SELECT " + "*" + 
			" FROM " + reactionSpecTable.getTableName() + 
			" WHERE " + reactionSpecTable.simContextRef + " = " + simContextKey +
			" ORDER BY " + reactionSpecTable.id;
//System.out.println("SimulationContextDbDriver.assignReactionSpecsSQL(), sql = "+sql);
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			KeyValue reactionStepRef = new KeyValue(rset.getBigDecimal(reactionSpecTable.reactionStepRef.toString()));
			int mapping = rset.getInt(reactionSpecTable.mapping.toString());

			//
			ReactionSpec[] reactionSpecs = simContext.getReactionContext().getReactionSpecs();
			for (int i=0;i<reactionSpecs.length;i++){
				if (reactionSpecs[i].getReactionStep().getKey().compareEqual(reactionStepRef)) {
					try {
						reactionSpecs[i].setReactionMapping(mapping);
					} catch (Exception e) {
						throw new DataAccessException("Error setting ReactionSpec 'mapping' for SimulationContext:"+simContext.getVersion().getName()+" id="+simContextKey);
					}
					break;
				}
			}
		}
	} finally {
		stmt.close();
	}
}


/**
 * This method was created in VisualAge.
 * @param simContextKey cbit.sql.KeyValue
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignSpeciesContextSpecsSQL(Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	sql = 	" SELECT " + "*" + 
			" FROM " + speciesContextSpecTable.getTableName() + 
			" WHERE " + speciesContextSpecTable.simContextRef + " = " + simContextKey +
			" ORDER BY " + speciesContextSpecTable.id;
	Statement stmt = con.createStatement();
	Boolean bUseConcentration = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			KeyValue speciesContextRef = new KeyValue(rset.getBigDecimal(speciesContextSpecTable.specContextRef.toString()));
			//KeyValue simContextRef = new KeyValue(rset.getBigDecimal(speciesContextSpecTable.simContextRef.toString()));
			boolean bEnableDiffusing = rset.getBoolean(speciesContextSpecTable.bEnableDif.toString());
			boolean bForceConstant = rset.getBoolean(speciesContextSpecTable.bForceConst.toString());
			//boolean bForceIndep = rset.getBoolean(speciesContextSpecTable.bForceIndep.toString());
			String initCondConcExpS = null;
			String initCondCountExpS = null;
			initCondConcExpS = rset.getString(speciesContextSpecTable.initCondExp.toString());
			if(rset.wasNull()){
				initCondConcExpS = null;
				initCondCountExpS = rset.getString(speciesContextSpecTable.initCondCountExp.toString());
				if(rset.wasNull()){
					throw new DataAccessException("Not expecting both initialCondition expressions types to be NULL");
				}
				if(bUseConcentration == null){
					bUseConcentration = false;
				}else if (bUseConcentration){
					throw new DataAccessException("Not expecting both Concentration and Count in initialConditions");
				}
			}else{
				if(bUseConcentration == null){
					bUseConcentration = true;
				}else if (!bUseConcentration){
					throw new DataAccessException("Not expecting both Concentration and Count in initialConditions");
				}
			}
			String diffRateString = rset.getString(speciesContextSpecTable.diffRateExp.toString());
			String boundaryXmString = rset.getString(speciesContextSpecTable.boundaryXmExp.toString());
			if (rset.wasNull()){
				boundaryXmString = null;
			}
			
			String boundaryXpString = rset.getString(speciesContextSpecTable.boundaryXpExp.toString());
			if (rset.wasNull()){
				boundaryXpString = null;
			}
			
			String boundaryYmString = rset.getString(speciesContextSpecTable.boundaryYmExp.toString());
			if (rset.wasNull()){
				boundaryYmString = null;
			}
			
			String boundaryYpString = rset.getString(speciesContextSpecTable.boundaryYpExp.toString());
			if (rset.wasNull()){
				boundaryYpString = null;
			}
			
			String boundaryZmString = rset.getString(speciesContextSpecTable.boundaryZmExp.toString());
			if (rset.wasNull()){
				boundaryZmString = null;
			}
			
			String boundaryZpString = rset.getString(speciesContextSpecTable.boundaryZpExp.toString());
			if (rset.wasNull()){
				boundaryZpString = null;
			}

			String velocityXString = rset.getString(speciesContextSpecTable.velocityXExp.toString());
			if (rset.wasNull()){
				velocityXString = null;
			}
			
			String velocityYString = rset.getString(speciesContextSpecTable.velocityYExp.toString());
			if (rset.wasNull()){
				velocityYString = null;
			}

			String velocityZString = rset.getString(speciesContextSpecTable.velocityZExp.toString());
			if (rset.wasNull()){
				velocityZString = null;
			}

			String wellMixedString = rset.getString(speciesContextSpecTable.bWellMixed.toString());
			if (rset.wasNull()){
				wellMixedString = null;
			}

			String forceContinuousString = rset.getString(speciesContextSpecTable.bForceContinuous.toString());
			if (rset.wasNull()){
				forceContinuousString = null;
			}

			//
			SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
			for (int i=0;i<speciesContextSpecs.length;i++){
				SpeciesContextSpec scs = speciesContextSpecs[i];
				if (scs.getSpeciesContext().getKey().compareEqual(speciesContextRef)) {
					try {
						//scs.setEnableDiffusing(bEnableDiffusing);
						scs.setConstant(bForceConstant);
						if(initCondConcExpS != null){
							scs.getInitialConcentrationParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(initCondConcExpS)));	
							scs.getInitialCountParameter().setExpression(null);
						}else {
							scs.getInitialCountParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(initCondCountExpS)));
							scs.getInitialConcentrationParameter().setExpression(null);
						}
						scs.getDiffusionParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(diffRateString)));
						if (boundaryXmString!=null){
							scs.getBoundaryXmParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(boundaryXmString)));
						}else{
							scs.getBoundaryXmParameter().setExpression(null);
						}
						if (boundaryXpString!=null){
							scs.getBoundaryXpParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(boundaryXpString)));
						}else{
							scs.getBoundaryXpParameter().setExpression(null);
						}
						if (boundaryYmString!=null){
							scs.getBoundaryYmParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(boundaryYmString)));
						}else{
							scs.getBoundaryYmParameter().setExpression(null);
						}
						if (boundaryYpString!=null){
							scs.getBoundaryYpParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(boundaryYpString)));
						}else{
							scs.getBoundaryYpParameter().setExpression(null);
						}
						if (boundaryZmString!=null){
							scs.getBoundaryZmParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(boundaryZmString)));
						}else{
							scs.getBoundaryZmParameter().setExpression(null);
						}
						if (boundaryZpString!=null){
							scs.getBoundaryZpParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(boundaryZpString)));
						}else{
							scs.getBoundaryZpParameter().setExpression(null);
						}
						if (velocityXString!=null){
							scs.getVelocityXParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(velocityXString)));
						}else{
							scs.getVelocityXParameter().setExpression(null);
						}
						if (velocityYString!=null){
							scs.getVelocityYParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(velocityYString)));
						}else{
							scs.getVelocityYParameter().setExpression(null);
						}
						if (velocityZString!=null){
							scs.getVelocityZParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(velocityZString)));
						}else{
							scs.getVelocityZParameter().setExpression(null);
						}
						if (wellMixedString!=null){
							int value = Integer.parseInt(wellMixedString);
							if (value!=0 && value!=1){
								throw new DataAccessException("unexpected value for bSpatial column in SimulationCOntextDbDriver: \""+wellMixedString+"\", expecting 0 or 1");
							}
							boolean bWellMixed = (value==1)?true:false;
							scs.setWellMixed(bWellMixed);
						}
						if (forceContinuousString!=null){
							int value = Integer.parseInt(forceContinuousString);
							if (value!=0 && value!=1){
								throw new DataAccessException("unexpected value for bForceContinuous column in SimulationCOntextDbDriver: \""+forceContinuousString+"\", expecting 0 or 1");
							}
							boolean bForceContinuous = (value==1)?true:false;
							scs.setForceContinuous(bForceContinuous);
						}
					} catch (Exception e) {
						throw new DataAccessException("Error setting SpeciesContextSpec info for SimulationContext:"+simContext.getVersion().getName()+" id="+simContextKey);
					}
				break;
				}
			}
		}
	} finally {
		stmt.close();
	}
	if(bUseConcentration != null){
		try {
			simContext.setUsingConcentration(bUseConcentration, false);
		} catch (Exception e) {
			throw new RuntimeException("not expected to ever fail: "+e.getMessage(), e);
		}
	}
}


/**
 * This method was created in VisualAge.
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignStimuliSQL(Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	sql = 	" SELECT " + "*" + 
			" FROM " + stimulusTable.getTableName() + 
			" WHERE " + stimulusTable.simContextRef + " = " + simContextKey;
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			try {
				//KeyValue key = new KeyValue(rset.getBigDecimal(stimulusTable.id.toString()));			
				KeyValue structureRef = new KeyValue(rset.getBigDecimal(stimulusTable.structRef.toString()));			
				//KeyValue simContextRef = new KeyValue(rset.getBigDecimal(stimulusTable.simContextRef.toString()));			
				//
				// lookup structure from SimulationContext by its key
				//
				// DBCache will not always give same instance consistently (usually this is
				// fixed up later in the ReferenceResolver at the Client).
				//
				Structure theStructure = null;
				Structure structureArray[] = simContext.getModel().getStructures();
				for (int i=0;i<structureArray.length;i++){
					Structure structure = structureArray[i];
					if (structure.getKey().compareEqual(structureRef)) {
						theStructure = structure;
						break;
					}
				}
				String name = rset.getString(stimulusTable.name.toString());			
				if (rset.wasNull()){
					name = null;
				}
				int stimulusType = rset.getInt(stimulusTable.stimulusType.toString());
				Expression exp = null;
				String expString = rset.getString(stimulusTable.expression.toString());
				if (rset.wasNull()){
					exp = null;
				}else{
					exp = new Expression(TokenMangler.getSQLRestoredString(expString));
				}
				double posX = rset.getBigDecimal(stimulusTable.positionX.toString()).doubleValue();
				double posY = rset.getBigDecimal(stimulusTable.positionY.toString()).doubleValue();
				double posZ = rset.getBigDecimal(stimulusTable.positionZ.toString()).doubleValue();

				org.vcell.util.CommentStringTokenizer paramsCST = null;
				String paramsS = rset.getString(StimulusTable.table.params.toString());
				if(!rset.wasNull()){
					paramsCST =
						new org.vcell.util.CommentStringTokenizer(
							org.vcell.util.TokenMangler.getSQLRestoredString(paramsS)
						);
				}
				
				if (stimulusType == StimulusTable.GROUND_ELECTRODE){
					//
					// an Electrode (ground)
					//
					Electrode groundElectrode = new Electrode((Feature)theStructure,new org.vcell.util.Coordinate(posX,posY,posZ));
					simContext.setGroundElectrode(groundElectrode);
				}else if (stimulusType == StimulusTable.TOTALCURRENT_CLAMP_STIMULUS){
					Electrode electrode = new Electrode((Feature)theStructure,new org.vcell.util.Coordinate(posX,posY,posZ));
					TotalCurrentClampStimulus stimulus = new TotalCurrentClampStimulus(electrode,name,exp,simContext);
					stimulus.parameterVCMLSet(paramsCST);
					ElectricalStimulus newStimuli[] = (ElectricalStimulus[])org.vcell.util.BeanUtils.addElement(simContext.getElectricalStimuli(),stimulus);
					simContext.setElectricalStimuli(newStimuli);
				}else if (stimulusType == StimulusTable.CURRENTDENSITY_CLAMP_STIMULUS){
					Electrode electrode = new Electrode((Feature)theStructure,new org.vcell.util.Coordinate(posX,posY,posZ));
					CurrentDensityClampStimulus stimulus = new CurrentDensityClampStimulus(electrode,name,exp,simContext);
					stimulus.parameterVCMLSet(paramsCST);
					ElectricalStimulus newStimuli[] = (ElectricalStimulus[])org.vcell.util.BeanUtils.addElement(simContext.getElectricalStimuli(),stimulus);
					simContext.setElectricalStimuli(newStimuli);
				}else if (stimulusType == StimulusTable.VOLTAGE_CLAMP_STIMULUS){
					Electrode electrode = new Electrode((Feature)theStructure,new org.vcell.util.Coordinate(posX,posY,posZ));
					VoltageClampStimulus stimulus = new VoltageClampStimulus(electrode,name,exp,simContext);
					stimulus.parameterVCMLSet(paramsCST);
					ElectricalStimulus newStimuli[] = (ElectricalStimulus[])org.vcell.util.BeanUtils.addElement(simContext.getElectricalStimuli(),stimulus);
					simContext.setElectricalStimuli(newStimuli);
				}else{
					throw new DataAccessException("unknown stimulusType <"+stimulusType+">");
				}
				
			}catch (ExpressionException | PropertyVetoException e){
				lg.error(e.getMessage(),e);
			}
		}
	} finally {
		stmt.close();
	}
}


/**
 * This method was created in VisualAge.
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void assignStructureMappingsSQL(QueryHashtable dbc, Connection con,KeyValue simContextKey, SimulationContext simContext) throws SQLException, DataAccessException {
	String sql;
	sql = 	" SELECT " + "*" + 
			" FROM " + structureMappingTable.getTableName() + 
			" WHERE " + structureMappingTable.simContextRef + " = " + simContextKey;
	Statement stmt = con.createStatement();
	try {
//log.print(sql);
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			BigDecimal subvolumeRefBigDecimal = rset.getBigDecimal(structureMappingTable.subVolumeRef.toString());
			KeyValue subVolumeRef = (subvolumeRefBigDecimal==null?null:new KeyValue(subvolumeRefBigDecimal));
			BigDecimal surfaceClassRefBigDecimal = rset.getBigDecimal(structureMappingTable.surfaceClassRef.toString());
			KeyValue surfaceClassRef = (surfaceClassRefBigDecimal==null?null:new KeyValue(surfaceClassRefBigDecimal));
			KeyValue structureRef = new KeyValue(rset.getBigDecimal(structureMappingTable.structRef.toString()));

			//
			// lookup structure and subVolume from SimulationContext by their keys
			//
			// DBCache will not always give same instance consistently (usually this is
			// fixed up later in the ReferenceResolver at the Client).
			//
			Structure theStructure = null;
			Structure structureArray[] = simContext.getModel().getStructures();
			for (int i=0;i<structureArray.length;i++){
				Structure structure = structureArray[i];
				if (structure.getKey().compareEqual(structureRef)) {
					theStructure = structure;
					break;
				}
			}
			if (theStructure == null) {
				throw new DataAccessException("Can't match structure and subvolume");
			}
			GeometryClass theGeometryClass = null;
			KeyValue geometryClassKey = (subVolumeRef==null?surfaceClassRef:subVolumeRef);
			if(geometryClassKey != null){
				GeometryClass[] geometryClasses = simContext.getGeometry().getGeometryClasses();
				for (int i=0;i<geometryClasses.length;i++){
					if (geometryClasses[i].getKey().compareEqual(geometryClassKey)){
						theGeometryClass = geometryClasses[i];
						break;
					}
				}
				if (theGeometryClass == null) {
					throw new DataAccessException("Can't find Geometryclass");
				}
			}
			
			Expression sizeExpression = null;
			String sizeExpressionS = rset.getString(StructureMappingTable.table.sizeExp.getUnqualifiedColName());
			if(!rset.wasNull() && sizeExpressionS!=null && sizeExpressionS.length()>0){
				try {
					sizeExpressionS = TokenMangler.getSQLRestoredString(sizeExpressionS);
					sizeExpression = new Expression(sizeExpressionS);
				} catch (ExpressionException e) {
					e.printStackTrace();
					throw new DataAccessException("SimulationContextDbDriver.assignStructureMappingSQL : Couldn't parse non-null size expression for Structure "+theStructure.getName());
				}
			}
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(theStructure);
			try {
				sm.getSizeParameter().setExpression(sizeExpression);
			} catch (Exception e1) {
				throw new DataAccessException("SimulationContextDbDriver.assignStructureMappingSQL : Couldn't set size expression '"+sizeExpressionS+"'for Structure "+theStructure.getName());
			}
			try {
				sm.setGeometryClass(theGeometryClass);
			}catch (PropertyVetoException e){
				lg.error(e.getMessage(),e);
				throw new DataAccessException(e.getMessage());
			}
			if (sm instanceof FeatureMapping) {
				FeatureMapping fm = (FeatureMapping) sm;
				String boundaryTypeXmString = rset.getString(structureMappingTable.boundaryTypeXm.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeXm(new BoundaryConditionType(boundaryTypeXmString));
				}
				String boundaryTypeXpString = rset.getString(structureMappingTable.boundaryTypeXp.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeXp(new BoundaryConditionType(boundaryTypeXpString));
				}
				String boundaryTypeYmString = rset.getString(structureMappingTable.boundaryTypeYm.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeYm(new BoundaryConditionType(boundaryTypeYmString));
				}
				String boundaryTypeYpString = rset.getString(structureMappingTable.boundaryTypeYp.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeYp(new BoundaryConditionType(boundaryTypeYpString));
				}
				String boundaryTypeZmString = rset.getString(structureMappingTable.boundaryTypeZm.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeZm(new BoundaryConditionType(boundaryTypeZmString));
				}
				String boundaryTypeZpString = rset.getString(structureMappingTable.boundaryTypeZp.toString());
				if (!rset.wasNull()){
					fm.setBoundaryConditionTypeZp(new BoundaryConditionType(boundaryTypeZpString));
				}
				String volPerUnitArea = rset.getString(structureMappingTable.volPerUnitAreaExp.toString());
				if (!rset.wasNull()) {
					try {
						fm.getVolumePerUnitAreaParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(volPerUnitArea)));
					} catch (ExpressionException e) {
						throw new DataAccessException("parse error in surfaceToVol expression: " + e.getMessage(), e);
					}
				}
				String volPerUnitVol = rset.getString(structureMappingTable.volPerUnitVolExp.toString());
				if (!rset.wasNull()) {
					try {
						fm.getVolumePerUnitVolumeParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(volPerUnitVol)));
					} catch (ExpressionException e) {
						throw new DataAccessException("parse error in surfaceToVol expression: " + e.getMessage(), e);
					}
				}
			} else if (sm instanceof MembraneMapping) {
				MembraneMapping mm = (MembraneMapping) sm;
				String surfToVolString = rset.getString(structureMappingTable.surfToVolExp.toString());
				if (!rset.wasNull()) {
					try {
						mm.getSurfaceToVolumeParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(surfToVolString)));
					} catch (ExpressionException e) {
						throw new DataAccessException("parse error in surfaceToVol expression: " + e.getMessage(), e);
					}
				}
				String volFractString = rset.getString(structureMappingTable.volFractExp.toString());
				if (!rset.wasNull()) {
					try {
						mm.getVolumeFractionParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(volFractString)));
					} catch (ExpressionException e) {
						throw new DataAccessException("parse error in volFract expression: " + e.getMessage(), e);
					}
				}
				boolean bCalculateVoltage = rset.getBoolean(structureMappingTable.bCalculateVoltage.toString());
				if (!rset.wasNull()) {
					mm.setCalculateVoltage(bCalculateVoltage);
				}
				java.math.BigDecimal specificCapacitance = rset.getBigDecimal(structureMappingTable.specificCap.toString());
				if (!rset.wasNull()) {
					try {
						mm.getSpecificCapacitanceParameter().setExpression(new Expression(specificCapacitance.doubleValue()));
					}catch (ExpressionBindingException e){
						throw new DataAccessException("error setting membrane specific capacitance: "+e.getMessage(), e);
					}
				}
				String initialVoltageString = rset.getString(structureMappingTable.initialVoltage.toString());
				if (!rset.wasNull()) {
					try {
						mm.getInitialVoltageParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(initialVoltageString)));
					}catch (ExpressionException e){
						throw new DataAccessException("database parse error in initial membrane voltage: "+e.getMessage(), e);
					}
				}
				String areaPerUnitArea = rset.getString(structureMappingTable.areaPerUnitAreaExp.toString());
				if (!rset.wasNull()) {
					try {
						mm.getAreaPerUnitAreaParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(areaPerUnitArea)));
					} catch (ExpressionException e) {
						throw new DataAccessException("parse error in surfaceToVol expression: " + e.getMessage(), e);
					}
				}
				String areaPerUnitVol = rset.getString(structureMappingTable.areaPerUnitVolExp.toString());
				if (!rset.wasNull()) {
					try {
						mm.getAreaPerUnitVolumeParameter().setExpression(new Expression(TokenMangler.getSQLRestoredString(areaPerUnitVol)));
					} catch (ExpressionException e) {
						throw new DataAccessException("parse error in surfaceToVol expression: " + e.getMessage(), e);
					}
				}
			} else {
				throw new DataAccessException("unknown structureMapping type");
			}
//System.out.println("Structure Key = " + theStructure + " - " + "SubVolume Key " + theSubVolume.getKey());
		}
	} finally {
		stmt.close();
	}
}


/**
 * removeModel method comment.
 */
private void deleteSimContextSQL(Connection con,User user, KeyValue simContextKey) throws SQLException, PermissionException, DataAccessException, DependencyException {
	//log.print("deleteSimContextSQL(user=" + user + ", simContextKey=" + simContextKey + ")");

	//
	// check for external references (from BioModel)
	//
	failOnExternalRefs(con, BioModelSimContextLinkTable.table.simContextRef, BioModelSimContextLinkTable.table, simContextKey,SimContextTable.table);

	
	KeyValue mathKey = getDeletableMathKeyFromSimContext(con,user,simContextKey);
	
	//
	// delete SimulationContext (Model and Geometry link tables are ON DELETE CASCADE)
	//
	String sql;
	sql = DatabasePolicySQL.enforceOwnershipDelete(user,simContextTable,simContextTable.id.getQualifiedColName() + " = " + simContextKey);
	updateCleanSQL(con, sql);

	//
	// delete MathDescription that was owned by the SimulationContext (note: any Simulations must be deleted first)
	//
	try {
		mathDescDB.deleteVersionable(con,user,VersionableType.MathDescription,mathKey);
		if (lg.isTraceEnabled()) {
			lg.trace("SimulationContextDbDriver.delete("+simContextKey+") deletion of MathDescription("+mathKey+") succeeded");
		}
	}catch (PermissionException | DependencyException e){
		if (lg.isWarnEnabled()) {
			lg.warn("SimulationContextDbDriver.delete("+simContextKey+") deletion of MathDescription("+mathKey+") failed: "+e.getMessage());
		}
	}
}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
public void deleteVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
				throws DependencyException, ObjectNotFoundException,
						SQLException,DataAccessException,PermissionException {

	deleteVersionableInit(con, user, vType, vKey);
	if (vType.equals(VersionableType.SimulationContext)){
		deleteSimContextSQL(con, user, vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * getModels method comment.
 */
private KeyValue getDeletableMathKeyFromSimContext(Connection con,User user,KeyValue simContextKey) throws SQLException, DataAccessException {

	KeyValue mathKey = null;
	String sql;
	
	sql = 	" SELECT " + SimContextTable.table.mathRef.getQualifiedColName()  +
			" FROM " + SimContextTable.table.getTableName() + "," + MathDescTable.table.getTableName() +
			" WHERE " + SimContextTable.table.id.getQualifiedColName() + " = " + simContextKey +
			" AND " + SimContextTable.table.mathRef.getQualifiedColName() + " = " + MathDescTable.table.id.getQualifiedColName() +
			" AND " + MathDescTable.table.versionFlag.getQualifiedColName() + " <> " + VersionFlag.Archived.getIntValue() +
			" AND " + MathDescTable.table.ownerRef.getQualifiedColName() + " = " + user.getID();
			
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		if (rset.next()) {
			mathKey = new KeyValue(rset.getBigDecimal(SimContextTable.table.mathRef.getUnqualifiedColName()));
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	return mathKey;
}


/**
 * getModel method comment.
 */
private SimulationContext getSimulationContextSQL(QueryHashtable dbc, Connection con,User user, KeyValue simContextKey/*, ReactStepDbDriver reactStepDB*/) 
						throws SQLException, DataAccessException, IllegalMappingException, PropertyVetoException {
							
	SimulationContext simContext = null;
		
	String sql;			
	Field[] f = {new cbit.sql.StarField(simContextTable),userTable.userid};
	Table[] t = {simContextTable,userTable};
	String condition =	simContextTable.id.getQualifiedColName() + " = " + simContextKey + 
					" AND " + 
						simContextTable.ownerRef.getQualifiedColName() + " = " + userTable.id.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,(OuterJoin)null,condition,null,dbSyntax);
//System.out.println(sql);

	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()) {
			simContext = simContextTable.getSimContext(dbc, con,user,rset, geomDB,modelDB,mathDescDB);
		} else {
			throw new ObjectNotFoundException("SimulationContext id=" + simContextKey + " not found for user '" + user + "'");
		}
	} finally {
		stmt.close();
	}

	DataSymbolTable.table.populateDataSymbols(con, simContextKey, simContext.getDataContext(),user, simContext.getModel().getUnitSystem());
	
	ArrayList<AnnotatedFunction> outputFunctionList = ApplicationMathTable.table.getOutputFunctionsSimcontext(con, simContextKey,dbSyntax);
	if(outputFunctionList != null){
		OutputFunctionContext outputFnContext = simContext.getOutputFunctionContext();
		outputFnContext.setOutputFunctions(outputFunctionList);
	}
	
	SimContextTable.table.readAppComponents(con, simContext,dbSyntax);
	
	assignStimuliSQL(con,simContextKey, simContext);
	assignStructureMappingsSQL(dbc, con,simContextKey, simContext);
	assignSpeciesContextSpecsSQL(con,simContextKey, simContext);
	assignReactionSpecsSQL(con,simContextKey, simContext);
	
	for (GeometryClass gc : simContext.getGeometry().getGeometryClasses()) {
		try {
			StructureSizeSolver.updateUnitStructureSizes(simContext, gc);
		}catch (Exception e){
			lg.error("failed to updateUnitStructureSizes",e);
		}
	}
	simContext.getGeometryContext().enforceHierarchicalBoundaryConditions(simContext.getModel().getStructureTopology());

	simContext.getModel().refreshDependencies();
	assignAnalysisTasksSQL(con,simContextKey, simContext);
	simContext.refreshDependencies();  // really needed to calculate MembraneMapping parameters that are not stored (inside/outside flux correction factors).
	
	return simContext;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public Versionable getVersionable(QueryHashtable dbc, Connection con, User user, VersionableType vType, KeyValue vKey) 
			throws ObjectNotFoundException, SQLException, DataAccessException {
				
	Versionable versionable = (Versionable) dbc.get(vKey);
	if (versionable != null) {
		return versionable;
	} else {
		try {
			if (vType.equals(VersionableType.SimulationContext)){
				versionable = getSimulationContextSQL(dbc, con, user, vKey);
			}else{
				throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
			}
		} catch (IllegalMappingException e) {
			throw new DataAccessException(e.getMessage());
		} catch (PropertyVetoException e) {
			throw new DataAccessException(e.getMessage());
		}
		dbc.put(versionable.getVersion().getVersionKey(),versionable);
	}
	return versionable;
}


/**
 * This method was created in VisualAge.
 */
private void insertAnalysisTasksSQL(Connection con, KeyValue simContextKey, SimulationContext simContext, DatabaseSyntax dbSyntax) throws SQLException, DataAccessException {
	AnalysisTaskXMLTable analysisTaskXMLTable = AnalysisTaskXMLTable.table;
	
	cbit.vcell.modelopt.AnalysisTask[] analysisTasks = simContext.getAnalysisTasks();
	//
	// store analysisTasks
	//
	if (analysisTasks != null) {
		for (int i=0;i<analysisTasks.length;i++){
			cbit.vcell.modelopt.AnalysisTask analysisTask = analysisTasks[i];
			String analysisTaskXML = null;
			if (analysisTask instanceof cbit.vcell.modelopt.ParameterEstimationTask){				
				org.jdom.Element xmlRootElement = cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence.getXML((cbit.vcell.modelopt.ParameterEstimationTask)analysisTask);
				analysisTaskXML = cbit.util.xml.XmlUtil.xmlToString(xmlRootElement);
			}else{
				throw new DataAccessException("can't generate xml for analysisTask type '"+analysisTask.getClass().getName()+"'");
			}

			KeyValue newID = keyFactory.getNewKey(con);
			
			switch(dbSyntax){
			case ORACLE:{
				updateCleanSQL(con,"INSERT INTO "+analysisTaskXMLTable.getTableName()+
					" VALUES (" + newID +","+simContextKey.toString()+",EMPTY_CLOB(),current_timestamp)");
				
				updateCleanLOB(con,analysisTaskXMLTable.id.toString(),newID,analysisTaskXMLTable.getTableName(),analysisTaskXMLTable.analysisTaskXML,analysisTaskXML,dbSyntax);
				break;
			}
			case POSTGRES:{
				updatePreparedCleanSQL(con,"INSERT INTO "+analysisTaskXMLTable.getTableName()+
						" VALUES (" + newID +","+simContextKey.toString()+",?,current_timestamp)",analysisTaskXML);
					
				break;
			}
			default:{
				throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
			}
			}
		}
	}
}


/**
 * This method was created in VisualAge.
 */
private void insertReactionSpecsSQL(Connection con, KeyValue simContextKey, SimulationContext simContext, Model updatedModel) 
			throws SQLException, DataAccessException {
				
	String sql;
	ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
	for (int i=0;i<reactionSpecs.length;i++){
		//
		KeyValue newReactionSpecKey = keyFactory.getNewKey(con);
		KeyValue reactionStepKey = updatedModel.getReactionStep(reactionSpecs[i].getReactionStep().getName()).getKey();
		//
		sql = 	"INSERT INTO " + reactionSpecTable.getTableName() + " " + reactionSpecTable.getSQLColumnList() +
				" VALUES " + reactionSpecTable.getSQLValueList(newReactionSpecKey, simContextKey, reactionSpecs[i], reactionStepKey);
//System.out.println("SimulationContextDbDriver.insertReactionSpecsSQL(), sql = "+sql);
		updateCleanSQL(con, sql);
	}
}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void insertSimulationContext(InsertHashtable hash, Connection con, User user, SimulationContext simContext,
									KeyValue updatedMathDescKey, Model updatedModel, KeyValue updatedGeometryKey,
									Version newVersion, boolean bVersionChildren) 
						throws SQLException, DependencyException, DataAccessException,RecordChangedException {
							

	////
	//// Insert MathDescription for this SimContext
	////
	//KeyValue mathDescKey = null;
	//MathDescription mathDesc = simContext.getMathDescription();
	//if (mathDesc!=null){
		//Version mathVersion = mathDesc.getVersion();
		//if (mathVersion != null && mathVersion.getVersionKey() != null) {
			//mathDescKey = mathDescDB.updateVersionable(hash, con, user, mathDesc, bVersionChildren);
		//}else{
			//String mathName = mathDesc.getName(); // + "_generated";
			//while (isNameUsed(con,VersionableType.MathDescription,user,mathName)){
				//mathName = cbit.util.TokenMangler.getNextRandomToken(mathName);
			//}
			//mathDescKey = mathDescDB.insertVersionable(hash, con, user, mathDesc, mathName, bVersionChildren);
		//}
	//}

	////
	//// insert model (if needed)
	////
	//Model model = simContext.getReactionContext().getModel();
	//KeyValue modelKey = hash.getDatabaseKey(model);
	//try {
		//if (modelKey==null){
			//Version modelVersion = model.getVersion();
			//if (modelVersion != null && modelVersion.getVersionKey() != null) {
				//modelKey = modelDB.updateVersionable(hash, con, user, model, bVersionChildren);
			//} else {
				//String modelName = model.getName(); // + "_model";
				//while (isNameUsed(con,VersionableType.Model,user,modelName)){
					//modelName = cbit.util.TokenMangler.getNextEnumeratedToken(modelName);
				//}
				//modelKey = modelDB.insertVersionable(hash, con, user, model, modelName, bVersionChildren);
			//}
		//}
	//} catch (RecordChangedException rce) {
		//throw rce;
	//}
	//Model updatedModel = (Model)modelDB.getVersionable(con,user,VersionableType.Model,modelKey);
	////
	//// insert geometry (if needed)
	////
	//Geometry geom = simContext.getGeometryContext().getGeometry();
	//KeyValue geomKey = hash.getDatabaseKey(geom);
	//try {
		//if (geomKey==null){
			//Version geomVersion = geom.getVersion();
			//if (geomVersion != null && geomVersion.getVersionKey() != null) {
				//geomKey = geomDB.updateVersionable(hash, con, user, geom, bVersionChildren);
			//} else {
				//String geomName = geom.getName(); // + "_geometry";
				//while (isNameUsed(con,VersionableType.Geometry,user,geomName)){
					//geomName = cbit.util.TokenMangler.getNextEnumeratedToken(geomName);
				//}
				//geomKey = geomDB.insertVersionable(hash, con, user, geom, geomName, bVersionChildren);
			//}
		//}
	//} catch (RecordChangedException rce) {
		//throw rce;
	//}
	
	insertSimulationContextSQL(con, user, simContext, updatedMathDescKey, updatedModel.getKey(), updatedGeometryKey, newVersion);

	insertStructureMappingsSQL(hash, con, newVersion.getVersionKey(), simContext);   // links to structure (dictionary)
	insertStimuliSQL(hash,con, newVersion.getVersionKey(), simContext); // inserts Stimuli
	insertSpeciesContextSpecsSQL(con, newVersion.getVersionKey(), simContext, updatedModel); // links to speciesContext
	insertReactionSpecsSQL(con, newVersion.getVersionKey(), simContext, updatedModel); // links to reactionSteps
	insertAnalysisTasksSQL(con, newVersion.getVersionKey(), simContext,dbSyntax); // inserts AnalysisTasks
	ApplicationMathTable.table.saveOutputFunctionsSimContext(con, newVersion.getVersionKey(), simContext.getOutputFunctionContext().getOutputFunctionsList(),dbSyntax,keyFactory);
	DataSymbolTable.table.saveDataSymbols(con,keyFactory,newVersion.getVersionKey(),simContext.getDataContext(),user);
	
	hash.put(simContext,newVersion.getVersionKey());
}


/**
 * This method was created in VisualAge.
 */
private void insertSimulationContextSQL(Connection con, User user,SimulationContext simContext,KeyValue mathDescKey,
										KeyValue modelKey,KeyValue geomKey,Version version)
									throws SQLException,DataAccessException {
										
	String sql = null;
	String appComponentXmlStr = SimContextTable.getAppComponentsForDatabase(simContext);
	Object[] o = {simContext,mathDescKey,modelKey,geomKey, appComponentXmlStr};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,simContextTable,o,version,dbSyntax);
//System.out.println(sql);
	if (appComponentXmlStr!=null){
		varchar2_CLOB_update(
			con,
			sql,
			appComponentXmlStr,
			SimContextTable.table,
			version.getVersionKey(),
			SimContextTable.table.appComponentsLarge,
			SimContextTable.table.appComponentsSmall,
			dbSyntax);
	}else{
		updateCleanSQL(con,sql);
	}
}


/**
 * This method was created in VisualAge.
 * @param con java.sql.Connection
 * @param simContextKey cbit.sql.KeyValue
 * @param simContext cbit.vcell.mapping.SimulationContext
 */
private void insertSpeciesContextSpecsSQL(Connection con, KeyValue simContextKey, SimulationContext simContext, Model updatedModel) throws SQLException {
	String sql;
	SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec speciesContextSpec = speciesContextSpecs[i];
		KeyValue scKey = updatedModel.getSpeciesContext(speciesContextSpec.getSpeciesContext().getName()).getKey();
		KeyValue newSpeciesContextSpecKey = keyFactory.getNewKey(con);
		//
		sql = 	"INSERT INTO " + speciesContextSpecTable.getTableName() + " " + speciesContextSpecTable.getSQLColumnList() + 
				" VALUES " + speciesContextSpecTable.getSQLValueList(newSpeciesContextSpecKey, simContextKey, speciesContextSpec, scKey);
//System.out.println(sql);
		updateCleanSQL(con, sql);
	}
}


/**
 * This method was created in VisualAge.
 */
private void insertStimuliSQL(InsertHashtable hash, Connection con, KeyValue simContextKey, SimulationContext simContext) 
			throws SQLException, DataAccessException {
				
	StimulusTable stimulusTable = StimulusTable.table;

	ElectricalStimulus stimuli[] = simContext.getElectricalStimuli();
	//
	// store stimuli
	//
	for (int i=0;i<stimuli.length;i++){
		ElectricalStimulus stimulus = stimuli[i];
		KeyValue newStimuliKey = keyFactory.getNewKey(con);
		//
		String sql = "INSERT INTO " + stimulusTable.getTableName() + " " + stimulusTable.getSQLColumnList() +
					" VALUES " + stimulusTable.getSQLValueList(hash, newStimuliKey, simContextKey, stimulus);
//System.out.println(sql);
		updateCleanSQL(con, sql);
	}
	//
	// store ground electrode
	//
	Electrode groundElectrode = simContext.getGroundElectrode();
	if (groundElectrode != null){
		KeyValue newStimuliKey = keyFactory.getNewKey(con);
		//
		String sql = "INSERT INTO " + stimulusTable.getTableName() + " " + stimulusTable.getSQLColumnList() +
					" VALUES " + stimulusTable.getSQLValueList(hash, newStimuliKey, simContextKey, groundElectrode);
//System.out.println(sql);
		updateCleanSQL(con, sql);
	}
}


/**
 * This method was created in VisualAge.
 */
private void insertStructureMappingsSQL(InsertHashtable hash, Connection con, KeyValue simContextKey, SimulationContext simContext) 
			throws SQLException, DataAccessException {
				
	String sql;
	StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping structureMapping = structureMappings[i];
		//
		// check for incomplete StructureMappings, this allows partially mapped SimContext to be saved without Exceptions.
		// it's ok to have missing StructureMappings in the database, assignStructureMappingsSQL() is tolerant of this.
		//
		//
		KeyValue newStuctureMappingKey = keyFactory.getNewKey(con);
		//
		sql = 	"INSERT INTO " + structureMappingTable.getTableName() + " " + structureMappingTable.getSQLColumnList() +
				" VALUES " + structureMappingTable.getSQLValueList(hash, newStuctureMappingKey, simContextKey, structureMapping);
//System.out.println(sql);
		updateCleanSQL(con, sql);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public KeyValue insertVersionable(InsertHashtable hash, Connection con, User user, SimulationContext simContext, KeyValue updatedMathDescKey, Model updatedModel, KeyValue updatedGeometryKey, String name, boolean bVersion) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	Version newVersion = insertVersionableInit(hash, con, user, simContext, name, simContext.getDescription(), bVersion);
	try{
		insertSimulationContext(hash, con, user, simContext, updatedMathDescKey, updatedModel, updatedGeometryKey, newVersion, bVersion);
		return newVersion.getVersionKey();
	} catch (DependencyException e) {
		lg.error(e.getMessage(),e);
		throw new DataAccessException("DependencyException: " + e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash,Connection con,User user,SimulationContext simContext,KeyValue updatedMathDescKey,Model updatedModel,KeyValue updatedGeometryKey,boolean bVersion)
    throws DataAccessException, SQLException, RecordChangedException {

    Version newVersion = null;
    try {
        newVersion = updateVersionableInit(hash, con, user, simContext, bVersion);
        insertSimulationContext(hash,con,user,simContext,updatedMathDescKey,updatedModel,updatedGeometryKey,newVersion,bVersion);
    } catch (DependencyException e) {
        lg.error(e.getMessage(),e);
        throw new DataAccessException("MathException: " + e.getMessage());
    }
    return newVersion.getVersionKey();
}


public SimContextRep[] getSimContextReps(Connection con, KeyValue startingSimContextKey, int numRows)
		throws SQLException, DataAccessException, ObjectNotFoundException {
	String sql = simContextTable.getPreparedStatement_SimContextReps();
	
	PreparedStatement stmt = con.prepareStatement(sql);

//	System.out.println(sql);
	simContextTable.setPreparedStatement_SimContextReps(stmt, startingSimContextKey, numRows);

	ArrayList<SimContextRep> simContextReps = new ArrayList<SimContextRep>();
	try {
		ResultSet rset = stmt.executeQuery();

		//showMetaData(rset);

		while (rset.next()) {
			SimContextRep simContextRep = simContextTable.getSimContextRep(rset);
			simContextReps.add(simContextRep);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return simContextReps.toArray(new SimContextRep[simContextReps.size()]);
}


public SimContextRep getSimContextRep(Connection con, KeyValue simContextKey)
		throws SQLException, DataAccessException, ObjectNotFoundException {
	String sql = simContextTable.getPreparedStatement_SimContextRep();
	
	PreparedStatement stmt = con.prepareStatement(sql);

	//System.out.println(sql);
	simContextTable.setPreparedStatement_SimContextRep(stmt, simContextKey);

	ArrayList<SimContextRep> simContextReps = new ArrayList<SimContextRep>();
	try {
		ResultSet rset = stmt.executeQuery();

		//showMetaData(rset);

		while (rset.next()) {
			SimContextRep simContextRep = simContextTable.getSimContextRep(rset);
			simContextReps.add(simContextRep);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	if (simContextReps.size()==0){
		return null;
	}else if (simContextReps.size()==1){
		return simContextReps.get(0);
	}else{
		throw new DataAccessException("more than one simContextRep found for SimContextKey="+simContextKey);
	}
}


}
