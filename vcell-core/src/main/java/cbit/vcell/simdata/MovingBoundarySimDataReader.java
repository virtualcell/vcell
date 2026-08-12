package cbit.vcell.simdata;

import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMeshMovingBoundary;
import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;

import java.io.File;
import java.util.Vector;

public class MovingBoundarySimDataReader {
    /** the Solution group, which holds one group per saved time */
    private static Group solutionGroup(HdfFile solFile) throws Exception {
        for (Node member : solFile.getChildren().values())
        {
            if (member instanceof Group
                    && CartesianMeshMovingBoundary.MBSDataGroup.valueOf(member.getName())
                    == CartesianMeshMovingBoundary.MBSDataGroup.Solution)
            {
                return (Group) member;
            }
        }
        throw new Exception("Group " + CartesianMeshMovingBoundary.MBSDataGroup.Solution + " not found");
    }

    private static String stringAttribute(Node node, CartesianMeshMovingBoundary.MSBDataAttribute which)
    {
        Attribute attribute = node.getAttribute(which.name());
        if (attribute == null)
        {
            return null;
        }
        Object value = attribute.getData();
        return value instanceof String[] ? ((String[]) value)[0] : String.valueOf(value);
    }

    public static void readMBSDataMetadata(String fileName, Vector<DataBlock> dataBlockList) throws Exception
    {
        try (HdfFile solFile = new HdfFile(new File(fileName).toPath()))
        {
            // any time group will do: they all carry the same variables
            Group timeGroup = null;
            for (Node member : solutionGroup(solFile).getChildren().values())
            {
                if (member instanceof Group && member.getName().startsWith("time"))
                {
                    timeGroup = (Group) member;
                    break;
                }
            }
            if (timeGroup == null)
            {
                throw new Exception("No time group found");
            }

            for (Node member : timeGroup.getChildren().values())
            {
                if (!(member instanceof Dataset))
                {
                    continue;
                }
                String varName = stringAttribute(member, CartesianMeshMovingBoundary.MSBDataAttribute.name);
                Attribute sizeAttribute = member.getAttribute(CartesianMeshMovingBoundary.MSBDataAttribute.size.name());
                int size = sizeAttribute == null ? 0 : intValue(sizeAttribute.getData());
                String vt = stringAttribute(member, CartesianMeshMovingBoundary.MSBDataAttribute.type);

                VariableType varType = null;
                if (CartesianMeshMovingBoundary.MSBDataAttributeValue.Point.name().equals(vt))
                {
                    varType = VariableType.POINT_VARIABLE;
                }
                else if (CartesianMeshMovingBoundary.MSBDataAttributeValue.Volume.name().equals(vt))
                {
                    varType = VariableType.VOLUME;
                }
                // PointSubDomain is a position, not a displayed variable

                if (varType == VariableType.VOLUME || varType == VariableType.POINT_VARIABLE)
                {
                    dataBlockList.addElement(DataBlock.createDataBlock(varName, varType.getType(), size, 0));
                }
            }
        }
    }

    private static int intValue(Object value)
    {
        if (value instanceof int[])
        {
            return ((int[]) value)[0];
        }
        if (value instanceof long[])
        {
            return (int) ((long[]) value)[0];
        }
        return ((Number) value).intValue();
    }

    private static double doubleValue(Object value)
    {
        if (value instanceof double[])
        {
            return ((double[]) value)[0];
        }
        if (value instanceof float[])
        {
            return ((float[]) value)[0];
        }
        return ((Number) value).doubleValue();
    }

    public static double[] readMBSData(String fileName, Vector<DataBlock> dataBlockList, String varName, Double time) throws Exception {
        try (HdfFile solFile = new HdfFile(new File(fileName).toPath()))
        {
            int size = 0;
            boolean found = false;
            for (DataBlock dataBlock : dataBlockList)
            {
                if (dataBlock.getVarName().equals(varName))
                {
                    size = dataBlock.getSize();
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                throw new Exception("Variable " + varName + " not found");
            }

            Group timeGroup = null;
            for (Node member : solutionGroup(solFile).getChildren().values())
            {
                if (!(member instanceof Group))
                {
                    continue;
                }
                Attribute timeAttribute = member.getAttribute(CartesianMeshMovingBoundary.MSBDataAttribute.time.name());
                if (timeAttribute != null && Math.abs(doubleValue(timeAttribute.getData()) - time) < 1e-8)
                {
                    timeGroup = (Group) member;
                    break;
                }
            }
            if (timeGroup == null)
            {
                throw new Exception("No time group found for time=" + time);
            }

            Dataset varDataset = null;
            for (Node member : timeGroup.getChildren().values())
            {
                if (member instanceof Dataset
                        && varName.equals(stringAttribute(member, CartesianMeshMovingBoundary.MSBDataAttribute.name)))
                {
                    varDataset = (Dataset) member;
                    break;
                }
            }
            if (varDataset == null)
            {
                throw new Exception("Data for Variable " + varName + " at time " + time + " not found");
            }

            double[] data = new double[size];
            System.arraycopy((double[]) varDataset.getData(), 0, data, 0, size);
            return data;
        }
    }

}
