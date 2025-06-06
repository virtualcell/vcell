# vcell_client.MathModelResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**delete_math_model**](MathModelResourceApi.md#delete_math_model) | **DELETE** /api/v1/mathModel/{id} | 
[**get_summaries**](MathModelResourceApi.md#get_summaries) | **GET** /api/v1/mathModel/summaries | 
[**get_summary**](MathModelResourceApi.md#get_summary) | **GET** /api/v1/mathModel/summary/{id} | 
[**get_vcml**](MathModelResourceApi.md#get_vcml) | **GET** /api/v1/mathModel/{id} | 
[**save_math_model**](MathModelResourceApi.md#save_math_model) | **POST** /api/v1/mathModel | 


# **delete_math_model**
> delete_math_model(id)



Remove specific Math Model.

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
    api_instance = vcell_client.MathModelResourceApi(api_client)
    id = 'id_example' # str | 

    try:
        api_instance.delete_math_model(id)
    except Exception as e:
        print("Exception when calling MathModelResourceApi->delete_math_model: %s\n" % e)
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

# **get_summaries**
> List[MathModelSummary] get_summaries(include_public_and_shared=include_public_and_shared)



Return MathModel summaries.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.math_model_summary import MathModelSummary
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
    api_instance = vcell_client.MathModelResourceApi(api_client)
    include_public_and_shared = True # bool | Include MathModel summaries that are public and shared with the requester. Default is true. (optional)

    try:
        api_response = api_instance.get_summaries(include_public_and_shared=include_public_and_shared)
        print("The response of MathModelResourceApi->get_summaries:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling MathModelResourceApi->get_summaries: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **include_public_and_shared** | **bool**| Include MathModel summaries that are public and shared with the requester. Default is true. | [optional] 

### Return type

[**List[MathModelSummary]**](MathModelSummary.md)

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

# **get_summary**
> MathModelSummary get_summary(id)



All of the text based information about a MathModel (summary, version, publication status, etc...), but not the actual MathModel itself.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.math_model_summary import MathModelSummary
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
    api_instance = vcell_client.MathModelResourceApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get_summary(id)
        print("The response of MathModelResourceApi->get_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling MathModelResourceApi->get_summary: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**MathModelSummary**](MathModelSummary.md)

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

# **get_vcml**
> str get_vcml(id)



Returns MathModel in VCML format.

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
    api_instance = vcell_client.MathModelResourceApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get_vcml(id)
        print("The response of MathModelResourceApi->get_vcml:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling MathModelResourceApi->get_vcml: %s\n" % e)
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

# **save_math_model**
> str save_math_model(body, new_name=new_name, sim_names=sim_names)



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
    api_instance = vcell_client.MathModelResourceApi(api_client)
    body = 'body_example' # str | 
    new_name = 'new_name_example' # str | Name to save new MathModel under. Leave blank if re-saving existing MathModel. (optional)
    sim_names = ['sim_names_example'] # List[str] | The name of simulations that will be prepared for future execution. (optional)

    try:
        api_response = api_instance.save_math_model(body, new_name=new_name, sim_names=sim_names)
        print("The response of MathModelResourceApi->save_math_model:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling MathModelResourceApi->save_math_model: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | **str**|  | 
 **new_name** | **str**| Name to save new MathModel under. Leave blank if re-saving existing MathModel. | [optional] 
 **sim_names** | [**List[str]**](str.md)| The name of simulations that will be prepared for future execution. | [optional] 

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
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

