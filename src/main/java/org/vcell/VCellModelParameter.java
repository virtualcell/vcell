package org.vcell;

/**
 * Created by kevingaffney on 7/7/17.
 */
public class VCellModelParameter {

    public static final int CONCENTRATION = 0;
    public static final int DIFFUSION_CONSTANT = 1;
    public static final int REACTION_RATE = 2;

    private String id;
    private String value;
    private String unit;
    private int parameterType;

    public VCellModelParameter(String id, String value, String unit, int parameterType) {
        this.id = id;
        this.value = value;
        this.unit = unit;
        this.parameterType = parameterType;
    }

    public String getId() {
        return id;
    }

    public String getUnit() {
        return unit;
    }

    public int getParameterType() {
        return parameterType;
    }
}
