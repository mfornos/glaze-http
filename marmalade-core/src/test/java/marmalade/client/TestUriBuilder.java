package marmalade.client;

import static marmalade.client.UriBuilder.uriBuilder;
import static marmalade.client.UriBuilder.uriBuilderFrom;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.testng.annotations.Test;

public class TestUriBuilder
{
   @Test
   public void testAcceptsHttpAndHttpScheme()
   {
      uriBuilderFrom(URI.create("http://example.com"));
      uriBuilderFrom(URI.create("https://example.com"));
      uriBuilderFrom(URI.create("HTTP://example.com"));
      uriBuilderFrom(URI.create("HTTPS://example.com"));
   }

   @Test
   public void testAddEmptyParameter()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").addParameter("pretty").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/?pretty");
   }

   @Test
   public void testAddMixedEmptyAndNonEmptyParameters()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").addParameter("pretty").addParameter("pretty", true).addParameter("pretty").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/?pretty&pretty=true&pretty");
   }

   @Test
   public void testAddMultipleEmptyParameters()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").addParameter("pretty").addParameter("pretty").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/?pretty&pretty");
   }

   @Test
   public void testAddParameter()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").replacePath("/").addParameter("k1", 1).addParameter("k1", 2).addParameter("k1", 0).addParameter("k2", 3).build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/?k1=1&k1=2&k1=0&k2=3");
   }

   @Test
   public void testAddParameterMultivalued()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").replacePath("/").addParameter("k1", "1", "2", "0").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/?k1=1&k1=2&k1=0");
   }

   @Test
   public void testAppendAbsolutePath()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").appendPath("/a/b/c").appendPath("/x/y/z").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/a/b/c/x/y/z");
   }

   @Test
   public void testAppendPathElidesSlashes()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").appendPath("/a/b/c/").appendPath("/x/y/z").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/a/b/c/x/y/z");
   }

   @Test
   public void testAppendRelativePath()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").appendPath("/a/b/c").appendPath("x/y/z").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/a/b/c/x/y/z");
   }

   @Test
   public void testAppendRelativePathToDefault()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").appendPath("a/b/c").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/a/b/c");
   }

   @Test
   public void testAppendToDefaultPath()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").appendPath("/a/b/c").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/a/b/c");
   }

   @Test
   public void testBasic()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com");
   }

   @Test
   public void testCreateFromUri()
   {
      URI original = URI.create("http://www.example.com:8081/a%20/%C3%A5?k=1&k=2&%C3%A5=3");
      assertEquals(UriBuilder.uriBuilderFrom(original).build(), original);
   }

   @Test
   public void testDefaultPort()
   {
      URI uri = UriBuilder.uriBuilderFrom(URI.create("http://www.example.com:8081")).defaultPort().build();

      assertEquals(uri.toASCIIString(), "http://www.example.com");
   }

   @Test
   public void testDoesNotStripTrailingSlash()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").appendPath("/a/b/c/").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/a/b/c/");
   }

   @Test
   public void testEncodesPath()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").replacePath("/`#%^{}|[]<>?áéíóú").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/%60%23%25%5E%7B%7D%7C%5B%5D%3C%3E%3F%C3%A1%C3%A9%C3%AD%C3%B3%C3%BA");
   }

   @Test
   public void testEncodesQueryParameters()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").replaceParameter("a", "&").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/?a=%26");
   }

   @Test
   public void testFull()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").port(8081).replacePath("/a/b/c").replaceParameter("k", "1").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com:8081/a/b/c?k=1");
   }

   @Test
   public void testQueryParametersNoPath()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").addParameter("a", "1").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/?a=1");
   }

   @Test
   public void testReplaceParameterMultivalued()
   {
      URI uri = UriBuilder.uriBuilderFrom(URI.create("http://www.example.com/?k1=1&k1=2&k2=3")).replaceParameter("k1", "a", "b", "c").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/?k2=3&k1=a&k1=b&k1=c");
   }

   @Test
   public void testReplaceParameters()
   {
      URI uri = UriBuilder.uriBuilderFrom(URI.create("http://www.example.com:8081/?k1=1&k1=2&k2=3")).replaceParameter("k1", "4").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com:8081/?k2=3&k1=4");
   }

   @Test
   public void testReplacePathWithRelative()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").replacePath("a/b/c").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/a/b/c");
   }

   @Test
   public void testReplacePort()
   {
      URI uri = UriBuilder.uriBuilderFrom("http://www.example.com:8081/").port(80).build();

      assertEquals(uri.toASCIIString(), "http://www.example.com:80/");
   }

   @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = ".*scheme.*")
   public void testVerifiesSchemeIsSet()
   {
      uriBuilder().build();
   }

   @Test
   public void testWithPath()
   {
      URI uri = uriBuilder().scheme("http").host("www.example.com").replacePath("/a/b/c").build();

      assertEquals(uri.toASCIIString(), "http://www.example.com/a/b/c");
   }

}
