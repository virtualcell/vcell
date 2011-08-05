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
 * SearchCategory.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.webservices.chebi;

public class SearchCategory implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected SearchCategory(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "ALL";
    public static final java.lang.String _value2 = "CHEBI ID";
    public static final java.lang.String _value3 = "CHEBI NAME";
    public static final java.lang.String _value4 = "DEFINITION";
    public static final java.lang.String _value5 = "ALL NAMES";
    public static final java.lang.String _value6 = "IUPAC NAME";
    public static final java.lang.String _value7 = "DATABASE LINK/REGISTRY NUMBER/CITATION";
    public static final java.lang.String _value8 = "FORMULA";
    public static final java.lang.String _value9 = "MASS";
    public static final java.lang.String _value10 = "CHARGE";
    public static final java.lang.String _value11 = "INCHI/INCHI KEY";
    public static final java.lang.String _value12 = "SMILES";
    public static final SearchCategory value1 = new SearchCategory(_value1);
    public static final SearchCategory value2 = new SearchCategory(_value2);
    public static final SearchCategory value3 = new SearchCategory(_value3);
    public static final SearchCategory value4 = new SearchCategory(_value4);
    public static final SearchCategory value5 = new SearchCategory(_value5);
    public static final SearchCategory value6 = new SearchCategory(_value6);
    public static final SearchCategory value7 = new SearchCategory(_value7);
    public static final SearchCategory value8 = new SearchCategory(_value8);
    public static final SearchCategory value9 = new SearchCategory(_value9);
    public static final SearchCategory value10 = new SearchCategory(_value10);
    public static final SearchCategory value11 = new SearchCategory(_value11);
    public static final SearchCategory value12 = new SearchCategory(_value12);
    public java.lang.String getValue() { return _value_;}
    public static SearchCategory fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        SearchCategory enumeration = (SearchCategory)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static SearchCategory fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(SearchCategory.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "SearchCategory"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
