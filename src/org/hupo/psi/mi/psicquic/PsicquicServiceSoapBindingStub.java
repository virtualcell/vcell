/**
 * PsicquicServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hupo.psi.mi.psicquic;

public class PsicquicServiceSoapBindingStub extends org.apache.axis.client.Stub implements org.hupo.psi.mi.psicquic.PsicquicService {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[8];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVersion");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getByInteractor");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "dbRef"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "dbRef"), org.hupo.psi.mi.psicquic.DbRef.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "infoRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "requestInfo"), org.hupo.psi.mi.psicquic.RequestInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setReturnClass(org.hupo.psi.mi.psicquic.QueryResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getByQuery");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "query"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "infoRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "requestInfo"), org.hupo.psi.mi.psicquic.RequestInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setReturnClass(org.hupo.psi.mi.psicquic.QueryResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getByInteraction");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "dbRef"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "dbRef"), org.hupo.psi.mi.psicquic.DbRef.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "infoRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "requestInfo"), org.hupo.psi.mi.psicquic.RequestInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setReturnClass(org.hupo.psi.mi.psicquic.QueryResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getByInteractionList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "dbRef"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "dbRef"), org.hupo.psi.mi.psicquic.DbRef[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "infoRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "requestInfo"), org.hupo.psi.mi.psicquic.RequestInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setReturnClass(org.hupo.psi.mi.psicquic.QueryResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSupportedDbAcs");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSupportedReturnTypes");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getByInteractorList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "dbRef"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "dbRef"), org.hupo.psi.mi.psicquic.DbRef[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "infoRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "requestInfo"), org.hupo.psi.mi.psicquic.RequestInfo.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "operand"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setReturnClass(org.hupo.psi.mi.psicquic.QueryResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "psicquicFault"),
                      "org.hupo.psi.mi.psicquic.PsicquicFault",
                      new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault"), 
                      true
                     ));
        _operations[7] = oper;

    }

    public PsicquicServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public PsicquicServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public PsicquicServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", ">feature>featureRangeList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.BaseLocation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "baseLocation");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "featureRange");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", ">interactor>organism");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.InteractorOrganism.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "alias");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Alias.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attribute");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Attribute.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attributeList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Attribute[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attribute");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "attribute");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availability");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Availability.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availabilityList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Availability[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availability");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "availability");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "baseLocation");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.BaseLocation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "bibref");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Bibref.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "bioSource");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.BioSource.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidence");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Confidence.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidenceBase");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ConfidenceBase.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidenceList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Confidence[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidence");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "confidence");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "cvType");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.CvType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "dbReference");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.DbReference.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "entry");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Entry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "entrySet");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Entry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "entry");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "entry");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalInteractor");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ExperimentalInteractor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalInteractorList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ExperimentalInteractor[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalInteractor");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalInteractor");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalPreparation");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ExperimentalPreparation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalPreparationList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ExperimentalPreparation[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalPreparation");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalPreparation");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalRole");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ExperimentalRole.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalRoleList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ExperimentalRole[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalRole");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentalRole");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentDescription");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ExperimentDescription.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentDescriptionList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ExperimentDescription[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentDescription");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentDescription");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ExperimentList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentRefList");
            cachedSerQNames.add(qName);
            cls = int[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "experimentRef");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "feature");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Feature.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "featureList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Feature[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "feature");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "feature");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganism");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.HostOrganism.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganismList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.HostOrganism[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganism");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "hostOrganism");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteraction");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.InferredInteraction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteractionList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.InferredInteraction[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteraction");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteraction");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "inferredInteractionParticipant");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.InferredInteractionParticipant.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interaction");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Interaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactionList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Interaction[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interaction");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interaction");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactor");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Interactor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactorList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Interactor[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactor");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interactor");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "interval");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Interval.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "names");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Names.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "openCvType");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.OpenCvType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameter");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Parameter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameterBase");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ParameterBase.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameterList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Parameter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameter");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "parameter");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participant");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Participant.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantIdentificationMethod");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ParticipantIdentificationMethod.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantIdentificationMethodList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.ParticipantIdentificationMethod[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantIdentificationMethod");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantIdentificationMethod");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participantList");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Participant[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participant");
            qName2 = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "participant");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "position");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Position.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "source");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Source.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/mif", "xref");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.mif.Xref.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", ">psicquicFault");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.psicquic.PsicquicFault.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "dbRef");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.psicquic.DbRef.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "queryResponse");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.psicquic.QueryResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "requestInfo");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.psicquic.RequestInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "resultInfo");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.psicquic.ResultInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "resultSet");
            cachedSerQNames.add(qName);
            cls = org.hupo.psi.mi.psicquic.ResultSet.class;
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
                    _call.setEncodingStyle(null);
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

    public java.lang.String getVersion() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "getVersion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

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
  throw axisFaultException;
}
    }

    public org.hupo.psi.mi.psicquic.QueryResponse getByInteractor(org.hupo.psi.mi.psicquic.DbRef dbRef, org.hupo.psi.mi.psicquic.RequestInfo infoRequest) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getByInteractor");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "getByInteractor"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {dbRef, infoRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.hupo.psi.mi.psicquic.QueryResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.hupo.psi.mi.psicquic.QueryResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.hupo.psi.mi.psicquic.QueryResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.hupo.psi.mi.psicquic.QueryResponse getByQuery(java.lang.String query, org.hupo.psi.mi.psicquic.RequestInfo infoRequest) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getByQuery");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "getByQuery"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {query, infoRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.hupo.psi.mi.psicquic.QueryResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.hupo.psi.mi.psicquic.QueryResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.hupo.psi.mi.psicquic.QueryResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.hupo.psi.mi.psicquic.QueryResponse getByInteraction(org.hupo.psi.mi.psicquic.DbRef dbRef, org.hupo.psi.mi.psicquic.RequestInfo infoRequest) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getByInteraction");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "getByInteraction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {dbRef, infoRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.hupo.psi.mi.psicquic.QueryResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.hupo.psi.mi.psicquic.QueryResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.hupo.psi.mi.psicquic.QueryResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.hupo.psi.mi.psicquic.QueryResponse getByInteractionList(org.hupo.psi.mi.psicquic.DbRef[] dbRef, org.hupo.psi.mi.psicquic.RequestInfo infoRequest) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getByInteractionList");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "getByInteractionList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {dbRef, infoRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.hupo.psi.mi.psicquic.QueryResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.hupo.psi.mi.psicquic.QueryResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.hupo.psi.mi.psicquic.QueryResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] getSupportedDbAcs() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "getSupportedDbAcs"));

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

    public java.lang.String[] getSupportedReturnTypes() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "getSupportedReturnTypes"));

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

    public org.hupo.psi.mi.psicquic.QueryResponse getByInteractorList(org.hupo.psi.mi.psicquic.DbRef[] dbRef, org.hupo.psi.mi.psicquic.RequestInfo infoRequest, java.lang.String operand) throws java.rmi.RemoteException, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault, org.hupo.psi.mi.psicquic.PsicquicFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://psi.hupo.org/mi/psicquic", "getByInteractorList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {dbRef, infoRequest, operand});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.hupo.psi.mi.psicquic.QueryResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.hupo.psi.mi.psicquic.QueryResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.hupo.psi.mi.psicquic.QueryResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.hupo.psi.mi.psicquic.PsicquicFault) {
              throw (org.hupo.psi.mi.psicquic.PsicquicFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
