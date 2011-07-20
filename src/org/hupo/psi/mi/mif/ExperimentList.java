/**
 * ExperimentList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class ExperimentList  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.ExperimentDescription experimentDescription;

    private java.lang.Integer experimentRef;

    public ExperimentList() {
    }

    public ExperimentList(
           org.hupo.psi.mi.mif.ExperimentDescription experimentDescription,
           java.lang.Integer experimentRef) {
           this.experimentDescription = experimentDescription;
           this.experimentRef = experimentRef;
    }


    /**
     * Gets the experimentDescription value for this ExperimentList.
     * 
     * @return experimentDescription
     */
    public org.hupo.psi.mi.mif.ExperimentDescription getExperimentDescription() {
        return experimentDescription;
    }


    /**
     * Sets the experimentDescription value for this ExperimentList.
     * 
     * @param experimentDescription
     */
    public void setExperimentDescription(org.hupo.psi.mi.mif.ExperimentDescription experimentDescription) {
        this.experimentDescription = experimentDescription;
    }


    /**
     * Gets the experimentRef value for this ExperimentList.
     * 
     * @return experimentRef
     */
    public java.lang.Integer getExperimentRef() {
        return experimentRef;
    }


    /**
     * Sets the experimentRef value for this ExperimentList.
     * 
     * @param experimentRef
     */
    public void setExperimentRef(java.lang.Integer experimentRef) {
        this.experimentRef = experimentRef;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExperimentList)) return false;
        ExperimentList other = (ExperimentList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.experimentDescription==null && other.getExperimentDescription()==null) || 
             (this.experimentDescription!=null &&
              this.experimentDescription.equals(other.getExperimentDescription()))) &&
            ((this.experimentRef==null && other.getExperimentRef()==null) || 
             (this.experimentRef!=null &&
              this.experimentRef.equals(other.getExperimentRef())));
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
        if (getExperimentDescription() != null) {
            _hashCode += getExperimentDescription().hashCode();
        }
        if (getExperimentRef() != null) {
            _hashCode += getExperimentRef().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExperimentList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("experimentDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentDescription"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("experimentRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentRef"));
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
