# Shape


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
from vcell_client.models.shape import Shape

# TODO update the JSON string below
json = "{}"
# create an instance of Shape from a JSON string
shape_instance = Shape.from_json(json)
# print the JSON string representation of the object
print Shape.to_json()

# convert the object into a dict
shape_dict = shape_instance.to_dict()
# create an instance of Shape from a dict
shape_form_dict = shape.from_dict(shape_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


