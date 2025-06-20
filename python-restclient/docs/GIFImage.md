# GIFImage


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**gif_encoded_data** | **bytearray** |  | [optional] 
**size** | [**ISize**](ISize.md) |  | [optional] 

## Example

```python
from vcell_client.models.gif_image import GIFImage

# TODO update the JSON string below
json = "{}"
# create an instance of GIFImage from a JSON string
gif_image_instance = GIFImage.from_json(json)
# print the JSON string representation of the object
print GIFImage.to_json()

# convert the object into a dict
gif_image_dict = gif_image_instance.to_dict()
# create an instance of GIFImage from a dict
gif_image_form_dict = gif_image.from_dict(gif_image_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


