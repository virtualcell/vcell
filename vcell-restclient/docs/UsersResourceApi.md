# UsersResourceApi

All URIs are relative to *http://localhost:9000*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**apiUsersBearerTokenPost**](UsersResourceApi.md#apiUsersBearerTokenPost) | **POST** /api/users/bearerToken |  |
| [**apiUsersBearerTokenPostWithHttpInfo**](UsersResourceApi.md#apiUsersBearerTokenPostWithHttpInfo) | **POST** /api/users/bearerToken |  |
| [**apiUsersGetIdentityGet**](UsersResourceApi.md#apiUsersGetIdentityGet) | **GET** /api/users/getIdentity |  |
| [**apiUsersGetIdentityGetWithHttpInfo**](UsersResourceApi.md#apiUsersGetIdentityGetWithHttpInfo) | **GET** /api/users/getIdentity |  |
| [**apiUsersMapUserPost**](UsersResourceApi.md#apiUsersMapUserPost) | **POST** /api/users/mapUser |  |
| [**apiUsersMapUserPostWithHttpInfo**](UsersResourceApi.md#apiUsersMapUserPostWithHttpInfo) | **POST** /api/users/mapUser |  |
| [**apiUsersMeGet**](UsersResourceApi.md#apiUsersMeGet) | **GET** /api/users/me |  |
| [**apiUsersMeGetWithHttpInfo**](UsersResourceApi.md#apiUsersMeGetWithHttpInfo) | **GET** /api/users/me |  |



## apiUsersBearerTokenPost

> AccesTokenRepresentationRecord apiUsersBearerTokenPost(userId, userPassword, clientId)



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
        defaultClient.setBasePath("http://localhost:9000");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String userId = "userId_example"; // String | 
        String userPassword = "userPassword_example"; // String | 
        String clientId = "clientId_example"; // String | 
        try {
            AccesTokenRepresentationRecord result = apiInstance.apiUsersBearerTokenPost(userId, userPassword, clientId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiUsersBearerTokenPost");
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

## apiUsersBearerTokenPostWithHttpInfo

> ApiResponse<AccesTokenRepresentationRecord> apiUsersBearerTokenPost apiUsersBearerTokenPostWithHttpInfo(userId, userPassword, clientId)



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
        defaultClient.setBasePath("http://localhost:9000");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String userId = "userId_example"; // String | 
        String userPassword = "userPassword_example"; // String | 
        String clientId = "clientId_example"; // String | 
        try {
            ApiResponse<AccesTokenRepresentationRecord> response = apiInstance.apiUsersBearerTokenPostWithHttpInfo(userId, userPassword, clientId);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiUsersBearerTokenPost");
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


## apiUsersGetIdentityGet

> UserIdentityJSONSafe apiUsersGetIdentityGet()



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
        defaultClient.setBasePath("http://localhost:9000");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            UserIdentityJSONSafe result = apiInstance.apiUsersGetIdentityGet();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiUsersGetIdentityGet");
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
| **403** | Not Allowed |  -  |
| **401** | Not Authorized |  -  |

## apiUsersGetIdentityGetWithHttpInfo

> ApiResponse<UserIdentityJSONSafe> apiUsersGetIdentityGet apiUsersGetIdentityGetWithHttpInfo()



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
        defaultClient.setBasePath("http://localhost:9000");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            ApiResponse<UserIdentityJSONSafe> response = apiInstance.apiUsersGetIdentityGetWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiUsersGetIdentityGet");
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
| **403** | Not Allowed |  -  |
| **401** | Not Authorized |  -  |


## apiUsersMapUserPost

> Boolean apiUsersMapUserPost(mapUser)



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
        defaultClient.setBasePath("http://localhost:9000");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        MapUser mapUser = new MapUser(); // MapUser | 
        try {
            Boolean result = apiInstance.apiUsersMapUserPost(mapUser);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiUsersMapUserPost");
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
| **403** | Not Allowed |  -  |
| **401** | Not Authorized |  -  |

## apiUsersMapUserPostWithHttpInfo

> ApiResponse<Boolean> apiUsersMapUserPost apiUsersMapUserPostWithHttpInfo(mapUser)



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
        defaultClient.setBasePath("http://localhost:9000");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        MapUser mapUser = new MapUser(); // MapUser | 
        try {
            ApiResponse<Boolean> response = apiInstance.apiUsersMapUserPostWithHttpInfo(mapUser);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiUsersMapUserPost");
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
| **403** | Not Allowed |  -  |
| **401** | Not Authorized |  -  |


## apiUsersMeGet

> User apiUsersMeGet()



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
        defaultClient.setBasePath("http://localhost:9000");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            User result = apiInstance.apiUsersMeGet();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiUsersMeGet");
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

## apiUsersMeGetWithHttpInfo

> ApiResponse<User> apiUsersMeGet apiUsersMeGetWithHttpInfo()



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
        defaultClient.setBasePath("http://localhost:9000");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            ApiResponse<User> response = apiInstance.apiUsersMeGetWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#apiUsersMeGet");
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

