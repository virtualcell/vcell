package org.vcell.restclient.utils;


import cbit.image.GIFImage;
import cbit.image.GifParsingException;
import cbit.image.VCImageInfo;
import cbit.vcell.field.FieldDataAllDBEntries;
import cbit.vcell.field.io.FieldData;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataIdentifier;
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
import java.math.BigDecimal;
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

    public static VersionFlag dtoToVersionFlag(org.vcell.restclient.model.VersionFlag dto){
        if (dto.getVersionFlag() == null){
            return null;
        }
        return VersionFlag.fromInt(dto.getVersionFlag());
    }

    public static GroupAccess dtoToGroupAccess(org.vcell.restclient.model.GroupAccess dto){

        if (dto instanceof org.vcell.restclient.model.GroupAccessAll){
            return new GroupAccessAll();
        } else if (dto instanceof org.vcell.restclient.model.GroupAccessSome gs){

            boolean[] bValues = new boolean[gs.getHiddenMembers().size()];
            for (int i = 0; i < bValues.length; i++){
                bValues[i] = gs.getHiddenMembers().get(i);
            }
            User[] users = new User[gs.getGroupMembers().size()];
            for (int i = 0; i < users.length; i++){
                users[i] = dtoToUser(gs.getGroupMembers().get(i));
            }
            return new GroupAccessSome(gs.getGroupid(), gs.getHash(), users, bValues);
        } else if (dto instanceof org.vcell.restclient.model.GroupAccessNone) {
            return new GroupAccessNone();
        }
        throw new ClassCastException("Expected classes of 'GroupAccessAll', 'GroupAccessSome', or 'GroupAccessNone' but instead got: " + dto.getClass().getName());
    }

    public static Version versionDTOToVersion(org.vcell.restclient.model.Version dto){
        return new Version(
                new KeyValue(dto.getVersionKey()), dto.getName(), dtoToUser(dto.getOwner()),
                dtoToGroupAccess(dto.getGroupAccess()), dto.getBranchPointRefKey() == null ? null : new KeyValue(dto.getBranchPointRefKey()),
                dto.getBranchID(), new Date(dto.getDate().toEpochSecond()),
                dtoToVersionFlag(dto.getFlag()), dto.getAnnot()
        );
    }

    public static BioModelChildSummary.MathType dtoToBioModelMathType(MathType dto){
        return BioModelChildSummary.MathType.valueOf(dto.getValue());
    }

    public static BioModelChildSummary dtoToBioModelChildSummary(org.vcell.restclient.model.BioModelChildSummary dto){
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
        return new BioModelChildSummary(dto.getSimulationContextNames().toArray(new String[0]),
                mathTypes, scAnnotations, simNames, simAnnots, geoNames,
                geoDims);
    }

    public static VCellSoftwareVersion dtoToVCellSoftwareVersion(org.vcell.restclient.model.VCellSoftwareVersion dto){
        return VCellSoftwareVersion.fromString(dto.getSoftwareVersionString());
    }

    public static VCDocument.VCDocumentType dtoToVCDocumentType(VCDocumentType dto){
        return VCDocument.VCDocumentType.valueOf(dto.name());
    }

    public static PublicationInfo dtoToPublicationInfo(org.vcell.restclient.model.PublicationInfo dto){
        return new PublicationInfo(new KeyValue(dto.getPublicationKey()), new KeyValue(dto.getVersionKey()),
                dto.getTitle(), dto.getAuthors().toArray(new String[0]), dto.getCitation(),dto.getPubmedid(),
                dto.getDoi(), dto.getUrl(), dtoToVCDocumentType(dto.getVcDocumentType()), dtoToUser(dto.getUser()),
                new Date(dto.getPubdate().toEpochDay()));
    }

    public static BioModelInfo bioModelContextToBioModelInfo(BioModelSummary summary){
        BioModelInfo bioModelInfo = new BioModelInfo(versionDTOToVersion(summary.getVersion()), dtoToBioModelChildSummary(summary.getSummary()),
                dtoToVCellSoftwareVersion(summary.getvCellSoftwareVersion()));
        if (summary.getPublicationInformation() != null){
            for (org.vcell.restclient.model.PublicationInfo pubInfo : summary.getPublicationInformation()){
                bioModelInfo.addPublicationInfo(dtoToPublicationInfo(pubInfo));
            }
        }
        return bioModelInfo;
    }

    public static MathModelChildSummary mathModelChildSummary(org.vcell.restclient.model.MathModelChildSummary dto){
        return new MathModelChildSummary(BioModelChildSummary.MathType.valueOf(dto.getModelType().getValue()),
                dto.getGeometryName(), dto.getGeometryDimension(), dto.getSimulationNames().toArray(new String[0]),
                dto.getSimulationAnnotations().toArray(new String[0]));
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
                DtoModelTransforms.dtoToOrigin(summary.getOrigin()), new KeyValue(summary.getImageRef()),
                DtoModelTransforms.dtoToVCellSoftwareVersion(summary.getSoftwareVersion()));
    }

    public static VCImageInfo imageSummaryToVCImageInfo(org.vcell.restclient.model.VCImageSummary summary) {
        try {
            return new VCImageInfo(DtoModelTransforms.versionDTOToVersion(summary.getVersion()),
                    DtoModelTransforms.dtoToISize(summary.getSize()), DtoModelTransforms.dtoToExtent(summary.getExtent()),
                    new GIFImage(Files.readAllBytes(summary.getPreview().getGifEncodedData().toPath())),
                    DtoModelTransforms.dtoToVCellSoftwareVersion(summary.getSoftwareVersion()));
        } catch (GifParsingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
