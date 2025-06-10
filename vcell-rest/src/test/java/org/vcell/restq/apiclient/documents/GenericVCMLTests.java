package org.vcell.restq.apiclient.documents;

import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.jupiter.api.Assertions;
import org.vcell.restclient.ApiException;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.Versionable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

public class GenericVCMLTests {

    public record TestSaveObject(
            String modelString,
            String name,
            ArrayList<String> simNames
    ){ }

    public record ModelsToClean(
            VCDocument model1,
            VCDocument model2
    ){ }

    @FunctionalInterface
    protected interface ApiCall <I, R> {
        public R call(I input) throws ApiException;
    }

    @FunctionalInterface
    protected interface XMLCall <I, R> {
        public R apply(I input) throws XmlParseException;
    }

    @FunctionalInterface
    protected interface CleanModel{
        public void clean(ModelsToClean model) throws Exception;
    }

    @FunctionalInterface
    protected interface DeleteModel {
        public void call(String model) throws Exception;
    }

    public static void genericTestSave(String testModelString,
                                       XMLCall<XMLSource, VCDocument> xmlToDocument,
                                       XMLCall<VCDocument, String> documentToXML,
                                       ApiCall<TestSaveObject, String> saveCall,
                                       CleanModel cleanModels) throws Exception {

        String savedModelVCML = saveCall.call(new TestSaveObject(testModelString, "TestModel", null));
        VCDocument savedModel = xmlToDocument.apply(new XMLSource(savedModelVCML));
        // Can't resave without changes
        try{
            saveCall.call(new TestSaveObject(testModelString, "TestModel", null));
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(500, e.getCode());
            Assertions.assertTrue(e.getMessage().contains("already has"));
        }
        savedModel.setDescription("Changed description");
        savedModelVCML = saveCall.call(new TestSaveObject(documentToXML.apply(savedModel), null, null));
        savedModel = xmlToDocument.apply(new XMLSource(savedModelVCML));

        VCDocument testModel = xmlToDocument.apply(new XMLSource(testModelString));
        cleanModels.clean(new ModelsToClean(savedModel, testModel));
        Assertions.assertTrue(savedModel.compareEqual(testModel));

        // Can't save new model under the same name if it does not have the same versionable object as the original.
        if (savedModel instanceof Versionable){
            ((Versionable) savedModel).clearVersion();
        } else {
            Assertions.fail();
        }
        try{
            saveCall.call(new TestSaveObject(documentToXML.apply(savedModel), "TestModel", null));
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(500, e.getCode());
        }
    }

    public static void genericTestGet(String testVCML, XMLCall<XMLSource, VCDocument> xmlToDocument,
                               ApiCall<String, String> getVCML,
                               ApiCall<String, ?> getSummaryCall,
                               ApiCall<String, String> notOwnerGetVCML,
                               ApiCall<String, ?> notOwnerGetSummaryCall,
                               ApiCall<TestSaveObject, String> saveCall) throws Exception {
        try {
            getSummaryCall.call("44");
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }
        try {
            getVCML.call("44");
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }

        String savedModelVCML = saveCall.call(new TestSaveObject(testVCML, "Test", null));
        VCDocument savedModel = xmlToDocument.apply(new XMLSource(savedModelVCML));
        String retrievedVCML = getVCML.call(savedModel.getVersion().getVersionKey().toString());
        Assertions.assertEquals(savedModelVCML, retrievedVCML);

        try{
            notOwnerGetVCML.call(savedModel.getVersion().getVersionKey().toString());
            Assertions.fail();
        } catch (ApiException e) {
            // Not found for that particular user because they don't own it
            Assertions.assertEquals(404, e.getCode());
        }
        try{
            notOwnerGetSummaryCall.call(savedModel.getVersion().getVersionKey().toString());
            Assertions.fail();
        } catch (ApiException e) {
            // Not found for that particular user because they don't own it
            Assertions.assertEquals(404, e.getCode());
        }
    }

    public static void genericTestDelete(String testVCML, ApiCall<TestSaveObject, String> saveCall,
                                  XMLCall<XMLSource, VCDocument> xmlToDocument,
                                  DeleteModel delete, DeleteModel notOwnerDelete, ApiCall<String, String> getVCML) throws Exception {

        String savedModelVCML = saveCall.call(new TestSaveObject(testVCML, "Test", null));
        String saved2 = saveCall.call(new TestSaveObject(testVCML, "Test2", null));

        VCDocument savedModel = xmlToDocument.apply(new XMLSource(savedModelVCML));
        VCDocument savedModel2 = xmlToDocument.apply(new XMLSource(saved2));

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
            getVCML.call(savedModel.getVersion().getVersionKey().toString());
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }

        // Wasn't deleted so can get
        Assertions.assertDoesNotThrow(() -> getVCML.call(savedModel2.getVersion().getVersionKey().toString()));
    }

}
