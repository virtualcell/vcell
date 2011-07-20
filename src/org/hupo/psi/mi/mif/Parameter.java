/**
 * Parameter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class Parameter  extends org.hupo.psi.mi.mif.ParameterBase  implements java.io.Serializable {
    private int experimentRef;

    private java.math.BigDecimal uncertainty;  // attribute

    public Parameter() {
    }

    public Parameter(
           java.lang.String term,
           java.lang.String termAc,
           java.lang.String unit,
           java.lang.String unitAc,
           short base,
           short exponent,
           java.math.BigDecimal factor,
           java.math.BigDecimal uncertainty,
           int experimentRef) {
        super(
            term,
            termAc,
            unit,
            unitAc,
            base,
            exponent,
            factor);
        this.uncertainty = uncertainty;
        this.experimentRef = experimentRef;
    }


    /**
     * Gets the experimentRef value for this Parameter.
     * 
     * @return experimentRef
     */
    public int getExperimentRef() {
        return experimentRef;
    }


    /**
     * Sets the experimentRef value for this Parameter.
     * 
     * @param experimentRef
     */
    public void setExperimentRef(int experimentRef) {
        this.experimentRef = experimentRef;
    }


    /**
     * Gets the uncertainty value for this Parameter.
     * 
     * @return uncertainty
     */
    public java.math.BigDecimal getUncertainty() {
        return uncertainty;
    }


    /**
     * Sets the uncertainty value for this Parameter.
     * 
     * @param uncertainty
     */
    public void setUncertainty(java.math.BigDecimal uncertainty) {
        this.uncertainty = uncertainty;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Parameter)) return false;
        Parameter other = (Parameter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.experimentRef == other.getExperimentRef() &&
            ((this.uncertainty==null && other.getUncertainty()==null) || 
             (this.uncertainty!=null &&
              this.uncertainty.equals(other.getUncertainty())));
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
        _hashCode += getExperimentRef();
        if (getUncertainty() != null) {
            _hashCode += getUncertainty().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Parameter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameter"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("uncertainty");
        attrField.setXmlName(new javax.xml.namespace.QName("", "uncertainty"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("experimentRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
