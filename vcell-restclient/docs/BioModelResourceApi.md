# BioModelResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**advancedSaveAsBioModel**](BioModelResourceApi.md#advancedSaveAsBioModel) | **POST** /api/v1/bioModel/advancedSaveAs | Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML. |
| [**advancedSaveAsBioModelWithHttpInfo**](BioModelResourceApi.md#advancedSaveAsBioModelWithHttpInfo) | **POST** /api/v1/bioModel/advancedSaveAs | Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML. |
| [**advancedSaveBioModel**](BioModelResourceApi.md#advancedSaveBioModel) | **POST** /api/v1/bioModel/advancedSave | Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML. |
| [**advancedSaveBioModelWithHttpInfo**](BioModelResourceApi.md#advancedSaveBioModelWithHttpInfo) | **POST** /api/v1/bioModel/advancedSave | Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML. |
| [**deleteBioModel**](BioModelResourceApi.md#deleteBioModel) | **DELETE** /api/v1/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database. |
| [**deleteBioModelWithHttpInfo**](BioModelResourceApi.md#deleteBioModelWithHttpInfo) | **DELETE** /api/v1/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database. |
| [**getBioModel**](BioModelResourceApi.md#getBioModel) | **GET** /api/v1/bioModel/{bioModelID} | Get BioModel. |
| [**getBioModelWithHttpInfo**](BioModelResourceApi.md#getBioModelWithHttpInfo) | **GET** /api/v1/bioModel/{bioModelID} | Get BioModel. |
| [**getBioModelVCML**](BioModelResourceApi.md#getBioModelVCML) | **GET** /api/v1/bioModel/{bioModelID}/vcml_download | Get the BioModel in VCML format. |
| [**getBioModelVCMLWithHttpInfo**](BioModelResourceApi.md#getBioModelVCMLWithHttpInfo) | **GET** /api/v1/bioModel/{bioModelID}/vcml_download | Get the BioModel in VCML format. |
| [**saveBioModel**](BioModelResourceApi.md#saveBioModel) | **POST** /api/v1/bioModel/save | Save the BioModel, returning saved BioModel as VCML. |
| [**saveBioModelWithHttpInfo**](BioModelResourceApi.md#saveBioModelWithHttpInfo) | **POST** /api/v1/bioModel/save | Save the BioModel, returning saved BioModel as VCML. |
| [**saveBioModelAs**](BioModelResourceApi.md#saveBioModelAs) | **POST** /api/v1/bioModel/saveAs | Save as a new BioModel under the name given. Returns saved BioModel as VCML. |
| [**saveBioModelAsWithHttpInfo**](BioModelResourceApi.md#saveBioModelAsWithHttpInfo) | **POST** /api/v1/bioModel/saveAs | Save as a new BioModel under the name given. Returns saved BioModel as VCML. |



## advancedSaveAsBioModel

> String advancedSaveAsBioModel(bioModelXML, name, simsRequiringUpdates)

Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelXML = "bioModelXML_example"; // String | 
        String name = "name_example"; // String | 
        List<String> simsRequiringUpdates = Arrays.asList(); // List<String> | 
        try {
            String result = apiInstance.advancedSaveAsBioModel(bioModelXML, name, simsRequiringUpdates);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#advancedSaveAsBioModel");
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
| **bioModelXML** | **String**|  | [optional] |
| **name** | **String**|  | [optional] |
| **simsRequiringUpdates** | [**List&lt;String&gt;**](String.md)|  | [optional] |

### Return type

**String**


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## advancedSaveAsBioModelWithHttpInfo

> ApiResponse<String> advancedSaveAsBioModel advancedSaveAsBioModelWithHttpInfo(bioModelXML, name, simsRequiringUpdates)

Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelXML = "bioModelXML_example"; // String | 
        String name = "name_example"; // String | 
        List<String> simsRequiringUpdates = Arrays.asList(); // List<String> | 
        try {
            ApiResponse<String> response = apiInstance.advancedSaveAsBioModelWithHttpInfo(bioModelXML, name, simsRequiringUpdates);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#advancedSaveAsBioModel");
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
| **bioModelXML** | **String**|  | [optional] |
| **name** | **String**|  | [optional] |
| **simsRequiringUpdates** | [**List&lt;String&gt;**](String.md)|  | [optional] |

### Return type

ApiResponse<**String**>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## advancedSaveBioModel

> String advancedSaveBioModel(bioModelXML, simsRequiringUpdates)

Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelXML = "bioModelXML_example"; // String | 
        List<String> simsRequiringUpdates = Arrays.asList(); // List<String> | 
        try {
            String result = apiInstance.advancedSaveBioModel(bioModelXML, simsRequiringUpdates);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#advancedSaveBioModel");
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
| **bioModelXML** | **String**|  | [optional] |
| **simsRequiringUpdates** | [**List&lt;String&gt;**](String.md)|  | [optional] |

### Return type

**String**


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## advancedSaveBioModelWithHttpInfo

> ApiResponse<String> advancedSaveBioModel advancedSaveBioModelWithHttpInfo(bioModelXML, simsRequiringUpdates)

Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelXML = "bioModelXML_example"; // String | 
        List<String> simsRequiringUpdates = Arrays.asList(); // List<String> | 
        try {
            ApiResponse<String> response = apiInstance.advancedSaveBioModelWithHttpInfo(bioModelXML, simsRequiringUpdates);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#advancedSaveBioModel");
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
| **bioModelXML** | **String**|  | [optional] |
| **simsRequiringUpdates** | [**List&lt;String&gt;**](String.md)|  | [optional] |

### Return type

ApiResponse<**String**>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

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
- **Accept**: text/xml

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

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
- **Accept**: text/xml

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## saveBioModel

> String saveBioModel(body)

Save the BioModel, returning saved BioModel as VCML.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String body = "body_example"; // String | 
        try {
            String result = apiInstance.saveBioModel(body);
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
| **body** | **String**|  | [optional] |

### Return type

**String**


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: text/xml
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## saveBioModelWithHttpInfo

> ApiResponse<String> saveBioModel saveBioModelWithHttpInfo(body)

Save the BioModel, returning saved BioModel as VCML.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String body = "body_example"; // String | 
        try {
            ApiResponse<String> response = apiInstance.saveBioModelWithHttpInfo(body);
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
| **body** | **String**|  | [optional] |

### Return type

ApiResponse<**String**>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: text/xml
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## saveBioModelAs

> String saveBioModelAs(bioModelXML, name)

Save as a new BioModel under the name given. Returns saved BioModel as VCML.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelXML = "bioModelXML_example"; // String | 
        String name = "name_example"; // String | 
        try {
            String result = apiInstance.saveBioModelAs(bioModelXML, name);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#saveBioModelAs");
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
| **bioModelXML** | **String**|  | [optional] |
| **name** | **String**|  | [optional] |

### Return type

**String**


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## saveBioModelAsWithHttpInfo

> ApiResponse<String> saveBioModelAs saveBioModelAsWithHttpInfo(bioModelXML, name)

Save as a new BioModel under the name given. Returns saved BioModel as VCML.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelXML = "bioModelXML_example"; // String | 
        String name = "name_example"; // String | 
        try {
            ApiResponse<String> response = apiInstance.saveBioModelAsWithHttpInfo(bioModelXML, name);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#saveBioModelAs");
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
| **bioModelXML** | **String**|  | [optional] |
| **name** | **String**|  | [optional] |

### Return type

ApiResponse<**String**>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

