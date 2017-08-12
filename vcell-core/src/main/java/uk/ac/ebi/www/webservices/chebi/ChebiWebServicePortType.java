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
 * ChebiWebServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.webservices.chebi;

public interface ChebiWebServicePortType extends java.rmi.Remote {
    public uk.ac.ebi.www.webservices.chebi.LiteEntity[] getLiteEntity(java.lang.String search, uk.ac.ebi.www.webservices.chebi.SearchCategory searchCategory, int maximumResults, uk.ac.ebi.www.webservices.chebi.StarsCategory stars) throws java.rmi.RemoteException, uk.ac.ebi.www.webservices.chebi.ChebiWebServiceFault;
    public uk.ac.ebi.www.webservices.chebi.Entity getCompleteEntity(java.lang.String chebiId) throws java.rmi.RemoteException, uk.ac.ebi.www.webservices.chebi.ChebiWebServiceFault;
    public uk.ac.ebi.www.webservices.chebi.Entity[] getCompleteEntityByList(java.lang.String[] listOfChEBIIds) throws java.rmi.RemoteException, uk.ac.ebi.www.webservices.chebi.ChebiWebServiceFault;
    public uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] getOntologyParents(java.lang.String chebiId) throws java.rmi.RemoteException, uk.ac.ebi.www.webservices.chebi.ChebiWebServiceFault;
    public uk.ac.ebi.www.webservices.chebi.OntologyDataItem[] getOntologyChildren(java.lang.String chebiId) throws java.rmi.RemoteException, uk.ac.ebi.www.webservices.chebi.ChebiWebServiceFault;
    public uk.ac.ebi.www.webservices.chebi.LiteEntity[] getAllOntologyChildrenInPath(java.lang.String chebiId, uk.ac.ebi.www.webservices.chebi.RelationshipType relationshipType, boolean structureOnly) throws java.rmi.RemoteException, uk.ac.ebi.www.webservices.chebi.ChebiWebServiceFault;
    public uk.ac.ebi.www.webservices.chebi.LiteEntity[] getStructureSearch(java.lang.String structure, uk.ac.ebi.www.webservices.chebi.StructureType type, uk.ac.ebi.www.webservices.chebi.StructureSearchCategory structureSearchCategory, int totalResults, float tanimotoCutoff) throws java.rmi.RemoteException, uk.ac.ebi.www.webservices.chebi.ChebiWebServiceFault;
}
