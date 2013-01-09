package glaze;

import static glaze.Glaze.Post;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.APPLICATION_XML;

import glaze.test.data.Card;
import glaze.test.data.Member;
import glaze.test.http.BaseHttpTest;
import glaze.test.http.Condition;

import java.io.IOException;


import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestMapping extends BaseHttpTest
{

   @Test(timeOut = 5000)
   public void postJson()
   {
      server.expect(Condition.when("POST").respond("{\"id\":\"ABCDEFG\"}", ContentType.APPLICATION_JSON));

      Card in = new Card("Hello", "world", "0989080");

      Member out = Post(baseUrl + "/").bean(in).as(APPLICATION_JSON).map(Member.class);
      Assert.assertEquals(out.id, "ABCDEFG");

      out = Post(baseUrl + "/", APPLICATION_JSON).bean(in).map(Member.class);
      Assert.assertEquals(out.id, "ABCDEFG");
   }

   @Test(timeOut = 5000)
   public void postJsonBackXml()
   {
      server.expect(Condition.when("POST").respond("<xml><id>ABCDEFG</id></xml>", ContentType.APPLICATION_XML));

      Card in = new Card("Hello", "world", "0989080");

      Member out = Post(baseUrl + "/").bean(in).as(APPLICATION_JSON).map(Member.class, new BasicHttpContext());
      Assert.assertEquals(out.id, "ABCDEFG");

      out = Post(baseUrl + "/", APPLICATION_JSON).bean(in).map(Member.class);
      Assert.assertEquals(out.id, "ABCDEFG");
   }

   @Test(timeOut = 5000)
   public void postJsonForceBackXml()
   {
      server.expect(Condition.when("POST").respond("<xml><id>ABCDEFG</id></xml>", ContentType.APPLICATION_OCTET_STREAM));

      Card in = new Card("Hello", "world", "0989080");

      Member out = Post(baseUrl + "/").bean(in).as(APPLICATION_JSON).map(Member.class, APPLICATION_XML);
      Assert.assertEquals(out.id, "ABCDEFG");
      out = Post(baseUrl + "/").bean(in).as(APPLICATION_JSON).map(Member.class, new BasicHttpContext(), APPLICATION_XML);
      Assert.assertEquals(out.id, "ABCDEFG");
   }

   @Test(timeOut = 5000)
   public void postUrlEncoded() throws ParseException, IOException
   {
      server.expect(Condition.when("POST").respond("{\"id\":\"ABCDEFG\"}", ContentType.APPLICATION_JSON));

      Card in = new Card("Hello", "world", "0989080");

      HttpPost req = (HttpPost) Post(baseUrl).bean(in).build();
      Assert.assertEquals(EntityUtils.toString(req.getEntity()), "name=Hello&desc=world&idList=0989080");

      Member out = Post(baseUrl + "/").bean(in).map(Member.class);
      Assert.assertEquals(out.id, "ABCDEFG");

   }

}
