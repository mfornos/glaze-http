package marmalade.oauth.creds;

import oauth.signpost.OAuthConsumer;

public class ConsumerCredentialsProvider extends DefaultCredentialsProvider
{

   public ConsumerCredentialsProvider(final OAuthConsumer consumer)
   {
      super(consumer.getConsumerKey(), consumer.getConsumerSecret(), consumer.getToken(), consumer.getTokenSecret());
   }

}
