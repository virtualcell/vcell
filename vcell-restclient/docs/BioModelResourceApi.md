# BioModelResourceApi

All URIs are relative to *https://vcellapi-test.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteBioModel**](BioModelResourceApi.md#deleteBioModel) | **DELETE** /api/v1/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database. |
| [**deleteBioModelWithHttpInfo**](BioModelResourceApi.md#deleteBioModelWithHttpInfo) | **DELETE** /api/v1/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database. |
| [**getBiomodelById**](BioModelResourceApi.md#getBiomodelById) | **GET** /api/v1/bioModel/{bioModelID} | Get BioModel information in JSON format by ID. |
| [**getBiomodelByIdWithHttpInfo**](BioModelResourceApi.md#getBiomodelByIdWithHttpInfo) | **GET** /api/v1/bioModel/{bioModelID} | Get BioModel information in JSON format by ID. |
| [**uploadBioModel**](BioModelResourceApi.md#uploadBioModel) | **POST** /api/v1/bioModel/upload_bioModel | Upload the BioModel to VCell database. Returns BioModel ID. |
| [**uploadBioModelWithHttpInfo**](BioModelResourceApi.md#uploadBioModelWithHttpInfo) | **POST** /api/v1/bioModel/upload_bioModel | Upload the BioModel to VCell database. Returns BioModel ID. |



## deleteBioModel

> void deleteBioModel(bioModelID)

Delete the BioModel from VCell&#39;s database.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.BioModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            apiInstance.deleteBioModel(bioModelID);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#deleteBioModel");
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
| **bioModelID** | **String**|  | |

### Return type


null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |

## deleteBioModelWithHttpInfo

> ApiResponse<Void> deleteBioModel deleteBioModelWithHttpInfo(bioModelID)

Delete the BioModel from VCell&#39;s database.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.BioModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.deleteBioModelWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#deleteBioModel");
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
| **bioModelID** | **String**|  | |

### Return type


ApiResponse<Void>

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |


## getBiomodelById

> BioModel getBiomodelById(bioModelID)

Get BioModel information in JSON format by ID.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.BioModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            BioModel result = apiInstance.getBiomodelById(bioModelID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBiomodelById");
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
| **bioModelID** | **String**|  | |

### Return type

[**BioModel**](BioModel.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## getBiomodelByIdWithHttpInfo

> ApiResponse<BioModel> getBiomodelById getBiomodelByIdWithHttpInfo(bioModelID)

Get BioModel information in JSON format by ID.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.BioModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<BioModel> response = apiInstance.getBiomodelByIdWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBiomodelById");
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
| **bioModelID** | **String**|  | |

### Return type

ApiResponse<[**BioModel**](BioModel.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## uploadBioModel

> String uploadBioModel(body)

Upload the BioModel to VCell database. Returns BioModel ID.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.BioModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String body = "body_example"; // String | 
        try {
            String result = apiInstance.uploadBioModel(body);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#uploadBioModel");
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
| **body** | **String**|  | [optional] |

### Return type

**String**


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: text/xml
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## uploadBioModelWithHttpInfo

> ApiResponse<String> uploadBioModel uploadBioModelWithHttpInfo(body)

Upload the BioModel to VCell database. Returns BioModel ID.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.BioModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String body = "body_example"; // String | 
        try {
            ApiResponse<String> response = apiInstance.uploadBioModelWithHttpInfo(body);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#uploadBioModel");
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
| **body** | **String**|  | [optional] |

### Return type

ApiResponse<**String**>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: text/xml
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

