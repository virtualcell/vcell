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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.vcell.restclient.model.Extent;
import org.vcell.restclient.model.ISize;
import org.vcell.restclient.model.Origin;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * AnalyzedFile
 */
@JsonPropertyOrder({
  AnalyzedFile.JSON_PROPERTY_SHORT_SPEC_DATA,
  AnalyzedFile.JSON_PROPERTY_VAR_NAMES,
  AnalyzedFile.JSON_PROPERTY_TIMES,
  AnalyzedFile.JSON_PROPERTY_ORIGIN,
  AnalyzedFile.JSON_PROPERTY_EXTENT,
  AnalyzedFile.JSON_PROPERTY_ISIZE,
  AnalyzedFile.JSON_PROPERTY_ANNOTATION,
  AnalyzedFile.JSON_PROPERTY_NAME
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class AnalyzedFile {
  public static final String JSON_PROPERTY_SHORT_SPEC_DATA = "shortSpecData";
  private List<List<List<Integer>>> shortSpecData;

  public static final String JSON_PROPERTY_VAR_NAMES = "varNames";
  private List<String> varNames;

  public static final String JSON_PROPERTY_TIMES = "times";
  private List<Double> times;

  public static final String JSON_PROPERTY_ORIGIN = "origin";
  private Origin origin;

  public static final String JSON_PROPERTY_EXTENT = "extent";
  private Extent extent;

  public static final String JSON_PROPERTY_ISIZE = "isize";
  private ISize isize;

  public static final String JSON_PROPERTY_ANNOTATION = "annotation";
  private String annotation;

  public static final String JSON_PROPERTY_NAME = "name";
  private String name;

  public AnalyzedFile() { 
  }

  public AnalyzedFile shortSpecData(List<List<List<Integer>>> shortSpecData) {
    this.shortSpecData = shortSpecData;
    return this;
  }

  public AnalyzedFile addShortSpecDataItem(List<List<Integer>> shortSpecDataItem) {
    if (this.shortSpecData == null) {
      this.shortSpecData = new ArrayList<>();
    }
    this.shortSpecData.add(shortSpecDataItem);
    return this;
  }

   /**
   * Get shortSpecData
   * @return shortSpecData
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SHORT_SPEC_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<List<List<Integer>>> getShortSpecData() {
    return shortSpecData;
  }


  @JsonProperty(JSON_PROPERTY_SHORT_SPEC_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setShortSpecData(List<List<List<Integer>>> shortSpecData) {
    this.shortSpecData = shortSpecData;
  }


  public AnalyzedFile varNames(List<String> varNames) {
    this.varNames = varNames;
    return this;
  }

  public AnalyzedFile addVarNamesItem(String varNamesItem) {
    if (this.varNames == null) {
      this.varNames = new ArrayList<>();
    }
    this.varNames.add(varNamesItem);
    return this;
  }

   /**
   * Get varNames
   * @return varNames
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_VAR_NAMES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getVarNames() {
    return varNames;
  }


  @JsonProperty(JSON_PROPERTY_VAR_NAMES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setVarNames(List<String> varNames) {
    this.varNames = varNames;
  }


  public AnalyzedFile times(List<Double> times) {
    this.times = times;
    return this;
  }

  public AnalyzedFile addTimesItem(Double timesItem) {
    if (this.times == null) {
      this.times = new ArrayList<>();
    }
    this.times.add(timesItem);
    return this;
  }

   /**
   * Get times
   * @return times
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_TIMES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<Double> getTimes() {
    return times;
  }


  @JsonProperty(JSON_PROPERTY_TIMES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTimes(List<Double> times) {
    this.times = times;
  }


  public AnalyzedFile origin(Origin origin) {
    this.origin = origin;
    return this;
  }

   /**
   * Get origin
   * @return origin
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ORIGIN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Origin getOrigin() {
    return origin;
  }


  @JsonProperty(JSON_PROPERTY_ORIGIN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOrigin(Origin origin) {
    this.origin = origin;
  }


  public AnalyzedFile extent(Extent extent) {
    this.extent = extent;
    return this;
  }

   /**
   * Get extent
   * @return extent
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_EXTENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Extent getExtent() {
    return extent;
  }


  @JsonProperty(JSON_PROPERTY_EXTENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExtent(Extent extent) {
    this.extent = extent;
  }


  public AnalyzedFile isize(ISize isize) {
    this.isize = isize;
    return this;
  }

   /**
   * Get isize
   * @return isize
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ISIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ISize getIsize() {
    return isize;
  }


  @JsonProperty(JSON_PROPERTY_ISIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIsize(ISize isize) {
    this.isize = isize;
  }


  public AnalyzedFile annotation(String annotation) {
    this.annotation = annotation;
    return this;
  }

   /**
   * Get annotation
   * @return annotation
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ANNOTATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getAnnotation() {
    return annotation;
  }


  @JsonProperty(JSON_PROPERTY_ANNOTATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAnnotation(String annotation) {
    this.annotation = annotation;
  }


  public AnalyzedFile name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getName() {
    return name;
  }


  @JsonProperty(JSON_PROPERTY_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setName(String name) {
    this.name = name;
  }


  /**
   * Return true if this AnalyzedFile object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnalyzedFile analyzedFile = (AnalyzedFile) o;
    return Objects.equals(this.shortSpecData, analyzedFile.shortSpecData) &&
        Objects.equals(this.varNames, analyzedFile.varNames) &&
        Objects.equals(this.times, analyzedFile.times) &&
        Objects.equals(this.origin, analyzedFile.origin) &&
        Objects.equals(this.extent, analyzedFile.extent) &&
        Objects.equals(this.isize, analyzedFile.isize) &&
        Objects.equals(this.annotation, analyzedFile.annotation) &&
        Objects.equals(this.name, analyzedFile.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shortSpecData, varNames, times, origin, extent, isize, annotation, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnalyzedFile {\n");
    sb.append("    shortSpecData: ").append(toIndentedString(shortSpecData)).append("\n");
    sb.append("    varNames: ").append(toIndentedString(varNames)).append("\n");
    sb.append("    times: ").append(toIndentedString(times)).append("\n");
    sb.append("    origin: ").append(toIndentedString(origin)).append("\n");
    sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
    sb.append("    isize: ").append(toIndentedString(isize)).append("\n");
    sb.append("    annotation: ").append(toIndentedString(annotation)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

    // add `shortSpecData` to the URL query string
    if (getShortSpecData() != null) {
      for (int i = 0; i < getShortSpecData().size(); i++) {
        if (getShortSpecData().get(i) != null) {
          joiner.add(String.format("%sshortSpecData%s%s=%s", prefix, suffix,
              "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix),
              URLEncoder.encode(String.valueOf(getShortSpecData().get(i)), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
        }
      }
    }

    // add `varNames` to the URL query string
    if (getVarNames() != null) {
      for (int i = 0; i < getVarNames().size(); i++) {
        joiner.add(String.format("%svarNames%s%s=%s", prefix, suffix,
            "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix),
            URLEncoder.encode(String.valueOf(getVarNames().get(i)), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
      }
    }

    // add `times` to the URL query string
    if (getTimes() != null) {
      for (int i = 0; i < getTimes().size(); i++) {
        joiner.add(String.format("%stimes%s%s=%s", prefix, suffix,
            "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix),
            URLEncoder.encode(String.valueOf(getTimes().get(i)), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
      }
    }

    // add `origin` to the URL query string
    if (getOrigin() != null) {
      joiner.add(getOrigin().toUrlQueryString(prefix + "origin" + suffix));
    }

    // add `extent` to the URL query string
    if (getExtent() != null) {
      joiner.add(getExtent().toUrlQueryString(prefix + "extent" + suffix));
    }

    // add `isize` to the URL query string
    if (getIsize() != null) {
      joiner.add(getIsize().toUrlQueryString(prefix + "isize" + suffix));
    }

    // add `annotation` to the URL query string
    if (getAnnotation() != null) {
      joiner.add(String.format("%sannotation%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getAnnotation()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `name` to the URL query string
    if (getName() != null) {
      joiner.add(String.format("%sname%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getName()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    return joiner.toString();
  }
}

