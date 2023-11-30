# MathmodelRef


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**mm_key** | **int** |  | [optional] 
**name** | **str** |  | [optional] 
**owner_name** | **str** |  | [optional] 
**owner_key** | **int** |  | [optional] 
**version_flag** | **int** |  | [optional] 

## Example

```python
from vcell_client.models.mathmodel_ref import MathmodelRef

# TODO update the JSON string below
json = "{}"
# create an instance of MathmodelRef from a JSON string
mathmodel_ref_instance = MathmodelRef.from_json(json)
# print the JSON string representation of the object
print MathmodelRef.to_json()

# convert the object into a dict
mathmodel_ref_dict = mathmodel_ref_instance.to_dict()
# create an instance of MathmodelRef from a dict
mathmodel_ref_form_dict = mathmodel_ref.from_dict(mathmodel_ref_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


