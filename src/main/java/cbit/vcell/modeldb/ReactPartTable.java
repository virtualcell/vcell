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
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.Table;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
/**
 * This type was created in VisualAge.
 */
public class ReactPartTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_reactpart";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field role			= new Field("role",				"varchar(10)",	"NOT NULL");
	public final Field stoich		= new Field("stoich",			"integer",		"NOT NULL");
	public final Field reactStepRef	= new Field("reactStepRef",		"integer",		"NOT NULL "+ReactStepTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field scRef			= new Field("scRef",			"integer",		"NOT NULL "+SpeciesContextModelTable.REF_TYPE);

	private final Field fields[] = {role,stoich,reactStepRef,scRef};
	
	public static final ReactPartTable table = new ReactPartTable();

	private static final String ROLE_REACTANT = "reactant";
	private static final String ROLE_PRODUCT = "product";
	private static final String ROLE_CATALYST = "catalyst";
	private static final String ROLE_FLUX = "flux";
	
/**
 * ModelTable constructor comment.
 */
private ReactPartTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 * @param rset java.sql.ResultSet
 */
public ReactionParticipant getReactionParticipant(KeyValue rpKey, java.sql.ResultSet rset, SessionLog log) throws java.sql.SQLException, DataAccessException {
	
	ReactionParticipant rp = null;
	
	KeyValue key = rpKey;
	String role = rset.getString(ReactPartTable.table.role.toString());
	int stoichiometry = rset.getInt(ReactPartTable.table.stoich.toString());
	if (role.equals(ROLE_CATALYST)){
		rp = new Catalyst(key,null,null);
	}else if (role.equals(ROLE_PRODUCT)){
		rp = new Product(key,null,null,stoichiometry);
	}else if (role.equals(ROLE_REACTANT)){
		rp = new Reactant(key,null,null,stoichiometry);
	}else if (role.equals(ROLE_FLUX)){
		rp = new FluxReaction.Flux(key,null,null);
	}else{
		throw new DataAccessException("unexpected value of "+ReactPartTable.table.role.toString()+"'"+role+"'");
	}
	rp.setStoichiometry(stoichiometry);
	return rp;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param rp cbit.vcell.model.ReactionParticipant
 */
private String getRole(ReactionParticipant rp) throws DataAccessException {
	if (rp instanceof Catalyst){
		return ROLE_CATALYST;
	}else if (rp instanceof Product){
		return ROLE_PRODUCT;
	}else if (rp instanceof Reactant){
		return ROLE_REACTANT;
	}else if (rp instanceof FluxReaction.Flux){
		return ROLE_FLUX;
	}else{
		throw new DataAccessException("reactionParticipant type "+rp.getClass().toString()+" not supported");
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(InsertHashtable hash, KeyValue key, ReactionParticipant reactionParticipant, KeyValue reactStepKey) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append("'"+getRole(reactionParticipant)+"',");
	buffer.append(reactionParticipant.getStoichiometry()+",");
	buffer.append(reactStepKey+",");
	buffer.append(hash.getDatabaseKey(reactionParticipant.getSpeciesContext())+")");

	return buffer.toString();
}
}
