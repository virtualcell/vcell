# SimulationResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getSimulationStatus**](SimulationResourceApi.md#getSimulationStatus) | **GET** /api/v1/Simulation/{simID}/simulationStatus | Get the status of simulation running |
| [**getSimulationStatusWithHttpInfo**](SimulationResourceApi.md#getSimulationStatusWithHttpInfo) | **GET** /api/v1/Simulation/{simID}/simulationStatus | Get the status of simulation running |
| [**startSimulation**](SimulationResourceApi.md#startSimulation) | **POST** /api/v1/Simulation/{simID}/startSimulation | Start a simulation. |
| [**startSimulationWithHttpInfo**](SimulationResourceApi.md#startSimulationWithHttpInfo) | **POST** /api/v1/Simulation/{simID}/startSimulation | Start a simulation. |
| [**stopSimulation**](SimulationResourceApi.md#stopSimulation) | **POST** /api/v1/Simulation/{simID}/stopSimulation | Stop a simulation. |
| [**stopSimulationWithHttpInfo**](SimulationResourceApi.md#stopSimulationWithHttpInfo) | **POST** /api/v1/Simulation/{simID}/stopSimulation | Stop a simulation. |



## getSimulationStatus

> SimulationStatusPersistentRecord getSimulationStatus(simID, bioModelID, mathModelID)

Get the status of simulation running

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SimulationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        SimulationResourceApi apiInstance = new SimulationResourceApi(defaultClient);
        String simID = "simID_example"; // String | 
        String bioModelID = "bioModelID_example"; // String | 
        String mathModelID = "mathModelID_example"; // String | 
        try {
            SimulationStatusPersistentRecord result = apiInstance.getSimulationStatus(simID, bioModelID, mathModelID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling SimulationResourceApi#getSimulationStatus");
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
| **simID** | **String**|  | |
| **bioModelID** | **String**|  | [optional] |
| **mathModelID** | **String**|  | [optional] |

### Return type

[**SimulationStatusPersistentRecord**](SimulationStatusPersistentRecord.md)


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

## getSimulationStatusWithHttpInfo

> ApiResponse<SimulationStatusPersistentRecord> getSimulationStatus getSimulationStatusWithHttpInfo(simID, bioModelID, mathModelID)

Get the status of simulation running

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SimulationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        SimulationResourceApi apiInstance = new SimulationResourceApi(defaultClient);
        String simID = "simID_example"; // String | 
        String bioModelID = "bioModelID_example"; // String | 
        String mathModelID = "mathModelID_example"; // String | 
        try {
            ApiResponse<SimulationStatusPersistentRecord> response = apiInstance.getSimulationStatusWithHttpInfo(simID, bioModelID, mathModelID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling SimulationResourceApi#getSimulationStatus");
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
| **simID** | **String**|  | |
| **bioModelID** | **String**|  | [optional] |
| **mathModelID** | **String**|  | [optional] |

### Return type

ApiResponse<[**SimulationStatusPersistentRecord**](SimulationStatusPersistentRecord.md)>


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


## startSimulation

> List<StatusMessage> startSimulation(simID)

Start a simulation.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SimulationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        SimulationResourceApi apiInstance = new SimulationResourceApi(defaultClient);
        String simID = "simID_example"; // String | 
        try {
            List<StatusMessage> result = apiInstance.startSimulation(simID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling SimulationResourceApi#startSimulation");
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
| **simID** | **String**|  | |

### Return type

[**List&lt;StatusMessage&gt;**](StatusMessage.md)


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

## startSimulationWithHttpInfo

> ApiResponse<List<StatusMessage>> startSimulation startSimulationWithHttpInfo(simID)

Start a simulation.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SimulationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        SimulationResourceApi apiInstance = new SimulationResourceApi(defaultClient);
        String simID = "simID_example"; // String | 
        try {
            ApiResponse<List<StatusMessage>> response = apiInstance.startSimulationWithHttpInfo(simID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling SimulationResourceApi#startSimulation");
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
| **simID** | **String**|  | |

### Return type

ApiResponse<[**List&lt;StatusMessage&gt;**](StatusMessage.md)>


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


## stopSimulation

> List<StatusMessage> stopSimulation(simID)

Stop a simulation.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SimulationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        SimulationResourceApi apiInstance = new SimulationResourceApi(defaultClient);
        String simID = "simID_example"; // String | 
        try {
            List<StatusMessage> result = apiInstance.stopSimulation(simID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling SimulationResourceApi#stopSimulation");
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
| **simID** | **String**|  | |

### Return type

[**List&lt;StatusMessage&gt;**](StatusMessage.md)


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

## stopSimulationWithHttpInfo

> ApiResponse<List<StatusMessage>> stopSimulation stopSimulationWithHttpInfo(simID)

Stop a simulation.

### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.SimulationResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcell.cam.uchc.edu");
        

        SimulationResourceApi apiInstance = new SimulationResourceApi(defaultClient);
        String simID = "simID_example"; // String | 
        try {
            ApiResponse<List<StatusMessage>> response = apiInstance.stopSimulationWithHttpInfo(simID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling SimulationResourceApi#stopSimulation");
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
| **simID** | **String**|  | |

### Return type

ApiResponse<[**List&lt;StatusMessage&gt;**](StatusMessage.md)>


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

