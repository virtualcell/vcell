package org.vcell.restclient.utils;


import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.DataIdentifier;
import org.vcell.restclient.model.AnalyzedResultsFromFieldData;
import org.vcell.restclient.model.FieldDataSaveResults;
import org.vcell.restclient.model.FieldDataShape;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        dto.key(keyValueToDTO(externalDataIdentifier.getKey()));
        dto.owner(userToDTO(externalDataIdentifier.getOwner()));
        dto.name(externalDataIdentifier.getName());
        return dto;
    }

    public static ExternalDataIdentifier dtoToExternalDataIdentifier(org.vcell.restclient.model.ExternalDataIdentifier dto){
        return new ExternalDataIdentifier(
                dtoToKeyValue(dto.getKey()), dtoToUser(dto.getOwner()), dto.getName()
        );
    }

    public static org.vcell.restclient.model.KeyValue keyValueToDTO(KeyValue kv) {
        org.vcell.restclient.model.KeyValue k = new org.vcell.restclient.model.KeyValue();
        k.setValue(kv.toString().transform(BigDecimal::new));
        return k;
    }
    public static KeyValue dtoToKeyValue(org.vcell.restclient.model.KeyValue dto){
        return dto == null ? null : new KeyValue(dto.getValue());
    }

    public static org.vcell.restclient.model.User userToDTO(User user) {
        org.vcell.restclient.model.User userDTO = new org.vcell.restclient.model.User();
        userDTO.setUserName(user.getName());
        userDTO.setKey(keyValueToDTO(user.getID()));
        return userDTO;
    }

    public static User dtoToUser(org.vcell.restclient.model.User dto){
        return new User(dto.getUserName(), dtoToKeyValue(dto.getKey()));
    }

    public static FieldDataFileOperationResults fieldDataInfoDTOToFileOperationResults(FieldDataShape dto){
        FieldDataFileOperationResults results = new FieldDataFileOperationResults();
        results.extent = dtoToExtent(dto.getExtent());
        results.origin = dtoToOrigin(dto.getOrigin());
        results.iSize = dtoToISize(dto.getIsize());
        results.times = dto.getTimes().stream().mapToDouble(Double::doubleValue).toArray();
        results.dataIdentifierArr = dto.getDataIdentifier().stream().map(DtoModelTransforms::dtoToDataIdentifier).toArray(DataIdentifier[]::new);
        return results;
    }

    public static FieldDataFileOperationResults fieldDataSaveResultsDTOToFileOperationResults(FieldDataSaveResults dto, User owner){
        FieldDataFileOperationResults fieldDataFileOperationResults = new FieldDataFileOperationResults();
        fieldDataFileOperationResults.externalDataIdentifier = new ExternalDataIdentifier(new KeyValue(dto.getFieldDataID()), owner, dto.getFieldDataName());
        return fieldDataFileOperationResults;
    }

    public static AnalyzedResultsFromFieldData fieldDataSpecToAnalyzedResultsDTO(FieldDataFileOperationSpec fieldDataFileOperationSpec){
        List<List<List<Integer>>> listVersion = Arrays.stream(fieldDataFileOperationSpec.shortSpecData) // Stream of short[][]
                .map(twoDArray -> Arrays.stream(twoDArray) // Stream of short[]
                        .map(oneDArray -> {
                            List<Integer> list = new ArrayList<>();
                            for (short j : oneDArray) {
                                list.add((int) j);
                            }
                            return list;
                        }).collect(Collectors.toList())).collect(Collectors.toList());
        AnalyzedResultsFromFieldData analyzedResultsFromFieldData = new AnalyzedResultsFromFieldData();
        analyzedResultsFromFieldData.annotation(fieldDataFileOperationSpec.annotation); analyzedResultsFromFieldData.isize(iSizeToDTO(fieldDataFileOperationSpec.isize));
        analyzedResultsFromFieldData.extent(extentToDTO(fieldDataFileOperationSpec.extent)); analyzedResultsFromFieldData.origin(originToDTO(fieldDataFileOperationSpec.origin));
        analyzedResultsFromFieldData.times(Arrays.stream(fieldDataFileOperationSpec.times).boxed().toList()); analyzedResultsFromFieldData.setName(fieldDataFileOperationSpec.fieldDataName);
        analyzedResultsFromFieldData.varNames(Arrays.stream(fieldDataFileOperationSpec.varNames).toList()); analyzedResultsFromFieldData.shortSpecData(listVersion);
        return analyzedResultsFromFieldData;
    }

}
