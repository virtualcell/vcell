/**
 * HostOrganism.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class HostOrganism  extends org.hupo.psi.mi.mif.BioSource  implements java.io.Serializable {
    private int[] experimentRefList;

    public HostOrganism() {
    }

    public HostOrganism(
           int ncbiTaxId,
           org.hupo.psi.mi.mif.Names names,
           org.hupo.psi.mi.mif.OpenCvType cellType,
           org.hupo.psi.mi.mif.OpenCvType compartment,
           org.hupo.psi.mi.mif.OpenCvType tissue,
           int[] experimentRefList) {
        super(
            names,
            cellType,
            compartment,
            tissue,
            ncbiTaxId);
        this.experimentRefList = experimentRefList;
    }


    /**
     * Gets the experimentRefList value for this HostOrganism.
     * 
     * @return experimentRefList
     */
    public int[] getExperimentRefList() {
        return experimentRefList;
    }


    /**
     * Sets the experimentRefList value for this HostOrganism.
     * 
     * @param experimentRefList
     */
    public void setExperimentRefList(int[] experimentRefList) {
        this.experimentRefList = experimentRefList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof HostOrganism)) return false;
        HostOrganism other = (HostOrganism) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
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
        int _hashCode = super.hashCode();
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
        new org.apache.axis.description.TypeDesc(HostOrganism.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganism"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
