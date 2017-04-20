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
 * StructureDataItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.webservices.chebi;

public class StructureDataItem  implements java.io.Serializable {
    private java.lang.String structure;

    private java.lang.String type;

    private java.lang.String dimension;

    private java.lang.Boolean defaultStructure;

    private uk.ac.ebi.www.webservices.chebi.CommentDataItem[] comments;

    public StructureDataItem() {
    }

    public StructureDataItem(
           java.lang.String structure,
           java.lang.String type,
           java.lang.String dimension,
           java.lang.Boolean defaultStructure,
           uk.ac.ebi.www.webservices.chebi.CommentDataItem[] comments) {
           this.structure = structure;
           this.type = type;
           this.dimension = dimension;
           this.defaultStructure = defaultStructure;
           this.comments = comments;
    }


    /**
     * Gets the structure value for this StructureDataItem.
     * 
     * @return structure
     */
    public java.lang.String getStructure() {
        return structure;
    }


    /**
     * Sets the structure value for this StructureDataItem.
     * 
     * @param structure
     */
    public void setStructure(java.lang.String structure) {
        this.structure = structure;
    }


    /**
     * Gets the type value for this StructureDataItem.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this StructureDataItem.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the dimension value for this StructureDataItem.
     * 
     * @return dimension
     */
    public java.lang.String getDimension() {
        return dimension;
    }


    /**
     * Sets the dimension value for this StructureDataItem.
     * 
     * @param dimension
     */
    public void setDimension(java.lang.String dimension) {
        this.dimension = dimension;
    }


    /**
     * Gets the defaultStructure value for this StructureDataItem.
     * 
     * @return defaultStructure
     */
    public java.lang.Boolean getDefaultStructure() {
        return defaultStructure;
    }


    /**
     * Sets the defaultStructure value for this StructureDataItem.
     * 
     * @param defaultStructure
     */
    public void setDefaultStructure(java.lang.Boolean defaultStructure) {
        this.defaultStructure = defaultStructure;
    }


    /**
     * Gets the comments value for this StructureDataItem.
     * 
     * @return comments
     */
    public uk.ac.ebi.www.webservices.chebi.CommentDataItem[] getComments() {
        return comments;
    }


    /**
     * Sets the comments value for this StructureDataItem.
     * 
     * @param comments
     */
    public void setComments(uk.ac.ebi.www.webservices.chebi.CommentDataItem[] comments) {
        this.comments = comments;
    }

    public uk.ac.ebi.www.webservices.chebi.CommentDataItem getComments(int i) {
        return this.comments[i];
    }

    public void setComments(int i, uk.ac.ebi.www.webservices.chebi.CommentDataItem _value) {
        this.comments[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StructureDataItem)) return false;
        StructureDataItem other = (StructureDataItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.structure==null && other.getStructure()==null) || 
             (this.structure!=null &&
              this.structure.equals(other.getStructure()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.dimension==null && other.getDimension()==null) || 
             (this.dimension!=null &&
              this.dimension.equals(other.getDimension()))) &&
            ((this.defaultStructure==null && other.getDefaultStructure()==null) || 
             (this.defaultStructure!=null &&
              this.defaultStructure.equals(other.getDefaultStructure()))) &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              java.util.Arrays.equals(this.comments, other.getComments())));
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
        if (getStructure() != null) {
            _hashCode += getStructure().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getDimension() != null) {
            _hashCode += getDimension().hashCode();
        }
        if (getDefaultStructure() != null) {
            _hashCode += getDefaultStructure().hashCode();
        }
        if (getComments() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getComments());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getComments(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StructureDataItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "StructureDataItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("structure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "structure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dimension");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "dimension"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultStructure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "defaultStructure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comments");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "Comments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "CommentDataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
