/**
 * ExperimentalInteractor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class ExperimentalInteractor  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.Interactor interactor;

    private java.lang.Integer interactorRef;

    private int[] experimentRefList;

    public ExperimentalInteractor() {
    }

    public ExperimentalInteractor(
           org.hupo.psi.mi.mif.Interactor interactor,
           java.lang.Integer interactorRef,
           int[] experimentRefList) {
           this.interactor = interactor;
           this.interactorRef = interactorRef;
           this.experimentRefList = experimentRefList;
    }


    /**
     * Gets the interactor value for this ExperimentalInteractor.
     * 
     * @return interactor
     */
    public org.hupo.psi.mi.mif.Interactor getInteractor() {
        return interactor;
    }


    /**
     * Sets the interactor value for this ExperimentalInteractor.
     * 
     * @param interactor
     */
    public void setInteractor(org.hupo.psi.mi.mif.Interactor interactor) {
        this.interactor = interactor;
    }


    /**
     * Gets the interactorRef value for this ExperimentalInteractor.
     * 
     * @return interactorRef
     */
    public java.lang.Integer getInteractorRef() {
        return interactorRef;
    }


    /**
     * Sets the interactorRef value for this ExperimentalInteractor.
     * 
     * @param interactorRef
     */
    public void setInteractorRef(java.lang.Integer interactorRef) {
        this.interactorRef = interactorRef;
    }


    /**
     * Gets the experimentRefList value for this ExperimentalInteractor.
     * 
     * @return experimentRefList
     */
    public int[] getExperimentRefList() {
        return experimentRefList;
    }


    /**
     * Sets the experimentRefList value for this ExperimentalInteractor.
     * 
     * @param experimentRefList
     */
    public void setExperimentRefList(int[] experimentRefList) {
        this.experimentRefList = experimentRefList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExperimentalInteractor)) return false;
        ExperimentalInteractor other = (ExperimentalInteractor) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.interactor==null && other.getInteractor()==null) || 
             (this.interactor!=null &&
              this.interactor.equals(other.getInteractor()))) &&
            ((this.interactorRef==null && other.getInteractorRef()==null) || 
             (this.interactorRef!=null &&
              this.interactorRef.equals(other.getInteractorRef()))) &&
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
        if (getInteractor() != null) {
            _hashCode += getInteractor().hashCode();
        }
        if (getInteractorRef() != null) {
            _hashCode += getInteractorRef().hashCode();
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
        new org.apache.axis.description.TypeDesc(ExperimentalInteractor.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalInteractor"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interactor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interactorRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactorRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
