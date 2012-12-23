package marmalade.examples.mashape;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MashapeConfig
{
   static Properties props = new Properties();
   static {
      try {
         props.load(new FileReader("src/main/resources/mashable.properties"));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static String getPublicKey()
   {
      return props.getProperty("key.public").replaceAll("\\s+", "");
   }

   public static String getPrivateKey()
   {
      return props.getProperty("key.private").replaceAll("\\s+", "");
   }

}
