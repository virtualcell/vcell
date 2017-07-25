package org.vcell;

import java.util.List;

import net.imagej.Dataset;

public class Datasets {
	
	public static boolean areSameSize(Dataset[] datasets, int... dimensions) {
    	
    	if (datasets.length < 2) {
    		return true;
    	}
    	
    	for (int i = 1; i < datasets.length; i++) {
    		
    		Dataset dataset = datasets[i];
    		if (dataset.numDimensions() != datasets[0].numDimensions()) {
    			return false;
    		}
    		
        	for (int d : dimensions) {
        		if (dataset.dimension(d) != datasets[0].dimension(d)) {
        			return false;
        		}
        	}
    	}
    	
    	return true;
    }

}
