package marmalade;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import marmalade.client.Response;
import marmalade.client.async.DefaultAsyncClient;
import marmalade.client.handlers.ErrorHandler;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;
import marmalade.util.TypeHelper;

import org.apache.http.HttpStatus;
import org.apache.http.impl.nio.conn.PoolingClientAsyncConnectionManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestAsyncClose extends BaseHttpTest
{
   @Test(timeOut = 5000)
   public void asyncMapClose() throws InterruptedException, ExecutionException
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_FORBIDDEN));

      DefaultAsyncClient client = new DefaultAsyncClient();
      try {
         final PoolingClientAsyncConnectionManager cman = (PoolingClientAsyncConnectionManager) client.getHttpClient().getConnectionManager();

         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);

         Future<Map<String, Object>> out = Marmalade.Get(baseUrl + "/").withErrorHandler(new ErrorHandler()
         {
            @Override
            public void onError(Response response)
            {
               Assert.assertEquals(cman.getTotalStats().getLeased(), 1);
               Assert.assertEquals(response.status(), HttpStatus.SC_FORBIDDEN);
            }
         }).mapAsync(client, TypeHelper.plainMap());

         Assert.assertNull(out.get());
         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);
      } finally {
         client.shutdown();
      }

   }

   // TODO test send ErrorHandler when implemented
}
