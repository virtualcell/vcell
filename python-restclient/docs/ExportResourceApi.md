# vcell_client.ExportResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**export_n5**](ExportResourceApi.md#export_n5) | **POST** /api/v1/export/N5 | 
[**export_status**](ExportResourceApi.md#export_status) | **PATCH** /api/v1/export/status | 


# **export_n5**
> int export_n5(n5_export_request=n5_export_request)



Create an N5 (ImageJ compatible) export. The request must contain the standard export information, exportable data type, dataset name, and sub-volume specifications.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.n5_export_request import N5ExportRequest
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
    api_instance = vcell_client.ExportResourceApi(api_client)
    n5_export_request = vcell_client.N5ExportRequest() # N5ExportRequest |  (optional)

    try:
        api_response = api_instance.export_n5(n5_export_request=n5_export_request)
        print("The response of ExportResourceApi->export_n5:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExportResourceApi->export_n5: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **n5_export_request** | [**N5ExportRequest**](N5ExportRequest.md)|  | [optional] 

### Return type

**int**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**400** | Bad Request. |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**422** | Unprocessable content submitted |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **export_status**
> List[ExportEvent] export_status(body=body)



Get the status of your export jobs past the timestamp (UTC format).

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.export_event import ExportEvent
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
    api_instance = vcell_client.ExportResourceApi(api_client)
    body = '2013-10-20T19:20:30+01:00' # datetime |  (optional)

    try:
        api_response = api_instance.export_status(body=body)
        print("The response of ExportResourceApi->export_status:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ExportResourceApi->export_status: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | **datetime**|  | [optional] 

### Return type

[**List[ExportEvent]**](ExportEvent.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

