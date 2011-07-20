/**
 * Xref.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class Xref  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.DbReference primaryRef;

    private org.hupo.psi.mi.mif.DbReference[] secondaryRef;

    public Xref() {
    }

    public Xref(
           org.hupo.psi.mi.mif.DbReference primaryRef,
           org.hupo.psi.mi.mif.DbReference[] secondaryRef) {
           this.primaryRef = primaryRef;
           this.secondaryRef = secondaryRef;
    }


    /**
     * Gets the primaryRef value for this Xref.
     * 
     * @return primaryRef
     */
    public org.hupo.psi.mi.mif.DbReference getPrimaryRef() {
        return primaryRef;
    }


    /**
     * Sets the primaryRef value for this Xref.
     * 
     * @param primaryRef
     */
    public void setPrimaryRef(org.hupo.psi.mi.mif.DbReference primaryRef) {
        this.primaryRef = primaryRef;
    }


    /**
     * Gets the secondaryRef value for this Xref.
     * 
     * @return secondaryRef
     */
    public org.hupo.psi.mi.mif.DbReference[] getSecondaryRef() {
        return secondaryRef;
    }


    /**
     * Sets the secondaryRef value for this Xref.
     * 
     * @param secondaryRef
     */
    public void setSecondaryRef(org.hupo.psi.mi.mif.DbReference[] secondaryRef) {
        this.secondaryRef = secondaryRef;
    }

    public org.hupo.psi.mi.mif.DbReference getSecondaryRef(int i) {
        return this.secondaryRef[i];
    }

    public void setSecondaryRef(int i, org.hupo.psi.mi.mif.DbReference _value) {
        this.secondaryRef[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Xref)) return false;
        Xref other = (Xref) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.primaryRef==null && other.getPrimaryRef()==null) || 
             (this.primaryRef!=null &&
              this.primaryRef.equals(other.getPrimaryRef()))) &&
            ((this.secondaryRef==null && other.getSecondaryRef()==null) || 
             (this.secondaryRef!=null &&
              java.util.Arrays.equals(this.secondaryRef, other.getSecondaryRef())));
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
        if (getPrimaryRef() != null) {
            _hashCode += getPrimaryRef().hashCode();
        }
        if (getSecondaryRef() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSecondaryRef());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSecondaryRef(), i);
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
        new org.apache.axis.description.TypeDesc(Xref.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "xref"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "primaryRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "dbReference"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secondaryRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "secondaryRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "dbReference"));
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
