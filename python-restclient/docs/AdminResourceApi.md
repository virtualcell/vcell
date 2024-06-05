# vcell_client.AdminResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

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

# Defining the host is optional and defaults to https://vcell-dev.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcell-dev.cam.uchc.edu"
)


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

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/pdf

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | The PDF report |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

