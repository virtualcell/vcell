# ApplicationInfo


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** |  | [optional] 
**type** | [**MathType**](MathType.md) |  | [optional] 
**dimensions** | **int** |  | [optional] 
**geometry_name** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.application_info import ApplicationInfo

# TODO update the JSON string below
json = "{}"
# create an instance of ApplicationInfo from a JSON string
application_info_instance = ApplicationInfo.from_json(json)
# print the JSON string representation of the object
print ApplicationInfo.to_json()

# convert the object into a dict
application_info_dict = application_info_instance.to_dict()
# create an instance of ApplicationInfo from a dict
application_info_form_dict = application_info.from_dict(application_info_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


