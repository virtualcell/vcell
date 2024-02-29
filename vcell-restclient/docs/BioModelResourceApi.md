# BioModelResourceApi

All URIs are relative to *http://localhost:9000*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteBioModel**](BioModelResourceApi.md#deleteBioModel) | **DELETE** /api/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database. |
| [**deleteBioModelWithHttpInfo**](BioModelResourceApi.md#deleteBioModelWithHttpInfo) | **DELETE** /api/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database. |
| [**getBioModelBNGL**](BioModelResourceApi.md#getBioModelBNGL) | **GET** /api/bioModel/{bioModelID}/bngl_download | Get the BioModel in BNGL format. |
| [**getBioModelBNGLWithHttpInfo**](BioModelResourceApi.md#getBioModelBNGLWithHttpInfo) | **GET** /api/bioModel/{bioModelID}/bngl_download | Get the BioModel in BNGL format. |
| [**getBioModelDIAGRAM**](BioModelResourceApi.md#getBioModelDIAGRAM) | **GET** /api/bioModel/{bioModelID}/diagram_download | Get the BioModels diagram. |
| [**getBioModelDIAGRAMWithHttpInfo**](BioModelResourceApi.md#getBioModelDIAGRAMWithHttpInfo) | **GET** /api/bioModel/{bioModelID}/diagram_download | Get the BioModels diagram. |
| [**getBioModelOMEX**](BioModelResourceApi.md#getBioModelOMEX) | **GET** /api/bioModel/{bioModelID}/omex_download | Get the BioModel in OMEX format. |
| [**getBioModelOMEXWithHttpInfo**](BioModelResourceApi.md#getBioModelOMEXWithHttpInfo) | **GET** /api/bioModel/{bioModelID}/omex_download | Get the BioModel in OMEX format. |
| [**getBioModelSBML**](BioModelResourceApi.md#getBioModelSBML) | **GET** /api/bioModel/{bioModelID}/sbml_download | Get the BioModel in SBML format. |
| [**getBioModelSBMLWithHttpInfo**](BioModelResourceApi.md#getBioModelSBMLWithHttpInfo) | **GET** /api/bioModel/{bioModelID}/sbml_download | Get the BioModel in SBML format. |
| [**getBioModelVCML**](BioModelResourceApi.md#getBioModelVCML) | **GET** /api/bioModel/{bioModelID}/vcml_download | Get the BioModel in VCML format. |
| [**getBioModelVCMLWithHttpInfo**](BioModelResourceApi.md#getBioModelVCMLWithHttpInfo) | **GET** /api/bioModel/{bioModelID}/vcml_download | Get the BioModel in VCML format. |
| [**getBiomodelById**](BioModelResourceApi.md#getBiomodelById) | **GET** /api/bioModel/{bioModelID} | Get BioModel information in JSON format by ID. |
| [**getBiomodelByIdWithHttpInfo**](BioModelResourceApi.md#getBiomodelByIdWithHttpInfo) | **GET** /api/bioModel/{bioModelID} | Get BioModel information in JSON format by ID. |
| [**uploadBioModel**](BioModelResourceApi.md#uploadBioModel) | **POST** /api/bioModel/upload_bioModel | Upload the BioModel to VCell database. Returns BioModel ID. |
| [**uploadBioModelWithHttpInfo**](BioModelResourceApi.md#uploadBioModelWithHttpInfo) | **POST** /api/bioModel/upload_bioModel | Upload the BioModel to VCell database. Returns BioModel ID. |



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
        defaultClient.setBasePath("http://localhost:9000");

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
        defaultClient.setBasePath("http://localhost:9000");

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


## getBioModelBNGL

> void getBioModelBNGL(bioModelID)

Get the BioModel in BNGL format.

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            apiInstance.getBioModelBNGL(bioModelID);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelBNGL");
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

## getBioModelBNGLWithHttpInfo

> ApiResponse<Void> getBioModelBNGL getBioModelBNGLWithHttpInfo(bioModelID)

Get the BioModel in BNGL format.

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.getBioModelBNGLWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelBNGL");
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


## getBioModelDIAGRAM

> void getBioModelDIAGRAM(bioModelID)

Get the BioModels diagram.

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            apiInstance.getBioModelDIAGRAM(bioModelID);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelDIAGRAM");
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

## getBioModelDIAGRAMWithHttpInfo

> ApiResponse<Void> getBioModelDIAGRAM getBioModelDIAGRAMWithHttpInfo(bioModelID)

Get the BioModels diagram.

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.getBioModelDIAGRAMWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelDIAGRAM");
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


## getBioModelOMEX

> void getBioModelOMEX(bioModelID)

Get the BioModel in OMEX format.

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            apiInstance.getBioModelOMEX(bioModelID);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelOMEX");
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

## getBioModelOMEXWithHttpInfo

> ApiResponse<Void> getBioModelOMEX getBioModelOMEXWithHttpInfo(bioModelID)

Get the BioModel in OMEX format.

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.getBioModelOMEXWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelOMEX");
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


## getBioModelSBML

> void getBioModelSBML(bioModelID)

Get the BioModel in SBML format.

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            apiInstance.getBioModelSBML(bioModelID);
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelSBML");
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

## getBioModelSBMLWithHttpInfo

> ApiResponse<Void> getBioModelSBML getBioModelSBMLWithHttpInfo(bioModelID)

Get the BioModel in SBML format.

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.getBioModelSBMLWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling BioModelResourceApi#getBioModelSBML");
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


## getBioModelVCML

> void getBioModelVCML(bioModelID)

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            apiInstance.getBioModelVCML(bioModelID);
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

## getBioModelVCMLWithHttpInfo

> ApiResponse<Void> getBioModelVCML getBioModelVCMLWithHttpInfo(bioModelID)

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
        defaultClient.setBasePath("http://localhost:9000");

        BioModelResourceApi apiInstance = new BioModelResourceApi(defaultClient);
        String bioModelID = "bioModelID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.getBioModelVCMLWithHttpInfo(bioModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
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
        defaultClient.setBasePath("http://localhost:9000");

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
        defaultClient.setBasePath("http://localhost:9000");

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
        defaultClient.setBasePath("http://localhost:9000");

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
        defaultClient.setBasePath("http://localhost:9000");

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

