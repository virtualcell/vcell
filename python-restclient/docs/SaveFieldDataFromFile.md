# SaveFieldDataFromFile


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**short_spec_data** | **List[List[List[int]]]** |  | [optional] 
**var_names** | **List[str]** |  | [optional] 
**times** | **List[float]** |  | [optional] 
**origin** | [**Origin**](Origin.md) |  | [optional] 
**extent** | [**Extent**](Extent.md) |  | [optional] 
**isize** | [**ISize**](ISize.md) |  | [optional] 
**annotation** | **str** |  | [optional] 
**name** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.save_field_data_from_file import SaveFieldDataFromFile

# TODO update the JSON string below
json = "{}"
# create an instance of SaveFieldDataFromFile from a JSON string
save_field_data_from_file_instance = SaveFieldDataFromFile.from_json(json)
# print the JSON string representation of the object
print SaveFieldDataFromFile.to_json()

# convert the object into a dict
save_field_data_from_file_dict = save_field_data_from_file_instance.to_dict()
# create an instance of SaveFieldDataFromFile from a dict
save_field_data_from_file_form_dict = save_field_data_from_file.from_dict(save_field_data_from_file_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


