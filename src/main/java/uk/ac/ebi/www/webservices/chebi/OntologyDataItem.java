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
 * OntologyDataItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.webservices.chebi;

public class OntologyDataItem  implements java.io.Serializable {
    private java.lang.String chebiName;

    private java.lang.String chebiId;

    private java.lang.String type;

    private java.lang.String status;

    private boolean cyclicRelationship;

    private uk.ac.ebi.www.webservices.chebi.CommentDataItem[] comments;

    private uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] ontologyElement;

    public OntologyDataItem() {
    }

    public OntologyDataItem(
           java.lang.String chebiName,
           java.lang.String chebiId,
           java.lang.String type,
           java.lang.String status,
           boolean cyclicRelationship,
           uk.ac.ebi.www.webservices.chebi.CommentDataItem[] comments,
           uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] ontologyElement) {
           this.chebiName = chebiName;
           this.chebiId = chebiId;
           this.type = type;
           this.status = status;
           this.cyclicRelationship = cyclicRelationship;
           this.comments = comments;
           this.ontologyElement = ontologyElement;
    }


    /**
     * Gets the chebiName value for this OntologyDataItem.
     * 
     * @return chebiName
     */
    public java.lang.String getChebiName() {
        return chebiName;
    }


    /**
     * Sets the chebiName value for this OntologyDataItem.
     * 
     * @param chebiName
     */
    public void setChebiName(java.lang.String chebiName) {
        this.chebiName = chebiName;
    }


    /**
     * Gets the chebiId value for this OntologyDataItem.
     * 
     * @return chebiId
     */
    public java.lang.String getChebiId() {
        return chebiId;
    }


    /**
     * Sets the chebiId value for this OntologyDataItem.
     * 
     * @param chebiId
     */
    public void setChebiId(java.lang.String chebiId) {
        this.chebiId = chebiId;
    }


    /**
     * Gets the type value for this OntologyDataItem.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this OntologyDataItem.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the status value for this OntologyDataItem.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this OntologyDataItem.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the cyclicRelationship value for this OntologyDataItem.
     * 
     * @return cyclicRelationship
     */
    public boolean isCyclicRelationship() {
        return cyclicRelationship;
    }


    /**
     * Sets the cyclicRelationship value for this OntologyDataItem.
     * 
     * @param cyclicRelationship
     */
    public void setCyclicRelationship(boolean cyclicRelationship) {
        this.cyclicRelationship = cyclicRelationship;
    }


    /**
     * Gets the comments value for this OntologyDataItem.
     * 
     * @return comments
     */
    public uk.ac.ebi.www.webservices.chebi.CommentDataItem[] getComments() {
        return comments;
    }


    /**
     * Sets the comments value for this OntologyDataItem.
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


    /**
     * Gets the ontologyElement value for this OntologyDataItem.
     * 
     * @return ontologyElement
     */
    public uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] getOntologyElement() {
        return ontologyElement;
    }


    /**
     * Sets the ontologyElement value for this OntologyDataItem.
     * 
     * @param ontologyElement
     */
    public void setOntologyElement(uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] ontologyElement) {
        this.ontologyElement = ontologyElement;
    }

    public uk.ac.ebi.www.webservices.chebi.OntologyDataItem getOntologyElement(int i) {
        return this.ontologyElement[i];
    }

    public void setOntologyElement(int i, uk.ac.ebi.www.webservices.chebi.OntologyDataItem _value) {
        this.ontologyElement[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OntologyDataItem)) return false;
        OntologyDataItem other = (OntologyDataItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.chebiName==null && other.getChebiName()==null) || 
             (this.chebiName!=null &&
              this.chebiName.equals(other.getChebiName()))) &&
            ((this.chebiId==null && other.getChebiId()==null) || 
             (this.chebiId!=null &&
              this.chebiId.equals(other.getChebiId()))) &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            this.cyclicRelationship == other.isCyclicRelationship() &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              java.util.Arrays.equals(this.comments, other.getComments()))) &&
            ((this.ontologyElement==null && other.getOntologyElement()==null) || 
             (this.ontologyElement!=null &&
              java.util.Arrays.equals(this.ontologyElement, other.getOntologyElement())));
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
        if (getChebiName() != null) {
            _hashCode += getChebiName().hashCode();
        }
        if (getChebiId() != null) {
            _hashCode += getChebiId().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        _hashCode += (isCyclicRelationship() ? Boolean.TRUE : Boolean.FALSE).hashCode();
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
        if (getOntologyElement() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOntologyElement());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOntologyElement(), i);
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
        new org.apache.axis.description.TypeDesc(OntologyDataItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "OntologyDataItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chebiName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "chebiName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chebiId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "chebiId"));
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
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cyclicRelationship");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "cyclicRelationship"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ontologyElement");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "OntologyElement"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "OntologyDataItem"));
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
