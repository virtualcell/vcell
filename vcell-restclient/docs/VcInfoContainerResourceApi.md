# VcInfoContainerResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getVCInfoContainer**](VcInfoContainerResourceApi.md#getVCInfoContainer) | **GET** /api/v1/vcInfoContainer | Return a single bulk collection of metadata summaries (BioModels, MathModels, Geometries, Images) visible to the requester. Anonymous requests return public records only; an authenticated request additionally includes the requester&#39;s own and shared records. |
| [**getVCInfoContainerWithHttpInfo**](VcInfoContainerResourceApi.md#getVCInfoContainerWithHttpInfo) | **GET** /api/v1/vcInfoContainer | Return a single bulk collection of metadata summaries (BioModels, MathModels, Geometries, Images) visible to the requester. Anonymous requests return public records only; an authenticated request additionally includes the requester&#39;s own and shared records. |



## getVCInfoContainer

> VCInfoContainerSummary getVCInfoContainer()

Return a single bulk collection of metadata summaries (BioModels, MathModels, Geometries, Images) visible to the requester. Anonymous requests return public records only; an authenticated request additionally includes the requester&#39;s own and shared records.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcInfoContainerResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcInfoContainerResourceApi apiInstance = new VcInfoContainerResourceApi(defaultClient);
        try {
            VCInfoContainerSummary result = apiInstance.getVCInfoContainer();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling VcInfoContainerResourceApi#getVCInfoContainer");
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

[**VCInfoContainerSummary**](VCInfoContainerSummary.md)


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

## getVCInfoContainerWithHttpInfo

> ApiResponse<VCInfoContainerSummary> getVCInfoContainer getVCInfoContainerWithHttpInfo()

Return a single bulk collection of metadata summaries (BioModels, MathModels, Geometries, Images) visible to the requester. Anonymous requests return public records only; an authenticated request additionally includes the requester&#39;s own and shared records.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.VcInfoContainerResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        VcInfoContainerResourceApi apiInstance = new VcInfoContainerResourceApi(defaultClient);
        try {
            ApiResponse<VCInfoContainerSummary> response = apiInstance.getVCInfoContainerWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling VcInfoContainerResourceApi#getVCInfoContainer");
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

ApiResponse<[**VCInfoContainerSummary**](VCInfoContainerSummary.md)>


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

