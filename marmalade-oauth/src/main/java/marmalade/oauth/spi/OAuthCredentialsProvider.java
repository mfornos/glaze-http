package marmalade.oauth.spi;

import marmalade.client.Client;

import org.apache.http.client.CredentialsProvider;

/**
 *
 */
public interface OAuthCredentialsProvider
{

   void visit(CredentialsProvider provider);

   void visit(Client client);

}
