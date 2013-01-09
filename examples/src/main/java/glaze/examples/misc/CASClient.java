package glaze.examples.misc;

import static glaze.Glaze.Delete;
import static glaze.Glaze.Get;
import static glaze.Glaze.Post;
import static glaze.client.Form.newForm;
import static glaze.client.UriBuilder.uriBuilderFrom;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;
import glaze.client.Response;
import glaze.client.handlers.CroakErrorHandler;
import glaze.client.handlers.ErrorHandler;

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

      Delete(ticketUri).send();
   }

   public static void main(String... args)
   {
      new CASClient();
   }

}
