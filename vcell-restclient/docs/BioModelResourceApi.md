# BioModelResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteBioModel**](BioModelResourceApi.md#deleteBioModel) | **DELETE** /api/v1/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database. |
| [**deleteBioModelWithHttpInfo**](BioModelResourceApi.md#deleteBioModelWithHttpInfo) | **DELETE** /api/v1/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database. |
| [**getBioModel**](BioModelResourceApi.md#getBioModel) | **GET** /api/v1/bioModel/{bioModelID} | Get BioModel. |
| [**getBioModelWithHttpInfo**](BioModelResourceApi.md#getBioModelWithHttpInfo) | **GET** /api/v1/bioModel/{bioModelID} | Get BioModel. |
| [**getBioModelSummaries**](BioModelResourceApi.md#getBioModelSummaries) | **GET** /api/v1/bioModel/summaries | Return BioModel summaries. |
| [**getBioModelSummariesWithHttpInfo**](BioModelResourceApi.md#getBioModelSummariesWithHttpInfo) | **GET** /api/v1/bioModel/summaries | Return BioModel summaries. |
| [**getBioModelSummary**](BioModelResourceApi.md#getBioModelSummary) | **GET** /api/v1/bioModel/{bioModelID}/summary | All of the text based information about a BioModel (summary, version, publication status, etc...), but not the actual BioModel itself. |
| [**getBioModelSummaryWithHttpInfo**](BioModelResourceApi.md#getBioModelSummaryWithHttpInfo) | **GET** /api/v1/bioModel/{bioModelID}/summary | All of the text based information about a BioModel (summary, version, publication status, etc...), but not the actual BioModel itself. |
| [**getBioModelVCML**](BioModelResourceApi.md#getBioModelVCML) | **GET** /api/v1/bioModel/{bioModelID}/vcml_download | Get the BioModel in VCML format. |
| [**getBioModelVCMLWithHttpInfo**](BioModelResourceApi.md#getBioModelVCMLWithHttpInfo) | **GET** /api/v1/bioModel/{bioModelID}/vcml_download | Get the BioModel in VCML format. |
| [**saveBioModel**](BioModelResourceApi.md#saveBioModel) | **POST** /api/v1/bioModel/save | Save&#39;s the given BioModel. Optional parameters of name and simulations to update due to math changes. Returns saved BioModel as VCML. |
| [**saveBioModelWithHttpInfo**](BioModelResourceApi.md#saveBioModelWithHttpInfo) | **POST** /api/v1/bioModel/save | Save&#39;s the given BioModel. Optional parameters of name and simulations to update due to math changes. Returns saved BioModel as VCML. |



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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

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
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

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
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |


## getBioModel

> BioModel getBioModel(bioModelID)

Get BioModel.

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            BioModel result = apiInstance.getBioModel(bioModelID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModel");
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
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |

## getBioModelWithHttpInfo

> ApiResponse<BioModel> getBioModel getBioModelWithHttpInfo(bioModelID)

Get BioModel.

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<BioModel> response = apiInstance.getBioModelWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModel");
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
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |


## getBioModelSummaries

> List<BioModelSummary> getBioModelSummaries(includePublicAndShared)

Return BioModel summaries.

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        Boolean includePublicAndShared = true; // Boolean | Includes BioModel summaries that are public or shared with requester.
        try {
            List<BioModelSummary> result = apiInstance.getBioModelSummaries(includePublicAndShared);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelSummaries");
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
| **includePublicAndShared** | **Boolean**| Includes BioModel summaries that are public or shared with requester. | [optional] |

### Return type

[**List&lt;BioModelSummary&gt;**](BioModelSummary.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **500** | Data Access Exception |  -  |

## getBioModelSummariesWithHttpInfo

> ApiResponse<List<BioModelSummary>> getBioModelSummaries getBioModelSummariesWithHttpInfo(includePublicAndShared)

Return BioModel summaries.

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        Boolean includePublicAndShared = true; // Boolean | Includes BioModel summaries that are public or shared with requester.
        try {
            ApiResponse<List<BioModelSummary>> response = apiInstance.getBioModelSummariesWithHttpInfo(includePublicAndShared);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelSummaries");
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
| **includePublicAndShared** | **Boolean**| Includes BioModel summaries that are public or shared with requester. | [optional] |

### Return type

ApiResponse<[**List&lt;BioModelSummary&gt;**](BioModelSummary.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **500** | Data Access Exception |  -  |


## getBioModelSummary

> BioModelSummary getBioModelSummary(bioModelID)

All of the text based information about a BioModel (summary, version, publication status, etc...), but not the actual BioModel itself.

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            BioModelSummary result = apiInstance.getBioModelSummary(bioModelID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelSummary");
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

[**BioModelSummary**](BioModelSummary.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |

## getBioModelSummaryWithHttpInfo

> ApiResponse<BioModelSummary> getBioModelSummary getBioModelSummaryWithHttpInfo(bioModelID)

All of the text based information about a BioModel (summary, version, publication status, etc...), but not the actual BioModel itself.

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<BioModelSummary> response = apiInstance.getBioModelSummaryWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelSummary");
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

ApiResponse<[**BioModelSummary**](BioModelSummary.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **500** | Data Access Exception |  -  |


## getBioModelVCML

> String getBioModelVCML(bioModelID)

Get the BioModel in VCML format.

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            String result = apiInstance.getBioModelVCML(bioModelID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelVCML");
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

**String**


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |

## getBioModelVCMLWithHttpInfo

> ApiResponse<String> getBioModelVCML getBioModelVCMLWithHttpInfo(bioModelID)

Get the BioModel in VCML format.

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
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<String> response = apiInstance.getBioModelVCMLWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelVCML");
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

ApiResponse<**String**>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: text/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **404** | Not found |  -  |
| **500** | Data Access Exception |  -  |


## saveBioModel

> String saveBioModel(saveBioModel)

Save&#39;s the given BioModel. Optional parameters of name and simulations to update due to math changes. Returns saved BioModel as VCML.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.BioModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        SaveBioModel saveBioModel = new SaveBioModel(); // SaveBioModel | 
        try {
            String result = apiInstance.saveBioModel(saveBioModel);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#saveBioModel");
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
| **saveBioModel** | [**SaveBioModel**](SaveBioModel.md)|  | [optional] |

### Return type

**String**


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |

## saveBioModelWithHttpInfo

> ApiResponse<String> saveBioModel saveBioModelWithHttpInfo(saveBioModel)

Save&#39;s the given BioModel. Optional parameters of name and simulations to update due to math changes. Returns saved BioModel as VCML.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.BioModelResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        SaveBioModel saveBioModel = new SaveBioModel(); // SaveBioModel | 
        try {
            ApiResponse<String> response = apiInstance.saveBioModelWithHttpInfo(saveBioModel);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#saveBioModel");
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
| **saveBioModel** | [**SaveBioModel**](SaveBioModel.md)|  | [optional] |

### Return type

ApiResponse<**String**>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |
| **422** | Unprocessable content submitted |  -  |
| **500** | Data Access Exception |  -  |

