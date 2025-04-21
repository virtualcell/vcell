# AnalyzedFile


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
from vcell_client.models.analyzed_file import AnalyzedFile

# TODO update the JSON string below
json = "{}"
# create an instance of AnalyzedFile from a JSON string
analyzed_file_instance = AnalyzedFile.from_json(json)
# print the JSON string representation of the object
print AnalyzedFile.to_json()

# convert the object into a dict
analyzed_file_dict = analyzed_file_instance.to_dict()
# create an instance of AnalyzedFile from a dict
analyzed_file_form_dict = analyzed_file.from_dict(analyzed_file_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


