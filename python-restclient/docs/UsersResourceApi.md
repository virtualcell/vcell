# vcell_client.UsersResourceApi

All URIs are relative to *https://vcellapi-test.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_legacy_api_token**](UsersResourceApi.md#get_legacy_api_token) | **POST** /api/v1/users/bearerToken | Get token for legacy API
[**get_mapped_user**](UsersResourceApi.md#get_mapped_user) | **GET** /api/v1/users/mappedUser | Get mapped VCell identity
[**get_me**](UsersResourceApi.md#get_me) | **GET** /api/v1/users/me | Get current user
[**map_new_user**](UsersResourceApi.md#map_new_user) | **POST** /api/v1/users/newUser | create vcell user
[**map_user**](UsersResourceApi.md#map_user) | **POST** /api/v1/users/mapUser | map vcell user
[**unmap_user**](UsersResourceApi.md#unmap_user) | **PUT** /api/v1/users/unmapUser/{userName} | remove vcell identity mapping


# **get_legacy_api_token**
> AccesTokenRepresentationRecord get_legacy_api_token()

Get token for legacy API

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.acces_token_representation_record import AccesTokenRepresentationRecord
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://vcellapi-test.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcellapi-test.cam.uchc.edu"
)


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.UsersResourceApi(api_client)

    try:
        # Get token for legacy API
        api_response = api_instance.get_legacy_api_token()
        print("The response of UsersResourceApi->get_legacy_api_token:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->get_legacy_api_token: %s\n" % e)
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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_mapped_user**
> UserIdentityJSONSafe get_mapped_user()

Get mapped VCell identity

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.user_identity_json_safe import UserIdentityJSONSafe
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://vcellapi-test.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcellapi-test.cam.uchc.edu"
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
        # Get mapped VCell identity
        api_response = api_instance.get_mapped_user()
        print("The response of UsersResourceApi->get_mapped_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->get_mapped_user: %s\n" % e)
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
**200** | Successful, returning the identity |  -  |
**404** | Identity not found |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_me**
> Identity get_me()

Get current user

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.identity import Identity
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://vcellapi-test.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcellapi-test.cam.uchc.edu"
)


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.UsersResourceApi(api_client)

    try:
        # Get current user
        api_response = api_instance.get_me()
        print("The response of UsersResourceApi->get_me:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->get_me: %s\n" % e)
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
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **map_new_user**
> map_new_user(user_registration_info=user_registration_info)

create vcell user

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.user_registration_info import UserRegistrationInfo
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://vcellapi-test.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcellapi-test.cam.uchc.edu"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.UsersResourceApi(api_client)
    user_registration_info = vcell_client.UserRegistrationInfo() # UserRegistrationInfo |  (optional)

    try:
        # create vcell user
        api_instance.map_new_user(user_registration_info=user_registration_info)
    except Exception as e:
        print("Exception when calling UsersResourceApi->map_new_user: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user_registration_info** | [**UserRegistrationInfo**](UserRegistrationInfo.md)|  | [optional] 

### Return type

void (empty response body)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Successful, returning the identity |  -  |
**409** | VCell Identity not mapped, userid already exists |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **map_user**
> bool map_user(user_login_info_for_mapping=user_login_info_for_mapping)

map vcell user

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.user_login_info_for_mapping import UserLoginInfoForMapping
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://vcellapi-test.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcellapi-test.cam.uchc.edu"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.UsersResourceApi(api_client)
    user_login_info_for_mapping = vcell_client.UserLoginInfoForMapping() # UserLoginInfoForMapping |  (optional)

    try:
        # map vcell user
        api_response = api_instance.map_user(user_login_info_for_mapping=user_login_info_for_mapping)
        print("The response of UsersResourceApi->map_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->map_user: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user_login_info_for_mapping** | [**UserLoginInfoForMapping**](UserLoginInfoForMapping.md)|  | [optional] 

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
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **unmap_user**
> bool unmap_user(user_name)

remove vcell identity mapping

### Example

```python
import time
import os
import vcell_client
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://vcellapi-test.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcellapi-test.cam.uchc.edu"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.UsersResourceApi(api_client)
    user_name = 'user_name_example' # str | 

    try:
        # remove vcell identity mapping
        api_response = api_instance.unmap_user(user_name)
        print("The response of UsersResourceApi->unmap_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->unmap_user: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user_name** | **str**|  | 

### Return type

**bool**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

