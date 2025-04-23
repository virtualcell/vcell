# FieldData


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**short_spec_data** | **List[List[List[int]]]** |  | [optional] 
**double_spec_data** | **List[List[List[float]]]** |  | [optional] 
**var_names** | **List[str]** |  | [optional] 
**times** | **List[float]** |  | [optional] 
**origin** | [**Origin**](Origin.md) |  | [optional] 
**extent** | [**Extent**](Extent.md) |  | [optional] 
**isize** | [**ISize**](ISize.md) |  | [optional] 
**annotation** | **str** |  | [optional] 
**name** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.field_data import FieldData

# TODO update the JSON string below
json = "{}"
# create an instance of FieldData from a JSON string
field_data_instance = FieldData.from_json(json)
# print the JSON string representation of the object
print FieldData.to_json()

# convert the object into a dict
field_data_dict = field_data_instance.to_dict()
# create an instance of FieldData from a dict
field_data_form_dict = field_data.from_dict(field_data_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


