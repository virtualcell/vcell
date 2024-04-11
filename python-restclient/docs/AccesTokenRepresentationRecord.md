# AccesTokenRepresentationRecord


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
from vcell_client.models.acces_token_representation_record import AccesTokenRepresentationRecord

# TODO update the JSON string below
json = "{}"
# create an instance of AccesTokenRepresentationRecord from a JSON string
acces_token_representation_record_instance = AccesTokenRepresentationRecord.from_json(json)
# print the JSON string representation of the object
print AccesTokenRepresentationRecord.to_json()

# convert the object into a dict
acces_token_representation_record_dict = acces_token_representation_record_instance.to_dict()
# create an instance of AccesTokenRepresentationRecord from a dict
acces_token_representation_record_form_dict = acces_token_representation_record.from_dict(acces_token_representation_record_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


