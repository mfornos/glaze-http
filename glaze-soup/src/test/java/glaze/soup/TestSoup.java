package glaze.soup;

import glaze.Glaze;
import glaze.client.async.AsyncClient;
import glaze.soup.Mode;
import glaze.soup.async.SoupConsumer;
import glaze.soup.async.SoupSelectConsumer;
import glaze.soup.sync.SoupHandler;
import glaze.soup.sync.SoupSelectHandler;
import glaze.spi.Registry;
import glaze.test.http.BaseHttpTest;
import glaze.test.http.Condition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestSoup extends BaseHttpTest
{

   private class Article
   {
      public String creator;
      public String content;

      @Override
      public String toString()
      {
         return "Article [creator=" + creator + ", content=" + content + "]";
      }
   }

   @Test
   public void asyncDoc() throws InterruptedException, ExecutionException
   {
      server.expect(Condition.when("GET").respond(readFile("src/test/resources/data/feed00.xml"), ContentType.TEXT_XML));

      try {
         Future<Document> fdoc = Glaze.Get(baseUrl).withConsumer(SoupConsumer.instance(Mode.XML)).executeAsync();
         Document doc = fdoc.get();
         Assert.assertNotNull(doc);
         Assert.assertEquals(doc.getAllElements().size(), 661);
      } finally {
         Registry.lookup(AsyncClient.class).reset();
      }
   }

   @Test
   public void asyncSelect() throws InterruptedException, ExecutionException
   {
      server.expect(Condition.when("GET").respond(readFile("src/test/resources/data/feed00.xml"), ContentType.TEXT_XML));

      try {
         Future<Elements> fels = Glaze.Get(baseUrl).withConsumer(SoupSelectConsumer.Select("rss channel item title")).executeAsync();
         Elements els = fels.get();
         Assert.assertNotNull(els);
         Assert.assertEquals(els.size(), 25);
      } finally {
         Registry.lookup(AsyncClient.class).reset();
      }
   }

   @Test
   public void syncDoc()
   {
      server.expect(Condition.when("GET").respond(readFile("src/test/resources/data/feed00.xml"), ContentType.TEXT_XML));

      Document doc = Glaze.Get(baseUrl).withHandler(SoupHandler.instance(Mode.XML)).execute();

      Assert.assertNotNull(doc);
      Assert.assertEquals(doc.getAllElements().size(), 661);
   }

   @Test
   public void syncMap()
   {
      server.expect(Condition.when("GET").respond(readFile("src/test/resources/data/feed01.xml"), ContentType.TEXT_XML));

      ResponseHandler<List<Article>> handler = articleMapper();

      List<Article> articles = Glaze.Get(baseUrl).withHandler(handler).execute();
      Assert.assertEquals(articles.size(), 43);
      for (Article a : articles) {
         Assert.assertNotNull(a.content);
      }
   }

   @Test
   public void syncSelect()
   {
      server.expect(Condition.when("GET").respond(readFile("src/test/resources/data/feed00.xml"), ContentType.TEXT_XML));

      Elements els = Glaze.Get(baseUrl).withHandler(SoupSelectHandler.Select("rss channel item title", Mode.XML)).execute();

      Assert.assertNotNull(els);
      Assert.assertEquals(els.size(), 25);
   }

   private SoupHandler<List<Article>> articleMapper()
   {
      return new SoupHandler<List<Article>>(Mode.XML)
      {
         private static final long serialVersionUID = -7163961642551419213L;

         @Override
         protected List<Article> onDocument(Document doc)
         {
            List<Article> articles = new ArrayList<Article>();
            Elements se = doc.select("dc|creator");
            for (Element el : se) {
               Article a = new Article();
               a.creator = el.text();
               String text = el.parent().select("title").text();
               a.content = text;
               articles.add(a);
            }
            return articles;
         }
      };
   }

}
