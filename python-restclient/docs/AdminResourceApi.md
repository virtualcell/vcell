# vcell_client.AdminResourceApi

All URIs are relative to *http://localhost:9000*

Method | HTTP request | Description
------------- | ------------- | -------------
[**api_admin_get**](AdminResourceApi.md#api_admin_get) | **GET** /api/admin | 


# **api_admin_get**
> str api_admin_get()



### Example

```python
import time
import os
import vcell_client
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
    api_instance = vcell_client.AdminResourceApi(api_client)

    try:
        api_response = api_instance.api_admin_get()
        print("The response of AdminResourceApi->api_admin_get:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminResourceApi->api_admin_get: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

**str**

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

