# MathModelResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteMathModel**](MathModelResourceApi.md#deleteMathModel) | **DELETE** /api/v1/mathModel/{id} |  |
| [**deleteMathModelWithHttpInfo**](MathModelResourceApi.md#deleteMathModelWithHttpInfo) | **DELETE** /api/v1/mathModel/{id} |  |
| [**getSummaries**](MathModelResourceApi.md#getSummaries) | **GET** /api/v1/mathModel/summaries |  |
| [**getSummariesWithHttpInfo**](MathModelResourceApi.md#getSummariesWithHttpInfo) | **GET** /api/v1/mathModel/summaries |  |
| [**getSummary**](MathModelResourceApi.md#getSummary) | **GET** /api/v1/mathModel/summary/{id} |  |
| [**getSummaryWithHttpInfo**](MathModelResourceApi.md#getSummaryWithHttpInfo) | **GET** /api/v1/mathModel/summary/{id} |  |
| [**getVCML**](MathModelResourceApi.md#getVCML) | **GET** /api/v1/mathModel/{id} |  |
| [**getVCMLWithHttpInfo**](MathModelResourceApi.md#getVCMLWithHttpInfo) | **GET** /api/v1/mathModel/{id} |  |
| [**saveMathModel**](MathModelResourceApi.md#saveMathModel) | **POST** /api/v1/mathModel |  |
| [**saveMathModelWithHttpInfo**](MathModelResourceApi.md#saveMathModelWithHttpInfo) | **POST** /api/v1/mathModel |  |



## deleteMathModel

> void deleteMathModel(id)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            apiInstance.deleteMathModel(id);
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#deleteMathModel");
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
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |

## deleteMathModelWithHttpInfo

> ApiResponse<Void> deleteMathModel deleteMathModelWithHttpInfo(id)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.deleteMathModelWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#deleteMathModel");
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
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |


## getSummaries

> List<MathModelSummary> getSummaries(includePublicAndShared)



Return MathModel summaries.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        Boolean includePublicAndShared = true; // Boolean | Include MathModel summaries that are public and shared with the requester.
        try {
            List<MathModelSummary> result = apiInstance.getSummaries(includePublicAndShared);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#getSummaries");
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
| **includePublicAndShared** | **Boolean**| Include MathModel summaries that are public and shared with the requester. | [optional] |

### Return type

[**List&lt;MathModelSummary&gt;**](MathModelSummary.md)


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

## getSummariesWithHttpInfo

> ApiResponse<List<MathModelSummary>> getSummaries getSummariesWithHttpInfo(includePublicAndShared)



Return MathModel summaries.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        Boolean includePublicAndShared = true; // Boolean | Include MathModel summaries that are public and shared with the requester.
        try {
            ApiResponse<List<MathModelSummary>> response = apiInstance.getSummariesWithHttpInfo(includePublicAndShared);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#getSummaries");
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
| **includePublicAndShared** | **Boolean**| Include MathModel summaries that are public and shared with the requester. | [optional] |

### Return type

ApiResponse<[**List&lt;MathModelSummary&gt;**](MathModelSummary.md)>


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


## getSummary

> MathModelSummary getSummary(id)



All of the text based information about a MathModel (summary, version, publication status, etc...), but not the actual MathModel itself.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            MathModelSummary result = apiInstance.getSummary(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#getSummary");
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

[**MathModelSummary**](MathModelSummary.md)


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

## getSummaryWithHttpInfo

> ApiResponse<MathModelSummary> getSummary getSummaryWithHttpInfo(id)



All of the text based information about a MathModel (summary, version, publication status, etc...), but not the actual MathModel itself.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            ApiResponse<MathModelSummary> response = apiInstance.getSummaryWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#getSummary");
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

ApiResponse<[**MathModelSummary**](MathModelSummary.md)>


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


## getVCML

> String getVCML(id)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            String result = apiInstance.getVCML(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#getVCML");
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

## getVCMLWithHttpInfo

> ApiResponse<String> getVCML getVCMLWithHttpInfo(id)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        String id = "id_example"; // String | 
        try {
            ApiResponse<String> response = apiInstance.getVCMLWithHttpInfo(id);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#getVCML");
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


## saveMathModel

> String saveMathModel(body, newName, simNames)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        String body = "body_example"; // String | 
        String newName = "newName_example"; // String | Name to save new MathModel under. Leave blank if re-saving existing MathModel.
        List<String> simNames = Arrays.asList(); // List<String> | The name of simulations that will be prepared for future execution.
        try {
            String result = apiInstance.saveMathModel(body, newName, simNames);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#saveMathModel");
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
| **newName** | **String**| Name to save new MathModel under. Leave blank if re-saving existing MathModel. | [optional] |
| **simNames** | [**List&lt;String&gt;**](String.md)| The name of simulations that will be prepared for future execution. | [optional] |

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
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

## saveMathModelWithHttpInfo

> ApiResponse<String> saveMathModel saveMathModelWithHttpInfo(body, newName, simNames)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.MathModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        MathModelResourceApi apiInstance = new MathModelResourceApi(defaultClient);
        String body = "body_example"; // String | 
        String newName = "newName_example"; // String | Name to save new MathModel under. Leave blank if re-saving existing MathModel.
        List<String> simNames = Arrays.asList(); // List<String> | The name of simulations that will be prepared for future execution.
        try {
            ApiResponse<String> response = apiInstance.saveMathModelWithHttpInfo(body, newName, simNames);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling MathModelResourceApi#saveMathModel");
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
| **newName** | **String**| Name to save new MathModel under. Leave blank if re-saving existing MathModel. | [optional] |
| **simNames** | [**List&lt;String&gt;**](String.md)| The name of simulations that will be prepared for future execution. | [optional] |

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
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

