# UsersResourceApi

All URIs are relative to *https://vcellapi-test.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getLegacyApiToken**](UsersResourceApi.md#getLegacyApiToken) | **POST** /api/v1/users/bearerToken | Get token for legacy API |
| [**getLegacyApiTokenWithHttpInfo**](UsersResourceApi.md#getLegacyApiTokenWithHttpInfo) | **POST** /api/v1/users/bearerToken | Get token for legacy API |
| [**getMe**](UsersResourceApi.md#getMe) | **GET** /api/v1/users/me | Get current user |
| [**getMeWithHttpInfo**](UsersResourceApi.md#getMeWithHttpInfo) | **GET** /api/v1/users/me | Get current user |
| [**getVCellIdentity**](UsersResourceApi.md#getVCellIdentity) | **GET** /api/v1/users/getIdentity | Get mapped VCell identity |
| [**getVCellIdentityWithHttpInfo**](UsersResourceApi.md#getVCellIdentityWithHttpInfo) | **GET** /api/v1/users/getIdentity | Get mapped VCell identity |
| [**setVCellIdentity**](UsersResourceApi.md#setVCellIdentity) | **POST** /api/v1/users/mapUser | set or replace vcell identity mapping |
| [**setVCellIdentityWithHttpInfo**](UsersResourceApi.md#setVCellIdentityWithHttpInfo) | **POST** /api/v1/users/mapUser | set or replace vcell identity mapping |



## getLegacyApiToken

> AccesTokenRepresentationRecord getLegacyApiToken(userId, userPassword, clientId)

Get token for legacy API

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
            AccesTokenRepresentationRecord result = apiInstance.getLegacyApiToken(userId, userPassword, clientId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getLegacyApiToken");
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

## getLegacyApiTokenWithHttpInfo

> ApiResponse<AccesTokenRepresentationRecord> getLegacyApiToken getLegacyApiTokenWithHttpInfo(userId, userPassword, clientId)

Get token for legacy API

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
            ApiResponse<AccesTokenRepresentationRecord> response = apiInstance.getLegacyApiTokenWithHttpInfo(userId, userPassword, clientId);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getLegacyApiToken");
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


## getMe

> User getMe()

Get current user

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
            User result = apiInstance.getMe();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getMe");
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

## getMeWithHttpInfo

> ApiResponse<User> getMe getMeWithHttpInfo()

Get current user

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
            ApiResponse<User> response = apiInstance.getMeWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getMe");
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


## getVCellIdentity

> UserIdentityJSONSafe getVCellIdentity()

Get mapped VCell identity

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
            UserIdentityJSONSafe result = apiInstance.getVCellIdentity();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getVCellIdentity");
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

## getVCellIdentityWithHttpInfo

> ApiResponse<UserIdentityJSONSafe> getVCellIdentity getVCellIdentityWithHttpInfo()

Get mapped VCell identity

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
            ApiResponse<UserIdentityJSONSafe> response = apiInstance.getVCellIdentityWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getVCellIdentity");
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


## setVCellIdentity

> Boolean setVCellIdentity(mapUser)

set or replace vcell identity mapping

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
            Boolean result = apiInstance.setVCellIdentity(mapUser);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#setVCellIdentity");
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

## setVCellIdentityWithHttpInfo

> ApiResponse<Boolean> setVCellIdentity setVCellIdentityWithHttpInfo(mapUser)

set or replace vcell identity mapping

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
            ApiResponse<Boolean> response = apiInstance.setVCellIdentityWithHttpInfo(mapUser);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#setVCellIdentity");
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

