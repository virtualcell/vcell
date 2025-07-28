# StandardExportInfo


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**output_context** | [**List[AnnotatedFunctionDTO]**](AnnotatedFunctionDTO.md) |  | [optional] 
**context_name** | **str** |  | [optional] 
**simulation_name** | **str** |  | [optional] 
**simulation_key** | **str** |  | [optional] 
**simulation_job** | **int** |  | [optional] 
**geometry_specs** | [**GeometrySpecDTO**](GeometrySpecDTO.md) |  | [optional] 
**time_specs** | [**TimeSpecs**](TimeSpecs.md) |  | [optional] 
**variable_specs** | [**VariableSpecs**](VariableSpecs.md) |  | [optional] 

## Example

```python
from vcell_client.models.standard_export_info import StandardExportInfo

# TODO update the JSON string below
json = "{}"
# create an instance of StandardExportInfo from a JSON string
standard_export_info_instance = StandardExportInfo.from_json(json)
# print the JSON string representation of the object
print StandardExportInfo.to_json()

# convert the object into a dict
standard_export_info_dict = standard_export_info_instance.to_dict()
# create an instance of StandardExportInfo from a dict
standard_export_info_form_dict = standard_export_info.from_dict(standard_export_info_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


