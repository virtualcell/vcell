# HelloWorldApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getHelloWorld**](HelloWorldApi.md#getHelloWorld) | **GET** /api/v1/helloworld | Get hello world message. |
| [**getHelloWorldWithHttpInfo**](HelloWorldApi.md#getHelloWorldWithHttpInfo) | **GET** /api/v1/helloworld | Get hello world message. |



## getHelloWorld

> HelloWorldMessage getHelloWorld()

Get hello world message.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.HelloWorldApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        HelloWorldApi apiInstance = new HelloWorldApi(defaultClient);
        try {
            HelloWorldMessage result = apiInstance.getHelloWorld();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling HelloWorldApi#getHelloWorld");
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

[**HelloWorldMessage**](HelloWorldMessage.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## getHelloWorldWithHttpInfo

> ApiResponse<HelloWorldMessage> getHelloWorld getHelloWorldWithHttpInfo()

Get hello world message.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.HelloWorldApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        HelloWorldApi apiInstance = new HelloWorldApi(defaultClient);
        try {
            ApiResponse<HelloWorldMessage> response = apiInstance.getHelloWorldWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling HelloWorldApi#getHelloWorld");
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

ApiResponse<[**HelloWorldMessage**](HelloWorldMessage.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

