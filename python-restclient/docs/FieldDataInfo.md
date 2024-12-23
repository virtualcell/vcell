# FieldDataInfo


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**extent** | [**Extent**](Extent.md) |  | [optional] 
**origin** | [**Origin**](Origin.md) |  | [optional] 
**isize** | [**ISize**](ISize.md) |  | [optional] 
**data_identifier** | [**List[DataIdentifier]**](DataIdentifier.md) |  | [optional] 
**times** | **List[float]** |  | [optional] 

## Example

```python
from vcell_client.models.field_data_info import FieldDataInfo

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataInfo from a JSON string
field_data_info_instance = FieldDataInfo.from_json(json)
# print the JSON string representation of the object
print FieldDataInfo.to_json()

# convert the object into a dict
field_data_info_dict = field_data_info_instance.to_dict()
# create an instance of FieldDataInfo from a dict
field_data_info_form_dict = field_data_info.from_dict(field_data_info_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


