/**
 * Entry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class Entry  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.Source source;

    private org.hupo.psi.mi.mif.Availability[] availabilityList;

    private org.hupo.psi.mi.mif.ExperimentDescription[] experimentList;

    private org.hupo.psi.mi.mif.Interactor[] interactorList;

    private org.hupo.psi.mi.mif.Interaction[] interactionList;

    private org.hupo.psi.mi.mif.Attribute[] attributeList;

    public Entry() {
    }

    public Entry(
           org.hupo.psi.mi.mif.Source source,
           org.hupo.psi.mi.mif.Availability[] availabilityList,
           org.hupo.psi.mi.mif.ExperimentDescription[] experimentList,
           org.hupo.psi.mi.mif.Interactor[] interactorList,
           org.hupo.psi.mi.mif.Interaction[] interactionList,
           org.hupo.psi.mi.mif.Attribute[] attributeList) {
           this.source = source;
           this.availabilityList = availabilityList;
           this.experimentList = experimentList;
           this.interactorList = interactorList;
           this.interactionList = interactionList;
           this.attributeList = attributeList;
    }


    /**
     * Gets the source value for this Entry.
     * 
     * @return source
     */
    public org.hupo.psi.mi.mif.Source getSource() {
        return source;
    }


    /**
     * Sets the source value for this Entry.
     * 
     * @param source
     */
    public void setSource(org.hupo.psi.mi.mif.Source source) {
        this.source = source;
    }


    /**
     * Gets the availabilityList value for this Entry.
     * 
     * @return availabilityList
     */
    public org.hupo.psi.mi.mif.Availability[] getAvailabilityList() {
        return availabilityList;
    }


    /**
     * Sets the availabilityList value for this Entry.
     * 
     * @param availabilityList
     */
    public void setAvailabilityList(org.hupo.psi.mi.mif.Availability[] availabilityList) {
        this.availabilityList = availabilityList;
    }


    /**
     * Gets the experimentList value for this Entry.
     * 
     * @return experimentList
     */
    public org.hupo.psi.mi.mif.ExperimentDescription[] getExperimentList() {
        return experimentList;
    }


    /**
     * Sets the experimentList value for this Entry.
     * 
     * @param experimentList
     */
    public void setExperimentList(org.hupo.psi.mi.mif.ExperimentDescription[] experimentList) {
        this.experimentList = experimentList;
    }


    /**
     * Gets the interactorList value for this Entry.
     * 
     * @return interactorList
     */
    public org.hupo.psi.mi.mif.Interactor[] getInteractorList() {
        return interactorList;
    }


    /**
     * Sets the interactorList value for this Entry.
     * 
     * @param interactorList
     */
    public void setInteractorList(org.hupo.psi.mi.mif.Interactor[] interactorList) {
        this.interactorList = interactorList;
    }


    /**
     * Gets the interactionList value for this Entry.
     * 
     * @return interactionList
     */
    public org.hupo.psi.mi.mif.Interaction[] getInteractionList() {
        return interactionList;
    }


    /**
     * Sets the interactionList value for this Entry.
     * 
     * @param interactionList
     */
    public void setInteractionList(org.hupo.psi.mi.mif.Interaction[] interactionList) {
        this.interactionList = interactionList;
    }


    /**
     * Gets the attributeList value for this Entry.
     * 
     * @return attributeList
     */
    public org.hupo.psi.mi.mif.Attribute[] getAttributeList() {
        return attributeList;
    }


    /**
     * Sets the attributeList value for this Entry.
     * 
     * @param attributeList
     */
    public void setAttributeList(org.hupo.psi.mi.mif.Attribute[] attributeList) {
        this.attributeList = attributeList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Entry)) return false;
        Entry other = (Entry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.availabilityList==null && other.getAvailabilityList()==null) || 
             (this.availabilityList!=null &&
              java.util.Arrays.equals(this.availabilityList, other.getAvailabilityList()))) &&
            ((this.experimentList==null && other.getExperimentList()==null) || 
             (this.experimentList!=null &&
              java.util.Arrays.equals(this.experimentList, other.getExperimentList()))) &&
            ((this.interactorList==null && other.getInteractorList()==null) || 
             (this.interactorList!=null &&
              java.util.Arrays.equals(this.interactorList, other.getInteractorList()))) &&
            ((this.interactionList==null && other.getInteractionList()==null) || 
             (this.interactionList!=null &&
              java.util.Arrays.equals(this.interactionList, other.getInteractionList()))) &&
            ((this.attributeList==null && other.getAttributeList()==null) || 
             (this.attributeList!=null &&
              java.util.Arrays.equals(this.attributeList, other.getAttributeList())));
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
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getAvailabilityList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAvailabilityList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAvailabilityList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExperimentList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExperimentList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExperimentList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getInteractorList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInteractorList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInteractorList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getInteractionList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInteractionList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInteractionList(), i);
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Entry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "entry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "source"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("availabilityList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availabilityList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availability"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availability"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("experimentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentDescription"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentDescription"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interactorList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactorList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactor"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interactionList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactionList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interaction"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interaction"));
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
