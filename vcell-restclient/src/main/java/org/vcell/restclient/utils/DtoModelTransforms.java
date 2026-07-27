package org.vcell.restclient.utils;


import cbit.image.GIFImage;
import cbit.image.GifParsingException;
import cbit.image.VCImageInfo;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportEventController;
import cbit.vcell.export.server.*;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.field.FieldDataAllDBEntries;
import cbit.vcell.field.io.FieldData;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import org.vcell.restclient.model.*;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.MathModelChildSummary;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.document.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class DtoModelTransforms {

    public static org.vcell.restclient.model.ISize iSizeToDTO(org.vcell.util.ISize iSize){
        org.vcell.restclient.model.ISize iSizeDTO = new org.vcell.restclient.model.ISize();
        iSizeDTO.setX(iSize.getX());
        iSizeDTO.setY(iSize.getY());
        iSizeDTO.setZ(iSize.getZ());
        return iSizeDTO;
    }

    public static org.vcell.util.ISize dtoToISize(org.vcell.restclient.model.ISize dto){
        return new org.vcell.util.ISize(dto.getX(), dto.getY(), dto.getZ());
    }

    public static org.vcell.restclient.model.Origin originToDTO(Origin origin){
        org.vcell.restclient.model.Origin dtoOrigin = new org.vcell.restclient.model.Origin();
        dtoOrigin.setX(origin.getX());
        dtoOrigin.setY(origin.getY());
        dtoOrigin.setZ(origin.getZ());
        return dtoOrigin;
    }

    public static Origin dtoToOrigin(org.vcell.restclient.model.Origin dtoOrigin){
        return new Origin(dtoOrigin.getX(), dtoOrigin.getY(), dtoOrigin.getZ());
    }


    public static org.vcell.restclient.model.Extent extentToDTO(Extent extent) {
        org.vcell.restclient.model.Extent newExtent = new org.vcell.restclient.model.Extent();
        newExtent.setX(extent.getX());
        newExtent.setY(extent.getY());
        newExtent.setZ(extent.getZ());
        return newExtent;
    }

    public static Extent dtoToExtent(org.vcell.restclient.model.Extent dto) {
        return new Extent(dto.getX(), dto.getY(), dto.getZ());
    }

    public static DataIdentifier dtoToDataIdentifier(org.vcell.restclient.model.DataIdentifier dto){
        return new DataIdentifier(dto.getName(), dtoToVariableType(dto.getVariableType()),
                dto.getDomain() == null ? null : dtoToDomain(dto.getDomain()),
                dto.getbFunction() == null ? false : dto.getbFunction(), dto.getDisplayName());
    }

    public static org.vcell.restclient.model.VariableType variableTypeToDTO(VariableType vt) {
        org.vcell.restclient.model.VariableType dto = new org.vcell.restclient.model.VariableType();
        dto.setType(vt.getType());
        dto.setName(vt.getTypeName());
        dto.setUnits(vt.getDefaultUnits());
        dto.setLabel(vt.getDefaultLabel());
        return dto;
    }

    public static VariableType dtoToVariableType(org.vcell.restclient.model.VariableType dto) {
        return VariableType.getVariableTypeFromInteger(dto.getType());
    }

    public static Variable.Domain dtoToDomain(org.vcell.restclient.model.Domain dto){
        return new Variable.Domain(dto.getName());
    }

    public static org.vcell.restclient.model.ExternalDataIdentifier externalDataIdentifierToDTO(ExternalDataIdentifier externalDataIdentifier) {
        org.vcell.restclient.model.ExternalDataIdentifier dto = new org.vcell.restclient.model.ExternalDataIdentifier();
        dto.key(externalDataIdentifier.getKey().toString());
        dto.owner(userToDTO(externalDataIdentifier.getOwner()));
        dto.name(externalDataIdentifier.getName());
        return dto;
    }

    public static ExternalDataIdentifier dtoToExternalDataIdentifier(org.vcell.restclient.model.ExternalDataIdentifier dto){
        return new ExternalDataIdentifier(
                new KeyValue(dto.getKey()), dtoToUser(dto.getOwner()), dto.getName()
        );
    }

    public static org.vcell.restclient.model.User userToDTO(User user) {
        org.vcell.restclient.model.User userDTO = new org.vcell.restclient.model.User();
        userDTO.setUserName(user.getName());
        userDTO.setKey(user.getID().toString());
        return userDTO;
    }

    public static User dtoToUser(org.vcell.restclient.model.User dto){
        SpecialUser.SPECIAL_CLAIM[] claims = new SpecialUser.SPECIAL_CLAIM[] {};
        if (dto.getMySpecials() != null) {
            claims = dto.getMySpecials().stream().map(c -> SpecialUser.SPECIAL_CLAIM.fromDatabase(c.getValue())).toArray(SpecialUser.SPECIAL_CLAIM[]::new);
        }
        return new SpecialUser(dto.getUserName(), new KeyValue(dto.getKey()), claims);
    }

    /** Rebuild a native {@link User} from the flat summary DTO (name + string key). */
    public static User dtoToUser(org.vcell.restclient.model.UserRep dto){
        if (dto == null) {
            return null;
        }
        return new User(dto.getUserName(), new KeyValue(dto.getKey()));
    }

    public static cbit.vcell.field.io.FieldDataShape DTOToFieldDataShape(FieldDataShape dto){
        cbit.vcell.field.io.FieldDataShape results = new cbit.vcell.field.io.FieldDataShape();
        results.extent = dtoToExtent(dto.getExtent());
        results.origin = dtoToOrigin(dto.getOrigin());
        results.iSize = dtoToISize(dto.getIsize());
        results.times = dto.getTimes().stream().mapToDouble(Double::doubleValue).toArray();
        results.variableInformation = dto.getDataIdentifier().stream().map(DtoModelTransforms::dtoToDataIdentifier).toArray(DataIdentifier[]::new);
        return results;
    }

    public static org.vcell.restclient.model.FieldData fieldDataToDTO(FieldData fieldData){
        org.vcell.restclient.model.FieldData analyzedFile = new org.vcell.restclient.model.FieldData();
        List<List<List<Integer>>> shortData = new ArrayList<>();
        for (int i = 0; i < fieldData.data.length; i++){
            shortData.add(new ArrayList<>());
            for(int j = 0; j < fieldData.data[i].length; j++){
                shortData.get(i).add(new ArrayList<>());
                for(int k = 0; k < fieldData.data[i][j].length; k++){
                    shortData.get(i).get(j).add((int) fieldData.data[i][j][k]);
                }
            }
        }
        List<List<List<Double>>> doubleData = new ArrayList<>();
        for (int i = 0; i < fieldData.doubleData.length; i++){
            doubleData.add(new ArrayList<>());
            for(int j = 0; j < fieldData.doubleData[i].length; j++){
                doubleData.get(i).add(new ArrayList<>());
                for(int k = 0; k < fieldData.doubleData[i][j].length; k++){
                    doubleData.get(i).get(j).add(fieldData.doubleData[i][j][k]);
                }
            }
        }
        analyzedFile.times(fieldData.times).name(fieldData.fileName).annotation(fieldData.annotation)
                .extent(extentToDTO(fieldData.extent)).isize(iSizeToDTO(fieldData.iSize)).origin(originToDTO(fieldData.origin))
                .varNames(fieldData.channelNames).shortSpecData(shortData);
        return analyzedFile;
    }

    public static FieldDataAllDBEntries fieldDataReferencesToDBResults(List<FieldDataReference> dto){
        FieldDataAllDBEntries fieldDataDBOperationResults = new FieldDataAllDBEntries();
        ArrayList<ExternalDataIdentifier> externalDataIdentifiers = new ArrayList<>();
        ArrayList<String> externalDataAnnotations = new ArrayList<>();
        HashMap<ExternalDataIdentifier, Vector<KeyValue>> externalDataIDSimRefs = new HashMap<>();
        for (FieldDataReference fieldDataReference : dto){
            ExternalDataIdentifier externalDataIdentifier = dtoToExternalDataIdentifier(fieldDataReference.getFieldDataID());
            externalDataIdentifiers.add(externalDataIdentifier);
            externalDataAnnotations.add(fieldDataReference.getAnnotation());
            List<KeyValue> keyValues = fieldDataReference.getSimulationsReferencingThisID().stream().map(KeyValue::new).collect(Collectors.toList());
            externalDataIDSimRefs.put(externalDataIdentifier, new Vector<>(keyValues));
        }
        fieldDataDBOperationResults.ids = externalDataIdentifiers.toArray(new ExternalDataIdentifier[0]);
        fieldDataDBOperationResults.annotationsForIds = externalDataAnnotations.toArray(new String[0]);
        fieldDataDBOperationResults.edisToSimRefs = externalDataIDSimRefs;
        return fieldDataDBOperationResults;
    }

    public static GroupAccess dtoToGroupAccess(org.vcell.restclient.model.GroupAccessRep dto){
        if (dto == null) {
            return null;
        }
        String type = dto.getType();
        if ("all".equals(type)) {
            return new GroupAccessAll();
        } else if ("none".equals(type)) {
            return new GroupAccessNone();
        } else if ("some".equals(type)) {
            List<org.vcell.restclient.model.GroupMemberRep> members =
                    dto.getMembers() == null ? Collections.emptyList() : dto.getMembers();
            User[] users = new User[members.size()];
            boolean[] hidden = new boolean[members.size()];
            KeyValue[] userRefs = new KeyValue[members.size()];
            for (int i = 0; i < members.size(); i++) {
                org.vcell.restclient.model.GroupMemberRep m = members.get(i);
                users[i] = dtoToUser(m.getUser());
                hidden[i] = Boolean.TRUE.equals(m.getHidden());
                userRefs[i] = users[i].getID();
            }
            // The server-internal integrity hash is not transmitted; recompute it so the
            // reconstructed GroupAccessSome is self-consistent (its constructor validates the hash).
            java.math.BigDecimal hash = GroupAccess.calculateHash(userRefs, hidden);
            return new GroupAccessSome(new java.math.BigDecimal(dto.getGroupid()), hash, users, hidden);
        }
        throw new IllegalArgumentException("Unexpected GroupAccessRep type: " + type);
    }

    public static Version versionDTOToVersion(org.vcell.restclient.model.VersionRep dto){
        if (dto == null) {
            return null;
        }
        return new Version(
                new KeyValue(dto.getVersionKey()), dto.getName(), dtoToUser(dto.getOwner()),
                dtoToGroupAccess(dto.getGroupAccess()),
                dto.getBranchPointRefKey() == null ? null : new KeyValue(dto.getBranchPointRefKey()),
                dto.getBranchId() == null ? null : new java.math.BigDecimal(dto.getBranchId()),
                dto.getDate() == null ? null : new Date(dto.getDate()),
                dto.getFlag() == null ? null : VersionFlag.fromInt(dto.getFlag()), dto.getAnnotation()
        );
    }

    public static BioModelChildSummary.MathType dtoToBioModelMathType(MathType dto){
        return BioModelChildSummary.MathType.valueOf(dto.getValue());
    }

    public static BioModelChildSummary dtoToBioModelChildSummary(org.vcell.restclient.model.BioModelChildSummary dto){
        if (dto == null) {
            return null; // a BioModel can legitimately have no child summary (matches the native DB path)
        }
        BioModelChildSummary.MathType[] mathTypes = new BioModelChildSummary.MathType[dto.getAppTypes().size()];
        for (int i = 0; i < mathTypes.length; i++){
            mathTypes[i] = dtoToBioModelMathType(dto.getAppTypes().get(i));
        }
        String[][] simNames = null;
        if (dto.getSimNames() != null){
            simNames = new String[dto.getSimNames().size()][];
            for (int i = 0; i < simNames.length; i++){
                simNames[i] = dto.getSimNames().get(i).toArray(new String[0]);
            }
        }
        String[][] simAnnots = null;
        if (dto.getSimAnnots() != null){
            simAnnots = new String[dto.getSimAnnots().size()][];
            for (int i = 0; i < simAnnots.length; i++){
                simAnnots[i] = dto.getSimAnnots().get(i).toArray(new String[0]);
            }
        }
        String[] scAnnotations = dto.getScAnnots() == null ? null : dto.getScAnnots().toArray(new String[0]);
        String[] geoNames = dto.getGeoNames() == null ? null : dto.getGeoNames().toArray(new String[0]);
        int[] geoDims = dto.getGeoDims() == null ? null : dto.getGeoDims().stream().mapToInt(Integer::intValue).toArray();
        String[] simKeys = dto.getSimKeys() == null ? new String[0] : dto.getSimKeys().toArray(new String[0]);
        return new BioModelChildSummary(dto.getSimulationContextNames().toArray(new String[0]),
                mathTypes, scAnnotations, simNames, simAnnots, simKeys, geoNames,
                geoDims);
    }

    public static VCellSoftwareVersion dtoToVCellSoftwareVersion(String softwareVersionString){
        return VCellSoftwareVersion.fromString(softwareVersionString);
    }

    public static PublicationInfo dtoToPublicationInfo(org.vcell.restclient.model.PublicationInfoRep dto){
        return new PublicationInfo(new KeyValue(dto.getPublicationKey()), new KeyValue(dto.getVersionKey()),
                dto.getTitle(), dto.getAuthors() == null ? new String[0] : dto.getAuthors().toArray(new String[0]),
                dto.getCitation(), dto.getPubmedid(), dto.getDoi(), dto.getUrl(),
                VCDocument.VCDocumentType.valueOf(dto.getVcDocumentType()), dtoToUser(dto.getUser()),
                dto.getPubDate() == null ? null : new Date(dto.getPubDate()));
    }

    public static BioModelInfo bioModelContextToBioModelInfo(BioModelSummary summary){
        BioModelInfo bioModelInfo = new BioModelInfo(versionDTOToVersion(summary.getVersion()), dtoToBioModelChildSummary(summary.getSummary()),
                dtoToVCellSoftwareVersion(summary.getvCellSoftwareVersion()));
        if (summary.getPublicationInformation() != null){
            for (org.vcell.restclient.model.PublicationInfoRep pubInfo : summary.getPublicationInformation()){
                bioModelInfo.addPublicationInfo(dtoToPublicationInfo(pubInfo));
            }
        }
        return bioModelInfo;
    }

    public static MathModelChildSummary mathModelChildSummary(org.vcell.restclient.model.MathModelChildSummary dto){
        if (dto == null) {
            return null; // a MathModel can legitimately have no model-info summary (matches the native DB path)
        }
        String[] simKeys = dto.getSimKeys() == null ? new String[0] : dto.getSimKeys().toArray(new String[0]);
        return new MathModelChildSummary(BioModelChildSummary.MathType.valueOf(dto.getModelType().getValue()),
                dto.getGeometryName(), dto.getGeometryDimension(), dto.getSimulationNames().toArray(new String[0]),
                dto.getSimulationAnnotations().toArray(new String[0]), simKeys);
    }

    public static MathModelInfo mathModelContextToMathModel(MathModelSummary summary){
        return new MathModelInfo(
                versionDTOToVersion(summary.getVersion()), new KeyValue(summary.getKeyValue()),
                mathModelChildSummary(summary.getModelInfo()), dtoToVCellSoftwareVersion(summary.getSoftwareVersion())
        );
    }

    public static GeometryInfo geometrySummaryToGeometryInfo(org.vcell.restclient.model.GeometrySummary summary){
        return new GeometryInfo(DtoModelTransforms.versionDTOToVersion(summary.getVersion()),
                summary.getDimension(), DtoModelTransforms.dtoToExtent(summary.getExtent()),
                DtoModelTransforms.dtoToOrigin(summary.getOrigin()),
                // analytic geometries have no image reference -> null KeyValue (matches the native DB path)
                summary.getImageRef() == null ? null : new KeyValue(summary.getImageRef()),
                DtoModelTransforms.dtoToVCellSoftwareVersion(summary.getSoftwareVersion()));
    }

    public static VCImageInfo imageSummaryToVCImageInfo(org.vcell.restclient.model.VCImageSummary summary) {
        try {
            org.vcell.restclient.model.PreviewRep preview = summary.getPreview();
            GIFImage browseGif = (preview == null || preview.getGifBase64() == null) ? null
                    : new GIFImage(Base64.getDecoder().decode(preview.getGifBase64()));
            return new VCImageInfo(DtoModelTransforms.versionDTOToVersion(summary.getVersion()),
                    DtoModelTransforms.dtoToISize(summary.getSize()), DtoModelTransforms.dtoToExtent(summary.getExtent()),
                    browseGif,
                    DtoModelTransforms.dtoToVCellSoftwareVersion(summary.getSoftwareVersion()));
        } catch (GifParsingException e) {
            throw new RuntimeException(e);
        }
    }

    public static TimeSpecs dtoToTimeSpecs(org.vcell.restclient.model.TimeSpecs dto){
        return new TimeSpecs(dto.getBeginTimeIndex(), dto.getEndTimeIndex(), dto.getAllTimes().stream().mapToDouble(Double::doubleValue).toArray(),
                ExportEnums.TimeMode.valueOf(dto.getMode().getValue()));
    }

    public static cbit.vcell.export.server.VariableSpecs dtoToVariableSpecs(org.vcell.restclient.model.VariableSpecs dto){
        return new cbit.vcell.export.server.VariableSpecs(dto.getVariableNames(), ExportEnums.VariableMode.valueOf(dto.getMode().getValue()));
    }

    public static org.vcell.restclient.model.TimeSpecs timeSpecsToDTO(cbit.vcell.export.server.TimeSpecs timeSpecs) {
        org.vcell.restclient.model.TimeSpecs dto = new org.vcell.restclient.model.TimeSpecs();
        dto.setBeginTimeIndex(timeSpecs.getBeginTimeIndex());
        dto.setEndTimeIndex(timeSpecs.getEndTimeIndex());
        dto.setAllTimes(Arrays.stream(timeSpecs.getAllTimes()).boxed().collect(Collectors.toList()));
        dto.setMode(org.vcell.restclient.model.TimeMode.fromValue(timeSpecs.getMode().name()));
        return dto;
    }

    // File: vcell-restclient/src/main/java/org/vcell/restclient/utils/DtoModelTransforms.java

    public static org.vcell.restclient.model.Curve toRestCurve(cbit.vcell.geometry.Curve coreCurve) {
        if (coreCurve instanceof cbit.vcell.geometry.AnalyticCurve ac) {
            org.vcell.restclient.model.AnalyticCurve restAc = new org.vcell.restclient.model.AnalyticCurve();
            restAc.setType("AnalyticCurve");
            restAc.setExpX(ac.getExpressionX().infix());
            restAc.setExpY(ac.getExpressionY().infix());
            restAc.setExpZ(ac.getExpressionZ().infix());
            restAc.setOffset(toRestCoordinate(ac.getAnalyticOffset()));
            restAc.setValid(ac.isValid());
            return restAc;
        } else if (coreCurve instanceof cbit.vcell.geometry.CompositeCurve cc) {
            org.vcell.restclient.model.CompositeCurve restCc = new org.vcell.restclient.model.CompositeCurve();
            restCc.setType("CompositeCurve");
            List<Object> restCurves = new ArrayList<>();
            for (int i = 0; i < cc.getCurveCount(); i++) {
                restCurves.add(toRestCurve(cc.getCurve(i)));
            }
            restCc.setFieldCurves(restCurves);
            restCc.setCurveCount(cc.getCurveCount());
            restCc.setValid(cc.isValid());
            return restCc;
        } else if (coreCurve instanceof cbit.vcell.geometry.ControlPointCurve cpc) {
            return toRestControlPointCurve(cpc);
        }
        throw new IllegalArgumentException("Unknown curve type: " + coreCurve.getClass().getName());
    }

    // Helper function for coordinate conversion
    private static org.vcell.restclient.model.Coordinate toRestCoordinate(org.vcell.util.Coordinate coreCoord) {
        org.vcell.restclient.model.Coordinate dto = new org.vcell.restclient.model.Coordinate();
        dto.setX(coreCoord.getX());
        dto.setY(coreCoord.getY());
        dto.setZ(coreCoord.getZ());
        return dto;
    }

    public static org.vcell.restclient.model.ControlPointCurve toRestControlPointCurve(cbit.vcell.geometry.ControlPointCurve curve) {
        java.util.List<org.vcell.restclient.model.Coordinate> restControlPoints = new java.util.ArrayList<>();
        if (curve.getControlPointsVector() != null){
            for (org.vcell.util.Coordinate cp : curve.getControlPointsVector()) {
                restControlPoints.add(new org.vcell.restclient.model.Coordinate()
                        .x(cp.getX())
                        .y(cp.getY())
                        .z(cp.getZ()));
            }
        }
        if (curve instanceof cbit.vcell.geometry.Spline spline) {
            org.vcell.restclient.model.Spline restSpline = new org.vcell.restclient.model.Spline();
            restSpline.setType("Spline");
            restSpline.setControlPoints(restControlPoints);
            restSpline.setControlPointCount(spline.getControlPointCount());
            restSpline.setControlPointsVector(restControlPoints);
            restSpline.setMaxControlPoints(spline.getMaxControlPoints());
            restSpline.setMinControlPoints(spline.getMinControlPoints());
            restSpline.setControlPointAddable(spline.isControlPointAddable());
            restSpline.setValid(spline.isValid());
            restSpline.setSegmentCount(spline.getSegmentCount());
            restSpline.setNumSamplePoints(spline.getNumSamplePoints());
            restSpline.setSpatialLength(spline.getSpatialLength());
            restSpline.setClosed(spline.isClosed());
            return restSpline;
        } else if (curve instanceof cbit.vcell.geometry.SampledCurve sampledCurve) {
            org.vcell.restclient.model.SampledCurve restSampledCurve = new org.vcell.restclient.model.SampledCurve();
            restSampledCurve.setType("SampledCurve");
            restSampledCurve.setControlPoints(restControlPoints);
            restSampledCurve.setControlPointCount(sampledCurve.getControlPointCount());
            restSampledCurve.setControlPointsVector(restControlPoints);
            restSampledCurve.setMaxControlPoints(sampledCurve.getMaxControlPoints());
            restSampledCurve.setMinControlPoints(sampledCurve.getMinControlPoints());
            restSampledCurve.setControlPointAddable(sampledCurve.isControlPointAddable());
            restSampledCurve.setValid(sampledCurve.isValid());
            restSampledCurve.setSegmentCount(sampledCurve.getSegmentCount());
            restSampledCurve.setNumSamplePoints(sampledCurve.getNumSamplePoints());
            restSampledCurve.setSpatialLength(sampledCurve.getSpatialLength());
            restSampledCurve.setClosed(sampledCurve.isClosed());
            return restSampledCurve;
        } else {
            throw new IllegalArgumentException("Unsupported curve type: " + curve.getClass().getName());
        }
    }

    public static org.vcell.restclient.model.CurveSelectionInfo curveSelectionInfoToDTO(cbit.vcell.geometry.CurveSelectionInfo core) {
        if (core == null) return null;
        org.vcell.restclient.model.CurveSelectionInfo dto = new org.vcell.restclient.model.CurveSelectionInfo();

        dto.setFieldCurve(toRestCurve(core.getCurve()));
        dto.setFieldType(core.getType());
        dto.setFieldControlPoint(core.getControlPoint());
        dto.setFieldSegment(core.getSegment());
        dto.setFieldU(core.getU());
        dto.setFieldUExtended(core.getUExtended());
        dto.setFieldControlPointExtended(core.getControlPointExtended());
        dto.setFieldSegmentExtended(core.getSegmentExtended());
        dto.setFieldDirectionNegative(core.getDirectionNegative());

        return dto;
    }


    public static org.vcell.restclient.model.SpatialSelection spacialSelectionToDTO(cbit.vcell.simdata.SpatialSelection coreSelection) {
        if (coreSelection instanceof cbit.vcell.simdata.SpatialSelectionMembrane core) {
            org.vcell.restclient.model.SpatialSelectionMembrane rest = new org.vcell.restclient.model.SpatialSelectionMembrane();
            rest.selectionSource(core.getSelectionSource() != null ? (SampledCurve) toRestControlPointCurve(core.getSelectionSource()) : null);
            rest.fieldSampledDataIndexes(core.getFieldSampledDataIndexes() != null ?
                    java.util.Arrays.stream(core.getFieldSampledDataIndexes()).boxed().toList() : null);
            rest.curveSelectionInfo(curveSelectionInfoToDTO(core.getCurveSelectionInfo()));
            rest.variableType(variableTypeToDTO(core.getVariableType()));
            rest.type("Membrane");
            return rest;
        } else if (coreSelection instanceof cbit.vcell.simdata.SpatialSelectionContour core) {
            org.vcell.restclient.model.SpatialSelectionContour rest = new org.vcell.restclient.model.SpatialSelectionContour();
            rest.fieldSampledDataIndexes(core.getSampledDataIndexes() != null ?
                    java.util.Arrays.stream(core.getSampledDataIndexes()).boxed().toList() : null);
            rest.curveSelectionInfo(curveSelectionInfoToDTO(core.getCurveSelectionInfo()));
            rest.variableType(variableTypeToDTO(core.getVariableType()));
            rest.type("Contour");
            return rest;
        } else if (coreSelection instanceof cbit.vcell.simdata.SpatialSelectionVolume core) {
            org.vcell.restclient.model.SpatialSelectionVolume rest = new org.vcell.restclient.model.SpatialSelectionVolume();
            rest.curveSelectionInfo(curveSelectionInfoToDTO(core.getCurveSelectionInfo()));
            rest.variableType(variableTypeToDTO(core.getVariableType()));
            rest.type("Volume");
            return rest;
        }
        throw new IllegalArgumentException("Unsupported SpatialSelection type: " + coreSelection.getClass());
    }

    public static GeometrySpecDTO geometrySpecToDTO(GeometrySpecs geometrySpec) {
        GeometrySpecDTO dto = new GeometrySpecDTO();
        dto.selections(geometrySpec.getSelections() == null ? null : Arrays.stream(geometrySpec.getSelections())
                .map(DtoModelTransforms::spacialSelectionToDTO).collect(Collectors.toList()));
        dto.axis(geometrySpec.getAxis());
        dto.geometryMode(org.vcell.restclient.model.GeometryMode.fromValue(geometrySpec.getMode().name()));
        dto.sliceNumber(geometrySpec.getSliceNumber());
        return dto;
    }

    public static org.vcell.restclient.model.VariableSpecs variableSpecsToDTO(cbit.vcell.export.server.VariableSpecs variableSpecs) {
        org.vcell.restclient.model.VariableSpecs dto = new org.vcell.restclient.model.VariableSpecs();
        dto.setVariableNames(new ArrayList<>(Arrays.asList(variableSpecs.getVariableNames())));
        dto.setMode(org.vcell.restclient.model.VariableMode.fromValue(variableSpecs.getMode().name()));
        return dto;
    }

    public static StandardExportInfo exportRequestFromExportSpecs(ExportSpecs exportSpecs) {
        if (exportSpecs.getVCDataIdentifier() instanceof VCSimulationDataIdentifier vcd){
            StandardExportInfo exportRequest = new StandardExportInfo();
            exportRequest.setSimulationKey(exportSpecs.getVCDataIdentifier().getDataKey().toString());
            exportRequest.setSimulationJob(vcd.getJobIndex());
            exportRequest.setContextName(exportSpecs.getContextName());
            exportRequest.setVariableSpecs(variableSpecsToDTO(exportSpecs.getVariableSpecs()));
            exportRequest.setTimeSpecs(timeSpecsToDTO(exportSpecs.getTimeSpecs()));
            exportRequest.geometrySpecs(exportSpecs.getGeometrySpecs() == null ? null : geometrySpecToDTO(exportSpecs.getGeometrySpecs()));
            return exportRequest;
        } else {
            throw new IllegalArgumentException("Expected VCSimulationDataIdentifier but got: " + exportSpecs.getVCDataIdentifier().getClass().getName());
        }
    }

    public static N5ExportRequest n5ExportRequestFromExportSpecs(ExportSpecs exportSpecs) {
        if (exportSpecs.getFormatSpecificSpecs() instanceof N5Specs n5Specs){
            N5ExportRequest n5ExportRequest = new N5ExportRequest();
            HashMap<String, String> subVolumeMapping = new HashMap<>();
            for (Map.Entry<Integer, String> entry : n5Specs.getSubVolumeMapping().entrySet()) {
                subVolumeMapping.put(entry.getKey().toString(), entry.getValue());
            }
            n5ExportRequest.subVolume(subVolumeMapping);
            n5ExportRequest.datasetName(n5Specs.dataSetName);
            n5ExportRequest.exportableDataType(ExportableDataType.fromValue(n5Specs.getDataType().name()));
            n5ExportRequest.standardExportInformation(DtoModelTransforms.exportRequestFromExportSpecs(exportSpecs));
            return n5ExportRequest;
        } else {
            throw new IllegalArgumentException("Expected N5Specs but got: " + exportSpecs.getFormatSpecificSpecs().getClass().getName());
        }
    }

    public static ExportEvent dtoToExportEvent(org.vcell.restclient.model.ExportEvent restEvent) {
        if (restEvent == null) return null;

        // Convert fields
        ExportEnums.ExportProgressType eventType = ExportEnums.ExportProgressType.valueOf(restEvent.getEventType().getValue());
        Double progress = restEvent.getProgress();
        String format = restEvent.getFormat();
        String location = restEvent.getLocation();
        org.vcell.util.document.User user = restEvent.getUser() == null ? null : dtoToUser(restEvent.getUser());
        long jobID = restEvent.getJobID() != null ? restEvent.getJobID() : 0L;
        org.vcell.util.document.KeyValue dataKey = restEvent.getDataKey() != null ? new org.vcell.util.document.KeyValue(restEvent.getDataKey()) : null;
        String dataIdString = restEvent.getDataIdString();

        // Construct ExportEvent
        return new cbit.rmi.event.ExportEvent(
                ExportEventController.class, // source (set as needed)
                jobID,
                user,
                dataIdString,
                dataKey,
                eventType,
                format,
                location,
                progress
        );
    }
}
