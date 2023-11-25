# PublicationResourceApi

All URIs are relative to *http://localhost:9000*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiPublicationsDelete**](PublicationResourceApi.md#apiPublicationsDelete) | **DELETE** /api/publications |  |
| [**apiPublicationsDeleteWithHttpInfo**](PublicationResourceApi.md#apiPublicationsDeleteWithHttpInfo) | **DELETE** /api/publications |  |
| [**apiPublicationsGet**](PublicationResourceApi.md#apiPublicationsGet) | **GET** /api/publications |  |
| [**apiPublicationsGetWithHttpInfo**](PublicationResourceApi.md#apiPublicationsGetWithHttpInfo) | **GET** /api/publications |  |
| [**apiPublicationsPost**](PublicationResourceApi.md#apiPublicationsPost) | **POST** /api/publications |  |
| [**apiPublicationsPostWithHttpInfo**](PublicationResourceApi.md#apiPublicationsPostWithHttpInfo) | **POST** /api/publications |  |



## apiPublicationsDelete

> void apiPublicationsDelete(body)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.PublicationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:9000");
        

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Long body = 56L; // Long | 
        try {
            apiInstance.apiPublicationsDelete(body);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#apiPublicationsDelete");
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
| **body** | **Long**|  | [optional] |

### Return type


null (empty response body)

### Authorization

[OpenIDConnect](../README.md#OpenIDConnect)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## apiPublicationsDeleteWithHttpInfo

> ApiResponse<Void> apiPublicationsDelete apiPublicationsDeleteWithHttpInfo(body)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.PublicationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:9000");
        

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Long body = 56L; // Long | 
        try {
            ApiResponse<Void> response = apiInstance.apiPublicationsDeleteWithHttpInfo(body);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#apiPublicationsDelete");
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
| **body** | **Long**|  | [optional] |

### Return type


ApiResponse<Void>

### Authorization

[OpenIDConnect](../README.md#OpenIDConnect)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## apiPublicationsGet

> List<Publication> apiPublicationsGet()



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.PublicationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:9000");

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        try {
            List<Publication> result = apiInstance.apiPublicationsGet();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#apiPublicationsGet");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**List&lt;Publication&gt;**](Publication.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## apiPublicationsGetWithHttpInfo

> ApiResponse<List<Publication>> apiPublicationsGet apiPublicationsGetWithHttpInfo()



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.PublicationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:9000");

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        try {
            ApiResponse<List<Publication>> response = apiInstance.apiPublicationsGetWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#apiPublicationsGet");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response headers: " + e.getResponseHeaders());
            System.err.println("Reason: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

ApiResponse<[**List&lt;Publication&gt;**](Publication.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## apiPublicationsPost

> String apiPublicationsPost(publication)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.PublicationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:9000");
        

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Publication publication = new Publication(); // Publication | 
        try {
            String result = apiInstance.apiPublicationsPost(publication);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#apiPublicationsPost");
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
| **publication** | [**Publication**](Publication.md)|  | [optional] |

### Return type

**String**


### Authorization

[OpenIDConnect](../README.md#OpenIDConnect)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## apiPublicationsPostWithHttpInfo

> ApiResponse<String> apiPublicationsPost apiPublicationsPostWithHttpInfo(publication)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.PublicationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:9000");
        

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Publication publication = new Publication(); // Publication | 
        try {
            ApiResponse<String> response = apiInstance.apiPublicationsPostWithHttpInfo(publication);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#apiPublicationsPost");
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
| **publication** | [**Publication**](Publication.md)|  | [optional] |

### Return type

ApiResponse<**String**>


### Authorization

[OpenIDConnect](../README.md#OpenIDConnect)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

