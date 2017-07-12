package org.vcell;

import org.vcell.*;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * Created by kevingaffney on 7/7/17.
 */
class VCellModelServiceTest {

    @Test
    void exportSBML() throws IOException, XMLStreamException {
        VCellModel model = new VCellModel("model_test");
        File file = new File("/Users/kevingaffney/Desktop/sbml-test.xml");
        VCellModelService service = new VCellModelService();
        service.writeSBMLToFile(model, file);
    }
}