package org.vcell.cli.simstatus;

import java.util.HashMap;

public class StatusYaml {
    HashMap<String, SedDocument> sedDocuments;
    String status;
}

// Sample STATUS YML
/*
sedDocuments:
  BIOMD0000000912_sim.sedml:
    outputs:
      BIOMD0000000912_sim:
        dataSets:
          data_set_E: SKIPPED
          data_set_I: PASSED
          data_set_T: SKIPPED
          data_set_time: SKIPPED
        status: SKIPPED
      plot_1:
        curves:
          plot_1_E_time: SKIPPED
          plot_1_I_time: SKIPPED
          plot_1_T_time: SKIPPED
        status: SKIPPED
    status: SUCCEEDED
    tasks:
      BIOMD0000000912_sim:
        status: SKIPPED
status: SUCCEEDED
* */
