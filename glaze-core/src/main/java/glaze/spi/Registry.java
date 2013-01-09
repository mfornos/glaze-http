package glaze.spi;

import glaze.GlazeException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;


import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

   private static final RegistryShutdownHook REGISTRY_SHUTDOWN_HOOK = new RegistryShutdownHook();

   public static final String NS_DEFAULT = "default";

   private final static ServiceLoader<HookProvider> hookContribs = ServiceLoader.load(HookProvider.class);;

   @SuppressWarnings("rawtypes")
   private final static ServiceLoader<ServiceProvider> serviceContribs = ServiceLoader.load(ServiceProvider.class);

   private final static ServiceLoader<MapperProvider> mapperContribs = ServiceLoader.load(MapperProvider.class);

   private static final Logger LOGGER = LoggerFactory.getLogger(Registry.class);

   private static final Map<String, Registry> instances = new HashMap<String, Registry>();

   private final Map<Class<?>, Object> services;

   private final Map<String, ObjectMapper> mappers;

   private final String namespace;

   static {
      synchronized (instances) {
         initialize();
      }
   }

   public static Registry defaultedInstance(String namespace)
   {
      return instances.containsKey(namespace) ? instances.get(namespace) : instance();
   }

   public static Registry instance()
   {
      return instances.get(NS_DEFAULT);
   }

   public static Registry instance(String namespace)
   {
      return instances.get(namespace);
   }

   public static Collection<Registry> instances()
   {
      return instances.values();
   }

   public static boolean isRegitered(Class<?> type)
   {
      return isRegitered(NS_DEFAULT, type);
   }

   public static boolean isRegitered(String namespace, Class<?> type)
   {
      return instance(namespace).services.containsKey(type);
   }

   public static <T> T lookup(Class<T> type)
   {
      return lookup(NS_DEFAULT, type);
   }

   @SuppressWarnings("unchecked")
   public static <T> T lookup(String namespace, Class<T> type)
   {
      Object service = instance(namespace).services.get(type);
      if (service == null) {
         throw new GlazeException(String.format("Service '%s' not found.\n%s", type, instance(namespace)));
      }
      return (T) service;
   }

   public static ObjectMapper lookupMapper(ContentType type)
   {
      return lookupMapper(NS_DEFAULT, type.getMimeType());
   }

   public static ObjectMapper lookupMapper(String contentType)
   {
      return lookupMapper(NS_DEFAULT, contentType);
   }

   public static ObjectMapper lookupMapper(String namespace, ContentType type)
   {
      ObjectMapper mapper = instance(namespace).mappers.get(type.getMimeType());
      return mapper == null ? instance().mappers.get(type.getMimeType()) : mapper;
   }

   public static ObjectMapper lookupMapper(String namespace, String contentType)
   {
      return lookupMapper(namespace, ContentType.parse(contentType));
   }

   static Registry getOrCreate(Object contrib)
   {
      Named named = contrib.getClass().getAnnotation(Named.class);
      Registry registry = named == null ? instance() : instance(named.value());
      if (registry == null && named != null) {
         LOGGER.info("Creating registry for namespace: '{}'", named.value());
         registry = new Registry(named.value());
         instances.put(named.value(), registry);
      }
      return registry;
   }

   static void reset()
   {
      synchronized (instances) {
         instances.clear();
      }
      initialize();
   }

   private static void initialize()
   {
      if (!instances.containsKey(NS_DEFAULT)) {
         Registry defaultInstance = new Registry(NS_DEFAULT);
         instances.put(NS_DEFAULT, defaultInstance);

         Runtime.getRuntime().removeShutdownHook(REGISTRY_SHUTDOWN_HOOK);
         Runtime.getRuntime().addShutdownHook(REGISTRY_SHUTDOWN_HOOK);

         registerServices();
         registerMappers();
         registerHooks();

         logInstancesState();
      }
   }

   private static void logInstancesState()
   {
      for (Registry registry : instances.values()) {
         registry.logState();
      }
   }

   private static void registerHooks()
   {
      // TODO advise hooks on registration?
      for (HookProvider hook : hookContribs) {
         Registry registry = getOrCreate(hook);

         for (Map.Entry<Class<?>, Object> entry : registry.services.entrySet()) {
            if (hook.acceptService(entry.getKey())) {
               hook.visitService(entry.getKey(), entry.getValue());
            }
         }
         for (Map.Entry<String, ObjectMapper> entry : registry.mappers.entrySet()) {
            if (hook.acceptMapper(entry.getKey())) {
               hook.visitMapper(entry.getKey(), entry.getValue());
            }
         }
      }
   }

   private static void registerMappers()
   {

      // Register mapper contribs
      for (MapperProvider contrib : mapperContribs) {
         Registry registry = getOrCreate(contrib);
         registry.registerMapper(contrib.mimeType(), contrib.mapper());
      }

      Registry defaultRegistry = instance();

      // Assure that mappers for JSON and XML are available
      if (!defaultRegistry.isMapperRegistered(ContentType.APPLICATION_JSON)) {
         ObjectMapper mapper = new ObjectMapper();
         mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
         mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
         mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
         mapper.setSerializationInclusion(Include.NON_NULL);
         mapper.registerModule(new AfterburnerModule());
         defaultRegistry.registerMapper(ContentType.APPLICATION_JSON, mapper);
      }

      if (!defaultRegistry.isMapperRegistered(ContentType.APPLICATION_XML)) {
         JacksonXmlModule module = new JacksonXmlModule();
         module.setDefaultUseWrapper(false);
         XmlMapper mapper = new XmlMapper(module);
         mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
         mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
         defaultRegistry.registerMapper(ContentType.APPLICATION_XML, mapper);
      }

   }

   private static void registerServices()
   {
      for (ServiceProvider<?> contrib : serviceContribs) {
         Registry registry = getOrCreate(contrib);
         registry.register(contrib.serviceClass(), contrib.serviceImpl());
      }
   }

   private Registry(String namespace)
   {
      this.namespace = namespace;
      this.services = new HashMap<Class<?>, Object>();
      this.mappers = new HashMap<String, ObjectMapper>();
   }

   public boolean isMapperRegistered(ContentType type)
   {
      return mappers.containsKey(type.getMimeType());
   }

   public String namespace()
   {
      return namespace;
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

   void logState()
   {
      String title = "Marmalade Registry";
      StringBuilder msg = new StringBuilder("\n");
      msg.append(title);
      msg.append("\n");
      msg.append(new String(new char[title.length()]).replace("\0", "="));
      msg.append("\nNamespace: '");
      msg.append(namespace);
      msg.append("'\n\nServices:\n");
      if (services.isEmpty()) {
         msg.append("* [Empty]\n");
      }
      for (Map.Entry<Class<?>, Object> entry : services.entrySet()) {
         msg.append(String.format("* %s => %s\n", entry.getKey().getSimpleName(), entry.getValue().getClass()));
      }
      msg.append("\nMappers:\n");
      if (mappers.isEmpty()) {
         msg.append("* [Empty]\n");
      }
      for (String mtype : mappers.keySet()) {
         msg.append(String.format("* %s\n", mtype));
      }
      msg.append("\n~\n\n");

      LOGGER.info(msg.toString());
   }

   protected Map<Class<?>, Object> services()
   {
      return services;
   }

}
