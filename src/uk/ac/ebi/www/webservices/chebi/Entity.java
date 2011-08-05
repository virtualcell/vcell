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
 * Entity.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.webservices.chebi;

public class Entity  implements java.io.Serializable {
    private java.lang.String chebiId;

    private java.lang.String chebiAsciiName;

    private java.lang.String definition;

    private java.lang.String status;

    private java.lang.String smiles;

    private java.lang.String inchi;

    private java.lang.String inchiKey;

    private java.lang.String charge;

    private java.lang.String mass;

    private java.lang.Integer entityStar;

    private java.lang.String[] secondaryChEBIIds;

    private uk.ac.ebi.www.webservices.chebi.DataItem[] synonyms;

    private uk.ac.ebi.www.webservices.chebi.DataItem[] iupacNames;

    private uk.ac.ebi.www.webservices.chebi.DataItem[] formulae;

    private uk.ac.ebi.www.webservices.chebi.DataItem[] registryNumbers;

    private uk.ac.ebi.www.webservices.chebi.DataItem[] citations;

    private uk.ac.ebi.www.webservices.chebi.StructureDataItem[] chemicalStructures;

    private uk.ac.ebi.www.webservices.chebi.DataItem[] databaseLinks;

    private uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] ontologyParents;

    private uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] ontologyChildren;

    private uk.ac.ebi.www.webservices.chebi.CommentDataItem[] generalComments;

    public Entity() {
    }

    public Entity(
           java.lang.String chebiId,
           java.lang.String chebiAsciiName,
           java.lang.String definition,
           java.lang.String status,
           java.lang.String smiles,
           java.lang.String inchi,
           java.lang.String inchiKey,
           java.lang.String charge,
           java.lang.String mass,
           java.lang.Integer entityStar,
           java.lang.String[] secondaryChEBIIds,
           uk.ac.ebi.www.webservices.chebi.DataItem[] synonyms,
           uk.ac.ebi.www.webservices.chebi.DataItem[] iupacNames,
           uk.ac.ebi.www.webservices.chebi.DataItem[] formulae,
           uk.ac.ebi.www.webservices.chebi.DataItem[] registryNumbers,
           uk.ac.ebi.www.webservices.chebi.DataItem[] citations,
           uk.ac.ebi.www.webservices.chebi.StructureDataItem[] chemicalStructures,
           uk.ac.ebi.www.webservices.chebi.DataItem[] databaseLinks,
           uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] ontologyParents,
           uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] ontologyChildren,
           uk.ac.ebi.www.webservices.chebi.CommentDataItem[] generalComments) {
           this.chebiId = chebiId;
           this.chebiAsciiName = chebiAsciiName;
           this.definition = definition;
           this.status = status;
           this.smiles = smiles;
           this.inchi = inchi;
           this.inchiKey = inchiKey;
           this.charge = charge;
           this.mass = mass;
           this.entityStar = entityStar;
           this.secondaryChEBIIds = secondaryChEBIIds;
           this.synonyms = synonyms;
           this.iupacNames = iupacNames;
           this.formulae = formulae;
           this.registryNumbers = registryNumbers;
           this.citations = citations;
           this.chemicalStructures = chemicalStructures;
           this.databaseLinks = databaseLinks;
           this.ontologyParents = ontologyParents;
           this.ontologyChildren = ontologyChildren;
           this.generalComments = generalComments;
    }


    /**
     * Gets the chebiId value for this Entity.
     * 
     * @return chebiId
     */
    public java.lang.String getChebiId() {
        return chebiId;
    }


    /**
     * Sets the chebiId value for this Entity.
     * 
     * @param chebiId
     */
    public void setChebiId(java.lang.String chebiId) {
        this.chebiId = chebiId;
    }


    /**
     * Gets the chebiAsciiName value for this Entity.
     * 
     * @return chebiAsciiName
     */
    public java.lang.String getChebiAsciiName() {
        return chebiAsciiName;
    }


    /**
     * Sets the chebiAsciiName value for this Entity.
     * 
     * @param chebiAsciiName
     */
    public void setChebiAsciiName(java.lang.String chebiAsciiName) {
        this.chebiAsciiName = chebiAsciiName;
    }


    /**
     * Gets the definition value for this Entity.
     * 
     * @return definition
     */
    public java.lang.String getDefinition() {
        return definition;
    }


    /**
     * Sets the definition value for this Entity.
     * 
     * @param definition
     */
    public void setDefinition(java.lang.String definition) {
        this.definition = definition;
    }


    /**
     * Gets the status value for this Entity.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this Entity.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the smiles value for this Entity.
     * 
     * @return smiles
     */
    public java.lang.String getSmiles() {
        return smiles;
    }


    /**
     * Sets the smiles value for this Entity.
     * 
     * @param smiles
     */
    public void setSmiles(java.lang.String smiles) {
        this.smiles = smiles;
    }


    /**
     * Gets the inchi value for this Entity.
     * 
     * @return inchi
     */
    public java.lang.String getInchi() {
        return inchi;
    }


    /**
     * Sets the inchi value for this Entity.
     * 
     * @param inchi
     */
    public void setInchi(java.lang.String inchi) {
        this.inchi = inchi;
    }


    /**
     * Gets the inchiKey value for this Entity.
     * 
     * @return inchiKey
     */
    public java.lang.String getInchiKey() {
        return inchiKey;
    }


    /**
     * Sets the inchiKey value for this Entity.
     * 
     * @param inchiKey
     */
    public void setInchiKey(java.lang.String inchiKey) {
        this.inchiKey = inchiKey;
    }


    /**
     * Gets the charge value for this Entity.
     * 
     * @return charge
     */
    public java.lang.String getCharge() {
        return charge;
    }


    /**
     * Sets the charge value for this Entity.
     * 
     * @param charge
     */
    public void setCharge(java.lang.String charge) {
        this.charge = charge;
    }


    /**
     * Gets the mass value for this Entity.
     * 
     * @return mass
     */
    public java.lang.String getMass() {
        return mass;
    }


    /**
     * Sets the mass value for this Entity.
     * 
     * @param mass
     */
    public void setMass(java.lang.String mass) {
        this.mass = mass;
    }


    /**
     * Gets the entityStar value for this Entity.
     * 
     * @return entityStar
     */
    public java.lang.Integer getEntityStar() {
        return entityStar;
    }


    /**
     * Sets the entityStar value for this Entity.
     * 
     * @param entityStar
     */
    public void setEntityStar(java.lang.Integer entityStar) {
        this.entityStar = entityStar;
    }


    /**
     * Gets the secondaryChEBIIds value for this Entity.
     * 
     * @return secondaryChEBIIds
     */
    public java.lang.String[] getSecondaryChEBIIds() {
        return secondaryChEBIIds;
    }


    /**
     * Sets the secondaryChEBIIds value for this Entity.
     * 
     * @param secondaryChEBIIds
     */
    public void setSecondaryChEBIIds(java.lang.String[] secondaryChEBIIds) {
        this.secondaryChEBIIds = secondaryChEBIIds;
    }

    public java.lang.String getSecondaryChEBIIds(int i) {
        return this.secondaryChEBIIds[i];
    }

    public void setSecondaryChEBIIds(int i, java.lang.String _value) {
        this.secondaryChEBIIds[i] = _value;
    }


    /**
     * Gets the synonyms value for this Entity.
     * 
     * @return synonyms
     */
    public uk.ac.ebi.www.webservices.chebi.DataItem[] getSynonyms() {
        return synonyms;
    }


    /**
     * Sets the synonyms value for this Entity.
     * 
     * @param synonyms
     */
    public void setSynonyms(uk.ac.ebi.www.webservices.chebi.DataItem[] synonyms) {
        this.synonyms = synonyms;
    }

    public uk.ac.ebi.www.webservices.chebi.DataItem getSynonyms(int i) {
        return this.synonyms[i];
    }

    public void setSynonyms(int i, uk.ac.ebi.www.webservices.chebi.DataItem _value) {
        this.synonyms[i] = _value;
    }


    /**
     * Gets the iupacNames value for this Entity.
     * 
     * @return iupacNames
     */
    public uk.ac.ebi.www.webservices.chebi.DataItem[] getIupacNames() {
        return iupacNames;
    }


    /**
     * Sets the iupacNames value for this Entity.
     * 
     * @param iupacNames
     */
    public void setIupacNames(uk.ac.ebi.www.webservices.chebi.DataItem[] iupacNames) {
        this.iupacNames = iupacNames;
    }

    public uk.ac.ebi.www.webservices.chebi.DataItem getIupacNames(int i) {
        return this.iupacNames[i];
    }

    public void setIupacNames(int i, uk.ac.ebi.www.webservices.chebi.DataItem _value) {
        this.iupacNames[i] = _value;
    }


    /**
     * Gets the formulae value for this Entity.
     * 
     * @return formulae
     */
    public uk.ac.ebi.www.webservices.chebi.DataItem[] getFormulae() {
        return formulae;
    }


    /**
     * Sets the formulae value for this Entity.
     * 
     * @param formulae
     */
    public void setFormulae(uk.ac.ebi.www.webservices.chebi.DataItem[] formulae) {
        this.formulae = formulae;
    }

    public uk.ac.ebi.www.webservices.chebi.DataItem getFormulae(int i) {
        return this.formulae[i];
    }

    public void setFormulae(int i, uk.ac.ebi.www.webservices.chebi.DataItem _value) {
        this.formulae[i] = _value;
    }


    /**
     * Gets the registryNumbers value for this Entity.
     * 
     * @return registryNumbers
     */
    public uk.ac.ebi.www.webservices.chebi.DataItem[] getRegistryNumbers() {
        return registryNumbers;
    }


    /**
     * Sets the registryNumbers value for this Entity.
     * 
     * @param registryNumbers
     */
    public void setRegistryNumbers(uk.ac.ebi.www.webservices.chebi.DataItem[] registryNumbers) {
        this.registryNumbers = registryNumbers;
    }

    public uk.ac.ebi.www.webservices.chebi.DataItem getRegistryNumbers(int i) {
        return this.registryNumbers[i];
    }

    public void setRegistryNumbers(int i, uk.ac.ebi.www.webservices.chebi.DataItem _value) {
        this.registryNumbers[i] = _value;
    }


    /**
     * Gets the citations value for this Entity.
     * 
     * @return citations
     */
    public uk.ac.ebi.www.webservices.chebi.DataItem[] getCitations() {
        return citations;
    }


    /**
     * Sets the citations value for this Entity.
     * 
     * @param citations
     */
    public void setCitations(uk.ac.ebi.www.webservices.chebi.DataItem[] citations) {
        this.citations = citations;
    }

    public uk.ac.ebi.www.webservices.chebi.DataItem getCitations(int i) {
        return this.citations[i];
    }

    public void setCitations(int i, uk.ac.ebi.www.webservices.chebi.DataItem _value) {
        this.citations[i] = _value;
    }


    /**
     * Gets the chemicalStructures value for this Entity.
     * 
     * @return chemicalStructures
     */
    public uk.ac.ebi.www.webservices.chebi.StructureDataItem[] getChemicalStructures() {
        return chemicalStructures;
    }


    /**
     * Sets the chemicalStructures value for this Entity.
     * 
     * @param chemicalStructures
     */
    public void setChemicalStructures(uk.ac.ebi.www.webservices.chebi.StructureDataItem[] chemicalStructures) {
        this.chemicalStructures = chemicalStructures;
    }

    public uk.ac.ebi.www.webservices.chebi.StructureDataItem getChemicalStructures(int i) {
        return this.chemicalStructures[i];
    }

    public void setChemicalStructures(int i, uk.ac.ebi.www.webservices.chebi.StructureDataItem _value) {
        this.chemicalStructures[i] = _value;
    }


    /**
     * Gets the databaseLinks value for this Entity.
     * 
     * @return databaseLinks
     */
    public uk.ac.ebi.www.webservices.chebi.DataItem[] getDatabaseLinks() {
        return databaseLinks;
    }


    /**
     * Sets the databaseLinks value for this Entity.
     * 
     * @param databaseLinks
     */
    public void setDatabaseLinks(uk.ac.ebi.www.webservices.chebi.DataItem[] databaseLinks) {
        this.databaseLinks = databaseLinks;
    }

    public uk.ac.ebi.www.webservices.chebi.DataItem getDatabaseLinks(int i) {
        return this.databaseLinks[i];
    }

    public void setDatabaseLinks(int i, uk.ac.ebi.www.webservices.chebi.DataItem _value) {
        this.databaseLinks[i] = _value;
    }


    /**
     * Gets the ontologyParents value for this Entity.
     * 
     * @return ontologyParents
     */
    public uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] getOntologyParents() {
        return ontologyParents;
    }


    /**
     * Sets the ontologyParents value for this Entity.
     * 
     * @param ontologyParents
     */
    public void setOntologyParents(uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] ontologyParents) {
        this.ontologyParents = ontologyParents;
    }

    public uk.ac.ebi.www.webservices.chebi.OntologyDataItem getOntologyParents(int i) {
        return this.ontologyParents[i];
    }

    public void setOntologyParents(int i, uk.ac.ebi.www.webservices.chebi.OntologyDataItem _value) {
        this.ontologyParents[i] = _value;
    }


    /**
     * Gets the ontologyChildren value for this Entity.
     * 
     * @return ontologyChildren
     */
    public uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] getOntologyChildren() {
        return ontologyChildren;
    }


    /**
     * Sets the ontologyChildren value for this Entity.
     * 
     * @param ontologyChildren
     */
    public void setOntologyChildren(uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] ontologyChildren) {
        this.ontologyChildren = ontologyChildren;
    }

    public uk.ac.ebi.www.webservices.chebi.OntologyDataItem getOntologyChildren(int i) {
        return this.ontologyChildren[i];
    }

    public void setOntologyChildren(int i, uk.ac.ebi.www.webservices.chebi.OntologyDataItem _value) {
        this.ontologyChildren[i] = _value;
    }


    /**
     * Gets the generalComments value for this Entity.
     * 
     * @return generalComments
     */
    public uk.ac.ebi.www.webservices.chebi.CommentDataItem[] getGeneralComments() {
        return generalComments;
    }


    /**
     * Sets the generalComments value for this Entity.
     * 
     * @param generalComments
     */
    public void setGeneralComments(uk.ac.ebi.www.webservices.chebi.CommentDataItem[] generalComments) {
        this.generalComments = generalComments;
    }

    public uk.ac.ebi.www.webservices.chebi.CommentDataItem getGeneralComments(int i) {
        return this.generalComments[i];
    }

    public void setGeneralComments(int i, uk.ac.ebi.www.webservices.chebi.CommentDataItem _value) {
        this.generalComments[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Entity)) return false;
        Entity other = (Entity) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.chebiId==null && other.getChebiId()==null) || 
             (this.chebiId!=null &&
              this.chebiId.equals(other.getChebiId()))) &&
            ((this.chebiAsciiName==null && other.getChebiAsciiName()==null) || 
             (this.chebiAsciiName!=null &&
              this.chebiAsciiName.equals(other.getChebiAsciiName()))) &&
            ((this.definition==null && other.getDefinition()==null) || 
             (this.definition!=null &&
              this.definition.equals(other.getDefinition()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.smiles==null && other.getSmiles()==null) || 
             (this.smiles!=null &&
              this.smiles.equals(other.getSmiles()))) &&
            ((this.inchi==null && other.getInchi()==null) || 
             (this.inchi!=null &&
              this.inchi.equals(other.getInchi()))) &&
            ((this.inchiKey==null && other.getInchiKey()==null) || 
             (this.inchiKey!=null &&
              this.inchiKey.equals(other.getInchiKey()))) &&
            ((this.charge==null && other.getCharge()==null) || 
             (this.charge!=null &&
              this.charge.equals(other.getCharge()))) &&
            ((this.mass==null && other.getMass()==null) || 
             (this.mass!=null &&
              this.mass.equals(other.getMass()))) &&
            ((this.entityStar==null && other.getEntityStar()==null) || 
             (this.entityStar!=null &&
              this.entityStar.equals(other.getEntityStar()))) &&
            ((this.secondaryChEBIIds==null && other.getSecondaryChEBIIds()==null) || 
             (this.secondaryChEBIIds!=null &&
              java.util.Arrays.equals(this.secondaryChEBIIds, other.getSecondaryChEBIIds()))) &&
            ((this.synonyms==null && other.getSynonyms()==null) || 
             (this.synonyms!=null &&
              java.util.Arrays.equals(this.synonyms, other.getSynonyms()))) &&
            ((this.iupacNames==null && other.getIupacNames()==null) || 
             (this.iupacNames!=null &&
              java.util.Arrays.equals(this.iupacNames, other.getIupacNames()))) &&
            ((this.formulae==null && other.getFormulae()==null) || 
             (this.formulae!=null &&
              java.util.Arrays.equals(this.formulae, other.getFormulae()))) &&
            ((this.registryNumbers==null && other.getRegistryNumbers()==null) || 
             (this.registryNumbers!=null &&
              java.util.Arrays.equals(this.registryNumbers, other.getRegistryNumbers()))) &&
            ((this.citations==null && other.getCitations()==null) || 
             (this.citations!=null &&
              java.util.Arrays.equals(this.citations, other.getCitations()))) &&
            ((this.chemicalStructures==null && other.getChemicalStructures()==null) || 
             (this.chemicalStructures!=null &&
              java.util.Arrays.equals(this.chemicalStructures, other.getChemicalStructures()))) &&
            ((this.databaseLinks==null && other.getDatabaseLinks()==null) || 
             (this.databaseLinks!=null &&
              java.util.Arrays.equals(this.databaseLinks, other.getDatabaseLinks()))) &&
            ((this.ontologyParents==null && other.getOntologyParents()==null) || 
             (this.ontologyParents!=null &&
              java.util.Arrays.equals(this.ontologyParents, other.getOntologyParents()))) &&
            ((this.ontologyChildren==null && other.getOntologyChildren()==null) || 
             (this.ontologyChildren!=null &&
              java.util.Arrays.equals(this.ontologyChildren, other.getOntologyChildren()))) &&
            ((this.generalComments==null && other.getGeneralComments()==null) || 
             (this.generalComments!=null &&
              java.util.Arrays.equals(this.generalComments, other.getGeneralComments())));
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
        if (getChebiId() != null) {
            _hashCode += getChebiId().hashCode();
        }
        if (getChebiAsciiName() != null) {
            _hashCode += getChebiAsciiName().hashCode();
        }
        if (getDefinition() != null) {
            _hashCode += getDefinition().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getSmiles() != null) {
            _hashCode += getSmiles().hashCode();
        }
        if (getInchi() != null) {
            _hashCode += getInchi().hashCode();
        }
        if (getInchiKey() != null) {
            _hashCode += getInchiKey().hashCode();
        }
        if (getCharge() != null) {
            _hashCode += getCharge().hashCode();
        }
        if (getMass() != null) {
            _hashCode += getMass().hashCode();
        }
        if (getEntityStar() != null) {
            _hashCode += getEntityStar().hashCode();
        }
        if (getSecondaryChEBIIds() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSecondaryChEBIIds());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSecondaryChEBIIds(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSynonyms() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSynonyms());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSynonyms(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIupacNames() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getIupacNames());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getIupacNames(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFormulae() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFormulae());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFormulae(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRegistryNumbers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRegistryNumbers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRegistryNumbers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCitations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCitations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCitations(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getChemicalStructures() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChemicalStructures());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChemicalStructures(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDatabaseLinks() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDatabaseLinks());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDatabaseLinks(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOntologyParents() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOntologyParents());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOntologyParents(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getOntologyChildren() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOntologyChildren());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOntologyChildren(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGeneralComments() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGeneralComments());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGeneralComments(), i);
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
        new org.apache.axis.description.TypeDesc(Entity.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "Entity"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chebiId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "chebiId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chebiAsciiName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "chebiAsciiName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("definition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "definition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("smiles");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "smiles"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inchi");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "inchi"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inchiKey");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "inchiKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("charge");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "charge"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "mass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entityStar");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "entityStar"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secondaryChEBIIds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "SecondaryChEBIIds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("synonyms");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "Synonyms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "DataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iupacNames");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "IupacNames"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "DataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("formulae");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "Formulae"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "DataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registryNumbers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "RegistryNumbers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "DataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("citations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "Citations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "DataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chemicalStructures");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "ChemicalStructures"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "StructureDataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("databaseLinks");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "DatabaseLinks"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "DataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ontologyParents");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "OntologyParents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "OntologyDataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ontologyChildren");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "OntologyChildren"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "OntologyDataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("generalComments");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "GeneralComments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "CommentDataItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
