/**
 * ExperimentDescription.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class ExperimentDescription  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.Names names;

    private org.hupo.psi.mi.mif.Bibref bibref;

    private org.hupo.psi.mi.mif.Xref xref;

    private org.hupo.psi.mi.mif.HostOrganism[] hostOrganismList;

    private org.hupo.psi.mi.mif.CvType interactionDetectionMethod;

    private org.hupo.psi.mi.mif.CvType participantIdentificationMethod;

    private org.hupo.psi.mi.mif.CvType featureDetectionMethod;

    private org.hupo.psi.mi.mif.Confidence[] confidenceList;

    private org.hupo.psi.mi.mif.Attribute[] attributeList;

    private int id;  // attribute

    public ExperimentDescription() {
    }

    public ExperimentDescription(
           org.hupo.psi.mi.mif.Names names,
           org.hupo.psi.mi.mif.Bibref bibref,
           org.hupo.psi.mi.mif.Xref xref,
           org.hupo.psi.mi.mif.HostOrganism[] hostOrganismList,
           org.hupo.psi.mi.mif.CvType interactionDetectionMethod,
           org.hupo.psi.mi.mif.CvType participantIdentificationMethod,
           org.hupo.psi.mi.mif.CvType featureDetectionMethod,
           org.hupo.psi.mi.mif.Confidence[] confidenceList,
           org.hupo.psi.mi.mif.Attribute[] attributeList,
           int id) {
           this.names = names;
           this.bibref = bibref;
           this.xref = xref;
           this.hostOrganismList = hostOrganismList;
           this.interactionDetectionMethod = interactionDetectionMethod;
           this.participantIdentificationMethod = participantIdentificationMethod;
           this.featureDetectionMethod = featureDetectionMethod;
           this.confidenceList = confidenceList;
           this.attributeList = attributeList;
           this.id = id;
    }


    /**
     * Gets the names value for this ExperimentDescription.
     * 
     * @return names
     */
    public org.hupo.psi.mi.mif.Names getNames() {
        return names;
    }


    /**
     * Sets the names value for this ExperimentDescription.
     * 
     * @param names
     */
    public void setNames(org.hupo.psi.mi.mif.Names names) {
        this.names = names;
    }


    /**
     * Gets the bibref value for this ExperimentDescription.
     * 
     * @return bibref
     */
    public org.hupo.psi.mi.mif.Bibref getBibref() {
        return bibref;
    }


    /**
     * Sets the bibref value for this ExperimentDescription.
     * 
     * @param bibref
     */
    public void setBibref(org.hupo.psi.mi.mif.Bibref bibref) {
        this.bibref = bibref;
    }


    /**
     * Gets the xref value for this ExperimentDescription.
     * 
     * @return xref
     */
    public org.hupo.psi.mi.mif.Xref getXref() {
        return xref;
    }


    /**
     * Sets the xref value for this ExperimentDescription.
     * 
     * @param xref
     */
    public void setXref(org.hupo.psi.mi.mif.Xref xref) {
        this.xref = xref;
    }


    /**
     * Gets the hostOrganismList value for this ExperimentDescription.
     * 
     * @return hostOrganismList
     */
    public org.hupo.psi.mi.mif.HostOrganism[] getHostOrganismList() {
        return hostOrganismList;
    }


    /**
     * Sets the hostOrganismList value for this ExperimentDescription.
     * 
     * @param hostOrganismList
     */
    public void setHostOrganismList(org.hupo.psi.mi.mif.HostOrganism[] hostOrganismList) {
        this.hostOrganismList = hostOrganismList;
    }


    /**
     * Gets the interactionDetectionMethod value for this ExperimentDescription.
     * 
     * @return interactionDetectionMethod
     */
    public org.hupo.psi.mi.mif.CvType getInteractionDetectionMethod() {
        return interactionDetectionMethod;
    }


    /**
     * Sets the interactionDetectionMethod value for this ExperimentDescription.
     * 
     * @param interactionDetectionMethod
     */
    public void setInteractionDetectionMethod(org.hupo.psi.mi.mif.CvType interactionDetectionMethod) {
        this.interactionDetectionMethod = interactionDetectionMethod;
    }


    /**
     * Gets the participantIdentificationMethod value for this ExperimentDescription.
     * 
     * @return participantIdentificationMethod
     */
    public org.hupo.psi.mi.mif.CvType getParticipantIdentificationMethod() {
        return participantIdentificationMethod;
    }


    /**
     * Sets the participantIdentificationMethod value for this ExperimentDescription.
     * 
     * @param participantIdentificationMethod
     */
    public void setParticipantIdentificationMethod(org.hupo.psi.mi.mif.CvType participantIdentificationMethod) {
        this.participantIdentificationMethod = participantIdentificationMethod;
    }


    /**
     * Gets the featureDetectionMethod value for this ExperimentDescription.
     * 
     * @return featureDetectionMethod
     */
    public org.hupo.psi.mi.mif.CvType getFeatureDetectionMethod() {
        return featureDetectionMethod;
    }


    /**
     * Sets the featureDetectionMethod value for this ExperimentDescription.
     * 
     * @param featureDetectionMethod
     */
    public void setFeatureDetectionMethod(org.hupo.psi.mi.mif.CvType featureDetectionMethod) {
        this.featureDetectionMethod = featureDetectionMethod;
    }


    /**
     * Gets the confidenceList value for this ExperimentDescription.
     * 
     * @return confidenceList
     */
    public org.hupo.psi.mi.mif.Confidence[] getConfidenceList() {
        return confidenceList;
    }


    /**
     * Sets the confidenceList value for this ExperimentDescription.
     * 
     * @param confidenceList
     */
    public void setConfidenceList(org.hupo.psi.mi.mif.Confidence[] confidenceList) {
        this.confidenceList = confidenceList;
    }


    /**
     * Gets the attributeList value for this ExperimentDescription.
     * 
     * @return attributeList
     */
    public org.hupo.psi.mi.mif.Attribute[] getAttributeList() {
        return attributeList;
    }


    /**
     * Sets the attributeList value for this ExperimentDescription.
     * 
     * @param attributeList
     */
    public void setAttributeList(org.hupo.psi.mi.mif.Attribute[] attributeList) {
        this.attributeList = attributeList;
    }


    /**
     * Gets the id value for this ExperimentDescription.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this ExperimentDescription.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExperimentDescription)) return false;
        ExperimentDescription other = (ExperimentDescription) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.names==null && other.getNames()==null) || 
             (this.names!=null &&
              this.names.equals(other.getNames()))) &&
            ((this.bibref==null && other.getBibref()==null) || 
             (this.bibref!=null &&
              this.bibref.equals(other.getBibref()))) &&
            ((this.xref==null && other.getXref()==null) || 
             (this.xref!=null &&
              this.xref.equals(other.getXref()))) &&
            ((this.hostOrganismList==null && other.getHostOrganismList()==null) || 
             (this.hostOrganismList!=null &&
              java.util.Arrays.equals(this.hostOrganismList, other.getHostOrganismList()))) &&
            ((this.interactionDetectionMethod==null && other.getInteractionDetectionMethod()==null) || 
             (this.interactionDetectionMethod!=null &&
              this.interactionDetectionMethod.equals(other.getInteractionDetectionMethod()))) &&
            ((this.participantIdentificationMethod==null && other.getParticipantIdentificationMethod()==null) || 
             (this.participantIdentificationMethod!=null &&
              this.participantIdentificationMethod.equals(other.getParticipantIdentificationMethod()))) &&
            ((this.featureDetectionMethod==null && other.getFeatureDetectionMethod()==null) || 
             (this.featureDetectionMethod!=null &&
              this.featureDetectionMethod.equals(other.getFeatureDetectionMethod()))) &&
            ((this.confidenceList==null && other.getConfidenceList()==null) || 
             (this.confidenceList!=null &&
              java.util.Arrays.equals(this.confidenceList, other.getConfidenceList()))) &&
            ((this.attributeList==null && other.getAttributeList()==null) || 
             (this.attributeList!=null &&
              java.util.Arrays.equals(this.attributeList, other.getAttributeList()))) &&
            this.id == other.getId();
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
        if (getNames() != null) {
            _hashCode += getNames().hashCode();
        }
        if (getBibref() != null) {
            _hashCode += getBibref().hashCode();
        }
        if (getXref() != null) {
            _hashCode += getXref().hashCode();
        }
        if (getHostOrganismList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getHostOrganismList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getHostOrganismList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getInteractionDetectionMethod() != null) {
            _hashCode += getInteractionDetectionMethod().hashCode();
        }
        if (getParticipantIdentificationMethod() != null) {
            _hashCode += getParticipantIdentificationMethod().hashCode();
        }
        if (getFeatureDetectionMethod() != null) {
            _hashCode += getFeatureDetectionMethod().hashCode();
        }
        if (getConfidenceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConfidenceList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getConfidenceList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAttributeList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAttributeList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttributeList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getId();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExperimentDescription.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentDescription"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("id");
        attrField.setXmlName(new javax.xml.namespace.QName("", "id"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("names");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "names"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "names"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bibref");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "bibref"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "bibref"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("xref");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "xref"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "xref"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostOrganismList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganismList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganism"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganism"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interactionDetectionMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactionDetectionMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participantIdentificationMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantIdentificationMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("featureDetectionMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "featureDetectionMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confidenceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidenceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidence"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidence"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attributeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attribute"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attribute"));
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
