package marmalade.spi;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultMapperProvider implements ServiceProvider<ObjectMapper>
{

   @Override
   public Class<ObjectMapper> serviceClass()
   {
      return ObjectMapper.class;
   }

   @Override
   public ObjectMapper serviceImpl()
   {
      return new ObjectMapper();
   }

}
