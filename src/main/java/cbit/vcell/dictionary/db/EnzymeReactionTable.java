/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.dictionary.db;

import java.util.Vector;

import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.DBNonFormalUnboundSpecies;
import cbit.vcell.dictionary.EnzymeInfo;
import cbit.vcell.dictionary.FormalCompound;
import cbit.vcell.dictionary.FormalEnzyme;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.ReactionDescription;

/**
 * Represents a table for storing Enzyme information in a database
 * Creation date: (7/3/2002 3:36:45 PM)
 * @author: Steven Woolley
 */
public class EnzymeReactionTable extends Table {
    private static final String TABLE_NAME = "vc_enzymereaction";
    public static final String REF_TYPE =
        "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";
        
    public final Field reactionId = 	new Field("reactionId", 	"varchar2(32)",	"NOT NULL");
    public final Field enzymeRef = 		new Field("enzymeRef", 		"number",		EnzymeTable.REF_TYPE+" ON DELETE CASCADE");
    //
    public final Field compoundRef = 	new Field("compoundRef", 	"number",		CompoundTable.REF_TYPE+" ON DELETE CASCADE NOT NULL");
    public final Field type = 			new Field("type", 			"varchar2(1)",	"NOT NULL"); // "R(reactant)","P(product)"
    public final Field stoich = 		new Field("stoich", 		"number",		"NOT NULL");
    public final Field parsedECNumber = new Field("parsedECNumber", "varchar2(32)",	"");
    
    private final Field fields[] = {reactionId, enzymeRef, compoundRef, type, stoich,parsedECNumber};

    public static final EnzymeReactionTable table = new EnzymeReactionTable();

    public static final char REACANT_TYPE_REACTANT = 'R';
    public static final char REACANT_TYPE_PRODUCT = 'P';
    //
	public static final String RXQ_ENZYMEFID_ALIAS = "enzymeFID";
	public static final String RXQ_ENZYMENAME_ALIAS = "enzymeName";
	public static final String RXQ_CMPDNDNAME_ALIAS = "compoundName";
	public static final String RXQ_ENZYMEID_ALIAS = "enzymeID";
	public static final String RXQ_CMPNDID_ALIAS = "compoundID";
    //
/**
 * Creates a new CompoundTable object with the defined table values and fields
 * Creation date: (6/25/2002 3:53:09 PM)
 */
public EnzymeReactionTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2003 12:04:28 PM)
 * @return java.lang.String
 * @param sType char
 * @param typeID java.lang.String[]
 * @param wildCard java.lang.String
 */
private String createRPTypeSearchCondition(char sType, String[] refs) {

	StringBuffer reactantsCondition = new StringBuffer();
	
	reactantsCondition.append(
		"SELECT DISTINCT " + EnzymeReactionTable.table.reactionId.getQualifiedColName() +
		" FROM " + EnzymeReactionTable.table.getTableName() +
		" WHERE " +
		EnzymeReactionTable.table.type.getQualifiedColName()+"="+"'"+sType+"'" + " AND (");
	
	for(int i = 0; i < refs.length;i+= 1){
		reactantsCondition.append((i > 0?" OR ":"") + EnzymeReactionTable.table.compoundRef.getQualifiedColName()+"="+refs[i]);
	}
	
	reactantsCondition.append(")");

	if(refs.length > 1){
		reactantsCondition.append(" GROUP BY " +
			EnzymeReactionTable.table.reactionId.getQualifiedColName() + "," +
			EnzymeReactionTable.table.enzymeRef.getQualifiedColName() +
			" HAVING COUNT("+EnzymeReactionTable.table.compoundRef.getQualifiedColName()+")="+refs.length
		);
	}
	
	return reactantsCondition.toString();
}
/**
 * returns a Compound object from the ResultSet
 * @return cbit.vcell.dictionary.Compound
 * @param rset java.sql.ResultSet
 */
public ReactionDescription[] getReactions(java.sql.ResultSet rset) throws java.sql.SQLException {

	Vector reactions = new Vector();

	String currentReactionID = null;
	String currentEnzymeFID = null;
	ReactionDescription current_dbfr = null;
	
	while(rset.next()){
			
		String reactionID = null;
		String enzymeFID = null;
		if(!rset.isAfterLast()){
			reactionID = rset.getString(EnzymeReactionTable.table.reactionId.toString());
			enzymeFID = rset.getString(RXQ_ENZYMEFID_ALIAS);
		}
		
		if(!reactionID.equals(currentReactionID) || !org.vcell.util.Compare.isEqualOrNull(enzymeFID,currentEnzymeFID)){
			java.math.BigDecimal enzymeID = rset.getBigDecimal(RXQ_ENZYMEID_ALIAS);
			if(enzymeID != null){
				String enzymeName = rset.getString(RXQ_ENZYMENAME_ALIAS);
				current_dbfr = new ReactionDescription(reactionID);
				current_dbfr.addReactionElement(
					new FormalEnzyme(new KeyValue(enzymeID),new EnzymeInfo(enzymeFID,new String[]{enzymeName},null,null,null)),
					0,ReactionDescription.RX_ELEMENT_CATALYST);
			}else if (enzymeFID != null){
				current_dbfr = new ReactionDescription(reactionID);
				current_dbfr.addReactionElement(new DBNonFormalUnboundSpecies(enzymeFID)/*FormalUnknown(enzymeFID,null)*/,0,ReactionDescription.RX_ELEMENT_CATALYST);

			}else{
				current_dbfr = new ReactionDescription(reactionID);
			}
			reactions.add(current_dbfr);
		}
		currentReactionID = reactionID;
		currentEnzymeFID = enzymeFID;
		//
		FormalCompound formalCompound =
			new FormalCompound(
				new KeyValue(rset.getBigDecimal(RXQ_CMPNDID_ALIAS)),
				new CompoundInfo(rset.getString(CompoundTable.table.keggID.toString()),new String[]{rset.getString(RXQ_CMPDNDNAME_ALIAS)},null,null,null)
			);
		current_dbfr.addReactionElement(
			formalCompound,
			rset.getInt(EnzymeReactionTable.table.stoich.toString()),
			rset.getString(EnzymeReactionTable.table.type.toString()).charAt(0)
			);
	}

	ReactionDescription[] reactionsArr = null;
	if(reactions.size() > 0){
		reactionsArr = new ReactionDescription[reactions.size()];
		reactions.copyInto(reactionsArr);
	}
	return reactionsArr;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2003 10:29:26 AM)
 */
public String getSQLReactionQuery(cbit.vcell.model.ReactionQuerySpec rqs) {
	
	DBFormalSpecies reactantsCompoundRef = rqs.getReactantBoundSpecies();
	DBFormalSpecies enzymeRef = rqs.getCatalystBoundSpecies();
	DBFormalSpecies productsCompoundRef = rqs.getProductBoundSpecies();
	String reactantWildCard = rqs.getReactantLikeString();
	String enzymeWildCard = rqs.getCatalystLikeString();
	String productWildCard = rqs.getProductLikeString();
	String repWildCard = rqs.getAnyReactionParticipantLikeString();
	DBFormalSpecies typeWildCardDBFS = rqs.getAnyReactionParticipantBoundSpecies();
	
	String typeWildCard = (typeWildCardDBFS != null?typeWildCardDBFS.getDBFormalSpeciesKey().toString():null);
	//
	//
	//
	if((repWildCard != null || typeWildCard != null) &&
		(
		(reactantWildCard != null) ||
		(enzymeWildCard != null) ||
		(productWildCard != null) ||
		(reactantsCompoundRef != null) ||
		(enzymeRef != null) ||
		(productsCompoundRef != null)
		)
		){
			throw new IllegalArgumentException("Illegal arguments to getSQLReactionQuery");
	}

	String nameConditionWild = null;
	String nameConditionR = null;
	String nameConditionP = null;
	String nameConditionE = null;
	
	String typeConditionWild = null;
	String typeConditionR = null;
	String typeConditionP = null;
	String typeConditionE = null;
	
	String rpNameCondition =
		" SELECT DISTINCT " + EnzymeReactionTable.table.reactionId.getQualifiedColName() +
		" FROM " +
			EnzymeReactionTable.table.getTableName() + "," +
			CompoundTable.table.getTableName() + "," +
			CompoundAliasTable.table.getTableName() +
		" WHERE " +
		EnzymeReactionTable.table.compoundRef.getQualifiedColName()+"="+CompoundTable.table.id.getQualifiedColName() +
		" AND " +
		CompoundAliasTable.table.compoundRef.getQualifiedColName()+"="+CompoundTable.table.id.getQualifiedColName() +
		" AND " +
		CompoundAliasTable.table.preferred.getQualifiedColName()+"="+"'T'";
	//
	if(repWildCard == null){
		if(reactantWildCard != null){
			nameConditionR = rpNameCondition +
			" AND " + "LOWER(" + CompoundAliasTable.table.name.getQualifiedColName() + ") LIKE LOWER('"+reactantWildCard+"')" +
			" AND " + EnzymeReactionTable.table.type.getQualifiedColName()+"="+"'"+REACANT_TYPE_REACTANT+"'";
		}else if(productWildCard != null){
			nameConditionP = rpNameCondition +
			" AND " + "LOWER(" + CompoundAliasTable.table.name.getQualifiedColName() + ") LIKE LOWER('"+productWildCard+"')" +
			" AND " + EnzymeReactionTable.table.type.getQualifiedColName()+"="+"'"+REACANT_TYPE_PRODUCT+"'";
		}
	}
	//
	if(typeWildCard == null){
		if(reactantsCompoundRef != null){
			typeConditionR = createRPTypeSearchCondition(REACANT_TYPE_REACTANT,new String[] { reactantsCompoundRef.getDBFormalSpeciesKey().toString() });
		}else if(productsCompoundRef != null){
			typeConditionP = createRPTypeSearchCondition(REACANT_TYPE_PRODUCT,new String[] { productsCompoundRef.getDBFormalSpeciesKey().toString() });
		}
	}
	//
	if(repWildCard != null || enzymeWildCard != null){
		nameConditionE =
			" SELECT DISTINCT " + EnzymeReactionTable.table.reactionId.getQualifiedColName() +
			" FROM " +
				EnzymeReactionTable.table.getTableName() + "," +
				EnzymeTable.table.getTableName() + "," +
				EnzymeAliasTable.table.getTableName() +
			" WHERE " +
			EnzymeReactionTable.table.enzymeRef.getQualifiedColName() + " IS NOT NULL" +
			" AND " +
			EnzymeReactionTable.table.enzymeRef.getQualifiedColName()+"="+EnzymeTable.table.id.getQualifiedColName() +
			" AND " +
			EnzymeAliasTable.table.enzymeRef.getQualifiedColName()+"="+EnzymeTable.table.id.getQualifiedColName() +
			" AND " +
			EnzymeAliasTable.table.preferred.getQualifiedColName()+"="+"'T'" +
			" AND " +
			"LOWER(" + EnzymeAliasTable.table.name.getQualifiedColName() + ") LIKE LOWER('"+(repWildCard != null?repWildCard:enzymeWildCard)+"')";
	}

	if(enzymeRef != null || typeWildCard != null){
		typeConditionE = 
			" SELECT DISTINCT " + EnzymeReactionTable.table.reactionId.getQualifiedColName() +
			" FROM " +
				EnzymeReactionTable.table.getTableName() +
			" WHERE " +
			EnzymeReactionTable.table.enzymeRef.getQualifiedColName() + " IS NOT NULL" +
			" AND " +
			EnzymeReactionTable.table.enzymeRef.getQualifiedColName()+"="+(typeWildCard != null?typeWildCard:enzymeRef.getDBFormalSpeciesKey().toString());
	}
	//
	if(repWildCard != null){
		nameConditionWild =
			rpNameCondition +" AND " + "LOWER(" + CompoundAliasTable.table.name.getQualifiedColName() + ") LIKE LOWER('"+repWildCard+"')" +
			" UNION " +
			nameConditionE;
	}
	if(typeWildCard != null){
		typeConditionWild =
			"SELECT DISTINCT " + EnzymeReactionTable.table.reactionId.getQualifiedColName() +
			" FROM " + EnzymeReactionTable.table.getTableName() +
			" WHERE " +
			EnzymeReactionTable.table.compoundRef.getQualifiedColName()+"="+typeWildCard +
			" OR " +
			EnzymeReactionTable.table.enzymeRef.getQualifiedColName()+"="+typeWildCard;
	}
	//
	String nameConditions = "";
	nameConditions = nameConditions +
		(nameConditionWild != null?nameConditionWild + " ":"");
	nameConditions = nameConditions +
		(nameConditionR != null?nameConditionR + " ":"");
	nameConditions = nameConditions +
		(nameConditionP != null?(nameConditions.length() > 0?" INTERSECT ":"") + nameConditionP + " ":"");
	nameConditions = nameConditions +
		(nameConditionE != null && nameConditionWild == null?(nameConditions.length() > 0?" INTERSECT ":"") + nameConditionE + " ":"");
		
	String typeConditions = "";
	typeConditions = typeConditions +
		(typeConditionWild != null?typeConditionWild + " ":"");
	typeConditions = typeConditions +
		(typeConditionR != null?typeConditionR + " ":"");
	typeConditions = typeConditions +
		(typeConditionP != null?(typeConditions.length() > 0?" INTERSECT ":"") + typeConditionP + " ":"");
	typeConditions = typeConditions +
		(typeConditionE != null && typeWildCard == null?(typeConditions.length() > 0?" INTERSECT ":"") + typeConditionE + " ":"");

	String conditions = null;
	if(nameConditions.length() > 0 && typeConditions.length() > 0){
		conditions = "("+nameConditions+") UNION ("+typeConditions+")";
	}else if (nameConditions.length() > 0){
		conditions = nameConditions;
	}else if (typeConditions.length() > 0){
		conditions = typeConditions;
	}
	//
	String selectSQL_NotNull =
		"SELECT DISTINCT " +
			EnzymeReactionTable.table.reactionId.getQualifiedColName()+ "," +
			EnzymeTable.table.ecNumber.getQualifiedColName() + " " + RXQ_ENZYMEFID_ALIAS + "," +
			EnzymeTable.table.id.getQualifiedColName() + " " + RXQ_ENZYMEID_ALIAS + "," +
			EnzymeAliasTable.table.name.getQualifiedColName() + " " + RXQ_ENZYMENAME_ALIAS  + "," +
			EnzymeReactionTable.table.type.getQualifiedColName() + "," +
			EnzymeReactionTable.table.stoich.getQualifiedColName() + "," +
			CompoundTable.table.keggID.getQualifiedColName() + "," +
			CompoundTable.table.id.getQualifiedColName() + " " + RXQ_CMPNDID_ALIAS + "," +
			CompoundAliasTable.table.name.getQualifiedColName() + " " + RXQ_CMPDNDNAME_ALIAS;
			
	String selectSQL_Null =
		"SELECT DISTINCT " +
			EnzymeReactionTable.table.reactionId.getQualifiedColName()+ "," +
			EnzymeReactionTable.table.parsedECNumber.getQualifiedColName() + " " + RXQ_ENZYMEFID_ALIAS + "," +
			"TO_NUMBER(NULL)" + " " + RXQ_ENZYMEID_ALIAS + "," +
			"TO_CHAR(NULL)" + " " + RXQ_ENZYMENAME_ALIAS  + "," +
			EnzymeReactionTable.table.type.getQualifiedColName() + "," +
			EnzymeReactionTable.table.stoich.getQualifiedColName() + "," +
			CompoundTable.table.keggID.getQualifiedColName() + "," +
			CompoundTable.table.id.getQualifiedColName() + " " + RXQ_CMPNDID_ALIAS + "," +
			CompoundAliasTable.table.name.getQualifiedColName() + " " + RXQ_CMPDNDNAME_ALIAS;
			
	String fromSQL_NotNull =
		" FROM " +
			EnzymeReactionTable.table.getTableName() + "," +
			CompoundTable.table.getTableName() + "," +
			CompoundAliasTable.table.getTableName() + "," +
			EnzymeTable.table.getTableName() + "," +
			EnzymeAliasTable.table.getTableName();

	String fromSQL_Null =
		" FROM " +
			EnzymeReactionTable.table.getTableName() + "," +
			CompoundTable.table.getTableName() + "," +
			CompoundAliasTable.table.getTableName();

	String joinSQL_NotNull =
		EnzymeReactionTable.table.enzymeRef.getQualifiedColName() + " IS NOT NULL " +
		" AND " +
		EnzymeReactionTable.table.compoundRef.getQualifiedColName()+"="+CompoundTable.table.id.getQualifiedColName() +
		" AND " +
		CompoundAliasTable.table.compoundRef.getQualifiedColName()+"="+CompoundTable.table.id.getQualifiedColName() +
		" AND " +
		CompoundAliasTable.table.preferred.getQualifiedColName()+"="+"'T'" +
		" AND " +
		EnzymeReactionTable.table.enzymeRef.getQualifiedColName()+"="+EnzymeTable.table.id.getQualifiedColName() +
		" AND " +
		EnzymeAliasTable.table.enzymeRef.getQualifiedColName()+"="+EnzymeTable.table.id.getQualifiedColName() +
		" AND " +
		EnzymeAliasTable.table.preferred.getQualifiedColName()+"="+"'T'";

	String joinSQL_Null =
		EnzymeReactionTable.table.enzymeRef.getQualifiedColName() + " IS NULL " +
		" AND " +
		EnzymeReactionTable.table.compoundRef.getQualifiedColName()+"="+CompoundTable.table.id.getQualifiedColName() +
		" AND " +
		CompoundAliasTable.table.compoundRef.getQualifiedColName()+"="+CompoundTable.table.id.getQualifiedColName() +
		" AND " +
		CompoundAliasTable.table.preferred.getQualifiedColName()+"="+"'T'";

	String orderBy =
		" ORDER BY " + 
		EnzymeReactionTable.table.reactionId.getUnqualifiedColName() + "," + 
		RXQ_ENZYMEFID_ALIAS + "," +
		EnzymeReactionTable.table.type.getUnqualifiedColName()+ " DESC";

	String sqlNonNull =
		selectSQL_NotNull + " " + 
		fromSQL_NotNull +
		" WHERE " + EnzymeReactionTable.table.reactionId.getQualifiedColName() + " IN " + "(" + conditions + ")" +
		" AND " + joinSQL_NotNull;
			
	String sqlNull =
		selectSQL_Null + " " + 
		fromSQL_Null +
		" WHERE " + EnzymeReactionTable.table.reactionId.getQualifiedColName() + " IN " + "(" + conditions + ")" +
		" AND " + joinSQL_Null;

	String sql =
		sqlNonNull +
		" UNION " +
		sqlNull +
		orderBy;

	//System.out.println(sqlNonNull+"\n"+sqlNull+"\n"+sql);
	return sql;
}
/**
 * Returns an SQL String with a value list taken from the parameter Enzyme
 * @return java.lang.String
 * @param key KeyValue
 * @param enzyme Enzyme
 */
public String getSQLValueList(KeyValue key,String keggReactionID,KeyValue enzymeRef,KeyValue compoundRef,int stoich,char reactantType,String parsedECNumber){

	if(reactantType != REACANT_TYPE_PRODUCT && reactantType != REACANT_TYPE_REACTANT){
		throw new RuntimeException("Unknown reactantType="+reactantType);
	}

    StringBuffer buffer = new StringBuffer();
    buffer.append("(");
    buffer.append(key + ",");
    buffer.append("'"+keggReactionID+"',");
    buffer.append((enzymeRef != null?enzymeRef.toString():"null")+",");
    buffer.append(compoundRef+",");
    buffer.append("'"+reactantType+"',");
    buffer.append(stoich+",");
    buffer.append((parsedECNumber!= null?"'"+parsedECNumber+"'":"null"));
	buffer.append(")");
    return buffer.toString();
}
}
