package org.vcell.restclient.utils;


import org.vcell.util.Extent;
import org.vcell.util.Origin;

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


}
