# vcell_client.HelloWorldApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_hello_world**](HelloWorldApi.md#get_hello_world) | **GET** /api/v1/helloworld | Get hello world message.


# **get_hello_world**
> HelloWorldMessage get_hello_world()

Get hello world message.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.hello_world_message import HelloWorldMessage
from vcell_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://vcell.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcell.cam.uchc.edu"
)


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.HelloWorldApi(api_client)

    try:
        # Get hello world message.
        api_response = api_instance.get_hello_world()
        print("The response of HelloWorldApi->get_hello_world:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling HelloWorldApi->get_hello_world: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

[**HelloWorldMessage**](HelloWorldMessage.md)

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

