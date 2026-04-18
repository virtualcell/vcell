# OptimizationResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getOptimizationStatus**](OptimizationResourceApi.md#getOptimizationStatus) | **GET** /api/v1/optimization/{optId} | Get status, progress, or results of an optimization job |
| [**getOptimizationStatusWithHttpInfo**](OptimizationResourceApi.md#getOptimizationStatusWithHttpInfo) | **GET** /api/v1/optimization/{optId} | Get status, progress, or results of an optimization job |
| [**listOptimizationJobs**](OptimizationResourceApi.md#listOptimizationJobs) | **GET** /api/v1/optimization | List optimization jobs for the authenticated user |
| [**listOptimizationJobsWithHttpInfo**](OptimizationResourceApi.md#listOptimizationJobsWithHttpInfo) | **GET** /api/v1/optimization | List optimization jobs for the authenticated user |
| [**stopOptimization**](OptimizationResourceApi.md#stopOptimization) | **POST** /api/v1/optimization/{optId}/stop | Stop a running optimization job |
| [**stopOptimizationWithHttpInfo**](OptimizationResourceApi.md#stopOptimizationWithHttpInfo) | **POST** /api/v1/optimization/{optId}/stop | Stop a running optimization job |
| [**submitOptimization**](OptimizationResourceApi.md#submitOptimization) | **POST** /api/v1/optimization | Submit a new parameter estimation optimization job |
| [**submitOptimizationWithHttpInfo**](OptimizationResourceApi.md#submitOptimizationWithHttpInfo) | **POST** /api/v1/optimization | Submit a new parameter estimation optimization job |



## getOptimizationStatus

> OptimizationJobStatus getOptimizationStatus(optId)

Get status, progress, or results of an optimization job

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.OptimizationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        OptimizationResourceApi apiInstance = new OptimizationResourceApi(defaultClient);
        Long optId = 56L; // Long | 
        try {
            OptimizationJobStatus result = apiInstance.getOptimizationStatus(optId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling OptimizationResourceApi#getOptimizationStatus");
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
| **optId** | **Long**|  | |

### Return type

[**OptimizationJobStatus**](OptimizationJobStatus.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |

## getOptimizationStatusWithHttpInfo

> ApiResponse<OptimizationJobStatus> getOptimizationStatus getOptimizationStatusWithHttpInfo(optId)

Get status, progress, or results of an optimization job

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.OptimizationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        OptimizationResourceApi apiInstance = new OptimizationResourceApi(defaultClient);
        Long optId = 56L; // Long | 
        try {
            ApiResponse<OptimizationJobStatus> response = apiInstance.getOptimizationStatusWithHttpInfo(optId);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling OptimizationResourceApi#getOptimizationStatus");
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
| **optId** | **Long**|  | |

### Return type

ApiResponse<[**OptimizationJobStatus**](OptimizationJobStatus.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |


## listOptimizationJobs

> List<OptimizationJobStatus> listOptimizationJobs()

List optimization jobs for the authenticated user

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.OptimizationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        OptimizationResourceApi apiInstance = new OptimizationResourceApi(defaultClient);
        try {
            List<OptimizationJobStatus> result = apiInstance.listOptimizationJobs();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling OptimizationResourceApi#listOptimizationJobs");
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

[**List&lt;OptimizationJobStatus&gt;**](OptimizationJobStatus.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

## listOptimizationJobsWithHttpInfo

> ApiResponse<List<OptimizationJobStatus>> listOptimizationJobs listOptimizationJobsWithHttpInfo()

List optimization jobs for the authenticated user

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.OptimizationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        OptimizationResourceApi apiInstance = new OptimizationResourceApi(defaultClient);
        try {
            ApiResponse<List<OptimizationJobStatus>> response = apiInstance.listOptimizationJobsWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling OptimizationResourceApi#listOptimizationJobs");
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

ApiResponse<[**List&lt;OptimizationJobStatus&gt;**](OptimizationJobStatus.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |


## stopOptimization

> OptimizationJobStatus stopOptimization(optId)

Stop a running optimization job

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.OptimizationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        OptimizationResourceApi apiInstance = new OptimizationResourceApi(defaultClient);
        Long optId = 56L; // Long | 
        try {
            OptimizationJobStatus result = apiInstance.stopOptimization(optId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling OptimizationResourceApi#stopOptimization");
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
| **optId** | **Long**|  | |

### Return type

[**OptimizationJobStatus**](OptimizationJobStatus.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |

## stopOptimizationWithHttpInfo

> ApiResponse<OptimizationJobStatus> stopOptimization stopOptimizationWithHttpInfo(optId)

Stop a running optimization job

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.OptimizationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        OptimizationResourceApi apiInstance = new OptimizationResourceApi(defaultClient);
        Long optId = 56L; // Long | 
        try {
            ApiResponse<OptimizationJobStatus> response = apiInstance.stopOptimizationWithHttpInfo(optId);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling OptimizationResourceApi#stopOptimization");
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
| **optId** | **Long**|  | |

### Return type

ApiResponse<[**OptimizationJobStatus**](OptimizationJobStatus.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authenticated |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |


## submitOptimization

> OptimizationJobStatus submitOptimization(optProblem)

Submit a new parameter estimation optimization job

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.OptimizationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        OptimizationResourceApi apiInstance = new OptimizationResourceApi(defaultClient);
        OptProblem optProblem = new OptProblem(); // OptProblem | 
        try {
            OptimizationJobStatus result = apiInstance.submitOptimization(optProblem);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling OptimizationResourceApi#submitOptimization");
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
| **optProblem** | [**OptProblem**](OptProblem.md)|  | [optional] |

### Return type

[**OptimizationJobStatus**](OptimizationJobStatus.md)


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

## submitOptimizationWithHttpInfo

> ApiResponse<OptimizationJobStatus> submitOptimization submitOptimizationWithHttpInfo(optProblem)

Submit a new parameter estimation optimization job

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.OptimizationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        OptimizationResourceApi apiInstance = new OptimizationResourceApi(defaultClient);
        OptProblem optProblem = new OptProblem(); // OptProblem | 
        try {
            ApiResponse<OptimizationJobStatus> response = apiInstance.submitOptimizationWithHttpInfo(optProblem);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling OptimizationResourceApi#submitOptimization");
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
| **optProblem** | [**OptProblem**](OptProblem.md)|  | [optional] |

### Return type

ApiResponse<[**OptimizationJobStatus**](OptimizationJobStatus.md)>


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

