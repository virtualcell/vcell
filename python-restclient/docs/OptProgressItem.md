# OptProgressItem


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**num_function_evaluations** | **int** |  | [optional] 
**obj_func_value** | **float** |  | [optional] 

## Example

```python
from vcell_client.models.opt_progress_item import OptProgressItem

# TODO update the JSON string below
json = "{}"
# create an instance of OptProgressItem from a JSON string
opt_progress_item_instance = OptProgressItem.from_json(json)
# print the JSON string representation of the object
print OptProgressItem.to_json()

# convert the object into a dict
opt_progress_item_dict = opt_progress_item_instance.to_dict()
# create an instance of OptProgressItem from a dict
opt_progress_item_form_dict = opt_progress_item.from_dict(opt_progress_item_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


