/**
 * BioSource.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class BioSource  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.Names names;

    private org.hupo.psi.mi.mif.OpenCvType cellType;

    private org.hupo.psi.mi.mif.OpenCvType compartment;

    private org.hupo.psi.mi.mif.OpenCvType tissue;

    private int ncbiTaxId;  // attribute

    public BioSource() {
    }

    public BioSource(
           org.hupo.psi.mi.mif.Names names,
           org.hupo.psi.mi.mif.OpenCvType cellType,
           org.hupo.psi.mi.mif.OpenCvType compartment,
           org.hupo.psi.mi.mif.OpenCvType tissue,
           int ncbiTaxId) {
           this.names = names;
           this.cellType = cellType;
           this.compartment = compartment;
           this.tissue = tissue;
           this.ncbiTaxId = ncbiTaxId;
    }


    /**
     * Gets the names value for this BioSource.
     * 
     * @return names
     */
    public org.hupo.psi.mi.mif.Names getNames() {
        return names;
    }


    /**
     * Sets the names value for this BioSource.
     * 
     * @param names
     */
    public void setNames(org.hupo.psi.mi.mif.Names names) {
        this.names = names;
    }


    /**
     * Gets the cellType value for this BioSource.
     * 
     * @return cellType
     */
    public org.hupo.psi.mi.mif.OpenCvType getCellType() {
        return cellType;
    }


    /**
     * Sets the cellType value for this BioSource.
     * 
     * @param cellType
     */
    public void setCellType(org.hupo.psi.mi.mif.OpenCvType cellType) {
        this.cellType = cellType;
    }


    /**
     * Gets the compartment value for this BioSource.
     * 
     * @return compartment
     */
    public org.hupo.psi.mi.mif.OpenCvType getCompartment() {
        return compartment;
    }


    /**
     * Sets the compartment value for this BioSource.
     * 
     * @param compartment
     */
    public void setCompartment(org.hupo.psi.mi.mif.OpenCvType compartment) {
        this.compartment = compartment;
    }


    /**
     * Gets the tissue value for this BioSource.
     * 
     * @return tissue
     */
    public org.hupo.psi.mi.mif.OpenCvType getTissue() {
        return tissue;
    }


    /**
     * Sets the tissue value for this BioSource.
     * 
     * @param tissue
     */
    public void setTissue(org.hupo.psi.mi.mif.OpenCvType tissue) {
        this.tissue = tissue;
    }


    /**
     * Gets the ncbiTaxId value for this BioSource.
     * 
     * @return ncbiTaxId
     */
    public int getNcbiTaxId() {
        return ncbiTaxId;
    }


    /**
     * Sets the ncbiTaxId value for this BioSource.
     * 
     * @param ncbiTaxId
     */
    public void setNcbiTaxId(int ncbiTaxId) {
        this.ncbiTaxId = ncbiTaxId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BioSource)) return false;
        BioSource other = (BioSource) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.names==null && other.getNames()==null) || 
             (this.names!=null &&
              this.names.equals(other.getNames()))) &&
            ((this.cellType==null && other.getCellType()==null) || 
             (this.cellType!=null &&
              this.cellType.equals(other.getCellType()))) &&
            ((this.compartment==null && other.getCompartment()==null) || 
             (this.compartment!=null &&
              this.compartment.equals(other.getCompartment()))) &&
            ((this.tissue==null && other.getTissue()==null) || 
             (this.tissue!=null &&
              this.tissue.equals(other.getTissue()))) &&
            this.ncbiTaxId == other.getNcbiTaxId();
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
        if (getNames() != null) {
            _hashCode += getNames().hashCode();
        }
        if (getCellType() != null) {
            _hashCode += getCellType().hashCode();
        }
        if (getCompartment() != null) {
            _hashCode += getCompartment().hashCode();
        }
        if (getTissue() != null) {
            _hashCode += getTissue().hashCode();
        }
        _hashCode += getNcbiTaxId();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BioSource.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "bioSource"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("ncbiTaxId");
        attrField.setXmlName(new javax.xml.namespace.QName("", "ncbiTaxId"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("names");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "names"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "names"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cellType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cellType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "openCvType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("compartment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "compartment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "openCvType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tissue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "tissue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "openCvType"));
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
