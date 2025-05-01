# vcell_client.BioModelResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**advanced_save_as_bio_model**](BioModelResourceApi.md#advanced_save_as_bio_model) | **POST** /api/v1/bioModel/advancedSaveAs | Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.
[**advanced_save_bio_model**](BioModelResourceApi.md#advanced_save_bio_model) | **POST** /api/v1/bioModel/advancedSave | Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.
[**delete_bio_model**](BioModelResourceApi.md#delete_bio_model) | **DELETE** /api/v1/bioModel/{bioModelID} | Delete the BioModel from VCell&#39;s database.
[**get_bio_model**](BioModelResourceApi.md#get_bio_model) | **GET** /api/v1/bioModel/{bioModelID} | Get BioModel.
[**get_bio_model_vcml**](BioModelResourceApi.md#get_bio_model_vcml) | **GET** /api/v1/bioModel/{bioModelID}/vcml_download | Get the BioModel in VCML format.
[**save_bio_model**](BioModelResourceApi.md#save_bio_model) | **POST** /api/v1/bioModel/save | Save the BioModel, returning saved BioModel as VCML.
[**save_bio_model_as**](BioModelResourceApi.md#save_bio_model_as) | **POST** /api/v1/bioModel/saveAs | Save as a new BioModel under the name given. Returns saved BioModel as VCML.


# **advanced_save_as_bio_model**
> str advanced_save_as_bio_model(bio_model_xml=bio_model_xml, name=name, sims_requiring_updates=sims_requiring_updates)

Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.

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
    bio_model_xml = 'bio_model_xml_example' # str |  (optional)
    name = 'name_example' # str |  (optional)
    sims_requiring_updates = ['sims_requiring_updates_example'] # List[str] |  (optional)

    try:
        # Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.
        api_response = api_instance.advanced_save_as_bio_model(bio_model_xml=bio_model_xml, name=name, sims_requiring_updates=sims_requiring_updates)
        print("The response of BioModelResourceApi->advanced_save_as_bio_model:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->advanced_save_as_bio_model: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bio_model_xml** | **str**|  | [optional] 
 **name** | **str**|  | [optional] 
 **sims_requiring_updates** | [**List[str]**](str.md)|  | [optional] 

### Return type

**str**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **advanced_save_bio_model**
> str advanced_save_bio_model(bio_model_xml=bio_model_xml, sims_requiring_updates=sims_requiring_updates)

Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.

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
    bio_model_xml = 'bio_model_xml_example' # str |  (optional)
    sims_requiring_updates = ['sims_requiring_updates_example'] # List[str] |  (optional)

    try:
        # Save the BioModel while also specifying which simulations within the BioModel need to be updated due to mathematical changes. Returns saved BioModel as VCML.
        api_response = api_instance.advanced_save_bio_model(bio_model_xml=bio_model_xml, sims_requiring_updates=sims_requiring_updates)
        print("The response of BioModelResourceApi->advanced_save_bio_model:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->advanced_save_bio_model: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bio_model_xml** | **str**|  | [optional] 
 **sims_requiring_updates** | [**List[str]**](str.md)|  | [optional] 

### Return type

**str**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

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
 - **Accept**: text/xml

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **save_bio_model**
> str save_bio_model(body=body)

Save the BioModel, returning saved BioModel as VCML.

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
        # Save the BioModel, returning saved BioModel as VCML.
        api_response = api_instance.save_bio_model(body=body)
        print("The response of BioModelResourceApi->save_bio_model:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->save_bio_model: %s\n" % e)
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
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **save_bio_model_as**
> str save_bio_model_as(bio_model_xml=bio_model_xml, name=name)

Save as a new BioModel under the name given. Returns saved BioModel as VCML.

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
    bio_model_xml = 'bio_model_xml_example' # str |  (optional)
    name = 'name_example' # str |  (optional)

    try:
        # Save as a new BioModel under the name given. Returns saved BioModel as VCML.
        api_response = api_instance.save_bio_model_as(bio_model_xml=bio_model_xml, name=name)
        print("The response of BioModelResourceApi->save_bio_model_as:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling BioModelResourceApi->save_bio_model_as: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **bio_model_xml** | **str**|  | [optional] 
 **name** | **str**|  | [optional] 

### Return type

**str**

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: text/plain

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

