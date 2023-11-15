package org.vcell.sbml.vcell;

import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.CSGNode;
import cbit.vcell.model.Model;
import cbit.vcell.render.Vect3d;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.ext.spatial.*;
import org.vcell.util.Issue;

public interface ISBMLImporter {

    record SBMLIssueSource(SBase issueSource) implements Issue.IssueSource {}

    SBMLSymbolMapping getSymbolMapping();
    BioModel getBioModel() throws VCLoggerException;

    static boolean isRestrictedXYZT(String name, BioModel vcBioModel, boolean bSpatial){
        if(bSpatial){
            return false;    // if spatial, xyzt are not restricted / reserved
        }
        Model.ReservedSymbol rs = vcBioModel.getModel().getReservedSymbolByName(name);
        if(rs == null){
            return false;
        }
        return rs.isX() || rs.isY() || rs.isZ() || rs.isTime();
    }

    static cbit.vcell.geometry.CSGNode getVCellCSGNode(org.sbml.jsbml.ext.spatial.CSGNode sbmlCSGNode){
        String csgNodeName = sbmlCSGNode.getParent().getId();
        if(sbmlCSGNode instanceof org.sbml.jsbml.ext.spatial.CSGPrimitive){
            PrimitiveKind primitiveKind = ((org.sbml.jsbml.ext.spatial.CSGPrimitive) sbmlCSGNode).getPrimitiveType();
            cbit.vcell.geometry.CSGPrimitive.PrimitiveType vcellCSGPrimitiveType = getVCellPrimitiveType(primitiveKind);
            return new cbit.vcell.geometry.CSGPrimitive(csgNodeName, vcellCSGPrimitiveType);
        } else if(sbmlCSGNode instanceof CSGPseudoPrimitive){
            throw new RuntimeException("Pseudo primitives not yet supported in CSGeometry.");
        } else if(sbmlCSGNode instanceof CSGSetOperator sbmlSetOperator){
            cbit.vcell.geometry.CSGSetOperator vcellSetOperator = getCsgSetOperator(sbmlSetOperator, csgNodeName);
            for(int c = 0; c < sbmlSetOperator.getListOfCSGNodes().size(); c++){
                vcellSetOperator.addChild(getVCellCSGNode(sbmlSetOperator.getListOfCSGNodes().get(c)));
            }
            return vcellSetOperator;
        } else if(sbmlCSGNode instanceof CSGTransformation sbmlTransformation){
            cbit.vcell.geometry.CSGNode vcellCSGChild = getVCellCSGNode(sbmlTransformation.getCSGNode());
            if(sbmlTransformation instanceof org.sbml.jsbml.ext.spatial.CSGTranslation sbmlTranslation){
                Vect3d translation = new Vect3d(
                        sbmlTranslation.getTranslateX(),
                        sbmlTranslation.getTranslateY(),
                        sbmlTranslation.getTranslateZ());
                cbit.vcell.geometry.CSGTranslation vcellTranslation = new cbit.vcell.geometry.CSGTranslation(csgNodeName, translation);
                vcellTranslation.setChild(vcellCSGChild);
                return vcellTranslation;
            } else if(sbmlTransformation instanceof CSGRotation sbmlRotation){
                Vect3d axis = new Vect3d(
                        sbmlRotation.getRotateX(),
                        sbmlRotation.getRotateY(),
                        sbmlRotation.getRotateZ());
                double rotationAngleRadians = sbmlRotation.getRotateAngleInRadians();
                cbit.vcell.geometry.CSGRotation vcellRotation = new cbit.vcell.geometry.CSGRotation(
                        csgNodeName, axis, rotationAngleRadians);
                vcellRotation.setChild(vcellCSGChild);
                return vcellRotation;
            } else if(sbmlTransformation instanceof CSGScale sbmlScale){
                Vect3d scale = new Vect3d(sbmlScale.getScaleX(),
                        sbmlScale.getScaleY(), sbmlScale.getScaleZ());
                cbit.vcell.geometry.CSGScale vcellScale = new cbit.vcell.geometry.CSGScale(
                        csgNodeName, scale);
                vcellScale.setChild(vcellCSGChild);
                return vcellScale;
            } else if(sbmlTransformation instanceof CSGHomogeneousTransformation){
                throw new SBMLImportException("homogeneous transformations not supported yet.");
            } else {
                throw new SBMLImportException("unsupported type of CSGTransformation");
            }
        } else {
            throw new SBMLImportException("unsupported type of CSGNode");
        }
    }

    private static cbit.vcell.geometry.CSGSetOperator getCsgSetOperator(CSGSetOperator sbmlSetOperator, String csgNodeName){
        final cbit.vcell.geometry.CSGSetOperator.OperatorType opType;
        switch(sbmlSetOperator.getOperationType()){
            case difference:{
                opType = cbit.vcell.geometry.CSGSetOperator.OperatorType.DIFFERENCE;
                break;
            }
            case intersection:{
                opType = cbit.vcell.geometry.CSGSetOperator.OperatorType.INTERSECTION;
                break;
            }
            case union:{
                opType = cbit.vcell.geometry.CSGSetOperator.OperatorType.UNION;
                break;
            }
            default:{
                throw new RuntimeException("sbml CSG geometry set operator " + sbmlSetOperator.getOperationType().name() + " not supported");
            }
        }
        return new cbit.vcell.geometry.CSGSetOperator(csgNodeName, opType);
    }

    private static cbit.vcell.geometry.CSGPrimitive.PrimitiveType getVCellPrimitiveType(PrimitiveKind primitiveKind){
        final cbit.vcell.geometry.CSGPrimitive.PrimitiveType vcellCSGPrimitiveType;
        switch(primitiveKind){
            case cone:{
                vcellCSGPrimitiveType = cbit.vcell.geometry.CSGPrimitive.PrimitiveType.CONE;
                break;
            }
            case cube:{
                vcellCSGPrimitiveType = cbit.vcell.geometry.CSGPrimitive.PrimitiveType.CUBE;
                break;
            }
            case cylinder:{
                vcellCSGPrimitiveType = cbit.vcell.geometry.CSGPrimitive.PrimitiveType.CYLINDER;
                break;
            }
            case sphere:{
                vcellCSGPrimitiveType = cbit.vcell.geometry.CSGPrimitive.PrimitiveType.SPHERE;
                break;
            }
//		case circle:
//		case rightTriangle:
//		case square:
            default:{
                throw new RuntimeException("Constructive Solid Geometry primative type " + primitiveKind.name() + " not supported");
            }
        }
        return vcellCSGPrimitiveType;
    }
}
