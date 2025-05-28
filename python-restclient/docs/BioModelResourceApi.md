# vcell_client.BioModelResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**delete_bio_model**](BioModelResourceApi.md#delete_bio_model) | **DELETE** /api/v1/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database.
[**get_bio_model**](BioModelResourceApi.md#get_bio_model) | **GET** /api/v1/bioModel/{bioModelID} | Get BioModel.
[**get_bio_model_summaries**](BioModelResourceApi.md#get_bio_model_summaries) | **GET** /api/v1/bioModel/summaries | Return BioModel summaries.
[**get_bio_model_summary**](BioModelResourceApi.md#get_bio_model_summary) | **GET** /api/v1/bioModel/{bioModelID}/summary | All of the text based information about a BioModel (summary, version, publication status, etc...), but not the actual BioModel itself.
[**get_bio_model_vcml**](BioModelResourceApi.md#get_bio_model_vcml) | **GET** /api/v1/bioModel/{bioModelID}/vcml_download | Get the BioModel in VCML format.
[**save_bio_model**](BioModelResourceApi.md#save_bio_model) | **POST** /api/v1/bioModel | Save&#39;s the given BioModel. Optional parameters of name and simulations to update due to math changes. Returns saved BioModel as VCML.


# **delete_bio_model**
> delete_bio_model(bio_model_id)

Delete the BioModel from VCell's database.

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
    api_instance = vcell_client.BioModelResourceApi(api_client)
    bio_model_id = 'bio_model_id_example' # str | 

    try:
        # Delete the BioModel from VCell's database.
        api_instance.delete_bio_model(bio_model_id)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->delete_bio_model: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bio_model_id** | **str**|  | 

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
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_bio_model**
> BioModel get_bio_model(bio_model_id)

Get BioModel.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.bio_model import BioModel
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
    api_instance = vcell_client.BioModelResourceApi(api_client)
    bio_model_id = 'bio_model_id_example' # str | 

    try:
        # Get BioModel.
        api_response = api_instance.get_bio_model(bio_model_id)
        print("The response of BioModelResourceApi->get_bio_model:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->get_bio_model: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bio_model_id** | **str**|  | 

### Return type

[**BioModel**](BioModel.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_bio_model_summaries**
> List[BioModelSummary] get_bio_model_summaries(include_public_and_shared=include_public_and_shared)

Return BioModel summaries.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.bio_model_summary import BioModelSummary
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
    api_instance = vcell_client.BioModelResourceApi(api_client)
    include_public_and_shared = True # bool | Includes BioModel summaries that are public or shared with requester. (optional)

    try:
        # Return BioModel summaries.
        api_response = api_instance.get_bio_model_summaries(include_public_and_shared=include_public_and_shared)
        print("The response of BioModelResourceApi->get_bio_model_summaries:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->get_bio_model_summaries: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **include_public_and_shared** | **bool**| Includes BioModel summaries that are public or shared with requester. | [optional] 

### Return type

[**List[BioModelSummary]**](BioModelSummary.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_bio_model_summary**
> BioModelSummary get_bio_model_summary(bio_model_id)

All of the text based information about a BioModel (summary, version, publication status, etc...), but not the actual BioModel itself.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.bio_model_summary import BioModelSummary
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
    api_instance = vcell_client.BioModelResourceApi(api_client)
    bio_model_id = 'bio_model_id_example' # str | 

    try:
        # All of the text based information about a BioModel (summary, version, publication status, etc...), but not the actual BioModel itself.
        api_response = api_instance.get_bio_model_summary(bio_model_id)
        print("The response of BioModelResourceApi->get_bio_model_summary:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->get_bio_model_summary: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bio_model_id** | **str**|  | 

### Return type

[**BioModelSummary**](BioModelSummary.md)

### Authorization

No authorization required

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

# **get_bio_model_vcml**
> str get_bio_model_vcml(bio_model_id)

Get the BioModel in VCML format.

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
    api_instance = vcell_client.BioModelResourceApi(api_client)
    bio_model_id = 'bio_model_id_example' # str | 

    try:
        # Get the BioModel in VCML format.
        api_response = api_instance.get_bio_model_vcml(bio_model_id)
        print("The response of BioModelResourceApi->get_bio_model_vcml:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->get_bio_model_vcml: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bio_model_id** | **str**|  | 

### Return type

**str**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/xml, application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **save_bio_model**
> str save_bio_model(save_bio_model=save_bio_model)

Save's the given BioModel. Optional parameters of name and simulations to update due to math changes. Returns saved BioModel as VCML.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.save_bio_model import SaveBioModel
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
    api_instance = vcell_client.BioModelResourceApi(api_client)
    save_bio_model = vcell_client.SaveBioModel() # SaveBioModel |  (optional)

    try:
        # Save's the given BioModel. Optional parameters of name and simulations to update due to math changes. Returns saved BioModel as VCML.
        api_response = api_instance.save_bio_model(save_bio_model=save_bio_model)
        print("The response of BioModelResourceApi->save_bio_model:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->save_bio_model: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **save_bio_model** | [**SaveBioModel**](SaveBioModel.md)|  | [optional] 

### Return type

**str**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: application/json
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

