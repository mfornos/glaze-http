package marmalade.trello;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TrelloConfig
{
   static Properties props = new Properties();
   static {
      try {
         props.load(new FileReader("src/examples/resources/trello.properties"));
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public static String getKey() {
      return props.getProperty("key");
   }
   
   public static String getSecret() {
      return props.getProperty("secret");
   }

   public static String getTokenKey()
   {
      return props.getProperty("token.key");
   }
   
   public static String getTokenSecret()
   {
      return props.getProperty("token.secret");
   }
   
}
