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
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * MathmodelRef
 */
@JsonPropertyOrder({
  MathmodelRef.JSON_PROPERTY_MM_KEY,
  MathmodelRef.JSON_PROPERTY_NAME,
  MathmodelRef.JSON_PROPERTY_OWNER_NAME,
  MathmodelRef.JSON_PROPERTY_OWNER_KEY,
  MathmodelRef.JSON_PROPERTY_VERSION_FLAG
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MathmodelRef {
  public static final String JSON_PROPERTY_MM_KEY = "mmKey";
  private Long mmKey;

  public static final String JSON_PROPERTY_NAME = "name";
  private String name;

  public static final String JSON_PROPERTY_OWNER_NAME = "ownerName";
  private String ownerName;

  public static final String JSON_PROPERTY_OWNER_KEY = "ownerKey";
  private Long ownerKey;

  public static final String JSON_PROPERTY_VERSION_FLAG = "versionFlag";
  private Integer versionFlag;

  public MathmodelRef() { 
  }

  public MathmodelRef mmKey(Long mmKey) {
    this.mmKey = mmKey;
    return this;
  }

   /**
   * Get mmKey
   * @return mmKey
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_MM_KEY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Long getMmKey() {
    return mmKey;
  }


  @JsonProperty(JSON_PROPERTY_MM_KEY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMmKey(Long mmKey) {
    this.mmKey = mmKey;
  }


  public MathmodelRef name(String name) {
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


  public MathmodelRef ownerName(String ownerName) {
    this.ownerName = ownerName;
    return this;
  }

   /**
   * Get ownerName
   * @return ownerName
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_OWNER_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getOwnerName() {
    return ownerName;
  }


  @JsonProperty(JSON_PROPERTY_OWNER_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }


  public MathmodelRef ownerKey(Long ownerKey) {
    this.ownerKey = ownerKey;
    return this;
  }

   /**
   * Get ownerKey
   * @return ownerKey
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_OWNER_KEY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Long getOwnerKey() {
    return ownerKey;
  }


  @JsonProperty(JSON_PROPERTY_OWNER_KEY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOwnerKey(Long ownerKey) {
    this.ownerKey = ownerKey;
  }


  public MathmodelRef versionFlag(Integer versionFlag) {
    this.versionFlag = versionFlag;
    return this;
  }

   /**
   * Get versionFlag
   * @return versionFlag
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_VERSION_FLAG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getVersionFlag() {
    return versionFlag;
  }


  @JsonProperty(JSON_PROPERTY_VERSION_FLAG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setVersionFlag(Integer versionFlag) {
    this.versionFlag = versionFlag;
  }


  /**
   * Return true if this MathmodelRef object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MathmodelRef mathmodelRef = (MathmodelRef) o;
    return Objects.equals(this.mmKey, mathmodelRef.mmKey) &&
        Objects.equals(this.name, mathmodelRef.name) &&
        Objects.equals(this.ownerName, mathmodelRef.ownerName) &&
        Objects.equals(this.ownerKey, mathmodelRef.ownerKey) &&
        Objects.equals(this.versionFlag, mathmodelRef.versionFlag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mmKey, name, ownerName, ownerKey, versionFlag);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MathmodelRef {\n");
    sb.append("    mmKey: ").append(toIndentedString(mmKey)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    ownerName: ").append(toIndentedString(ownerName)).append("\n");
    sb.append("    ownerKey: ").append(toIndentedString(ownerKey)).append("\n");
    sb.append("    versionFlag: ").append(toIndentedString(versionFlag)).append("\n");
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

    // add `mmKey` to the URL query string
    if (getMmKey() != null) {
      joiner.add(String.format("%smmKey%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getMmKey()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `name` to the URL query string
    if (getName() != null) {
      joiner.add(String.format("%sname%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getName()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `ownerName` to the URL query string
    if (getOwnerName() != null) {
      joiner.add(String.format("%sownerName%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getOwnerName()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `ownerKey` to the URL query string
    if (getOwnerKey() != null) {
      joiner.add(String.format("%sownerKey%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getOwnerKey()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `versionFlag` to the URL query string
    if (getVersionFlag() != null) {
      joiner.add(String.format("%sversionFlag%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getVersionFlag()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    return joiner.toString();
  }
}

