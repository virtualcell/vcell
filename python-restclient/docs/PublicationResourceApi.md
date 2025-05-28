# vcell_client.PublicationResourceApi

All URIs are relative to *https://vcell.cam.uchc.edu*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_publication**](PublicationResourceApi.md#create_publication) | **POST** /api/v1/publications | Create publication
[**delete_publication**](PublicationResourceApi.md#delete_publication) | **DELETE** /api/v1/publications/{id} | Delete publication
[**get_publication_by_id**](PublicationResourceApi.md#get_publication_by_id) | **GET** /api/v1/publications/{id} | Get publication by ID
[**get_publications**](PublicationResourceApi.md#get_publications) | **GET** /api/v1/publications | Get all publications
[**update_publication**](PublicationResourceApi.md#update_publication) | **PUT** /api/v1/publications | Update publication


# **create_publication**
> int create_publication(publication=publication)

Create publication

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.publication import Publication
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
    api_instance = vcell_client.PublicationResourceApi(api_client)
    publication = vcell_client.Publication() # Publication |  (optional)

    try:
        # Create publication
        api_response = api_instance.create_publication(publication=publication)
        print("The response of PublicationResourceApi->create_publication:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PublicationResourceApi->create_publication: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **publication** | [**Publication**](Publication.md)|  | [optional] 

### Return type

**int**

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
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_publication**
> delete_publication(id)

Delete publication

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
    api_instance = vcell_client.PublicationResourceApi(api_client)
    id = 56 # int | 

    try:
        # Delete publication
        api_instance.delete_publication(id)
    except Exception as e:
        print("Exception when calling PublicationResourceApi->delete_publication: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **int**|  | 

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
**401** | Not Authorized |  -  |
**403** | Not Allowed |  -  |
**404** | Not found |  -  |
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_publication_by_id**
> Publication get_publication_by_id(id)

Get publication by ID

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.publication import Publication
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
    api_instance = vcell_client.PublicationResourceApi(api_client)
    id = 56 # int | 

    try:
        # Get publication by ID
        api_response = api_instance.get_publication_by_id(id)
        print("The response of PublicationResourceApi->get_publication_by_id:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PublicationResourceApi->get_publication_by_id: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **int**|  | 

### Return type

[**Publication**](Publication.md)

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

# **get_publications**
> List[Publication] get_publications()

Get all publications

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.publication import Publication
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
    api_instance = vcell_client.PublicationResourceApi(api_client)

    try:
        # Get all publications
        api_response = api_instance.get_publications()
        print("The response of PublicationResourceApi->get_publications:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PublicationResourceApi->get_publications: %s\n" % e)
```



### Parameters
This endpoint does not need any parameter.

### Return type

[**List[Publication]**](Publication.md)

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

# **update_publication**
> Publication update_publication(publication=publication)

Update publication

### Example

```python
import time
import os
import vcell_client
from vcell_client.models.publication import Publication
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
    api_instance = vcell_client.PublicationResourceApi(api_client)
    publication = vcell_client.Publication() # Publication |  (optional)

    try:
        # Update publication
        api_response = api_instance.update_publication(publication=publication)
        print("The response of PublicationResourceApi->update_publication:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling PublicationResourceApi->update_publication: %s\n" % e)
```



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **publication** | [**Publication**](Publication.md)|  | [optional] 

### Return type

[**Publication**](Publication.md)

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
**500** | Data Access Exception |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

