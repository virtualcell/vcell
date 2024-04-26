package cbit.vcell.simdata;

import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMeshMovingBoundary;
import ncsa.hdf.object.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Vector;

public class MovingBoundarySimDataReader {
    public static void readMBSDataMetadata(String fileName, Vector<DataBlock> dataBlockList) throws Exception
    {
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
        FileFormat solFile = null;
        try {
            solFile = fileFormat.createInstance(fileName, FileFormat.READ);
            solFile.open();
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)solFile.getRootNode();
            Group rootGroup = (Group)rootNode.getUserObject();
            Group solutionGroup = null;
            for (Object member : rootGroup.getMemberList())
            {
                String memberName = ((HObject)member).getName();
                if (member instanceof Group)
                {
                    CartesianMeshMovingBoundary.MBSDataGroup group = CartesianMeshMovingBoundary.MBSDataGroup.valueOf(memberName);
                    if (group == CartesianMeshMovingBoundary.MBSDataGroup.Solution)
                    {
                        solutionGroup = (Group) member;
                        break;
                    }
                }
            }
            if (solutionGroup == null)
            {
                throw new Exception("Group " + CartesianMeshMovingBoundary.MBSDataGroup.Solution + " not found");
            }

            // find any timeGroup
            Group timeGroup = null;
            for (Object member : solutionGroup.getMemberList())
            {
                String memberName = ((HObject)member).getName();
                if (member instanceof Group && memberName.startsWith("time"))
                {
                    timeGroup = (Group) member;
                    break;
                }
            }

            if (timeGroup == null)
            {
                throw new Exception("No time group found");
            }

            // find all the datasets in that time group
            for (Object member : timeGroup.getMemberList())
            {
                if (member instanceof Dataset)
                {
                    List<Attribute> solAttrList = ((Dataset)member).getMetadata();
                    int size = 0;
                    String varName = null;
                    VariableType varType = null;
                    for (Attribute attr : solAttrList)
                    {
                        String attrName = attr.getName();
                        Object attrValue = attr.getValue();
                        if(attrName.equals(CartesianMeshMovingBoundary.MSBDataAttribute.name.name()))
                        {
                            varName = ((String[]) attrValue)[0];
                        }
                        else if (attrName.equals(CartesianMeshMovingBoundary.MSBDataAttribute.size.name()))
                        {
                            size = ((int[]) attrValue)[0];
                        }
                        else if (attrName.equals(CartesianMeshMovingBoundary.MSBDataAttribute.type.name()))
                        {
                            String vt = ((String[]) attrValue)[0];
                            if (vt.equals(CartesianMeshMovingBoundary.MSBDataAttributeValue.Point.name()))
                            {
                                varType = VariableType.POINT_VARIABLE;
                            }
                            else if (vt.equals(CartesianMeshMovingBoundary.MSBDataAttributeValue.Volume.name()))
                            {
                                varType = VariableType.VOLUME;
                            }
                            else if (vt.equals(CartesianMeshMovingBoundary.MSBDataAttributeValue.PointSubDomain.name()))
                            {
                                // Position for PointSubdomain
                            }
                        }
                    }
                    if (varType == VariableType.VOLUME)
                    {
                        // only display volume
                        dataBlockList.addElement(DataBlock.createDataBlock(varName, varType.getType(), size, 0));
                    }
                    if (varType == VariableType.POINT_VARIABLE)
                    {
                        // only display volume
                        dataBlockList.addElement(DataBlock.createDataBlock(varName, varType.getType(), size, 0));
                    }

                }
            }
        }
        finally
        {
            if (solFile != null)
            {
                try {
                    solFile.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    public static double[] readMBSData(String fileName, Vector<DataBlock> dataBlockList, String varName, Double time) throws Exception {
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
        FileFormat solFile = null;
        double[] data = null;
        try {
            solFile = fileFormat.createInstance(fileName, FileFormat.READ);
            solFile.open();
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)solFile.getRootNode();
            Group rootGroup = (Group)rootNode.getUserObject();
            Group solutionGroup = null;
            for (Object member : rootGroup.getMemberList())
            {
                String memberName = ((HObject)member).getName();
                if (member instanceof Group)
                {
                    CartesianMeshMovingBoundary.MBSDataGroup group = CartesianMeshMovingBoundary.MBSDataGroup.valueOf(memberName);
                    if (group == CartesianMeshMovingBoundary.MBSDataGroup.Solution)
                    {
                        solutionGroup = (Group) member;
                        break;
                    }
                }
            }
            if (solutionGroup == null)
            {
                throw new Exception("Group " + CartesianMeshMovingBoundary.MBSDataGroup.Solution + " not found");
            }

            int varIndex = -1;
            int size = 0;
            for (int i = 0; i < dataBlockList.size(); ++ i)
            {
                DataBlock dataBlock = dataBlockList.get(i);
                if (dataBlock.getVarName().equals(varName))
                {
                    varIndex = i;
                    size = dataBlock.getSize();
                    break;
                }
            }

            if (varIndex == -1)
            {
                throw new Exception("Variable " + varName + " not found");
            }

            // find time group for that time
            Group timeGroup = null;
            for (Object member : solutionGroup.getMemberList())
            {
                if (member instanceof Group)
                {
                    Group group = (Group)member;
                    List<Attribute> dsAttrList = group.getMetadata();
                    Attribute timeAttribute = null;
                    for (Attribute attr : dsAttrList)
                    {
                        if (attr.getName().equals(CartesianMeshMovingBoundary.MSBDataAttribute.time.name()))
                        {
                            timeAttribute = attr;
                            break;
                        }
                    }
                    if (timeAttribute != null)
                    {
                        double t = ((double[]) timeAttribute.getValue())[0];
                        if (Math.abs(t - time) < 1e-8)
                        {
                            timeGroup = group;
                            break;
                        }
                    }
                }
            }

            if (timeGroup == null)
            {
                throw new Exception("No time group found for time=" + time);
            }

            // find variable dataset
            Dataset varDataset = null;
            for (Object member : timeGroup.getMemberList())
            {
                if (member instanceof Dataset)
                {
                    List<Attribute> dsAttrList = ((Dataset)member).getMetadata();
                    String var = null;
                    for (Attribute attr : dsAttrList)
                    {
                        if (attr.getName().equals(CartesianMeshMovingBoundary.MSBDataAttribute.name.name()))
                        {
                            var = ((String[]) attr.getValue())[0];
                            break;
                        }
                    }
                    if (var != null && var.equals(varName))
                    {
                        varDataset = (Dataset) member;
                        break;
                    }
                }
            }
            if (varDataset == null)
            {
                throw new Exception("Data for Variable " + varName + " at time " + time + " not found");
            }

            data = new double[size];
            System.arraycopy((double[])varDataset.getData(), 0, data, 0, size);
            return data;
        }
        finally
        {
            if (solFile != null)
            {
                try {
                    solFile.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

}
