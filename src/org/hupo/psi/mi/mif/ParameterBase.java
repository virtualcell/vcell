/**
 * ParameterBase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class ParameterBase  implements java.io.Serializable {
    private java.lang.String term;  // attribute

    private java.lang.String termAc;  // attribute

    private java.lang.String unit;  // attribute

    private java.lang.String unitAc;  // attribute

    private short base;  // attribute

    private short exponent;  // attribute

    private java.math.BigDecimal factor;  // attribute

    public ParameterBase() {
    }

    public ParameterBase(
           java.lang.String term,
           java.lang.String termAc,
           java.lang.String unit,
           java.lang.String unitAc,
           short base,
           short exponent,
           java.math.BigDecimal factor) {
           this.term = term;
           this.termAc = termAc;
           this.unit = unit;
           this.unitAc = unitAc;
           this.base = base;
           this.exponent = exponent;
           this.factor = factor;
    }


    /**
     * Gets the term value for this ParameterBase.
     * 
     * @return term
     */
    public java.lang.String getTerm() {
        return term;
    }


    /**
     * Sets the term value for this ParameterBase.
     * 
     * @param term
     */
    public void setTerm(java.lang.String term) {
        this.term = term;
    }


    /**
     * Gets the termAc value for this ParameterBase.
     * 
     * @return termAc
     */
    public java.lang.String getTermAc() {
        return termAc;
    }


    /**
     * Sets the termAc value for this ParameterBase.
     * 
     * @param termAc
     */
    public void setTermAc(java.lang.String termAc) {
        this.termAc = termAc;
    }


    /**
     * Gets the unit value for this ParameterBase.
     * 
     * @return unit
     */
    public java.lang.String getUnit() {
        return unit;
    }


    /**
     * Sets the unit value for this ParameterBase.
     * 
     * @param unit
     */
    public void setUnit(java.lang.String unit) {
        this.unit = unit;
    }


    /**
     * Gets the unitAc value for this ParameterBase.
     * 
     * @return unitAc
     */
    public java.lang.String getUnitAc() {
        return unitAc;
    }


    /**
     * Sets the unitAc value for this ParameterBase.
     * 
     * @param unitAc
     */
    public void setUnitAc(java.lang.String unitAc) {
        this.unitAc = unitAc;
    }


    /**
     * Gets the base value for this ParameterBase.
     * 
     * @return base
     */
    public short getBase() {
        return base;
    }


    /**
     * Sets the base value for this ParameterBase.
     * 
     * @param base
     */
    public void setBase(short base) {
        this.base = base;
    }


    /**
     * Gets the exponent value for this ParameterBase.
     * 
     * @return exponent
     */
    public short getExponent() {
        return exponent;
    }


    /**
     * Sets the exponent value for this ParameterBase.
     * 
     * @param exponent
     */
    public void setExponent(short exponent) {
        this.exponent = exponent;
    }


    /**
     * Gets the factor value for this ParameterBase.
     * 
     * @return factor
     */
    public java.math.BigDecimal getFactor() {
        return factor;
    }


    /**
     * Sets the factor value for this ParameterBase.
     * 
     * @param factor
     */
    public void setFactor(java.math.BigDecimal factor) {
        this.factor = factor;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ParameterBase)) return false;
        ParameterBase other = (ParameterBase) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.term==null && other.getTerm()==null) || 
             (this.term!=null &&
              this.term.equals(other.getTerm()))) &&
            ((this.termAc==null && other.getTermAc()==null) || 
             (this.termAc!=null &&
              this.termAc.equals(other.getTermAc()))) &&
            ((this.unit==null && other.getUnit()==null) || 
             (this.unit!=null &&
              this.unit.equals(other.getUnit()))) &&
            ((this.unitAc==null && other.getUnitAc()==null) || 
             (this.unitAc!=null &&
              this.unitAc.equals(other.getUnitAc()))) &&
            this.base == other.getBase() &&
            this.exponent == other.getExponent() &&
            ((this.factor==null && other.getFactor()==null) || 
             (this.factor!=null &&
              this.factor.equals(other.getFactor())));
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
        if (getTerm() != null) {
            _hashCode += getTerm().hashCode();
        }
        if (getTermAc() != null) {
            _hashCode += getTermAc().hashCode();
        }
        if (getUnit() != null) {
            _hashCode += getUnit().hashCode();
        }
        if (getUnitAc() != null) {
            _hashCode += getUnitAc().hashCode();
        }
        _hashCode += getBase();
        _hashCode += getExponent();
        if (getFactor() != null) {
            _hashCode += getFactor().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParameterBase.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameterBase"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("term");
        attrField.setXmlName(new javax.xml.namespace.QName("", "term"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("termAc");
        attrField.setXmlName(new javax.xml.namespace.QName("", "termAc"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("unit");
        attrField.setXmlName(new javax.xml.namespace.QName("", "unit"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("unitAc");
        attrField.setXmlName(new javax.xml.namespace.QName("", "unitAc"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("base");
        attrField.setXmlName(new javax.xml.namespace.QName("", "base"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("exponent");
        attrField.setXmlName(new javax.xml.namespace.QName("", "exponent"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("factor");
        attrField.setXmlName(new javax.xml.namespace.QName("", "factor"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
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
