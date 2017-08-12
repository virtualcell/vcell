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
 * ChebiWebServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.webservices.chebi;

public class ChebiWebServiceServiceLocator extends org.apache.axis.client.Service implements uk.ac.ebi.www.webservices.chebi.ChebiWebServiceService {

    public ChebiWebServiceServiceLocator() {
    }


    public ChebiWebServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ChebiWebServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ChebiWebServicePort
    private java.lang.String ChebiWebServicePort_address = "http://www.ebi.ac.uk:80/webservices/chebi/2.0/webservice";

    public java.lang.String getChebiWebServicePortAddress() {
        return ChebiWebServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ChebiWebServicePortWSDDServiceName = "ChebiWebServicePort";

    public java.lang.String getChebiWebServicePortWSDDServiceName() {
        return ChebiWebServicePortWSDDServiceName;
    }

    public void setChebiWebServicePortWSDDServiceName(java.lang.String name) {
        ChebiWebServicePortWSDDServiceName = name;
    }

    public uk.ac.ebi.www.webservices.chebi.ChebiWebServicePortType getChebiWebServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ChebiWebServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getChebiWebServicePort(endpoint);
    }

    public uk.ac.ebi.www.webservices.chebi.ChebiWebServicePortType getChebiWebServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            uk.ac.ebi.www.webservices.chebi.ChebiWebServiceBindingStub _stub = new uk.ac.ebi.www.webservices.chebi.ChebiWebServiceBindingStub(portAddress, this);
            _stub.setPortName(getChebiWebServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setChebiWebServicePortEndpointAddress(java.lang.String address) {
        ChebiWebServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (uk.ac.ebi.www.webservices.chebi.ChebiWebServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                uk.ac.ebi.www.webservices.chebi.ChebiWebServiceBindingStub _stub = new uk.ac.ebi.www.webservices.chebi.ChebiWebServiceBindingStub(new java.net.URL(ChebiWebServicePort_address), this);
                _stub.setPortName(getChebiWebServicePortWSDDServiceName());
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
        if ("ChebiWebServicePort".equals(inputPortName)) {
            return getChebiWebServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "ChebiWebServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.ebi.ac.uk/webservices/chebi", "ChebiWebServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ChebiWebServicePort".equals(portName)) {
            setChebiWebServicePortEndpointAddress(address);
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
