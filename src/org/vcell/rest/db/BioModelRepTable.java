/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.rest.db;
import org.vcell.rest.common.BioModelRep;
import org.vcell.rest.common.SimContextRefRep;
import org.vcell.rest.common.SimRefRep;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.modeldb.BioModelTable;

import com.google.gson.Gson;
/**
 * This type was created in VisualAge.
 */
public class BioModelRepTable extends cbit.sql.Table {
	
	private static final String TABLE_NAME = "vc_biomodelrep";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field biomodelRef 				= new Field("biomodelRef",			"integer",			"NOT NULL "+BioModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field simRefReps				= new Field("simRefReps",			"VARCHAR2(4000)",	"");
	public final Field simContextRefReps		= new Field("simContextRefReps",	"VARCHAR2(4000)",	"");

	private final Field fields[] = {biomodelRef,simRefReps,simContextRefReps};
	
	public static final BioModelRepTable table = new BioModelRepTable();

/**
 * ModelTable constructor comment.
 */
private BioModelRepTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * select vc_biomodel.id, 
 * '['||wm_concat('{"key":'||vc_simulation.id||',"name":"'||vc_simulation.name||'"}')||']' as sims,
 * '['||wm_concat('{"key":'||vc_simContext.id||',"name":"'||vc_simcontext.name||'"}')||']' as simContexts
 * from vc_biomodel, VC_BIOMODELSIM, VC_SIMULATION, VC_BIOMODELSIMCONTEXT, VC_SIMCONTEXT 
 * where vc_biomodel.id=VC_BIOMODELSIMCONTEXT.BIOMODELREF and VC_BIOMODELSIMCONTEXT.SIMCONTEXTREF=VC_SIMCONTEXT.id 
 * and vc_biomodel.id=vc_biomodelsim.BIOMODELREF and vc_biomodelsim.SIMREF=vc_simulation.id 
 * and vc_biomodel.id = 2000199 
 * group by vc_biomodel.id;
 * 
 * returns (1 row)
 * 
 * id = 2000199
 * sims = [{"key":2011674,"name":"simspec10_16241_SIM"},{"key":2011678,"name":"simspec10_16420_SIM"},{"key":2011678,"name":"simspec10_16420_SIM"},{"key":2011674,"name":"simspec10_16241_SIM"}]
 * simContexts = [{"key":16239,"name":"simspec9"},{"key":16418,"name":"simspec10"},{"key":16418,"name":"simspec10"},{"key":16239,"name":"simspec9"}]
 * 
 */
public BioModelRep getReactionStep(Structure structure, Model model, KeyValue rsKey, java.sql.ResultSet rset, SessionLog log) throws java.sql.SQLException, DataAccessException {
	Gson gson = new Gson();
	
	KeyValue bmkey = new KeyValue(rset.getBigDecimal(table.biomodelRef.getUnqualifiedColName()));

	String simRefRepsJson = rset.getString(BioModelRepTable.table.simRefReps.toString());
	String simContextRefRepsJson = rset.getString(BioModelRepTable.table.simContextRefReps.toString());
	SimRefRep[] simRefReps = gson.fromJson(simRefRepsJson, SimRefRep[].class);
	SimContextRefRep[] simContextRefReps = gson.fromJson(simContextRefRepsJson, SimContextRefRep[].class);
	
	return new BioModelRep(bmkey.toString(), simRefReps, simContextRefReps);
}


}
