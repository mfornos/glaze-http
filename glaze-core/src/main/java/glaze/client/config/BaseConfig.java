package glaze.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseConfig
{
   private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfig.class);

   private final String systemVarName;

   private final String defaultConfigFile;

   public BaseConfig(String systemVarName, String defaultConfigFile)
   {
      this.systemVarName = systemVarName;
      this.defaultConfigFile = defaultConfigFile;
   }

   protected String getConfigPath()
   {
      String config = System.getProperty(systemVarName);
      if (null == config || config.isEmpty()) {
         if (LOGGER.isDebugEnabled())
            LOGGER.debug("{} system property was not defined, defaults to: {} ", systemVarName, defaultConfigFile);
         config = defaultConfigFile;
      }
      return config;
   }

   protected String getDefaultConfigFile()
   {
      return defaultConfigFile;
   }

   protected String getSystemVarName()
   {
      return systemVarName;
   }
}
