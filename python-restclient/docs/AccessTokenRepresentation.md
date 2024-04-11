# AccessTokenRepresentation


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**token** | **str** |  | [optional] 
**creation_date_seconds** | **int** |  | [optional] 
**expire_date_seconds** | **int** |  | [optional] 
**user_id** | **str** |  | [optional] 
**user_key** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.access_token_representation import AccessTokenRepresentation

# TODO update the JSON string below
json = "{}"
# create an instance of AccessTokenRepresentation from a JSON string
access_token_representation_instance = AccessTokenRepresentation.from_json(json)
# print the JSON string representation of the object
print AccessTokenRepresentation.to_json()

# convert the object into a dict
access_token_representation_dict = access_token_representation_instance.to_dict()
# create an instance of AccessTokenRepresentation from a dict
access_token_representation_form_dict = access_token_representation.from_dict(access_token_representation_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


