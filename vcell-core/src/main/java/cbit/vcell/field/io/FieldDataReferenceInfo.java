package cbit.vcell.field.io;

import org.vcell.util.document.KeyValue;

public class FieldDataReferenceInfo {
    public static final String FIELDDATATYPENAME = "FieldData";
    public String referenceSourceType;
    public String referenceSourceName;
    public String applicationName;
    public String simulationName;
    public String refSourceVersionDate;
    public String[] funcNames;
    public KeyValue refSourceVersionKey;

}
