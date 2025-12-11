# GeometryResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteGeometry**](GeometryResourceApi.md#deleteGeometry) | **DELETE** /api/v1/geometry/{id} |  |
| [**deleteGeometryWithHttpInfo**](GeometryResourceApi.md#deleteGeometryWithHttpInfo) | **DELETE** /api/v1/geometry/{id} |  |
| [**getGeometrySummaries**](GeometryResourceApi.md#getGeometrySummaries) | **GET** /api/v1/geometry/summaries |  |
| [**getGeometrySummariesWithHttpInfo**](GeometryResourceApi.md#getGeometrySummariesWithHttpInfo) | **GET** /api/v1/geometry/summaries |  |
| [**getGeometrySummary**](GeometryResourceApi.md#getGeometrySummary) | **GET** /api/v1/geometry/summary/{id} |  |
| [**getGeometrySummaryWithHttpInfo**](GeometryResourceApi.md#getGeometrySummaryWithHttpInfo) | **GET** /api/v1/geometry/summary/{id} |  |
| [**getGeometryVCML**](GeometryResourceApi.md#getGeometryVCML) | **GET** /api/v1/geometry/{id} |  |
| [**getGeometryVCMLWithHttpInfo**](GeometryResourceApi.md#getGeometryVCMLWithHttpInfo) | **GET** /api/v1/geometry/{id} |  |
| [**saveGeometry**](GeometryResourceApi.md#saveGeometry) | **POST** /api/v1/geometry |  |
| [**saveGeometryWithHttpInfo**](GeometryResourceApi.md#saveGeometryWithHttpInfo) | **POST** /api/v1/geometry |  |



## deleteGeometry

> void deleteGeometry(id)



Remove specific Geometry.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            apiInstance.deleteGeometry(id);
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#deleteGeometry");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | |

### Return type


null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |

## deleteGeometryWithHttpInfo

> ApiResponse<Void> deleteGeometry deleteGeometryWithHttpInfo(id)



Remove specific Geometry.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.deleteGeometryWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#deleteGeometry");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | |

### Return type


ApiResponse<Void>

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |


## getGeometrySummaries

> List<GeometrySummary> getGeometrySummaries(includePublicAndShared)



Return Geometry summaries.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        Boolean includePublicAndShared = true; // Boolean | Include Geometry summaries that are public and shared with the requester. Default is true.
        try {
            List<GeometrySummary> result = apiInstance.getGeometrySummaries(includePublicAndShared);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#getGeometrySummaries");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **includePublicAndShared** | **Boolean**| Include Geometry summaries that are public and shared with the requester. Default is true. | [optional] |

### Return type

[**List&lt;GeometrySummary&gt;**](GeometrySummary.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **500** | Data Access Exception |  -  |

## getGeometrySummariesWithHttpInfo

> ApiResponse<List<GeometrySummary>> getGeometrySummaries getGeometrySummariesWithHttpInfo(includePublicAndShared)



Return Geometry summaries.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        Boolean includePublicAndShared = true; // Boolean | Include Geometry summaries that are public and shared with the requester. Default is true.
        try {
            ApiResponse<List<GeometrySummary>> response = apiInstance.getGeometrySummariesWithHttpInfo(includePublicAndShared);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#getGeometrySummaries");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **includePublicAndShared** | **Boolean**| Include Geometry summaries that are public and shared with the requester. Default is true. | [optional] |

### Return type

ApiResponse<[**List&lt;GeometrySummary&gt;**](GeometrySummary.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **500** | Data Access Exception |  -  |


## getGeometrySummary

> GeometrySummary getGeometrySummary(id)



All of the text based information about a Geometry (dimensions, extent, origin, etc...), but not the actual Geometry itself.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            GeometrySummary result = apiInstance.getGeometrySummary(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#getGeometrySummary");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | |

### Return type

[**GeometrySummary**](GeometrySummary.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |

## getGeometrySummaryWithHttpInfo

> ApiResponse<GeometrySummary> getGeometrySummary getGeometrySummaryWithHttpInfo(id)



All of the text based information about a Geometry (dimensions, extent, origin, etc...), but not the actual Geometry itself.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            ApiResponse<GeometrySummary> response = apiInstance.getGeometrySummaryWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#getGeometrySummary");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | |

### Return type

ApiResponse<[**GeometrySummary**](GeometrySummary.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |


## getGeometryVCML

> String getGeometryVCML(id)



Returns &lt;Geometry&gt; as root element in VCML format.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            String result = apiInstance.getGeometryVCML(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#getGeometryVCML");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | |

### Return type

**String**


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |

## getGeometryVCMLWithHttpInfo

> ApiResponse<String> getGeometryVCML getGeometryVCMLWithHttpInfo(id)



Returns &lt;Geometry&gt; as root element in VCML format.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            ApiResponse<String> response = apiInstance.getGeometryVCMLWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#getGeometryVCML");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | |

### Return type

ApiResponse<**String**>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |


## saveGeometry

> String saveGeometry(body, newName)



Save&#39;s VCML with &lt;Geometry&gt; as the root element.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        String body = "body_example"; // String | 
        String newName = "newName_example"; // String | Name to save new Geometry under. Leave blank if re-saving existing Geometry.
        try {
            String result = apiInstance.saveGeometry(body, newName);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#saveGeometry");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **body** | **String**|  | |
| **newName** | **String**| Name to save new Geometry under. Leave blank if re-saving existing Geometry. | [optional] |

### Return type

**String**


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/xml
- **Accept**: application/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |

## saveGeometryWithHttpInfo

> ApiResponse<String> saveGeometry saveGeometryWithHttpInfo(body, newName)



Save&#39;s VCML with &lt;Geometry&gt; as the root element.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.GeometryResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        GeometryResourceApi apiInstance = new GeometryResourceApi(defaultClient);
        String body = "body_example"; // String | 
        String newName = "newName_example"; // String | Name to save new Geometry under. Leave blank if re-saving existing Geometry.
        try {
            ApiResponse<String> response = apiInstance.saveGeometryWithHttpInfo(body, newName);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling GeometryResourceApi#saveGeometry");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **body** | **String**|  | |
| **newName** | **String**| Name to save new Geometry under. Leave blank if re-saving existing Geometry. | [optional] |

### Return type

ApiResponse<**String**>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/xml
- **Accept**: application/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |

