package marmalade.client.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPropertyConfig extends BaseConfig
{

   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPropertyConfig.class);

   private final Properties properties;

   public DefaultPropertyConfig()
   {
      this("default.marmalade.config", "marmalade.properties");
   }

   public DefaultPropertyConfig(String systemVarName, String defaultPropertyFile)
   {
      super(systemVarName, defaultPropertyFile);

      this.properties = new Properties();
      loadProperties();
   }

   public String get(final String key)
   {
      return getImpl(key);
   }

   public String get(final String key, final String defaultValue)
   {
      return getImpl(key, defaultValue);
   }

   public boolean getAsBoolean(final String key)
   {
      String val = getImpl(key);
      return Boolean.parseBoolean(val);
   }

   public boolean getAsBoolean(String key, boolean defaultValue)
   {
      return exists(key) ? getAsBoolean(key) : defaultValue;
   }

   public double getAsDouble(String key)
   {
      String val = getImpl(key);
      return Double.parseDouble(val);
   }

   public double getAsDouble(String key, double defaultValue)
   {
      return exists(key) ? getAsDouble(key) : defaultValue;
   }

   public long getAsInt(final String key)
   {
      String val = getImpl(key);
      return Integer.parseInt(val);
   }

   public long getAsInt(String key, int defaultValue)
   {
      return exists(key) ? getAsInt(key) : defaultValue;
   }

   public long getAsLong(final String key)
   {
      String val = getImpl(key);
      return Long.parseLong(val);
   }

   public long getAsLong(String key, long defaultValue)
   {
      return exists(key) ? getAsLong(key) : defaultValue;
   }

   public Properties getProperties()
   {
      return properties;
   }

   @Override
   public String toString()
   {
      return "PropertyConfig [properties=" + properties + "]";
   }

   protected boolean exists(final String key)
   {
      return properties.containsKey(key);
   }

   protected String getImpl(final String key)
   {
      return getImpl(key, null);
   }

   protected String getImpl(final String key, final String defaultValue)
   {
      return properties.getProperty(key, defaultValue);
   }

   protected void loadProperties()
   {
      final String configPath = getConfigPath();

      // try load from FS
      try {
         properties.load(new FileInputStream(configPath));
         return;
      } catch (Exception e) {
         LOGGER.warn("loading configuration from fs failed for {}={}", getSystemVarName(), configPath);
      }

      // try load from classpath
      try {
         properties.load(DefaultPropertyConfig.class.getClassLoader().getResourceAsStream(configPath));
         return;
      } catch (Exception e) {
         LOGGER.warn("loading configuration from classpath failed for {}={}", getSystemVarName(), configPath);
      }

      // fallback
      LOGGER.warn("{} couldn't be loaded, try fallback to load default properties from classpath: {}", getSystemVarName(), getDefaultConfigFile());
      try {
         properties.load(DefaultPropertyConfig.class.getClassLoader().getResourceAsStream(getDefaultConfigFile()));
         return;
      } catch (Exception e) {
         LOGGER.error(String.format("loading default configuration from classpath: %s failed", getDefaultConfigFile()), e);
      }
   }

}
