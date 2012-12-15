package marmalade;

import static marmalade.test.http.Condition.when;
import static marmalade.test.http.Expressions.any;
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
      
      Assert.assertEquals(Marmalade.Post(baseUrl + "/reqtest").bean("hello").as(ContentType.DEFAULT_TEXT).send().discardContent().getStatus(), HttpStatus.SC_OK);
      Assert.assertEquals(Marmalade.Get(baseUrl + "/reqtest").send().discardContent().getStatus(), HttpStatus.SC_OK);
      Assert.assertEquals(Marmalade.Put(baseUrl + "/reqtest").bean("hello").as(ContentType.DEFAULT_TEXT).send().discardContent().getStatus(), HttpStatus.SC_OK);
      Assert.assertEquals(Marmalade.Head(baseUrl + "/reqtest").send().discardContent().getStatus(), HttpStatus.SC_OK);
      Assert.assertEquals(Marmalade.Delete(baseUrl + "/reqtest").send().discardContent().getStatus(), HttpStatus.SC_OK);
      Assert.assertEquals(Marmalade.Delete(baseUrl + "/reqtest").send(new BasicHttpContext()).discardContent().getStatus(), HttpStatus.SC_OK);
   }

}
