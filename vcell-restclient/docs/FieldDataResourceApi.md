# FieldDataResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**copyFieldData**](FieldDataResourceApi.md#copyFieldData) | **POST** /api/v1/fieldData/copy | Copy an existing field data entry. |
| [**copyFieldDataWithHttpInfo**](FieldDataResourceApi.md#copyFieldDataWithHttpInfo) | **POST** /api/v1/fieldData/copy | Copy an existing field data entry. |
| [**createNewFieldDataFromFileAlreadyAnalyzed**](FieldDataResourceApi.md#createNewFieldDataFromFileAlreadyAnalyzed) | **POST** /api/v1/fieldData/createFieldDataFromFileAlreadyAnalyzed |  |
| [**createNewFieldDataFromFileAlreadyAnalyzedWithHttpInfo**](FieldDataResourceApi.md#createNewFieldDataFromFileAlreadyAnalyzedWithHttpInfo) | **POST** /api/v1/fieldData/createFieldDataFromFileAlreadyAnalyzed |  |
| [**createNewFieldDataFromSimulation**](FieldDataResourceApi.md#createNewFieldDataFromSimulation) | **POST** /api/v1/fieldData/createFieldDataFromSimulation | Create new field data from a simulation. |
| [**createNewFieldDataFromSimulationWithHttpInfo**](FieldDataResourceApi.md#createNewFieldDataFromSimulationWithHttpInfo) | **POST** /api/v1/fieldData/createFieldDataFromSimulation | Create new field data from a simulation. |
| [**deleteFieldData**](FieldDataResourceApi.md#deleteFieldData) | **DELETE** /api/v1/fieldData | Delete the selected field data. |
| [**deleteFieldDataWithHttpInfo**](FieldDataResourceApi.md#deleteFieldDataWithHttpInfo) | **DELETE** /api/v1/fieldData | Delete the selected field data. |
| [**generateFieldDataEstimate**](FieldDataResourceApi.md#generateFieldDataEstimate) | **POST** /api/v1/fieldData/analyzeFieldDataFromFile |  |
| [**generateFieldDataEstimateWithHttpInfo**](FieldDataResourceApi.md#generateFieldDataEstimateWithHttpInfo) | **POST** /api/v1/fieldData/analyzeFieldDataFromFile |  |
| [**getAllFieldDataIDs**](FieldDataResourceApi.md#getAllFieldDataIDs) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data. |
| [**getAllFieldDataIDsWithHttpInfo**](FieldDataResourceApi.md#getAllFieldDataIDsWithHttpInfo) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data. |
| [**getFieldDataFromID**](FieldDataResourceApi.md#getFieldDataFromID) | **GET** /api/v1/fieldData | Get the field data from the selected field data ID. |
| [**getFieldDataFromIDWithHttpInfo**](FieldDataResourceApi.md#getFieldDataFromIDWithHttpInfo) | **GET** /api/v1/fieldData | Get the field data from the selected field data ID. |



## copyFieldData

> FieldDataNoCopyConflict copyFieldData(fieldDataDBOperationSpec)

Copy an existing field data entry.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        FieldDataDBOperationSpec fieldDataDBOperationSpec = new FieldDataDBOperationSpec(); // FieldDataDBOperationSpec | 
        try {
            FieldDataNoCopyConflict result = apiInstance.copyFieldData(fieldDataDBOperationSpec);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#copyFieldData");
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
| **fieldDataDBOperationSpec** | [**FieldDataDBOperationSpec**](FieldDataDBOperationSpec.md)|  | [optional] |

### Return type

[**FieldDataNoCopyConflict**](FieldDataNoCopyConflict.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## copyFieldDataWithHttpInfo

> ApiResponse<FieldDataNoCopyConflict> copyFieldData copyFieldDataWithHttpInfo(fieldDataDBOperationSpec)

Copy an existing field data entry.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        FieldDataDBOperationSpec fieldDataDBOperationSpec = new FieldDataDBOperationSpec(); // FieldDataDBOperationSpec | 
        try {
            ApiResponse<FieldDataNoCopyConflict> response = apiInstance.copyFieldDataWithHttpInfo(fieldDataDBOperationSpec);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#copyFieldData");
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
| **fieldDataDBOperationSpec** | [**FieldDataDBOperationSpec**](FieldDataDBOperationSpec.md)|  | [optional] |

### Return type

ApiResponse<[**FieldDataNoCopyConflict**](FieldDataNoCopyConflict.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## createNewFieldDataFromFileAlreadyAnalyzed

> FieldDataSaveResults createNewFieldDataFromFileAlreadyAnalyzed(analyzedResultsFromFieldData)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        AnalyzedResultsFromFieldData analyzedResultsFromFieldData = new AnalyzedResultsFromFieldData(); // AnalyzedResultsFromFieldData | 
        try {
            FieldDataSaveResults result = apiInstance.createNewFieldDataFromFileAlreadyAnalyzed(analyzedResultsFromFieldData);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createNewFieldDataFromFileAlreadyAnalyzed");
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
| **analyzedResultsFromFieldData** | [**AnalyzedResultsFromFieldData**](AnalyzedResultsFromFieldData.md)|  | [optional] |

### Return type

[**FieldDataSaveResults**](FieldDataSaveResults.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## createNewFieldDataFromFileAlreadyAnalyzedWithHttpInfo

> ApiResponse<FieldDataSaveResults> createNewFieldDataFromFileAlreadyAnalyzed createNewFieldDataFromFileAlreadyAnalyzedWithHttpInfo(analyzedResultsFromFieldData)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        AnalyzedResultsFromFieldData analyzedResultsFromFieldData = new AnalyzedResultsFromFieldData(); // AnalyzedResultsFromFieldData | 
        try {
            ApiResponse<FieldDataSaveResults> response = apiInstance.createNewFieldDataFromFileAlreadyAnalyzedWithHttpInfo(analyzedResultsFromFieldData);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createNewFieldDataFromFileAlreadyAnalyzed");
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
| **analyzedResultsFromFieldData** | [**AnalyzedResultsFromFieldData**](AnalyzedResultsFromFieldData.md)|  | [optional] |

### Return type

ApiResponse<[**FieldDataSaveResults**](FieldDataSaveResults.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## createNewFieldDataFromSimulation

> ExternalDataIdentifier createNewFieldDataFromSimulation(fieldDataDBOperationSpec)

Create new field data from a simulation.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        FieldDataDBOperationSpec fieldDataDBOperationSpec = new FieldDataDBOperationSpec(); // FieldDataDBOperationSpec | 
        try {
            ExternalDataIdentifier result = apiInstance.createNewFieldDataFromSimulation(fieldDataDBOperationSpec);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createNewFieldDataFromSimulation");
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
| **fieldDataDBOperationSpec** | [**FieldDataDBOperationSpec**](FieldDataDBOperationSpec.md)|  | [optional] |

### Return type

[**ExternalDataIdentifier**](ExternalDataIdentifier.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## createNewFieldDataFromSimulationWithHttpInfo

> ApiResponse<ExternalDataIdentifier> createNewFieldDataFromSimulation createNewFieldDataFromSimulationWithHttpInfo(fieldDataDBOperationSpec)

Create new field data from a simulation.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        FieldDataDBOperationSpec fieldDataDBOperationSpec = new FieldDataDBOperationSpec(); // FieldDataDBOperationSpec | 
        try {
            ApiResponse<ExternalDataIdentifier> response = apiInstance.createNewFieldDataFromSimulationWithHttpInfo(fieldDataDBOperationSpec);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createNewFieldDataFromSimulation");
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
| **fieldDataDBOperationSpec** | [**FieldDataDBOperationSpec**](FieldDataDBOperationSpec.md)|  | [optional] |

### Return type

ApiResponse<[**ExternalDataIdentifier**](ExternalDataIdentifier.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## deleteFieldData

> void deleteFieldData(body)

Delete the selected field data.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String body = "body_example"; // String | 
        try {
            apiInstance.deleteFieldData(body);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#deleteFieldData");
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


null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: text/plain
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |

## deleteFieldDataWithHttpInfo

> ApiResponse<Void> deleteFieldData deleteFieldDataWithHttpInfo(body)

Delete the selected field data.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String body = "body_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.deleteFieldDataWithHttpInfo(body);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#deleteFieldData");
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


ApiResponse<Void>

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: text/plain
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |


## generateFieldDataEstimate

> FieldDataFileOperationSpec generateFieldDataEstimate(_file, fileName)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        File _file = new File("/path/to/file"); // File | 
        String fileName = "fileName_example"; // String | 
        try {
            FieldDataFileOperationSpec result = apiInstance.generateFieldDataEstimate(_file, fileName);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#generateFieldDataEstimate");
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
| **_file** | **File**|  | [optional] |
| **fileName** | **String**|  | [optional] |

### Return type

[**FieldDataFileOperationSpec**](FieldDataFileOperationSpec.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## generateFieldDataEstimateWithHttpInfo

> ApiResponse<FieldDataFileOperationSpec> generateFieldDataEstimate generateFieldDataEstimateWithHttpInfo(_file, fileName)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        File _file = new File("/path/to/file"); // File | 
        String fileName = "fileName_example"; // String | 
        try {
            ApiResponse<FieldDataFileOperationSpec> response = apiInstance.generateFieldDataEstimateWithHttpInfo(_file, fileName);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#generateFieldDataEstimate");
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
| **_file** | **File**|  | [optional] |
| **fileName** | **String**|  | [optional] |

### Return type

ApiResponse<[**FieldDataFileOperationSpec**](FieldDataFileOperationSpec.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## getAllFieldDataIDs

> FieldDataReferences getAllFieldDataIDs()

Get all of the ids used to identify, and retrieve field data.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        try {
            FieldDataReferences result = apiInstance.getAllFieldDataIDs();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getAllFieldDataIDs");
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

[**FieldDataReferences**](FieldDataReferences.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## getAllFieldDataIDsWithHttpInfo

> ApiResponse<FieldDataReferences> getAllFieldDataIDs getAllFieldDataIDsWithHttpInfo()

Get all of the ids used to identify, and retrieve field data.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        try {
            ApiResponse<FieldDataReferences> response = apiInstance.getAllFieldDataIDsWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getAllFieldDataIDs");
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

ApiResponse<[**FieldDataReferences**](FieldDataReferences.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## getFieldDataFromID

> FieldDataInfo getFieldDataFromID(body)

Get the field data from the selected field data ID.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String body = "body_example"; // String | 
        try {
            FieldDataInfo result = apiInstance.getFieldDataFromID(body);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getFieldDataFromID");
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

[**FieldDataInfo**](FieldDataInfo.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: text/plain
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## getFieldDataFromIDWithHttpInfo

> ApiResponse<FieldDataInfo> getFieldDataFromID getFieldDataFromIDWithHttpInfo(body)

Get the field data from the selected field data ID.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String body = "body_example"; // String | 
        try {
            ApiResponse<FieldDataInfo> response = apiInstance.getFieldDataFromIDWithHttpInfo(body);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getFieldDataFromID");
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

ApiResponse<[**FieldDataInfo**](FieldDataInfo.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: text/plain
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

