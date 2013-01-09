package glaze.soup.sync;

import glaze.soup.Mode;
import glaze.util.ResponseUtil;

import java.io.IOException;
import java.io.Serializable;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class SoupHandler<T> implements ResponseHandler<T>, Serializable
{
   private static final long serialVersionUID = -590561025854232430L;

   public static SoupHandler<Document> instance(Mode mode)
   {
      return new SoupHandler<Document>(mode)
      {
         private static final long serialVersionUID = -268684803417627991L;

         @Override
         protected Document onDocument(Document doc)
         {
            return doc;
         }
      };
   }

   private String baseUri;

   private Mode mode;

   public SoupHandler()
   {
      this("", Mode.HTML);
   }

   public SoupHandler(Mode mode)
   {
      this("", mode);
   }

   public SoupHandler(String baseUri)
   {
      this(baseUri, Mode.HTML);
   }

   public SoupHandler(String baseUri, Mode mode)
   {
      this.baseUri = baseUri;
      this.mode = mode;
   }

   @Override
   public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException
   {
      HttpEntity entity = response.getEntity();
      String charset = ResponseUtil.resolveEncoding(response);
      Document doc = Jsoup.parse(entity.getContent(), charset, baseUri, mode.getParser());
      return onDocument(doc);
   }

   abstract protected T onDocument(Document doc);

}
