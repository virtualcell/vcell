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

import java.sql.Connection;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.Identifiable;
import org.vcell.util.document.KeyValue;

import cbit.sql.*;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class MIRIAMTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_miriam";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] miriamTableConstraint =
		new String[] {
			"miriam_only_1 CHECK("+
			"DECODE(bioModelRef,NULL,0,bioModelRef,1)+"+
//			"DECODE(mathModelRef,NULL,0,mathModelRef,1)+"+
			"DECODE(speciesRef,NULL,0,speciesRef,1)+"+
			"DECODE(structRef,NULL,0,structRef,1)+"+
			"DECODE(reactStepRef,NULL,0,reactStepRef,1) = 1)",
			"miriam_info_not_null CHECK("+
			"DECODE(annotation,NULL,0,structRef,1)+"+
			"DECODE(userNotes,NULL,0,reactStepRef,1) > 0)"
		};

	public final Field bioModelRef		= new Field("bioModelRef",	"integer",			BioModelTable.REF_TYPE+ " ON DELETE CASCADE");
//	public final Field mathModelRef		= new Field("mathModelRef",	"integer",			MathModelTable.REF_TYPE+ " ON DELETE CASCADE");
	public final Field speciesRef		= new Field("speciesRef",	"integer",			SpeciesTable.REF_TYPE+ " ON DELETE CASCADE");
	public final Field structRef		= new Field("structRef",	"integer",			StructTable.REF_TYPE+ " ON DELETE CASCADE");
	public final Field reactStepRef		= new Field("reactStepRef",	"integer",			ReactStepTable.REF_TYPE+ " ON DELETE CASCADE");
	public final Field annotation		= new Field("annotation",	"varchar2(4000)",	"");
	public final Field userNotes		= new Field("userNotes",	"varchar2(4000)",	"");
	
	private final Field fields[] = {bioModelRef,/*mathModelRef,*/speciesRef,structRef,reactStepRef,annotation,userNotes};
	
	public static final MIRIAMTable table = new MIRIAMTable();
/**
 * ModelTable constructor comment.
 */
private MIRIAMTable() {
	super(TABLE_NAME,miriamTableConstraint);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 * @param rset java.sql.ResultSet
 */
//public void insertMIRIAM(Connection con,MIRIAMAnnotatable miriamAnnotatable,KeyValue referenceKey)throws DataAccessException,SQLException{
//
//	String miriamAnnotation = null;
//	String miriamNotes = null;
//	if(miriamAnnotatable.getMIRIAMAnnotation() != null){
//		if(miriamAnnotatable.getMIRIAMAnnotation().getAnnotation() != null){
//			miriamAnnotation = XmlUtil.xmlToString(miriamAnnotatable.getMIRIAMAnnotation().getAnnotation(),true);
////			miriamAnnotation = cbit.util.TokenMangler.getSQLEscapedString(miriamAnnotation);
//		}
//		if(miriamAnnotatable.getMIRIAMAnnotation().getUserNotes() != null){
//			miriamNotes = XmlUtil.xmlToString(miriamAnnotatable.getMIRIAMAnnotation().getUserNotes(),true);
////			miriamNotes = cbit.util.TokenMangler.getSQLEscapedString(miriamNotes);
//		}
//	}
//	if(miriamAnnotation == null && miriamNotes == null){
//		return;
//	}
//	int annotLength = (miriamAnnotation == null?0:miriamAnnotation.length());
//	int notesLength = (miriamNotes == null?0:miriamNotes.length());
//	int maxLength =
//		Math.max(annotLength,notesLength);
//	final int MAX_CHARS = 3000;
//	for(int i=0;i<maxLength;i+= MAX_CHARS){
//		String annotS = (i<annotLength?miriamAnnotation.substring(i,i+Math.min(MAX_CHARS, annotLength-i)):null);
//		if(annotS != null){
//			annotS = org.vcell.util.TokenMangler.getSQLEscapedString(annotS);
//		}
//		String notesS = (i<notesLength?miriamNotes.substring(i,i+Math.min(MAX_CHARS, notesLength-i)):null);
//		if(notesS != null){
//			notesS = org.vcell.util.TokenMangler.getSQLEscapedString(notesS);
//		}
//		
//		String miriamTableValues = MIRIAMTable.table.getSQLValueList(miriamAnnotatable, referenceKey,annotS,notesS);
////		if(miriamTableValues != null){
//			String sql = "INSERT INTO " + MIRIAMTable.table.getTableName() + " " + 
//				MIRIAMTable.table.getSQLColumnList() + " VALUES " + miriamTableValues;
//			DbDriver.updateCleanSQL(con,sql);
////		}
//	}
//
//}
public void setMIRIAMAnnotation(Connection con,Identifiable identifiable,KeyValue referenceKey) throws java.sql.SQLException, DataAccessException {
//	
//	//MIRIAM Annotation
//	String sql =
//		" SELECT " +
//			MIRIAMTable.table.annotation.getQualifiedColName() + "," +
//			MIRIAMTable.table.userNotes.getQualifiedColName() +
//		" FROM " + MIRIAMTable.table.getTableName() + 
//		" WHERE " +
//			(identifiable instanceof BioModelMetaData?MIRIAMTable.table.bioModelRef:"") +
////			(miriamAnnotatable instanceof MathModelMetaData?MIRIAMTable.table.mathModelRef:"") +
//			(identifiable instanceof Species?MIRIAMTable.table.speciesRef:"") +
//			(identifiable instanceof Structure?MIRIAMTable.table.structRef:"") +
//			(identifiable instanceof ReactionStep?MIRIAMTable.table.reactStepRef:"") +
//			" = " + referenceKey
//			+" ORDER BY "+MIRIAMTable.table.id.getUnqualifiedColName();
//	Statement stmt = con.createStatement();
//	try {
//		ResultSet rset = stmt.executeQuery(sql);
//		StringBuffer annotSB = new StringBuffer();
//		StringBuffer notesSB = new StringBuffer();
//		while(rset.next()){
//			String annotation = rset.getString(MIRIAMTable.table.annotation.toString());
//			if(!rset.wasNull()){
//				annotation = org.vcell.util.TokenMangler.getSQLRestoredString(annotation);
//				annotSB.append(annotation);
//			}
//			String notes = rset.getString(MIRIAMTable.table.userNotes.toString());
//			if(!rset.wasNull()){
//				notes = org.vcell.util.TokenMangler.getSQLRestoredString(notes);
//				notesSB.append(notes);
//			}
//		}
//		if(annotSB.length() > 0 || notesSB.length() > 0){
//			MIRIAMAnnotation miriamAnnot = new MIRIAMAnnotation();
//			if(annotSB.length() > 0){
//				Element annotElement = XmlUtil.stringToXML(annotSB.toString(), null);
//				MIRIAMHelper.cleanEmptySpace(annotElement);
//				miriamAnnot.setAnnotation(annotElement);
//			}
//			if(notesSB.length() > 0){
//				Element notesElement = XmlUtil.stringToXML(notesSB.toString(), null);
//				MIRIAMHelper.cleanEmptySpace(notesElement);
//				miriamAnnot.setUserNotes(notesElement);
//			}
//			identifiable.setMIRIAMAnnotation(miriamAnnot);
//		}
//	} finally {
//		stmt.close(); // Release resources include resultset
//	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
private String getSQLValueList(
		Identifiable miriamAnnotatable,KeyValue referenceKey,
		String miriamAnnotation,String miriamNotes) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(NewSEQ+",");
	buffer.append((miriamAnnotatable instanceof BioModelMetaData?referenceKey:"null")+",");
//	buffer.append((miriamAnnotatable instanceof MathModelMetaData?referenceKey:"null")+",");
	buffer.append((miriamAnnotatable instanceof Species?referenceKey:"null")+",");
	buffer.append((miriamAnnotatable instanceof Structure?referenceKey:"null")+",");
	buffer.append((miriamAnnotatable instanceof ReactionStep?referenceKey:"null")+",");
	buffer.append((miriamAnnotation!= null ?"'"+miriamAnnotation+"'" : "null")+",");
	buffer.append((miriamNotes!= null ?"'"+miriamNotes+"'" : "null")+")");
	
	return buffer.toString();
}

}
