package cbit.vcell.modeldb;
import cbit.util.ISize;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.*;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.DataAccessException;
import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class SurfaceDescriptionTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_surfacedesc";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field geometryRef	= new Field("geometryRef",		"integer",	"NOT NULL "+GeometryTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field sampleSizeX	= new Field("sampleSizeX",		"integer",	"NOT NULL");
	public final Field sampleSizeY	= new Field("sampleSizeY",		"integer",	"NOT NULL");
	public final Field sampleSizeZ	= new Field("sampleSizeZ",		"integer",	"NOT NULL");
	public final Field filterFreq	= new Field("filterFreq",		"NUMBER",	"NOT NULL");
	
	private final Field fields[] = {geometryRef,sampleSizeX,sampleSizeY,sampleSizeZ,filterFreq};
	
	public static final SurfaceDescriptionTable table = new SurfaceDescriptionTable();

/**
 * ModelTable constructor comment.
 */
private SurfaceDescriptionTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, GeometrySurfaceDescription geometrySurfaceDescription, KeyValue geomKey) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key + ",");
	buffer.append(geomKey + ",");
	cbit.util.ISize sampleSize = geometrySurfaceDescription.getVolumeSampleSize();
	if (sampleSize==null){
		throw new DataAccessException("sampleSize is null in GeometrySurfaceDescription, cannot save");
	}
	buffer.append(sampleSize.getX() + ",");
	buffer.append(sampleSize.getY() + ",");
	buffer.append(sampleSize.getZ() + ",");
	Double filterCutoffFrequency = geometrySurfaceDescription.getFilterCutoffFrequency();
	if (filterCutoffFrequency==null){
		throw new DataAccessException("filterCutoffFrequency is null in GeometrySurfaceDescription, cannot save");
	}
	buffer.append(filterCutoffFrequency.doubleValue());
	buffer.append(")");
	
	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return Model
 * @param rset ResultSet
 * @param log SessionLog
 */
public void populateGeometrySurfaceDescription(ResultSet rset, GeometrySurfaceDescription geometrySurfaceDescription, SessionLog log) throws SQLException, java.beans.PropertyVetoException, DataAccessException {
	
	int sizeX = rset.getInt(this.sampleSizeX.toString());
	int sizeY = rset.getInt(this.sampleSizeY.toString());
	int sizeZ = rset.getInt(this.sampleSizeZ.toString());
	ISize sampleSize = new ISize(sizeX,sizeY,sizeZ);

	double filterCutoff = rset.getBigDecimal(this.filterFreq.toString()).doubleValue();

	geometrySurfaceDescription.setVolumeSampleSize(sampleSize);
	geometrySurfaceDescription.setFilterCutoffFrequency(new Double(filterCutoff));
}
}