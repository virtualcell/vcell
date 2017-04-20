/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * LiteEntity.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.webservices.chebi;

public class LiteEntity  implements java.io.Serializable {
    private java.lang.String chebiId;

    private java.lang.String chebiAsciiName;

    private java.lang.Float searchScore;

    private java.lang.Integer entityStar;

    public LiteEntity() {
    }

    public LiteEntity(
           java.lang.String chebiId,
           java.lang.String chebiAsciiName,
           java.lang.Float searchScore,
           java.lang.Integer entityStar) {
           this.chebiId = chebiId;
           this.chebiAsciiName = chebiAsciiName;
           this.searchScore = searchScore;
           this.entityStar = entityStar;
    }


    /**
     * Gets the chebiId value for this LiteEntity.
     * 
     * @return chebiId
     */
    public java.lang.String getChebiId() {
        return chebiId;
    }


    /**
     * Sets the chebiId value for this LiteEntity.
     * 
     * @param chebiId
     */
    public void setChebiId(java.lang.String chebiId) {
        this.chebiId = chebiId;
    }


    /**
     * Gets the chebiAsciiName value for this LiteEntity.
     * 
     * @return chebiAsciiName
     */
    public java.lang.String getChebiAsciiName() {
        return chebiAsciiName;
    }


    /**
     * Sets the chebiAsciiName value for this LiteEntity.
     * 
     * @param chebiAsciiName
     */
    public void setChebiAsciiName(java.lang.String chebiAsciiName) {
        this.chebiAsciiName = chebiAsciiName;
    }


    /**
     * Gets the searchScore value for this LiteEntity.
     * 
     * @return searchScore
     */
    public java.lang.Float getSearchScore() {
        return searchScore;
    }


    /**
     * Sets the searchScore value for this LiteEntity.
     * 
     * @param searchScore
     */
    public void setSearchScore(java.lang.Float searchScore) {
        this.searchScore = searchScore;
    }


    /**
     * Gets the entityStar value for this LiteEntity.
     * 
     * @return entityStar
     */
    public java.lang.Integer getEntityStar() {
        return entityStar;
    }


    /**
     * Sets the entityStar value for this LiteEntity.
     * 
     * @param entityStar
     */
    public void setEntityStar(java.lang.Integer entityStar) {
        this.entityStar = entityStar;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LiteEntity)) return false;
        LiteEntity other = (LiteEntity) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.chebiId==null && other.getChebiId()==null) || 
             (this.chebiId!=null &&
              this.chebiId.equals(other.getChebiId()))) &&
            ((this.chebiAsciiName==null && other.getChebiAsciiName()==null) || 
             (this.chebiAsciiName!=null &&
              this.chebiAsciiName.equals(other.getChebiAsciiName()))) &&
            ((this.searchScore==null && other.getSearchScore()==null) || 
             (this.searchScore!=null &&
              this.searchScore.equals(other.getSearchScore()))) &&
            ((this.entityStar==null && other.getEntityStar()==null) || 
             (this.entityStar!=null &&
              this.entityStar.equals(other.getEntityStar())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getChebiId() != null) {
            _hashCode += getChebiId().hashCode();
        }
        if (getChebiAsciiName() != null) {
            _hashCode += getChebiAsciiName().hashCode();
        }
        if (getSearchScore() != null) {
            _hashCode += getSearchScore().hashCode();
        }
        if (getEntityStar() != null) {
            _hashCode += getEntityStar().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LiteEntity.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "LiteEntity"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chebiId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "chebiId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chebiAsciiName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "chebiAsciiName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchScore");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "searchScore"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entityStar");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "entityStar"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
