# PublishModelsRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**biomodel_keys** | **List[int]** |  | [optional] 
**mathmodel_keys** | **List[int]** |  | [optional] 

## Example

```python
from vcell_client.models.publish_models_request import PublishModelsRequest

# TODO update the JSON string below
json = "{}"
# create an instance of PublishModelsRequest from a JSON string
publish_models_request_instance = PublishModelsRequest.from_json(json)
# print the JSON string representation of the object
print(PublishModelsRequest.to_json())

# convert the object into a dict
publish_models_request_dict = publish_models_request_instance.to_dict()
# create an instance of PublishModelsRequest from a dict
publish_models_request_from_dict = PublishModelsRequest.from_dict(publish_models_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


