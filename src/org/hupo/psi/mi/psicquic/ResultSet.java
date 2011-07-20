/**
 * ResultSet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.psicquic;

public class ResultSet  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.Entry[] entrySet;

    private java.lang.String mitab;

    public ResultSet() {
    }

    public ResultSet(
           org.hupo.psi.mi.mif.Entry[] entrySet,
           java.lang.String mitab) {
           this.entrySet = entrySet;
           this.mitab = mitab;
    }


    /**
     * Gets the entrySet value for this ResultSet.
     * 
     * @return entrySet
     */
    public org.hupo.psi.mi.mif.Entry[] getEntrySet() {
        return entrySet;
    }


    /**
     * Sets the entrySet value for this ResultSet.
     * 
     * @param entrySet
     */
    public void setEntrySet(org.hupo.psi.mi.mif.Entry[] entrySet) {
        this.entrySet = entrySet;
    }


    /**
     * Gets the mitab value for this ResultSet.
     * 
     * @return mitab
     */
    public java.lang.String getMitab() {
        return mitab;
    }


    /**
     * Sets the mitab value for this ResultSet.
     * 
     * @param mitab
     */
    public void setMitab(java.lang.String mitab) {
        this.mitab = mitab;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ResultSet)) return false;
        ResultSet other = (ResultSet) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.entrySet==null && other.getEntrySet()==null) || 
             (this.entrySet!=null &&
              java.util.Arrays.equals(this.entrySet, other.getEntrySet()))) &&
            ((this.mitab==null && other.getMitab()==null) || 
             (this.mitab!=null &&
              this.mitab.equals(other.getMitab())));
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
        if (getEntrySet() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEntrySet());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEntrySet(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMitab() != null) {
            _hashCode += getMitab().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResultSet.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "resultSet"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entrySet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "entrySet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "entry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "entry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mitab");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "mitab"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
