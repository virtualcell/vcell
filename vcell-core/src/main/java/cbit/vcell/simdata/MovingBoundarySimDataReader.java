package cbit.vcell.simdata;

import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMeshMovingBoundary;
import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MovingBoundarySimDataReader {

    public static void readMBSDataMetadata(String fileName, Vector<DataBlock> dataBlockList) throws Exception
    {
        try (HdfFile hdfFile = new HdfFile(new File(fileName))) {
            Map<String, Node> childNodes = hdfFile.getChildren();
            Group solutionGroup = null;
            for (Node member : childNodes.values()) {
                String memberName = member.getName();
                if (member instanceof Group memberGroup) {
                    CartesianMeshMovingBoundary.MBSDataGroup group = CartesianMeshMovingBoundary.MBSDataGroup.valueOf(memberName);
                    if (group == CartesianMeshMovingBoundary.MBSDataGroup.Solution) {
                        solutionGroup = memberGroup;
                        break;
                    }
                }
            }
            if (solutionGroup == null) {
                throw new Exception("Group " + CartesianMeshMovingBoundary.MBSDataGroup.Solution + " not found");
            }
            Group finalSolutionGroup = solutionGroup;
            List<Node> solutionGroupMemberNodes = hdfFile.getChildren().values().stream()
                    .filter(node -> node.getParent() == finalSolutionGroup).toList();
            // find any timeGroup
            Group timeGroup = null;
            for (Node member : solutionGroupMemberNodes) {
                if (member instanceof Group group && member.getName().startsWith("time")) {
                    timeGroup = group;
                    break;
                }
            }

            if (timeGroup == null) {
                throw new Exception("No time group found");
            }

            Group finalTimeGroup = timeGroup;
            List<Node> timeGroupMemberNodes = hdfFile.getChildren().values().stream()
                    .filter(node -> node.getParent() == finalTimeGroup).toList();
            // find all the datasets in that time group
            for (Object timeMember : timeGroupMemberNodes) {
                if (timeMember instanceof Dataset timeDataset) {
                    Map<String, Attribute> solAttrList = timeDataset.getAttributes();
                    int size = 0;
                    String varName = null;
                    VariableType varType = null;
                    for (Attribute attr : solAttrList.values())
                    {
                        String attrName = attr.getName();
                        Object attrValue = attr.getData();
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
     }

    public static double[] readMBSData(String fileName, Vector<DataBlock> dataBlockList, String varName, Double time) throws Exception {
        double[] data = null;
        try (HdfFile hdfFile = new HdfFile(new File(fileName))) {
            List<Node> memberNodes = hdfFile.getChildren().values().stream().filter(node -> node.getParent() == hdfFile.getParent()).toList();
            Group solutionGroup = null;
            for (Node member : memberNodes) {
                String memberName = member.getName();
                if (member instanceof Group memberGroup) {
                    CartesianMeshMovingBoundary.MBSDataGroup group = CartesianMeshMovingBoundary.MBSDataGroup.valueOf(memberName);
                    if (group == CartesianMeshMovingBoundary.MBSDataGroup.Solution)
                    {
                        solutionGroup = memberGroup;
                        break;
                    }
                }
            }
            if (solutionGroup == null) {
                throw new Exception("Group " + CartesianMeshMovingBoundary.MBSDataGroup.Solution + " not found");
            }

            int varIndex = -1;
            int size = 0;
            for (int i = 0; i < dataBlockList.size(); ++ i) {
                DataBlock dataBlock = dataBlockList.get(i);
                if (dataBlock.getVarName().equals(varName)) {
                    varIndex = i;
                    size = dataBlock.getSize();
                    break;
                }
            }

            if (varIndex == -1) {
                throw new Exception("Variable " + varName + " not found");
            }

            // find time group for that time
            Group finalSolutionGroup = solutionGroup;
            List<Node> solutionGroupChildren = hdfFile.getChildren().values().stream().filter(node -> node.getParent() == finalSolutionGroup).toList();
            Group timeGroup = null;
            for (Object member : solutionGroupChildren) {
                if (member instanceof Group) {
                    Group group = (Group)member;
                    Attribute timeAttribute = group.getAttribute(CartesianMeshMovingBoundary.MSBDataAttribute.time.name());
                    if (timeAttribute != null) {
                        double t = ((double[]) timeAttribute.getData())[0];
                        if (Math.abs(t - time) < 1e-8) {
                            timeGroup = group;
                            break;
                        }
                    }
                }
            }

            if (timeGroup == null) {
                throw new Exception("No time group found for time=" + time);
            }

            // find variable dataset
            Group finalTimeGroup = timeGroup;
            List<Node> timeGroupChildren = hdfFile.getChildren().values().stream().filter(node -> node.getParent() == finalTimeGroup).toList();
            Dataset varDataset = null;
            for (Node member : timeGroupChildren) {
                if (member instanceof Dataset dataset) {
                    Attribute nameAttr = dataset.getAttribute(CartesianMeshMovingBoundary.MSBDataAttribute.name.name());
                    if (nameAttr != null && varName.equals(nameAttr.getData())) {
                        varDataset = (Dataset) member;
                        break;
                    }
                }
            }
            if (varDataset == null) {
                throw new Exception("Data for Variable " + varName + " at time " + time + " not found");
            }

            data = new double[size];
            System.arraycopy((double[])varDataset.getData(), 0, data, 0, size);
            return data;
        }
    }
}
