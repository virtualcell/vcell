/**
 * DbReference.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class DbReference  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.Attribute[] attributeList;

    private java.lang.String db;  // attribute

    private java.lang.String dbAc;  // attribute

    private java.lang.String id;  // attribute

    private java.lang.String secondary;  // attribute

    private java.lang.String version;  // attribute

    private java.lang.String refType;  // attribute

    private java.lang.String refTypeAc;  // attribute

    public DbReference() {
    }

    public DbReference(
           org.hupo.psi.mi.mif.Attribute[] attributeList,
           java.lang.String db,
           java.lang.String dbAc,
           java.lang.String id,
           java.lang.String secondary,
           java.lang.String version,
           java.lang.String refType,
           java.lang.String refTypeAc) {
           this.attributeList = attributeList;
           this.db = db;
           this.dbAc = dbAc;
           this.id = id;
           this.secondary = secondary;
           this.version = version;
           this.refType = refType;
           this.refTypeAc = refTypeAc;
    }


    /**
     * Gets the attributeList value for this DbReference.
     * 
     * @return attributeList
     */
    public org.hupo.psi.mi.mif.Attribute[] getAttributeList() {
        return attributeList;
    }


    /**
     * Sets the attributeList value for this DbReference.
     * 
     * @param attributeList
     */
    public void setAttributeList(org.hupo.psi.mi.mif.Attribute[] attributeList) {
        this.attributeList = attributeList;
    }


    /**
     * Gets the db value for this DbReference.
     * 
     * @return db
     */
    public java.lang.String getDb() {
        return db;
    }


    /**
     * Sets the db value for this DbReference.
     * 
     * @param db
     */
    public void setDb(java.lang.String db) {
        this.db = db;
    }


    /**
     * Gets the dbAc value for this DbReference.
     * 
     * @return dbAc
     */
    public java.lang.String getDbAc() {
        return dbAc;
    }


    /**
     * Sets the dbAc value for this DbReference.
     * 
     * @param dbAc
     */
    public void setDbAc(java.lang.String dbAc) {
        this.dbAc = dbAc;
    }


    /**
     * Gets the id value for this DbReference.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this DbReference.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the secondary value for this DbReference.
     * 
     * @return secondary
     */
    public java.lang.String getSecondary() {
        return secondary;
    }


    /**
     * Sets the secondary value for this DbReference.
     * 
     * @param secondary
     */
    public void setSecondary(java.lang.String secondary) {
        this.secondary = secondary;
    }


    /**
     * Gets the version value for this DbReference.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this DbReference.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }


    /**
     * Gets the refType value for this DbReference.
     * 
     * @return refType
     */
    public java.lang.String getRefType() {
        return refType;
    }


    /**
     * Sets the refType value for this DbReference.
     * 
     * @param refType
     */
    public void setRefType(java.lang.String refType) {
        this.refType = refType;
    }


    /**
     * Gets the refTypeAc value for this DbReference.
     * 
     * @return refTypeAc
     */
    public java.lang.String getRefTypeAc() {
        return refTypeAc;
    }


    /**
     * Sets the refTypeAc value for this DbReference.
     * 
     * @param refTypeAc
     */
    public void setRefTypeAc(java.lang.String refTypeAc) {
        this.refTypeAc = refTypeAc;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DbReference)) return false;
        DbReference other = (DbReference) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.attributeList==null && other.getAttributeList()==null) || 
             (this.attributeList!=null &&
              java.util.Arrays.equals(this.attributeList, other.getAttributeList()))) &&
            ((this.db==null && other.getDb()==null) || 
             (this.db!=null &&
              this.db.equals(other.getDb()))) &&
            ((this.dbAc==null && other.getDbAc()==null) || 
             (this.dbAc!=null &&
              this.dbAc.equals(other.getDbAc()))) &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.secondary==null && other.getSecondary()==null) || 
             (this.secondary!=null &&
              this.secondary.equals(other.getSecondary()))) &&
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion()))) &&
            ((this.refType==null && other.getRefType()==null) || 
             (this.refType!=null &&
              this.refType.equals(other.getRefType()))) &&
            ((this.refTypeAc==null && other.getRefTypeAc()==null) || 
             (this.refTypeAc!=null &&
              this.refTypeAc.equals(other.getRefTypeAc())));
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
        if (getDb() != null) {
            _hashCode += getDb().hashCode();
        }
        if (getDbAc() != null) {
            _hashCode += getDbAc().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getSecondary() != null) {
            _hashCode += getSecondary().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        if (getRefType() != null) {
            _hashCode += getRefType().hashCode();
        }
        if (getRefTypeAc() != null) {
            _hashCode += getRefTypeAc().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DbReference.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "dbReference"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("db");
        attrField.setXmlName(new javax.xml.namespace.QName("", "db"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("dbAc");
        attrField.setXmlName(new javax.xml.namespace.QName("", "dbAc"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("id");
        attrField.setXmlName(new javax.xml.namespace.QName("", "id"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("secondary");
        attrField.setXmlName(new javax.xml.namespace.QName("", "secondary"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("version");
        attrField.setXmlName(new javax.xml.namespace.QName("", "version"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("refType");
        attrField.setXmlName(new javax.xml.namespace.QName("", "refType"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("refTypeAc");
        attrField.setXmlName(new javax.xml.namespace.QName("", "refTypeAc"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
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
