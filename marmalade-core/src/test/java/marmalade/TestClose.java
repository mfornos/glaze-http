package marmalade;

import java.util.Map;

import marmalade.client.Response;
import marmalade.client.handlers.DefaultResponseHandler;
import marmalade.client.handlers.ErrorHandler;
import marmalade.client.sync.DefaultSyncClient;
import marmalade.client.sync.SyncClient;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;
import marmalade.util.TypeHelper;

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

         Map<String, Object> out = Marmalade.Get(baseUrl + "/").withErrorHandler(new ErrorHandler()
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

         Response out = Marmalade.Get(baseUrl + "/").withErrorHandler(new ErrorHandler()
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

         Response out = Marmalade.Get(baseUrl + "/").withHandler(new DefaultResponseHandler()
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
         }).send(client);

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

         Response out = Marmalade.Get(baseUrl + "/").withHandler(new DefaultResponseHandler()
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
         }).send(client);

         Assert.assertNull(out);
         Assert.assertEquals(cman.getTotalStats().getLeased(), 0);
      } finally {
         client.shutdown();
      }
   }
}
