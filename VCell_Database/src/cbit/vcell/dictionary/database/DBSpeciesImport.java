package cbit.vcell.dictionary.database;

import java.util.Vector;

import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.ConnectionFactory;
import cbit.sql.DBCacheTable;
import cbit.sql.Field;
import cbit.sql.KeyFactory;
import cbit.sql.MysqlConnectionFactory;
import cbit.sql.MysqlKeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.sql.Table;
import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.DBSpecies;
import cbit.vcell.dictionary.EnzymeInfo;
import cbit.vcell.dictionary.FormalSpeciesType;
import cbit.vcell.dictionary.ProteinInfo;
/**
 * This Class reads in Kegg's flatfile databases for Enzymes and Compounds and inserts them into a database
 * Creation date: (6/24/2002 10:23:18 AM)
 * @author: Steven Woolley
 */
public class DBSpeciesImport {
	//
	//
	public static final String DICTTYPE_COMPOUND = 	"cmpnd";
	public static final String DICTTYPE_ENZYME = 	"enz";
	public static final String DICTTYPE_PROTEIN = 	"prot";
	public static final String DICTTYPE_REACTION = 	"react";
	//
	private static String driverType = null;
	private static String driverName = null;
	private static String connectURL = null;
	private static String dbSchemaUser = null;
	private static String dbPassword = null;
	//
	private static boolean bPrintOnly = true;
	//
	static class XferObsolete {
		private Vector xferFrom = new Vector();
		private Vector unresolvedXferTo = new Vector();
		private Vector resolvedXferTo = null;
		private Vector notResolved = null;
		private String[] resolvedMasterList = null;
		
		public void xfer(String from,String to){
			resolvedXferTo = null;
			notResolved = null;
			resolvedMasterList = null;
			xferFrom.add(from);
			unresolvedXferTo.add(to);
		}
		public void resolve(String[] masterList){
			resolvedMasterList = masterList;
			for(int i=0;i < resolvedMasterList.length;i+= 1){
				if(xferFrom.contains(resolvedMasterList[i])){
					throw new RuntimeException("MasterList contains value that is XFerred from");
				}
			}
			resolvedXferTo = (Vector)unresolvedXferTo.clone();
			notResolved = new Vector();
			for(int i = 0; i < resolvedXferTo.size();i+= 1){
				boolean bFound = false;
				String resolveThis = (String)resolvedXferTo.get(i);
				while(!bFound){
					for(int j = 0; j < masterList.length;j+= 1){
						if(masterList[j].equals(resolveThis)){
							bFound = true;
							break;
						}
					}
					if(!bFound){
						if(xferFrom.contains(resolveThis)){
							resolveThis = (String)resolvedXferTo.get(xferFrom.indexOf(resolveThis));
						}else{
							notResolved.add(xferFrom.get(i));
						}
					}else{
						resolvedXferTo.set(i,resolveThis);
					}
				}
			}
			for(int i=0;i < resolvedXferTo.size();i+= 1){
				boolean bFound = false;
				for(int j=0;j < resolvedMasterList.length;j+= 1){
					if(resolvedMasterList[j].equals((String)resolvedXferTo.get(i))){
						bFound = true;
						break;
					}
				}
				if(!bFound){
					throw new RuntimeException("MasterList does not contain value that is XFerred to");
				}
			}
		}
		public String lookup(String oldValue){
			if(resolvedXferTo == null){
				throw new RuntimeException("Must resolve with Master before lookup");
			}
			for(int i = 0; i < resolvedMasterList.length;i+= 1){
				if(oldValue.equals(resolvedMasterList[i])){
					return oldValue;
				}
			}
			int fromIndex = xferFrom.indexOf(oldValue);
			if(fromIndex != -1){
				return (String)resolvedXferTo.elementAt(fromIndex);
			}
			return null;
		}
		public String[] getNotResolved(){
			if(resolvedXferTo == null){
				throw new RuntimeException("Must resolve with Master before getNotResolved");
			}
			if(notResolved == null || notResolved.size() == 0){
				return null;
			}
			String[] temp = new String[notResolved.size()];
			notResolved.copyInto(temp);
			return temp;
		}
	}
	//
	static class TableColumnsAssociation {

		String tableName = null;
		String columnAName = null;
		Vector columnAValues = new Vector();
		String columnBName = null;
		Vector columnBValues = new Vector();
		java.sql.Connection con = null;
		
		TableColumnsAssociation(java.sql.Connection argCon,String argTableName,String argColumnAName,String argColumnBName) throws java.sql.SQLException{

			this.con = argCon;
			this.tableName = argTableName;
			this.columnAName = argColumnAName;
			this.columnBName = argColumnBName;
			
			String sql = "SELECT "+columnAName+","+columnBName+" FROM "+tableName;
			java.sql.Statement stmt = null;
			int counter = 0;
			int size = 0;
			try{
				stmt = con.createStatement();
				java.sql.ResultSet rset = stmt.executeQuery(sql);
				String aVal = null;
				String bVal = null;
				while(rset.next()){
					aVal = rset.getString(columnAName);
					columnAValues.add(aVal);
					bVal = rset.getString(columnBName);
					columnBValues.add(bVal);
					counter+= 1;
					size+= aVal.length() + bVal.length();
				}
				if(rset != null){
					rset.close();
				}
			}finally{
				if(stmt != null){
					stmt.close();
				}
			}
			System.out.println("TableColumnsAssociation Count="+counter+" Size="+size);
		}
		Vector getAValues(){
			return (Vector)columnAValues.clone();
		}
		Vector getBValues(){
			return (Vector)columnBValues.clone();
		}
		String getAValueUsingBValue(String bValue){
			int index = columnBValues.indexOf(bValue);
			if(index != -1){
				return (String)columnAValues.get(index);
			}
			return null;
		}
		String getBValueUsingAValue(String aValue){
			int index = columnAValues.indexOf(aValue);
			if(index != -1){
				return (String)columnBValues.get(index);
			}
			return null;
		}
	}
	//
	static class DMLVCellDictionary {

		ConnectedDictDbDriver conDictDB = null;
		Table dmlTable = null;
		Field[] udpateFields = null;
		Field conditionField = null;
		//Field updateConditionField = null;
		//Field deleteConditionField = null;
		java.sql.PreparedStatement updatePS = null;
		java.sql.PreparedStatement insertPS = null;
		java.sql.PreparedStatement deletePS = null;
		
		DMLVCellDictionary(ConnectedDictDbDriver argConDictDB,Table argTable,Field[] argUpdateFields,Field argConditionField/*Field argUpdateConditionField,Field argDeleteConditionField*/){
			if(argConDictDB == null || argTable == null || argConditionField == null){
				throw new IllegalArgumentException("Args null");
			}
			this.conDictDB = argConDictDB;
			this.dmlTable = argTable;
			this.udpateFields = argUpdateFields;
			this.conditionField = argConditionField;
		}
		void close() throws java.sql.SQLException{
			try{
				if(insertPS != null){
					insertPS.close();
					insertPS = null;
				}
			}finally{
				try{
					if(updatePS != null){
						updatePS.close();
						updatePS = null;
					}
				}finally{
					if(deletePS != null){
						deletePS.close();
						deletePS = null;
					}
				}
			}
		}
		void insert(String[] values) throws java.sql.SQLException{
			if(insertPS == null){
				String sql = "INSERT INTO "+dmlTable.getTableName()+" VALUES (";
				for(int i = 0; i < values.length;i+= 1){
					sql+=(i!= (values.length-1)?"?,":"?");
				}
				sql=sql+")";
				System.out.println(sql);
				insertPS = conDictDB.getConnection().prepareStatement(sql);
			}
			//insertPS.clearParameters();
			for(int i = 0; i < values.length;i+= 1){
				insertPS.setString(i+1,values[i]);
			}
			insertPS.executeUpdate();
		}
		void update(String[] values,String updateConditionValue) throws java.sql.SQLException{
			if(udpateFields == null){
				throw new RuntimeException("No Fields to Update");
			}
			if(updatePS == null){
				String sql = "UPDATE "+dmlTable.getTableName()+" SET ";
				for(int i = 0; i < udpateFields.length;i+= 1){
					sql+=udpateFields[i]+"="+(i!= (udpateFields.length-1)?"?,":"?");
				}
				sql=sql+" WHERE "+conditionField.toString()+"=?";
				System.out.println(sql);
				updatePS = conDictDB.getConnection().prepareStatement(sql);
			}
			//updatePS.clearParameters();
			for(int i = 0; i < values.length;i+= 1){
				updatePS.setString(i+1,values[i]);
			}
			updatePS.setString(values.length+1,updateConditionValue);
			updatePS.executeUpdate();
		}
		void delete(Object deleteConditionValue) throws java.sql.SQLException{
			if(deletePS == null){
				String sql = "DELETE FROM "+dmlTable.getTableName()+" WHERE "+conditionField.toString()+"=?";
				System.out.println(sql);
				deletePS = conDictDB.getConnection().prepareStatement(sql);
			}
			//deletePS.clearParameters();
			if(deleteConditionValue instanceof java.math.BigDecimal){
				deletePS.setBigDecimal(1,(java.math.BigDecimal)deleteConditionValue);
			}else if(deleteConditionValue instanceof String){
				deletePS.setString(1,(String)deleteConditionValue);
			}
			deletePS.executeUpdate();
		}
	}
	//
	//
	static class ConnectedDictDbDriver {
		
		private java.sql.Connection con = null;
		private cbit.vcell.modeldb.DictionaryDbDriver dictDb = null;
		private ConnectionFactory conFactory = null;
		private KeyFactory keyFactory = null;
		private Object lock = null;
		
		public ConnectedDictDbDriver() throws Exception{
	        SessionLog log = new StdoutSessionLog("DBSpeciesImport");
	        //new cbit.vcell.server.PropertyLoader();
	        if (driverType.equalsIgnoreCase("ORACLE")) {
	            conFactory =
	                new OraclePoolingConnectionFactory(
	                    log,
	                    driverName,
	                    connectURL,
	                    dbSchemaUser,
	                    dbPassword);
	            keyFactory = new OracleKeyFactory();
	        } else if (driverType.equalsIgnoreCase("MYSQL")) {
	                conFactory = new MysqlConnectionFactory();
	                keyFactory = new MysqlKeyFactory();
			}else{
				throw new RuntimeException("Unsupported Driver Type.  Should be 'oracle' -or- 'mysql'.");
			}
			
			//this.lock = new Object();
			//this.con = conFactory.getConnection(lock);
			this.dictDb = new cbit.vcell.modeldb.DictionaryDbDriver(new org.vcell.util.NullSessionLog(),new DBCacheTable(100000,1000000));
		}
		public java.sql.Connection getConnection() throws java.sql.SQLException{
			if(this.con == null){
				this.lock = new Object();
				this.con = conFactory.getConnection(lock);
			}
			return this.con;
		}
		public KeyValue getNewKey() throws Exception{
			return this.keyFactory.getNewKey(getConnection());
		}
		public cbit.vcell.modeldb.DictionaryDbDriver getDictDbDriver(){
			return this.dictDb;
		}
		public void close() throws Exception{
			try{
				if(con != null && conFactory != null && lock != null){
					conFactory.release(con,lock);
				}
			}catch(Exception e){
				//
			}
			try{
				if(con != null){
					this.con.close();
					this.con = null;
				}
			}catch(Exception e){
				//
			}
		}
	}
	//
	//
	static class BoundTableIdToFormalId {
		
		String[] id = null;
		String[] formalID = null;
		String[] foreignKeyRef = null;
		FormalSpeciesType formalSpeciesType = null;

		BoundTableIdToFormalId(FormalSpeciesType argFst,Vector argID,Vector argFormalID,Vector argForeignKeyRef){
			if(	argID == null || 
				argFormalID == null || 
				argForeignKeyRef == null || 
				argFst == null || 
				(argID.size() != argFormalID.size()) || 
				(argID.size() != argForeignKeyRef.size()) || 
				(argID.size() == 0)){
				throw new IllegalArgumentException("BoundTableIdToFormalId: Illegal Arguments");
			}
			for(int i = 0;i <argID.size();i+= 1){
				String currentVal = (String)argID.get(i);
				for(int j = 0;j <argID.size();j+= 1){
					if(currentVal.equals(argID.get(j)) && (i != j)){
						throw new IllegalArgumentException("Values must be unique");
					}
				}
			}
			for(int i = 0;i <argFormalID.size();i+= 1){
				String currentVal = (String)argFormalID.get(i);
				for(int j = 0;j <argFormalID.size();j+= 1){
					if(currentVal.equals(argFormalID.get(j)) && (i != j)){
						throw new IllegalArgumentException("Values must be unique");
					}
				}
			}
			for(int i = 0;i <argForeignKeyRef.size();i+= 1){
				String currentVal = (String)argForeignKeyRef.get(i);
				for(int j = 0;j <argForeignKeyRef.size();j+= 1){
					if(currentVal.equals(argForeignKeyRef.get(j)) && (i != j)){
						throw new IllegalArgumentException("Values must be unique");
					}
				}
			}
			this.id = new String[argID.size()];
			argID.copyInto(this.id);
			this.formalID = new String[argFormalID.size()];
			argFormalID.copyInto(this.formalID);
			this.foreignKeyRef = new String[argForeignKeyRef.size()];
			argForeignKeyRef.copyInto(this.foreignKeyRef);
			this.formalSpeciesType = argFst;
		}
		public String[] getIds(){
			return this.id;
		}
		public String getIdFromFormalId(String argFormalID){
			for(int i = 0;i<id.length;i+= 1){
				if(formalID[i].equals(argFormalID)){
					return id[i];
				}
			}
			return null;
		}
		public String getForeignKeyRefFromFormalId(String argFormalID){
			for(int i = 0;i<id.length;i+= 1){
				if(formalID[i].equals(argFormalID)){
					return foreignKeyRef[i];
				}
			}
			return null;
		}
		public String[] getFormalIds(){
			return this.formalID;
		}
		//public Vector getFormalIdsVector(){
			//Vector v = new Vector();
			//for(int i = 0;i < formalID.length;i+= 1){
				//v.add(formalID[i]);
			//}
			//return v;
		//}
		public FormalSpeciesType getFormalSpeciesType(){
			return this.formalSpeciesType;
		}
	}
	//
	//
	static class RedirectedForeignKeyRef {
		
		Vector id = new Vector();
		Vector old_fkr = new Vector();
		Vector new_fkr = new Vector();
		cbit.sql.Table table = null;
		cbit.sql.Field field = null;
		
		RedirectedForeignKeyRef(cbit.sql.Table argTable,cbit.sql.Field argField){
			if(argTable == null || argField == null){
				throw new IllegalArgumentException("TableIdForeignKeyRef: Parms cannot be null");
			}
			this.table = argTable;
			this.field = argField;
		}
		public void add(String argId,String argOld_fkr,String argNew_fkr){
			if(argId == null || argOld_fkr == null || argNew_fkr == null){
				throw new IllegalArgumentException("TableIdForeignKeyRef.add: Parms cannot be null");
			}
			checkConsistency();
			this.id.add(argId);
			this.old_fkr.add(argOld_fkr);
			this.new_fkr.add(argNew_fkr);
		}
		public void add(String argId,String argOld_fkr){
			if(argId == null || argOld_fkr == null){
				throw new IllegalArgumentException("TableIdForeignKeyRef.add: Parms cannot be null");
			}
			checkConsistency();
			this.id.add(argId);
			this.old_fkr.add(argOld_fkr);
		}
		private void checkConsistency(){
			if(
				id.size() != old_fkr.size() ||
				(new_fkr.size()>0?id.size() != new_fkr.size():false)
			){
				throw new RuntimeException("TableIdForeignKeyRef Inconsistent");
			}
			for(int i = 0;i <id.size();i+= 1){
				String currentVal = (String)id.get(i);
				for(int j = 0;j <id.size();j+= 1){
					if(currentVal.equals(id.get(j)) && (i != j)){
						throw new IllegalArgumentException("Values must be unique");
					}
				}
			}
			for(int i = 0;i <old_fkr.size();i+= 1){
				String currentVal = (String)old_fkr.get(i);
				for(int j = 0;j <old_fkr.size();j+= 1){
					if(currentVal.equals(old_fkr.get(j)) && (i != j)){
						throw new IllegalArgumentException("Values must be unique");
					}
				}
			}
			for(int i = 0;i <new_fkr.size();i+= 1){
				String currentVal = (String)new_fkr.get(i);
				for(int j = 0;j <new_fkr.size();j+= 1){
					if(currentVal.equals(new_fkr.get(j)) && (i != j)){
						throw new IllegalArgumentException("Values must be unique");
					}
				}
			}
		}
		private String[] VectorToStringArray(Vector v){
			if(v == null || v.size() == 0){
				return null;
			}
			String[] results = new String[v.size()];
			v.copyInto(results);
			return results;
		}
		public boolean containsId(String argId){
			return id.contains(argId);
		}
		public String[] getIds(){
			return VectorToStringArray(this.id);
		}
		public String[] getOldForeignKeyRefs(){
			return VectorToStringArray(this.old_fkr);
		}
		public String[] getNewForeignKeyRefs(){
			if(this.new_fkr.size() == 0){
				throw new RuntimeException("No New Foreign Keys Available");
			}
			return VectorToStringArray(this.new_fkr);
		}
		public cbit.sql.Table getTable(){
			return this.table;
		}
		public cbit.sql.Field getField(){
			return this.field;
		}
		public String getUpdateRefSQL(int index){
			if(this.new_fkr.size() == 0){
				throw new RuntimeException("No New Foreign Keys Available");
			}
			if(index >= this.size()){
				throw new IndexOutOfBoundsException("index="+index+" is greater than size="+this.size());
			}
			return "UPDATE "+table.getTableName()+
				" SET "+field.toString()+" = "+(String)new_fkr.get(index)+
				" WHERE "+table.id.toString()+" = "+(String)id.get(index) +
				" AND "+field.toString()+" = "+(String)old_fkr.get(index);
		}
		public int size(){
			return id.size();
		}
	}
	//
/**
 * Insert the method's description here.
 * Creation date: (3/11/2003 12:18:59 PM)
 * @return java.lang.String[]
 * @param proteinBlock java.lang.String
 */
private static String collectProteinLines(String proteinBlock,org.apache.regexp.RE lineSepRegexp) {
	
	String[] linesTemp = lineSepRegexp.split(proteinBlock);
	StringBuffer resultsSB = new StringBuffer();
	StringBuffer fixedLineSB = new StringBuffer();
	int lineIDIndex = 0;
	for(int i = 0; i < linesTemp.length;i+= 1){
		
		boolean bLineSwitch = 	!(
									(linesTemp[lineIDIndex].charAt(0) == linesTemp[i].charAt(0)) &&
									(linesTemp[lineIDIndex].charAt(1) == linesTemp[i].charAt(1))
								);
		boolean bBeginLine = (i == 0) || bLineSwitch;
		boolean bIsEnd = (i == (linesTemp.length-1));
		boolean bLineNeedsSave = bLineSwitch || bIsEnd;
		
		if(bLineSwitch){
			lineIDIndex = i;
		}
		if(bIsEnd){
			if(bBeginLine){
				fixedLineSB.append(linesTemp[i].trim());
			}else{
				fixedLineSB.append(" ");
				fixedLineSB.append(linesTemp[i].substring(2).trim());
			}
		}
		if(bLineNeedsSave){
			resultsSB.append(fixedLineSB.toString());
			resultsSB.append("\n");
			fixedLineSB.setLength(0);
		}
		if(bBeginLine){
			fixedLineSB.append(linesTemp[i].trim());
		}else{
			fixedLineSB.append(" ");
			fixedLineSB.append(linesTemp[i].substring(2).trim());
		}
	}	
	return new String(resultsSB.toString().toCharArray());
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/2003 4:23:47 PM)
 */
public static void createReactions(java.io.InputStream bis,ConnectedDictDbDriver conDictDb,XferObsolete xo,boolean bNoChangeDB) throws Exception{

	if(!bNoChangeDB){
		//Make sure we're starting new transaction
		conDictDb.getConnection().commit();
		conDictDb.getConnection().setAutoCommit(false);
		//
		java.sql.Statement stmt = conDictDb.getConnection().createStatement();
		try{
			try{
				stmt.executeUpdate("DROP TABLE "+EnzymeReactionTable.table.getTableName());
			}catch(java.sql.SQLException e){
				//ORA-00942: table or view does not exist
				if(e.getErrorCode() != 942){
					throw e;
				}
			}
			stmt.executeUpdate(EnzymeReactionTable.table.getCreateSQL());
		}finally{
			if(stmt != null){
				stmt.close();
			}
		}
	}
	//
	int inFileNotInDB = 0;
	int inFileIncomplete = 0;
	int enzymeInDB = 0;
	int compoundInDB = 0;
	int reactionsAdded = 0;
	int reactantsAdded = 0;
	int partialAdded = 0;
	int noEnzymeAdded = 0;
	
	java.io.FileInputStream fis = null;
	java.sql.Statement stmt = null;
	try{
		java.util.Hashtable enzymeToID = null;
		java.util.Hashtable compoundToID = null;
		String sql = null;
		if(conDictDb != null){
			java.sql.ResultSet rset = null;
			// Get link from ecNumber to enzyme table id
			stmt = conDictDb.getConnection().createStatement();
			sql = "SELECT id,ecNumber FROM vc_enzyme";
			rset = stmt.executeQuery(sql);
			enzymeToID = new java.util.Hashtable();
			while(rset.next()){
				String ecNumber = (String)rset.getString(EnzymeTable.table.ecNumber.toString());
				java.math.BigDecimal id = (java.math.BigDecimal)rset.getBigDecimal(EnzymeTable.table.id.toString());
				enzymeToID.put(ecNumber,id);
			}
			stmt.close();
			//String[] dbECNumbers = new String[enzymeToID.size()];
			//enzymeToID.keySet().toArray(dbECNumbers);
			//xo.resolve(dbECNumbers);
			
			// Get link from KEGGid to compound table id
			stmt = conDictDb.getConnection().createStatement();
			sql = "SELECT id,keggID FROM vc_compound";
			rset = stmt.executeQuery(sql);
			compoundToID = new java.util.Hashtable();
			while(rset.next()){
				String keggID = (String)rset.getString(CompoundTable.table.keggID.toString());
				java.math.BigDecimal id = (java.math.BigDecimal)rset.getBigDecimal(CompoundTable.table.id.toString());
				compoundToID.put(keggID,id);
			}
			stmt.close();
			//
			if(!bNoChangeDB){
				stmt = conDictDb.getConnection().createStatement();
			}
	}
		//
		enzymeInDB = (enzymeToID != null?enzymeToID.size():0);
		compoundInDB = (compoundToID!= null?compoundToID.size():0);

		//
		// Read KEGG reaction file
		//
		org.apache.regexp.RECompiler reCompiler = new org.apache.regexp.RECompiler();
		org.apache.regexp.RE regexp = new org.apache.regexp.RE();
		regexp.setMatchFlags(org.apache.regexp.RE.MATCH_SINGLELINE | org.apache.regexp.RE.MATCH_NORMAL);
		//
		org.apache.regexp.REProgram equationProgram = reCompiler.compile("EQUATION\\s*(.+?)\\n\\S");
		org.apache.regexp.REProgram ecNumbersProgram = reCompiler.compile("ENZYME\\s*(.+?)\\n\\S");
		org.apache.regexp.REProgram reactionNumberProgram = reCompiler.compile("ENTRY\\s*(R\\d+)");
		org.apache.regexp.REProgram reactionSepProgram = reCompiler.compile("[+\\s]+");
		//org.apache.regexp.REProgram lineSepProgram = reCompiler.compile("\\n");
		org.apache.regexp.REProgram spaceSepProgram = reCompiler.compile("\\s+");
		org.apache.regexp.REProgram ecNumberProgram = reCompiler.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

		String block = null;
		int counter = 0;
		while((block = readTextBlock(bis,"///")) != null){
			// So system doesn't lockup during long read
			if(counter%100 == 0){
			Thread.sleep(5);
			}
			counter+= 1;
			
			//
			regexp.setProgram(reactionNumberProgram);
			String keggReactionID = null;
			if(regexp.match(block)){
				keggReactionID = regexp.getParen(1).trim();
			}
			//
			regexp.setProgram(equationProgram);
			String reaction = null;
			if(regexp.match(block)){
				reaction = regexp.getParen(1).trim();
				reaction = reaction.replace('\n',' ');
			}
			//
			regexp.setProgram(ecNumbersProgram);
			String[] ecNumbers = null;
			if(regexp.match(block)){
				String ecNumberBlock = null;
				ecNumberBlock = regexp.getParen(1);
				//regexp.setProgram(lineSepProgram);
				//ecNumberBlock = collectProteinLines(ecNumberBlock,regexp);
				regexp.setProgram(spaceSepProgram);
				ecNumbers = regexp.split(ecNumberBlock);
			}
			//
			if(keggReactionID != null && reaction != null){
				if(ecNumbers == null){
					ecNumbers = new String[1];
					ecNumbers[0] = null;
				}else{//resolve all,remove duplicates
					Vector v = new Vector();
					for(int k = 0;k < ecNumbers.length;k+= 1){
						String rtemp = xo.lookup(ecNumbers[k]);
						if(rtemp == null){
							rtemp = ecNumbers[k];
						}
						if(!v.contains(rtemp)){
							v.add(rtemp);
						}
					}
					ecNumbers = new String[v.size()];
					v.copyInto(ecNumbers);
				}
				for(int k = 0;k < ecNumbers.length;k+= 1){
					String ecNumber = ecNumbers[k];
					//There may be reactions without enzymes
					//ECNumbers are "Class.sub-Class.sub-sub-Class.serial#"
					String partialECNumber = null;
					if(ecNumber != null){
						ecNumber = ecNumber.trim();
						regexp.setProgram(ecNumberProgram);
						if(!regexp.match(ecNumber)){
							partialECNumber = ecNumber;
							partialAdded+= 1;
						}
					}
					//int enzymeIDIndex = 0;
					//do{
						//String partialECNumberMatch = null;
						java.math.BigDecimal enzymeID = null;
						String resolvedECNumber = null;
						if(ecNumber != null){
							if(partialECNumber == null){//ecNumber was 1 complete enzymeID not a class of enzymes
								resolvedECNumber = xo.lookup(ecNumber);
								if(resolvedECNumber == null){//Obsolete ecNumber that did not have a substitute, skip it
									System.out.println("Couldn't resolve "+ecNumber);
									continue;
								}
								if(!resolvedECNumber.equals(ecNumber)){
									System.out.println("RESOLVED "+ecNumber +" --> "+resolvedECNumber);
								}
								enzymeID = (java.math.BigDecimal) enzymeToID.get(resolvedECNumber);
							}
						}else{//No ecNumber means no enzyme found for this reaction
							noEnzymeAdded+= 1;
						}
	System.out.println(keggReactionID+" --> "+(resolvedECNumber != null?resolvedECNumber:ecNumber)+" : "+reaction);
						regexp.setProgram(reactionSepProgram);
						String[] equation = regexp.split(reaction);
						String stoichStr = "1";
						boolean isProduct = false;
					//if(enzymeID != null){//There may be Enzymes in the kegg reaction file not in our database
						Vector inserts = new Vector();
						for(int j = 0; j < equation.length;j+= 1){
							if(equation[j].charAt(0) == 'C'){
								int stoichValue = 0;
								if(stoichStr.length() == 1 && stoichStr.charAt(0) == 'n'){
									stoichStr = "1";
								}
								try{
									stoichValue = Integer.parseInt(stoichStr);
								}catch(NumberFormatException e){
									stoichStr = null;
								}
								java.math.BigDecimal compoundID =(java.math.BigDecimal) compoundToID.get(equation[j]);
								if((compoundID == null) || //There may be Compounds in the kegg reaction file not in our database
									(stoichStr == null)) // stoich couldn't be parsed
								{
									inFileNotInDB+= 1;
									inserts = null;
									break;
								}
								//
								sql ="INSERT INTO "+EnzymeReactionTable.table.getTableName()+
										" VALUES "+
										EnzymeReactionTable.table.getSQLValueList(
											conDictDb.getNewKey(),
											keggReactionID,
											((enzymeID!=null)?new KeyValue(enzymeID):null),
											((compoundID!=null)?new KeyValue(compoundID):null),
											stoichValue,
											(isProduct?EnzymeReactionTable.REACANT_TYPE_PRODUCT:EnzymeReactionTable.REACANT_TYPE_REACTANT),
											(resolvedECNumber != null?resolvedECNumber:ecNumber));
								inserts.add(sql);
								//
								stoichStr = "1";
							}else if(equation[j].equals("<=>")){
								isProduct = true;
							}else{
								stoichStr = equation[j];
							}					
						}
						// Do inserts
						if(inserts != null){
							reactionsAdded+= 1;
							reactantsAdded+= inserts.size();
							//stmt = con.createStatement();
							for(int j = 0; j < inserts.size();j+= 1){
								sql = (String)inserts.get(j);
								//System.out.println(sql);
								if(!bNoChangeDB){stmt.executeUpdate(sql);}
							}
							if((reactionsAdded%100) == 0){
								if(!bNoChangeDB){conDictDb.getConnection().commit();}
							}
							//stmt.close();
						}
					//}else{
						//inFileNotInDB+= 1;
					//}
				//}while(enzymeIDIndex < enzymeToID.size());
				}
					
			}else{
				inFileIncomplete+= 1;
			}
		}

		if(!bNoChangeDB){conDictDb.getConnection().commit();}
		//
		System.out.println("There were "+enzymeInDB+" Enzymes in database");
		System.out.println("There were "+compoundInDB+" Compounds in database");
		System.out.println("There were "+reactionsAdded+" insertable Reaction Combos (complete ecNumber,partial ecNumber,no ecNumber) found in reaction File");
		System.out.println("There were "+reactantsAdded+" Reaction Components added to EnzymeReaction Table");
		System.out.println("There were "+inFileIncomplete+" Reactions in file that were incomplete and not added to db");
		System.out.println("There were "+inFileNotInDB+" Reactions not added to DB because of enzymes or compounds in File but not in DB");
		System.out.println("There were "+partialAdded+" Reactions added that had partial ECNumber");
		System.out.println("There were "+noEnzymeAdded+" Reactions added that had no enzyme");
	}finally{
		if(stmt != null){
			stmt.close();
		}
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2003 3:02:42 PM)
 * @return java.lang.String[]
 */
private static DBSpecies[] deleteObsolete(ConnectedDictDbDriver conDictDb,
	DMLVCellDictionary mainDict,
	BoundTableIdToFormalId boundDBSpecies,
	Vector unchangedFormalIDs) throws Exception{
	//
	//Delete Obsolete FormalIDs -OR- return DBFormalSpecies if it is bound
	//
	FormalSpeciesType fst = boundDBSpecies.getFormalSpeciesType();
	int idParm;
	if(fst.equals(FormalSpeciesType.compound)){
		idParm = FormalSpeciesType.COMPOUND_ID;
	}else if (fst.equals(FormalSpeciesType.enzyme)){
		idParm = FormalSpeciesType.ENZYME_ID;
	}else if (fst.equals(FormalSpeciesType.protein)){
		idParm = FormalSpeciesType.PROTEIN_ID;
	}else{
		throw new RuntimeException("Unsupported type="+fst.getName());
	}
	
	//
	Vector results = new Vector();
	//
	java.sql.Statement stmt = null;
	try{
		stmt = conDictDb.getConnection().createStatement();
		for(int i = 0;i < unchangedFormalIDs.size();i+= 1){
			String formalID = (String)unchangedFormalIDs.get(i);
			String boundFKR = boundDBSpecies.getForeignKeyRefFromFormalId(formalID);
			//boolean bDeleteSucceeded = false;
			if(boundFKR == null){
				//try{
					mainDict.delete(formalID);//Aliases go away automatically
					conDictDb.getConnection().commit();
					System.out.println("Obsolete "+fst.getName()+" with formalID="+formalID+" DELETED from Dictionary");
					//bDeleteSucceeded = true;
				//}catch(java.sql.SQLException e){
					//System.out.println("Error Deleteing Obsolete FID="+formalID+" of type "+fst.getName()+" -> "+e.getClass().getName()+":"+e.getMessage());
					//try{
						//conDictDb.getConnection().rollback();
					//}catch(java.sql.SQLException e2){
						////
					//}
				//}
			}else{
			//if(!bDeleteSucceeded){
				//try{
					//String boundID = boundDBSpecies.getIdFromFormalId(formalID);
					DBSpecies dbfs = (DBSpecies)conDictDb.getDictDbDriver().getDatabaseSpecies(
							conDictDb.getConnection(),
							null,
							boundFKR/*boundID*/,
							true,
							fst,
							idParm,-1,false
							)[0];
					results.add(dbfs);
				//}catch(java.sql.SQLException e){
					//System.out.println("Error Recording Bound Obsolete FID="+formalID+" of type "+fst.getName()+" -> "+e.getClass().getName()+":"+e.getMessage());
				//}
			//}
			}
		}
	}catch(Exception e){
		throw new RuntimeException("Error Deleteing Obsolete Entries.  Dictionary may contain Obsolete entries\n"+e.getClass().getName()+" "+e.getMessage());
	}finally{
		if(stmt != null){
			stmt.close();
		}
	}
	if(results.size() > 0){
		DBSpecies[] dbfsArr = new DBSpecies[results.size()];
		results.copyInto(dbfsArr);
		return dbfsArr;
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2003 2:35:04 PM)
 * @param sql java.lang.String
 */
private static void doUpdate(String sql,java.sql.Statement stmt) throws java.sql.SQLException{

	System.out.println(sql);
	
	if(!bPrintOnly){
		stmt.executeUpdate(sql);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/2003 1:35:27 PM)
 * @return java.lang.String[]
 * @param unfixedNames java.lang.String
 */
private static String[] fixNames(String unfixedNamesBlock,org.apache.regexp.RE nameSepRegexp) {
	
	String[] namesTemp = nameSepRegexp.split(unfixedNamesBlock);
	Vector fixedNames = new Vector();
	String fixedName = null;
	for(int i = 0; i < namesTemp.length;i+= 1){
		String unfixedName = namesTemp[i].trim();
		if(unfixedName.charAt(0) == '$'){
			fixedName = fixedName + unfixedName.substring(1);
		}else{
			if(fixedName != null){
				fixedNames.add(fixedName);
			}
			fixedName = unfixedName;
		}
		if(i == (namesTemp.length-1)){
			fixedNames.add(fixedName);
		}
	}
	String[] names = new String[fixedNames.size()];
	fixedNames.copyInto(names);
	return names;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2003 4:47:45 PM)
 */
private static BoundTableIdToFormalId getBoundDBSpecies(java.sql.Connection con,FormalSpeciesType fst) throws Exception{

	//
	//Returns keys=formalID(of type) -> values=DBSpeciesTable.id
	//
	BoundTableIdToFormalId results = null;
	Vector boundIds = new Vector();
	Vector boundFormalIds = new Vector();
	Vector boundForeignKeys = new Vector();
	String sql = null;
	String formalIDAlias= "formalid";
	String foreignKeyAlias= "foreignkey";
	
	if(fst.equals(FormalSpeciesType.compound)){
		sql = "SELECT "+CompoundTable.table.keggID.getQualifiedColName()+" "+formalIDAlias+","+
						DBSpeciesTable.table.compoundRef.getQualifiedColName()+" "+foreignKeyAlias+","+
						DBSpeciesTable.table.id.getQualifiedColName()+
				" FROM "+CompoundTable.table.getTableName()+","+DBSpeciesTable.table.getTableName() +
				" WHERE "+DBSpeciesTable.table.compoundRef.getQualifiedColName()+" IS NOT NULL"+
				" AND "+DBSpeciesTable.table.compoundRef.getQualifiedColName()+"="+CompoundTable.table.id.getQualifiedColName();
	}else if (fst.equals(FormalSpeciesType.enzyme)){
		sql = "SELECT "+EnzymeTable.table.ecNumber.getQualifiedColName()+" "+formalIDAlias+","+
						DBSpeciesTable.table.enzymeRef.getQualifiedColName()+" "+foreignKeyAlias+","+
						DBSpeciesTable.table.id.getQualifiedColName()+
				" FROM "+EnzymeTable.table.getTableName()+","+DBSpeciesTable.table.getTableName() +
				" WHERE "+DBSpeciesTable.table.enzymeRef.getQualifiedColName()+" IS NOT NULL"+
				" AND "+DBSpeciesTable.table.enzymeRef.getQualifiedColName()+"="+EnzymeTable.table.id.getQualifiedColName();
	}else if (fst.equals(FormalSpeciesType.protein)){
		sql = "SELECT "+ProteinTable.table.swissProtEntryName.getQualifiedColName()+" "+formalIDAlias+","+
						DBSpeciesTable.table.proteinRef.getQualifiedColName()+" "+foreignKeyAlias+","+
						DBSpeciesTable.table.id.getQualifiedColName()+
				" FROM "+ProteinTable.table.getTableName()+","+DBSpeciesTable.table.getTableName() +
				" WHERE "+DBSpeciesTable.table.proteinRef.getQualifiedColName()+" IS NOT NULL"+
				" AND "+DBSpeciesTable.table.proteinRef.getQualifiedColName()+"="+ProteinTable.table.id.getQualifiedColName();
	}else{
		throw new RuntimeException("Unsupported type="+fst.getName());
	}

	java.sql.Statement stmt = con.createStatement();
	try{
		java.sql.ResultSet rset = stmt.executeQuery(sql);
		while(rset.next()){
			String formalID = rset.getString(formalIDAlias);
			java.math.BigDecimal dbSpeciesTableID = rset.getBigDecimal(DBSpeciesTable.table.id.getUnqualifiedColName());
			java.math.BigDecimal dbSpeciesTableForeignKey = rset.getBigDecimal(foreignKeyAlias);
			boundIds.add(dbSpeciesTableID.toString());
			boundFormalIds.add(formalID);
			boundForeignKeys.add(dbSpeciesTableForeignKey.toString());
		}
	}finally{
		if(stmt != null){
			stmt.close();
		}
	}
	if(boundIds.size() > 0){
		results = new BoundTableIdToFormalId(fst,boundIds,boundFormalIds,boundForeignKeys);
	}
	return results;
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args){

//oracle frmdev.vcell.uchc.edu vctemp vcell cbittech cmpnd C:\\Temp\\speciesDB\\KEGG_Ligand_25\\compound false
//oracle frmdev.vcell.uchc.edu vctemp vcell cbittech enz C:\\Temp\\speciesDB\\KEGG_Ligand_25\\enzyme false
//oracle frmdev.vcell.uchc.edu vctemp vcell cbittech react C:\\Temp\\speciesDB\\KEGG_Ligand_25\\reaction false C:\\Temp\\speciesDB\\KEGG_Ligand_25\\enzyme

//oracle frmdev.vcell.uchc.edu vctemp vcell cbittech prot C:\\Temp\\speciesDB\\SWISSPROT_41\\sprot40.dat false

	Object lock = null;
	ConnectionFactory conFactory = null;
	java.io.BufferedInputStream bis = null;
	java.io.FileInputStream fis	= null;
	FormalSpeciesType fst = null;
	ConnectedDictDbDriver conDictDB = null;
	
    try {
        if ((args.length < 8) || (args.length > 9)) {
            System.out.println(
                "Usage: (oracle|mysql) host databaseSID schemaUser schemaUserPassword DictType['cmpnd','enz','prot'] DictFile bCheckOnly");
            System.out.println(
                "Usage: (oracle|mysql) host databaseSID schemaUser schemaUserPassword DictType['react'] ReactFile bCheckOnly EnzymeFile");
            System.exit(0);
        }
        driverType = args[0];
        driverName = "oracle.jdbc.driver.OracleDriver";
        String host = args[1];
        String db = args[2];
        connectURL = "jdbc:oracle:thin:@" + host + ":1521:" + db;
        dbSchemaUser = args[3];
        dbPassword = args[4];
        String dictionaryType = args[5];
        java.io.File dictionaryFile = (args[6] != null?new java.io.File(args[6]):null);
		if(!dictionaryFile.exists()){
			throw new RuntimeException("File ="+dictionaryFile.getPath()+" doesn't exist");
		}
		boolean bCheckOnly = (new Boolean(args[7])).booleanValue();
		bPrintOnly = bCheckOnly;
		//
		java.io.File reactionEnzymeFile = null;
		if(args.length == 9){
	        reactionEnzymeFile = (args[8] != null?new java.io.File(args[8]):null);
			if(!reactionEnzymeFile.exists()){
				throw new RuntimeException("File ="+reactionEnzymeFile.getPath()+" doesn't exist");
			}
		}
		//
		String dbSpeciesForeignKeyColumnName = null;
		String dbSpeciesForeignKeyReferencedTable = null;
		if(dictionaryType.equals(DICTTYPE_COMPOUND)){
			fst = FormalSpeciesType.compound;
			dbSpeciesForeignKeyColumnName = DBSpeciesTable.table.compoundRef.toString();
			dbSpeciesForeignKeyReferencedTable = CompoundTable.table.getTableName();
		}else if (dictionaryType.equals(DICTTYPE_ENZYME)){
			fst = FormalSpeciesType.enzyme;
			dbSpeciesForeignKeyColumnName = DBSpeciesTable.table.enzymeRef.toString();
			dbSpeciesForeignKeyReferencedTable = EnzymeTable.table.getTableName();
		}else if (dictionaryType.equals(DICTTYPE_PROTEIN)){
			fst = FormalSpeciesType.protein;
			dbSpeciesForeignKeyColumnName = DBSpeciesTable.table.proteinRef.toString();
			dbSpeciesForeignKeyReferencedTable = ProteinTable.table.getTableName();
		}else if (dictionaryType.equals(DICTTYPE_REACTION)){
			fst = null;
		}else{
			throw new RuntimeException("Unknown DictionaryType="+dictionaryType);
		}
		//
        int ok =
            javax.swing.JOptionPane.showConfirmDialog(
                new javax.swing.JFrame(),""
                	+ (!bCheckOnly?"EXTREME DANGER!!!!\nDictionary Tables in Schema below will be DESTROYED and re-created":"")
                	//+ (!bCheckOnly && (fst.equals(FormalSpeciesType.compound) || fst.equals(FormalSpeciesType.enzyme))?"\nReactionTable will be Dropped":"")
                	+ (!bCheckOnly && fst != null && fst.equals(FormalSpeciesType.compound)?"\nCompoundTable and CompoundAliasTable will be Dropped":"")
                	+ (!bCheckOnly && fst != null && fst.equals(FormalSpeciesType.enzyme)?"\nEnzymeTable and EnzymeAliasTable will be Dropped":"")
                	+ (!bCheckOnly && fst != null && fst.equals(FormalSpeciesType.protein)?"\nProteinTable and ProteinAliasTable will be Dropped":"")
                    + "\nconnectURL="
                    + connectURL
                    + "\nUser="
                    + dbSchemaUser
                    + "\npassword="
                    + dbPassword
                    + "\nDictionaryType="
                    + dictionaryType
                    + "\nDictionaryFile="
                    + dictionaryFile.getPath()
                    + "\nDatabase Will "+(bCheckOnly?"NOT":"")+" Be Updated(Changed)"
                    ,
                "Confirm",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);
        if (ok != javax.swing.JOptionPane.OK_OPTION) {
            throw new RuntimeException("Aborted by user");
        }
		//
		//
		fis = new java.io.FileInputStream(dictionaryFile);
		bis = new java.io.BufferedInputStream(fis);
		//
		if(dictionaryType.equals(DICTTYPE_REACTION)){
			conDictDB = new ConnectedDictDbDriver();
			XferObsolete xo = resolveEnzymeECNumbers(conDictDB.getConnection(),reactionEnzymeFile);
			createReactions(bis,conDictDB,xo,bPrintOnly);
		}else{
			if(!bPrintOnly){
				conDictDB = new ConnectedDictDbDriver();
			}
			//Get entries form DBspeciesTable for FormalSpeciesType
			BoundTableIdToFormalId boundDBSpecies = null;
			if(conDictDB != null){
				boundDBSpecies = getBoundDBSpecies(conDictDB.getConnection(),fst);
			}
			//
			if(dictionaryType.equals(DICTTYPE_COMPOUND)){
				updateFormalCompounds(bis,conDictDB,boundDBSpecies);
			}else if (dictionaryType.equals(DICTTYPE_ENZYME)){
				updateFormalEnzymes(dictionaryFile,conDictDB,boundDBSpecies);
			}else if (dictionaryType.equals(DICTTYPE_PROTEIN)){
				updateFormalProteins(bis,conDictDB,boundDBSpecies);
			}else{
				throw new RuntimeException("Unknown DictionaryType="+dictionaryType);
			}
		}
		
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    }finally{
	    try{
		    if(conDictDB != null){
			    conDictDB.close();
		    }
	    }catch(Exception e){
		    //
	    }
	    try{
		   if(bis != null){
			    bis.close();
		   }
	    }catch(Exception e){  
	    	//
	    }
	    try{
		    if(fis != null){
			    fis.close();
		    }
	    }catch(Exception e){
		    //
	    }
    }
    System.exit(0);
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2003 9:58:45 PM)
 * @return java.lang.String
 * @param is java.io.InputStream
 * @param separator java.lang.String
 */
private static String readTextBlock(java.io.InputStream is, String blockSeparator) throws java.io.IOException{

	StringBuffer block = new StringBuffer();
	int currentChar;
	int matchCount = 0;
	while((currentChar = is.read()) != -1){

		if( (block.length() == 0) && Character.isWhitespace((char)currentChar)){
			continue;
		}
		block.append((char)currentChar);
		
		if((char)currentChar == blockSeparator.charAt(matchCount)){
			matchCount+= 1;
			if(matchCount == blockSeparator.length()){
				break;
			}
		}else{
			matchCount = 0;
		}
	}

	if(block.length() == 0){
		return null;
	}
	return block.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2003 3:41:29 PM)
 * @param enzymeFile java.io.File
 */
private static XferObsolete resolveEnzymeECNumbers(java.sql.Connection con,java.io.File enzymeFile) throws Exception{

	org.apache.regexp.RECompiler reCompiler = new org.apache.regexp.RECompiler();
	org.apache.regexp.RE regexp = new org.apache.regexp.RE();
	regexp.setMatchFlags(org.apache.regexp.RE.MATCH_SINGLELINE | org.apache.regexp.RE.MATCH_NORMAL);
				
	org.apache.regexp.REProgram ecNumberProgram = reCompiler.compile("ENTRY\\s*EC\\s([\\d\\.-]+).+?\\n");
	org.apache.regexp.REProgram namesProgram = reCompiler.compile("NAME\\s*(.+?)\\n\\S");
	org.apache.regexp.REProgram commentProgram = reCompiler.compile("COMMENT\\s*(.+?)\\n\\S");
	org.apache.regexp.REProgram identicalProgram = reCompiler.compile("IDENTICAL\\s*TO\\s*EC\\s*([\\d\\.]+)");
	org.apache.regexp.REProgram includedProgram = reCompiler.compile("NOW\\s*INCLUDED\\s*WITH\\s*EC\\s*([\\d\\.]+)");
	org.apache.regexp.REProgram nameSepProgram = reCompiler.compile("\\n");

	//
	java.io.BufferedInputStream bis = new java.io.BufferedInputStream(new java.io.FileInputStream(enzymeFile));
	//
	Vector resolvableObsoleteECNumbers = new Vector();
	XferObsolete xo = new XferObsolete();
	//
	try{
		Vector xferFrom = new Vector();
		Vector xferTo = new Vector();
		String obsoleteECNumber = null;
		String block = null;
		int counter = 1;
		while((block = readTextBlock(bis,"///")) != null){
			// So system doesn't lockup during long read
			if(counter%100 == 0){
				System.out.println("resolveEnzyme "+counter);
				Thread.sleep(5);
			}
			counter+= 1;
			//
			regexp.setProgram(ecNumberProgram);
			String ecNumber = null;
			if(regexp.match(block)){
				if(regexp.getParen(0).indexOf("Obsolete") == -1){
					ecNumber = new String(regexp.getParen(1).trim().toCharArray());
				}else{
					obsoleteECNumber = new String(regexp.getParen(1).trim().toCharArray());
				}
			}
			//
			regexp.setProgram(commentProgram);
			String comment = null;
			if(regexp.match(block)){
				comment = regexp.getParen(1).trim().toUpperCase();
			}
			//
			regexp.setProgram(namesProgram);
			String[] names = null;
			if(regexp.match(block)){
				String namesBlock = regexp.getParen(1).trim();
				regexp.setProgram(nameSepProgram);
				names = fixNames(namesBlock,regexp);
			}
			if(names != null && obsoleteECNumber != null){
				String xferECNumber = null;
				String xferToS = "Transferred to ";
				if(names[0].indexOf(xferToS) != -1){
					xferECNumber = new String(names[0].substring(names[0].indexOf(xferToS)+xferToS.length()).trim().toCharArray());
					//See if there are multiple XFer choices, just take first one if so
					if(xferECNumber.indexOf(",") != -1){
						xferECNumber = xferECNumber.substring(0,xferECNumber.indexOf(","));
						System.out.println("WARNING----- Obsolete EC="+obsoleteECNumber+" XFerred to "+xferECNumber+" from multiple list "+names[0]);
					}else if(xferECNumber.indexOf(" ") != -1){
						xferECNumber = xferECNumber.substring(0,xferECNumber.indexOf(" "));
						System.out.println("WARNING----- Obsolete EC="+obsoleteECNumber+" XFerred to "+xferECNumber+" from multiple list "+names[0]);
					}
				}else if(names[0].indexOf("Deleted entry") != -1 && (comment != null)){
					regexp.setProgram(identicalProgram);
					if(regexp.match(comment)){
						xferECNumber = regexp.getParen(1);
						System.out.println(obsoleteECNumber+" Identical to "+xferECNumber);
					}else{
						regexp.setProgram(includedProgram);
						if(regexp.match(comment)){
							xferECNumber = regexp.getParen(1);
							System.out.println(obsoleteECNumber+" Now included with "+xferECNumber);
						}else{
							System.out.println(obsoleteECNumber+" No XFer found");
						}
					}
				}else{
					throw new RuntimeException(obsoleteECNumber+" "+names[0]+" Unknown Obsolete Enzyme type");
				}
				//
				if(xferECNumber != null){
					if(xferFrom.contains(obsoleteECNumber)){
						throw new RuntimeException("Duplicate OBSOLETE TRANSFER ecnumber in transfer list");
					}
					xferFrom.add(obsoleteECNumber);
					xferTo.add(xferECNumber);
					xo.xfer(obsoleteECNumber,xferECNumber);
					resolvableObsoleteECNumbers.add(obsoleteECNumber);
				}
				//
				obsoleteECNumber = null;
				continue;
			}
		}
	}finally{
		if(bis != null){
			bis.close();
		}
	}
	//
	if(con != null){
		con.commit();
		Vector masterECNumbersV = new Vector();
		java.sql.Statement stmt = con.createStatement();
		try{
			java.sql.ResultSet rset = stmt.executeQuery("SELECT "+EnzymeTable.table.ecNumber.toString()+" FROM "+EnzymeTable.table.getTableName());
			while(rset.next()){
				String dbECNumber = rset.getString(EnzymeTable.table.ecNumber.toString());
				if(!resolvableObsoleteECNumbers.contains(dbECNumber)){
					masterECNumbersV.add(dbECNumber);
				}
			}
		}finally{
			if(stmt != null){
				stmt.close();
			}
		}
		String[] masterECNumbers = new String[masterECNumbersV.size()];
		masterECNumbersV.copyInto(masterECNumbers);
		//
		xo.resolve(masterECNumbers);
	}
	//
	return xo;
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2003 9:50:56 PM)
 * @return cbit.vcell.dictionary.CompoundInfo[]
 * @param con java.sql.Connection
 * @param fileName java.lang.String
 */
public static void updateFormalCompounds(java.io.InputStream bis,ConnectedDictDbDriver conDictDb,BoundTableIdToFormalId boundDBSpecies) throws Exception{

	TableColumnsAssociation compoundIDToKeggID = null;
	Vector updatedFormaIDs = new Vector();
	Vector insertedFormalIDs = new Vector();
	Vector unchangedFormalIDs = null;
	Vector errorFormalIDs = new Vector();
	DMLVCellDictionary mainDict = null;
	DMLVCellDictionary aliasDict = null;	
	if(conDictDb != null){
		//Make sure we're starting new transaction
		conDictDb.getConnection().commit();
		conDictDb.getConnection().setAutoCommit(false);
		compoundIDToKeggID = new TableColumnsAssociation(conDictDb.getConnection(),CompoundTable.table.getTableName(),CompoundTable.table.id.toString(),CompoundTable.table.keggID.toString());
		unchangedFormalIDs = compoundIDToKeggID.getBValues();
		mainDict = new DMLVCellDictionary(conDictDb,CompoundTable.table,
				new Field[]{CompoundTable.table.formula,CompoundTable.table.casID},
				CompoundTable.table.keggID);
		aliasDict = new DMLVCellDictionary(conDictDb,CompoundAliasTable.table,null,CompoundAliasTable.table.compoundRef);
	}
	//
	//
	//
	org.apache.regexp.RECompiler reCompiler = new org.apache.regexp.RECompiler();
	org.apache.regexp.RE regexp = new org.apache.regexp.RE();
	regexp.setMatchFlags(org.apache.regexp.RE.MATCH_SINGLELINE | org.apache.regexp.RE.MATCH_NORMAL);
	
	int compoundCount = 0;
	int skippedCount =0;
			
	org.apache.regexp.REProgram keggIDProgram = reCompiler.compile("ENTRY\\s*(C\\d+)");
	org.apache.regexp.REProgram namesProgram = reCompiler.compile("NAME\\s*(.+?)\\n\\S");
	org.apache.regexp.REProgram formulaProgram = reCompiler.compile("FORMULA\\s*(.+?)\\n");
	org.apache.regexp.REProgram casIDProgram = reCompiler.compile("DBLINKS\\s*CAS:\\s(.+?)\\n");
	org.apache.regexp.REProgram nameSepProgram = reCompiler.compile("\\n");
	try{
	String block = null;
	int counter = 1;
	long beginTime = System.currentTimeMillis();
	while((block = readTextBlock(bis,"///")) != null){
		// So IDE system doesn't lockup during long read
		if(counter%100 == 0){
			System.out.println(counter);
			Thread.sleep(5);
		}
		counter+= 1;
		//
		regexp.setProgram(keggIDProgram);
		String keggID = null;
		if(regexp.match(block)){
				keggID = new String(regexp.getParen(1).trim().toCharArray());
		}
		//
		regexp.setProgram(namesProgram);
		String[] names = null;
		if(regexp.match(block)){
			String namesBlock = regexp.getParen(1).trim();
			regexp.setProgram(nameSepProgram);
			names = fixNames(namesBlock,regexp);
		}
		//
		regexp.setProgram(formulaProgram);
		String formula = null;
		if(regexp.match(block)){
			formula = regexp.getParen(1).trim();
		}
		//
		regexp.setProgram(casIDProgram);
		String casID = null;
		if(regexp.match(block)){
			casID = regexp.getParen(1).trim();
		}

		//
		//
		//
		//
		if(names != null && names.length != 0 && keggID != null){
			compoundCount+= 1;
			CompoundInfo compoundInfo = new CompoundInfo(keggID,names,formula,casID,null);
			//
			if(conDictDb != null){
				try{
					String mainDictID = compoundIDToKeggID.getAValueUsingBValue(compoundInfo.getFormalID());
					if(mainDictID != null){
						mainDict.update(new String[]{compoundInfo.getFormula(),compoundInfo.getCasID()},compoundInfo.getFormalID());
						unchangedFormalIDs.remove(compoundInfo.getFormalID());
						updatedFormaIDs.add(compoundInfo.getFormalID());
					}else{
						mainDictID = conDictDb.getNewKey().toString();
						mainDict.insert(new String[]{
									mainDictID,
									compoundInfo.getFormula(),
									compoundInfo.getCasID(),
									compoundInfo.getFormalID()});
						insertedFormalIDs.add(compoundInfo.getFormalID());
						System.out.println("Inserted="+compoundInfo.toString());
					}
					//
					aliasDict.delete(new java.math.BigDecimal(mainDictID));
					//
					for(int i = 0;i < names.length;i+= 1){
						KeyValue aliasKeyValue = conDictDb.getNewKey();
						aliasDict.insert(new String[]{aliasKeyValue.toString(),mainDictID,names[i],(i==0?"T":"F")});
					}
					conDictDb.getConnection().commit();
				}catch(java.sql.SQLException e){
					conDictDb.getConnection().rollback();
					System.out.println(e.getClass().getName()+"\n"+e.getMessage()+"\n"+compoundInfo.toString());
					errorFormalIDs.add(compoundInfo.getFormalID());
				}
			}
		}else{
			skippedCount+= 1;
		}
	}
	//
	if(conDictDb != null){
		DBSpecies[] dbfs = deleteObsolete(conDictDb,mainDict,boundDBSpecies,unchangedFormalIDs);
		if(dbfs != null){
			System.out.println(	"----------------------WARNING BEGIN----------------------\n"+
								"Obsolete Compounds NOT DELETED from Dictionary because they are Bound.\n"+
								"Automatic Rebinding Ambiguous.  You MUST MANUALLY REBIND compoundRef in DBSpeciesTable to appropriate Compound\n"+
								"--------------------------------------------------------\n"
							);
			for(int i = 0;i < dbfs.length;i+= 1){
				System.out.println(dbfs[i]);
			}
			System.out.println(	"----------------------WARNING END------------------------\n");
		}
	}
	//
	System.out.println("Seconds for Compound Insert="+((System.currentTimeMillis()-beginTime)/1000));
	}finally{
		if(mainDict != null){
			try{
				mainDict.close();
			}catch(Exception e){
				//
			}
		}
		if(aliasDict != null){
			try{
				aliasDict.close();
			}catch(Exception e){
				//
			}
		}
	}
	System.out.println("Compound Count="+compoundCount+" Skipped Count="+skippedCount+" Error Count="+errorFormalIDs.size());
	System.out.println("Updated Count="+updatedFormaIDs.size()+" Inserted Count="+insertedFormalIDs.size()+" Obsolete Count="+unchangedFormalIDs.size());
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2003 9:50:56 PM)
 * @return cbit.vcell.dictionary.EnzymeInfo[]
 * @param con java.sql.Connection
 * @param fileName java.lang.String
 */
public static void updateFormalEnzymes(java.io.File enzymeFile,ConnectedDictDbDriver conDictDb,BoundTableIdToFormalId boundDBSpecies) throws Exception{

	TableColumnsAssociation enzymeIDToECNumber = null;
	Vector updatedFormaIDs = new Vector();
	Vector insertedFormalIDs = new Vector();
	Vector unchangedFormalIDs = null;
	Vector errorFormalIDs = new Vector();
	DMLVCellDictionary mainDict = null;
	DMLVCellDictionary aliasDict = null;	
	if(conDictDb != null){
		//Make sure we're starting new transaction
		conDictDb.getConnection().commit();
		conDictDb.getConnection().setAutoCommit(false);
		enzymeIDToECNumber = new TableColumnsAssociation(conDictDb.getConnection(),EnzymeTable.table.getTableName(),EnzymeTable.table.id.toString(),EnzymeTable.table.ecNumber.toString());
		unchangedFormalIDs = enzymeIDToECNumber.getBValues();
		mainDict = new DMLVCellDictionary(conDictDb,EnzymeTable.table,
				new Field[]{EnzymeTable.table.reaction,EnzymeTable.table.sysname,EnzymeTable.table.casID},
				EnzymeTable.table.ecNumber);
		aliasDict = new DMLVCellDictionary(conDictDb,EnzymeAliasTable.table,null,EnzymeAliasTable.table.enzymeRef);
	}
	//
	//
	//
	//
	
	org.apache.regexp.RECompiler reCompiler = new org.apache.regexp.RECompiler();
	org.apache.regexp.RE regexp = new org.apache.regexp.RE();
	regexp.setMatchFlags(org.apache.regexp.RE.MATCH_SINGLELINE | org.apache.regexp.RE.MATCH_NORMAL);
	
	int obsoleteCount = 0;
	int skippedCount = 0;
	int enzymeCount = 0;
	
	Vector deletedECNumber = new Vector();
			
	org.apache.regexp.REProgram ecNumberProgram = reCompiler.compile("ENTRY\\s*EC\\s([\\d\\.-]+).+?\\n");
	org.apache.regexp.REProgram namesProgram = reCompiler.compile("NAME\\s*(.+?)\\n\\S");
	org.apache.regexp.REProgram sysnameProgram = reCompiler.compile("SYSNAME\\s*(.+?)\\n\\S");
	org.apache.regexp.REProgram casIDProgram = reCompiler.compile("DBLINKS.+?CAS:\\s(.+?)\\n");
	org.apache.regexp.REProgram reactionProgram = reCompiler.compile("REACTION\\s*(.+?)\\n\\S");
	org.apache.regexp.REProgram nameSepProgram = reCompiler.compile("\\n");
	//
	//
	java.io.BufferedInputStream bis = new java.io.BufferedInputStream(new java.io.FileInputStream(enzymeFile));
	//
	long beginTime = System.currentTimeMillis();
	try{
		String block = null;
		int counter = 1;
		while((block = readTextBlock(bis,"///")) != null){
			// So system doesn't lockup during long read
			if(counter%100 == 0){
				System.out.println(counter);
				Thread.sleep(5);
			}
			counter+= 1;
			//
			regexp.setProgram(ecNumberProgram);
			String ecNumber = null;
			if(regexp.match(block)){
				if(regexp.getParen(0).indexOf("Obsolete") == -1){
					ecNumber = new String(regexp.getParen(1).trim().toCharArray());
				}else{
					//obsoleteECNumber = new String(regexp.getParen(1).trim().toCharArray());
					obsoleteCount+= 1;
				}
			}
			//
			regexp.setProgram(namesProgram);
			String[] names = null;
			if(regexp.match(block)){
				String namesBlock = regexp.getParen(1).trim();
				regexp.setProgram(nameSepProgram);
				names = fixNames(namesBlock,regexp);
			}
			//
			regexp.setProgram(sysnameProgram);
			String sysname = null;
			if(regexp.match(block)){
				String sysnameBlock = regexp.getParen(1).trim();
				regexp.setProgram(nameSepProgram);
				sysname = fixNames(sysnameBlock,regexp)[0];
			}
			//
			regexp.setProgram(casIDProgram);
			String casID = null;
			if(regexp.match(block)){
				casID = regexp.getParen(1).trim();
			}
			//
			regexp.setProgram(reactionProgram);
			String reaction = null;
			if(regexp.match(block)){
				String reactionBlock = regexp.getParen(1).trim();
				regexp.setProgram(nameSepProgram);
				reaction = fixNames(reactionBlock,regexp)[0];
			}

		//
		//
		//
		//
		if(names != null && names.length != 0 && ecNumber != null){
			enzymeCount+= 1;
			EnzymeInfo enzymeInfo = new EnzymeInfo(ecNumber,names,reaction,sysname,casID);
			//
			if(conDictDb != null){
				try{
				String mainDictID = enzymeIDToECNumber.getAValueUsingBValue(enzymeInfo.getFormalID());
				if(mainDictID != null){
					mainDict.update(new String[]{enzymeInfo.getReaction(),enzymeInfo.getSysname(),enzymeInfo.getCasID()},enzymeInfo.getFormalID());
					unchangedFormalIDs.remove(enzymeInfo.getFormalID());
					updatedFormaIDs.add(enzymeInfo.getFormalID());
				}else{
					mainDictID = conDictDb.getNewKey().toString();
					mainDict.insert(new String[]{
								mainDictID,
								enzymeInfo.getReaction(),
								enzymeInfo.getFormalID(),
								enzymeInfo.getSysname(),
								enzymeInfo.getCasID()});
					insertedFormalIDs.add(enzymeInfo.getFormalID());
					System.out.println("Inserted="+enzymeInfo.toString());
				}
				//
				aliasDict.delete(new java.math.BigDecimal(mainDictID));
				//
				for(int i = 0;i < names.length;i+= 1){
					KeyValue aliasKeyValue = conDictDb.getNewKey();
					aliasDict.insert(new String[]{aliasKeyValue.toString(),mainDictID,names[i],(i==0?"T":"F")});
				}
				conDictDb.getConnection().commit();
				}catch(java.sql.SQLException e){
					conDictDb.getConnection().rollback();
					System.out.println(e.getClass().getName()+"\n"+e.getMessage()+"\n"+enzymeInfo.toString());
					errorFormalIDs.add(enzymeInfo.getFormalID());
				}
			}
		}else{
			skippedCount+= 1;
		}
	}
		//
	if(conDictDb != null){
		DBSpecies[] boundObsolete = deleteObsolete(conDictDb,mainDict,boundDBSpecies,unchangedFormalIDs);
		//Try to automatically Resolve Bound Obsolete ECNumbers
		if(boundObsolete != null){
			//
			XferObsolete xo = resolveEnzymeECNumbers(conDictDb.getConnection(),enzymeFile);
			java.util.List unresolvedObsolete = java.util.Arrays.asList(xo.getNotResolved());
			//
			//Redirect DBSpeciesTable Obsolete EnzymeRef to New EnzymeRef
			java.sql.Statement stmt = null;
			try{
				String sql;
				stmt = conDictDb.getConnection().createStatement();
				for(int i = 0;i < boundObsolete.length;i+= 1){
					String fromFormalID = boundObsolete[i].getFormalSpeciesInfo().getFormalID();
					String toFormalID = xo.lookup(fromFormalID);
					if(toFormalID != null){
						String toEnzymeID = null;
						try{
							java.sql.ResultSet rset = stmt.executeQuery("SELECT id FROM "+EnzymeTable.table.tableName+" WHERE "+EnzymeTable.table.ecNumber.toString()+" = "+"'"+toFormalID+"'");
							if(rset.next()){
								toEnzymeID = rset.getBigDecimal("id").toString();
								if(rset.next()){
									throw new RuntimeException("Expecting only 1 id for ecnumber="+toFormalID);
								}
							}else{
								throw new RuntimeException("Couldn't find id for ecnumber="+toFormalID);
							}
							rset.close();
							//
							String fromEnzymeID = boundDBSpecies.getForeignKeyRefFromFormalId(fromFormalID);
							sql = 	"UPDATE "+DBSpeciesTable.table.tableName+
									" SET "+DBSpeciesTable.table.enzymeRef.toString()+"="+toEnzymeID+
									" WHERE "+
									DBSpeciesTable.table.enzymeRef.toString()+"="+fromEnzymeID;

							int rowsUpdated = stmt.executeUpdate(sql);
							if(rowsUpdated == 1){
								stmt.executeUpdate("DELETE FROM "+EnzymeTable.table.tableName+" WHERE id="+fromEnzymeID);//Aliases go automatically
								conDictDb.getConnection().commit();
								boundObsolete[i] = null;
								System.out.println("BoundSpecies.id="+boundDBSpecies.getIdFromFormalId(fromFormalID)+
													" enzymeRef="+fromEnzymeID+
													" FID="+fromFormalID+
													" transferred to "+
													" enzymeRef="+toEnzymeID+
													" FID="+toFormalID);
							}else{
								throw new RuntimeException("ERROR------------\n"+sql+"\nUpdated "+rowsUpdated+" rows.  Expecting only 1 update");
							}
						}catch(Exception e){
							System.out.println("Error Rebinding: "+fromFormalID+" To "+toFormalID+"\n"+e.getClass().getName()+"\n"+e.getMessage());
							try{
								conDictDb.getConnection().rollback();
							}catch(java.sql.SQLException e2){
								//do Nothing
							}
						}
					}
				}
			}finally{
				if(stmt != null){
					stmt.close();
				}
			}
			//Print warning for Bound Obsolete ECNumbers that couldn't be resolved
			boolean bWarned = false;
			for(int i = 0;i < boundObsolete.length;i+= 1){
				if(boundObsolete[i] != null){
					if(!bWarned){
						System.out.println(	"----------------------WARNING BEGIN----------------------\n"+
											"Obsolete Enzymes NOT DELETED from Dictionary because they are Bound.\n"+
											"Automatic Rebinding Ambiguous.  You MUST MANUALLY REBIND enzymeRef in DBSpeciesTable to appropriate Enzyme\n"+
											"--------------------------------------------------------"
										);
					}
					if(unresolvedObsolete != null && unresolvedObsolete.contains(boundObsolete[i].getFormalSpeciesInfo().getFormalID())){
						System.out.print("XFER Unresolved - ");
					}
					System.out.println(boundObsolete[i]);
					bWarned = true;
				}
				
			}
			if(bWarned){
				System.out.println(	"----------------------WARNING END------------------------");
			}
		}
	}
	System.out.println("Seconds for Enzyme Insert="+((System.currentTimeMillis()-beginTime)/1000));
	}finally{
		if(mainDict != null){
			try{
				mainDict.close();
			}catch(Exception e){
				//
			}
		}
		if(aliasDict != null){
			try{
				aliasDict.close();
			}catch(Exception e){
				//
			}
		}
		if(bis != null){
			bis.close();
		}
	}
	System.out.println("Enzyme Count="+enzymeCount+" Skipped Count="+skippedCount+" Error Count="+errorFormalIDs.size());
	System.out.println("Updated Count="+updatedFormaIDs.size()+" Inserted Count="+insertedFormalIDs.size()+" Obsolete Count="+unchangedFormalIDs.size());
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2003 9:50:56 PM)
 * @return cbit.vcell.dictionary.ProteinInfo[]
 * @param con java.sql.Connection
 * @param fileName java.lang.String
 */
public static void updateFormalProteins(java.io.InputStream bis,ConnectedDictDbDriver conDictDb,BoundTableIdToFormalId boundDBSpecies) throws Exception{

	String[] names = new String[1];
	String[] values4 = new String[4];
	String[] values5 = new String[5];
	String[] values7 = new String[7];
	
	TableColumnsAssociation proteinIDToSwissProtID = null;
	Vector updatedFormaIDs = new Vector(10000,10000);
	Vector insertedFormalIDs = new Vector(10000,10000);
	Vector unchangedFormalIDs = null;
	Vector errorFormalIDs = new Vector();
	DMLVCellDictionary mainDict = null;
	DMLVCellDictionary aliasDict = null;	
	if(conDictDb != null){
		
		////set Batching
		//if(conDictDb.getConnection() instanceof oracle.jdbc.OracleConnection){
			//((oracle.jdbc.OracleConnection)conDictDb.getConnection()).setDefaultExecuteBatch(25);
		//}
		
		//Make sure we're starting new transaction
		conDictDb.getConnection().commit();
		conDictDb.getConnection().setAutoCommit(false);
		proteinIDToSwissProtID = new TableColumnsAssociation(conDictDb.getConnection(),ProteinTable.table.getTableName(),ProteinTable.table.id.toString(),ProteinTable.table.swissProtEntryName.toString());
		unchangedFormalIDs = proteinIDToSwissProtID.getBValues();
		mainDict = new DMLVCellDictionary(conDictDb,ProteinTable.table,
				new Field[]{ProteinTable.table.organism,
							ProteinTable.table.accessionNumber,
							ProteinTable.table.keywords,
							ProteinTable.table.description,
							ProteinTable.table.molWeight},
				ProteinTable.table.swissProtEntryName);
		aliasDict = new DMLVCellDictionary(conDictDb,ProteinAliasTable.table,null,ProteinAliasTable.table.proteinRef);
	}
	//
	//
	//
	//
	org.apache.regexp.RECompiler reCompiler = new org.apache.regexp.RECompiler();
	org.apache.regexp.RE regexp = new org.apache.regexp.RE();
	regexp.setMatchFlags(org.apache.regexp.RE.MATCH_MULTILINE | org.apache.regexp.RE.MATCH_NORMAL);
	
	int proteinCount = 0;
	int skippedCount = 0;
	
	org.apache.regexp.REProgram swissprotIDProgram = 	reCompiler.compile("^ID\\s*(\\S+)");
	org.apache.regexp.REProgram accessionProgram = 		reCompiler.compile("\\nAC\\s*(.+?)\\n");
	org.apache.regexp.REProgram nameProgram = 			reCompiler.compile("\\nDE\\s*(.+?)[\\.\\(\\[]");
	org.apache.regexp.REProgram descrProgram = 			reCompiler.compile("\\nDE(.+?)([\\(\\[].+?)\\n");
	org.apache.regexp.REProgram organismProgram = 		reCompiler.compile("\\nOS\\s*(.+?)\\.+\\n");
	org.apache.regexp.REProgram keywordProgram = 		reCompiler.compile("\\nKW\\s*(.+?)\\.+\\n");
	org.apache.regexp.REProgram mwProgram = 			reCompiler.compile("\\nSQ.*\\s(\\d*)\\sMW;");
	org.apache.regexp.REProgram lineSepProgram = 		reCompiler.compile("\\n");
	//
	String collectedProteinLines = null;
	//
	try{
	String block = null;
	int counter = 1;
	long beginTime = System.currentTimeMillis();
	while((block = readTextBlock(bis,"//\n")) != null){
		// So system doesn't lockup during long read in IDE
		if(counter%100 == 0){
			System.out.println(counter+" minutes="+((float)(System.currentTimeMillis()-beginTime)/(float)60000));
			Thread.sleep(50);
		}
		if(counter%1000 == 0){
			if(conDictDb != null){
				//mainDict.close();
				//aliasDict.close();
				//conDictDb.close();
				//regexp = new org.apache.regexp.RE();
				//regexp.setMatchFlags(org.apache.regexp.RE.MATCH_MULTILINE | org.apache.regexp.RE.MATCH_NORMAL);
			}
		}
		counter+= 1;
		//
		regexp.setProgram(lineSepProgram);
		collectedProteinLines = collectProteinLines(block,regexp);
		//
		String swissprotID = null;
		String accession = null;
		String organism = null;
		String name = null;
		String keyword = null;
		String descr = null;
		String molecularWeight = null;
		//
		regexp.setProgram(swissprotIDProgram);
		if(regexp.match(collectedProteinLines)){
			swissprotID = new String(regexp.getParen(1).trim().toCharArray());//Do this to avoid OutOfMemoryError
		}
		//
		regexp.setProgram(accessionProgram);
		if(regexp.match(collectedProteinLines)){
			accession = regexp.getParen(1).trim();
		}
		//
		regexp.setProgram(organismProgram);
		if(regexp.match(collectedProteinLines)){
			organism = regexp.getParen(1).trim();
		}
		//
		regexp.setProgram(keywordProgram);
		if(regexp.match(collectedProteinLines)){
			keyword = regexp.getParen(1).trim();
		}
		//
		regexp.setProgram(nameProgram);
		if(regexp.match(collectedProteinLines)){
			name = regexp.getParen(1);
		}
		//
		regexp.setProgram(mwProgram);
		if(regexp.match(collectedProteinLines)){
			molecularWeight = regexp.getParen(1);
		}

		//
		regexp.setProgram(descrProgram);
		if(regexp.match(collectedProteinLines)){
			descr = regexp.getParen(2);//2nd subexpression
		}

	//
	//
		if(name != null && swissprotID != null){
			//
			names[0] = name;
			//
			proteinCount+= 1;
			ProteinInfo proteinInfo = new ProteinInfo(swissprotID,names,organism,accession,keyword,descr,(molecularWeight != null?Double.parseDouble(molecularWeight):ProteinInfo.UNKNOWN_MW));
			//
			if(conDictDb != null){
				//try{
					String mainDictID = proteinIDToSwissProtID.getAValueUsingBValue(proteinInfo.getFormalID());
					if(mainDictID != null){
						values5[0] = proteinInfo.getOrganism();
						values5[1] = proteinInfo.getAccession();
						values5[2] = proteinInfo.getKeyWords();
						values5[3] = proteinInfo.getDescription();
						values5[4] = proteinInfo.getMolecularWeight()+"";
						mainDict.update(values5,swissprotID);

						unchangedFormalIDs.remove(proteinInfo.getFormalID());
						updatedFormaIDs.add(proteinInfo.getFormalID());
					}else{
						mainDictID = conDictDb.getNewKey().toString();
						values7[0] = mainDictID;
						values7[1] = proteinInfo.getOrganism();
						values7[2] = proteinInfo.getAccession();
						values7[3] = proteinInfo.getFormalID();
						values7[4] = proteinInfo.getKeyWords();
						values7[5] = proteinInfo.getDescription();
						values7[6] = proteinInfo.getMolecularWeight()+"";
						mainDict.insert(values7);
						insertedFormalIDs.add(proteinInfo.getFormalID());
						System.out.println("Inserted="+proteinInfo.getFormalID());
					}
					//
					aliasDict.delete(new java.math.BigDecimal(mainDictID));
					//
					for(int i = 0;i < names.length;i+= 1){
						KeyValue aliasKeyValue = conDictDb.getNewKey();
						values4[0] = aliasKeyValue.toString();
						values4[1] = mainDictID;
						values4[2] = names[i];
						values4[3] = (i==0?"T":"F");
						aliasDict.insert(values4);
					}
					if(counter%1000 == 0){
						conDictDb.getConnection().commit();
					}
				//}catch(java.sql.SQLException e){
					//conDictDb.getConnection().rollback();
					//System.out.println(e.getClass().getName()+"\n"+e.getMessage()+"\n"+proteinInfo.getFormalID());
					//errorFormalIDs.add(proteinInfo.getFormalID());
				//}
			}
		}else{
			skippedCount+= 1;
		}
	}
	//
	if(conDictDb != null){
		conDictDb.getConnection().commit();
		DBSpecies[] dbfs = deleteObsolete(conDictDb,mainDict,boundDBSpecies,unchangedFormalIDs);
		//Try to resolve with Primary Accession Number
		if(dbfs != null){
			for(int i=0;i<dbfs.length;i+= 1){
				KeyValue proteinKey = dbfs[i].getDBFormalSpeciesKey();
				java.sql.Statement stmt = null;
				try{
					String primaryAccessionNumber = null;
					stmt = conDictDb.getConnection().createStatement();
					//Get PrimaryAccessionNumber
					java.sql.ResultSet rset = stmt.executeQuery(
						"SELECT SUBSTR(accessionnumber,1,INSTR(LTRIM(accessionnumber),';',1,1)-1)" +
						" FROM "+ProteinTable.table.getTableName() +
						" WHERE " +ProteinTable.table.id.toString() + " = " + dbfs[i].getDBFormalSpeciesKey()
						);
					if(rset.next()){
						primaryAccessionNumber = rset.getString(1);
					}else{
						throw new RuntimeException("Couldn't find obsolete dbfs id="+dbfs[i].toString());
					}
					rset.close();
					// Find matching NEW protein
					KeyValue reassignKey = null;
					rset = stmt.executeQuery(
						"SELECT " + ProteinTable.table.id.toString() +
						" FROM " + ProteinTable.table.getTableName() +
						" WHERE " + ProteinTable.table.id.toString() + " != " + dbfs[i].getDBFormalSpeciesKey() +
						" AND " +
						" LTRIM("+ProteinTable.table.accessionNumber.toString()+")" + " LIKE '"+primaryAccessionNumber+";%'"
						);
					if(rset.next()){
						reassignKey = new KeyValue(rset.getBigDecimal(1));
						if(rset.next()){
							throw new RuntimeException("Found more than 1 reassignment PrimaryAccessionNumber for obsolete dbfs id="+dbfs[i].toString());
						};
					}else{
						throw new RuntimeException("Couldn't find reassignment PrimaryAccessionNumber for obsolete dbfs id="+dbfs[i].toString());
					}
					rset.close();
					//Do ReAssignment
					int updateCount = stmt.executeUpdate(
					//System.out.println(
						"UPDATE "+DBSpeciesTable.table.getTableName() +
						" SET " + DBSpeciesTable.table.proteinRef.toString() + " = " +reassignKey +
						" WHERE " + DBSpeciesTable.table.id.toString() + " = " + dbfs[i].getDBSpeciesKey() +
						" AND " + DBSpeciesTable.table.proteinRef.toString() + " = " + dbfs[i].getDBFormalSpeciesKey()
						);
					if(updateCount != 1){
						throw new RuntimeException("Update returned "+updateCount+", Expecting to update only 1 row");
					}
					stmt.executeUpdate(
					//System.out.println(
						"DELETE FROM "+ProteinTable.table.getTableName()+
						" WHERE "+
						ProteinTable.table.id.toString() + " = " + dbfs[i].getDBFormalSpeciesKey()
						);

					conDictDb.getConnection().commit();
					System.out.println("Success Reassigning "+dbfs[i]+" to "+ProteinTable.table.getTableName()+".id="+reassignKey);
				}catch(Throwable e){
					conDictDb.getConnection().rollback();
					System.out.println("---Error Reassigning "+dbfs[i]+" -- "+e.getMessage());
				}finally{
					if(stmt != null){stmt.close();}
				}
			}
		}
		//if(dbfs != null){
			//System.out.println(	"----------------------WARNING BEGIN----------------------\n"+
								//"Obsolete Proteins NOT DELETED from Dictionary because they are Bound.\n"+
								//"Automatic Rebinding Ambiguous.  You MUST MANUALLY REBIND proteinRef in DBSpeciesTable to appropriate Protein\n"+
								//"--------------------------------------------------------\n"
							//);
			//for(int i = 0;i < dbfs.length;i+= 1){
				//System.out.println(dbfs[i]);
				
			//}
			//System.out.println(	"----------------------WARNING END------------------------\n");
		//}
	}
	//
	System.out.println("Seconds for Protein Insert="+((System.currentTimeMillis()-beginTime)/1000));
	}finally{
		if(mainDict != null){
			try{
				mainDict.close();
			}catch(Exception e){
				//
			}
		}
		if(aliasDict != null){
			try{
				aliasDict.close();
			}catch(Exception e){
				//
			}
		}
	}
	System.out.println("Protein Count="+proteinCount+" Skipped Count="+skippedCount);
	//System.out.println("Error Count="+errorFormalIDs.size());
	System.out.println("Updated Count="+updatedFormaIDs.size());
	System.out.println("Inserted Count="+insertedFormalIDs.size()+" Obsolete Count="+(unchangedFormalIDs != null?unchangedFormalIDs.size():0));
	
}
}
