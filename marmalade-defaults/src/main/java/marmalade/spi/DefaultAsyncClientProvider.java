package marmalade.spi;

import marmalade.client.async.AsyncClient;
import marmalade.client.async.DefaultAsyncClient;

public class DefaultAsyncClientProvider implements ServiceProvider<AsyncClient>
{

   @Override
   public Class<AsyncClient> serviceClass()
   {
      return AsyncClient.class;
   }

   @Override
   public AsyncClient serviceImpl()
   {
      return new DefaultAsyncClient();
   }

}
