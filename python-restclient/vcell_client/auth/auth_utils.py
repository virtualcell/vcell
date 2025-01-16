import socket
import webbrowser
from contextlib import closing
from http.server import HTTPServer, BaseHTTPRequestHandler
from typing import Optional
from urllib import parse

import requests
from pydantic import BaseModel, StrictStr
from requests_oauth2client import OAuth2Client, AuthorizationRequest, AuthorizationResponse, BearerToken

from vcell_client.api_client import ApiClient, Configuration

LOGIN_SUCCESS = "/login_success"
OIDC_TEST_CALLBACK = "/oidc_test_callback"
WELL_KNOWN_CONFIG_PATH = "/.well-known/openid-configuration"


class AuthCodeResponse(BaseModel):
    access_token: Optional[StrictStr] = None
    id_token: Optional[StrictStr] = None
    refresh_token: Optional[StrictStr] = None


class OAuthHttpServer(HTTPServer):

    def __init__(self, *args, success_redirect_url: str, **kwargs):
        HTTPServer.__init__(self, *args, **kwargs)
        self.authorization_code = ""
        self.path = ""
        self.fullpath = ""
        self.success_redirect_url = success_redirect_url


class OAuthHttpHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        # redirect to the login_success page
        success_redirect_url: str = self.server.success_redirect_url
        self.send_response(303)  # Send HTTP 303 response
        self.send_header("Location", success_redirect_url)  # Set the Location header to the redirect URL
        self.end_headers()

        # # close the window instead of showing the login_success page
        # self.send_response(200)
        # self.send_header("Content-Type", "text/html")
        # self.end_headers()
        # self.wfile.write("<script type=\"application/javascript\">window.close();</script>".encode("UTF-8"))

        parsed = parse.urlparse(self.path)
        qs = parse.parse_qs(parsed.query)
        self.server.path = parsed.path
        self.server.authorization_code = qs["code"][0]
        self.server.fullpath = self.path

    def log_message(self, format, *args):
        # Override this method to suppress logging
        return

def find_free_port() -> int:
    #  all ports must be registered as http://localhost:<port>/oidc_test_callback in Auth0 redirect URI.
    #  specific ports (ending with 111) are chosen from dynamic/private port range 49152-65535
    #  to avoid conflicts with other services and be tolerated by firewalls.
    # 
    #  corresponding redirect URI registration in Auth0:
    #    http://localhost:51111/oidc_test_callback,http://localhost:52111/oidc_test_callback,
    #    http://localhost:53111/oidc_test_callback,http://localhost:54111/oidc_test_callback,
    #    http://localhost:55111/oidc_test_callback,http://localhost:56111/oidc_test_callback,
    #    http://localhost:57111/oidc_test_callback,http://localhost:58111/oidc_test_callback,
    #    http://localhost:59111/oidc_test_callback,http://localhost:60111/oidc_test_callback,
    #    http://localhost:61111/oidc_test_callback,http://localhost:62111/oidc_test_callback,
    #    http://localhost:63111/oidc_test_callback,http://localhost:64111/oidc_test_callback,
    #    http://localhost:65111/oidc_test_callback
    # 
    #  selected ports which are registered are 51111, 52111, ... 64111, 65111
    for port in range(51111, 65112, 1000):  # Iterate over the range of ports
        with closing(socket.socket(socket.AF_INET, socket.SOCK_STREAM)) as s:
            try:
                assert(type(s) == socket.socket)
                s.bind(('', port))  # Try to bind to the port
            except socket.error as e:
                continue  # If bind fails, this port is not free, so move on to the next one
            return port  # If bind succeeds, this port is free, so return it
    raise RuntimeError('No free ports found')  # If no free port is found in the range


def login_interactive_tokens(client_id: str, auth_url: str, token_url: str, jwks_uri: str, success_redirect_url: str) -> AuthCodeResponse:
    """
        This function is used to login interactively to the VCell API.
        It is used for the ApiClient class to set the access token.
        :param api_base_url: The base URL of the VCell API.
        :param client_id: The client ID of the VCell API OIDC auth provider.
        :param auth_url: The auth URL of the VCell API IODC auth provider.
        :return: An AuthCodeResponse object with access, id and refresh tokens.
    """
    hostname = "localhost"
    temp_http_port = find_free_port()

    with OAuthHttpServer((hostname, temp_http_port), OAuthHttpHandler, success_redirect_url=success_redirect_url) as httpd:
        redirectURI = f'http://{hostname}:{temp_http_port}{OIDC_TEST_CALLBACK}'
        oauth2client = OAuth2Client(
            token_endpoint=token_url,
            authorization_endpoint=auth_url,
            redirect_uri=redirectURI,
            client_id=client_id,
            jwks_uri=jwks_uri
        )

        authorization_request: AuthorizationRequest = oauth2client.authorization_request(scope="openid email profile offline_access")

        webbrowser.open(url=authorization_request.uri, new=1, autoraise=True)

        httpd.handle_request()

        authorization_response: AuthorizationResponse = authorization_request.validate_callback(httpd.fullpath)

        token: BearerToken = oauth2client.authorization_code(code=authorization_response, validate=False)

        auth_code_response: AuthCodeResponse = AuthCodeResponse(access_token=token.access_token, id_token=str(token.id_token), refresh_token=token.refresh_token)
        return auth_code_response


def get_authorization_and_token_endpoints(issuer_url: str) -> tuple[str, str, str]:
    response = requests.get(f'{issuer_url}{WELL_KNOWN_CONFIG_PATH}')
    response.raise_for_status()  # Raise an exception if the request was unsuccessful
    data = response.json()
    return data.get('authorization_endpoint'), data.get('token_endpoint'), data.get('jwks_uri')


def login_interactive(api_base_url: str = "https://vcell.cam.uchc.edu/api/v1", client_id: str = "cjoWhd7W8A8znf7Z7vizyvKJCiqTgRtf",
                      issuer_url: str = "https://dev-dzhx7i2db3x3kkvq.us.auth0.com", insecure: bool = False) -> ApiClient:
    """
        This function is used to login interactively to the VCell API.
        It is used for the ApiClient class to set the access token.
        Only change the default variables set if you know what you are doing.
        :param api_base_url: The base URL of the VCell API.
        :param client_id: The client ID of the VCell API OIDC auth provider.
        :param issuer_url: The base URL of the VCell API OIDC auth provider.
        :param insecure: If a custom endpoint is used that does not have proper SSL certificate.
        :return: An ApiClient object with the access token set.
    """
    auth_url, token_url, jwks_uri = get_authorization_and_token_endpoints(issuer_url)
    auth_code_response: AuthCodeResponse = login_interactive_tokens(
        client_id=client_id, auth_url=auth_url, token_url=token_url, jwks_uri=jwks_uri, success_redirect_url=api_base_url + LOGIN_SUCCESS)
    id_token = auth_code_response.id_token
    config = Configuration(host=api_base_url, access_token=id_token)
    if insecure:
        config.assert_hostname = False
        config.verify_ssl = False
    api_client = ApiClient(configuration=config)
    api_client.set_default_header('Authorization', f'Bearer {id_token}')
    return api_client
