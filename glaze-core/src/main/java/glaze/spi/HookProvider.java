package glaze.spi;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hook SPI.
 */
public interface HookProvider
{
   boolean acceptService(Class<?> type);

   boolean acceptMapper(String mime);

   void visitService(Class<?> type, Object impl);

   void visitMapper(String mime, ObjectMapper mapper);
}
