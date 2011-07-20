/**
 * PsicquicService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.psicquic;

public interface PsicquicService extends java.rmi.Remote {
    public java.lang.String getVersion() throws java.rmi.RemoteException;
    public org.hupo.psi.mi.psicquic.QueryResponse getByInteractor(org.hupo.psi.mi.psicquic.DbRef dbRef, org.hupo.psi.mi.psicquic.RequestInfo infoRequest) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault;
    public org.hupo.psi.mi.psicquic.QueryResponse getByQuery(java.lang.String query, org.hupo.psi.mi.psicquic.RequestInfo infoRequest) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault;
    public org.hupo.psi.mi.psicquic.QueryResponse getByInteraction(org.hupo.psi.mi.psicquic.DbRef dbRef, org.hupo.psi.mi.psicquic.RequestInfo infoRequest) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault;
    public org.hupo.psi.mi.psicquic.QueryResponse getByInteractionList(org.hupo.psi.mi.psicquic.DbRef[] dbRef, org.hupo.psi.mi.psicquic.RequestInfo infoRequest) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault;
    public java.lang.String[] getSupportedDbAcs() throws java.rmi.RemoteException;
    public java.lang.String[] getSupportedReturnTypes() throws java.rmi.RemoteException;
    public org.hupo.psi.mi.psicquic.QueryResponse getByInteractorList(org.hupo.psi.mi.psicquic.DbRef[] dbRef, org.hupo.psi.mi.psicquic.RequestInfo infoRequest, java.lang.String operand) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault;
}
