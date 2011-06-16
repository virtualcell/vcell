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
 * MiriamProvider.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.miriamws.main.MiriamWebServices;

public interface MiriamProvider extends java.rmi.Remote {
    public java.lang.String getName(java.lang.String uri) throws java.rmi.RemoteException;
    public java.lang.String getLocation(java.lang.String uri, java.lang.String resource) throws java.rmi.RemoteException;
    public java.lang.String getURL(java.lang.String name, java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String getURI(java.lang.String name, java.lang.String id, java.lang.String type) throws java.rmi.RemoteException;
    public java.lang.String getURI(java.lang.String name, java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String[] getLocations(java.lang.String nickname, java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String[] getLocations(java.lang.String uri) throws java.rmi.RemoteException;
    public java.lang.String[] getNames(java.lang.String uri) throws java.rmi.RemoteException;
    public java.lang.String getURN(java.lang.String name, java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String[] getDataResources(java.lang.String nickname) throws java.rmi.RemoteException;
    public java.lang.String isDeprecated(java.lang.String uri) throws java.rmi.RemoteException;
    public java.lang.String getServicesInfo() throws java.rmi.RemoteException;
    public java.lang.String getServicesVersion() throws java.rmi.RemoteException;
    public java.lang.String getJavaLibraryVersion() throws java.rmi.RemoteException;
    public java.lang.String getDataTypeURN(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String[] getDataTypeURNs(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String getDataTypeURL(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String[] getDataTypeURLs(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String getDataTypeURI(java.lang.String name, java.lang.String type) throws java.rmi.RemoteException;
    public java.lang.String getDataTypeURI(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String[] getDataTypeURIs(java.lang.String name, java.lang.String type) throws java.rmi.RemoteException;
    public java.lang.String[] getDataTypeURIs(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String[] getDataTypeAllURIs(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String getDataTypeDef(java.lang.String nickname) throws java.rmi.RemoteException;
    public java.lang.String[] getDataEntries(java.lang.String nickname, java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String[] getDataEntries(java.lang.String uri) throws java.rmi.RemoteException;
    public java.lang.String getDataEntry(java.lang.String uri, java.lang.String resource) throws java.rmi.RemoteException;
    public java.lang.String getOfficialURI(java.lang.String uri) throws java.rmi.RemoteException;
    public java.lang.String getOfficialDataTypeURI(java.lang.String nickname) throws java.rmi.RemoteException;
    public java.lang.String getMiriamURI(java.lang.String uri) throws java.rmi.RemoteException;
    public java.lang.String getDataTypePattern(java.lang.String nickname) throws java.rmi.RemoteException;
    public java.lang.String getResourceInfo(java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String getResourceInstitution(java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String getResourceLocation(java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String[] getDataTypeSynonyms(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String[] getDataTypesName() throws java.rmi.RemoteException;
    public java.lang.String[] getDataTypesId() throws java.rmi.RemoteException;
    public java.lang.String checkRegExp(java.lang.String identifier, java.lang.String datatype) throws java.rmi.RemoteException;
}
