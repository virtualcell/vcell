// Code generated by jtd-codegen for Java + Jackson v0.2.1

package org.vcell.optimization.jtd;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;

@JsonSerialize
public class OptProblem {
    @JsonProperty("copasiOptimizationMethod")
    private CopasiOptimizationMethod copasiOptimizationMethod;

    @JsonProperty("dataSet")
    private List<List<Double>> dataSet;

    @JsonProperty("mathModelSbmlContents")
    private String mathModelSbmlContents;

    @JsonProperty("numberOfOptimizationRuns")
    private Integer numberOfOptimizationRuns;

    @JsonProperty("parameterDescriptionList")
    private List<ParameterDescription> parameterDescriptionList;

    @JsonProperty("referenceVariable")
    private List<ReferenceVariable> referenceVariable;

    public OptProblem() {
    }

    /**
     * Getter for copasiOptimizationMethod.<p>
     */
    public CopasiOptimizationMethod getCopasiOptimizationMethod() {
        return copasiOptimizationMethod;
    }

    /**
     * Setter for copasiOptimizationMethod.<p>
     */
    public void setCopasiOptimizationMethod(CopasiOptimizationMethod copasiOptimizationMethod) {
        this.copasiOptimizationMethod = copasiOptimizationMethod;
    }

    /**
     * Getter for dataSet.<p>
     */
    public List<List<Double>> getDataSet() {
        return dataSet;
    }

    /**
     * Setter for dataSet.<p>
     */
    public void setDataSet(List<List<Double>> dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Getter for mathModelSbmlContents.<p>
     */
    public String getMathModelSbmlContents() {
        return mathModelSbmlContents;
    }

    /**
     * Setter for mathModelSbmlContents.<p>
     */
    public void setMathModelSbmlContents(String mathModelSbmlContents) {
        this.mathModelSbmlContents = mathModelSbmlContents;
    }

    /**
     * Getter for numberOfOptimizationRuns.<p>
     */
    public Integer getNumberOfOptimizationRuns() {
        return numberOfOptimizationRuns;
    }

    /**
     * Setter for numberOfOptimizationRuns.<p>
     */
    public void setNumberOfOptimizationRuns(Integer numberOfOptimizationRuns) {
        this.numberOfOptimizationRuns = numberOfOptimizationRuns;
    }

    /**
     * Getter for parameterDescriptionList.<p>
     */
    public List<ParameterDescription> getParameterDescriptionList() {
        return parameterDescriptionList;
    }

    /**
     * Setter for parameterDescriptionList.<p>
     */
    public void setParameterDescriptionList(List<ParameterDescription> parameterDescriptionList) {
        this.parameterDescriptionList = parameterDescriptionList;
    }

    /**
     * Getter for referenceVariable.<p>
     */
    public List<ReferenceVariable> getReferenceVariable() {
        return referenceVariable;
    }

    /**
     * Setter for referenceVariable.<p>
     */
    public void setReferenceVariable(List<ReferenceVariable> referenceVariable) {
        this.referenceVariable = referenceVariable;
    }
}