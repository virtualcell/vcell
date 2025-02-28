# vcell_client.SolverResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_fv_solver_input_from_sbml**](SolverResourceApi.md#get_fv_solver_input_from_sbml) | **POST** /api/v1/solver/getFVSolverInput | Retrieve finite volume input from SBML spatial model.
[**get_fv_solver_input_from_vcml**](SolverResourceApi.md#get_fv_solver_input_from_vcml) | **POST** /api/v1/solver/getFVSolverInputFromVCML | Retrieve finite volume input from SBML spatial model.


# **get_fv_solver_input_from_sbml**
> bytearray get_fv_solver_input_from_sbml(sbml_file=sbml_file, duration=duration, output_time_step=output_time_step)

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
    duration = 5.0 # float |  (optional) (default to 5.0)
    output_time_step = 0.1 # float |  (optional) (default to 0.1)

    try:
        # Retrieve finite volume input from SBML spatial model.
        api_response = api_instance.get_fv_solver_input_from_sbml(sbml_file=sbml_file, duration=duration, output_time_step=output_time_step)
        print("The response of SolverResourceApi->get_fv_solver_input_from_sbml:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SolverResourceApi->get_fv_solver_input_from_sbml: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sbml_file** | **bytearray**|  | [optional] 
 **duration** | **float**|  | [optional] [default to 5.0]
 **output_time_step** | **float**|  | [optional] [default to 0.1]

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

# **get_fv_solver_input_from_vcml**
> bytearray get_fv_solver_input_from_vcml(vcml_file=vcml_file, simulation_name=simulation_name)

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
    vcml_file = None # bytearray |  (optional)
    simulation_name = 'simulation_name_example' # str |  (optional)

    try:
        # Retrieve finite volume input from SBML spatial model.
        api_response = api_instance.get_fv_solver_input_from_vcml(vcml_file=vcml_file, simulation_name=simulation_name)
        print("The response of SolverResourceApi->get_fv_solver_input_from_vcml:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SolverResourceApi->get_fv_solver_input_from_vcml: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **vcml_file** | **bytearray**|  | [optional] 
 **simulation_name** | **str**|  | [optional] 

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

