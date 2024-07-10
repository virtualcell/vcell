# UsersResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**forgotLegacyPassword**](UsersResourceApi.md#forgotLegacyPassword) | **POST** /api/v1/users/forgotLegacyPassword | The end user has forgotten the legacy password they used for VCell, so they will be emailed it. |
| [**forgotLegacyPasswordWithHttpInfo**](UsersResourceApi.md#forgotLegacyPasswordWithHttpInfo) | **POST** /api/v1/users/forgotLegacyPassword | The end user has forgotten the legacy password they used for VCell, so they will be emailed it. |
| [**getGuestLegacyApiToken**](UsersResourceApi.md#getGuestLegacyApiToken) | **POST** /api/v1/users/guestBearerToken | Method to get legacy tokens for guest users |
| [**getGuestLegacyApiTokenWithHttpInfo**](UsersResourceApi.md#getGuestLegacyApiTokenWithHttpInfo) | **POST** /api/v1/users/guestBearerToken | Method to get legacy tokens for guest users |
| [**getLegacyApiToken**](UsersResourceApi.md#getLegacyApiToken) | **POST** /api/v1/users/bearerToken | Get token for legacy API |
| [**getLegacyApiTokenWithHttpInfo**](UsersResourceApi.md#getLegacyApiTokenWithHttpInfo) | **POST** /api/v1/users/bearerToken | Get token for legacy API |
| [**getMappedUser**](UsersResourceApi.md#getMappedUser) | **GET** /api/v1/users/mappedUser | Get mapped VCell identity |
| [**getMappedUserWithHttpInfo**](UsersResourceApi.md#getMappedUserWithHttpInfo) | **GET** /api/v1/users/mappedUser | Get mapped VCell identity |
| [**getMe**](UsersResourceApi.md#getMe) | **GET** /api/v1/users/me | Get current user |
| [**getMeWithHttpInfo**](UsersResourceApi.md#getMeWithHttpInfo) | **GET** /api/v1/users/me | Get current user |
| [**mapNewUser**](UsersResourceApi.md#mapNewUser) | **POST** /api/v1/users/newUser | create vcell user |
| [**mapNewUserWithHttpInfo**](UsersResourceApi.md#mapNewUserWithHttpInfo) | **POST** /api/v1/users/newUser | create vcell user |
| [**mapUser**](UsersResourceApi.md#mapUser) | **POST** /api/v1/users/mapUser | map vcell user |
| [**mapUserWithHttpInfo**](UsersResourceApi.md#mapUserWithHttpInfo) | **POST** /api/v1/users/mapUser | map vcell user |
| [**processMagicLink**](UsersResourceApi.md#processMagicLink) | **GET** /api/v1/users/processMagicLink | Process the magic link and map the user |
| [**processMagicLinkWithHttpInfo**](UsersResourceApi.md#processMagicLinkWithHttpInfo) | **GET** /api/v1/users/processMagicLink | Process the magic link and map the user |
| [**requestRecoveryEmail**](UsersResourceApi.md#requestRecoveryEmail) | **POST** /api/v1/users/requestRecoveryEmail | request a recovery email to link a VCell account. |
| [**requestRecoveryEmailWithHttpInfo**](UsersResourceApi.md#requestRecoveryEmailWithHttpInfo) | **POST** /api/v1/users/requestRecoveryEmail | request a recovery email to link a VCell account. |
| [**unmapUser**](UsersResourceApi.md#unmapUser) | **PUT** /api/v1/users/unmapUser/{userName} | remove vcell identity mapping |
| [**unmapUserWithHttpInfo**](UsersResourceApi.md#unmapUserWithHttpInfo) | **PUT** /api/v1/users/unmapUser/{userName} | remove vcell identity mapping |



## forgotLegacyPassword

> void forgotLegacyPassword(userID)

The end user has forgotten the legacy password they used for VCell, so they will be emailed it.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String userID = "userID_example"; // String | 
        try {
            apiInstance.forgotLegacyPassword(userID);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#forgotLegacyPassword");
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
| **userID** | **String**|  | [optional] |

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
| **200** | Legacy password sent in email |  -  |
| **401** | Need to login to Auth0 |  -  |
| **500** | Internal Error |  -  |
| **403** | Not Allowed |  -  |

## forgotLegacyPasswordWithHttpInfo

> ApiResponse<Void> forgotLegacyPassword forgotLegacyPasswordWithHttpInfo(userID)

The end user has forgotten the legacy password they used for VCell, so they will be emailed it.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String userID = "userID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.forgotLegacyPasswordWithHttpInfo(userID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#forgotLegacyPassword");
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
| **userID** | **String**|  | [optional] |

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
| **200** | Legacy password sent in email |  -  |
| **401** | Need to login to Auth0 |  -  |
| **500** | Internal Error |  -  |
| **403** | Not Allowed |  -  |


## getGuestLegacyApiToken

> AccesTokenRepresentationRecord getGuestLegacyApiToken()

Method to get legacy tokens for guest users

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            AccesTokenRepresentationRecord result = apiInstance.getGuestLegacyApiToken();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getGuestLegacyApiToken");
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

[**AccesTokenRepresentationRecord**](AccesTokenRepresentationRecord.md)


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

## getGuestLegacyApiTokenWithHttpInfo

> ApiResponse<AccesTokenRepresentationRecord> getGuestLegacyApiToken getGuestLegacyApiTokenWithHttpInfo()

Method to get legacy tokens for guest users

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            ApiResponse<AccesTokenRepresentationRecord> response = apiInstance.getGuestLegacyApiTokenWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getGuestLegacyApiToken");
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

ApiResponse<[**AccesTokenRepresentationRecord**](AccesTokenRepresentationRecord.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## getLegacyApiToken

> AccesTokenRepresentationRecord getLegacyApiToken()

Get token for legacy API

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            AccesTokenRepresentationRecord result = apiInstance.getLegacyApiToken();
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

This endpoint does not need any parameter.

### Return type

[**AccesTokenRepresentationRecord**](AccesTokenRepresentationRecord.md)


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

## getLegacyApiTokenWithHttpInfo

> ApiResponse<AccesTokenRepresentationRecord> getLegacyApiToken getLegacyApiTokenWithHttpInfo()

Get token for legacy API

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            ApiResponse<AccesTokenRepresentationRecord> response = apiInstance.getLegacyApiTokenWithHttpInfo();
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

This endpoint does not need any parameter.

### Return type

ApiResponse<[**AccesTokenRepresentationRecord**](AccesTokenRepresentationRecord.md)>


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


## getMappedUser

> UserIdentityJSONSafe getMappedUser()

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            UserIdentityJSONSafe result = apiInstance.getMappedUser();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getMappedUser");
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
| **200** | Successful, returning the identity |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## getMappedUserWithHttpInfo

> ApiResponse<UserIdentityJSONSafe> getMappedUser getMappedUserWithHttpInfo()

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            ApiResponse<UserIdentityJSONSafe> response = apiInstance.getMappedUserWithHttpInfo();
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#getMappedUser");
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
| **200** | Successful, returning the identity |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## getMe

> Identity getMe()

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            Identity result = apiInstance.getMe();
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

[**Identity**](Identity.md)


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

> ApiResponse<Identity> getMe getMeWithHttpInfo()

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        try {
            ApiResponse<Identity> response = apiInstance.getMeWithHttpInfo();
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

ApiResponse<[**Identity**](Identity.md)>


### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |


## mapNewUser

> void mapNewUser(userRegistrationInfo)

create vcell user

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        UserRegistrationInfo userRegistrationInfo = new UserRegistrationInfo(); // UserRegistrationInfo | 
        try {
            apiInstance.mapNewUser(userRegistrationInfo);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#mapNewUser");
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
| **userRegistrationInfo** | [**UserRegistrationInfo**](UserRegistrationInfo.md)|  | [optional] |

### Return type


null (empty response body)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful, returning the identity |  -  |
| **409** | VCell Identity not mapped, userid already exists |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |

## mapNewUserWithHttpInfo

> ApiResponse<Void> mapNewUser mapNewUserWithHttpInfo(userRegistrationInfo)

create vcell user

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        UserRegistrationInfo userRegistrationInfo = new UserRegistrationInfo(); // UserRegistrationInfo | 
        try {
            ApiResponse<Void> response = apiInstance.mapNewUserWithHttpInfo(userRegistrationInfo);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#mapNewUser");
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
| **userRegistrationInfo** | [**UserRegistrationInfo**](UserRegistrationInfo.md)|  | [optional] |

### Return type


ApiResponse<Void>

### Authorization

[openId](../README.md#openId)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful, returning the identity |  -  |
| **409** | VCell Identity not mapped, userid already exists |  -  |
| **401** | Not Authorized |  -  |
| **403** | Not Allowed |  -  |


## mapUser

> Boolean mapUser(userLoginInfoForMapping)

map vcell user

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        UserLoginInfoForMapping userLoginInfoForMapping = new UserLoginInfoForMapping(); // UserLoginInfoForMapping | 
        try {
            Boolean result = apiInstance.mapUser(userLoginInfoForMapping);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#mapUser");
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
| **userLoginInfoForMapping** | [**UserLoginInfoForMapping**](UserLoginInfoForMapping.md)|  | [optional] |

### Return type

**Boolean**


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

## mapUserWithHttpInfo

> ApiResponse<Boolean> mapUser mapUserWithHttpInfo(userLoginInfoForMapping)

map vcell user

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        UserLoginInfoForMapping userLoginInfoForMapping = new UserLoginInfoForMapping(); // UserLoginInfoForMapping | 
        try {
            ApiResponse<Boolean> response = apiInstance.mapUserWithHttpInfo(userLoginInfoForMapping);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#mapUser");
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
| **userLoginInfoForMapping** | [**UserLoginInfoForMapping**](UserLoginInfoForMapping.md)|  | [optional] |

### Return type

ApiResponse<**Boolean**>


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


## processMagicLink

> void processMagicLink(magic)

Process the magic link and map the user

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String magic = "magic_example"; // String | 
        try {
            apiInstance.processMagicLink(magic);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#processMagicLink");
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
| **magic** | **String**|  | [optional] |

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
| **200** | User mapped successfully |  -  |
| **400** | Invalid or expired magic link |  -  |

## processMagicLinkWithHttpInfo

> ApiResponse<Void> processMagicLink processMagicLinkWithHttpInfo(magic)

Process the magic link and map the user

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String magic = "magic_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.processMagicLinkWithHttpInfo(magic);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#processMagicLink");
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
| **magic** | **String**|  | [optional] |

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
| **200** | User mapped successfully |  -  |
| **400** | Invalid or expired magic link |  -  |


## requestRecoveryEmail

> void requestRecoveryEmail(email, userID)

request a recovery email to link a VCell account.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String email = "email_example"; // String | 
        String userID = "userID_example"; // String | 
        try {
            apiInstance.requestRecoveryEmail(email, userID);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#requestRecoveryEmail");
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
| **email** | **String**|  | [optional] |
| **userID** | **String**|  | [optional] |

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
| **200** | magic link sent in email if appropriate |  -  |
| **400** | unable to process request |  -  |
| **403** | Not Allowed |  -  |
| **401** | Not Authorized |  -  |

## requestRecoveryEmailWithHttpInfo

> ApiResponse<Void> requestRecoveryEmail requestRecoveryEmailWithHttpInfo(email, userID)

request a recovery email to link a VCell account.

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String email = "email_example"; // String | 
        String userID = "userID_example"; // String | 
        try {
            ApiResponse<Void> response = apiInstance.requestRecoveryEmailWithHttpInfo(email, userID);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#requestRecoveryEmail");
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
| **email** | **String**|  | [optional] |
| **userID** | **String**|  | [optional] |

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
| **200** | magic link sent in email if appropriate |  -  |
| **400** | unable to process request |  -  |
| **403** | Not Allowed |  -  |
| **401** | Not Authorized |  -  |


## unmapUser

> Boolean unmapUser(userName)

remove vcell identity mapping

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String userName = "userName_example"; // String | 
        try {
            Boolean result = apiInstance.unmapUser(userName);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#unmapUser");
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
| **userName** | **String**|  | |

### Return type

**Boolean**


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

## unmapUserWithHttpInfo

> ApiResponse<Boolean> unmapUser unmapUserWithHttpInfo(userName)

remove vcell identity mapping

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
        defaultClient.setBasePath("https://vcell-dev.cam.uchc.edu");
        

        UsersResourceApi apiInstance = new UsersResourceApi(defaultClient);
        String userName = "userName_example"; // String | 
        try {
            ApiResponse<Boolean> response = apiInstance.unmapUserWithHttpInfo(userName);
            System.out.println("Status code: " + response.getStatusCode());
            System.out.println("Response headers: " + response.getHeaders());
            System.out.println("Response body: " + response.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling UsersResourceApi#unmapUser");
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
| **userName** | **String**|  | |

### Return type

ApiResponse<**Boolean**>


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

