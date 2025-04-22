package cbit.vcell.field.io;

import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMesh;

public class FieldData extends FieldDataSpec{
    public short[][][] data;
    public double[][][] doubleData;

    public VariableType[] variableTypes;
    public CartesianMesh cartesianMesh;
}
