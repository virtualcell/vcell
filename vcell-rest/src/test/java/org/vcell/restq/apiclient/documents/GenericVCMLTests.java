package org.vcell.restq.apiclient.documents;

import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlParseException;
import org.junit.jupiter.api.Assertions;
import org.vcell.restclient.ApiException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.Versionable;

import java.util.ArrayList;

class GenericVCMLTests {

    @FunctionalInterface
    protected interface ApiCall <I, R> {
        public R call(I input) throws ApiException;
    }

    @FunctionalInterface
    protected interface SaveCall{
        public String call(String modelString, String modelName, ArrayList<String> simNames) throws ApiException;
    }

    @FunctionalInterface
    protected interface XMLCall <I, R> {
        public R apply(I input) throws XmlParseException;
    }

    @FunctionalInterface
    protected interface CleanModel <I extends VCDocument>{
        public void applyClean(I model1, I model2) throws Exception;
    }

    @FunctionalInterface
    protected interface DeleteModel {
        public void call(String model) throws Exception;
    }

    public static <DocumentType extends VCDocument> void genericSaveTest(String testModelString,
                             XMLCall<XMLSource, DocumentType> xmlToDocument,
                             XMLCall<DocumentType, String> documentToXML,
                             SaveCall saveCall,
                             CleanModel<DocumentType> cleanModels) throws Exception {

        String savedModelVCML = saveCall.call(testModelString, "TestModel", null);
        DocumentType savedModel = xmlToDocument.apply(new XMLSource(savedModelVCML));
        // Can't resave without changes
        try{
            saveCall.call(testModelString, "TestModel", null);
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(500, e.getCode());
            Assertions.assertTrue(e.getMessage().contains("already has"));
        }
        savedModel.setDescription("Changed description");
        savedModelVCML = saveCall.call(documentToXML.apply(savedModel), null, null);
        savedModel = xmlToDocument.apply(new XMLSource(savedModelVCML));

        DocumentType testModel = xmlToDocument.apply(new XMLSource(testModelString));
        cleanModels.applyClean(savedModel, testModel);
        Assertions.assertTrue(savedModel.compareEqual(testModel));

        // Can't save new model under the same name if it does not have the same versionable object as the original.
        if (savedModel instanceof Versionable){
            ((Versionable) savedModel).clearVersion();
        } else {
            Assertions.fail();
        }
        try{
            saveCall.call(documentToXML.apply(savedModel), "TestModel", null);
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(500, e.getCode());
        }
    }

    public static <DocumentType extends VCDocument, SummaryObject> void genericGetTest(String testVCML,
                                   XMLCall<XMLSource, DocumentType> xmlToDocument,
                                   ApiCall<KeyValue, String> getVCML,
                                   ApiCall<KeyValue, SummaryObject> getSummaryCall,
                                   ApiCall<KeyValue, String> notOwnerGetVCML,
                                   ApiCall<KeyValue, SummaryObject> notOwnerGetSummaryCall,
                                   SaveCall saveCall) throws Exception {
        try {
            getSummaryCall.call(new KeyValue("44"));
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }
        try {
            getVCML.call(new KeyValue("44"));
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }

        String savedModelVCML = saveCall.call(testVCML, "Test", null);
        VCDocument savedModel = xmlToDocument.apply(new XMLSource(savedModelVCML));
        String retrievedVCML = getVCML.call(savedModel.getVersion().getVersionKey());
        Assertions.assertEquals(savedModelVCML, retrievedVCML);

        try{
            notOwnerGetVCML.call(savedModel.getVersion().getVersionKey());
            Assertions.fail();
        } catch (ApiException e) {
            // Not found for that particular user because they don't own it
            Assertions.assertEquals(404, e.getCode());
        }
        try{
            notOwnerGetSummaryCall.call(savedModel.getVersion().getVersionKey());
            Assertions.fail();
        } catch (ApiException e) {
            // Not found for that particular user because they don't own it
            Assertions.assertEquals(404, e.getCode());
        }
    }

    public static <DocumentType extends VCDocument> void genericDeleteTest(String testVCML, SaveCall saveCall,
                           XMLCall<XMLSource, DocumentType> xmlToDocument,
                           DeleteModel delete, DeleteModel notOwnerDelete, ApiCall<KeyValue, String> getVCML) throws Exception {

        String savedModelVCML = saveCall.call(testVCML, "Test", null);
        String saved2 = saveCall.call(testVCML, "Test2", null);

        DocumentType savedModel = xmlToDocument.apply(new XMLSource(savedModelVCML));
        DocumentType savedModel2 = xmlToDocument.apply(new XMLSource(saved2));

        // only owners should be able to delete
        try{
            notOwnerDelete.call(savedModel.getVersion().getVersionKey().toString());
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }

        delete.call(savedModel.getVersion().getVersionKey().toString());
        // Can't delete twice
        Assertions.assertThrows(ApiException.class, () -> delete.call(savedModel.getVersion().getVersionKey().toString()));
        try{
            // Can't grab what's not deleted
            getVCML.call(savedModel.getVersion().getVersionKey());
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }

        // Wasn't deleted so can get
        Assertions.assertDoesNotThrow(() -> getVCML.call(savedModel2.getVersion().getVersionKey()));
    }

}
