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
 * WSDbfetchSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.ws.services.WSDbfetch;

public class WSDbfetchSoapBindingStub extends org.apache.axis.client.Stub implements uk.ac.ebi.www.ws.services.WSDbfetch.WSDBFetchServer {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[7];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDbFormats");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "db"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "ArrayOf_xsd_string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getDbFormatsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfParamsException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfParamsException"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("fetchData");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "query"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "format"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "style"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "fetchDataReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfConnException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfConnException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfNoEntryFoundException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfParamsException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfParamsException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.www.ws.services.WSDbfetch.InputException",
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "InputException"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("fetchBatch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "db"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "ids"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "format"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "style"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "fetchBatchReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfConnException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfConnException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfNoEntryFoundException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfParamsException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfParamsException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.www.ws.services.WSDbfetch.InputException",
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "InputException"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSupportedDBs");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "ArrayOf_xsd_string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getSupportedDBsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSupportedFormats");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "ArrayOf_xsd_string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getSupportedFormatsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getFormatStyles");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "db"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "format"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "ArrayOf_xsd_string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getFormatStylesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fault"),
                      "uk.ac.ebi.jdbfetch.exceptions.DbfParamsException",
                      new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfParamsException"), 
                      true
                     ));
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSupportedStyles");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "ArrayOf_xsd_string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getSupportedStylesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

    }

    public WSDbfetchSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public WSDbfetchSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public WSDbfetchSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfConnException");
            cachedSerQNames.add(qName);
            cls = uk.ac.ebi.jdbfetch.exceptions.DbfConnException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfException");
            cachedSerQNames.add(qName);
            cls = uk.ac.ebi.jdbfetch.exceptions.DbfException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfNoEntryFoundException");
            cachedSerQNames.add(qName);
            cls = uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://exceptions.jdbfetch.ebi.ac.uk", "DbfParamsException");
            cachedSerQNames.add(qName);
            cls = uk.ac.ebi.jdbfetch.exceptions.DbfParamsException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "ArrayOf_xsd_string");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "InputException");
            cachedSerQNames.add(qName);
            cls = uk.ac.ebi.www.ws.services.WSDbfetch.InputException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }


    /**
     * Get a list of formats for a given database (see http://www.ebi.ac.uk/Tools/webservices/services/dbfetch#getdbformats_db).
     */
    public java.lang.String[] getDbFormats(java.lang.String db) throws java.rmi.RemoteException, uk.ac.ebi.jdbfetch.exceptions.DbfParamsException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "getDbFormats"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {db});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfParamsException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfParamsException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }


    /**
     * Get a database entry (see http://www.ebi.ac.uk/Tools/webservices/services/dbfetch#fetchdata_query_format_style).
     */
    public java.lang.String fetchData(java.lang.String query, java.lang.String format, java.lang.String style) throws java.rmi.RemoteException, uk.ac.ebi.jdbfetch.exceptions.DbfConnException, uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException, uk.ac.ebi.jdbfetch.exceptions.DbfException, uk.ac.ebi.jdbfetch.exceptions.DbfParamsException, uk.ac.ebi.www.ws.services.WSDbfetch.InputException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fetchData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {query, format, style});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfConnException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfConnException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfParamsException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfParamsException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.www.ws.services.WSDbfetch.InputException) {
              throw (uk.ac.ebi.www.ws.services.WSDbfetch.InputException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }


    /**
     * Get a set of database entries (see http://www.ebi.ac.uk/Tools/webservices/services/dbfetch#fetchbatch_db_ids_format_style).
     */
    public java.lang.String fetchBatch(java.lang.String db, java.lang.String ids, java.lang.String format, java.lang.String style) throws java.rmi.RemoteException, uk.ac.ebi.jdbfetch.exceptions.DbfConnException, uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException, uk.ac.ebi.jdbfetch.exceptions.DbfException, uk.ac.ebi.jdbfetch.exceptions.DbfParamsException, uk.ac.ebi.www.ws.services.WSDbfetch.InputException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "fetchBatch"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {db, ids, format, style});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfConnException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfConnException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfNoEntryFoundException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfParamsException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfParamsException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.www.ws.services.WSDbfetch.InputException) {
              throw (uk.ac.ebi.www.ws.services.WSDbfetch.InputException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }


    /**
     * Get a list of available databases (see http://www.ebi.ac.uk/Tools/webservices/services/dbfetch#getsupporteddbs).
     */
    public java.lang.String[] getSupportedDBs() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "getSupportedDBs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }


    /**
     * Get a list of databases and formats (see http://www.ebi.ac.uk/Tools/webservices/services/dbfetch#getsupportedformats).
     */
    public java.lang.String[] getSupportedFormats() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "getSupportedFormats"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }


    /**
     * Get a list of available styles for a format of a database (see
     * http://www.ebi.ac.uk/Tools/webservices/services/dbfetch#getformatstyles_db_format).
     */
    public java.lang.String[] getFormatStyles(java.lang.String db, java.lang.String format) throws java.rmi.RemoteException, uk.ac.ebi.jdbfetch.exceptions.DbfParamsException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "getFormatStyles"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {db, format});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof uk.ac.ebi.jdbfetch.exceptions.DbfParamsException) {
              throw (uk.ac.ebi.jdbfetch.exceptions.DbfParamsException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }


    /**
     * Get a list of databases and styles (see http://www.ebi.ac.uk/Tools/webservices/services/dbfetch#fetchdata_query_format_style).
     */
    public java.lang.String[] getSupportedStyles() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.ebi.ac.uk/ws/services/WSDbfetch", "getSupportedStyles"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
