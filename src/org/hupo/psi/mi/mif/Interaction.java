/**
 * Interaction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class Interaction  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.Names names;

    private org.hupo.psi.mi.mif.Xref xref;

    private org.hupo.psi.mi.mif.Availability availability;

    private java.lang.Integer availabilityRef;

    private org.hupo.psi.mi.mif.ExperimentList experimentList;

    private org.hupo.psi.mi.mif.Participant[] participantList;

    private org.hupo.psi.mi.mif.InferredInteraction[] inferredInteractionList;

    private org.hupo.psi.mi.mif.CvType[] interactionType;

    private java.lang.Boolean modelled;

    private java.lang.Boolean intraMolecular;

    private java.lang.Boolean negative;

    private org.hupo.psi.mi.mif.Confidence[] confidenceList;

    private org.hupo.psi.mi.mif.Parameter[] parameterList;

    private org.hupo.psi.mi.mif.Attribute[] attributeList;

    private java.lang.String imexId;  // attribute

    private int id;  // attribute

    public Interaction() {
    }

    public Interaction(
           org.hupo.psi.mi.mif.Names names,
           org.hupo.psi.mi.mif.Xref xref,
           org.hupo.psi.mi.mif.Availability availability,
           java.lang.Integer availabilityRef,
           org.hupo.psi.mi.mif.ExperimentList experimentList,
           org.hupo.psi.mi.mif.Participant[] participantList,
           org.hupo.psi.mi.mif.InferredInteraction[] inferredInteractionList,
           org.hupo.psi.mi.mif.CvType[] interactionType,
           java.lang.Boolean modelled,
           java.lang.Boolean intraMolecular,
           java.lang.Boolean negative,
           org.hupo.psi.mi.mif.Confidence[] confidenceList,
           org.hupo.psi.mi.mif.Parameter[] parameterList,
           org.hupo.psi.mi.mif.Attribute[] attributeList,
           java.lang.String imexId,
           int id) {
           this.names = names;
           this.xref = xref;
           this.availability = availability;
           this.availabilityRef = availabilityRef;
           this.experimentList = experimentList;
           this.participantList = participantList;
           this.inferredInteractionList = inferredInteractionList;
           this.interactionType = interactionType;
           this.modelled = modelled;
           this.intraMolecular = intraMolecular;
           this.negative = negative;
           this.confidenceList = confidenceList;
           this.parameterList = parameterList;
           this.attributeList = attributeList;
           this.imexId = imexId;
           this.id = id;
    }


    /**
     * Gets the names value for this Interaction.
     * 
     * @return names
     */
    public org.hupo.psi.mi.mif.Names getNames() {
        return names;
    }


    /**
     * Sets the names value for this Interaction.
     * 
     * @param names
     */
    public void setNames(org.hupo.psi.mi.mif.Names names) {
        this.names = names;
    }


    /**
     * Gets the xref value for this Interaction.
     * 
     * @return xref
     */
    public org.hupo.psi.mi.mif.Xref getXref() {
        return xref;
    }


    /**
     * Sets the xref value for this Interaction.
     * 
     * @param xref
     */
    public void setXref(org.hupo.psi.mi.mif.Xref xref) {
        this.xref = xref;
    }


    /**
     * Gets the availability value for this Interaction.
     * 
     * @return availability
     */
    public org.hupo.psi.mi.mif.Availability getAvailability() {
        return availability;
    }


    /**
     * Sets the availability value for this Interaction.
     * 
     * @param availability
     */
    public void setAvailability(org.hupo.psi.mi.mif.Availability availability) {
        this.availability = availability;
    }


    /**
     * Gets the availabilityRef value for this Interaction.
     * 
     * @return availabilityRef
     */
    public java.lang.Integer getAvailabilityRef() {
        return availabilityRef;
    }


    /**
     * Sets the availabilityRef value for this Interaction.
     * 
     * @param availabilityRef
     */
    public void setAvailabilityRef(java.lang.Integer availabilityRef) {
        this.availabilityRef = availabilityRef;
    }


    /**
     * Gets the experimentList value for this Interaction.
     * 
     * @return experimentList
     */
    public org.hupo.psi.mi.mif.ExperimentList getExperimentList() {
        return experimentList;
    }


    /**
     * Sets the experimentList value for this Interaction.
     * 
     * @param experimentList
     */
    public void setExperimentList(org.hupo.psi.mi.mif.ExperimentList experimentList) {
        this.experimentList = experimentList;
    }


    /**
     * Gets the participantList value for this Interaction.
     * 
     * @return participantList
     */
    public org.hupo.psi.mi.mif.Participant[] getParticipantList() {
        return participantList;
    }


    /**
     * Sets the participantList value for this Interaction.
     * 
     * @param participantList
     */
    public void setParticipantList(org.hupo.psi.mi.mif.Participant[] participantList) {
        this.participantList = participantList;
    }


    /**
     * Gets the inferredInteractionList value for this Interaction.
     * 
     * @return inferredInteractionList
     */
    public org.hupo.psi.mi.mif.InferredInteraction[] getInferredInteractionList() {
        return inferredInteractionList;
    }


    /**
     * Sets the inferredInteractionList value for this Interaction.
     * 
     * @param inferredInteractionList
     */
    public void setInferredInteractionList(org.hupo.psi.mi.mif.InferredInteraction[] inferredInteractionList) {
        this.inferredInteractionList = inferredInteractionList;
    }


    /**
     * Gets the interactionType value for this Interaction.
     * 
     * @return interactionType
     */
    public org.hupo.psi.mi.mif.CvType[] getInteractionType() {
        return interactionType;
    }


    /**
     * Sets the interactionType value for this Interaction.
     * 
     * @param interactionType
     */
    public void setInteractionType(org.hupo.psi.mi.mif.CvType[] interactionType) {
        this.interactionType = interactionType;
    }

    public org.hupo.psi.mi.mif.CvType getInteractionType(int i) {
        return this.interactionType[i];
    }

    public void setInteractionType(int i, org.hupo.psi.mi.mif.CvType _value) {
        this.interactionType[i] = _value;
    }


    /**
     * Gets the modelled value for this Interaction.
     * 
     * @return modelled
     */
    public java.lang.Boolean getModelled() {
        return modelled;
    }


    /**
     * Sets the modelled value for this Interaction.
     * 
     * @param modelled
     */
    public void setModelled(java.lang.Boolean modelled) {
        this.modelled = modelled;
    }


    /**
     * Gets the intraMolecular value for this Interaction.
     * 
     * @return intraMolecular
     */
    public java.lang.Boolean getIntraMolecular() {
        return intraMolecular;
    }


    /**
     * Sets the intraMolecular value for this Interaction.
     * 
     * @param intraMolecular
     */
    public void setIntraMolecular(java.lang.Boolean intraMolecular) {
        this.intraMolecular = intraMolecular;
    }


    /**
     * Gets the negative value for this Interaction.
     * 
     * @return negative
     */
    public java.lang.Boolean getNegative() {
        return negative;
    }


    /**
     * Sets the negative value for this Interaction.
     * 
     * @param negative
     */
    public void setNegative(java.lang.Boolean negative) {
        this.negative = negative;
    }


    /**
     * Gets the confidenceList value for this Interaction.
     * 
     * @return confidenceList
     */
    public org.hupo.psi.mi.mif.Confidence[] getConfidenceList() {
        return confidenceList;
    }


    /**
     * Sets the confidenceList value for this Interaction.
     * 
     * @param confidenceList
     */
    public void setConfidenceList(org.hupo.psi.mi.mif.Confidence[] confidenceList) {
        this.confidenceList = confidenceList;
    }


    /**
     * Gets the parameterList value for this Interaction.
     * 
     * @return parameterList
     */
    public org.hupo.psi.mi.mif.Parameter[] getParameterList() {
        return parameterList;
    }


    /**
     * Sets the parameterList value for this Interaction.
     * 
     * @param parameterList
     */
    public void setParameterList(org.hupo.psi.mi.mif.Parameter[] parameterList) {
        this.parameterList = parameterList;
    }


    /**
     * Gets the attributeList value for this Interaction.
     * 
     * @return attributeList
     */
    public org.hupo.psi.mi.mif.Attribute[] getAttributeList() {
        return attributeList;
    }


    /**
     * Sets the attributeList value for this Interaction.
     * 
     * @param attributeList
     */
    public void setAttributeList(org.hupo.psi.mi.mif.Attribute[] attributeList) {
        this.attributeList = attributeList;
    }


    /**
     * Gets the imexId value for this Interaction.
     * 
     * @return imexId
     */
    public java.lang.String getImexId() {
        return imexId;
    }


    /**
     * Sets the imexId value for this Interaction.
     * 
     * @param imexId
     */
    public void setImexId(java.lang.String imexId) {
        this.imexId = imexId;
    }


    /**
     * Gets the id value for this Interaction.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this Interaction.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Interaction)) return false;
        Interaction other = (Interaction) obj;
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
            ((this.availability==null && other.getAvailability()==null) || 
             (this.availability!=null &&
              this.availability.equals(other.getAvailability()))) &&
            ((this.availabilityRef==null && other.getAvailabilityRef()==null) || 
             (this.availabilityRef!=null &&
              this.availabilityRef.equals(other.getAvailabilityRef()))) &&
            ((this.experimentList==null && other.getExperimentList()==null) || 
             (this.experimentList!=null &&
              this.experimentList.equals(other.getExperimentList()))) &&
            ((this.participantList==null && other.getParticipantList()==null) || 
             (this.participantList!=null &&
              java.util.Arrays.equals(this.participantList, other.getParticipantList()))) &&
            ((this.inferredInteractionList==null && other.getInferredInteractionList()==null) || 
             (this.inferredInteractionList!=null &&
              java.util.Arrays.equals(this.inferredInteractionList, other.getInferredInteractionList()))) &&
            ((this.interactionType==null && other.getInteractionType()==null) || 
             (this.interactionType!=null &&
              java.util.Arrays.equals(this.interactionType, other.getInteractionType()))) &&
            ((this.modelled==null && other.getModelled()==null) || 
             (this.modelled!=null &&
              this.modelled.equals(other.getModelled()))) &&
            ((this.intraMolecular==null && other.getIntraMolecular()==null) || 
             (this.intraMolecular!=null &&
              this.intraMolecular.equals(other.getIntraMolecular()))) &&
            ((this.negative==null && other.getNegative()==null) || 
             (this.negative!=null &&
              this.negative.equals(other.getNegative()))) &&
            ((this.confidenceList==null && other.getConfidenceList()==null) || 
             (this.confidenceList!=null &&
              java.util.Arrays.equals(this.confidenceList, other.getConfidenceList()))) &&
            ((this.parameterList==null && other.getParameterList()==null) || 
             (this.parameterList!=null &&
              java.util.Arrays.equals(this.parameterList, other.getParameterList()))) &&
            ((this.attributeList==null && other.getAttributeList()==null) || 
             (this.attributeList!=null &&
              java.util.Arrays.equals(this.attributeList, other.getAttributeList()))) &&
            ((this.imexId==null && other.getImexId()==null) || 
             (this.imexId!=null &&
              this.imexId.equals(other.getImexId()))) &&
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
        if (getAvailability() != null) {
            _hashCode += getAvailability().hashCode();
        }
        if (getAvailabilityRef() != null) {
            _hashCode += getAvailabilityRef().hashCode();
        }
        if (getExperimentList() != null) {
            _hashCode += getExperimentList().hashCode();
        }
        if (getParticipantList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParticipantList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParticipantList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getInferredInteractionList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInferredInteractionList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInferredInteractionList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getInteractionType() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInteractionType());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInteractionType(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getModelled() != null) {
            _hashCode += getModelled().hashCode();
        }
        if (getIntraMolecular() != null) {
            _hashCode += getIntraMolecular().hashCode();
        }
        if (getNegative() != null) {
            _hashCode += getNegative().hashCode();
        }
        if (getConfidenceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConfidenceList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getConfidenceList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getParameterList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParameterList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParameterList(), i);
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
        if (getImexId() != null) {
            _hashCode += getImexId().hashCode();
        }
        _hashCode += getId();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Interaction.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interaction"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("imexId");
        attrField.setXmlName(new javax.xml.namespace.QName("", "imexId"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
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
        elemField.setFieldName("availability");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availability"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("availabilityRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availabilityRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("experimentList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participantList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participant"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participant"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inferredInteractionList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteractionList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteraction"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteraction"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modelled");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "modelled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("intraMolecular");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "intraMolecular"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("negative");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "negative"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("confidenceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidenceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidence"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidence"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parameterList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameterList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameter"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameter"));
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
