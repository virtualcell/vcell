# vcell_client.VCImageResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**delete_image_vcml**](VCImageResourceApi.md#delete_image_vcml) | **DELETE** /api/v1/image/{id} | 
[**get_image_summaries**](VCImageResourceApi.md#get_image_summaries) | **GET** /api/v1/image/summaries | 
[**get_image_summary**](VCImageResourceApi.md#get_image_summary) | **GET** /api/v1/image/summary/{id} | 
[**get_image_vcml**](VCImageResourceApi.md#get_image_vcml) | **GET** /api/v1/image/{id} | 
[**save_image_vcml**](VCImageResourceApi.md#save_image_vcml) | **POST** /api/v1/image | 


# **delete_image_vcml**
> delete_image_vcml(id)



Remove specific image VCML.

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
    api_instance = vcell_client.VCImageResourceApi(api_client)
    id = 'id_example' # str | 

    try:
        api_instance.delete_image_vcml(id)
    except Exception as e:
        print("Exception when calling VCImageResourceApi->delete_image_vcml: %s\n" % e)
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
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_image_summaries**
> List[VCImageSummary] get_image_summaries(include_public_and_shared=include_public_and_shared)



Return Image summaries.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.vc_image_summary import VCImageSummary
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
    api_instance = vcell_client.VCImageResourceApi(api_client)
    include_public_and_shared = True # bool | Include Image summaries that are public and shared with the requester. Default is true. (optional)

    try:
        api_response = api_instance.get_image_summaries(include_public_and_shared=include_public_and_shared)
        print("The response of VCImageResourceApi->get_image_summaries:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling VCImageResourceApi->get_image_summaries: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **include_public_and_shared** | **bool**| Include Image summaries that are public and shared with the requester. Default is true. | [optional] 

### Return type

[**List[VCImageSummary]**](VCImageSummary.md)

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

# **get_image_summary**
> VCImageSummary get_image_summary(id)



All of the miscellaneous information about an Image (Extent, ISize, preview, etc...), but not the actual Image itself.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.vc_image_summary import VCImageSummary
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
    api_instance = vcell_client.VCImageResourceApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get_image_summary(id)
        print("The response of VCImageResourceApi->get_image_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling VCImageResourceApi->get_image_summary: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **str**|  | 

### Return type

[**VCImageSummary**](VCImageSummary.md)

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

# **get_image_vcml**
> str get_image_vcml(id)



Get specific image VCML.

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
    api_instance = vcell_client.VCImageResourceApi(api_client)
    id = 'id_example' # str | 

    try:
        api_response = api_instance.get_image_vcml(id)
        print("The response of VCImageResourceApi->get_image_vcml:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling VCImageResourceApi->get_image_vcml: %s\n" % e)
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
 - **Accept**: text/plain, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**422** | Unprocessable content submitted |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **save_image_vcml**
> str save_image_vcml(body, name=name)



Save the VCML representation of an image.

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
    api_instance = vcell_client.VCImageResourceApi(api_client)
    body = 'body_example' # str | 
    name = 'name_example' # str | Name to save new ImageVCML under. Leave blank if re-saving existing ImageVCML. (optional)

    try:
        api_response = api_instance.save_image_vcml(body, name=name)
        print("The response of VCImageResourceApi->save_image_vcml:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling VCImageResourceApi->save_image_vcml: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | **str**|  | 
 **name** | **str**| Name to save new ImageVCML under. Leave blank if re-saving existing ImageVCML. | [optional] 

### Return type

**str**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: text/plain, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authenticated |  -  |
**422** | Unprocessable content submitted |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

