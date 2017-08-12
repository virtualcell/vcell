/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * RelationshipType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.webservices.chebi;

public class RelationshipType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected RelationshipType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "is a";
    public static final java.lang.String _value2 = "has part";
    public static final java.lang.String _value3 = "is conjugate base of";
    public static final java.lang.String _value4 = "is conjugate acid of";
    public static final java.lang.String _value5 = "is tautomer of";
    public static final java.lang.String _value6 = "is enantiomer of";
    public static final java.lang.String _value7 = "has functional parent";
    public static final java.lang.String _value8 = "has parent hydride";
    public static final java.lang.String _value9 = "is substituent group from";
    public static final java.lang.String _value10 = "has role";
    public static final RelationshipType value1 = new RelationshipType(_value1);
    public static final RelationshipType value2 = new RelationshipType(_value2);
    public static final RelationshipType value3 = new RelationshipType(_value3);
    public static final RelationshipType value4 = new RelationshipType(_value4);
    public static final RelationshipType value5 = new RelationshipType(_value5);
    public static final RelationshipType value6 = new RelationshipType(_value6);
    public static final RelationshipType value7 = new RelationshipType(_value7);
    public static final RelationshipType value8 = new RelationshipType(_value8);
    public static final RelationshipType value9 = new RelationshipType(_value9);
    public static final RelationshipType value10 = new RelationshipType(_value10);
    public java.lang.String getValue() { return _value_;}
    public static RelationshipType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        RelationshipType enumeration = (RelationshipType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static RelationshipType fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RelationshipType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "RelationshipType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
