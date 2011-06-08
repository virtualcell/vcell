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
