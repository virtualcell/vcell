/**
 * InferredInteraction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class InferredInteraction  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.InferredInteractionParticipant[] participant;

    private int[] experimentRefList;

    public InferredInteraction() {
    }

    public InferredInteraction(
           org.hupo.psi.mi.mif.InferredInteractionParticipant[] participant,
           int[] experimentRefList) {
           this.participant = participant;
           this.experimentRefList = experimentRefList;
    }


    /**
     * Gets the participant value for this InferredInteraction.
     * 
     * @return participant
     */
    public org.hupo.psi.mi.mif.InferredInteractionParticipant[] getParticipant() {
        return participant;
    }


    /**
     * Sets the participant value for this InferredInteraction.
     * 
     * @param participant
     */
    public void setParticipant(org.hupo.psi.mi.mif.InferredInteractionParticipant[] participant) {
        this.participant = participant;
    }

    public org.hupo.psi.mi.mif.InferredInteractionParticipant getParticipant(int i) {
        return this.participant[i];
    }

    public void setParticipant(int i, org.hupo.psi.mi.mif.InferredInteractionParticipant _value) {
        this.participant[i] = _value;
    }


    /**
     * Gets the experimentRefList value for this InferredInteraction.
     * 
     * @return experimentRefList
     */
    public int[] getExperimentRefList() {
        return experimentRefList;
    }


    /**
     * Sets the experimentRefList value for this InferredInteraction.
     * 
     * @param experimentRefList
     */
    public void setExperimentRefList(int[] experimentRefList) {
        this.experimentRefList = experimentRefList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InferredInteraction)) return false;
        InferredInteraction other = (InferredInteraction) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.participant==null && other.getParticipant()==null) || 
             (this.participant!=null &&
              java.util.Arrays.equals(this.participant, other.getParticipant()))) &&
            ((this.experimentRefList==null && other.getExperimentRefList()==null) || 
             (this.experimentRefList!=null &&
              java.util.Arrays.equals(this.experimentRefList, other.getExperimentRefList())));
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
        if (getParticipant() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParticipant());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParticipant(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExperimentRefList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExperimentRefList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExperimentRefList(), i);
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
        new org.apache.axis.description.TypeDesc(InferredInteraction.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteraction"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participant");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participant"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteractionParticipant"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("experimentRefList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentRefList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentRef"));
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
