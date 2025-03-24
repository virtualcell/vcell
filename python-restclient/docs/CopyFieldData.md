# CopyFieldData


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**model_id** | **str** |  | [optional] 
**model_type** | [**ModelType**](ModelType.md) |  | [optional] 

## Example

```python
from vcell_client.models.copy_field_data import CopyFieldData

# TODO update the JSON string below
json = "{}"
# create an instance of CopyFieldData from a JSON string
copy_field_data_instance = CopyFieldData.from_json(json)
# print the JSON string representation of the object
print CopyFieldData.to_json()

# convert the object into a dict
copy_field_data_dict = copy_field_data_instance.to_dict()
# create an instance of CopyFieldData from a dict
copy_field_data_form_dict = copy_field_data.from_dict(copy_field_data_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


