package marmalade.spi;

import marmalade.client.sync.DefaultSyncClient;
import marmalade.client.sync.SyncClient;

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
