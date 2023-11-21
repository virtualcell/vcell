/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cbit.vcell.math.VariableType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.CompressionUtils;

/**
 * This type was created in VisualAge.
 */
public class SimDataBlock implements java.io.Serializable, SimDataHolder {
    private final static Logger lg = LogManager.getLogger(SimDataBlock.class);

    private transient PDEDataInfo info = null;
    private transient double data[] = null;
    private transient VariableType varType = null;

    private byte[] compressedBytes = null;

    /**
     * SimDataBlock constructor comment.
     */
    public SimDataBlock(PDEDataInfo info, double data[], VariableType argVarType){
        this.info = info;
        this.data = data;
        this.varType = argVarType;
    }


    /**
     * This method was created in VisualAge.
     *
     * @return double[]
     */
    public double[] getData(){
        if(data == null){
            inflate();
        }
        return data;
    }


    /**
     * This method was created in VisualAge.
     *
     * @return cbit.vcell.server.SimDataInfo
     */
    public PDEDataInfo getPDEDataInfo(){
        if(info == null){
            inflate();
        }
        return info;
    }


    /**
     * This method was created in VisualAge.
     *
     * @return long
     */
    public long getSizeInBytes(){
        return data.length * 8;
    }


    /**
     * This method was created in VisualAge.
     *
     * @return long
     */
    public long getTimeStamp(){
        return getPDEDataInfo().getTimeStamp();
    }


    /**
     * Insert the method's description here.
     * Creation date: (7/6/01 1:33:24 PM)
     *
     * @return cbit.vcell.simdata.VariableType
     */
    public VariableType getVariableType(){
        if(varType == null){
            inflate();
        }
        return varType;
    }


    private void inflate(){
        if(compressedBytes == null){
            return;
        }

        try {
            //Object objArray[] =  { varType, info, data};
            Object[] objArray = (Object[]) CompressionUtils.fromCompressedSerialized(compressedBytes);
            varType = (VariableType) objArray[0];
            info = (PDEDataInfo) objArray[1];
            data = (double[]) objArray[2];

            compressedBytes = null;

        } catch(Exception ex){
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }


    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException{
        int compressedSize = s.readInt();
        compressedBytes = new byte[compressedSize];
        s.readFully(compressedBytes, 0, compressedSize);
    }


    private void writeObject(ObjectOutputStream s) throws IOException{
        Object[] objArray = {varType, info, data};

        if(compressedBytes == null){
            compressedBytes = CompressionUtils.toCompressedSerialized(objArray);
        }
        s.writeInt(compressedBytes.length);
        s.write(compressedBytes);
    }
}
