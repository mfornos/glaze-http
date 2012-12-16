package marmalade;

import static marmalade.Marmalade.Post;
import static org.apache.http.entity.ContentType.MULTIPART_FORM_DATA;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import marmalade.test.data.Foo;

import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.testng.annotations.Test;

public class TestMultipart
{
   @Test(timeOut = 5000)
   public void multipartSimple() throws FileNotFoundException
   {
      File file = new File("src/test/resources/files/keiller.jpg");

      String uri = "http://xxx.com/?dump&dir=test-up";
      HttpPost post = (HttpPost) Post(uri, MULTIPART_FORM_DATA).bean(file).build();

      MultipartEntity entity = (MultipartEntity) post.getEntity();
      assertTrue(entity.getContentLength() > 0);
      assertTrue(entity.getContentType().getValue().startsWith("multipart/form-data; boundary="));

      post = (HttpPost) Post(uri, MULTIPART_FORM_DATA).bean(new FileInputStream(file)).build();

      entity = (MultipartEntity) post.getEntity();
      assertTrue(entity.getContentLength() > 0);
      assertTrue(entity.getContentType().getValue().startsWith("multipart/form-data; boundary="));

      byte[] binData = new byte[] { 0x1, 0x1, 0x1, 0x0, 0xB, 0xA, 0xB, 0xB, 0xE };

      post = (HttpPost) Post(uri, MULTIPART_FORM_DATA).bean(binData).build();

      entity = (MultipartEntity) post.getEntity();
      assertTrue(entity.getContentLength() > 0);
      assertTrue(entity.getContentType().getValue().startsWith("multipart/form-data; boundary="));
   }

   @Test(timeOut = 5000)
   public void multipartUnmanaged()
   {
      File file = new File("src/test/resources/files/keiller.jpg");

      MultipartEntity entity = new MultipartEntity();
      entity.addPart("photo", new FileBody(file, "image/jpeg"));

      String uri = "http://xxx.com/?dump&dir=test-up";
      HttpPost post = (HttpPost) Post(uri).entity(entity).build();

      assertEquals((MultipartEntity) post.getEntity(), entity);
   }

   @Test(timeOut = 5000)
   public void multipartWithAnnotations() throws ParseException, IOException
   {
      Foo bar = new Foo();
      File file = new File("src/test/resources/files/keiller.jpg");
      bar.setAttachment(file);

      String uri = "http://xxx.com/?dump&dir=test-up";
      HttpPost post = (HttpPost) Post(uri).bean(bar).as(MULTIPART_FORM_DATA).build();

      MultipartEntity entity = (MultipartEntity) post.getEntity();
      assertTrue(entity.getContentLength() > 0);
      assertTrue(entity.getContentType().getValue().startsWith("multipart/form-data; boundary="));
   }
}
