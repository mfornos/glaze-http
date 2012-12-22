package marmalade.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import marmalade.spi.Registry;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

public class EntityMapper
{

   private enum Maps {

      UrlEncodedMap {
         @Override
         boolean accept(String ns, Object bean, ContentType type)
         {
            return ContentType.APPLICATION_FORM_URLENCODED.getMimeType().equals(type.getMimeType());
         }

         @Override
         HttpEntity map(String ns, Object bean, ContentType type)
         {
            return FormHelper.asUrlEncodedFormEntity(bean);
         }

      },

      MultipartMap {
         @Override
         boolean accept(String ns, Object bean, ContentType type)
         {
            return ContentType.MULTIPART_FORM_DATA.getMimeType().equals(type.getMimeType());
         }

         @Override
         HttpEntity map(String ns, Object bean, ContentType type)
         {
            return FormHelper.asMultipartEntity(bean);
         }
      },

      ObjectMapperMap {
         @Override
         boolean accept(String ns, Object bean, ContentType type)
         {
            ObjectMapper mapper = Registry.lookupMapper(ns, type);
            return mapper != null && mapper.canSerialize(bean.getClass());
         }

         @Override
         HttpEntity map(String ns, Object bean, ContentType type)
         {
            ObjectMapper mapper = Registry.lookupMapper(ns, type);
            CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
            OutputStream out = cbb.getOutputStream();
            try {
               mapper.writeValue(new BufferedOutputStream(cbb.getOutputStream()), bean);
               BasicHttpEntity basicEntity = new BasicHttpEntity();
               basicEntity.setContent(new BufferedInputStream(cbb.getInputStream()));
               basicEntity.setContentType(type.toString());
               return basicEntity;
            } catch (IOException e) {
               LOGGER.error(String.format("Error mapping '%s', returning a dummy StringEntity.", bean.getClass()), e);
               return new StringEntity(bean.toString(), type);
            } finally {
               try {
                  out.close();
               } catch (IOException e) {
                  LOGGER.error(e.getMessage(), e);
               }
            }
         }

         @Override
         HttpEntity repeatableMap(String ns, Object bean, ContentType type)
         {

            ObjectMapper mapper = Registry.lookupMapper(ns, type);

            StringWriter writer = new StringWriter();
            try {
               mapper.writeValue(writer, bean);
            } catch (IOException e) {
               LOGGER.error(e.getMessage(), e);
            } finally {
               try {
                  writer.close();
               } catch (IOException e) {
                  LOGGER.error(e.getMessage(), e);
               }
            }

            return new StringEntity(writer.toString(), type);
         }
      },

      // Always keep fall-back last
      Fallback {
         @Override
         boolean accept(String ns, Object bean, ContentType type)
         {
            return true;
         }

         @Override
         HttpEntity map(String ns, Object bean, ContentType type)
         {
            return new StringEntity(bean.toString(), type);
         }

      };

      abstract boolean accept(String ns, Object bean, ContentType type);

      abstract HttpEntity map(String ns, Object bean, ContentType type);

      HttpEntity repeatableMap(String ns, Object bean, ContentType type)
      {
         return map(null, bean, type);
      }

   }

   private static final Logger LOGGER = LoggerFactory.getLogger(EntityMapper.class);

   public static HttpEntity map(Object bean, ContentType type)
   {
      return map(Registry.NS_DEFAULT, bean, type, false);
   }

   public static HttpEntity map(String namespace, Object bean, ContentType type, boolean repeatable)
   {
      String ns = Optional.fromNullable(namespace).or(Registry.NS_DEFAULT);
      for (Maps m : Maps.values()) {
         if (m.accept(ns, bean, type)) {
            return repeatable ? m.repeatableMap(ns, bean, type) : m.map(ns, bean, type);
         }
      }
      return new StringEntity(bean.toString(), type);
   }

}
