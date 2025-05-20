# SolverResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getFVSolverInputFromSBML**](SolverResourceApi.md#getFVSolverInputFromSBML) | **POST** /api/v1/solver/getFVSolverInput | Retrieve finite volume input from SBML spatial model. |
| [**getFVSolverInputFromSBMLWithHttpInfo**](SolverResourceApi.md#getFVSolverInputFromSBMLWithHttpInfo) | **POST** /api/v1/solver/getFVSolverInput | Retrieve finite volume input from SBML spatial model. |
| [**getFVSolverInputFromVCML**](SolverResourceApi.md#getFVSolverInputFromVCML) | **POST** /api/v1/solver/getFVSolverInputFromVCML | Retrieve finite volume input from SBML spatial model. |
| [**getFVSolverInputFromVCMLWithHttpInfo**](SolverResourceApi.md#getFVSolverInputFromVCMLWithHttpInfo) | **POST** /api/v1/solver/getFVSolverInputFromVCML | Retrieve finite volume input from SBML spatial model. |



## getFVSolverInputFromSBML

> File getFVSolverInputFromSBML(sbmlFile, duration, outputTimeStep)

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        SolverResourceApi apiInstance = new SolverResourceApi(defaultClient);
        File sbmlFile = new File("/path/to/file"); // File | 
        Double duration = 5.0D; // Double | 
        Double outputTimeStep = 0.1D; // Double | 
        try {
            File result = apiInstance.getFVSolverInputFromSBML(sbmlFile, duration, outputTimeStep);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling SolverResourceApi#getFVSolverInputFromSBML");
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
| **duration** | **Double**|  | [optional] [default to 5.0] |
| **outputTimeStep** | **Double**|  | [optional] [default to 0.1] |

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

## getFVSolverInputFromSBMLWithHttpInfo

> ApiResponse<File> getFVSolverInputFromSBML getFVSolverInputFromSBMLWithHttpInfo(sbmlFile, duration, outputTimeStep)

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        SolverResourceApi apiInstance = new SolverResourceApi(defaultClient);
        File sbmlFile = new File("/path/to/file"); // File | 
        Double duration = 5.0D; // Double | 
        Double outputTimeStep = 0.1D; // Double | 
        try {
            ApiResponse<File> response = apiInstance.getFVSolverInputFromSBMLWithHttpInfo(sbmlFile, duration, outputTimeStep);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling SolverResourceApi#getFVSolverInputFromSBML");
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
| **duration** | **Double**|  | [optional] [default to 5.0] |
| **outputTimeStep** | **Double**|  | [optional] [default to 0.1] |

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


## getFVSolverInputFromVCML

> File getFVSolverInputFromVCML(vcmlFile, simulationName)

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        SolverResourceApi apiInstance = new SolverResourceApi(defaultClient);
        File vcmlFile = new File("/path/to/file"); // File | 
        String simulationName = "simulationName_example"; // String | 
        try {
            File result = apiInstance.getFVSolverInputFromVCML(vcmlFile, simulationName);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling SolverResourceApi#getFVSolverInputFromVCML");
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
| **vcmlFile** | **File**|  | [optional] |
| **simulationName** | **String**|  | [optional] |

### Return type

[**File**](File.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/octet-stream, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **422** | Unprocessable content submitted |  -  |

## getFVSolverInputFromVCMLWithHttpInfo

> ApiResponse<File> getFVSolverInputFromVCML getFVSolverInputFromVCMLWithHttpInfo(vcmlFile, simulationName)

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        SolverResourceApi apiInstance = new SolverResourceApi(defaultClient);
        File vcmlFile = new File("/path/to/file"); // File | 
        String simulationName = "simulationName_example"; // String | 
        try {
            ApiResponse<File> response = apiInstance.getFVSolverInputFromVCMLWithHttpInfo(vcmlFile, simulationName);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling SolverResourceApi#getFVSolverInputFromVCML");
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
| **vcmlFile** | **File**|  | [optional] |
| **simulationName** | **String**|  | [optional] |

### Return type

ApiResponse<[**File**](File.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/octet-stream, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **422** | Unprocessable content submitted |  -  |

