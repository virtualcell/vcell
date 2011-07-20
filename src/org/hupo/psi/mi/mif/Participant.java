/**
 * Participant.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.mif;

public class Participant  implements java.io.Serializable {
    private org.hupo.psi.mi.mif.Names names;

    private org.hupo.psi.mi.mif.Xref xref;

    private java.lang.Integer interactionRef;

    private org.hupo.psi.mi.mif.Interactor interactor;

    private java.lang.Integer interactorRef;

    private org.hupo.psi.mi.mif.ParticipantIdentificationMethod[] participantIdentificationMethodList;

    private org.hupo.psi.mi.mif.CvType biologicalRole;

    private org.hupo.psi.mi.mif.ExperimentalRole[] experimentalRoleList;

    private org.hupo.psi.mi.mif.ExperimentalPreparation[] experimentalPreparationList;

    private org.hupo.psi.mi.mif.ExperimentalInteractor[] experimentalInteractorList;

    private org.hupo.psi.mi.mif.Feature[] featureList;

    private org.hupo.psi.mi.mif.HostOrganism[] hostOrganismList;

    private org.hupo.psi.mi.mif.Confidence[] confidenceList;

    private org.hupo.psi.mi.mif.Parameter[] parameterList;

    private org.hupo.psi.mi.mif.Attribute[] attributeList;

    private int id;  // attribute

    public Participant() {
    }

    public Participant(
           org.hupo.psi.mi.mif.Names names,
           org.hupo.psi.mi.mif.Xref xref,
           java.lang.Integer interactionRef,
           org.hupo.psi.mi.mif.Interactor interactor,
           java.lang.Integer interactorRef,
           org.hupo.psi.mi.mif.ParticipantIdentificationMethod[] participantIdentificationMethodList,
           org.hupo.psi.mi.mif.CvType biologicalRole,
           org.hupo.psi.mi.mif.ExperimentalRole[] experimentalRoleList,
           org.hupo.psi.mi.mif.ExperimentalPreparation[] experimentalPreparationList,
           org.hupo.psi.mi.mif.ExperimentalInteractor[] experimentalInteractorList,
           org.hupo.psi.mi.mif.Feature[] featureList,
           org.hupo.psi.mi.mif.HostOrganism[] hostOrganismList,
           org.hupo.psi.mi.mif.Confidence[] confidenceList,
           org.hupo.psi.mi.mif.Parameter[] parameterList,
           org.hupo.psi.mi.mif.Attribute[] attributeList,
           int id) {
           this.names = names;
           this.xref = xref;
           this.interactionRef = interactionRef;
           this.interactor = interactor;
           this.interactorRef = interactorRef;
           this.participantIdentificationMethodList = participantIdentificationMethodList;
           this.biologicalRole = biologicalRole;
           this.experimentalRoleList = experimentalRoleList;
           this.experimentalPreparationList = experimentalPreparationList;
           this.experimentalInteractorList = experimentalInteractorList;
           this.featureList = featureList;
           this.hostOrganismList = hostOrganismList;
           this.confidenceList = confidenceList;
           this.parameterList = parameterList;
           this.attributeList = attributeList;
           this.id = id;
    }


    /**
     * Gets the names value for this Participant.
     * 
     * @return names
     */
    public org.hupo.psi.mi.mif.Names getNames() {
        return names;
    }


    /**
     * Sets the names value for this Participant.
     * 
     * @param names
     */
    public void setNames(org.hupo.psi.mi.mif.Names names) {
        this.names = names;
    }


    /**
     * Gets the xref value for this Participant.
     * 
     * @return xref
     */
    public org.hupo.psi.mi.mif.Xref getXref() {
        return xref;
    }


    /**
     * Sets the xref value for this Participant.
     * 
     * @param xref
     */
    public void setXref(org.hupo.psi.mi.mif.Xref xref) {
        this.xref = xref;
    }


    /**
     * Gets the interactionRef value for this Participant.
     * 
     * @return interactionRef
     */
    public java.lang.Integer getInteractionRef() {
        return interactionRef;
    }


    /**
     * Sets the interactionRef value for this Participant.
     * 
     * @param interactionRef
     */
    public void setInteractionRef(java.lang.Integer interactionRef) {
        this.interactionRef = interactionRef;
    }


    /**
     * Gets the interactor value for this Participant.
     * 
     * @return interactor
     */
    public org.hupo.psi.mi.mif.Interactor getInteractor() {
        return interactor;
    }


    /**
     * Sets the interactor value for this Participant.
     * 
     * @param interactor
     */
    public void setInteractor(org.hupo.psi.mi.mif.Interactor interactor) {
        this.interactor = interactor;
    }


    /**
     * Gets the interactorRef value for this Participant.
     * 
     * @return interactorRef
     */
    public java.lang.Integer getInteractorRef() {
        return interactorRef;
    }


    /**
     * Sets the interactorRef value for this Participant.
     * 
     * @param interactorRef
     */
    public void setInteractorRef(java.lang.Integer interactorRef) {
        this.interactorRef = interactorRef;
    }


    /**
     * Gets the participantIdentificationMethodList value for this Participant.
     * 
     * @return participantIdentificationMethodList
     */
    public org.hupo.psi.mi.mif.ParticipantIdentificationMethod[] getParticipantIdentificationMethodList() {
        return participantIdentificationMethodList;
    }


    /**
     * Sets the participantIdentificationMethodList value for this Participant.
     * 
     * @param participantIdentificationMethodList
     */
    public void setParticipantIdentificationMethodList(org.hupo.psi.mi.mif.ParticipantIdentificationMethod[] participantIdentificationMethodList) {
        this.participantIdentificationMethodList = participantIdentificationMethodList;
    }


    /**
     * Gets the biologicalRole value for this Participant.
     * 
     * @return biologicalRole
     */
    public org.hupo.psi.mi.mif.CvType getBiologicalRole() {
        return biologicalRole;
    }


    /**
     * Sets the biologicalRole value for this Participant.
     * 
     * @param biologicalRole
     */
    public void setBiologicalRole(org.hupo.psi.mi.mif.CvType biologicalRole) {
        this.biologicalRole = biologicalRole;
    }


    /**
     * Gets the experimentalRoleList value for this Participant.
     * 
     * @return experimentalRoleList
     */
    public org.hupo.psi.mi.mif.ExperimentalRole[] getExperimentalRoleList() {
        return experimentalRoleList;
    }


    /**
     * Sets the experimentalRoleList value for this Participant.
     * 
     * @param experimentalRoleList
     */
    public void setExperimentalRoleList(org.hupo.psi.mi.mif.ExperimentalRole[] experimentalRoleList) {
        this.experimentalRoleList = experimentalRoleList;
    }


    /**
     * Gets the experimentalPreparationList value for this Participant.
     * 
     * @return experimentalPreparationList
     */
    public org.hupo.psi.mi.mif.ExperimentalPreparation[] getExperimentalPreparationList() {
        return experimentalPreparationList;
    }


    /**
     * Sets the experimentalPreparationList value for this Participant.
     * 
     * @param experimentalPreparationList
     */
    public void setExperimentalPreparationList(org.hupo.psi.mi.mif.ExperimentalPreparation[] experimentalPreparationList) {
        this.experimentalPreparationList = experimentalPreparationList;
    }


    /**
     * Gets the experimentalInteractorList value for this Participant.
     * 
     * @return experimentalInteractorList
     */
    public org.hupo.psi.mi.mif.ExperimentalInteractor[] getExperimentalInteractorList() {
        return experimentalInteractorList;
    }


    /**
     * Sets the experimentalInteractorList value for this Participant.
     * 
     * @param experimentalInteractorList
     */
    public void setExperimentalInteractorList(org.hupo.psi.mi.mif.ExperimentalInteractor[] experimentalInteractorList) {
        this.experimentalInteractorList = experimentalInteractorList;
    }


    /**
     * Gets the featureList value for this Participant.
     * 
     * @return featureList
     */
    public org.hupo.psi.mi.mif.Feature[] getFeatureList() {
        return featureList;
    }


    /**
     * Sets the featureList value for this Participant.
     * 
     * @param featureList
     */
    public void setFeatureList(org.hupo.psi.mi.mif.Feature[] featureList) {
        this.featureList = featureList;
    }


    /**
     * Gets the hostOrganismList value for this Participant.
     * 
     * @return hostOrganismList
     */
    public org.hupo.psi.mi.mif.HostOrganism[] getHostOrganismList() {
        return hostOrganismList;
    }


    /**
     * Sets the hostOrganismList value for this Participant.
     * 
     * @param hostOrganismList
     */
    public void setHostOrganismList(org.hupo.psi.mi.mif.HostOrganism[] hostOrganismList) {
        this.hostOrganismList = hostOrganismList;
    }


    /**
     * Gets the confidenceList value for this Participant.
     * 
     * @return confidenceList
     */
    public org.hupo.psi.mi.mif.Confidence[] getConfidenceList() {
        return confidenceList;
    }


    /**
     * Sets the confidenceList value for this Participant.
     * 
     * @param confidenceList
     */
    public void setConfidenceList(org.hupo.psi.mi.mif.Confidence[] confidenceList) {
        this.confidenceList = confidenceList;
    }


    /**
     * Gets the parameterList value for this Participant.
     * 
     * @return parameterList
     */
    public org.hupo.psi.mi.mif.Parameter[] getParameterList() {
        return parameterList;
    }


    /**
     * Sets the parameterList value for this Participant.
     * 
     * @param parameterList
     */
    public void setParameterList(org.hupo.psi.mi.mif.Parameter[] parameterList) {
        this.parameterList = parameterList;
    }


    /**
     * Gets the attributeList value for this Participant.
     * 
     * @return attributeList
     */
    public org.hupo.psi.mi.mif.Attribute[] getAttributeList() {
        return attributeList;
    }


    /**
     * Sets the attributeList value for this Participant.
     * 
     * @param attributeList
     */
    public void setAttributeList(org.hupo.psi.mi.mif.Attribute[] attributeList) {
        this.attributeList = attributeList;
    }


    /**
     * Gets the id value for this Participant.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this Participant.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Participant)) return false;
        Participant other = (Participant) obj;
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
            ((this.interactionRef==null && other.getInteractionRef()==null) || 
             (this.interactionRef!=null &&
              this.interactionRef.equals(other.getInteractionRef()))) &&
            ((this.interactor==null && other.getInteractor()==null) || 
             (this.interactor!=null &&
              this.interactor.equals(other.getInteractor()))) &&
            ((this.interactorRef==null && other.getInteractorRef()==null) || 
             (this.interactorRef!=null &&
              this.interactorRef.equals(other.getInteractorRef()))) &&
            ((this.participantIdentificationMethodList==null && other.getParticipantIdentificationMethodList()==null) || 
             (this.participantIdentificationMethodList!=null &&
              java.util.Arrays.equals(this.participantIdentificationMethodList, other.getParticipantIdentificationMethodList()))) &&
            ((this.biologicalRole==null && other.getBiologicalRole()==null) || 
             (this.biologicalRole!=null &&
              this.biologicalRole.equals(other.getBiologicalRole()))) &&
            ((this.experimentalRoleList==null && other.getExperimentalRoleList()==null) || 
             (this.experimentalRoleList!=null &&
              java.util.Arrays.equals(this.experimentalRoleList, other.getExperimentalRoleList()))) &&
            ((this.experimentalPreparationList==null && other.getExperimentalPreparationList()==null) || 
             (this.experimentalPreparationList!=null &&
              java.util.Arrays.equals(this.experimentalPreparationList, other.getExperimentalPreparationList()))) &&
            ((this.experimentalInteractorList==null && other.getExperimentalInteractorList()==null) || 
             (this.experimentalInteractorList!=null &&
              java.util.Arrays.equals(this.experimentalInteractorList, other.getExperimentalInteractorList()))) &&
            ((this.featureList==null && other.getFeatureList()==null) || 
             (this.featureList!=null &&
              java.util.Arrays.equals(this.featureList, other.getFeatureList()))) &&
            ((this.hostOrganismList==null && other.getHostOrganismList()==null) || 
             (this.hostOrganismList!=null &&
              java.util.Arrays.equals(this.hostOrganismList, other.getHostOrganismList()))) &&
            ((this.confidenceList==null && other.getConfidenceList()==null) || 
             (this.confidenceList!=null &&
              java.util.Arrays.equals(this.confidenceList, other.getConfidenceList()))) &&
            ((this.parameterList==null && other.getParameterList()==null) || 
             (this.parameterList!=null &&
              java.util.Arrays.equals(this.parameterList, other.getParameterList()))) &&
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
        if (getInteractionRef() != null) {
            _hashCode += getInteractionRef().hashCode();
        }
        if (getInteractor() != null) {
            _hashCode += getInteractor().hashCode();
        }
        if (getInteractorRef() != null) {
            _hashCode += getInteractorRef().hashCode();
        }
        if (getParticipantIdentificationMethodList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParticipantIdentificationMethodList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParticipantIdentificationMethodList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getBiologicalRole() != null) {
            _hashCode += getBiologicalRole().hashCode();
        }
        if (getExperimentalRoleList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExperimentalRoleList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExperimentalRoleList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExperimentalPreparationList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExperimentalPreparationList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExperimentalPreparationList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExperimentalInteractorList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExperimentalInteractorList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExperimentalInteractorList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFeatureList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFeatureList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFeatureList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getHostOrganismList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getHostOrganismList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getHostOrganismList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        _hashCode += getId();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Participant.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participant"));
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
        elemField.setFieldName("interactionRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactionRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interactor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interactorRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactorRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("participantIdentificationMethodList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantIdentificationMethodList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantIdentificationMethod"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantIdentificationMethod"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("biologicalRole");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "biologicalRole"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("experimentalRoleList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalRoleList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalRole"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalRole"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("experimentalPreparationList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalPreparationList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalPreparation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalPreparation"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("experimentalInteractorList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalInteractorList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalInteractor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalInteractor"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("featureList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "featureList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "feature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "feature"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostOrganismList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganismList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganism"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganism"));
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
