package marmalade.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public final class TypeHelper
{

   public static final <K, V> TypeReference<Map<K, V>> newMap()
   {
      return new TypeReference<Map<K, V>>()
      {
         //
      };
   }

   public static final <K, V> MapType newMapType(Class<K> keyClass, Class<V> valueClass)
   {
      TypeFactory factory = TypeFactory.defaultInstance();
      return factory.constructMapType(Map.class, keyClass, valueClass);
   }

   @SuppressWarnings("unchecked")
   public static final <T> Class<T> resolveClass(TypeReference<T> tref)
   {
      Type type = tref.getType();
      Class<T> clazz;

      if (type instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType) type;
         clazz = (Class<T>) pt.getRawType();
      } else {
         clazz = (Class<T>) type;
      }

      return clazz;
   }

   public static TypeReference<Map<String, Object>> plainMap()
   {
      return TypeHelper.<String, Object> newMap();
   }

}
