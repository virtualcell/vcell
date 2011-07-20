/**
 * RequestInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.psicquic;

public class RequestInfo  implements java.io.Serializable {
    private java.lang.String resultType;

    private int firstResult;

    private int blockSize;

    public RequestInfo() {
    }

    public RequestInfo(
           java.lang.String resultType,
           int firstResult,
           int blockSize) {
           this.resultType = resultType;
           this.firstResult = firstResult;
           this.blockSize = blockSize;
    }


    /**
     * Gets the resultType value for this RequestInfo.
     * 
     * @return resultType
     */
    public java.lang.String getResultType() {
        return resultType;
    }


    /**
     * Sets the resultType value for this RequestInfo.
     * 
     * @param resultType
     */
    public void setResultType(java.lang.String resultType) {
        this.resultType = resultType;
    }


    /**
     * Gets the firstResult value for this RequestInfo.
     * 
     * @return firstResult
     */
    public int getFirstResult() {
        return firstResult;
    }


    /**
     * Sets the firstResult value for this RequestInfo.
     * 
     * @param firstResult
     */
    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }


    /**
     * Gets the blockSize value for this RequestInfo.
     * 
     * @return blockSize
     */
    public int getBlockSize() {
        return blockSize;
    }


    /**
     * Sets the blockSize value for this RequestInfo.
     * 
     * @param blockSize
     */
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestInfo)) return false;
        RequestInfo other = (RequestInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.resultType==null && other.getResultType()==null) || 
             (this.resultType!=null &&
              this.resultType.equals(other.getResultType()))) &&
            this.firstResult == other.getFirstResult() &&
            this.blockSize == other.getBlockSize();
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
        if (getResultType() != null) {
            _hashCode += getResultType().hashCode();
        }
        _hashCode += getFirstResult();
        _hashCode += getBlockSize();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "requestInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "resultType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "firstResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blockSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "blockSize"));
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
