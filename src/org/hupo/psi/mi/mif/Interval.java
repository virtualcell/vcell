/**
 * Interval.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class Interval  implements java.io.Serializable {
    private org.apache.axis.types.UnsignedLong begin;  // attribute

    private org.apache.axis.types.UnsignedLong end;  // attribute

    public Interval() {
    }

    public Interval(
           org.apache.axis.types.UnsignedLong begin,
           org.apache.axis.types.UnsignedLong end) {
           this.begin = begin;
           this.end = end;
    }


    /**
     * Gets the begin value for this Interval.
     * 
     * @return begin
     */
    public org.apache.axis.types.UnsignedLong getBegin() {
        return begin;
    }


    /**
     * Sets the begin value for this Interval.
     * 
     * @param begin
     */
    public void setBegin(org.apache.axis.types.UnsignedLong begin) {
        this.begin = begin;
    }


    /**
     * Gets the end value for this Interval.
     * 
     * @return end
     */
    public org.apache.axis.types.UnsignedLong getEnd() {
        return end;
    }


    /**
     * Sets the end value for this Interval.
     * 
     * @param end
     */
    public void setEnd(org.apache.axis.types.UnsignedLong end) {
        this.end = end;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Interval)) return false;
        Interval other = (Interval) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.begin==null && other.getBegin()==null) || 
             (this.begin!=null &&
              this.begin.equals(other.getBegin()))) &&
            ((this.end==null && other.getEnd()==null) || 
             (this.end!=null &&
              this.end.equals(other.getEnd())));
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
        if (getBegin() != null) {
            _hashCode += getBegin().hashCode();
        }
        if (getEnd() != null) {
            _hashCode += getEnd().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Interval.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interval"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("begin");
        attrField.setXmlName(new javax.xml.namespace.QName("", "begin"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedLong"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("end");
        attrField.setXmlName(new javax.xml.namespace.QName("", "end"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedLong"));
        typeDesc.addFieldDesc(attrField);
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
