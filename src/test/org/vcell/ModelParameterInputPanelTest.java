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
        parameters.add(new VCellModelParameter("id1", null, "unit1", VCellModelParameter.DIFFUSION_CONSTANT));
        parameters.add(new VCellModelParameter("id2", null, "unit-2", VCellModelParameter.CONCENTRATION));
        ModelParameterInputPanel panel = new ModelParameterInputPanel(parameters);
        JOptionPane.showConfirmDialog(
                null,
                panel,
                "Test",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
    }

    @Test
    public void generateHtml() throws Exception {
        ModelParameterInputPanel panel = new ModelParameterInputPanel(new ArrayList<>());
        String html = panel.generateHtml("Binding (molecules.µm2.s-1)");
        assertEquals("<html>Binding (molecules.µm<sup>2</sup>.s<sup>-1</sup>)</html>", html);
    }
}