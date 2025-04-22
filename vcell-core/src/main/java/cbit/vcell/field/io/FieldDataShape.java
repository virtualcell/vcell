package cbit.vcell.field.io;

import cbit.vcell.simdata.DataIdentifier;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

public class FieldDataShape {
    public Extent extent;
    public Origin origin;
    public ISize iSize;

    /**
     * Data IDs for variables (what domain are they within, are they a function, their type, and name)
     */
    public DataIdentifier[] variableInformation;
    public double[] times;
}
