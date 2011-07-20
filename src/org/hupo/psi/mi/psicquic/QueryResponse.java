/**
 * QueryResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.psicquic;

public class QueryResponse  implements java.io.Serializable {
    private org.hupo.psi.mi.psicquic.ResultSet resultSet;

    private org.hupo.psi.mi.psicquic.ResultInfo resultInfo;

    public QueryResponse() {
    }

    public QueryResponse(
           org.hupo.psi.mi.psicquic.ResultSet resultSet,
           org.hupo.psi.mi.psicquic.ResultInfo resultInfo) {
           this.resultSet = resultSet;
           this.resultInfo = resultInfo;
    }


    /**
     * Gets the resultSet value for this QueryResponse.
     * 
     * @return resultSet
     */
    public org.hupo.psi.mi.psicquic.ResultSet getResultSet() {
        return resultSet;
    }


    /**
     * Sets the resultSet value for this QueryResponse.
     * 
     * @param resultSet
     */
    public void setResultSet(org.hupo.psi.mi.psicquic.ResultSet resultSet) {
        this.resultSet = resultSet;
    }


    /**
     * Gets the resultInfo value for this QueryResponse.
     * 
     * @return resultInfo
     */
    public org.hupo.psi.mi.psicquic.ResultInfo getResultInfo() {
        return resultInfo;
    }


    /**
     * Sets the resultInfo value for this QueryResponse.
     * 
     * @param resultInfo
     */
    public void setResultInfo(org.hupo.psi.mi.psicquic.ResultInfo resultInfo) {
        this.resultInfo = resultInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryResponse)) return false;
        QueryResponse other = (QueryResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.resultSet==null && other.getResultSet()==null) || 
             (this.resultSet!=null &&
              this.resultSet.equals(other.getResultSet()))) &&
            ((this.resultInfo==null && other.getResultInfo()==null) || 
             (this.resultInfo!=null &&
              this.resultInfo.equals(other.getResultInfo())));
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
        if (getResultSet() != null) {
            _hashCode += getResultSet().hashCode();
        }
        if (getResultInfo() != null) {
            _hashCode += getResultInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(QueryResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultSet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "resultSet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "resultSet"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "resultInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "resultInfo"));
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
