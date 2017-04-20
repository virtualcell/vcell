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
 * BioModelsWebServices.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices;

public interface BioModelsWebServices extends java.rmi.Remote {
    public java.lang.String getSimpleModelsByReactomeIds(java.lang.String[] reactomeIds) throws java.rmi.RemoteException;
    public java.lang.String getSimpleModelsRelatedWithChEBI() throws java.rmi.RemoteException;
    public java.lang.String getSimpleModelsByChEBIIds(java.lang.String[] chEBIIds) throws java.rmi.RemoteException;
    public java.lang.String helloBioModels() throws java.rmi.RemoteException;
    public java.lang.String[] getAllModelsId() throws java.rmi.RemoteException;
    public java.lang.String[] getAllCuratedModelsId() throws java.rmi.RemoteException;
    public java.lang.String[] getAllNonCuratedModelsId() throws java.rmi.RemoteException;
    public java.lang.String getModelById(java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String getModelSBMLById(java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String getSimpleModelById(java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String getModelNameById(java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByName(java.lang.String modelName) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByPublication(java.lang.String publicationIdOrText) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByPerson(java.lang.String personName) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByChEBIId(java.lang.String chEBIId) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByChEBI(java.lang.String text) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByUniprotId(java.lang.String uniprotId) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByUniprotIds(java.lang.String[] uniprotIds) throws java.rmi.RemoteException;
    public java.lang.String getSimpleModelsByUniprotIds(java.lang.String[] uniProtIds) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByUniprot(java.lang.String text) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByGOId(java.lang.String GOId) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByGO(java.lang.String text) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByTaxonomyId(java.lang.String taxonomyId) throws java.rmi.RemoteException;
    public java.lang.String[] getModelsIdByTaxonomy(java.lang.String text) throws java.rmi.RemoteException;
    public java.lang.String getSubModelSBML(java.lang.String modelId, java.lang.String[] elementsIds) throws java.rmi.RemoteException;
}
