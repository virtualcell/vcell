# vcell_client.VCInfoContainerResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_vc_info_container**](VCInfoContainerResourceApi.md#get_vc_info_container) | **GET** /api/v1/vcInfoContainer | Return a single bulk collection of metadata summaries (BioModels, MathModels, Geometries, Images) visible to the requester. Anonymous requests return public records only; an authenticated request additionally includes the requester&#39;s own and shared records.


# **get_vc_info_container**
> VCInfoContainerSummary get_vc_info_container()

Return a single bulk collection of metadata summaries (BioModels, MathModels, Geometries, Images) visible to the requester. Anonymous requests return public records only; an authenticated request additionally includes the requester's own and shared records.

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.vc_info_container_summary import VCInfoContainerSummary
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
    api_instance = vcell_client.VCInfoContainerResourceApi(api_client)

    try:
        # Return a single bulk collection of metadata summaries (BioModels, MathModels, Geometries, Images) visible to the requester. Anonymous requests return public records only; an authenticated request additionally includes the requester's own and shared records.
        api_response = api_instance.get_vc_info_container()
        print("The response of VCInfoContainerResourceApi->get_vc_info_container:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling VCInfoContainerResourceApi->get_vc_info_container: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

[**VCInfoContainerSummary**](VCInfoContainerSummary.md)

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

