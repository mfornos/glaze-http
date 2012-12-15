package marmalade.test.http;

import static marmalade.test.http.Expressions.eq;

import java.util.HashMap;
import java.util.Map;

import marmalade.test.http.Expressions.Expression;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.simpleframework.http.Request;

public class Condition
{
   public static Condition when(Expression expr)
   {
      return new Condition("method", expr).path("/");
   }

   public static Condition when(String expr)
   {
      return when(eq(expr));
   }

   private final HashMap<String, Expression> expressions;

   private ResponseBuilder response;

   private final HashMap<String, Expression> headers;

   private boolean satisfied;

   public Condition(String field, Expression expr)
   {
      this.satisfied = false;
      this.headers = new HashMap<String, Expression>();
      this.expressions = new HashMap<String, Expression>();
      this.expressions.put(field, expr);
   }

   public Condition and(String field, Expression expr)
   {
      expressions.put(field, expr);
      return this;
   }

   public Condition and(String field, String expr)
   {
      return and(field, eq(expr));
   }

   public void checkSatisfied()
   {
      if (!isSatisfied()) {
         throw new UnsatisfiedExpectationException(this);
      }
   }

   public Condition header(String name, Expression expr)
   {
      headers.put(name, expr);
      return this;
   }

   public Condition header(String name, String expr)
   {
      return header(name, eq(expr));
   }

   public boolean isSatisfied()
   {
      return satisfied;
   }

   public ResponseBuilder match(Request req)
   {
      boolean matched = false;
      matched = match("method", req.getMethod());

      if (matched) {
         String target = req.getTarget();
         matched = match("path", target);
      }

      if (matched && hasHeaderConditions()) {
         matched = matchAnyHeader(req);
      }

      return matched ? getResponse() : null;
   }

   public boolean match(String field, String value)
   {
      boolean matched = false;

      for (Map.Entry<String, Expression> entry : expressions.entrySet()) {
         if (entry.getKey().equalsIgnoreCase(field) && entry.getValue().match(value)) {
            matched = true;
            break;
         }
      }

      return matched;
   }

   public Condition path(Expression expr)
   {
      return and("path", expr);
   }

   public Condition path(String expr)
   {
      return and("path", expr);
   }

   public ResponseBuilder respond(int status)
   {
      response = ResponseBuilder.respond(status, this);
      return response.body("");
   }

   public ResponseBuilder respond(String content)
   {
      return respond(content, ContentType.DEFAULT_TEXT);
   }

   public ResponseBuilder respond(String content, ContentType ctype)
   {
      return respond(HttpStatus.SC_OK).body(content, ctype);
   }

   @Override
   public String toString()
   {
      return "Condition [expressions=" + expressions + ", response=" + response + ", headers=" + headers
            + ", satisfied=" + satisfied + "]";
   }

   private ResponseBuilder getResponse()
   {
      this.satisfied = true;
      return response;
   }

   private boolean hasHeaderConditions()
   {
      return !headers.isEmpty();
   }

   private boolean matchAnyHeader(Request req)
   {
      boolean matched = false;

      for (Map.Entry<String, Expression> header : headers.entrySet()) {
         if (req.contains(header.getKey()) && header.getValue().match(req.getValue(header.getKey()))) {
            matched = true;
            break;
         }
      }

      return matched;
   }

}
