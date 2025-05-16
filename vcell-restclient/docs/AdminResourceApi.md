# AdminResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getUsage**](AdminResourceApi.md#getUsage) | **GET** /api/v1/admin/usage | Get usage summary |
| [**getUsageWithHttpInfo**](AdminResourceApi.md#getUsageWithHttpInfo) | **GET** /api/v1/admin/usage | Get usage summary |



## getUsage

> File getUsage()

Get usage summary

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.AdminResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        AdminResourceApi apiInstance = new AdminResourceApi(defaultClient);
        try {
            File result = apiInstance.getUsage();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AdminResourceApi#getUsage");
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

[**File**](File.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/pdf

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The PDF report |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

## getUsageWithHttpInfo

> ApiResponse<File> getUsage getUsageWithHttpInfo()

Get usage summary

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.AdminResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        AdminResourceApi apiInstance = new AdminResourceApi(defaultClient);
        try {
            ApiResponse<File> response = apiInstance.getUsageWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AdminResourceApi#getUsage");
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

ApiResponse<[**File**](File.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/pdf

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The PDF report |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

