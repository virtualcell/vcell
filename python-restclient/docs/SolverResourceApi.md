# vcell_client.SolverResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_fv_solver_input**](SolverResourceApi.md#get_fv_solver_input) | **GET** /api/v1/solver/getFVSolverInput | Retrieve finite volume input from SBML spatial model.


# **get_fv_solver_input**
> bytearray get_fv_solver_input(sbml_file=sbml_file)

Retrieve finite volume input from SBML spatial model.

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
    api_instance = vcell_client.SolverResourceApi(api_client)
    sbml_file = None # bytearray |  (optional)

    try:
        # Retrieve finite volume input from SBML spatial model.
        api_response = api_instance.get_fv_solver_input(sbml_file=sbml_file)
        print("The response of SolverResourceApi->get_fv_solver_input:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SolverResourceApi->get_fv_solver_input: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sbml_file** | **bytearray**|  | [optional] 

### Return type

**bytearray**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/octet-stream

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

