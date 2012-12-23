package marmalade.soup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import marmalade.Marmalade;
import marmalade.client.async.AsyncClient;
import marmalade.soup.async.SoupConsumer;
import marmalade.soup.async.SoupSelectConsumer;
import marmalade.soup.sync.SoupHandler;
import marmalade.soup.sync.SoupSelectHandler;
import marmalade.spi.Registry;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;

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
         Future<Document> fdoc = Marmalade.Get(baseUrl).withConsumer(SoupConsumer.instance(Mode.XML)).executeAsync();
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
         Future<Elements> fels = Marmalade.Get(baseUrl).withConsumer(SoupSelectConsumer.Select("rss channel item title")).executeAsync();
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

      Document doc = Marmalade.Get(baseUrl).withHandler(SoupHandler.instance(Mode.XML)).execute();

      Assert.assertNotNull(doc);
      Assert.assertEquals(doc.getAllElements().size(), 661);
   }

   @Test
   public void syncMap()
   {
      server.expect(Condition.when("GET").respond(readFile("src/test/resources/data/feed01.xml"), ContentType.TEXT_XML));

      ResponseHandler<List<Article>> handler = articleMapper();

      List<Article> articles = Marmalade.Get(baseUrl).withHandler(handler).execute();
      Assert.assertEquals(articles.size(), 43);
      for (Article a : articles) {
         Assert.assertNotNull(a.content);
      }
   }

   @Test
   public void syncSelect()
   {
      server.expect(Condition.when("GET").respond(readFile("src/test/resources/data/feed00.xml"), ContentType.TEXT_XML));

      Elements els = Marmalade.Get(baseUrl).withHandler(SoupSelectHandler.Select("rss channel item title", Mode.XML)).execute();

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
