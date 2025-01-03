# FieldDataResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**analyzeFieldDataFile**](FieldDataResourceApi.md#analyzeFieldDataFile) | **POST** /api/v1/fieldData/analyzeFieldDataFile | Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores. |
| [**analyzeFieldDataFileWithHttpInfo**](FieldDataResourceApi.md#analyzeFieldDataFileWithHttpInfo) | **POST** /api/v1/fieldData/analyzeFieldDataFile | Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores. |
| [**createFieldDataFromAnalyzedFile**](FieldDataResourceApi.md#createFieldDataFromAnalyzedFile) | **POST** /api/v1/fieldData/createFieldDataFromAnalyzedFile | Take the analyzed results of the field data, modify it to your liking, then save it on the server. |
| [**createFieldDataFromAnalyzedFileWithHttpInfo**](FieldDataResourceApi.md#createFieldDataFromAnalyzedFileWithHttpInfo) | **POST** /api/v1/fieldData/createFieldDataFromAnalyzedFile | Take the analyzed results of the field data, modify it to your liking, then save it on the server. |
| [**deleteFieldData**](FieldDataResourceApi.md#deleteFieldData) | **DELETE** /api/v1/fieldData/delete/{fieldDataID} | Delete the selected field data. |
| [**deleteFieldDataWithHttpInfo**](FieldDataResourceApi.md#deleteFieldDataWithHttpInfo) | **DELETE** /api/v1/fieldData/delete/{fieldDataID} | Delete the selected field data. |
| [**getAllFieldDataIDs**](FieldDataResourceApi.md#getAllFieldDataIDs) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data. |
| [**getAllFieldDataIDsWithHttpInfo**](FieldDataResourceApi.md#getAllFieldDataIDsWithHttpInfo) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data. |
| [**getFieldDataShapeFromID**](FieldDataResourceApi.md#getFieldDataShapeFromID) | **GET** /api/v1/fieldData/fieldDataShape/{fieldDataID} | Get the shape of the field data. That is it&#39;s size, origin, extent, and data identifiers. |
| [**getFieldDataShapeFromIDWithHttpInfo**](FieldDataResourceApi.md#getFieldDataShapeFromIDWithHttpInfo) | **GET** /api/v1/fieldData/fieldDataShape/{fieldDataID} | Get the shape of the field data. That is it&#39;s size, origin, extent, and data identifiers. |



## analyzeFieldDataFile

> AnalyzedResultsFromFieldData analyzeFieldDataFile(_file, fileName)

Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores.

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
            AnalyzedResultsFromFieldData result = apiInstance.analyzeFieldDataFile(_file, fileName);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#analyzeFieldDataFile");
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

[**AnalyzedResultsFromFieldData**](AnalyzedResultsFromFieldData.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## analyzeFieldDataFileWithHttpInfo

> ApiResponse<AnalyzedResultsFromFieldData> analyzeFieldDataFile analyzeFieldDataFileWithHttpInfo(_file, fileName)

Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores.

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
            ApiResponse<AnalyzedResultsFromFieldData> response = apiInstance.analyzeFieldDataFileWithHttpInfo(_file, fileName);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#analyzeFieldDataFile");
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

ApiResponse<[**AnalyzedResultsFromFieldData**](AnalyzedResultsFromFieldData.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## createFieldDataFromAnalyzedFile

> FieldDataSaveResults createFieldDataFromAnalyzedFile(analyzedResultsFromFieldData)

Take the analyzed results of the field data, modify it to your liking, then save it on the server.

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
            FieldDataSaveResults result = apiInstance.createFieldDataFromAnalyzedFile(analyzedResultsFromFieldData);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createFieldDataFromAnalyzedFile");
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

## createFieldDataFromAnalyzedFileWithHttpInfo

> ApiResponse<FieldDataSaveResults> createFieldDataFromAnalyzedFile createFieldDataFromAnalyzedFileWithHttpInfo(analyzedResultsFromFieldData)

Take the analyzed results of the field data, modify it to your liking, then save it on the server.

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
            ApiResponse<FieldDataSaveResults> response = apiInstance.createFieldDataFromAnalyzedFileWithHttpInfo(analyzedResultsFromFieldData);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createFieldDataFromAnalyzedFile");
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


## deleteFieldData

> void deleteFieldData(fieldDataID)

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
        String fieldDataID = "fieldDataID_example"; // String | 
        try {
            apiInstance.deleteFieldData(fieldDataID);
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
| **fieldDataID** | **String**|  | |

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

## deleteFieldDataWithHttpInfo

> ApiResponse<Void> deleteFieldData deleteFieldDataWithHttpInfo(fieldDataID)

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
        String fieldDataID = "fieldDataID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.deleteFieldDataWithHttpInfo(fieldDataID);
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
| **fieldDataID** | **String**|  | |

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


## getAllFieldDataIDs

> List<FieldDataReference> getAllFieldDataIDs()

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
            List<FieldDataReference> result = apiInstance.getAllFieldDataIDs();
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

[**List&lt;FieldDataReference&gt;**](FieldDataReference.md)


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

> ApiResponse<List<FieldDataReference>> getAllFieldDataIDs getAllFieldDataIDsWithHttpInfo()

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
            ApiResponse<List<FieldDataReference>> response = apiInstance.getAllFieldDataIDsWithHttpInfo();
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

ApiResponse<[**List&lt;FieldDataReference&gt;**](FieldDataReference.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## getFieldDataShapeFromID

> FieldDataShape getFieldDataShapeFromID(fieldDataID)

Get the shape of the field data. That is it&#39;s size, origin, extent, and data identifiers.

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
        String fieldDataID = "fieldDataID_example"; // String | 
        try {
            FieldDataShape result = apiInstance.getFieldDataShapeFromID(fieldDataID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getFieldDataShapeFromID");
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
| **fieldDataID** | **String**|  | |

### Return type

[**FieldDataShape**](FieldDataShape.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## getFieldDataShapeFromIDWithHttpInfo

> ApiResponse<FieldDataShape> getFieldDataShapeFromID getFieldDataShapeFromIDWithHttpInfo(fieldDataID)

Get the shape of the field data. That is it&#39;s size, origin, extent, and data identifiers.

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
        String fieldDataID = "fieldDataID_example"; // String | 
        try {
            ApiResponse<FieldDataShape> response = apiInstance.getFieldDataShapeFromIDWithHttpInfo(fieldDataID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getFieldDataShapeFromID");
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
| **fieldDataID** | **String**|  | |

### Return type

ApiResponse<[**FieldDataShape**](FieldDataShape.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

