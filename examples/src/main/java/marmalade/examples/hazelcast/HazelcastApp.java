package marmalade.examples.hazelcast;

import static marmalade.Marmalade.Get;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import marmalade.client.wire.tasks.SerializableResponse;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastApp
{

   public HazelcastApp(HazelcastInstance instance) throws InterruptedException, ExecutionException
   {
      ExecutorService svc = instance.getExecutorService();

      Future<SerializableResponse> response = svc.submit(Get("http://ask.com").buildSendCall());

      System.out.println(response.get().asString());
   }

   public static void main(String[] args) throws InterruptedException, ExecutionException
   {
      new HazelcastApp(Hazelcast.newHazelcastInstance(null));
      System.exit(0);
   }

}
