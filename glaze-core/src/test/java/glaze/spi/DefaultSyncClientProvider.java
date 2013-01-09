package glaze.spi;

import glaze.client.sync.DefaultSyncClient;
import glaze.client.sync.SyncClient;
import glaze.spi.ServiceProvider;

public class DefaultSyncClientProvider implements ServiceProvider<SyncClient>
{

   @Override
   public Class<SyncClient> serviceClass()
   {
     return SyncClient.class;
   }

   @Override
   public SyncClient serviceImpl()
   {
      return new DefaultSyncClient();
   }

}
