/**
 * InferredInteractionParticipant.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class InferredInteractionParticipant  implements java.io.Serializable {
    private java.lang.Integer participantFeatureRef;

    private java.lang.Integer participantRef;

    public InferredInteractionParticipant() {
    }

    public InferredInteractionParticipant(
           java.lang.Integer participantFeatureRef,
           java.lang.Integer participantRef) {
           this.participantFeatureRef = participantFeatureRef;
           this.participantRef = participantRef;
    }


    /**
     * Gets the participantFeatureRef value for this InferredInteractionParticipant.
     * 
     * @return participantFeatureRef
     */
    public java.lang.Integer getParticipantFeatureRef() {
        return participantFeatureRef;
    }


    /**
     * Sets the participantFeatureRef value for this InferredInteractionParticipant.
     * 
     * @param participantFeatureRef
     */
    public void setParticipantFeatureRef(java.lang.Integer participantFeatureRef) {
        this.participantFeatureRef = participantFeatureRef;
    }


    /**
     * Gets the participantRef value for this InferredInteractionParticipant.
     * 
     * @return participantRef
     */
    public java.lang.Integer getParticipantRef() {
        return participantRef;
    }


    /**
     * Sets the participantRef value for this InferredInteractionParticipant.
     * 
     * @param participantRef
     */
    public void setParticipantRef(java.lang.Integer participantRef) {
        this.participantRef = participantRef;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InferredInteractionParticipant)) return false;
        InferredInteractionParticipant other = (InferredInteractionParticipant) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.participantFeatureRef==null && other.getParticipantFeatureRef()==null) || 
             (this.participantFeatureRef!=null &&
              this.participantFeatureRef.equals(other.getParticipantFeatureRef()))) &&
            ((this.participantRef==null && other.getParticipantRef()==null) || 
             (this.participantRef!=null &&
              this.participantRef.equals(other.getParticipantRef())));
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
        if (getParticipantFeatureRef() != null) {
            _hashCode += getParticipantFeatureRef().hashCode();
        }
        if (getParticipantRef() != null) {
            _hashCode += getParticipantRef().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InferredInteractionParticipant.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteractionParticipant"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participantFeatureRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantFeatureRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participantRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantRef"));
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
