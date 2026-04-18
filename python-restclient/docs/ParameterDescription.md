# ParameterDescription


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**initial_value** | **float** |  | [optional] 
**max_value** | **float** |  | [optional] 
**min_value** | **float** |  | [optional] 
**name** | **str** |  | [optional] 
**scale** | **float** |  | [optional] 

## Example

```python
from vcell_client.models.parameter_description import ParameterDescription

# TODO update the JSON string below
json = "{}"
# create an instance of ParameterDescription from a JSON string
parameter_description_instance = ParameterDescription.from_json(json)
# print the JSON string representation of the object
print ParameterDescription.to_json()

# convert the object into a dict
parameter_description_dict = parameter_description_instance.to_dict()
# create an instance of ParameterDescription from a dict
parameter_description_form_dict = parameter_description.from_dict(parameter_description_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


