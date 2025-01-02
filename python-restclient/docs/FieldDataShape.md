# FieldDataShape


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**extent** | [**Extent**](Extent.md) |  | [optional] 
**origin** | [**Origin**](Origin.md) |  | [optional] 
**isize** | [**ISize**](ISize.md) |  | [optional] 
**data_identifier** | [**List[DataIdentifier]**](DataIdentifier.md) |  | [optional] 
**times** | **List[float]** |  | [optional] 

## Example

```python
from vcell_client.models.field_data_shape import FieldDataShape

# TODO update the JSON string below
json = "{}"
# create an instance of FieldDataShape from a JSON string
field_data_shape_instance = FieldDataShape.from_json(json)
# print the JSON string representation of the object
print FieldDataShape.to_json()

# convert the object into a dict
field_data_shape_dict = field_data_shape_instance.to_dict()
# create an instance of FieldDataShape from a dict
field_data_shape_form_dict = field_data_shape.from_dict(field_data_shape_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


