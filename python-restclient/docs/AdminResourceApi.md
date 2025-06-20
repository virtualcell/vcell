# vcell_client.AdminResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_usage**](AdminResourceApi.md#get_usage) | **GET** /api/v1/admin/usage | Get usage summary


# **get_usage**
> bytearray get_usage()

Get usage summary

### Example

```python
import time
import os
import vcell_client
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://vcell.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcell.cam.uchc.edu"
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
        # Get usage summary
        api_response = api_instance.get_usage()
        print("The response of AdminResourceApi->get_usage:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminResourceApi->get_usage: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

**bytearray**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/pdf, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | The PDF report |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

