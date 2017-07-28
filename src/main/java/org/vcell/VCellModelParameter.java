package org.vcell;

/**
 * Created by kevingaffney on 7/7/17.
 */
public class VCellModelParameter {

    public static final String CONCENTRATION = "Concentration";
    public static final String DIFFUSION = "Diffusion";
    public static final String REACTION = "Reaction";

    private String id;
    private String value;
    private String unit;
    private boolean shouldScan;
    private String parameterType;

    public VCellModelParameter(String id, String value, String unit, String parameterType) {
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

    public String getParameterType() {
        return parameterType;
    }
    
    public static String[] getAllParameterTypes() {
    	return new String[] {
    			CONCENTRATION,
    			DIFFUSION,
    			REACTION
    	};
    }
    
    @Override
    public String toString() {
    	return id + " (" + unit + ")";
    }
}
