package marmalade;

import java.util.Map;

import marmalade.client.handlers.ErrorHandler;
import marmalade.client.handlers.ErrorResponseException;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestErrors extends BaseHttpTest
{
   @Test
   public void simple()
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

   @Test
   public void customHandler()
   {
      server.expect(Condition.when("GET").respond(HttpStatus.SC_FORBIDDEN));

      Map<String, Object> out = Marmalade.Get(baseUrl + "/").with(new ErrorHandler()
      {
         @Override
         public void onError(HttpResponse response)
         {
            Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_FORBIDDEN);
         }
      }).map();

      Assert.assertNull(out);
   }
}
