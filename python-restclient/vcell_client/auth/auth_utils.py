import socket
import webbrowser
from contextlib import closing
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib import parse

from IPython.core.display_functions import display
from requests_oauth2client import OAuth2Client, AuthorizationRequest, AuthorizationResponse, BearerToken

from vcell_client import AuthCodeResponse
from vcell_client.api_client import ApiClient, Configuration


class OAuthHttpServer(HTTPServer):
    def __init__(self, *args, **kwargs):
        HTTPServer.__init__(self, *args, **kwargs)
        self.authorization_code = ""
        self.path = ""
        self.fullpath = ""


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
        self.server.fullpath = self.path


def find_free_port() -> int:
    with closing(socket.socket(socket.AF_INET, socket.SOCK_STREAM)) as s:
        s.bind(('', 0))
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        return int(s.getsockname()[1])


def login_interactive_tokens(api_base_url: str, client_id: str, auth_url: str, token_url: str) -> AuthCodeResponse:
    """
        This function is used to login interactively to the VCell API.
        It is used for the ApiClient class to set the access token.
        :param api_base_url: The base URL of the VCell API.
        :param client_id: The client ID of the VCell API OIDC auth provider.
        :param auth_url: The auth URL of the VCell API IODC auth provider.
        :return: An AuthCodeResponse object with access, id and refresh tokens.
    """
    hostname = "localhost"
    temp_http_port = 9999 # find_free_port()

    with OAuthHttpServer((hostname, temp_http_port), OAuthHttpHandler) as httpd:
        redirectURI = f'http://{hostname}:{temp_http_port}/oidc_test_callback'

        oauth2client = OAuth2Client(
            token_endpoint=token_url,
            authorization_endpoint=auth_url,
            redirect_uri=redirectURI,
            client_id=client_id,
            jwks_uri="https://dev-dzhx7i2db3x3kkvq.us.auth0.com/.well-known/jwks.json"
        )

        authorization_request: AuthorizationRequest = oauth2client.authorization_request(scope="openid email profile offline_access")
        display(authorization_request)

        webbrowser.open(url=authorization_request.uri, new=1, autoraise=True)

        httpd.handle_request()

        authorization_response: AuthorizationResponse = authorization_request.validate_callback(httpd.fullpath)

        token: BearerToken = oauth2client.authorization_code(code=authorization_response, validate=False)
        display(token)

        auth_code_response: AuthCodeResponse = AuthCodeResponse(access_token=token.access_token, id_token=str(token.id_token), refresh_token=token.refresh_token)
        return auth_code_response


def login_interactive(api_base_url: str, client_id: str, auth_url: str, token_url: str) -> ApiClient:
    """
        This function is used to login interactively to the VCell API.
        It is used for the ApiClient class to set the access token.
        :param api_base_url: The base URL of the VCell API.
        :param client_id: The client ID of the VCell API OIDC auth provider.
        :param auth_url: The auth URL of the VCell API IODC auth provider.
        :return: An ApiClient object with the access token set.
    """
    auth_code_response: AuthCodeResponse = login_interactive_tokens(api_base_url, client_id, auth_url, token_url)
    id_token = auth_code_response.id_token
    api_client = ApiClient(configuration=Configuration(host=api_base_url, access_token=id_token))
    api_client.set_default_header('Authorization', f'Bearer {id_token}')
    return api_client

