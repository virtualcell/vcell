# vcell_client.UsersResourceApi

All URIs are relative to *https://vcellapi-test.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**api_v1_users_me_get**](UsersResourceApi.md#api_v1_users_me_get) | **GET** /api/v1/users/me | 


# **api_v1_users_me_get**
> User api_v1_users_me_get()



### Example

```python
import time
import os
import vcell_client
from vcell_client.models.user import User
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
        api_response = api_instance.api_v1_users_me_get()
        print("The response of UsersResourceApi->api_v1_users_me_get:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UsersResourceApi->api_v1_users_me_get: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

[**User**](User.md)

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

