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
 * WSDBFetchServerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.ws.services.WSDbfetch;

public interface WSDBFetchServerService extends javax.xml.rpc.Service {

/**
 * WSDbfetch: entry retrieval using entry identifiers or accession
 * numbers for 
 * various biological databases, including EMBL-Bank, InterPro, MEDLINE,
 * Patent 
 * Proteins, PDB, RefSeq, UniParc, UniProtKB and UniRef.
 * 
 * Note: this is an RPC/encoded SOAP interface to the WSDbfetch service,
 * for 
 * other interfaces please see the documentation 
 * (http://www.ebi.ac.uk/Tools/webservices/services/dbfetch).
 */
    public java.lang.String getWSDbfetchAddress();

    public uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServer getWSDbfetch() throws javax.xml.rpc.ServiceException;

    public uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServer getWSDbfetch(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
