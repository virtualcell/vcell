# ExportResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**exportN5**](ExportResourceApi.md#exportN5) | **POST** /api/v1/export/N5 |  |
| [**exportN5WithHttpInfo**](ExportResourceApi.md#exportN5WithHttpInfo) | **POST** /api/v1/export/N5 |  |
| [**exportStatus**](ExportResourceApi.md#exportStatus) | **PATCH** /api/v1/export/status |  |
| [**exportStatusWithHttpInfo**](ExportResourceApi.md#exportStatusWithHttpInfo) | **PATCH** /api/v1/export/status |  |



## exportN5

> Long exportN5(n5ExportRequest)



Create an N5 (ImageJ compatible) export. The request must contain the standard export information, exportable data type, dataset name, and sub-volume specifications.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.ExportResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        ExportResourceApi apiInstance = new ExportResourceApi(defaultClient);
        N5ExportRequest n5ExportRequest = new N5ExportRequest(); // N5ExportRequest | 
        try {
            Long result = apiInstance.exportN5(n5ExportRequest);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ExportResourceApi#exportN5");
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
| **n5ExportRequest** | [**N5ExportRequest**](N5ExportRequest.md)|  | [optional] |

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
| **400** | Bad Request. |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |

## exportN5WithHttpInfo

> ApiResponse<Long> exportN5 exportN5WithHttpInfo(n5ExportRequest)



Create an N5 (ImageJ compatible) export. The request must contain the standard export information, exportable data type, dataset name, and sub-volume specifications.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.ExportResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        ExportResourceApi apiInstance = new ExportResourceApi(defaultClient);
        N5ExportRequest n5ExportRequest = new N5ExportRequest(); // N5ExportRequest | 
        try {
            ApiResponse<Long> response = apiInstance.exportN5WithHttpInfo(n5ExportRequest);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling ExportResourceApi#exportN5");
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
| **n5ExportRequest** | [**N5ExportRequest**](N5ExportRequest.md)|  | [optional] |

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
| **400** | Bad Request. |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |


## exportStatus

> List<ExportEvent> exportStatus(body)



Get the status of your export jobs past the timestamp (UTC format).

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.ExportResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        ExportResourceApi apiInstance = new ExportResourceApi(defaultClient);
        OffsetDateTime body = OffsetDateTime.now(); // OffsetDateTime | 
        try {
            List<ExportEvent> result = apiInstance.exportStatus(body);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ExportResourceApi#exportStatus");
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
| **body** | **OffsetDateTime**|  | [optional] |

### Return type

[**List&lt;ExportEvent&gt;**](ExportEvent.md)


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

## exportStatusWithHttpInfo

> ApiResponse<List<ExportEvent>> exportStatus exportStatusWithHttpInfo(body)



Get the status of your export jobs past the timestamp (UTC format).

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.ExportResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        ExportResourceApi apiInstance = new ExportResourceApi(defaultClient);
        OffsetDateTime body = OffsetDateTime.now(); // OffsetDateTime | 
        try {
            ApiResponse<List<ExportEvent>> response = apiInstance.exportStatusWithHttpInfo(body);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling ExportResourceApi#exportStatus");
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
| **body** | **OffsetDateTime**|  | [optional] |

### Return type

ApiResponse<[**List&lt;ExportEvent&gt;**](ExportEvent.md)>


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

