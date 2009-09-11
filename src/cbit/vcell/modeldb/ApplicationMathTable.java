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
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.Function;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.xml.Xmlproducer;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

/**
 * This type was created in VisualAge.
 */
public class ApplicationMathTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_applicationmath";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simContextRef	= new Field("simContextRef",	"integer",	"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field outputFuncLarge	= new Field("outputFuncLRG",	"CLOB",				"");
	public final Field outputFuncSmall	= new Field("outputFuncSML",	"VARCHAR2(4000)",	"");
	
	private final Field fields[] = {simContextRef,outputFuncLarge,outputFuncSmall};
	
	public static final ApplicationMathTable table = new ApplicationMathTable();
/**
 * ModelTable constructor comment.
 */
private ApplicationMathTable() {
	super(TABLE_NAME);
	addFields(fields);
}

public void saveOutputFunctions(Connection con,KeyValue simContextRef,ArrayList<AnnotatedFunction> outputFunctionsList) throws SQLException,DataAccessException{
	
	if(outputFunctionsList == null || outputFunctionsList.size() == 0){
		return;
	}
	String outputFunctionsXML =
		XmlUtil.xmlToString((new Xmlproducer(false)).getXML(outputFunctionsList));
	
	String tableValues = null;
	if(DbDriver.varchar2_CLOB_is_Varchar2_OK(outputFunctionsXML)){
		tableValues = "null"+","+DbDriver.INSERT_VARCHAR2_HERE;
	}else{
		tableValues = DbDriver.INSERT_CLOB_HERE+","+"null";
	}
	
	String sql = 
		"INSERT INTO "+ApplicationMathTable.table.getTableName()+
		" VALUES ("+
		DbDriver.getNewKey(con).toString()+","+
		simContextRef.toString()+","+
		tableValues+")";
	
	DbDriver.varchar2_CLOB_update(con,
		sql,
		outputFunctionsXML,
		ApplicationMathTable.table,
		DbDriver.getNewKey(con),
		ApplicationMathTable.table.outputFuncLarge,
		ApplicationMathTable.table.outputFuncSmall);
}

public ArrayList<AnnotatedFunction> getOutputFunctions(Connection con,KeyValue simContextRef) throws SQLException,DataAccessException{
	Statement stmt = null;
	try{
		stmt = con.createStatement();
		ResultSet rset =
			stmt.executeQuery(
				"SELECT * FROM "+ApplicationMathTable.table.getTableName()+
				" WHERE "+ApplicationMathTable.table.simContextRef.getUnqualifiedColName()+" = "+simContextRef.toString());
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
