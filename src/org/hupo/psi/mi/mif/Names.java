/**
 * Names.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class Names  implements java.io.Serializable {
    private java.lang.String shortLabel;

    private java.lang.String fullName;

    private org.hupo.psi.mi.mif.Alias[] alias;

    public Names() {
    }

    public Names(
           java.lang.String shortLabel,
           java.lang.String fullName,
           org.hupo.psi.mi.mif.Alias[] alias) {
           this.shortLabel = shortLabel;
           this.fullName = fullName;
           this.alias = alias;
    }


    /**
     * Gets the shortLabel value for this Names.
     * 
     * @return shortLabel
     */
    public java.lang.String getShortLabel() {
        return shortLabel;
    }


    /**
     * Sets the shortLabel value for this Names.
     * 
     * @param shortLabel
     */
    public void setShortLabel(java.lang.String shortLabel) {
        this.shortLabel = shortLabel;
    }


    /**
     * Gets the fullName value for this Names.
     * 
     * @return fullName
     */
    public java.lang.String getFullName() {
        return fullName;
    }


    /**
     * Sets the fullName value for this Names.
     * 
     * @param fullName
     */
    public void setFullName(java.lang.String fullName) {
        this.fullName = fullName;
    }


    /**
     * Gets the alias value for this Names.
     * 
     * @return alias
     */
    public org.hupo.psi.mi.mif.Alias[] getAlias() {
        return alias;
    }


    /**
     * Sets the alias value for this Names.
     * 
     * @param alias
     */
    public void setAlias(org.hupo.psi.mi.mif.Alias[] alias) {
        this.alias = alias;
    }

    public org.hupo.psi.mi.mif.Alias getAlias(int i) {
        return this.alias[i];
    }

    public void setAlias(int i, org.hupo.psi.mi.mif.Alias _value) {
        this.alias[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Names)) return false;
        Names other = (Names) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.shortLabel==null && other.getShortLabel()==null) || 
             (this.shortLabel!=null &&
              this.shortLabel.equals(other.getShortLabel()))) &&
            ((this.fullName==null && other.getFullName()==null) || 
             (this.fullName!=null &&
              this.fullName.equals(other.getFullName()))) &&
            ((this.alias==null && other.getAlias()==null) || 
             (this.alias!=null &&
              java.util.Arrays.equals(this.alias, other.getAlias())));
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
        if (getShortLabel() != null) {
            _hashCode += getShortLabel().hashCode();
        }
        if (getFullName() != null) {
            _hashCode += getFullName().hashCode();
        }
        if (getAlias() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAlias());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAlias(), i);
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
        new org.apache.axis.description.TypeDesc(Names.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "names"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortLabel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "shortLabel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "fullName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alias");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "alias"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "alias"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
