# CreateFieldDataFromSimulation


## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**sim_reference** | [**KeyValue**](KeyValue.md) |  | [optional] 
**job_index** | **int** |  | [optional] 
**new_field_data_name** | **str** |  | [optional] 

## Example

```python
from vcell_client.models.create_field_data_from_simulation import CreateFieldDataFromSimulation

# TODO update the JSON string below
json = "{}"
# create an instance of CreateFieldDataFromSimulation from a JSON string
create_field_data_from_simulation_instance = CreateFieldDataFromSimulation.from_json(json)
# print the JSON string representation of the object
print CreateFieldDataFromSimulation.to_json()

# convert the object into a dict
create_field_data_from_simulation_dict = create_field_data_from_simulation_instance.to_dict()
# create an instance of CreateFieldDataFromSimulation from a dict
create_field_data_from_simulation_form_dict = create_field_data_from_simulation.from_dict(create_field_data_from_simulation_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


