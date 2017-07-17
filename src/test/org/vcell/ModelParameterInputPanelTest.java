package org.vcell;

import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by kevingaffney on 7/11/17.
 */
public class ModelParameterInputPanelTest {

    @Test
    public void constructor() throws Exception {
        ArrayList<VCellModelParameter> parameters = new ArrayList<>();
        parameters.add(new VCellModelParameter("A_diff", null, "µm2.s-1", VCellModelParameter.DIFFUSION_CONSTANT));
        parameters.add(new VCellModelParameter("A_conc", null, "µM", VCellModelParameter.CONCENTRATION));
        parameters.add(new VCellModelParameter("B_diff", null, "µm2.s-1", VCellModelParameter.DIFFUSION_CONSTANT));
        parameters.add(new VCellModelParameter("B_conc", null, "µM", VCellModelParameter.CONCENTRATION));
        ModelParameterInputPanel panel = new ModelParameterInputPanel(parameters);
        JOptionPane.showConfirmDialog(
                null,
                panel,
                "Input Parameters",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
    }
}