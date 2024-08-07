// Code generated by jtd-codegen for Java + Jackson v0.2.1

package org.vcell.optimization.jtd;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class OptProgressItem {
    @JsonProperty("numFunctionEvaluations")
    private Integer numFunctionEvaluations;

    @JsonProperty("objFuncValue")
    private Double objFuncValue;

    public OptProgressItem() {
    }

    /**
     * Getter for numFunctionEvaluations.<p>
     */
    public Integer getNumFunctionEvaluations() {
        return numFunctionEvaluations;
    }

    /**
     * Setter for numFunctionEvaluations.<p>
     */
    public void setNumFunctionEvaluations(Integer numFunctionEvaluations) {
        this.numFunctionEvaluations = numFunctionEvaluations;
    }

    /**
     * Getter for objFuncValue.<p>
     */
    public Double getObjFuncValue() {
        return objFuncValue;
    }

    /**
     * Setter for objFuncValue.<p>
     */
    public void setObjFuncValue(Double objFuncValue) {
        this.objFuncValue = objFuncValue;
    }
}
