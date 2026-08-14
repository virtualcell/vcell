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

    /**
     * How far the nearest saved time may be from the requested one before it is treated as a
     * different time altogether.
     *
     * The requested time has been through the .log file, which carries six significant digits, so
     * the error scales with the magnitude of the time rather than being a fixed quantity -- an
     * absolute bound is either too tight for late times or needlessly loose for early ones. The
     * relative part covers the rounding; the absolute floor keeps t=0 and very small times from
     * demanding exactness they cannot have.
     *
     * 1e-4 is chosen with margin at both ends. Six significant digits is a relative error of at
     * most 5e-6 -- worst when the leading digit is 1, which is why the error is not the 5e-7 that
     * "six digits" suggests: 0.0104058 stands for 0.010405830662209813, a relative error of 3e-6.
     * So this is roughly 20x the largest rounding error possible, while still well inside the
     * spacing of saved times (1e-3 apart in the file this was diagnosed on, i.e. a relative gap of
     * about 1e-3 at t=1, ten times this bound). Nearest-match does the real work; this only decides
     * when to declare that no saved time corresponds to the request at all.
     *
     * Package-visible so the rule can be tested against real values without an HDF5 fixture.
     */
    static double timeMatchTolerance(double time)
    {
        return Math.max(1e-9, 1e-4 * Math.abs(time));
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

            // Match the nearest saved time rather than requiring near-equality.
            //
            // The caller's time comes from the .log file, which the solver writes with C's "%g" --
            // six significant digits. The time attributes in the HDF5 file are full doubles, so the
            // two disagree by up to a part in 1e6: for this file the logged 0.122269 stands for
            // 0.1222685102809653, a difference of 4.9e-7. An exact-ish match therefore fails for
            // almost every time point; on a 386-point simulation only the 20 earliest times, where
            // six digits happen to be enough, could be read at all. The rest failed with
            // "No time group found", which reads like missing data rather than a rounding problem.
            //
            // Nearest-match is the right shape regardless of how the log is formatted. The bound
            // below only rejects a time that belongs to no saved point at all: saved times are far
            // apart compared with the rounding error (here 1.0e-3 apart versus a 4.0e-6 worst-case
            // error), so nearest is unambiguous by three orders of magnitude.
            Group timeGroup = null;
            double closestDelta = Double.MAX_VALUE;
            for (Node member : solutionGroup(solFile).getChildren().values())
            {
                if (!(member instanceof Group))
                {
                    continue;
                }
                Attribute timeAttribute = member.getAttribute(CartesianMeshMovingBoundary.MSBDataAttribute.time.name());
                if (timeAttribute == null)
                {
                    continue;
                }
                double delta = Math.abs(doubleValue(timeAttribute.getData()) - time);
                if (delta < closestDelta)
                {
                    closestDelta = delta;
                    timeGroup = (Group) member;
                }
            }
            if (timeGroup == null || closestDelta > timeMatchTolerance(time))
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
