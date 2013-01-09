package glaze;

import static glaze.test.http.Condition.when;
import static glaze.test.http.Expressions.any;
import glaze.Glaze;
import glaze.client.Response;
import glaze.test.http.BaseHttpTest;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.BasicHttpContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestRequest extends BaseHttpTest
{

   @Test(timeOut = 5000)
   public void test()
   {
      server.expect(when(any()).path("/reqtest").respond("OK"));

      Response response = Glaze.Post(baseUrl + "/reqtest").bean("hello").as(ContentType.DEFAULT_TEXT).send();
      Assert.assertEquals(response.discardContent().status(), HttpStatus.SC_OK);

      response = Glaze.Get(baseUrl + "/reqtest").send();
      Assert.assertEquals(response.discardContent().status(), HttpStatus.SC_OK);

      response = Glaze.Put(baseUrl + "/reqtest").bean("hello").as(ContentType.DEFAULT_TEXT).send();
      Assert.assertEquals(response.discardContent().status(), HttpStatus.SC_OK);

      response = Glaze.Head(baseUrl + "/reqtest").send();
      Assert.assertEquals(response.discardContent().status(), HttpStatus.SC_OK);

      response = Glaze.Delete(baseUrl + "/reqtest").send();
      Assert.assertEquals(response.discardContent().status(), HttpStatus.SC_OK);

      response = Glaze.Delete(baseUrl + "/reqtest").send(new BasicHttpContext());
      Assert.assertEquals(response.discardContent().status(), HttpStatus.SC_OK);
   }

}
