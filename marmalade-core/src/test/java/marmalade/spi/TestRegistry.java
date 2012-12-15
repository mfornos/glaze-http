package marmalade.spi;

import marmalade.client.sync.SyncClient;
import marmalade.mime.ContentTypeEx;

import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestRegistry
{
   @Test
   public void init()
   {
      Assert.assertNotNull(Registry.lookup(SyncClient.class));
      Assert.assertNotNull(Registry.lookupMapper(ContentType.APPLICATION_JSON));
      Assert.assertNotNull(Registry.lookupMapper(ContentType.APPLICATION_JSON.getMimeType()));
   }

   @Test
   public void register()
   {
      Registry registry = Registry.instance();
      registry.register(String.class, "Hello");
      Assert.assertEquals(registry.unregister(String.class), "Hello");

      Assert.assertFalse(registry.isMapperRegistered(ContentTypeEx.SMILE));
      Assert.assertTrue(registry.isMapperRegistered(ContentType.APPLICATION_JSON));

      ObjectMapper mapper = new ObjectMapper();
      registry.registerMapper("any/one", mapper);
      Assert.assertEquals(registry.unregisterMapper("any/one"), mapper);

      registry.registerMapper(ContentType.APPLICATION_SVG_XML, mapper);
      Assert.assertEquals(registry.unregisterMapper(ContentType.APPLICATION_SVG_XML), mapper);

      registry.reset();
   }
}
