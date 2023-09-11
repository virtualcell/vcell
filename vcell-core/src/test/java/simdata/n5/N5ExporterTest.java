package simdata.n5;



/*
    In order to determine whether the exportation keeps the information intact we have to have two identical
    models. Then, when both models are loaded into ImageJ's image controller class, do some analysis on the models and the
    results should be the exact same.

    Can do analysis manually, such as taking VCell data and doing a histogram that way along with other metrics for variable concentration.

    Can manually create image that has very basic model, single blob in the center with some complex metadata, and make that
    be the control.
 */

import cbit.vcell.math.MathException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.simdata.n5.N5Exporter;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.janelia.saalfeldlab.n5.N5FSReader;
import org.janelia.saalfeldlab.n5.N5Reader;
import org.junit.Before;
import org.junit.Test;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class N5ExporterTest {

    private N5Reader n5Reader;
    private VCData controlModel;

    @Before
    public void initalizeVCData() throws IOException, DataAccessException, MathException {

        String n5FilePath = ClassLoader.getSystemClassLoader().getResource("/simdata/n5/N5ExportData").getPath();

        this.controlModel = N5Exporter.getVCData(ClassLoader.getSystemClassLoader().getResource("/simdata/n5").getPath(), "1115478432");
        ArrayList<String> species = new ArrayList<>();
        species.add("IP3_CYT");

        N5Exporter.exportToN5(this.controlModel, ClassLoader.getSystemClassLoader().getResource(n5FilePath).getPath(), species);
        this.n5Reader = new N5FSReader(n5FilePath);
    }


    @Test
    public void testDimensions() throws MathException, DataAccessException {
        //X, Y, T, Z, Channels
        int[] dimensions = {controlModel.getMesh().getSizeX(), controlModel.getMesh().getSizeY(), controlModel.getDataTimes().length, controlModel.getMesh().getSizeZ()};

    }

    @Test
    public void testUnits(){
        //Have the same units and means for measuring distance, range of 10 at point x,y,z should equal the same if units are appropriate

    }

    @Test
    public void testHistogram() throws IOException, DataAccessException {
        // Is the histogram over entire slice of an image result in the same for both parties
        // is it the same for some random region of space in the slice for the intensities should be the same

        // Try to get the block data all at once, load dataset into memory

        Double[] doubles;
        N5FSReader n5Reader;





        controlModel.getVarAndFunctionDataIdentifiers(new OutputContext(new AnnotatedFunction[0]));

    }


}
