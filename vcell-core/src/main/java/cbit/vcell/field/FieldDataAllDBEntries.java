package cbit.vcell.field;

import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * ID's and information on all field data owned
 */
public class FieldDataAllDBEntries {
    public ExternalDataIdentifier[] ids;
    public String[] annotationsForIds;
    public HashMap<ExternalDataIdentifier, Vector<KeyValue>> edisToSimRefs;
}
