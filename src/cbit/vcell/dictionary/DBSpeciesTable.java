package cbit.vcell.dictionary;

import cbit.sql.Field;
import cbit.sql.Table;

/**
 * Insert the type's description here.
 * Creation date: (2/6/2003 3:57:02 PM)
 * @author: Frank Morgan
 */
public class DBSpeciesTable extends cbit.sql.Table {
	
    private static final String TABLE_NAME = "vc_dbspecies";
    public static final String REF_TYPE =
        "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] dbSpeciesTableConstraint =
    			new String[] {
    			"com_enz_pro_unique UNIQUE(compoundRef,enzymeRef,proteinRef)",
    			"com_enz_pro_only_1 CHECK(DECODE(compoundref,NULL,0,compoundref,1)+DECODE(enzymeref,NULL,0,enzymeref,1)+DECODE(proteinref,NULL,0,proteinref,1) = 1)"};

    public final Field compoundRef 	= new Field("compoundRef", 	CompoundTable.REF_TYPE, "");
	public final Field enzymeRef 	= new Field("enzymeRef", 	EnzymeTable.REF_TYPE, "");
	public final Field proteinRef 	= new Field("proteinRef", 	ProteinTable.REF_TYPE, "");
	
	private final Field fields[] = {compoundRef,enzymeRef,proteinRef};

    public static final DBSpeciesTable table = new DBSpeciesTable();

    			
/**
 * DBSpeciesTable constructor comment.
 * @param argTableName java.lang.String
 */
public DBSpeciesTable() {
	super(TABLE_NAME,dbSpeciesTableConstraint);
	addFields(fields);
}
/**
 * Returns an SQL String with a value list taken from the parameter Compound
 * @return java.lang.String
 * @param key KeyValue
 * @param compound Compound
 */
public String getSQLValueList(cbit.sql.KeyValue key, DBFormalSpecies dbfs) {

    StringBuffer buffer = new StringBuffer();
    buffer.append("(");
    buffer.append(key + ",");
	buffer.append((dbfs.getFormalSpeciesType().equals(FormalSpeciesType.compound)?dbfs.getDBFormalSpeciesKey().toString():"null") + ",");
	buffer.append((dbfs.getFormalSpeciesType().equals(FormalSpeciesType.enzyme)?dbfs.getDBFormalSpeciesKey().toString():"null") + ",");
	buffer.append((dbfs.getFormalSpeciesType().equals(FormalSpeciesType.protein)?dbfs.getDBFormalSpeciesKey().toString():"null"));
	buffer.append(")");

    return buffer.toString();
}
}
