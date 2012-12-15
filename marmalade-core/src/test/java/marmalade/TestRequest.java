package marmalade;

import static marmalade.test.http.Condition.when;
import static marmalade.test.http.Expressions.any;
import marmalade.client.Response;
import marmalade.test.http.BaseHttpTest;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.BasicHttpContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestRequest extends BaseHttpTest
{

   @Test
   public void test()
   {
      server.expect(when(any()).path("/reqtest").respond("OK"));

      Response response = Marmalade.Post(baseUrl + "/reqtest").bean("hello").as(ContentType.DEFAULT_TEXT).send();
      Assert.assertEquals(response.discardContent().getStatus(), HttpStatus.SC_OK);

      response = Marmalade.Get(baseUrl + "/reqtest").send();
      Assert.assertEquals(response.discardContent().getStatus(), HttpStatus.SC_OK);

      response = Marmalade.Put(baseUrl + "/reqtest").bean("hello").as(ContentType.DEFAULT_TEXT).send();
      Assert.assertEquals(response.discardContent().getStatus(), HttpStatus.SC_OK);

      response = Marmalade.Head(baseUrl + "/reqtest").send();
      Assert.assertEquals(response.discardContent().getStatus(), HttpStatus.SC_OK);

      response = Marmalade.Delete(baseUrl + "/reqtest").send();
      Assert.assertEquals(response.discardContent().getStatus(), HttpStatus.SC_OK);

      response = Marmalade.Delete(baseUrl + "/reqtest").send(new BasicHttpContext());
      Assert.assertEquals(response.discardContent().getStatus(), HttpStatus.SC_OK);
   }

}
