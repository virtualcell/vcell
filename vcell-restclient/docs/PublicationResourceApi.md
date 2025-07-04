# PublicationResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createPublication**](PublicationResourceApi.md#createPublication) | **POST** /api/v1/publications | Create publication |
| [**createPublicationWithHttpInfo**](PublicationResourceApi.md#createPublicationWithHttpInfo) | **POST** /api/v1/publications | Create publication |
| [**deletePublication**](PublicationResourceApi.md#deletePublication) | **DELETE** /api/v1/publications/{id} | Delete publication |
| [**deletePublicationWithHttpInfo**](PublicationResourceApi.md#deletePublicationWithHttpInfo) | **DELETE** /api/v1/publications/{id} | Delete publication |
| [**getPublicationById**](PublicationResourceApi.md#getPublicationById) | **GET** /api/v1/publications/{id} | Get publication by ID |
| [**getPublicationByIdWithHttpInfo**](PublicationResourceApi.md#getPublicationByIdWithHttpInfo) | **GET** /api/v1/publications/{id} | Get publication by ID |
| [**getPublications**](PublicationResourceApi.md#getPublications) | **GET** /api/v1/publications | Get all publications |
| [**getPublicationsWithHttpInfo**](PublicationResourceApi.md#getPublicationsWithHttpInfo) | **GET** /api/v1/publications | Get all publications |
| [**updatePublication**](PublicationResourceApi.md#updatePublication) | **PUT** /api/v1/publications | Update publication |
| [**updatePublicationWithHttpInfo**](PublicationResourceApi.md#updatePublicationWithHttpInfo) | **PUT** /api/v1/publications | Update publication |



## createPublication

> Long createPublication(publication)

Create publication

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Publication publication = new Publication(); // Publication | 
        try {
            Long result = apiInstance.createPublication(publication);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#createPublication");
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

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

## createPublicationWithHttpInfo

> ApiResponse<Long> createPublication createPublicationWithHttpInfo(publication)

Create publication

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Publication publication = new Publication(); // Publication | 
        try {
            ApiResponse<Long> response = apiInstance.createPublicationWithHttpInfo(publication);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#createPublication");
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

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |


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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

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

[openId](../README.md#openId)

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

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

[openId](../README.md#openId)

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


## getPublicationById

> Publication getPublicationById(id)

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Long id = 56L; // Long | 
        try {
            Publication result = apiInstance.getPublicationById(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#getPublicationById");
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
| **500** | Data Access Exception |  -  |

## getPublicationByIdWithHttpInfo

> ApiResponse<Publication> getPublicationById getPublicationByIdWithHttpInfo(id)

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Long id = 56L; // Long | 
        try {
            ApiResponse<Publication> response = apiInstance.getPublicationByIdWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#getPublicationById");
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
| **500** | Data Access Exception |  -  |


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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

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
| **500** | Data Access Exception |  -  |

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

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
| **500** | Data Access Exception |  -  |


## updatePublication

> Publication updatePublication(publication)

Update publication

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Publication publication = new Publication(); // Publication | 
        try {
            Publication result = apiInstance.updatePublication(publication);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#updatePublication");
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

[**Publication**](Publication.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

## updatePublicationWithHttpInfo

> ApiResponse<Publication> updatePublication updatePublicationWithHttpInfo(publication)

Update publication

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        PublicationResourceApi apiInstance = new PublicationResourceApi(defaultClient);
        Publication publication = new Publication(); // Publication | 
        try {
            ApiResponse<Publication> response = apiInstance.updatePublicationWithHttpInfo(publication);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling PublicationResourceApi#updatePublication");
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

ApiResponse<[**Publication**](Publication.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

