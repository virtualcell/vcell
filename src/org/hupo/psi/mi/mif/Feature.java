/**
 * Feature.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class Feature  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.Names names;

    private org.hupo.psi.mi.mif.Xref xref;

    private org.hupo.psi.mi.mif.CvType featureType;

    private org.hupo.psi.mi.mif.CvType featureDetectionMethod;

    private int[] experimentRefList;

    private org.hupo.psi.mi.mif.BaseLocation[] featureRangeList;

    private org.hupo.psi.mi.mif.Attribute[] attributeList;

    private int id;  // attribute

    public Feature() {
    }

    public Feature(
           org.hupo.psi.mi.mif.Names names,
           org.hupo.psi.mi.mif.Xref xref,
           org.hupo.psi.mi.mif.CvType featureType,
           org.hupo.psi.mi.mif.CvType featureDetectionMethod,
           int[] experimentRefList,
           org.hupo.psi.mi.mif.BaseLocation[] featureRangeList,
           org.hupo.psi.mi.mif.Attribute[] attributeList,
           int id) {
           this.names = names;
           this.xref = xref;
           this.featureType = featureType;
           this.featureDetectionMethod = featureDetectionMethod;
           this.experimentRefList = experimentRefList;
           this.featureRangeList = featureRangeList;
           this.attributeList = attributeList;
           this.id = id;
    }


    /**
     * Gets the names value for this Feature.
     * 
     * @return names
     */
    public org.hupo.psi.mi.mif.Names getNames() {
        return names;
    }


    /**
     * Sets the names value for this Feature.
     * 
     * @param names
     */
    public void setNames(org.hupo.psi.mi.mif.Names names) {
        this.names = names;
    }


    /**
     * Gets the xref value for this Feature.
     * 
     * @return xref
     */
    public org.hupo.psi.mi.mif.Xref getXref() {
        return xref;
    }


    /**
     * Sets the xref value for this Feature.
     * 
     * @param xref
     */
    public void setXref(org.hupo.psi.mi.mif.Xref xref) {
        this.xref = xref;
    }


    /**
     * Gets the featureType value for this Feature.
     * 
     * @return featureType
     */
    public org.hupo.psi.mi.mif.CvType getFeatureType() {
        return featureType;
    }


    /**
     * Sets the featureType value for this Feature.
     * 
     * @param featureType
     */
    public void setFeatureType(org.hupo.psi.mi.mif.CvType featureType) {
        this.featureType = featureType;
    }


    /**
     * Gets the featureDetectionMethod value for this Feature.
     * 
     * @return featureDetectionMethod
     */
    public org.hupo.psi.mi.mif.CvType getFeatureDetectionMethod() {
        return featureDetectionMethod;
    }


    /**
     * Sets the featureDetectionMethod value for this Feature.
     * 
     * @param featureDetectionMethod
     */
    public void setFeatureDetectionMethod(org.hupo.psi.mi.mif.CvType featureDetectionMethod) {
        this.featureDetectionMethod = featureDetectionMethod;
    }


    /**
     * Gets the experimentRefList value for this Feature.
     * 
     * @return experimentRefList
     */
    public int[] getExperimentRefList() {
        return experimentRefList;
    }


    /**
     * Sets the experimentRefList value for this Feature.
     * 
     * @param experimentRefList
     */
    public void setExperimentRefList(int[] experimentRefList) {
        this.experimentRefList = experimentRefList;
    }


    /**
     * Gets the featureRangeList value for this Feature.
     * 
     * @return featureRangeList
     */
    public org.hupo.psi.mi.mif.BaseLocation[] getFeatureRangeList() {
        return featureRangeList;
    }


    /**
     * Sets the featureRangeList value for this Feature.
     * 
     * @param featureRangeList
     */
    public void setFeatureRangeList(org.hupo.psi.mi.mif.BaseLocation[] featureRangeList) {
        this.featureRangeList = featureRangeList;
    }


    /**
     * Gets the attributeList value for this Feature.
     * 
     * @return attributeList
     */
    public org.hupo.psi.mi.mif.Attribute[] getAttributeList() {
        return attributeList;
    }


    /**
     * Sets the attributeList value for this Feature.
     * 
     * @param attributeList
     */
    public void setAttributeList(org.hupo.psi.mi.mif.Attribute[] attributeList) {
        this.attributeList = attributeList;
    }


    /**
     * Gets the id value for this Feature.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this Feature.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Feature)) return false;
        Feature other = (Feature) obj;
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
            ((this.xref==null && other.getXref()==null) || 
             (this.xref!=null &&
              this.xref.equals(other.getXref()))) &&
            ((this.featureType==null && other.getFeatureType()==null) || 
             (this.featureType!=null &&
              this.featureType.equals(other.getFeatureType()))) &&
            ((this.featureDetectionMethod==null && other.getFeatureDetectionMethod()==null) || 
             (this.featureDetectionMethod!=null &&
              this.featureDetectionMethod.equals(other.getFeatureDetectionMethod()))) &&
            ((this.experimentRefList==null && other.getExperimentRefList()==null) || 
             (this.experimentRefList!=null &&
              java.util.Arrays.equals(this.experimentRefList, other.getExperimentRefList()))) &&
            ((this.featureRangeList==null && other.getFeatureRangeList()==null) || 
             (this.featureRangeList!=null &&
              java.util.Arrays.equals(this.featureRangeList, other.getFeatureRangeList()))) &&
            ((this.attributeList==null && other.getAttributeList()==null) || 
             (this.attributeList!=null &&
              java.util.Arrays.equals(this.attributeList, other.getAttributeList()))) &&
            this.id == other.getId();
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
        if (getXref() != null) {
            _hashCode += getXref().hashCode();
        }
        if (getFeatureType() != null) {
            _hashCode += getFeatureType().hashCode();
        }
        if (getFeatureDetectionMethod() != null) {
            _hashCode += getFeatureDetectionMethod().hashCode();
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
        if (getFeatureRangeList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFeatureRangeList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFeatureRangeList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAttributeList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAttributeList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttributeList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getId();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Feature.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "feature"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("id");
        attrField.setXmlName(new javax.xml.namespace.QName("", "id"));
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
        elemField.setFieldName("xref");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "xref"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "xref"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("featureType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "featureType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("featureDetectionMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "featureDetectionMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("featureRangeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "featureRangeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "baseLocation"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "featureRange"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attributeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attribute"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attribute"));
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
