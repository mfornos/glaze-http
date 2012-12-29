package marmalade;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Map;

import marmalade.client.Response;
import marmalade.client.handlers.ErrorHandler;
import marmalade.client.handlers.ErrorResponseException;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestErrors extends BaseHttpTest
{
   @Test(timeOut = 5000)
   public void errorsBasic()
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_FORBIDDEN));

      try {
         Marmalade.Get(baseUrl + "/").map();
         Assert.fail("Exception not thrown");
      } catch (MarmaladeException ex) {
         Assert.assertEquals(ex.getMessage(), "HTTP/1.1 403 OK");
      }

      try {
         Marmalade.Get(baseUrl + "/").map();
         Assert.fail("Exception not thrown");
      } catch (ErrorResponseException ex) {
         Assert.assertEquals(ex.getStatusCode(), HttpStatus.SC_FORBIDDEN);
      }

   }

   @Test(timeOut = 5000)
   public void errorsCustomHandlerMap()
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_FORBIDDEN));

      Map<String, Object> out = Marmalade.Get(baseUrl + "/").withErrorHandler(new ErrorHandler()
      {
         @Override
         public void onError(Response response)
         {
            Assert.assertEquals(response.status(), HttpStatus.SC_FORBIDDEN);
         }
      }).map();

      Assert.assertNull(out);
   }

   @Test(timeOut = 5000)
   public void errorsCustomHandlerSend()
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_FORBIDDEN));

      Response out = Marmalade.Get(baseUrl + "/").withHandler(new ResponseHandler<Response>()
      {
         @Override
         public Response handleResponse(HttpResponse response) throws ClientProtocolException, IOException
         {
            return null;
         }
      }).execute();

      Assert.assertNull(out);
   }

   @Test(timeOut = 5000)
   public void errorsNested()
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_FORBIDDEN));

      try {
         Marmalade.Get(baseUrl + "/").map();
         Assert.fail("Exception not thrown");
      } catch (ErrorResponseException e) {
         try {
            throw new ErrorResponseException(new RuntimeException(e));
         } catch (ErrorResponseException ne) {
            Assert.assertEquals(ne.getStatusCode(), HttpStatus.SC_FORBIDDEN);
         }
      }

   }

   @Test
   public void exception()
   {
      try {
         throw new ConcurrentModificationException();
      } catch (ConcurrentModificationException e) {
         try {
            throw new ErrorResponseException(new UnsupportedOperationException(e));
         } catch (ErrorResponseException ne) {
            Assert.assertEquals(ne.getStatusCode(), -1);
         }
      }
   }
}
