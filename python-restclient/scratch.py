
from vcell_client import Publication, HelloWorldMessage
from vcell_client.api.publication_resource_api import PublicationResourceApi
from datetime import date
import pandas as pd  # for pretty printing only
from vcell_client.auth.auth_utils import login_interactive
from vcell_client.api.hello_world_api import HelloWorldApi

api_url: str = "http://localhost:9000"  # local vcell-rest service - use `quarkus dev` in vcell-rest folder to start
client_id: str = 'cjoWhd7W8A8znf7Z7vizyvKJCiqTgRtf' # default client id defined in keycloak for quarkus dev services
auth_url: str = 'https://dev-dzhx7i2db3x3kkvq.us.auth0.com/authorize'
token_url: str = 'https://dev-dzhx7i2db3x3kkvq.us.auth0.com/oauth/token'

print("log in with vcell userid/password or google account")
api_client = login_interactive(api_base_url=api_url, client_id=client_id, auth_url=auth_url, token_url=token_url)

# access resource-specific api (all such api objects can use the same api_client)
publication_api = PublicationResourceApi(api_client)
helloworld_api = HelloWorldApi(api_client)


hello: HelloWorldMessage = helloworld_api.get_hello_world()
print(hello)

# get all publications (expecting [] unless other publications have been added outside this notebook)
initial_pubs: list[Publication] = publication_api.get_publications()
print(f"initial publications: {initial_pubs}")

# create two publications and add them to the database (needs admin role)
# pub1 = Publication(title="publication 1", authors=["author 1", "author 2"], year=1995, citation="citation 1",
#     pubmedid="pubmedid 1", doi="doi 1", endnoteid=1, url="url 1", wittid=1, biomodel_refs=[], mathmodel_refs=[], var_date=date.today())
# pub2 = pub1.model_copy()
# pub2.title="publication 2"
# pub1_key: int = publication_api.create_publication(publication=pub1)
# pub2_key: int = publication_api.create_publication(publication=pub2)

# get all publications again and pretty print them with pandas (doesn't need auth)
some_pubs: list[Publication] = publication_api.get_publications()
print(pd.DataFrame([pub.to_dict() for pub in some_pubs]))

# get one publication by id (doesn't need auth)
# pub1_from_db = publication_api.get_publication_by_id(id=pub1_key)
# print(pd.DataFrame([pub1_from_db.to_dict()]))

# delete the publications (needs admin role)
# for pub in some_pubs:
#     publication_api.delete_publication(id=pub.pub_key)

# confirm that all publications have been deleted
final_pubs: list[Publication] = publication_api.get_publications()
print(f"final publications: {final_pubs}")
