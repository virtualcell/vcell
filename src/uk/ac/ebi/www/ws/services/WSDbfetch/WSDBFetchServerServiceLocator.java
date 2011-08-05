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
 * WSDBFetchServerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.ws.services.WSDbfetch;

public class WSDBFetchServerServiceLocator extends org.apache.axis.client.Service implements uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServerService {

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

    public WSDBFetchServerServiceLocator() {
    }


    public WSDBFetchServerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WSDBFetchServerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WSDbfetch
    private java.lang.String WSDbfetch_address = "http://www.ebi.ac.uk/ws/services/WSDbfetch";

    public java.lang.String getWSDbfetchAddress() {
        return WSDbfetch_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WSDbfetchWSDDServiceName = "WSDbfetch";

    public java.lang.String getWSDbfetchWSDDServiceName() {
        return WSDbfetchWSDDServiceName;
    }

    public void setWSDbfetchWSDDServiceName(java.lang.String name) {
        WSDbfetchWSDDServiceName = name;
    }

    public uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServer getWSDbfetch() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WSDbfetch_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWSDbfetch(endpoint);
    }

    public uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServer getWSDbfetch(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            uk.ac.ebi.www.ws.services.WSDbfetch.WSDbfetchSoapBindingStub _stub = new uk.ac.ebi.www.ws.services.WSDbfetch.WSDbfetchSoapBindingStub(portAddress, this);
            _stub.setPortName(getWSDbfetchWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWSDbfetchEndpointAddress(java.lang.String address) {
        WSDbfetch_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServer.class.isAssignableFrom(serviceEndpointInterface)) {
                uk.ac.ebi.www.ws.services.WSDbfetch.WSDbfetchSoapBindingStub _stub = new uk.ac.ebi.www.ws.services.WSDbfetch.WSDbfetchSoapBindingStub(new java.net.URL(WSDbfetch_address), this);
                _stub.setPortName(getWSDbfetchWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("WSDbfetch".equals(inputPortName)) {
            return getWSDbfetch();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "WSDBFetchServerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "WSDbfetch"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WSDbfetch".equals(portName)) {
            setWSDbfetchEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
