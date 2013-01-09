package glaze;

import glaze.Glaze;
import glaze.client.Response;
import glaze.client.handlers.DefaultResponseHandler;
import glaze.client.handlers.ErrorHandler;
import glaze.client.sync.DefaultSyncClient;
import glaze.client.sync.SyncClient;
import glaze.test.http.BaseHttpTest;
import glaze.test.http.Condition;
import glaze.util.TypeHelper;

import java.util.Map;


import org.apache.http.HttpStatus;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestClose extends BaseHttpTest
{
   @Test(timeOut = 5000)
   public void synMapClose()
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_FORBIDDEN));

      SyncClient client = new DefaultSyncClient();
      try {
         final PoolingClientConnectionManager cman = (PoolingClientConnectionManager) client.getHttpClient().getConnectionManager();

         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);

         Map<String, Object> out = Glaze.Get(baseUrl + "/").withErrorHandler(new ErrorHandler()
         {
            @Override
            public void onError(Response response)
            {
               Assert.assertEquals(cman.getTotalStats().getLeased(), 1);
               Assert.assertEquals(response.status(), HttpStatus.SC_FORBIDDEN);
            }
         }).map(client, TypeHelper.plainMap());

         Assert.assertNull(out);
         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);
      } finally {
         client.shutdown();
      }

   }

   @Test(timeOut = 5000)
   public void synSendClose()
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_FORBIDDEN));

      SyncClient client = new DefaultSyncClient();
      try {
         final PoolingClientConnectionManager cman = (PoolingClientConnectionManager) client.getHttpClient().getConnectionManager();

         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);

         Response out = Glaze.Get(baseUrl + "/").withErrorHandler(new ErrorHandler()
         {
            @Override
            public void onError(Response response)
            {
               Assert.assertEquals(cman.getTotalStats().getLeased(), 1);
               Assert.assertEquals(response.status(), HttpStatus.SC_FORBIDDEN);
            }
         }).send(client);

         Assert.assertNull(out);
         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);
      } finally {
         client.shutdown();
      }

   }

   @Test(timeOut = 5000)
   public void synSendHandlerClose()
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_FORBIDDEN));

      SyncClient client = new DefaultSyncClient();
      try {
         final PoolingClientConnectionManager cman = (PoolingClientConnectionManager) client.getHttpClient().getConnectionManager();

         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);

         Response out = Glaze.Get(baseUrl + "/").withHandler(new DefaultResponseHandler()
         {
            @Override
            public Response onError(Response response)
            {
               Assert.assertEquals(cman.getTotalStats().getLeased(), 1);
               Assert.assertEquals(response.status(), HttpStatus.SC_FORBIDDEN);
               return null;
            }

            @Override
            protected Response onResponse(Response response)
            {
               Assert.fail("response OK invocation");
               return null;
            }
         }).execute(client);

         Assert.assertNull(out);
         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);
      } finally {
         client.shutdown();
      }

   }

   @Test(timeOut = 5000)
   public void synSendHandlerOkClose()
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_ACCEPTED));

      SyncClient client = new DefaultSyncClient();
      try {
         final PoolingClientConnectionManager cman = (PoolingClientConnectionManager) client.getHttpClient().getConnectionManager();

         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);

         Response out = Glaze.Get(baseUrl + "/").withHandler(new DefaultResponseHandler()
         {
            @Override
            public Response onError(Response response)
            {
               Assert.fail("response ERROR invocation");
               return null;
            }

            @Override
            protected Response onResponse(Response response)
            {
               Assert.assertEquals(cman.getTotalStats().getLeased(), 1);
               Assert.assertEquals(response.status(), HttpStatus.SC_ACCEPTED);
               return null;
            }
         }).execute(client);

         Assert.assertNull(out);
         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);
      } finally {
         client.shutdown();
      }
   }
}
