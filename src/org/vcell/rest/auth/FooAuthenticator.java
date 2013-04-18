package org.vcell.rest.auth;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.security.Authenticator;
import org.restlet.util.WrapperList;

/**
 * This class defines the object that will determine if requests to our
 * "secured" API path will be allowed. Typically, you would want to use the
 * Restlet `Enroler` class to define roles for authenticated users so that
 * an instance of the Restlet `Authorizer` class can determine if an
 * authenticated user is allowed to access a resource.
 *
 * For our simple example we are assuming that all authenticated users are
 * also authorized to access the API. If our API were more complex, e.g. it
 * had sections for regular users and super users, then we would need to
 * implement more robust security with Authorizer instances along the
 * routing chain.
 */
public class FooAuthenticator extends Authenticator {
  //
  // We are using a simple list of keys to determine if a request is valid.
  // Since this class will be instantiated for each request, we use a thread
  // safe list provided by the Restlet API.
  //
  private static WrapperList<String> allowedKeys = null;

  //
  // We have to define this method in or else an unsupported
  // void constructor will be invoked by the Restlet API. See the Restlet
  // API javadocs for more constructors that could be used.
  //
  public FooAuthenticator(Context context) {
    super(context);

    if (this.allowedKeys == null) {
      this.allowedKeys = new WrapperList<String>(0);
      allowedKeys.add("abc123");
      allowedKeys.add("foobar");
    }
  }

  //
  // This is the only method, other than the constructor, that needs to be
  // defined for a an instance of Authenticator. The result of this method
  // determines if a request is from a valid (true) or invalid (false) user.
  //
  @Override
  protected boolean authenticate(Request request, Response response) {
    boolean isAllowed = false;

    String apiKey = (String)request.getAttributes().get("apikey");

    if (apiKey == null) {
      //
      // Notice that we can set standard HTTP/REST error codes very easily
      // using the Restlet API. Whenever the response is sent to the user, this
      // error code will be set to the unauthorized error code (unless a filter
      // or restlet further down the chain changes it).
      //
      response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED, "Missing API key.");
    } else {
      if (allowedKeys.contains(apiKey)) {
        isAllowed = true;
      } else {
        response.setStatus(
          Status.CLIENT_ERROR_UNAUTHORIZED,
          String.format("%s is not an allowed API key.", apiKey));
      }
    }

    return isAllowed;
  }
}