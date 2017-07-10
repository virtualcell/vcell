package org.vcell;

import java.util.ArrayList;

/**
 * Created by kevingaffney on 7/7/17.
 */
public class VCellModel {

    public final static int TIRF = 0;

    private String name;
    private ArrayList<VCellModelParameter> parameters;

    public String getName() {
        return name;
    }
}
