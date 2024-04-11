# vcell_client.UsersResourceApi

All URIs are relative to *http://localhost:9000*

Method | HTTP request | Description
------------- | ------------- | -------------
[**api_users_bearer_token_post**](UsersResourceApi.md#api_users_bearer_token_post) | **POST** /api/users/bearerToken | 
[**api_users_get_identity_get**](UsersResourceApi.md#api_users_get_identity_get) | **GET** /api/users/getIdentity | 
[**api_users_map_user_post**](UsersResourceApi.md#api_users_map_user_post) | **POST** /api/users/mapUser | 
[**api_users_me_get**](UsersResourceApi.md#api_users_me_get) | **GET** /api/users/me | 


# **api_users_bearer_token_post**
> AccesTokenRepresentationRecord api_users_bearer_token_post(user_id=user_id, user_password=user_password, client_id=client_id)



### Example

```python
import time
import os
import vcell_client
from vcell_client.models.acces_token_representation_record import AccesTokenRepresentationRecord
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:9000
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "http://localhost:9000"
)


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.UsersResourceApi(api_client)
    user_id = 'user_id_example' # str |  (optional)
    user_password = 'user_password_example' # str |  (optional)
    client_id = 'client_id_example' # str |  (optional)

    try:
        api_response = api_instance.api_users_bearer_token_post(user_id=user_id, user_password=user_password, client_id=client_id)
        print("The response of UsersResourceApi->api_users_bearer_token_post:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->api_users_bearer_token_post: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user_id** | **str**|  | [optional] 
 **user_password** | **str**|  | [optional] 
 **client_id** | **str**|  | [optional] 

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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **api_users_get_identity_get**
> UserIdentityJSONSafe api_users_get_identity_get()



### Example

```python
import time
import os
import vcell_client
from vcell_client.models.user_identity_json_safe import UserIdentityJSONSafe
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:9000
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "http://localhost:9000"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.UsersResourceApi(api_client)

    try:
        api_response = api_instance.api_users_get_identity_get()
        print("The response of UsersResourceApi->api_users_get_identity_get:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->api_users_get_identity_get: %s\n" % e)
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
**200** | OK |  -  |
**403** | Not Allowed |  -  |
**401** | Not Authorized |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **api_users_map_user_post**
> bool api_users_map_user_post(map_user=map_user)



### Example

```python
import time
import os
import vcell_client
from vcell_client.models.map_user import MapUser
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:9000
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "http://localhost:9000"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.UsersResourceApi(api_client)
    map_user = vcell_client.MapUser() # MapUser |  (optional)

    try:
        api_response = api_instance.api_users_map_user_post(map_user=map_user)
        print("The response of UsersResourceApi->api_users_map_user_post:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->api_users_map_user_post: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **map_user** | [**MapUser**](MapUser.md)|  | [optional] 

### Return type

**bool**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**403** | Not Allowed |  -  |
**401** | Not Authorized |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **api_users_me_get**
> User api_users_me_get()



### Example

```python
import time
import os
import vcell_client
from vcell_client.models.user import User
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost:9000
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "http://localhost:9000"
)


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.UsersResourceApi(api_client)

    try:
        api_response = api_instance.api_users_me_get()
        print("The response of UsersResourceApi->api_users_me_get:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->api_users_me_get: %s\n" % e)
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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

