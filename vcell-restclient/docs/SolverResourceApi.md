# SolverResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getFVSolverInput**](SolverResourceApi.md#getFVSolverInput) | **POST** /api/v1/solver/getFVSolverInput | Retrieve finite volume input from SBML spatial model. |
| [**getFVSolverInputWithHttpInfo**](SolverResourceApi.md#getFVSolverInputWithHttpInfo) | **POST** /api/v1/solver/getFVSolverInput | Retrieve finite volume input from SBML spatial model. |



## getFVSolverInput

> File getFVSolverInput(sbmlFile)

Retrieve finite volume input from SBML spatial model.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SolverResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        SolverResourceApi apiInstance = new SolverResourceApi(defaultClient);
        File sbmlFile = new File("/path/to/file"); // File | 
        try {
            File result = apiInstance.getFVSolverInput(sbmlFile);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling SolverResourceApi#getFVSolverInput");
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
| **sbmlFile** | **File**|  | [optional] |

### Return type

[**File**](File.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/octet-stream

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## getFVSolverInputWithHttpInfo

> ApiResponse<File> getFVSolverInput getFVSolverInputWithHttpInfo(sbmlFile)

Retrieve finite volume input from SBML spatial model.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SolverResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        SolverResourceApi apiInstance = new SolverResourceApi(defaultClient);
        File sbmlFile = new File("/path/to/file"); // File | 
        try {
            ApiResponse<File> response = apiInstance.getFVSolverInputWithHttpInfo(sbmlFile);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling SolverResourceApi#getFVSolverInput");
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
| **sbmlFile** | **File**|  | [optional] |

### Return type

ApiResponse<[**File**](File.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/octet-stream

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

