package marmalade.spi;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shutdown hook that invokes disposable methods of registered services.
 * 
 * @see Dispose
 */
public class RegistryShutdownHook extends Thread
{

   private static final Logger LOGGER = LoggerFactory.getLogger(RegistryShutdownHook.class);

   @Override
   public void run()
   {
      LOGGER.info("Shutting down...");

      Collection<Registry> instances = Registry.instances();
      for (Registry instance : instances) {
         shutdownInstance(instance);
      }
   }

   private void callIfNeeded(Object instance, Method[] methods)
   {
      for (Method m : methods) {
         if (m.getAnnotation(Dispose.class) != null) {
            try {
               LOGGER.info("Dispose {}", instance.getClass().getName());
               m.invoke(instance);
            } catch (Exception e) {
               LOGGER.error(e.getMessage(), e);
            }
         }
      }
   }

   private void shutdownInstance(Registry instance)
   {
      LOGGER.info("Namespace: '{}'", instance.namespace());

      Map<Class<?>, Object> services = instance.services();

      for (Class<?> clazz : services.keySet()) {
         callIfNeeded(services.get(clazz), clazz.getMethods());
      }
   }

}
