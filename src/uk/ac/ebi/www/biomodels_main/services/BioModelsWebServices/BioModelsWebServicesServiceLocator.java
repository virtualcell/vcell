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
 * BioModelsWebServicesServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices;

import javax.xml.namespace.QName;

@SuppressWarnings("serial")
public class BioModelsWebServicesServiceLocator extends org.apache.axis.client.Service implements uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesService {

    public BioModelsWebServicesServiceLocator() {
    }


    public BioModelsWebServicesServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public BioModelsWebServicesServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BioModelsWebServices
    private java.lang.String BioModelsWebServices_address = "http://www.ebi.ac.uk/biomodels-main/services/BioModelsWebServices";

    public java.lang.String getBioModelsWebServicesAddress() {
        return BioModelsWebServices_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BioModelsWebServicesWSDDServiceName = "BioModelsWebServices";

    public java.lang.String getBioModelsWebServicesWSDDServiceName() {
        return BioModelsWebServicesWSDDServiceName;
    }

    public void setBioModelsWebServicesWSDDServiceName(java.lang.String name) {
        BioModelsWebServicesWSDDServiceName = name;
    }

    public uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices getBioModelsWebServices() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BioModelsWebServices_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBioModelsWebServices(endpoint);
    }

    public uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices getBioModelsWebServices(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesSoapBindingStub _stub = new uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesSoapBindingStub(portAddress, this);
            _stub.setPortName(getBioModelsWebServicesWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBioModelsWebServicesEndpointAddress(java.lang.String address) {
        BioModelsWebServices_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @SuppressWarnings("rawtypes")
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices.class.isAssignableFrom(serviceEndpointInterface)) {
                uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesSoapBindingStub _stub = new uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesSoapBindingStub(new java.net.URL(BioModelsWebServices_address), this);
                _stub.setPortName(getBioModelsWebServicesWSDDServiceName());
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
    @SuppressWarnings("rawtypes")
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("BioModelsWebServices".equals(inputPortName)) {
            return getBioModelsWebServices();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.ebi.ac.uk/biomodels-main/services/BioModelsWebServices", "BioModelsWebServicesService");
    }

    private java.util.HashSet<QName> ports = null;

    public java.util.Iterator<QName> getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet<QName>();
            ports.add(new javax.xml.namespace.QName("http://www.ebi.ac.uk/biomodels-main/services/BioModelsWebServices", "BioModelsWebServices"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BioModelsWebServices".equals(portName)) {
            setBioModelsWebServicesEndpointAddress(address);
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
