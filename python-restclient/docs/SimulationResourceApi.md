# vcell_client.SimulationResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_simulation_status**](SimulationResourceApi.md#get_simulation_status) | **GET** /api/v1/Simulation/{simID}/simulationStatus | Get the status of simulation running
[**start_simulation**](SimulationResourceApi.md#start_simulation) | **POST** /api/v1/Simulation/{simID}/startSimulation | Start a simulation.
[**stop_simulation**](SimulationResourceApi.md#stop_simulation) | **POST** /api/v1/Simulation/{simID}/stopSimulation | Stop a simulation.


# **get_simulation_status**
> SimulationStatusPersistentRecord get_simulation_status(sim_id, bio_model_id=bio_model_id, math_model_id=math_model_id)

Get the status of simulation running

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.simulation_status_persistent_record import SimulationStatusPersistentRecord
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
    api_instance = vcell_client.SimulationResourceApi(api_client)
    sim_id = 'sim_id_example' # str | 
    bio_model_id = 'bio_model_id_example' # str |  (optional)
    math_model_id = 'math_model_id_example' # str |  (optional)

    try:
        # Get the status of simulation running
        api_response = api_instance.get_simulation_status(sim_id, bio_model_id=bio_model_id, math_model_id=math_model_id)
        print("The response of SimulationResourceApi->get_simulation_status:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SimulationResourceApi->get_simulation_status: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sim_id** | **str**|  | 
 **bio_model_id** | **str**|  | [optional] 
 **math_model_id** | **str**|  | [optional] 

### Return type

[**SimulationStatusPersistentRecord**](SimulationStatusPersistentRecord.md)

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
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **start_simulation**
> List[StatusMessage] start_simulation(sim_id)

Start a simulation.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.status_message import StatusMessage
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
    api_instance = vcell_client.SimulationResourceApi(api_client)
    sim_id = 'sim_id_example' # str | 

    try:
        # Start a simulation.
        api_response = api_instance.start_simulation(sim_id)
        print("The response of SimulationResourceApi->start_simulation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SimulationResourceApi->start_simulation: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sim_id** | **str**|  | 

### Return type

[**List[StatusMessage]**](StatusMessage.md)

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
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **stop_simulation**
> List[StatusMessage] stop_simulation(sim_id)

Stop a simulation.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.status_message import StatusMessage
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
    api_instance = vcell_client.SimulationResourceApi(api_client)
    sim_id = 'sim_id_example' # str | 

    try:
        # Stop a simulation.
        api_response = api_instance.stop_simulation(sim_id)
        print("The response of SimulationResourceApi->stop_simulation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SimulationResourceApi->stop_simulation: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sim_id** | **str**|  | 

### Return type

[**List[StatusMessage]**](StatusMessage.md)

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
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

