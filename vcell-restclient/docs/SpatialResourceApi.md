# SpatialResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**retrieveFiniteVolumeInputFromSpatialModel**](SpatialResourceApi.md#retrieveFiniteVolumeInputFromSpatialModel) | **GET** /api/v1/spatial/retrieveFiniteVolumeInputFromSpatialModel | Retrieve finite volume input from spatial model |
| [**retrieveFiniteVolumeInputFromSpatialModelWithHttpInfo**](SpatialResourceApi.md#retrieveFiniteVolumeInputFromSpatialModelWithHttpInfo) | **GET** /api/v1/spatial/retrieveFiniteVolumeInputFromSpatialModel | Retrieve finite volume input from spatial model |



## retrieveFiniteVolumeInputFromSpatialModel

> File retrieveFiniteVolumeInputFromSpatialModel(sbmlFile)

Retrieve finite volume input from spatial model

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SpatialResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        SpatialResourceApi apiInstance = new SpatialResourceApi(defaultClient);
        File sbmlFile = new File("/path/to/file"); // File | 
        try {
            File result = apiInstance.retrieveFiniteVolumeInputFromSpatialModel(sbmlFile);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling SpatialResourceApi#retrieveFiniteVolumeInputFromSpatialModel");
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

## retrieveFiniteVolumeInputFromSpatialModelWithHttpInfo

> ApiResponse<File> retrieveFiniteVolumeInputFromSpatialModel retrieveFiniteVolumeInputFromSpatialModelWithHttpInfo(sbmlFile)

Retrieve finite volume input from spatial model

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SpatialResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        SpatialResourceApi apiInstance = new SpatialResourceApi(defaultClient);
        File sbmlFile = new File("/path/to/file"); // File | 
        try {
            ApiResponse<File> response = apiInstance.retrieveFiniteVolumeInputFromSpatialModelWithHttpInfo(sbmlFile);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling SpatialResourceApi#retrieveFiniteVolumeInputFromSpatialModel");
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

