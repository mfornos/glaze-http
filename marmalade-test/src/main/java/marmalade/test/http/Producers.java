package marmalade.test.http;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.simpleframework.http.Request;

public class Producers
{
   public static class Headers implements Producer
   {
      private final String value;

      private final String[] keys;

      public Headers(String value, String... keys)
      {
         this.value = value;
         this.keys = keys;
      }

      @Override
      public String produce(Request request)
      {
         Object[] params = new Object[keys.length];

         for (int i = 0; i < params.length; i++) {
            params[i] = request.getValue(keys[i]);
         }

         return String.format(value, params);
      }
   }

   public static class Id implements Producer
   {
      private final String value;

      public Id(String value)
      {
         this.value = value;
      }

      @Override
      public String produce(Request request)
      {
         return value;
      }
   }

   public static interface Producer
   {
      String produce(Request request);
   }

   public static class Rand implements Producer
   {
      private static final Random random = new Random();

      private final String value;

      public Rand(String value)
      {
         this.value = value;
      }

      @Override
      public String produce(Request request)
      {
         return String.format(value, random.nextInt());
      }
   }

   public static class Seq implements Producer
   {
      private static final AtomicInteger seq = new AtomicInteger(0);

      public static void reset()
      {
         seq.set(0);
      }

      private final String value;

      public Seq(String value)
      {
         this.value = value;
      }

      @Override
      public String produce(Request request)
      {
         return String.format(value, seq.incrementAndGet());
      }
   }

   public static Producer headers(String value, String... values)
   {
      return new Headers(value, values);
   }

   public static Producer id(String value)
   {
      return new Id(value);
   }

   public static Producer rand(String value)
   {
      return new Rand(value);
   }

   public static Producer seq(String value)
   {
      return new Seq(value);
   }

}
