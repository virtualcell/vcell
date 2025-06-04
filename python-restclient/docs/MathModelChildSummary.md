# MathModelChildSummary


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**geo_name** | **str** |  | [optional] 
**geo_dim** | **int** |  | [optional] 
**sim_names** | **List[str]** |  | [optional] 
**sim_annots** | **List[str]** |  | [optional] 
**model_type** | [**MathType**](MathType.md) |  | [optional] 
**geometry_dimension** | **int** |  | [optional] 
**geometry_name** | **str** |  | [optional] 
**simulation_annotations** | **List[str]** |  | [optional] 
**simulation_names** | **List[str]** |  | [optional] 

## Example

```python
from vcell_client.models.math_model_child_summary import MathModelChildSummary

# TODO update the JSON string below
json = "{}"
# create an instance of MathModelChildSummary from a JSON string
math_model_child_summary_instance = MathModelChildSummary.from_json(json)
# print the JSON string representation of the object
print MathModelChildSummary.to_json()

# convert the object into a dict
math_model_child_summary_dict = math_model_child_summary_instance.to_dict()
# create an instance of MathModelChildSummary from a dict
math_model_child_summary_form_dict = math_model_child_summary.from_dict(math_model_child_summary_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


