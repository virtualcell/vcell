# AuthResourceApi

All URIs are relative to *http://localhost:9000*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**codeExchange**](AuthResourceApi.md#codeExchange) | **GET** /api/auth/code-flow | Get access token using authorization code flow |
| [**codeExchangeWithHttpInfo**](AuthResourceApi.md#codeExchangeWithHttpInfo) | **GET** /api/auth/code-flow | Get access token using authorization code flow |



## codeExchange

> AuthCodeResponse codeExchange(code, redirectURL)

Get access token using authorization code flow

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.AuthResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:9000");

        AuthResourceApi apiInstance = new AuthResourceApi(defaultClient);
        String code = "code_example"; // String | 
        String redirectURL = "redirectURL_example"; // String | 
        try {
            AuthCodeResponse result = apiInstance.codeExchange(code, redirectURL);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AuthResourceApi#codeExchange");
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
| **code** | **String**|  | [optional] |
| **redirectURL** | **String**|  | [optional] |

### Return type

[**AuthCodeResponse**](AuthCodeResponse.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## codeExchangeWithHttpInfo

> ApiResponse<AuthCodeResponse> codeExchange codeExchangeWithHttpInfo(code, redirectURL)

Get access token using authorization code flow

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.AuthResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:9000");

        AuthResourceApi apiInstance = new AuthResourceApi(defaultClient);
        String code = "code_example"; // String | 
        String redirectURL = "redirectURL_example"; // String | 
        try {
            ApiResponse<AuthCodeResponse> response = apiInstance.codeExchangeWithHttpInfo(code, redirectURL);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling AuthResourceApi#codeExchange");
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
| **code** | **String**|  | [optional] |
| **redirectURL** | **String**|  | [optional] |

### Return type

ApiResponse<[**AuthCodeResponse**](AuthCodeResponse.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

