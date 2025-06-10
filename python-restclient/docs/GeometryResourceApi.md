# vcell_client.GeometryResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**delete_geometry**](GeometryResourceApi.md#delete_geometry) | **DELETE** /api/v1/geometry/{id} | 
[**get_geometry_summaries**](GeometryResourceApi.md#get_geometry_summaries) | **GET** /api/v1/geometry/summaries | 
[**get_geometry_summary**](GeometryResourceApi.md#get_geometry_summary) | **GET** /api/v1/geometry/summary/{id} | 
[**get_geometry_vcml**](GeometryResourceApi.md#get_geometry_vcml) | **GET** /api/v1/geometry/{id} | 
[**save_geometry**](GeometryResourceApi.md#save_geometry) | **POST** /api/v1/geometry | 


# **delete_geometry**
> delete_geometry(id)



Remove specific Geometry.

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


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.GeometryResourceApi(api_client)
    id = 'id_example' # str | 

    try:
        api_instance.delete_geometry(id)
    except Exception as e:
        print("Exception when calling GeometryResourceApi->delete_geometry: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | No Content |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_geometry_summaries**
> List[GeometrySummary] get_geometry_summaries(include_public_and_shared=include_public_and_shared)



Return Geometry summaries.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.geometry_summary import GeometrySummary
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
    api_instance = vcell_client.GeometryResourceApi(api_client)
    include_public_and_shared = True # bool | Include Geometry summaries that are public and shared with the requester. Default is true. (optional)

    try:
        api_response = api_instance.get_geometry_summaries(include_public_and_shared=include_public_and_shared)
        print("The response of GeometryResourceApi->get_geometry_summaries:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling GeometryResourceApi->get_geometry_summaries: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **include_public_and_shared** | **bool**| Include Geometry summaries that are public and shared with the requester. Default is true. | [optional] 

### Return type

[**List[GeometrySummary]**](GeometrySummary.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_geometry_summary**
> GeometrySummary get_geometry_summary(id)



All of the text based information about a Geometry (dimensions, extent, origin, etc...), but not the actual Geometry itself.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.geometry_summary import GeometrySummary
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
    api_instance = vcell_client.GeometryResourceApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get_geometry_summary(id)
        print("The response of GeometryResourceApi->get_geometry_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling GeometryResourceApi->get_geometry_summary: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**GeometrySummary**](GeometrySummary.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_geometry_vcml**
> str get_geometry_vcml(id)



Returns Geometry in VCML format.

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


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.GeometryResourceApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get_geometry_vcml(id)
        print("The response of GeometryResourceApi->get_geometry_vcml:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling GeometryResourceApi->get_geometry_vcml: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

**str**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **save_geometry**
> str save_geometry(body, new_name=new_name)



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
    api_instance = vcell_client.GeometryResourceApi(api_client)
    body = 'body_example' # str | 
    new_name = 'new_name_example' # str | Name to save new Geometry under. Leave blank if re-saving existing Geometry. (optional)

    try:
        api_response = api_instance.save_geometry(body, new_name=new_name)
        print("The response of GeometryResourceApi->save_geometry:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling GeometryResourceApi->save_geometry: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | **str**|  | 
 **new_name** | **str**| Name to save new Geometry under. Leave blank if re-saving existing Geometry. | [optional] 

### Return type

**str**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: application/xml
 - **Accept**: application/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |
**422** | Unprocessable content submitted |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

