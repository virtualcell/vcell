/*
 * VCell API
 * VCell API
 *
 * The version of the OpenAPI document: 1.0.1
 * Contact: vcell_support@uchc.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.vcell.restclient.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.Arrays;
import org.vcell.restclient.model.HtcJobID;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * SimulationExecutionStatus
 */
@JsonPropertyOrder({
  SimulationExecutionStatus.JSON_PROPERTY_FIELD_START_DATE,
  SimulationExecutionStatus.JSON_PROPERTY_FIELD_LATEST_UPDATE_DATE,
  SimulationExecutionStatus.JSON_PROPERTY_FIELD_END_DATE,
  SimulationExecutionStatus.JSON_PROPERTY_FIELD_COMPUTE_HOST,
  SimulationExecutionStatus.JSON_PROPERTY_FIELD_HAS_DATA,
  SimulationExecutionStatus.JSON_PROPERTY_FIELD_HTC_JOB_I_D,
  SimulationExecutionStatus.JSON_PROPERTY_COMPUTE_HOST,
  SimulationExecutionStatus.JSON_PROPERTY_END_DATE,
  SimulationExecutionStatus.JSON_PROPERTY_LATEST_UPDATE_DATE,
  SimulationExecutionStatus.JSON_PROPERTY_START_DATE,
  SimulationExecutionStatus.JSON_PROPERTY_HTC_JOB_I_D
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SimulationExecutionStatus {
  public static final String JSON_PROPERTY_FIELD_START_DATE = "fieldStartDate";
  private LocalDate fieldStartDate;

  public static final String JSON_PROPERTY_FIELD_LATEST_UPDATE_DATE = "fieldLatestUpdateDate";
  private LocalDate fieldLatestUpdateDate;

  public static final String JSON_PROPERTY_FIELD_END_DATE = "fieldEndDate";
  private LocalDate fieldEndDate;

  public static final String JSON_PROPERTY_FIELD_COMPUTE_HOST = "fieldComputeHost";
  private String fieldComputeHost;

  public static final String JSON_PROPERTY_FIELD_HAS_DATA = "fieldHasData";
  private Boolean fieldHasData;

  public static final String JSON_PROPERTY_FIELD_HTC_JOB_I_D = "fieldHtcJobID";
  private HtcJobID fieldHtcJobID;

  public static final String JSON_PROPERTY_COMPUTE_HOST = "computeHost";
  private String computeHost;

  public static final String JSON_PROPERTY_END_DATE = "endDate";
  private LocalDate endDate;

  public static final String JSON_PROPERTY_LATEST_UPDATE_DATE = "latestUpdateDate";
  private LocalDate latestUpdateDate;

  public static final String JSON_PROPERTY_START_DATE = "startDate";
  private LocalDate startDate;

  public static final String JSON_PROPERTY_HTC_JOB_I_D = "htcJobID";
  private HtcJobID htcJobID;

  public SimulationExecutionStatus() { 
  }

  public SimulationExecutionStatus fieldStartDate(LocalDate fieldStartDate) {
    this.fieldStartDate = fieldStartDate;
    return this;
  }

   /**
   * Get fieldStartDate
   * @return fieldStartDate
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FIELD_START_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public LocalDate getFieldStartDate() {
    return fieldStartDate;
  }


  @JsonProperty(JSON_PROPERTY_FIELD_START_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFieldStartDate(LocalDate fieldStartDate) {
    this.fieldStartDate = fieldStartDate;
  }


  public SimulationExecutionStatus fieldLatestUpdateDate(LocalDate fieldLatestUpdateDate) {
    this.fieldLatestUpdateDate = fieldLatestUpdateDate;
    return this;
  }

   /**
   * Get fieldLatestUpdateDate
   * @return fieldLatestUpdateDate
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FIELD_LATEST_UPDATE_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public LocalDate getFieldLatestUpdateDate() {
    return fieldLatestUpdateDate;
  }


  @JsonProperty(JSON_PROPERTY_FIELD_LATEST_UPDATE_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFieldLatestUpdateDate(LocalDate fieldLatestUpdateDate) {
    this.fieldLatestUpdateDate = fieldLatestUpdateDate;
  }


  public SimulationExecutionStatus fieldEndDate(LocalDate fieldEndDate) {
    this.fieldEndDate = fieldEndDate;
    return this;
  }

   /**
   * Get fieldEndDate
   * @return fieldEndDate
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FIELD_END_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public LocalDate getFieldEndDate() {
    return fieldEndDate;
  }


  @JsonProperty(JSON_PROPERTY_FIELD_END_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFieldEndDate(LocalDate fieldEndDate) {
    this.fieldEndDate = fieldEndDate;
  }


  public SimulationExecutionStatus fieldComputeHost(String fieldComputeHost) {
    this.fieldComputeHost = fieldComputeHost;
    return this;
  }

   /**
   * Get fieldComputeHost
   * @return fieldComputeHost
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FIELD_COMPUTE_HOST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFieldComputeHost() {
    return fieldComputeHost;
  }


  @JsonProperty(JSON_PROPERTY_FIELD_COMPUTE_HOST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFieldComputeHost(String fieldComputeHost) {
    this.fieldComputeHost = fieldComputeHost;
  }


  public SimulationExecutionStatus fieldHasData(Boolean fieldHasData) {
    this.fieldHasData = fieldHasData;
    return this;
  }

   /**
   * Get fieldHasData
   * @return fieldHasData
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FIELD_HAS_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getFieldHasData() {
    return fieldHasData;
  }


  @JsonProperty(JSON_PROPERTY_FIELD_HAS_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFieldHasData(Boolean fieldHasData) {
    this.fieldHasData = fieldHasData;
  }


  public SimulationExecutionStatus fieldHtcJobID(HtcJobID fieldHtcJobID) {
    this.fieldHtcJobID = fieldHtcJobID;
    return this;
  }

   /**
   * Get fieldHtcJobID
   * @return fieldHtcJobID
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_FIELD_HTC_JOB_I_D)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public HtcJobID getFieldHtcJobID() {
    return fieldHtcJobID;
  }


  @JsonProperty(JSON_PROPERTY_FIELD_HTC_JOB_I_D)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFieldHtcJobID(HtcJobID fieldHtcJobID) {
    this.fieldHtcJobID = fieldHtcJobID;
  }


  public SimulationExecutionStatus computeHost(String computeHost) {
    this.computeHost = computeHost;
    return this;
  }

   /**
   * Get computeHost
   * @return computeHost
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_COMPUTE_HOST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getComputeHost() {
    return computeHost;
  }


  @JsonProperty(JSON_PROPERTY_COMPUTE_HOST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setComputeHost(String computeHost) {
    this.computeHost = computeHost;
  }


  public SimulationExecutionStatus endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

   /**
   * Get endDate
   * @return endDate
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_END_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public LocalDate getEndDate() {
    return endDate;
  }


  @JsonProperty(JSON_PROPERTY_END_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }


  public SimulationExecutionStatus latestUpdateDate(LocalDate latestUpdateDate) {
    this.latestUpdateDate = latestUpdateDate;
    return this;
  }

   /**
   * Get latestUpdateDate
   * @return latestUpdateDate
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_LATEST_UPDATE_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public LocalDate getLatestUpdateDate() {
    return latestUpdateDate;
  }


  @JsonProperty(JSON_PROPERTY_LATEST_UPDATE_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLatestUpdateDate(LocalDate latestUpdateDate) {
    this.latestUpdateDate = latestUpdateDate;
  }


  public SimulationExecutionStatus startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

   /**
   * Get startDate
   * @return startDate
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_START_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public LocalDate getStartDate() {
    return startDate;
  }


  @JsonProperty(JSON_PROPERTY_START_DATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }


  public SimulationExecutionStatus htcJobID(HtcJobID htcJobID) {
    this.htcJobID = htcJobID;
    return this;
  }

   /**
   * Get htcJobID
   * @return htcJobID
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_HTC_JOB_I_D)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public HtcJobID getHtcJobID() {
    return htcJobID;
  }


  @JsonProperty(JSON_PROPERTY_HTC_JOB_I_D)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setHtcJobID(HtcJobID htcJobID) {
    this.htcJobID = htcJobID;
  }


  /**
   * Return true if this SimulationExecutionStatus object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SimulationExecutionStatus simulationExecutionStatus = (SimulationExecutionStatus) o;
    return Objects.equals(this.fieldStartDate, simulationExecutionStatus.fieldStartDate) &&
        Objects.equals(this.fieldLatestUpdateDate, simulationExecutionStatus.fieldLatestUpdateDate) &&
        Objects.equals(this.fieldEndDate, simulationExecutionStatus.fieldEndDate) &&
        Objects.equals(this.fieldComputeHost, simulationExecutionStatus.fieldComputeHost) &&
        Objects.equals(this.fieldHasData, simulationExecutionStatus.fieldHasData) &&
        Objects.equals(this.fieldHtcJobID, simulationExecutionStatus.fieldHtcJobID) &&
        Objects.equals(this.computeHost, simulationExecutionStatus.computeHost) &&
        Objects.equals(this.endDate, simulationExecutionStatus.endDate) &&
        Objects.equals(this.latestUpdateDate, simulationExecutionStatus.latestUpdateDate) &&
        Objects.equals(this.startDate, simulationExecutionStatus.startDate) &&
        Objects.equals(this.htcJobID, simulationExecutionStatus.htcJobID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldStartDate, fieldLatestUpdateDate, fieldEndDate, fieldComputeHost, fieldHasData, fieldHtcJobID, computeHost, endDate, latestUpdateDate, startDate, htcJobID);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SimulationExecutionStatus {\n");
    sb.append("    fieldStartDate: ").append(toIndentedString(fieldStartDate)).append("\n");
    sb.append("    fieldLatestUpdateDate: ").append(toIndentedString(fieldLatestUpdateDate)).append("\n");
    sb.append("    fieldEndDate: ").append(toIndentedString(fieldEndDate)).append("\n");
    sb.append("    fieldComputeHost: ").append(toIndentedString(fieldComputeHost)).append("\n");
    sb.append("    fieldHasData: ").append(toIndentedString(fieldHasData)).append("\n");
    sb.append("    fieldHtcJobID: ").append(toIndentedString(fieldHtcJobID)).append("\n");
    sb.append("    computeHost: ").append(toIndentedString(computeHost)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    latestUpdateDate: ").append(toIndentedString(latestUpdateDate)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    htcJobID: ").append(toIndentedString(htcJobID)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  /**
   * Convert the instance into URL query string.
   *
   * @return URL query string
   */
  public String toUrlQueryString() {
    return toUrlQueryString(null);
  }

  /**
   * Convert the instance into URL query string.
   *
   * @param prefix prefix of the query string
   * @return URL query string
   */
  public String toUrlQueryString(String prefix) {
    String suffix = "";
    String containerSuffix = "";
    String containerPrefix = "";
    if (prefix == null) {
      // style=form, explode=true, e.g. /pet?name=cat&type=manx
      prefix = "";
    } else {
      // deepObject style e.g. /pet?id[name]=cat&id[type]=manx
      prefix = prefix + "[";
      suffix = "]";
      containerSuffix = "]";
      containerPrefix = "[";
    }

    StringJoiner joiner = new StringJoiner("&");

    // add `fieldStartDate` to the URL query string
    if (getFieldStartDate() != null) {
      joiner.add(String.format("%sfieldStartDate%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getFieldStartDate()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `fieldLatestUpdateDate` to the URL query string
    if (getFieldLatestUpdateDate() != null) {
      joiner.add(String.format("%sfieldLatestUpdateDate%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getFieldLatestUpdateDate()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `fieldEndDate` to the URL query string
    if (getFieldEndDate() != null) {
      joiner.add(String.format("%sfieldEndDate%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getFieldEndDate()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `fieldComputeHost` to the URL query string
    if (getFieldComputeHost() != null) {
      joiner.add(String.format("%sfieldComputeHost%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getFieldComputeHost()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `fieldHasData` to the URL query string
    if (getFieldHasData() != null) {
      joiner.add(String.format("%sfieldHasData%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getFieldHasData()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `fieldHtcJobID` to the URL query string
    if (getFieldHtcJobID() != null) {
      joiner.add(getFieldHtcJobID().toUrlQueryString(prefix + "fieldHtcJobID" + suffix));
    }

    // add `computeHost` to the URL query string
    if (getComputeHost() != null) {
      joiner.add(String.format("%scomputeHost%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getComputeHost()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `endDate` to the URL query string
    if (getEndDate() != null) {
      joiner.add(String.format("%sendDate%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getEndDate()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `latestUpdateDate` to the URL query string
    if (getLatestUpdateDate() != null) {
      joiner.add(String.format("%slatestUpdateDate%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getLatestUpdateDate()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `startDate` to the URL query string
    if (getStartDate() != null) {
      joiner.add(String.format("%sstartDate%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getStartDate()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `htcJobID` to the URL query string
    if (getHtcJobID() != null) {
      joiner.add(getHtcJobID().toUrlQueryString(prefix + "htcJobID" + suffix));
    }

    return joiner.toString();
  }
}
