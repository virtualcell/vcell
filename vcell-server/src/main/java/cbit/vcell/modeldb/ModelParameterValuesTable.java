package cbit.vcell.modeldb;

import cbit.sql.Field;
import cbit.sql.Table;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringJoiner;

/**
 * Defines the vc_model_parameter_values table and how to read rows from it.
 */
public class ModelParameterValuesTable extends Table {
    private static final String TABLE_NAME = "vc_model_parameter_values";

    /** FK back to vc_model_export_history.id */
    public final Field exportId           = new Field("export_id",           Field.SQLDataType.integer,       "NOT NULL REFERENCES vc_model_export_history(id)");
    public final Field userRef            = new Field("user_ref",            Field.SQLDataType.integer,       "NOT NULL " + UserTable.REF_TYPE);
    public final Field modelRef           = new Field("model_ref",           Field.SQLDataType.integer,       "NOT NULL " + ModelTable.REF_TYPE);
    public final Field parameterName      = new Field("parameter_name",      Field.SQLDataType.varchar_50,    "NOT NULL");
    public final Field parameterOriginal  = new Field("parameter_original",  Field.SQLDataType.number_as_real,       "NOT NULL");
    public final Field parameterChanged   = new Field("parameter_changed",   Field.SQLDataType.number_as_real,       "NOT NULL");

    private final Field[] fields = {
            exportId,
            userRef,
            modelRef,
            parameterName,
            parameterOriginal,
            parameterChanged
    };

    private final Field[] insertFields = {
            exportId, userRef, modelRef, parameterName, parameterOriginal, parameterChanged
    };

    public static final ModelParameterValuesTable table = new ModelParameterValuesTable();

    private ModelParameterValuesTable() {
        super(TABLE_NAME);
        addFields(fields);
    }


    public String getInsertSQL() {
        StringJoiner cols = new StringJoiner(",", "(", ")");
        for (Field f : insertFields) {
            cols.add(f.getQualifiedColName());
        }
        StringJoiner ph = new StringJoiner(",", "(", ")");
        for (int i = 0; i < insertFields.length; i++) {
            ph.add("?");
        }
        return "INSERT INTO " + TABLE_NAME + " " + cols.toString() + " VALUES " + ph.toString();
    }

    public void bindForInsert(
            PreparedStatement ps,
            long            exportIdValue,
            long            userRefValue,
            long            modelRefValue,
            String          parameterNameValue,
            BigDecimal      originalValue,
            BigDecimal      changedValue
    ) throws SQLException {
        int i=1;
        ps.setLong       (i++, exportIdValue);
        ps.setLong       (i++, userRefValue);
        ps.setLong       (i++, modelRefValue);
        ps.setString     (i++, parameterNameValue);
        ps.setBigDecimal (i++, originalValue);
        ps.setBigDecimal (i++, changedValue);
    }

    public record ModelParameterValueRecord(long userRef, long modelRef, String parameterName,
                                            BigDecimal parameterOriginal, BigDecimal parameterChanged) {
    }


    public ModelParameterValueRecord getModelParameterValueRecord(ResultSet rset, ModelParameterValueRecord modelParameterValueRecord) throws SQLException {
        long userRef           = rset.getLong((int) modelParameterValueRecord.userRef);
        long modelRef           = rset.getLong((int) modelParameterValueRecord.modelRef);
        String paramName        = rset.getString(parameterName.toString());
        BigDecimal origVal        = rset.getBigDecimal(parameterOriginal.toString());
        BigDecimal changedVal     = rset.getBigDecimal(parameterChanged.toString());

        return new ModelParameterValueRecord(
                userRef,
                modelRef,
                paramName,
                origVal,
                changedVal
        );
    }
}
