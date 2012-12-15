package marmalade.examples.misc;

import static marmalade.Marmalade.Get;
import static marmalade.Marmalade.Post;
import static marmalade.client.Form.newForm;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import marmalade.client.Form;
import marmalade.client.UriBuilder;
import marmalade.client.Response;
import marmalade.client.sync.SyncClient;
import marmalade.spi.Registry;

import org.apache.http.ParseException;

/**
 * A slightly extravagant login ritual for Tapestry 't:formdata'
 * particularities.
 * 
 */
public class TapestryLogin
{

   public static void main(String... args) throws ParseException, IOException
   {
      // Trust self-signed certs
      Registry.lookup(SyncClient.class).trustSelfSignedCertificates();

      // Prepare uri
      UriBuilder base = UriBuilder.uriBuilderFrom(baseUri);
      URI index = base.build();

      // 1) Get request -> extract t:formdata token from response
      String token = extractToken(Get(index).send());

      // Prepare the login form
      Form form = newForm().add("jsecLogin", username).add("jsecPassword", password).add("t:formdata", token);

      // 2) Post request -> sign in
      Post(base.appendPath("signin.jsecloginform").build()).entity(form.build()).send();

      // Now we have the session cookie
      // 3) Get request -> protected index page content
      String page = Get(index).send().asString();

      System.out.println(page);
   }

   private static String extractToken(Response response)
   {
      Pattern formdata = Pattern.compile(".*value=\"(.*)?\" name=\"t\\:formdata\".*", Pattern.MULTILINE
            | Pattern.DOTALL);

      Matcher matcher = formdata.matcher(response.asString());

      if (matcher.matches()) {
         return matcher.group(1);
      } else {
         throw new RuntimeException("t:formdata not found.");
      }
   }

   // TODO clear this
   private static String baseUri = "https://127.0.0.1:8080/";
   private static String username = "admin", password = "admin";

}
