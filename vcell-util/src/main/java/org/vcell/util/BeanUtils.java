/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


package org.vcell.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * Insert the type's description here.
 * Creation date: (8/18/2000 2:29:31 AM)
 */
public final class BeanUtils {
    private static final Logger lg = LogManager.getLogger(BeanUtils.class);
    public static final String vcDateFormat = "dd-MMM-yyyy HH:mm:ss";
    public static final DateTimeFormatter vcDateTimeFormatter = DateTimeFormatter.ofPattern(vcDateFormat);

    /**
     * newline used in email of Content-Type: text/plain
     */
    public static final String PLAINTEXT_EMAIL_NEWLINE = "\r\n";

    public static final String FD_EXP_MESSG = "Didn't find field functions";


    public static class XYZ {
        public double x;
        public double y;
        public double z;

        public XYZ(){
            x = 0;
            y = 0;
            z = 0;
        }

        public XYZ(double argX, double argY, double argZ){
            x = argX;
            y = argY;
            z = argZ;
        }
    }

    public static Range selectRange(boolean bAuto, boolean bAllTimes, Range rangeFromClient, Range currentVarAndTimeValRange){
        if(bAuto && !bAllTimes){// value range at each current time-point
            return currentVarAndTimeValRange;
        }
        // will have been set by user, custom range
        // or will have been calculated during sim run (PostProcessing), if bAlltimes selected
        return rangeFromClient;
    }

    public static Serializable cloneSerializable(Serializable obj) throws ClassNotFoundException, java.io.IOException{
        return CompressionUtils.fromSerialized(CompressionUtils.toSerialized(obj));
    }

    public static int parameterScanCoordinateToJobIndex(int[] parameterScanCoordinate, int[] scanBounds){
        // Used to look up simulation job index from parameter scans in MathOverrides
        // see also jobIndexToScanParameterCoordinate()
        //     Note 1: scanBounds is the highest zero-based index in each dimension (e.g. 4,5,6 for 3D matrix of size 5x6x7)
        //     Note 2: PLEASE DO NOT CHANGE MAPPING - it is immortalized by stored simulation job datasets
        if(parameterScanCoordinate.length != scanBounds.length)
            throw new RuntimeException("coordinates and bounds arrays have different lengths");
        int jobIndex = 0;
        for(int i = 0; i < scanBounds.length; i++){
            if(parameterScanCoordinate[i] < 0 || scanBounds[i] < parameterScanCoordinate[i]) throw new RuntimeException("invalid coordinate");
            int offset = 1;
            for(int j = i + 1; j < scanBounds.length; j++){
                offset *= scanBounds[j] + 1;
            }
            jobIndex += parameterScanCoordinate[i] * offset;
        }
        return jobIndex;
    }

    public static int[] jobIndexToScanParameterCoordinate(int jobIndex, int[] scanBounds){
        // Used to look up MathOverrides parameter scan indices from simulation Job indices
        // see also parameterScanCoordinateToJobIndex()
        //     Note 1: scanBounds is the highest zero-based index in each dimension (e.g. 4,5,6 for 3D matrix of size 5x6x7)
        //     Note 2: PLEASE DO NOT CHANGE MAPPING - it is immortalized by stored simulation job datasets
        if(jobIndex < 0) throw new RuntimeException("invalid index, negative number");
        int[] parameterScanCoordinates = new int[scanBounds.length];
        for(int i = 0; i < scanBounds.length; i++){
            int offset = 1;
            for(int j = i + 1; j < scanBounds.length; j++){
                offset *= scanBounds[j] + 1;
            }
            parameterScanCoordinates[i] = jobIndex / offset;
            if(parameterScanCoordinates[i] > scanBounds[i]) throw new RuntimeException("invalid index, number too large");
            jobIndex -= offset * parameterScanCoordinates[i];
        }
        return parameterScanCoordinates;
    }

    public static String forceStringLength(String originalString, int targetLength, String pad, boolean shouldPrependPad){
        if(targetLength < 0) throw new IllegalArgumentException("Size can not be negative");
        if(targetLength == 0) return "";

        String padString = pad == null || pad.isEmpty() ? " " : pad;
        StringBuilder fullPadding = new StringBuilder();

        fullPadding.append(padString.repeat(targetLength));

        // We just want a padding
        if(originalString == null || originalString.isEmpty()) return fullPadding.substring(0, targetLength);

        // We just want a substring
        if(targetLength <= originalString.length()) return originalString.substring(0, targetLength);
        // Todo: Add option to get end-substring using `shouldPrependPad = false`

        if(shouldPrependPad) return fullPadding.substring(0, targetLength - originalString.length()) + originalString;

        return originalString + fullPadding.substring(0, targetLength - originalString.length());
    }

    public static boolean triggersPropertyChangeEvent(Object a, Object b){
        return (a == null || !a.equals(b));
    }


    public static void sendSMTP(String smtpHost, int smtpPort, String from, String to, String subject, String content)
            throws MessagingException{

        // Create a mail session
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", "" + smtpPort);
        Session session = Session.getDefaultInstance(props, null);

        // Construct the message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(content);

        // Send the message
        Transport.send(msg);
    }

    public static String readBytesFromFile(File file, ClientTaskStatusSupport clientTaskStatusSupport) throws IOException{
        StringBuilder stringBuffer = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
                if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
                    break;
                }
            }
        }
        return stringBuffer.toString();
    }

    public static String exceptionMessage(Throwable e){
        if(e.getMessage() != null){
            return e.getMessage();
        }

        if(e.getCause() != null){
            if(e.getCause().getMessage() != null){
                return e.getCause().getMessage();
            } else {
                return e.getCause().getClass().getName();
            }
        } else {
            return e.getClass().getName();
        }
    }

    public static final String SQL_ESCAPE_CHARACTER = "/";

    public static String convertToSQLSearchString(String searchString){
        if(searchString == null || searchString.isEmpty()){
            return searchString;
        }
        return getConvertedLikeString(searchString);
    }

    private static String getConvertedLikeString(String searchString){
        //The character "%" matches any string of zero or more characters except null.
        //The character "_" matches any single character.
        //A wildcard character is treated as a literal if preceded by the character designated as the escape character.
        //Default ESCAPE character for VCell = '/' defined in DictionaryDbDriver.getDatabaseSpecies
        StringBuilder convertedLikeString = new StringBuilder();
        StringBuilder sb = new StringBuilder(searchString);
        for(int i = 0; i < sb.length(); i += 1){
            if(sb.charAt(i) == '*'){
                convertedLikeString.append("%");//sql wildcard
            } else if(sb.charAt(i) == '%'){
                convertedLikeString.append(SQL_ESCAPE_CHARACTER + "%");// "%" special in sql 'like'
            } else if(sb.charAt(i) == '_'){
                convertedLikeString.append(SQL_ESCAPE_CHARACTER + "_");// "_" special in sql 'like'
            } else {
                convertedLikeString.append(sb.charAt(i));
            }
        }
        if(!convertedLikeString.toString().contains("%") && !convertedLikeString.toString().contains("_")){
            convertedLikeString = new StringBuilder("%" + convertedLikeString + "%");
        }
        return convertedLikeString.toString();
    }

    /**
     * @param clzz class to get leaf from
     * @return class name without package info
     * @throws NullPointerException if clzz null
     */
    public static String leafName(Class<?> clzz){
        String cname = clzz.getName();
        final int ldot = cname.lastIndexOf('.');
        if(ldot != -1){
            cname = cname.substring(ldot + 1);
        }
        return cname;
    }

    /**
     * see if numerator / divisor is integer using decimal (base 10) arithmetic instead of
     * floating point (base 2)
     *
     * @param numerator the numerator
     * @param divisor   the divisor
     * @return true if decimal numerator / decimal divisor is integer
     */
    public static boolean isNotAnIntegerMultiple(double numerator, double divisor){
        BigDecimal n = BigDecimal.valueOf(numerator);
        BigDecimal d = BigDecimal.valueOf(divisor);
        BigDecimal remainder = n.remainder(d);
        return remainder.compareTo(BigDecimal.ZERO) != 0;
    }

    public static String generateDateTimeString(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return String.format("%d%02d%02d_%02d%02d%02d", year, month, day, hour, minute, second);
    }
}
