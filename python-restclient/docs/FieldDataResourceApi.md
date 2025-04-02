# vcell_client.FieldDataResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**analyze_file**](FieldDataResourceApi.md#analyze_file) | **POST** /api/v1/fieldData/analyzeFile | Analyze the field data from supported files (Tiff, Zip, and Non-GPL BioFormats). Please don&#39;t use color mapped images for the files (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores.
[**copy_models_field_data**](FieldDataResourceApi.md#copy_models_field_data) | **POST** /api/v1/fieldData/copyModelsFieldData | Copy all existing field data from a BioModel/MathModel that you have access to, but don&#39;t own.
[**create_from_analyzed_file**](FieldDataResourceApi.md#create_from_analyzed_file) | **POST** /api/v1/fieldData/createFromAnalyzedFile | Take the Analyzed results of the field data, and save them to the server. User may adjust the analyzed file before uploading to edit defaults.
[**create_from_simulation**](FieldDataResourceApi.md#create_from_simulation) | **POST** /api/v1/fieldData/createFromSimulation | Create new field data from existing simulation results.
[**delete**](FieldDataResourceApi.md#delete) | **DELETE** /api/v1/fieldData/delete/{fieldDataID} | Delete the selected field data.
[**get_all_ids**](FieldDataResourceApi.md#get_all_ids) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data.
[**get_shape_from_id**](FieldDataResourceApi.md#get_shape_from_id) | **GET** /api/v1/fieldData/shape/{fieldDataID} | Get the shape of the field data. That is it&#39;s size, origin, extent, times, and data identifiers.


# **analyze_file**
> AnalyzedFile analyze_file(file=file, file_name=file_name)

Analyze the field data from supported files (Tiff, Zip, and Non-GPL BioFormats). Please don't use color mapped images for the files (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.analyzed_file import AnalyzedFile
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    file = None # bytearray |  (optional)
    file_name = 'file_name_example' # str |  (optional)

    try:
        # Analyze the field data from supported files (Tiff, Zip, and Non-GPL BioFormats). Please don't use color mapped images for the files (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores.
        api_response = api_instance.analyze_file(file=file, file_name=file_name)
        print("The response of FieldDataResourceApi->analyze_file:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->analyze_file: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **bytearray**|  | [optional] 
 **file_name** | **str**|  | [optional] 

### Return type

[**AnalyzedFile**](AnalyzedFile.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **copy_models_field_data**
> Dict[str, ExternalDataIdentifier] copy_models_field_data(source_model=source_model)

Copy all existing field data from a BioModel/MathModel that you have access to, but don't own.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.external_data_identifier import ExternalDataIdentifier
from vcell_client.models.source_model import SourceModel
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    source_model = vcell_client.SourceModel() # SourceModel |  (optional)

    try:
        # Copy all existing field data from a BioModel/MathModel that you have access to, but don't own.
        api_response = api_instance.copy_models_field_data(source_model=source_model)
        print("The response of FieldDataResourceApi->copy_models_field_data:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->copy_models_field_data: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **source_model** | [**SourceModel**](SourceModel.md)|  | [optional] 

### Return type

[**Dict[str, ExternalDataIdentifier]**](ExternalDataIdentifier.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_from_analyzed_file**
> FieldDataSavedResults create_from_analyzed_file(analyzed_file=analyzed_file)

Take the Analyzed results of the field data, and save them to the server. User may adjust the analyzed file before uploading to edit defaults.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.analyzed_file import AnalyzedFile
from vcell_client.models.field_data_saved_results import FieldDataSavedResults
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    analyzed_file = vcell_client.AnalyzedFile() # AnalyzedFile |  (optional)

    try:
        # Take the Analyzed results of the field data, and save them to the server. User may adjust the analyzed file before uploading to edit defaults.
        api_response = api_instance.create_from_analyzed_file(analyzed_file=analyzed_file)
        print("The response of FieldDataResourceApi->create_from_analyzed_file:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->create_from_analyzed_file: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **analyzed_file** | [**AnalyzedFile**](AnalyzedFile.md)|  | [optional] 

### Return type

[**FieldDataSavedResults**](FieldDataSavedResults.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_from_simulation**
> create_from_simulation(sim_key_reference=sim_key_reference, job_index=job_index, new_field_data_name=new_field_data_name)

Create new field data from existing simulation results.

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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    sim_key_reference = 'sim_key_reference_example' # str |  (optional)
    job_index = 56 # int |  (optional)
    new_field_data_name = 'new_field_data_name_example' # str |  (optional)

    try:
        # Create new field data from existing simulation results.
        api_instance.create_from_simulation(sim_key_reference=sim_key_reference, job_index=job_index, new_field_data_name=new_field_data_name)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->create_from_simulation: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sim_key_reference** | **str**|  | [optional] 
 **job_index** | **int**|  | [optional] 
 **new_field_data_name** | **str**|  | [optional] 

### Return type

void (empty response body)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: application/x-www-form-urlencoded
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Created |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete**
> delete(field_data_id)

Delete the selected field data.

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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    field_data_id = 'field_data_id_example' # str | 

    try:
        # Delete the selected field data.
        api_instance.delete(field_data_id)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->delete: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **field_data_id** | **str**|  | 

### Return type

void (empty response body)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | No Content |  -  |
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_all_ids**
> List[FieldDataReference] get_all_ids()

Get all of the ids used to identify, and retrieve field data.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.field_data_reference import FieldDataReference
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)

    try:
        # Get all of the ids used to identify, and retrieve field data.
        api_response = api_instance.get_all_ids()
        print("The response of FieldDataResourceApi->get_all_ids:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->get_all_ids: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

[**List[FieldDataReference]**](FieldDataReference.md)

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

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_shape_from_id**
> FieldDataShape get_shape_from_id(field_data_id)

Get the shape of the field data. That is it's size, origin, extent, times, and data identifiers.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.field_data_shape import FieldDataShape
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    field_data_id = 'field_data_id_example' # str | 

    try:
        # Get the shape of the field data. That is it's size, origin, extent, times, and data identifiers.
        api_response = api_instance.get_shape_from_id(field_data_id)
        print("The response of FieldDataResourceApi->get_shape_from_id:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->get_shape_from_id: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **field_data_id** | **str**|  | 

### Return type

[**FieldDataShape**](FieldDataShape.md)

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

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

