package marmalade.examples.misc;

import static marmalade.Marmalade.Delete;
import static marmalade.Marmalade.Get;
import static marmalade.Marmalade.Post;
import static marmalade.client.Form.newForm;
import static marmalade.client.UriBuilder.uriBuilderFrom;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;
import marmalade.client.Response;
import marmalade.client.handlers.CroakErrorHandler;
import marmalade.client.handlers.ErrorHandler;

import org.apache.http.HttpEntity;

/**
 * See https://wiki.jasig.org/display/CASUM/RESTful+API
 * 
 */
public class CASClient
{
   String svcUri = "http://127.0.0.1/service";
   String tgtUri = "https://127.0.0.1/cas/v1/tickets";
   String username = "username";
   String password = "password";
   String app = "myApp";
   ErrorHandler eh = new CroakErrorHandler();

   public CASClient()
   {
      // 1. Grab the Ticket Granting Ticket (TGT)

      HttpEntity credentials = newForm().add("username", username).add("password", password).add("app", app).build();
      String ticketUri = Post(tgtUri).entity(credentials).setAccept(TEXT_PLAIN).withErrorHandler(eh).send().header(LOCATION);

      // 2. Grab a service ticket (ST) for a CAS protected service

      String ticket = Post(ticketUri).entity(newForm().add("service", svcUri).build()).withErrorHandler(eh).send().asString();

      // 3. Grab the protected document

      Response document = Get(uriBuilderFrom(svcUri).addParameter("ticket", ticket).build()).send();
      System.out.println(document.asString());

      // 4. Logout

      Delete(ticketUri);
   }

   public static void main(String... args)
   {
      new CASClient();
   }

}
