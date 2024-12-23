# vcell_client.BioModelResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**delete_bio_model**](BioModelResourceApi.md#delete_bio_model) | **DELETE** /api/v1/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database.
[**get_biomodel_by_id**](BioModelResourceApi.md#get_biomodel_by_id) | **GET** /api/v1/bioModel/{bioModelID} | Get BioModel information in JSON format by ID.
[**upload_bio_model**](BioModelResourceApi.md#upload_bio_model) | **POST** /api/v1/bioModel/upload_bioModel | Upload the BioModel to VCell database. Returns BioModel ID.


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

# Defining the host is optional and defaults to https://vcell-dev.cam.uchc.edu
# See configuration.py for a list of all supported configuration parameters.
configuration = vcell_client.Configuration(
    host = "https://vcell-dev.cam.uchc.edu"
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
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | No Content |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_biomodel_by_id**
> BioModel get_biomodel_by_id(bio_model_id)

Get BioModel information in JSON format by ID.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.bio_model import BioModel
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
    api_instance = vcell_client.BioModelResourceApi(api_client)
    bio_model_id = 'bio_model_id_example' # str | 

    try:
        # Get BioModel information in JSON format by ID.
        api_response = api_instance.get_biomodel_by_id(bio_model_id)
        print("The response of BioModelResourceApi->get_biomodel_by_id:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->get_biomodel_by_id: %s\n" % e)
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
**200** | return BioModel information in JSON format |  -  |
**404** | BioModel not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **upload_bio_model**
> str upload_bio_model(body=body)

Upload the BioModel to VCell database. Returns BioModel ID.

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

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.BioModelResourceApi(api_client)
    body = 'body_example' # str |  (optional)

    try:
        # Upload the BioModel to VCell database. Returns BioModel ID.
        api_response = api_instance.upload_bio_model(body=body)
        print("The response of BioModelResourceApi->upload_bio_model:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->upload_bio_model: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | **str**|  | [optional] 

### Return type

**str**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: text/xml
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**403** | Not Allowed |  -  |
**401** | Not Authorized |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

