# FieldDataResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**advancedCreate**](FieldDataResourceApi.md#advancedCreate) | **POST** /api/v1/fieldData/advancedCreate | Combines the two separate requests of Analyze File and Save. The following files are accepted: .tif and .zip. |
| [**advancedCreateWithHttpInfo**](FieldDataResourceApi.md#advancedCreateWithHttpInfo) | **POST** /api/v1/fieldData/advancedCreate | Combines the two separate requests of Analyze File and Save. The following files are accepted: .tif and .zip. |
| [**analyzeFile**](FieldDataResourceApi.md#analyzeFile) | **POST** /api/v1/fieldData/analyzeFile | Analyze uploaded image file (Tiff, Zip, and Non-GPL BioFormats) and return field data. Color mapped images not supported (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores. |
| [**analyzeFileWithHttpInfo**](FieldDataResourceApi.md#analyzeFileWithHttpInfo) | **POST** /api/v1/fieldData/analyzeFile | Analyze uploaded image file (Tiff, Zip, and Non-GPL BioFormats) and return field data. Color mapped images not supported (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores. |
| [**copyModelsFieldData**](FieldDataResourceApi.md#copyModelsFieldData) | **POST** /api/v1/fieldData/copyModelsFieldData | Copy all existing field data from a BioModel/MathModel that you have access to, but don&#39;t own. |
| [**copyModelsFieldDataWithHttpInfo**](FieldDataResourceApi.md#copyModelsFieldDataWithHttpInfo) | **POST** /api/v1/fieldData/copyModelsFieldData | Copy all existing field data from a BioModel/MathModel that you have access to, but don&#39;t own. |
| [**createFromFile**](FieldDataResourceApi.md#createFromFile) | **POST** /api/v1/fieldData/createFromFile | Submit a file that converts into field data, with all defaults derived from the file submitted. |
| [**createFromFileWithHttpInfo**](FieldDataResourceApi.md#createFromFileWithHttpInfo) | **POST** /api/v1/fieldData/createFromFile | Submit a file that converts into field data, with all defaults derived from the file submitted. |
| [**createFromSimulation**](FieldDataResourceApi.md#createFromSimulation) | **POST** /api/v1/fieldData/createFromSimulation | Create new field data from existing simulation results. |
| [**createFromSimulationWithHttpInfo**](FieldDataResourceApi.md#createFromSimulationWithHttpInfo) | **POST** /api/v1/fieldData/createFromSimulation | Create new field data from existing simulation results. |
| [**delete**](FieldDataResourceApi.md#delete) | **DELETE** /api/v1/fieldData/delete/{fieldDataID} | Delete the selected field data. |
| [**deleteWithHttpInfo**](FieldDataResourceApi.md#deleteWithHttpInfo) | **DELETE** /api/v1/fieldData/delete/{fieldDataID} | Delete the selected field data. |
| [**getAllIDs**](FieldDataResourceApi.md#getAllIDs) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data. |
| [**getAllIDsWithHttpInfo**](FieldDataResourceApi.md#getAllIDsWithHttpInfo) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data. |
| [**getShapeFromID**](FieldDataResourceApi.md#getShapeFromID) | **GET** /api/v1/fieldData/shape/{fieldDataID} | Get the shape of the field data. That is it&#39;s size, origin, extent, times, and data identifiers. |
| [**getShapeFromIDWithHttpInfo**](FieldDataResourceApi.md#getShapeFromIDWithHttpInfo) | **GET** /api/v1/fieldData/shape/{fieldDataID} | Get the shape of the field data. That is it&#39;s size, origin, extent, times, and data identifiers. |
| [**save**](FieldDataResourceApi.md#save) | **POST** /api/v1/fieldData/save | Take the generated field data, and save it to the server. User may adjust the analyzed file before uploading to edit defaults. |
| [**saveWithHttpInfo**](FieldDataResourceApi.md#saveWithHttpInfo) | **POST** /api/v1/fieldData/save | Take the generated field data, and save it to the server. User may adjust the analyzed file before uploading to edit defaults. |



## advancedCreate

> FieldDataSavedResults advancedCreate(_file, fileName, extent, iSize, channelNames, times, annotation, origin)

Combines the two separate requests of Analyze File and Save. The following files are accepted: .tif and .zip.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        File _file = new File("/path/to/file"); // File | 
        String fileName = "fileName_example"; // String | 
        Extent extent = new Extent(); // Extent | 
        ISize iSize = new ISize(); // ISize | 
        List<String> channelNames = Arrays.asList(); // List<String> | 
        List<Double> times = Arrays.asList(); // List<Double> | 
        String annotation = "annotation_example"; // String | 
        Origin origin = new Origin(); // Origin | 
        try {
            FieldDataSavedResults result = apiInstance.advancedCreate(_file, fileName, extent, iSize, channelNames, times, annotation, origin);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#advancedCreate");
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
| **extent** | [**Extent**](Extent.md)|  | [optional] |
| **iSize** | [**ISize**](ISize.md)|  | [optional] |
| **channelNames** | [**List&lt;String&gt;**](String.md)|  | [optional] |
| **times** | [**List&lt;Double&gt;**](Double.md)|  | [optional] |
| **annotation** | **String**|  | [optional] |
| **origin** | [**Origin**](Origin.md)|  | [optional] |

### Return type

[**FieldDataSavedResults**](FieldDataSavedResults.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## advancedCreateWithHttpInfo

> ApiResponse<FieldDataSavedResults> advancedCreate advancedCreateWithHttpInfo(_file, fileName, extent, iSize, channelNames, times, annotation, origin)

Combines the two separate requests of Analyze File and Save. The following files are accepted: .tif and .zip.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        File _file = new File("/path/to/file"); // File | 
        String fileName = "fileName_example"; // String | 
        Extent extent = new Extent(); // Extent | 
        ISize iSize = new ISize(); // ISize | 
        List<String> channelNames = Arrays.asList(); // List<String> | 
        List<Double> times = Arrays.asList(); // List<Double> | 
        String annotation = "annotation_example"; // String | 
        Origin origin = new Origin(); // Origin | 
        try {
            ApiResponse<FieldDataSavedResults> response = apiInstance.advancedCreateWithHttpInfo(_file, fileName, extent, iSize, channelNames, times, annotation, origin);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#advancedCreate");
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
| **extent** | [**Extent**](Extent.md)|  | [optional] |
| **iSize** | [**ISize**](ISize.md)|  | [optional] |
| **channelNames** | [**List&lt;String&gt;**](String.md)|  | [optional] |
| **times** | [**List&lt;Double&gt;**](Double.md)|  | [optional] |
| **annotation** | **String**|  | [optional] |
| **origin** | [**Origin**](Origin.md)|  | [optional] |

### Return type

ApiResponse<[**FieldDataSavedResults**](FieldDataSavedResults.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## analyzeFile

> FieldData analyzeFile(_file, fileName)

Analyze uploaded image file (Tiff, Zip, and Non-GPL BioFormats) and return field data. Color mapped images not supported (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
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
            FieldData result = apiInstance.analyzeFile(_file, fileName);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#analyzeFile");
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

[**FieldData**](FieldData.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## analyzeFileWithHttpInfo

> ApiResponse<FieldData> analyzeFile analyzeFileWithHttpInfo(_file, fileName)

Analyze uploaded image file (Tiff, Zip, and Non-GPL BioFormats) and return field data. Color mapped images not supported (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
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
            ApiResponse<FieldData> response = apiInstance.analyzeFileWithHttpInfo(_file, fileName);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#analyzeFile");
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

ApiResponse<[**FieldData**](FieldData.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## copyModelsFieldData

> Map<String, ExternalDataIdentifier> copyModelsFieldData(sourceModel)

Copy all existing field data from a BioModel/MathModel that you have access to, but don&#39;t own.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        SourceModel sourceModel = new SourceModel(); // SourceModel | 
        try {
            Map<String, ExternalDataIdentifier> result = apiInstance.copyModelsFieldData(sourceModel);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#copyModelsFieldData");
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
| **sourceModel** | [**SourceModel**](SourceModel.md)|  | [optional] |

### Return type

[**Map&lt;String, ExternalDataIdentifier&gt;**](ExternalDataIdentifier.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## copyModelsFieldDataWithHttpInfo

> ApiResponse<Map<String, ExternalDataIdentifier>> copyModelsFieldData copyModelsFieldDataWithHttpInfo(sourceModel)

Copy all existing field data from a BioModel/MathModel that you have access to, but don&#39;t own.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        SourceModel sourceModel = new SourceModel(); // SourceModel | 
        try {
            ApiResponse<Map<String, ExternalDataIdentifier>> response = apiInstance.copyModelsFieldDataWithHttpInfo(sourceModel);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#copyModelsFieldData");
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
| **sourceModel** | [**SourceModel**](SourceModel.md)|  | [optional] |

### Return type

ApiResponse<[**Map&lt;String, ExternalDataIdentifier&gt;**](ExternalDataIdentifier.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## createFromFile

> FieldDataSavedResults createFromFile(_file, fieldDataName)

Submit a file that converts into field data, with all defaults derived from the file submitted.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        File _file = new File("/path/to/file"); // File | 
        String fieldDataName = "fieldDataName_example"; // String | 
        try {
            FieldDataSavedResults result = apiInstance.createFromFile(_file, fieldDataName);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createFromFile");
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
| **fieldDataName** | **String**|  | [optional] |

### Return type

[**FieldDataSavedResults**](FieldDataSavedResults.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## createFromFileWithHttpInfo

> ApiResponse<FieldDataSavedResults> createFromFile createFromFileWithHttpInfo(_file, fieldDataName)

Submit a file that converts into field data, with all defaults derived from the file submitted.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        File _file = new File("/path/to/file"); // File | 
        String fieldDataName = "fieldDataName_example"; // String | 
        try {
            ApiResponse<FieldDataSavedResults> response = apiInstance.createFromFileWithHttpInfo(_file, fieldDataName);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createFromFile");
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
| **fieldDataName** | **String**|  | [optional] |

### Return type

ApiResponse<[**FieldDataSavedResults**](FieldDataSavedResults.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## createFromSimulation

> void createFromSimulation(simKeyReference, jobIndex, newFieldDataName)

Create new field data from existing simulation results.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String simKeyReference = "simKeyReference_example"; // String | 
        Integer jobIndex = 56; // Integer | 
        String newFieldDataName = "newFieldDataName_example"; // String | 
        try {
            apiInstance.createFromSimulation(simKeyReference, jobIndex, newFieldDataName);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createFromSimulation");
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
| **simKeyReference** | **String**|  | [optional] |
| **jobIndex** | **Integer**|  | [optional] |
| **newFieldDataName** | **String**|  | [optional] |

### Return type


null (empty response body)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/x-www-form-urlencoded
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Created |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## createFromSimulationWithHttpInfo

> ApiResponse<Void> createFromSimulation createFromSimulationWithHttpInfo(simKeyReference, jobIndex, newFieldDataName)

Create new field data from existing simulation results.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String simKeyReference = "simKeyReference_example"; // String | 
        Integer jobIndex = 56; // Integer | 
        String newFieldDataName = "newFieldDataName_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.createFromSimulationWithHttpInfo(simKeyReference, jobIndex, newFieldDataName);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#createFromSimulation");
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
| **simKeyReference** | **String**|  | [optional] |
| **jobIndex** | **Integer**|  | [optional] |
| **newFieldDataName** | **String**|  | [optional] |

### Return type


ApiResponse<Void>

### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/x-www-form-urlencoded
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Created |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## delete

> void delete(fieldDataID)

Delete the selected field data.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String fieldDataID = "fieldDataID_example"; // String | 
        try {
            apiInstance.delete(fieldDataID);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#delete");
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

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## deleteWithHttpInfo

> ApiResponse<Void> delete deleteWithHttpInfo(fieldDataID)

Delete the selected field data.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String fieldDataID = "fieldDataID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.deleteWithHttpInfo(fieldDataID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#delete");
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

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | No Content |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## getAllIDs

> List<FieldDataReference> getAllIDs()

Get all of the ids used to identify, and retrieve field data.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        try {
            List<FieldDataReference> result = apiInstance.getAllIDs();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getAllIDs");
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

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## getAllIDsWithHttpInfo

> ApiResponse<List<FieldDataReference>> getAllIDs getAllIDsWithHttpInfo()

Get all of the ids used to identify, and retrieve field data.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        try {
            ApiResponse<List<FieldDataReference>> response = apiInstance.getAllIDsWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getAllIDs");
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

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## getShapeFromID

> FieldDataShape getShapeFromID(fieldDataID)

Get the shape of the field data. That is it&#39;s size, origin, extent, times, and data identifiers.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String fieldDataID = "fieldDataID_example"; // String | 
        try {
            FieldDataShape result = apiInstance.getShapeFromID(fieldDataID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getShapeFromID");
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

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## getShapeFromIDWithHttpInfo

> ApiResponse<FieldDataShape> getShapeFromID getShapeFromIDWithHttpInfo(fieldDataID)

Get the shape of the field data. That is it&#39;s size, origin, extent, times, and data identifiers.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        String fieldDataID = "fieldDataID_example"; // String | 
        try {
            ApiResponse<FieldDataShape> response = apiInstance.getShapeFromIDWithHttpInfo(fieldDataID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#getShapeFromID");
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

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## save

> FieldDataSavedResults save(fieldData)

Take the generated field data, and save it to the server. User may adjust the analyzed file before uploading to edit defaults.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        FieldData fieldData = new FieldData(); // FieldData | 
        try {
            FieldDataSavedResults result = apiInstance.save(fieldData);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#save");
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
| **fieldData** | [**FieldData**](FieldData.md)|  | [optional] |

### Return type

[**FieldDataSavedResults**](FieldDataSavedResults.md)


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## saveWithHttpInfo

> ApiResponse<FieldDataSavedResults> save saveWithHttpInfo(fieldData)

Take the generated field data, and save it to the server. User may adjust the analyzed file before uploading to edit defaults.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.FieldDataResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        FieldDataResourceApi apiInstance = new FieldDataResourceApi(defaultClient);
        FieldData fieldData = new FieldData(); // FieldData | 
        try {
            ApiResponse<FieldDataSavedResults> response = apiInstance.saveWithHttpInfo(fieldData);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling FieldDataResourceApi#save");
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
| **fieldData** | [**FieldData**](FieldData.md)|  | [optional] |

### Return type

ApiResponse<[**FieldDataSavedResults**](FieldDataSavedResults.md)>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

