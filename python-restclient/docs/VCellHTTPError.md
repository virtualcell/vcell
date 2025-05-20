# VCellHTTPError


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**exception_type** | **str** |  | [optional] 
**message** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.v_cell_http_error import VCellHTTPError

# TODO update the JSON string below
json = "{}"
# create an instance of VCellHTTPError from a JSON string
v_cell_http_error_instance = VCellHTTPError.from_json(json)
# print the JSON string representation of the object
print VCellHTTPError.to_json()

# convert the object into a dict
v_cell_http_error_dict = v_cell_http_error_instance.to_dict()
# create an instance of VCellHTTPError from a dict
v_cell_http_error_form_dict = v_cell_http_error.from_dict(v_cell_http_error_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


