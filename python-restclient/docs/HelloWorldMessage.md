# HelloWorldMessage


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**message** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.hello_world_message import HelloWorldMessage

# TODO update the JSON string below
json = "{}"
# create an instance of HelloWorldMessage from a JSON string
hello_world_message_instance = HelloWorldMessage.from_json(json)
# print the JSON string representation of the object
print HelloWorldMessage.to_json()

# convert the object into a dict
hello_world_message_dict = hello_world_message_instance.to_dict()
# create an instance of HelloWorldMessage from a dict
hello_world_message_form_dict = hello_world_message.from_dict(hello_world_message_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


