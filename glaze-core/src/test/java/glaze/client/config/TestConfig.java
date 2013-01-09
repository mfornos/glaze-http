package glaze.client.config;

import glaze.client.config.DefaultPropertyConfig;
import glaze.client.config.DefaultYamlConfig;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestConfig
{
   private class ConfigProps extends DefaultPropertyConfig
   {
      public ConfigProps()
      {
         super("my.config", "conf/myconfig.properties");
      }

      public String bye()
      {
         return get("bye", "man");
      }

      public String hello()
      {
         return get("hello", "man");
      }
   }

   private class ConfigYaml extends DefaultYamlConfig
   {

      public ConfigYaml()
      {
         super("my.config", "conf/myconfig.yml");
      }

      public String bye()
      {
         return get("bye", "man");
      }

      public Number four()
      {
         return find("vuh.po.four", 100);
      }

      public String hello()
      {
         return get("hello", "man");
      }

      public String one()
      {
         return find("nest.one", "none");
      }

      public Number two()
      {
         return find("nest.two", 100);
      }
   }

   @Test
   public void basicProperties()
   {
      ConfigProps config = new ConfigProps();
      Assert.assertEquals(config.hello(), "world!");
      Assert.assertEquals(config.bye(), "man");
      Assert.assertEquals(config.getAsInt("num", 25), 10);

      try {
         System.setProperty("my.config", "src/test/resources/conf/alt.properties");
         config = new ConfigProps();
         Assert.assertEquals(config.hello(), "man");
         Assert.assertEquals(config.bye(), "sir");
         Assert.assertEquals(config.getAsInt("num", 25), 25);
      } finally {
         System.clearProperty("my.config");
      }
   }

   @Test
   public void basicYaml()
   {
      ConfigYaml config = new ConfigYaml();
      Assert.assertEquals(config.hello(), "world!");
      Assert.assertEquals(config.bye(), "man");
      Assert.assertEquals(config.one(), "sexto");
      Assert.assertEquals(config.two(), 6);
      Assert.assertEquals(config.four(), 4);

      try {
         System.setProperty("my.config", "src/test/resources/conf/alt.yml");
         config = new ConfigYaml();
         Assert.assertEquals(config.hello(), "man");
         Assert.assertEquals(config.bye(), "sir");
         Assert.assertEquals(config.one(), "none");
         Assert.assertEquals(config.two(), 100);
         Assert.assertEquals(config.four(), 100);
      } finally {
         System.clearProperty("my.config");
      }
   }
}
