package glaze;

import glaze.Glaze;
import glaze.client.Response;
import glaze.client.async.DefaultAsyncClient;
import glaze.client.handlers.ErrorHandler;
import glaze.test.http.BaseHttpTest;
import glaze.test.http.Condition;
import glaze.util.TypeHelper;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


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

         Future<Map<String, Object>> out = Glaze.Get(baseUrl + "/").withErrorHandler(new ErrorHandler()
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
