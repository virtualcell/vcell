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
 * BiomodelRef
 */
@JsonPropertyOrder({
  BiomodelRef.JSON_PROPERTY_BM_KEY,
  BiomodelRef.JSON_PROPERTY_NAME,
  BiomodelRef.JSON_PROPERTY_OWNER_NAME,
  BiomodelRef.JSON_PROPERTY_OWNER_KEY,
  BiomodelRef.JSON_PROPERTY_VERSION_FLAG
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class BiomodelRef {
  public static final String JSON_PROPERTY_BM_KEY = "bmKey";
  private Long bmKey;

  public static final String JSON_PROPERTY_NAME = "name";
  private String name;

  public static final String JSON_PROPERTY_OWNER_NAME = "ownerName";
  private String ownerName;

  public static final String JSON_PROPERTY_OWNER_KEY = "ownerKey";
  private Long ownerKey;

  public static final String JSON_PROPERTY_VERSION_FLAG = "versionFlag";
  private Integer versionFlag;

  public BiomodelRef() { 
  }

  public BiomodelRef bmKey(Long bmKey) {
    this.bmKey = bmKey;
    return this;
  }

   /**
   * Get bmKey
   * @return bmKey
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_BM_KEY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Long getBmKey() {
    return bmKey;
  }


  @JsonProperty(JSON_PROPERTY_BM_KEY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setBmKey(Long bmKey) {
    this.bmKey = bmKey;
  }


  public BiomodelRef name(String name) {
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


  public BiomodelRef ownerName(String ownerName) {
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


  public BiomodelRef ownerKey(Long ownerKey) {
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


  public BiomodelRef versionFlag(Integer versionFlag) {
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
   * Return true if this BiomodelRef object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BiomodelRef biomodelRef = (BiomodelRef) o;
    return Objects.equals(this.bmKey, biomodelRef.bmKey) &&
        Objects.equals(this.name, biomodelRef.name) &&
        Objects.equals(this.ownerName, biomodelRef.ownerName) &&
        Objects.equals(this.ownerKey, biomodelRef.ownerKey) &&
        Objects.equals(this.versionFlag, biomodelRef.versionFlag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bmKey, name, ownerName, ownerKey, versionFlag);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BiomodelRef {\n");
    sb.append("    bmKey: ").append(toIndentedString(bmKey)).append("\n");
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

    // add `bmKey` to the URL query string
    if (getBmKey() != null) {
      joiner.add(String.format("%sbmKey%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getBmKey()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
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

