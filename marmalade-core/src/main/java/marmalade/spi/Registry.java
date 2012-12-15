package marmalade.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

import marmalade.MarmaladeException;
import marmalade.client.async.AsyncClient;
import marmalade.client.async.DefaultAsyncClient;
import marmalade.client.sync.DefaultSyncClient;
import marmalade.client.sync.SyncClient;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

/**
 * <p>
 * Simple pluggable registry for services, mappers and hooks. To plug in a
 * service you need to create the convenient
 * 'META-INF/services/full.interface.name' containing the full class names of
 * your implementations separated by new lines.
 * </p>
 * Example:<br/>
 * 
 * <strong>Custom HttpClient</strong>
 * 
 * 1) implement the ServiceContrib
 * 
 * <pre>
 * package mysvc.pkg;
 * 
 * import ...;
 * 
 * public class HttpClientContrib implements ServiceContrib {
 *    public Class<?> serviceClass() {
 *       return HttpClient.class;
 *    }
 * 
 *    public Object serviceImpl() {
 *       return new DefaultHttpClient();
 *    }
 * }
 * </pre>
 * 
 * 2) in '<em>META-INF/services/marmalade.spi.ServiceContrib</em>' add the
 * following line:
 * 
 * <pre>
 * mysvc.pkg.HttpClientContrib
 * </pre>
 * 
 * <strong>Register an ObjectMapper for a content-type</strong>
 * 
 * <pre>
 * [TODO document]
 * </pre>
 * 
 * @see ServiceLoader
 */
public class Registry
{

   private static final ServiceLoader<HookContrib> hookContribs = ServiceLoader.load(HookContrib.class);

   private static final ServiceLoader<ServiceContrib> serviceContribs = ServiceLoader.load(ServiceContrib.class);

   private static final ServiceLoader<MapperContrib> mapperContribs = ServiceLoader.load(MapperContrib.class);

   private static final Logger LOGGER = LoggerFactory.getLogger(Registry.class);

   private static final Registry instance = new Registry();

   private static Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();

   private static Map<String, ObjectMapper> mappers = new HashMap<String, ObjectMapper>();

   static {
      instance.initialize();
   }

   public static Registry instance()
   {
      return instance;
   }

   public static boolean isRegitered(Class<?> type)
   {
      return services.containsKey(type);
   }

   @SuppressWarnings("unchecked")
   public static <T> T lookup(Class<T> type)
   {
      Object service = services.get(type);
      if (service == null) {
         throw new MarmaladeException(String.format("Service '%s' not found.\n%s", type, instance));
      }
      return (T) service;
   }

   public static ObjectMapper lookupMapper(ContentType type)
   {
      return mappers.get(type.getMimeType());
   }

   public static ObjectMapper lookupMapper(String contentType)
   {
      return lookupMapper(ContentType.parse(contentType));
   }

   private Registry()
   {

   }

   public boolean isMapperRegistered(ContentType type)
   {
      return mappers.containsKey(type.getMimeType());
   }

   public void register(Class<?> serviceClass, Object serviceImpl)
   {
      synchronized (services) {
         services.put(serviceClass, serviceImpl);
      }
   }

   public void registerMapper(ContentType type, ObjectMapper mapper)
   {
      registerMapper(type.getMimeType(), mapper);
   }

   public void registerMapper(String mimeType, ObjectMapper mapper)
   {
      synchronized (mappers) {
         mappers.put(mimeType, mapper);
      }
   }

   @Override
   public String toString()
   {
      return "Registry [services=" + services + ", mappers=" + mappers + "]";
   }

   @SuppressWarnings("unchecked")
   public <T> T unregister(Class<?> type)
   {
      synchronized (services) {
         return (T) services.remove(type);
      }
   }

   public ObjectMapper unregisterMapper(ContentType type)
   {
      return unregisterMapper(type.getMimeType());
   }

   public ObjectMapper unregisterMapper(String mime)
   {
      synchronized (mappers) {
         return mappers.remove(mime);
      }
   }

   protected void reset()
   {
      synchronized (services) {
         services.clear();
      }
      initialize();
   }

   private void fallbackRegisterService(Class<?> type, Callable<?> load)
   {
      Object impl = locateServiceContrib(type);

      if (impl == null) {
         try {
            impl = load.call();
         } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
         }
      }

      register(type, impl);
   }

   private void initialize()
   {

      fallbackRegisterService(SyncClient.class, new Callable<SyncClient>()
      {
         @Override
         public SyncClient call()
         {
            return new DefaultSyncClient();
         }
      });

      fallbackRegisterService(AsyncClient.class, new Callable<AsyncClient>()
      {
         @Override
         public AsyncClient call()
         {
            return new DefaultAsyncClient();
         }
      });

      registerServices();
      registerMappers();

      // TODO advise hooks on registration?
      for (HookContrib hook : hookContribs) {

         for (Map.Entry<Class<?>, Object> entry : services.entrySet()) {
            if (hook.acceptService(entry.getKey())) {
               hook.visitService(entry.getKey(), entry.getValue());
            }
         }
         for (Map.Entry<String, ObjectMapper> entry : mappers.entrySet()) {
            if (hook.acceptMapper(entry.getKey())) {
               hook.visitMapper(entry.getKey(), entry.getValue());
            }
         }

      }

      logState();
   }

   private ServiceContrib locateServiceContrib(Class<?> type)
   {
      for (ServiceContrib contrib : serviceContribs) {
         if (type.equals(contrib.serviceClass())) {
            return contrib;
         }
      }
      return null;
   }

   private void logState()
   {

      String title = "Marmalade Registry";
      StringBuilder msg = new StringBuilder("\n");
      msg.append(title);
      msg.append("\n");
      msg.append(new String(new char[title.length()]).replace("\0", "="));
      msg.append("\nServices:\n");
      for (Map.Entry<Class<?>, Object> entry : services.entrySet()) {
         msg.append(String.format("* %s => %s\n", entry.getKey().getSimpleName(), entry.getValue().getClass()));
      }
      msg.append("\nMappers:\n");
      for (String mtype : mappers.keySet()) {
         msg.append(String.format("* %s\n", mtype));
      }
      msg.append("\n~\n\n");

      LOGGER.info(msg.toString());
   }

   private void registerMappers()
   {

      // Default mapper for conversions &c.
      fallbackRegisterService(ObjectMapper.class, new Callable<ObjectMapper>()
      {
         @Override
         public ObjectMapper call()
         {
            return new ObjectMapper();
         }
      });

      // Register mapper contribs
      for (MapperContrib contrib : mapperContribs) {
         registerMapper(contrib.mimeType(), contrib.mapper());
      }

      // Assure that mappers for JSON and XML are available
      if (!isMapperRegistered(ContentType.APPLICATION_JSON)) {
         ObjectMapper objectMapper = new ObjectMapper();
         objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
         objectMapper.registerModule(new AfterburnerModule());
         registerMapper(ContentType.APPLICATION_JSON, objectMapper);
      }

      if (!isMapperRegistered(ContentType.APPLICATION_XML)) {
         JacksonXmlModule module = new JacksonXmlModule();
         module.setDefaultUseWrapper(false);
         XmlMapper xmlMapper = new XmlMapper(module);
         registerMapper(ContentType.APPLICATION_XML, xmlMapper);
      }

   }

   private void registerServices()
   {
      for (ServiceContrib contrib : serviceContribs) {
         register(contrib.serviceClass(), contrib.serviceImpl());
      }
   }

}
