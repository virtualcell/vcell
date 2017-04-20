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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.jdom.Element;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.util.xml.XmlUtil;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.xml.Xmlproducer;


/**
 * This type was created in VisualAge.
 */
public class ApplicationMathTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_applicationmath";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] appMathTableConstraint =
		new String[] {
		"math_or_app CHECK(DECODE(simContextRef,NULL,0,simContextRef,1)+DECODE(mathModelRef,NULL,0,mathModelRef,1) = 1)"};

	public final Field simContextRef	= new Field("simContextRef",	"integer",SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field outputFuncLarge	= new Field("outputFuncLRG",	"CLOB",				"");
	public final Field outputFuncSmall	= new Field("outputFuncSML",	"VARCHAR2(4000)",	"");
	public final Field mathModelRef		= new Field("mathModelRef",		"integer",MathModelTable.REF_TYPE+" ON DELETE CASCADE");

	private final Field fields[] = {simContextRef,outputFuncLarge,outputFuncSmall,mathModelRef};
	
	public static final ApplicationMathTable table = new ApplicationMathTable();
/**
 * ModelTable constructor comment.
 */
private ApplicationMathTable() {
	super(TABLE_NAME,appMathTableConstraint);
	addFields(fields);
}

public void saveOutputFunctionsSimContext(Connection con,KeyValue simContextRef,ArrayList<AnnotatedFunction> outputFunctionsList) throws SQLException,DataAccessException{
	saveOutputFunctions(con, null, simContextRef, outputFunctionsList);
}
public void saveOutputFunctionsMathModel(Connection con,KeyValue mathModelRef,ArrayList<AnnotatedFunction> outputFunctionsList) throws SQLException,DataAccessException{
	saveOutputFunctions(con, mathModelRef, null, outputFunctionsList);
}
private void saveOutputFunctions(Connection con,KeyValue mathModelRef,KeyValue simContextRef,ArrayList<AnnotatedFunction> outputFunctionsList) throws SQLException,DataAccessException{
	
	if(outputFunctionsList == null || outputFunctionsList.size() == 0){
		return;
	}
	if(mathModelRef == null && simContextRef == null){
		throw new DataAccessException("must have either mathmodel or simcontext reference for saving OutputFunctions");
	}
	if(mathModelRef != null && simContextRef != null){
		throw new DataAccessException("OutputFunctions can be saved to either a mathmodel or simcontext, not both");
	}
	String outputFunctionsXML =
		XmlUtil.xmlToString((new Xmlproducer(false)).getXML(outputFunctionsList));
	
	String tableValues = null;
	if(DbDriver.varchar2_CLOB_is_Varchar2_OK(outputFunctionsXML)){
		tableValues = "null"+","+DbDriver.INSERT_VARCHAR2_HERE;
	}else{
		tableValues = DbDriver.INSERT_CLOB_HERE+","+"null";
	}
	
	KeyValue newKey = DbDriver.getNewKey(con);
	String sql = 
		"INSERT INTO "+ApplicationMathTable.table.getTableName()+
		" VALUES ("+
		newKey.toString()+","+
		(simContextRef != null?simContextRef.toString():"NULL")+","+
		tableValues+","+
		(mathModelRef != null?mathModelRef.toString():"NULL")+")";
	
	DbDriver.varchar2_CLOB_update(con,
		sql,
		outputFunctionsXML,
		ApplicationMathTable.table,
		newKey,
		ApplicationMathTable.table.outputFuncLarge,
		ApplicationMathTable.table.outputFuncSmall);
}

public ArrayList<AnnotatedFunction> getOutputFunctionsSimcontext(Connection con,KeyValue simContextRef) throws SQLException,DataAccessException{
	return getOutputFunctions(con, null, simContextRef);
}
public ArrayList<AnnotatedFunction> getOutputFunctionsMathModel(Connection con,KeyValue mathModelRef) throws SQLException,DataAccessException{
	return getOutputFunctions(con, mathModelRef, null);
}
private ArrayList<AnnotatedFunction> getOutputFunctions(Connection con,KeyValue mathModelRef,KeyValue simContextRef) throws SQLException,DataAccessException{
	Statement stmt = null;
	try{
		stmt = con.createStatement();
		ResultSet rset =
			stmt.executeQuery(
				"SELECT * FROM "+ApplicationMathTable.table.getTableName()+
				" WHERE "+
				(simContextRef != null?ApplicationMathTable.table.simContextRef.getUnqualifiedColName()+" = "+simContextRef.toString():"")+
				(mathModelRef != null?ApplicationMathTable.table.mathModelRef.getUnqualifiedColName()+" = "+mathModelRef.toString():"")
				);
		if(!rset.next()){
			return null;
		}
		String outputFunctionsXML =
			DbDriver.varchar2_CLOB_get(rset, ApplicationMathTable.table.outputFuncSmall, ApplicationMathTable.table.outputFuncLarge);
		rset.close();
		if(outputFunctionsXML == null){
			return null;
		}
		return convertOutputFunctionXMLToFuncList(outputFunctionsXML);
	}catch (XmlParseException e){
		throw new DataAccessException(e.getMessage(),e);
	}finally{
		if(stmt != null){stmt.close();}
	}
}

private ArrayList<AnnotatedFunction> convertOutputFunctionXMLToFuncList(String outputFunctionsXML) throws XmlParseException{
	Element outputFunctionElement = XmlUtil.stringToXML(outputFunctionsXML, null).getRootElement();
	ArrayList<AnnotatedFunction> outputFunctionList = (new XmlReader(false)).getOutputFunctions(outputFunctionElement);
	return outputFunctionList;

}
}
