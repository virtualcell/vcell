# vcell_client.SpatialResourceApi

All URIs are relative to *https://vcell-dev.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**retrieve_finite_volume_input_from_spatial_model**](SpatialResourceApi.md#retrieve_finite_volume_input_from_spatial_model) | **GET** /api/v1/spatial/retrieveFiniteVolumeInputFromSpatialModel | Retrieve finite volume input from spatial model


# **retrieve_finite_volume_input_from_spatial_model**
> bytearray retrieve_finite_volume_input_from_spatial_model(sbml_file=sbml_file)

Retrieve finite volume input from spatial model

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
    api_instance = vcell_client.SpatialResourceApi(api_client)
    sbml_file = None # bytearray |  (optional)

    try:
        # Retrieve finite volume input from spatial model
        api_response = api_instance.retrieve_finite_volume_input_from_spatial_model(sbml_file=sbml_file)
        print("The response of SpatialResourceApi->retrieve_finite_volume_input_from_spatial_model:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling SpatialResourceApi->retrieve_finite_volume_input_from_spatial_model: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sbml_file** | **bytearray**|  | [optional] 

### Return type

**bytearray**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/octet-stream

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

