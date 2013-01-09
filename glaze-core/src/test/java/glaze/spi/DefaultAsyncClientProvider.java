package glaze.spi;

import glaze.client.async.AsyncClient;
import glaze.client.async.DefaultAsyncClient;
import glaze.spi.ServiceProvider;

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
