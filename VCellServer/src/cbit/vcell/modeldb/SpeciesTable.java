package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
import cbit.vcell.model.*;
import cbit.vcell.server.*;
import cbit.vcell.dictionary.*;
/**
 * This type was created in VisualAge.
 */
public class SpeciesTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_species";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field commonName		= new Field("commonName",	"varchar(255)",		"NOT NULL");
	public final Field dbSpeciesRef		= new Field("dbSpeciesRef",	"integer",			DBSpeciesTable.REF_TYPE);
	public final Field annotation		= new Field("annotation",	"varchar(1024)",	"");
	
	private final Field fields[] = {commonName,dbSpeciesRef,annotation};
	
	public static final SpeciesTable table = new SpeciesTable();
/**
 * ModelTable constructor comment.
 */
private SpeciesTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 * @param rset java.sql.ResultSet
 */
public Species getSpecies(java.sql.ResultSet rset, SessionLog log,DBSpecies dbSpecies) throws java.sql.SQLException, DataAccessException {
	
	String annotation = rset.getString(SpeciesTable.table.annotation.toString());
	if (annotation!=null){
		annotation = cbit.util.TokenMangler.getSQLRestoredString(annotation);
	}
	String cNameStr = rset.getString(SpeciesTable.table.commonName.toString());
	Species species = new Species(cNameStr,annotation,dbSpecies);
	return species;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue ownerKey, Species species) throws DataAccessException {

//	int defaultCharge = 0;

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append("'"+species.getCommonName()+"'"+",");
	KeyValue dbSpeciesKey = (species.getDBSpecies() == null ? null :species.getDBSpecies().getDBSpeciesKey());
	buffer.append((dbSpeciesKey != null?dbSpeciesKey.toString():"null")+",");
	buffer.append((species.getAnnotation() != null ? "'"+cbit.util.TokenMangler.getSQLEscapedString(species.getAnnotation())+"'" : "null")+")");
	
	return buffer.toString();
}
}
