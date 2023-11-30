import webbrowser
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib import parse

from IPython.core.display_functions import display
from requests_oauthlib import OAuth2Session

from vcell_client import AuthCodeResponse
from vcell_client.api.auth_resource_api import AuthResourceApi
from vcell_client.api_client import ApiClient, Configuration
from contextlib import closing
import socket


class OAuthHttpServer(HTTPServer):
    def __init__(self, *args, **kwargs):
        HTTPServer.__init__(self, *args, **kwargs)
        self.authorization_code = ""
        self.path = ""


class OAuthHttpHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-Type", "text/html")
        self.end_headers()
        self.wfile.write("<script type=\"application/javascript\">window.close();</script>".encode("UTF-8"))

        parsed = parse.urlparse(self.path)
        qs = parse.parse_qs(parsed.query)
        self.server.path = parsed.path
        self.server.authorization_code = qs["code"][0]


def find_free_port() -> int:
    with closing(socket.socket(socket.AF_INET, socket.SOCK_STREAM)) as s:
        s.bind(('', 0))
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        return int(s.getsockname()[1])


def login_interactive_tokens(api_base_url: str, client_id: str, auth_url: str) -> AuthCodeResponse:
    """
        This function is used to login interactively to the VCell API.
        It is used for the ApiClient class to set the access token.
        :param api_base_url: The base URL of the VCell API.
        :param client_id: The client ID of the VCell API OIDC auth provider.
        :param auth_url: The auth URL of the VCell API IODC auth provider.
        :return: An AuthCodeResponse object with access, id and refresh tokens.
    """
    hostname = socket.gethostname()
    temp_http_port = find_free_port()
    # display("temp_http_port: " + str(temp_http_port))
    # display("hostname: " + hostname)

    with OAuthHttpServer((hostname, temp_http_port), OAuthHttpHandler) as httpd:
        redirectURI = f'http://{hostname}:{temp_http_port}/oidc_test_callback'

        oauth = OAuth2Session(client_id=client_id, redirect_uri=redirectURI, scope=["microprofile-jwt", "openid"])

        # Generate authorization URL
        authorization_url, state = oauth.authorization_url(auth_url)
        authorization_url += "&response_mode=query" + "&prompt=login" + "&nonce==Ib3bq2ecIpwC8rhuozWCB5Gs5bjxyIli6T-AjqjfY2s"

        # display(authorization_url)

        webbrowser.open_new(authorization_url)

        httpd.handle_request()

        path = httpd.path
        # display("path: " + path)

        auth_code = httpd.authorization_code
        # display("auth_code: " + auth_code)

        unauthorized_api_client = ApiClient(configuration=Configuration(host=api_base_url))
        auth_api = AuthResourceApi(unauthorized_api_client)
        auth_code_response: AuthCodeResponse = auth_api.code_exchange(code=auth_code, redirect_url=redirectURI)
        # display(auth_code_response.access_token)
        return auth_code_response


def login_interactive(api_base_url: str, client_id: str, auth_url: str) -> ApiClient:
    """
        This function is used to login interactively to the VCell API.
        It is used for the ApiClient class to set the access token.
        :param api_base_url: The base URL of the VCell API.
        :param client_id: The client ID of the VCell API OIDC auth provider.
        :param auth_url: The auth URL of the VCell API IODC auth provider.
        :return: An ApiClient object with the access token set.
    """
    auth_code_response: AuthCodeResponse = login_interactive_tokens(api_base_url, client_id, auth_url)
    access_token = auth_code_response.access_token
    api_client = ApiClient(configuration=Configuration(host=api_base_url, access_token=access_token))
    api_client.set_default_header('Authorization', f'Bearer {access_token}')
    return api_client

