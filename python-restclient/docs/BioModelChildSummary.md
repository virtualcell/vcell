# BioModelChildSummary


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**app_types** | [**List[MathType]**](MathType.md) |  | [optional] 
**geometry_dimensions** | **List[int]** |  | [optional] 
**geometry_names** | **List[str]** |  | [optional] 
**simulation_context_annotations** | **List[str]** |  | [optional] 
**simulation_context_names** | **List[str]** |  | [optional] 
**all_simulation_names** | **List[List[str]]** |  | [optional] 
**all_simulation_annots** | **List[List[str]]** |  | [optional] 
**application_info** | [**List[ApplicationInfo]**](ApplicationInfo.md) |  | [optional] 

## Example

```python
from vcell_client.models.bio_model_child_summary import BioModelChildSummary

# TODO update the JSON string below
json = "{}"
# create an instance of BioModelChildSummary from a JSON string
bio_model_child_summary_instance = BioModelChildSummary.from_json(json)
# print the JSON string representation of the object
print BioModelChildSummary.to_json()

# convert the object into a dict
bio_model_child_summary_dict = bio_model_child_summary_instance.to_dict()
# create an instance of BioModelChildSummary from a dict
bio_model_child_summary_form_dict = bio_model_child_summary.from_dict(bio_model_child_summary_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


