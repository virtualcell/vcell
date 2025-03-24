# SourceModel


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**model_id** | **str** |  | [optional] 
**model_type** | [**ModelType**](ModelType.md) |  | [optional] 

## Example

```python
from vcell_client.models.source_model import SourceModel

# TODO update the JSON string below
json = "{}"
# create an instance of SourceModel from a JSON string
source_model_instance = SourceModel.from_json(json)
# print the JSON string representation of the object
print SourceModel.to_json()

# convert the object into a dict
source_model_dict = source_model_instance.to_dict()
# create an instance of SourceModel from a dict
source_model_form_dict = source_model.from_dict(source_model_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


