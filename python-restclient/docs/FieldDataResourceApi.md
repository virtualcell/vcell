# vcell_client.FieldDataResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**analyze_field_data_file**](FieldDataResourceApi.md#analyze_field_data_file) | **POST** /api/v1/fieldData/analyzeFieldDataFile | Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores.
[**create_field_data_from_analyzed_file**](FieldDataResourceApi.md#create_field_data_from_analyzed_file) | **POST** /api/v1/fieldData/createFieldDataFromAnalyzedFile | Take the analyzed results of the field data, modify it to your liking, then save it on the server.
[**delete_field_data**](FieldDataResourceApi.md#delete_field_data) | **DELETE** /api/v1/fieldData/delete/{fieldDataID} | Delete the selected field data.
[**get_all_field_data_ids**](FieldDataResourceApi.md#get_all_field_data_ids) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data.
[**get_field_data_shape_from_id**](FieldDataResourceApi.md#get_field_data_shape_from_id) | **GET** /api/v1/fieldData/fieldDataShape/{fieldDataID} | Get the shape of the field data. That is it&#39;s size, origin, extent, and data identifiers.


# **analyze_field_data_file**
> AnalyzedResultsFromFieldData analyze_field_data_file(file=file, file_name=file_name)

Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.analyzed_results_from_field_data import AnalyzedResultsFromFieldData
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    file = None # bytearray |  (optional)
    file_name = 'file_name_example' # str |  (optional)

    try:
        # Analyze the field data from the uploaded file. Filenames must be lowercase alphanumeric, and can contain underscores.
        api_response = api_instance.analyze_field_data_file(file=file, file_name=file_name)
        print("The response of FieldDataResourceApi->analyze_field_data_file:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->analyze_field_data_file: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **bytearray**|  | [optional] 
 **file_name** | **str**|  | [optional] 

### Return type

[**AnalyzedResultsFromFieldData**](AnalyzedResultsFromFieldData.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_field_data_from_analyzed_file**
> FieldDataSaveResults create_field_data_from_analyzed_file(analyzed_results_from_field_data=analyzed_results_from_field_data)

Take the analyzed results of the field data, modify it to your liking, then save it on the server.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.analyzed_results_from_field_data import AnalyzedResultsFromFieldData
from vcell_client.models.field_data_save_results import FieldDataSaveResults
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
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    analyzed_results_from_field_data = vcell_client.AnalyzedResultsFromFieldData() # AnalyzedResultsFromFieldData |  (optional)

    try:
        # Take the analyzed results of the field data, modify it to your liking, then save it on the server.
        api_response = api_instance.create_field_data_from_analyzed_file(analyzed_results_from_field_data=analyzed_results_from_field_data)
        print("The response of FieldDataResourceApi->create_field_data_from_analyzed_file:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->create_field_data_from_analyzed_file: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **analyzed_results_from_field_data** | [**AnalyzedResultsFromFieldData**](AnalyzedResultsFromFieldData.md)|  | [optional] 

### Return type

[**FieldDataSaveResults**](FieldDataSaveResults.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_field_data**
> delete_field_data(field_data_id)

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


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    field_data_id = 'field_data_id_example' # str | 

    try:
        # Delete the selected field data.
        api_instance.delete_field_data(field_data_id)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->delete_field_data: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **field_data_id** | **str**|  | 

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

# **get_all_field_data_ids**
> List[FieldDataReference] get_all_field_data_ids()

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


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.FieldDataResourceApi(api_client)

    try:
        # Get all of the ids used to identify, and retrieve field data.
        api_response = api_instance.get_all_field_data_ids()
        print("The response of FieldDataResourceApi->get_all_field_data_ids:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->get_all_field_data_ids: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

[**List[FieldDataReference]**](FieldDataReference.md)

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

# **get_field_data_shape_from_id**
> FieldDataShape get_field_data_shape_from_id(field_data_id)

Get the shape of the field data. That is it's size, origin, extent, and data identifiers.

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


# Enter a context with an instance of the API client
with vcell_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = vcell_client.FieldDataResourceApi(api_client)
    field_data_id = 'field_data_id_example' # str | 

    try:
        # Get the shape of the field data. That is it's size, origin, extent, and data identifiers.
        api_response = api_instance.get_field_data_shape_from_id(field_data_id)
        print("The response of FieldDataResourceApi->get_field_data_shape_from_id:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->get_field_data_shape_from_id: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **field_data_id** | **str**|  | 

### Return type

[**FieldDataShape**](FieldDataShape.md)

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
