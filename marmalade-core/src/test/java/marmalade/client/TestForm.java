package marmalade.client;

import static marmalade.Marmalade.Post;
import static marmalade.client.Form.newForm;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestForm
{

   @Test
   public void simple() throws ParseException, IOException
   {
      Form form = newForm().add("jsecLogin", "user").add("jsecPassword", "passà").add("token", "007 001");
      HttpUriRequest request = Post("http://localhost").entity(form.build()).build();

      Assert.assertNotNull(request);
      HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
      Assert.assertEquals(entity.getContentType().getValue(), ContentType.APPLICATION_FORM_URLENCODED.toString());
      Assert.assertEquals(EntityUtils.toString(entity), "jsecLogin=user&jsecPassword=pass%E0&token=007+001");

      Charset cs = Charset.forName("UTF-8");
      form = newForm(cs).add("jsecLogin", "user").add(0, "jsecPassword", "passà").add("token", "007 001");
      request = Post("http://localhost").entity(form.build()).build();

      entity = ((HttpEntityEnclosingRequest) request).getEntity();
      Assert.assertEquals(ContentType.get(entity).getCharset(), cs);
      Assert.assertEquals(EntityUtils.toString(entity), "jsecPassword=pass%C3%A0&jsecLogin=user&token=007+001");

      form = newForm().add("jsecLogin", "user").add("jsecPassword", "passà").add("token", "007 001").charset(cs);
      request = Post("http://localhost").entity(form.build()).build();

      entity = ((HttpEntityEnclosingRequest) request).getEntity();
      Assert.assertEquals(ContentType.get(entity).getCharset(), cs);
      Assert.assertEquals(EntityUtils.toString(entity), "jsecLogin=user&jsecPassword=pass%C3%A0&token=007+001");
   }

}
