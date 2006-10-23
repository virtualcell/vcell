package cbit.vcell.modeldb;

import org.vcell.expression.IExpression;

import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.Table;
import cbit.util.DataAccessException;
import cbit.util.document.KeyValue;
import cbit.vcell.modelapp.CurrentClampStimulus;
import cbit.vcell.modelapp.ElectricalStimulus;
import cbit.vcell.modelapp.Electrode;
import cbit.vcell.modelapp.VoltageClampStimulus;
/**
 * This type was created in VisualAge.
 */
public class StimulusTable extends cbit.sql.Table {

	final static int UNKNOWN_STIMULUS = 0;
	final static int CURRENT_CLAMP_STIMULUS = 1;
	final static int VOLTAGE_CLAMP_STIMULUS = 2;
	final static int GROUND_ELECTRODE = 3;
	
	private static final String TABLE_NAME = "vc_stimulus";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field structRef		= new Field("structRef",	"integer",	"NOT NULL "+StructTable.REF_TYPE);
	public final Field simContextRef	= new Field("simContextRef","integer",	"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field name				= new Field("name",	        "varchar(255)",		"");
	public final Field stimulusType 	= new Field("stimulusType",	"integer",	"NOT NULL");
	public final Field expression		= new Field("expression",	"varchar(255)",		"");
	public final Field positionX		= new Field("posX",			"NUMBER",	"NOT NULL");
	public final Field positionY		= new Field("posY",			"NUMBER",	"NOT NULL");
	public final Field positionZ		= new Field("posZ",			"NUMBER",	"NOT NULL");
	public final Field params			= new Field("params",		"VARCHAR2(4000)",	"");


	private final Field fields[] = {structRef,simContextRef,name,stimulusType,
									expression,positionX,positionY,positionZ,params};
	
	public static final StimulusTable table = new StimulusTable();

/**
 * ModelTable constructor comment.
 */
private StimulusTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(InsertHashtable hash, KeyValue Key, KeyValue simContextKey, ElectricalStimulus stimulus) throws DataAccessException {

	KeyValue structureKey = hash.getDatabaseKey(stimulus.getElectrode().getFeature());
	if (structureKey==null){
		structureKey = stimulus.getElectrode().getFeature().getKey();
		if (structureKey==null){
			throw new DataAccessException("no key for structure "+stimulus.getElectrode().getFeature());
		}
	}

	IExpression exp = null;
	int stimulusType = UNKNOWN_STIMULUS;
	if (stimulus instanceof CurrentClampStimulus){
		stimulusType = CURRENT_CLAMP_STIMULUS;
		exp = ((CurrentClampStimulus)stimulus).getCurrentParameter().getExpression();
	}else if (stimulus instanceof VoltageClampStimulus){
		stimulusType = VOLTAGE_CLAMP_STIMULUS;
		exp = ((VoltageClampStimulus)stimulus).getVoltageParameter().getExpression();
	}else{
		throw new DataAccessException("storage for Stimulus type "+stimulus.getClass().getName()+" not yet supported");
	}

	java.io.StringWriter esParameterWriter = new java.io.StringWriter();
	java.io.PrintWriter pw = new java.io.PrintWriter(esParameterWriter);
	stimulus.parameterVCMLWrite(pw);
	pw.flush();
	pw.close();

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Key + ",");
	buffer.append(structureKey + ",");
	buffer.append(simContextKey + ",");
	buffer.append("'"+stimulus.getName()+"',");
	buffer.append(stimulusType+",");
	buffer.append("'"+exp.infix()+"',");
	buffer.append(stimulus.getElectrode().getPosition().getX()+",");
	buffer.append(stimulus.getElectrode().getPosition().getY()+",");
	buffer.append(stimulus.getElectrode().getPosition().getZ()+",");
	buffer.append("'"+cbit.util.TokenMangler.getSQLEscapedString(esParameterWriter.getBuffer().toString())+"'");
	buffer.append(")");

	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(InsertHashtable hash, KeyValue Key, KeyValue simContextKey, Electrode electrode) throws DataAccessException {

	KeyValue structureKey = hash.getDatabaseKey(electrode.getFeature());
	if (structureKey==null){
		structureKey = electrode.getFeature().getKey();
		if (structureKey==null){
			throw new DataAccessException("no key for structure "+electrode.getFeature());
		}
	}

	int stimulusType = GROUND_ELECTRODE;

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Key + ",");
	buffer.append(structureKey + ",");
	buffer.append(simContextKey + ",");
	buffer.append("null,");
	buffer.append(stimulusType+",");
	buffer.append("null,"); // expression is null
	buffer.append(electrode.getPosition().getX()+",");
	buffer.append(electrode.getPosition().getY()+",");
	buffer.append(electrode.getPosition().getZ()+",");
	buffer.append("NULL");//params is null
	buffer.append(")");

	return buffer.toString();
}
}