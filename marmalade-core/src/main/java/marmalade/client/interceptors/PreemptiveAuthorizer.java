package marmalade.client.interceptors;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 *
 */
public class PreemptiveAuthorizer implements HttpRequestInterceptor
{

   private final String schemeName;

   public PreemptiveAuthorizer(String schemeName)
   {
      this.schemeName = schemeName;
   }

   @Override
   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException
   {

      AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

      if (authState != null && authState.getAuthScheme() != null) {
         return;
      }

      HttpHost target = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
      CredentialsProvider provider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
      AuthSchemeRegistry schemes = (AuthSchemeRegistry) context.getAttribute(ClientContext.AUTHSCHEME_REGISTRY);
      HttpParams params = request.getParams();
      AuthScheme scheme = schemes.getAuthScheme(schemeName, params);
      
      if (scheme != null) {
         AuthScope targetScope = new AuthScope(target.getHostName(), target.getPort(), scheme.getRealm(), scheme.getSchemeName());
         Credentials creds = provider.getCredentials(targetScope);

         if (creds != null) {
            authState.update(scheme, creds);
         }
      }

   }

}
