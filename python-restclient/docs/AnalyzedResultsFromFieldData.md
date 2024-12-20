# AnalyzedResultsFromFieldData


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
from vcell_client.models.analyzed_results_from_field_data import AnalyzedResultsFromFieldData

# TODO update the JSON string below
json = "{}"
# create an instance of AnalyzedResultsFromFieldData from a JSON string
analyzed_results_from_field_data_instance = AnalyzedResultsFromFieldData.from_json(json)
# print the JSON string representation of the object
print AnalyzedResultsFromFieldData.to_json()

# convert the object into a dict
analyzed_results_from_field_data_dict = analyzed_results_from_field_data_instance.to_dict()
# create an instance of AnalyzedResultsFromFieldData from a dict
analyzed_results_from_field_data_form_dict = analyzed_results_from_field_data.from_dict(analyzed_results_from_field_data_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


