# AdminResourceApi

All URIs are relative to *https://vcellapi-test.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getUsage**](AdminResourceApi.md#getUsage) | **GET** /api/v1/admin/usage | Get usage summary |
| [**getUsageWithHttpInfo**](AdminResourceApi.md#getUsageWithHttpInfo) | **GET** /api/v1/admin/usage | Get usage summary |



## getUsage

> UsageSummary getUsage()

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
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");
        

        AdminResourceApi apiInstance = new AdminResourceApi(defaultClient);
        try {
            UsageSummary result = apiInstance.getUsage();
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

[**UsageSummary**](UsageSummary.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## getUsageWithHttpInfo

> ApiResponse<UsageSummary> getUsage getUsageWithHttpInfo()

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
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");
        

        AdminResourceApi apiInstance = new AdminResourceApi(defaultClient);
        try {
            ApiResponse<UsageSummary> response = apiInstance.getUsageWithHttpInfo();
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

ApiResponse<[**UsageSummary**](UsageSummary.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

