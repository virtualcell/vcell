package org.vcell.cli.run.hdf5;

import java.util.ArrayList;
import java.util.List;

/**
 * Struct-class for holding dataset meta-data
 */
public class Hdf5SedmlMetadata {
    // comments are example values
    public String _type; // SedPlot2D
    public List<String> sedmlDataSetDataTypes = new ArrayList<>(); // float64, float64, float64
    public List<String> sedmlDataSetIds = new ArrayList<>(); // dataGen_tsk_0_0_s0, dataGen_tsk_0_0_s1, time_tsk_0_0
    public List<String> sedmlDataSetLabels = new ArrayList<>(); // dataGen_tsk_0_0_s0, dataGen_tsk_0_0_s1, time_tsk_0_0
    public List<String> sedmlDataSetNames = new ArrayList<>(); // dataGen_tsk_0_0_s0, dataGen_tsk_0_0_s1, time_tsk_0_0
    public List<String> sedmlDataSetShapes = new ArrayList<>(); // 21, 21, 21
    public String sedmlId; // plot2d_simple
    public String sedmlName; // Application0_simple_plot
    public String uri; // ___0_export_NO_scan_test.sedml/plot2d_simple
}