/*
 * Copyright 2009 John Kristian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package marmalade.oauth;

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
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

/**
 * An interceptor that initiates authorization without waiting for a challenge
 * from the server. This avoids a response/request exchange (for the challenge),
 * but it doesn't give the server a chance to select the authorization scheme or
 * realm.
 * 
 * Here's an example using OAuth:
 * 
 * <pre>
 * Client client = (Client) impl;
 * AbstractHttpClient httpClient = (AbstractHttpClient) client.getHttpClient();
 * httpClient.getAuthSchemes().register(OAuthSchemeFactory.SCHEME_NAME, new OAuthSchemeFactory());
 * 
 * OAuthCredentialsProvider provider = Registry.lookup(OAuthCredentialsProvider.class);
 * provider.visit(httpClient.getCredentialsProvider());
 * client.proxyAuthPref(&quot;oauth&quot;, &quot;basic&quot;, &quot;digest&quot;);
 * 
 * httpClient.addRequestInterceptor(new PreemptiveAuthorizer(), 0);
 * </pre>
 * 
 * @author John Kristian
 * @author mfornos
 */
public class PreemptiveAuthorizer implements HttpRequestInterceptor
{

   /**
    * If no auth scheme has been selected for the given context, consider each
    * of the preferred auth schemes and select the first one for which an
    * AuthScheme and matching Credentials are available.
    */
   @SuppressWarnings("unchecked")
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

      for (String schemeName : (Iterable<String>) params.getParameter(AuthPNames.PROXY_AUTH_PREF)) {
         AuthScheme scheme = schemes.getAuthScheme(schemeName, params);
         if (scheme != null) {
            AuthScope targetScope = new AuthScope(target.getHostName(), target.getPort(), scheme.getRealm(), scheme.getSchemeName());
            Credentials creds = provider.getCredentials(targetScope);

            if (creds != null) {
               authState.update(scheme, creds);
               return;
            }
         }
      }
   }
}
