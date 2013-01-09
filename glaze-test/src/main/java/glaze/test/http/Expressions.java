package glaze.test.http;

import java.util.regex.Pattern;

public final class Expressions
{
   public static class Eq implements Expression
   {
      private final String expr;

      public Eq(String expr)
      {
         this.expr = expr;
      }

      @Override
      public boolean match(String value)
      {
         return expr.equalsIgnoreCase(value);
      }
   }

   public static interface Expression
   {
      boolean match(String value);
   }

   public static class Regex implements Expression
   {
      private final Pattern pattern;

      public Regex(String expr)
      {
         this.pattern = Pattern.compile(expr);
      }

      @Override
      public boolean match(String value)
      {
         return pattern.matcher(value).matches();
      }
   }

   public static Expression ANY = new Regex(".*");

   public static Expression any()
   {
      return Expressions.ANY;
   }

   public static Expression eq(String expr)
   {
      return new Eq(expr);
   }

   public static Expression regex(String expr)
   {
      return new Regex(expr);
   }
}
