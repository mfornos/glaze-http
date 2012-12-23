package marmalade.client.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class DefaultYamlConfig extends BaseConfig
{
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultYamlConfig.class);

   private Map<String, Object> config;

   public DefaultYamlConfig()
   {
      this("default.marmalade.config", "marmalade.yml");
   }

   public DefaultYamlConfig(String systemVarName, String defaultPropertyFile)
   {
      super(systemVarName, defaultPropertyFile);

      config = loadYaml();
   }

   @SuppressWarnings("unchecked")
   public Map<String, Object> child(String key)
   {
      return (Map<String, Object>) config.get(key);
   }

   @SuppressWarnings("unchecked")
   public <T> T find(String expr, T defaultValue)
   {
      String[] paths = expr.split("\\.");
      Map<String, Object> node = config;
      for (int i = 0; i < paths.length - 1; i++) {
         if (!node.containsKey(paths[i])) {
            return defaultValue;
         }
         node = (Map<String, Object>) node.get(paths[i]);
      }

      String last = paths[paths.length - 1];
      return (T) ((node.containsKey(last)) ? node.get(last) : defaultValue);
   }

   @SuppressWarnings("unchecked")
   public <T> T get(String key)
   {
      return (T) config.get(key);
   }

   @SuppressWarnings("unchecked")
   public <T> T get(String key, T defaultValue)
   {
      return (T) (config.containsKey(key) ? get(key) : defaultValue);
   }

   public Map<String, Object> getConfig()
   {
      return config;
   }

   private Map<String, Object> load(Yaml yaml, InputStream in)
   {
      try {
         // yaml.loadAs(reader, type);
         @SuppressWarnings("unchecked")
         Map<String, Object> obj = (Map<String, Object>) yaml.load(in);
         return obj;
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
               LOGGER.error(e.getMessage(), e);
            }
         }
      }
   }

   private Map<String, Object> loadYaml()
   {
      Yaml yaml = new Yaml();
      String configPath = getConfigPath();

      // try from fs
      try {
         return load(yaml, new FileInputStream(configPath));
      } catch (Exception e) {
         LOGGER.warn("loading configuration from fs failed for {}={}", getSystemVarName(), configPath);
      }

      // try from classpath
      try {
         return load(yaml, DefaultYamlConfig.class.getClassLoader().getResourceAsStream(configPath));
      } catch (Exception e) {
         LOGGER.warn("loading configuration from classpath failed for {}={}", getSystemVarName(), configPath);
      }

      // fallback
      LOGGER.warn("{} couldn't be loaded, try fallback to load default yaml from classpath: {}", getSystemVarName(), getDefaultConfigFile());
      try {
         return load(yaml, DefaultYamlConfig.class.getClassLoader().getResourceAsStream(getDefaultConfigFile()));
      } catch (Exception e) {
         LOGGER.error(String.format("loading default configuration from classpath: %s failed", getDefaultConfigFile()), e);
      }

      return null;
   }
}
