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
 * MiriamProviderService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.www.miriamws.main.MiriamWebServices;

public interface MiriamProviderService extends javax.xml.rpc.Service {
    public java.lang.String getMiriamWebServicesAddress();

    public uk.ac.ebi.www.miriamws.main.MiriamWebServices.MiriamProvider getMiriamWebServices() throws javax.xml.rpc.ServiceException;

    public uk.ac.ebi.www.miriamws.main.MiriamWebServices.MiriamProvider getMiriamWebServices(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
