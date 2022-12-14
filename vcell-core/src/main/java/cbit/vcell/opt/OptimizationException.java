/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;

public class OptimizationException extends RuntimeException {
    private double[] parameterValues = null;

    public OptimizationException(String message) {
        super(message);
    }

    public OptimizationException(String message, Exception e) {
        super(message, e);
    }

    public OptimizationException(String message, double[] argParameterValues) {
        super(message);
        this.parameterValues = argParameterValues;
    }

    public OptimizationException(String message, double[] argParameterValues, Exception e) {
        super(message, e);
        this.parameterValues = argParameterValues;
    }

    public String getMessage() {
        StringBuffer buffer = new StringBuffer(super.getMessage());
        if (parameterValues != null) {
            buffer.append(" at parameters = [");
            for (int i = 0; parameterValues != null && i < parameterValues.length; i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(parameterValues[i]);
            }
            buffer.append("]");
        }
        return buffer.toString();
    }

    public double[] getParameterValues() {
        return parameterValues;
    }
}
