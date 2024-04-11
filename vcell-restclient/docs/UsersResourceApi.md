# UsersResourceApi

All URIs are relative to *https://vcellapi-test.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiV1UsersBearerTokenPost**](UsersResourceApi.md#apiV1UsersBearerTokenPost) | **POST** /api/v1/users/bearerToken |  |
| [**apiV1UsersBearerTokenPostWithHttpInfo**](UsersResourceApi.md#apiV1UsersBearerTokenPostWithHttpInfo) | **POST** /api/v1/users/bearerToken |  |
| [**apiV1UsersGetIdentityGet**](UsersResourceApi.md#apiV1UsersGetIdentityGet) | **GET** /api/v1/users/getIdentity |  |
| [**apiV1UsersGetIdentityGetWithHttpInfo**](UsersResourceApi.md#apiV1UsersGetIdentityGetWithHttpInfo) | **GET** /api/v1/users/getIdentity |  |
| [**apiV1UsersMapUserPost**](UsersResourceApi.md#apiV1UsersMapUserPost) | **POST** /api/v1/users/mapUser |  |
| [**apiV1UsersMapUserPostWithHttpInfo**](UsersResourceApi.md#apiV1UsersMapUserPostWithHttpInfo) | **POST** /api/v1/users/mapUser |  |
| [**apiV1UsersMeGet**](UsersResourceApi.md#apiV1UsersMeGet) | **GET** /api/v1/users/me |  |
| [**apiV1UsersMeGetWithHttpInfo**](UsersResourceApi.md#apiV1UsersMeGetWithHttpInfo) | **GET** /api/v1/users/me |  |



## apiV1UsersBearerTokenPost

> AccesTokenRepresentationRecord apiV1UsersBearerTokenPost(userId, userPassword, clientId)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.UsersResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String userId = "userId_example"; // String | 
        String userPassword = "userPassword_example"; // String | 
        String clientId = "clientId_example"; // String | 
        try {
            AccesTokenRepresentationRecord result = apiInstance.apiV1UsersBearerTokenPost(userId, userPassword, clientId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiV1UsersBearerTokenPost");
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
| **userId** | **String**|  | [optional] |
| **userPassword** | **String**|  | [optional] |
| **clientId** | **String**|  | [optional] |

### Return type

[**AccesTokenRepresentationRecord**](AccesTokenRepresentationRecord.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/x-www-form-urlencoded
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## apiV1UsersBearerTokenPostWithHttpInfo

> ApiResponse<AccesTokenRepresentationRecord> apiV1UsersBearerTokenPost apiV1UsersBearerTokenPostWithHttpInfo(userId, userPassword, clientId)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.UsersResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String userId = "userId_example"; // String | 
        String userPassword = "userPassword_example"; // String | 
        String clientId = "clientId_example"; // String | 
        try {
            ApiResponse<AccesTokenRepresentationRecord> response = apiInstance.apiV1UsersBearerTokenPostWithHttpInfo(userId, userPassword, clientId);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiV1UsersBearerTokenPost");
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
| **userId** | **String**|  | [optional] |
| **userPassword** | **String**|  | [optional] |
| **clientId** | **String**|  | [optional] |

### Return type

ApiResponse<[**AccesTokenRepresentationRecord**](AccesTokenRepresentationRecord.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/x-www-form-urlencoded
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## apiV1UsersGetIdentityGet

> UserIdentityJSONSafe apiV1UsersGetIdentityGet()



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.UsersResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            UserIdentityJSONSafe result = apiInstance.apiV1UsersGetIdentityGet();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiV1UsersGetIdentityGet");
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

[**UserIdentityJSONSafe**](UserIdentityJSONSafe.md)


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

## apiV1UsersGetIdentityGetWithHttpInfo

> ApiResponse<UserIdentityJSONSafe> apiV1UsersGetIdentityGet apiV1UsersGetIdentityGetWithHttpInfo()



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.UsersResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            ApiResponse<UserIdentityJSONSafe> response = apiInstance.apiV1UsersGetIdentityGetWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiV1UsersGetIdentityGet");
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

ApiResponse<[**UserIdentityJSONSafe**](UserIdentityJSONSafe.md)>


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


## apiV1UsersMapUserPost

> Boolean apiV1UsersMapUserPost(mapUser)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.UsersResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        MapUser mapUser = new MapUser(); // MapUser | 
        try {
            Boolean result = apiInstance.apiV1UsersMapUserPost(mapUser);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiV1UsersMapUserPost");
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
| **mapUser** | [**MapUser**](MapUser.md)|  | [optional] |

### Return type

**Boolean**


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## apiV1UsersMapUserPostWithHttpInfo

> ApiResponse<Boolean> apiV1UsersMapUserPost apiV1UsersMapUserPostWithHttpInfo(mapUser)



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.auth.*;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.UsersResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        MapUser mapUser = new MapUser(); // MapUser | 
        try {
            ApiResponse<Boolean> response = apiInstance.apiV1UsersMapUserPostWithHttpInfo(mapUser);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiV1UsersMapUserPost");
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
| **mapUser** | [**MapUser**](MapUser.md)|  | [optional] |

### Return type

ApiResponse<**Boolean**>


### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## apiV1UsersMeGet

> User apiV1UsersMeGet()



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.UsersResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            User result = apiInstance.apiV1UsersMeGet();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiV1UsersMeGet");
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

[**User**](User.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## apiV1UsersMeGetWithHttpInfo

> ApiResponse<User> apiV1UsersMeGet apiV1UsersMeGetWithHttpInfo()



### Example

```java
// Import classes:
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.models.*;
import org.vcell.restclient.api.UsersResourceApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://vcellapi-test.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            ApiResponse<User> response = apiInstance.apiV1UsersMeGetWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiV1UsersMeGet");
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

ApiResponse<[**User**](User.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

