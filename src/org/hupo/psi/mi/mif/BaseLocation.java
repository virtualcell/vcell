/**
 * BaseLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class BaseLocation  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.CvType startStatus;

    private org.hupo.psi.mi.mif.Interval beginInterval;

    private org.hupo.psi.mi.mif.Position begin;

    private org.hupo.psi.mi.mif.CvType endStatus;

    private org.hupo.psi.mi.mif.Interval endInterval;

    private org.hupo.psi.mi.mif.Position end;

    private java.lang.Boolean isLink;

    public BaseLocation() {
    }

    public BaseLocation(
           org.hupo.psi.mi.mif.CvType startStatus,
           org.hupo.psi.mi.mif.Interval beginInterval,
           org.hupo.psi.mi.mif.Position begin,
           org.hupo.psi.mi.mif.CvType endStatus,
           org.hupo.psi.mi.mif.Interval endInterval,
           org.hupo.psi.mi.mif.Position end,
           java.lang.Boolean isLink) {
           this.startStatus = startStatus;
           this.beginInterval = beginInterval;
           this.begin = begin;
           this.endStatus = endStatus;
           this.endInterval = endInterval;
           this.end = end;
           this.isLink = isLink;
    }


    /**
     * Gets the startStatus value for this BaseLocation.
     * 
     * @return startStatus
     */
    public org.hupo.psi.mi.mif.CvType getStartStatus() {
        return startStatus;
    }


    /**
     * Sets the startStatus value for this BaseLocation.
     * 
     * @param startStatus
     */
    public void setStartStatus(org.hupo.psi.mi.mif.CvType startStatus) {
        this.startStatus = startStatus;
    }


    /**
     * Gets the beginInterval value for this BaseLocation.
     * 
     * @return beginInterval
     */
    public org.hupo.psi.mi.mif.Interval getBeginInterval() {
        return beginInterval;
    }


    /**
     * Sets the beginInterval value for this BaseLocation.
     * 
     * @param beginInterval
     */
    public void setBeginInterval(org.hupo.psi.mi.mif.Interval beginInterval) {
        this.beginInterval = beginInterval;
    }


    /**
     * Gets the begin value for this BaseLocation.
     * 
     * @return begin
     */
    public org.hupo.psi.mi.mif.Position getBegin() {
        return begin;
    }


    /**
     * Sets the begin value for this BaseLocation.
     * 
     * @param begin
     */
    public void setBegin(org.hupo.psi.mi.mif.Position begin) {
        this.begin = begin;
    }


    /**
     * Gets the endStatus value for this BaseLocation.
     * 
     * @return endStatus
     */
    public org.hupo.psi.mi.mif.CvType getEndStatus() {
        return endStatus;
    }


    /**
     * Sets the endStatus value for this BaseLocation.
     * 
     * @param endStatus
     */
    public void setEndStatus(org.hupo.psi.mi.mif.CvType endStatus) {
        this.endStatus = endStatus;
    }


    /**
     * Gets the endInterval value for this BaseLocation.
     * 
     * @return endInterval
     */
    public org.hupo.psi.mi.mif.Interval getEndInterval() {
        return endInterval;
    }


    /**
     * Sets the endInterval value for this BaseLocation.
     * 
     * @param endInterval
     */
    public void setEndInterval(org.hupo.psi.mi.mif.Interval endInterval) {
        this.endInterval = endInterval;
    }


    /**
     * Gets the end value for this BaseLocation.
     * 
     * @return end
     */
    public org.hupo.psi.mi.mif.Position getEnd() {
        return end;
    }


    /**
     * Sets the end value for this BaseLocation.
     * 
     * @param end
     */
    public void setEnd(org.hupo.psi.mi.mif.Position end) {
        this.end = end;
    }


    /**
     * Gets the isLink value for this BaseLocation.
     * 
     * @return isLink
     */
    public java.lang.Boolean getIsLink() {
        return isLink;
    }


    /**
     * Sets the isLink value for this BaseLocation.
     * 
     * @param isLink
     */
    public void setIsLink(java.lang.Boolean isLink) {
        this.isLink = isLink;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BaseLocation)) return false;
        BaseLocation other = (BaseLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.startStatus==null && other.getStartStatus()==null) || 
             (this.startStatus!=null &&
              this.startStatus.equals(other.getStartStatus()))) &&
            ((this.beginInterval==null && other.getBeginInterval()==null) || 
             (this.beginInterval!=null &&
              this.beginInterval.equals(other.getBeginInterval()))) &&
            ((this.begin==null && other.getBegin()==null) || 
             (this.begin!=null &&
              this.begin.equals(other.getBegin()))) &&
            ((this.endStatus==null && other.getEndStatus()==null) || 
             (this.endStatus!=null &&
              this.endStatus.equals(other.getEndStatus()))) &&
            ((this.endInterval==null && other.getEndInterval()==null) || 
             (this.endInterval!=null &&
              this.endInterval.equals(other.getEndInterval()))) &&
            ((this.end==null && other.getEnd()==null) || 
             (this.end!=null &&
              this.end.equals(other.getEnd()))) &&
            ((this.isLink==null && other.getIsLink()==null) || 
             (this.isLink!=null &&
              this.isLink.equals(other.getIsLink())));
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
        if (getStartStatus() != null) {
            _hashCode += getStartStatus().hashCode();
        }
        if (getBeginInterval() != null) {
            _hashCode += getBeginInterval().hashCode();
        }
        if (getBegin() != null) {
            _hashCode += getBegin().hashCode();
        }
        if (getEndStatus() != null) {
            _hashCode += getEndStatus().hashCode();
        }
        if (getEndInterval() != null) {
            _hashCode += getEndInterval().hashCode();
        }
        if (getEnd() != null) {
            _hashCode += getEnd().hashCode();
        }
        if (getIsLink() != null) {
            _hashCode += getIsLink().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BaseLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "baseLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "startStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("beginInterval");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "beginInterval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interval"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("begin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "begin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "position"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "endStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endInterval");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "endInterval"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interval"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("end");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "end"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "position"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isLink");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "isLink"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
