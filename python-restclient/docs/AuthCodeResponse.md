# AuthCodeResponse


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**access_token** | **str** |  | [optional] 
**id_token** | **str** |  | [optional] 
**refresh_token** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.auth_code_response import AuthCodeResponse

# TODO update the JSON string below
json = "{}"
# create an instance of AuthCodeResponse from a JSON string
auth_code_response_instance = AuthCodeResponse.from_json(json)
# print the JSON string representation of the object
print AuthCodeResponse.to_json()

# convert the object into a dict
auth_code_response_dict = auth_code_response_instance.to_dict()
# create an instance of AuthCodeResponse from a dict
auth_code_response_form_dict = auth_code_response.from_dict(auth_code_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


