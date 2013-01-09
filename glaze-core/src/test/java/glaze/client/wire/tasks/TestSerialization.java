package glaze.client.wire.tasks;

import static glaze.test.http.Condition.when;
import static glaze.test.http.Expressions.any;

import glaze.Glaze;
import glaze.client.Response;
import glaze.client.sync.DefaultSyncClient;
import glaze.client.wire.tasks.SerializableRequest;
import glaze.test.http.BaseHttpTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestSerialization extends BaseHttpTest
{
   @Test
   public void serialize() throws IOException, ClassNotFoundException
   {
      server.expect(when(any()).path("/serialize").respond("{\"id\":\"abcd\"}", ContentType.APPLICATION_JSON));

      HttpGet get = new HttpGet(baseUrl + "/serialize");

      serdeser(get);

      HttpPost post = new HttpPost(baseUrl + "/serialize");

      serdeser(post);

      DefaultSyncClient client = new DefaultSyncClient();

      String data = "<xml><bla>hi</bla></xml>";
      HttpUriRequest put = Glaze.Put(baseUrl + "/serialize").bean(data).as(ContentType.APPLICATION_ATOM_XML).build();
      HttpRequestBase created = serdeser(put);

      HttpEntity entity = ((HttpEntityEnclosingRequest) created).getEntity();
      Assert.assertEquals(EntityUtils.toString(entity), data);
      Assert.assertEquals(ContentType.get(entity).getMimeType(), ContentType.APPLICATION_ATOM_XML.getMimeType());
      Assert.assertEquals(ContentType.get(entity).getCharset(), ContentType.APPLICATION_ATOM_XML.getCharset());

      Response response = client.execute(created);
      Assert.assertEquals(response.status(), HttpStatus.SC_OK);
   }

   private HttpRequestBase serdeser(HttpUriRequest req) throws FileNotFoundException, IOException,
         ClassNotFoundException
   {
      SerializableRequest ser = SerializableRequest.from(req);

      // write

      String serfile = "src/test/resources/ser/bean.ser";
      FileOutputStream fout = new FileOutputStream(serfile);
      ObjectOutputStream sout = new ObjectOutputStream(fout);
      sout.writeObject(ser);
      sout.flush();

      // read

      FileInputStream fin = new FileInputStream(serfile);
      ObjectInputStream sin = new ObjectInputStream(fin);
      SerializableRequest deser = (SerializableRequest) sin.readObject();

      return deser.materialize();
   }
}
