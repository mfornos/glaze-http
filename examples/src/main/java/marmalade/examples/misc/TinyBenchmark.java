package marmalade.examples.misc;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import marmalade.Marmalade;
import marmalade.client.Response;
import marmalade.client.UriBuilder;

public class TinyBenchmark
{
   private static abstract class Benchmark
   {
      public void run(String name)
      {
         run(name, 10, 1000);
      }

      public void run(String name, int loops, int times)
      {
         System.out.println("\nWarm up...\n");
         warmup(loops, times);

         char[] cs = new char[25];
         Arrays.fill(cs, '=');
         System.out.format("%s [loops=%s, times=%s]%s\n\n", name, loops, times, new String(cs));

         double stats = 0.0;

         for (int i = 0; i < loops; i++) {
            stats += runBench(times);
         }

         System.out.format("-Avg: %sms\n-Per unit: %sms\n\n", stats / loops, stats / (loops * times));
      }

      protected void warmup(int loops, int times)
      {
         try {

            System.gc();
            Thread.sleep(500);

            for (int i = 0; i < loops; i++) {
               bench(times);
            }

         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      protected abstract void bench(int times) throws Exception;

      private double runBench(int times)
      {
         try {
            long begin = System.nanoTime();

            bench(times);

            long time = System.nanoTime() - begin;
            return time / 1000000.0;
         } catch (Exception e) {
            e.printStackTrace();
            return -1;
         }
      }
   }

   private static final URI LOCALHOST_URI = UriBuilder.uriBuilderFrom("http://127.0.0.1").build();

   public static void main(String[] args) throws InterruptedException, ExecutionException
   {
      new Benchmark()
      {
         @Override
         protected void bench(int times)
         {
            for (int i = 0; i < times; i++) {
               Response r = Marmalade.Get(LOCALHOST_URI).send();
               r.discardContent();
            }
         }
      }.run("Sync");

      new Benchmark()
      {
         @Override
         protected void bench(int times) throws Exception
         {
            List<Future<Response>> futures = new ArrayList<Future<Response>>();
            for (int i = 0; i < times; i++) {
               futures.add(Marmalade.Get(LOCALHOST_URI).sendAsync());
            }

            for (Future<Response> r : futures) {
               Response response = r.get();
               response.discardContent();
            }
         }
      }.run("Async");

      System.exit(0);
   }
}
