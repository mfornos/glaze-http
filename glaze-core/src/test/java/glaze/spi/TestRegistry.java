package glaze.spi;

import glaze.client.sync.SyncClient;
import glaze.mime.ContentTypeEx;
import glaze.spi.Named;
import glaze.spi.Registry;
import glaze.spi.ServiceProvider;

import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestRegistry
{
   @Named("myapi")
   private class MyProvider implements ServiceProvider<String>
   {

      @Override
      public Class<String> serviceClass()
      {
         return String.class;
      }

      @Override
      public String serviceImpl()
      {
         return "Hello";
      }

   }

   @Test
   public void init()
   {
      Assert.assertNotNull(Registry.lookup(SyncClient.class));
      Assert.assertNotNull(Registry.lookupMapper(ContentType.APPLICATION_JSON));
      Assert.assertNotNull(Registry.lookupMapper(ContentType.APPLICATION_JSON.getMimeType()));
   }

   @Test
   public void namespace()
   {
      MyProvider stringProvider = new MyProvider();
      Registry reg = Registry.getOrCreate(stringProvider);
      reg.register(String.class, stringProvider.serviceImpl());
      reg.logState();

      Assert.assertEquals(reg.namespace(), "myapi");
      Assert.assertEquals(Registry.lookup("myapi", String.class).toString(), "Hello");

      Registry.reset();
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

      Registry.reset();
   }

}
