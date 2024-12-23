# vcell_client.FieldDataResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**copy_field_data**](FieldDataResourceApi.md#copy_field_data) | **POST** /api/v1/fieldData/copy | Copy an existing field data entry.
[**create_new_field_data_from_file_already_analyzed**](FieldDataResourceApi.md#create_new_field_data_from_file_already_analyzed) | **POST** /api/v1/fieldData/createFieldDataFromFileAlreadyAnalyzed | 
[**create_new_field_data_from_simulation**](FieldDataResourceApi.md#create_new_field_data_from_simulation) | **POST** /api/v1/fieldData/createFieldDataFromSimulation | Create new field data from a simulation.
[**delete_field_data**](FieldDataResourceApi.md#delete_field_data) | **DELETE** /api/v1/fieldData | Delete the selected field data.
[**generate_field_data_estimate**](FieldDataResourceApi.md#generate_field_data_estimate) | **POST** /api/v1/fieldData/analyzeFieldDataFromFile | 
[**get_all_field_data_ids**](FieldDataResourceApi.md#get_all_field_data_ids) | **GET** /api/v1/fieldData/IDs | Get all of the ids used to identify, and retrieve field data.
[**get_field_data_from_id**](FieldDataResourceApi.md#get_field_data_from_id) | **GET** /api/v1/fieldData | Get the field data from the selected field data ID.


# **copy_field_data**
> FieldDataNoCopyConflict copy_field_data(field_data_db_operation_spec=field_data_db_operation_spec)

Copy an existing field data entry.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.field_data_db_operation_spec import FieldDataDBOperationSpec
from vcell_client.models.field_data_no_copy_conflict import FieldDataNoCopyConflict
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
    field_data_db_operation_spec = vcell_client.FieldDataDBOperationSpec() # FieldDataDBOperationSpec |  (optional)

    try:
        # Copy an existing field data entry.
        api_response = api_instance.copy_field_data(field_data_db_operation_spec=field_data_db_operation_spec)
        print("The response of FieldDataResourceApi->copy_field_data:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->copy_field_data: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **field_data_db_operation_spec** | [**FieldDataDBOperationSpec**](FieldDataDBOperationSpec.md)|  | [optional] 

### Return type

[**FieldDataNoCopyConflict**](FieldDataNoCopyConflict.md)

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

# **create_new_field_data_from_file_already_analyzed**
> FieldDataSaveResults create_new_field_data_from_file_already_analyzed(analyzed_results_from_field_data=analyzed_results_from_field_data)



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
        api_response = api_instance.create_new_field_data_from_file_already_analyzed(analyzed_results_from_field_data=analyzed_results_from_field_data)
        print("The response of FieldDataResourceApi->create_new_field_data_from_file_already_analyzed:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->create_new_field_data_from_file_already_analyzed: %s\n" % e)
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

# **create_new_field_data_from_simulation**
> ExternalDataIdentifier create_new_field_data_from_simulation(field_data_db_operation_spec=field_data_db_operation_spec)

Create new field data from a simulation.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.external_data_identifier import ExternalDataIdentifier
from vcell_client.models.field_data_db_operation_spec import FieldDataDBOperationSpec
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
    field_data_db_operation_spec = vcell_client.FieldDataDBOperationSpec() # FieldDataDBOperationSpec |  (optional)

    try:
        # Create new field data from a simulation.
        api_response = api_instance.create_new_field_data_from_simulation(field_data_db_operation_spec=field_data_db_operation_spec)
        print("The response of FieldDataResourceApi->create_new_field_data_from_simulation:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->create_new_field_data_from_simulation: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **field_data_db_operation_spec** | [**FieldDataDBOperationSpec**](FieldDataDBOperationSpec.md)|  | [optional] 

### Return type

[**ExternalDataIdentifier**](ExternalDataIdentifier.md)

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
> delete_field_data(body=body)

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
    body = 'body_example' # str |  (optional)

    try:
        # Delete the selected field data.
        api_instance.delete_field_data(body=body)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->delete_field_data: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | **str**|  | [optional] 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: text/plain
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | No Content |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **generate_field_data_estimate**
> FieldDataFileOperationSpec generate_field_data_estimate(file=file, file_name=file_name)



### Example

```python
import time
import os
import vcell_client
from vcell_client.models.field_data_file_operation_spec import FieldDataFileOperationSpec
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
        api_response = api_instance.generate_field_data_estimate(file=file, file_name=file_name)
        print("The response of FieldDataResourceApi->generate_field_data_estimate:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->generate_field_data_estimate: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **bytearray**|  | [optional] 
 **file_name** | **str**|  | [optional] 

### Return type

[**FieldDataFileOperationSpec**](FieldDataFileOperationSpec.md)

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

# **get_all_field_data_ids**
> FieldDataReferences get_all_field_data_ids()

Get all of the ids used to identify, and retrieve field data.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.field_data_references import FieldDataReferences
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

[**FieldDataReferences**](FieldDataReferences.md)

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

# **get_field_data_from_id**
> FieldDataInfo get_field_data_from_id(body=body)

Get the field data from the selected field data ID.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.field_data_info import FieldDataInfo
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
    body = 'body_example' # str |  (optional)

    try:
        # Get the field data from the selected field data ID.
        api_response = api_instance.get_field_data_from_id(body=body)
        print("The response of FieldDataResourceApi->get_field_data_from_id:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FieldDataResourceApi->get_field_data_from_id: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | **str**|  | [optional] 

### Return type

[**FieldDataInfo**](FieldDataInfo.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: text/plain
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

