package marmalade.oauth;

import com.google.common.base.Preconditions;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthException;

public abstract class OAuthFlow
{
   public enum FlowState {
      INIT, AUTH_REQUESTED, VERIFIED
   }

   private final OAuthConsumer consumer;
   private final OAuthProvider provider;
   private FlowState state;

   public OAuthFlow(OAuthConsumer consumer, OAuthProvider provider)
   {
      this.consumer = consumer;
      this.provider = provider;
      this.state = FlowState.INIT;
   }

   public OAuthFlow(String key, String secret)
   {
      this.consumer = new DefaultOAuthConsumer(key, secret);
      this.provider = new DefaultOAuthProvider(requestTokenUrl(), accessTokenUrl(), authorizeWebsiteUrl());
      this.state = FlowState.INIT;
   }

   public OAuthConsumer confirmAuthorization(String pin) throws OAuthException
   {
      Preconditions.checkState(FlowState.AUTH_REQUESTED.equals(state));

      provider.retrieveAccessToken(consumer, pin);
      this.state = FlowState.VERIFIED;
      return consumer;
   }

   public OAuthConsumer getConsumer()
   {
      return consumer;
   }

   public FlowState getState()
   {
      return state;
   }

   public String name()
   {
      return "Unnamed";
   }

   public String requestAuthorization() throws OAuthException
   {
      return requestAuthorization(OAuth.OUT_OF_BAND);
   }

   public String requestAuthorization(String callback) throws OAuthException
   {
      this.state = FlowState.AUTH_REQUESTED;
      return provider.retrieveRequestToken(consumer, callback);
   }

   abstract protected String accessTokenUrl();

   abstract protected String authorizeWebsiteUrl();

   abstract protected String requestTokenUrl();
}
