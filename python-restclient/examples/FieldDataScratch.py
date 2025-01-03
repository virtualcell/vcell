from vcell_client.auth.auth_utils import login_interactive
from vcell_client.api.field_data_resource_api import FieldDataResourceApi

#################
# Create Client #
#################

api_url = "https://minikube.island"  # local vcell-rest service - use `quarkus dev` in vcell-rest folder to start
# api_url = "http://localhost:9000"
api_client = login_interactive(api_base_url=api_url, insecure=True)
field_data_api = FieldDataResourceApi(api_client=api_client)

##########################
# Get Current Field Data #
##########################
ids = field_data_api.get_all_field_data_ids()
oldLenOfIDs = len(ids)

# #####################
# # Create Field Data #
# #####################
analyzed = field_data_api.analyze_field_data_file("/Users/evalencia/Downloads/file_example_TIFF_1MB.tiff", "test_file2")
results = field_data_api.create_field_data_from_analyzed_file(analyzed)

################################
# Retrieve Shape of Field Data #
################################
fd_shape = field_data_api.get_field_data_shape_from_id(results.field_data_id)
print(fd_shape)

#####################
# Delete Field Data #
#####################
ids = field_data_api.get_all_field_data_ids()
assert len(ids) == oldLenOfIDs + 1
field_data_api.delete_field_data(results.field_data_id)
ids = field_data_api.get_all_field_data_ids()
assert len(ids) == oldLenOfIDs

