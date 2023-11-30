# vcell_client.AuthResourceApi

All URIs are relative to *http://localhost:9000*

Method | HTTP request | Description
------------- | ------------- | -------------
[**code_exchange**](AuthResourceApi.md#code_exchange) | **GET** /api/auth/code-flow | Get access token using authorization code flow


# **code_exchange**
> AuthCodeResponse code_exchange(code=code, redirect_url=redirect_url)

Get access token using authorization code flow

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.auth_code_response import AuthCodeResponse
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
    api_instance = vcell_client.AuthResourceApi(api_client)
    code = 'code_example' # str |  (optional)
    redirect_url = 'redirect_url_example' # str |  (optional)

    try:
        # Get access token using authorization code flow
        api_response = api_instance.code_exchange(code=code, redirect_url=redirect_url)
        print("The response of AuthResourceApi->code_exchange:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthResourceApi->code_exchange: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **code** | **str**|  | [optional] 
 **redirect_url** | **str**|  | [optional] 

### Return type

[**AuthCodeResponse**](AuthCodeResponse.md)

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

