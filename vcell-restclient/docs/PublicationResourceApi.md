# PublicationResourceApi

All URIs are relative to *http://localhost:9000*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**addPublication**](PublicationResourceApi.md#addPublication) | **POST** /api/publications | Add publication |
| [**addPublicationWithHttpInfo**](PublicationResourceApi.md#addPublicationWithHttpInfo) | **POST** /api/publications | Add publication |
| [**deletePublication**](PublicationResourceApi.md#deletePublication) | **DELETE** /api/publications/{id} | Delete publication |
| [**deletePublicationWithHttpInfo**](PublicationResourceApi.md#deletePublicationWithHttpInfo) | **DELETE** /api/publications/{id} | Delete publication |
| [**getPublication**](PublicationResourceApi.md#getPublication) | **GET** /api/publications/{id} | Get publication by ID |
| [**getPublicationWithHttpInfo**](PublicationResourceApi.md#getPublicationWithHttpInfo) | **GET** /api/publications/{id} | Get publication by ID |
| [**getPublications**](PublicationResourceApi.md#getPublications) | **GET** /api/publications | Get all publications |
| [**getPublicationsWithHttpInfo**](PublicationResourceApi.md#getPublicationsWithHttpInfo) | **GET** /api/publications | Get all publications |



## addPublication

> Long addPublication(publication)

Add publication

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
            Long result = apiInstance.addPublication(publication);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#addPublication");
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

**Long**


### Authorization

[BearerToken](../README.md#BearerToken)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## addPublicationWithHttpInfo

> ApiResponse<Long> addPublication addPublicationWithHttpInfo(publication)

Add publication

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
            ApiResponse<Long> response = apiInstance.addPublicationWithHttpInfo(publication);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#addPublication");
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

ApiResponse<**Long**>


### Authorization

[BearerToken](../README.md#BearerToken)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## deletePublication

> void deletePublication(id)

Delete publication

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
        Long id = 56L; // Long | 
        try {
            apiInstance.deletePublication(id);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#deletePublication");
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
| **id** | **Long**|  | |

### Return type


null (empty response body)

### Authorization

[BearerToken](../README.md#BearerToken)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## deletePublicationWithHttpInfo

> ApiResponse<Void> deletePublication deletePublicationWithHttpInfo(id)

Delete publication

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
        Long id = 56L; // Long | 
        try {
            ApiResponse<Void> response = apiInstance.deletePublicationWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#deletePublication");
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
| **id** | **Long**|  | |

### Return type


ApiResponse<Void>

### Authorization

[BearerToken](../README.md#BearerToken)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## getPublication

> Publication getPublication(id)

Get publication by ID

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
        Long id = 56L; // Long | 
        try {
            Publication result = apiInstance.getPublication(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#getPublication");
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
| **id** | **Long**|  | |

### Return type

[**Publication**](Publication.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## getPublicationWithHttpInfo

> ApiResponse<Publication> getPublication getPublicationWithHttpInfo(id)

Get publication by ID

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
        Long id = 56L; // Long | 
        try {
            ApiResponse<Publication> response = apiInstance.getPublicationWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#getPublication");
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
| **id** | **Long**|  | |

### Return type

ApiResponse<[**Publication**](Publication.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## getPublications

> List<Publication> getPublications()

Get all publications

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
            List<Publication> result = apiInstance.getPublications();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#getPublications");
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

## getPublicationsWithHttpInfo

> ApiResponse<List<Publication>> getPublications getPublicationsWithHttpInfo()

Get all publications

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
            ApiResponse<List<Publication>> response = apiInstance.getPublicationsWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#getPublications");
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

