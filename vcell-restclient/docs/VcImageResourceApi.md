# VcImageResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteImageVCML**](VcImageResourceApi.md#deleteImageVCML) | **DELETE** /api/v1/image/{id} |  |
| [**deleteImageVCMLWithHttpInfo**](VcImageResourceApi.md#deleteImageVCMLWithHttpInfo) | **DELETE** /api/v1/image/{id} |  |
| [**getImageSummaries**](VcImageResourceApi.md#getImageSummaries) | **GET** /api/v1/image/summaries |  |
| [**getImageSummariesWithHttpInfo**](VcImageResourceApi.md#getImageSummariesWithHttpInfo) | **GET** /api/v1/image/summaries |  |
| [**getImageSummary**](VcImageResourceApi.md#getImageSummary) | **GET** /api/v1/image/summary/{id} |  |
| [**getImageSummaryWithHttpInfo**](VcImageResourceApi.md#getImageSummaryWithHttpInfo) | **GET** /api/v1/image/summary/{id} |  |
| [**getImageVCML**](VcImageResourceApi.md#getImageVCML) | **GET** /api/v1/image/{id} |  |
| [**getImageVCMLWithHttpInfo**](VcImageResourceApi.md#getImageVCMLWithHttpInfo) | **GET** /api/v1/image/{id} |  |
| [**saveImageVCML**](VcImageResourceApi.md#saveImageVCML) | **POST** /api/v1/image |  |
| [**saveImageVCMLWithHttpInfo**](VcImageResourceApi.md#saveImageVCMLWithHttpInfo) | **POST** /api/v1/image |  |



## deleteImageVCML

> void deleteImageVCML(id)



Remove specific image VCML.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            apiInstance.deleteImageVCML(id);
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#deleteImageVCML");
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

## deleteImageVCMLWithHttpInfo

> ApiResponse<Void> deleteImageVCML deleteImageVCMLWithHttpInfo(id)



Remove specific image VCML.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.deleteImageVCMLWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#deleteImageVCML");
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


## getImageSummaries

> List<VCImageSummary> getImageSummaries(includePublicAndShared)



Return Image summaries.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        Boolean includePublicAndShared = true; // Boolean | Include Image summaries that are public and shared with the requester. Default is true.
        try {
            List<VCImageSummary> result = apiInstance.getImageSummaries(includePublicAndShared);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#getImageSummaries");
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
| **includePublicAndShared** | **Boolean**| Include Image summaries that are public and shared with the requester. Default is true. | [optional] |

### Return type

[**List&lt;VCImageSummary&gt;**](VCImageSummary.md)


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

## getImageSummariesWithHttpInfo

> ApiResponse<List<VCImageSummary>> getImageSummaries getImageSummariesWithHttpInfo(includePublicAndShared)



Return Image summaries.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        Boolean includePublicAndShared = true; // Boolean | Include Image summaries that are public and shared with the requester. Default is true.
        try {
            ApiResponse<List<VCImageSummary>> response = apiInstance.getImageSummariesWithHttpInfo(includePublicAndShared);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#getImageSummaries");
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
| **includePublicAndShared** | **Boolean**| Include Image summaries that are public and shared with the requester. Default is true. | [optional] |

### Return type

ApiResponse<[**List&lt;VCImageSummary&gt;**](VCImageSummary.md)>


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


## getImageSummary

> VCImageSummary getImageSummary(id)



All of the miscellaneous information about an Image (Extent, ISize, preview, etc...), but not the actual Image itself.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            VCImageSummary result = apiInstance.getImageSummary(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#getImageSummary");
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

[**VCImageSummary**](VCImageSummary.md)


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

## getImageSummaryWithHttpInfo

> ApiResponse<VCImageSummary> getImageSummary getImageSummaryWithHttpInfo(id)



All of the miscellaneous information about an Image (Extent, ISize, preview, etc...), but not the actual Image itself.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            ApiResponse<VCImageSummary> response = apiInstance.getImageSummaryWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#getImageSummary");
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

ApiResponse<[**VCImageSummary**](VCImageSummary.md)>


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


## getImageVCML

> String getImageVCML(id)



Get specific image VCML.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            String result = apiInstance.getImageVCML(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#getImageVCML");
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
- **Accept**: text/plain, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |

## getImageVCMLWithHttpInfo

> ApiResponse<String> getImageVCML getImageVCMLWithHttpInfo(id)



Get specific image VCML.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            ApiResponse<String> response = apiInstance.getImageVCMLWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#getImageVCML");
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
- **Accept**: text/plain, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |


## saveImageVCML

> String saveImageVCML(body, name)



Save the VCML representation of an image.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        String body = "body_example"; // String | 
        String name = "name_example"; // String | Name to save new ImageVCML under. Leave blank if re-saving existing ImageVCML.
        try {
            String result = apiInstance.saveImageVCML(body, name);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#saveImageVCML");
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
| **name** | **String**| Name to save new ImageVCML under. Leave blank if re-saving existing ImageVCML. | [optional] |

### Return type

**String**


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |

## saveImageVCMLWithHttpInfo

> ApiResponse<String> saveImageVCML saveImageVCMLWithHttpInfo(body, name)



Save the VCML representation of an image.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcImageResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcImageResourceApi apiInstance = new VcImageResourceApi(defaultClient);
        String body = "body_example"; // String | 
        String name = "name_example"; // String | Name to save new ImageVCML under. Leave blank if re-saving existing ImageVCML.
        try {
            ApiResponse<String> response = apiInstance.saveImageVCMLWithHttpInfo(body, name);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling VcImageResourceApi#saveImageVCML");
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
| **name** | **String**| Name to save new ImageVCML under. Leave blank if re-saving existing ImageVCML. | [optional] |

### Return type

ApiResponse<**String**>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |

