package org.vcell.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import java.io.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class CompressionUtils {
    private static final Logger lg = LogManager.getLogger(CompressionUtils.class);

    public static byte[] compress(byte[] bytes) throws IOException{
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (DeflaterOutputStream dos = new DeflaterOutputStream(bos)) {
                dos.write(bytes, 0, bytes.length);
            }
            return bos.toByteArray();
        }
    }

    public static byte[] uncompress(byte[] compressedBytes) throws IOException{
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedBytes)) {
                try (InflaterInputStream iis = new InflaterInputStream(bis)) {
                    int temp;
                    byte[] buf = new byte[65536]; // 64 KiB
                    while ((temp = iis.read(buf, 0, buf.length)) != -1) {
                        bos.write(buf, 0, temp);
                    }
                }
            }
            return bos.toByteArray();
        }
    }

    public static byte[] toCompressedSerialized(Serializable cacheObj) throws IOException{
        long before = System.currentTimeMillis();
        byte[] compressedSerialization;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (DeflaterOutputStream dos = new DeflaterOutputStream(bos)) {
                try (ObjectOutputStream oos = new ObjectOutputStream(dos)) {
                    oos.writeObject(cacheObj);
                    oos.flush();
                }
                dos.flush();
            }
            bos.flush();
            compressedSerialization = bos.toByteArray();
        }

        long after = System.currentTimeMillis();
        if(lg.isTraceEnabled()){
            byte[] normalSerialization = CompressionUtils.toSerialized(cacheObj);
            lg.trace(String.format("toCompressedSerialized(), t=%d ms, (%s) ratio=%d/%d",
                    (after - before), cacheObj.toString(), compressedSerialization.length, normalSerialization.length));
        }

        return compressedSerialization;
    }

    public static Serializable fromCompressedSerialized(byte[] objData) throws ClassNotFoundException, IOException{
        long before = 0, after;
        boolean traceIsEnabled = lg.isTraceEnabled();
        if(traceIsEnabled) before = System.currentTimeMillis();

        Serializable cacheClone;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(objData)) {
            try (InflaterInputStream iis = new InflaterInputStream(bis)) {
                try (ObjectInputStream ois = new ObjectInputStream(iis)) {
                    cacheClone = (Serializable) ois.readObject();
                }
            }
        }

        if(traceIsEnabled){
            after = System.currentTimeMillis();
            lg.trace(String.format("fromCompressedSerialized(): t=%d ms, (%s)", after - before, cacheClone.toString()));
        }
        return cacheClone;
    }

    public static byte[] toSerialized(Serializable cacheObj) throws IOException{
        long before = 0;
        if(lg.isTraceEnabled()){
            before = System.currentTimeMillis();
        }

        byte[] objData;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(cacheObj);
                oos.flush();
            }
            objData = bos.toByteArray();
        }

        if(lg.isTraceEnabled()){
            long after = System.currentTimeMillis();
            lg.trace(String.format("toSerialized, t=%d ms, (%s)", after - before, cacheObj.toString()));
        }

        return objData;
    }

    public static Serializable fromSerialized(byte[] objData) throws ClassNotFoundException, IOException{
        long before = 0, after;
        if(lg.isTraceEnabled()) before = System.currentTimeMillis();

        Serializable cacheClone;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(objData)) {
            try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                cacheClone = (Serializable) ois.readObject();
            }
        }

        if(lg.isTraceEnabled()){
            after = System.currentTimeMillis();
            lg.trace(String.format("fromSerialized(): t=%d ms, (%s)", after - before, cacheClone.toString()));
        }
        return cacheClone;
    }
}
