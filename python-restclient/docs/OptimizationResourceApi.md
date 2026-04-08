# vcell_client.OptimizationResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_optimization_status**](OptimizationResourceApi.md#get_optimization_status) | **GET** /api/v1/optimization/{optId} | Get status, progress, or results of an optimization job
[**list_optimization_jobs**](OptimizationResourceApi.md#list_optimization_jobs) | **GET** /api/v1/optimization | List optimization jobs for the authenticated user
[**stop_optimization**](OptimizationResourceApi.md#stop_optimization) | **POST** /api/v1/optimization/{optId}/stop | Stop a running optimization job
[**submit_optimization**](OptimizationResourceApi.md#submit_optimization) | **POST** /api/v1/optimization | Submit a new parameter estimation optimization job


# **get_optimization_status**
> OptimizationJobStatus get_optimization_status(opt_id)

Get status, progress, or results of an optimization job

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.optimization_job_status import OptimizationJobStatus
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
    api_instance = vcell_client.OptimizationResourceApi(api_client)
    opt_id = 56 # int | 

    try:
        # Get status, progress, or results of an optimization job
        api_response = api_instance.get_optimization_status(opt_id)
        print("The response of OptimizationResourceApi->get_optimization_status:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OptimizationResourceApi->get_optimization_status: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **opt_id** | **int**|  | 

### Return type

[**OptimizationJobStatus**](OptimizationJobStatus.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_optimization_jobs**
> List[OptimizationJobStatus] list_optimization_jobs()

List optimization jobs for the authenticated user

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.optimization_job_status import OptimizationJobStatus
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
    api_instance = vcell_client.OptimizationResourceApi(api_client)

    try:
        # List optimization jobs for the authenticated user
        api_response = api_instance.list_optimization_jobs()
        print("The response of OptimizationResourceApi->list_optimization_jobs:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OptimizationResourceApi->list_optimization_jobs: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

[**List[OptimizationJobStatus]**](OptimizationJobStatus.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **stop_optimization**
> OptimizationJobStatus stop_optimization(opt_id)

Stop a running optimization job

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.optimization_job_status import OptimizationJobStatus
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
    api_instance = vcell_client.OptimizationResourceApi(api_client)
    opt_id = 56 # int | 

    try:
        # Stop a running optimization job
        api_response = api_instance.stop_optimization(opt_id)
        print("The response of OptimizationResourceApi->stop_optimization:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OptimizationResourceApi->stop_optimization: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **opt_id** | **int**|  | 

### Return type

[**OptimizationJobStatus**](OptimizationJobStatus.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **submit_optimization**
> OptimizationJobStatus submit_optimization(opt_problem=opt_problem)

Submit a new parameter estimation optimization job

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.opt_problem import OptProblem
from vcell_client.models.optimization_job_status import OptimizationJobStatus
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
    api_instance = vcell_client.OptimizationResourceApi(api_client)
    opt_problem = vcell_client.OptProblem() # OptProblem |  (optional)

    try:
        # Submit a new parameter estimation optimization job
        api_response = api_instance.submit_optimization(opt_problem=opt_problem)
        print("The response of OptimizationResourceApi->submit_optimization:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OptimizationResourceApi->submit_optimization: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **opt_problem** | [**OptProblem**](OptProblem.md)|  | [optional] 

### Return type

[**OptimizationJobStatus**](OptimizationJobStatus.md)

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

