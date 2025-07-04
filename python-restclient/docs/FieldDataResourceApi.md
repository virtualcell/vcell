# vcell_client.FieldDataResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**advanced_create**](FieldDataResourceApi.md#advanced_create) | **POST** /api/v1/fieldData/advancedCreate | Create Field Data with granular detail in one request.The following files are accepted: .tif and .zip.
[**analyze_file**](FieldDataResourceApi.md#analyze_file) | **POST** /api/v1/fieldData/analyzeFile | Analyze uploaded image file (Tiff, Zip, and Non-GPL BioFormats) and return field data. Color mapped images not supported (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores.
[**copy_models_field_data**](FieldDataResourceApi.md#copy_models_field_data) | **POST** /api/v1/fieldData/copyModelsFieldData | Copy all existing field data from a BioModel/MathModel that you have access to, but don&#39;t own.
[**create_from_file**](FieldDataResourceApi.md#create_from_file) | **POST** /api/v1/fieldData/createFromFile | Submit a .zip or .tif file that converts into field data, with all defaults derived from the file submitted.
[**create_from_simulation**](FieldDataResourceApi.md#create_from_simulation) | **POST** /api/v1/fieldData/createFromSimulation | Create new field data from existing simulation results.
[**delete**](FieldDataResourceApi.md#delete) | **DELETE** /api/v1/fieldData/delete/{fieldDataID} | Delete the selected field data.
[**get_all_ids**](FieldDataResourceApi.md#get_all_ids) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data.
[**get_shape_from_id**](FieldDataResourceApi.md#get_shape_from_id) | **GET** /api/v1/fieldData/shape/{fieldDataID} | Get the shape of the field data. That is it&#39;s size, origin, extent, times, and data identifiers.
[**save**](FieldDataResourceApi.md#save) | **POST** /api/v1/fieldData/save | Take the generated field data, and save it to the server. User may adjust the analyzed file before uploading to edit defaults.


# **advanced_create**
> FieldDataSavedResults advanced_create(file=file, file_name=file_name, extent=extent, i_size=i_size, channel_names=channel_names, times=times, annotation=annotation, origin=origin)

Create Field Data with granular detail in one request.The following files are accepted: .tif and .zip.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.extent import Extent
from vcell_client.models.field_data_saved_results import FieldDataSavedResults
from vcell_client.models.i_size import ISize
from vcell_client.models.origin import Origin
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    file = None # bytearray |  (optional)
    file_name = 'file_name_example' # str |  (optional)
    extent = vcell_client.Extent() # Extent |  (optional)
    i_size = vcell_client.ISize() # ISize |  (optional)
    channel_names = ['channel_names_example'] # List[str] |  (optional)
    times = [3.4] # List[float] |  (optional)
    annotation = 'annotation_example' # str |  (optional)
    origin = vcell_client.Origin() # Origin |  (optional)

    try:
        # Create Field Data with granular detail in one request.The following files are accepted: .tif and .zip.
        api_response = api_instance.advanced_create(file=file, file_name=file_name, extent=extent, i_size=i_size, channel_names=channel_names, times=times, annotation=annotation, origin=origin)
        print("The response of FieldDataResourceApi->advanced_create:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->advanced_create: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **bytearray**|  | [optional] 
 **file_name** | **str**|  | [optional] 
 **extent** | [**Extent**](Extent.md)|  | [optional] 
 **i_size** | [**ISize**](ISize.md)|  | [optional] 
 **channel_names** | [**List[str]**](str.md)|  | [optional] 
 **times** | [**List[float]**](float.md)|  | [optional] 
 **annotation** | **str**|  | [optional] 
 **origin** | [**Origin**](Origin.md)|  | [optional] 

### Return type

[**FieldDataSavedResults**](FieldDataSavedResults.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**422** | Unprocessable content submitted |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **analyze_file**
> FieldData analyze_file(file=file, file_name=file_name)

Analyze uploaded image file (Tiff, Zip, and Non-GPL BioFormats) and return field data. Color mapped images not supported (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.field_data import FieldData
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    file = None # bytearray |  (optional)
    file_name = 'file_name_example' # str |  (optional)

    try:
        # Analyze uploaded image file (Tiff, Zip, and Non-GPL BioFormats) and return field data. Color mapped images not supported (the colors in those images will be interpreted as separate channels). Filenames must be lowercase alphanumeric, and can contain underscores.
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

[**FieldData**](FieldData.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**422** | Unprocessable content submitted |  -  |
**500** | Data Access Exception |  -  |

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
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**422** | Unprocessable content submitted |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_from_file**
> FieldDataSavedResults create_from_file(file=file, field_data_name=field_data_name)

Submit a .zip or .tif file that converts into field data, with all defaults derived from the file submitted.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.field_data_saved_results import FieldDataSavedResults
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    file = None # bytearray |  (optional)
    field_data_name = 'field_data_name_example' # str |  (optional)

    try:
        # Submit a .zip or .tif file that converts into field data, with all defaults derived from the file submitted.
        api_response = api_instance.create_from_file(file=file, field_data_name=field_data_name)
        print("The response of FieldDataResourceApi->create_from_file:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->create_from_file: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **bytearray**|  | [optional] 
 **field_data_name** | **str**|  | [optional] 

### Return type

[**FieldDataSavedResults**](FieldDataSavedResults.md)

### Authorization

[openId](../README.md#openId)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**422** | Unprocessable content submitted |  -  |
**500** | Data Access Exception |  -  |

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
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Created |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**500** | Data Access Exception |  -  |

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
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | No Content |  -  |
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**500** | Data Access Exception |  -  |

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
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**500** | Data Access Exception |  -  |

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
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **save**
> FieldDataSavedResults save(field_data=field_data)

Take the generated field data, and save it to the server. User may adjust the analyzed file before uploading to edit defaults.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.field_data import FieldData
from vcell_client.models.field_data_saved_results import FieldDataSavedResults
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    field_data = vcell_client.FieldData() # FieldData |  (optional)

    try:
        # Take the generated field data, and save it to the server. User may adjust the analyzed file before uploading to edit defaults.
        api_response = api_instance.save(field_data=field_data)
        print("The response of FieldDataResourceApi->save:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->save: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **field_data** | [**FieldData**](FieldData.md)|  | [optional] 

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
**401** | Not Authenticated |  -  |
**403** | Not Allowed |  -  |
**422** | Unprocessable content submitted |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

